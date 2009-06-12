/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsDocumentFactory.java,v $
 * Date   : $Date: 2009/06/12 13:51:18 $
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

package com.alkacon.opencms.documentcenter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides helper methods to create CmsDocument objects or sorted lists of CmsDocument objects from {@link org.opencms.file.CmsResource} objects.<p>
 * 
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.2.0 
 */
public final class CmsDocumentFactory {

    /**
     * A comparator for the creation date of 2 documents ascending.<p>
     */
    protected static final Comparator COMPARE_DATECREATED_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getDateCreated() > d2.getDateCreated()) {
                orderValue = 1;
            } else if (d1.getDateCreated() < d2.getDateCreated()) {
                orderValue = -1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the creation date of 2 documents descending.<p>
     */
    protected static final Comparator COMPARE_DATECREATED_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getDateCreated() > d2.getDateCreated()) {
                orderValue = -1;
            } else if (d1.getDateCreated() < d2.getDateCreated()) {
                orderValue = 1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the last modified date of 2 documents ascending.<p>
     */
    protected static final Comparator COMPARE_DATEMODIFIED_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getDateLastModified() > d2.getDateLastModified()) {
                orderValue = 1;
            } else if (d1.getDateLastModified() < d2.getDateLastModified()) {
                orderValue = -1;
            }
            return orderValue;
        }
    };

    /**
     * A comparator for the last modified date of 2 documents descending.<p>
     */
    protected static final Comparator COMPARE_DATEMODIFIED_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getDateLastModified() > d2.getDateLastModified()) {
                orderValue = -1;
            } else if (d1.getDateLastModified() < d2.getDateLastModified()) {
                orderValue = 1;
            }
            return orderValue;
        }
    };

    /**
     * A comparator for the ID of 2 documents ascending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_ID_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = d1.getDocumentId().compareToIgnoreCase(d2.getDocumentId());
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the ID of 2 documents descending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_ID_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = -(d1.getDocumentId().compareToIgnoreCase(d2.getDocumentId()));
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the postfix of 2 documents ascending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_POSTFIX_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = d1.getPostfixType().compareToIgnoreCase(d2.getPostfixType());
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the postfix of 2 documents descending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_POSTFIX_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = -(d1.getPostfixType().compareToIgnoreCase(d2.getPostfixType()));
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the size of 2 documents ascending.<p>
     */
    protected static final Comparator COMPARE_SIZE_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getSize() > d2.getSize()) {
                orderValue = 1;
            } else if (d1.getSize() < d2.getSize()) {
                orderValue = -1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the size of 2 documents descending.<p>
     */
    protected static final Comparator COMPARE_SIZE_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getSize() > d2.getSize()) {
                orderValue = -1;
            } else if (d1.getSize() < d2.getSize()) {
                orderValue = 1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the sort order property value of 2 documents ascending.<p>
     */
    protected static final Comparator COMPARE_SORTORDER_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getSortOrder() > d2.getSortOrder()) {
                orderValue = 1;
            } else if (d1.getSortOrder() < d2.getSortOrder()) {
                orderValue = -1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the sort order property value of 2 documents descending.<p>
     */
    protected static final Comparator COMPARE_SORTORDER_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = 0;
            if (d1.getSortOrder() > d2.getSortOrder()) {
                orderValue = -1;
            } else if (d1.getSortOrder() < d2.getSortOrder()) {
                orderValue = 1;
            }
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the title of 2 documents ascending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_TITLE_ASC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = d1.getTitle().compareToIgnoreCase(d2.getTitle());
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /**
     * A comparator for the title of 2 documents descending ignoring case differences.<p>
     */
    protected static final Comparator COMPARE_TITLE_DESC = new Comparator() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {

            CmsDocument d1 = getSortDocument(o1);
            CmsDocument d2 = getSortDocument(o2);

            int orderValue = -(d1.getTitle().compareToIgnoreCase(d2.getTitle()));
            return orderBySecondCriteria(orderValue, d1, d2);
        }
    };

    /** Sort methods array for fast lookup. */
    protected static final String[] SORT_METHODS = {
        CmsDocument.SORT_METHOD_ALPHABETICAL,
        CmsDocument.SORT_METHOD_BY_DATEMODIFIED,
        CmsDocument.SORT_METHOD_BY_SORTORDER,
        CmsDocument.SORT_METHOD_SIZE,
        CmsDocument.SORT_METHOD_TYPE,
        CmsDocument.SORT_METHOD_BY_DATECREATED,
        CmsDocument.SORT_METHOD_BY_ID};

    /** Sort methods list for fast lookup. */
    protected static final List SORT_METHODS_LIST = Arrays.asList(SORT_METHODS);

    /** File names array which should be ignored as documents. */
    protected static final String[] VFS_IGNORED_FILES = {
        "$",
        "index.",
        "news.html",
        "newdocuments.html",
        "newdocuments.jsp",
        "search.html"};

    /** File names list which should be ignored as documents. */
    protected static final List VFS_IGNORED_FILES_LIST = Arrays.asList(VFS_IGNORED_FILES);

    /**
     * Returns an initialized document object or a null document if the document should not be displayed in the list.<p>
     * 
     * Only the resource name without path information is checked against the file name patterns to ignore.<p>
     * 
     * @param cms the current users context
     * @param resource the resource acting as document
     * @param includeFolders flag to determine if folders are included or not
     * 
     * @return an initialized document object or a null document
     */
    public static CmsDocument createDocument(CmsObject cms, CmsResource resource, boolean includeFolders) {

        return createDocument(cms, resource, includeFolders, true, false);
    }

    /**
     * Returns an initialized document object or a null document if the document should not be displayed in the list.<p>
     * 
     * @param cms the current users context
     * @param resource the resource acting as document
     * @param includeFolders flag to determine if folders are included or not
     * @param completePathForIgnored if true, the complete site path of the resource is checked for the ignored files patterns
     * 
     * @return an initialized document object or a null document
     */
    public static CmsDocument createDocument(
        CmsObject cms,
        CmsResource resource,
        boolean includeFolders,
        boolean completePathForIgnored) {

        return createDocument(cms, resource, includeFolders, true, completePathForIgnored);
    }

    /**
     * Returns an initialized document object or a null document if the document should not be displayed in the list.<p>
     * 
     * The document visibility is only checked if the checkIgnored value is set to <code>true</code>.<p>
     * 
     * @param cms the current users context
     * @param resource the resource acting as document
     * @param includeFolders flag to determine if folders are included or not
     * @param checkIgnored if true, the resource is checked if it should be ignored depending on the ignored files patterns
     * @param completePathForIgnored if true, the complete site path of the resource is checked for the ignored files patterns
     * 
     * @return an initialized document object or a null document
     */
    public static CmsDocument createDocument(
        CmsObject cms,
        CmsResource resource,
        boolean includeFolders,
        boolean checkIgnored,
        boolean completePathForIgnored) {

        // read all resource properties to get the needed information for the document
        Map allProperties = null;
        try {
            allProperties = CmsProperty.toMap(cms.readPropertyObjects(resource, false));
        } catch (Exception e) {
            // error reading properties
            allProperties = Collections.EMPTY_MAP;
        }

        // check if the document should be added to the list
        String showDocumentValue = (String)allProperties.get(CmsDocument.PROPERTY_SHOWDOCUMENT);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(showDocumentValue)) {
            if (!Boolean.valueOf(showDocumentValue).booleanValue()) {
                return CmsDocument.getNullDocument();
            }
        } else if (resource.isFolder() && !includeFolders) {
            return CmsDocument.getNullDocument();
        }

        String path = cms.getSitePath(resource);

        // check if the document should not be shown
        if (checkIgnored && isIgnoredDocument(path, completePathForIgnored)) {
            return CmsDocument.getNullDocument();
        }

        // set title and sort order from properties
        String title = null;
        if (resource.isFolder()) {
            // try to get localized title for folder
            String locTitle = CmsPropertyDefinition.PROPERTY_TITLE
                + "_"
                + cms.getRequestContext().getLocale().toString();
            try {
                title = cms.readPropertyObject(resource, locTitle, false).getValue();
            } catch (CmsException e) {
                // ignore, property might not be defined
            }
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            title = (String)allProperties.get(CmsPropertyDefinition.PROPERTY_TITLE);
        }
        // check the presence of the title property
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            title = resource.getName();
            if (title.endsWith("/")) {
                title = title.substring(0, (title.length() - 1));
            }
        }
        String sortOrder = (String)allProperties.get(CmsDocument.PROPERTY_SORTORDER);

        // return the initialized document
        return new CmsDocument(resource, path, cms.getRequestContext().getLocale(), title, sortOrder);

    }

    /**
     * Returns an initialized document including all language versions and attachments of the specified resource.<p>
     * 
     * @param jsp the current users JSP context
     * @param resourceName the absolute path of the resource to get the document for
     * @return an initialized document including all language versions and attachments of the specified resource
     */
    public static CmsDocument createDocumentWithVersions(CmsDocumentFrontend jsp, String resourceName) {

        // use special document list with flag "only versions" set to true
        List result = new CmsDocumentList(
            jsp.isUseTypes(),
            jsp.getDefaultType(),
            jsp.isUseAttachments(),
            jsp.isUseLanguages(),
            true,
            2);
        CmsObject cms = jsp.getCmsObject();
        try {
            // read the current resource
            CmsResource res = cms.readResource(resourceName);
            // add the document of the current resource to the list
            CmsDocument document = createDocument(cms, res, false, false, false);

            // check if there is a document with the given resource name
            if (document.isNullDocument()) {
                return document;
            }
            result.add(document);

            List files = cms.getFilesInFolder(CmsResource.getFolderPath(resourceName));
            if (files.contains(res)) {
                // remove the original file from the list of found files
                files.remove(res);
            }
            Iterator i = files.iterator();
            while (i.hasNext()) {
                CmsResource currRes = (CmsResource)i.next();
                // add the resource if it is not he same 
                result.add(createDocument(cms, currRes, false, false, false));
            }

            // close the list
            ((CmsDocumentList)result).closeList();
        } catch (CmsException e) {
            // error getting documents
        }

        return (CmsDocument)result.get(0);
    }

    /**
     * Builds a list with all <code>{@link CmsDocument}</code> Objects inside a given folder sorted as specified by the sort method.<p>
     * 
     * @param jsp the current users JSP context
     * @param folder the folder in as specified in the channel name
     * @param sortMethodProperty the sort method property of the folder of this resource
     * @return ArrayList with all documents of the specified folder
     */
    public static List getDocumentsForFolder(CmsDocumentFrontend jsp, String folder, String sortMethodProperty) {

        sortMethodProperty = sortMethodProperty.trim();
        CmsObject cms = jsp.getCmsObject();

        // check if files and folders should be sorted together
        boolean sortIncludeFolders = false;
        if (sortMethodProperty.toLowerCase().indexOf(CmsDocument.VALUE_INCLUDE_FOLDERS) != -1) {
            sortMethodProperty = sortMethodProperty.substring(0, sortMethodProperty.lastIndexOf(":"));
            sortIncludeFolders = true;
        }

        try {

            // get all subfolders in the folder from the VFS
            List subfolders = cms.getSubFolders(folder);
            // get all files in the folder from the VFS
            List files = cms.getFilesInFolder(folder);

            List documentList = new ArrayList(files.size() + subfolders.size());

            // sort files and folders together
            if (sortIncludeFolders) {
                files.addAll(subfolders);
                if (files.size() > 0) {
                    documentList = getSortedDocuments(jsp, files, sortMethodProperty, true);
                }
            } else {
                // create separately sorted folder & files list           
                if (subfolders.size() > 0) {
                    documentList.addAll(getSortedDocuments(jsp, subfolders, sortMethodProperty, true));
                }
                if (files.size() > 0) {
                    documentList.addAll(getSortedDocuments(jsp, files, sortMethodProperty, false));
                }
            }
            return documentList;
        } catch (Exception e) {
            // ignore
        }

        return new ArrayList(0);
    }

    /**
     * Returns a sorted list of documents from a given list of resources.<p>
     * 
     * @param jsp the current users JSP context
     * @param resources the resource list
     * @param sortMethod the sort method for the documents
     * @param includeFolders flag to determine if folders should be added to the document list
     * @return list with sorted documents 
     */
    public static List getSortedDocuments(
        CmsDocumentFrontend jsp,
        List resources,
        String sortMethod,
        boolean includeFolders) {

        return getSortedDocuments(jsp, resources, sortMethod, includeFolders, false);
    }

    /**
     * Returns a sorted list of documents from a given list of resources.<p>
     * 
     * @param jsp the current users JSP context
     * @param resources the resource list
     * @param sortMethod the sort method for the documents
     * @param includeFolders flag to determine if folders should be added to the document list
     * @param completePathForIgnored if true, the complete site path of the resource is checked for the ignored files patterns
     * @return list with sorted documents 
     */
    public static List getSortedDocuments(
        CmsDocumentFrontend jsp,
        List resources,
        String sortMethod,
        boolean includeFolders,
        boolean completePathForIgnored) {

        if (sortMethod.toLowerCase().indexOf(CmsDocument.VALUE_INCLUDE_FOLDERS) != -1) {
            sortMethod = sortMethod.substring(0, sortMethod.lastIndexOf(":"));
        }

        // create for each file an instance of CmsDocument and add it to the list of all documents
        Iterator allResources = resources.iterator();
        List documentList = new CmsDocumentList(
            jsp.isUseTypes(),
            jsp.getDefaultType(),
            jsp.isUseAttachments(),
            jsp.isUseLanguages(),
            false,
            resources.size());

        while (allResources.hasNext()) {
            CmsResource currentResource = (CmsResource)allResources.next();

            CmsDocument document = CmsDocumentFactory.createDocument(
                jsp.getCmsObject(),
                currentResource,
                includeFolders,
                completePathForIgnored);
            documentList.add(document);
        }

        // close the list
        ((CmsDocumentList)documentList).closeList();

        // sort the documents if we found at least 2 documents
        if (documentList.size() > 1) {
            // extract the sort method parts to determine the sort method to use
            String sortDirection = "";
            if (sortMethod.indexOf(":") != -1) {
                sortDirection = sortMethod.substring(sortMethod.indexOf(":") + 1);
                sortMethod = sortMethod.substring(0, sortMethod.indexOf(":"));
            }

            // determine the sort comparator to use
            if (CmsStringUtil.isEmpty(sortMethod)) {
                sortMethod = CmsDocument.SORT_METHOD_ALPHABETICAL;
            }
            Comparator comp = getSortComparator(sortMethod, sortDirection);

            // sort the documents
            Collections.sort(documentList, comp);
        }

        // clear objects to release memory
        allResources = null;

        return documentList;
    }

    /**
     * Returns the comparator to sort the document list with depending on the sort method and direction values.<p>
     * 
     * @param sortMethod the sort method for the documents
     * @param sortDirection the dort direction (ascending or descending) for the documents
     * @return the comparator to sort the document list with
     */
    protected static Comparator getSortComparator(String sortMethod, String sortDirection) {

        String key = sortMethod.trim().toLowerCase();
        switch (SORT_METHODS_LIST.indexOf(key)) {
            case 0:
                // sort in alphabetical order
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_DESC)) {
                    return COMPARE_TITLE_DESC;
                } else {
                    return COMPARE_TITLE_ASC;
                }
            case 1:
                // sort by date (modified)
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_ASC)) {
                    return COMPARE_DATEMODIFIED_ASC;
                } else {
                    return COMPARE_DATEMODIFIED_DESC;
                }
            case 2:
                // sort by the value of the SortOrder property
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_ASC)) {
                    return COMPARE_SORTORDER_ASC;
                } else {
                    return COMPARE_SORTORDER_DESC;
                }
            case 3:
                // sort by file size
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_ASC)) {
                    return COMPARE_SIZE_ASC;
                } else {
                    return COMPARE_SIZE_DESC;
                }
            case 4:
                // sort by postfix type
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_DESC)) {
                    return COMPARE_POSTFIX_DESC;
                } else {
                    return COMPARE_POSTFIX_ASC;
                }
            case 5:
                // sort by date (created)
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_ASC)) {
                    return COMPARE_DATECREATED_ASC;
                } else {
                    return COMPARE_DATECREATED_DESC;
                }
            case 6:
                // sort by document ID
                if (sortDirection.equalsIgnoreCase(CmsDocument.SORT_DIRECTION_DESC)) {
                    return COMPARE_ID_DESC;
                } else {
                    return COMPARE_ID_ASC;
                }
            default:
                return COMPARE_TITLE_ASC;
        }
    }

    /**
     * Returns the document for the sort procedure matching the current frontend locale.<p>
     * 
     * @param o the Object to be sorted
     * @return the document for the sort procedure matching the current frontend locale
     */
    protected static CmsDocument getSortDocument(Object o) {

        return ((CmsDocument)o).getDocumentForCurrentLocale();
    }

    /**
     * Returns if the document should be ignored depending on its resource name.<p>
     * 
     * @param resourcePath the site path of the document to check
     * @param completePathForIgnored if true, the complete site path of the resource is checked for the ignored files patterns
     * @return true if the document should be ignored depending on its resource name, otherwise false
     */
    protected static boolean isIgnoredDocument(String resourcePath, boolean completePathForIgnored) {

        Iterator i = VFS_IGNORED_FILES_LIST.iterator();
        String resourceName = "";
        if (completePathForIgnored) {
            // check the complete resource path
            resourceName = resourcePath;
        } else {
            // check only the resource name
            resourceName = CmsResource.getName(resourcePath);
        }
        while (i.hasNext()) {
            String ignoreFile = (String)i.next();
            if (resourceName.indexOf(ignoreFile) != -1) {
                // this file should be ignored
                return true;
            }
        }
        // file should be shown
        return false;
    }

    /**
     * Sorts 2 documents by the second sorting criteria (modification date) if the documents are in the same order.<p>
     * 
     * @param orderValue the order value after sorting by the first criteria
     * @param d1 the first document
     * @param d2 the second document
     * @return the new order value after sorting by the second criteria
     */
    protected static int orderBySecondCriteria(int orderValue, CmsDocument d1, CmsDocument d2) {

        if (orderValue == 0) {
            // objects are still in same order, use second criteria: modification date  
            if (d1.getDateLastModified() > d2.getDateLastModified()) {
                return -1;
            }
            if (d1.getDateLastModified() < d2.getDateLastModified()) {
                return 1;
            }
        }
        return orderValue;
    }

    /**
     * Hidden constructor, this is a utility class.<p>
     */
    private CmsDocumentFactory() {

        // nothing to initialize
    }

}
