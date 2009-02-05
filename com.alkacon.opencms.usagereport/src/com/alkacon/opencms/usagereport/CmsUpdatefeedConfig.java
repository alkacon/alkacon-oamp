/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.usagereport/src/com/alkacon/opencms/usagereport/CmsUpdatefeedConfig.java,v $
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

import org.opencms.file.CmsObject;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Serves for saving the configuration of the XML content.<p>
 * 
 * @author Ruediger Kurz 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeedConfig {

    /** Node name in the feed XSD. */
    public static final String NODE_SITEREF = "SiteRef";

    /** Node name in the feed XSD. */
    public static final String NODE_TITLE = "Title";

    /** Node name in the feed XSD. */
    public static final String NODE_DESCRIPTION = "Description";

    /** Node name in the feed XSD. */
    public static final String NODE_IMAGE = "Image";

    /** Node name in the feed XSD. */
    public static final String NODE_TYPE = "Type";

    /** Node name in the feed XSD. */
    public static final String NODE_TIMEFRAME = "TimeFrame";

    /** Node name in the feed XSD. */
    public static final String NODE_MINENTRIES = "MinEntries";

    /** Node name in the feed XSD. */
    public static final String NODE_MAXENTRIES = "MaxEntries";

    /** Node name in the feed XSD. */
    public static final String NODE_FOLDERS = "Folders";

    /** Node name in the feed XSD. */
    public static final String NODE_LANGGUAGES = "Languages";

    /** Node name in the feed XSD. */
    public static final String NODE_PERMISSIONS = "Permissions";

    /** Node name in the feed XSD. */
    public static final String NODE_AUTHOR = "Author";

    /** Node name in the feed XSD. */
    public static final String NODE_EXCLUDE = "Exclude";

    /** Default value for the permissions. */
    public static final String DEFAULT_PERMISSIONS = "Guest";

    /** Default value for the time frame. */
    public static final int DEFAULT_TIMEFRAME = 7;

    /** Default value for minimum entries. */
    public static final int DEFAULT_MINENTRIES = 10;

    /** Default value for maximum entries. */
    public static final int DEFAULT_MAXENTRIES = 25;

    private String m_siteRef;
    private String m_title;
    private String m_description;
    private CmsUpdatefeedConfigImage m_image;
    private String m_type;
    private int m_timeFrame;
    private int m_minEntries;
    private int m_maxEntries;
    private List m_folders;
    private String m_exclude;
    private List m_languages;
    private String m_permissions;
    private String m_author;

    /**
     * The constructor reads the XML configuration file (CmsXmlContent) 
     * and put the values into the local variables.<p>
     * 
     * @param cms the current users context
     * @param content a CmsXmlContent with the schema AlkaconUpdatefeeds
     * @param locale the locale of the XML content
     */
    public CmsUpdatefeedConfig(CmsObject cms, CmsXmlContent content, Locale locale) {

        String basePath = "/";
        m_siteRef = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_SITEREF), locale);
        m_title = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_TITLE), locale);
        m_description = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_DESCRIPTION), locale);

        m_type = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_TYPE), locale);

        // process the feed image (if set / available)
        CmsUpdatefeedConfigImage image = null;
        if (content.getValue(NODE_IMAGE, locale) != null) {
            String imagePath = CmsXmlUtils.createXpath(NODE_IMAGE, 1);
            String title = content.getStringValue(cms, CmsXmlUtils.concatXpath(imagePath, "Title"), locale);
            String description = content.getStringValue(cms, CmsXmlUtils.concatXpath(imagePath, "Description"), locale);
            String url = content.getStringValue(cms, CmsXmlUtils.concatXpath(imagePath, "Url"), locale);
            String link = content.getStringValue(cms, CmsXmlUtils.concatXpath(imagePath, "Link"), locale);

            image = new CmsUpdatefeedConfigImage();
            image.setTitle(title);
            image.setDescription(description);
            if (CmsStringUtil.isNotEmpty(link)) {
                image.setLink(OpenCms.getLinkManager().getServerLink(cms, link));
            }
            if (CmsStringUtil.isNotEmpty(url)) {
                image.setUrl(OpenCms.getLinkManager().getServerLink(cms, url));
            }
        }
        m_image = image;

        String timeFrameVal = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_TIMEFRAME), locale);
        m_timeFrame = -1;
        if (timeFrameVal != null) {
            try {
                m_timeFrame = Integer.parseInt(timeFrameVal);
            } catch (NumberFormatException e) {
                // parsing problem, use default
            }
        } else {
            m_timeFrame = DEFAULT_TIMEFRAME;
        }

        String minEntriesVal = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_MINENTRIES), locale);
        m_minEntries = -1;
        if (minEntriesVal != null) {
            try {
                m_minEntries = Integer.parseInt(minEntriesVal);
            } catch (NumberFormatException e) {
                // parsing problem, use default
            }
        } else {
            m_minEntries = DEFAULT_MINENTRIES;
        }

        String maxEntriesVal = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_MAXENTRIES), locale);
        m_maxEntries = -1;
        if (maxEntriesVal != null) {
            try {
                m_maxEntries = Integer.parseInt(maxEntriesVal);
            } catch (NumberFormatException e) {
                // parsing problem, use default
            }
        } else {
            m_maxEntries = DEFAULT_MAXENTRIES;
        }

        // process the folders
        m_folders = new ArrayList();
        int foldersSize = content.getValues(NODE_FOLDERS, locale).size();
        for (int i = 1; i <= foldersSize; i++) {
            String foldersPath = CmsXmlUtils.createXpath(NODE_FOLDERS, i);
            // description format
            String startfolder = content.getStringValue(
                cms,
                CmsXmlUtils.concatXpath(foldersPath, "Startfolder"),
                locale);
            String resTypes = content.getStringValue(cms, CmsXmlUtils.concatXpath(foldersPath, "ResTypes"), locale);
            String contentState = content.getStringValue(
                cms,
                CmsXmlUtils.concatXpath(foldersPath, "ContentState"),
                locale);
            CmsUpdatefeedConfigFolder folder = new CmsUpdatefeedConfigFolder();
            folder.setStartfolder(startfolder);
            folder.setResTypes(CmsStringUtil.splitAsList(resTypes, ","));
            folder.setContentState(contentState);
            m_folders.add(folder);
        }

        // check if there are more than one folder selected
        if (m_folders.size() > 1) {
            checkFolder();
        }

        /////////////////////////////
        // WHAT'S THE DEFAULT ???? //
        /////////////////////////////
        m_exclude = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_EXCLUDE), locale);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_exclude)) {
            m_exclude = "";
        }

        m_languages = new ArrayList();
        String languages = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_LANGGUAGES), locale);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(languages)) {
            m_languages = CmsStringUtil.splitAsList(languages, ",");
        }

        m_permissions = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_PERMISSIONS), locale);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_permissions)) {
            m_permissions = DEFAULT_PERMISSIONS;
        }

        m_author = content.getStringValue(cms, CmsXmlUtils.concatXpath(basePath, NODE_AUTHOR), locale);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_author)) {
            m_author = "";
        }
    }

    /**
     * Checks if there are any sub-folders.<p>
     * 
     * A helping method for the constructor.<p>
     */
    public void checkFolder() {

        // Sort the selected folders
        Collections.sort(m_folders, new CmsUpdatefeedConfigFolderCompataror());

        // Create a new list for the good folders
        // and add the first element of the sorted list
        List good = new ArrayList();
        good.add(m_folders.get(0));

        // iterate through the sorted list
        Iterator folIter = m_folders.iterator();
        while (folIter.hasNext()) {
            // save folders into folderToCheck
            CmsUpdatefeedConfigFolder folderToCheck = (CmsUpdatefeedConfigFolder)folIter.next();

            // go through the list of good folders
            int count = good.size();
            boolean isGood = false;
            for (int i = 0; i < count; i++) {

                // save the current folder
                CmsUpdatefeedConfigFolder goodFolder = (CmsUpdatefeedConfigFolder)good.get(i);

                // if the folder to check does not start with the good folder and
                // is not equal to the folder to check and does't already
                // exist in list of good folders, turn the isGood boolean on true
                // otherwise turn it on false and break.
                boolean sub = !folderToCheck.getStartfolder().startsWith(goodFolder.getStartfolder());
                boolean equ = !folderToCheck.equals(goodFolder);
                boolean ctc = !good.contains(folderToCheck);

                if (sub && equ && ctc) {
                    isGood = true;
                } else {
                    isGood = false;
                    break;
                }
            }
            // if the folder is good, add it to the list of good folders
            if (isGood) {
                good.add(folderToCheck);
            }
        }
        m_folders = good;
    }

    /**
     * Returns the site-reference.<p>
     *
     * @return the site-reference
     */
    public String getSiteRef() {

        return m_siteRef;
    }

    /**
     * Sets the site-reference.<p>
     *
     * @param siteRef the site-reference to set
     */
    public void setSiteRef(String siteRef) {

        m_siteRef = siteRef;
    }

    /**
     * Returns the title of the feed.<p>
     *
     * @return the title of the feed
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * Sets the title of the feed.<p>
     *
     * @param title the title of the feed to set
     */
    public void setTitle(String title) {

        m_title = title;
    }

    /**
     * Returns the description of the feed.<p>
     *
     * @return the description of the feed
     */
    public String getDescription() {

        return m_description;
    }

    /**
     * Sets the description of the feed.<p>
     *
     * @param description the description of the feed to set
     */
    public void setDescription(String description) {

        m_description = description;
    }

    /**
     * Returns the image of the feed.<p>
     *
     * @return the image of the feed
     */
    public CmsUpdatefeedConfigImage getImage() {

        return m_image;
    }

    /**
     * Sets the image of the feed.<p>
     *
     * @param image the image of the feed to set
     */
    public void setImage(CmsUpdatefeedConfigImage image) {

        m_image = image;
    }

    /**
     * Returns the type of the feed.<p>
     *
     * @return the type of the feed
     */
    public String getType() {

        return m_type;
    }

    /**
     * Sets the type of the feed.<p>
     *
     * @param type the type of the feed to set
     */
    public void setType(String type) {

        m_type = type;
    }

    /**
     * Returns the time frame to report on.<p>
     *
     * @return the time frame to report on
     */
    public int getTimeFrame() {

        return m_timeFrame;
    }

    /**
     * Sets the time frame to report on.<p>
     *
     * @param timeFrame the time frame to set
     */
    public void setTimeFrame(int timeFrame) {

        m_timeFrame = timeFrame;
    }

    /**
     * Returns the minimum number of entries.<p>
     *
     * @return the minimum number of entries
     */
    public int getMinEntries() {

        return m_minEntries;
    }

    /**
     * Sets the minimum number of entries.<p>
     *
     * @param minEntries the minimum number of entries to set
     */
    public void setMinEntries(int minEntries) {

        m_minEntries = minEntries;
    }

    /**
     * Returns the maximum number of entries.<p>
     *
     * @return the maximum number of entries
     */
    public int getMaxEntries() {

        return m_maxEntries;
    }

    /**
     * Sets the maximum number of entries.<p>
     *
     * @param maxEntries the maximum number of entries to set
     */
    public void setMaxEntries(int maxEntries) {

        m_maxEntries = maxEntries;
    }

    /**
     * Returns the folders where to create the feed from.<p>
     *
     * @return the folders where to create the feed from
     */
    public List getFolders() {

        return m_folders;
    }

    /**
     * Sets the folders where to create the feed from.<p>
     *
     * @param folders the folders to set
     */
    public void setFolders(List folders) {

        m_folders = folders;
    }

    /**
     * Returns the exclude property.<p>
     *
     * @return the exclude property
     */
    public String getExclude() {

        return m_exclude;
    }

    /**
     * Sets the exclude property.<p>
     *
     * @param exclude the exclude property to set
     */
    public void setExclude(String exclude) {

        m_exclude = exclude;
    }

    /**
     * Returns the languages to include in the feed.<p>
     *
     * @return the languages to include in the feed
     */
    public List getLanguages() {

        return m_languages;
    }

    /**
     * Sets the languages to include in the feed.<p>
     *
     * @param languages the languages to set
     */
    public void setLanguages(List languages) {

        m_languages = languages;
    }

    /**
     * Returns the permissions to impersonate when generating the feed.<p>
     *
     * @return the permissions to impersonate when generating the feed
     */
    public String getPermissions() {

        return m_permissions;
    }

    /**
     * Sets the permissions to impersonate when generating the feed.<p>
     *
     * @param permissions the permissions to set
     */
    public void setPermissions(String permissions) {

        m_permissions = permissions;
    }

    /**
     * Returns the author of a feed entry (fixed text, username macro, empty).<p>
     *
     * @return the author of a feed entry
     */
    public String getAuthor() {

        return m_author;
    }

    /**
     * Sets the author of a feed entry (fixed text, username macro, empty).<p>
     *
     * @param author the author of a feed entry to set
     */
    public void setAuthor(String author) {

        m_author = author;
    }

}
