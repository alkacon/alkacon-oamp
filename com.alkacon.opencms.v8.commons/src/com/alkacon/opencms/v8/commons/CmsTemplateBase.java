/*
 * File   : $Source: /home/cvs/EbkModules/src-modules/org/opencms/frontend/templateone/CmsTemplateBase.java,v $
 * Date   : $Date: 2008-12-09 14:07:47 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.commons;

import org.opencms.ade.configuration.CmsADEConfigData;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResourceFilter;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Base action element for all template one beans.<p>
 * 
 * @author Alexander Kandzior
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.1.0
 */
public class CmsTemplateBase extends CmsJspActionElement {

    /** The old uri is stored in this parameter (if required). */
    public static final String ATTRIBUTE_ORIGINAL_URI = "__originalOpenCmsUri";

    /** Request parameter name for the uri. */
    public static final String PARAM_URI = "uri";

    /** Name of the property key to init RSS feeds for events. */
    public static final String PROPERTY_RSSFEED_EVENTS = "rssfeed.events";

    /** Name of the property key to init RSS feeds for news. */
    public static final String PROPERTY_RSSFEED_NEWS = "rssfeed.news";

    /** Name of the property key to init RSS feeds for services. */
    public static final String PROPERTY_RSSFEED_SERVICES = "rssfeed.services";

    /** Name of the property key to init if RSS feeds detail is active. */
    public static final String PROPERTY_RSSFEEDDETAIL_ACTIVE = "rssfeeddetail.active";

    /** Name of the property key to init RSS feeds detail title. */
    public static final String PROPERTY_RSSFEEDDETAIL_TITLE = "rssfeeddetail.title";

    /** File name of the rss feed configuration file. */
    public static final String RSSFEED_CONFIG_FILE = "rssfeed";

    /** Default title for RSS feed events. */
    public static final String RSSFEED_EVENTS_DEFAULT_TITLE = "Veranstaltungen";

    /** Default title for RSS feed news. */
    public static final String RSSFEED_NEWS_DEFAULT_TITLE = "Aktuelles";

