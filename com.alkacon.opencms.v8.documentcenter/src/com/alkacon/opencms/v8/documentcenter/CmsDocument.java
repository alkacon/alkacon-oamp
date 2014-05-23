/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsDocument.java,v $
 * Date   : $Date: 2010/12/15 09:37:44 $
 * Version: $Revision: 1.5 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.documentcenter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A single document in a folder with it's properties such as SortOrder, Title, 
 * "Last Modified Date" etc. for the document lists. Additionally, this 
 * object has some static methods to list the documents from a document folder.<p>
 *
 * @author Andreas Zahner 
 * @author Peter Bonrad
 * @author Peter Sreckovic
 * 
 * @version $Revision: 1.5 $ 
 * 
 * @since 6.0.0 
 */
public class CmsDocument {

    /** The default number of days a document is modified. Taken if no module parameter is set. */
    public static final int DEFAULT_MODIFIED_PERIOD = 4;

    /** The default number of days a document is new. Taken if no module parameter is set. */
    public static final int DEFAULT_NEW_PERIOD = 4;

    /** The name of the module parameter to define the number of days a document is modified. */
    public static final String MODULE_PARAMETER_MODIFIEDPERIOD = "ModifiedPeriod";

    /** The name of the module parameter to define the number of days a document is new. */
    public static final String MODULE_PARAMETER_NEWPERIOD = "NewPeriod";

    /** The name of the documents module. */
    public static final String MODULENAME = "com.alkacon.opencms.v8.documentcenter";

    /** Name of the show document property, if "false", the document is not shown in the list. */
    public static final String PROPERTY_SHOWDOCUMENT = "showdocument";

    /** Name of the sort order property. */
    public static final String PROPERTY_SORTORDER = "SortOrder";

    /** Sort direction ascending.  */
    public static final String SORT_DIRECTION_ASC = "asc";

    /** Sort direction descending.  */
    public static final String SORT_DIRECTION_DESC = "desc";

    /** Folder property sort order "alphabetical".  */
    public static final String SORT_METHOD_ALPHABETICAL = "a";

    /** Folder property sort order "by date (created)".  */
    public static final String SORT_METHOD_BY_DATECREATED = "dc";

    /** Folder property sort order "by date (modified)".  */
    public static final String SORT_METHOD_BY_DATEMODIFIED = "d";

    /** Folder property sort order "by ID".  */
    public static final String SORT_METHOD_BY_ID = "i";

    /** Folder property sort order "sortorder".  */
    public static final String SORT_METHOD_BY_SORTORDER = "s";

    /** Folder property sort order "size".  */
    public static final String SORT_METHOD_SIZE = "g";

    /** Folder property sort order "type".  */
    public static final String SORT_METHOD_TYPE = "t";

    /** Value of the "include folders" part in the sort method property. */
    public static final String VALUE_INCLUDE_FOLDERS = "includefolders";

    /** Date format for the getDateString methods. */
    protected static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd.MM.yy");

    /** Variable for debugging the methods. */
    protected static final int DEBUG = 0;

    /** The default document title. */
    protected static final String DEFAULT_VALUE_TITLE = "-";

    /** Name of the sort method property. */
    protected static final String PROPERTY_SORTMETHOD = "SortMethod";

    /** Prefix for the id in the document file name. */
    private static final String DOCUMENT_ID = "id";

    /** The null document object to be used if a document should not be shown (determined in {@link CmsDocumentFactory#createDocument(org.opencms.file.CmsObject, CmsResource, boolean)}. */
    private static final CmsDocument NULL_DOCUMENT = new CmsDocument();

    /** Pattern to determine the document id number. */
    private static final Pattern PATTERN_DOC_ID = Pattern.compile(".*(_" + DOCUMENT_ID + ")([a-z,A-Z,\\d]+)[\\.,_].*");

    /** Pattern to determine the document locale. */
    private static final Pattern PATTERN_DOC_LOCALE = Pattern.compile(".*(_)([a-z]{2})[\\.,_].*");

    /** The attachment number. */
    private Integer m_attachmentNumber;

    /** The attachments of the document container. */
    private Map<Integer, CmsDocument> m_attachments;

    /** Member variables for the object. */
    private CmsResource m_cmsResource;

    /** The current locale of the document center frontend. */
    private Locale m_currentLocale;

    /** The document matching the current locale, may be the object instance itself. */
    private CmsDocument m_currentLocaleDocument;

