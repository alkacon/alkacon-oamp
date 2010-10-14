/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/A_CmsNewsletterMailData.java,v $
 * Date   : $Date: 2010/10/14 13:17:50 $
 * Version: $Revision: 1.10 $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;
import org.opencms.util.CmsMacroResolver;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.Email;

/**
 * Basic implementation to generate newsletter emails and the list of recipients from a newsletter structured content VFS file.<p>
 *  
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.10 $ 
 * 
 * @since 7.0.3 
 */
public abstract class A_CmsNewsletterMailData implements I_CmsNewsletterMailData {

    /** The macro name for the "siteurl" macro. */
    protected static final String MACRO_SITEURL = "siteurl";

    /** The macro name for the "title" macro. */
    protected static final String MACRO_TITLE = "title";

    /** The node name for the BCC node. */
    protected static final String NODE_BCC = "BCC";

    /** The node name for the From node. */
    protected static final String NODE_FROM = "From";

    /** The node name for the From name node. */
    protected static final String NODE_FROM_NAME = "FromName";

    /** The node name for the Subject node. */
    protected static final String NODE_SUBJECT = "Subject";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(A_CmsNewsletterMailData.class);

    /** The OpenCms user context. */
    private CmsObject m_cms;

    /** The newsletter content. */
    private CmsXmlContent m_content;

    /** The email encoding. */
    private String m_encoding;

    /** The email from address. */
    private String m_from;

    /** The email from name. */
    private String m_fromName;

    /** The group to send the newsletter to. */
    private CmsGroup m_group;

    /** The JSP action element. */
    private CmsJspActionElement m_jsp;

    /** The Locale to use to read the newsletter content. */
    private Locale m_locale;

    /** The organizational unit to send the newsletter to. */
    private CmsOrganizationalUnit m_ou;

    /** The list of recipients to send the newsletter to. */
    private List<InternetAddress> m_recipients;

    /** The email subject. */
    private String m_subject;

