/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.feeder/src/com/alkacon/opencms/feeder/CmsFeedContentMapping.java,v $
 * Date   : $Date: 2008/12/13 13:23:24 $
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

package com.alkacon.opencms.feeder;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsRequestContext;
import org.opencms.file.collectors.CmsDateResourceComparator;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsHtmlExtractor;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.I_CmsXmlContentHandler;
import org.opencms.xml.types.CmsXmlHtmlValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Describes the mapping from an OpenCms XML content to a syndication feed entry.<p>
 * 
 * @author Alexander Kandzior 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3 $ 
 */
public class CmsFeedContentMapping {

    /**
     * Describes one individual mapping from an XML content field to a field of the syndication feed.<p>
     */
    class CmsFeedFieldMapping {

        /** The default value for the feed field in case no XML content match is found. */
        private String m_defaultValue;

        /** The field in the feed to map the XML content to. */
        private String m_feedField;

        /** The maximum length the field is allowed to have. */
        private int m_maxLenght;

        /** The fields in the XML content to map. */
        private List m_xmlFields;

        /**        
        * Creates a new feed field mapping to a list of XML fields with default value and max length.<p> 
        * 
        * @param xmlFields the List of field in the XML content to map (String with xpath)
        * @param feedField the field in the feed to map the XML content to
        * @param maxLength the maximum length the field is allowed to have
        * @param defaultValue the default value for the feed field in case no XML content match is found
        */
        public CmsFeedFieldMapping(List xmlFields, String feedField, int maxLength, String defaultValue) {

            m_xmlFields = xmlFields;
            m_feedField = feedField;
            m_defaultValue = defaultValue;
            m_maxLenght = maxLength;
        }

        /**
         * Creates a new feed field mapping without default value.<p> 
         * 
         * @param xmlField the field in the XML content to map
         * @param feedField the field in the feed to map the XML content to
         */
        public CmsFeedFieldMapping(String xmlField, String feedField) {

            this(xmlField, feedField, null);
        }

        /**
         * Creates a new feed field mapping with default value and max length.<p> 
         * 
         * @param xmlField the field in the XML content to map
         * @param feedField the field in the feed to map the XML content to
         * @param maxLength the maximum length the field is allowed to have
         * @param defaultValue the default value for the feed field in case no XML content match is found
         */
        public CmsFeedFieldMapping(String xmlField, String feedField, int maxLength, String defaultValue) {

            this(new ArrayList(Collections.singletonList(xmlField)), feedField, maxLength, defaultValue);
        }

        /**
         * Creates a new feed field mapping with default value.<p> 
         * 
         * @param xmlField the field in the XML content to map
         * @param feedField the field in the feed to map the XML content to
         * @param defaultValue the default value for the feed field in case no XML content match is found
         */
        public CmsFeedFieldMapping(String xmlField, String feedField, String defaultValue) {

            this(xmlField, feedField, 0, defaultValue);
        }

        /**
         * Adds another fields in the XML content to map.<p>
         *
         * @param xmlField the additional field in the XML content to map
         */
        public void addXmlField(String xmlField) {

            if (!m_xmlFields.contains(xmlField)) {
                m_xmlFields.add(xmlField);
            }
        }

        /**
         * This is a special implementation that also returns true if the object compared to 
         * is a String equal to {@link #getFeedField()}.<p>
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {

            String feedField = null;
            if (obj instanceof CmsFeedFieldMapping) {
                feedField = ((CmsFeedFieldMapping)obj).m_feedField;
            }
            if (obj instanceof String) {
                feedField = (String)obj;
            }
            return m_feedField.equals(feedField);
        }

        /**
         * Returns the default value for the feed field in case no XML content match is found.<p>
         *
         * @return the default value for the feed field in case no XML content match is found
         */
        public String getDefaultValue() {

            return m_defaultValue;
        }

        /**
         * Returns the field in the feed to map the XML content to.<p>
         *
         * @return the field in the feed to map the XML content to
         */
        public String getFeedField() {

            return m_feedField;
        }

        /**
         * Returns the maximum length the field is allowed to have.<p>
         *
         * A value of <code>0</code> or less indicates that the field length is unlimited.<p>
         *
         * @return the maximum length the field is allowed to have
         */
        public int getMaxLenght() {

            return m_maxLenght;
        }

