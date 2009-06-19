/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsDocumentSearch.java,v $
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
import org.opencms.file.CmsRequestContext;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.search.CmsSearch;
import org.opencms.search.CmsSearchIndex;
import org.opencms.search.CmsSearchResult;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class for executing searches with lucene inside the document center.
 * 
 * @author Peter Bonrad
 */
public class CmsDocumentSearch {

    /** The name of the property where the name of the index can be found. */
    public static final String PROPERTY_SEARCHINDEX = "search.index";

    /** Session key prefix. */
    protected static final String SESSION_KEY_PARAMS = CmsDocumentSearch.class.getName() + ".param.";

    /** Session key for the query text. */
    public static final String SEARCH_PARAM_QUERY = SESSION_KEY_PARAMS + "query";

    /** The current user's Cms object. */
    private CmsObject m_cms;

    /** The request used tu initialize the Category Tree. */
    private HttpServletRequest m_request;

    /**
     * Creates a new CmsDocumentSearch.<p>
     * 
     * @param cms the current user's Cms object
     * @param request the Http request
     */
    public CmsDocumentSearch(CmsObject cms, HttpServletRequest request) {

        m_cms = cms;
        m_request = request;
    }

    /**
     * Execute a CmsSearch and returns a list with all found resources. The query text is taken out
     * of the actual session.<p>
     * 
     * @param index the name of the index which should be used
     * @return a list with all found resources
     */
    public List execute(String index) {

        String query = (String)m_request.getSession().getAttribute(CmsDocumentSearch.SEARCH_PARAM_QUERY);
        return execute(index, query);
    }

    /**
     * Execute a CmsSearch and returns a list with all found resources.<p>
     * 
     * @param index the name of the index which should be used
     * @param query the query to search for
     * @return a list with all found resources
     */
    public List execute(String index, String query) {

        ArrayList result = new ArrayList();
        long t = System.currentTimeMillis();
        CmsSearch search = new CmsSearch();
        search.setMatchesPerPage(-1);
        search.setIndex(index);

        OpenCms.getSearchManager().getIndex(index).addConfigurationParameter(
            CmsSearchIndex.EXCERPT,
            CmsStringUtil.FALSE);

        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(query)) {
            search.setQuery(query);
            search.init(m_cms);

            // filter the selected categories
            String categories = (String)m_request.getSession().getAttribute(
                NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_CATEGORYLIST);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(categories)) {
                search.setSearchRoots(CmsStringUtil.splitAsArray(categories, CategoryTree.C_LIST_SEPARATOR));
            } else {
                try {
                    search.setSearchRoot(m_cms.getSitePath(m_cms.readAncestor(m_cms.getRequestContext().getUri(), 260)));
                } catch (CmsException ex) {
                    return result;
                }
            }
            System.out.println("###Prepare search: " + (System.currentTimeMillis() - t));
            CmsRequestContext context = m_cms.getRequestContext();
            Iterator iter = search.getSearchResult().iterator();

            System.out.println("###Excecute search: " + (System.currentTimeMillis() - t));

            while (iter.hasNext()) {
                CmsSearchResult searchResult = (CmsSearchResult)iter.next();
                try {

                    CmsResource resource = m_cms.readResource(context.removeSiteRoot(searchResult.getPath()));

                    // filter files starting with a "$"
                    if (resource.getName().startsWith("$")) {
                        continue;
                    }

                    result.add(resource);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        }

        System.out.println("###get files: " + (System.currentTimeMillis() - t));

        return result;
    }
}
