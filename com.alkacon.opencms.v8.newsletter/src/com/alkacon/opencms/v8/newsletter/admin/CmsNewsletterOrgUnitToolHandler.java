/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.newsletter/src/com/alkacon/opencms/v8/newsletter/admin/CmsNewsletterOrgUnitToolHandler.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.5 $
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

import com.alkacon.opencms.v8.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.tools.CmsDefaultToolHandler;
import org.opencms.workplace.tools.accounts.A_CmsOrgUnitDialog;

import java.util.Iterator;
import java.util.List;

/**
 * Newsletter new and delete unit tool handler that hides the tool if the current user
 * has not the needed privileges.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterOrgUnitToolHandler extends CmsDefaultToolHandler {

    /** Delete file path constant. */
    private static final String DELETE_FILE = "/system/workplace/admin/v8-newsletter/orgunit_delete.jsp";

    /**
     * @see org.opencms.workplace.tools.A_CmsToolHandler#getDisabledHelpText()
     */
    public String getDisabledHelpText() {

        if (getLink().equals(DELETE_FILE)) {
            // fot newsletter delete icon, display special disabled help text
            return "${key."
                + com.alkacon.opencms.v8.newsletter.Messages.GUI_ALK_V8_NEWSLETTER_ORGUNIT_ADMIN_TOOL_DISABLED_DELETE_HELP_0
                + "}";
        }
        return super.getDisabledHelpText();
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsToolHandler#isEnabled(org.opencms.workplace.CmsWorkplace)
     */
    public boolean isEnabled(CmsWorkplace wp) {

        if (getLink().equals(DELETE_FILE)) {
            // for delete newsletter ou, check if it is enabled
            String ouFqn = wp.getJsp().getRequest().getParameter(A_CmsOrgUnitDialog.PARAM_OUFQN);
            if (ouFqn == null) {
                ouFqn = wp.getCms().getRequestContext().getOuFqn();
            }
            try {
                List childOus = OpenCms.getOrgUnitManager().getOrganizationalUnits(wp.getCms(), ouFqn, false);
                Iterator i = childOus.iterator();
                while (i.hasNext()) {
                    CmsOrganizationalUnit unit = (CmsOrganizationalUnit)i.next();
                    if (unit.getName().endsWith(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX)) {
                        // found a newsletter OU, we can delete it
                        ouFqn = unit.getName();
                    }
                }

                if (OpenCms.getOrgUnitManager().getUsers(wp.getCms(), ouFqn, true).size() > 0) {
                    return false;
                }
                if (OpenCms.getOrgUnitManager().getGroups(wp.getCms(), ouFqn, true).size() > 0) {
                    List groups = OpenCms.getOrgUnitManager().getGroups(wp.getCms(), ouFqn, true);
                    Iterator itGroups = groups.iterator();
                    while (itGroups.hasNext()) {
                        CmsGroup group = (CmsGroup)itGroups.next();
                        if (!OpenCms.getDefaultUsers().isDefaultGroup(group.getName())) {
                            return false;
                        }
                    }
                }
                if (OpenCms.getOrgUnitManager().getOrganizationalUnits(wp.getCms(), ouFqn, true).size() > 0) {
                    return false;
                }
            } catch (CmsException e) {
                // noop
            }
        }
        return true;
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsToolHandler#isVisible(org.opencms.workplace.CmsWorkplace)
     */
    public boolean isVisible(CmsWorkplace wp) {

        // only display the new newsletter unit icon if we are not in already in a newsletter ou and we have the correct permissions
        CmsObject cms = wp.getCms();
        String ouFqn = wp.getJsp().getRequest().getParameter(A_CmsOrgUnitDialog.PARAM_OUFQN);
        if (ouFqn == null) {
            ouFqn = cms.getRequestContext().getOuFqn();
        }

        if (getLink().equals(DELETE_FILE)) {
            // for delete newsletter ou, check if it is visible
            //            if (ouFqn != null && !ouFqn.endsWith(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX)) {
            //                try {
            //                    // get the direct child OUs and check if there is already a newsletter OU
            //                    List childOus = OpenCms.getOrgUnitManager().getOrganizationalUnits(cms, ouFqn, false);
            //                    Iterator i = childOus.iterator();
            //                    while (i.hasNext()) {
            //                        CmsOrganizationalUnit unit = (CmsOrganizationalUnit)i.next();
            //                        if (unit.getName().endsWith(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX)) {
            //                            // found a newsletter OU, we can delete it
            //                            return OpenCms.getRoleManager().hasRole(cms, CmsRole.ACCOUNT_MANAGER);
            //                        }
            //                    }
            //                } catch (CmsException e) {
            //                    // error getting child OUs
            //                    return false;
            //                }
            //                return false;
            //            }
        } else {
            // for new newsletter ou, check if there is no other newsletter ou present
            if ((ouFqn != null) && (ouFqn.indexOf(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX) == -1)) {
                return OpenCms.getRoleManager().hasRole(cms, CmsRole.ACCOUNT_MANAGER);
            }
        }
        return false;
    }
}
