/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsDocumentFrontend.java,v $
 * Date   : $Date: 2010/03/19 15:31:13 $
 * Version: $Revision: 1.3 $
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

import org.opencms.file.CmsPropertyDefinition;
import org.opencms.i18n.CmsMessages;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.jsp.CmsJspNavElement;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Provides customized methods for the document center frontend output.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.2.1
 */
/**
 *
 */
public class CmsDocumentFrontend extends CmsJspActionElement {

    /** Request attribute that stores if a disclaimer should be shown. */
    public static final String ATTR_DISCLAIMER = "oamp_doccenter_disclaimer";

    /** Request attribute that stores the absolute path to the current document center folder. */
    public static final String ATTR_FULLPATH = "oamp_doccenter_fullpath";

    /** Request attribute that stores the relative path to the current document center folder. */
    public static final String ATTR_PATHPART = "oamp_doccenter_pathpart";

    /** Request attribute that stores the absolute path to the document center start folder. */
    public static final String ATTR_STARTPATH = "oamp_doccenter_startpath";

    /** Name of the column: date created. */
    public static final String COLUMN_NAME_DATECREATED = "datecreated";

    /** Name of the column: date modified. */
    public static final String COLUMN_NAME_DATEMODIFIED = "datemodified";

    /** Name of the column: document folder. */
    public static final String COLUMN_NAME_FOLDER = "folder";

    /** Name of the column: document id. */
    public static final String COLUMN_NAME_ID = "id";

    /** Name of the column: document languages. */
    public static final String COLUMN_NAME_LANGUAGE = "lang";

    /** Name of the column: document size. */
    public static final String COLUMN_NAME_SIZE = "size";

    /** Name of the column: document title. */
    public static final String COLUMN_NAME_TITLE = "title";

    /** Name of the column: document type. */
    public static final String COLUMN_NAME_TYPE = "type";

    /** The sortable column default sort directions, must correspond to the sortable columns {@link #COLUMNS_SORTABLE}. */
    public static final String[] COLUMNS_DIRECTIONS = {
        CmsDocument.SORT_DIRECTION_ASC,
        CmsDocument.SORT_DIRECTION_ASC,
        CmsDocument.SORT_DIRECTION_ASC,
        CmsDocument.SORT_DIRECTION_DESC,
        CmsDocument.SORT_DIRECTION_DESC,
        CmsDocument.SORT_DIRECTION_DESC};

    /** The sortable column default sort directions as list. */
    public static final List<String> COLUMNS_DIRECTIONS_LIST = Arrays.asList(COLUMNS_DIRECTIONS);

    /** Stores the column names that are sortable. */
    public static final String[] COLUMNS_SORTABLE = {
        COLUMN_NAME_TYPE,
        COLUMN_NAME_ID,
        COLUMN_NAME_TITLE,
        COLUMN_NAME_SIZE,
        COLUMN_NAME_DATEMODIFIED,
        COLUMN_NAME_DATECREATED};

    /** The column names that are sortable as list. */
    public static final List<String> COLUMNS_SORTABLE_LIST = Arrays.asList(COLUMNS_SORTABLE);

    /** Name of the file extensions of the icons of the document list. */
    public static final String ICON_POSTFIX = ".gif";

    /** Page type: default (shows the document list). */
    public static final String PAGE_TYPE_DEFAULT = "default";

    /** Request parameter name for the sort column parameter. */
    public static final String PARAM_SORT_COLUMN = "sortcol";

    /** Request parameter name for the sort direction parameter. */
    public static final String PARAM_SORT_DIRECTION = "sortdir";

    /** Property name to look if the document id column is shown. */
    public static final String PROPERTY_COLUMN_ID = "docs.columnid";

    /** Property name to look for document list column names to hide. */
    public static final String PROPERTY_COLUMNS_HIDE = "docs.hidecolumns";

    /** Property name to look for document list date columns to hide (old way, used for compatibility reasons). */
    public static final String PROPERTY_COLUMNS_HIDE_DATE = "categoryDateCreated";

    /** Property name to determine if the document center should consider attachments of the documents. */
    public static final String PROPERTY_USE_ATTACHMENTS = "docs.useattachments";

    /** Property name to set the default type if using different types. */
    public static final String PROPERTY_USE_DEFAULTTYPE = "docs.defaulttype";

    /** Property name to determine if the document center should consider language versions of the documents. */
    public static final String PROPERTY_USE_LANGUAGES = "docs.uselanguages";

    /** Property name to determine if the document center should consider different types of the documents. */
    public static final String PROPERTY_USE_TYPES = "docs.usetypes";

