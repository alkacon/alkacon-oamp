/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterMailData.java,v $
 * Date   : $Date: 2007/11/19 15:49:15 $
 * Version: $Revision: 1.4 $
 *
 * This file is part of the Alkacon OpenCms Add-On Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Package.  
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
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.mail.CmsSimpleMail;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsHtmlExtractor;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.Email;

/**
 * Generates newsletter emails and the list of recipients from a newsletter structured content VFS file.<p>
 * 
 * Provides some utility methods to generate email previews and get the email contents as string.<p>
 *  
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterMailData extends A_CmsNewsletterMailData {

    /** The node name for the ConfFile node. */
    protected static final String NODE_CONFFILE = "ConfFile";

    /** The node name for the HTML node. */
    protected static final String NODE_HTML = "Html";

    /** The node name for the MailFoot node. */
    protected static final String NODE_MAILFOOT = "MailFoot";

    /** The node name for the MailHead node. */
    protected static final String NODE_MAILHEAD = "MailHead";

    /** The node name for the Text node. */
    protected static final String NODE_TEXT = "Text";

    /** Resource type name of a newsletter resource. */
    public static final String RESOURCETYPE_NEWSLETTER_NAME = "alkacon-newsletter";

    /** The xpath for the Config node including trailing "/". */
    protected static final String XPATH_CONFIG = "Config/";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMailData.class);

    /**
     * Empty constructor.<p>
     * 
     * Be sure to call {@link #initialize(CmsJspActionElement, CmsGroup, String)} to get correct results.<p>
     */
    public CmsNewsletterMailData() {

        // noop
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * @param fileName the fileName of the newsletter
     * @param group the group to send the newsletter to
     * @param jsp the JSP action element
     * @throws CmsException if reading or unmarshalling the file fails
     */
    public CmsNewsletterMailData(String fileName, CmsGroup group, CmsJspActionElement jsp)
    throws CmsException {

        initialize(jsp, group, fileName);
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

}
