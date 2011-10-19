/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/NewDocumentsTree.java,v $
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

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsUUID;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;

/**
 * Helper class which provides static methods for the new documents functions of the document management.<p>
 *
 * @author  Andreas Zahner 
 * @author  Michael Emmerich 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public final class NewDocumentsTree {

    /** Session key prefix. */
    protected static final String C_SESSION_KEY_PARAMS = NewDocumentsTree.class.getName() + ".param.";

    /** Session key for search all. */
    public static final String C_DOCUMENT_SEARCH_PARAM_ALL = C_SESSION_KEY_PARAMS + "all";

    /** Session key for categorylist. */
    public static final String C_DOCUMENT_SEARCH_PARAM_CATEGORYLIST = C_SESSION_KEY_PARAMS + "categoryList";

    /** Session key for enddate. */
    public static final String C_DOCUMENT_SEARCH_PARAM_ENDDATE = C_SESSION_KEY_PARAMS + "endDate";

    /** Session key for site. */
    public static final String C_DOCUMENT_SEARCH_PARAM_SITE = C_SESSION_KEY_PARAMS + "site";

    /** Session key for startdate. */
    public static final String C_DOCUMENT_SEARCH_PARAM_STARTDATE = C_SESSION_KEY_PARAMS + "startDate";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(NewDocumentsTree.class);

    /**
     * Hide public constructor.<p> 
     */
    private NewDocumentsTree() {

        //noop
    }

    /**
     * Creates an HTML list with checkboxes for all given categories.<p>
     * 
     * @param categories list with the categories (LgtCategory)
     * @param attributes optional attributes for the input tags
     * @return the HTML code for a category input list
     */
    public static String buildCategoryList(List<CmsCategory> categories, String attributes) {

        StringBuffer retValue = new StringBuffer(128);
        Iterator<CmsCategory> i = categories.iterator();
        int counter = 0;
        while (i.hasNext()) {
            CmsCategory curCat = i.next();
            if (!"".equals(curCat.getCmsResource())) {
                retValue.append("<input type=\"checkbox\" name=\"cat"
                    + counter
                    + "\" id=\"cat"
                    + counter
                    + "\" value=\""
                    + curCat.getCmsResource()
                    + "\"");
                if (attributes != null) {
                    retValue.append(" " + attributes);
                }
                retValue.append(">&nbsp;");
                retValue.append(curCat.getTitle() + "<br>\n");
                counter++;
            }
        }

        // clear objects to release memory
        i = null;

        return retValue.toString();
    }

    /**
     * Builds the HTML code for a select box of days.<p>
     * 
     * @param timeMillis the time in milliseconds which should be preselected
     * @param fieldName the prefix of the name attribute
     * @param attributes optional additional attributes of the select tag
     * @param localeString the current locale String
     * @return the HTML code for a select box of days
     */
    public static String buildSelectDay(long timeMillis, String fieldName, String attributes, String localeString) {

        StringBuffer retValue = new StringBuffer(512);
        Locale locale = new Locale(localeString);
        Calendar cal = new GregorianCalendar(locale);
        cal.setTimeInMillis(timeMillis);

        retValue.append("<select name=\"" + fieldName + "day\"");
        if (attributes != null) {
            retValue.append(" " + attributes);
        }
        retValue.append(">\n");
        for (int i = 1; i < 32; i++) {
            retValue.append("\t<option value=\"" + i + "\"");
            if (cal.get(Calendar.DAY_OF_MONTH) == i) {
                retValue.append(" selected=\"selected\"");
            }
            retValue.append(">" + i + "</option>\n");
        }
        retValue.append("</select>\n");

        return retValue.toString();
    }

    /**
     * Builds the HTML code for a select box of months.<p>
     * 
     * @param timeMillis the time in milliseconds which should be preselected
     * @param fieldName the prefix of the name attribute
     * @param attributes optional additional attributes of the select tag
     * @param localeString the current locale String
     * @return the HTML code for a select box of months
     */
    public static String buildSelectMonth(long timeMillis, String fieldName, String attributes, String localeString) {

        StringBuffer retValue = new StringBuffer(512);
        Locale locale = new Locale(localeString);
        Calendar cal = new GregorianCalendar(locale);
        cal.setTimeInMillis(timeMillis);
        Calendar calTemp = new GregorianCalendar(locale);
        calTemp.setTimeInMillis(timeMillis);
        // set day to 2 to avoid display errors for days 29, 30 and 31
        calTemp.set(Calendar.DAY_OF_MONTH, 2);
        DateFormat df = new SimpleDateFormat("MMMM", locale);

        retValue.append("<select name=\"" + fieldName + "month\"");
        if (attributes != null) {
            retValue.append(" " + attributes);
        }
        retValue.append(">\n");
        for (int i = 0; i < 12; i++) {
            calTemp.set(Calendar.MONTH, i);
            retValue.append("\t<option value=\"" + (i + 1) + "\"");
            if (cal.get(Calendar.MONTH) == i) {
                retValue.append(" selected=\"selected\"");
            }
            retValue.append(">" + df.format(calTemp.getTime()) + "</option>\n");
        }
        retValue.append("</select>\n");

        return retValue.toString();
    }

    /**
     * Builds the HTML code for a select box of years.<p>
     * 
     * @param timeMillis the time in milliseconds which should be preselected
     * @param fieldName the prefix of the name attribute
     * @param attributes optional additional attributes of the select tag
     * @param localeString the current locale String
     * @param startyear the year to start with
     * @param endyear the last year to display in the selection
     * @return the HTML code for a select box of years
     */
    public static String buildSelectYear(
        long timeMillis,
        String fieldName,
        String attributes,
        String localeString,
        int startyear,
        int endyear) {

        StringBuffer retValue = new StringBuffer(512);
        Locale locale = new Locale(localeString);
        Calendar cal = new GregorianCalendar(locale);
        cal.setTimeInMillis(timeMillis);

        if (startyear > endyear) {
            startyear = endyear;
        }

        retValue.append("<select name=\"" + fieldName + "year\"");
        if (attributes != null) {
            retValue.append(" " + attributes);
        }
        retValue.append(">\n");
        for (int i = startyear; i <= endyear; i++) {
            retValue.append("\t<option value=\"" + i + "\"");
            if (cal.get(Calendar.YEAR) == i) {
                retValue.append(" selected=\"selected\"");
            }
            retValue.append(">" + i + "</option>\n");
        }
        retValue.append("</select>\n");

        return retValue.toString();
    }

    /**
     * Returns a list of resources which contains no linked entries.<p>
     * 
     * Links on the same resource entry are deleted from the list of resources.
     * This method has to be used after calling the method CmsObject.getResourcesInTimeRange(String, long, long);
     * 
     * @param resources the list of resources which may contain links
     * @return a filtered list of resources
     */
    public static List<CmsResource> filterLinkedResources(List<CmsResource> resources) {

        List<CmsResource> filteredResources = new ArrayList<CmsResource>();
        Set<CmsUUID> addedResources = new HashSet<CmsUUID>();
        long currentTime = System.currentTimeMillis();
        Iterator<CmsResource> i = resources.iterator();

        while (i.hasNext()) {

            CmsResource currentResource = i.next();

            // filter those documents that are folders or outside the release and expire window
            if (currentResource.isFolder()
                || (currentResource.getDateReleased() > currentTime)
                || (currentResource.getDateExpired() < currentTime)) {
                // skip folders and resources outside time range
                continue;
            }

            if (CmsDocumentFactory.isIgnoredDocument(currentResource.getRootPath(), true)) {
                // this resource is ignored, skip it before checking the resource id
                continue;
            }

            CmsUUID resId = currentResource.getResourceId();

            if (!addedResources.contains(resId)) {

                // add resource to return list and ID to set
                addedResources.add(resId);
                filteredResources.add(currentResource);
            }
        }

        // clear objects to release memory
        i = null;
        addedResources = null;
        resources = null;

        return filteredResources;
    }

    /**
     * Returns a String which holds the selected categories for the result page of the new documents query.<p>
     * 
     * @param cms the CmsObject
     * @param request the HttpServletRequest
     * @param messageAll the localized message String used when all categories were selected
     * @return String with comma separated selected categories or localized "all" message
     */
    public static String getCategories(CmsObject cms, HttpServletRequest request, String messageAll) {

        StringBuffer retValue = new StringBuffer(128);

        // get the current user's HHTP session
        //HttpSession session = ((HttpServletRequest)cms.getRequestContext().getRequest().getOriginalRequest()).getSession();
        HttpSession session = request.getSession();

        // get the required parameters
        String paramAll = (String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_ALL);

        if ("true".equals(paramAll)) {
            return messageAll;
        } else {
            List<String> categories = getCategoryList((String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_CATEGORYLIST));
            Iterator<String> i = categories.iterator();
            while (i.hasNext()) {
                String path = i.next();
                try {
                    retValue.append(cms.readPropertyObject(path, CmsCategory.CATEGORY_TITLE, false).getValue());
                } catch (CmsException e) {
                    // noop
                }
                if (i.hasNext()) {
                    retValue.append(", ");
                }
            }

            // clear objects to release memory
            categories = null;
            i = null;
        }
        return retValue.toString();
    }

    /**
     * Builds a list of the categories which were selected in the form.<p>
     * 
     * @param allCategories String with all selected category paths
     * @return List with all selected categories (holds String objects with absolute paths)
     */
    public static List<String> getCategoryList(String allCategories) {

        ArrayList<String> categories = new ArrayList<String>();

        // get the indiviadual category paths from the token
        StringTokenizer T = new StringTokenizer(allCategories, CategoryTree.C_LIST_SEPARATOR);
        while (T.hasMoreTokens()) {
            String curToken = T.nextToken();
            if (!"".equals(curToken.trim())) {
                // add the category to the list
                categories.add(curToken);
            }
        }
        return categories;
    }

    /**
     * Returns a list of new resources in the specified folder or category folder depending on the request parameters.<p>
     * 
     * @param cms the CmsObject to perform some operations
     * @param request the HttpServletRequest to get the needed request parameters
     * @return the list of new resources
     */
    public static List<CmsResource> getNewResources(CmsObject cms, HttpServletRequest request) {

        // get the current user's HHTP session
        HttpSession session = request.getSession();

        String startFolder = (String)request.getAttribute(CmsDocumentFrontend.ATTR_FULLPATH);

        // get the required parameters
        String paramAll = (String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_ALL);
        String paramStartDate = (String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_STARTDATE);
        String paramEndDate = (String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_ENDDATE);

        // parse the date Strings to long
        long startDate = Long.parseLong(paramStartDate);
        long endDate = Long.parseLong(paramEndDate);

        // create list of categories if selected
        List<String> selectedCategoryList = new ArrayList<String>();
        paramAll = (paramAll == null) ? "false" : paramAll;
        if (!"true".equals(paramAll)) {
            // search individual categories
            selectedCategoryList = getCategoryList((String)session.getAttribute(C_DOCUMENT_SEARCH_PARAM_CATEGORYLIST));
            if (selectedCategoryList.size() == 0) {
                return new ArrayList<CmsResource>(0);
            }
        }

        String openedCategories = CategoryTree.getTreeInfo(cms, CategoryTree.C_USER_INFO_OPENED_CATEGORIES, request);
        List<String> openedCategoryList = CategoryTree.commaStringToList(
            openedCategories,
            CategoryTree.C_LIST_SEPARATOR);

        return getNewResourceList(cms, startFolder, startDate, endDate, selectedCategoryList, openedCategoryList);
    }

    /**
     * Creates a nice localized date String from the given String.<p>
     * 
     * @param dateLongString the date as String representation of a long value
     * @param localeString the current locale String
     * @return nice formatted date string in long mode (e.g. 15. April 2003)
     */
    public static String getNiceDate(String dateLongString, String localeString) {

        Locale locale = new Locale(localeString.toLowerCase());
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
        Calendar cal = new GregorianCalendar(locale);
        try {
            cal.setTimeInMillis(Long.parseLong(dateLongString));
        } catch (Exception e) {
            //noop
        }
        return df.format(cal.getTime());
    }

    /**
     * Creates a nice localized date String from the given day, month and year Strings.<p>
     * 
     * @param day the number of the day as String
     * @param month the number of the month as String
     * @param year the number of the year as String
     * @param localeString the current locale String
     * @return nice formatted date string in long mode (e.g. 15. April 2003)
     */
    public static String getNiceDate(String day, String month, String year, String localeString) {

        Locale locale = new Locale(localeString.toLowerCase());
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
        Calendar cal = new GregorianCalendar(locale);
        try {
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        } catch (Exception e) {
            // noop
        }
        return df.format(cal.getTime());
    }

    /**
     * Creates a list of new resources of the specified folder and filters the unwanted resources.<p>
     * 
     * If the parameter categoryFolders is an empty list, all new resources are returned, otherwise
     * only those resources which are in a subfolder specified by the list.<p>
     * @param cms the CmsObject
     * @param startFolder the root folder
     * @param startDate the start date in milliseconds
     * @param endDate the end date in milliseconds
     * @param selectedCategories list with selected categories/folders
     * @param openedCategories list with opened categories/folders
     * @return list of new resources
     */
    private static List<CmsResource> getNewResourceList(
        CmsObject cms,
        String startFolder,
        long startDate,
        long endDate,
        List<String> selectedCategories,
        List<String> openedCategories) {

        List<CmsResource> searchResult = null;
        List<CmsResource> foundResources = null;
        Set<CmsUUID> addedResources = null;

        if (LOG.isDebugEnabled()) {

            StringBuffer buf = new StringBuffer();
            for (int i = 0, n = selectedCategories.size(); i < n; i++) {
                buf.append(selectedCategories.get(i));

                if (i < (n - 1)) {
                    buf.append(", ");
                }
            }

            LOG.debug("################ INPUT VALUES FOR NEW DOCUMENTS SEARCH");
            LOG.debug("startDate : " + startDate + " " + new java.util.Date(startDate).toString());
            LOG.debug("endDate : " + endDate + " " + new java.util.Date(endDate).toString());
            LOG.debug("startFolder : " + startFolder);
            LOG.debug("categories : " + buf.toString());
        }

        try {

            // get all resources in the site root which are in the time range
            CmsResourceFilter filter = CmsResourceFilter.IGNORE_EXPIRATION;
            filter = filter.addRequireLastModifiedAfter(startDate);
            filter = filter.addRequireLastModifiedBefore(endDate);
            foundResources = cms.readResources(startFolder, filter);
        } catch (CmsException e) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Error reading resources in time range "
                    + new java.util.Date(startDate).toString()
                    + " - "
                    + new java.util.Date(endDate).toString()
                    + " below folder "
                    + startFolder, e);
            }
            foundResources = Collections.emptyList();
        }

        if (selectedCategories.size() == 0) {

            // return all found resources with filtered links
            searchResult = filterLinkedResources(foundResources);
        } else {

            addedResources = new HashSet<CmsUUID>();
            searchResult = new ArrayList<CmsResource>();
            long currentTime = System.currentTimeMillis();

            for (int i = 0, n = foundResources.size(); i < n; i++) {

                // analyze each resource if it has to be included in the search result

                CmsResource resource = foundResources.get(i);

                // filter those documents that are folders or outside the release and expire window
                if (resource.isFolder()
                    || (resource.getDateReleased() > currentTime)
                    || (resource.getDateExpired() < currentTime)) {
                    // skip folders and resources outside time range
                    continue;
                }

                String resourceName = cms.getRequestContext().removeSiteRoot(resource.getRootPath());
                String parentFolder = CmsResource.getParentFolder(resourceName);
                boolean addToResult = false;

                if (!selectedCategories.contains(parentFolder) && openedCategories.contains(parentFolder)) {

                    // skip resources that are inside an opened, but un-selected category/folder
                    continue;
                }

                // check if the parent folder of the resource is one of the selected categories/folders
                addToResult = selectedCategories.contains(parentFolder);

                if (!addToResult) {

                    // check if the resource is inside a collapsed sub-tree 
                    // of a selected category

                    int openedCategoryCount = 0;

                    while (!"/".equals(parentFolder)) {

                        if (openedCategories.contains(parentFolder)) {
                            openedCategoryCount++;
                        }

                        if (selectedCategories.contains(parentFolder) && (openedCategoryCount == 0)) {

                            // we found a selected parent category, 
                            // and it's sub-tree is collapsed
                            addToResult = true;
                            break;
                        }

                        parentFolder = CmsResource.getParentFolder(parentFolder);
                    }
                }

                if (!addToResult) {

                    // continue with the next resource
                    continue;
                }

                if (CmsDocumentFactory.isIgnoredDocument(resourceName, true)) {
                    // this resource is ignored, skip it before checking the resource id
                    continue;
                }

                // check if the resource is a sibling that has already been added to the search result
                CmsUUID resourceId = resource.getResourceId();
                if (!addedResources.contains(resourceId)) {

                    // add resource to the result
                    addedResources.add(resourceId);
                    searchResult.add(resource);
                }
            }
        }

        return searchResult;
    }

}
