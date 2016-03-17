/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsForm.java,v $
 * Date   : $Date: 2011-03-24 16:33:50 $
 * Version: $Revision: 1.29 $
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

package com.alkacon.opencms.v8.formgenerator;

import static com.alkacon.opencms.v8.formgenerator.CmsFormContentUtil.getContentStringValue;
import static com.alkacon.opencms.v8.formgenerator.CmsFormContentUtil.getContentValue;
import static com.alkacon.opencms.v8.formgenerator.CmsFormContentUtil.getContentValues;

import org.opencms.configuration.CmsConfigurationException;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsMessages;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlHtmlValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.fileupload.FileItem;

/**
 * Represents an input form with all configured fields and options.<p>
 *
 * Provides the necessary information to create an input form, email messages and confirmation outputs.<p>
 *
 * @author Andreas Zahner
 * @author Thomas Weckert
 * @author Jan Baudisch
 *
 * @version $Revision: 1.29 $
 *
 * @since 7.0.4
 */
public class CmsForm {

    /** Constant used as value in the XML content defining that the data target is database. */
    public static final String DATATARGET_DATABASE = "database";

    /** Constant used as value in the XML content defining that the data target is email. */
    public static final String DATATARGET_EMAIL = "email";

    /** Constant used as value in the XML content defining that the data target is database and email. */
    public static final String DATATARGET_EMAIL_DATABASE = "email-database";

    /** The macro to determine that field items for radio buttons and check boxes should be shown in a row. */
    public static final String MACRO_SHOW_ITEMS_IN_ROW = "%(row)";

    /** Mail type: html mail. */
    public static final String MAILTYPE_HTML = "html";

    /** Mail type: text mail. */
    public static final String MAILTYPE_TEXT = "text";

    /** The module name. */
    public static final String MODULE_NAME = CmsForm.class.getPackage().getName();

    /** Name of the CSS style sheet module parameter. */
    public static final String MODULE_PARAM_CSS = "css";

    /** Name of the module parameter excel.delimiter defines the column delimiter in excel. */
    public static final String MODULE_PARAM_CSV_DELIMITER = "export.delimiter";

    /** Name of the db index table space module parameter. */
    public static final String MODULE_PARAM_DB_INDEXTABLESPACE = "index-tablespace";

    /** Name of the db-pool module parameter. */
    public static final String MODULE_PARAM_DB_POOL = "db-pool";

    /** Name of the db-provider module parameter. */
    public static final String MODULE_PARAM_DB_PROVIDER = "db-provider";

    /** Name of the module parameter for the configuration of the number formatting of the export data. */
    public static final String MODULE_PARAM_EXPORT_NUMBERASSTRING = "export.numberasstring";

    /** Name of the module parameter for the configuration of the time format of the export data. */
    public static final String MODULE_PARAM_EXPORT_TIMEFORMAT = "export.timeformat";

    /**
     * Module parameter for the content encoding (text encoding) of the exported csv data. This encoding may vary
     * from the value of the content-encoding property of the webform XML content because e.g. Microsoft Excel seems
     * to be hard-wired to use the windows-1252 encoding.
     */
    public static final String MODULE_PARAM_EXPORTENCODING = "export.encoding";

    /**
     * Module parameter for the line separator of the exported csv data. If set to
     * {@link #MODULE_PARAMVALUE_EXPORTLINESEPARATOR_WINDOWS} exported "\n" will be transformed to "\r\n".
     */
    public static final String MODULE_PARAM_EXPORTLINESEPARATOR = "export.lineseparator";

    /** Name of the module parameter for the configuration of the prefix list of system readable fonts like 'arial|serif'. */
    public static final String MODULE_PARAM_FONT_PREFIX = "font-prefix";

    /** Name of the template file parameter that is used as default HTML template for the form. */
    public static final String MODULE_PARAM_TEMPLATE_FILE = "templatefile";

    /** Name of the group module parameter that is used to grant access to the workplace tool. */
    public static final String MODULE_PARAM_TOOL_GROUP = "usergroup";

    /** Name of the upload folder module parameter. */
    public static final String MODULE_PARAM_UPLOADFOLDER = "uploadfolder";

    /** Name of the upload project module parameter. */
    public static final String MODULE_PARAM_UPLOADPROJECT = "uploadproject";

    /** Name of the upload VFS module parameter. */
    public static final String MODULE_PARAM_UPLOADVFS = "uploadvfs";

    /**
     * Constant value for the module parameter {@link #MODULE_PARAM_EXPORTLINESEPARATOR} to have "\r\n" transformed to
     * "\n", which is required for viewing the cvs in Microsoft (Windows!) Excel.
     */
    public static final String MODULE_PARAMVALUE_EXPORTLINESEPARATOR_EXCEL = "excel";

    /**
     * Constant value for the module parameter {@link #MODULE_PARAM_EXPORTLINESEPARATOR} to have "\r\n" transformed to
     * "\r\n".
     */
    public static final String MODULE_PARAMVALUE_EXPORTLINESEPARATOR_UNIX = "unix";

    /**
     * Constant value for the module parameter {@link #MODULE_PARAM_EXPORTLINESEPARATOR} to have "\n" transformed to
     * "\r\n".
     */
    public static final String MODULE_PARAMVALUE_EXPORTLINESEPARATOR_WINDOWS = "windows";

    /** Configuration node name for the optional webform action class. */
    public static final String NODE_ACTION_CLASS = "ActionClass";

    /** Configuration node name for the optional captcha. */
    public static final String NODE_CAPTCHA = "FormCaptcha";

    /** Configuration node name for the optional captcha. */
    public static final String NODE_CAPTCHA_PRESET = "Preset";

    /** Configuration node name for the column to show the text. */
    public static final String NODE_COLUMN = "Column";

    /** Configuration node name for the confirmation mail checkbox label text. */
    public static final String NODE_CONFIRMATIONMAILCHECKBOXLABEL = "ConfirmationCheckboxLabel";

    /** Configuration node name for the confirmation mail enabled node. */
    public static final String NODE_CONFIRMATIONMAILENABLED = "ConfirmationMailEnabled";

    /** Configuration node name for the confirmation mail input field node. */
    public static final String NODE_CONFIRMATIONMAILFIELD = "ConfirmationField";

    /** Configuration node name for the option confirmation mail from node. */
    public static final String NODE_CONFIRMATIONMAILFROM = "ConfirmationMailFrom";

    /** Configuration node name for the option confirmation mail from name node. */
    public static final String NODE_CONFIRMATIONMAILFROMNAME = "ConfirmationMailFromName";

    /** Configuration node name for the confirmation mail optional flag node. */
    public static final String NODE_CONFIRMATIONMAILOPTIONAL = "ConfirmationMailOptional";

    /** Configuration node name for the confirmation mail subject node. */
    public static final String NODE_CONFIRMATIONMAILSUBJECT = "ConfirmationMailSubject";

    /** Configuration node name for the confirmation mail text node. */
    public static final String NODE_CONFIRMATIONMAILTEXT = "ConfirmationMailText";

    /** Configuration node name for the optional CSS file node. */
    public static final String NODE_CSS_FILE = "CSS";

    /** Configuration node name for the optional nested data target. */
    public static final String NODE_DATATARGET = "DataTarget";

    /** Configuration node name for the transport field in the optional nested data target. */
    public static final String NODE_DATATARGET_FORMID = "FormId";

    /** Configuration node name for the transport field in the optional nested data target. */
    public static final String NODE_DATATARGET_TRANSPORT = "Transport";

    /** Configuration node name for the date. */
    public static final String NODE_DATE = "Date";

    /** Configuration node name for the optional dynamic field class node. */
    public static final String NODE_DYNAMICFIELDCLASS = "DynamicFieldClass";

    /** Configuration node name for the Email node. */
    public static final String NODE_EMAIL = "Email";

    /** Configuration node name for the field value node. */
    public static final String NODE_FIELDDEFAULTVALUE = "FieldDefault";

    /** Configuration node name for the field error message node. */
    public static final String NODE_FIELDERRORMESSAGE = "FieldErrorMessage";

    /** Configuration node name for the field item node. */
    public static final String NODE_FIELDITEM = "FieldItem";

    /** Configuration node name for the field description node. */
    public static final String NODE_FIELDLABEL = "FieldLabel";

    /** Configuration node name for the field mandatory node. */
    public static final String NODE_FIELDMANDATORY = "FieldMandatory";

    /** Configuration node name for the field params node. */
    public static final String NODE_FIELDPARAMS = "FieldParams";

    /** Configuration node name for the field type node. */
    public static final String NODE_FIELDTYPE = "FieldType";

    /** Configuration node name for the field validation node. */
    public static final String NODE_FIELDVALIDATION = "FieldValidation";

    /** Configuration node name for the form attributes node. */
    public static final String NODE_FORMATTRIBUTES = "FormAttributes";

    /** Configuration node name for the form check page text node. */
    public static final String NODE_FORMCHECKTEXT = "CheckText";

    /** Configuration node name for the form confirmation text node. */
    public static final String NODE_FORMCONFIRMATION = "FormConfirmation";

    /** Configuration node name for the form field attributes node. */
    public static final String NODE_FORMFIELDATTRIBUTES = "FormFieldAttributes";

    /** Configuration node name for the form field width ratio node. */
    public static final String NODE_FORMFIELDWIDTHRATIO = "FormFieldWidthRatio";

    /** Configuration node name for the form footer text node. */
    public static final String NODE_FORMFOOTERTEXT = "FormFooterText";

    /** Configuration node name for the form middle text node. */
    public static final String NODE_FORMMIDDLETEXT = "FormMiddleText";

    /** Configuration node name for the form text node. */
    public static final String NODE_FORMTEXT = "FormText";

    /** Configuration node name for the optional forward mode. */
    public static final String NODE_FORWARD_MODE = "ForwardMode";

    /** Configuration node name for the input field node. */
    public static final String NODE_INPUTFIELD = "InputField";

    /** Configuration node name for the instant redirect node. */
    public static final String NODE_INSTANTREDIRECT = "InstantRedirect";

    /** Configuration node name for the item description node. */
    public static final String NODE_ITEMDESCRIPTION = "ItemDescription";

    /** Configuration node name for the item selected node. */
    public static final String NODE_ITEMSELECTED = "ItemSelected";

    /** Configuration node name for the item value node. */
    public static final String NODE_ITEMVALUE = "ItemValue";

    /** Configuration node name for the optional keep session node. */
    public static final String NODE_KEEPSESSION = "KeepSession";

    /** Configuration node name for the optional link node. */
    public static final String NODE_LINK = "Link";

    /** Configuration node name for the email BCC recipient(s) node. */
    public static final String NODE_MAILBCC = "MailBCC";

    /** Configuration node name for the email CC recipient(s) node. */
    public static final String NODE_MAILCC = "MailCC";

    /** Configuration node name for the email CSS style sheet node. */
    public static final String NODE_MAILCSS = "MailCSS";

    /** Configuration node name for the email sender address node. */
    public static final String NODE_MAILFROM = "MailFrom";

    /** Configuration node name for the email sender name node. */
    public static final String NODE_MAILFROMNAME = "MailFromName";

    /** Configuration node name for the email subject node. */
    public static final String NODE_MAILSUBJECT = "MailSubject";

    /** Configuration node name for the email text node. */
    public static final String NODE_MAILTEXT = "MailText";

    /** Configuration node name for the email recipient(s) node. */
    public static final String NODE_MAILTO = "MailTo";

    /** Configuration node name for the email type node. */
    public static final String NODE_MAILTYPE = "MailType";

    /** Parent node for a nested web form. */
    public static final String NODE_NESTED_FORM = "Form";

    /** Configuration node name for the optional form configuration. */
    public static final String NODE_OPTIONALCONFIGURATION = "OptionalFormConfiguration";

    /** Configuration node name for the optional confirmation mail configuration. */
    public static final String NODE_OPTIONALCONFIRMATION = "OptionalConfirmationMail";

    /** Configuration node name for the optional form expiration configuration. */
    public static final String NODE_OPTIONALEXPIRATION = "OptionalFormExpiration";

