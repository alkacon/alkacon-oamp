/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNewsletterListSend.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.7 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterMail;
import com.alkacon.opencms.newsletter.CmsNewsletterManager;
import com.alkacon.opencms.newsletter.I_CmsNewsletterMailData;

import org.opencms.db.CmsUserSettings;
import org.opencms.file.CmsGroup;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.explorer.CmsExplorer;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.list.A_CmsListExplorerDialog;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListExplorerColumn;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListOrderEnum;
import org.opencms.workplace.list.I_CmsListResourceCollector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Newsletter list to select which newsletter is sent.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3  
 */
public class CmsNewsletterListSend extends A_CmsListExplorerDialog {

    /** List column id constant. */
    public static final String LIST_ACTION_SEND = "ease";

    /** List column id constant. */
    public static final String LIST_COLUMN_DATA = "ecda";

    /** List column id constant. */
    public static final String LIST_COLUMN_SCORE = "cs";

    /** List column id constant. */
    public static final String LIST_COLUMN_SEND = "ecse";

    /** list id constant. */
    public static final String LIST_ID = "anls";

    /** Path to the list buttons. */
    public static final String PATH_BUTTONS = "tools/newsletter/buttons/";

    /** The internal collector instance. */
    private I_CmsListResourceCollector m_collector;

    /** Stores the value of the request parameter for the group id. */
    private String m_paramGroupId;

    /** Stores the value of the request parameter for the organizational unit fqn. */
    private String m_paramOufqn;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsNewsletterListSend(CmsJspActionElement jsp) {

        super(
            jsp,
            LIST_ID,
            Messages.get().container(Messages.GUI_NEWSLETTER_LIST_NAME_0),
            A_CmsListExplorerDialog.LIST_COLUMN_NAME,
            CmsListOrderEnum.ORDER_ASCENDING,
            null);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsNewsletterListSend(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListMultiActions()
     */
    public void executeListMultiActions() {

        throwListUnsupportedActionException();
    }

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterListSend.class);

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() {

        if (getParamListAction().equals(LIST_ACTION_SEND)) {
            // send the newsletter to the selected list
            String resourceName = (String)getSelectedItem().get(LIST_COLUMN_NAME);
            CmsUUID groupId = new CmsUUID(getParamGroupId());
            try {
                CmsGroup group = getCms().readGroup(groupId);
                // generate the newsletter mail and list of recipients
                I_CmsNewsletterMailData mailData = CmsNewsletterManager.getMailData(getJsp(), group, resourceName);
                String rootPath = resourceName;
                if (mailData.getContent() != null) {
                    rootPath = mailData.getContent().getFile().getRootPath();
                }
                if (mailData.isSendable()) {
                    //send the emails to the mailing list group
                    CmsNewsletterMail nlMail = new CmsNewsletterMail(
                        mailData.getEmail(),
                        mailData.getRecipients(),
                        rootPath);
                    nlMail.start();
                    getList().clear();
                }
            } catch (Exception e) {
                // should never happen
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().container(Messages.LOG_NEWSLETTER_SEND_FAILED_0), e);
                }
                throwListUnsupportedActionException();
            }
        } else {
            throwListUnsupportedActionException();
        }
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListExplorerDialog#getCollector()
     */
    public I_CmsListResourceCollector getCollector() {

        if (m_collector == null) {
            m_collector = new CmsNewsletterResourcesCollector(this);
            // set the right resource util parameters
            CmsResourceUtil resUtil = getResourceUtil();
            resUtil.setAbbrevLength(50);
            resUtil.setSiteMode(CmsResourceUtil.SITE_MODE_MATCHING);
        }
        return m_collector;
    }

    /**
     * Returns the value of the request parameter for the group id.<p>
     * 
     * @return the value of the request parameter for the group id
     */
    public String getParamGroupId() {

        return m_paramGroupId;
    }

    /**
     * Returns the organizational unit fqn parameter value.<p>
     * 
     * @return the organizational unit fqn parameter value
     */
    public String getParamOufqn() {

        return m_paramOufqn;
    }

