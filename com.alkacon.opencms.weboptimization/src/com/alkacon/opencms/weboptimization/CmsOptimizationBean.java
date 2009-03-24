/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationBean.java,v $
 * Date   : $Date: 2009/03/24 12:52:42 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.weboptimization;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.loader.CmsTemplateLoaderFacade;
import org.opencms.loader.CmsXmlContentLoader;
import org.opencms.loader.I_CmsResourceLoader;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.xml.content.CmsXmlContent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Base bean for optimization.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationBean extends CmsJspActionElement {

    /** type attribute constant. */
    protected static final String ATTR_TYPE = "type";

    /** Node name constant. */
    protected static final String N_LINE_BREAK_POS = "LineBreakPos";

    /** Node name constant. */
    protected static final String N_OPTIONS = "Options";

    /** Node name constant. */
    protected static final String N_PATH = "Path";

    /** Node resource constant. */
    protected static final String N_RESOURCE = "Resource";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsOptimizationBean.class);

    /**
     * Default constructor.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsOptimizationBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Creates a new HTML tag with the given name and attributes.<p>
     * 
     * @param tag the tag name
     * @param attr the attributes, can be <code>null</code>
     * @param xmlStyle if <code>true</code>,the tag will be closed in the xml style
     * 
     * @return the HTML code
     */
    protected String createTag(String tag, Map attr, boolean xmlStyle) {

        StringBuffer sb = new StringBuffer();
        sb.append("<");
        sb.append(tag);
        if (attr != null) {
            Iterator it = attr.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("=\"");
                sb.append(entry.getValue());
                sb.append("\"");
            }
        }
        sb.append(" ");
        if (xmlStyle) {
            sb.append("/");
        }
        sb.append(">");
        if (!xmlStyle) {
            sb.append("</");
            sb.append(tag);
            sb.append(">");
        }
        return sb.toString();
    }

    /**
     * Returns the content of all files of the given path with the 'right' extension.<p> 
     * 
     * @param cms the cms context
     * @param path the path to process, can be a folder
     * 
     * @return the content of all files
     * 
     * @throws Exception if something goes wrong 
     */
    protected String getAllContent(CmsObject cms, String path) throws Exception {

        // get the extension (should be .css or .js)
        String ext = cms.getRequestContext().getUri().substring(cms.getRequestContext().getUri().lastIndexOf('.'));

        // retrieve the actual files to process
        List resorces = resolveResource(cms, path, ext);
        if (resorces.isEmpty()) {
            LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_NOTHING_TO_PROCESS_1, path));
            return "";
        }

        // merge file contents
        StringBuffer sb = new StringBuffer();
        Iterator itRes = resorces.iterator();
        while (itRes.hasNext()) {
            CmsResource res = (CmsResource)itRes.next();
            byte[] data = getBinaryContent(cms, res);
            sb.append(new String(data, getRequestContext().getEncoding()));
        }
        return sb.toString();
    }

    /**
     * Returns the processed output of an OpenCms resource.<p>
     * 
     * @param resource the resource to process
     * 
     * @return the processed output
     * 
     * @throws Exception if something goes wrong
     */
    protected byte[] getBinaryContent(CmsObject cms, CmsResource resource) throws Exception {

        I_CmsResourceLoader loader = OpenCms.getResourceManager().getLoader(resource);
        // We need to handle separately xmlcontent files since we want the rendered content and not the xml data
        if (loader.getLoaderId() == CmsXmlContentLoader.RESOURCE_LOADER_ID) {
            // HACK: the A_CmsXmlDocumentLoader.getTemplatePropertyDefinition() method is not accessible :(
            String templatePropertyDef = CmsPropertyDefinition.PROPERTY_TEMPLATE_ELEMENTS;
            CmsTemplateLoaderFacade loaderFacade = OpenCms.getResourceManager().getTemplateLoaderFacade(cms, resource, templatePropertyDef);
            loader = loaderFacade.getLoader();
            String oldUri = cms.getRequestContext().getUri();
            try {
                cms.getRequestContext().setUri(cms.getSitePath(resource));
                resource = loaderFacade.getLoaderStartResource();
                return loader.dump(cms, resource, null, null, getRequest(), getResponse());
            } finally {
                cms.getRequestContext().setUri(oldUri);
            }
        }
        return loader.dump(cms, resource, null, null, getRequest(), getResponse());
    }

    /**
     * Resolves the right locale to use.<p>
     * 
     * @param cms the cms context
     * @param xml the xmlcontent
     * 
     * @return the locale to use
     */
    protected Locale resolveLocale(CmsObject cms, CmsXmlContent xml) {

        Locale locale = cms.getRequestContext().getLocale();
        if (!xml.hasLocale(locale)) {
            locale = OpenCms.getLocaleManager().getDefaultLocale(cms, cms.getRequestContext().getUri());
            if (!xml.hasLocale(locale)) {
                locale = CmsLocaleManager.getDefaultLocale();
                if (!xml.hasLocale(locale)) {
                    locale = (Locale)xml.getLocales().get(0);
                }
            }
        }
        return locale;
    }

    /**
     * Will check the extension, and if the path is a folder retrieve 
     * all files with the same extension in the folder.<p>
     * 
     * @param cms the cms context
     * @param path the resource path, can be a folder!
     * @param ext the file extension to filter
     * 
     * @return the list of files
     * 
     * @throws CmsException if something goes wrong
     */
    protected List resolveResource(CmsObject cms, String path, String ext) throws CmsException {

        CmsResource res = cms.readResource(path);

        List resorces = new ArrayList();
        if (res.isFolder()) {
            // if folder, get all files with the given extension in the folder
            List files = cms.readResources(path, CmsResourceFilter.DEFAULT_FILES);
            Iterator itFiles = files.iterator();
            while (itFiles.hasNext()) {
                CmsResource file = (CmsResource)itFiles.next();
                if (file.getRootPath().endsWith(ext)) {
                    resorces.add(file);
                }
            }
        } else {
            if (res.getRootPath().endsWith(ext)) {
                resorces.add(res);
            }
        }
        return resorces;
    }
}
