/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.feeder/src/com/alkacon/opencms/feeder/CmsFeed.java,v $
 * Date   : $Date: 2008/12/13 13:23:24 $
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

package com.alkacon.opencms.feeder;

import org.opencms.file.CmsDataAccessException;
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
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $ 
 */
public class CmsFeed {

    /** Node name in the feed XSD. */
    public static final String NODE_COLLECTOR = "Collector";

    /** Node name in the feed XSD. */
    public static final String NODE_COPYRIGHT = "Copyright";

    /** Node name in the feed XSD. */
    public static final String NODE_DATAFORM = "DataForm";

    /** Node name in the feed XSD. */
    public static final String NODE_DESCRIPTION = "Description";

    /** Node name in the feed XSD. */
    public static final String NODE_DESCRIPTIONFORMAT = "DescriptionFormat";

    /** Node name in the feed XSD. */
    public static final String NODE_ID = "Id";

    /** Node name in the feed XSD. */
    public static final String NODE_IMAGE = "Image";

    /** Node name in the feed XSD. */
    public static final String NODE_MAPPING = "Mapping";

    /** Node name in the feed XSD. */
    public static final String NODE_MAXENTRIES = "MaxEntries";

    /** Node name in the feed XSD. */
    public static final String NODE_PARAMETER = "Parameter";

    /** Node name in the feed XSD. */
    public static final String NODE_RESOURCESET = "ResourceSet";

    /** Name of the required outer node for the XSD that defines the feed content. */
    public static final String NODE_SCHEMA = "AlkaconFeeds";

    /** Node name in the feed XSD. */
    public static final String NODE_TITLE = "Title";

    /** Node name in the feed XSD. */
    public static final String NODE_TYPE = "Type";

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

    /** The schema name. */
    private String m_schemaName;

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
     * Creates a new feed based on the given resource.<p>
     * 
     * With this constructor, the feed will not be initialized. You must call {@link #init()} first
     * before using the feed.<p>
     * 
     * The content must use the XSD from the Alkacon feed content definition.<p>
     * 
     * @param cms the current users OpenCms context
     * @param locale the locale to use
     * @param res the resource that defines the Link for the feed
     * 
     * @throws CmsException in case something goes wrong
     */
    public CmsFeed(CmsObject cms, Locale locale, CmsResource res)
    throws CmsException {

        this(cms, locale, res, CmsXmlContentFactory.unmarshal(cms, cms.readFile(res)));
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
        m_schemaName = m_content.getContentDefinition().getOuterName();
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

        this(cms, OpenCms.getLocaleManager().getDefaultLocale(cms, resourceName), cms.readResource(resourceName));
        init();
    }

    /**
     * Initialize this feed.<p>
     * 
     * @throws CmsException in case something goes wrong
     */
    public void init() throws CmsException {

        // make sure the schema is of the correct type
        if (!NODE_SCHEMA.equals(m_schemaName)) {
            throw new CmsException(Messages.get().container(Messages.ERR_BAD_FEED_CD_2, m_schemaName, NODE_SCHEMA));
        }

        CmsFeedGenerator feed = new CmsFeedGenerator();
        processResourceSet(feed, "/"); // this is only for compatibility with v1.x

        int resSets = m_content.getValues(NODE_RESOURCESET, m_locale).size();
        for (int i = 1; i <= resSets; i++) {
            String resSetPath = CmsXmlUtils.createXpath(NODE_RESOURCESET, i);
            processResourceSet(feed, resSetPath);
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

    private void processResourceSet(CmsFeedGenerator feed, String resSetPath)
    throws CmsException, CmsDataAccessException {

        // first lookup the collector        
        String collectorStr = m_content.getStringValue(
            m_cms,
            CmsXmlUtils.concatXpath(resSetPath, NODE_COLLECTOR),
            m_locale);
        I_CmsResourceCollector collector = OpenCms.getResourceManager().getContentCollector(collectorStr);
        if (collector == null) {
            throw new CmsException(Messages.get().container(Messages.ERR_BAD_FEED_CD_2, m_schemaName, NODE_SCHEMA));
        }
        // use the collector to collect the resources for the feed 
        String params = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(resSetPath, NODE_PARAMETER), m_locale);
        List entries = collector.getResults(m_cms, collectorStr, params);

        // description format
        String descFormat = m_content.getStringValue(
            m_cms,
            CmsXmlUtils.concatXpath(resSetPath, NODE_DESCRIPTIONFORMAT),
            m_locale);

        // data form parameters
        String dataPath = CmsXmlUtils.concatXpath(resSetPath, NODE_DATAFORM);
        String dataType = m_content.getStringValue(m_cms, CmsXmlUtils.concatXpath(dataPath, NODE_ID), m_locale);
        String maxEntriesVal = m_content.getStringValue(
            m_cms,
            CmsXmlUtils.concatXpath(dataPath, NODE_MAXENTRIES),
            m_locale);
        int maxEntries = -1;
        if (maxEntriesVal != null) {
            try {
                maxEntries = Integer.parseInt(maxEntriesVal);
            } catch (NumberFormatException e) {
                // parsing problem, use default
            }
        }

        // process the default mappings (if set / available)
        CmsFeedContentMapping defaultMapping = null;
        int mapsize = m_content.getValues(CmsXmlUtils.concatXpath(resSetPath, NODE_MAPPING), m_locale).size();
        if (mapsize > 1) {
            defaultMapping = new CmsFeedContentMapping(descFormat, dataType, maxEntries);
            for (int j = 1; j <= mapsize; j++) {
                String basePath = CmsXmlUtils.concatXpath(resSetPath, CmsXmlUtils.createXpath(NODE_MAPPING, j));

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
                for (int k = 0; k < xmlNodes.size(); k++) {
                    nodes.add(((I_CmsXmlContentValue)xmlNodes.get(k)).getStringValue(m_cms));
                }
                defaultMapping.addFeedFieldMapping(nodes, field, maxLenghtStr, defaultValue);
            }
        }
        feed.addResourceSet(entries, defaultMapping);
    }
}