    /** Configuration node name for the optional field text configuration. */
    public static final String NODE_OPTIONALFIELDTEXT = "OptionalFieldText";

    /** Configuration node name for the optional form max submissions configuration. */
    public static final String NODE_OPTIONALMAXSUBMISSIONS = "OptionalFormMaxSubmissions";

    /** Configuration node name for the optional form release configuration. */
    public static final String NODE_OPTIONALRELEASE = "OptionalFormRelease";

    /** Configuration node name for the optional sub field configuration. */
    public static final String NODE_OPTIONALSUBFIELD = "OptionalSubField";

    /** Configuration node name for the parent field. */
    public static final String NODE_PARENTFIELD = "ParentField";

    /** Configuration node name for the optional property file. */
    public static final String NODE_PROPERTY_FILE = "PropertyFile";

    /** Configuration node name for the Show check page node. */
    public static final String NODE_SHOWCHECK = "ShowCheck";

    /** Configuration node name for the Show mandatory markings node. */
    public static final String NODE_SHOWMANDATORY = "ShowMandatory";

    /** Configuration node name for the Show reset button node. */
    public static final String NODE_SHOWRESET = "ShowReset";

    /** Configuration node name for the optional target URI. */
    public static final String NODE_TARGET_URI = "TargetUri";

    /** Configuration node name for the optional HTML template file. */
    public static final String NODE_TEMPLATE_FILE = "TemplateFile";

    /** Configuration node name for the text node. */
    public static final String NODE_TEXT = "Text";

    /** Configuration node name for the title node. */
    public static final String NODE_TITLE = "Title";

    /** Configuration node name for the value. */
    public static final String NODE_VALUE = "Value";

    /** Configuration node name for the optional csv export configuration */
    public static final String NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION = "OptionalCsvExportConfiguration";

    /** Request parameter name for the optional send confirmation email checkbox. */
    public static final String PARAM_SENDCONFIRMATION = "sendconfirmation";

    /** Mailto property: can be attached to container page. Overwrites the "Mail to" field from the webform*/
    public static final String PROPERTY_MAILTO = "webform.mailto";

    /** The path to the default HTML templates for the form. */
    public static final String VFS_PATH_DEFAULT_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + MODULE_NAME
        + "/resources/formtemplates/default.st";

    /** The path to the error HTML templates for the form. */
    public static final String VFS_PATH_ERROR_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + CmsForm.MODULE_NAME
        + "/resources/formtemplates/error.st";

    /** Resource type ID of XML content forms. */
    public static final String TYPE_NAME = "alkacon-v8-webform";

    /** configuration value. */
    protected String m_actionClass;

    /** The captcha field. */
    protected CmsCaptchaField m_captchaField;

    /** The form configuration ID. */
    protected int m_configId;

    /** The list of possible configuration errors. */
    protected List<String> m_configurationErrors;

    /** The form configuration URI. */
    protected String m_configUri;

    /** configuration value. */
    protected String m_confirmationMailCheckboxLabel;

    /** configuration value. */
    protected boolean m_confirmationMailEnabled;

    /** configuration value. */
    protected int m_confirmationMailField;

    /** configuration value. */
    protected String m_confirmationMailFieldDbLabel;

    /** configuration value. */
    protected String m_confirmationMailFrom;

    /** configuration value. */
    protected String m_confirmationMailFromName;

    /** configuration value. */
    protected boolean m_confirmationMailOptional;

    /** configuration value. */
    protected String m_confirmationMailSubject;

    /** configuration value. */
    protected String m_confirmationMailText;

    /** configuration value. */
    protected String m_confirmationMailTextPlain;

    /** The name of the CSS file to use for the form. */
    protected String m_cssFile;

    /** Stores the form dynamic input fields. */
    protected List<I_CmsField> m_dynaFields;

    /** The class name for the dynamic field value resolver. */
    protected String m_dynamicFieldClass;

    /** The optional form expiration date. */
    protected long m_expirationDate;

    /** The form expiration text. */
    protected String m_expirationText;

    /** Stores the form input fields. */
    protected List<I_CmsField> m_fields;

    /** Allows to access form fields internally by name. */
    protected Map<String, I_CmsField> m_fieldsByName;

    /** configuration value. */
    protected String m_formAction;

    /** configuration value. */
    protected String m_formAttributes;

    /** configuration value. */
    protected String m_formCheckText;

    /** configuration value. */
    protected String m_formConfirmationText;

    /** configuration value. */
    protected String m_formFieldAttributes;

    /** configuration value. */
    protected int m_formFieldWidthRatio;

    /** configuration value. */
    protected String m_formFooterText;

    /** The form id needed in case it is stored in the database. */
    protected String m_formId;

    /** configuration value. */
    protected String m_formMiddleText;

    /** configuration value. */
    protected String m_formText;

    /** If there is at least one mandatory field. */
    protected boolean m_hasMandatoryFields;

    /** indicates if an instant redirect is configured. */
    protected boolean m_instantRedirect;

    /** The current jsp action element. */
    protected CmsJspActionElement m_jspAction;

    /** configuration value. */
    protected String m_mailBCC;

    /** configuration value. */
    protected String m_mailCC;

    /** The optional email CSS style sheet. */
    protected String m_mailCSS;

    /** The mail sender address. */
    protected String m_mailFrom;

    /** The mail sender name. */
    protected String m_mailFromName;

    /** configuration value. */
    protected String m_mailSubject;

    /** configuration value. */
    protected String m_mailSubjectPrefix;

    /** configuration value. */
    protected String m_mailText;

    /** configuration value. */
    protected String m_mailTextPlain;

    /** configuration value. */
    protected String m_mailTo;

    /** The email type, either HTML or text mail. */
    protected String m_mailType;

    /** The optional form max submissions. */
    protected long m_maxSubmissions;

    /** The optional form max submissions text. */
    protected String m_maxSubmissionsText;

    /** The map of request parameters. */
    protected Map<String, String[]> m_parameterMap;

    /** The optional property file URI. */
    protected String m_propertyFile;

    /** Interval to refresh the session. */
    protected int m_refreshSessionInterval;

    /** The optional form release date. */
    protected long m_releaseDate;

    /** The optional form release text. */
    protected String m_releaseText;

    /** Flag if the check page has to be shown. */
    protected boolean m_showCheck;

    /** Flag if the text to explain mandatory fields has to be shown. */
    protected boolean m_showMandatory;

    /** Flag if the reset button has to be shown. */
    protected boolean m_showReset;

    /** The form target URI. */
    protected String m_targetUri;

    /** The optional HTML template file URI. */
    protected String m_templateFile;

    /** configuration value. */
    protected String m_title;

    /** Flag to signal that data should be stored in the database - defaults to false. */
    protected boolean m_transportDatabase;

    /** Flag to signal that data should be sent by email - defaults to true. */
    protected boolean m_transportEmail = true;

    /** The optional CSV export configuration, unparsed as string. */
    private String m_csvExportConfiguration;

    /** The forward mode. */
    private boolean m_forwardMode;

