/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsSubscribersList.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.6 $
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

import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItemDetails;
import org.opencms.workplace.list.CmsListItemDetailsFormatter;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListMultiAction;
import org.opencms.workplace.list.CmsListSearchAction;
import org.opencms.workplace.list.I_CmsListDirectAction;
import org.opencms.workplace.tools.accounts.A_CmsUsersList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Subscribers management view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.6 $ 
 * 
 * @since 7.0.3 
 */
public class CmsSubscribersList extends A_CmsUsersList {

    /** List id constant. */
    public static final String LIST_ID = "llu";

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsSubscribersList(CmsJspActionElement jsp) {

        super(jsp, LIST_ID, Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsSubscribersList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#getGroupIcon()
     */
    protected String getGroupIcon() {

        return "tools/newsletter/buttons/mailinglist.png";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#getUsers()
     */
    protected List getUsers() throws CmsException {

        return OpenCms.getOrgUnitManager().getUsers(getCms(), getParamOufqn(), false);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#readUser(java.lang.String)
     */
    protected CmsUser readUser(String name) throws CmsException {

        return getCms().readUser(name);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);

        metadata.getColumnDefinition(LIST_COLUMN_ROLE).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_LOGIN).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_EMAIL).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_LASTLOGIN).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_NAME).setVisible(false);

        CmsListColumnDefinition viewCol = metadata.getColumnDefinition(LIST_COLUMN_EDIT);
        viewCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_ICON_HELP_0));

        CmsListColumnDefinition deleteCol = metadata.getColumnDefinition(LIST_COLUMN_DELETE);
        deleteCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_DELETE_HELP_0));

        CmsListColumnDefinition mailinglistsCol = metadata.getColumnDefinition(LIST_COLUMN_GROUPS);
        mailinglistsCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_MAILINGLISTS_0));
        mailinglistsCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_MAILINGLISTS_HELP_0));

        I_CmsListDirectAction mailinglistsAction = mailinglistsCol.getDirectAction(LIST_ACTION_GROUPS);
        mailinglistsAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_MAILINGLISTS_NAME_0));
        mailinglistsAction.setHelpText(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_ACTION_MAILINGLISTS_HELP_0));

        CmsListColumnDefinition displayCol = metadata.getColumnDefinition(LIST_COLUMN_DISPLAY);
        displayCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_EMAIL_0));
        displayCol.setWidth("100%");

        CmsListDefaultAction defEditAction = displayCol.getDefaultAction(LIST_DEFACTION_EDIT);
        defEditAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_NAME_0));
        defEditAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_HELP_0));

        CmsListColumnDefinition activateCol = metadata.getColumnDefinition(LIST_COLUMN_ACTIVATE);
        activateCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_ACTIVATE_HELP_0));

        I_CmsListDirectAction activateAction = activateCol.getDirectAction(LIST_ACTION_ACTIVATE);
        activateAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_ACTIVATE_HELP_0));
        activateAction.setConfirmationMessage(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_ACTION_ACTIVATE_CONF_0));

        I_CmsListDirectAction deactivateAction = activateCol.getDirectAction(LIST_ACTION_DEACTIVATE);
        deactivateAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_DEACTIVATE_HELP_0));
        deactivateAction.setConfirmationMessage(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_ACTION_DEACTIVATE_CONF_0));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setDeleteAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setDeleteAction(CmsListColumnDefinition deleteCol) {

        CmsListDirectAction deleteAction = new CmsListDirectAction(LIST_ACTION_DELETE);
        deleteAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_DELETE_NAME_0));
        deleteAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_DELETE_HELP_0));
        deleteAction.setConfirmationMessage(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_DELETE_CONF_0));
        deleteAction.setIconPath(ICON_DELETE);
        deleteCol.addDirectAction(deleteAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setEditAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setEditAction(CmsListColumnDefinition editCol) {

        CmsListDirectAction editAction = new CmsListDirectAction(LIST_ACTION_EDIT);
        editAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_NAME_0));
        editAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_HELP_0));
        editAction.setIconPath("tools/newsletter/buttons/subscriber.png");
        editCol.addDirectAction(editAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // add mailing lists details
        CmsListItemDetails mailinglistsDetails = new CmsListItemDetails(LIST_DETAIL_GROUPS);
        mailinglistsDetails.setAtColumn(LIST_COLUMN_DISPLAY);
        mailinglistsDetails.setVisible(false);
        mailinglistsDetails.setShowActionName(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_DETAIL_SHOW_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setShowActionHelpText(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_DETAIL_SHOW_MAILINGLISTS_HELP_0));
        mailinglistsDetails.setHideActionName(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_DETAIL_HIDE_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setHideActionHelpText(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_DETAIL_HIDE_MAILINGLISTS_HELP_0));
        mailinglistsDetails.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_DETAIL_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setFormatter(new CmsListItemDetailsFormatter(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_DETAIL_MAILINGLISTS_NAME_0)));
        metadata.addItemDetails(mailinglistsDetails);

        // makes the list searchable
        CmsListSearchAction searchAction = new CmsListSearchAction(metadata.getColumnDefinition(LIST_COLUMN_DISPLAY));
        searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_NAME));
        metadata.setSearchAction(searchAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // add delete multi action
        CmsListMultiAction deleteMultiAction = new CmsListMultiAction(LIST_MACTION_DELETE);
        deleteMultiAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_DELETE_NAME_0));
        deleteMultiAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_DELETE_HELP_0));
        deleteMultiAction.setConfirmationMessage(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_MACTION_DELETE_CONF_0));
        deleteMultiAction.setIconPath(ICON_MULTI_DELETE);
        metadata.addMultiAction(deleteMultiAction);

        // add the activate user multi action
        CmsListMultiAction activateUser = new CmsListMultiAction(LIST_MACTION_ACTIVATE);
        activateUser.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_ACTIVATE_NAME_0));
        activateUser.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_ACTIVATE_HELP_0));
        activateUser.setConfirmationMessage(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_MACTION_ACTIVATE_CONF_0));
        activateUser.setIconPath(ICON_MULTI_ACTIVATE);
        metadata.addMultiAction(activateUser);

        // add the deactivate user multi action
        CmsListMultiAction deactivateUser = new CmsListMultiAction(LIST_MACTION_DEACTIVATE);
        deactivateUser.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_DEACTIVATE_NAME_0));
        deactivateUser.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_DEACTIVATE_HELP_0));
        deactivateUser.setConfirmationMessage(Messages.get().container(
            Messages.GUI_SUBSCRIBERS_LIST_MACTION_DEACTIVATE_CONF_0));
        deactivateUser.setIconPath(ICON_MULTI_DEACTIVATE);
        metadata.addMultiAction(deactivateUser);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#validateParamaters()
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