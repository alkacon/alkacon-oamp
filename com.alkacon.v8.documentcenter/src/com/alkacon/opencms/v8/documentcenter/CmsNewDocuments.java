/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsNewDocuments.java,v $
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
import org.opencms.util.CmsUUID;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class which provides static methods for the new documents functions of the LGT intranet.<p>
 *
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public final class CmsNewDocuments {

    /**
     * Hide public constructor.<p> 
     */
    private CmsNewDocuments() {

        // empty    
    }

    /**
     * Creates an HTML list with checkboxes for all given categories.<p>
     * 
     * @param categories list with the categories (CmsCategory)
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
        Iterator<CmsResource> i = resources.iterator();
        while (i.hasNext()) {
            CmsResource currentResource = i.next();
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

        // get requet parameter for all categories
        String paramAll = request.getParameter("all");

        if ("true".equals(paramAll)) {
            return messageAll;
        } else {
            List<String> categories = getCategoryList(request.getParameter("categorylist"));
            Iterator<String> i = categories.iterator();
            while (i.hasNext()) {
                String path = i.next();
                try {
                    retValue.append(cms.readPropertyObject(path, CmsCategory.CATEGORY_TITLE, false));
                } catch (CmsException e) {
                    // should never happen
                }
                if (i.hasNext()) {
                    retValue.append(", ");
                }
            }
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
        StringTokenizer T = new StringTokenizer(allCategories, "*");
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

        // get the needed request parameters
        String paramSite = request.getParameter("site");
        String paramAll = request.getParameter("all");
        String paramStartDate = request.getParameter("startdate");
        String paramEndDate = request.getParameter("enddate");

        // parse the date Strings to long
        long startDate = Long.parseLong(paramStartDate);
        long endDate = Long.parseLong(paramEndDate);

        // create list of categories if selected
        List<String> categories = new ArrayList<String>();
        if (!"true".equals(paramAll)) {
            // search individual categories
            categories = getCategoryList(request.getParameter("categorylist"));
            if (categories.size() == 0) {
                return new ArrayList<CmsResource>(0);
            }
        }
        return getNewResourceList(cms, paramSite, startDate, endDate, categories);
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
        cal.setTimeInMillis(Long.parseLong(dateLongString));
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
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        return df.format(cal.getTime());
    }

    /**
     * Creates a list of new resources of the specified folder and filters the unwanted resources.<p>
     * 
     * If the parameter categoryFolders is an empty list, all new resources are returned, otherwise
     * only those resources which are in a subfolder specified by the list.<p>
     * @param cms the CmsObject
     * @param site the site root folder
     * @param startDate the start date in milliseconds
     * @param endDate the end date in milliseconds
     * @param categoryFolders list with subfolders
     * @return list of new resources
     */
    private static List<CmsResource> getNewResourceList(
        CmsObject cms,
        String site,
        long startDate,
        long endDate,
        List<String> categoryFolders) {

        List<CmsResource> allResources = new ArrayList<CmsResource>();

        // get all resources in the site root which are in the time range
        try {
            CmsResourceFilter filter = CmsResourceFilter.IGNORE_EXPIRATION.addRequireLastModifiedAfter(startDate).addRequireLastModifiedBefore(
                endDate);
            allResources = cms.readResources(site, filter);
        } catch (CmsException e) {
            // should never happen
        }

        if (categoryFolders.size() == 0) {
            // return all found resources with filtered links
            return filterLinkedResources(allResources);
        } else {
            // check every resource if it is in a subfolder of the list categoryFolders
            Set<CmsUUID> addedResources = new HashSet<CmsUUID>();
            List<CmsResource> newResources = new ArrayList<CmsResource>();
            Iterator<CmsResource> i = allResources.iterator();
            while (i.hasNext()) {
                CmsResource curResource = i.next();
                boolean add = false;
                String sitePath = cms.getSitePath(curResource);
                if (CmsDocumentFactory.isIgnoredDocument(sitePath, true)) {
                    // this resource is ignored, skip it before checking the resource id
                    continue;
                }
                Iterator<String> k = categoryFolders.iterator();
                while (k.hasNext()) {
                    // check if the path of the resource starts with the path of the categories in the list
                    if (sitePath.startsWith(k.next())) {
                        CmsUUID resId = curResource.getResourceId();
                        // check if the resource is a link already added to the result
                        if (!addedResources.contains(resId)) {
                            // add resource to return list and ID to set
                            addedResources.add(resId);
                            add = true;
                        }
                        break;
                    }
                }

                if (add) {
                    newResources.add(curResource);
                }
            }

            return newResources;
        }

    }

}
