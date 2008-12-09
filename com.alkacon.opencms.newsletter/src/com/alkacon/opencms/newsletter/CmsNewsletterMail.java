/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterMail.java,v $
 * Date   : $Date: 2008/12/09 14:29:28 $
 * Version: $Revision: 1.12 $
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

import org.opencms.main.CmsLog;

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
 * @version $Revision: 1.12 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterMail extends Thread {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMail.class);

    /** The email data to send. */
    private I_CmsNewsletterMailData m_mailData;

    /** The name of the newsletter to send. */
    private String m_newsletterName;

    /** The newsletter mail recipients of type {@link InternetAddress}. */
    private List m_recipients;

    /**
     * Constructor, with parameters.<p>
     * 
     * @param mailData the email to send
     * @param recipients the newsletter mail recipients
     * @param newsletterName the name of the newsletter to send
     */
    public CmsNewsletterMail(I_CmsNewsletterMailData mailData, List recipients, String newsletterName) {

        m_mailData = mailData;
        m_recipients = recipients;
        m_newsletterName = newsletterName;
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {

        try {
            sendMail();
        } catch (Throwable t) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERROR_NEWSLETTER_SEND_FAILED_1,
                    getNewsletterName()), t);
            }
        }
    }

    /**
     * Sends the newsletter mails to the recipients.<p>
     */
    public void sendMail() {

        Iterator i = getRecipients().iterator();
        while (i.hasNext()) {
            InternetAddress to = (InternetAddress)i.next();
            List toList = new ArrayList(1);
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
    private List getRecipients() {

        return m_recipients;
    }

}
