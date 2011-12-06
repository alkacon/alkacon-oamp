/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.newsletter/src/com/alkacon/opencms/v8/newsletter/CmsNewsletterMail.java,v $
 * Date   : $Date: 2010/10/14 13:17:49 $
 * Version: $Revision: 1.13 $
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

package com.alkacon.opencms.v8.newsletter;

import org.opencms.mail.CmsSimpleMail;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.Email;

/**
 * Sends newsletter emails to users that are subscribed to the mailing list.<p>
 *  
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.13 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterMail extends Thread {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMail.class);

    /** The email data to send. */
    private I_CmsNewsletterMailData m_mailData;

    /** The error messages thrown while sending the newsletter. */
    private List<String> m_mailErrors;

    /** The name of the newsletter to send. */
    private String m_newsletterName;

    /** The newsletter mail recipients of type {@link InternetAddress}. */
    private List<InternetAddress> m_recipients;

    /** The email address to send an error report to. */
    private String m_reportRecipientAddress;

    /**
     * Constructor, with parameters.<p>
     * 
     * @param mailData the email to send
     * @param recipients the newsletter mail recipients
     * @param reportRecipientAddress the email address to send a report to
     * @param newsletterName the name of the newsletter to send
     */
    public CmsNewsletterMail(
        I_CmsNewsletterMailData mailData,
        List<InternetAddress> recipients,
        String reportRecipientAddress,
        String newsletterName) {

        m_mailData = mailData;
        m_recipients = recipients;
        m_reportRecipientAddress = reportRecipientAddress;
        m_newsletterName = newsletterName;
        m_mailErrors = new ArrayList<String>();
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        try {
            // send the newsletter mails
            sendMail();
        } catch (Throwable t) {
            // general failure, log it and add detailed error message to report
            if (LOG.isErrorEnabled()) {
                LOG.error(
                    Messages.get().getBundle().key(Messages.LOG_ERROR_NEWSLETTER_SEND_FAILED_1, getNewsletterName()),
                    t);
            }
            getMailErrors().add(
                0,
                Messages.get().getBundle().key(
                    Messages.MAIL_ERROR_NEWSLETTER_SEND_FAILED_2,
                    getNewsletterName(),
                    t.getLocalizedMessage())
                    + "\n");
        }
        if (!getMailErrors().isEmpty() && CmsStringUtil.isNotEmptyOrWhitespaceOnly(getReportRecipientAddress())) {
            // there were errors found while sending the newsletter, send error report mail
            CmsSimpleMail errorMail = new CmsSimpleMail();
            try {
                // set from address using the newsletter configuration
                errorMail.setFrom(m_mailData.getEmail().getFromAddress().getAddress());
            } catch (Exception e) {
                // failed to set from address in error report mail
                getMailErrors().add(
                    0,
                    Messages.get().getBundle().key(
                        Messages.MAIL_ERROR_EMAIL_FROM_ADDRESS_1,
                        m_mailData.getContent().getFile().getRootPath())
                        + "\n");
            }
            try {
                errorMail.addTo(getReportRecipientAddress());
                errorMail.setSubject(Messages.get().getBundle().key(Messages.MAIL_ERROR_SUBJECT_1, getNewsletterName()));
                // generate the error report mail content
                StringBuffer msg = new StringBuffer(1024);
                msg.append(Messages.get().getBundle().key(Messages.MAIL_ERROR_BODY_1, getNewsletterName()));
                msg.append("\n\n");
                for (Iterator<String> i = getMailErrors().iterator(); i.hasNext();) {
                    // loop the stored error messages
                    msg.append(i.next());
                    if (i.hasNext()) {
                        msg.append("\n");
                    }
                }
                errorMail.setMsg(msg.toString());
                // send the error report mail
                errorMail.send();
            } catch (Throwable t) {
                // failed to send error mail, log failure
                if (LOG.isErrorEnabled()) {
                    LOG.error(
                        Messages.get().getBundle().key(
                            Messages.LOG_ERROR_MAIL_REPORT_FAILED_1,
                            getReportRecipientAddress()),
                        t);
                }
            }
        }
    }

    /**
     * Sends the newsletter mails to the recipients.<p>
     */
    public void sendMail() {

        Iterator<InternetAddress> i = getRecipients().iterator();
        int errLogCount = 0;
        while (i.hasNext()) {
            InternetAddress to = i.next();
            List<InternetAddress> toList = new ArrayList<InternetAddress>(1);
            toList.add(to);
            try {
                Email mail = getMailData().getEmail();
                mail.setTo(toList);
                mail.send();
            } catch (Exception e) {
                // log failed mail send process
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERROR_NEWSLETTER_EMAIL_SEND_FAILED_2,
                        to.getAddress(),
                        getNewsletterName()));
                }
                if (LOG.isDebugEnabled() && (errLogCount < 10)) {
                    LOG.debug(e);
                    errLogCount++;
                }
                // store message for error report mail
                String errMsg = Messages.get().getBundle().key(Messages.MAIL_ERROR_EMAIL_ADDRESS_1, to.getAddress());
                if (errLogCount == 10) {
                    errMsg += "\nStack:\n" + errMsg + "\n";
                }
                getMailErrors().add(errMsg);
            }
        }
    }

    /**
     * Returns the email data to send.<p>
     * 
     * @return the email data to send
     */
    private I_CmsNewsletterMailData getMailData() {

        return m_mailData;
    }

    /**
     * Returns the error messages thrown while sending the newsletter.<p>
     * 
     * @return the error messages thrown while sending the newsletter
     */
    private List<String> getMailErrors() {

        return m_mailErrors;
    }

    /**
     * Returns the name of the newsletter to send.<p>
     * 
     * @return the name of the newsletter to send
     */
    private String getNewsletterName() {

        return m_newsletterName;
    }

    /**
     * Returns the newsletter mail recipients of type {@link InternetAddress}.<p>
     * 
     * @return the newsletter mail recipients
     */
    private List<InternetAddress> getRecipients() {

        return m_recipients;
    }

    /**
     * Returns the email address to send an error report to.<p>
     * 
     * @return the email address to send an error report to
     */
    private String getReportRecipientAddress() {

        return m_reportRecipientAddress;
    }

}
