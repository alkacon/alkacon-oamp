/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterMailData.java,v $
 * Date   : $Date: 2007/11/09 13:43:43 $
 * Version: $Revision: 1.1 $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;
import org.opencms.lock.CmsLock;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.mail.CmsSimpleMail;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsHtmlExtractor;
import org.opencms.util.CmsStringUtil;
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
 * Generates newsletter emails and the list of recipients from a newsletter structured content VFS file.<p>
 * 
 * Provides some utility methods to generate email previews and get the email contents as string.<p>
 *  
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterMailData implements I_CmsNewsletterMailData {

    /** The node name for the BCC node. */
    protected static final String NODE_BCC = "BCC";

    /** The node name for the ConfFile node. */
    protected static final String NODE_CONFFILE = "ConfFile";

    /** The node name for the From node. */
    protected static final String NODE_FROM = "From";

    /** The node name for the HTML node. */
    protected static final String NODE_HTML = "Html";

    /** The node name for the MailFoot node. */
    protected static final String NODE_MAILFOOT = "MailFoot";

    /** The node name for the MailHead node. */
    protected static final String NODE_MAILHEAD = "MailHead";

    /** The node name for the Subject node. */
    protected static final String NODE_SUBJECT = "Subject";

    /** The node name for the Text node. */
    protected static final String NODE_TEXT = "Text";

    /** The node name for the To node. */
    protected static final String NODE_TO = "To";

    /** Resource type name of a newsletter resource. */
    public static final String RESOURCETYPE_NEWSLETTER_NAME = "alkacon-newsletter";

    /** The xpath for the Config node including trailing "/". */
    protected static final String XPATH_CONFIG = "Config/";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMailData.class);

    /** The OpenCms user context. */
    private CmsObject m_cms;

    /** The newsletter content. */
    private CmsXmlContent m_content;

    /** The group to send the newsletter to. */
    private CmsGroup m_group;

    /** The Locale to use to read the newsletter content. */
    private Locale m_locale;

    /**
     * Empty constructor.<p>
     * 
     * Be sure to call {@link #initialize(CmsObject, CmsGroup, String)} to get correct results.<p>
     */
    public CmsNewsletterMailData() {

        // noop
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * @param fileName the fileName of the newsletter
     * @param group the group to send the newsletter to
     * @param cms the current OpenCms user context
     * @throws CmsException if reading or unmarshalling the file fails
     */
    public CmsNewsletterMailData(String fileName, CmsGroup group, CmsObject cms)
    throws CmsException {

        initialize(cms, group, fileName);
    }

    /**
     * Returns the newsletter xml content.<p>
     * 
     * @return the newsletter xml content
     */
    public CmsXmlContent getContent() {

        return m_content;
    }

    /**
     * Returns the mail to send as newsletter, with set subject, text and from address.<p>
     * 
     * @return the mail to send as newsletter
     * @throws CmsException if generating the email content fails
     */
    public Email getEmail() throws CmsException {

        // get the email data from the content fields
        String from = getContent().getStringValue(getCms(), NODE_FROM, getLocale());
        String subject = getContent().getStringValue(getCms(), NODE_SUBJECT, getLocale());
        String text = getContent().getStringValue(getCms(), NODE_TEXT, getLocale());
        boolean isHtmlMail = Boolean.valueOf(
            getContent().getStringValue(getCms(), XPATH_CONFIG + NODE_HTML, getLocale())).booleanValue();
        if (isHtmlMail) {
            // create and send HTML email
            CmsHtmlMail mail = new CmsHtmlMail();
            try {
                mail.setFrom(from);
            } catch (MessagingException e) {
                // log invalid from email address
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERROR_NEWSLETTER_EMAIL_FROM_2,
                        from,
                        getContent().getFile().getRootPath()));
                }
            }

            mail.setSubject(subject);
            // create the email content and use it as HTML message
            mail.setHtmlMsg(getEmailContent());
            // extract the text from the HTML field
            try {
                text = CmsHtmlExtractor.extractText(text, getCms().getRequestContext().getEncoding());
            } catch (Exception e) {
                // cleaning text failed
            }
            mail.setTextMsg(text);
            // set the mail encoding
            mail.setCharset(getCms().getRequestContext().getEncoding());
            return mail;
        } else {
            // create and send text only email
            CmsSimpleMail mail = new CmsSimpleMail();
            try {
                mail.setFrom(from);
            } catch (MessagingException e) {
                // log invalid from email address
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERROR_NEWSLETTER_EMAIL_FROM_2,
                        from,
                        getContent().getFile().getRootPath()));
                }
            }
            mail.setSubject(subject);
            // extract the text from the HTML field
            try {
                text = CmsHtmlExtractor.extractText(text, getCms().getRequestContext().getEncoding());
            } catch (Exception e) {
                // cleaning text failed
            }
            mail.setMsg(text);
            // set the mail encoding
            mail.setCharset(getCms().getRequestContext().getEncoding());
            return mail;
        }
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getEmailContentPreview()
     */
    public String getEmailContentPreview() throws CmsException {

        String result = getEmailContent();
        if (result.indexOf("</body>") == -1) {
            StringBuffer previewHtml = new StringBuffer(result.length() + 256);
            previewHtml.append("<html><head></head><body style=\"background-color: #FFF;\">\n<pre style=\"font-family: Courier New, monospace; font-size: 13px; color: #000;\">");
            previewHtml.append(result);
            previewHtml.append("</pre>\n</body></html>");
            result = previewHtml.toString();
        }
        return result;
    }

    /**
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#getResourceTypeName()
     */
    public String getResourceTypeName() {
    
        return RESOURCETYPE_NEWSLETTER_NAME;
    }

    /**
     * Returns the recipients of the newsletter mail, items are of type {@link InternetAddress}.<p>
     * 
     * @return the recipients of the newsletter mail
     * @throws CmsException if getting the recipients from a mailing list group fails
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
     * @see com.alkacon.opencms.newsletter.I_CmsNewsletterMailData#initialize(org.opencms.file.CmsObject, org.opencms.file.CmsGroup, java.lang.String)
     */
    public void initialize(CmsObject cms, CmsGroup group, String fileName) throws CmsException {

        CmsFile file = cms.readFile(fileName);
        m_content = CmsXmlContentFactory.unmarshal(cms, file);
        m_group = group;
        m_cms = cms;
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
            String value = "" + System.currentTimeMillis() + CmsProperty.VALUE_LIST_DELIMITER;
            value += getGroup().getId();
            CmsProperty property = new CmsProperty(CmsNewsletterManager.PROPERTY_NEWSLETTER_DATA, value, null, true);
            getCms().writePropertyObject(resourceName, property);
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
     * Returns the email content from the newsletter VFS file.<p>
     * 
     * @return the email content
     * @throws CmsException if reading or unmarshalling the file fails
     */
    protected String getEmailContent() throws CmsException {

        String text = getContent().getStringValue(getCms(), NODE_TEXT, getLocale());
        boolean isHtmlMail = Boolean.valueOf(
            getContent().getStringValue(getCms(), XPATH_CONFIG + NODE_HTML, getLocale())).booleanValue();
        if (isHtmlMail) {
            // create the content of the HTML mail
            StringBuffer mailHtml = new StringBuffer(4096);
            String mailHead = "";
            String mailFoot = "";
            boolean foundExternalConfig = false;
            if (getContent().hasValue(XPATH_CONFIG + NODE_CONFFILE, getLocale())) {
                // optional external configuration file specified, use this as mail configuration
                String path = getContent().getStringValue(getCms(), XPATH_CONFIG + NODE_CONFFILE, getLocale());
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(path)
                    && getCms().existsResource(path)
                    && !CmsResource.isFolder(path)) {
                    CmsFile mailConfig = getCms().readFile(path);
                    CmsXmlContent mailContent = CmsXmlContentFactory.unmarshal(getCms(), mailConfig);
                    // get the mail head and foot from the external configuration file content
                    if (mailContent.hasValue(NODE_MAILHEAD, getLocale())) {
                        mailHead = mailContent.getStringValue(getCms(), NODE_MAILHEAD, getLocale());
                        mailFoot = mailContent.getStringValue(getCms(), NODE_MAILFOOT, getLocale());
                        foundExternalConfig = true;
                    }
                }
            }
            if (!foundExternalConfig) {
                // no external configuration specified, use internal configuration values
                mailHead = getContent().getStringValue(getCms(), XPATH_CONFIG + NODE_MAILHEAD, getLocale());
                mailFoot = getContent().getStringValue(getCms(), XPATH_CONFIG + NODE_MAILFOOT, getLocale());
            }
            mailHtml.append(mailHead);
            mailHtml.append(text);
            mailHtml.append(mailFoot);
            return mailHtml.toString();
        } else {
            // create the content of the text mail
            try {
                return CmsHtmlExtractor.extractText(text, getCms().getRequestContext().getEncoding());
            } catch (Exception e) {
                // error extracting text, return unmodified text                
                return text;
            }
        }
    }

    /**
     * Returns the OpenCms user context.<p>
     * 
     * @return the OpenCms user context
     */
    private CmsObject getCms() {

        return m_cms;
    }

    /**
     * Returns the group to send the newsletter to.<p>
     * 
     * @return the group to send the newsletter to
     */
    private CmsGroup getGroup() {

        return m_group;
    }

    /**
     * Returns the Locale to use to read the newsletter content.<p>
     * 
     * @return the Locale to use to read the newsletter content
     */
    private Locale getLocale() {

        return m_locale;
    }

}