        /**
         * Returns the fields in the XML content to map.<p>
         *
         * @return the fields in the XML content to map
         */
        public List getXmlFields() {

            return m_xmlFields;
        }

        /**
         * Returns <code>true</code> in case a default value for the feed field is available.<p>
         * 
         * @return <code>true</code> in case a default value for the feed field is available
         */
        public boolean hasDefaultValue() {

            return m_defaultValue != null;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {

            return m_feedField.hashCode();
        }

        /**
         * Returns <code>true</code> in case the maximum length of the field has been set.<p>
         * 
         * @return <code>true</code> in case the maximum length of the field has been set
         */
        public boolean hasMaxLenghtLimit() {

            return m_maxLenght > 0;
        }
    }

    /**
     * Macro resolver for generating rss entry fields.<p>
     * 
     * It provides access to xml content fields, resource properties, data form fields and more.<p>
     */
    class CmsFeedMacroResolver extends CmsMacroResolver {

        /** Macro prefix name constant. */
        public static final String MACRO_CONVERT_DATE = "convertToDate.";

        /** Macro prefix name constant. */
        public static final String MACRO_DATA = "data.";

        /** Macro suffix name constant. */
        public static final String MACRO_DATE = "DATE";

        /** Macro name constant. */
        public static final String MACRO_DESC_FORMAT = "desc.format";

        /** Macro suffix name constant. */
        public static final String MACRO_ID = "ID";

        /** Macro prefix name constant. */
        public static final String MACRO_LINK = "link.";

        /** Macro null result constant. */
        public static final String MACRO_NULL = "_NULL_";

        /** Macro suffix name constant. */
        public static final String MACRO_URI = "URI";

        /** Macro prefix name constant. */
        public static final String MACRO_XML = "xml.";

        /** The cms context. */
        private CmsObject m_cms;

        /** The xml content to make available with macros. */
        private CmsXmlContent m_content;

        /** The optional data form data to make available with macros. */
        private CmsFormDataBean m_dataBean;

        /** The type (plain/html) of the last accessed xml content field. */
        private String m_descType = null;

        /** The locale to read the xml content fields from. */
        private Locale m_locale;

        /**
         * Default constructor.<p>
         * 
         * @param cms the cms context
         * @param content the xml content to make available with macros
         * @param locale the locale to read the xml content fields from
         * @param dataBean the optional data form data to make available with macros
         */
        public CmsFeedMacroResolver(CmsObject cms, CmsXmlContent content, Locale locale, CmsFormDataBean dataBean) {

            m_content = content;
            m_locale = locale;
            m_cms = cms;
            m_dataBean = dataBean;
            setCmsObject(cms);
            setKeepEmptyMacros(false);
        }

