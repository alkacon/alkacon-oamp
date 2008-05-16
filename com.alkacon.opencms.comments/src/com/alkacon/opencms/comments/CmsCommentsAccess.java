/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/CmsCommentsAccess.java,v $
 * Date   : $Date: 2008/05/16 11:40:26 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.comments;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsPermissionSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.logging.Log;

/**
 * Provides direct access to comments.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.5
 */
public class CmsCommentsAccess extends CmsJspActionElement {

    /** Action name constant. */
    public static final String ACTION_APPROVE = "approve";

    /** Action name constant. */
    public static final String ACTION_BLOCK = "block";

    /** Action name constant. */
    public static final String ACTION_DELETE = "delete";

    /** Parameter name constant. */
    public static final String PARAM_ACTION = "cmtaction";

    /** The log object for this class. */
    protected static final Log LOG = CmsLog.getLog(CmsCommentsAccess.class);

    /** Field name constant. */
    private static final String FIELD_NAME = "name";

    /** Parameter name constant. */
    public static final String PARAM_PAGE = "cmtpage";

    /** Parameter name constant. */
    public static final String PARAM_SHOW = "cmtshow";

    /** Parameter name constant. */
    public static final String PARAM_URI = "cmturi";

    /** Property name constant. */
    private static final String PROPERTY_COMMENTS = "comments";

    /** Form state for approved comments. */
    private static final int STATE_APPROVED = 1;

    /** Form state for blocked comments. */
    private static final int STATE_BLOCKED = 2;

    /** Form state for new comments. */
    private static final int STATE_NEW = 0;

    /** The configuration. */
    private CmsCommentConfiguration m_config;

    /** Map where the key is the author name and the value the number of comments. */
    private Map m_countByAuthor;

    /** The current page. */
    private int m_page;

    /** The requested resource. */
    private CmsResource m_resource;

    /** Cached value, if the current user is valid. */
    private Boolean m_userValid;