    /** Default title for RSS feed service. */
    public static final String RSSFEED_SERVICES_DEFAULT_TITLE = "Gottesdienste";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsTemplateBase.class);

    /** Stores the rss feed configuration. */
    private CmsXmlContent m_rssFeedConfiguration;

    /** Stores the path to the start folder for navigation and search. */
    private String m_startFolder;

    /**
     * Empty constructor, required for every JavaBean.<p>
     */
    public CmsTemplateBase() {

        super();
    }

    /**
     * @see CmsJspActionElement#CmsJspActionElement(PageContext, HttpServletRequest, HttpServletResponse)
     */
    public CmsTemplateBase(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Returns the initialized xmlcontent configuration file.<p>
     * 
     * @param fileName the absolute path to the configuration file
     * @param cms the CmsObject to access the VFS
     * @return the initialized xmlcontent configuration file
     */
    protected static CmsXmlContent getConfigurationFile(String fileName, CmsObject cms) {

        CmsXmlContent configuration = null;
        try {
            CmsFile configFile = cms.readFile(fileName, CmsResourceFilter.IGNORE_EXPIRATION);
            configuration = CmsXmlContentFactory.unmarshal(cms, configFile);
        } catch (Exception e) {
            // problem getting properties, log error
            if (LOG.isInfoEnabled()) {
                /**
                LOG.info(Messages.get().getBundle().key(
                    
                    Messages.LOG_XMLCONTEN_CONFIG_NOT_FOUND_2,
                    fileName,
                    cms.getRequestContext().getUri()));
                */
            }
        }
        return configuration;
    }

    /**
     * Returns the original OpenCms request context URI thas has been changed, or <code>null</code>
     * if the URI was not changed.<p>
     * 
     * @return the original OpenCms request context URI thas has been changed, or <code>null</code>
     * if the URI was not changed
     */
    public String getOriginalUri() {

        return (String)getRequest().getAttribute(ATTRIBUTE_ORIGINAL_URI);
    }

    /**
     * Returns the rss feed configuration properties for the current web site area.<p>
     * 
     * @return the rss feed configuration properties
     */
    public CmsXmlContent getRssFeedConfiguration() {

        if (m_rssFeedConfiguration == null) {
            m_rssFeedConfiguration = getConfigurationFile(getStartFolder()
                + CmsADEConfigData.CONTENT_FOLDER_NAME
                + "/"
                + RSSFEED_CONFIG_FILE, getCmsObject());
        }
        return m_rssFeedConfiguration;
    }

    /**
     * Returns the value for the specified property key name from the configuration.<p>
     * 
     * Returns the default value argument if the property is not found.<p>
     * 
     * @param key the property key name to look up
     * @param defaultValue a default value
     * @return the value for the specified property key name
     */
    public String getRssFeedConfigurationValue(String key, String defaultValue) {

        String value = null;
        try {
            value = getRssFeedConfiguration().getStringValue(null, key, getRequestContext().getLocale());
        } catch (Exception e) {
            // log error in debug mode
            if (LOG.isDebugEnabled()) {
                LOG.debug(e);
            }
        }
        if (CmsStringUtil.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Returns the start folder for navigation and search results.<p>
     * 
     * First it is tried to find a parent folder of type document center, then a subsitemap and finally the 
     * root folder is used if no folder
     * 
     * @return the start folder for navigation and search results
     */
    public String getStartFolder() {

        if (m_startFolder == null) {
            m_startFolder = getCmsObject().getRequestContext().removeSiteRoot(
                OpenCms.getADEManager().getSubSiteRoot(getCmsObject(), getCmsObject().getRequestContext().getRootUri()));
        }
        return m_startFolder;
    }

    /**
     * Initializes the RSS feed parameters.<p>
     * 
     * @return Html RSS feed code
     */
    public String initRssFeed() {

        StringBuffer rssFeedHtmlCode = new StringBuffer(1024);
        // check for rss feed for news
        boolean newsRssFeed = Boolean.valueOf(
            getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_NEWS + "/" + PROPERTY_RSSFEEDDETAIL_ACTIVE,
                CmsStringUtil.FALSE)).booleanValue();
        if (newsRssFeed) {
            String title = getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_NEWS + "/" + PROPERTY_RSSFEEDDETAIL_TITLE,
                RSSFEED_NEWS_DEFAULT_TITLE);
            rssFeedHtmlCode.append("<link rel=\"alternate\" type=\"application/rss+xml\" title=\""
                + title
                + "\" href=\"");
            rssFeedHtmlCode.append(link("/system/modules/com.alkacon.opencms.feeder/resources/rssfeed-news-v8.html?path="));
            rssFeedHtmlCode.append(getStartFolder());
            rssFeedHtmlCode.append("&configfile="
                + CmsADEConfigData.CONTENT_FOLDER_NAME
                + "/"
                + RSSFEED_CONFIG_FILE
                + "\">");
        }
        // check for rss feed for events
        boolean eventsRssFeed = Boolean.valueOf(
            getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_EVENTS + "/" + PROPERTY_RSSFEEDDETAIL_ACTIVE,
                CmsStringUtil.FALSE)).booleanValue();
        if (eventsRssFeed) {
            String title = getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_EVENTS + "/" + PROPERTY_RSSFEEDDETAIL_TITLE,
                RSSFEED_EVENTS_DEFAULT_TITLE);
            rssFeedHtmlCode.append("<link rel=\"alternate\" type=\"application/rss+xml\" title=\""
                + title
                + "\" href=\"");
            rssFeedHtmlCode.append(link("/system/modules/com.alkacon.opencms.feeder/resources/rssfeed-events-v8.html?path="));
            rssFeedHtmlCode.append(getStartFolder());
            rssFeedHtmlCode.append("&configfile="
                + CmsADEConfigData.CONTENT_FOLDER_NAME
                + "/"
                + RSSFEED_CONFIG_FILE
                + "\">");
        }
        // check for rss feed for services
        boolean servicesRssFeed = Boolean.valueOf(
            getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_SERVICES + "/" + PROPERTY_RSSFEEDDETAIL_ACTIVE,
                CmsStringUtil.FALSE)).booleanValue();
        if (servicesRssFeed) {
            String title = getRssFeedConfigurationValue(
                PROPERTY_RSSFEED_EVENTS + "/" + PROPERTY_RSSFEED_SERVICES,
                RSSFEED_SERVICES_DEFAULT_TITLE);
            rssFeedHtmlCode.append("<link rel=\"alternate\" type=\"application/rss+xml\" title=\""
                + title
                + "\" href=\"");
            rssFeedHtmlCode.append(link("/system/modules/com.alkacon.opencms.feeder/resources/rssfeed-services-v8.html?path="));
            rssFeedHtmlCode.append(getStartFolder());
            rssFeedHtmlCode.append("&configfile="
                + CmsADEConfigData.CONTENT_FOLDER_NAME
                + "/"
                + RSSFEED_CONFIG_FILE
                + "\">");
        }
        return rssFeedHtmlCode.toString();
    }

    /**
     * Initializes the URI of the current template page.<p>
     * 
     * This checks for the presence of a special <code>uri</code> parameter.
     * If this parameter is found, the OpenCms request context URI is switched to this value.<p>
     */
    public void initUri() {

        String uri = getRequest().getParameter(PARAM_URI);
        if (CmsStringUtil.isNotEmpty(uri) && getCmsObject().existsResource(uri)) {
            getRequest().setAttribute(ATTRIBUTE_ORIGINAL_URI, getRequestContext().getUri());
            getRequestContext().setUri(uri);
        }
    }

    /**
     * Returns <code>true</code> if the OpenCms request context URI has been changed.<p>
     * 
     * @return <code>true</code> if the OpenCms request context URI has been changed
     */
    public boolean isUriChanged() {

        return null != getOriginalUri();
    }
}