        /**
         * @see org.opencms.util.CmsMacroResolver#getMacroValue(java.lang.String)
         */
        public String getMacroValue(String macro) {

            String ret = null;
            if (macro.equals(CmsFeedMacroResolver.MACRO_DESC_FORMAT)) {
                return m_descType;
            } else if (macro.startsWith(CmsFeedMacroResolver.MACRO_CONVERT_DATE)) {
                String dateMacro = macro.substring(CmsFeedMacroResolver.MACRO_CONVERT_DATE.length());
                try {
                    // try to parse the date as long
                    ret = "" + Long.valueOf(dateMacro).longValue();
                } catch (NumberFormatException e) {
                    ret = "-1";
                    // no luck parsing, so we have no date - try using other options...
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(dateMacro)) {
                        List items = CmsStringUtil.splitAsList(dateMacro, '|', true);
                        ret = "" + CmsDateResourceComparator.calculateDate(m_cms, m_content.getFile(), items, -1);
                    }
                }
            } else if (macro.startsWith(CmsFeedMacroResolver.MACRO_LINK)) {
                String linkMacro = macro.substring(CmsFeedMacroResolver.MACRO_LINK.length());
                ret = OpenCms.getLinkManager().getServerLink(m_cms, linkMacro);
            } else if (macro.startsWith(CmsFeedMacroResolver.MACRO_XML)) {
                String xmlMacro = macro.substring(CmsFeedMacroResolver.MACRO_XML.length());
                if (xmlMacro.startsWith("_") && xmlMacro.endsWith("_") && (xmlMacro.length() > 2)) {
                    xmlMacro = xmlMacro.substring(1, xmlMacro.length() - 1);
                    if (xmlMacro.equals(CmsFeedMacroResolver.MACRO_URI)) {
                        ret = m_cms.getSitePath(m_content.getFile());
                    }
                } else {
                    I_CmsXmlContentValue xmlValue = m_content.getValue(xmlMacro, m_locale);
                    if (xmlValue == null) {
                        return CmsFeedMacroResolver.MACRO_NULL;
                    } else {
                        // save the last for the desc.format macro
                        if (xmlValue instanceof CmsXmlHtmlValue) {
                            // the content is HTML
                            m_descType = CONTENT_TYPE_HTML;
                        } else {
                            // assume default "text/plain"
                            m_descType = CONTENT_TYPE_TEXT;
                        }
                        return xmlValue.getStringValue(m_cms);
                    }
                }
            } else if (macro.startsWith(I_CmsXmlContentHandler.MAPTO_PROPERTY)) {
                String propertyMacro = macro.substring(I_CmsXmlContentHandler.MAPTO_PROPERTY.length());
                try {
                    ret = m_cms.readPropertyObject(m_content.getFile(), propertyMacro, true).getValue();
                    if (ret == null) {
                        ret = "";
                    }
                } catch (CmsException ex) {
                    // if property could not be read, populate the value with text from the default value field
                    ret = CmsFeedMacroResolver.MACRO_NULL;
                }
            } else if (macro.startsWith(I_CmsXmlContentHandler.MAPTO_ATTRIBUTE)) {
                macro.substring(I_CmsXmlContentHandler.MAPTO_ATTRIBUTE.length());
            } else if (macro.startsWith(CmsFeedMacroResolver.MACRO_DATA)) {
                String dataMacro = macro.substring(CmsFeedMacroResolver.MACRO_DATA.length());
                if (dataMacro.startsWith("_") && dataMacro.endsWith("_") && (dataMacro.length() > 2)) {
                    dataMacro = dataMacro.substring(1, dataMacro.length() - 1);
                    if (dataMacro.equals(CmsFeedMacroResolver.MACRO_ID)) {
                        return "" + m_dataBean.getEntryId();
                    } else if (dataMacro.equals(CmsFeedMacroResolver.MACRO_DATE)) {
                        return "" + m_dataBean.getDateCreated();
                    }
                } else {
                    String value = m_dataBean.getFieldValue(dataMacro);
                    if (value != null) {
                        return value;
                    }
                }
            } else {
                ret = super.getMacroValue(macro);
            }
            return ret;
        }
    }

    /** Content type "text/html". */
    public static final String CONTENT_TYPE_HTML = "text/html";

    /** Content type "text/plain". */
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    /** Constant to map to the feed entry author. */
    public static final String FEED_AUTHOR = "Author";

    /** Constant to map to the feed entry published date. */
    public static final String FEED_DATE_PUBLISHED = "DatePublished";

    /** Constant to map to the feed entry update date. */
    public static final String FEED_DATE_UPDATED = "DateUpdated";

    /** Constant to map to the feed entry description (also called value). */
    public static final String FEED_DESCRIPTION = "Description";

    /** Constant to map to the feed link. */
    public static final String FEED_LINK = "Link";

    /** Constant to map to the feed entry title. */
    public static final String FEED_TITLE = "Title";

    /** Constant array with all possible feed mappings. */
    public static final String[] MAPPINGS = {
        FEED_TITLE,
        FEED_AUTHOR,
        FEED_DESCRIPTION,
        FEED_DATE_PUBLISHED,
        FEED_DATE_UPDATED,
        FEED_LINK};

    /** Constant list with all possible feed mappings. */
    public static final List MAPPINGS_LIST = Collections.unmodifiableList(Arrays.asList(MAPPINGS));

    /** The data type id. */
    private String m_dataType;

    /** The description format. */
    private String m_descFormat;

    /** The map of mappings from the XML content to the feed. */
    private Map m_feedMappings;

    /** The max number of data entries per file. */
    private int m_maxEntries;

    /**
     * Creates a new feed content mapping.<p>
     */
    public CmsFeedContentMapping() {

        m_feedMappings = new HashMap();
    }