    /**
     * Default constructor which parses the configuration file.<p>
     *
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the configuration file, otherwise from the request
     * @throws Exception if parsing the configuration fails
     */
    public CmsForm(CmsFormHandler jsp, CmsMessages messages, boolean initial)
    throws Exception {

        this(jsp, messages, initial, null, null);
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
    public CmsForm(CmsFormHandler jsp, CmsMessages messages, boolean initial, String formConfigUri, String formAction)
    throws Exception {

        // for backwards compatibility, not calling the next constructor with additional parameter
        init(jsp, messages, initial, formConfigUri, formAction);
    }

    /**
     * Constructor which parses the configuration file using a given configuration file URI.<p>
     *
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the configuration file, otherwise from the request
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * @param formAction the desired action submitted by the form
     * @param dynamicConfig map of configurations that can overwrite the configuration from the configuration file
     *
     * @throws Exception if parsing the configuration fails
     */
    public CmsForm(
        CmsFormHandler jsp,
        CmsMessages messages,
        boolean initial,
        String formConfigUri,
        String formAction,
        Map<String, String> dynamicConfig)
        throws Exception {

        init(jsp, messages, initial, formConfigUri, formAction, dynamicConfig);
    }

    /**
     * If the given value is not empty, macros in it will be resolved, otherwise returns the default value.<p>
     *
     * @param resolver the macro resolver to use
     * @param value the configuration value to check and resolve macros in
     * @param defaultValue the default value to return in case the value is empty
     *
     * @return the checked value
     */
    public static String getConfigurationValue(CmsMacroResolver resolver, String value, String defaultValue) {

        if (CmsStringUtil.isNotEmpty(value)) {
            String result = resolver.resolveMacros(value);
            return result;
        }
        return defaultValue;
    }

    /**
     * Checks if the given value is empty and returns in that case the default value.<p>
     *
     * @param value the configuration value to check
     * @param defaultValue the default value to return in case the value is empty
     * @return the checked value
     */
    public static String getConfigurationValue(String value, String defaultValue) {

        if (CmsStringUtil.isNotEmpty(value)) {
            return value;
        }
        return defaultValue;
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
     * Returns the value from the dynamic configuration if it is not empty or whitespace only - if so, it returns null.
     * @param dynamicConfig the dynamic configuration
     * @param key the configuration option to read
     *
     * @return If existing, the (non-whitespace-only) value of the configuration option, otherwise null
     */
    public static String getValueFromDynamicConfig(Map<String, String> dynamicConfig, String key) {

        String value = null;
        if (dynamicConfig != null) {
            value = dynamicConfig.get(key);
            if ((value != null) && value.trim().isEmpty()) {
                value = null;
            }
        }
        return value;
    }

    /**
     * Tests, if the captcha field (if configured at all) should be displayed on the check page.<p>
     *
     * @return true, if the captcha field should be displayed on the check page
     */
    public boolean captchaFieldIsOnCheckPage() {

        return getShowCheck();
    }

    /**
     * Tests, if the captcha field (if configured at all) should be displayed on the input page.<p>
     *
     * @return true, if the captcha field should be displayed on the input page
     */
    public boolean captchaFieldIsOnInputPage() {

        return !getShowCheck();
    }

    /**
     * Returns the action class.
     * <p>
     *
     * @return the action class.
     */
    public String getActionClass() {

        return m_actionClass;
    }

    /**
     * Returns a list of field objects, exclusive sub fields, inclusive dynamic fields, for the online form.<p>
     *
     * @return a list of field objects, exclusive sub fields, inclusive dynamic fields
     */
    public List<I_CmsField> getAllFields() {

        return getAllFields(false, false, true);
    }

    /**
     * Returns a list of field objects, depending on the given parameters.<p>
     * @param includeShownSubFields indicates if only the shown sub fields should be included
     * @param includeAllSubFields indicates if all possible sub fields should be included
     * @param includeDynamicFields indicates if the dynamic fields should be included
     *
     * @return a list of field objects, depending on the given parameters
     */
    public List<I_CmsField> getAllFields(
        boolean includeShownSubFields,
        boolean includeAllSubFields,
        boolean includeDynamicFields) {

        List<I_CmsField> result = new ArrayList<I_CmsField>(m_fields.size() + 16);
        if (includeAllSubFields) {
            // all sub fields have to be added
            Iterator<I_CmsField> i = m_fields.iterator();
            while (i.hasNext()) {
                I_CmsField field = i.next();
                result.add(field);
                if (field.isHasSubFields()) {
                    Iterator<Entry<String, List<I_CmsField>>> k = field.getSubFields().entrySet().iterator();
                    while (k.hasNext()) {
                        Map.Entry<String, List<I_CmsField>> entry = k.next();
                        result.addAll(entry.getValue());
                    }
                }
            }
        } else if (includeShownSubFields) {
            // only shown sub fields have to be added
            Iterator<I_CmsField> i = m_fields.iterator();
            while (i.hasNext()) {
                I_CmsField field = i.next();
                result.add(field);
                if (field.hasCurrentSubFields()) {
                    result.addAll(field.getCurrentSubFields());
                }
            }
        } else {
            // no sub fields have to be added
            result = new ArrayList<I_CmsField>(m_fields);
        }

        if (includeDynamicFields) {
            result.addAll(m_dynaFields);
        }

        return result;
    }

    /**
     * Returns the (opt.) captcha field of this form.<p>
     *
     * @return the (opt.) captcha field of this form
     */
    public CmsCaptchaField getCaptchaField() {

        return m_captchaField;
    }

    /**
     * Returns the configuration ID of this form.<p>
     *
     * This ID is used as suffix for form field names and other field specific stuff,
     * making it possible to have more than one form on a page.<p>
     *
     * @return the configuration ID of this form
     */
    public int getConfigId() {

        if (m_configId == 0) {
            m_configId = getConfigUri().hashCode();
        }
        return m_configId;
    }

    /**
     * Returns the form configuration errors.<p>
     *
     * @return the form configuration errors
     */
    public List<String> getConfigurationErrors() {

        return m_configurationErrors;
    }

    /**
     * Returns the configuration Uri.<p>
     *
     * @return the configuration Uri
     */
    public String getConfigUri() {

        return m_configUri;
    }

    /**
     * Returns the label for the optional confirmation mail checkbox on the input form.<p>
     *
     * @return the label for the optional confirmation mail checkbox on the input form
     */
    public String getConfirmationMailCheckboxLabel() {

        return m_confirmationMailCheckboxLabel;
    }

    /**
     * Returns the confirmation mail receiver email address.<p>
     *
     * @return the confirmation mail receiver email address or <code>null</code> if not found
     */
    public String getConfirmationMailEmail() {

        if (getConfirmationMailField() != -1) {
            try {
                I_CmsField mailField = getFields().get(getConfirmationMailField());
                return mailField.getValue();
            } catch (Exception e) {
                // field not found
            }
        } else if (CmsStringUtil.isNotEmpty(getConfirmationMailFieldDbLabel())) {
            I_CmsField mailField = getFieldByDbLabel(getConfirmationMailFieldDbLabel());
            if (mailField != null) {
                return mailField.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the index number of the input field containing the email address for the optional confirmation mail.<p>
     *
     * @return the index number of the input field containing the email address for the optional confirmation mail
     *
     * @deprecated use {@link #getConfirmationMailFieldDbLabel()} instead
     */
    @Deprecated
    public int getConfirmationMailField() {

        return m_confirmationMailField;
    }

    /**
     * Returns the DB label of the input field containing the email address for the optional confirmation mail.<p>
     *
     * @return the DB label of the input field containing the email address for the optional confirmation mail
     */
    public String getConfirmationMailFieldDbLabel() {

        return m_confirmationMailFieldDbLabel;
    }

    /**
     * Returns the optional confirmation mail from.<p>
     *
     * @return the optional confirmation mail from
     */
    public String getConfirmationMailFrom() {

        return m_confirmationMailFrom;
    }

    /**
     * Returns the optional confirmation mail from name.<p>
     *
     * @return the optional confirmation mail from name
     */
    public String getConfirmationMailFromName() {

        return m_confirmationMailFromName;
    }

    /**
     * Returns the subject of the optional confirmation mail.<p>
     *
     * @return the subject of the optional confirmation mail
     */
    public String getConfirmationMailSubject() {

        return m_confirmationMailSubject;
    }

    /**
     * Returns the text of the optional confirmation mail.<p>
     *
     * @return the text of the optional confirmation mail
     */
    public String getConfirmationMailText() {

        return m_confirmationMailText;
    }

    /**
     * Returns the plain text of the optional confirmation mail.<p>
     *
     * @return the plain text of the optional confirmation mail
     */
    public String getConfirmationMailTextPlain() {

        return m_confirmationMailTextPlain;
    }

    /**
     * Returns the name of the CSS file to use for the form.<p>
     *
     * @return the name of the CSS file to use for the form
     */
    public String getCssFile() {

        return m_cssFile;
    }

    /**
     * Get the column configuration for CSV export.
     *
     * @return the list of columns that should be exported in a csv file (with placeholders for default columns), or null if no configuration is given
     */
    public String getCsvExportConfiguration() {

        return m_csvExportConfiguration;
    }

    /**
     * Returns the class name for the dynamic field value resolver.<p>
     *
     * @return the class name for the dynamic field value resolver
     */
    public String getDynamicFieldClass() {

        return m_dynamicFieldClass;
    }

    /**
     * Returns the optional form expiration date.<p>
     *
     * @return the optional form expiration date
     */
    public long getExpirationDate() {

        return m_expirationDate;
    }

    /**
     * Returns the form expiration text.<p>
     *
     * @return the form expiration text
     */
    public String getExpirationText() {

        return m_expirationText;
    }

    /**
     * Returns the field with the given database label.<p>
     *
     * @param dbLabel the database label
     *
     * @return the field with the given database label or <code>null</code> if not found
     */
    public I_CmsField getFieldByDbLabel(String dbLabel) {

        Iterator<I_CmsField> it = getAllFields(false, true, true).iterator();
        while (it.hasNext()) {
            I_CmsField field = it.next();
            if (field.getDbLabel().equals(dbLabel)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns the field specified by it's name (Xpath).<p>
     *
     * @param fieldName the field's name (Xpath)
     *
     * @return the field, or null
     */
    public I_CmsField getFieldByName(String fieldName) {

        return m_fieldsByName.get(fieldName);
    }

    /**
     * Returns a list of field objects for the online form.<p>
     *
     * @return a list of field objects for the online form
     */
    public List<I_CmsField> getFields() {

        return m_fields;
    }

    /**
     * Returns the value for a field specified by it's name (Xpath).<p>
     *
     * @param fieldName the field's name (Xpath)
     *
     * @return the field value, or null
     */
    public String getFieldStringValueByName(String fieldName) {

        I_CmsField field = m_fieldsByName.get(fieldName);
        if (field != null) {
            String fieldValue = field.getValue();
            if ((field instanceof CmsDynamicField)) {
                fieldValue = getDynamicFieldValue((CmsDynamicField)field);
            }
            return (fieldValue != null) ? fieldValue.trim() : "";
        }

        return "";
    }

    /**
     * Returns the global form attributes.<p>
     *
     * @return the global form attributes
     */
    public String getFormAttributes() {

        return m_formAttributes;
    }

    /**
     * Returns the form check text.<p>
     *
     * @return the form check text
     */
    public String getFormCheckText() {

        return m_formCheckText;
    }

    /**
     * Returns the form confirmation text.<p>
     *
     * @return the form confirmation text
     */
    public String getFormConfirmationText() {

        return m_formConfirmationText;
    }

    /**
     * Returns the optional form input field attributes.<p>
     *
     * @return the optional form input field attributes
     */
    public String getFormFieldAttributes() {

        return m_formFieldAttributes;
    }

    /**
     * Returns the optional form field width ratio.<p>
     *
     * @return the optional form field width ratio
     */
    public int getFormFieldWidthRatio() {

        return m_formFieldWidthRatio;
    }

    /**
     * Returns the form footer text.<p>
     *
     * @return the form footer text
     */
    public String getFormFooterText() {

        return m_formFooterText;
    }

    /**
     * Returns the id identifying the form entries that came from this form in the database.<p>
     *
     * @return the id identifying the form entries that came from this form in the database
     */
    public String getFormId() {

        return m_formId;
    }

    /**
     * Returns the form middle text.<p>
     *
     * @return the form middle text
     */
    public String getFormMiddleText() {

        return m_formMiddleText;
    }

    /**
     * Returns the form text.<p>
     *
     * @return the form text
     */
    public String getFormText() {

        return m_formText;
    }

    /**
     * Returns the current jsp action element.<p>
     *
     * @return the jsp action element
     */
    public CmsJspActionElement getJspAction() {

        return m_jspAction;
    }

    /**
     * Returns the mail bcc recipient(s).<p>
     *
     * @return the mail bcc recipient(s)
     */
    public String getMailBCC() {

        return m_mailBCC;
    }

    /**
     * Returns the mail cc recipient(s).<p>
     *
     * @return the mail cc recipient(s)
     */
    public String getMailCC() {

        return m_mailCC;
    }

    /**
     * Returns the optional email CSS style sheet.<p>
     *
     * @return the optional email CSS style sheet
     */
    public String getMailCSS() {

        return m_mailCSS;
    }

    /**
     * Returns the mail sender address.<p>
     *
     * @return the mail sender address
     */
    public String getMailFrom() {

        return m_mailFrom;
    }

    /**
     * Returns the mail sender name.<p>
     *
     * @return the mail sender name
     */
    public String getMailFromName() {

        return m_mailFromName;
    }

    /**
     * Returns the mail subject.<p>
     *
     * @return the mail subject
     */
    public String getMailSubject() {

        return m_mailSubject;
    }

    /**
     * Returns the mail subject prefix.<p>
     *
     * @return the mail subject prefix
     */
    public String getMailSubjectPrefix() {

        return m_mailSubjectPrefix;
    }

    /**
     * Returns the mail text.<p>
     *
     * @return the mail text
     */
    public String getMailText() {

        return m_mailText;
    }

    /**
     * Returns the mail text as plain text.<p>
     *
     * @return the mail text as plain text
     */
    public String getMailTextPlain() {

        return m_mailTextPlain;
    }

    /**
     * Returns the mail recipient(s).<p>
     *
     * @return the mail recipient(s)
     */
    public String getMailTo() {

        return m_mailTo;
    }

    /**
     * Returns the mail type ("text" or "html").<p>
     *
     * @return the mail type
     */
    public String getMailType() {

        return m_mailType;
    }

    /**
     * Returns the optional form maximum submissions number.<p>
     *
     * @return the optional form maximum submissions number
     */
    public long getMaximumSubmissions() {

        return m_maxSubmissions;
    }

    /**
     * Returns the form maximum submissions text.<p>
     *
     * @return the form maximum submissions text
     */
    public String getMaximumSubmissionsText() {

        return m_maxSubmissionsText;
    }

    /**
     * Returns the property file.
     * <p>
     *
     * @return the property file.
     */
    public String getPropertyFile() {

        return m_propertyFile;
    }

    /**
     * Returns the interval to refresh the session.<p>
     *
     * @return the interval to refresh the session
     */
    public int getRefreshSessionInterval() {

        return m_refreshSessionInterval;
    }

    /**
     * Returns the optional form release date.<p>
     *
     * @return the optional form release date
     */
    public long getReleaseDate() {

        return m_releaseDate;
    }

    /**
     * Returns the form release text.<p>
     *
     * @return the form release text
     */
    public String getReleaseText() {

        return m_releaseText;
    }

    /**
     * Returns if the check page should be shown.<p>
     *
     * @return true if the check page should be shown, otherwise false
     */
    public boolean getShowCheck() {

        return m_showCheck;
    }

    /**
     * Returns the target URI of this form.<p>
     *
     * This optional target URI can be used to redirect the user to an OpenCms page instead of displaying a confirmation
     * text from the form's XML content.<p>
     *
     * @return the target URI
     */
    public String getTargetUri() {

        return m_targetUri;
    }

    /**
     * Returns the optional HTML template file.<p>
     *
     * @return the optional HTML template file
     */
    public String getTemplateFile() {

        return m_templateFile;
    }

    /**
     * Returns the form title.<p>
     *
     * @return the form title
     */
    public String getTitle() {

        return m_title;
    }

    /**
         * Tests if a captcha field is configured for this form.<p>
         *
         * @return true, if a captcha field is configured for this form
         */
    public boolean hasCaptchaField() {

        return m_captchaField != null;
    }

    /**
     * Returns if the form has configuration errors.<p>
     *
     * @return true if the form has configuration errors, otherwise false
     */
    public boolean hasConfigurationErrors() {

        return m_configurationErrors.size() > 0;
    }

    /**
     * Returns true if at least one of the configured fields is mandatory.<p>
     *
     * @return true if at least one of the configured fields is mandatory, otherwise false
     */
    public boolean hasMandatoryFields() {

        return m_hasMandatoryFields;
    }

    /**
     * Tests if this form has a target URI specified.<p>
     *
     * This optional target URI can be used to redirect the user to an OpenCms page instead of displaying a confirmation
     * text from the form's XML content.<p>
     *
     * @return the target URI
     */
    public boolean hasTargetUri() {

        return CmsStringUtil.isNotEmpty(m_targetUri);
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

        init(jsp, messages, initial, formConfigUri, formAction, null);
    }

    /**
     * Initializes the form configuration and creates the necessary form field objects.<p>
     *
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the XML configuration
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * @param formAction the desired action submitted by the form
     * @param dynamicConfig map of configurations that can overwrite the configuration from the configuration file
     *
     * @throws Exception if parsing the configuration fails
     */
    public void init(
        CmsFormHandler jsp,
        CmsMessages messages,
        boolean initial,
        String formConfigUri,
        String formAction,
        Map<String, String> dynamicConfig) throws Exception {

        m_parameterMap = jsp.getParameterMap();
        // read the form configuration file from VFS
        if (CmsStringUtil.isEmpty(formConfigUri)) {
            formConfigUri = jsp.getRequestContext().getUri();
        }
        m_configUri = formConfigUri;
        CmsFile file = jsp.getCmsObject().readFile(formConfigUri);
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(jsp.getCmsObject(), file);

        // get current locale
        Locale locale = jsp.getRequestContext().getLocale();
        if (!content.hasLocale(locale)) {
            // the current locale is not available in the content, use other locale
            boolean foundLocale = false;
            List<Locale> defaultLocales = OpenCms.getLocaleManager().getDefaultLocales(
                jsp.getCmsObject(),
                formConfigUri);
            // first try to get one of the default locales
            for (Iterator<Locale> i = defaultLocales.iterator(); i.hasNext();) {
                Locale defaultLocale = i.next();
                if (content.hasLocale(defaultLocale)) {
                    locale = defaultLocale;
                    foundLocale = true;
                    break;
                }
            }
            if (!foundLocale) {
                // no locale found yet, use first one of content as fallback
                locale = content.getLocales().get(0);
            }
        }

        // init member variables
        initMembers();

        m_formAction = formAction;
        m_fields = new ArrayList<I_CmsField>();
        m_dynaFields = new ArrayList<I_CmsField>();
        m_fieldsByName = new HashMap<String, I_CmsField>();
        m_jspAction = jsp;

        // initialize general form configuration
        // for backwards compatibility, check if dynamicConfig really exists
        if (dynamicConfig == null) {
            initFormGlobalConfiguration(content, jsp.getCmsObject(), locale, messages);
        } else {
            initFormGlobalConfiguration(content, jsp.getCmsObject(), locale, messages, dynamicConfig);
        }

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
     * Tests if the check page was submitted.<p>
     *
     * @return true, if the check page was submitted
     */
    public boolean isCheckPageSubmitted() {

        return CmsFormHandler.ACTION_CONFIRMED.equals(m_formAction);
    }

    /**
     * Returns if the optional confirmation mail is enabled.<p>
     *
     * @return true if the optional confirmation mail is enabled, otherwise false
     */
    public boolean isConfirmationMailEnabled() {

        return m_confirmationMailEnabled;
    }

    /**
     * Returns if the confirmation mail if optional, i.e. selectable by the form submitter.<p>
     *
     * @return true if the confirmation mail if optional, i.e. selectable by the form submitter, otherwise false
     */
    public boolean isConfirmationMailOptional() {

        return m_confirmationMailOptional;
    }

    /**
     * Returns <code>true</code> if the request should be forwarded to
     * the given target URI, <code>false</code> otherwise.<p>
     *
     * @return the <code>true</code> if the request should be forwarded
     */
    public boolean isForwardMode() {

        return m_forwardMode;
    }

    /**
     * Tests if the input page was submitted.<p>
     *
     * @return true, if the input page was submitted
     */
    public boolean isInputFormSubmitted() {

        return CmsFormHandler.ACTION_SUBMIT.equals(m_formAction);
    }

    /**
     * Returns the flag if an instant redirect to the configured target URI should be executed.<p>
     *
     * @return the flag if an instant redirect to the configured target URI should be executed
     */
    public boolean isInstantRedirect() {

        return m_instantRedirect;
    }

    /**
     * Returns if the session should be refreshed when displaying the form.<p>
     *
     * @return <code>true</code> if the session should be refreshed, otherwise <code>false</code>
     */
    public boolean isRefreshSession() {

        return m_refreshSessionInterval > 0;
    }

    /**
     * Returns if the mandatory marks and text should be shown.<p>
     *
     * @return true if the mandatory marks and text should be shown, otherwise false
     */
    public boolean isShowMandatory() {

        return m_showMandatory;
    }

    /**
     * Returns if the reset button should be shown.<p>
     *
     * @return true if the reset button should be shown, otherwise false
     */
    public boolean isShowReset() {

        return m_showReset;
    }

    /**
     * Returns true to signal that data should be stored in the database or false (default).<p>
     *
     * @return true to signal that data should be stored in the database or false (default)
     */
    public boolean isTransportDatabase() {

        return m_transportDatabase;
    }

    /**
     * Returns true to signal that data should be sent by email (default) or false.<p>
     *
     * @return true to signal that data should be sent by email (default) or false
     */
    public boolean isTransportEmail() {

        return m_transportEmail;
    }

    /**
     * Removes the captcha field from the list of all fields, if present.<p>
     */
    public void removeCaptchaField() {

        Iterator<I_CmsField> it = m_fields.iterator();
        while (it.hasNext()) {
            I_CmsField field = it.next();
            if (field instanceof CmsCaptchaField) {
                it.remove();
                m_fieldsByName.remove(field.getName());
            }
        }
    }

    /**
     * Sets the form text.<p>
     *
     * @param formText the form text
     */
    public void setFormText(String formText) {

        m_formText = formText;
    }

    /**
     * Sets if the mandatory marks and text should be shown.<p>
     *
     * @param showMandatory the setting for the mandatory marks
     */
    public void setShowMandatory(boolean showMandatory) {

        m_showMandatory = showMandatory;
    }

    /**
     * Sets if the reset button should be shown.<p>
     *
     * @param showReset the setting for the reset button
     */
    public void setShowReset(boolean showReset) {

        m_showReset = showReset;
    }

    /**
     * Sets the HTML template file.<p>
     *
     * This is public to be able to set the template file from a formatter JSP file.<p>
     *
     * @param templateFile the HTML template file
     */
    public void setTemplateFile(final String templateFile) {

        m_templateFile = templateFile;
    }

    /**
     * Sets the form title.<p>
     *
     * @param title the form title
     */
    public void setTitle(String title) {

        m_title = title;
    }

    /**
     * Adds a field to the form.<p>
     *
     * @param field the field to be added to the form
     */
    protected void addField(I_CmsField field) {

        if ((field instanceof CmsDisplayField) || (field instanceof CmsHiddenDisplayField)) {
            m_fields.add(field);
        } else if (field instanceof CmsDynamicField) {
            m_dynaFields.add(field);
        } else {
            m_fields.add(field);
        }
        // the fields are also internally backed in a map keyed by their field name
        m_fieldsByName.put(field.getName(), field);
    }

    /**
     * Creates the checkbox field to activate the confirmation mail in the input form.<p>
     *
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the XML configuration, otherwise values are read from the request
     *
     * @return the checkbox field to activate the confirmation mail in the input form
     */
    protected I_CmsField createConfirmationMailCheckbox(CmsMessages messages, boolean initial) {

        A_CmsField field = new CmsCheckboxField();
        field.setName(PARAM_SENDCONFIRMATION + getConfigId());
        field.setDbLabel(PARAM_SENDCONFIRMATION);
        field.setLabel(messages.key("form.confirmation.label"));
        // check the field status
        boolean isChecked = false;
        if (!initial && Boolean.valueOf(getParameter(PARAM_SENDCONFIRMATION + getConfigId())).booleanValue()) {
            // checkbox is checked by user
            isChecked = true;
        }
        // create item for field
        CmsFieldItem item = new CmsFieldItem(
            Boolean.toString(true),
            getConfirmationMailCheckboxLabel(),
            isChecked,
            false);
        List<CmsFieldItem> items = new ArrayList<CmsFieldItem>(1);
        items.add(item);
        field.setItems(items);
        return field;
    }

    /**
     * Resolves the value of a dynamic field.<p>
     *
     * @param field the field to resolve the value for
     *
     * @return the value of the given dynamic field
     */
    protected String getDynamicFieldValue(CmsDynamicField field) {

        if (field.getResolvedValue() == null) {
            try {
                I_CmsDynamicFieldResolver resolver = (I_CmsDynamicFieldResolver)Class.forName(
                    getDynamicFieldClass()).newInstance();
                field.setResolvedValue(resolver.resolveValue(field, this));
            } catch (Throwable e) {
                field.setResolvedValue(e.getLocalizedMessage());
            }
        }
        return field.getResolvedValue();
    }

    /**
     * Instantiates a new type instance of the given field type.<p>
     *
     * @param fieldType the field type to instantiate
     *
     * @return the instantiated field type or <code>null</code> is fails
     */
    protected I_CmsField getField(String fieldType) {

        return CmsFieldFactory.getSharedInstance().getField(fieldType);
    }

    /**
     * Returns the request parameter with the specified name.<p>
     *
     * @param parameter the parameter to return
     *
     * @return the parameter value
     */
    protected String getParameter(String parameter) {

        try {
            return (m_parameterMap.get(parameter))[0];
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Initializes the optional captcha field.<p>
     *
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param content the XML configuration content
     * @param locale the currently active Locale
     * @param initial if true, field values are filled with values specified in the XML configuration, otherwise values are read from the request
     */
    protected void initCaptchaField(CmsJspActionElement jsp, CmsXmlContent content, Locale locale, boolean initial) {

        boolean captchaFieldIsOnInputPage = captchaFieldIsOnInputPage();
        boolean displayCheckPage = captchaFieldIsOnCheckPage() && isInputFormSubmitted();
        boolean submittedCheckPage = captchaFieldIsOnCheckPage() && isCheckPageSubmitted();

        // Todo: read the captcha settings here, don't provide xmlcontent with form!!!
        if (captchaFieldIsOnInputPage || displayCheckPage || submittedCheckPage) {

            CmsObject cms = jsp.getCmsObject();

            I_CmsXmlContentValue xmlValueCaptcha = CmsFormContentUtil.getContentValue(content, NODE_CAPTCHA, locale);
            if (xmlValueCaptcha != null) {

                // get the field label
                String xPathCaptcha = xmlValueCaptcha.getPath() + "/";
                String stringValue = content.getStringValue(cms, xPathCaptcha + NODE_FIELDLABEL, locale);
                String fieldLabel = getConfigurationValue(stringValue, "");

                // get the field value
                String fieldValue = "";
                if (!initial) {
                    fieldValue = getParameter(CmsCaptchaField.C_PARAM_CAPTCHA_PHRASE + getConfigId());
                    if (fieldValue == null) {
                        fieldValue = "";
                    }
                }

                // get the image settings from the XML content
                CmsCaptchaSettings captchaSettings = CmsCaptchaSettings.getInstance(jsp);
                captchaSettings.init(cms, content, locale, getConfigId());
                m_captchaField = new CmsCaptchaField(captchaSettings, fieldLabel, fieldValue);
            }
        }
    }

    /**
     * Initializes the general online form settings.<p>
     *
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     * @param messages the localized messages
     * @throws Exception if initializing the form settings fails
     */
    protected void initFormGlobalConfiguration(
        CmsXmlContent content,
        CmsObject cms,
        Locale locale,
        CmsMessages messages) throws Exception {

        initFormGlobalConfiguration(content, cms, locale, messages, null);
    }

    /**
    * Initializes the general online form settings.<p>
    *
    * @param content the XML configuration content
    * @param cms the CmsObject to access the content values
    * @param locale the currently active Locale
    * @param messages the localized messages
    * @param dynamicConfig map of configurations that can overwrite the configuration from the configuration file
    *
    * @throws Exception if initializing the form settings fails
    *
    */
    protected void initFormGlobalConfiguration(
        CmsXmlContent content,
        CmsObject cms,
        Locale locale,
        CmsMessages messages,
        Map<String, String> dynamicConfig) throws Exception {

        // create a macro resolver with the cms object
        CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(cms).setKeepEmptyMacros(true);

        String stringValue;

        // get the form text
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_FORMTEXT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_FORMTEXT, locale);
        }
        setFormText(getConfigurationValue(resolver, stringValue, null));
        // get the form title
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_TITLE);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_TITLE, locale);
        }
        setTitle(getConfigurationValue(resolver, stringValue, ""));
        // get the form middle text
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_FORMMIDDLETEXT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_FORMMIDDLETEXT, locale);
        }
        setFormMiddleText(getConfigurationValue(resolver, stringValue, null));
        // get the form footer text
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_FORMFOOTERTEXT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_FORMFOOTERTEXT, locale);
        }
        setFormFooterText(getConfigurationValue(resolver, stringValue, null));
        // get the form confirmation text
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_FORMCONFIRMATION);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_FORMCONFIRMATION, locale);
        }
        setFormConfirmationText(getConfigurationValue(resolver, stringValue, ""));
        // get the optional target URI
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_TARGET_URI);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_TARGET_URI, locale);
        }
        setTargetUri(getConfigurationValue(stringValue, ""));
        // get the optional forward mode
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_FORWARD_MODE);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_FORWARD_MODE, locale);
        }
        setForwardMode(Boolean.parseBoolean(getConfigurationValue(stringValue, Boolean.FALSE.toString())));
        // get the optional instant redirect
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_INSTANTREDIRECT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_INSTANTREDIRECT, locale);
        }
        setInstantRedirect(Boolean.parseBoolean(getConfigurationValue(stringValue, Boolean.FALSE.toString())));
        // get the mail from address
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_MAILFROM);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_MAILFROM, locale);
        }
        setMailFrom(getConfigurationValue(stringValue, ""));
        // get the mail from name
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_MAILFROMNAME);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_MAILFROMNAME, locale);
        }
        setMailFromName(getConfigurationValue(stringValue, ""));
        // get the mail to address(es)
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_MAILTO);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_MAILTO, locale);
        }
        String mailto = (cms.readPropertyObject(cms.getRequestContext().getUri(), PROPERTY_MAILTO, false)).getValue("");
        if (CmsStringUtil.isNotEmpty(mailto)) {
            setMailTo(mailto);
        } else {
            setMailTo(getConfigurationValue(stringValue, ""));
        }
        // get the mail subject
        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_MAILSUBJECT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, NODE_MAILSUBJECT, locale);
        }
        setMailSubject(getConfigurationValue(resolver, stringValue, ""));
        // get the optional mail subject prefix from localized messages
        stringValue = messages.key("form.mailsubject.prefix");
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
            // prefix present, set it
            setMailSubjectPrefix(stringValue + " ");
        } else {
            // no prefix present
            setMailSubjectPrefix("");
        }

        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_MAILTEXT);
        if (stringValue == null) {
            CmsXmlHtmlValue mailTextValue = (CmsXmlHtmlValue)getContentValue(content, NODE_MAILTEXT, locale);
            if (mailTextValue != null) {
                // get the mail text
                stringValue = mailTextValue.getStringValue(cms);
                stringValue = getConfigurationValue(resolver, stringValue, "");
            } else {
                stringValue = "";
            }
        }
        setMailText(stringValue);
        // get the mail text as plain text
        stringValue = CmsHtmlToTextConverter.htmlToText(stringValue, cms.getRequestContext().getEncoding());
        setMailTextPlain(stringValue);

        stringValue = getValueFromDynamicConfig(dynamicConfig, NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION);
        if (stringValue == null) {
            if (content.hasValue(NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION, locale)) {
                stringValue = getContentStringValue(content, cms, NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION, locale);
            }
        }
        setCsvExportConfiguration(stringValue);

        // optional data target configuration
        String pathPrefix = NODE_DATATARGET + "/";
        stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_DATATARGET_TRANSPORT);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_DATATARGET_TRANSPORT, locale);
        }
        stringValue = getConfigurationValue(stringValue, "email");
        if (DATATARGET_EMAIL.equals(stringValue)) {
            this.setTransportEmail(true);
            this.setTransportDatabase(false);
        } else if (DATATARGET_DATABASE.equals(stringValue)) {
            this.setTransportEmail(false);
            this.setTransportDatabase(true);
        } else if (DATATARGET_EMAIL_DATABASE.equals(stringValue)) {
            this.setTransportEmail(true);
            this.setTransportDatabase(true);

        } else {
            // default behavior:
            this.setTransportEmail(true);
            this.setTransportDatabase(false);
        }

        stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_DATATARGET_FORMID);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_DATATARGET_FORMID, locale);
        }
        setFormId(getConfigurationValue(stringValue, content.getFile().getRootPath()));

        if (content.hasValue(NODE_OPTIONALCONFIGURATION, locale)) {
            // optional configuration options
            pathPrefix = NODE_OPTIONALCONFIGURATION + "/";

            // get the mail type
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_MAILTYPE, locale);
            setMailType(getConfigurationValue(stringValue, MAILTYPE_HTML));
            // get the mail CC recipient(s)
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_MAILCC, locale);
            setMailCC(getConfigurationValue(stringValue, ""));
            // get the mail BCC recipient(s)
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_MAILBCC, locale);
            setMailBCC(getConfigurationValue(stringValue, ""));
            // get the mail CSS style sheet
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_MAILCSS, locale);
            setMailCSS(getConfigurationValue(stringValue, ""));
            // get the form check page flag
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_SHOWCHECK, locale);
            setShowCheck(Boolean.valueOf(stringValue).booleanValue());
            // get the check page text
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_FORMCHECKTEXT, locale);
            setFormCheckText(getConfigurationValue(resolver, stringValue, ""));
            // get the dynamic fields class
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_DYNAMICFIELDCLASS, locale);
            setDynamicFieldClass(getConfigurationValue(stringValue, ""));
            // get the CSS file
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CSS_FILE, locale);
            setCssFile(getConfigurationValue(stringValue, ""));
            // get the optional HTML template file
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_TEMPLATE_FILE, locale);
            String defaultTemplateFile = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                MODULE_PARAM_TEMPLATE_FILE,
                VFS_PATH_DEFAULT_TEMPLATEFILE);
            setTemplateFile(getConfigurationValue(stringValue, defaultTemplateFile));
            // get the optional property file
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_PROPERTY_FILE, locale);
            setPropertyFile(getConfigurationValue(stringValue, ""));
            // get the optional web form action class
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_ACTION_CLASS, locale);
            setActionClass(getConfigurationValue(stringValue, ""));
            // get the show mandatory setting
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_SHOWMANDATORY, locale);
            setShowMandatory(
                Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
            // get the show reset button setting
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_SHOWRESET, locale);
            setShowReset(Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
            // get the form attributes
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_FORMATTRIBUTES, locale);
            if (CmsStringUtil.isNotEmpty(stringValue)) {
                setFormAttributes(" " + stringValue);
            }
            // get the field attributes
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_FORMFIELDATTRIBUTES, locale);
            if (CmsStringUtil.isNotEmpty(stringValue)) {
                setFormFieldAttributes(" " + stringValue);
            } else {
                // no field attributes specified, check default field attributes
                String defaultAttributes = messages.key("form.field.default.attributes");
                if (CmsStringUtil.isNotEmpty(defaultAttributes)) {
                    setFormFieldAttributes(" " + defaultAttributes);
                }
            }
            // get the field width ratio if set
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_FORMFIELDWIDTHRATIO, locale);
            if (CmsStringUtil.isNotEmpty(stringValue)) {
                try {
                    setFormFieldWidthRatio(Integer.parseInt(stringValue));
                } catch (NumberFormatException nfe) {
                    // invalid value, ignore setting
                }
            }

            // get the refresh session interval
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_KEEPSESSION, locale);
            if (CmsStringUtil.isNotEmpty(stringValue)) {
                try {
                    setRefreshSessionInterval(Integer.parseInt(stringValue) * 1000);
                } catch (NumberFormatException nfe) {
                    // invalid value found, just do not set value
                }
            }
        } else {
            // no optional configuration enabled
            // set default field attributes if present
            String defaultAttributes = messages.key("form.field.default.attributes");
            if (CmsStringUtil.isNotEmpty(defaultAttributes)) {
                setFormFieldAttributes(" " + defaultAttributes);
            }
        }

        // set or potentially overwrite optional configurations by the dynamic configuration
        if (dynamicConfig != null) {
            // get the mail type
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_MAILTYPE);
            if (stringValue != null) {
                setMailType(getConfigurationValue(stringValue, MAILTYPE_HTML));
            }
            // get the mail CC recipient(s)
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_MAILCC);
            if (stringValue != null) {
                setMailCC(getConfigurationValue(stringValue, ""));
            }
            // get the mail BCC recipient(s)
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_MAILBCC);
            if (stringValue != null) {
                setMailBCC(getConfigurationValue(stringValue, ""));
            }
            // get the mail CSS style sheet
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_MAILCSS);
            if (stringValue != null) {
                setMailCSS(getConfigurationValue(stringValue, ""));
            }
            // get the form check page flag
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_SHOWCHECK);
            if (stringValue != null) {
                setShowCheck(Boolean.valueOf(stringValue).booleanValue());
            }
            // get the check page text
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_FORMCHECKTEXT);
            if (stringValue != null) {
                setFormCheckText(getConfigurationValue(resolver, stringValue, ""));
            }
            // get the dynamic fields class
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_DYNAMICFIELDCLASS);
            if (stringValue != null) {
                setDynamicFieldClass(getConfigurationValue(stringValue, ""));
            }
            // get the CSS file
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CSS_FILE);
            if (stringValue != null) {
                setCssFile(getConfigurationValue(stringValue, ""));
            }
            // get the optional HTML template file
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_TEMPLATE_FILE);
            if (stringValue != null) {
                String defaultTemplateFile = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                    MODULE_PARAM_TEMPLATE_FILE,
                    VFS_PATH_DEFAULT_TEMPLATEFILE);
                setTemplateFile(getConfigurationValue(stringValue, defaultTemplateFile));
            }
            // get the optional property file
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_PROPERTY_FILE);
            if (stringValue != null) {
                setPropertyFile(getConfigurationValue(stringValue, ""));
            }
            // get the optional web form action class
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_ACTION_CLASS);
            if (stringValue != null) {
                setActionClass(getConfigurationValue(stringValue, ""));
            }
            // get the show mandatory setting
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_SHOWMANDATORY);
            if (stringValue != null) {
                setShowMandatory(
                    Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
            }
            // get the show reset button setting
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_SHOWRESET);
            if (stringValue != null) {
                setShowReset(
                    Boolean.valueOf(getConfigurationValue(stringValue, Boolean.TRUE.toString())).booleanValue());
            }
            // get the form attributes
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_FORMATTRIBUTES);
            if (stringValue != null) {
                setFormAttributes(" " + stringValue);
            }
            // get the field attributes
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_FORMFIELDATTRIBUTES);
            if (stringValue != null) {
                setFormFieldAttributes(" " + stringValue);
            }
            // get the refresh session interval
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_KEEPSESSION);
            if (stringValue != null) {
                try {
                    setRefreshSessionInterval(Integer.parseInt(stringValue) * 1000);
                } catch (NumberFormatException nfe) {
                    // invalid value found, just do not set value
                }
            }
        }

        // optional confirmation mail nodes
        pathPrefix = NODE_OPTIONALCONFIRMATION + "/";

        // get the confirmation mail enabled flag
        stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILENABLED);
        if (stringValue == null) {
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILENABLED, locale);
        }
        setConfirmationMailEnabled(Boolean.valueOf(stringValue).booleanValue());
        // get other confirmation mail nodes only if confirmation mail is enabled
        if (isConfirmationMailEnabled()) {
            // get the optional confirmation mail from
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILFROM);
            if (stringValue == null) {
                stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILFROM, locale);
            }
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
                setConfirmationMailFrom(stringValue);
            } else {
                setConfirmationMailFrom("");
            }

            // get the optional confirmation mail from name
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILFROMNAME);
            if (stringValue == null) {
                stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILFROMNAME, locale);
            }
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
                setConfirmationMailFromName(stringValue);
            } else {
                setConfirmationMailFromName("");
            }

            // get the confirmation mail subject
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILSUBJECT);
            if (stringValue == null) {
                stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILSUBJECT, locale);
            }
            setConfirmationMailSubject(getConfigurationValue(stringValue, ""));

            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILTEXT);
            if (stringValue == null) {
                CmsXmlHtmlValue mailTextValue = (CmsXmlHtmlValue)getContentValue(
                    content,
                    pathPrefix + NODE_CONFIRMATIONMAILTEXT,
                    locale);
                if (mailTextValue != null) {
                    // get the confirmation mail text
                    stringValue = mailTextValue.getStringValue(cms);
                    stringValue = getConfigurationValue(resolver, stringValue, "");
                } else {
                    stringValue = "";
                }
            }
            setConfirmationMailText(stringValue);
            // get the confirmation mail text as plain text
            stringValue = CmsHtmlToTextConverter.htmlToText(stringValue, cms.getRequestContext().getEncoding());
            setConfirmationMailTextPlain(stringValue);

            // get the confirmation mail field index number
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILTEXT);
            if (stringValue == null) {
                stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILFIELD, locale);
            }
            stringValue = getConfigurationValue(stringValue, "nonumber");
            int fieldIndex = -1;
            try {
                fieldIndex = Integer.parseInt(stringValue) - 1;
            } catch (Exception e) {
                // no field number given, store DB label
                if (stringValue.contains("|")) {
                    stringValue = stringValue.substring(stringValue.indexOf("|") + 1);
                }
                setConfirmationMailFieldDbLabel(stringValue);
            }
            setConfirmationMailField(fieldIndex);
            // get the confirmation mail optional flag
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILOPTIONAL);
            if (stringValue == null) {
                stringValue = getContentStringValue(content, cms, pathPrefix + NODE_CONFIRMATIONMAILOPTIONAL, locale);
            }
            setConfirmationMailOptional(Boolean.valueOf(stringValue).booleanValue());
            // get the confirmation mail checkbox label text
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_CONFIRMATIONMAILCHECKBOXLABEL);
            if (stringValue == null) {
                stringValue = getContentStringValue(
                    content,
                    cms,
                    pathPrefix + NODE_CONFIRMATIONMAILCHECKBOXLABEL,
                    locale);
            }
            setConfirmationMailCheckboxLabel(
                getConfigurationValue(stringValue, messages.key("form.confirmation.checkbox")));
        }

        if (content.hasValue(NODE_OPTIONALEXPIRATION, locale)) {
            // optional confirmation mail nodes
            pathPrefix = NODE_OPTIONALEXPIRATION + "/";
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_DATE, locale);
            try {
                setExpirationDate(Long.parseLong(stringValue));
            } catch (Exception e) {
                // no valid expiration date defined, ignore setting
            }
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_TEXT, locale);
            setExpirationText(getConfigurationValue(stringValue, ""));
        }

        // possibly set or overwrite options by the dynamic configuration
        if (dynamicConfig != null) {
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_DATE);
            if (stringValue != null) {
                try {
                    setExpirationDate(Long.parseLong(stringValue));
                } catch (Exception e) {
                    // no valid expiration date defined, ignore setting
                }
            }
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_TEXT);
            if (stringValue != null) {
                setExpirationText(getConfigurationValue(stringValue, ""));
            }
        }

        if (content.hasValue(NODE_OPTIONALRELEASE, locale)) {
            // optional form release nodes
            pathPrefix = NODE_OPTIONALRELEASE + "/";
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_DATE, locale);
            try {
                setReleaseDate(Long.parseLong(stringValue));
            } catch (Exception e) {
                // no valid release date defined, ignore setting
            }
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_TEXT, locale);
            setReleaseText(getConfigurationValue(stringValue, ""));
        }

        // possibly set or overwrite options by the dynamic configuration
        if (dynamicConfig != null) {
            // optional form release nodes
            pathPrefix = NODE_OPTIONALRELEASE + "/";
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_DATE);
            if (stringValue != null) {
                try {
                    setReleaseDate(Long.parseLong(stringValue));
                } catch (Exception e) {
                    // no valid release date defined, ignore setting
                }
            }
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_TEXT);
            if (stringValue != null) {
                setReleaseText(getConfigurationValue(stringValue, ""));
            }
        }

        if (content.hasValue(NODE_OPTIONALCONFIGURATION + "/" + NODE_OPTIONALMAXSUBMISSIONS, locale)) {
            // optional form release nodes
            pathPrefix = NODE_OPTIONALCONFIGURATION + "/" + NODE_OPTIONALMAXSUBMISSIONS + "/";
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_VALUE, locale);
            try {
                setMaximumSubmissions(Long.parseLong(stringValue));
            } catch (Exception e) {
                // no valid release date defined, ignore setting
            }
            stringValue = getContentStringValue(content, cms, pathPrefix + NODE_TEXT, locale);
            setMaximumSubmissionsText(getConfigurationValue(stringValue, ""));
        }

        // possibly set or overwrite options by the dynamic configuration
        if (dynamicConfig != null) {
            // optional form release nodes
            pathPrefix = NODE_OPTIONALCONFIGURATION + "/" + NODE_OPTIONALMAXSUBMISSIONS + "/";
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_VALUE);
            if (stringValue != null) {
                try {
                    setMaximumSubmissions(Long.parseLong(stringValue));
                } catch (Exception e) {
                    // no valid release date defined, ignore setting
                }
            }
            stringValue = getValueFromDynamicConfig(dynamicConfig, pathPrefix + NODE_TEXT);
            if (stringValue != null) {
                setMaximumSubmissionsText(getConfigurationValue(stringValue, ""));
            }
        }
    }

    /**
     * Initializes the field objects of the form.<p>
     *
     * @param content the XML configuration content
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param locale the currently active Locale
     * @param messages the localized messages
     * @param initial if true, field values are filled with values specified in the XML configuration, otherwise values are read from the request
     * @throws CmsConfigurationException if parsing the configuration fails
     */
    protected void initInputFields(
        CmsXmlContent content,
        CmsJspActionElement jsp,
        Locale locale,
        CmsMessages messages,
        boolean initial) throws CmsConfigurationException {

        // initialize the optional field texts
        Map<String, CmsFieldText> fieldTexts = new HashMap<String, CmsFieldText>();
        List<I_CmsXmlContentValue> textValues = getContentValues(content, NODE_OPTIONALFIELDTEXT, locale);
        for (Iterator<I_CmsXmlContentValue> i = textValues.iterator(); i.hasNext();) {
            I_CmsXmlContentValue textField = i.next();
            String textFieldPath = textField.getPath() + "/";
            String fieldLabel = content.getStringValue(jsp.getCmsObject(), textFieldPath + NODE_INPUTFIELD, locale);
            String fieldText = content.getStringValue(jsp.getCmsObject(), textFieldPath + NODE_TEXT, locale);
            String column = content.getStringValue(jsp.getCmsObject(), textFieldPath + NODE_COLUMN, locale);
            fieldTexts.put(fieldLabel, new CmsFieldText(fieldText, column));
        }

        // store the xPaths to the sub field definitions
        Map<String, List<String>> subFieldPaths = new HashMap<String, List<String>>();
        List<I_CmsXmlContentValue> subFieldValues = getContentValues(content, NODE_OPTIONALSUBFIELD, locale);
        for (Iterator<I_CmsXmlContentValue> i = subFieldValues.iterator(); i.hasNext();) {
            I_CmsXmlContentValue subField = i.next();
            String fieldLabel = content.getStringValue(
                jsp.getCmsObject(),
                subField.getPath() + "/" + NODE_PARENTFIELD,
                locale);
            if (fieldLabel.contains("|")) {
                fieldLabel = fieldLabel.substring(fieldLabel.indexOf("|") + 1);
            }
            List<String> storedPaths = subFieldPaths.get(fieldLabel);
            if (storedPaths == null) {
                storedPaths = new ArrayList<String>();
            }
            storedPaths.add(subField.getPath());
            subFieldPaths.put(fieldLabel, storedPaths);
        }

        // get the file uploads stored in the session
        @SuppressWarnings("unchecked")
        Map<String, FileItem> fileUploads = (Map<String, FileItem>)jsp.getRequest().getSession().getAttribute(
            CmsFormHandler.ATTRIBUTE_FILEITEMS);

        // initialize the defined input fields
        List<I_CmsXmlContentValue> fieldValues = getContentValues(content, NODE_INPUTFIELD, locale);
        for (Iterator<I_CmsXmlContentValue> i = fieldValues.iterator(); i.hasNext();) {
            // add the initialized field
            addField(
                createInputField(
                    i.next().getPath(),
                    content,
                    jsp,
                    locale,
                    messages,
                    fieldTexts,
                    subFieldPaths,
                    fileUploads,
                    "",
                    initial,
                    false));
        }

        if (!jsp.getRequestContext().getCurrentProject().isOnlineProject()) {
            // validate the form configuration in offline project
            validateFormConfiguration(messages);
        }

        if (isConfirmationMailEnabled() && isConfirmationMailOptional()) {
            // add the checkbox to activate confirmation mail for customer
            I_CmsField confirmationMailCheckbox = createConfirmationMailCheckbox(messages, initial);
            addField(confirmationMailCheckbox);
        }
    }

    /**
     * Initializes the member variables.<p>
     */
    protected void initMembers() {

        setConfigurationErrors(new ArrayList<String>());
        setFormAttributes("");
        setFormCheckText("");
        setFormConfirmationText("");
        setFormFieldAttributes("");
        setFormText("");
        setFormFooterText("");
        setMailBCC("");
        setMailCC("");
        setMailFrom("");
        setMailSubject("");
        setMailText("");
        setMailTextPlain("");
        setMailTo("");
        setMailType(MAILTYPE_HTML);
        setConfirmationMailSubject("");
        setConfirmationMailText("");
        setConfirmationMailTextPlain("");
        setRefreshSessionInterval(-1);
        setShowMandatory(true);
        setShowReset(true);
        String tempfilePath = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
            MODULE_PARAM_TEMPLATE_FILE,
            VFS_PATH_DEFAULT_TEMPLATEFILE);
        setTemplateFile(tempfilePath);
        setTitle("");
    }

    /**
     * Marks the individual items of checkboxes, selectboxes and radiobuttons as selected depending on the given request parameters.<p>
     *
     * @param field the current field
     * @param value the value of the input field
     *
     * @return <code>"true"</code> if the current item is selected or checked, otherwise false
     */
    protected String readSelectedFromRequest(I_CmsField field, String value) {

        String result = "";
        if (field.needsItems()) {
            // select box or radio button or checkbox
            try {
                String[] values = m_parameterMap.get(field.getName());
                for (int i = 0; i < values.length; i++) {
                    if (CmsStringUtil.isNotEmpty(values[i]) && values[i].equals(value)) {
                        // mark this as selected
                        result = Boolean.toString(true);
                    }
                }
            } catch (Exception e) {
                // keep value null;
            }

        } else {
            // always display fields value arrays
            result = Boolean.toString(true);
        }
        return result;
    }

    /**
     * Sets the action class.
     * <p>
     *
     * @param actionClass the action class.
     */
    protected void setActionClass(final String actionClass) {

        m_actionClass = actionClass;
    }

    /**
     * Sets the form configuration errors.<p>
     *
     * @param configurationErrors the form configuration errors
     */
    protected void setConfigurationErrors(List<String> configurationErrors) {

        m_configurationErrors = configurationErrors;
    }

    /**
     * Sets the label for the optional confirmation mail checkbox on the input form.<p>
     *
     * @param confirmationMailCheckboxLabel the label for the optional confirmation mail checkbox on the input form
     */
    protected void setConfirmationMailCheckboxLabel(String confirmationMailCheckboxLabel) {

        m_confirmationMailCheckboxLabel = confirmationMailCheckboxLabel;
    }

    /**
     * Sets if the optional confirmation mail is enabled.<p>
     *
     * @param confirmationMailEnabled true if the optional confirmation mail is enabled, otherwise false
     */
    protected void setConfirmationMailEnabled(boolean confirmationMailEnabled) {

        m_confirmationMailEnabled = confirmationMailEnabled;
    }

    /**
     * Sets the index number of the input field containing the email address for the optional confirmation mail.<p>
     *
     * @param confirmationMailFieldName the name of the input field containing the email address for the optional confirmation mail
     *
     * @deprecated use {@link #setConfirmationMailFieldDbLabel(String)} instead
     */
    @Deprecated
    protected void setConfirmationMailField(int confirmationMailFieldName) {

        m_confirmationMailField = confirmationMailFieldName;
    }

    /**
     * Sets the DB label of the input field containing the email address for the optional confirmation mail.<p>
     *
     * @param confirmationMailFieldDbLabel the DB label of the input field containing the email address for the optional confirmation mail
     */
    protected void setConfirmationMailFieldDbLabel(String confirmationMailFieldDbLabel) {

        m_confirmationMailFieldDbLabel = confirmationMailFieldDbLabel;
    }

    /**
     * Sets the optional confirmation mail from.<p>
     *
     * @param confirmationMailFrom the optional confirmation mail from
     */
    protected void setConfirmationMailFrom(String confirmationMailFrom) {

        m_confirmationMailFrom = confirmationMailFrom;
    }

    /**
     * Sets the optional confirmation mail from name.<p>
     *
     * @param confirmationMailFromName the optional confirmation mail from
     */
    protected void setConfirmationMailFromName(String confirmationMailFromName) {

        m_confirmationMailFromName = confirmationMailFromName;
    }

    /**
     * Sets if the confirmation mail if optional, i.e. selectable by the form submitter.<p>
     *
     * @param confirmationMailOptional true if the confirmation mail if optional, i.e. selectable by the form submitter, otherwise false
     */
    protected void setConfirmationMailOptional(boolean confirmationMailOptional) {

        m_confirmationMailOptional = confirmationMailOptional;
    }

    /**
     * Sets the subject of the optional confirmation mail.<p>
     *
     * @param confirmationMailSubject the subject of the optional confirmation mail
     */
    protected void setConfirmationMailSubject(String confirmationMailSubject) {

        m_confirmationMailSubject = confirmationMailSubject;
    }

    /**
     * Sets the text of the optional confirmation mail.<p>
     *
     * @param confirmationMailText the text of the optional confirmation mail
     */
    protected void setConfirmationMailText(String confirmationMailText) {

        m_confirmationMailText = confirmationMailText;
    }

    /**
     * Sets the plain text of the optional confirmation mail.<p>
     *
     * @param confirmationMailTextPlain the plain text of the optional confirmation mail
     */
    protected void setConfirmationMailTextPlain(String confirmationMailTextPlain) {

        m_confirmationMailTextPlain = confirmationMailTextPlain;
    }

    /**
     * Sets the name of the CSS file to use for the form.<p>
     *
     * @param cssFile the name of the CSS file to use for the form
     */
    protected void setCssFile(String cssFile) {

        m_cssFile = cssFile;
    }

    /**
     * Sets the class name for the dynamic field value resolver.<p>
     *
     * @param className the class name to set
     */
    protected void setDynamicFieldClass(String className) {

        m_dynamicFieldClass = className;
    }

    /**
     * Sets the optional form expiration date.<p>
     *
     * @param expirationDate the optional form expiration date
     */
    protected void setExpirationDate(long expirationDate) {

        m_expirationDate = expirationDate;
    }

    /**
     * Sets the form expiration text.<p>
     *
     * @param expirationText the form expiration text
     */
    protected void setExpirationText(String expirationText) {

        m_expirationText = expirationText;
    }

    /**
     * Sets the global form attributes.<p>
     *
     * @param formAttributes the global form attributes
     */
    protected void setFormAttributes(String formAttributes) {

        m_formAttributes = formAttributes;
    }

    /**
     * Sets the form check text.<p>
     *
     * @param formCheckText the form confirmation text
     */
    protected void setFormCheckText(String formCheckText) {

        m_formCheckText = formCheckText;
    }

    /**
     * Sets the form confirmation text.<p>
     *
     * @param formConfirmationText the form confirmation text
     */
    protected void setFormConfirmationText(String formConfirmationText) {

        m_formConfirmationText = formConfirmationText;
    }

    /**
     * Sets the optional form input field attributes.<p>
     *
     * @param formFieldAttributes the optional form input field attributes
     */
    protected void setFormFieldAttributes(String formFieldAttributes) {

        m_formFieldAttributes = formFieldAttributes;
    }

    /**
     * Sets the optional form field width ratio.<p>
     *
     * @param formFieldWidthRatio the optional form field width ratio
     */
    protected void setFormFieldWidthRatio(int formFieldWidthRatio) {

        m_formFieldWidthRatio = formFieldWidthRatio;
    }

    /**
     * Sets the form footer text.<p>
     *
     * @param formFooterText the form text
     */
    protected void setFormFooterText(String formFooterText) {

        m_formFooterText = formFooterText;
    }

    /**
     * Sets the id identifying the form entries that came from this form in the database.<p>
     *
     * @param formId the id identifying the form entries that came from this form in the database
     */
    protected void setFormId(final String formId) {

        m_formId = formId;
    }

    /**
     * Sets the form middle text.<p>
     *
     * @param formMiddleText the form text
     */
    protected void setFormMiddleText(String formMiddleText) {

        m_formMiddleText = formMiddleText;
    }

    /**
     * Sets the optional forward mode.<p>
     *
     * @param isForward
     */
    protected void setForwardMode(boolean isForward) {

        m_forwardMode = isForward;
    }

    /**
     * Sets if at least one of the configured fields is mandatory.<p>
     *
     * @param hasMandatoryFields true if at least one of the configured fields is mandatory, otherwise false
     */
    protected void setHasMandatoryFields(boolean hasMandatoryFields) {

        m_hasMandatoryFields = hasMandatoryFields;
    }

    /**
     * Sets the flag if an instant redirect to the configured target URI should be executed.<p>
     *
     * @param instantRedirect the flag if an instant redirect to the configured target URI should be executed
     */
    protected void setInstantRedirect(boolean instantRedirect) {

        m_instantRedirect = instantRedirect;
    }

    /**
     * Sets the mail bcc recipient(s).<p>
     *
     * @param mailBCC the mail bcc recipient(s)
     */
    protected void setMailBCC(String mailBCC) {

        m_mailBCC = mailBCC;
    }

    /**
     * Sets the mail cc recipient(s).<p>
     *
     * @param mailCC the mail cc recipient(s)
     */
    protected void setMailCC(String mailCC) {

        m_mailCC = mailCC;
    }

    /**
     * Sets the optional email CSS style sheet.<p>
     *
     * @param mailCSS the optional email CSS style sheet
     */
    protected void setMailCSS(String mailCSS) {

        m_mailCSS = mailCSS;
    }

    /**
     * Sets the mail sender address.<p>
     *
     * @param mailFrom the mail sender address
     */
    protected void setMailFrom(String mailFrom) {

        m_mailFrom = mailFrom;
    }

    /**
     * Sets the mail sender name.<p>
     *
     * @param mailFromName the mail sender name
     */
    protected void setMailFromName(String mailFromName) {

        m_mailFromName = mailFromName;
    }

    /**
     * Sets the mail subject.<p>
     *
     * @param mailSubject the mail subject
     */
    protected void setMailSubject(String mailSubject) {

        m_mailSubject = mailSubject;
    }

    /**
     * Sets the mail subject prefix.<p>
     *
     * @param mailSubjectPrefix the mail subject prefix
     */
    protected void setMailSubjectPrefix(String mailSubjectPrefix) {

        m_mailSubjectPrefix = mailSubjectPrefix;
    }

    /**
     * Sets the mail text.<p>
     *
     * @param mailText the mail text
     */
    protected void setMailText(String mailText) {

        m_mailText = mailText;
    }

    /**
     * Sets the mail text as plain text.<p>
     *
     * @param mailTextPlain the mail text as plain text
     */
    protected void setMailTextPlain(String mailTextPlain) {

        m_mailTextPlain = mailTextPlain;
    }

    /**
     * Sets the mail recipient(s).<p>
     *
     * @param mailTo the mail recipient(s)
     */
    protected void setMailTo(String mailTo) {

        m_mailTo = mailTo;
    }

    /**
     * Sets the mail type ("text" or "html").<p>
     *
     * @param mailType the mail type
     */
    protected void setMailType(String mailType) {

        m_mailType = mailType;
    }

    /**
     * Sets the optional form max submissions number.<p>
     *
     * @param maxSubmissions the optional form max submissions number
     */
    protected void setMaximumSubmissions(long maxSubmissions) {

        m_maxSubmissions = maxSubmissions;
    }

    /**
     * Sets the form max submissions text.<p>
     *
     * @param maxSubmissionsText the form release text
     */
    protected void setMaximumSubmissionsText(String maxSubmissionsText) {

        m_maxSubmissionsText = maxSubmissionsText;
    }

    /**
     * Sets the property file.
     * <p>
     *
     * @param propertyFile the property file.
     */
    protected void setPropertyFile(final String propertyFile) {

        m_propertyFile = propertyFile;
    }

    /**
     * Sets the interval to refresh the session.<p>
     *
     * @param refreshSessionInterval the interval to refresh the session
     */
    protected void setRefreshSessionInterval(int refreshSessionInterval) {

        m_refreshSessionInterval = refreshSessionInterval;
    }

    /**
     * Sets the optional form release date.<p>
     *
     * @param releaseDate the optional form release date
     */
    protected void setReleaseDate(long releaseDate) {

        m_releaseDate = releaseDate;
    }

    /**
     * Sets the form release text.<p>
     *
     * @param releaseText the form release text
     */
    protected void setReleaseText(String releaseText) {

        m_releaseText = releaseText;
    }

    /**
     * Sets if the check page should be shown.<p>
     *
     * @param showCheck true if the check page should be shown, otherwise false
     */
    protected void setShowCheck(boolean showCheck) {

        m_showCheck = showCheck;
    }

    /**
     * Sets the target URI of this form.<p>
     *
     * This optional target URI can be used to redirect the user to an OpenCms page instead of displaying a confirmation
     * text from the form's XML content.<p>
     *
     * @param targetUri the target URI
     */
    protected void setTargetUri(String targetUri) {

        m_targetUri = targetUri;
    }

    /**
     * Sets if data should be stored to database or not (default).<p>
     *
     * @param transportDatabase flag to decide if data should be stored to database or not (default)
     */
    protected void setTransportDatabase(final boolean transportDatabase) {

        m_transportDatabase = transportDatabase;
    }

    /**
     * Sets if data should be sent by email (default) or not.<p>
     *
     * @param transportEmail flag to decide if data should be sent by email (default) or not
     */
    protected void setTransportEmail(final boolean transportEmail) {

        m_transportEmail = transportEmail;
    }

    /**
     * Validates the loaded online form configuration and creates a list of error messages, if necessary.<p>
     *
     * @param messages the localized messages
     */
    protected void validateFormConfiguration(CmsMessages messages) {

        if (isConfirmationMailEnabled()) {
            // confirmation mail is enabled, make simple field check to avoid errors
            I_CmsField confirmField = null;
            if (getConfirmationMailField() != -1) {
                try {
                    // try to get the confirmation email field
                    confirmField = getFields().get(getConfirmationMailField());
                } catch (IndexOutOfBoundsException e) {
                    // specified confirmation email field does not exist
                    getConfigurationErrors().add(messages.key("form.configuration.error.emailfield.notfound"));
                    setConfirmationMailEnabled(false);
                    return;
                }
            } else if (CmsStringUtil.isNotEmpty(getConfirmationMailFieldDbLabel())) {
                confirmField = getFieldByDbLabel(getConfirmationMailFieldDbLabel());
                if (confirmField == null) {
                    getConfigurationErrors().add(messages.key("form.configuration.error.emailfield.notfound"));
                    setConfirmationMailEnabled(false);
                    return;
                }
            }
            if ((confirmField != null) && !CmsEmailField.class.isAssignableFrom(confirmField.getClass())) {
                // specified confirmation mail input field has wrong field type
                getConfigurationErrors().add(messages.key("form.configuration.error.emailfield.type"));
            }
        }
    }

    /**
     * Initializes the field objects of the form.<p>
     *
     * @param xPath the xPath of the input field to initialize
     * @param content the XML configuration content
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param locale the currently active Locale
     * @param messages the localized messages
     * @param fieldTexts the optional field texts
     * @param subFieldPaths the optional sub field xPaths
     * @param fileUploads the uploaded files
     * @param subFieldNameSuffix the suffix for the sub field name used to create the HTML code and request parameter names
     * @param initial if true, field values are filled with values specified in the XML configuration, otherwise values are read from the request
     * @param subField indicates if a sub field should be created
     *
     * @return an initialized input field
     *
     * @throws CmsConfigurationException if parsing the configuration fails
     */
    private I_CmsField createInputField(
        String xPath,
        CmsXmlContent content,
        CmsJspActionElement jsp,
        Locale locale,
        CmsMessages messages,
        Map<String, CmsFieldText> fieldTexts,
        Map<String, List<String>> subFieldPaths,
        Map<String, FileItem> fileUploads,
        String subFieldNameSuffix,
        boolean initial,
        boolean subField) throws CmsConfigurationException {

        CmsObject cms = jsp.getCmsObject();

        // create the xPath prefix
        String inputFieldPath = xPath + "/";

        // get the field from the factory for the specified type
        // important: we don't use getContentStringValue, since the path comes directly from the input field
        String stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDTYPE, locale);
        I_CmsField field = getField(stringValue);

        // get the field labels
        stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDLABEL, locale);

        String locLabel = getConfigurationValue(stringValue, "");
        String dbLabel = locLabel;
        boolean useDbLabel = false;
        int pos = locLabel.indexOf('|');
        if (pos > -1) {
            locLabel = locLabel.substring(0, pos);
            if ((pos + 1) < dbLabel.length()) {
                dbLabel = dbLabel.substring(pos + 1);
                useDbLabel = true;
            }
        }
        field.setLabel(locLabel);
        field.setDbLabel(dbLabel);

        // create the field name
        String fieldName = xPath;
        // cut off the XML content index ("[number]")
        int indexStart = fieldName.lastIndexOf('[') + 1;
        String index = fieldName.substring(indexStart, fieldName.length() - 1);
        if (useDbLabel) {
            // set field name to db label
            fieldName = dbLabel;
            field.setName(fieldName + getConfigId());
        } else {
            // set the field name to generic value "InputField-number"
            // replace the index ("[number]") of the xmlcontent field by "-number":
            fieldName = new StringBuffer(fieldName.substring(0, indexStart - 1)).append('-').append(index).toString();
            // cut off initial path all but the last path segments
            // (make sure there is a slash in the string first).
            fieldName = "/" + fieldName;
            int slashIndex = fieldName.lastIndexOf("/");
            fieldName = fieldName.substring(slashIndex + 1);
            field.setName(fieldName + subFieldNameSuffix + getConfigId());
        }

        // get the optional text that is shown below the field
        CmsFieldText fieldText = fieldTexts.get(dbLabel);
        if (fieldText != null) {
            field.setText(fieldText);
        }

        // get the optional subfields
        if (!subField) {
            List<String> subFieldPathList = subFieldPaths.get(dbLabel);
            if (subFieldPathList != null) {
                // there are sub fields defined for this input field
                for (Iterator<String> i = subFieldPathList.iterator(); i.hasNext();) {
                    String subPath = i.next() + "/";
                    String fieldValue = content.getStringValue(cms, subPath + NODE_VALUE, locale);
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(fieldValue)) {
                        // a field value is specified, add the sub fields for the value
                        String suffix = new StringBuffer("-").append(index).append("-").append(
                            fieldValue.hashCode()).toString();
                        List<I_CmsXmlContentValue> fieldValues = content.getValues(subPath + NODE_INPUTFIELD, locale);
                        for (Iterator<I_CmsXmlContentValue> k = fieldValues.iterator(); k.hasNext();) {
                            field.addSubField(
                                fieldValue,
                                createInputField(
                                    k.next().getPath(),
                                    content,
                                    jsp,
                                    locale,
                                    messages,
                                    fieldTexts,
                                    subFieldPaths,
                                    fileUploads,
                                    suffix,
                                    initial,
                                    true));
                        }
                    }
                }
            }
        } else {
            // mark this field as sub field
            field.setSubField(subField);
        }

        // get the field parameters
        stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDPARAMS, locale);
        field.setParameters(stringValue);

        // validation error message
        stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDERRORMESSAGE, locale);
        field.setErrorMessage(stringValue);

        // fill object members in case this is no hidden field
        if (!CmsHiddenField.class.isAssignableFrom(field.getClass())) {
            // get the field validation regular expression
            stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDVALIDATION, locale);
            if (CmsEmailField.class.isAssignableFrom(field.getClass()) && CmsStringUtil.isEmpty(stringValue)) {
                // set default email validation expression for confirmation email address input field
                field.setValidationExpression(CmsEmailField.VALIDATION_REGEX);
            } else {
                field.setValidationExpression(getConfigurationValue(stringValue, ""));
            }
            if (CmsFileUploadField.class.isAssignableFrom(field.getClass())) {
                if (fileUploads != null) {
                    FileItem attachment = fileUploads.get(field.getName());
                    if (attachment != null) {
                        ((CmsFileUploadField)field).setFileSize(attachment.get().length);
                    }
                }
            }

            // get the field mandatory flag
            stringValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDMANDATORY, locale);
            boolean isMandatory = Boolean.valueOf(stringValue).booleanValue();
            field.setMandatory(isMandatory);
            if (isMandatory) {
                // set flag that determines if mandatory fields are present
                setHasMandatoryFields(true);
            }

            // special case by table fields
            if (CmsTableField.class.isAssignableFrom(field.getClass())) {
                CmsTableField tableField = (CmsTableField)field;
                String fieldValue = content.getStringValue(

                    cms,
                    inputFieldPath + NODE_FIELDDEFAULTVALUE,
                    locale);
                tableField.parseDefault(fieldValue, m_parameterMap);
            }

            if (field.needsItems()) {
                // create items for checkboxes, radio buttons and selectboxes
                String fieldValue = content.getStringValue(

                    cms,
                    inputFieldPath + NODE_FIELDDEFAULTVALUE,
                    locale);
                if (CmsStringUtil.isNotEmpty(fieldValue)) {
                    // look for %(row) makro before resolving other macros
                    boolean showInRow = false;
                    if (fieldValue.startsWith(MACRO_SHOW_ITEMS_IN_ROW)) {
                        showInRow = true;
                        fieldValue = fieldValue.substring(MACRO_SHOW_ITEMS_IN_ROW.length());
                    }
                    // substitute eventual macros
                    CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(cms).setJspPageContext(
                        jsp.getJspContext());
                    fieldValue = resolver.resolveMacros(fieldValue);
                    StringTokenizer T = new StringTokenizer(fieldValue, "|");
                    List<CmsFieldItem> items = new ArrayList<CmsFieldItem>(T.countTokens());
                    while (T.hasMoreTokens()) {
                        String part = T.nextToken();
                        // check pre selection of current item
                        boolean isPreselected = part.indexOf('*') != -1;
                        String value = "";
                        String label = "";
                        String selected = "";
                        int delimPos = part.indexOf(':');
                        if (delimPos != -1) {
                            // a special label text is given
                            value = part.substring(0, delimPos);
                            label = part.substring(delimPos + 1);
                        } else {
                            // no special label text present, use complete String
                            value = part;
                            label = value;
                        }

                        if (isPreselected) {
                            // remove preselected flag marker from Strings
                            value = CmsStringUtil.substitute(value, "*", "");
                            label = CmsStringUtil.substitute(label, "*", "");
                        }

                        if (initial) {
                            // only fill in values from configuration file if called initially
                            if (isPreselected) {
                                selected = Boolean.toString(true);
                            }
                        } else {
                            // get selected flag from request for current item
                            selected = readSelectedFromRequest(field, value);
                        }

                        // add new item object
                        items.add(new CmsFieldItem(value, label, Boolean.valueOf(selected).booleanValue(), showInRow));

                    }
                    field.setItems(items);
                } else {
                    // no items specified for checkbox, radio button or selectbox
                    throw new CmsConfigurationException(
                        Messages.get().container(
                            Messages.ERR_INIT_INPUT_FIELD_MISSING_ITEM_2,
                            field.getName(),
                            field.getType()));
                }
            }
        }
        // get the field value
        if (initial
            && CmsStringUtil.isEmpty(getParameter(field.getName()))
            && !CmsTableField.class.isAssignableFrom(field.getClass())) {
            // only fill in values from configuration file if called initially
            if (!field.needsItems()) {
                if (CmsDisplayField.class.isAssignableFrom(field.getClass())
                    || CmsHiddenDisplayField.class.isAssignableFrom(field.getClass())) {
                    String fieldValue = getDynamicFieldValue((CmsDynamicField)field);
                    field.setValue(fieldValue);
                } else {
                    String fieldValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDDEFAULTVALUE, locale);
                    if (CmsStringUtil.isNotEmpty(fieldValue)) {
                        CmsMacroResolver resolver = CmsMacroResolver.newInstance().setCmsObject(cms).setJspPageContext(
                            jsp.getJspContext());
                        fieldValue = resolver.resolveMacros(fieldValue);
                        field.setValue(fieldValue.trim());
                    }
                }
            } else {
                // for field that needs items,
                // the default value is used to set the items and not really a value
                field.setValue(null);
            }
        } else if (CmsFileUploadField.class.isAssignableFrom(field.getClass())) {
            // specific handling for file upload fields, because if they are filled out
            // they also shall be filled out if there are empty because there was an
            // other mandatory field not filled out or there was browsed through
            // different pages with the "prev" and the "next" buttons
            // here are also used hidden fields on the current page, that is why
            // it is possible that there are tow fields with the same name, one field
            // is the file upload field, the second one is the hidden field
            // the file upload field is the first one, the hidden field is the second one
            String[] parameterValues = m_parameterMap.get(field.getName());
            StringBuffer value = new StringBuffer();
            if (parameterValues != null) {
                if (parameterValues.length == 1) {
                    // there file upload field value is empty, so take the hidden field value
                    value.append(parameterValues[0]);
                } else {
                    // there are two fields with the same name
                    if (parameterValues[0].isEmpty()) {
                        // the file upload field is empty, so take the hidden field value
                        value.append(parameterValues[1]);
                    } else {
                        // the file upload field is not empty, so take this value, because
                        // so the user choosed another file than before (in the hidden field)
                        value.append(parameterValues[0]);
                    }
                }
            }
            field.setValue(value.toString());
        } else if (CmsDisplayField.class.isAssignableFrom(field.getClass())
            || CmsDisplayField.class.isAssignableFrom(field.getClass())) {
            String fieldValue = getDynamicFieldValue((CmsDynamicField)field);
            if (CmsStringUtil.isEmpty(fieldValue)) {
                // get field value from request for standard fields
                String[] parameterValues = m_parameterMap.get(field.getName());
                if (parameterValues != null) {
                    fieldValue = parameterValues[0];
                }
            }
            field.setValue(fieldValue);
        } else if (CmsEmptyField.class.isAssignableFrom(field.getClass())) {
            String fieldValue = content.getStringValue(cms, inputFieldPath + NODE_FIELDDEFAULTVALUE, locale);
            field.setValue(fieldValue);
        } else if (!CmsTableField.class.isAssignableFrom(field.getClass())) {
            // get field value from request for standard fields
            String[] parameterValues = m_parameterMap.get(field.getName());
            StringBuffer value = new StringBuffer();
            if (parameterValues != null) {
                for (int j = 0; j < parameterValues.length; j++) {
                    if (j != 0) {
                        value.append(", ");
                    }
                    value.append(parameterValues[j]);
                }
            }
            field.setValue(value.toString());
        }
        return field;
    }

    /**
     * Set the column configuration for CSV export.
     *
     * @param stringValue String with the pipe-separated column values
     */
    private void setCsvExportConfiguration(final String stringValue) {

        m_csvExportConfiguration = stringValue;
    }
}
