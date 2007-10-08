/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterManager.java,v $
 * Date   : $Date: 2007/10/08 15:38:47 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Provides methods to manage the users for the newsletter.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsNewsletterManager extends A_CmsModuleAction {

    /** The name of the newsletter module. */
    public static final String MODULE_NAME = CmsNewsletterManager.class.getPackage().getName();

    /** Module parameter name for the project name to use for user deletion operations. */
    public static final String MODULE_PARAM_PROJECT_NAME = "project_name";

    /** Name of the sub-organizational unit for newsletter containing mailing lists and subscribers. */
    public static final String NEWSLETTER_OU_SIMPLENAME = "newsletter/";

    /** Pattern to validate email addresses. */
    public static final Pattern PATTERN_VALIDATION_EMAIL = Pattern.compile("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,4})");

    /** Module parameter name for the user password of the newsletter users. */
    private static final String MODULE_PARAM_PASSWORD_USER = "user_password";

    /** The default password for all newsletter users, should be overwritten in the module parameter. */
    private static final String PASSWORD_USER = "Uw82-QnM";

    /** Name of the additional user info: flag to determine if the newsletter user is active. */
    private static final String USER_ADDITIONALINFO_ACTIVE = "AlkNewsletter_ActiveUser";

    /** Name of the additional user info: flag to determine if the newsletter user is marked for deletion. */
    private static final String USER_ADDITIONALINFO_TODELETE = "AlkNewsletter_UserToDelete";

    /** The admin CmsObject that is used for user/group operations. */
    private CmsObject m_adminCms;

    /**
     * Returns the organizational units that can contain mailing lists to display.<p>
     * 
     * @param cms the current cms context
     * 
     * @return the organizational units
     * 
     * @throws CmsException if something goes wrong
     */
    public static List getOrgUnits(CmsObject cms) throws CmsException {

        List ous = OpenCms.getRoleManager().getOrgUnitsForRole(cms, CmsRole.ACCOUNT_MANAGER.forOrgUnit(""), true);
        Iterator it = ous.iterator();
        while (it.hasNext()) {
            CmsOrganizationalUnit ou = (CmsOrganizationalUnit)it.next();
            if (!ou.getSimpleName().equals(CmsNewsletterManager.NEWSLETTER_OU_SIMPLENAME)) {
                it.remove();
            }
        }
        return ous;
    }

    /**
     * Returns if the email is a valid one using a regular expression pattern.<p>
     * 
     * @param email the email to check
     * @return true if the email is valid, otherwise false
     */
    public static boolean isValidEmail(String email) {

        return PATTERN_VALIDATION_EMAIL.matcher(email).matches();
    }

    /**
     * Returns if the given user is active to receive newsletter emails.<p>
     * 
     * @param user the user to check
     * @return true if the given user is active to receive newsletter emails, otherwise false
     */
    protected static boolean isActiveUser(CmsUser user) {

        Boolean active = (Boolean)user.getAdditionalInfo(USER_ADDITIONALINFO_ACTIVE);
        return active.booleanValue();
    }

    /**
     * @see org.opencms.module.A_CmsModuleAction#initialize(org.opencms.file.CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)
     */
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        // store the admin CmsObject
        m_adminCms = adminCms;
    }

    /**
     * Activates the newsletter user with the given email address .<p>
     * 
     * @param email the email address of the user
     * @return true if the user was activated, otherwise false
     */
    protected boolean activateNewsletterUser(String email) {

        try {
            CmsUser user = getAdminCms().readUser(email);
            user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE, Boolean.valueOf(true));
            getAdminCms().writeUser(user);
            return true;
        } catch (CmsException e) {
            // error reading or writing user
            return false;
        }
    }

    /**
     * Creates a new newsletter user with the specified email address.<p>
     * 
     * Returns the created user or null if the user could not be created.<p>
     * 
     * The user will be activated or not depending on the activate parameter value.<p>
     * 
     * @param email the email address of the new user
     * @param groupName the name of the group to add the user to
     * @param activate if true, the user will be activated directly to receive newsletters, otherwise not
     * @return the new created user or null if the creation failed
     */
    protected CmsUser createNewsletterUser(String email, String groupName, boolean activate) {

        CmsUser user = null;
        // create additional infos containing the active flag set to false
        Map additionalInfos = new HashMap(1);
        additionalInfos.put(USER_ADDITIONALINFO_ACTIVE, Boolean.valueOf(activate));
        try {
            // create the user with additional infos
            user = getAdminCms().createUser(email, getPassword(), "Alkacon OpenCms newsletter user", additionalInfos);
            // set the users email address
            user.setEmail(email);
            getAdminCms().writeUser(user);
            // add the user to the given newsletter group
            getAdminCms().addUserToGroup(user.getName(), groupName);
        } catch (CmsException e) {
            // error creating user
        }
        return user;
    }

    /**
     * Deletes a newsletter user with given email address.<p>
     * 
     * If the delete flag should be checked, the user has to be marked for deletion for a successful delete operation.<p>
     * 
     * @param email the email address of the user to delete
     * @param checkDeleteFlag determines if the delete flag chould be checked before deleting the user
     * @return true if deletion was successful, otherwise false
     */
    protected boolean deleteNewsletterUser(String email, boolean checkDeleteFlag) {

        try {
            CmsUser user = getAdminCms().readUser(email);
            boolean isToDelete = !checkDeleteFlag
                || ((Boolean)user.getAdditionalInfo(USER_ADDITIONALINFO_TODELETE)).booleanValue();
            if (isToDelete) {
                // in order to delete a user, we have to switch to an offline project
                CmsObject cms = OpenCms.initCmsObject(getAdminCms());
                String projectName = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_PROJECT_NAME,
                    "Offline");
                CmsProject project = cms.readProject(projectName);
                cms.getRequestContext().setCurrentProject(project);
                cms.deleteUser(email);
                return true;
            }
        } catch (CmsException e) {
            // error reading or deleting user
        }
        return false;
    }

    /**
     * Returns if a newsletter user with the given email address exists.<p>
     * 
     * @param email the email address of the user
     * @return true if a newsletter user with the given email address exists, otherwise false
     */
    protected boolean existsNewsletterUser(String email) {

        try {
            getAdminCms().readUser(email);
            return true;
        } catch (CmsException e) {
            // error reading user, does not exist
            return false;
        }
    }

    /**
     * Marks a newsletter user to be deleted, this is necessary for the deletion confirmation.<p>
     * 
     * @param email the email address of the user
     * @return true if the user was successfully marked to be deleted, otherwise false
     */
    protected boolean markToDeleteNewsletterUser(String email) {

        try {
            CmsUser user = getAdminCms().readUser(email);
            user.setAdditionalInfo(USER_ADDITIONALINFO_TODELETE, Boolean.valueOf(true));
            getAdminCms().writeUser(user);
            return true;
        } catch (CmsException e) {
            // error reading or writing user
            return false;
        }
    }

    /**
     * Returns the admin CmsObject that is used for user/group operations.<p>
     * 
     * @return the admin CmsObject that is used for user/group operations
     */
    private CmsObject getAdminCms() {

        return m_adminCms;
    }

    /**
     * Returns the password to use for all newsletter users.<p>
     * 
     * @return the password to use for all newsletter users
     */
    private String getPassword() {

        return OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(MODULE_PARAM_PASSWORD_USER, PASSWORD_USER);

    }

}