    /**
     * Constructor, with parameters.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsCommentsAccess(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Approves the given comment entry.<p>
     * 
     * @param entryId the id of the comment entry to approve
     */
    public void approve(int entryId) {

        if (!isUserCanManage()) {
            return;
        }
        try {
            CmsFormDataAccess.getInstance().updateState(entryId, STATE_APPROVED);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Blocks the given comment entry.<p>
     * 
     * @param entryId the id of the comment entry to block
     */
    public void block(int entryId) {

        if (!isUserCanManage()) {
            return;
        }
        try {
            CmsFormDataAccess.getInstance().updateState(entryId, STATE_BLOCKED);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Deletes the given comment entry.<p>
     * 
     * @param entryId the id of the comment entry to delete
     */
    public void delete(int entryId) {

        if (!isUserCanManage()) {
            return;
        }
        try {
            CmsFormDataAccess.getInstance().deleteForm(entryId);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Returns the list of comments (for the given page).<p>
     * 
     * @return a list of {@link com.alkacon.opencms.formgenerator.database.CmsFormDataBean} objects
     */
    public List getComments() {

        CmsCommentFormHandler jsp = null;
        try {
            jsp = new CmsCommentFormHandler(getJspContext(), getRequest(), getResponse(), getConfig().getConfigUri());
            jsp.getCommentFormConfiguration().removeCaptchaField();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        int itemSize = (jsp == null ? 2000000 : jsp.getCommentFormConfiguration().getAllFields().size());
        int itemsPerPage = getConfig().getList() * itemSize;

        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(getResource().getStructureId());
        filter = filter.filterOrderDesc();
        if (getConfig().getList() > 0) {
            int base = m_page * itemsPerPage;
            filter = filter.filterIndex(base, base + itemsPerPage);
        }
        if (!isUserCanManage()) {
            // easy case
            if (getConfig().isModerated()) {
                filter = filter.filterState(STATE_APPROVED);
            }
            try {
                return CmsFormDataAccess.getInstance().readForms(filter);
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        // hard case, if moderated
        List ret = new ArrayList();

        // first new comments, oldest first
        CmsFormDatabaseFilter newFilter = ((CmsFormDatabaseFilter)filter.clone()).filterState(STATE_NEW);
        if (getConfig().getList() > 0) {
            int base = m_page * itemsPerPage;
            newFilter = newFilter.filterIndex(base, base + itemsPerPage);
            newFilter = newFilter.filterOrderAsc();
        }
        try {
            ret.addAll(CmsFormDataAccess.getInstance().readForms(newFilter));
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        int cmtCount = 0;
        // then, blocked comments, oldest first
        if ((getConfig().getList() < 0) || (ret.size() < getConfig().getList())) {
            CmsFormDatabaseFilter blockedFilter = ((CmsFormDatabaseFilter)filter.clone()).filterState(STATE_BLOCKED);
            if (getConfig().getList() > 0) {
                cmtCount = getCountNewComments();
                int base = (m_page * itemsPerPage) - (cmtCount * itemSize);
                blockedFilter = blockedFilter.filterIndex(base <= 0 ? 0 : base, base + itemsPerPage);
                blockedFilter = blockedFilter.filterOrderAsc();
            }
            try {
                ret.addAll(CmsFormDataAccess.getInstance().readForms(blockedFilter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }

        // at last, approved comments, newest first
        if ((getConfig().getList() < 0) || (ret.size() < getConfig().getList())) {
            CmsFormDatabaseFilter approvedFilter = ((CmsFormDatabaseFilter)filter.clone()).filterState(STATE_APPROVED);
            if (getConfig().getList() > 0) {
                cmtCount += getCountBlockedComments();
                int base = (m_page * itemsPerPage) - (cmtCount * itemSize);
                approvedFilter = approvedFilter.filterIndex(base <= 0 ? 0 : base, base + itemsPerPage);
                approvedFilter = approvedFilter.filterOrderDesc();
            }
            try {
                ret.addAll(CmsFormDataAccess.getInstance().readForms(approvedFilter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        return ret;
    }

    /**
     * Returns the config.<p>
     *
     * @return the config
     */
    public CmsCommentConfiguration getConfig() {

        return m_config;
    }

    /**
     * Returns the number of approved comments.<p>
     * 
     * @return the number of approved comments
     */
    public int getCountApprovedComments() {

        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(m_resource.getStructureId());
        filter = filter.filterState(STATE_APPROVED);
        try {
            return CmsFormDataAccess.getInstance().countForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }

    /**
     * Returns the number of blocked comments.<p>
     * 
     * Always zero if not moderated.<p>
     * 
     * @return the number of blocked comments
     */
    public int getCountBlockedComments() {

        if (!m_config.isModerated()) {
            return 0;
        }
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(m_resource.getStructureId());
        filter = filter.filterState(STATE_BLOCKED);
        try {
            return CmsFormDataAccess.getInstance().countForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }

    /**
     * Returns the number of comments a given author has written.<p>
     * 
     * To use this method you have to define a field called {@link #FIELD_NAME}.<p>
     * 
     * @return a map where the key is the author name and the value the number of comments
     */
    public Map getCountByAuthor() {

        if (m_countByAuthor == null) {
            m_countByAuthor = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String author = String.valueOf(input);
                    CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
                    filter = filter.filterFormId(CmsCommentForm.FORM_ID);
                    filter = filter.filterField(FIELD_NAME, author);
                    try {
                        return new Integer(CmsFormDataAccess.getInstance().countForms(filter));
                    } catch (SQLException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(e.getLocalizedMessage(), e);
                        }
                    }
                    return new Integer(0);
                }
            });
        }
        return m_countByAuthor;
    }

    /**
     * Returns the number of comments.<p>
     * 
     * This depends of the moderation mode and your permissions.<p>
     * 
     * @return the number of comments
     */
    public int getCountComments() {

        if (!m_config.isModerated()) {
            return 0;
        }
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(m_resource.getStructureId());
        if (!isUserCanManage()) {
            if (getConfig().isModerated()) {
                filter = filter.filterState(STATE_APPROVED);
            }
        }
        try {
            return CmsFormDataAccess.getInstance().countForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }

    /**
     * Returns the number of new comments.<p>
     * 
     * Always zero if not moderated.<p>
     * 
     * @return the number of new comments
     */
    public int getCountNewComments() {

        if (!m_config.isModerated()) {
            return 0;
        }
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(m_resource.getStructureId());
        filter = filter.filterState(STATE_NEW);
        try {
            return CmsFormDataAccess.getInstance().countForms(filter);
        } catch (SQLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        return 0;
    }

    /**
     * Returns the page.<p>
     *
     * @return the page
     */
    public int getPage() {

        return m_page;
    }

    /**
     * Returns the resource.<p>
     * 
     * @return the resource
     */
    public CmsResource getResource() {

        return m_resource;
    }

    /**
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super.init(context, req, res);
        try {
            m_resource = getCmsObject().readResource(req.getParameter(PARAM_URI));
            String configUri = getCmsObject().readPropertyObject(m_resource, PROPERTY_COMMENTS, true).getValue();
            m_config = new CmsCommentConfiguration(this, configUri);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        m_page = 0;
        try {
            m_page = Integer.parseInt(req.getParameter(PARAM_PAGE));
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Checks if this user is the default guest user.<p>
     * 
     * @return <code>true</code> if this user is the default guest user
     */
    public boolean isGuestUser() {

        return getRequestContext().currentUser().isGuestUser();
    }

    /**
     * Checks if the current can manage the comments.<p>
     * 
     * @return if the current can manage the comments
     */
    public boolean isUserCanManage() {

        try {
            return getCmsObject().hasPermissions(
                m_resource,
                CmsPermissionSet.ACCESS_WRITE,
                false,
                CmsResourceFilter.DEFAULT);
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        return false;
    }

    /**
     * Checks if the current can post new comments.<p>
     * 
     * @return if the current can post new comments
     */
    public boolean isUserCanPost() {

        if (m_config.getSecurity().isNone()) {
            return true;
        }
        return isUserValid();
    }

    /**
     * Checks if the current can view comments.<p>
     * 
     * @return if the current can view comments
     */
    public boolean isUserCanView() {

        if (m_config.getSecurity().isView()) {
            return isUserValid();
        }
        return true;
    }

    /**
     * Checks if the current user is valid.<p> 
     * 
     * @return <code>true</code>, if the current user is valid
     */
    public boolean isUserValid() {

        if (m_userValid == null) {
            CmsUser user = getRequestContext().currentUser();
            if (m_config.getGroups().isEmpty() && m_config.getOrgUnits().isEmpty()) {
                m_userValid = Boolean.valueOf(!user.isGuestUser());
            } else if (user.isGuestUser()) {
                m_userValid = Boolean.FALSE;
            } else {
                CmsObject cms = getCmsObject();
                Iterator itGroups = m_config.getGroups().iterator();
                while (itGroups.hasNext() && (m_userValid == null)) {
                    CmsGroup group = (CmsGroup)itGroups.next();
                    try {
                        if (cms.userInGroup(user.getName(), group.getName())) {
                            m_userValid = Boolean.TRUE;
                        }
                    } catch (CmsException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(e.getLocalizedMessage());
                        }
                    }
                }
                Iterator itOus = m_config.getOrgUnits().iterator();
                while (itOus.hasNext() && (m_userValid == null)) {
                    CmsOrganizationalUnit ou = (CmsOrganizationalUnit)itOus.next();
                    if (ou.getName().startsWith(user.getOuFqn())) {
                        m_userValid = Boolean.TRUE;
                    }
                }
                m_userValid = Boolean.FALSE;
            }
        }
        return m_userValid.booleanValue();
    }

    /**
     * Execute the given action.<p>
     */
    public void doAction() {

        String action = getRequest().getParameter(CmsCommentsAccess.PARAM_ACTION);
        if ((action == null) || (action.trim().length() == 0)) {
            return;
        }
        if (action.startsWith(CmsCommentsAccess.ACTION_DELETE)) {
            delete(Integer.parseInt(action.substring(CmsCommentsAccess.ACTION_DELETE.length())));
        } else if (action.startsWith(CmsCommentsAccess.ACTION_BLOCK)) {
            block(Integer.parseInt(action.substring(CmsCommentsAccess.ACTION_BLOCK.length())));
        } else if (action.startsWith(CmsCommentsAccess.ACTION_APPROVE)) {
            approve(Integer.parseInt(action.substring(CmsCommentsAccess.ACTION_APPROVE.length())));
        }
    }
}
