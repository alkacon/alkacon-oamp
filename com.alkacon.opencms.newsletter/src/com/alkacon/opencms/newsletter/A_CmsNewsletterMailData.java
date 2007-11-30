/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/A_CmsNewsletterMailData.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.4 $
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
 * @version $Revision: 1.4 $ 
 * 
 * @since 7.0.3 
 */
public abstract class A_CmsNewsletterMailData implements I_CmsNewsletterMailData {

    /** The node name for the BCC node. */
    protected static final String NODE_BCC = "BCC";

    /** The node name for the From node. */
    protected static final String NODE_FROM = "From";

    /** The node name for the Subject node. */
    protected static final String NODE_SUBJECT = "Subject";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(A_CmsNewsletterMailData.class);

    /** The OpenCms user context. */
    private CmsObject m_cms;

    /** The newsletter content. */
    private CmsXmlContent m_content;

    /** The group to send the newsletter to. */
    private CmsGroup m_group;

    /** The JSP action element. */
    private CmsJspActionElement m_jsp;

    /** The Locale to use to read the newsletter content. */
    private Locale m_locale;

    /**
     * Returns the newsletter xml content.<p>
     * 
     * @return the newsletter xml content
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
    public abstract String getEmailContentPreview() throws CmsException;

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getRecipients()
     */
    public List getRecipients() throws CmsException {

        List recipients = new ArrayList();
        Iterator i = getCms().getUsersOfGroup(getGroup().getName()).iterator();
        while (i.hasNext()) {
            CmsUser user = (CmsUser)i.next();
            if (CmsNewsletterManager.isActiveUser(user, getGroup().getName())) {
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

        m_cms = jsp.getCmsObject();
        CmsFile file = getCms().readFile(fileName);
        m_content = CmsXmlContentFactory.unmarshal(getCms(), file);
        m_group = group;
        m_jsp = jsp;
        m_locale = OpenCms.getLocaleManager().getDefaultLocale(getCms(), fileName);
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#isSendable()
     */
    public boolean isSendable() throws CmsException {

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
            String value = "" + System.currentTimeMillis() + CmsProperty.VALUE_LIST_DELIMITER;
            value += getGroup().getId();
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
        return true;
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
     * Returns the group to send the newsletter to.<p>
     * 
     * @return the group to send the newsletter to
     */
    protected CmsGroup getGroup() {

        return m_group;
    }

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

}