    /** The property values of the sort methods, must be in the same order as {@link #COLUMNS_SORTABLE}. */
    public static final String[] SORT_METHODS = {
        CmsDocument.SORT_METHOD_TYPE,
        CmsDocument.SORT_METHOD_BY_ID,
        CmsDocument.SORT_METHOD_ALPHABETICAL,
        CmsDocument.SORT_METHOD_SIZE,
        CmsDocument.SORT_METHOD_BY_DATEMODIFIED,
        CmsDocument.SORT_METHOD_BY_DATECREATED};

    /** The property values of the sort methods as list. */
    public static final List<String> SORT_METHODS_LIST = Arrays.asList(SORT_METHODS);

    /** The extension of the default type if using different types of documents. */
    private String m_defaultType;

    /** The page type to show. */
    private String m_pageType;

    /** The parameter of the sort column. */
    private String m_paramSortColumn;

    /** The parameter of the sort direction. */
    private String m_paramSortDirection;

    /** The value of the sort method property ("method:direction:includefolders"). */
    private String m_sortMethod;

    /** Determines if attachments of documents are present. */
    private Boolean m_useAttachments;

    /** Determines if language versions of documents are present. */
    private Boolean m_useLanguages;

    /** Determines if different types of documents are present. */
    private Boolean m_useTypes;

    /**
     * Empty constructor, required for every JavaBean.
     */
    public CmsDocumentFrontend() {

        super();
    }

    /**
     * Constructor, with parameters.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsDocumentFrontend(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
        // TODO: fix all current uri references to use proper sitepath
    }

    /**
     * Creates the HTML code for the default breadcrumb navigation without the "up one folder" icon.<p>
     * 
     * Used by: elements/navigation.jsp.<p>
     * 
     * @param startFolder the start folder to build the navigation from
     * @param navList the navigation elements (CmsJspNavElement)
     * @param anchorClass the CSS class which will be used for the anchors
     * @param separator the separator which will be used to separate the entries
     * @param sepBeforeFirst if true, separator will be displayed before first element, too
     * @return the HTML code for the breadcrumb navigation
     */
    public String buildBreadCrumbNavigation(
        String startFolder,
        List<CmsJspNavElement> navList,
        String anchorClass,
        String separator,
        boolean sepBeforeFirst) {

        StringBuffer result = new StringBuffer(64);
        boolean isFirst = true;
        if (sepBeforeFirst) {
            isFirst = false;
        }

        String locNavText = CmsPropertyDefinition.PROPERTY_NAVTEXT + "_" + getRequestContext().getLocale().toString();
        String locTitle = CmsPropertyDefinition.PROPERTY_TITLE + "_" + getRequestContext().getLocale().toString();
        String currFolder = (String)getRequest().getAttribute(ATTR_FULLPATH);

        // create the navigation 
        Iterator<CmsJspNavElement> i = navList.iterator();
        while (i.hasNext()) {
            CmsJspNavElement navElement = i.next();

            String navText = navElement.getProperties().get(locNavText);
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(navText)) {
                navText = navElement.getNavText();
            }

            if (navElement.getResourceName().startsWith(startFolder)) {

                // check the navigation text
                if (navText.indexOf("??? NavText") != -1) {
                    navText = navElement.getProperties().get(locTitle);
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(navText)) {
                        navText = navElement.getTitle();
                    }
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(navText)) {
                        navText = navElement.getFileName();
                    }
                    if (navText.endsWith("/")) {
                        navText = navText.substring(0, (navText.length() - 1));
                    }
                }

                // don't show separator in front of first element
                if (!isFirst) {
                    result.append(separator);
                } else {
                    isFirst = false;
                }

