/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/CmsCommentsAccess.java,v $
 * Date   : $Date: 2008/06/09 09:44:24 $
 * Version: $Revision: 1.9 $
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

import org.opencms.db.CmsDbEntryNotFoundException;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspLoginBean;
import org.opencms.main.CmsEvent;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.I_CmsEventListener;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsPermissionSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.logging.Log;

/**
 * Provides direct access to comments.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.9 $
 * 
 * @since 7.0.5
 */
public class CmsCommentsAccess extends CmsJspLoginBean {

    /** Action name constant. */
    public static final String ACTION_APPROVE = "approve";

    /** Action name constant. */
    public static final String ACTION_BLOCK = "block";

    /** Action name constant. */
    public static final String ACTION_DELETE = "delete";

    /** Session attribute to control manageability. */
    public static final String ATTR_CMT_MANAGE = "ATTR_CMT_MANAGE";

    /** Parameter name constant. */
    public static final String PARAM_ACTION = "cmtaction";

    /** Parameter name constant. */
    public static final String PARAM_ENTRY = "cmtentry";

    /** Parameter name constant. */
    public static final String PARAM_PAGE = "cmtpage";

    /** Parameter name constant. */
    public static final String PARAM_SHOW = "cmtshow";

    /** Parameter name constant. */
    public static final String PARAM_STATE = "cmtstate";

    /** Parameter name constant. */
    public static final String PARAM_URI = "cmturi";

    /** The log object for this class. */
    protected static final Log LOG = CmsLog.getLog(CmsCommentsAccess.class);

    /** Cached configurations. */
    protected static Map m_configs = Collections.synchronizedMap(new HashMap());

    /** Property name constant. */
    private static final String PROPERTY_COMMENTS = "comments";

    /** Form state for approved comments. */
    private static final int STATE_APPROVED = 1;

    /** Form state for blocked comments. */
    private static final int STATE_BLOCKED = 2;

    /** Form state for new comments. */
    private static final int STATE_NEW = 0;

    /** Cached list of current page comments. */
    private List m_comments;

    /** The configuration. */
    private CmsCommentConfiguration m_config;

    /** Cached count of approved comments. */
    private Integer m_countApprovedComments;

    /** Cached count of blocked comments. */
    private Integer m_countBlockedComments;

    /** Map where the key is the author name and the value the number of comments. */
    private Map m_countByAuthor;

    /** Cached count of all comments. */
    private Integer m_countComments;

    /** Cached count of new comments. */
    private Integer m_countNewComments;

    /** Cached count of filtered comments. */
    private Integer m_countStateComments;

    /** Right login exception. */
    private CmsException m_exc;

    /** The current page. */
    private int m_page;

    /** The requested resource. */
    private CmsResource m_resource;

    /** If the comment element should start maximized. */
    private boolean m_show;

    /** If set, the state of comments to be displayed. */
    private Integer m_state;

    /** The path to the actual page to comment. */
    private String m_uri;

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

