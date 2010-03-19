/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/CmsCommentForm.java,v $
 * Date   : $Date: 2010/03/19 15:31:12 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.comments;

import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.CmsFormHandler;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlHtmlValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.logging.Log;

/**
 * Represents a comment form with all configured fields and options.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.3 $
 * 
 * @since 7.0.5 
 */
public class CmsCommentForm extends CmsForm {

    /** Comment form id constant. */
    public static final String FORM_ID = "__oamp-comment__";

    /** Configuration node name for the mail alert. */
    public static final String NODE_MAILALERT = "MailAlert";

    /** Configuration node name for the Email receipt address node. */
    public static final String NODE_RESPONSIBLE = "MailResponsible";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCommentForm.class);

    /** Resource type ID of XML content. */
    private static final String TYPE_NAME = "oampcomments";

    /** The form handler. */
    private CmsFormHandler m_formHandler;

    /** If the resource responsibles should get the email alert or not. */
    private boolean m_responsible;

    /**
     * Default constructor which parses the configuration file.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the configuration file, otherwise from the request
     * 
     * @throws Exception if parsing the configuration fails
     */
    public CmsCommentForm(CmsCommentFormHandler jsp, CmsMessages messages, boolean initial)
    throws Exception {

        super(jsp, messages, initial);
    }

    /**
     * Constructor which parses the configuration file using a given configuration file URI.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the configuration file, otherwise from the request
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * @param formAction the desired action submitted by the form
     * 
     * @throws Exception if parsing the configuration fails
     */
    public CmsCommentForm(
        CmsCommentFormHandler jsp,
        CmsMessages messages,
        boolean initial,
        String formConfigUri,
        String formAction)
    throws Exception {

        super(jsp, messages, initial, formConfigUri, formAction);
    }

    /**
     * Returns the resource type name of XML content forms.<p>
     * 
     * @return the resource type name of XML content forms
     */
    public static String getStaticType() {

        return TYPE_NAME;
    }

    /**
     * Returns the form handler.<p>
     *
     * @return the form handler
     */
    public CmsFormHandler getFormHandler() {

        return m_formHandler;
    }

    /**
     * Initializes the form configuration and creates the necessary form field objects.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the XML configuration
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * @param formAction the desired action submitted by the form
     * 
     * @throws Exception if parsing the configuration fails
     */
    public void init(CmsFormHandler jsp, CmsMessages messages, boolean initial, String formConfigUri, String formAction)
    throws Exception {

        m_parameterMap = jsp.getParameterMap();
        // read the form configuration file from VFS
        if (CmsStringUtil.isEmpty(formConfigUri)) {
            formConfigUri = jsp.getRequestContext().getUri();
        }
        m_configUri = formConfigUri;
        CmsFile file = jsp.getCmsObject().readFile(formConfigUri);
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(jsp.getCmsObject(), file);

        // get current Locale
        Locale locale = jsp.getRequestContext().getLocale();

        // init member variables
        initMembers();

        m_formAction = formAction;
        m_fields = new ArrayList();
        m_dynaFields = new ArrayList();
        m_fieldsByName = new HashMap();
        m_formHandler = jsp;

        // initialize general form configuration
        initFormGlobalConfiguration(content, jsp.getCmsObject(), locale, messages);
        initFormMailAlert(content, jsp.getCmsObject(), locale, messages);

        // disable mail confirmation 
        setConfirmationMailEnabled(false);
        setConfirmationMailSubject("");
        setConfirmationMailText("");
        setConfirmationMailField(-1);
        // enable db persistence
        setTransportDatabase(true);
        setFormId(FORM_ID);

        // initialize the form input fields
        initInputFields(content, jsp, locale, messages, initial);
        // init. the optional captcha field
        initCaptchaField(jsp, content, locale, initial);

        // add the captcha field to the list of all fields, if the form has no check page
        if (captchaFieldIsOnInputPage() && (m_captchaField != null)) {
            addField(m_captchaField);
        }
    }

    /**
     * Returns the responsible flag.<p>
     *
     * @return the responsible flag
     */
    public boolean isResponsible() {

        return m_responsible;
    }

    /**
     * Initializes the general online form settings.<p>
     * 
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     * @param messages the localized messages
     * 
     * @throws Exception if initializing the form settings fails
     */
    protected void initFormGlobalConfiguration(CmsXmlContent content, CmsObject cms, Locale locale, CmsMessages messages)
    throws Exception {

        // get the form text
        String stringValue = content.getStringValue(cms, NODE_FORMTEXT, locale);
        setFormText(getConfigurationValue(stringValue, ""));
        // get the form footer text
        stringValue = content.getStringValue(cms, NODE_FORMFOOTERTEXT, locale);
        setFormFooterText(getConfigurationValue(stringValue, ""));
        // set the form confirmation text
        stringValue = content.getStringValue(cms, NODE_FORMCONFIRMATION, locale);
        setFormConfirmationText(getConfigurationValue(stringValue, ""));

        // get the unused target URI
        setTargetUri("");

        // optional configuration options
        String pathPrefix = NODE_OPTIONALCONFIGURATION + "/";
        if (content.getValue(NODE_OPTIONALCONFIGURATION, locale) == null) {
            return;
        }
        // no check page
        setShowCheck(false);
        setFormCheckText("");
        // get the dynamic fields class
        stringValue = content.getStringValue(cms, pathPrefix + NODE_DYNAMICFIELDCLASS, locale);
        setDynamicFieldClass(getConfigurationValue(stringValue, ""));
        // get the show mandatory setting
        stringValue = content.getStringValue(cms, pathPrefix + NODE_SHOWMANDATORY, locale);
        setShowMandatory(Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
        // no reset button
        setShowReset(false);
        // get the form attributes
        stringValue = content.getStringValue(cms, pathPrefix + NODE_FORMATTRIBUTES, locale);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setFormAttributes(" " + stringValue);
        }
        // get the field attributes
        stringValue = content.getStringValue(cms, pathPrefix + NODE_FORMFIELDATTRIBUTES, locale);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setFormFieldAttributes(" " + stringValue);
        } else {
            // no field attributes specified, check default field attributes
            String defaultAttributes = messages.key("form.field.default.attributes");
            if (CmsStringUtil.isNotEmpty(defaultAttributes)) {
                setFormFieldAttributes(" " + defaultAttributes);
            }
        }
    }

    /**
     * Initializes the mail alert settings.<p>
     * 
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     * @param messages the localized messages
     */
    protected void initFormMailAlert(CmsXmlContent content, CmsObject cms, Locale locale, CmsMessages messages) {

        if (content.getValue(NODE_MAILALERT, locale) == null) {
            setTransportEmail(false);
            return;
        }
        setTransportEmail(true);
        // optional configuration options
        String pathPrefix = NODE_MAILALERT + "/";

        String stringValue;
        // get the mail from address
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILFROM, locale);
        setMailFrom(getConfigurationValue(stringValue, ""));

        // get the mail to address(es)
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILTO, locale);
        setMailTo(getConfigurationValue(stringValue, ""));

        // set if using responsibles or not
        stringValue = content.getStringValue(cms, pathPrefix + NODE_RESPONSIBLE, locale);
        setResponsible(getConfigurationValue(stringValue, "false"));

        // get the mail CC recipient(s)
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILCC, locale);
        setMailCC(getConfigurationValue(stringValue, ""));
        // get the mail BCC recipient(s)
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILBCC, locale);
        setMailBCC(getConfigurationValue(stringValue, ""));
        // get the mail subject
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILSUBJECT, locale);
        setMailSubject(getConfigurationValue(stringValue, ""));
        // get the optional mail subject prefix from localized messages
        stringValue = messages.key("form.mailsubject.prefix");
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
            // prefix present, set it
            setMailSubjectPrefix(stringValue + " ");
        } else {
            // no prefix present
            setMailSubjectPrefix("");
        }
        CmsXmlHtmlValue mailTextValue = (CmsXmlHtmlValue)content.getValue(pathPrefix + NODE_MAILTEXT, locale);
        if (mailTextValue != null) {
            // get the mail text as plain text
            stringValue = mailTextValue.getPlainText(cms);
            setMailTextPlain(getConfigurationValue(stringValue, ""));
            // get the mail text
            stringValue = mailTextValue.getStringValue(cms);
            setMailText(getConfigurationValue(stringValue, ""));
        } else {
            setMailTextPlain("");
            setMailText("");
        }
        // get the mail type
        stringValue = content.getStringValue(cms, pathPrefix + NODE_MAILTYPE, locale);
        setMailType(getConfigurationValue(stringValue, MAILTYPE_HTML));
    }

    /**
     * Sets the responsible flag.<p>
     * 
     * @param responsible if the resource responsibles should get the email alert
     */
    private void setResponsible(String responsible) {

        m_responsible = Boolean.valueOf(responsible).booleanValue();
        if (m_responsible) {
            String mailTo = getMailTo();
            CmsObject cms = m_formHandler.getCmsObject();
            String uri = cms.getRequestContext().getUri();
            try {
                Iterator responsibles = cms.readResponsibleUsers(cms.readResource(uri)).iterator();
                while (responsibles.hasNext()) {
                    CmsUser responsibleUser = (CmsUser)responsibles.next();
                    String email = responsibleUser.getEmail();
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(email)) {
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(mailTo)) {
                            mailTo += ";";
                        }
                        mailTo += email;
                    }
                }
            } catch (CmsException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
            setMailTo(mailTo);
        }
    }
}
