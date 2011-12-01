/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.weboptimization/src/com/alkacon/opencms/v8/weboptimization/CmsOptimizationCss.java,v $
 * Date   : $Date: 2010/01/08 09:46:05 $
 * Version: $Revision: 1.3 $
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

package com.alkacon.opencms.v8.weboptimization;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsIllegalArgumentException;
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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * Bean for optimizing css files.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3 $ 
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
	 * @see com.alkacon.opencms.v8.weboptimization.CmsOptimizationBean#includeDefault(java.lang.String,
	 *      com.alkacon.opencms.v8.weboptimization.CmsOptimizationBean.IncludeMode)
	 */
	@Override
	public void includeDefault(String path, IncludeMode mode) throws Exception {

		CmsObject cms = getCmsObject();
		// check the resource type
		CmsFile file = cms.readFile(path);
		if (file.getTypeId() != RESOURCE_TYPE_CSS) {
			throw new CmsIllegalArgumentException(Messages.get().container(
					Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2, path,
					new Integer(file.getTypeId())));
		}
		if (mode == IncludeMode.OPTIMIZED) {
			// if we are forcing optimization
			// handle this resource
			writeCssInclude(cms, path);
			return;
		}
		
		Resolution resolution = resolveInclude(cms, file, mode, EXT_CSS, RESOURCE_TYPE_CSS);
		if (resolution.hasOptimizedLeft()) {
			// handle this resource
			writeCssInclude(cms, path);
		}
		Iterator<CmsResource> itRes = resolution.getResources().iterator();
		while (itRes.hasNext()) {
			CmsResource res = itRes.next();
			String resPath = cms.getSitePath(res);
			// handle this resource
			writeCssInclude(cms, resPath);
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
		// cache the current project
		boolean online = cms.getRequestContext().currentProject().isOnlineProject();

        // iterate the resources
        Iterator<I_CmsXmlContentValue> itRes = xml.getValues(N_RESOURCE, locale).iterator();
        while (itRes.hasNext()) {
            I_CmsXmlContentValue value = itRes.next();
            if (!isOptimized(cms, xml, value, locale, online)) {
            	continue;
            }
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

        Map<String,String> attrs = new HashMap<String,String>();
        attrs.put(ATTR_HREF, link(uri));
        attrs.put(ATTR_TYPE, TYPE_TEXT_CSS);
        attrs.put(ATTR_REL, REL_STYLESHEET);
        attrs.put(ATTR_MEDIA, MEDIA_ALL);
        getJspContext().getOut().println(createTag(TAG_LINK, attrs, true));
    }
}
