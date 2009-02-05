/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.usagereport/src/com/alkacon/opencms/usagereport/CmsUpdatefeed.java,v $
 * Date   : $Date: 2009/02/05 09:56:20 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2008 Alkacon Software (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.usagereport;

import com.alkacon.opencms.feeder.Messages;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.collectors.CmsDateResourceComparator;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.main.CmsContextInfo;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * Creates a syndication feed based on a XML content that uses the AlkaconUpdatefeed schema XSD.<p>
 * 
 * The main-class is CmsUpdateFeed it creates a syndication feed based on the XML content that uses
 * the AlkaconUpdatefeed schema XSD. The classes named CmsUpdateFeedConfig... are Beans which help 
 * to save the configuration from the xml content except the CmsUpdateFeedConfigFolderComparator 
 * which helps to sort the CmsUpdateFeedConfigFolder-objects according to their folder-path.<p>
 * 
 * The class CmsUpdateFeedManager is used as action class in the module configuration and it 
 * is need to get the admin-cms-object.<p>
 * 
 * @author Ruediger Kurz 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeed {

    /** Name of the required outer node for the XSD that defines the feed content. */
    public static final String NODE_SCHEMA = "AlkaconUpdatefeeds";

    /** Content type "text/plain". */
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    /** The exclude property key. */
    public static final String PROPERTY_EXCLUDE = "sitemap_hidden";

    /** The String for added configuration. */
    public static final String CONFIG_ADDED = "added";

    /** The String for added and new configuration. */
    public static final String CONFIG_NEWANDADDED = "addedand";

    /** The module name. */
    private static final String MODULE_NAME = "com.alkacon.opencms.usagereport";

    /** The module parameter for lower factor. */
    private static final String MODULE_PARAM_TIMEFRAME_FACTOR_1 = "timeframe.factor1";

    /** The module parameter for upper factor. */
    private static final String MODULE_PARAM_TIMEFRAME_FACTOR_2 = "timeframe.factor2";

    /** The current users OpenCms context. */
    private CmsObject m_cms;

    /** The XML content that contains the definition of the feed. */
    private CmsXmlContent m_content;

    /** The locale to use. */
    private Locale m_locale;

    /** The resource that defines the Link for the feed. */
    private CmsResource m_res;

    /** The schema name. */
    private String m_schemaName;

    /** The Configuration of the xml-content. */
    private CmsUpdatefeedConfig m_config;

    /** The resources that are candidates. */
    private List m_resources;

    /** The admin-Cms-Object. */
    private CmsObject m_adminCms;

    /** The clone-Cms-Object. */
    private CmsObject m_myCms;

    /** The lower time frame factor. */
    private int m_factor1;

    /** The upper time frame factor. */
    private int m_factor2;

    /**
     * Creates a new, initialized feed based on the current URI of the given OpenCms user context.<p>
     * 
     * The content must use the XSD from the Alkacon feed content definition.<p>
     * 
     * @param cms the current users OpenCms context
     * 
     * @throws CmsException in case something goes wrong
     */
    public CmsUpdatefeed(CmsObject cms)
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
    public CmsUpdatefeed(CmsObject cms, Locale locale, CmsResource res)
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
    public CmsUpdatefeed(CmsObject cms, Locale locale, CmsResource res, CmsXmlContent content) {

        m_cms = cms;
        m_locale = locale;
        m_res = res;
        m_content = content;
        m_schemaName = m_content.getContentDefinition().getOuterName();

        // get the adminCms-object
        CmsUpdatefeedManager manager = (CmsUpdatefeedManager)OpenCms.getModuleManager().getModule(MODULE_NAME).getActionInstance();
        m_adminCms = manager.getAdminCms();
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
    public CmsUpdatefeed(CmsObject cms, String resourceName)
    throws CmsException {

        this(cms, OpenCms.getLocaleManager().getDefaultLocale(cms, resourceName), cms.readResource(resourceName));
        init();

    }

    /**
     * Initialize this object into a state so that the JSP can call
     * the write method to put out the RSS-feed.<p>
     * 
     * Next to the different constructors and the methods which write the feed into a outputstream
     * there is a method called init(), which initializes the CmsUpdateFeed-object. Thereby the 
     * module parameters are read and the admin-cms-object is transformed into a cms-object which 
     * has the right permissions configured in the xml content. Furthermore the CmsUpdateFeedConfig... 
     * objects are initialized. Finally the method processResources() is called to collect the 
     * resources which should appear in the feed.<p>
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void init() throws CmsException {

        // read the module parameter factor1
        m_factor1 = 2;
        if (OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(MODULE_PARAM_TIMEFRAME_FACTOR_1) != null) {
            try {
                m_factor1 = Integer.parseInt(OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_TIMEFRAME_FACTOR_1));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // read the module parameter factor2
        m_factor2 = 5;
        if (OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(MODULE_PARAM_TIMEFRAME_FACTOR_2) != null) {
            try {
                m_factor2 = Integer.parseInt(OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_TIMEFRAME_FACTOR_2));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // make sure the schema is of the correct type
        if (!NODE_SCHEMA.equals(m_schemaName)) {
            throw new CmsException(Messages.get().container(Messages.ERR_BAD_FEED_CD_2, m_schemaName, NODE_SCHEMA));
        }

        // get the configuration for the feed
        m_config = new CmsUpdatefeedConfig(m_cms, m_content, m_locale);

        // create a CmsContextInfo-object
        CmsContextInfo ci = new CmsContextInfo();
        ci.setUserName(m_config.getPermissions());
        ci.setSiteRoot(m_cms.getRequestContext().getSiteRoot());
        ci.setProjectName(m_cms.getRequestContext().currentProject().getName());

        // make a CmsObject which has the configured permissions
        m_myCms = OpenCms.initCmsObject(m_adminCms, ci);

        // collect all resources which are relevant for the feed
        processResources(m_config.getTimeFrame() * m_factor1);
        if (m_resources.size() < m_config.getMinEntries()) {
            processResources(m_config.getTimeFrame() * m_factor2);
        }
    }

    /**
     * Collects all resources which are relevant for the feed.<p>
     * 
     * This method implements the main routine to collect the resources
     * which fit into the configuration. It calls other local methods 
     * to filter out the not wanted resources. What the method does:<p>
     * 
     * <ul>
     *  <li>getResources (all resources fitting in to the time frame)</li>
     *  <li>filterLocales (all resources from the wanted locale)</li>
     *  <li>filterExcluded (all resources which don't have set a exclude-property)</li>
     *  <li>sort resources (sort resources according to their dateLastModified)</li>
     *  <li>cut the list of resources (cut the list to the max entries)</li>
     * </ul>
     * 
     * First time this method is called the parameter timeFrame is
     * set to the configured value multiplied by factor1. The second 
     * time, if the resulting resources are fewer than MinEntries this
     * method will be called again.<p> 
     * 
     * 
     * @param timeFrame an <code>int</code> which defines the time frame
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void processResources(int timeFrame) throws CmsException {

        // get all modified resources in the given time frame
        List resources = getResources(timeFrame);

        // filter out all resources which have the wrong locale
        resources = filterLocales(resources);

        // filter out the excluded resources
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(m_config.getExclude())) {
            resources = filterExcludes(resources);
        }

        // sort the resources by dateLastModified
        Collections.sort(resources, new CmsDateResourceComparator(
            m_myCms,
            Arrays.asList(new String[] {"dateLastModified"}),
            false));

        // if the size of results is larger then maximum entries
        // cut the list after maximum entries
        if (resources.size() > m_config.getMaxEntries()) {
            resources = resources.subList(0, m_config.getMaxEntries());
        }

        m_resources = resources;
    }

    /**
     * <b>Collects all resources which fit into the considered parameters:</b>
     * <ul>
     *  <li>TimeFrame</li>
     *  <li>ContentState</li>
     *  <li>ResTypes</li>
     * </ul>
     * 
     * <b>Why these Parameters have to be considered?</b><p>
     * The time frame is used to formalize a request which reads the all resources which last modified 
     * date is in the given time frame for the configured folders. The ContentState and ResTypes are
     * Parameters which belong to each folder which should be searched through. For saving performance
     * the folders should only be iterated one time.<p>
     * 
     * <b>What does the method do in detail?</b>
     * <ul>
     *  <li>collects all resources which last modified date is in the given time frame for the configured folders</li>
     *  <li>separates the new resources from the new and changed resources</li>
     *  <li>removes resources which have not the correct resource-type</li>
     *  <li>decides whether new resources are designated or new and changed resources are designated</li>
     * </ul>
     * 
     * @param timeFrame an <code>int</code> which defines the time frame
     * 
     * @return a list of CmsResource-objects
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public List getResources(int timeFrame) throws CmsException {

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.HOUR_OF_DAY, timeFrame * 24 * -1);
        long time = cal.getTimeInMillis();

        // iterate through the folders
        List result = new ArrayList();
        Iterator folderIter = m_config.getFolders().iterator();
        while (folderIter.hasNext()) {

            // get the resources (LastModifiedAfter(time)) for the current folder
            CmsUpdatefeedConfigFolder folder = (CmsUpdatefeedConfigFolder)folderIter.next();
            List resourceList = m_myCms.readResources(
                folder.getStartfolder(),
                CmsResourceFilter.DEFAULT.addRequireFile().addRequireLastModifiedAfter(time),
                true);

            // create two temporary lists for separating the new or the new and changed resources 
            List newResources = new ArrayList();
            List newandchangedResources = new ArrayList();

            // iterate through the resources of the current folder
            Iterator resIter = resourceList.iterator();
            while (resIter.hasNext()) {
                CmsResource res = (CmsResource)resIter.next();

                // separate new or new and changed resources
                if (res.getDateCreated() >= time) {
                    newResources.add(res);
                }
                newandchangedResources.add(res);

                // remove resourcetypes which should not be in the RSS
                if (!folder.getResTypes().isEmpty()) {
                    I_CmsResourceType type = OpenCms.getResourceManager().getResourceType(res.getTypeId());
                    if (!folder.getResTypes().contains(type.getTypeName())) {
                        newResources.remove(res);
                        newandchangedResources.remove(res);
                    }
                }
            }

            // decides whether new resourced are designated
            // or new and changed resources are designated

            // only new resources are selected
            if (folder.getContentState().equals(CONFIG_ADDED)) {
                result.addAll(newResources);

                // new and changed resources are selected
            } else if (folder.getContentState().equals(CONFIG_NEWANDADDED)) {
                result.addAll(newandchangedResources);

                // DEFAULT: new and changed resources are selected
            } else {
                result.addAll(newandchangedResources);
            }
        }
        return result;
    }

    /**
     * Sorts all resources, which have the property exclude set on true, out.<p>
     * 
     * @param resources the resources to filter
     * 
     * @return a list of resources
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public List filterExcludes(List resources) throws CmsException {

        List results = new ArrayList(resources.size());
        Iterator resIter = resources.iterator();
        while (resIter.hasNext()) {
            CmsResource resource = (CmsResource)resIter.next();
            CmsProperty prop = m_myCms.readPropertyObject(resource, m_config.getExclude(), true);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(prop.getValue())) {
                if (!prop.getValue().equals(CmsStringUtil.TRUE)) {
                    results.add(resource);
                }
            } else {
                results.add(resource);
            }
        }
        return results;
    }

    /**
     * Sorts all resources which don't have the designated locale out.<p>
     * 
     * @param resources the resources to filter
     * 
     * @return a list of resources
     */
    public List filterLocales(List resources) {

        List locales = new ArrayList();

        // if languages are set in the configuration use the configured properties
        if (!m_config.getLanguages().isEmpty()) {
            Iterator langIter = m_config.getLanguages().iterator();
            while (langIter.hasNext()) {
                String tmp = (String)langIter.next();
                locales.add(new Locale(tmp));
            }
        }

        // otherwise if no languages are set in the configuration take the locale
        // of the XML content itself as default-language
        else {
            locales.add(m_locale);
        }

        List results = new ArrayList(resources.size());
        Iterator resIter = resources.iterator();
        while (resIter.hasNext()) {
            CmsResource resource = (CmsResource)resIter.next();
            Locale locale = OpenCms.getLocaleManager().getDefaultLocale(m_myCms, m_myCms.getSitePath(resource));
            List available = new ArrayList(1);
            available.add(locale);
            Locale localeHit = OpenCms.getLocaleManager().getFirstMatchingLocale(locales, available);
            if (localeHit != null) {
                results.add(resource);
            }
        }
        return results;
    }

    /**
     * Creates a feed using the current settings.<p>
     * 
     * @param cms the OpenCms user context to generate the feed with
     * @param locale the currently selected locale to use for the feed
     * 
     * @return a feed created using the current settings
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     * 
     */
    public SyndFeed getFeed(CmsObject cms, Locale locale) throws CmsException {

        // Create the feed
        SyndFeed feed = new SyndFeedImpl();

        // Take the infos out of the config-Object and put them into the feed
        if (CmsStringUtil.isNotEmpty(m_config.getType())) {
            feed.setFeedType(m_config.getType());
        }
        if (CmsStringUtil.isNotEmpty(m_config.getTitle())) {
            feed.setTitle(m_config.getTitle());
        }
        if (CmsStringUtil.isNotEmpty(m_config.getDescription())) {
            feed.setDescription(m_config.getDescription());
        }

        // process the rss feed image
        SyndImage image = null;
        if (m_config.getImage() != null) {
            image = new SyndImageImpl();
            image.setTitle(m_config.getImage().getTitle());
            image.setDescription(m_config.getImage().getDescription());
            image.setUrl(m_config.getImage().getUrl());
            image.setLink(m_config.getImage().getLink());
        }
        feed.setImage(image);

        feed.setLanguage(locale.getLanguage());

        feed.setEncoding(CmsEncoder.lookupEncoding(
            CmsLocaleManager.getResourceEncoding(m_cms, m_res),
            OpenCms.getSystemInfo().getDefaultEncoding()));
        feed.setLink(OpenCms.getLinkManager().getServerLink(cms, m_res.getRootPath()));

        // call the getEntries-Method
        feed.setEntries(getEntries());

        return feed;
    }

    /**
     * 
     * This method creates the entries of the feed.<p>
     * 
     * It iterates through the resulting resources
     * and creates a list of SyndEntries.<p>
     * 
     * @return resultEntries a SyndEntry List 
     * 
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public List getEntries() throws CmsException {

        List resultEntries = new ArrayList();

        Iterator resIter = m_resources.iterator();
        while (resIter.hasNext()) {
            CmsResource r = (CmsResource)resIter.next();

            // iterate through the resource properties
            // and save the properties "Title" and "Description" 
            // into a String variables

            String title = m_cms.readPropertyObject(r, CmsPropertyDefinition.PROPERTY_TITLE, false).getValue(
                r.getRootPath());
            String description = m_cms.readPropertyObject(r, CmsPropertyDefinition.PROPERTY_DESCRIPTION, false).getValue();

            // create a SyndContent
            SyndContent sc = new SyndContentImpl();
            sc.setValue(description);
            sc.setType(CONTENT_TYPE_TEXT);

            // create a Link
            String link = OpenCms.getLinkManager().getServerLink(m_cms, r.getRootPath());

            ///////////////////////
            // build a SyndEntry //
            ///////////////////////

            SyndEntry resultEntry = new SyndEntryImpl();
            resultEntry.setTitle(title);
            resultEntry.setLink(link);
            resultEntry.setPublishedDate(new Date(r.getDateLastModified()));
            // set the guid
            resultEntry.setDescription(sc);

            // Set the Author

            // Author is %(user.lastModified)
            if (m_config.getAuthor().equals("%(user.lastModified)")) {
                resultEntry.setAuthor(m_cms.readUser(r.getUserLastModified()).getFullName());

                // Author is %(user.created)
            } else if (m_config.getAuthor().equals("%(user.created)")) {
                resultEntry.setAuthor(m_cms.readUser(r.getUserCreated()).getFullName());

                // Author is empty
            } else if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_config.getAuthor())) {
                resultEntry.setAuthor("");

                // Author is any String
            } else {
                resultEntry.setAuthor(m_config.getAuthor());
            }

            // add the SyndEntry to the resulting list
            resultEntries.add(resultEntry);
        }
        return resultEntries;
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

        write(m_cms, m_locale, out);
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

        write(m_cms, m_locale, writer);
    }

    /**
     * Write the feed result to the provided output stream.<p>
     *
     * @param cms the current users OpenCms context
     * @param locale the locale to use
     * @param out the output stream to write the feed to
     * 
     * @throws IOException in case of errors writing to the stream
     * @throws FeedException in case of errors generating the feed
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void write(CmsObject cms, Locale locale, OutputStream out) throws IOException, FeedException, CmsException {

        Writer writer = new OutputStreamWriter(out);
        write(cms, locale, writer);
    }

    /**
     * Write the feed result to the provided writer.<p>
     *
     * @param cms the current users OpenCms context
     * @param locale the locale to use
     * @param writer the writer to write the feed to
     * 
     * @throws IOException in case of errors writing to the stream
     * @throws FeedException in case of errors generating the feed
     * @throws CmsException in case of errors accessing the OpenCms VFS
     */
    public void write(CmsObject cms, Locale locale, Writer writer) throws IOException, FeedException, CmsException {

        SyndFeed feed = getFeed(cms, locale);
        SyndFeedOutput out = new SyndFeedOutput();
        out.output(feed, writer);
    }
}
