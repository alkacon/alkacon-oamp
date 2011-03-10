/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.registration/src/com/alkacon/opencms/registration/CmsRegistrationForm.java,v $
 * Date   : $Date: 2011/03/10 11:59:04 $
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

package com.alkacon.opencms.registration;

import com.alkacon.opencms.formgenerator.CmsEmailField;
import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.CmsFormHandler;
import com.alkacon.opencms.formgenerator.I_CmsField;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlHtmlValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a webuser registration form with all configured fields and options.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4 
 */
public class CmsRegistrationForm extends CmsForm {

    /** Configuration node name for the user creation options. */
    public static final String NODE_ACTION = "Action";

    /** Configuration node name for the activation text node. */
    public static final String NODE_ACTIVATIONTEXT = "ActivationText";

    /** Configuration node name for the confirmation mail node. */
    public static final String NODE_CONFIRMATIONMAIL = "ConfirmationMail";

    /** Configuration node name for the email as login option. */
    public static final String NODE_EMAILASLOGIN = "EmailAsLogin";

    /** Configuration node name for the group. */
    public static final String NODE_GROUP = "Group";

    /** Configuration node name for the mail alert. */
    public static final String NODE_MAILALERT = "MailAlert";

    /** Configuration node name for the organizational unit. */
    public static final String NODE_ORGANIZATIONALUNIT = "OrganizationalUnit";

    /** Resource type ID of XML content. */
    private static final String TYPE_NAME = "alkacon-registration";

    /** The activation text. */
    private String m_activationText;

    /** The use email as login option. */
    private boolean m_emailAsLogin;

    /** The group, the users will be members of. */
    private String m_group;

    /** The organizational unit, where to create the users. */
    private String m_orgUnit;

