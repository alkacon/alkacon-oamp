/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CategoryTree.java,v $
 * Date   : $Date: 2010/03/19 15:31:14 $
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

import org.opencms.file.CmsFolder;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspNavBuilder;
import org.opencms.jsp.CmsJspNavElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsFileUtil;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;

/**
 * A Java bean to do a query for VFS documents.<p>
 * 
 * The category tree is internally stored by a map of adjacency lists
 * containing category objects keyed by their parent categories/folders.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public class CategoryTree {

    /** The form action to trigger a search for new documents. */
    public static final String C_ACTION_SEARCH = "search";

    /** The form action to toggle the category tree. */
    public static final String C_ACTION_TOGGLE_TREE = "toggleTree";

    /** The default max. tree depth of the category tree. */
    public static final int C_DEFAULT_MAX_TREE_DEPTH = 2;

    /** The sep.-char. to build lists. */
    public static final String C_LIST_SEPARATOR = ",";

    /** The category toggle +/- in the tree. */
    public static final String C_PARAM_TOGGLE_CATEGORY = "toggleCategory";

    /** The mode +/- how to tree was toggled. */
    public static final String C_PARAM_TOGGLE_MODE = "toggleMode";

    /** The category property. */
    public static final String C_PROP_CATEGORY = "category";

    /** The property key to read the max. folder/category depth of the tree. */
    public static final String C_PROPERTY_MAX_TREE_DEPTH = "categoryTreeMaxDepth";

    /** The key to get the opened categories from the user info hash. */
    public static final String C_USER_INFO_OPENED_CATEGORIES = "opened_categories";

    /** The key to get the selected categories from the user info hash. */
    public static final String C_USER_INFO_SELECTED_CATEGORIES = "selected_categories";

    /** A separator for property prefixes. */
    private static final String C_PREFIX_SEPARATOR = ":";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CategoryTree.class);

    /** The action submitted from the form what to do next. */
    private String m_action;

    /** A boolean flag that saves if the user checked the "select all categories" checkbox in the form. */
    private boolean m_allSelected;

    /** The current user's Cms object. */
    private CmsObject m_cms;

    /** The max. folder/category depth of the tree. */
    private int m_maxTreeDepth;

    /** The folder names of the categories opened in the tree. */
    private List<String> m_openedCategoryList;

    /** The name of the property definition for the localized title, e.g. "Title_en". */
    private String m_propertyTitle;

    /** The request used to initialize the Category Tree. */
    private HttpServletRequest m_request;

    /** The root folder from where the tree is built. */
    private String m_rootFolder;

    /** The folder names of the categories selected in the tree. */
    private List<String> m_selectedCategoryList;

    /** The map to stroe the category tree. */
    private Map<String, List<CmsCategory>> m_treeMap;

    /** Indicates if navigation properties should be used to create the category tree. */
    private boolean m_useNavigation;

    /**
     * Creates a new category tree.<p>
     * @param cms the current user's Cms object
     * @param request the Http request
     * @param rootFolder the root folder from where the category tree is built (usually "/")
     * @param maxTreeDepth the max. category/folder depth of the tree
     */
    public CategoryTree(CmsObject cms, HttpServletRequest request, String rootFolder, String maxTreeDepth) {

        String value = null;
        String mode = null;
        String category = null;

        m_cms = cms;
        m_rootFolder = (rootFolder != null) ? CmsFileUtil.addTrailingSeparator(rootFolder) : rootFolder;

        if ((m_rootFolder != null) && (m_rootFolder.length() > 1)) {
            // check if navigation properties should be used for category tree
            List<CmsResource> allCategories = new ArrayList<CmsResource>();
            try {
                allCategories = cms.readResourcesWithProperty(
                    m_rootFolder,
                    C_PROP_CATEGORY,
                    null,
                    CmsResourceFilter.DEFAULT);
            } catch (CmsException e) {
                // should never happen
            }
            m_useNavigation = allCategories.size() < 1;
        }

        m_request = request;
        m_propertyTitle = CmsPropertyDefinition.PROPERTY_TITLE + "_" + cms.getRequestContext().getLocale().toString();

        if (maxTreeDepth != null) {
            try {
                m_maxTreeDepth = Integer.parseInt(maxTreeDepth);
            } catch (Exception e) {

                if (LOG.isErrorEnabled()) {
                    LOG.error(
                        "Error: maxTreeDepth is not a string representing an integer! Using default max. tree depth "
                            + C_DEFAULT_MAX_TREE_DEPTH,
                        e);
                }
                m_maxTreeDepth = C_DEFAULT_MAX_TREE_DEPTH;
            }
        } else {

            m_maxTreeDepth = C_DEFAULT_MAX_TREE_DEPTH;

            if (LOG.isErrorEnabled()) {
                LOG.error("Error: maxTreeDepth is null! Using default max. tree depth " + C_DEFAULT_MAX_TREE_DEPTH);
            }
        }

        // save all required request params.

        value = request.getParameter("action");
        if (value != null) {
            m_action = value;
        }

        value = request.getParameter("openedCategories");
        if (value != null) {
            m_openedCategoryList = commaStringToList(value, C_LIST_SEPARATOR);
        } else {
            m_openedCategoryList = new ArrayList<String>();
        }

        value = request.getParameter("categorylist");
        if (value != null) {
            m_selectedCategoryList = commaStringToList(value, C_LIST_SEPARATOR);
        } else {
            m_selectedCategoryList = new ArrayList<String>();
        }

        value = request.getParameter("all");
        if (value != null) {
            m_allSelected = true;
        } else {
            m_allSelected = false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("################ INPUT VALUES FOR CATEGORY TREE");
            LOG.debug("action : " + m_action);
            LOG.debug("opened categories : " + m_openedCategoryList.toString());
            LOG.debug("selected categories : " + m_selectedCategoryList.toString());
            LOG.debug("root folder : " + m_rootFolder);
            LOG.debug("max. tree depth : " + m_maxTreeDepth);
        }

        // initialize everything by switching the submitted form action

        if (m_action != null) {

            if (C_ACTION_TOGGLE_TREE.equalsIgnoreCase(m_action)) {

                // the user toggled the tree...

                mode = request.getParameter(C_PARAM_TOGGLE_MODE);
                category = request.getParameter(C_PARAM_TOGGLE_CATEGORY);

                if ((mode != null) && (category != null)) {

                    if ("+".equalsIgnoreCase(mode)) {

                        // the user opened a sub-tree
                        openCategory(category);
                        saveTreeInfo();
                    } else if ("-".equalsIgnoreCase(mode)) {

                        // the user closed a sub-tree
                        closeCategory(category);
                        saveTreeInfo();
                    } else if (LOG.isErrorEnabled()) {

                        LOG.debug("Unknown toggle mode " + mode + " submitted!");
                    }
                }
            } else if ("redirect_a".equalsIgnoreCase(m_action)
                || C_ACTION_SEARCH.equalsIgnoreCase(m_action)
                || ("searchText".equalsIgnoreCase(m_action))) {

                // the user triggered a search for new documents...
                saveTreeInfo();
            }
        }
    }

    /**
     * Turns comma-separated string into a List of strings.<p>
     * 
     * @param str the string to be split
     * @param sep a separator character
     * @return a List of tokens
     */
    public static List<String> commaStringToList(String str, String sep) {

        List<String> result = null;
        StringTokenizer tokenizer = null;

        if (str != null) {

            tokenizer = new StringTokenizer(str, sep);
            result = new ArrayList<String>(tokenizer.countTokens());

            while (tokenizer.hasMoreTokens()) {
                result.add(tokenizer.nextToken());
            }

            Collections.sort(result);
        } else {

            result = new ArrayList<String>();
        }

        return result;
    }

    /**
     * Cuts off a prefix separated by <code>:</code> from the "real" property value.<p>
     * 
     * @param value a property value
     * @return the property value without the prefix
     */
    public static String cutPrefix(String value) {

        if (value == null) {
            return value;
        }
        int index = value.indexOf(C_PREFIX_SEPARATOR);
        if (index != -1) {
            return value.substring(index + 1);
        }

        return value;
    }

    /**
     * Returns the tree info for the specified key from the current user's info hash.<p>
     * 
     * @param cms the CmsObject
     * @param key the key
     * @param request the current request
     * 
     * @return the tree info for the specified key
     * @see #C_USER_INFO_OPENED_CATEGORIES
     * @see #C_USER_INFO_SELECTED_CATEGORIES
     */
    public static String getTreeInfo(CmsObject cms, String key, HttpServletRequest request) {

        key = cms.getRequestContext().addSiteRoot(cms.getRequestContext().getUri()) + "_" + key;

        // try to read the tree settings fomr the request
        String value;
        value = (String)request.getSession(true).getAttribute(key);

        // if nothing was found in the session, try to read some infoes from the user
        if (value == null) {
            value = (String)cms.getRequestContext().getCurrentUser().getAdditionalInfo(key);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving additional info for user "
                + cms.getRequestContext().getCurrentUser().getName()
                + " with key: "
                + key
                + ", value: "
                + value);
        }

        return value;
    }

    /**
     * Turns a List of strings into a comma-separated string.<p>
     * 
     * @param list the list
     * @param sep the separator
     * @return a comma-separated string
     */
    public static String listToCommaString(List<String> list, String sep) {

        StringBuffer buf = new StringBuffer();

        for (int i = 0, n = list.size(); i < n; i++) {

            buf.append(list.get(i));
            if (i < (n - 1)) {
                buf.append(sep);
            }
        }

        return buf.toString();
    }

    /**
     * Builds the HTML of the category tree.<p>
     * @param categoryTree a List with the categories currently visible in the tree
     * @param optCheckboxArgs optional arguments to be included in the checkbox tags, or null
     * @param optAnchorArgs optional arguments to be included in the anchor tags to toggle the tree, or null
     * @param indent a HTML fragment (e.g. a span tag) to indent sub-elements in the tree
     * @return the HTML of the category tree
     */
    public String buildCategoryTree(
        List<CmsCategory> categoryTree,
        String optCheckboxArgs,
        String optAnchorArgs,
        String indent) {

        StringBuffer buf = new StringBuffer();
        CmsCategory category = null;
        String resourceName = null;
        String toggleMode = null;
        int counter = 0;
        int pathLevel = 0;
        int pathLevelRootFolder = CmsResource.getPathLevel(m_rootFolder) + 1;

        // build the HTML of the category tree
        for (int i = 0, n = categoryTree.size(); i < n; i++) {

            category = categoryTree.get(i);

            // check if this is a main category
            boolean mainCat = category.isMainCategory();

            resourceName = category.getCmsResource();

            if ((resourceName != null) && !"".equalsIgnoreCase(resourceName)) {

                toggleMode = m_openedCategoryList.contains(category.getCmsResource()) ? "-" : "+";

                pathLevel = CmsResource.getPathLevel(category.getCmsResource()) - pathLevelRootFolder;
                for (int j = 0; j < pathLevel; j++) {
                    buf.append(indent);
                }

                buf.append("<input type=\"checkbox\" name=\"cat").append(counter).append("\"");
                buf.append(" id=\"cat").append(counter).append("\"");
                buf.append(" value=\"").append(resourceName).append("\"");

                if (m_selectedCategoryList.contains(resourceName)) {
                    buf.append(" checked=\"checked\"");
                }

                if (optCheckboxArgs != null) {
                    buf.append(" ").append(optCheckboxArgs);
                }

                buf.append("/>&nbsp;");
                buf.append("<a");

                if (optAnchorArgs != null) {
                    buf.append(" ").append(optAnchorArgs);
                }

                buf.append(
                    " href=\"#\" class=\"btn btn-info btn-small\" onclick=\"toggleTree("
                        + categoryTree.size()
                        + ",'"
                        + resourceName
                        + "','").append(toggleMode).append("')\">");
                buf.append("[").append(toggleMode).append("]</a>");
                buf.append("&nbsp;");
                if (mainCat) {
                    buf.append("<span class=\"treetopcategory\">");
                }
                buf.append("<label for=\"").append("cat").append(counter).append("\">");
                buf.append(category.getTitle());
                buf.append("</label>");
                if (mainCat) {
                    buf.append("</span>");
                }
                buf.append("<br/>\n");
                counter++;
            }

        }

        return buf.toString();
    }

    /**
     * Returns a list of all categories sorted ascending by their position in the 
     * category tree.<p>
     * 
     * The tree contains the main-categories, plus the sub-categories of all opened 
     * categories.<p>
     * 
     * @return a list of all categories
     */
    public List<CmsCategory> getCategoryTree() {

        List<CmsCategory> result = null;
        CmsCategory category = null;
        String parentFolder = null;
        List<CmsCategory> treeList = null;
        boolean addToResult = false;

        try {
            // create a new tree map
            m_treeMap = Collections.synchronizedMap(new HashMap<String, List<CmsCategory>>());

            // add all selected categories to the tree        
            addSelectedCategories();

            // add all opened categories to the tree
            addOpenedCategories();

            // add all folders having the "category" property set to tree
            addCategoryFolders();

            // turn the tree map into a List in DFS order
            treeList = toList(new CmsCategory("", "", m_rootFolder, true));
            if (treeList == null) {
                // the tree is empty...
                return Collections.emptyList();
            } else {
                // tree[0] is the root folder and can be skipped from the result tree
                if (treeList.size() > 1) {
                    treeList = treeList.subList(1, treeList.size());
                }
            }

            // build the final result tree. this is to include only sub-trees
            // in the result that either have a parent folder being opened, 
            // or a "sibling" node in the tree that is selected
            result = new ArrayList<CmsCategory>();
            for (int i = 0, n = treeList.size(); i < n; i++) {

                category = treeList.get(i);
                parentFolder = CmsResource.getParentFolder(category.getCmsResource());

                // add the category to the result if it is a main category
                addToResult = category.isMainCategory();
                // or if it is a sub-folder/category of an opened category
                addToResult |= m_openedCategoryList.contains(parentFolder);
                // and if it's folder depth is below the max. folder depth
                addToResult &= CmsResource.getPathLevel(category.getCmsResource()) <= m_maxTreeDepth;

                if (addToResult) {
                    result.add(category);
                }
            }
        } catch (Exception e) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Error building category tree", e);
            }
        }

        return result;
    }

    /**
     * Returns the comma-separated string of opened categories.<p>
     * 
     * @return the comma-separated string of opened categories
     */
    public String getOpenedCategories() {

        if ((m_openedCategoryList == null) || (m_openedCategoryList.size() == 0)) {
            return "";
        }

        return listToCommaString(m_openedCategoryList, C_LIST_SEPARATOR);
    }

    /**
     * Returns "true" if all categories should be pre-selected in the form.<p>
     * 
     * This string can be used to trigger a JavaScript in the form.<p>
     * 
     * @return "true" if all categories should be pre-selected in the form
     */
    public String getPreSelectAllCategories() {

        // all categories should be pre-selected either if the user checked the
        // "select all categories" checkbox", or if the user openes the form for
        // the first time "action=null" and he doesn't have a tree of selected
        // categories saved in his user info hash.

        if (m_allSelected
            || (((m_action == null) || "".equalsIgnoreCase(m_action)) && (m_selectedCategoryList.size() == 0))) {
            return "true";
        }

        return "false";
    }

    /**
     * Returns the root folder from where the tree is built.<p>
     * 
     * @return the root folder from where the tree is built
     */
    public String getRootFolder() {

        return m_rootFolder;
    }

    /**
     * Returns the comma-separated string of selected categories.<p>
     * 
     * @return the comma-separated string of selected categories
     */
    public String getSelectedCategories() {

        if ((m_selectedCategoryList == null) || (m_selectedCategoryList.size() == 0)) {
            return "";
        }

        return listToCommaString(m_selectedCategoryList, C_LIST_SEPARATOR);
    }

    /**
     * Adds a category to the tree if it is not already contained in the tree.<p>
     * 
     * @param parent the parent folder of the category
     * @param category the category
     */
    protected void addCategory(String parent, CmsCategory category) {

        // get the child list of the folder specified by the parent ID
        List<CmsCategory> children = m_treeMap.get(parent);

        if (children == null) {

            // the child is obviously the first child of it's parent folder
            children = new ArrayList<CmsCategory>();

            // add the new child list as a sub-tree
            m_treeMap.put(parent, children);
        }

        if (!children.contains(category)) {
            // add the child to the child list
            children.add(category);
        }
    }

    /**
     * Adds all folders having the "category" property set to the tree .<p>
     */
    protected void addCategoryFolders() {

        List<CmsResource> resources = null;
        CmsResource resource = null;
        String position = null;
        String title = null;
        String resourceName = null;
        CmsCategory category = null;
        String parentFolder = null;

        if (m_useNavigation) {
            CmsJspNavBuilder navBuilder = new CmsJspNavBuilder(m_cms);
            CmsJspNavElement rootElement = navBuilder.getNavigationForResource(m_rootFolder);
            List<CmsJspNavElement> navElements = navBuilder.getSiteNavigation(
                m_rootFolder,
                rootElement.getNavTreeLevel() + m_maxTreeDepth);
            resources = new ArrayList<CmsResource>(navElements.size());
            for (CmsJspNavElement navItem : navElements) {
                if (navItem.isFolderLink()) {
                    resources.add(navItem.getResource());
                }
            }
        } else {
            try {

                // read all resources having the "category" property set from current root folder
                resources = m_cms.readResourcesWithProperty(m_rootFolder, C_PROP_CATEGORY);
            } catch (CmsException e) {

                if (LOG.isErrorEnabled()) {
                    LOG.error("Error reading resources with property " + C_PROP_CATEGORY, e);
                }

                resources = Collections.emptyList();
            }
        }

        // turn the resources into a map of Category objects 
        // keyed by their parent folder in the category tree        
        for (int i = 0, n = resources.size(); i < n; i++) {
            resource = resources.get(i);

            try {

                resourceName = m_cms.getRequestContext().removeSiteRoot(resource.getRootPath());
                if (m_useNavigation) {
                    position = m_cms.readPropertyObject(resourceName, CmsPropertyDefinition.PROPERTY_NAVPOS, false).getValue();
                } else {
                    position = m_cms.readPropertyObject(resourceName, C_PROP_CATEGORY, false).getValue();
                }
                if ((position != null) && !"".equalsIgnoreCase(position)) {
                    position = CategoryTree.cutPrefix(position);
                    title = readTitle(resourceName);
                    parentFolder = CmsResource.getParentFolder(resourceName);
                    category = new CmsCategory(title, position, resourceName, m_rootFolder.equals(parentFolder));

                    if (parentFolder.startsWith(m_rootFolder)) {

                        // add only folders starting with the root folder
                        // because we want a tree beginning from the root folder                    
                        addCategory(parentFolder, category);
                    }
                }
            } catch (CmsException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error reading properties of resource " + resource.getRootPath(), e);
                } else if (LOG.isErrorEnabled()) {
                    LOG.error("Error reading properties of resource " + resource.getRootPath());
                }
            }
        }
    }

    /**
     * Adds all currently opened categories and their sub-categories/folders to the tree.<p>
     */
    protected void addOpenedCategories() {

        String folder = null;
        String subFolder = null;
        String parentFolder = null;
        String position = null;
        String title = null;
        CmsCategory category = null;
        CmsCategory subCategory = null;
        List<String> subCategories = null;
        String openedCategories = null;

        // check if the user has a list of opened categories saved in his info hash
        openedCategories = getTreeInfo(m_cms, C_USER_INFO_OPENED_CATEGORIES, m_request);
        if ((openedCategories != null) && !"".equalsIgnoreCase(openedCategories)) {
            m_openedCategoryList = commaStringToList(openedCategories, C_LIST_SEPARATOR);
        } else {
            m_openedCategoryList = new ArrayList<String>();
        }

        for (int i = 0, n = m_openedCategoryList.size(); i < n; i++) {

            try {

                folder = m_openedCategoryList.get(i);
                parentFolder = CmsResource.getParentFolder(folder);
                if (m_useNavigation) {
                    position = m_cms.readPropertyObject(folder, CmsPropertyDefinition.PROPERTY_NAVPOS, false).getValue();
                } else {
                    position = m_cms.readPropertyObject(folder, C_PROP_CATEGORY, false).getValue();
                }
                if ((position != null) && !"".equalsIgnoreCase(position)) {
                    position = CategoryTree.cutPrefix(position);
                    title = readTitle(folder);
                    boolean isMain = (CmsResource.getPathLevel(folder) - CmsResource.getPathLevel(m_rootFolder)) == 1;
                    category = new CmsCategory(title, position, folder, isMain);

                    if (folder.startsWith(m_rootFolder) && !m_rootFolder.equalsIgnoreCase(folder)) {

                        // add the opened category itself to the tree
                        addCategory(parentFolder, category);

                        // add all sub-categories/folders to the tree
                        subCategories = getSubCategories(folder);
                        for (int j = 0, m = subCategories.size(); j < m; j++) {

                            subFolder = subCategories.get(j);
                            if (m_useNavigation) {
                                position = m_cms.readPropertyObject(
                                    subFolder,
                                    CmsPropertyDefinition.PROPERTY_NAVPOS,
                                    false).getValue();
                            } else {
                                position = m_cms.readPropertyObject(subFolder, C_PROP_CATEGORY, false).getValue();
                            }
                            if ((position != null) && !"".equalsIgnoreCase(position)) {
                                position = CategoryTree.cutPrefix(position);
                                title = readTitle(subFolder);
                                subCategory = new CmsCategory(title, position, subFolder, false);
                                addCategory(folder, subCategory);
                            }
                        }
                    }
                }
            } catch (CmsException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Error reading properties of resource " + folder, e);
                }
            }
        }
    }

    /**
     * Adds all selected categories/folders and their parent categories/folders to the tree.<p>
     */
    protected void addSelectedCategories() {

        String selectedCategories = null;
        String folder = null;
        List<String> subFolders = null;
        String subFolder = null;
        String title = null;
        String position = null;
        CmsCategory category = null;

        // check if the user has a list of selected categories saved in his info hash
        selectedCategories = getTreeInfo(m_cms, C_USER_INFO_SELECTED_CATEGORIES, m_request);
        if ((selectedCategories != null) && !"".equalsIgnoreCase(selectedCategories)) {
            m_selectedCategoryList = commaStringToList(selectedCategories, C_LIST_SEPARATOR);
        } else {
            m_selectedCategoryList = new ArrayList<String>();
        }

        // add all parent categories/folders of the selected categories to the tree
        for (int i = 0, n = m_selectedCategoryList.size(); i < n; i++) {

            folder = m_selectedCategoryList.get(i);
            if (!folder.startsWith(m_rootFolder)) {
                continue;
            }
            while (!m_rootFolder.equalsIgnoreCase(folder)) {

                folder = CmsResource.getParentFolder(folder);
                if (m_rootFolder.equalsIgnoreCase(folder)) {

                    // continue if the parent folder is the root folder
                    // because we want a tree beginning from the root folder
                    continue;
                }
                boolean isMain = (CmsResource.getPathLevel(folder) - CmsResource.getPathLevel(m_rootFolder)) == 1;

                try {

                    // add all sub folders of the current folder to the tree map
                    subFolders = getSubCategories(folder);
                    for (int j = 0, m = subFolders.size(); j < m; j++) {

                        subFolder = subFolders.get(j);
                        if (m_useNavigation) {
                            position = m_cms.readPropertyObject(subFolder, CmsPropertyDefinition.PROPERTY_NAVPOS, false).getValue();
                        } else {
                            position = m_cms.readPropertyObject(subFolder, C_PROP_CATEGORY, false).getValue();
                        }
                        if ((position != null) && !"".equalsIgnoreCase(position)) {
                            position = CategoryTree.cutPrefix(position);
                            title = readTitle(subFolder);
                            category = new CmsCategory(title, position, subFolder, isMain);

                            addCategory(folder, category);
                        }

                    }
                } catch (CmsException e) {

                    if (LOG.isErrorEnabled()) {
                        LOG.error("Error reading sub-folders of " + folder, e);
                    }
                }
            }
        }
    }

    /**
     * Removes all categories from the tree.<p>
     */
    protected synchronized void clear() {

        List<CmsCategory> children = null;
        String parent = null;
        List<String> list = null;

        if (m_treeMap == null) {
            return;
        }

        // iterate over all adjacency lists to clear them
        list = new ArrayList<String>(m_treeMap.keySet());
        for (int i = 0, n = list.size(); i < n; i++) {
            parent = list.get(i);

            // clear the adjacency list of the current parent
            children = m_treeMap.get(parent);
            children.clear();

            // remove the parent from the tree
            m_treeMap.remove(parent);
        }

        // clear the tree map again (robustness)
        m_treeMap.clear();
    }

    /**
     * Closes a category in the tree.<p>
     * 
     * @param closedCategoryFolder the category to be closed
     */
    protected void closeCategory(String closedCategoryFolder) {

        String category = null;
        List<String> tmpList = null;

        // update the list of opened categories
        tmpList = new ArrayList<String>();
        for (int i = 0, n = m_openedCategoryList.size(); i < n; i++) {

            category = m_openedCategoryList.get(i);
            if (!category.startsWith(closedCategoryFolder)) {
                tmpList.add(category);
            }
        }
        m_openedCategoryList = tmpList;

        // update the list of selected categories
        tmpList = new ArrayList<String>();
        for (int i = 0, n = m_selectedCategoryList.size(); i < n; i++) {

            category = m_selectedCategoryList.get(i);
            if (closedCategoryFolder.equalsIgnoreCase(category) || !category.startsWith(closedCategoryFolder)) {
                tmpList.add(category);
            }
        }
        m_selectedCategoryList = tmpList;
    }

    /**
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {

        try {

            clear();
            m_treeMap = null;
        } catch (Throwable t) {
            // ignore
        }

        super.finalize();
    }

    /**
     * Returns all sub-categories/folders of a specified category.<p>
     * 
     * Default case: the first 2 folder levels are categories. Thus, for the first 2 folder 
     * levels only folders having the "category" property set should be visible in the
     * tree. Down from the 2nd folder level, all folders should be visible in the tree.<p>
     * 
     * Overwrite this method if you want to make folders visible in the tree from
     * a folder level different than the default case here.<p>
     * 
     * @param parentFolder the parent category folder
     * @return a List of all sub-categories/folders
     */
    protected List<String> getSubCategories(String parentFolder) {

        String title = null;
        String position = null;
        List<CmsResource> list = null;
        String subFolder = null;
        List<String> result = new ArrayList<String>();

        try {

            // turn the parent folder into a category 
            if (m_useNavigation) {
                position = m_cms.readPropertyObject(parentFolder, CmsPropertyDefinition.PROPERTY_NAVPOS, false).getValue();
            } else {
                position = m_cms.readPropertyObject(parentFolder, C_PROP_CATEGORY, false).getValue();
            }
            position = CategoryTree.cutPrefix(position);
            title = m_cms.readPropertyObject(parentFolder, CmsPropertyDefinition.PROPERTY_TITLE, false).getValue();
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                // fall back: the navigation text
                title = m_cms.readPropertyObject(parentFolder, CmsPropertyDefinition.PROPERTY_NAVTEXT, false).getValue();
            }
            boolean isMain = (CmsResource.getPathLevel(parentFolder) - CmsResource.getPathLevel(m_rootFolder)) == 1;
            if (m_useNavigation) {
                CmsJspNavBuilder navBuilder = new CmsJspNavBuilder(m_cms);
                List<CmsJspNavElement> navElements = navBuilder.getNavigationForFolder(parentFolder);
                list = new ArrayList<CmsResource>(navElements.size());
                for (CmsJspNavElement navItem : navElements) {
                    if (navItem.isFolderLink()) {
                        list.add(navItem.getResource());
                    }
                }
            } else {
                list = m_cms.getSubFolders(parentFolder);
            }

            if (isMain) {

                // add only folders having the "category" property set if the
                // parent folder is a main category in the tree

                for (int i = 0, n = list.size(); i < n; i++) {

                    subFolder = m_cms.getRequestContext().removeSiteRoot((list.get(i)).getRootPath());
                    if (m_useNavigation) {
                        position = m_cms.readPropertyObject(subFolder, CmsPropertyDefinition.PROPERTY_NAVPOS, false).getValue();
                    } else {
                        position = m_cms.readPropertyObject(subFolder, C_PROP_CATEGORY, false).getValue();
                    }

                    if ((position != null) && !"".equalsIgnoreCase(position)) {
                        position = CategoryTree.cutPrefix(position);
                        result.add(subFolder);
                    }
                }
            } else {

                for (int i = 0, n = list.size(); i < n; i++) {

                    subFolder = m_cms.getRequestContext().removeSiteRoot(((CmsFolder)list.get(i)).getRootPath());
                    result.add(subFolder);
                }
            }
        } catch (CmsException e) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Error reading properties", e);
            }
        }

        return result;
    }

    /**
     * Opens a category in the tree.<p>
     * 
     * @param openedCategoryFolder the category to be opened
     */
    protected void openCategory(String openedCategoryFolder) {

        List<String> subCategories = null;

        // the user opened a sub-tree
        if (!m_openedCategoryList.contains(openedCategoryFolder)) {
            m_openedCategoryList.add(openedCategoryFolder);

            if (m_selectedCategoryList.contains(openedCategoryFolder)) {

                // add all sub-categories of the opened category
                // if the opened category is currently selected
                subCategories = getSubCategories(openedCategoryFolder);
                m_selectedCategoryList.addAll(subCategories);
            }
        }
    }

    /**
     * Reads the value of the title property of the specified resource.<p>
     * 
     * First reads the localized title, if this is not found, the common title.<p>
     * 
     * @param resourceName the resource name to look up the property
     * @return the value of the title property of the specified resource
     */
    protected String readTitle(String resourceName) {

        String title = "";
        try {
            try {
                // first read the title property for the current locale
                title = m_cms.readPropertyObject(resourceName, m_propertyTitle, false).getValue();
            } catch (CmsException exc) {
                // ignore, property might not exist
            }
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                // localized title not found, read the usual title
                title = m_cms.readPropertyObject(resourceName, CmsPropertyDefinition.PROPERTY_TITLE, false).getValue();
            }
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                // usual title not found, read navigation text
                title = m_cms.readPropertyObject(resourceName, CmsPropertyDefinition.PROPERTY_NAVTEXT, false).getValue();
            }
        } catch (CmsException e) {
            // ignore
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
            // no title property found at all, use the resource name as title
            title = CmsResource.getName(resourceName);
        }
        return title;
    }

    /**
     * Saves the tree info (opened/selected categories) in the current user's info hash.<p>
     */
    protected void saveTreeInfo() {

        String prefix = m_cms.getRequestContext().addSiteRoot(m_cms.getRequestContext().getUri()) + "_";

        // save the categories that the user selected in his info hash
        saveUserInfo(
            prefix + C_USER_INFO_SELECTED_CATEGORIES,
            listToCommaString(m_selectedCategoryList, C_LIST_SEPARATOR));

        // save the categories that the user selected in his info hash
        saveUserInfo(prefix + C_USER_INFO_OPENED_CATEGORIES, listToCommaString(m_openedCategoryList, C_LIST_SEPARATOR));
    }

    /**
     * Saves a key/value pair in the additional info hash of the current user.<p>
     * 
     * @param key the key
     * @param value the value
     */
    protected void saveUserInfo(String key, String value) {

        CmsUser user = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("saving additional info for user "
                + m_cms.getRequestContext().getCurrentUser().getName()
                + " with key: "
                + key
                + ", value: "
                + value);
        }

        if (value == null) {

            return;
        }

        try {
            // store the current information about the tree stauts in the session
            HttpSession session = m_request.getSession(true);
            session.setAttribute(key, value);

            // if the user is not the guest user, store the settings in the user, too
            user = m_cms.getRequestContext().getCurrentUser();
            if (!user.isGuestUser()) {
                user.setAdditionalInfo(key, value);
                m_cms.writeUser(user);
            }
        } catch (CmsException e) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Error saving additional info for user "
                    + user.getName()
                    + " with key: "
                    + key
                    + ", value: "
                    + value, e);
            }
        }
    }

    /**
     * Recursively builds a DFS list of a sub-tree beginning 
     * from the specified parent category.<p>
     * 
     * @param parent the category from where the sub-tree is built
     * @return a List of categories in the sub tree in DFS order
     */
    protected List<CmsCategory> toList(CmsCategory parent) {

        List<CmsCategory> result = null;
        List<CmsCategory> children = null;
        CmsCategory category = null;

        if (parent == null) {

            return null;
        }

        result = new ArrayList<CmsCategory>();
        result.add(parent);

        // get the adjacency list with the child objects of the current parent ID
        children = m_treeMap.get(parent.getCmsResource());
        if (children == null) {

            return result;
        }

        // add the sub-tree of the current parent resource to the result list
        Collections.sort(children);
        for (int i = 0, n = children.size(); i < n; i++) {

            category = children.get(i);
            result.addAll(toList(category));
        }

        return result;
    }

}