/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsCategory.java,v $
 * Date   : $Date: 2009/06/19 21:22:16 $
 * Version: $Revision: 1.2 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2009 Alkacon Software GmbH (http://www.alkacon.com)
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
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A single category for the document management, with methods to get
 * a sorted list of main and sub category. Additionally, there are getter and setter
 * methods for the needed information to build a category overview.<p>
 *
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.0 
 */
public class CmsCategory extends Object implements Comparable {

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
    private List m_mainCategories;

    /** Position in the category list (e.g "4" for a main, "4.2" for a sub category). */
    private String m_position;

    /** List with all sub category elements. */
    private List m_subCategories;

    /** The displayed title of the category. */
    private String m_title;

    /**
     * Default constructor for CmsCategory.
     */
    public CmsCategory() {

        super();
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

        super();
        init(cms, folderUri, propertyName);
    }

    /**
     * Constructor with pre set values for all member variables.<p>
     * 
     * @param title the category title
     * @param position the category position
     * @param cmsResource the absolute path to the CmsResource 
     */
    public CmsCategory(String title, String position, String cmsResource) {

        super();
        setTitle(title);
        setPosition(position);
        setCmsResource(cmsResource);
    }

    /**

     * Compares this instance to another given object instance of this class to sort a set of categories.<p>

     * 

     * @param obj the other given object instance to compare with

     * @return integer value for sorting the objects

     */

    public int compareTo(Object obj) {

        if (obj == this) {
            return 0;
        }

        if (obj instanceof CmsCategory) {
            try {
                // get the int values of the positions
                return new Integer(getPosition()).compareTo(new Integer(((CmsCategory)obj).getPosition()));
            } catch (Exception e) {
                // ignore, return 0
            }
        }
        return 0;
    }

    /**
     * Tests if a given object is equal to this instance.<p>
     * 
     * @param obj the other given object instance to compare with
     * @return true if the given object is equal to this instance
     */
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
     * Returns the depth of this category in the category tree.<p>
     * 
     * @return the depth of this category in the category tree
     */
    public int getDepth() {

        if (m_position == null) {
            return -1;
        }

        int depth = 0;
        char sep = CATEGORY_SEPARATOR.charAt(0);
        char[] position = m_position.toCharArray();

        for (int i = 0, n = m_position.length(); i < n; i++) {
            if (position[i] == sep) {
                depth++;
            }
        }

        return depth;
    }

    /**
     * Returns the sorted list with all main categories.<p>
     * 
     * @return the list with all main categories
     */
    public List getMainCategories() {

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
    public List getSubCategories() {

        return m_subCategories;
    }

    /**
     * Returns a sorted list of all sub categories of the specified main category.<p>
     * 
     * @param mainCategory the value of the property "category" of the main category
     * @return sorted list of all sub categories of the specified main category
     */
    public List getSubCategory(String mainCategory) {

        List subCat = new ArrayList();

        Iterator i = getSubCategories().iterator();
        while (i.hasNext()) {
            CmsCategory curCat = (CmsCategory)i.next();
            int separator = curCat.getPosition().indexOf(CATEGORY_SEPARATOR);
            // get the main category subString from the position value
            String mainCatPosition = curCat.getPosition().substring(0, separator);
            if (mainCatPosition.equals(mainCategory)) {
                // only add current element to list if it belongs to the right main category
                String subPosition = curCat.getPosition().substring(separator + 1);
                subCat.add(new CmsCategory(curCat.getTitle(), subPosition, curCat.getCmsResource()));
            }
        }

        // sort the list of sub categories by the value of the property
        if (subCat.size() >= 2) {
            Collections.sort(subCat);
        }

        // set the size of the sub category list to an even value
        if ((subCat.size() % 2) == 1) {
            subCat.add(new CmsCategory());
        }

        // clear objects to release memory
        i = null;

        return subCat;
    }

    /**
     * Returns a sorted list of all sub categories of the specified main category.<p>
     * 
     * @param mainCategory the value of the property "category" of the main category
     * @param sortOrder the sort order, this is either "down" or "right"
     * @return sorted list of all sub categories of the specified main category
     */
    public List getSubCategory(String mainCategory, String sortOrder) {

        List unsortedCategories = getSubCategory(mainCategory);
        // if the sort order is right first, we can use the precalculated result
        if (sortOrder.equals(C_CATEGORY_SORT_ORDER_RIGHT)) {
            return unsortedCategories;
        } else {
            // otherwise we must resort the list
            int size = unsortedCategories.size();
            int middle = size / 2;
            List sortedCategories = new ArrayList();
            for (int i = 0; i < middle; i++) {
                sortedCategories.add(unsortedCategories.get(i));
                if (middle + i <= size) {
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
     * Calculate the hashcode of this object, based on its resource name String. 
     * 
     * @return the hashcode of the object
     */
    public int hashCode() {

        return getCmsResource().hashCode();
    }

    /**
     * Initializes the lists of main and sub categories for the LGT category overview.<p>
     * 
     * @param cms the CmsObject
     * @param folderUri the folder URI, all subelements are searched for the property
     * @param propertyName the name of the category property, usually "category"
     */
    public void init(CmsObject cms, String folderUri, String propertyName) {

        List allCategories = new ArrayList();
        List mainCat = new ArrayList();
        List subCat = new ArrayList();

        // get all resources in the current folder with the property propertyName set
        try {
            allCategories = cms.readResourcesWithProperty(folderUri, propertyName);
        } catch (CmsException e) {
            // should never happen
        }

        Locale locale = cms.getRequestContext().getLocale();
        String locTitle = CATEGORY_TITLE + "_" + locale.toString();

        Iterator i = allCategories.iterator();
        while (i.hasNext()) {
            // get the resource and the absolute path
            CmsResource curRes = (CmsResource)i.next();

            try {

                // filter deleted categories
                if (curRes.getState() == CmsResource.STATE_DELETED) {
                    continue;
                }

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

                if (positionString.indexOf(CATEGORY_SEPARATOR) == -1) {
                    // add resources which are a "main" category to the main list
                    mainCat.add(new CmsCategory(title, positionString, resourceName));
                } else {
                    // add resources which are a "sub" category to the sub list
                    subCat.add(new CmsCategory(title, positionString, resourceName));
                }

            } catch (CmsException e) {
                // do nothing, current resource is not added to the category list
            }
        }

        // sort the list of main categories by the value of the property
        if (mainCat.size() >= 2) {
            Collections.sort(mainCat);
        }

        // set the size of the main category list to an even value
        if ((mainCat.size() % 2) == 1) {
            mainCat.add(new CmsCategory());
        }

        // set the member variables
        setMainCategories(mainCat);
        setSubCategories(subCat);

        // clear objects to release memory
        mainCat = null;
        subCat = null;
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
    public void setMainCategories(List list) {

        m_mainCategories = list;
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
     * Sets the list with all sub categories.<p>
     * 
     * @param list the list with all sub categories
     */
    public void setSubCategories(List list) {

        m_subCategories = list;
    }

    /**
     * Sets the title of the category.<p>
     * 
     * @param string the title of the category
     */
    public void setTitle(String string) {

        m_title = string;
    }

}