    /**
     * Sets the value of the request parameter for the group id.<p>
     * 
     * @param paramGroupId the value of the request parameter for the group id
     */
    public void setParamGroupId(String paramGroupId) {

        m_paramGroupId = paramGroupId;
    }

    /**
     * Sets the organizational unit fqn parameter value.<p>
     * 
     * @param ouFqn the organizational unit fqn parameter value
     */
    public void setParamOufqn(String ouFqn) {

        if (ouFqn == null) {
            ouFqn = "";
        }
        m_paramOufqn = ouFqn;
    }

    /**
     * Generates the dialog starting html code.<p>
     * 
     * @return html code
     */
    protected String defaultActionHtmlStart() {

        StringBuffer result = new StringBuffer(2048);
        result.append(htmlStart(null));
        result.append(getList().listJs());
        result.append(CmsListExplorerColumn.getExplorerStyleDef());
        result.append("<script language='JavaScript'>\n");
        result.append(new CmsExplorer(getJsp()).getInitializationHeader());
        result.append("\ntop.updateWindowStore();\n");
        result.append("top.displayHead(top.win.head, 0, 1);\n}\n");
        result.append("</script>");
        result.append(bodyStart("dialog", "onload='initialize();'"));
        result.append(dialogStart());
        result.append(dialogContentStart(getParamTitle()));
        return result.toString();
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#fillDetails(java.lang.String)
     */
    protected void fillDetails(String detailId) {

        // no details
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        // add default resource bundles
        super.initMessages();
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);

        // add column with send icon
        CmsListColumnDefinition sendIconCol = new CmsListColumnDefinition(LIST_COLUMN_SEND);
        sendIconCol.setName(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_SEND_0));
        sendIconCol.setHelpText(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_SEND_HELP_0));
        sendIconCol.setWidth("20");
        sendIconCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);

        // add enabled send action
        CmsListDirectAction sendAction = new CmsListSendNewsletterAction(LIST_ACTION_SEND, LIST_COLUMN_NAME);
        //sendAction.setName(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_0));
        //sendAction.setHelpText(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_HELP_0));
        sendAction.setEnabled(true);
        //sendAction.setIconPath("tools/newsletter/buttons/newsletter_send.png");
        sendAction.setConfirmationMessage(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_CONF_0));
        sendIconCol.addDirectAction(sendAction);

        CmsListDirectAction nosendAction = new CmsListSendNewsletterAction(LIST_ACTION_SEND + "d", LIST_COLUMN_NAME);
        nosendAction.setEnabled(false);
        sendIconCol.addDirectAction(nosendAction);

        metadata.addColumn(sendIconCol, 0);

        // add column with information about the send process
        CmsListColumnDefinition newsletterCol = new CmsListExplorerColumn(LIST_COLUMN_DATA);
        newsletterCol.setName(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_DATA_0));
        newsletterCol.setHelpText(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_DATA_HELP_0));
        newsletterCol.setVisible(true);
        newsletterCol.setSorteable(false);
        metadata.addColumn(newsletterCol);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListExplorerDialog#setColumnVisibilities()
     */
    protected void setColumnVisibilities() {

        super.setColumnVisibilities();
        setColumnVisibility(LIST_COLUMN_EDIT.hashCode(), LIST_COLUMN_EDIT.hashCode());

        // set visibility of some columns to false, they are not required for the newsletter send list
        setColumnVisibility(CmsUserSettings.FILELIST_TYPE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_TYPE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_SIZE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_PERMISSIONS, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_DATE_CREATED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_USER_CREATED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_STATE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_LOCKEDBY, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_DATE_LASTMODIFIED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_DATE_RELEASED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_DATE_EXPIRED, 0);

        // set the visibility of standard columns to false
        setColumnVisibility(LIST_COLUMN_EDIT.hashCode(), 0);
        setColumnVisibility(LIST_COLUMN_TYPEICON.hashCode(), 0);
        setColumnVisibility(LIST_COLUMN_LOCKICON.hashCode(), 0);
        setColumnVisibility(LIST_COLUMN_PROJSTATEICON.hashCode(), 0);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListExplorerDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // no LIAs
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // no LMAs
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getParamOufqn())) {
            throw new Exception();
        }
    }
}
