/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.geomap/src/com/alkacon/opencms/geomap/CmsGoogleMapWidget.java,v $
 * Date   : $Date: 2011/02/16 13:05:25 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2008 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.geomap;

import com.alkacon.opencms.geomap.CmsGoogleMapWidgetConfig.CmsGoogleMapOption;

import org.opencms.cache.CmsMemoryObjectCache;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.loader.I_CmsResourceLoader;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsMacroResolver;
import org.opencms.widgets.A_CmsWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsWorkplace;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

/**
 * Provides a widget that for choosing a location with a Google Map.<p>
 *
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.5
 */
public class CmsGoogleMapWidget extends A_CmsWidget {

    /** Default header include file URI. */
    protected static final String HEADER_FILE = "/system/modules/com.alkacon.opencms.geomap/elements/header.include.html";

    /** Default xml messages file URI. */
    protected static final String MESSAGES_FILE = "/system/modules/com.alkacon.opencms.geomap/elements/messages.xml";

    /** Default html template file URI. */
    protected static final String TEMPLATE_FILE = "/system/modules/com.alkacon.opencms.geomap/elements/html-templates.xml";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsGoogleMapWidget.class);

    /** The configured map widget options. */
    private CmsGoogleMapWidgetConfig m_widgetOption;

    /**
     * Creates a new map widget.<p>
     */
    public CmsGoogleMapWidget() {

        // empty constructor is required for class registration
        this("");
    }

    /**
     * Creates a new map widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsGoogleMapWidget(final CmsGoogleMapWidgetConfig configuration) {

        super();
        m_widgetOption = configuration;
    }

    /**
     * Creates a new map widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsGoogleMapWidget(final String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.A_CmsWidget#getConfiguration()
     */
    @Override
    public String getConfiguration() {

        if (super.getConfiguration() != null) {
            return super.getConfiguration();
        }
        return getWidgetOption().toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogIncludes(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogIncludes(final CmsObject cms, final I_CmsWidgetDialog widgetDialog) {

        // HACK: this is not very save
        final CmsJspActionElement jsp = ((CmsWorkplace)widgetDialog).getJsp();
        return getCachedFileContent(cms, CmsGoogleMapWidget.HEADER_FILE, jsp.getRequest(), jsp.getResponse());
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogInitMethod(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogInitMethod(final CmsObject cms, final I_CmsWidgetDialog widgetDialog) {

        return "";
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    public String getDialogWidget(
        final CmsObject cms,
        final I_CmsWidgetDialog widgetDialog,
        final I_CmsWidgetParameter param) {

        final String id = param.getId();
        final CmsGoogleMapWidgetValue value = new CmsGoogleMapWidgetValue(param.getStringValue(cms));
        final CmsXmlMessages templates = new CmsXmlMessages(cms, CmsGoogleMapWidget.TEMPLATE_FILE);

        // create macro resolver with macros for form field value replacement
        final CmsMacroResolver resolver = new CmsXmlMessages(cms, CmsGoogleMapWidget.MESSAGES_FILE).getMacroResolver();
        // set cms object and localized messages in resolver
        resolver.setCmsObject(cms);
        resolver.addMacro("id", id);
        resolver.addMacro("value", value.toString());
        resolver.addMacro("options", getWidgetOption().getEditString());
        resolver.addMacro("button", resolver.resolveMacros(templates.key("Button")));
        resolver.addMacro("node", param.getName());
        resolver.addMacro("width", "" + value.getWidth());
        resolver.addMacro("height", "" + value.getHeight());

        final StringBuffer sbInline = new StringBuffer();
        final Iterator<CmsGoogleMapOption> itInline = getWidgetOption().getInline().iterator();
        while (itInline.hasNext()) {
            final CmsGoogleMapOption prop = itInline.next();
            final StringBuffer xpath = new StringBuffer(prop.toString());
            xpath.setCharAt(0, Character.toUpperCase(xpath.charAt(0)));
            sbInline.append(resolver.resolveMacros(templates.key(xpath.toString())));
        }
        resolver.addMacro("inline.properties", sbInline.toString());

        final StringBuffer sbPopup = new StringBuffer();
        final Iterator<CmsGoogleMapOption> itPopup = getWidgetOption().getPopup().iterator();
        while (itPopup.hasNext()) {
            final CmsGoogleMapOption prop = itPopup.next();
            final StringBuffer xpath = new StringBuffer(prop.toString());
            xpath.setCharAt(0, Character.toUpperCase(xpath.charAt(0)));
            sbPopup.append(resolver.resolveMacros(templates.key(xpath.toString())));
        }
        resolver.addMacro("popup.properties", sbPopup.toString());

        final StringBuffer result = new StringBuffer(4096);
        result.append("<td class=\"xmlTd\">");
        result.append(resolver.resolveMacros(templates.key("Main")));
        result.append("</td>");

        return result.toString();
    }

    /**
     * Returns the configured map widget options.<p>
     * 
     * @return the configured map widget options
     */
    public CmsGoogleMapWidgetConfig getWidgetOption() {

        return m_widgetOption;
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    public I_CmsWidget newInstance() {

        return new CmsGoogleMapWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#setConfiguration(java.lang.String)
     */
    @Override
    public void setConfiguration(final String configuration) {

        super.setConfiguration(configuration);
        m_widgetOption = new CmsGoogleMapWidgetConfig(configuration);
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#setEditorValue(org.opencms.file.CmsObject, java.util.Map, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    public void setEditorValue(
        final CmsObject cms,
        final Map formParameters,
        final I_CmsWidgetDialog widgetDialog,
        final I_CmsWidgetParameter param) {

        final String[] values = (String[])formParameters.get(param.getId());
        if ((values != null) && (values.length > 0)) {
            final String val = CmsEncoder.decode(values[0], CmsEncoder.ENCODING_UTF_8);
            param.setStringValue(cms, val);
        }
    }

    /**
     * Sets the configured map widget options.<p>
     * 
     * @param widgetOption the configured map widget options
     */
    public void setWidgetOption(final CmsGoogleMapWidgetConfig widgetOption) {

        setConfiguration(widgetOption.toString());
    }

    /**
     * Returns the cached content of the given file.<p>
     * 
     * @param cms the cms context
     * @param fileName the VFS file name
     * @param req the jsp request
     * @param res the jsp response
     *  
     * @return the cached file content
     */
    protected String getCachedFileContent(
        final CmsObject cms,
        final String fileName,
        final HttpServletRequest req,
        final HttpServletResponse res) {

        // check if the selected include file is available in the cache
        final CmsMemoryObjectCache cache = CmsMemoryObjectCache.getInstance();
        String cachedContent = (String)cache.getCachedObject(CmsGoogleMapWidget.class, fileName);

        if (cachedContent == null) {
            // the file is not available in the cache
            try {
                final CmsResource file = cms.readResource(fileName);
                // get the encoding for the resource
                final CmsProperty p = cms.readPropertyObject(
                    file,
                    CmsPropertyDefinition.PROPERTY_CONTENT_ENCODING,
                    true);
                String e = p.getValue();
                if (e == null) {
                    e = OpenCms.getSystemInfo().getDefaultEncoding();
                }
                // create a String with the right encoding
                final I_CmsResourceLoader loader = OpenCms.getResourceManager().getLoader(file);
                final byte[] contents = loader.dump(cms, file, null, null, req, res);
                cachedContent = CmsEncoder.createString(contents, e);
                // store this in the cache
                cache.putCachedObject(CmsGoogleMapWidget.class, fileName, cachedContent);

            } catch (final Exception e) {
                // this should better not happen
                cachedContent = "";
                CmsGoogleMapWidget.LOG.error(
                    Messages.get().getBundle().key(Messages.LOG_ERR_FILE_CONTENT_1, fileName),
                    e);
            }
        }
        return cachedContent;
    }
}