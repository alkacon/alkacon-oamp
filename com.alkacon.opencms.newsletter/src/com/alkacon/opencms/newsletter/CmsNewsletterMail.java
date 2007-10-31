/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterMail.java,v $
 * Date   : $Date: 2007/10/31 15:23:02 $
 * Version: $Revision: 1.7 $
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

import org.opencms.main.CmsLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.Email;

/**
 * Sends newsletter emails to users that are subscribed to the newsletter.<p>
 *  
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterMail extends Thread {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterMail.class);

    /** The email to send. */
    private Email m_mail;

    /** The name of the newsletter to send. */
    private String m_newsletterName;

    /** The newsletter mail recipients of type {@link InternetAddress}. */
    private List m_recipients;

    /**
     * Constructor, with parameters.<p>
     * 
     * @param mail the email to send
     * @param recipients the newsletter mail recipients
     * @param newsletterName the name of the newsletter to send
     */
    public CmsNewsletterMail(Email mail, List recipients, String newsletterName) {

        m_mail = mail;
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
            Email mail = getMail();
            mail.setTo(toList);
            try {
                mail.send();
            } catch (MessagingException e) {
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
     * Returns the email to send.<p>
     * 
     * @return the email to send
     */
    private Email getMail() {

        return m_mail;
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