    /** The creation data. */
    private long m_dateCreated;

    /** The date format to use for creating date outputs. */
    private DateFormat m_dateFormat;

    /** The attachment version number. */
    private String m_documentId;

    /** The file name of the document without attachment or locale suffixes. */
    private String m_documentName;

    /** The locale of this document container. */
    private Locale m_locale;

    /** All locales of this document. */
    private List<Locale> m_locales;

    /** The localized documents of the document container. */
    private Map<String, CmsDocument> m_localizedDocuments;

    /** The site path of the document. */
    private String m_path;

    /** Flag to determine if the document is shown. */
    private boolean m_showDocument;

    /** The sort order value of the document. */
    private int m_sortOrder;

    /** The title of the document. */
    private String m_title;

    /** The types of the document container. */
    private Map<String, CmsDocument> m_types;

    /**
     * Creates an initialized CmsDocument object.<p>
     * 
     * @param resource the resource acting as document
     * @param path the current site path of the document
     * @param currentLocale the current frontend locale
     * @param title the document title
     * @param sortOrder the document sort order value
     */
    public CmsDocument(CmsResource resource, String path, Locale currentLocale, String title, String sortOrder) {

        // set the relevant properties for this document
        setCmsResource(resource);
        setPath(path);
        setCurrentLocale(currentLocale);
        setTitle(title);
        setSortOrder(sortOrder);
        setAttachments(new TreeMap<Integer, CmsDocument>());
        setLocalizedDocuments(new TreeMap<String, CmsDocument>());
        setLocale(CmsLocaleManager.getDefaultLocale());
        setDocumentName(getPath());
        setDateFormat(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, currentLocale));
        setDocumentId("");
        setTypes(new TreeMap<String, CmsDocument>());

