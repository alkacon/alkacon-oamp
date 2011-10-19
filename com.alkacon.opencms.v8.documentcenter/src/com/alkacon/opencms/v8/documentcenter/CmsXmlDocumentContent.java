/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsXmlDocumentContent.java,v $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsFileUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;

/**
 * Helper class to get texts for Header, Footer, Disclaimer, DisclaimerDeclined and FolderEmpty out
 * of xml content of a default file.
 * 
 * @author Peter Bonrad
 */
public class CmsXmlDocumentContent {

    /** The name of the default file where the xml content can be found. */
    private static final String DEFAULT_FILE = "$default.html";

    /** The name of the element in the xml document where the text for the disclaimer can be found. */
    private static final String ELEMENT_DISCLAIMER = "Disclaimer";

    /** The name of the element in the xml document where the text for the disclaimer declined can be found. */
    private static final String ELEMENT_DISCLAIMER_DECLINED = "Disclaimer_Declined";

    /** The name of the element in the xml document where the text for the folder empty text can be found. */
    private static final String ELEMENT_FOLDEREMPTY = "FolderEmpty";

    /** The name of the element in the xml document where the text for the footer can be found. */
    private static final String ELEMENT_FOOTER = "Footer";

    /** The name of the element in the xml document where the text for the header can be found. */
    private static final String ELEMENT_HEADER = "Header";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsXmlDocumentContent.class);

    /** The name of the property where the path with the default file can be found. */
    private static final String PROPERTY_CONTENT_PATH = "docs.content";

    /** The name of the property where the page to show the disclaimer can be found. */
    private static final String PROPERTY_DISCLAIMER_PAGE = "disclaimer_page";

    /** The id of the xml document. */
    private static final int XMLDOCUMENT_TYPE = 268;

    /** The actual cms object. */
    private CmsJspActionElement m_cms;

    /**
     * Creates a new CmsXmlDocumentContent.<p>
     * 
     * @param cms the actual action element
     */
    public CmsXmlDocumentContent(CmsJspActionElement cms) {

        m_cms = cms;
    }

    /**
     * Returns the text with the disclaimer. Takes the path out of the property of the disclaimer
     * and searches there (and all parent folder) for the default file. If found it returns the content 
     * of the element "Disclaimer" or null if not found or an error occurs.<p>
     * 
     * @return the text with the disclaimer or null if not found or on errors
     */
    public String getDisclaimer() {

        String path = (String)m_cms.getRequest().getAttribute(CmsDocumentFrontend.ATTR_DISCLAIMER);
        if (path == null) {
            return null;
        }
        return getContentElement(null, ELEMENT_DISCLAIMER, true, CmsResource.getFolderPath(path));
    }

    /**
     * Returns the text with the disclaimer declined. Takes the path out of the property of the disclaimer
     * and searches there (and all parent folder) for the default file. If found it returns the content 
     * of the element "Disclaimer_Declined" or null if not found or an error occurs.<p>
     * 
     * @return the text for the disclaimer declined or null if not found
     */
    public String getDisclaimerDeclined() {

        String path = (String)m_cms.getRequest().getAttribute(CmsDocumentFrontend.ATTR_DISCLAIMER);
        if (path == null) {
            return null;
        }
        return getContentElement(null, ELEMENT_DISCLAIMER_DECLINED, true, CmsResource.getFolderPath(path));
    }

    /**
     * Returns the text for the folder empty.<p> 
     * 
     * @return the text for the folder empty or null if not found
     */
    public String getFolderEmpty() {

        return getContentElement(PROPERTY_CONTENT_PATH, ELEMENT_FOLDEREMPTY, true);
    }

    /**
     * Returns the text for the footer.<p>
     * 
     * @return the text for the footer or null if not found
     */
    public String getFooter() {

        return getContentElement(PROPERTY_CONTENT_PATH, ELEMENT_FOOTER, true);
    }

    /**
     * Returns the text for the header.<p>
     * 
     * @return the text for the header or null if not found
     */
    public String getHeader() {

        return getContentElement(PROPERTY_CONTENT_PATH, ELEMENT_HEADER, true);
    }

    /**
     * Looks in the list of paths for the default file and returns the text of the first found
     * element.<p>
     * 
     * @param paths list of paths to search for a default file
     * @param elementName the name of the element in the default file
     * @return the text of the element of the default file or null if not found
     */
    private String getContentElement(List<String> paths, String elementName) {

        Iterator<String> iter = paths.iterator();
        while (iter.hasNext()) {
            String actPath = iter.next();
            String text = getContentElement(elementName, actPath);
            if (text != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found in: " + actPath);
                }
                return text;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("NOT Found in: " + actPath);
            }
        }

        return null;
    }

    /**
     * Looks for the default file in the given path and returns the text in the given element.<p>
     * 
     * @param elementName the name of the element
     * @param path the path to look for the default file
     * @return the text of the element or null if not found
     */
    private String getContentElement(String elementName, String path) {

        // try to open the default file with the xml content of the disclaimer
        CmsXmlContent xmlContent = getDefaultXmlContent(path);
        if (xmlContent != null) {

            // get the locale to use         
            Locale loc = m_cms.getRequestContext().getLocale();

            return xmlContent.getStringValue(m_cms.getCmsObject(), elementName, loc);
        }

        return null;
    }

    /**
     * Looks for the default file and returns the text in the given element. If flag to search is false
     * then the element will only be searched in the folder specified in the property. If the flag is
     * true then the paths of the current folder up to the root folder of the document center will be 
     * included in the search.<p>
     * 
     * @param property the name of the property where the name of the folder to search in is defined
     * @param elementName the name of the element
     * @param search the flag to search only in folder specified by the property or to search in parent folders
     * @return the text of the element of the default file or null if not found
     */
    private String getContentElement(String property, String elementName, boolean search) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Search: " + elementName);
        }

        List<String> paths = getFolderList(property, search);
        return getContentElement(paths, elementName);
    }

    /**
     * Looks for the default file and returns the text in the given element. If flag to search is false
     * then the element will only be searched in the folder specified in the property. If the flag is
     * true then the paths of the given searchRoot up to the root folder of the document center will be 
     * included in the search.<p>
     * 
     * @param property the name of the property where the name of the folder to search in is defined
     * @param elementName the name of the element
     * @param search the flag to search only in folder specified by the property or to search in parent folders
     * @param searchRoot the folder where to start the search for a default file
     * @return the text of the element of the default file or null if not found
     */
    private String getContentElement(String property, String elementName, boolean search, String searchRoot) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Search: " + elementName);
        }

        List<String> paths = getFolderList(property, search, searchRoot);
        return getContentElement(paths, elementName);
    }

    /**
     * Returns the default file in the given path as an xml content. If default file is not
     * found or the file is of wrong type null will be returned.<p>
     * 
     * @param path the path to look for the default file
     * @return the xml content or null if not found or incorrect type
     */
    private CmsXmlContent getDefaultXmlContent(String path) {

        // check if given path is valid
        if (path == null) {
            return null;
        }

        // check if path ends with slash
        if (!path.endsWith("/")) {
            path += "/";
        }

        try {

            // try to open the default file with the xml content of the disclaimer
            CmsResource res = m_cms.getCmsObject().readResource(path + DEFAULT_FILE);

            // check if found resource is from correct type
            if (res.getTypeId() != XMLDOCUMENT_TYPE) {
                return null;
            }

            // open the xml content and return element with disclaimer
            CmsObject cms = m_cms.getCmsObject();
            CmsFile file = cms.readFile(res);
            CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(cms, file);

            return xmlContent;
        } catch (CmsException ex) {
            return null;
        }
    }

    /**
     * Returns a list with all paths to look for a default file. The order is to first
     * look in the folder specified in the property and then in the actual path and all
     * parent folder up to the root folder of the document center.<p>
     * 
     * @param property the name of the property where the name of the folder to search in is defined
     * @param search flag if to search in parent folders too
     * @return a list with all found paths to look for a default file
     */
    private List<String> getFolderList(String property, boolean search) {

        return getFolderList(
            property,
            search,
            CmsFileUtil.addTrailingSeparator((String)m_cms.getRequest().getAttribute(CmsDocumentFrontend.ATTR_FULLPATH)));
    }

    /**
     * Returns a list with all paths to look for a default file. The order is to first
     * look in the folder specified in the property and then in the given startRoot and all
     * parent folder up to the root folder of the document center.<p>
     * 
     * @param property the name of the property where the name of the folder to search in is defined
     * @param search flag if to search in parent folders too
     * @param searchRoot the directory where to start the search for the default file
     * @return a list with all found paths to look for a default file
     */
    private List<String> getFolderList(String property, boolean search, String searchRoot) {

        ArrayList<String> ret = new ArrayList<String>();

        // look for the path defined in the property
        String path = m_cms.property(property, "search", null);
        if (path != null) {
            ret.add(path);
        }

        // if value is not going to be searched return only folder of property
        if (!search) {
            return ret;
        }

        // check if searchRoot ends with slash
        if ((searchRoot != null) && (!searchRoot.endsWith("/"))) {
            searchRoot += "/";
        }
        String docCenterStart = CmsFileUtil.addTrailingSeparator((String)m_cms.getRequest().getAttribute(
            CmsDocumentFrontend.ATTR_STARTPATH));

        try {

            // add actual path and all parent paths up to the document center
            boolean next = true;
            String currentPath = searchRoot;
            while ((next) && (currentPath != null)) {
                if (!ret.contains(currentPath)) {
                    ret.add(currentPath);
                }

                // check if current folder is already root folder of document center
                CmsResource currentRes = m_cms.getCmsObject().readResource(currentPath);
                if (currentPath.equals(docCenterStart)) {
                    next = false;
                }

                currentPath = CmsResource.getParentFolder(currentPath);
            }

            // if current resource is not inside a document center only add folder from property
            if (next) {
                ret = new ArrayList<String>();
                if (path != null) {
                    ret.add(path);
                }
            }
        } catch (CmsException ex) {
            // do nothing
        }

        return ret;
    }
}
