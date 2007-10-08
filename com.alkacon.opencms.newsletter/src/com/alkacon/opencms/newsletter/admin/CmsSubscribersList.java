/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsSubscribersList.java,v $
 * Date   : $Date: 2007/10/08 15:38:46 $
 * Version: $Revision: 1.1 $
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
import org.opencms.main.OpenCms;
import org.opencms.security.CmsPrincipal;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListMetadata;
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
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.0 
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

        return CmsPrincipal.filterCore(OpenCms.getOrgUnitManager().getUsers(getCms(), getParamOufqn(), false));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#readUser(java.lang.String)
     */
    protected CmsUser readUser(String name) throws CmsException {

        return getCms().readUser(name);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setDeleteAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setDeleteAction(CmsListColumnDefinition deleteCol) {

        CmsListDirectAction deleteAction = new CmsListDirectAction(LIST_ACTION_DELETE);
        deleteAction.setIconPath(ICON_DELETE);
        deleteCol.addDirectAction(deleteAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setEditAction(org.opencms.workplace.list.CmsListColumnDefinition)
     */
    protected void setEditAction(CmsListColumnDefinition editCol) {

        CmsListDirectAction editAction = new CmsListDirectAction(LIST_ACTION_EDIT);
        editAction.setIconPath(PATH_BUTTONS + "user.png");
        editCol.addDirectAction(editAction);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsUsersList#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);
        metadata.getColumnDefinition(LIST_COLUMN_EDIT).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_ROLE).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_LOGIN).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_LASTLOGIN).setVisible(false);
        metadata.getColumnDefinition(LIST_COLUMN_NAME).setVisible(false);

        CmsListColumnDefinition mailinglistsCol = metadata.getColumnDefinition(LIST_COLUMN_GROUPS);
        mailinglistsCol.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_MAILINGLISTS_0));
        mailinglistsCol.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_MAILINGLISTS_HELP_0));
        I_CmsListDirectAction mailinglistsAction = mailinglistsCol.getDirectAction(LIST_ACTION_GROUPS);
        mailinglistsAction.setName(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_ACTION_MAILINGLISTS_NAME_0));
        mailinglistsAction.setHelpText(Messages.get().container(Messages.GUI_SUBSCRIBERS_LIST_COLS_MAILINGLISTS_HELP_0));
    }
}