    /**
     * Constructor with additional parameters for data driven entries.<p>
     * 
     * @param descFormat the description format
     * @param dataType the data type id
     * @param maxEntries the max number of data entries per file
     */
    public CmsFeedContentMapping(String descFormat, String dataType, int maxEntries) {

        this();
        m_descFormat = descFormat;
        m_dataType = dataType;
        m_maxEntries = maxEntries;
    }

    /**
     * Adds a new feed field mapping with default value and max length setting.<p> 
     * 
     * @param xmlFields the fields in the XML content to map
     * @param feedField the field in the feed to map the XML content to
     * @param maxLength the maximum length the field is allowed to have
     * @param defaultValue the default value for the feed field in case no XML content match is found
     */
    public void addFeedFieldMapping(List xmlFields, String feedField, int maxLength, String defaultValue) {

        if (MAPPINGS_LIST.contains(feedField)) {
            CmsFeedFieldMapping mapping = new CmsFeedFieldMapping(xmlFields, feedField, maxLength, defaultValue);
            m_feedMappings.put(feedField, mapping);
        }
    }

    /**
     * Adds a new feed field mapping with default value and max length setting.<p> 
     * 
     * @param xmlFields the fields in the XML content to map
     * @param feedField the field in the feed to map the XML content to 
     * @param maxLength the maximum length the field is allowed to have (will be converted to an int)
     * @param defaultValue the default value for the feed field in case no XML content match is found
     */
    public void addFeedFieldMapping(List xmlFields, String feedField, String maxLength, String defaultValue) {

        // store mappings as xpath to allow better control about what is mapped
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(defaultValue)) {
            // we don't allow only whitespace defaults
            defaultValue = null;
        }
        int maxLengthInt = 0;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(maxLength)) {
            try {
                maxLengthInt = Integer.parseInt(maxLength);
            } catch (NumberFormatException e) {
                // should not happen as the XSD already checks this
            }
        }
        addFeedFieldMapping(xmlFields, feedField, maxLengthInt, defaultValue);
    }

    /**
     * Returns a feed entry created from the given macro resolver using the configured rules of this content mapping.<p>
     * 
     * @param cms the current users OpenCms context
     * @param macroResolver the macro resolver to use to create the rss entry
     * 
     * @return a feed entry created from the given macro resolver using the configured rules of this content mapping
     */
    public SyndEntry getEntry(CmsObject cms, CmsMacroResolver macroResolver) {

        // get all configured mappings
        Set mappings = m_feedMappings.entrySet();
        // create the empty syndication entry
        SyndEntry result = new SyndEntryImpl();
        boolean hasTitle = false;
        boolean hasDescription = false;

        // indicate we always want to use full links in the generated HTML output
        cms.getRequestContext().setAttribute(CmsRequestContext.ATTRIBUTE_FULLLINKS, Boolean.TRUE);

        Iterator i = mappings.iterator();
        String link = null;
        Date publishedDate = null;
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            String feedField = (String)e.getKey();
            CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)e.getValue();

            String macroValue = null;
            List xmlFields = mapping.getXmlFields();
            for (int j = 0, size = xmlFields.size(); j < size; j++) {
                String xmlField = (String)xmlFields.get(j);
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(xmlField)) {
                    macroValue = macroResolver.resolveMacros(xmlField);
                    if (macroValue.equals(xmlField)) {
                        macroValue = macroResolver.getMacroValue(CmsFeedMacroResolver.MACRO_XML + xmlField);
                    }
                    if ((macroValue != null) && !macroValue.equals(CmsFeedMacroResolver.MACRO_NULL)) {
                        // found a matching XML content node
                        break;
                    }
                }
            }
            String value = null;
            String type = m_descFormat;
            if (type == null) {
                type = macroResolver.getMacroValue(CmsFeedMacroResolver.MACRO_DESC_FORMAT);
            } else if (type.equals("html")) {
                type = CONTENT_TYPE_HTML;
            } else {
                type = CONTENT_TYPE_TEXT;
            }

            if ((macroValue != null) && !macroValue.equals(CmsFeedMacroResolver.MACRO_NULL)) {
                // value was found in the content
                value = macroValue;
            } else if (mapping.hasDefaultValue()
                && mapping.getDefaultValue().startsWith(I_CmsXmlContentHandler.MAPTO_PROPERTY)) {
                // value not found in content and default value defines a property.
                value = macroResolver.getMacroValue(mapping.getDefaultValue());
                if (value.equals(CmsFeedMacroResolver.MACRO_NULL)) {
                    // if property could not be read, populate the value with text from the default value field
                    value = mapping.getDefaultValue();
                }
            } else if (mapping.hasDefaultValue()) {
                // value not found in content, use default value
                value = macroResolver.resolveMacros(mapping.getDefaultValue());
            }
            if (value != null) {
                if (mapping.hasMaxLenghtLimit()) {
                    // apply length restriction if required
                    value = applyLengthRestriction(cms, value, mapping.getMaxLenght());
                    type = CONTENT_TYPE_TEXT;
                }
                // a value to map was found
                int pos = MAPPINGS_LIST.indexOf(feedField);
                switch (pos) {
                    case 0: // Title
                        result.setTitle(value);
                        hasTitle = true;
                        break;
                    case 1: // Author
                        result.setAuthor(value);
                        break;
                    case 2: // Description
                        SyndContent description = new SyndContentImpl();
                        description.setValue(value);
                        description.setType(type);
                        result.setDescription(description);
                        hasDescription = true;
                        break;
                    case 3: // DatePublished
                        publishedDate = convertToDate(value, macroResolver);
                        if (publishedDate != null) {
                            result.setPublishedDate(publishedDate);
                        }
                        break;
                    case 4: // DateUpdated
                        Date updatedDate = convertToDate(value, macroResolver);
                        if (updatedDate != null) {
                            result.setUpdatedDate(updatedDate);
                        }
                        break;
                    case 5: // Link
                        // use link as provided in content
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(value)) {
                            link = OpenCms.getLinkManager().getServerLink(cms, value);
                        }
                        break;
                    default: // unknown, this cannot happen as all mappings are checked when created            
                }
            }
        }

        // remove the link processing attribute
        cms.getRequestContext().removeAttribute(CmsRequestContext.ATTRIBUTE_FULLLINKS);

        if (hasTitle && hasDescription) {
            // we need at least an entry and an description
            if (publishedDate == null) {
                // no publish date given, so we use the date the content was created
                result.setPublishedDate(convertToDate("dateCreated", macroResolver));
            }
            if (link == null) {
                // calculate the link with full server path
                link = OpenCms.getLinkManager().getServerLink(
                    cms,
                    macroResolver.getMacroValue(CmsFeedMacroResolver.MACRO_XML
                        + "_"
                        + CmsFeedMacroResolver.MACRO_URI
                        + "_"));
            }
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(link)) {
                result.setLink(link);
            }
        } else {
            // required mappings are not available
            result = null;
        }
        return result;
    }

    /**
     * Returns the path of the first XML element that is mapped to the feed Author.<p>
     * 
     * If no XML element has been mapped to the feed Author, <code>null</code> is returned.<p>
     * 
     * @return the path of the first XML element that is mapped to the feed Author
     */
    public String getMappingForAuthor() {

        CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)m_feedMappings.get(FEED_AUTHOR);
        return (mapping != null) ? (String)mapping.getXmlFields().get(0) : null;
    }

    /**
     * Returns the path of the first XML element that is mapped to the feed publish Date.<p>
     * 
     * If no XML element has been mapped to the feed publish Date, <code>null</code> is returned.<p>
     * 
     * @return the path of the first XML element that is mapped to the feed publish Date
     */
    public String getMappingForDatePublished() {

        CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)m_feedMappings.get(FEED_DATE_PUBLISHED);
        return (mapping != null) ? (String)mapping.getXmlFields().get(0) : null;
    }

    /**
     * Returns the path of the first XML element that is mapped to the feed update Date.<p>
     * 
     * If no XML element has been mapped to the feed update Date, <code>null</code> is returned.<p>
     * 
     * @return the path of the first XML element that is mapped to the feed update Date
     */
    public String getMappingForDateUpdated() {

        CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)m_feedMappings.get(FEED_DATE_UPDATED);
        return (mapping != null) ? (String)mapping.getXmlFields().get(0) : null;
    }

    /**
     * Returns the path of the first XML element that is mapped to the feed Description.<p>
     * 
     * If no XML element has been mapped to the feed Description, <code>null</code> is returned.<p>
     * 
     * @return the path of the first XML element that is mapped to the feed Description
     */
    public String getMappingForDescription() {

        CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)m_feedMappings.get(FEED_DESCRIPTION);
        return (mapping != null) ? (String)mapping.getXmlFields().get(0) : null;
    }

    /**
     * Returns the path of the first XML element that is mapped to the feed Title.<p>
     * 
     * If no XML element has been mapped to the feed Title, <code>null</code> is returned.<p>
     * 
     * @return the path of the first XML element that is mapped to the feed Title
     */
    public String getMappingForTitle() {

        CmsFeedFieldMapping mapping = (CmsFeedFieldMapping)m_feedMappings.get(FEED_TITLE);
        return (mapping != null) ? (String)mapping.getXmlFields().get(0) : null;
    }

    /**
     * Returns all rss entries from the given xml content in the given locale.<p>
     * 
     * @param cms the cms context
     * @param content the xml content to generate the rss entries for
     * @param locale the locale
     * 
     * @return a list of {@link SyndEntry} objects
     */
    public List getRssEntries(CmsObject cms, CmsXmlContent content, Locale locale) {

        List entries = new ArrayList();
        if ((content == null) || (locale == null) || !content.hasLocale(locale)) {
            // no entry can be created if input is silly
            return entries;
        }
        // indicate we always want to use full links in the generated HTML output
        cms.getRequestContext().setAttribute(CmsRequestContext.ATTRIBUTE_FULLLINKS, Boolean.TRUE);
        try {
            if (m_dataType == null) {
                CmsMacroResolver rssMacroResolver = new CmsFeedMacroResolver(cms, content, locale, null);
                SyndEntry entry = getEntry(cms, rssMacroResolver);
                if (entry != null) {
                    entries.add(entry);
                }
            } else {
                CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT.filterResourceId(
                    content.getFile().getStructureId()).filterFormId(m_dataType).filterOrderDesc().filterIndex(
                    0,
                    m_maxEntries);
                List dataBeans = CmsFormDataAccess.getInstance().readForms(filter);
                Iterator it = dataBeans.iterator();
                while (it.hasNext()) {
                    CmsFormDataBean dataBean = (CmsFormDataBean)it.next();
                    CmsMacroResolver rssMacroResolver = new CmsFeedMacroResolver(cms, content, locale, dataBean);
                    SyndEntry entry = getEntry(cms, rssMacroResolver);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            // something went really wrong
            e.printStackTrace();
        } finally {
            // remove the link processing attribute
            cms.getRequestContext().removeAttribute(CmsRequestContext.ATTRIBUTE_FULLLINKS);
        }
        return entries;
    }

    /**
     * Applies the max length limitation of the current field to the given value.<p>
     * 
     * @param cms the current users OpenCms context
     * @param value the String value generated form the XML content value
     * @param maxLength the max length setting of the current field
     * 
     * @return the input with the max length limitation of the current field applied
     */
    protected String applyLengthRestriction(CmsObject cms, String value, int maxLength) {

        if (value.length() <= maxLength) {
            return value;
        }
        String result;
        // value is too long, apply limitation
        try {
            // try to convert html to plain text
            result = CmsHtmlExtractor.extractText(value, cms.getRequestContext().getEncoding());
        } catch (Exception e) {
            result = value;
        }
        if (result.length() > maxLength) {
            result = CmsStringUtil.trimToSize(result, maxLength);
        }
        return result;
    }

    /**
     * Converts a value to a Date.<p>
     * 
     * In case the value itself can not be converted to a date, 
     * the given value is used to access the given files attributes or properties
     * using {@link CmsDateResourceComparator#calculateDate(CmsObject, org.opencms.file.CmsResource, List, long)}.<p>
     * 
     * @param stringValue the value
     * @param macroResolver the macro resolver
     * 
     * @return the converted Date, or <code>null</code> if no convertible data was found
     */
    protected Date convertToDate(String stringValue, CmsMacroResolver macroResolver) {

        String macroValue = macroResolver.getMacroValue(CmsFeedMacroResolver.MACRO_CONVERT_DATE + stringValue);
        Date result = null;
        try {
            // try to parse the date as long
            long date = Long.valueOf(macroValue).longValue();
            if (date != -1) {
                result = new Date(date);
            }
        } catch (NumberFormatException e) {
            // no luck parsing, skip value
        }
        return result;
    }
}