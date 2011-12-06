/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.newsletter/src/com/alkacon/opencms/v8/newsletter/admin/CmsShowMailinglistSubscribersList.java,v $
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

package com.alkacon.opencms.v8.newsletter.admin;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.I_CmsListDirectAction;
import org.opencms.workplace.tools.accounts.A_CmsEditUserDialog;
import org.opencms.workplace.tools.accounts.A_CmsOrgUnitDialog;
import org.opencms.workplace.tools.accounts.CmsShowGroupUsersList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Mailing list subscribers overview view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.6 $ 
 * 
 * @since 7.0.3 
 */
public class CmsShowMailinglistSubscribersList extends CmsShowGroupUsersList {

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsShowMailinglistSubscribersList(CmsJspActionElement jsp) {

        super(jsp, LIST_ID + "l");
        getList().setName(Messages.get().container(Messages.GUI_MAILINGLISTSUBSCRIBERS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsShowMailinglistSubscribersList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() throws IOException, ServletException {

        String userId = getSelectedItem().getId();

        Map params = new HashMap();
        params.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);
        params.put(A_CmsEditUserDialog.PARAM_USERID, userId);
        params.put(A_CmsOrgUnitDialog.PARAM_OUFQN, getParamOufqn());

        if (getParamListAction().equals(LIST_ACTION_EDIT)) {
            getToolManager().jspForwardTool(this, "/v8-newsletter/orgunit/subscribers/edit/user", params);
        } else {
            throwListUnsupportedActionException();
        }
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsGroupUsersList#getIconPath(org.opencms.workplace.list.CmsListItem)
     */
    public String getIconPath(CmsListItem item) {

        return "tools/v8-newsletter/buttons/v8-subscriber.png";
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

        CmsListColumnDefinition nameCol = metadata.getColumnDefinition(LIST_COLUMN_NAME);
        nameCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_EMAIL_0));
        nameCol.setWidth("100%");

        CmsListDefaultAction defAction = nameCol.getDefaultAction(LIST_ACTION_EDIT);
        defAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_NAME_0));
        defAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_DEFACTION_EDIT_HELP_0));

        I_CmsListDirectAction iconAction = iconCol.getDirectAction(LIST_ACTION_ICON);
        iconAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_INMAILINGLIST_NAME_0));
        iconAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_INMAILINGLIST_HELP_0));

        metadata.getColumnDefinition(LIST_COLUMN_FULLNAME).setVisible(false);
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
