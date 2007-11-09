/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterXmlContentHandler.java,v $
 * Date   : $Date: 2007/11/09 15:26:41 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
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
 
package com.alkacon.opencms.newsletter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.OpenCms;
import org.opencms.site.CmsSite;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsDefaultXmlContentHandler;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.Locale;

/**
 * Special XML content handler required for the newsletter contents because of the macro replacement
 * behaviour changes in the default values.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterXmlContentHandler extends CmsDefaultXmlContentHandler {
    
    /** The domain macro that can be used in the default value configurations. */
    private static final String MACRO_DOMAIN = "domain";
    
    /**
     * Creates a new instance of the newsletter XML content handler.<p>  
     */
    public CmsNewsletterXmlContentHandler() {

        init();
    }

    
    /**
     * Overwrites the default method because the macro resolver should keep empty macros.<p>
     * 
     * @see org.opencms.xml.content.I_CmsXmlContentHandler#getDefault(org.opencms.file.CmsObject, I_CmsXmlContentValue, java.util.Locale)
     */
    public String getDefault(CmsObject cms, I_CmsXmlContentValue value, Locale locale) {

        String defaultValue;
        if (value.getElement() == null) {
            // use the "getDefault" method of the given value, will use value from standard XML schema
            defaultValue = value.getDefault(locale);
        } else {
            String xpath = value.getPath();
            // look up the default from the configured mappings
            defaultValue = (String)m_defaultValues.get(xpath);
            if (defaultValue == null) {
                // no value found, try default xpath
                xpath = CmsXmlUtils.removeXpath(xpath);
                xpath = CmsXmlUtils.createXpath(xpath, 1);
                // look up the default value again with default index of 1 in all path elements
                defaultValue = (String)m_defaultValues.get(xpath);
            }
        }
        if (defaultValue != null) {
            CmsObject newCms = cms;
            try {
                // switch the current URI to the XML document resource so that properties can be read
                CmsResource file = value.getDocument().getFile();
                CmsSite site = OpenCms.getSiteManager().getSiteForRootPath(file.getRootPath());
                if (site != null) { 
                newCms = OpenCms.initCmsObject(cms);
                newCms.getRequestContext().setSiteRoot(site.getSiteRoot());
                newCms.getRequestContext().setUri(newCms.getSitePath(file));
                }
            } catch (Exception e) {
                // on any error just use the default input OpenCms context
            }
            // return the default value with processed macros
            CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(newCms).setMessages(
                getMessages(locale));
            CmsSite site = OpenCms.getSiteManager().getSiteForSiteRoot(cms.getRequestContext().getSiteRoot());
            String serverName = "";
            if (site != null) {
                serverName = site.getSiteMatcher().getServerName();
            }
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(serverName) || "*".equals(serverName)) {
                serverName = "yourdomain.com";
            }
            resolver.addMacro(MACRO_DOMAIN, serverName);
            resolver.setKeepEmptyMacros(true);
            return resolver.resolveMacros(defaultValue);
        }
        // no default value is available
        return null;
    }

}
