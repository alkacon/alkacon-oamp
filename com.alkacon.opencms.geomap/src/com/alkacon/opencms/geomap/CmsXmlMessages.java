/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.geomap/src/com/alkacon/opencms/geomap/CmsXmlMessages.java,v $
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

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Provides localized keys from an xml content.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsXmlMessages extends CmsMessages {

    /** Macro prefix. */
    public static final String MACRO_PREFIX = "xml.";

    /** The cms context. */
    private CmsObject m_cms;

    /** The content holding the localized values. */
    private CmsXmlContent m_content;

    /** The locale to use for getting localized values. */
    private Locale m_locale;

    /** The (optional) xPath prefix to the element nodes. */
    private String m_pathPrefix;

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     */
    public CmsXmlMessages(CmsObject cms) {

        this(cms, null, (Locale)null, null);
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     * @param uri the optional path to the xml content file containing localized keys, if <code>null</code> the current uri will be used
     */
    public CmsXmlMessages(CmsObject cms, String uri) {

        this(cms, uri, (Locale)null, null);
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     * @param uri the optional path to the xml content file containing localized keys, if <code>null</code> the current uri will be used
     * @param locale the optional locale to use for localization, if <code>null</code> the current locale will be used
     */
    public CmsXmlMessages(CmsObject cms, String uri, Locale locale) {

        this(cms, uri, locale, null);
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     * @param uri the optional path to the xml content file containing localized keys, if <code>null</code> the current uri will be used
     * @param locale the optional locale to use for localization, if <code>null</code> the current locale will be used
     * @param pathPrefix the optional xPath prefix to the element nodes
     */
    public CmsXmlMessages(CmsObject cms, String uri, Locale locale, String pathPrefix) {

        m_cms = cms;
        m_content = initContent(cms, uri);
        m_locale = initLocale(cms, m_content, locale);
        m_pathPrefix = initPathPrefix(pathPrefix);
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     * @param uri the optional path to the xml content file containing localized keys, if <code>null</code> the current uri will be used
     * @param locale the optional locale to use for localization, if <code>null</code> the current locale will be used
     */
    public CmsXmlMessages(CmsObject cms, String uri, String locale) {

        this(cms, uri, locale, null);
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * Creates the necessary member objects using the passed arguments.<p>
     * 
     * @param cms the cms context
     * @param uri the optional path to the xml content file containing localized keys, if <code>null</code> the current uri will be used
     * @param locale the optional locale to use for localization, if <code>null</code> the current locale will be used
     * @param pathPrefix the optional xPath prefix to the element nodes
     */
    public CmsXmlMessages(CmsObject cms, String uri, String locale, String pathPrefix) {

        this(cms, uri, (locale == null ? null : CmsLocaleManager.getLocale(locale)), pathPrefix);
    }

    /**
     * Returns a new macro resolver that will also resolve message macros.
     * 
     * If you have a message with xpath 'Abc/Def', you can use macro 'xml.abc.def' to access to it.
     * 
     * @return a new macro resolver
     */
    public CmsMacroResolver getMacroResolver() {

        return new CmsMacroResolver() {

            /**
             * @see org.opencms.util.CmsMacroResolver#getMacroValue(java.lang.String)
             */
            public String getMacroValue(String macro) {

                String value = super.getMacroValue(macro);
                if ((value != null) || !macro.startsWith(MACRO_PREFIX)) {
                    return value;
                }
                // cut macro name
                String macroName = macro.substring(MACRO_PREFIX.length());
                int pipePos = macro.indexOf('|');
                if (pipePos > 0) {
                    macroName = macro.substring(0, pipePos);
                }
                // camelize macro name
                String xpath = "";
                int pos = 0;
                while (pos >= 0) {
                    xpath += Character.toUpperCase(macroName.charAt(pos));
                    int newPos = macroName.indexOf('.', pos) + 1;
                    if (newPos == 0) {
                        newPos = macroName.length();
                    }
                    xpath += macroName.substring(pos + 1, newPos);
                    if (newPos == macroName.length()) {
                        newPos = -1;
                    }
                    pos = newPos;
                }
                // get the key for the given xpath
                xpath = xpath.replace('.', '/');
                // append possible parameters
                if (pipePos > 0) {
                    xpath += macro.substring(pipePos);
                }
                return keyWithParams(xpath);
            }
        };
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String)
     */
    public String key(String keyName) {

        return m_content.getStringValue(m_cms, m_pathPrefix + keyName, m_locale);
    }

    /**
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String, boolean)
     */
    public String key(String keyName, boolean allowNull) {

        try {
            return key(keyName);
        } catch (MissingResourceException e) {
            // not found, return warning
            if (allowNull) {
                return null;
            }
        }
        return formatUnknownKey(keyName);
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String, java.lang.Object)
     */
    public String key(String key, Object arg0) {

        return key(key, new Object[] {arg0});
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public String key(String key, Object arg0, Object arg1) {

        return key(key, new Object[] {arg0, arg1});
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public String key(String key, Object arg0, Object arg1, Object arg2) {

        return key(key, new Object[] {arg0, arg1, arg2});
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#key(java.lang.String, java.lang.Object[])
     */
    public String key(String key, Object[] args) {

        String value = key(key);
        CmsMacroResolver resolver = CmsMacroResolver.newInstance();
        for (int i = 0; i < args.length; i++) {
            resolver.addMacro(String.valueOf(i), args[i].toString());
        }
        return resolver.resolveMacros(value);
    }

    /**
     * Returns the localized resource String from the configuration file, if not found or set from the resource bundle.<p>
     * 
     * @see org.opencms.i18n.CmsMessages#keyDefault(java.lang.String, java.lang.String)
     */
    public String keyDefault(String keyName, String defaultValue) {

        String res = key(keyName);
        if (res != null) {
            return res;
        }
        return defaultValue;
    }

    /**
     * Initializes the content used for localizing the output.<p>
     * 
     * @param cms the cms context
     * @param uri the path to the xml content file containing localized keys
     * 
     * @return the unmarshalled xml content
     */
    protected CmsXmlContent initContent(CmsObject cms, String uri) {

        if (uri == null) {
            uri = cms.getRequestContext().getUri();
        }
        String rootPath = cms.getRequestContext().addSiteRoot(uri);

        CmsXmlContent content = null;
        // try to get XML content from cache
        Object o = CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().getCachedObject(cms, rootPath);
        if (o != null) {
            // found the cached XML content, use it
            content = (CmsXmlContent)o;
        } else {
            try {
                CmsFile configFile = cms.readFile(uri);
                content = CmsXmlContentFactory.unmarshal(cms, configFile);
                // store unmarshaled content in cache
                CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().putCachedObject(cms, uri, content);
            } catch (CmsException e) {
                // ignore, no configuration file found
            }
        }
        return content;
    }

    /**
     * Resolves the right locale to use.<p>
     * 
     * @param cms the cms context
     * @param xml the xmlcontent
     * @param locale the default locale
     * 
     * @return the locale to use
     */
    protected Locale initLocale(CmsObject cms, CmsXmlContent xml, Locale locale) {

        if (locale == null) {
            locale = cms.getRequestContext().getLocale();
        }
        if (!xml.hasLocale(locale)) {
            locale = OpenCms.getLocaleManager().getDefaultLocale(cms, cms.getSitePath(xml.getFile()));
            if (!xml.hasLocale(locale)) {
                locale = CmsLocaleManager.getDefaultLocale();
                if (!xml.hasLocale(locale)) {
                    locale = xml.getLocales().get(0);
                }
            }
        }
        return locale;
    }

    /**
     * Initializes the (optional) xPath prefix to the element nodes preventing NPEs.<p>
     * 
     * @param pathPrefix the (optional) xPath prefix to the element nodes
     * 
     * @return the pathPrefix or an empty string if <code>null</code>
     */
    protected String initPathPrefix(String pathPrefix) {

        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(pathPrefix)) {
            return pathPrefix;
        }
        return "";
    }
}