        // initialize the document locale and attachment properties from the file name
        init();
    }

    /**
     * Creates an empty CmsDocument object.<p>
     * 
     * The member values are initialized to null. The show document flag is initialized with false.<p>
     */
    private CmsDocument() {

        // nothing to do, all values will be initialized with "null" or <code>"false"</code> by default
    }

    /**
     * Returns the null document object.<p>
     * 
     * @return the null document object
     */
    public static final CmsDocument getNullDocument() {

        return NULL_DOCUMENT;
    }

    /**
     * Check the type/extension of the given resource name.<p>
     * 
     * @param resourceName the filename of the resource
     * @return the postfix/extension of the resource name in lower case or an empty string
     */
    public static String getPostfix(String resourceName) {

        // if this resource/document is a folder, return an empty string
        if (resourceName.endsWith("/")) {
            return "";
        }

        int lastDotIndex = resourceName.lastIndexOf('.');

        // if no dot appears to be in the resource name, return an empty string
        if (lastDotIndex == -1) {
            return "";
        }

        // get the postfix from the resource name
        return resourceName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * returns the adjusted postfix type for Word, Excel and Powerpoint like postfixes.<p>
     * 
     * @param postfix the postfix to adjust
     * @return the adjusted postfix
     */
    public static String getPostfixAdjusted(String postfix) {

        if ("doc".equals(postfix)
            || "docx".equals(postfix)
            || "dot".equals(postfix)
            || "rtf".equals(postfix)
            || "odt".equals(postfix)) {
            return "doc";
        } else if ("xls".equals(postfix) || "xlsx".equals(postfix) || "ods".equals(postfix)) {
            return "xls";
        } else if ("pps".equals(postfix)
            || "ppsx".equals(postfix)
            || "ppt".equals(postfix)
            || "pptx".equals(postfix)
            || "odp".equals(postfix)) {
            return "ppt";
        }
        return postfix;
    }

    /**
     * Check the type of the given resource name.<p>
     * 
     * @param resourceName the filename of the resource
     * @return String with document type name
     */
    public static String getPostfixType(String resourceName) {

        // get the postfix from the resource name
        String postfix = getPostfix(resourceName);

        if ("pdf".equals(postfix)) {
            return "PDF";
        } else if ("doc".equals(postfix)
            || "docx".equals(postfix)
            || "dot".equals(postfix)
            || "rtf".equals(postfix)
            || "odt".equals(postfix)) {
            return "Word";
        } else if ("xls".equals(postfix) || "xlsx".equals(postfix) || "ods".equals(postfix)) {
            return "Excel";
        } else if ("pps".equals(postfix)
            || "ppsx".equals(postfix)
            || "ppt".equals(postfix)
            || "pptx".equals(postfix)
            || "odp".equals(postfix)) {
            return "Powerpoint";
        } else if (postfix.startsWith("htm")) {
            return "HTML";
        } else if ("txt".equals(postfix)) {
            return "Text";
        } else if ("jsp".equals(postfix)) {
            return "JSP";
        } else if ("zip".equals(postfix)) {
            return "Zip";
        } else if ("gif".equals(postfix)) {
            return "GIF";
        } else if ("jpg".equals(postfix)) {
            return "JPG";
        } else if ("elnk".equals(postfix)) {
            return "eLink";
        } else if ("ilnk".equals(postfix)) {
            return "iLink";
        } else {
            // no known document type found
            return "";
        }
    }

    /**
     * Returns the locale of the given resource based on the resource name.<p>
     * 
     * @param resourceName the resource name to check for the locale information
     * @return the locale of the given resource based on the resource name
     */
    public static Locale getResourceLocale(String resourceName) {

        Locale result = CmsLocaleManager.getDefaultLocale();
        Matcher matcher = PATTERN_DOC_LOCALE.matcher(resourceName);
        if (matcher.matches()) {
            Locale testLocale = new Locale(matcher.group(2));
            if (OpenCms.getLocaleManager().getDefaultLocales().contains(testLocale)) {
                // this is the locale information for the document
                result = testLocale;
            }
        }
        return result;
    }

    /**
     * Checks if the current document was modified since x days.<p>
     * 
     * @param modDays maximum modified age of file in days
     * @param newDays  maximum "new" age of file in days
     * @param dateCreated creation date of the file as long
     * @param dateLastModified last modification date of the file as long
     * @return boolean true, if file was modified within modDays days
     */
    public static boolean isModified(int modDays, int newDays, long dateCreated, long dateLastModified) {

        // check if document is new
        if ((modDays < 0) || isNew(newDays, dateCreated)) {
            return false;
        }
        // 1 d = 86400000 ms
        return ((new Date().getTime() - (modDays * 86400000)) <= dateLastModified);
    }

    /**
     * Checks if the current document is younger than x days.<p>
     * 
     * @param days maximum age of file in days
     * @param dateCreated creation date of the file as long
     * @return boolean true, if file is younger than x days
     */
    public static boolean isNew(int days, long dateCreated) {

        // check parameter
        if (days < 0) {
            return false;
        }
        // 1 d = 86400000 ms
        return ((new Date().getTime() - (days * 86400000)) <= dateCreated);
    }

    /**
     * Checks if the current document is new. For that the document has to be modified
     * AND has to be new. That means the file last modification date is not older than
     * today minus <code>modDays</code> AND the file creation date is not older  than
     * today minus<code>newDays</code>.<p>
     * 
     * @param newDays maximum age of file creation in days
     * @param dateCreated creation date of the file as long
     * @param modDays maximum age of file last modification in days
     * @param dateLastModified last modification date of the file as long
     * @return boolean true, if file creation date is younger than newDays and
     * file last modification date is younger than modDays
     */
    public static boolean isNew(int newDays, long dateCreated, int modDays, long dateLastModified) {

        // check parameter
        if ((newDays < 0) || (modDays < 0)) {
            return false;
        }
        // 1 d = 86400000 ms
        if ((new Date().getTime() - (modDays * 86400000)) <= dateLastModified) {
            return ((new Date().getTime() - (newDays * 86400000)) <= dateCreated);
        }

        return false;
    }

    /**
     * Adds an attachment to the document with the matching attachment locale.<p>
     * 
     * @param doc the document attachment to add
     * @return true if the attachment was successfully added, otherwise false
     */
    public boolean addAttachment(CmsDocument doc) {

        // consider if the attachment locale is present in the documents
        CmsDocument localizedDoc = getLocalizedDocument(doc.getLocale());
        if (localizedDoc != null) {
            localizedDoc.getAttachments().put(doc.getAttachmentNumber(), doc);
            return true;
        }
        return false;
    }

    /**
     * Adds a type to the document.<p>
     * 
     * @param doc the document type to add
     */
    public void addType(CmsDocument doc) {

        m_types.put(doc.getPostfix(), doc);
    }

    /**
     * Tests if a given object is equal to this instance.<p>
     * 
     * @param obj the other given object instance to compare with
     * @return true if the object is equal to this instance, otherwise false
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof CmsDocument) {
            return getCmsResource().equals(((CmsDocument)obj).getCmsResource());
        }
        return false;
    }

    /**
     * Returns a formatted date String without time information depending on the current frontend locale.<p>
     * 
     * @param date the date value to format
     * @return a formatted date String depending on the current frontend locale
     */
    public String formatDate(long date) {

        return formatDate(date, false);
    }

    /**
     * Returns a formatted date String depending on the current frontend locale.<p>
     * 
     * @param date the date value to format
     * @param showTime flag to determine if the time information should be shown
     * @return a formatted date String depending on the current frontend locale
     */
    public String formatDate(long date, boolean showTime) {

        if (showTime) {
            return getDateFormat().format(new Date(date));
        }
        return DateFormat.getDateInstance(DateFormat.SHORT, getCurrentLocale()).format(new Date(date));
    }

    /**
     * The attachments of this document, sorted by their attachment number.<p>
     * 
     * @return the attachments of this document, sorted by their attachment number
     */
    public List<CmsDocument> getAttachedDocuments() {

        return new ArrayList<CmsDocument>(getAttachments().values());
    }

    /**
     * Returns the attachment number.<p>
     *
     * @return the attachment number
     */
    public Integer getAttachmentNumber() {

        return m_attachmentNumber;
    }

    /**
     * Returns the attachments of the current document.<p>
     *
     * @return the attachments of the current document
     */
    public Map<Integer, CmsDocument> getAttachments() {

        return m_attachments;
    }

    /**
     * Returns the attachments of the document with the specified locale.<p>
     * 
     * @param locale the locale of the document
     * @return the attachments of the document with the specified locale
     */
    public List<CmsDocument> getAttachments(Locale locale) {

        List<CmsDocument> result = new ArrayList<CmsDocument>();
        CmsDocument doc = getLocalizedDocument(locale);
        if (doc != null) {
            result.addAll(doc.getAttachments().values());
        }
        return result;
    }

    /**
     * Returns the CmsResource instance of this document.<p>
     * 
     * @return the CmsResource instance of this document
     */
    public CmsResource getCmsResource() {

        return m_cmsResource;
    }

    /**
     * Returns the current locale of the document center frontend.<p>
     *
     * @return the current locale of the document center frontend
     */
    public Locale getCurrentLocale() {

        return m_currentLocale;
    }

    /**
     * Returns the creation date of the document.<p>
     * 
     * @return the creation date of the document in milliseconds
     */
    public long getDateCreated() {

        if (m_dateCreated != 0) {
            // the property for date created is set for this resource
            return m_dateCreated;
        } else {
            // the property for date created is empty, so take the date created of the resource
            return m_cmsResource.getDateCreated();
        }
    }

    /**
     * Returns the creation date of the document.<p>
     * 
     * @return the creation date of the document as a formatted string
     */

    public String getDateCreatedString() {

        if (m_dateCreated != 0) {
            // the property for date created is set for this resource
            return getDateFormat().format(new Date(m_dateCreated));
        } else {
            // the property for date created is empty, so take the date created of the resource
            return getDateFormat().format(new Date(getDateCreated()));
        }
    }

    /**
     * Returns the date format to use for creating date outputs.<p>
     *
     * @return the date format to use for creating date outputs
     */
    public DateFormat getDateFormat() {

        return m_dateFormat;
    }

    /**
     * Returns the last modified date of the document.<p>
     * 
     * @return the last modified date of the document in milliseconds
     */
    public long getDateLastModified() {

        return m_cmsResource.getDateLastModified();
    }

    /**
     * Returns the last modified date of the document.<p>
     * 
     * @return the last modified date of the document as a formatted string
     */
    public String getDateLastModifiedString() {

        return getDateFormat().format(new Date(getDateLastModified()));
    }

    /**
     * Returns the document matching the current frontend locale.<p>
     * 
     * @return the document matching the current frontend locale
     */
    public CmsDocument getDocumentForCurrentLocale() {

        if (m_currentLocaleDocument == null) {
            m_currentLocaleDocument = getLocalizedDocument(getCurrentLocale(), true);
        }
        return m_currentLocaleDocument;
    }

    /**
     * Returns the document id.<p>
     *
     * @return the document id
     */
    public String getDocumentId() {

        return m_documentId;
    }

    /**
     * Returns the file name of the document without attachment or locale suffixes.<p>
     *
     * @return the file name of the document without attachment or locale suffixes
     */
    public String getDocumentName() {

        return m_documentName;
    }

    /**
     * Returns the file name of the document WITH attachment or locale suffixes.<p>
     *
     * @return the file name of the document WITH attachment or locale suffixes
     */
    public String getDocumentNameFullWithoutPostfix() {

        String docName = getCmsResource().getName();
        int index = docName.lastIndexOf('.');
        if (index != -1) {
            return docName.substring(0, index);
        }
        return docName;
    }

    /**
     * Returns the file name of the document without attachment or locale suffixes.<p>
     *
     * @return the file name of the document without attachment or locale suffixes
     */
    public String getDocumentNameWithoutPostfix() {

        int index = m_documentName.lastIndexOf('.');
        if (index != -1) {
            return m_documentName.substring(0, index);
        }
        return m_documentName;
    }

    /**
     * The different types of this document, sorted by their extension.<p>
     * 
     * @return the different types of this document, sorted by their extension
     */
    public List<CmsDocument> getDocumentTypes() {

        return new ArrayList<CmsDocument>(getTypes().values());
    }

    /**
     * Returns the locale of this document container.<p>
     *
     * @return the locale of this document container
     */
    public Locale getLocale() {

        return m_locale;
    }

    /**
     * Returns all available locales of the document.<p>
     * 
     * @return all available locales of the document
     */
    public List<Locale> getLocales() {

        if (m_locales == null) {
            List<String> result = new ArrayList<String>(m_localizedDocuments.keySet().size() + 1);
            result.add(getLocale().toString());
            Iterator<String> it = m_localizedDocuments.keySet().iterator();
            while (it.hasNext()) {
                result.add(it.next());
            }
            Collections.sort(result);
            m_locales = new ArrayList<Locale>(result.size());
            for (int i = 0; i < result.size(); i++) {
                Locale loc = new Locale(result.get(i));
                m_locales.add(loc);
            }
        }
        return m_locales;
    }

    /**
     * Returns the document with the specified locale.<p>
     * 
     * @param locale the locale of the document
     * @return the document with the specified locale or null if no document is found
     */
    public CmsDocument getLocalizedDocument(Locale locale) {

        return getLocalizedDocument(locale, false);
    }

    /**
     * Returns the document with the specified locale.<p>
     * 
     * @param locale the locale of the document
     * @param returnDefault if true, the default document for the current locale or the first document is returned
     * @return the document with the specified locale, if not found, the document with the frontend locale, and last the first found document
     */
    public CmsDocument getLocalizedDocument(Locale locale, boolean returnDefault) {

        CmsDocument doc;
        if (getLocale().equals(locale)) {
            doc = this;
        } else {
            doc = m_localizedDocuments.get(locale.toString());
            if ((doc == null) && returnDefault) {
                doc = m_localizedDocuments.get(getCurrentLocale().toString());
                if (locale.equals(getCurrentLocale()) || (doc == null)) {
                    doc = this;
                }
            }
        }
        return doc;
    }

    /**
     * Returns the localized documents of the document container.<p>
     *
     * @return the localized documents of the document container
     */
    public Map<String, CmsDocument> getLocalizedDocuments() {

        return m_localizedDocuments;
    }

    /**
     * Determine the days a document is modified. First try to read the value out of the module
     * parameter "ModifiedPeriod". If not found there, the default number of days specified in the
     * constant is used.
     * 
     * @return the number of days a document is modified
     */
    public int getModifiedPeriod() {

        // read module parameters
        CmsModule docModule = OpenCms.getModuleManager().getModule(MODULENAME);

        String modifiedPeriod = docModule.getParameter(MODULE_PARAMETER_MODIFIEDPERIOD);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(modifiedPeriod)) {
            return DEFAULT_MODIFIED_PERIOD;
        }

        return Integer.parseInt(modifiedPeriod);
    }

    /**
     * Determine the days a document is new. First try to read the value out of the module
     * parameter "NewPeriod". If not found there, the default number of days specified in the
     * constant is used.
     * 
     * @return the number of days a document is new
     */
    public int getNewPeriod() {

        // read module parameters
        CmsModule docModule = OpenCms.getModuleManager().getModule(MODULENAME);

        String newPeriod = docModule.getParameter(MODULE_PARAMETER_NEWPERIOD);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(newPeriod)) {
            return DEFAULT_NEW_PERIOD;
        }

        return Integer.parseInt(newPeriod);
    }

    /**
     * Returns the absolute site path of the document.<p>
     * 
     * @return the absolute site path of the document
     */
    public String getPath() {

        return m_path;
    }

    /**
     * Check the type of this document.<p>
     * 
     * @return String with document type name
     */
    public String getPostfix() {

        return getPostfix(m_cmsResource.getRootPath());
    }

    /**
     * Check the type of this document.<p>
     * 
     * @return String with document type name
     */
    public String getPostfixType() {

        return getPostfixType(m_cmsResource.getRootPath());
    }

    /**
     * Returns the absolute path including site root of the document.<p>
     * 
     * @return the absolute path including site root of the document
     */
    public String getRootPath() {

        return m_cmsResource.getRootPath();
    }

    /**
     * Returns the size of this document.<p>
     * 
     * @return int value of the size
     */
    public int getSize() {

        return m_cmsResource.getLength();
    }

    /**
     * Returns the sort order of the document.<p>
     * 
     * @return the sort order of the document
     */
    public int getSortOrder() {

        return m_sortOrder;
    }

    /**
     * Returns the count of sub documents, if this document is a folder.<p>
     * If this document is a file, 0 is returned.
     * @param cms the CmsObject to be passed
     * @return count of sub documents, if this "document" is a folder
     * @throws CmsException 
     * 
     *
     */
    public int getSubDocuments(CmsObject cms) throws CmsException {

        //        return m_cmsResource.isFolder();
        if (m_cmsResource.isFolder()) {
            List<CmsResource> list = cms.getResourcesInFolder(m_path, CmsResourceFilter.DEFAULT);

            for (int i = 0; i < list.size(); i++) {
                if ((list.get(i).getName()).startsWith("$")) {
                    list.remove(i);
                }
            }

            return list.size();
        } else {
            return 0;
        }

    }

    /**
     * Returns the title of the document.<p>
     * 
     * @return the title of the document
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * Returns the types of the current document.<p>
     *
     * @return the types of the current document
     */
    public Map<String, CmsDocument> getTypes() {

        return m_types;
    }

    /**
     * Returns true if the this document has attachments.<p>
     * 
     * @return true if the this document has attachments, otherwise false
     */
    public boolean hasAttachments() {

        return getAttachments().size() > 0;
    }

    /**
     * Returns true if the the document with the specified locale has attachments.<p>
     * 
     * @param locale the locale of the document
     * @return true if the the document with the specified locale has attachments, otherwise false
     */
    public boolean hasAttachments(Locale locale) {

        return getAttachments(locale).size() > 0;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return m_cmsResource.hashCode();
    }

    /**
     * Returns true if the this document has different types.<p>
     * 
     * @return true if the this document has different types, otherwise false
     */
    public boolean hasTypes() {

        return getTypes().size() > 0;
    }

    /**
     * Returns true if this document is an attachment, i.e. an attachment number is provided.<p>
     * 
     * @return true if this document is an attachment, otherwise false
     */
    public boolean isAttachment() {

        return m_attachmentNumber != null;
    }

    /**
     * Test if this "document" is a folder or not.<p>
     * 
     * @return true, if this "document" is a folder
     */
    public boolean isFolder() {

        return m_cmsResource.isFolder();
    }

    /**
     * Checks if the current document was modified since x days. The number of days is read out
     * of the module parameters ("ModifiedPeriod"). If nothing is defined there than the default
     * number of days is used, specified by the constant.<p>
     * 
     * @return boolean true, if file was modified within x days
     */
    public boolean isModified() {

        return isModified(getModifiedPeriod(), getNewPeriod());
    }

    /**
     * Checks if the current document was modified since x days.<p>
     * 
     * @param modDays maximum modified age of file in days
     * @param newDays  maximum "new" age of file in days
     * @return boolean true, if file was modified within modDays days
     */
    public boolean isModified(int modDays, int newDays) {

        // check if document is new
        if ((modDays < 0) || this.isNew(newDays)) {
            return false;
        }
        // 1 d = 86400000 ms
        return ((new Date().getTime() - (modDays * 86400000)) <= getDateLastModified());
    }

    /**
     * Checks if the current document is younger than x days. The number of days is read out
     * of the module parameters ("NewPeriod"). If nothing is defined there than the default
     * number of days is used, specified by the constant.<p>
     * 
     * @return boolean true, if document is younger than x days
     */
    public boolean isNew() {

        return isNew(getNewPeriod());
    }

    /**
     * Checks if the current document is younger than x days. The number of days is read out
     * of the module parameters ("NewPeriod"). If nothing is defined there than the default
     * number of days is used, specified by the constant. Possibility to check if the file
     * has to be modified as well. The number of days is again read out of the module 
     * parameter ("ModifiedPeriod"). And again the default value is taken out of the constant.<p>
     * 
     * @param checkModification flag if file has to be modified as well
     * @return boolean true, if document is younger than x days in file creation and modification
     */
    public boolean isNew(boolean checkModification) {

        if (checkModification) {
            return isNew(getNewPeriod(), getModifiedPeriod());
        }

        return isNew(getNewPeriod());
    }

    /**
     * Checks if the current document is younger than x days.<p>
     * 
     * @param days maximum age of file in days
     * @return boolean true, if file is younger than x days
     */
    public boolean isNew(int days) {

        return isNew(days, this.getDateCreated());
    }

    /**
     * Checks if the current document new. It is only new if the document is modified
     * AND new.<p>
     * 
     * @param newDays maximum age of file in days since creation date
     * @param modDays maximum age of file in days since last modification date
     * @return boolean true, if file creation date is younger than newDays and last modification date
     * is younger than modDays
     */
    public boolean isNew(int newDays, int modDays) {

        return isNew(newDays, this.getDateCreated(), modDays, this.getDateLastModified());
    }

    /**
     * Checks if this document object is the null document object.<p>
     * 
     * @return true if this document object is the null document object
     */
    public boolean isNullDocument() {

        return this == NULL_DOCUMENT;
    }

    /**
     * Returns true if the document is shown.<p>
     *
     * @return true if the document is shown, otherwise false
     */
    public boolean isShowDocument() {

        return m_showDocument;
    }

    /**
     * Returns true if the specified document is a version of this document, depending on the file name.<p>
     * 
     * @param doc the document to check
     * @return true if the specified document is a version of this document, otherwise false
     */
    public boolean isVersionOf(CmsDocument doc) {

        if (getDocumentName().equals(doc.getDocumentName())) {
            return true;
        } else if (doc.isAttachment() && getDocumentNameWithoutPostfix().equals(doc.getDocumentNameWithoutPostfix())) {
            return true;
        }
        return false;
    }

    /**
     * Merges the specified document with this document if it is no attachment.<p>
     * 
     * @param doc the document to merge
     * @return the merged document
     */
    public CmsDocument mergeDocuments(CmsDocument doc) {

        if ((!doc.isAttachment()) || (doc.getAttachmentNumber().equals(this.getAttachmentNumber()))) {
            // locale document version and no attachment, store it
            m_localizedDocuments.put(doc.getLocale().toString(), doc);
        }
        return this;
    }

    /**
     * Sets the attachment number.<p>
     *
     * @param attachmentNumber the attachment number
     */
    public void setAttachmentNumber(Integer attachmentNumber) {

        m_attachmentNumber = attachmentNumber;
    }

    /**
     * Sets the attachments of the document container.<p>
     *
     * @param attachments the attachments of the document container
     */
    public void setAttachments(Map<Integer, CmsDocument> attachments) {

        m_attachments = attachments;
    }

    /**
     * Sets the CmsResource instance of this document.<p>
     * 
     * @param resource the CmsResource instance of this document
     */
    public void setCmsResource(CmsResource resource) {

        m_cmsResource = resource;
    }

    /**
     * Sets the current locale of the document center frontend.<p>
     *
     * @param currentLocale the current locale of the document center frontend
     */
    public void setCurrentLocale(Locale currentLocale) {

        m_currentLocale = currentLocale;
    }

    /**
     * Sets the date created.<p>
     *
     * @param dateCreated the date created
     */
    public void setDateCreated(long dateCreated) {

        m_dateCreated = dateCreated;
    }

    /**
     * Sets the date format to use for creating date outputs.<p>
     *
     * @param dateFormat the date format to use for creating date outputs
     */
    public void setDateFormat(DateFormat dateFormat) {

        m_dateFormat = dateFormat;
    }

    /**
     * Sets the document id.<p>
     *
     * @param id the document id
     */
    public void setDocumentId(String id) {

        m_documentId = id;
    }

    /**
     * Sets the file name of the document without attachment or locale suffixes.<p>
     *
     * @param documentName the file name of the document without attachment or locale suffixes
     */
    public void setDocumentName(String documentName) {

        m_documentName = documentName;
    }

    /**
     * Sets the locale of this document container.<p>
     *
     * @param locale the locale of this document container
     */
    public void setLocale(Locale locale) {

        m_locale = locale;
    }

    /**
     * Sets the localized documents of the document container.<p>
     *
     * @param localizedDocuments the localized documents of the document container
     */
    public void setLocalizedDocuments(Map<String, CmsDocument> localizedDocuments) {

        m_localizedDocuments = localizedDocuments;
    }

    /**
     * Sets the absolute site path of the document.<p>
     * 
     * @param path the absolute site path of the document
     */
    public void setPath(String path) {

        m_path = path;
    }

    /**
     * Sets if the document is shown.<p>
     *
     * @param showDocument true if the document is shown, otherwise false
     */
    public void setShowDocument(boolean showDocument) {

        m_showDocument = showDocument;
    }

    /**
     * Sets the sort order of the document as an int.<p>
     * 
     * @param sortOrder the sort order of the document
     */
    public void setSortOrder(int sortOrder) {

        m_sortOrder = sortOrder;
    }

    /**
     * Sets the sort order of the document as a String 
     * which is converted into an int.<p>
     * 
     * @param sortOrder the sort order of the document
     */
    public void setSortOrder(String sortOrder) {

        int sortOrderValue = 0;

        if (sortOrder == null) {
            setSortOrder(0);
            return;
        }

        try {
            sortOrderValue = Integer.parseInt(sortOrder);
            setSortOrder(sortOrderValue);
        } catch (NumberFormatException e) {
            setSortOrder(0);
        }
    }

    /**
     * Sets the title of the document.<p>
     * 
     * @param title the title of the document
     */
    public void setTitle(String title) {

        CmsResource resource = null;

        if ((title == null) || title.equals("")) {
            resource = getCmsResource();
            if (resource != null) {
                m_title = resource.getName();
            } else {
                m_title = DEFAULT_VALUE_TITLE;
            }
        } else {
            m_title = title;
        }
    }

    /**
     * Sets the types of the document container.<p>
     *
     * @param types the types of the document container
     */
    public void setTypes(Map<String, CmsDocument> types) {

        m_types = types;
    }

    /**
     * Analyzes the document type.<p>
     * 
     * Determines the document locale, the attachment number and the attachment version (if present)
     * using regular expressions.<p>
     */
    private void init() {

        String docName = getCmsResource().getName();

        // First part deprecated. Integer.parseInt() caused exception if filenames contained "_" followed by
        // a number part with a number too large to fit in 32-bit. (+- 2.147.483.647). This method, splitting 
        // filenames into a root-doc (string) and attached parts (indicated by number) no longer supported. 

        // check if an attachment number is present
        //        Matcher matcher = PATTERN_ATT_NUMBER.matcher(docName);
        //        if (matcher.matches()) {
        //            // this is an attachment, set attachment number
        //            Integer partNumber = new Integer(Integer.parseInt(matcher.group(2)));
        //            setAttachmentNumber(partNumber);
        //            int startIndex = matcher.start(1);
        //            int endIndex = matcher.end(2);
        //            docName = docName.substring(0, startIndex) + docName.substring(endIndex);
        //        }

        // check if a locale information is present
        Matcher matcher = PATTERN_DOC_LOCALE.matcher(docName);
        if (matcher.matches()) {
            Locale testLocale = new Locale(matcher.group(2));
            if (OpenCms.getLocaleManager().getDefaultLocales().contains(testLocale)) {
                // this is the locale information for the document
                setLocale(testLocale);
                int startIndex = matcher.start(1);
                int endIndex = matcher.end(2);
                docName = docName.substring(0, startIndex) + docName.substring(endIndex);
            }
        }

        // check if a document id number is present
        matcher = PATTERN_DOC_ID.matcher(docName);
        if (matcher.matches()) {
            // this is a document id number
            setDocumentId(matcher.group(2));
            int startIndex = matcher.start(1);
            int endIndex = matcher.end(2);
            docName = docName.substring(0, startIndex) + docName.substring(endIndex);
        }

        setDocumentName(CmsResource.getFolderPath(getPath()) + docName);
    }

}