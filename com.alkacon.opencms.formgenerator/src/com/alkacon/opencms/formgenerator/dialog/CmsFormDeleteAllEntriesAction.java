/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormDeleteAllEntriesAction.java,v $
 * Date   : $Date: 2010/05/21 13:49:31 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator.dialog;

import org.opencms.i18n.CmsMessageContainer;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRole;
import org.opencms.workplace.list.CmsListDirectAction;

/**
 * List action to delete all entries of a form.<p>
 * 
 * This special handler is needed because only users with the role {@link CmsRole#DATABASE_MANAGER} should be able to
 * perform this action.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsFormDeleteAllEntriesAction extends CmsListDirectAction {

    /**
     * Default constructor.<p>
     * 
     * @param id unique id
     */
    public CmsFormDeleteAllEntriesAction(String id) {

        super(id);
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getHelpText()
     */
    @Override
    public CmsMessageContainer getHelpText() {

        if (!OpenCms.getRoleManager().hasRole(getWp().getCms(), CmsRole.DATABASE_MANAGER)) {
            return (Messages.get().container(Messages.GUI_ACTION_FORM_DELETE_IN_HELP_0));
        }
        return super.getHelpText();
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getIconPath()
     */
    @Override
    public String getIconPath() {

        if (!OpenCms.getRoleManager().hasRole(getWp().getCms(), CmsRole.DATABASE_MANAGER)) {
            return "tools/database/buttons/webform_delete_disabled.png";
        }
        return super.getIconPath();
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#isEnabled()
     */
    @Override
    public boolean isEnabled() {

        if (OpenCms.getRoleManager().hasRole(getWp().getCms(), CmsRole.DATABASE_MANAGER)) {
            return true;
        }
        return false;
    }

}