                if (navElement.getResourceName().equals(currFolder) && (navList.size() > 1)) {
                    // the current folder will not be linked
                    result.append("<span class=\"");
                    result.append(anchorClass);
                    result.append("\">");
                    result.append(navText);
                    result.append("</span>");
                } else {
                    // create the link to the folder
                    result.append("<a href=\"");
                    result.append(CmsDocumentFactory.getLink(this, navElement.getResourceName()));
                    result.append("\" class=\"");
                    result.append(anchorClass);
                    result.append("\">");
                    result.append(navText);
                    result.append("</a>");
                }
            }
        }
        return result.toString();
    }

    /**
     * Creates the HTML code for the document or resource icon in document list, version list and search result list.<p>
     * 
     * Used by: jsptemplates/list_documents.txt, elements/docversions.jsp, pages/jsp_pages/page_search_code.jsp.<p>
     * 
     * @param docName the resource name of the document
     * @param messages the localized messages
     * @param resourcePath the path to the images
     * @param isFolder true if the document is a folder, otherwise false
     * @return the HTML code for the document icon
     */
    public String buildDocIcon(String docName, CmsMessages messages, String resourcePath, boolean isFolder) {

        return buildDocIcon(docName, messages, resourcePath, isFolder, 16, 16);
    }

    /**
     * Creates the HTML code for the document or resource icon in document list, version list and search result list.<p>
     * 
     * Used by: jsptemplates/list_documents.txt, elements/docversions.jsp, pages/jsp_pages/page_search_code.jsp.<p>
     * 
     * @param docName the resource name of the document
     * @param messages the localized messages
     * @param resourcePath the path to the images
     * @param isFolder true if the document is a folder, otherwise false
     * @param imgWidth the width of the icon image
     * @param imgHeight the height of the icon image
     * @return the HTML code for the document icon
     */
    public String buildDocIcon(
        String docName,
        CmsMessages messages,
        String resourcePath,
        boolean isFolder,
        int imgWidth,
        int imgHeight) {

        String iconSrc, iconTitle, iconAlt;

        // folder
        if (isFolder) {
            iconSrc = "ic_folder";
            iconTitle = messages.key("documentlist.icon.folder.title");
            iconAlt = messages.key("documentlist.icon.folder.alt");
        }

        // file
        else {
            String postfix = CmsDocument.getPostfix(docName);

            iconSrc = "ic_app_" + postfix;
            iconTitle = messages.keyDefault("documentlist.icon.file.title." + postfix, "");
            iconAlt = messages.keyDefault("documentlist.icon.file.alt." + postfix, "");

            if ((postfix.equals("")) || (!getCmsObject().existsResource(resourcePath + iconSrc + ICON_POSTFIX))) {
                iconSrc = "ic_app_unknown";
                iconTitle = messages.key("documentlist.icon.file.title.unknown");
                iconAlt = messages.key("documentlist.icon.file.alt.unknown");
            }
        }

        StringBuffer result = new StringBuffer(256);
        result.append("<img src=\"");
        result.append(link(resourcePath + iconSrc + ICON_POSTFIX));
        result.append("\" width=\"").append(imgWidth).append("\" height=\"").append(imgHeight);
        result.append("\" border=\"0\" alt=\"");
        result.append(iconAlt);
        result.append("\" title=\"");
        result.append(iconTitle);
        result.append("\"/>");

        return result.toString();
    }

    /**
     * Returns the column header including the link to sort the list by the column criteria.<p>
     * 
     * @param columnName the internal column name
     * @param resourcePath the path to the image resources
     * @param messages the initialized localized messages to use
     * @return the column header including the link to sort the list by the column criteria
     */
    public String getColumnHeader(String columnName, String resourcePath, CmsMessages messages) {

        if (!isBeanSortInitialized()) {
            initSort();
        }
        if (m_pageType.equals("default") && COLUMNS_SORTABLE_LIST.contains(columnName)) {
            // column is sortable and we are on a default page, so columns are sortable
            StringBuffer result = new StringBuffer(256);

            String dir = m_paramSortDirection;
            String newDir = dir;
            boolean isCurrentColumn = false;
            if (columnName.equals(m_paramSortColumn)) {
                // the column is the current sort column
                isCurrentColumn = true;
                // switch new sort direction link for current sort column
                if ((dir != null) && dir.equals(CmsDocument.SORT_DIRECTION_ASC)) {
                    newDir = CmsDocument.SORT_DIRECTION_DESC;
                } else {
                    newDir = CmsDocument.SORT_DIRECTION_ASC;
                }
            } else {
                // use default sort direction for other columns
                newDir = COLUMNS_DIRECTIONS_LIST.get(COLUMNS_SORTABLE_LIST.indexOf(columnName));
            }

            // create the link for sorting the column
            StringBuffer link = new StringBuffer(128);
            link.append((String)getRequest().getAttribute(ATTR_FULLPATH));
            link.append("?").append(PARAM_SORT_COLUMN).append("=").append(columnName);
            link.append("&amp;").append(PARAM_SORT_DIRECTION).append("=").append(newDir);

            // set the title for the headline
            String sortTitle = messages.key(
                "documentlist.sort." + newDir,
                messages.key("documentlist.headline." + columnName));

            result.append("<a href=\"");
            result.append(CmsDocumentFactory.getLink(this, link.toString()));
            result.append("\" class=\"docshead\" title=\"");
            result.append(sortTitle);
            result.append("\">");
            result.append(messages.key("documentlist.headline." + columnName));
            if (isCurrentColumn) {
                // set the marker icon for the current sort column
                result.append("&nbsp;");
                result.append("<img src=\"");
                result.append(resourcePath).append("ic_sort_").append(dir).append(".png");
                result.append("\" border=\"0\" alt=\"");
                result.append(sortTitle);
                result.append("\" title=\"");
                result.append(sortTitle);
                result.append("\"/>");
            }
            result.append("</a>");
            return result.toString();
        } else {
            // column is not sortable, simply print localized headline
            return messages.key("documentlist.headline." + columnName);
        }
    }

    /**
     * Returns the defaultType.<p>
     *
     * @return the defaultType
     */
    public String getDefaultType() {

        return m_defaultType;
    }

    /**
     * Collects the names of the columns to hide in the document list view.<p>
     * 
     * Columns that can be hidden are: date created, date last modified, document id.<p>
     * 
     * @return the names of the clumns to hide
     */
    public List<String> getHiddenColumns() {

        List<String> result = new ArrayList<String>(4);
        String ignoredCols = property(PROPERTY_COLUMNS_HIDE, "search", "");
        result = CmsStringUtil.splitAsList(ignoredCols, ';');

        // backward compatibility: check for property defining visibility of date columns
        String showDateData = property("categoryDateCreated", "search", "");
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(showDateData)) {
            List<String> showDateList = CmsStringUtil.splitAsList(showDateData, ";");
            if (showDateList.size() >= 2) {
                if (!Boolean.valueOf(showDateList.get(1)).booleanValue()) {
                    result.add(COLUMN_NAME_DATEMODIFIED);
                }
            }
            if (showDateList.size() >= 1) {
                if (!Boolean.valueOf(showDateList.get(0)).booleanValue()) {
                    result.add(COLUMN_NAME_DATECREATED);
                }
            }
        }

        // manually add ID column to list of hidden columns if not explicitly set to visible
        String idCol = property(PROPERTY_COLUMN_ID, "search", Boolean.FALSE.toString());
        boolean showIdColumn = Boolean.valueOf(idCol).booleanValue();

        if (!showIdColumn) {
            result.add(COLUMN_NAME_ID);
        }

        // check if language versions are considered
        if (!isUseLanguages()) {
            result.add(COLUMN_NAME_LANGUAGE);
        }

        return result;
    }

    /**
     * Returns the sort method property value to use to sort the document list.<p>
     * 
     * @return the sort method property value to use to sort the document list
     */
    public String getSortMethod() {

        if (!isBeanSortInitialized()) {
            initSort();
        }
        return m_sortMethod;
    }

    /**
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        // call super initialization
        super.init(context, req, res);
        // check if document language versions should be considered, the default is false
        String useLanguages = property(PROPERTY_USE_LANGUAGES, "search", "false");
        setUseLanguages(Boolean.valueOf(useLanguages).booleanValue());

        // check if document attachments should be considered, the default is true
        String useAttachments = property(PROPERTY_USE_ATTACHMENTS, "search", "true");
        setUseAttachments(Boolean.valueOf(useAttachments).booleanValue());

        // check if different types of documents should be considered, the default is false
        String useTypes = property(PROPERTY_USE_TYPES, "search", "false");
        setUseTypes(Boolean.valueOf(useTypes).booleanValue());

        // get the extension for the default type if document allows different types of documents, the default is null
        String defaultType = property(PROPERTY_USE_DEFAULTTYPE, "search");
        setDefaultType(defaultType);
    }

    /**
     * Initializes the necessary members to determine the sort method of the document list.<p>
     */
    public void initSort() {

        // read the sort parameters from the request
        m_paramSortColumn = CmsStringUtil.escapeHtml(getRequest().getParameter(PARAM_SORT_COLUMN));
        m_paramSortDirection = CmsStringUtil.escapeHtml(getRequest().getParameter(PARAM_SORT_DIRECTION));

        // check the page type and store it
        m_pageType = CmsStringUtil.escapeHtml(getRequest().getParameter("page_type"));
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_pageType)) {
            m_pageType = property("page_type", "uri", PAGE_TYPE_DEFAULT);
        }

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramSortColumn)) {
            // no request parameters found, determine current sort method from property
            String defaultSortMethod = CmsDocument.SORT_METHOD_ALPHABETICAL;
            String idCol = property(PROPERTY_COLUMN_ID, "search", Boolean.FALSE.toString());
            if (Boolean.valueOf(idCol).booleanValue()) {
                defaultSortMethod = CmsDocument.SORT_METHOD_BY_ID;
            }
            m_sortMethod = property(CmsDocument.PROPERTY_SORTMETHOD, "search", defaultSortMethod);

            String sortMethod = m_sortMethod;
            if (sortMethod.toLowerCase().indexOf(CmsDocument.VALUE_INCLUDE_FOLDERS) != -1) {
                // remove "includefolders" suffix
                sortMethod = sortMethod.substring(0, sortMethod.lastIndexOf(":"));
            }

            if (sortMethod.indexOf(":") != -1) {
                // store direction and remove it from String
                m_paramSortDirection = sortMethod.substring(sortMethod.indexOf(":") + 1);
                sortMethod = sortMethod.substring(0, sortMethod.indexOf(":"));
            }

            // determine the sort column
            int listIndex = SORT_METHODS_LIST.indexOf(sortMethod.toLowerCase());
            if (listIndex > -1) {
                // only get column if method was found in list of sortable columns (NOT for method "by sort order")
                m_paramSortColumn = COLUMNS_SORTABLE_LIST.get(listIndex);

                if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramSortDirection)) {
                    // no sort direction explicitly defined, get default sort direction
                    m_paramSortDirection = COLUMNS_DIRECTIONS_LIST.get(listIndex);
                }
            }
        } else {
            // found request parameters
            int listIndex = COLUMNS_SORTABLE_LIST.indexOf(m_paramSortColumn);
            m_sortMethod = SORT_METHODS_LIST.get(listIndex) + ":" + m_paramSortDirection;
        }
    }

    /**
     * Returns if the necessary members to determine the sort method of the document list are initialized.<p>
     * 
     * @return true if the necessary members to determine the sort method of the document list are initialized
     */
    public boolean isBeanSortInitialized() {

        return m_sortMethod != null;
    }

    /**
     * Returns if the document center should consider attachments of the documents.<p>
     * 
     * @return true if the document center should consider attachments of the documents, otherwise false
     */
    public boolean isUseAttachments() {

        return m_useAttachments.booleanValue();
    }

    /**
     * Returns if the document center should consider language versions of the documents.<p>
     * 
     * @return true if the document center should consider language versions of the documents, otherwise false
     */
    public boolean isUseLanguages() {

        return m_useLanguages.booleanValue();
    }

    /**
     * Returns if the document center should consider different types of the documents.<p>
     * 
     * @return true if the document center should consider different types of the documents, otherwise false
     */
    public boolean isUseTypes() {

        return m_useTypes.booleanValue();
    }

    /**
     * Returns all properties of the document center file structure.<p>
     * 
     * This overrides the standard property mechanism to be able to get property values
     * conveniently from the document center folders and files.<p>
     * 
     * @see org.opencms.jsp.CmsJspActionElement#properties(java.lang.String)
     */
    @Override
    public Map<String, String> properties(String file) {

        String origUri = getRequestContext().getUri();
        try {
            getRequestContext().setUri((String)getRequest().getAttribute(CmsDocumentFrontend.ATTR_FULLPATH));
            return super.properties(file);
        } finally {
            getRequestContext().setUri(origUri);
        }

    }

    /**
     * Returns a selected file property value from the document center file structure.<p>
     * 
     * This overrides the standard property mechanism to be able to get property values
     * conveniently from the document center folders and files.<p>
     *  
     * @see org.opencms.jsp.CmsJspActionElement#property(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public String property(String name, String file, String defaultValue, boolean escapeHtml) {

        String origUri = getRequestContext().getUri();
        try {
            getRequestContext().setUri((String)getRequest().getAttribute(CmsDocumentFrontend.ATTR_FULLPATH));
            return super.property(name, file, defaultValue, escapeHtml);
        } finally {
            getRequestContext().setUri(origUri);
        }
    }

    /**
     * Sets the defaultType.<p>
     *
     * @param defaultType the defaultType to set
     */
    public void setDefaultType(String defaultType) {

        m_defaultType = defaultType;
    }

    /**
     * Sets if the document center should consider attachments of the documents.<p>
     *
     * @param useAttachments if the document center should consider attachments of the documents
     */
    public void setUseAttachments(boolean useAttachments) {

        m_useAttachments = Boolean.valueOf(useAttachments);
    }

    /**
     * Sets if the document center should consider language versions of the documents.<p>
     *
     * @param useLanguages if the document center should consider language versions of the documents
     */
    public void setUseLanguages(boolean useLanguages) {

        m_useLanguages = Boolean.valueOf(useLanguages);
    }

    /**
     * Sets if the document center should consider different types of the documents.<p>
     *
     * @param useTypes if the document center should consider different types of the documents
     */
    public void setUseTypes(boolean useTypes) {

        m_useTypes = Boolean.valueOf(useTypes);
    }

}
