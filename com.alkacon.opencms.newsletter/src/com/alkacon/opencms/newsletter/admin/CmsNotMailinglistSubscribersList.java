/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNotMailinglistSubscribersList.java,v $
 * Date   : $Date: 2007/11/19 15:49:15 $
 * Version: $Revision: 1.5 $
 *
 * This file is part of the Alkacon OpenCms Add-On Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.newsletter.admin;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListMultiAction;
import org.opencms.workplace.list.I_CmsListDirectAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Not mailing list subscribers view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.5 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNotMailinglistSubscribersList extends org.opencms.workplace.tools.accounts.CmsNotGroupUsersList {

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsNotMailinglistSubscribersList(CmsJspActionElement jsp) {

        super(jsp, LIST_ID + "l");
        getList().setName(Messages.get().container(Messages.GUI_NOTMAILINGLISTSUBSCRIBERS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsNotMailinglistSubscribersList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#initializeDetail(java.lang.String)
     */
    protected void initializeDetail(String detailId) {

        super.initializeDetail(detailId);
        if (detailId.equals(LIST_DETAIL_OTHEROU)) {
            getList().getMetadata().getColumnDefinition(LIST_COLUMN_ORGUNIT).setVisible(false);
            getList().getMetadata().getColumnDefinition(LIST_COLUMN_ORGUNIT).setPrintable(false);
        }
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
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupUsersList#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);

        CmsListColumnDefinition iconCol = metadata.getColumnDefinition(LIST_COLUMN_ICON);
        iconCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_ICON_0));
        iconCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_ICON_HELP_0));

        I_CmsListDirectAction iconAction = iconCol.getDirectAction(LIST_ACTION_ICON);
        iconAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_AVAILABLE_NAME_0));
        iconAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_AVAILABLE_HELP_0));
        iconAction.setIconPath(CmsSubscriberMailinglistsList.PATH_BUTTONS + "subscriber.png");

        CmsListColumnDefinition nameCol = metadata.getColumnDefinition(LIST_COLUMN_NAME);
        nameCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_EMAIL_0));
        nameCol.setWidth("100%");

        CmsListDefaultAction addAction = nameCol.getDefaultAction(LIST_DEFACTION_ADD);
        addAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_ADD_NAME_0));
        addAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_ADD_HELP_0));

        I_CmsListDirectAction stateAction = metadata.getColumnDefinition(LIST_COLUMN_STATE).getDirectAction(
            LIST_ACTION_ADD);
        stateAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_ADD_NAME_0));
        stateAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_ADD_HELP_0));

        metadata.getColumnDefinition(LIST_COLUMN_FULLNAME).setVisible(false);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsNotGroupUsersList#setIconAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setIconAction(CmsListColumnDefinition iconCol) {

        CmsListDirectAction iconAction = new CmsListDefaultAction(LIST_ACTION_ICON);
        iconAction.setEnabled(false);
        iconCol.addDirectAction(iconAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsNotGroupUsersList#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // no independent actions needed
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsNotGroupUsersList#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        super.setMultiActions(metadata);

        CmsListMultiAction addMultiAction = metadata.getMultiAction(LIST_MACTION_ADD);
        addMultiAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_ADD_NAME_0));
        addMultiAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_ADD_HELP_0));
        addMultiAction.setConfirmationMessage(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_MACTION_ADD_CONF_0));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupUsersList#validateParamaters()
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
