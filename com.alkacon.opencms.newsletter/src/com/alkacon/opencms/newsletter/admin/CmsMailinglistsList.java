/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsMailinglistsList.java,v $
 * Date   : $Date: 2007/10/26 13:01:14 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter.admin;

import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListItemDetails;
import org.opencms.workplace.list.CmsListItemDetailsFormatter;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListMultiAction;
import org.opencms.workplace.tools.accounts.A_CmsEditGroupDialog;
import org.opencms.workplace.tools.accounts.A_CmsGroupsList;
import org.opencms.workplace.tools.accounts.A_CmsOrgUnitDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Mailing list management view.<p>
 * 
 * @author Michael Moossen  
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 6.0.0 
 */
public class CmsMailinglistsList extends A_CmsGroupsList {

    /** list action id constant. */
    public static final String LIST_ACTION_SEND = "ase";

    /** list column id constant. */
    public static final String LIST_COLUMN_SEND = "cse";

    /** list id constant. */
    public static final String LIST_ID = "lgl";

    /** Path to the list buttons. */
    public static final String PATH_BUTTONS = "tools/newsletter/buttons/";

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsMailinglistsList(CmsJspActionElement jsp) {

        super(jsp, LIST_ID, Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsMailinglistsList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() throws IOException, ServletException, CmsRuntimeException {

        if (getParamListAction().equals(LIST_ACTION_SEND)) {
            // forward to the send newsletter screen
            String groupId = getSelectedItem().getId();
            String groupName = getSelectedItem().get(LIST_COLUMN_NAME).toString();

            Map params = new HashMap();
            params.put(A_CmsEditGroupDialog.PARAM_GROUPID, groupId);
            params.put(A_CmsOrgUnitDialog.PARAM_OUFQN, getParamOufqn());
            params.put(A_CmsEditGroupDialog.PARAM_GROUPNAME, groupName);
            // set action parameter to initial dialog call
            params.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);
            getToolManager().jspForwardTool(this, getCurrentToolPath() + "/edit/send", params);
        }
        // execute the super actions
        super.executeListSingleActions();
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#fillDetails(java.lang.String)
     */
    protected void fillDetails(String detailId) {

        // get content
        List groups = getList().getAllContent();
        Iterator itGroups = groups.iterator();
        while (itGroups.hasNext()) {
            CmsListItem item = (CmsListItem)itGroups.next();
            String groupName = item.get(LIST_COLUMN_NAME).toString();
            StringBuffer html = new StringBuffer(512);
            try {
                if (detailId.equals(LIST_DETAIL_USERS)) {
                    // users
                    List users = getCms().getUsersOfGroup(groupName, true);
                    Iterator itUsers = users.iterator();
                    while (itUsers.hasNext()) {
                        CmsUser user = (CmsUser)itUsers.next();
                        html.append(user.getSimpleName());
                        if (itUsers.hasNext()) {
                            html.append("<br>");
                        }
                        html.append("\n");
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                // ignore
            }
            item.set(detailId, html.toString());
        }
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#getGroups()
     */
    protected List getGroups() throws CmsException {

        return OpenCms.getOrgUnitManager().getGroups(getCms(), getParamOufqn(), false);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        CmsListColumnDefinition sendCol = new CmsListColumnDefinition(LIST_COLUMN_SEND);
        sendCol.setName(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_SEND_0));
        sendCol.setHelpText(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_COLS_SEND_HELP_0));
        CmsListDirectAction sendAction = new CmsListDirectAction(LIST_ACTION_SEND);
        sendAction.setName(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_0));
        sendAction.setHelpText(Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_HELP_0));
        sendAction.setIconPath(PATH_BUTTONS + "newsletter_send.png");
        sendCol.addDirectAction(sendAction);
        metadata.addColumn(sendCol);

        super.setColumns(metadata);

        CmsListColumnDefinition editCol = metadata.getColumnDefinition(LIST_COLUMN_EDIT);
        editCol.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_EDIT_0));
        editCol.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_EDIT_HELP_0));

        CmsListColumnDefinition usersCol = metadata.getColumnDefinition(LIST_COLUMN_USERS);
        usersCol.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_SUBSCRIBERS_0));
        usersCol.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_SUBSCRIBERS_HELP_0));
        CmsListDirectAction usersAction = (CmsListDirectAction)usersCol.getDirectAction(LIST_ACTION_USERS);
        usersAction.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_SUBSCRIBERS_NAME_0));
        usersAction.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_SUBSCRIBERS_HELP_0));
        usersAction.setIconPath(PATH_BUTTONS + "subscriber.png");

        metadata.getColumnDefinition(LIST_COLUMN_ACTIVATE).setVisible(false);

        CmsListColumnDefinition deleteCol = metadata.getColumnDefinition(LIST_COLUMN_DELETE);
        deleteCol.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_DELETE_0));
        deleteCol.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_COLS_DELETE_HELP_0));

        CmsListColumnDefinition nameCol = metadata.getColumnDefinition(LIST_COLUMN_DISPLAY);
        CmsListDefaultAction defAction = nameCol.getDefaultAction(LIST_DEFACTION_EDIT);
        defAction.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_DEFACTION_EDIT_NAME_0));
        defAction.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_DEFACTION_EDIT_HELP_0));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#setDeleteAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setDeleteAction(CmsListColumnDefinition deleteCol) {

        CmsListDirectAction deleteAction = new CmsListDirectAction(LIST_ACTION_DELETE);
        deleteAction.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_DELETE_NAME_0));
        deleteAction.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_DELETE_HELP_0));
        deleteAction.setIconPath(ICON_DELETE);
        deleteCol.addDirectAction(deleteAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#setEditAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setEditAction(CmsListColumnDefinition editCol) {

        CmsListDirectAction editAction = new CmsListDirectAction(LIST_ACTION_EDIT);
        editAction.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_EDIT_NAME_0));
        editAction.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_ACTION_EDIT_HELP_0));
        editAction.setIconPath(PATH_BUTTONS + "mailinglist.png");
        editCol.addDirectAction(editAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // add subscribers details
        CmsListItemDetails subscribersDetails = new CmsListItemDetails(LIST_DETAIL_USERS);
        subscribersDetails.setAtColumn(LIST_COLUMN_DISPLAY);
        subscribersDetails.setVisible(false);
        subscribersDetails.setShowActionName(Messages.get().container(
            Messages.GUI_MAILINGLISTS_DETAIL_SHOW_SUBSCRIBERS_NAME_0));
        subscribersDetails.setShowActionHelpText(Messages.get().container(
            Messages.GUI_MAILINGLISTS_DETAIL_SHOW_SUBSCRIBERS_HELP_0));
        subscribersDetails.setHideActionName(Messages.get().container(
            Messages.GUI_MAILINGLISTS_DETAIL_HIDE_SUBSCRIBERS_NAME_0));
        subscribersDetails.setHideActionHelpText(Messages.get().container(
            Messages.GUI_MAILINGLISTS_DETAIL_HIDE_SUBSCRIBERS_HELP_0));
        subscribersDetails.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_DETAIL_SUBSCRIBERS_NAME_0));
        subscribersDetails.setFormatter(new CmsListItemDetailsFormatter(Messages.get().container(
            Messages.GUI_MAILINGLISTS_DETAIL_SUBSCRIBERS_NAME_0)));
        metadata.addItemDetails(subscribersDetails);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // add delete multi action
        CmsListMultiAction deleteMultiAction = new CmsListMultiAction(LIST_MACTION_DELETE);
        deleteMultiAction.setName(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_MACTION_DELETE_NAME_0));
        deleteMultiAction.setHelpText(Messages.get().container(Messages.GUI_MAILINGLISTS_LIST_MACTION_DELETE_HELP_0));
        deleteMultiAction.setConfirmationMessage(Messages.get().container(
            Messages.GUI_MAILINGLISTS_LIST_MACTION_DELETE_CONF_0));
        deleteMultiAction.setIconPath(ICON_MULTI_DELETE);
        metadata.addMultiAction(deleteMultiAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupsList#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        super.validateParamaters();
        // this is to prevent the switch to the root ou 
        // if the oufqn param get lost (by reloading for example)
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getParamOufqn())) {
            throw new Exception();
        }
    }
}
