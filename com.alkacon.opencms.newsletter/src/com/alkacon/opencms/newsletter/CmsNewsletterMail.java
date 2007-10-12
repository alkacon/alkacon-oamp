/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterMail.java,v $
 * Date   : $Date: 2007/10/12 15:19:09 $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.mail.CmsSimpleMail;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
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

/**
 * Generates newsletter emails and sends them to users that subscribed to the newsletter.<p>
 * 
 * Provides some utility methods to generate email previews and get the email contents.<p>
 *  
 * @author Andreas Zahner
 */
public class CmsNewsletterMail {

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

    /** The xpath for the Config node including trailing "/". */
    protected static final String XPATH_CONFIG = "Config/";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMail.class);

    /** The OpenCms user context.<p> */
    private CmsObject m_cms;

    /** The newsletter content. */
    private CmsXmlContent m_content;

    /** The group to send the newsletter to. */
    private CmsGroup m_group;

    /** The Locale to use to read the newsletter content. */
    private Locale m_locale;

    /**
     * Constructor, with parameters.<p>
     * 
     * @param fileName the fileName of the newsletter
     * @param group the group to send the newsletter to
     * @param cms the current OpenCms user context
     * @param locale the locale to use for the content
     * @throws CmsException if reading or unmarshalling the file fails
     */
    public CmsNewsletterMail(String fileName, CmsGroup group, CmsObject cms, Locale locale)
    throws CmsException {

        CmsFile file = cms.readFile(fileName);
        m_content = CmsXmlContentFactory.unmarshal(cms, file);
        m_group = group;
        m_cms = cms;
        m_locale = locale;
    }

    /**
     * Returns the email content from the specified newsletter file.<p>
     * 
     * @param content the unmarshalled content of the newsletter
     * @param cms the current OpenCms user context
     * @param locale the locale to use for the content
     * @return the email content
     * @throws CmsException if unmarshalling the file fails
     */
    public static String getEmailContent(CmsXmlContent content, CmsObject cms, Locale locale) throws CmsException {

        String text = content.getStringValue(cms, NODE_TEXT, locale);
        boolean isHtmlMail = Boolean.valueOf(content.getStringValue(cms, XPATH_CONFIG + NODE_HTML, locale)).booleanValue();
        if (isHtmlMail) {
            // create the content of the HTML mail
            StringBuffer mailHtml = new StringBuffer(4096);
            String mailHead = "";
            String mailFoot = "";
            boolean foundExternalConfig = false;
            if (content.hasValue(XPATH_CONFIG + NODE_CONFFILE, locale)) {
                // optional external configuration file specified, use this as mail configuration
                String path = content.getStringValue(cms, XPATH_CONFIG + NODE_CONFFILE, locale);
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(path)
                    && cms.existsResource(path)
                    && !CmsResource.isFolder(path)) {
                    CmsFile mailConfig = cms.readFile(path);
                    CmsXmlContent mailContent = CmsXmlContentFactory.unmarshal(cms, mailConfig);
                    // get the mail head and foot from the external configuration file content
                    if (mailContent.hasValue(NODE_MAILHEAD, locale)) {
                        mailHead = mailContent.getStringValue(cms, NODE_MAILHEAD, locale);
                        mailFoot = mailContent.getStringValue(cms, NODE_MAILFOOT, locale);
                        foundExternalConfig = true;
                    }
                }
            }
            if (!foundExternalConfig) {
                // no external configuration specified, use internal configuration values
                mailHead = content.getStringValue(cms, XPATH_CONFIG + NODE_MAILHEAD, locale);
                mailFoot = content.getStringValue(cms, XPATH_CONFIG + NODE_MAILFOOT, locale);
            }
            mailHtml.append(mailHead);
            mailHtml.append(text);
            mailHtml.append(mailFoot);
            return mailHtml.toString();
        } else {
            // create the content of the text mail
            try {
                return CmsHtmlExtractor.extractText(text, cms.getRequestContext().getEncoding());
            } catch (Exception e) {
                // error extracting text, return unmodified text                
                return text;
            }
        }
    }

    /**
     * Returns the email content from the specified newsletter file.<p>
     * 
     * @param fileName the fileName of the newsletter
     * @param cms the current OpenCms user context
     * @param locale the locale to use for the content
     * @return the email content
     * @throws CmsException if reading or unmarshalling the file fails
     */
    public static String getEmailContent(String fileName, CmsObject cms, Locale locale) throws CmsException {

        CmsFile file = cms.readFile(fileName);
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(cms, file);
        return getEmailContent(content, cms, locale);
    }

    /**
     * Returns the email content to be shown in a preview, generates a valid html page that can be used.<p>
     *  
     * @param fileName the fileName of the newsletter to preview
     * @param cms the current OpenCms user context
     * @param locale the locale to use for the content
     * @return the email content to be shown in a preview as html page
     * @throws CmsException if reading or unmarshalling the file fails
     */
    public static String getEmailContentPreview(String fileName, CmsObject cms, Locale locale) throws CmsException {

        String result = getEmailContent(fileName, cms, locale);
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
     * Sends the newsletter mails to the recipients (members of the group specified in the constructor).<p>
     * 
     * @throws CmsException if reading the group users or getting the email content fails
     */
    public void sendMail() throws CmsException {

        // get the email data from the content fields
        String from = getContent().getStringValue(getCms(), NODE_FROM, getLocale());

        // create the list of recipients of the newsletter
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

        // get subject and mail text
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
            mail.setHtmlMsg(getEmailContent(getContent(), getCms(), getLocale()));
            // extract the text from the HTML field
            try {
                text = CmsHtmlExtractor.extractText(text, getCms().getRequestContext().getEncoding());
            } catch (Exception e) {
                // cleaning text failed
            }
            mail.setTextMsg(text);
            // set the mail encoding
            mail.setCharset(getCms().getRequestContext().getEncoding());
            i = recipients.iterator();
            while (i.hasNext()) {
                InternetAddress to = (InternetAddress)i.next();
                List toList = new ArrayList(1);
                toList.add(to);
                mail.setTo(toList);
                try {
                    mail.send();
                } catch (MessagingException e) {
                    // log failed mail send process
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(
                            Messages.LOG_ERROR_NEWSLETTER_EMAIL_SEND_FAILED_2,
                            to.getAddress(),
                            getContent().getFile().getRootPath()));
                    }
                }
            }
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
            i = recipients.iterator();
            while (i.hasNext()) {
                InternetAddress to = (InternetAddress)i.next();
                List toList = new ArrayList(1);
                toList.add(to);
                mail.setTo(toList);
                try {
                    mail.send();
                } catch (MessagingException e) {
                    // log failed mail send process
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(
                            Messages.LOG_ERROR_NEWSLETTER_EMAIL_SEND_FAILED_2,
                            to.getAddress(),
                            getContent().getFile().getRootPath()));
                    }
                }
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
     * Returns the newsletter content.<p>
     * 
     * @return the newsletter content
     */
    private CmsXmlContent getContent() {

        return m_content;
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
