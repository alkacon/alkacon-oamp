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

import com.alkacon.opencms.v8.comments.util.CmsCommentsUtil;
import com.alkacon.opencms.v8.formgenerator.CmsStringTemplateErrorListener;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean;

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.workplace.CmsWorkplace;

import java.io.StringReader;
import java.text.DateFormat;

import org.apache.commons.logging.Log;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

/** Handles the stringtemplates for displaying comments.
 *  Mainly used in the involved JSPs.
 */

public class CmsCommentStringTemplateHandler {

    /** just for private use, set in constructor */
    CmsCommentsAccess m_access;

    /** Stringtemplate group read from the configured file */
    private StringTemplateGroup m_outputTemplates = null;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCommentStringTemplateHandler.class);

    /** Path to the default stringtemplate file for comments */
    public static final String VFS_PATH_DEFAULT_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + CmsCommentsUtil.MODULE_NAME
        + "/resources/templates/default-comments.st";

    /** Module parameter to change the stringtemplate file for comments */
    public static final String MODULE_PARAM_TEMPLATE_FILE = "template-comments";

    /** Constructor taking CmsCommentsAccess to read all the configuration etc.
     *  
     * @param access comments access object to access the configuration
     *  
     * */
    public CmsCommentStringTemplateHandler(CmsCommentsAccess access) {

        m_access = access;
    }

    /**
     * Returns the output template group that generates the web form HTML output.<p>
     * 
     * @return the output template group that generates the web form HTML output
     */
    public StringTemplateGroup getOutputTemplateGroup() {

        if (m_outputTemplates == null) {
            // read default output templates from module parameter
            String vfsPath = OpenCms.getModuleManager().getModule(CmsCommentsUtil.MODULE_NAME).getParameter(
                MODULE_PARAM_TEMPLATE_FILE,
                VFS_PATH_DEFAULT_TEMPLATEFILE);
            try {
                // first try to get the initialized template group from VFS cache
                String rootPath = m_access.getRequestContext().addSiteRoot(vfsPath);
                StringTemplateGroup stGroup = (StringTemplateGroup)CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().getCachedObject(
                    m_access.getCmsObject(),
                    rootPath);
                if (stGroup == null) {
                    // template group is not in cache, read the file and generate template group
                    CmsFile stFile = m_access.getCmsObject().readFile(vfsPath);
                    String stContent = new String(
                        stFile.getContents(),
                        m_access.getCmsObject().getRequestContext().getEncoding());
                    StringTemplateErrorListener errors = new CmsStringTemplateErrorListener();
                    stGroup = new StringTemplateGroup(new StringReader(stContent), DefaultTemplateLexer.class, errors);
                    // store the template group in cache
                    CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().putCachedObject(
                        m_access.getCmsObject(),
                        rootPath,
                        stGroup);
                }
                m_outputTemplates = stGroup;
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while creating web form HTML output templates from file \"" + vfsPath + "\".");
            }
        }
        return m_outputTemplates;
    }

    /**
     * Returns the template for the comment's HTML output.<p>
     * 
     * @param templateName the name of the template to return
     * 
     * @return the template for the comment's HTML output
     */
    public StringTemplate getOutputTemplate(String templateName) {

        StringTemplate result = getOutputTemplateGroup().getInstanceOf(templateName);
        if (!m_access.getRequestContext().getCurrentProject().isOnlineProject() && (result == null)) {
            // no template with the specified name found, return initialized error template
            try {
                CmsFile stFile = m_access.getCmsObject().readFile(CmsCommentsUtil.VFS_PATH_ERROR_TEMPLATEFILE);
                String stContent = new String(
                    stFile.getContents(),
                    m_access.getCmsObject().getRequestContext().getEncoding());
                result = new StringTemplate(stContent, DefaultTemplateLexer.class);
                // set the error attributes of the template
                result.setAttribute("errorheadline", "Error getting output template");
                result.setAttribute("errortext", "The desired template \"" + templateName + "\" was not found.");
                result.setAttribute("errortemplatenames", getOutputTemplateGroup().getTemplateNames());
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while getting error output template from file \""
                    + CmsCommentsUtil.VFS_PATH_ERROR_TEMPLATEFILE
                    + "\".");
            }
        }
        return result;
    }

    /** Just to get a suitable object for a stringtemplate attribute */
    interface MessageType {

        /** Should return true if comments are moderated, otherwise false 
         * @return flag indicating if comments are currently managed and moderated */
        public boolean getIsManageModerated();

        /** Should return true if comments are moderated, otherwise false 
         * @return flag indicating if comments are currently managed and unmoderated */
        public boolean getIsManageUnmoderated();

        /** Should return true if a login facility should be provided, otherwise false.
         * @return flag indicating if a login facility should be given
         */
        public boolean getIsLogin();

        /**
         * @return flag indicating if user can post (if not manager)
         */
        public boolean getIsPost();

        /**
         * @return flag indicating if user can only view (if other permissions exist, it false should be returned)
         */
        public boolean getIsView();
    }

    /**
     * @return HTML generated by the "header" stringtemplate
     */
    public String buildHeaderHtml() {

        // create the header

        MessageType messageType = new MessageType() {

            public boolean getIsManageModerated() {

                return m_access.isUserCanManage() && m_access.getConfig().isModerated();
            }

            public boolean getIsManageUnmoderated() {

                return m_access.isUserCanManage() && !(m_access.getConfig().isModerated());
            }

            public boolean getIsLogin() {

                return m_access.isGuestUser() && m_access.getConfig().isOfferLogin();
            }

            public boolean getIsPost() {

                return !(m_access.isUserCanManage()) && m_access.isUserCanPost();
            }

            public boolean getIsView() {

                return !(getIsPost()) && m_access.isUserCanView();
            }
        };
        String message;
        String title = "";
        String[] commentsCount = new String[] {Integer.toString(m_access.getCountComments())};
        if (messageType.getIsManageModerated()) {
            message = m_access.getMessage(
                "header.user.manage.2",
                new String[] {
                    Integer.toString(m_access.getCountApprovedComments()),
                    Integer.toString(m_access.getCountNewComments())});
        } else if (messageType.getIsManageUnmoderated()) {
            message = m_access.getMessage("header.user.manage.1", commentsCount);
        } else if (messageType.getIsLogin()) {
            message = m_access.getMessage("header.user.login.1", commentsCount);
            title = m_access.getMessage("login.message.title");
        } else if (messageType.getIsPost()) {
            message = m_access.getMessage("header.user.post.1", commentsCount);
        } else if (messageType.getIsView()) {
            message = m_access.getMessage("header.user.read.1", commentsCount);
        } else {
            message = ""; // Should not happen
        }

        StringTemplate sTemplate = getOutputTemplate("header");
        // set the necessary attributes to use in the string template
        sTemplate.setAttribute("message", message);
        sTemplate.setAttribute("buttonTitle", title);
        sTemplate.setAttribute("messageType", messageType);
        return sTemplate.toString();
    }

    /**
     * @param title The title attribute passed to the stringtemplate
     * @return HTML generated by the "headline" stringtemplate
     */
    public String buildHeadlineHtml(final String title) {

        StringTemplate sTemplate = getOutputTemplate("headline");
        sTemplate.setAttribute("title", title);
        return sTemplate.toString();
    }

    /**
     * @return HTML generated by the "post_options" stringtemplate
     */
    public String buildPostOptionsHtml() {

        StringTemplate sTemplate = getOutputTemplate("post_options");
        boolean postOrLogin = m_access.isUserCanPost()
            || (m_access.isGuestUser() && m_access.getConfig().isOfferLogin());
        sTemplate.setAttribute("canPostOrOfferLogin", Boolean.valueOf(postOrLogin));
        sTemplate.setAttribute("canPost", Boolean.valueOf(m_access.isUserCanPost()));
        sTemplate.setAttribute("postButtonTitle", m_access.getMessage("form.message.post"));
        sTemplate.setAttribute("postButtonMessage", m_access.getMessage("post.0"));
        sTemplate.setAttribute("loginButtonTitle", m_access.getMessage("login.message.title"));
        sTemplate.setAttribute("loginButtonMessage", m_access.getMessage("post.user.login.0"));
        return sTemplate.toString();
    }

    /**
     * @return HTML generated by the "login" stringtemplate
     */
    public String buildLoginHtml() {

        String hiddenFields = "            <input type=\"hidden\" name=\"action\" value=\"login\" />\n"
            + "            <input type=\"hidden\" name=\"cmturi\" value=\""
            + m_access.getUri()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"cmtminimized\" value=\""
            + m_access.getConfig().isMinimized()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"cmtlist\" value=\""
            + m_access.getConfig().getList()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"cmtsecurity\" value=\""
            + m_access.getConfig().getSecurity().getMode()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"cmtformid\" value=\""
            + m_access.getConfig().getFormId()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"cmtallowreplies\" value=\""
            + m_access.getConfig().getAllowReplies()
            + "\" />\n"
            + "            <input type=\"hidden\" name=\"__locale\" value=\""
            + m_access.getRequestContext().getLocale().toString()
            + "\" />\n";
        StringTemplate sTemplate = getOutputTemplate("login");
        sTemplate.setAttribute("hiddenFields", hiddenFields);
        sTemplate.setAttribute("messageTitle", m_access.getMessage("login.message.title"));
        sTemplate.setAttribute("messageEnterData", m_access.getMessage("login.message.enterdata"));
        sTemplate.setAttribute("messageFailed", m_access.getMessage("login.message.failed"));
        sTemplate.setAttribute("userNameLabel", m_access.getMessage("login.label.username"));
        sTemplate.setAttribute("passwordLabel", m_access.getMessage("login.label.password"));
        sTemplate.setAttribute("loginButtonMessage", m_access.getMessage("login.label.login"));
        sTemplate.setAttribute("cancelButtonMessage", m_access.getMessage("login.label.cancel"));
        return sTemplate.toString();
    }

    /** Just a suitable object for a stringtemplate attribute */
    interface State {

        /**
         * @return flag indicating if all comments should be shown
         */
        public boolean getIsAll();

        /**
         * @return flag indicating if only new comments should be shown
         */
        public boolean getIsNew();

        /**
         * @return flag indicating if only blocked comments should be shown
         */
        public boolean getIsBlocked();

        /**
         * @return flag indicating if only approved comments should be shown
         */
        public boolean getIsApproved();
    }

    /**
     * @return a State object reflecting the state returned by m_access.getState()
     */
    State generateState() {

        State state = new State() {

            public boolean getIsAll() {

                return (m_access.getState() == null) || (m_access.getState().intValue() == -1);
            }

            public boolean getIsNew() {

                return m_access.getState() == Integer.valueOf(0);
            }

            public boolean getIsBlocked() {

                return m_access.getState() == Integer.valueOf(2);
            }

            public boolean getIsApproved() {

                return m_access.getState() == Integer.valueOf(1);
            }
        };
        return state;
    }

    /**
     * @return HTML generated by the "pagination" stringtemplate
     */
    public String buildPaginationHtml() {

        if (!(m_access.isNeedFilter() || m_access.isNeedPagination())) {
            return "";
        }

        //String stateNum = m_access.getState().toString();
        //String commentCount = Integer.toString(m_access.getCountStateComments());
        //String pageNum = Integer.toString(m_access.getPage());
        //String list = m_access.getConfig().getList();
        StringTemplate sTemplate = getOutputTemplate("pagination");
        sTemplate.setAttribute("state", generateState());
        sTemplate.setAttribute("stateNum", m_access.getState());
        sTemplate.setAttribute("commentCount", m_access.getCountStateComments());
        sTemplate.setAttribute("pageNum", m_access.getPage());
        sTemplate.setAttribute("list", m_access.getConfig().getList());
        sTemplate.setAttribute("needFilter", Boolean.valueOf(m_access.isNeedFilter()));
        sTemplate.setAttribute("needPagination", Boolean.valueOf(m_access.isNeedPagination()));
        sTemplate.setAttribute("messageAll", m_access.getMessage("pagination.all"));
        sTemplate.setAttribute("messageNew", m_access.getMessage("pagination.new"));
        sTemplate.setAttribute("messageBlocked", m_access.getMessage("pagination.blocked"));
        sTemplate.setAttribute("messageApproved", m_access.getMessage("pagination.approved"));
        return sTemplate.toString();
    }

    /**
     * @param comment The comment to which the reply-option should be attached
     * @return HTML generated by the "replies_option" stringtemplate
     */
    public String buildRepliesOptionHtml(final CmsFormDataBean comment) {

        if (!m_access.getConfig().isAllowReplies()) {
            return "";
        }

        StringTemplate sTemplate = getOutputTemplate("repliesOption");
        CmsRepliesAccessBean replies = new CmsRepliesAccessBean();
        int replyCount = replies.getCountRepliesForComment(comment.getEntryId());
        sTemplate.setAttribute("noReplies", Boolean.valueOf(replyCount == 0));
        String messageShowRepliesButton;
        if (replyCount == 1) {
            messageShowRepliesButton = "1 " + m_access.getMessage("oneReply");
        } else {
            messageShowRepliesButton = Integer.toString(replyCount) + " " + m_access.getMessage("manyReplies");
        }
        sTemplate.setAttribute("messageShowRepliesButton", messageShowRepliesButton);
        sTemplate.setAttribute("messageReplyButton", m_access.getMessage("doReply"));
        sTemplate.setAttribute("messagePost", m_access.getMessage("form.message.post"));
        String link = OpenCms.getLinkManager().substituteLink(
            m_access.getCmsObject(),
            "/system/modules/com.alkacon.opencms.v8.comments/elements/comment_form.jsp"
                + "?cmtparentid="
                + comment.getEntryId()
                + "&cmturi="
                + m_access.getUri()
                + "&cmtminimized="
                + m_access.getConfig().isMinimized()
                + "&cmtlist="
                + m_access.getConfig().getList()
                + "&cmtsecurity="
                + m_access.getConfig().getSecurity()
                + "&configUri="
                + m_access.getConfig().getConfigUri()
                + "&cmtformid="
                + m_access.getConfig().getFormId()
                + "&__locale="
                + m_access.getRequestContext().getLocale());
        sTemplate.setAttribute("linkPost", link);
        sTemplate.setAttribute("commentId", comment.getEntryId());
        sTemplate.setAttribute("userCanManage", Boolean.valueOf(m_access.isUserCanManage()));
        sTemplate.setAttribute("userCanPost", Boolean.valueOf(m_access.isUserCanPost()));
        return sTemplate.toString();
    }

    /**
     * @param state state of the comment to highlight
     * @param boxColor the color given as element setting
     * @param isReply is the comment a reply?
     * @return String rendered by the chosen stringtemplate, e.g., by "colorClassAll"
     */
    protected String getColorClass(final int state, final String boxColor, final boolean isReply) {

        StringTemplate colorTemplate;
        if (isReply) {
            colorTemplate = getOutputTemplate("colorClassReply");
        } else if (!(m_access.getConfig().isModerated() && m_access.isUserCanManage())) {
            colorTemplate = getOutputTemplate("colorClassAll");
        } else {
            switch (state) {
                case 0:
                    colorTemplate = getOutputTemplate("colorClassNew");
                    break;
                case 1:
                    colorTemplate = getOutputTemplate("colorClassApproved");
                    break;
                case 2:
                    colorTemplate = getOutputTemplate("colorClassBlocked");
                    break;
                default:
                    colorTemplate = getOutputTemplate("colorClassAll");
                    break;
            }
        }
        colorTemplate.setAttribute("boxColor", CmsEncoder.escapeXml(boxColor));
        return colorTemplate.toString();
    }

    /** Helper to build HTML from the stringtemplates "reply_manager" and "manager"
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @param isReply flag, indicating if the comment is a reply
     * @return The HTML rendered by the stringtemplate "reply_manager" or "manager" (depending on isReply)
     */
    protected String buildManagerHtmlHelper(final CmsFormDataBean comment, final String boxColor, final boolean isReply) {

        StringTemplate sTemplate = isReply ? getOutputTemplate("reply_manager") : getOutputTemplate("manager");
        sTemplate.setAttribute("commentId", comment.getEntryId());
        sTemplate.setAttribute("commentSubject", CmsEncoder.escapeXml(comment.getFieldValue("subject")));
        sTemplate.setAttribute("commentContent", CmsCommentsUtil.secureContent(comment.getFieldValue("comment")));
        sTemplate.setAttribute("stateNum", m_access.getState());
        String colorClass;
        if (!isReply) {
            sTemplate.setAttribute(
                "messageApprove",
                CmsEncoder.escapeXml(m_access.getMessage("comment.manager.approve")));
            sTemplate.setAttribute("messageBlock", CmsEncoder.escapeXml(m_access.getMessage("comment.manager.block")));
            sTemplate.setAttribute("state", generateState());
            sTemplate.setAttribute("isModerated", Boolean.valueOf(m_access.getConfig().isModerated()));
            sTemplate.setAttribute("repliesOption", buildRepliesOptionHtml(comment));
            colorClass = getColorClass(comment.getState(), boxColor, isReply);
        } else {
            colorClass = getColorClass(comment.getState(), boxColor, isReply);

        }
        sTemplate.setAttribute("colorClass", colorClass);
        sTemplate.setAttribute("messageDeleteConf", m_access.getMessage("comment.manager.delete.conf"));
        sTemplate.setAttribute("messageDelete", m_access.getMessage("comment.manager.delete"));
        String[] params = new String[] {
            CmsEncoder.escapeXml(comment.getFieldValue("name")),
            CmsEncoder.escapeXml(CmsCommentsUtil.formatDateTime(
                comment.getDateCreated(),
                m_access.getRequestContext().getLocale(),
                DateFormat.LONG,
                DateFormat.SHORT))};
        sTemplate.setAttribute("messageHeader", m_access.getMessage("comment.header.view.2", params));
        params = new String[] {CmsEncoder.escapeXml(comment.getFieldValue("username"))};
        sTemplate.setAttribute("messageUsername", m_access.getMessage("comment.manager.username.1", params));
        params = new String[] {CmsEncoder.escapeXml(comment.getFieldValue("email"))};
        sTemplate.setAttribute("messageEmail", m_access.getMessage("comment.manager.email.1", params));
        params = new String[] {CmsEncoder.escapeXml(comment.getFieldValue("ipaddress"))};
        sTemplate.setAttribute("messageIpAddress", m_access.getMessage("comment.manager.ipaddress.1", params));
        params = new String[] {CmsEncoder.escapeXml(m_access.getCountByAuthor().get(comment.getFieldValue("username")).toString())};
        sTemplate.setAttribute("messageCount", m_access.getMessage("comment.manager.count.1", params));
        return sTemplate.toString();
    }

    /**
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @return The HTML rendered by the stringtemplate "manager"
     */
    public String buildManagerHtml(final CmsFormDataBean comment, final String boxColor) {

        return buildManagerHtmlHelper(comment, boxColor, false);
    }

    /**
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @return The HTML rendered by the stringtemplate "reply_manager"
     */
    public String buildReplyManagerHtml(final CmsFormDataBean comment, final String boxColor) {

        return buildManagerHtmlHelper(comment, boxColor, true);
    }

    /**
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @param isReply flag, indicating if the comment to show is a reply
     * @return The HTML rendered by the stringtemplate "view" or "reply_view" (depending on isReply)
     */
    public String buildViewHtmlHelper(final CmsFormDataBean comment, final String boxColor, final boolean isReply) {

        StringTemplate sTemplate = isReply ? getOutputTemplate("reply_view") : getOutputTemplate("view");
        sTemplate.setAttribute("colorClass", getColorClass(comment.getState(), boxColor, isReply));
        sTemplate.setAttribute("commentSubject", CmsEncoder.escapeXml(comment.getFieldValue("subject")));
        String[] params = new String[] {
            CmsEncoder.escapeXml(comment.getFieldValue("name")),
            CmsEncoder.escapeXml(CmsCommentsUtil.formatDateTime(
                comment.getDateCreated(),
                m_access.getRequestContext().getLocale(),
                DateFormat.LONG,
                DateFormat.SHORT))};
        sTemplate.setAttribute("header", m_access.getMessage("comment.header.view.2", params));
        sTemplate.setAttribute("commentContent", CmsCommentsUtil.secureContent(comment.getFieldValue("comment")));
        if (!isReply) {
            sTemplate.setAttribute("repliesOption", buildRepliesOptionHtml(comment));
        }
        return sTemplate.toString();
    }

    /**
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @return The HTML rendered by the stringtemplate "view"
     */
    public String buildViewHtml(final CmsFormDataBean comment, final String boxColor) {

        return buildViewHtmlHelper(comment, boxColor, false);
    }

    /**
     * @param comment the comment to render
     * @param boxColor the color given as element setting
     * @return The HTML rendered by the stringtemplate "reply_view"
     */
    public String buildReplyViewHtml(final CmsFormDataBean comment, final String boxColor) {

        return buildViewHtmlHelper(comment, boxColor, true);

    }
}
