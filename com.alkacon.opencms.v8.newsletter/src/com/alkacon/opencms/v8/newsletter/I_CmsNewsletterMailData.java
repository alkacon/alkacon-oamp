/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.newsletter/src/com/alkacon/opencms/v8/newsletter/I_CmsNewsletterMailData.java,v $
 * Date   : $Date: 2010/10/14 13:17:50 $
 * Version: $Revision: 1.9 $
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

import org.opencms.file.CmsGroup;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.xml.content.CmsXmlContent;

import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;

/**
 * Provides methods to generate newsletter emails and the list of recipients in different ways,
 * e.g. using a different structured content.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.9 $ 
 * 
 * @since 7.0.3 
 */
public interface I_CmsNewsletterMailData {

    /**
     * Returns the newsletter xml content.<p>
     * 
     * @return the newsletter xml content
     */
    CmsXmlContent getContent();

    /**
     * Returns the mail to send as newsletter, with set subject, text and from address.<p>
     * 
     * @return the mail to send as newsletter
     * @throws CmsException if generating the email content fails
     */
    Email getEmail() throws CmsException;

    /**
     * Returns the email content to be shown in a preview, generates a valid HTML page that can be used.<p>
     * 
     * @return the email content to be shown in a preview as HTML page
     * @throws CmsException if generating the email preview fails
     */
    String getEmailContentPreview() throws CmsException;

    /**
     * Returns the email content to be shown in a preview, generates a valid HTML page or page parts that can be used.<p>
     * 
     * @param onlyPartialHtml sets if only page parts should be returned instead of a complete HTML page
     * 
     * @return the email content to be shown in a preview as HTML page or page parts
     * @throws CmsException if generating the email preview fails
     */
    String getEmailContentPreview(boolean onlyPartialHtml) throws CmsException;

    /**
     * Returns the recipients of the newsletter mail.<p>
     * 
     * @return the recipients of the newsletter mail
     * @throws CmsException if getting the recipients from a mailing list group fails
     */
    List<InternetAddress> getRecipients() throws CmsException;

    /**
     * Returns the resource type name of the newsletter XML content to use.<p>
     * 
     * @return the resource type name of the newsletter XML content to use
     */
    String getResourceTypeName();

    /**
     * Initializes the necessary members to generate the email and the list of recipients.<p>
     * 
     * @param jsp the current action element
     * @param group the mailing list group to send the newsletter to
     * @param fileName the fileName of a VFS file that can be used to generate the newsletter
     * @throws CmsException if reading the VFS file fails
     */
    void initialize(CmsJspActionElement jsp, CmsGroup group, String fileName) throws CmsException;

    /**
     * Initializes the necessary members to generate the email and the list of recipients.<p>
     * 
     * @param jsp the current action element
     * @param ou the organizational unit to send the newsletter to
     * @param fileName the fileName of a VFS file that can be used to generate the newsletter
     * @throws CmsException if reading the VFS file fails
     */
    void initialize(CmsJspActionElement jsp, CmsOrganizationalUnit ou, String fileName) throws CmsException;

    /**
     * Initializes the necessary members to generate the email and the list of recipients.<p>
     * 
     * @param jsp the current action element
     * @param recipients the recipients of the newsletter mail
     * @param fileName the fileName of a VFS file that can be used to generate the newsletter
     * @throws CmsException if reading the VFS file fails
     */
    void initialize(CmsJspActionElement jsp, List<InternetAddress> recipients, String fileName) throws CmsException;

    /**
     * Checks if the newsletter can be sent or not.<p>
     * 
     * This method can also write some data like the timestamp when the newsletter has been sent
     * or the mailing list which received it to e.g. VFS properties.<p>
     * 
     * @return true if newsletter can be sent to subscribers, otherwise false
     * @throws CmsException if something goes wrong
     */
    boolean isSendable() throws CmsException;

    /**
     * Sets the recipients of the newsletter mail.<p>
     * 
     * @param recipients the recipients of the newsletter mail
     */
    void setRecipients(List<InternetAddress> recipients);

}