    /**
     * Returns the newsletter XML content.<p>
     * 
     * @return the newsletter XML content
     */
    public CmsXmlContent getContent() {

        return m_content;
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getEmail()
     */
    public abstract Email getEmail() throws CmsException;

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getEmailContentPreview()
     */
    public String getEmailContentPreview() throws CmsException {

        return getEmailContentPreview(false);
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getEmailContentPreview(boolean)
     */
    public abstract String getEmailContentPreview(boolean onlyPartialHtml) throws CmsException;

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getRecipients()
     */
    public List<InternetAddress> getRecipients() throws CmsException {

        if (m_recipients != null) {
            // we have explicitly set recipients, use these
            return m_recipients;
        }
        // we have a group or an OU to check for recipients
        List<InternetAddress> recipients = new ArrayList<InternetAddress>();
        Iterator<CmsUser> i = new ArrayList<CmsUser>().iterator();
        String groupName = "";
        if (getGroup() != null) {
            // iterate over mailing list members (i.e. an OpenCms group)
            groupName = getGroup().getName();
            i = getCms().getUsersOfGroup(groupName).iterator();
        } else if (getOu() != null) {
            i = getOuUsers().iterator();
        }
        while (i.hasNext()) {
            CmsUser user = i.next();
            if (CmsNewsletterManager.isActiveUser(user, groupName)) {
                // add active users to the recipients
                try {
                    recipients.add(new InternetAddress(user.getEmail()));
                } catch (MessagingException e) {
                    // log invalid email address
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(
                            Messages.LOG_ERROR_NEWSLETTER_EMAIL_3,
                            user.getEmail(),
                            user.getName(),
                            getContent().getFile().getRootPath()));
                    }
                }
            }
        }

        if (getContent().hasValue(NODE_BCC, getLocale())) {
            // add the configured email address to the list of BCC recipients
            try {
                recipients.add(new InternetAddress(getContent().getStringValue(getCms(), NODE_BCC, getLocale())));
            } catch (MessagingException e) {
                // log invalid email address
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERROR_NEWSLETTER_EMAIL_BCC_2,
                        getContent().getStringValue(getCms(), NODE_BCC, getLocale()),
                        getContent().getFile().getRootPath()));
                }
            }
        }
        return recipients;
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getResourceTypeName()
     */
    public abstract String getResourceTypeName();

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#initialize(org.opencms.jsp.CmsJspActionElement, org.opencms.file.CmsGroup, java.lang.String)
     */
    public void initialize(CmsJspActionElement jsp, CmsGroup group, String fileName) throws CmsException {

        initialize(jsp, group, null, null, fileName);
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#initialize(org.opencms.jsp.CmsJspActionElement, org.opencms.security.CmsOrganizationalUnit, java.lang.String)
     */
    public void initialize(CmsJspActionElement jsp, CmsOrganizationalUnit ou, String fileName) throws CmsException {

        initialize(jsp, null, ou, null, fileName);
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#initialize(CmsJspActionElement, List, String)
     */
    public void initialize(CmsJspActionElement jsp, List<InternetAddress> recipients, String fileName)
    throws CmsException {

        initialize(jsp, null, null, recipients, fileName);
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#isSendable()
     */
    public boolean isSendable() throws CmsException {

        if (m_recipients == null) {
            // we have a group or OU to send the newsletter to, store send information
            CmsFile file = getContent().getFile();
            String resourceName = getCms().getSitePath(file);
            CmsLock lock = getCms().getLock(file);
            boolean unLocked = false;
            if (lock.isNullLock()) {
                unLocked = true;
                getCms().lockResource(resourceName);
                lock = getCms().getLock(file);
            }
            if (lock.isOwnedBy(getCms().getRequestContext().currentUser())) {
                // resource is locked by current user, write send information
                String value = String.valueOf(System.currentTimeMillis()) + CmsProperty.VALUE_LIST_DELIMITER;
                if (getGroup() != null) {
                    value += getGroup().getId();
                    CmsModule module = OpenCms.getModuleManager().getModule(CmsNewsletterManager.MODULE_NAME);
                    ((CmsNewsletterManager)module.getActionInstance()).saveSentNewsletterInfo(
                        getGroup().getId(),
                        file.getStructureId());
                } else if (getOu() != null) {
                    value += "ou:" + getOu().getName();
                }
                CmsProperty property = new CmsProperty(CmsNewsletterManager.PROPERTY_NEWSLETTER_DATA, value, null, true);
                getCms().writePropertyObject(resourceName, property);
                try {
                    getCms().unlockResource(resourceName);
                    unLocked = false;
                    OpenCms.getPublishManager().publishResource(getCms(), resourceName);
                } catch (Exception e) {
                    // unlocking and publishing failed maybe a parent folder is locked
                }
            } else {
                // resource is not locked by current user
                return false;
            }
            if (unLocked) {
                getCms().unlockResource(resourceName);
            }
        }
        return true;
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#setRecipients(java.util.List)
     */
    public void setRecipients(List<InternetAddress> recipients) {

        m_recipients = recipients;

    }

    /**
     * Returns the OpenCms user context.<p>
     * 
     * @return the OpenCms user context
     */
    protected CmsObject getCms() {

        return m_cms;
    }

    /**
     * Returns the email encoding.<p>
     * 
     * @return the email encoding
     */
    protected String getEncoding() {

        if (m_encoding == null) {
            m_encoding = getCms().getRequestContext().getEncoding();
        }
        return m_encoding;
    }

    /**
     * Returns the email from address.<p>
     * 
     * @return the email from address
     */
    protected String getFrom() {

        if (m_from == null) {
            m_from = getContent().getStringValue(getCms(), NODE_FROM, getLocale());
        }
        return m_from;
    }

    /**
     * Returns the email from address.<p>
     * 
     * @return the email from address
     */
    protected String getFromName() {

        if (m_fromName == null) {
            m_fromName = getContent().getStringValue(getCms(), NODE_FROM_NAME, getLocale());
        }
        return m_fromName;
    }

    /**
     * Returns the group to send the newsletter to.<p>
     * 
     * @return the group to send the newsletter to
     */
    protected CmsGroup getGroup() {

        return m_group;
    }

    /**
     * Returns the email HTML text.<p>
     * 
     * @return the email HTML text
     * @throws CmsException if extracting HTML text fails
     */
    protected abstract String getHtml() throws CmsException;

    /**
     * Returns the JSP action element.<p>
     * 
     * @return the JSP action element
     */
    protected CmsJspActionElement getJsp() {

        return m_jsp;
    }

    /**
     * Returns the Locale to use to read the newsletter content.<p>
     * 
     * @return the Locale to use to read the newsletter content
     */
    protected Locale getLocale() {

        return m_locale;
    }

    /**
     * Returns the organizational unit to send the newsletter to.<p>
     * 
     * @return the organizational unit to send the newsletter to
     */
    protected CmsOrganizationalUnit getOu() {

        return m_ou;
    }

    /**
     * Returns the users for the current OU and all sub OUs which are no web user or newsletter units.<p>
     * 
     * @return the users for the current OU and all sub OUs
     */
    protected List<CmsUser> getOuUsers() {

        List<CmsUser> result = new ArrayList<CmsUser>(128);
        try {
            List<CmsOrganizationalUnit> units = OpenCms.getRoleManager().getOrgUnitsForRole(
                getCms(),
                CmsRole.ACCOUNT_MANAGER.forOrgUnit(getOu().getName()),
                true);

            for (Iterator<CmsOrganizationalUnit> i = units.iterator(); i.hasNext();) {
                CmsOrganizationalUnit ou = i.next();
                if (!ou.hasFlagWebuser()
                    && !ou.getSimpleName().startsWith(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX)) {
                    result.addAll(OpenCms.getOrgUnitManager().getUsers(getCms(), ou.getName(), false));
                }
            }
        } catch (CmsException e) {
            // log error
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERROR_NEWSLETTER_UNITS_2,
                    getCms().getRequestContext().currentUser(),
                    getOu().getName()));
            }
        }

        return result;
    }

    /**
     * Returns the email subject.<p>
     * 
     * @return the email subject
     */
    protected String getSubject() {

        if (m_subject == null) {
            m_subject = getContent().getStringValue(getCms(), NODE_SUBJECT, getLocale());
        }
        return m_subject;
    }

    /**
     * Returns the email plain text.<p>
     * 
     * @return the email plain text
     * @throws CmsException if extracting text fails
     */
    protected abstract String getText() throws CmsException;

    /**
     * Returns the input with resolved macros.<p>
     * 
     * @param input the input to resolve
     * @return the input with resolved macros
     */
    protected String resolveMacros(String input) {

        CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(getCms()).setKeepEmptyMacros(false);
        resolver.addMacro(MACRO_SITEURL, OpenCms.getSiteManager().getCurrentSite(getCms()).getUrl());
        resolver.addMacro(MACRO_TITLE, getSubject());
        return resolver.resolveMacros(input);
    }

    /**
     * Initializes the necessary members to generate the email and the list of recipients.<p>
     * 
     * To make the newsletter work as expected, exactly one parameter of group, organizational unit or
     * list of recipients should not be <code>null</code>.<p> 
     * 
     * @param jsp the current action element
     * @param group the mailing list group to send the newsletter to
     * @param ou the organizational unit to send the newsletter to
     * @param recipients the recipients of the newsletter mail, items have to be of type {@link javax.mail.internet.InternetAddress}
     * @param fileName the fileName of a VFS file that can be used to generate the newsletter
     * @throws CmsException if reading the VFS file fails
     */
    private void initialize(
        CmsJspActionElement jsp,
        CmsGroup group,
        CmsOrganizationalUnit ou,
        List<InternetAddress> recipients,
        String fileName) throws CmsException {

        m_cms = jsp.getCmsObject();
        CmsFile file = getCms().readFile(fileName);
        m_content = CmsXmlContentFactory.unmarshal(getCms(), file);
        m_group = group;
        m_ou = ou;
        m_recipients = recipients;
        m_jsp = jsp;
        m_locale = OpenCms.getLocaleManager().getDefaultLocale(getCms(), fileName);
    }

}