    static {
        OpenCms.addCmsEventListener(new I_CmsEventListener() {

            /**
             * @see org.opencms.main.I_CmsEventListener#cmsEvent(org.opencms.main.CmsEvent)
             */
            public void cmsEvent(CmsEvent event) {

                switch (event.getType()) {
                    case EVENT_CLEAR_CACHES:
                    case EVENT_CLEAR_OFFLINE_CACHES:
                    case EVENT_CLEAR_ONLINE_CACHES:
                    case EVENT_PUBLISH_PROJECT:
                        m_configs.clear();
                        break;

                    case EVENT_RESOURCE_AND_PROPERTIES_MODIFIED:
                    case EVENT_RESOURCE_DELETED:
                    case EVENT_RESOURCE_MODIFIED:
                        if (event.getData() == null) {
                            m_configs.clear();
                            break;
                        }
                        CmsResource res = (CmsResource)event.getData().get("resource");
                        if (res == null) {
                            m_configs.clear();
                            break;
                        }

                        String cacheKey = res.getRootPath() + "-";
                        m_configs.remove(cacheKey);
                        break;

                    default:
                        break;
                }

            }
        }, new int[] {
            I_CmsEventListener.EVENT_CLEAR_CACHES,
            I_CmsEventListener.EVENT_CLEAR_OFFLINE_CACHES,
            I_CmsEventListener.EVENT_CLEAR_ONLINE_CACHES,
            I_CmsEventListener.EVENT_RESOURCE_AND_PROPERTIES_MODIFIED,
            I_CmsEventListener.EVENT_RESOURCE_DELETED,
            I_CmsEventListener.EVENT_RESOURCE_MODIFIED,
            I_CmsEventListener.EVENT_PUBLISH_PROJECT});
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
     * Execute the given action.<p>
     */
    public void doAction() {

        String action = getRequest().getParameter(CmsCommentsAccess.PARAM_ACTION);
        if ((action == null) || (action.trim().length() == 0)) {
            return;
        }
        int entry = -1;
        try {
            entry = Integer.parseInt(getRequest().getParameter(CmsCommentsAccess.PARAM_ENTRY));
        } catch (NumberFormatException e) {
            return;
        }
        if (action.equals(CmsCommentsAccess.ACTION_DELETE)) {
            delete(entry);
        } else if (action.equals(CmsCommentsAccess.ACTION_BLOCK)) {
            block(entry);
        } else if (action.equals(CmsCommentsAccess.ACTION_APPROVE)) {
            approve(entry);
        }
    }

    /**
     * Returns the current set session attribute to manage comments.<p>
     * 
     * @return the current set session attribute to manage comments
     */
    public Boolean getAttrManage() {

        HttpSession session = getRequest().getSession();
        if (session != null) {
            Object canManageObj = session.getAttribute(ATTR_CMT_MANAGE);
            if (canManageObj instanceof Boolean) {
                return (Boolean)canManageObj;
            }
        }
        return null;
    }

    /**
     * Returns the list of comments (for the given page).<p>
     * 
     * @return a list of {@link com.alkacon.opencms.formgenerator.database.CmsFormDataBean} objects
     */
    public List getComments() {

        if (m_comments == null) {
            CmsCommentFormHandler jsp = null;
            try {
                jsp = new CmsCommentFormHandler(getJspContext(), getRequest(), getResponse(), this);
                jsp.getCommentFormConfiguration().removeCaptchaField();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
            int itemSize = (jsp == null ? 2000000 : jsp.getCommentFormConfiguration().getAllFields().size());
            int itemsPerPage = getConfig().getList() * itemSize;

            CmsFormDatabaseFilter filter = getCommentFilter(false, true);
            filter = filter.filterOrderDesc();
            if (getConfig().getList() > 0) {
                int base = m_page * itemsPerPage;
                filter = filter.filterIndex(base, base + itemsPerPage);
            }

            try {
                m_comments = CmsFormDataAccess.getInstance().readForms(filter);
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_comments = new ArrayList();
            }
        }
        return m_comments;
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

        if (m_countApprovedComments == null) {
            CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
            filter = filter.filterFormId(CmsCommentForm.FORM_ID);
            filter = filter.filterResourceId(m_resource.getStructureId());
            filter = filter.filterState(STATE_APPROVED);
            try {
                m_countApprovedComments = new Integer(CmsFormDataAccess.getInstance().countForms(filter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_countApprovedComments = new Integer(0);
            }
        }
        return m_countApprovedComments.intValue();
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
        if (m_countBlockedComments == null) {
            CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
            filter = filter.filterFormId(CmsCommentForm.FORM_ID);
            filter = filter.filterResourceId(m_resource.getStructureId());
            filter = filter.filterState(STATE_BLOCKED);
            try {
                m_countBlockedComments = new Integer(CmsFormDataAccess.getInstance().countForms(filter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_countBlockedComments = new Integer(0);
            }
        }
        return m_countBlockedComments.intValue();
    }

    /**
     * Returns the number of comments a given author has written.<p>
     * 
     * To use this method you have to define a field called {@link CmsCommentFormHandler#FIELD_USERNAME}.<p>
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
                    filter = filter.filterField(CmsCommentFormHandler.FIELD_USERNAME, author);
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

        if (m_countComments == null) {
            CmsFormDatabaseFilter filter = getCommentFilter(true, false);
            try {
                m_countComments = new Integer(CmsFormDataAccess.getInstance().countForms(filter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_countComments = new Integer(0);
            }
        }
        return m_countComments.intValue();
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
        if (m_countNewComments == null) {
            CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.HEADERS;
            filter = filter.filterFormId(CmsCommentForm.FORM_ID);
            filter = filter.filterResourceId(m_resource.getStructureId());
            filter = filter.filterState(STATE_NEW);
            try {
                m_countNewComments = new Integer(CmsFormDataAccess.getInstance().countForms(filter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_countNewComments = new Integer(0);
            }
        }
        return m_countNewComments.intValue();
    }

    /**
     * Returns the number of comments filtered by state.<p>
     * 
     * This depends of the moderation mode and your permissions.<p>
     * 
     * @return the number of comments
     */
    public int getCountStateComments() {

        if (m_countStateComments == null) {
            CmsFormDatabaseFilter filter = getCommentFilter(true, true);
            try {
                m_countStateComments = new Integer(CmsFormDataAccess.getInstance().countForms(filter));
            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
                m_countStateComments = new Integer(0);
            }
        }
        return m_countStateComments.intValue();
    }

    /**
     * @see org.opencms.jsp.CmsJspLoginBean#getLoginException()
     */
    public CmsException getLoginException() {

        if (m_exc == null) {
            return super.getLoginException();
        }
        return m_exc;
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
     * Returns the total number of pages.<p>
     * 
     * @return the total number of pages
     */
    public int getPages() {

        int countCmts = getCountStateComments();
        if ((getConfig().getList() <= 0) || (countCmts == 0)) {
            return 1;
        }
        return (int)Math.ceil(((double)countCmts) / getConfig().getList());
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
     * Returns the frontend resource bundle name.<p>
     * 
     * @return the frontend resource bundle name
     */
    public String getResourceBundle() {

        if (getConfig().getResourceBundle() != null) {
            return getConfig().getResourceBundle();
        }
        return "com.alkacon.opencms.comments.frontend";
    }

    /**
     * Returns the state of the comments that should be displayed.<p>
     *
     * @return the state of the comments that should be displayed, <code>null</code> for all
     */
    public Integer getState() {

        return m_state;
    }

    /**
     * Returns the uri.<p>
     *
     * @return the uri
     */
    public String getUri() {

        return m_uri;
    }

    /**
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super.init(context, req, res);
        if (LOG.isDebugEnabled()) {
            Iterator it = req.getParameterMap().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                LOG.debug(Messages.get().getBundle().key(
                    Messages.LOG_INIT_PARAM_2,
                    entry.getKey(),
                    Arrays.asList((String[])entry.getValue())));
            }
        }
        try {
            m_uri = req.getParameter(PARAM_URI);
            m_resource = getCmsObject().readResource(m_uri);
            getCmsObject().getRequestContext().setUri(m_uri);
            String configUri = readConfigUri();
            String cacheKey = getCmsObject().getRequestContext().addSiteRoot(configUri)
                + (getCmsObject().getRequestContext().currentProject().isOnlineProject() ? "+" : "-");
            m_config = (CmsCommentConfiguration)m_configs.get(cacheKey);
            if (m_config == null) {
                m_config = new CmsCommentConfiguration(this, configUri);
                m_configs.put(cacheKey, m_config);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.get().getBundle().key(
                    Messages.LOG_INIT_PROJECT_1,
                    getCmsObject().getRequestContext().currentProject().getName()));
                LOG.debug(Messages.get().getBundle().key(
                    Messages.LOG_INIT_SITE_1,
                    getCmsObject().getRequestContext().getSiteRoot()));
                LOG.debug(Messages.get().getBundle().key(Messages.LOG_INIT_RESOURCE_1, m_resource));
            }
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        try {
            m_state = Integer.valueOf(req.getParameter(PARAM_STATE));
        } catch (NumberFormatException e) {
            m_state = null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.get().getBundle().key(Messages.LOG_INIT_STATE_1, m_state));
        }
        m_show = Boolean.valueOf(req.getParameter(PARAM_SHOW)).booleanValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.get().getBundle().key(Messages.LOG_INIT_SHOW_1, "" + m_show));
        }
        m_page = 0;
        try {
            m_page = Integer.parseInt(req.getParameter(PARAM_PAGE));
        } catch (Exception e) {
            // ignore
        }
        if (m_page >= getPages()) {
            m_page = getPages() - 1;
        }
        if (m_page < 0) {
            m_page = 0;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.get().getBundle().key(Messages.LOG_INIT_PAGE_1, "" + m_page));
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
     * Checks if the comment element should start maximized.<p>
     * 
     * @return <code>true</code>, if the comment element should start maximized
     */
    public boolean isMaximized() {

        return isShow() || !getConfig().isMinimized();
    }

    /**
     * Checks if the filter navigation is needed.<p>
     * 
     * @return <code>true</code> if the filter navigation is needed
     */
    public boolean isNeedFilter() {

        return getConfig().isModerated() && isUserCanManage() && (getCountComments() > 0);
    }

    /**
     * Checks if pagination is needed.<p>
     * 
     * @return <code>true</code>, if pagination is needed
     */
    public boolean isNeedPagination() {

        return (getConfig().getList() > 0) && (getCountStateComments() > getConfig().getList());
    }

    /**
     * Checks if the comment element should start maximized.<p>
     *
     * @return <code>true</code>, if the comment element should start maximized
     */
    public boolean isShow() {

        return m_show;
    }

    /**
     * Checks if the current can manage the comments.<p>
     * 
     * @return if the current can manage the comments
     */
    public boolean isUserCanManage() {

        Boolean attr = getAttrManage();
        if (attr != null) {
            if (!attr.booleanValue()) {
                return false;
            }
        }
        return isUserHasManPerm();
    }

    /**
     * Checks if the current can post new comments.<p>
     * 
     * @return if the current can post new comments
     */
    public boolean isUserCanPost() {

        if (m_config.getSecurity().isNone() || m_config.getSecurity().isNoView()) {
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
        if (m_config.getSecurity().isNoView()) {
            return isUserCanManage();
        }
        return true;
    }

    /**
     * Checks if the current user has enough permissions to manage the comments.<p>
     * 
     * @return <code>true</code> if the current user has enough permissions to manage the comments
     */
    public boolean isUserHasManPerm() {

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
                if (m_userValid == null) {
                    m_userValid = Boolean.FALSE;
                }
            }
        }
        return m_userValid.booleanValue();
    }

    /**
     * @see org.opencms.jsp.CmsJspLoginBean#login(java.lang.String, java.lang.String, java.lang.String)
     */
    public void login(String userName, String password, String projectName) {

        super.login(userName, password, projectName);
        if (isLoginSuccess()) {
            return;
        }
        CmsException exc = null;
        if (!(getLoginException() instanceof CmsDbEntryNotFoundException)) {
            // remember exception
            exc = getLoginException();
        }
        // iterate the organizational units
        Iterator itOus = getConfig().getOrgUnits().iterator();
        while (itOus.hasNext()) {
            CmsOrganizationalUnit ou = (CmsOrganizationalUnit)itOus.next();
            String ouFqn = ou.getName();
            // iterate parent ous
            while (!ouFqn.equals("")) {
                super.login(ouFqn + userName, password, projectName);
                if (isLoginSuccess()) {
                    return;
                }
                if (!(getLoginException() instanceof CmsDbEntryNotFoundException)) {
                    // remember exception
                    exc = getLoginException();
                }
                ouFqn = CmsOrganizationalUnit.getParentFqn(ouFqn);
            }
        }
        if (exc == null) {
            exc = new CmsDbEntryNotFoundException(org.opencms.db.Messages.get().container(
                org.opencms.db.Messages.ERR_UNKNOWN_USER_1,
                userName));
        }
        m_exc = exc;
    }

    /**
     * Returns the filter to read the comments.<p>
     * 
     * @param count <code>true</code> if counting comments
     * @param restrictState <code>true</code> if restricting by state
     * 
     * @return the filter to read the comments
     */
    private CmsFormDatabaseFilter getCommentFilter(boolean count, boolean restrictState) {

        CmsFormDatabaseFilter filter;
        if (count) {
            filter = CmsFormDatabaseFilter.HEADERS;
        } else {
            filter = CmsFormDatabaseFilter.DEFAULT;
        }
        filter = filter.filterFormId(CmsCommentForm.FORM_ID);
        filter = filter.filterResourceId(getResource().getStructureId());
        if (!isUserCanManage()) {
            // if not managing
            if (getConfig().isModerated()) {
                // if moderated, only show approved
                filter = filter.filterState(STATE_APPROVED);
            }
        } else {
            // if managing
            if (restrictState && (getState() != null)) {
                // show only requested comments
                filter = filter.filterState(getState().intValue());
            }
        }
        return filter;
    }

    /**
     * Returns the right configuration uri.<p>
     * 
     * @return the right configuration uri
     * 
     * @throws CmsException if something goes wrong
     */
    private String readConfigUri() throws CmsException {

        String configUri = getCmsObject().readPropertyObject(m_resource, PROPERTY_COMMENTS, true).getValue();
        if (!getCmsObject().existsResource(configUri)) {
            configUri = OpenCms.getModuleManager().getModule(CmsCommentFormHandler.MODULE_NAME).getParameter(
                CmsCommentFormHandler.MODULE_PARAM_CONFIG_PREFIX + configUri,
                configUri);
        }
        return configUri;
    }
}
