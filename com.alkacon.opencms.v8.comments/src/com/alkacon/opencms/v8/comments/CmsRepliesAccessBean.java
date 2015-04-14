/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.comments;

import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.main.CmsLog;
import org.opencms.util.CmsCollectionsGenericWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;

/**
 * Access to comment replies, optimized for JSPs.
 */
public class CmsRepliesAccessBean {

    /** The log object for this class. */
    protected static final Log LOG = CmsLog.getLog(CmsCommentsAccess.class);

    /** Lazily initialized map of the number of replies for comments with the id given as key. */
    private Map<String, Integer> m_countReplies;

    /** Lazily initialized map of the number of replies for comments with the id given as key. */
    private Map<String, List<CmsFormDataBean>> m_replies;

    /** The form id. */
    private String m_formId;

    /**
     * Returns a lazily initialized map that provides the number of replies, if a comment id is provided as key.
     * 
     * @return lazily initialized map that provides the number of replies, if a comment id is provided as key.
     */
    public Map<String, Integer> getCountReplies() {

        if (m_countReplies == null) {
            m_countReplies = CmsCollectionsGenericWrapper.createLazyMap(new Transformer() {

                public Object transform(Object sEntryId) {

                    int entryId;
                    try {
                        entryId = Integer.parseInt(sEntryId.toString());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    return Integer.valueOf(getCountRepliesForComment(entryId));
                }
            });
        }
        return m_countReplies;
    }

    /**
     * Returns the number of replies for a comment specified by its entry id.
     * 
     * @param entryId the entry id of the comment, where replies should be counted for
     * 
     * @return number of replies for the given comment
     */
    public int getCountRepliesForComment(int entryId) {

        int countReplies = 0;
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
        filter = filter.filterFormId(getReplyFormIdFilter());
        filter = filter.filterField(CmsCommentFormHandler.FIELD_REPLYTO, Integer.toString(entryId));
        try {
            countReplies = CmsFormDataAccess.getInstance().countForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            // nothing to do, go on as if zero replies are given
        }
        return countReplies;
    }

    /**
     * Returns the form id.<p>
     * 
     * @return the form id
     */
    public String getFromId() {

        return m_formId;
    }

    /**
     * Returns a lazily initialized map that provides the list of replies, if a comment id is provided as key.
     * 
     * @return lazily initialized map that provides the list of replies, if a comment id is provided as key.
     */
    public Map<String, List<CmsFormDataBean>> getReplies() {

        if (m_replies == null) {
            m_replies = CmsCollectionsGenericWrapper.createLazyMap(new Transformer() {

                public Object transform(Object sEntryId) {

                    int entryId;
                    try {
                        entryId = Integer.parseInt(sEntryId.toString());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    return getRepliesForComment(entryId);
                }
            });
        }
        return m_replies;
    }

    /**
     * Returns the replies for a comment specified by its entry id.
     * 
     * @param entryId the entry id of the comment, where replies should be returned for
     * 
     * @return replies for the given comment
     */
    public List<CmsFormDataBean> getRepliesForComment(int entryId) {

        List<CmsFormDataBean> replies;
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterFormId(getReplyFormIdFilter());
        filter = filter.filterField(CmsCommentFormHandler.FIELD_REPLYTO, Integer.toString(entryId));
        filter = filter.filterOrderAsc();
        try {
            replies = CmsFormDataAccess.getInstance().readForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            // nothing to do, go on as if zero replies are given
            return new ArrayList<CmsFormDataBean>();
        }
        return replies;
    }

    /**
     * Sets the form id.<p>
     * 
     * @param formId the form id
     */
    public void setFormId(String formId) {

        m_formId = formId;
    }

    /**
     * Returns the comment access filter form id.<p>
     * 
     * @return the comment access filter form id
     */
    private String getReplyFormIdFilter() {

        return CmsCommentsAccess.getReplyFromId(m_formId) + "," + CmsCommentsAccess.REPLIES_FORMID;
    }
}