    /**
     * Default constructor which parses the configuration file.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the configuration file, otherwise from the request
     * 
     * @throws Exception if parsing the configuration fails
     */
    public CmsRegistrationForm(CmsRegistrationFormHandler jsp, CmsMessages messages, boolean initial)
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
    public CmsRegistrationForm(
        CmsRegistrationFormHandler jsp,
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
     * @see com.alkacon.opencms.formgenerator.CmsForm#getConfirmationMailField()
     */
    @Override
    public int getConfirmationMailField() {

        if (m_fieldsByName.isEmpty() || (m_confirmationMailField >= 0)) {
            return m_confirmationMailField;
        }
        for (int i = 0; i < m_fields.size(); i++) {
            if (m_fields.get(i) instanceof CmsEmailField) {
                return i;
            }
        }
        return m_confirmationMailField;
    }

    /**
     * Returns the activation text.<p>
     * 
     * @return the activation text
     */
    public String getFormActivationText() {

        return m_activationText;
    }

    /**
     * Returns the group.<p>
     *
     * @return the group
     */
    public String getGroup() {

        return m_group;
    }

    /**
     * Returns the org Unit.<p>
     *
     * @return the org Unit
     */
    public String getOrgUnit() {

        return m_orgUnit;
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
    @Override
    public void init(CmsFormHandler jsp, CmsMessages messages, boolean initial, String formConfigUri, String formAction)
    throws Exception {

        m_parameterMap = jsp.getParameterMap();
        // read the form configuration file from VFS
        if (CmsStringUtil.isEmpty(formConfigUri)) {
            formConfigUri = jsp.getRequestContext().getUri();
        }
        CmsFile file = jsp.getCmsObject().readFile(formConfigUri);
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(jsp.getCmsObject(), file);

        // get current Locale
        Locale locale = jsp.getRequestContext().getLocale();

        // init member variables
        initMembers();

        m_formAction = formAction;
        m_fields = new ArrayList<I_CmsField>();
        m_dynaFields = new ArrayList<I_CmsField>();
        m_fieldsByName = new HashMap<String, I_CmsField>();

        // initialize general form configuration
        setTransportDatabase(false);

        initFormGlobalConfiguration(content, jsp.getCmsObject(), locale, messages);
        initFormMailAlert(content, jsp.getCmsObject(), locale, messages);
        initConfirmationMail(content, jsp.getCmsObject(), locale);
        initUserCreationOptions(content, jsp.getCmsObject(), locale);

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
     * Returns the email As Login option.<p>
     *
     * @return the email As Login option
     */
    public boolean isEmailAsLogin() {

        return m_emailAsLogin;
    }

    /**
     * Sets the activation text.<p>
     * 
     * @param activationText the activation text to set
     */
    public void setFormActivationText(String activationText) {

        m_activationText = activationText;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.CmsForm#getField(java.lang.String)
     */
    @Override
    protected I_CmsField getField(String fieldType) {

        if (fieldType.equals(CmsPasswordField.getStaticType())) {
            return new CmsPasswordField();
        }
        return super.getField(fieldType);
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
    @Override
    protected void initFormGlobalConfiguration(CmsXmlContent content, CmsObject cms, Locale locale, CmsMessages messages)
    throws Exception {

        // create a macro resolver with the cms object
        CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(cms).setKeepEmptyMacros(true);

        // get the form text
        String stringValue = content.getStringValue(cms, NODE_FORMTEXT, locale);
        setFormText(getConfigurationValue(resolver, stringValue, ""));
        // get the form footer text
        stringValue = content.getStringValue(cms, NODE_FORMFOOTERTEXT, locale);
        setFormFooterText(getConfigurationValue(resolver, stringValue, ""));

        // get the activation text
        stringValue = content.getStringValue(cms, NODE_ACTIVATIONTEXT, locale);
        setFormActivationText(getConfigurationValue(resolver, stringValue, ""));

        // get the form confirmation text
        stringValue = content.getStringValue(cms, NODE_FORMCONFIRMATION, locale);
        setFormConfirmationText(getConfigurationValue(resolver, stringValue, ""));
        // get the unused target URI
        setTargetUri("");

        // optional configuration options
        String pathPrefix = NODE_OPTIONALCONFIGURATION + "/";

        // get the form check page flag
        stringValue = content.getStringValue(cms, pathPrefix + NODE_SHOWCHECK, locale);
        setShowCheck(Boolean.valueOf(stringValue).booleanValue());
        // get the check page text
        stringValue = content.getStringValue(cms, pathPrefix + NODE_FORMCHECKTEXT, locale);
        setFormCheckText(getConfigurationValue(resolver, stringValue, ""));
        // get the dynamic fields class
        stringValue = content.getStringValue(cms, pathPrefix + NODE_DYNAMICFIELDCLASS, locale);
        setDynamicFieldClass(getConfigurationValue(stringValue, ""));
        // get the show mandatory setting
        stringValue = content.getStringValue(cms, pathPrefix + NODE_SHOWMANDATORY, locale);
        setShowMandatory(Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
        // get the show reset button setting
        stringValue = content.getStringValue(cms, pathPrefix + NODE_SHOWRESET, locale);
        setShowReset(Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
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
     * Initializes the confirmation mail settings.<p>
     * 
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     */
    private void initConfirmationMail(CmsXmlContent content, CmsObject cms, Locale locale) {

        setConfirmationMailEnabled(true);

        // get the confirmation mail node
        if (content.getValue(NODE_CONFIRMATIONMAIL, locale) == null) {
            // TODO: default settings
            setConfirmationMailSubject("");
            setConfirmationMailText("");
            setConfirmationMailField(-1);
            return;
        }

        // optional confirmation mail nodes
        String pathPrefix = NODE_CONFIRMATIONMAIL + "/";

        // get the confirmation mail subject
        String stringValue = content.getStringValue(cms, pathPrefix + NODE_CONFIRMATIONMAILSUBJECT, locale);
        setConfirmationMailSubject(getConfigurationValue(stringValue, ""));

        CmsXmlHtmlValue mailTextValue = (CmsXmlHtmlValue)content.getValue(
            pathPrefix + NODE_CONFIRMATIONMAILTEXT,
            locale);
        if (mailTextValue != null) {
            // get the confirmation mail text
            stringValue = mailTextValue.getPlainText(cms);
            setConfirmationMailTextPlain(getConfigurationValue(stringValue, ""));
            stringValue = mailTextValue.getStringValue(cms);
            setConfirmationMailText(getConfigurationValue(stringValue, ""));
        } else {
            setConfirmationMailTextPlain("");
            setConfirmationMailText("");
        }

        // get the confirmation mail field index number
        stringValue = content.getStringValue(cms, pathPrefix + NODE_CONFIRMATIONMAILFIELD, locale);
        int fieldIndex = -1;
        try {
            fieldIndex = Integer.parseInt(getConfigurationValue(stringValue, "1")) - 1;
        } catch (Exception e) {
            // ignore this exception, use first field
        }
        setConfirmationMailField(fieldIndex);
        setMailSubjectPrefix("");
    }

    /**
     * Initializes the user creation settings.<p>
     * 
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     */
    private void initUserCreationOptions(CmsXmlContent content, CmsObject cms, Locale locale) {

        if (content.getValue(NODE_ACTION, locale) == null) {
            // ignore for profile edition
            return;
        }
        String path = NODE_ACTION + "/";

        String stringValue = content.getStringValue(cms, path + NODE_EMAILASLOGIN, locale);
        m_emailAsLogin = Boolean.valueOf(stringValue).booleanValue();

        stringValue = content.getStringValue(cms, path + NODE_ORGANIZATIONALUNIT, locale);
        try {
            m_orgUnit = OpenCms.getOrgUnitManager().readOrganizationalUnit(cms, stringValue).getName();
        } catch (Throwable e) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_ORGUNIT_DOESNOT_EXIST_1,
                stringValue));
        }

        if (content.getValue(path + NODE_GROUP, locale) == null) {
            return;
        }
        stringValue = content.getStringValue(cms, path + NODE_GROUP, locale);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
            try {
                m_group = cms.readGroup(stringValue).getName();
            } catch (Throwable e) {
                throw new CmsIllegalArgumentException(Messages.get().container(
                    Messages.ERR_GROUP_DOESNOT_EXIST_1,
                    stringValue));
            }
        }
    }
}
