/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsOrgUnitsAdminList.java,v $
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

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItemDetails;
import org.opencms.workplace.list.CmsListItemDetailsFormatter;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListSearchAction;
import org.opencms.workplace.list.I_CmsListDirectAction;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Newsletter organization units management view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.7 $ 
 * 
 * @since 7.0.3 
 */
public class CmsOrgUnitsAdminList extends org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList {

    /** Parth to the newsletter buttons. */
    public static final String PATH_NL_BUTTONS = "tools/newsletter/buttons/";

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsOrgUnitsAdminList(CmsJspActionElement jsp) {

        super(jsp);
        getList().setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsOrgUnitsAdminList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsOrgUnitsList#getGroupIcon()
     */
    public String getGroupIcon() {

        return PATH_NL_BUTTONS + "mailinglist.png";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#getOverviewIcon()
     */
    public String getOverviewIcon() {

        return PATH_NL_BUTTONS + "newsletter_overview.png";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsOrgUnitsList#getUserIcon()
     */
    public String getUserIcon() {

        return PATH_NL_BUTTONS + "subscriber.png";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#hasMoreAdminOUs()
     */
    public boolean hasMoreAdminOUs() throws CmsException {

        boolean result = super.hasMoreAdminOUs();

        // delete default group "Users"
        Iterator it = CmsNewsletterManager.getOrgUnits(getCms()).iterator();
        while (it.hasNext()) {
            CmsOrganizationalUnit ou = (CmsOrganizationalUnit)it.next();
            String groupName = ou.getName() + OpenCms.getDefaultUsers().getGroupUsers();
            try {
                getCms().readGroup(groupName);
                // in order to delete a group, we have to switch to an offline project
                CmsObject cms = OpenCms.initCmsObject(getCms());
                String projectName = OpenCms.getModuleManager().getModule(CmsNewsletterManager.MODULE_NAME).getParameter(
                    CmsNewsletterManager.MODULE_PARAM_PROJECT_NAME,
                    "Offline");
                CmsProject project = cms.readProject(projectName);
                cms.getRequestContext().setCurrentProject(project);
                getCms().deleteGroup(groupName);
            } catch (Exception e) {
                // ok
            }
        }
        return result;
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#getForwardToolPath()
     */
    protected String getForwardToolPath() {

        return "/newsletter/orgunit";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#getGroupsToolPath()
     */
    protected String getGroupsToolPath() {

        return getCurrentToolPath() + "/orgunit/mailinglists";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsOrgUnitsList#getOrgUnits()
     */
    protected List getOrgUnits() throws CmsException {

        return CmsNewsletterManager.getOrgUnits(getCms());
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#getUsersToolPath()
     */
    protected String getUsersToolPath() {

        return getCurrentToolPath() + "/orgunit/subscribers";
    }

    /**
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitsAdminList#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);

        CmsListColumnDefinition overviewCol = metadata.getColumnDefinition(LIST_COLUMN_OVERVIEW);
        overviewCol.setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_OVERVIEW_0));
        overviewCol.setHelpText(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_OVERVIEW_HELP_0));
        overviewCol.removeDirectAction(LIST_ACTION_OVERVIEW);
        I_CmsListDirectAction overviewAction = new CmsListDirectAction(LIST_ACTION_OVERVIEW);
        overviewAction.setName(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_ACTION_OVERVIEW_NAME_0));
        overviewAction.setHelpText(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_OVERVIEW_HELP_0));
        overviewAction.setIconPath(getOverviewIcon());
        overviewCol.addDirectAction(overviewAction);

        CmsListColumnDefinition subscribersCol = metadata.getColumnDefinition(LIST_COLUMN_USER);
        subscribersCol.setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_USER_0));
        subscribersCol.setHelpText(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_USER_HELP_0));
        I_CmsListDirectAction subscribersAction = subscribersCol.getDirectAction(LIST_ACTION_USER);
        subscribersAction.setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_ACTION_USER_NAME_0));
        subscribersAction.setHelpText(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_USER_HELP_0));

        CmsListColumnDefinition mailinglistsCol = metadata.getColumnDefinition(LIST_COLUMN_GROUP);
        mailinglistsCol.setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_GROUP_0));
        mailinglistsCol.setHelpText(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_GROUP_HELP_0));
        I_CmsListDirectAction mailinglistsAction = mailinglistsCol.getDirectAction(LIST_ACTION_GROUP);
        mailinglistsAction.setName(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_ACTION_GROUP_NAME_0));
        mailinglistsAction.setHelpText(Messages.get().container(
            Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_GROUP_HELP_0));

        CmsListColumnDefinition descriptionCol = metadata.getColumnDefinition(LIST_COLUMN_DESCRIPTION);
        CmsListDefaultAction defAction = descriptionCol.getDefaultAction(LIST_DEFACTION_OVERVIEW);
        defAction.setName(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_DEFACTION_OVERVIEW_NAME_0));
        defAction.setHelpText(Messages.get().container(Messages.GUI_ALK_NEWSLETTER_ORGUNITS_LIST_COLS_OVERVIEW_HELP_0));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsOrgUnitsList#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // add mailing lists details
        CmsListItemDetails mailinglistsDetails = new CmsListItemDetails(LIST_DETAIL_GROUPS);
        mailinglistsDetails.setAtColumn(LIST_COLUMN_DESCRIPTION);
        mailinglistsDetails.setVisible(false);
        mailinglistsDetails.setShowActionName(Messages.get().container(
            Messages.GUI_ORGUNITS_DETAIL_SHOW_ALK_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setShowActionHelpText(Messages.get().container(
            Messages.GUI_ORGUNITS_DETAIL_SHOW_ALK_MAILINGLISTS_HELP_0));
        mailinglistsDetails.setHideActionName(Messages.get().container(
            Messages.GUI_ORGUNITS_DETAIL_HIDE_ALK_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setHideActionHelpText(Messages.get().container(
            Messages.GUI_ORGUNITS_DETAIL_HIDE_ALK_MAILINGLISTS_HELP_0));
        mailinglistsDetails.setName(Messages.get().container(Messages.GUI_ORGUNITS_DETAIL_ALK_MAILINGLISTS_NAME_0));
        mailinglistsDetails.setFormatter(new CmsListItemDetailsFormatter(Messages.get().container(
            Messages.GUI_ORGUNITS_DETAIL_ALK_MAILINGLISTS_NAME_0)));
        metadata.addItemDetails(mailinglistsDetails);

        // makes the list searchable
        CmsListSearchAction searchAction = new CmsListSearchAction(metadata.getColumnDefinition(LIST_COLUMN_NAME));
        searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_DESCRIPTION));
        metadata.setSearchAction(searchAction);
    }

}
