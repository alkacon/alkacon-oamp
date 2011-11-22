/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsCategory.java,v $
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
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.jsp.CmsJspNavBuilder;
import org.opencms.jsp.CmsJspNavElement;
import org.opencms.main.CmsException;
import org.opencms.util.CmsFileUtil;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A single category for the document management, with methods to get
 * a sorted list of main and sub category. Additionally, there are getter and setter
 * methods for the needed information to build a category overview.<p>
 *
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public class CmsCategory implements Comparable<CmsCategory> {

    /** Property value for sorting down first. */
    public static final String C_CATEGORY_SORT_ORDER_DOWN = "down";

    /** Property value for sorting right first. */
    public static final String C_CATEGORY_SORT_ORDER_RIGHT = "right";

    /** Separator in property value of sub categories. */
    public static final String CATEGORY_SEPARATOR = ".";

    /** Property name to look for the displayed title of the category. */
    public static final String CATEGORY_TITLE = CmsPropertyDefinition.PROPERTY_TITLE;

    /** Property name to look for the category languages. */
    public static final String PROPERTY_CATEGORY_LANGUAGES = "docs.catlanguage";

    /** Property value for all category languages. */
    public static final String VALUE_CATEGORY_LANGUAGES_ALL = "all";

    /** Absolute path to the category folder. */
    private String m_cmsResource;

    /** List with all main category elements. */
    private List<CmsCategory> m_mainCategories;

    /** Indicates if this is a main category. */
    private boolean m_mainCategory;

    /** Position in the category list (e.g "4" for a main, "4.2" for a sub category or the value of the NavPos property). */
    private String m_position;

    /** Map with all sub category elements with position of the main category as key. */
    private Map<String, List<CmsCategory>> m_subCategories;

    /** The displayed title of the category. */
    private String m_title;

    /**
     * Default constructor for CmsCategory.
     */
    public CmsCategory() {

        setTitle("");
        setPosition("");
        setCmsResource("");
    }

    /**
     * Constructor for initialization of the category lists.<p>
     * 
     * Use this constructor to get an initialized instance of CmsCategory 
     * to have access to the category lists.<p>
     * 
     * @param cms the CmsObject
     * @param folderUri the folder URI, all subelements are searched for the property
     * @param propertyName the name of the category property, usually "category"
     */
    public CmsCategory(CmsObject cms, String folderUri, String propertyName) {

        init(cms, folderUri, propertyName);
    }

    /**
     * Constructor with pre set values for all member variables.<p>
     * 
     * @param title the category title
     * @param position the category position
     * @param cmsResource the absolute path to the CmsResource
     * @param mainCategory <code>true</code> if this is a main category, otherwise <code>false</code>
     */
    public CmsCategory(String title, String position, String cmsResource, boolean mainCategory) {

        setTitle(title);
        setPosition(position);
        setCmsResource(cmsResource);
        setMainCategory(mainCategory);
    }

    /**
     * Compares this instance to another given object instance of this class to sort a set of categories.<p>
     * 
     * @param obj the other given object instance to compare with
     * @return integer value for sorting the objects
     */
    public int compareTo(CmsCategory obj) {

        if (obj == this) {
            return 0;
        }

        try {
            // get the float values of the positions
            return new Float(getPosition()).compareTo(new Float(obj.getPosition()));
        } catch (Exception e) {
            // ignore, return 0
        }
        return 0;
    }

    /**
     * Tests if a given object is equal to this instance.<p>
     * 
     * @param obj the other given object instance to compare with
     * @return true if the given object is equal to this instance
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof CmsCategory) {
            return getCmsResource().equals(((CmsCategory)obj).getCmsResource());
        }
        return false;
    }

    /**
     * Returns the CmsResource String of this category.<p>
     * 
     * @return the CmsResource String of this category
     */
    public String getCmsResource() {

        return m_cmsResource;
    }

    /**
     * Returns the sorted list with all main categories.<p>
     * 
     * @return the list with all main categories
     */
    public List<CmsCategory> getMainCategories() {

        return m_mainCategories;
    }

    /**
     * Returns the position of the category.<p>
     * 
     * @return the position of the category
     */
    public String getPosition() {

        return m_position;
    }

    /**
     * Returns the list with all sub categories.<p>
     * 
     * @return the list with all sub categories
     */
    public Map<String, List<CmsCategory>> getSubCategories() {

        return m_subCategories;
    }

    /**
     * Returns a sorted list of all sub categories of the specified main category.<p>
     * 
     * @param mainCategory the value of the property "category" of the main category
     * @return sorted list of all sub categories of the specified main category
     */
    public List<CmsCategory> getSubCategory(String mainCategory) {

        List<CmsCategory> subCat = getSubCategories().get(mainCategory);
        if (subCat == null) {
            return Collections.emptyList();
        }

        // set the size of the sub category list to an even value
        if ((subCat.size() % 2) == 1) {
            subCat.add(new CmsCategory());
        }

        return subCat;
    }

    /**
     * Returns a sorted list of all sub categories of the specified main category.<p>
     * 
     * @param mainCategory the value of the property "category" of the main category
     * @param sortOrder the sort order, this is either "down" or "right"
     * @return sorted list of all sub categories of the specified main category
     */
    public List<CmsCategory> getSubCategory(String mainCategory, String sortOrder) {

        List<CmsCategory> unsortedCategories = getSubCategory(mainCategory);
        // if the sort order is right first, we can use the precalculated result
        if (sortOrder.equals(C_CATEGORY_SORT_ORDER_RIGHT)) {
            return unsortedCategories;
        } else {
            // otherwise we must resort the list
            int size = unsortedCategories.size();
            int middle = size / 2;
            List<CmsCategory> sortedCategories = new ArrayList<CmsCategory>();
            for (int i = 0; i < middle; i++) {
                sortedCategories.add(unsortedCategories.get(i));
                if ((middle + i) <= size) {
                    sortedCategories.add(unsortedCategories.get(middle + i));
                }
            }
            return sortedCategories;
        }
    }

    /**
     * Returns the title of the category.<p>
     * 
     * @return the title of the category
     */
    public String getTitle() {

        return m_title;
    }

    /** 
     * Calculate the hash code of this object, based on its resource name String. 
     * 
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {

        return getCmsResource().hashCode();
    }

    /**
     * Initializes the lists of main and sub categories for the LGT category overview.<p>
     * 
     * @param cms the CmsObject
     * @param folderUri the folder URI, all sub elements are searched for the property
     * @param propertyName the name of the category property, usually "category"
     */
    public void init(CmsObject cms, String folderUri, String propertyName) {

        List<CmsResource> allCategories = new ArrayList<CmsResource>();
        List<CmsCategory> mainCategories = new ArrayList<CmsCategory>();
        Map<String, List<CmsCategory>> subCategories = new HashMap<String, List<CmsCategory>>();
        Locale locale = cms.getRequestContext().getLocale();
        String locTitle = CATEGORY_TITLE + "_" + locale.toString();

        // get all resources in the current folder with the property propertyName set
        try {
            allCategories = cms.readResourcesWithProperty(folderUri, propertyName, null, CmsResourceFilter.DEFAULT);
        } catch (CmsException e) {
            // should never happen
        }

        if (allCategories.isEmpty()) {
            // did not find resources with propertyName set, read categories using navigation properties
            CmsJspNavBuilder navBuilder = new CmsJspNavBuilder(cms);
            // first get all main categories (i.e. folders with navigation properties set)
            List<CmsJspNavElement> firstLevelNavigation = navBuilder.getNavigationForFolder(CmsFileUtil.addTrailingSeparator(folderUri));
            for (CmsJspNavElement firstLevelItem : firstLevelNavigation) {
                // accept only folders as categories
                if (firstLevelItem.isFolderLink()) {
                    // create category from navigation element
                    CmsCategory mCat = getCategoryFromNavElement(firstLevelItem, locale, locTitle, true);
                    if (mCat != null) {
                        mainCategories.add(mCat);
                        // determine sub categories of main category
                        List<CmsJspNavElement> secondLevelNavigation = navBuilder.getNavigationForFolder(mCat.getCmsResource());
                        List<CmsCategory> directSubCategories = new ArrayList<CmsCategory>(secondLevelNavigation.size());
                        for (CmsJspNavElement secondLevelItem : secondLevelNavigation) {
                            // accept only folders as sub categories
                            if (secondLevelItem.isFolderLink()) {
                                // create sub category from navigation element
                                CmsCategory sCat = getCategoryFromNavElement(secondLevelItem, locale, locTitle, false);
                                if (sCat != null) {
                                    directSubCategories.add(sCat);
                                }
                            }
                        }
                        // put sub categories to map
                        subCategories.put(mCat.getPosition(), directSubCategories);
                    }
                }
            }
        } else {

            Iterator<CmsResource> i = allCategories.iterator();
            while (i.hasNext()) {
                // get the resource and the absolute path
                CmsResource curRes = i.next();

                try {

                    // first check if this category is shown in the current frontend language
                    String catLanguage = "";
                    try {
                        catLanguage = cms.readPropertyObject(curRes, PROPERTY_CATEGORY_LANGUAGES, false).getValue();
                    } catch (CmsException e) {
                        // ignore, property might not be defined
                    }

                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(catLanguage)) {
                        if (!catLanguage.equals(VALUE_CATEGORY_LANGUAGES_ALL) && !catLanguage.equals(locale.toString())) {
                            // category is not shown in this frontend locale
                            continue;
                        }
                    }

                    // get the properties of the current resource
                    String positionString = cms.readPropertyObject(curRes, propertyName, false).getValue();
                    positionString = CategoryTree.cutPrefix(positionString);

                    String resourceName = cms.getSitePath(curRes);
                    String title = "";
                    try {
                        title = cms.readPropertyObject(curRes, locTitle, false).getValue();
                    } catch (CmsException e) {
                        // ignore, property might not be defined
                    }
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                        title = cms.readPropertyObject(curRes, CATEGORY_TITLE, false).getValue();
                    }
                    // check the presence of the title property
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                        title = CmsResource.getName(resourceName);
                        // cut the trailing "/" if present
                        if (CmsResource.isFolder(title)) {
                            title = title.substring(0, (title.length() - 1));
                        }
                    }

                    int separator = positionString.indexOf(CATEGORY_SEPARATOR);
                    if (separator == -1) {
                        // add resources which are a "main" category to the main list
                        mainCategories.add(new CmsCategory(title, positionString, resourceName, true));
                    } else {
                        // add resources which are a "sub" category to the correct sub list

                        // get the main category subString from the position value
                        String mainCatPosition = positionString.substring(0, separator);
                        List<CmsCategory> subCat = subCategories.get(mainCatPosition);
                        if (subCat == null) {
                            subCat = new ArrayList<CmsCategory>();
                        }
                        // determine sub category position
                        String subPosition = positionString.substring(separator + 1);
                        subCat.add(new CmsCategory(title, subPosition, resourceName, false));
                        Collections.sort(subCat);
                        // put list with added category back to map
                        subCategories.put(mainCatPosition, subCat);
                    }
                } catch (CmsException e) {
                    // do nothing, current resource is not added to the category list
                }
            }

            // sort the list of main categories by the value of the property
            if (mainCategories.size() >= 2) {
                Collections.sort(mainCategories);
            }
        }

        // set the size of the main category list to an even value
        if ((mainCategories.size() % 2) == 1) {
            mainCategories.add(new CmsCategory());
        }

        // set the member variables
        setMainCategories(mainCategories);
        setSubCategories(subCategories);
    }

    /**
     * Returns if this is a main category.<p>
     *
     * @return <code>true</code> if this is a main category, otherwise <code>false</code>
     */
    public boolean isMainCategory() {

        return m_mainCategory;
    }

    /**
     * Sets the CmsResource String of this category.<p>
     * 
     * @param cmsResource the CmsResource String of this category
     */
    public void setCmsResource(String cmsResource) {

        m_cmsResource = cmsResource;
    }

    /**
     * Sets the list with all main categories.<p>
     * 
     * @param list the list with all main categories
     */
    public void setMainCategories(List<CmsCategory> list) {

        m_mainCategories = list;
    }

    /**
     * Sets if this is a main category.<p>
     *
     * @param mainCategory <code>true</code> if this is a main category, otherwise <code>false</code>
     */
    public void setMainCategory(boolean mainCategory) {

        m_mainCategory = mainCategory;
    }

    /**
     * Sets the position of the category.<p>
     * 
     * @param string with the position of the category
     */
    public void setPosition(String string) {

        m_position = string;
    }

    /**
     * Sets the map with all sub categories.<p>
     * 
     * @param map the map with all sub categories
     */
    public void setSubCategories(Map<String, List<CmsCategory>> map) {

        m_subCategories = map;
    }

    /**
     * Sets the title of the category.<p>
     * 
     * @param string the title of the category
     */
    public void setTitle(String string) {

        m_title = string;
    }

    /**
     * Returns an initialized category created from the given navigation element.<p>
     * 
     * @param nav the navigation element to get the category from
     * @param locale the current locale
     * @param locTitle the property name to get the localized title
     * @param mainCategory <code>true</code> if this is a main category, otherwise <code>false</code>
     * 
     * @return an initialized category or <code>null</code> if no category could be created for the current locale
     */
    protected CmsCategory getCategoryFromNavElement(
        CmsJspNavElement nav,
        Locale locale,
        String locTitle,
        boolean mainCategory) {

        // first check if this category is shown in the current frontend language
        String catLanguage = nav.getProperty(PROPERTY_CATEGORY_LANGUAGES);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(catLanguage)) {
            if (!catLanguage.equals(VALUE_CATEGORY_LANGUAGES_ALL) && !catLanguage.equals(locale.toString())) {
                // category is not shown in this frontend locale
                return null;
            }
        }

        // determine the category title
        String title = nav.getProperty(locTitle);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            // fall back 1: the Title 
            title = nav.getProperty(CATEGORY_TITLE);
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            // fall back 2: the navigation text
            title = nav.getNavText();
        }
        // check if anything was found
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            // fall back 3: the resource name
            title = nav.getFileName();
            // cut the trailing "/" if present
            if (CmsResource.isFolder(title)) {
                title = CmsFileUtil.removeTrailingSeparator(title);
            }
        }

        return new CmsCategory(title, String.valueOf(nav.getNavPosition()), nav.getResourceName(), mainCategory);
    }

}
