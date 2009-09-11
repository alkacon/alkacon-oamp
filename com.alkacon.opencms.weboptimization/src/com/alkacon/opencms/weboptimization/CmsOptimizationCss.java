/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationCss.java,v $
 * Date   : $Date: 2009/09/11 07:39:52 $
 * Version: $Revision: 1.2 $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * Bean for optimizing css files.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationCss extends CmsOptimizationBean {

    /** href attribute constant. */
    protected static final String ATTR_HREF = "href";

    /** media attribute constant. */
    protected static final String ATTR_MEDIA = "media";

    /** rel attribute constant. */
    protected static final String ATTR_REL = "rel";

    /** css file extension constant. */
    protected static final String EXT_CSS = ".css";

    /** media all value constant. */
    protected static final String MEDIA_ALL = "all";

    /** stylesheet rel constant. */
    protected static final String REL_STYLESHEET = "stylesheet";

    /** Optimized css resource type constant. */
    protected static final int RESOURCE_TYPE_CSS = 763;

    /** link tag constant. */
    protected static final String TAG_LINK = "link";

    /** text/css type constant. */
    protected static final String TYPE_TEXT_CSS = "text/css";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsOptimizationCss.class);

    /**
     * Default constructor.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsOptimizationCss(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Include the given optimized file, will include the original 
     * files in the offline project for debugging purposes.<p>
     * 
     * @param path the uri of the file to be included
     * 
     * @throws Exception if something goes wrong
     */
    public void includeDefault(String path) throws Exception {

        if (getCmsObject().getRequestContext().currentProject().isOnlineProject()) {
            includeOptimized(path);
        } else {
            includeOriginal(path);
        }
    }

    /**
     * Will include the optimized file.<p>
     * 
     * @param path the optimized file uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOptimized(String path) throws Exception {

        CmsObject cms = getCmsObject();
        // check the resource type
        CmsResource res = cms.readResource(path);
        if (res.getTypeId() != RESOURCE_TYPE_CSS) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2,
                path,
                new Integer(res.getTypeId())));
        }
        writeCssInclude(cms, path);
    }

    /**
     * Will include the original files.<p>
     * 
     * @param path the optimized file uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOriginal(String path) throws Exception {

        CmsObject cms = getCmsObject();
        CmsFile file = cms.readFile(path);

        // check the resource type
        if (file.getTypeId() != RESOURCE_TYPE_CSS) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2,
                path,
                new Integer(file.getTypeId())));
        }

        // read the XML content
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, file);

        // resolve the locale
        Locale locale = resolveLocale(cms, xml);

        // iterate the resources
        Iterator itPath = xml.getValues(N_RESOURCE, locale).iterator();
        while (itPath.hasNext()) {
            I_CmsXmlContentValue value = (I_CmsXmlContentValue)itPath.next();
            // get the uri
            String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_PATH);
            String uri = xml.getValue(xpath, locale).getStringValue(cms);

            // retrieve the actual files to process
            List resorces = resolveResource(cms, uri, EXT_CSS);
            if (resorces.isEmpty()) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_NOTHING_TO_PROCESS_1, uri));
                continue;
            }

            Iterator itRes = resorces.iterator();
            while (itRes.hasNext()) {
                CmsResource res = (CmsResource)itRes.next();
                String resPath = cms.getSitePath(res);
                if (res.getTypeId() == RESOURCE_TYPE_CSS) {
                    // recurse in case of nested optimized resources
                    includeOriginal(resPath);
                } else {
                    // handle this resource
                    writeCssInclude(cms, resPath);
                }
            }
        }
    }

    /**
     * Will optimize the resources taken from the underlying XML content.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void optimize() throws Exception {

        CmsObject cms = getCmsObject();
        CmsFile file = cms.readFile(cms.getRequestContext().getUri());

        // check the resource type
        if (file.getTypeId() != RESOURCE_TYPE_CSS) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2,
                cms.getRequestContext().getUri(),
                new Integer(file.getTypeId())));
        }

        // read the XML content
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, file);

        // resolve the locale
        Locale locale = resolveLocale(cms, xml);

        // iterate the resources
        Iterator itRes = xml.getValues(N_RESOURCE, locale).iterator();
        while (itRes.hasNext()) {
            I_CmsXmlContentValue value = (I_CmsXmlContentValue)itRes.next();
            // get the path
            String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_PATH);
            String path = xml.getValue(xpath, locale).getStringValue(cms);
            // get the options
            CmsOptimizationCssOptions opts = new CmsOptimizationCssOptions();
            xpath = CmsXmlUtils.concatXpath(value.getPath(), N_OPTIONS);
            if (xml.hasValue(xpath, locale)) {
                value = xml.getValue(xpath, locale);
                xpath = CmsXmlUtils.concatXpath(value.getPath(), N_LINE_BREAK_POS);
                opts.setLineBreakPos(Integer.parseInt(xml.getStringValue(cms, xpath, locale)));
            }
            // process this resource
            optimizeCss(getAllContent(cms, path), opts);
        }
    }

    /**
     * Optimizes css code with the given options.<p>
     * 
     * @param cssCode the code to compress
     * @param options the options to use
     * 
     * @throws Exception if something goes wrong
     */
    public void optimizeCss(String cssCode, CmsOptimizationCssOptions options) throws Exception {

        Reader reader = new BufferedReader(new StringReader(cssCode));
        try {
            // process the js code
            CssCompressor cssc = new CssCompressor(reader);
            cssc.compress(getJspContext().getOut(), options.getLineBreakPos());
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Writes a new link css tag for the given resource.<p>
     * 
     * @param cms the cms context
     * @param uri the resource to include
     * 
     * @throws IOException if something goes wrong 
     */
    public void writeCssInclude(CmsObject cms, String uri) throws IOException {

        Map attrs = new HashMap();
        attrs.put(ATTR_HREF, link(uri));
        attrs.put(ATTR_TYPE, TYPE_TEXT_CSS);
        attrs.put(ATTR_REL, REL_STYLESHEET);
        attrs.put(ATTR_MEDIA, MEDIA_ALL);
        getJspContext().getOut().println(createTag(TAG_LINK, attrs, true));
    }
}
