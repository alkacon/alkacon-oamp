/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterManager.java,v $
 * Date   : $Date: 2007/11/09 13:43:43 $
 * Version: $Revision: 1.9 $
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
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides methods to manage the users (subscribers) for the newsletter.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterManager extends A_CmsModuleAction {

    /** The name of the newsletter module. */
    public static final String MODULE_NAME = CmsNewsletterManager.class.getPackage().getName();

    /** Module parameter name for the class name to use for generating the newsletter mail data. */
    public static final String MODULE_PARAM_CLASS_MAILDATA = "class_maildata";

    /** Module parameter name for the user password of the newsletter users. */
    public static final String MODULE_PARAM_PASSWORD_USER = "user_password";

    /** Module parameter name for the project name to use for user deletion operations. */
    public static final String MODULE_PARAM_PROJECT_NAME = "project_name";

    /** Name of the sub-organizational unit for newsletter containing mailing lists and subscribers. */
    public static final String NEWSLETTER_OU_SIMPLENAME = "newsletter/";

    /** Principal flag to set on users to make them invisible in common accounts management. */
    public static final int NEWSLETTER_PRINCIPAL_FLAG = (int)Math.pow(2, 18);

    /** Pattern to validate email addresses. */
    public static final Pattern PATTERN_VALIDATION_EMAIL = Pattern.compile("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,4})");

    /** The name of the property where the send data (date and mailing list) is written to. */
    public static final String PROPERTY_NEWSLETTER_DATA = "newsletter";

    /** Name of the additional user info: flag to determine if the newsletter user is active. */
    public static final String USER_ADDITIONALINFO_ACTIVE = "AlkNewsletter_ActiveUser:";

    /** Name of the additional user info: flag to determine if the newsletter user is marked for deletion. */
    public static final String USER_ADDITIONALINFO_TODELETE = "AlkNewsletter_UserToDelete:";

    /** The default password for all newsletter users, can/should be overwritten in the module parameter. */
    private static final String PASSWORD_USER = "Uw82-Qn!";

    /** The admin CmsObject that is used for user/group operations. */
    private CmsObject m_adminCms;

    /**
     * Returns the mail data class generating the newsletter mail and recipients.<p>
     * 
     * The instance must be correctly initialized afterwards using {@link I_CmsNewsletterMailData#initialize(CmsObject, CmsGroup, String)}.<p>
     * 
     * @return the mail data class generating the newsletter mail and recipients
     * @throws Exception if instanciating the mail data class fails
     */
    public static I_CmsNewsletterMailData getMailData() throws Exception {

        String className = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
            MODULE_PARAM_CLASS_MAILDATA,
            CmsNewsletterMailData.class.getName());
        return (I_CmsNewsletterMailData)Class.forName(className).newInstance();
    }

    /**
     * Returns the initialized mail data class generating the newsletter mail and recipients.<p>
     * 
     * @param cms the current OpenCms user context
     * @param group the group to send the newsletter to
     * @param fileName the fileName of the newsletter
     * @return the initialized mail data class generating the newsletter mail and recipients
     * @throws Exception if instanciating the mail data class fails
     */
    public static I_CmsNewsletterMailData getMailData(CmsObject cms, CmsGroup group, String fileName) throws Exception {

        I_CmsNewsletterMailData result = getMailData();
        result.initialize(cms, group, fileName);
        return result;
    }

    /**
     * Returns the resource type name of the mail data XML content.<p>
     * 
     * @return the resource type name of the mail data XML content
     * @throws Exception if instanciating the mail data class fails
     */
    public static String getMailDataResourceTypeName() throws Exception {

        return getMailData().getResourceTypeName();
    }

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
            if (!ou.getSimpleName().equals(NEWSLETTER_OU_SIMPLENAME)) {
                it.remove();
            }
        }
        return ous;
    }

    /**
     * Returns the password to use for all newsletter users.<p>
     * 
     * @return the password to use for all newsletter users
     */
    public static String getPassword() {

        return OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(MODULE_PARAM_PASSWORD_USER, PASSWORD_USER);
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
     * Returns if the given user is active to receive newsletter emails for the given group.<p>
     * 
     * @param user the user to check
     * @param groupName the name of the group the user is a member of
     * @return true if the given user is active to receive newsletter emails, otherwise false
     */
    protected static boolean isActiveUser(CmsUser user, String groupName) {

        Boolean active = (Boolean)user.getAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName);
        return ((active != null && active.booleanValue()) || active == null) && user.isEnabled();
    }

    /**
     * @see org.opencms.module.A_CmsModuleAction#initialize(org.opencms.file.CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)
     */
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        // store the admin CmsObject as member
        m_adminCms = adminCms;
    }

    /**
     * Activates the newsletter user with the given email address in the given group.<p>
     * 
     * @param email the email address of the user
     * @param groupName the name of the group to activate the newsletter user for
     * @return true if the user was activated, otherwise false
     */
    protected boolean activateNewsletterUser(String email, String groupName) {

        try {
            CmsUser user = getAdminCms().readUser(getAdminCms().readGroup(groupName).getOuFqn() + email);
            if (user.getAdditionalInfo().get(USER_ADDITIONALINFO_ACTIVE) != null) {
                // remove flag that this user is not active at all
                user.deleteAdditionalInfo(USER_ADDITIONALINFO_ACTIVE);
            }
            user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName, Boolean.valueOf(true));
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
        // create additional infos containing the active flag set to passed parameter

        try {
            String ouFqn = getAdminCms().readGroup(groupName).getOuFqn();
            try {
                // first try to read the user
                user = getAdminCms().readUser(ouFqn + email);
            } catch (CmsException e) {
                // user does not exist
            }
            if (user == null) {
                // create the user with additional infos
                user = getAdminCms().createUser(
                    ouFqn + email,
                    getPassword(),
                    "Alkacon OpenCms newsletter",
                    Collections.EMPTY_MAP);
                // set the users email address
                user.setEmail(email);
                // set the flag so that the new user does not appear in the accounts management view
                user.setFlags(user.getFlags() ^ CmsNewsletterManager.NEWSLETTER_PRINCIPAL_FLAG);
                if (!activate) {
                    // set the additional info as marker that the user is currently not active at all
                    user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE, Boolean.FALSE);
                }
            } else {
                Object o = user.getAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName);
                if (o != null) {
                    // user tried to subscribe to this mailing list group, return null to show error message
                    return null;
                }
            }
            user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName, Boolean.valueOf(activate));
            // write the user
            getAdminCms().writeUser(user);
            // add the user to the given mailing list group
            getAdminCms().addUserToGroup(user.getName(), groupName);
        } catch (CmsException e) {
            // error creating user or modifying user
        }
        return user;
    }

    /**
     * Deletes a newsletter user with given email address from the specified group.<p>
     * 
     * If the delete flag should be checked, the user has to be marked for deletion for a successful delete operation.<p>
     * 
     * @param email the email address of the user to delete
     * @param groupName the name of the group the user should be deleted from
     * @param checkDeleteFlag determines if the delete flag chould be checked before deleting the user
     * @return true if deletion was successful, otherwise false
     */
    protected boolean deleteNewsletterUser(String email, String groupName, boolean checkDeleteFlag) {

        try {
            String ouFqn = getAdminCms().readGroup(groupName).getOuFqn();
            CmsUser user = getAdminCms().readUser(ouFqn + email);
            boolean isToDelete = !checkDeleteFlag
                || ((Boolean)user.getAdditionalInfo(USER_ADDITIONALINFO_TODELETE + groupName)).booleanValue();
            if (isToDelete) {
                // in order to delete a user, we have to switch to an offline project
                CmsObject cms = OpenCms.initCmsObject(getAdminCms());
                String projectName = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_PROJECT_NAME,
                    "Offline");
                CmsProject project = cms.readProject(projectName);
                cms.getRequestContext().setCurrentProject(project);
                // remove the user from the specified group
                cms.removeUserFromGroup(user.getName(), groupName);

                if (cms.getGroupsOfUser(user.getName(), true).size() < 1) {
                    // delete the user if this was the last group the user belonged to
                    cms.deleteUser(user.getName());
                } else {
                    // remove the additional info attributes for the mailing list group
                    user.getAdditionalInfo().remove(USER_ADDITIONALINFO_TODELETE + groupName);
                    user.getAdditionalInfo().remove(USER_ADDITIONALINFO_ACTIVE + groupName);
                    cms.writeUser(user);
                }
                return true;
            }
        } catch (CmsException e) {
            // error reading or deleting user
        }
        return false;
    }

    /**
     * Returns if a newsletter user with the given email address exists in the given group.<p>
     * 
     * @param email the email address of the user
     * @param groupName the name of the group the user could be a member of
     * @return true if a newsletter user with the given email address exists, otherwise false
     */
    protected boolean existsNewsletterUser(String email, String groupName) {

        try {
            String ouFqn = getAdminCms().readGroup(groupName).getOuFqn();
            CmsUser user = getAdminCms().readUser(ouFqn + email);
            CmsGroup group = getAdminCms().readGroup(groupName);
            return getAdminCms().getGroupsOfUser(user.getName(), true).contains(group);
        } catch (CmsException e) {
            // error reading user, does not exist
            return false;
        }
    }

    /**
     * Marks a newsletter user to be deleted, this is necessary for the deletion confirmation.<p>
     * 
     * @param email the email address of the user
     * @param groupName the name of the group the user should be marked for deletion
     * @return true if the user was successfully marked to be deleted, otherwise false
     */
    protected boolean markToDeleteNewsletterUser(String email, String groupName) {

        try {
            String ouFqn = getAdminCms().readGroup(groupName).getOuFqn();
            CmsUser user = getAdminCms().readUser(ouFqn + email);
            user.setAdditionalInfo(USER_ADDITIONALINFO_TODELETE + groupName, Boolean.valueOf(true));
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
}
