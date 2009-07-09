/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterManager.java,v $
 * Date   : $Date: 2009/07/09 09:30:12 $
 * Version: $Revision: 1.18 $
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

package com.alkacon.opencms.newsletter;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsUser;
import org.opencms.file.types.CmsResourceTypeFolder;
import org.opencms.file.types.CmsResourceTypePlain;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.CmsWorkplace;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides methods to manage the subscribers (users) for the newsletter, to get all newsletter units and to get the
 * initialized mail data class to use for sending the newsletter emails.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.18 $ 
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

    /** Name of the prefix for newsletter sub-organizational units containing mailing lists and subscribers. */
    public static final String NEWSLETTER_OU_NAMEPREFIX = "nl_";

    /** Pattern to validate email addresses. */
    public static final Pattern PATTERN_VALIDATION_EMAIL = Pattern.compile("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,4})");

    /** The name of the property where the send data (date and mailing list) is written to. */
    public static final String PROPERTY_NEWSLETTER_DATA = "newsletter";

    /** Name of the additional user info: flag to determine if the newsletter user is active. */
    public static final String USER_ADDITIONALINFO_ACTIVE = "AlkNewsletter_ActiveUser:";

    /** Name of the additional user info: flag to determine if the newsletter user is marked for deletion. */
    public static final String USER_ADDITIONALINFO_TODELETE = "AlkNewsletter_UserToDelete:";

    /** The VFS folder path where the information about last sent newsletters is saved. */
    public static String VFS_PATH_NEWSLETTER_INFO = CmsWorkplace.VFS_PATH_SYSTEM + "shared/alkacon_newsletter/";

    /** The default password for all newsletter users, can/should be overwritten in the module parameter. */
    private static final String PASSWORD_USER = "Uw82-Qn!";

    /** The admin CmsObject that is used for user/group operations. */
    private CmsObject m_adminCms;

    /**
     * Returns the mail data class generating the newsletter mail and recipients.<p>
     * 
     * The instance must be correctly initialized afterwards using {@link I_CmsNewsletterMailData#initialize(CmsJspActionElement, CmsGroup, String)}.<p>
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
     * @param jsp the current action element
     * @param group the group to send the newsletter to
     * @param fileName the fileName of the newsletter
     * @return the initialized mail data class generating the newsletter mail and recipients
     * @throws Exception if instantiating the mail data class fails
     */
    public static I_CmsNewsletterMailData getMailData(CmsJspActionElement jsp, CmsGroup group, String fileName)
    throws Exception {

        I_CmsNewsletterMailData result = getMailData();
        result.initialize(jsp, group, fileName);
        return result;
    }

    /**
     * Returns the initialized mail data class generating the newsletter mail and recipients.<p>
     * 
     * @param jsp the current action element
     * @param ou the organizational unit to send the newsletter to
     * @param fileName the fileName of the newsletter
     * @return the initialized mail data class generating the newsletter mail and recipients
     * @throws Exception if instantiating the mail data class fails
     */
    public static I_CmsNewsletterMailData getMailData(CmsJspActionElement jsp, CmsOrganizationalUnit ou, String fileName)
    throws Exception {

        I_CmsNewsletterMailData result = getMailData();
        result.initialize(jsp, ou, fileName);
        return result;
    }

    /**
     * Returns the initialized mail data class generating the newsletter mail and recipients.<p>
     * 
     * @param jsp the current action element
     * @param recipients the recipients of the newsletter mail, items have to be of type {@link javax.mail.internet.InternetAddress}
     * @param fileName the fileName of the newsletter
     * @return the initialized mail data class generating the newsletter mail and recipients
     * @throws Exception if instantiating the mail data class fails
     */
    public static I_CmsNewsletterMailData getMailData(CmsJspActionElement jsp, List recipients, String fileName)
    throws Exception {

        I_CmsNewsletterMailData result = getMailData();
        result.initialize(jsp, recipients, fileName);
        return result;
    }

    /**
     * Returns the resource type name of the mail data XML content.<p>
     * 
     * @return the resource type name of the mail data XML content
     * @throws Exception if instantiating the mail data class fails
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
            if (!ou.getSimpleName().startsWith(NEWSLETTER_OU_NAMEPREFIX)) {
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
     * Returns if the given user is active to receive newsletter emails for the given group.<p>
     * 
     * @param user the user to check
     * @param groupName the name of the group the user is a member of
     * @return true if the given user is active to receive newsletter emails, otherwise false
     */
    public static boolean isActiveUser(CmsUser user, String groupName) {

        Boolean active = (Boolean)user.getAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName);
        return (((active != null) && active.booleanValue()) || (active == null)) && user.isEnabled();
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
     * @see org.opencms.module.A_CmsModuleAction#initialize(org.opencms.file.CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)
     */
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        // store the admin CmsObject as member
        m_adminCms = adminCms;

        // check if the folder exists where the last sent newsletter information is saved
        if (!getAdminCms().existsResource(VFS_PATH_NEWSLETTER_INFO)) {
            try {
                // in order to create the folder, we have to switch to an offline project
                CmsObject cms = OpenCms.initCmsObject(getAdminCms());
                String projectName = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_PROJECT_NAME,
                    "Offline");
                CmsProject project = cms.readProject(projectName);
                cms.getRequestContext().setCurrentProject(project);
                // create folder where the newsletter info files should be saved to

                cms.createResource(VFS_PATH_NEWSLETTER_INFO, CmsResourceTypeFolder.getStaticTypeId());
                OpenCms.getPublishManager().publishResource(cms, VFS_PATH_NEWSLETTER_INFO);
            } catch (Exception e) {
                // TODO: error creating folder, log info
            }
        }
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
                    "Alkacon OpenCms Newsletter",
                    Collections.EMPTY_MAP);
                // set the users email address
                user.setEmail(email);
                if (!activate) {
                    // set the additional info as marker that the new user is currently not active at all
                    user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE, Boolean.FALSE);
                }
            } else {
                Object o = user.getAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName);
                if (o != null) {
                    // user tried to subscribe to this mailing list group before, return null to show error message
                    return null;
                }
            }
            user.setAdditionalInfo(USER_ADDITIONALINFO_ACTIVE + groupName, Boolean.valueOf(activate));
            if (activate && (user.getAdditionalInfo().get(USER_ADDITIONALINFO_ACTIVE) != null)) {
                // remove flag that this user is not active at all
                user.deleteAdditionalInfo(USER_ADDITIONALINFO_ACTIVE);
            }
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
     * Returns if information about the last sent newsletter exists for the given group ID.<p>
     * 
     * @param groupId the group ID to check the presence of an information file
     * 
     * @return <code>true</code> if information about the last sent newsletter exists, otherwise <code>false</code>
     */
    protected boolean existsSentNewsletterInfo(CmsUUID groupId) {

        // file path where the newsletter info file is saved
        String infoFilePath = VFS_PATH_NEWSLETTER_INFO + NEWSLETTER_OU_NAMEPREFIX + groupId.hashCode();
        return getAdminCms().existsResource(infoFilePath);
    }

    /**
     * Returns if information about the last sent newsletter exists for the given group name.<p>
     * 
     * @param groupName the group name to check the presence of an information file
     * 
     * @return <code>true</code> if information about the last sent newsletter exists, otherwise <code>false</code>
     */
    protected boolean existsSentNewsletterInfo(String groupName) {

        try {
            CmsUUID groupId = getAdminCms().readGroup(groupName).getId();
            return existsSentNewsletterInfo(groupId);
        } catch (CmsException e) {
            // error reading group
            return false;
        }
    }

    /**
     * Returns the UUID of the last sent newsletter resource or <code>null</code>, if the information is not found.<p>
     * 
     * @param groupName the group name to get the last sent newsletter resource UUID
     * 
     * @return the UUID of the last sent newsletter resource
     */
    protected CmsUUID getSentNewsletterInfo(String groupName) {

        try {
            CmsUUID groupId = getAdminCms().readGroup(groupName).getId();
            String infoFilePath = VFS_PATH_NEWSLETTER_INFO + NEWSLETTER_OU_NAMEPREFIX + groupId.hashCode();
            if (getAdminCms().existsResource(infoFilePath)) {
                // we have an info file, read the content and return it as UUID
                String idStr = new String(getAdminCms().readFile(infoFilePath).getContents());
                return new CmsUUID(idStr);
            }
        } catch (CmsException e) {
            // error reading info file
        }
        return null;
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
     * Saves the information about the last sent newsletter resource to a file in <code>/system/shared/alkacon_newsletter/</code>.<p>
     * 
     * This can be used to send the last newsletter to newly registered subscribers.<p>
     * 
     * @param groupId the ID of the mailing list group to which the newsletter has been sent
     * @param id the structure ID of the newsletter resource
     */
    protected void saveSentNewsletterInfo(CmsUUID groupId, CmsUUID id) {

        try {
            // in order to write the info, we have to switch to an offline project
            CmsObject cms = OpenCms.initCmsObject(getAdminCms());
            String projectName = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                MODULE_PARAM_PROJECT_NAME,
                "Offline");
            CmsProject project = cms.readProject(projectName);
            cms.getRequestContext().setCurrentProject(project);
            // file name of the info file containing the path of the sent newsletter
            String infoFileName = NEWSLETTER_OU_NAMEPREFIX + groupId.hashCode();
            // folder where the newsletter info file should be saved
            String infoFilePath = VFS_PATH_NEWSLETTER_INFO + infoFileName;

            if (!cms.existsResource(infoFilePath)) {
                cms.createResource(infoFilePath, CmsResourceTypePlain.getStaticTypeId(), id.toString().getBytes(), null);
            } else {
                CmsLock lock = cms.getLock(infoFilePath);
                if (lock.isNullLock()) {
                    cms.lockResource(infoFilePath);
                    lock = cms.getLock(infoFilePath);
                }
                if (lock.isOwnedBy(cms.getRequestContext().currentUser())) {
                    CmsFile file = cms.readFile(infoFilePath);
                    file.setContents(id.toString().getBytes());
                    cms.writeFile(file);
                }
            }
            cms.unlockResource(infoFilePath);
            OpenCms.getPublishManager().publishResource(cms, infoFilePath);
        } catch (Exception e) {
            // TODO: failed to save the information
            return;
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
