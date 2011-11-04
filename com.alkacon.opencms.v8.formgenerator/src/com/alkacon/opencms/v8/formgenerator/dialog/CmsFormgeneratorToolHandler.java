/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/dialog/CmsFormgeneratorToolHandler.java,v $
 * Date   : $Date: 2010/05/21 13:49:30 $
 * Version: $Revision: 1.3 $
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

package com.alkacon.opencms.v8.formgenerator.dialog;

import com.alkacon.opencms.v8.formgenerator.CmsForm;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.security.CmsRole;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.tools.A_CmsToolHandler;

import org.apache.commons.logging.Log;

/**
 * Tool handler that looks at the module parameter "usergroup" of this module to get the group that has access 
 * to the tool it is configured for. 
 * 
 *  @since 1.3.0
 *  @author Achim Westermann
 */
public class CmsFormgeneratorToolHandler extends A_CmsToolHandler {

    /** The group that has access. */
    private String m_group;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormgeneratorToolHandler.class);

    /**
     * Initializes the group from the module parameter. 
     */
    public CmsFormgeneratorToolHandler() {

        super();

        try {
            CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);

            m_group = module.getParameter(CmsForm.MODULE_PARAM_TOOL_GROUP);
        } catch (Exception ex) {
            LOG.error(Messages.get().getBundle().key(
                Messages.ERR_TOOLHANDLER_MISSINGGROUP_2,
                new Object[] {CmsForm.MODULE_PARAM_TOOL_GROUP, CmsForm.MODULE_NAME}), ex);
        }
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsToolHandler#isEnabled(org.opencms.file.CmsObject)
     */
    public boolean isEnabled(CmsObject cms) {

        boolean result = false;
        CmsUser user = cms.getRequestContext().getCurrentUser();
        if (OpenCms.getRoleManager().hasRole(cms, CmsRole.DATABASE_MANAGER)) {
            // the database managers see the tool inside the "Database Management", hide this for them
            result = false;
        } else if (OpenCms.getRoleManager().hasRole(cms, CmsRole.ADMINISTRATOR)) {
            result = true;
        } else if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(m_group)) {
            String userStr = user.getName();
            try {
                result = cms.userInGroup(userStr, m_group);
            } catch (CmsException cme) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.ERR_TOOLHANDLER_CHECKGROUP_2,
                    new Object[] {userStr, m_group}), cme);
            }
        }
        return result;
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsToolHandler#isVisible(org.opencms.file.CmsObject)
     */
    public boolean isVisible(CmsObject cms) {

        return isEnabled(cms);
    }

}
