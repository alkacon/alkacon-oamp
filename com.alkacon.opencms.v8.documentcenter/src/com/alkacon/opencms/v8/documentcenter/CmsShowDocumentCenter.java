/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsShowDefaultFile.java,v $
 * Date   : $Date: 2010/03/19 15:31:13 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Module Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Module Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Module Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.v8.documentcenter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.I_CmsResourceInit;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsFileUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.containerpage.CmsContainerElementBean;
import org.opencms.xml.containerpage.CmsContainerPageBean;
import org.opencms.xml.containerpage.CmsXmlContainerPage;
import org.opencms.xml.containerpage.CmsXmlContainerPageFactory;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;

/**
 * The document center resource handler.<p>
 * 
 * This handler checks the presence of a document center element on a container page
 * and handles requests to folders and files of the center.<p>
 *
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 8.0.0 
 */
public class CmsShowDocumentCenter implements I_CmsResourceInit {

    /** Value for the session variable when disclaimer was accepted. */
    public static final String ACCEPTED_VALUE = "true";

    /** Disclaimer property name. */
    public static final String DISCLAIMER_PROPERTY = "disclaimer_page";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsShowDocumentCenter.class);

    /**
     * Sets the request attributes to make the document center work
     * 
     * @param startPath the start path of the document center
     * @param inDocPath the relative path to the current document or folder
     * @param req the current request
     * 
     * @return the absolute VFS path to the document center folder or file to show
     */
    public static String setDocumentCenterAttributes(String startPath, String inDocPath, HttpServletRequest req) {

        startPath = CmsFileUtil.removeTrailingSeparator(startPath);
        // set document center start path
        req.setAttribute(CmsDocumentFrontend.ATTR_STARTPATH, startPath);
        // set relative path to document folder
        req.setAttribute(CmsDocumentFrontend.ATTR_PATHPART, inDocPath);
        // set full VFS path to document folder
        String docPath = startPath + inDocPath;
        req.setAttribute(CmsDocumentFrontend.ATTR_FULLPATH, docPath);
        return docPath;
    }

    /**
     * Checks if a disclaimer has to be shown for the requested document URI.<p>
     * 
     * @param requestedDocumentUri the URI of the document to check
     * @param cms the current users context
     * @param req the current request
     * 
     * @return <code>true</code> if a disclaimer should be shown, otherwise <code>false</code>
     */
    protected boolean checkDisclaimer(String requestedDocumentUri, CmsObject cms, HttpServletRequest req) {

        boolean result = false;
        String disclaimer = null;

        try {
            disclaimer = cms.readPropertyObject(requestedDocumentUri, DISCLAIMER_PROPERTY, true).getValue();
        } catch (CmsException e) {
            // do nothing
        }

        if (disclaimer != null) {
            // found disclaimer property, eventually show other file
            LOG.debug(DISCLAIMER_PROPERTY + ": " + disclaimer);

            if (cms.existsResource(disclaimer)) {
                // get the HTTP session
                HttpSession session = req.getSession();
                // get the value of the session attribute according to the requested resource
                String accepted = (String)session.getAttribute(disclaimer);

                LOG.debug("Session Attribute " + disclaimer + ": " + accepted);

                // disclaimer has not been accepted, show disclaimer page
                if (!ACCEPTED_VALUE.equals(accepted)) {
                    result = true;
                    req.setAttribute(CmsDocumentFrontend.ATTR_DISCLAIMER, disclaimer);
                }
            } else {
                // specified disclaimer page does not exist
                LOG.error("Error reading disclaimer page '" + disclaimer);
            }
        }
        return result;
    }

    /**
     * @see org.opencms.main.I_CmsResourceInit#initResource(org.opencms.file.CmsResource, org.opencms.file.CmsObject, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public CmsResource initResource(CmsResource resource, CmsObject cms, HttpServletRequest req, HttpServletResponse res) {

        // search only when file is null!
        if (resource == null) {
            // get the requested URI
            String uri = cms.getRequestContext().getUri();
            if (uri.startsWith(CmsResource.VFS_FOLDER_SYSTEM)) {
                // don't show the system folder!
                return null;
            }
            if (cms.existsResource(uri)) {
                // resource does exist, check if disclaimer should be shown
                checkDisclaimer(uri, cms, req);
                return null;
            }

            // cut the site root from the URI if it is there already
            String siteRoot = OpenCms.getSiteManager().getSiteRoot(uri);
            if (siteRoot != null) {
                uri = uri.substring(siteRoot.length());
            } else {
                siteRoot = "";
            }

            // split the requested path in parts
            List<String> pathParts = CmsStringUtil.splitAsList(uri, '/');
            try {
                String fullPath = siteRoot;
                String prevPath = fullPath;
                for (String pathPart : pathParts) {
                    prevPath = fullPath;
                    fullPath += "/" + pathPart;
                    if (!cms.existsResource(fullPath)) {
                        // found non-existing path part, check last existing for document center property
                        String docCenterMarker = cms.readPropertyObject(
                            prevPath,
                            CmsDocumentFactory.PROPERTY_DOCCENTER_STARTFOLDER,
                            false).getValue();
                        if (CmsStringUtil.isNotEmpty(docCenterMarker)) {
                            // we have a doc center here, first read default file of the current folder
                            CmsResource result = cms.readDefaultFile(prevPath);
                            if (result != null) {
                                // get the start path from the container page containing the document center element
                                String startPath = getDocumentCenterStartPath(result, cms);
                                // calculate path to document
                                String inDocPath = (siteRoot + uri).substring(prevPath.length());
                                String fullDocPath = cms.getRequestContext().removeSiteRoot(
                                    setDocumentCenterAttributes(startPath, inDocPath, req));
                                if (!checkDisclaimer(fullDocPath, cms, req) && !CmsResource.isFolder(inDocPath)) {
                                    // no disclaimer to show, the document has to be shown
                                    result = cms.readResource(fullDocPath);
                                }
                                // set URI to requested document or container page
                                cms.getRequestContext().setUri(cms.getSitePath(result));
                                return result;
                            }
                        }
                        // no need to continue loop here 
                        return null;
                    }
                }
            } catch (CmsException e) {
                // ignore this exception
            } catch (UnsupportedOperationException e2) {
                // ignore this exception
            }
        }
        return resource;
    }

    /**
     * Returns the start folder of the document center that is configured in an element of the given resource.<p>
     * 
     * The resource has to be a container page.<p>
     * 
     * @param resource the resource to look up the document center start path
     * @param cms the current users context
     * 
     * @return the start folder of the document center
     * 
     * @throws CmsException if something goes wrong
     */
    private String getDocumentCenterStartPath(CmsResource resource, CmsObject cms) throws CmsException {

        // get the container page to fetch the document center configuration
        CmsXmlContainerPage cPage = CmsXmlContainerPageFactory.unmarshal(cms, resource);
        CmsContainerPageBean pageBean = cPage.getContainerPage(cms, cms.getRequestContext().getLocale());
        if (pageBean != null) {
            // found the page bean, look up document center element
            List<CmsContainerElementBean> elements = pageBean.getElements();
            for (CmsContainerElementBean element : elements) {
                if (element.getResource() == null) {
                    element.initResource(cms);
                }
                if (element.getResource().getTypeId() == 269) {
                    // read the start folder from the document center configuration
                    CmsXmlContent docConfig = CmsXmlContentFactory.unmarshal(cms, cms.readFile(element.getResource()));
                    return docConfig.getStringValue(cms, "Folder", cms.getRequestContext().getLocale());
                }
            }
        }
        return "";
    }

}
