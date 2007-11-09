/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterSubscriptionBean.java,v $
 * Date   : $Date: 2007/11/09 13:43:43 $
 * Version: $Revision: 1.6 $
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

import com.alkacon.opencms.commons.CmsStringCrypter;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;
import org.opencms.i18n.CmsMessages;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsHtmlExtractor;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Provides methods for the newsletter subscription JSP frontend.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterSubscriptionBean extends CmsJspActionElement {

    /** The name of the action: confirm the subscription. */
    public static final int ACTION_CONFIRMSUBSCRIPTION = 2;

    /** The name of the action confirm the unsubscription. */
    public static final int ACTION_CONFIRMUNSUBSCRIPTION = 3;

    /** The name of the action: subscribe. */
    public static final int ACTION_SUBSCRIBE = 0;

    /** The name of the action: unsubscribe. */
    public static final int ACTION_UNSUBSCRIBE = 1;

    /** The name of the action request parameter. */
    public static final String PARAM_ACTION = "action";

    /** The name of the email request parameter. */
    public static final String PARAM_EMAIL = "email";

    /** The name of the file request parameter. */
    public static final String PARAM_FILE = "file";

    /** The password used for encryption and decryption actions. */
    private static final String CRYPT_PASSWORD = "YwqP-82h";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterSubscriptionBean.class);

    /** The email macro that can be used in the configuration content. */
    private static final String MACRO_EMAIL = "email";

    /** The link macro that can be used in the configuration content. */
    private static final String MACRO_LINK = "link";

    /** The title macro that can be used in the configuration content. */
    private static final String MACRO_TITLE = "title";

    /** The node name for the Confirm node. */
    private static final String NODE_CONFIRM = "Confirm";

    /** The node name for the Error node. */
    private static final String NODE_ERROR = "Error";

    /** The node name for the MailFrom node. */
    private static final String NODE_MAILFROM = "MailFrom";

    /** The node name for the MailingList node. */
    private static final String NODE_MAILINGLIST = "MailingList";

    /** The node name for the MailSubject node. */
    private static final String NODE_MAILSUBJECT = "MailSubject";

    /** The node name for the MailText node. */
    private static final String NODE_MAILTEXT = "MailText";

    /** The node name for the Ok node. */
    private static final String NODE_OK = "Ok";

    /** The node name for the SubscribeError node. */
    private static final String NODE_SUBSCRIBE_ERROR = "SubscribeError";

    /** The node name for the SubscribeOk node. */
    private static final String NODE_SUBSCRIBE_OK = "SubscribeOk";

    /** The node name for the UnSubscribeError node. */
    private static final String NODE_UNSUBSCRIBE_ERROR = "UnSubscribeError";

    /** The node name for the UnSubscribeOk node. */
    private static final String NODE_UNSUBSCRIBE_OK = "UnSubscribeOk";

    /** The xpath to the confirm subnodes. */
    private static final String XPATH_1_CONFIRM = "Confirm/";

    /** The xpath to the subscribe subnodes. */
    private static final String XPATH_1_SUBSCRIBE = "Subscribe/";

    /** The xpath to the confirm mail subnodes. */
    private static final String XPATH_2_MAIL = XPATH_1_CONFIRM + "Mail/";

    /** The xpath to the confirm subscribe subnodes. */
    private static final String XPATH_2_SUBSCRIBE = XPATH_1_CONFIRM + "Subscribe/";

    /** The xpath to the confirm unsubscribe subnodes. */
    private static final String XPATH_2_UNSUBSCRIBE = XPATH_1_CONFIRM + "UnSubscribe/";

    /** The action that should be performed on the subscription page. */
    private int m_action;

    /** The checked action used to redisplay the form. */
    private int m_checkedAction;

    /** The subscription page configuration content. */
    private CmsXmlContent m_configContent;

    /** The email address for the newsletter subscribe/unsubscribe actions. */
    private String m_email;

    private List m_errors;

    /** the localized messages. */
    private CmsMessages m_messages;

    /** The resolver to use on the subscription page configuration texts. */
    private CmsMacroResolver m_resolver;

    /**
     * Empty constructor, required for every JavaBean.
     */
    public CmsNewsletterSubscriptionBean() {

        super();
    }

    /**
     * Constructor, with parameters.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsNewsletterSubscriptionBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super();
        init(context, req, res);
    }

    /**
     * Performs subscription confirmation action and returns the text ouput for the result page.<p>
     * 
     * @return the text ouput for the result page, according to the success of the subscription action
     */
    public String actionConfirmSubscribe() {

        String result = getConfigText(XPATH_2_SUBSCRIBE + NODE_ERROR);
        if (CmsStringUtil.isNotEmpty(getEmail())) {
            // try to activate the newsletter user
            if (getNewsletterManager().activateNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST))) {
                result = getConfigText(XPATH_2_SUBSCRIBE + NODE_OK);
            }
        }
        return result;
    }

    /**
     * Performs unsubscribe confirmation action and returns the text ouput for the result page.<p>
     * 
     * @return the text ouput for the result page, according to the success of the unsubscribe confirmation action
     */
    public String actionConfirmUnsubscribe() {

        String result = getConfigText(XPATH_2_UNSUBSCRIBE + NODE_ERROR);
        if (CmsStringUtil.isNotEmpty(getEmail())) {
            // try to delete the newsletter user
            if (getNewsletterManager().deleteNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST), isConfirmationEnabled())) {
                result = getConfigText(XPATH_2_UNSUBSCRIBE + NODE_OK);
            }
        }
        return result;
    }

    /**
     * Performs the subscription action and returns the text output for the result page.<p>
     * 
     * @return the text output for the result page, according to the success of the subscription action
     */
    public String actionSubscribe() {

        String result = getConfigText(XPATH_1_SUBSCRIBE + NODE_SUBSCRIBE_ERROR);

        if (CmsStringUtil.isNotEmpty(getEmail())) {
            // create the newsletter user in the configured newsletter group
            String groupName = getConfigText(NODE_MAILINGLIST);
            CmsUser user = getNewsletterManager().createNewsletterUser(getEmail(), groupName, !isConfirmationEnabled());
            if (user != null) {
                if (isConfirmationEnabled()) {
                    setLinkMacro(ACTION_CONFIRMSUBSCRIPTION, getEmail());
                    if (sendConfirmationMail(
                        getConfigText(XPATH_2_SUBSCRIBE + NODE_MAILSUBJECT),
                        getConfigText(XPATH_2_SUBSCRIBE + NODE_MAILTEXT))) {
                        result = getConfigText(XPATH_1_SUBSCRIBE + NODE_SUBSCRIBE_OK);
                    }
                } else {
                    result = getConfigText(XPATH_1_SUBSCRIBE + NODE_SUBSCRIBE_OK);
                }
            }
        }
        return result.toString();
    }

    /**
     * Performs unsubscribe action and returns the text output for the result page.<p>
     * 
     * @return the text output for the result page, according to the success of the unsubscribe action
     */
    public String actionUnsubscribe() {

        String result = getConfigText(XPATH_1_SUBSCRIBE + NODE_UNSUBSCRIBE_ERROR);
        if (CmsStringUtil.isNotEmpty(getEmail())) {
            if (isConfirmationEnabled()) {
                // email confirmation is enabled, mark the user to be deleted
                if (getNewsletterManager().markToDeleteNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST))) {
                    setLinkMacro(ACTION_CONFIRMUNSUBSCRIPTION, getEmail());
                    if (sendConfirmationMail(
                        getConfigText(XPATH_2_UNSUBSCRIBE + NODE_MAILSUBJECT),
                        getConfigText(XPATH_2_UNSUBSCRIBE + NODE_MAILTEXT))) {
                        result = getConfigText(XPATH_1_SUBSCRIBE + NODE_UNSUBSCRIBE_OK);
                    }
                }
            } else if (getNewsletterManager().deleteNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST), false)) {
                result = getConfigText(XPATH_1_SUBSCRIBE + NODE_UNSUBSCRIBE_OK);
            }

        }
        return result;
    }

    /**
     * Returns the action that should be performed on the subscription page.<p>
     *  
     * @return the action that should be performed on the subscription page
     */
    public int getAction() {

        return m_action;
    }

    /**
     * Returns the checked action used to redisplay the form.<p>
     *  
     * @return the checked action used to redisplay the form
     */
    public int getCheckedAction() {

        return m_checkedAction;
    }

    /**
     * Returns the configuration text for the specified element, can also be a xpath. Macros like email address are resolved.<p>
     * 
     * Example for element nodes: "Title", "Subscribe/Headline".<p> 
     * 
     * @param element the element to get the value for
     * @return the configuration text for the specified element
     */
    public String getConfigText(String element) {

        String result = getConfigContent().getStringValue(getCmsObject(), element, getRequestContext().getLocale());
        return getMacroResolver().resolveMacros(result);
    }

    /**
     * Returns the email address for the newsletter subscribe/unsubscribe actions.<p>
     * 
     * @return the email address for the newsletter subscribe/unsubscribe actions
     */
    public String getEmail() {

        if (CmsStringUtil.isEmpty(m_email)) {
            return "";
        }
        return m_email;
    }

    /**
     * Returns the errors found during validation.<p>
     * 
     * @return the errors found during validation
     */
    public List getErrors() {

        return m_errors;
    }

    /**
     * Returns the localized messages.<p>
     * 
     * @return the localized messages
     */
    public CmsMessages getMessages() {

        if (m_messages == null) {
            m_messages = new CmsMessages(
                CmsNewsletterManager.MODULE_NAME + ".workplace",
                getRequestContext().getLocale());
        }
        return m_messages;
    }

    /**
     * Returns the found validation errors formatted as HTML.<p>
     * 
     * Each error is enclosed with the provided element name.<p>
     * 
     * @param element the element name to enclose each error with
     * @return the found validation errors formatted as HTML
     */
    public String getValidationErrorsHtml(String element) {

        StringBuffer result = new StringBuffer(2048);
        Iterator i = getErrors().iterator();
        while (i.hasNext()) {
            // loop the error messages
            String error = (String)i.next();
            result.append("\t<").append(element).append(">");
            result.append(error);
            result.append("</").append(element).append(">\n");
        }
        return result.toString();
    }

    /**
     * Initializes the necessary subscription parameters and performs the subscription actions.<p>
     * 
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        // call super initialization
        super.init(context, req, res);
        // initialize error map
        m_errors = new ArrayList();
        // initialize members from request parameters
        m_action = -1;
        String action = req.getParameter(PARAM_ACTION);
        if (CmsStringUtil.isNotEmpty(action)) {
            try {
                m_action = Integer.parseInt(action);
                m_checkedAction = m_action;
                m_email = req.getParameter(PARAM_EMAIL);
                if (m_action == ACTION_CONFIRMSUBSCRIPTION || m_action == ACTION_CONFIRMUNSUBSCRIPTION) {
                    // decrypt email parameter if action is a confirm action
                    m_email = CmsStringCrypter.decrypt(m_email, CRYPT_PASSWORD);
                }
            } catch (NumberFormatException e) {
                // no valid action found, this is handled in the validate method
            }
            // validate the parameters
            validate();

        }
    }

    /**
     * Returns if the subscription confirmation is enabled.<p>
     * 
     * @return true if the subscription confirmation is enabled, otherwise false
     */
    public boolean isConfirmationEnabled() {

        String value = getConfigText(XPATH_1_CONFIRM + NODE_CONFIRM);
        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * Returns if validation errors are found for the subscription process.<p>
     * 
     * @return true if validation errors are found, otherwise false
     */
    public boolean isValid() {

        return getErrors().size() == 0;
    }

    /**
     * Returns the localized resource String for a given message key.<p>
     * 
     * @param keyName the key for the desired String
     * @return the resource string for the given key it it exists, or the empty String if not
     */
    public String key(String keyName) {

        return getMessages().keyDefault(keyName, "");
    }

    /**
     * Returns the subscription page configuration content.<p>
     * 
     * @return the subscription page configuration content
     */
    private CmsXmlContent getConfigContent() {

        if (m_configContent == null) {
            try {
                String uri = getRequestContext().getUri();
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getRequest().getParameter(PARAM_FILE))) {
                    uri = getRequest().getParameter(PARAM_FILE);
                }
                CmsFile file = getCmsObject().readFile(uri);
                m_configContent = CmsXmlContentFactory.unmarshal(getCmsObject(), file);
            } catch (CmsException e) {
                // error reading configuration content
            }
        }
        return m_configContent;
    }

    /**
     * Returns the resolver to use on the subscription page configuration texts.<p>
     * 
     * @return the resolver to use on the subscription page configuration texts
     */
    private CmsMacroResolver getMacroResolver() {

        if (m_resolver == null) {
            m_resolver = CmsMacroResolver.newInstance();
            m_resolver.setCmsObject(getCmsObject());
            m_resolver.addMacro(MACRO_EMAIL, getEmail());
        }
        return m_resolver;
    }

    /**
     * Returns the newsletter manager instance to work with for user management and email functions.<p>
     * 
     * @return the newsletter manager instance to work with for user management and email functions
     */
    private CmsNewsletterManager getNewsletterManager() {

        CmsModule module = OpenCms.getModuleManager().getModule(CmsNewsletterManager.MODULE_NAME);
        return (CmsNewsletterManager)module.getActionInstance();
    }

    /**
     * Sends the confirmation emails for subscription or unsubscription of a newsletter recipient.<p>
     * 
     * @param subject the mail subject to use
     * @param text the mail text to use
     * @return true if the confirmation mail was successfully sent, otherwise false
     */
    private boolean sendConfirmationMail(String subject, String text) {

        CmsHtmlMail mail = new CmsHtmlMail();

        try {
            // set the email addresses
            mail.addTo(getEmail());
            mail.setFrom(getConfigText(XPATH_2_MAIL + NODE_MAILFROM));
            // set the subject and title macro
            mail.setSubject(subject);
            setTitleMacro(subject);
            // set the mail encoding
            mail.setCharset(getCmsObject().getRequestContext().getEncoding());
            // create the mail message
            StringBuffer msg = new StringBuffer(4096);
            // first the html message
            String mailHead = "";
            String mailFoot = "";
            boolean foundExternalConfig = false;
            if (getConfigContent().hasValue(
                XPATH_2_MAIL + CmsNewsletterMailData.NODE_CONFFILE,
                getRequestContext().getLocale())) {
                String path = getConfigText(XPATH_2_MAIL + CmsNewsletterMailData.NODE_CONFFILE);
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(path)
                    && getCmsObject().existsResource(path)
                    && !CmsResource.isFolder(path)) {
                    // use external mail configuration file
                    CmsFile mailConfig = getCmsObject().readFile(path);
                    CmsXmlContent mailContent = CmsXmlContentFactory.unmarshal(getCmsObject(), mailConfig);
                    mailHead = mailContent.getStringValue(
                        getCmsObject(),
                        CmsNewsletterMailData.NODE_MAILHEAD,
                        getRequestContext().getLocale());
                    mailFoot = mailContent.getStringValue(
                        getCmsObject(),
                        CmsNewsletterMailData.NODE_MAILFOOT,
                        getRequestContext().getLocale());
                    foundExternalConfig = true;
                }
            }
            if (!foundExternalConfig) {
                // use internal mail configuration fields
                mailHead = getConfigText(XPATH_2_MAIL + CmsNewsletterMailData.NODE_MAILHEAD);
                mailFoot = getConfigText(XPATH_2_MAIL + CmsNewsletterMailData.NODE_MAILFOOT);
            }

            msg.append(getMacroResolver().resolveMacros(mailHead));
            msg.append(text);
            msg.append(getMacroResolver().resolveMacros(mailFoot));
            mail.setHtmlMsg(msg.toString());
            // second the text message
            mail.setTextMsg(CmsHtmlExtractor.extractText(text, getCmsObject().getRequestContext().getEncoding()));
            // send the mail
            mail.send();
            return true;
        } catch (Exception e) {
            // in case of an error, return false
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_MAIL_CONFIRMATION_1, getEmail()), e);
            }
            return false;
        }
    }

    /**
     * Sets the action that should be performed on the subscription page.<p>
     *  
     * @param action the action that should be performed on the subscription page
     */
    private void setAction(int action) {

        m_action = action;
    }

    /**
     * Creates the link for the confirmation email and adds the macro to the macro resolver.<p>
     * 
     * @param action the action to insert as request parameter value
     * @param email the email address for the confirmation
     */
    private void setLinkMacro(int action, String email) {

        StringBuffer result = new StringBuffer(1024);

        result.append("<a href=\"");
        result.append(OpenCms.getSiteManager().getCurrentSite(getCmsObject()).getUrl());
        StringBuffer link = new StringBuffer(1024);
        link.append(getRequestContext().getUri());
        // set action parameter
        link.append("?").append(PARAM_ACTION).append("=").append(action);
        // send encrypted email address as request parameter
        link.append("&").append(PARAM_EMAIL).append("=").append(CmsStringCrypter.encrypt(email, CRYPT_PASSWORD));
        result.append(link(link.toString()));
        result.append("\">");
        result.append(OpenCms.getSiteManager().getCurrentSite(getCmsObject()).getUrl());
        result.append(link(link.toString()));
        result.append("</a>");
        // add the link macro to the resolver
        getMacroResolver().addMacro(MACRO_LINK, result.toString());
    }

    /**
     * Adds the title macro to the macro resolver.<p>
     * 
     * @param title the title to add as macro value
     */
    private void setTitleMacro(String title) {

        getMacroResolver().addMacro(MACRO_TITLE, title);
    }

    /**
     * Validates the necessary parameters for the subscription actions and adds error messages to the list of errors.<p>
     */
    private void validate() {

        // check the action
        if (getAction() < 0 || getAction() > 3) {
            m_errors.add(key("validation.alknewsletter.error.action"));
            setAction(-1);
        }

        // check the email address
        boolean resetAction = false;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getEmail())) {
            m_errors.add(key("validation.alknewsletter.error.noemail"));
            resetAction = true;
        } else if (!CmsNewsletterManager.isValidEmail(getEmail())) {
            m_errors.add(key("validation.alknewsletter.error.invalidemail"));
            resetAction = true;
        }

        // check user existance depending on action if email address is valid
        if (!resetAction) {
            if (getAction() == ACTION_SUBSCRIBE && getNewsletterManager().existsNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST))) {
                m_errors.add(key("validation.alknewsletter.error.userexists"));
                resetAction = true;
            } else if (getAction() == ACTION_UNSUBSCRIBE && !getNewsletterManager().existsNewsletterUser(getEmail(), getConfigText(NODE_MAILINGLIST))) {
                m_errors.add(key("validation.alknewsletter.error.usernotexists"));
                resetAction = true;
            }

        }

        // reset the action if errors were found
        if (resetAction && (getAction() == ACTION_SUBSCRIBE || getAction() == ACTION_UNSUBSCRIBE)) {
            setAction(-1);
        }
    }

}
