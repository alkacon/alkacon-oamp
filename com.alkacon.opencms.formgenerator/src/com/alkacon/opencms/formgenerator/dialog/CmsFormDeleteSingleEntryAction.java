/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormDeleteSingleEntryAction.java,v $
 * Date   : $Date: 2010/09/17 12:49:30 $
 * Version: $Revision: 1.2 $
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

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsPermissionSet;
import org.opencms.security.CmsRole;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsListDirectAction;

import java.util.List;

/**
 * List action to delete a single form entry.<p>
 * 
 * This special handler is needed because only users with the role {@link CmsRole#DATABASE_MANAGER} or
 * with write permission on the form VFS file should be able to perform this action.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsFormDeleteSingleEntryAction extends CmsListDirectAction {

    /** Determines if the current entry can be deleted by the current user. */
    private Boolean m_editable;

    /**
     * Default constructor.<p>
     * 
     * @param id unique id
     */
    public CmsFormDeleteSingleEntryAction(String id) {

        super(id);
    }

    /**
     * Returns if the form entry can be deleted depending on the current users permissions and roles.<p>
     * 
     * @param wp the current workplace list dialog instance
     * 
     * @return <code>true</code> if the entry can be deleted by the current user, otherwise <code>false</code>
     */
    protected static boolean checkEditable(A_CmsListDialog wp) {

        try {
            CmsFormDataListDialog dialog = (CmsFormDataListDialog)wp;
            List<CmsFormDataBean> forms = CmsFormDataAccess.getInstance().readForms(
                CmsFormDatabaseFilter.HEADERS.filterFormId(dialog.getParamFormid()));
            CmsFormDataBean data = forms.get(0);
            CmsResource formFile = wp.getCms().readResource(data.getResourceId());
            return OpenCms.getRoleManager().hasRole(wp.getCms(), CmsRole.DATABASE_MANAGER)
                || wp.getCms().hasPermissions(formFile, CmsPermissionSet.ACCESS_WRITE, false, CmsResourceFilter.ALL);
        } catch (Exception e) {
            // error reading form resource, only check roles of current user
            return OpenCms.getRoleManager().hasRole(wp.getCms(), CmsRole.DATABASE_MANAGER);
        }
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getHelpText()
     */
    @Override
    public CmsMessageContainer getHelpText() {

        if (!isEditable()) {
            return (Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_IN_HELP_0));
        }
        return super.getHelpText();
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getIconPath()
     */
    @Override
    public String getIconPath() {

        if (!isEditable()) {
            return "tools/database/buttons/webform_delete_disabled.png";
        }
        return super.getIconPath();
    }

    /**
     * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#isEnabled()
     */
    @Override
    public boolean isEnabled() {

        return isEditable();
    }

    /**
     * Returns if the form entry can be deleted depending on the current users permissions and roles.<p>
     * 
     * @return <code>true</code> if the entry can be deleted by the current user, otherwise <code>false</code>
     */
    private boolean isEditable() {

        if (m_editable == null) {
            m_editable = Boolean.valueOf(checkEditable(getWp()));
        }
        return m_editable.booleanValue();
    }
}
