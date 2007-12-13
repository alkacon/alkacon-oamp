/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.feeder/src/com/alkacon/opencms/feeder/CmsFeed.java,v $
 * Date   : $Date: 2007/12/13 15:48:47 $
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

package com.alkacon.opencms.feeder;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.collectors.I_CmsResourceCollector;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;

/**
 * Creates a syndication feed from an XML content that uses the feed schema XSD.<p>
 * 
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsFeed {

    /** Node name in the feed XSD. */
    public static final String NODE_COLLECTOR = "Collector";

    /** Node name in the feed XSD. */
    public static final String NODE_COPYRIGHT = "Copyright";

    /** Node name in the feed XSD. */
    public static final String NODE_DESCRIPTION = "Description";

    /** Node name in the feed XSD. */
    public static final String NODE_PARAMETER = "Parameter";

    /** Name of the required outer node for the XSD that defines the feed content. */
    public static final String NODE_SCHEMA = "AlkaconFeeds";

    /** Node name in the feed XSD. */
    public static final String NODE_TITLE = "Title";

    /** Node name in the feed XSD. */
    public static final String NODE_IMAGE = "Image";

    /** Node name in the feed XSD. */
    public static final String NODE_TYPE = "Type";

    /** Node name in the feed XSD. */
    public static final String NODE_MAPPING = "Mapping";

    /** The current users OpenCms context. */
    private CmsObject m_cms;

    /** The XML content that contains the definition of the feed. */
    private CmsXmlContent m_content;

    /** The generated feed. */
    private CmsFeedGenerator m_feed;

    /** The locale to use. */
    private Locale m_locale;

    /** The resource that defines the Link for the feed. */
    private CmsResource m_res;

    /**
     * Creates a new, initialized feed based on the current URI of the given OpenCms user context.<p>
     * 
     * The content must use the XSD from the Alkacon feed content definition.<p>
     * 
     * @param cms the current users OpenCms context
     * 
     * @throws CmsException in case something goes wrong
     */
    public CmsFeed(CmsObject cms)
    throws CmsException {

        this(cms, cms.getRequestContext().getUri());
    }

    /**
     * Creates a new feed based on the given XML content.<p>
     * 
     * With this constructor, the feed will not be initialized. You must call {@link #init()} first
     * before using the feed.<p>
     * 
     * The content must use the XSD from the Alkacon feed content definition.<p>
     * 
     * @param cms the current users OpenCms context
     * @param locale the locale to use
     * @param res the resource that defines the Link for the feed
     * @param content the content to create the feed from
     */
    public CmsFeed(CmsObject cms, Locale locale, CmsResource res, CmsXmlContent content) {

        m_cms = cms;
        m_locale = locale;
        m_res = res;
        m_content = content;
    }

    /**
     * Creates a new, initialized feed from the given resource.<p>
     * 
     * The content must use the XSD from the Alkacon feed content definition.<p>
     * 
     * @param cms the current users OpenCms context
     * @param resourceName the resource to create the feed for
     * 
     * @throws CmsException in case something goes wrong
     */
    public CmsFeed(CmsObject cms, String resourceName)
    throws CmsException {

        m_cms = cms;
        m_locale = OpenCms.getLocaleManager().getDefaultLocale(cms, resourceName);
        m_res = cms.readResource(resourceName);
        CmsFile file = cms.readFile(m_res);
        m_content = CmsXmlContentFactory.unmarshal(cms, file);
        init();
    }

    /**
     * Initialize this feed.<p>
     * 
     * @throws CmsException in case something goes wrong
     */
    public void init() throws CmsException {

        // make sure the schema is of the correct type
        String outerName = m_content.getContentDefinition().getOuterName();
        if (!NODE_SCHEMA.equals(outerName)) {
            throw new CmsException(Messages.get().container(Messages.ERR_BAD_FEED_CD_2, outerName, NODE_SCHEMA));
        }

        // first lookup the collector        
        String collectorStr = m_content.getStringValue(m_cms, NODE_COLLECTOR, m_locale);
        I_CmsResourceCollector collector = OpenCms.getResourceManager().getContentCollector(collectorStr);
        if (collector == null) {
            throw new CmsException(Messages.get().container(Messages.ERR_BAD_FEED_CD_2, outerName, NODE_SCHEMA));
        }
        // use the collector to collect the resources for the feed 
        String params = m_content.getStringValue(m_cms, NODE_PARAMETER, m_locale);
        List entries = collector.getResults(m_cms, collectorStr, params);

        // process the default mappings (if set / available)
        CmsFeedContentMapping defaultMapping = null;
        int mapsize = m_content.getValues(NODE_MAPPING, m_locale).size();
        if (mapsize > 1) {
            defaultMapping = new CmsFeedContentMapping();
            for (int i = 1; i <= mapsize; i++) {
                String basePath = CmsXmlUtils.createXpath(NODE_MAPPING, i);

                String field = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(basePath, "Field"), m_locale);
                String defaultValue = m_content.getStringValue(
                    m_cms,
                    CmsXmlUtils.concatXpath(basePath, "Default"),
                    m_locale);
                String maxLenghtStr = m_content.getStringValue(
                    m_cms,
                    CmsXmlUtils.concatXpath(basePath, "MaxLength"),
                    m_locale);
                List xmlNodes = m_content.getValues(CmsXmlUtils.concatXpath(basePath, "XmlNode"), m_locale);
                List nodes = new ArrayList(xmlNodes.size());
                for (int j = 0; j < xmlNodes.size(); j++) {
                    nodes.add(((I_CmsXmlContentValue)xmlNodes.get(j)).getStringValue(m_cms));
                }
                defaultMapping.addFeedFieldMapping(nodes, field, maxLenghtStr, defaultValue);
            }
        }

        // process the feed image (if set / available)
        SyndImage image = null;
        if (m_content.getValue(NODE_IMAGE, m_locale) != null) {
            String basePath = CmsXmlUtils.createXpath(NODE_IMAGE, 1);
            String title = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(basePath, "Title"), m_locale);
            String description = m_content.getStringValue(
                m_cms,
                CmsXmlUtils.concatXpath(basePath, "Description"),
                m_locale);
            String url = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(basePath, "Url"), m_locale);
            String link = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(basePath, "Link"), m_locale);
            image = new SyndImageImpl();
            image.setTitle(title);
            image.setDescription(description);
            if (CmsStringUtil.isNotEmpty(link)) {
                image.setLink(OpenCms.getLinkManager().getServerLink(m_cms, link));
            }
            if (CmsStringUtil.isNotEmpty(url)) {
                image.setUrl(OpenCms.getLinkManager().getServerLink(m_cms, url));
            }
        }

        // no errors until here, so we can really start to create the feed
        CmsFeedGenerator feed = new CmsFeedGenerator(defaultMapping);
        // calculate the link with full server path
        feed.setFeedLink(OpenCms.getLinkManager().getServerLink(m_cms, m_res.getRootPath()));
        // calculate the encoding from the properties
        String encoding = CmsLocaleManager.getResourceEncoding(m_cms, m_res);
        feed.setFeedEncoding(encoding);
        // set the remaining variables (null values are handled in the feed generator)
        feed.setFeedTitle(m_content.getStringValue(m_cms, NODE_TITLE, m_locale));
        feed.setFeedType(m_content.getStringValue(m_cms, NODE_TYPE, m_locale));
        feed.setFeedCopyright(m_content.getStringValue(m_cms, NODE_COPYRIGHT, m_locale));
        feed.setFeedDescription(m_content.getStringValue(m_cms, NODE_DESCRIPTION, m_locale));
        feed.setFeedImage(image);

        // set the feed entries
        feed.setContentEntries(entries);

        // now store the created feed internally for later use
        m_feed = feed;
    }

    /**
     * Write the feed result to the provided output stream.<p>
     * 
     * @param out the output stream to write the feed to
     * 
     * @throws IOException in case of errors writing to the stream
     * @throws FeedException in case of errors generating the feed
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void write(OutputStream out) throws IOException, FeedException, CmsException {

        m_feed.write(m_cms, m_locale, out);
    }

    /**
     * Write the feed result to the provided writer.<p>
     * 
     * @param writer the writer to write the feed to
     * 
     * @throws IOException in case of errors writing to the stream
     * @throws FeedException in case of errors generating the feed
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void write(Writer writer) throws IOException, FeedException, CmsException {

        m_feed.write(m_cms, m_locale, writer);
    }
}