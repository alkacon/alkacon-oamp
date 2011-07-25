/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFormHandler.java,v $
 * Date   : $Date: 2011-05-17 12:36:06 $
 * Version: $Revision: 1.25 $
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

package com.alkacon.opencms.formgenerator;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.i18n.CmsMultiMessages;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.mail.CmsSimpleMail;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsByteArrayDataSource;
import org.opencms.util.CmsDateUtil;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

/**
 * The form handler controls the HTML or mail output of a configured email form.<p>
 * 
 * Provides methods to determine the action that takes place and methods to create different
 * output formats of a submitted form.<p>
 * 
 * @author Andreas Zahner 
 * @author Thomas Weckert
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.25 $
 * 
 * @since 7.0.4 
 */
public class CmsFormHandler extends CmsJspActionElement {

    /** Request parameter value for the form action parameter: correct the input. */
    public static final String ACTION_CONFIRMED = "confirmed";

    /** Request parameter value for the form action parameter: correct the input. */
    public static final String ACTION_CORRECT_INPUT = "correct";

    /** Request parameter value for the form action parameter: Jump to the download page of the CSV data (allow only for offline project!!!). */
    public static final String ACTION_DOWNLOAD_DATA_1 = "export1";

    /** Request parameter value for the form action parameter: Download the CSV data.  */
    public static final String ACTION_DOWNLOAD_DATA_2 = "export2";

    /** Request parameter value for the form action parameter: form submitted. */
    public static final String ACTION_SUBMIT = "submit";

    /** Name of the file item session attribute. */
    public static final String ATTRIBUTE_FILEITEMS = "fileitems";

    /** Form error: mandatory field not filled out. */
    public static final String ERROR_MANDATORY = "mandatory";

    /** Form error: unique error of input. */
    public static final String ERROR_UNIQUE = "unique";

    /** Form error: validation error of input. */
    public static final String ERROR_VALIDATION = "validation";

    /** Form info: mandatory upload field filled out already. */
    public static final String INFO_UPLOAD_FIELD_MANDATORY_FILLED_OUT = "uploadfield_mandatory_filled_out";

    /** Macro name for the date macro that can be used in mail text fields. */
    public static final String MACRO_DATE = "date";

    /** Macro name for the form data macro that can be used in mail text fields. */
    public static final String MACRO_FORMDATA = "formdata";

    /** Macro name for the locale macro that can be used in mail text fields. */
    public static final String MACRO_LOCALE = "locale";

    /** Macro name for the url macro that can be used in mail text fields. */
    public static final String MACRO_URL = "url";

    /** Request parameter name for the back parameter. */
    public static final String PARAM_BACK = "back";

    /** Request parameter name for the hidden form action parameter to determine the action. */
    public static final String PARAM_FORMACTION = "formaction";

    /** Request parameter name for the page parameter. */
    public static final String PARAM_PAGE = "page";

    /** Request parameter uri for the page parameter. */
    public static final String PARAM_URI = "uri";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormHandler.class);

    /** The entry id of the submitted form in the database. */
    protected int m_entryId = 0;

    /** Contains eventual validation errors. */
    protected Map<String, String> m_errors;

    /** The form configuration object. */
    protected CmsForm m_formConfiguration;

    /** Temporarily stores the fields as hidden fields in the String. */
    protected String m_hiddenFields;

    /** Contains eventual validation infos. */
    protected Map<String, String> m_infos;

    /** Flag indicating if the form is displayed for the first time. */
    protected boolean m_initial;

    /** Flag indicating if the form handler configuration has been initialized successfully. */
    protected boolean m_initSuccess;

    /** Boolean indicating if the form is validated correctly. */
    protected Boolean m_isValidatedCorrect;

    /** Needed to implant form fields into the mail text. */
    protected transient CmsMacroResolver m_macroResolver;

    /** The localized messages for the form handler. */
    protected CmsMultiMessages m_messages;

    /** The multipart file items. */
    protected List<FileItem> m_multipartFileItems;

    /** The templates for the web form output. */
    protected StringTemplateGroup m_outputTemplates;

    /** The map of request parameters. */
    protected Map<String, String[]> m_parameterMap;

    /**
     * Empty constructor, be sure to call one of the available initialization methods afterwards.<p>
     * 
     * Possible initialization methods are:<p>
     * <ul>
     * <li>{@link #init(PageContext, HttpServletRequest, HttpServletResponse)}</li>
     * <li>{@link #init(PageContext, HttpServletRequest, HttpServletResponse, String)}</li>
     * </ul>
     */
    public CmsFormHandler() {

        super();
        m_errors = new HashMap<String, String>();
        m_infos = new HashMap<String, String>();
    }

    /**
     * Gets the truncated file item name. Some clients, such as the Opera browser, do include path information. 
     * This method only returns the the base file name.<p>
     * 
     * @param name the file item name
     * 
     * @return the truncated file item name
     */
    public static String getTruncatedFileItemName(String name) {

        // here is not used the File.separator, because there are problems is the 
        // OpenCms OS and the explores OS are different
        // for example if you have an OpenCms on Unix and you use the 
        // formgenerator in a browser in Windows
        if (name.contains("/")) {
            int lastIndex = name.lastIndexOf("/");
            if (name.length() > lastIndex) {
                name = name.substring(lastIndex + 1);
                return name;
            }
        } else if (name.contains("\\")) {
            int lastIndex = name.lastIndexOf("\\");
            if (name.length() > lastIndex) {
                name = name.substring(lastIndex + 1);
                return name;
            }
        }
        return name;
    }

    /**
     * Adds on the first position the given messages.<p>
     * 
     * @param messages the localized messages
     */
    public void addMessages(CmsMessages messages) {

        CmsMultiMessages tmpOld = m_messages;
        m_messages = new CmsMultiMessages(messages.getLocale());
        m_messages.addMessages(messages);
        if (tmpOld != null) {
            m_messages.addMessages(tmpOld.getMessages());
        }
        tmpOld = null;
    }

    /**
     * Replaces line breaks with html &lt;br&gt;.<p>
     * 
     * @param value the value to substitute
     * @return the substituted value
     */
    public String convertToHtmlValue(String value) {

        return convertValue(value, "html");
    }

    /**
     * Replaces html &lt;br&gt; with line breaks.<p>
     * 
     * @param value the value to substitute
     * @return the substituted value
     */
    public String convertToPlainValue(String value) {

        return convertValue(value, "");
    }

    /**
     * Converts a given String value to the desired output format.<p>
     * 
     * The following output formats are possible:
     * <ul>
     * <li>"HTML" meaning that &lt;br&gt; tags are added</li>
     * <li>"plain"  or any other String value meaning that &lt;br&gt; tags are removed</li>
     * </ul>
     *  
     * @param value the String value to convert
     * @param outputType the type of the resulting output
     * @return the converted String in the desired output format
     */
    public String convertValue(String value, String outputType) {

        if ("html".equalsIgnoreCase(outputType)) {
            // output should be HTML, add line break tags and characters
            value = CmsStringUtil.escapeHtml(value);
        } else {
            // output should be plain, remove HTML line break tags and characters
            value = CmsStringUtil.substitute(value, "<br>", "\n");
        }
        return value;
    }

    /**
     * Creates the main web form output for usage on a JSP.<p>
     * 
     * @throws IOException if an I/O error occurs
     * @throws JspException if redirection fails
     */
    public void createForm() throws IOException, JspException {

        // the output writer
        Writer out = getJspContext().getOut();
        // the module path containing the resources
        String modulePath = CmsWorkplace.VFS_PATH_MODULES + CmsForm.MODULE_NAME + "/";

        if (showTemplate()) {
            // we are not on the download page, include CSS and JavaScript
            out.write(getStyleSheet());
            // determine if the user session should be kept
            String sessionJs = null;
            if (isInitSuccess() && getFormConfiguration().isRefreshSession()) {
                sessionJs = link(modulePath + "resources/js/keepsession.js");
            }
            // get the JS output template
            StringTemplate sTemplate = getOutputTemplate("form_js");
            sTemplate.setAttribute("formconfig", getFormConfiguration());
            sTemplate.setAttribute("sessionjs", sessionJs);
            sTemplate.setAttribute("sessionuri", link(modulePath + "elements/keepsession.jsp"));
            sTemplate.setAttribute("subfieldjs", link(modulePath + "resources/js/subfields.js"));
            out.write(sTemplate.toString());
            // check the template group syntax and show eventual errors
            out.write(buildTemplateGroupCheckHtml());
        }

        if (!isInitSuccess()) {
            // form was not configured correctly, show error message
            StringTemplate sTemplate = getOutputTemplate("initerror");
            sTemplate.setAttribute("headline", getMessages().key("form.init.error.headline"));
            sTemplate.setAttribute("text", getMessages().key("form.init.error.description"));
            out.write(sTemplate.toString());
        } else if (showExpired()) {
            // form is expired, show expiration text
            out.write(getFormConfiguration().getExpirationText());
        } else if (!showForm()) {
            // form has been submitted with correct values, decide further actions
            if (showCheck()) {
                // show optional check page
                out.write(buildCheckHtml());
            } else if (showDownloadData()) {
                // show the data download page
                //                include(modulePath + "elements/datadownload.jsp");
                Map modParameterMap = getParameterMap();
                modParameterMap.put(PARAM_URI, new String[] {getFormConfiguration().getConfigUri().toString()});
                include(modulePath + "elements/datadownload.jsp", null, false, modParameterMap);

            } else {
                // try to send a notification email with the submitted form field values
                if (sendData()) {
                    // successfully sent email, decide further actions
                    if (getFormConfiguration().hasTargetUri()) {
                        // another target URI is configured, redirect to it
                        getResponse().sendRedirect(link(getFormConfiguration().getTargetUri()));
                    } else {
                        // show confirmation end page
                        out.write(buildConfirmHtml());
                    }
                    // prepare the web form action class if configured
                    prepareAfterWebformAction();
                } else {
                    // failure sending email, show error message
                    StringTemplate sTemplate = getOutputTemplate("emailerror");
                    sTemplate.setAttribute("headline", getMessages().key("form.error.mail.headline"));
                    sTemplate.setAttribute("text", getMessages().key("form.error.mail.text"));
                    sTemplate.setAttribute("error", getErrors().get("sendmail"));
                    out.write(sTemplate.toString());
                }
            }
        } else {
            // create the main input form
            out.write(buildFormHtml());
        }
    }

    /**
     * Returns the configured form field values as hidden input fields.<p>
     * 
     * @return the configured form field values as hidden input fields
     */
    public String createHiddenFields() {

        if (CmsStringUtil.isEmpty(m_hiddenFields)) {
            List<I_CmsField> fields = getFormConfiguration().getAllFields(true, false, false);
            StringBuffer result = new StringBuffer(fields.size() * 8);
            // iterate the form fields
            for (int i = 0, n = fields.size(); i < n; i++) {
                I_CmsField currentField = fields.get(i);
                if (currentField == null) {
                    continue;
                } else if (CmsCheckboxField.class.isAssignableFrom(currentField.getClass())) {
                    // special case: checkbox, can have more than one value
                    Iterator<CmsFieldItem> k = currentField.getItems().iterator();
                    while (k.hasNext()) {
                        CmsFieldItem item = k.next();
                        if (item.isSelected()) {
                            result.append("<input type=\"hidden\" name=\"");
                            result.append(currentField.getName());
                            result.append("\" value=\"");
                            result.append(CmsEncoder.escapeXml(item.getValue()));
                            result.append("\" />\n");
                        }
                    }
                } else if (CmsTableField.class.isAssignableFrom(currentField.getClass())) {
                    // special case: table, can have more than one value
                    Iterator<CmsFieldItem> k = currentField.getItems().iterator();
                    while (k.hasNext()) {
                        CmsFieldItem item = k.next();
                        result.append("<input type=\"hidden\" name=\"");
                        result.append(currentField.getName() + item.getDbLabel());
                        result.append("\" value=\"");
                        result.append(CmsEncoder.escapeXml(item.getValue()));
                        result.append("\" />\n");
                    }
                } else if (CmsStringUtil.isNotEmpty(currentField.getValue())) {
                    // all other fields are converted to a simple hidden field
                    result.append("<input type=\"hidden\" name=\"");
                    result.append(currentField.getName());
                    result.append("\" value=\"");
                    result.append(CmsEncoder.escapeXml(currentField.getValue()));
                    result.append("\" />\n");
                }

            }
            // store the generated input fields for further usage to avoid unnecessary rebuilding
            m_hiddenFields = result.toString();
        }
        // return generated result list
        return m_hiddenFields;
    }

    /**
     * Creates the output String of the submitted fields for email creation.<p>
     * 
     * @param isHtmlMail if true, the output is formatted as HTML, otherwise as plain text
     * @param isConfirmationMail if true, the text for the confirmation mail is created, otherwise the text for mail receiver
     * 
     * @return the output String of the submitted fields for email creation
     */
    public String createMailTextFromFields(boolean isHtmlMail, boolean isConfirmationMail) {

        List<I_CmsField> fieldValues = getFormConfiguration().getAllFields(true, false, true);
        StringBuffer fieldsResult = new StringBuffer(fieldValues.size() * 16);
        List<I_CmsField> htmlFields = new ArrayList<I_CmsField>(fieldValues.size());
        String mailCss = null;
        // determine CSS to use for HTML email
        if (isHtmlMail) {
            // create HTML email using the template output
            if (CmsStringUtil.isNotEmpty(getFormConfiguration().getMailCSS())) {
                // use individually configured CSS
                mailCss = getFormConfiguration().getMailCSS();
            }
        }

        // generate output for submitted form fields
        Iterator<I_CmsField> i = fieldValues.iterator();
        while (i.hasNext()) {
            I_CmsField current = i.next();
            if (current instanceof CmsPagingField) {
                continue;
            }
            if (!useInFormDataMacro(current)) {
                continue;
            }
            if ((current instanceof CmsEmptyField) && !current.isMandatory()) {
                continue;
            }
            String value = current.toString();
            if (((current instanceof CmsDynamicField) && !((current instanceof CmsDisplayField) || (current instanceof CmsHiddenDisplayField)))) {
                if (!current.isMandatory()) {
                    // show dynamic fields only if they are marked as mandatory
                    continue;
                }
                // compute the value for the dynamic field
                value = getFormConfiguration().getFieldStringValueByName(current.getName());
            } else if (current instanceof CmsHiddenDisplayField) {
                // do not show hidden display fields and empty fields
                continue;
            } else if (current instanceof CmsFileUploadField) {
                value = current.getValue();
                value = CmsFormHandler.getTruncatedFileItemName(value);
            }
            if (isHtmlMail) {
                // format field label as HTML
                String label = current.getLabel();
                // special case for table and empty fields
                if (current instanceof CmsTableField) {
                    label = ((CmsTableField)current).buildLabel(this);
                } else if (current instanceof CmsEmptyField) {
                    label = "";
                }
                // format field value as HTML
                String fieldValue = convertToHtmlValue(value);
                // special case for table and empty fields
                if (current instanceof CmsTableField) {
                    fieldValue = ((CmsTableField)current).buildRows(this);
                } else if (current instanceof CmsEmptyField) {
                    fieldValue = value;
                }
                I_CmsField mailField = new CmsTextField();
                mailField.setLabel(label);
                mailField.setValue(fieldValue);
                htmlFields.add(mailField);
            } else {
                // format output as plain text
                String label;
                try {
                    label = CmsHtmlToTextConverter.htmlToText(
                        current.getLabel(),
                        getCmsObject().getRequestContext().getEncoding(),
                        true).trim();
                } catch (Exception e) {
                    // error parsing the String, provide it as is
                    label = current.getLabel();
                }
                fieldsResult.append(label);

                // special case by table fields
                if (current instanceof CmsTableField) {
                    fieldsResult.append(((CmsTableField)current).buildText(isConfirmationMail));
                    continue;
                }
                fieldsResult.append("\t");
                fieldsResult.append(value);
                fieldsResult.append("\n");
            }
        }

        // generate the main mail text
        String mailText;
        if (isHtmlMail) {
            // generate HTML email
            if (isConfirmationMail) {
                // append the confirmation email text
                mailText = getFormConfiguration().getConfirmationMailText();
            } else {
                // append the email text
                mailText = getFormConfiguration().getMailText();
            }
            // create field output
            StringTemplate sTemplate = getOutputTemplate("htmlemailfields");
            sTemplate.setAttribute("mailcss", mailCss);
            sTemplate.setAttribute("fields", htmlFields);
            fieldsResult.append(sTemplate.toString());
        } else {
            // generate simple text email
            if (isConfirmationMail) {
                // append the confirmation email text
                mailText = getFormConfiguration().getConfirmationMailTextPlain();
            } else {
                // append the email text
                mailText = getFormConfiguration().getMailTextPlain();
            }
        }
        // resolve the common macros
        mailText = m_macroResolver.resolveMacros(mailText);
        // check presence of formdata macro in mail text using new macro resolver (important, do not use the same here!)
        CmsMacroResolver macroResolver = CmsMacroResolver.newInstance();
        macroResolver.setKeepEmptyMacros(true);
        macroResolver.addMacro(MACRO_FORMDATA, "");
        if (mailText.length() > macroResolver.resolveMacros(mailText).length()) {
            // form data macro found, resolve it
            macroResolver.addMacro(MACRO_FORMDATA, fieldsResult.toString());
            mailText = macroResolver.resolveMacros(mailText);
        } else {
            // no form data macro found, add the fields below the mail text
            if (!isHtmlMail) {
                mailText += "\n\n";
            }
            mailText += fieldsResult;
        }

        if (isHtmlMail) {
            StringTemplate sTemplate = getOutputTemplate("htmlemail");
            String errorHeadline = null;
            if (!isConfirmationMail && getFormConfiguration().hasConfigurationErrors()) {
                // write form configuration errors to html mail
                errorHeadline = getMessages().key("form.configuration.error.headline");
            }
            // set necessary attributes
            sTemplate.setAttribute("mailcss", mailCss);
            sTemplate.setAttribute("mailtext", mailText);
            sTemplate.setAttribute("errorheadline", errorHeadline);
            sTemplate.setAttribute("errors", getFormConfiguration().getConfigurationErrors());
            return sTemplate.toString();
        } else {
            StringBuffer result = new StringBuffer(mailText);
            if (!isConfirmationMail && getFormConfiguration().hasConfigurationErrors()) {
                // write form configuration errors to text mail
                result.append("\n");
                result.append(getMessages().key("form.configuration.error.headline"));
                result.append("\n");
                for (int k = 0; k < getFormConfiguration().getConfigurationErrors().size(); k++) {
                    result.append(getFormConfiguration().getConfigurationErrors().get(k));
                    result.append("\n");
                }
            }
            return result.toString();
        }
    }

    /**
     * Returns if the data download should be initiated.<p>
     * 
     * @return true if the data download should be initiated, otherwise false
     */
    public boolean downloadData() {

        boolean result = false;

        String paramAction = getParameter(CmsFormHandler.PARAM_FORMACTION);
        if (CmsFormHandler.ACTION_DOWNLOAD_DATA_2.equals(paramAction)) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the entry id of the submitted form in the database.<p>
     * 
     * Returns a value > 0, if the form was submitted to database successfully,
     * 0, if the form is not submitted to database yet, 
     *
     * @return the entry id of the form
     */
    public int getEntryId() {

        return m_entryId;
    }

    /**
     * Returns the errors found when validating the form.<p>
     * 
     * @return the errors found when validating the form
     */
    public Map<String, String> getErrors() {

        return m_errors;
    }

    /**
     * Returns the check page text, after resolving macros.<p>
     * 
     * @return the check page text
     */
    public String getFormCheckText() {

        CmsMacroResolver macroResolver = CmsMacroResolver.newInstance();
        macroResolver.setKeepEmptyMacros(true);
        List<I_CmsField> fields = getFormConfiguration().getFields();
        I_CmsField field;
        Iterator<I_CmsField> itFields = fields.iterator();
        // add field values as macros
        while (itFields.hasNext()) {
            field = itFields.next();
            macroResolver.addMacro(field.getLabel(), field.getValue());
            if (!field.getLabel().equals(field.getDbLabel())) {
                macroResolver.addMacro(field.getDbLabel(), field.getValue());
            }
            if (field instanceof CmsTableField) {
                Iterator<CmsFieldItem> it = field.getItems().iterator();
                while (it.hasNext()) {
                    CmsFieldItem item = it.next();
                    macroResolver.addMacro(item.getLabel(), item.getValue());
                    if (!item.getLabel().equals(item.getDbLabel())) {
                        macroResolver.addMacro(item.getDbLabel(), item.getValue());
                    }
                }
            }
        }
        return macroResolver.resolveMacros(getFormConfiguration().getFormCheckText());
    }

    /**
     * Returns the form configuration.<p>
     * 
     * @return the form configuration
     */
    public CmsForm getFormConfiguration() {

        return m_formConfiguration;
    }

    /**
     * Returns the confirmation text, after resolving macros.<p>
     * 
     * @return the confirmation text
     */
    public String getFormConfirmationText() {

        return m_macroResolver.resolveMacros(getFormConfiguration().getFormConfirmationText());
    }

    /**
     * Returns the infos found when validating the form.<p>
     * 
     * @return the infos found when validating the form
     */
    public Map<String, String> getInfos() {

        return m_infos;
    }

    /**
     * Returns the localized messages.<p>
     *
     * @return the localized messages
     */
    public CmsMessages getMessages() {

        return m_messages;
    }

    /**
     * Returns the template for the web form HTML output.<p>
     * 
     * @param templateName the name of the template to return
     * 
     * @return the template for the web form HTML output
     */
    public StringTemplate getOutputTemplate(String templateName) {

        StringTemplate result = getOutputTemplateGroup().getInstanceOf(templateName);
        if (!getRequestContext().currentProject().isOnlineProject() && (result == null)) {
            // no template with the specified name found, return initialized error template
            try {
                CmsFile stFile = getCmsObject().readFile(CmsForm.VFS_PATH_ERROR_TEMPLATEFILE);
                String stContent = new String(stFile.getContents(), getCmsObject().getRequestContext().getEncoding());
                result = new StringTemplate(stContent, DefaultTemplateLexer.class);
                // set the error attributes of the template
                result.setAttribute("errorheadline", "Error getting output template");
                result.setAttribute("errortext", "The desired template \"" + templateName + "\" was not found.");
                result.setAttribute("errortemplatenames", getOutputTemplateGroup().getTemplateNames());
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while getting error output template from file \""
                    + CmsForm.VFS_PATH_ERROR_TEMPLATEFILE
                    + "\".");
            }
        }
        return result;
    }

    /**
     * Returns the output template group that generates the web form HTML output.<p>
     * 
     * @return the output template group that generates the web form HTML output
     */
    public StringTemplateGroup getOutputTemplateGroup() {

        if (m_outputTemplates == null) {
            String vfsPath = null;
            if (getFormConfiguration() != null) {
                // check if an individual output template path is set
                vfsPath = getFormConfiguration().getTemplateFile();
                if (!getCmsObject().existsResource(vfsPath)) {
                    // configured output template path does not exist, log error and use default output templates
                    LOG.error("Configured web form HTML output template file \"" + vfsPath + "\" does not exist.");
                    vfsPath = null;
                }
            }

            if (vfsPath == null) {
                // no valid template path configured, use default output templates
                vfsPath = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME).getParameter(
                    CmsForm.MODULE_PARAM_TEMPLATE_FILE,
                    CmsForm.VFS_PATH_DEFAULT_TEMPLATEFILE);
            }
            try {
                // first try to get the initialized template group from VFS cache
                String rootPath = getRequestContext().addSiteRoot(vfsPath);
                StringTemplateGroup stGroup = (StringTemplateGroup)CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().getCachedObject(
                    getCmsObject(),
                    rootPath);
                if (stGroup == null) {
                    // template group is not in cache, read the file and generate template group
                    CmsFile stFile = getCmsObject().readFile(vfsPath);
                    String stContent = new String(
                        stFile.getContents(),
                        getCmsObject().getRequestContext().getEncoding());
                    StringTemplateErrorListener errors = new CmsStringTemplateErrorListener();
                    stGroup = new StringTemplateGroup(new StringReader(stContent), DefaultTemplateLexer.class, errors);
                    // store the template group in cache
                    CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().putCachedObject(getCmsObject(), rootPath, stGroup);
                }
                m_outputTemplates = stGroup;
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while creating web form HTML output templates from file \"" + vfsPath + "\".");
            }
        }
        return m_outputTemplates;
    }

    /** 
     * Returns the map of request parameters.<p>
     * 
     * @return the map of request parameters
     */
    public Map<String, String[]> getParameterMap() {

        return m_parameterMap;
    }

    /**
     * Returns the HTML to include the CSS style sheet for the form.<p>
     * 
     * The CSS to use can be specified individually for each form and as module parameter {@link CmsForm#MODULE_PARAM_CSS}.
     * If no value is defined, the default CSS file shipped with the module is used.<p>
     * 
     * <b>Important</b>: to generate valid XHTML code, specify <code>false</code> as module parameter value and include the CSS
     * in your template head manually.<p>
     * 
     * @return the HTML to include the CSS style sheet for the form
     */
    public String getStyleSheet() {

        String cssParam = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME).getParameter(
            CmsForm.MODULE_PARAM_CSS);
        if (CmsStringUtil.FALSE.equalsIgnoreCase(cssParam)) {
            // parameter set to false, this overrides all other individual CSS settings
            return "";
        }
        StringBuffer result = new StringBuffer(64);
        // get the optional individual CSS file for the form
        String configuredCss = null;
        if (getFormConfiguration() != null) {
            configuredCss = getFormConfiguration().getCssFile();
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(configuredCss)) {
            // no individual CSS configured, use CSS configured as module parameter
            configuredCss = cssParam;
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(configuredCss)) {
                // no module parameter set, fall back to default CSS
                configuredCss = CmsWorkplace.VFS_PATH_MODULES + CmsForm.MODULE_NAME + "/resources/css/webform.css";
            }
        }

        result.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(link(configuredCss)).append("\" />");
        return result.toString();
    }

    /**
     * Return <code>null</code> or the file item for the given field in 
     * case it is of type <code>{@link CmsFileUploadField}</code>.<p>
     * 
     * @param isFileUploadField the field to the the file item of 
     * 
     * @return <code>null</code> or the file item for the given field in 
     *      case it is of type <code>{@link CmsFileUploadField}</code>
     */
    public FileItem getUploadFile(I_CmsField isFileUploadField) {

        FileItem result = null;
        FileItem current;
        String fieldName = isFileUploadField.getName();
        String uploadFileFieldName;
        Map<String, FileItem> fileUploads = (Map<String, FileItem>)getRequest().getSession().getAttribute(
            ATTRIBUTE_FILEITEMS);

        if (fileUploads != null) {
            Iterator<FileItem> i = fileUploads.values().iterator();
            while (i.hasNext()) {
                current = i.next();
                uploadFileFieldName = current.getFieldName();
                if (fieldName.equals(uploadFileFieldName)) {
                    result = current;
                }
            }
        }
        return result;
    }

    /**
     * Returns if the submitted values contain validation errors.<p>
     * 
     * @return <code>true</code> if the submitted values contain validation errors, otherwise <code>false</code>
     */
    public boolean hasValidationErrors() {

        return (!isInitial() && (getErrors().size() > 0));
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        try {
            init(context, req, res, null);
        } catch (CmsException e) {
            // log initialization error
            LOG.error(e);
        }
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws CmsException if initializing the form message objects fails
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res, String formConfigUri)
    throws CmsException {

        super.init(context, req, res);
        try {
            // initialize the form configuration
            configureForm(req, formConfigUri);
            m_initSuccess = true;
        } catch (Exception e) {
            // error in form initialization, initialize at least the localized messages
            initMessages(formConfigUri);
        }

    }

    /**
     * Returns if the form is displayed for the first time.<p>
     * 
     * @return <code>true</code> if the form is displayed for the first time, otherwise <code>false</code>
     */
    public boolean isInitial() {

        return m_initial;
    }

    /**
     * Returns if the form handler configuration has been initialized successfully.<p>
     * 
     * @return <code>true</code> if the form handler configuration has been initialized successfully, otherwise <code>false</code>
     */
    public boolean isInitSuccess() {

        return m_initSuccess;
    }

    /**
     * Prepares the after web form action.<p>
     */
    public void prepareAfterWebformAction() {

        String actionClass = getFormConfiguration().getActionClass();
        if (CmsStringUtil.isNotEmpty(actionClass)) {
            try {
                I_CmsWebformActionHandler handler = getObject(actionClass);
                handler.afterWebformAction(getCmsObject(), this);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Invalid webform action handler class: " + actionClass, e);
                }
            }
        }
    }

    /**
     * Sends the confirmation mail with the form data to the specified email address.<p>
     * 
     * @throws Exception if sending the confirmation mail fails
     */
    public void sendConfirmationMail() throws Exception {

        String mailTo = getFormConfiguration().getConfirmationMailEmail();
        if (CmsStringUtil.isNotEmpty(mailTo)) {
            // create the new confirmation mail message depending on the configured email type
            if (getFormConfiguration().getMailType().equals(CmsForm.MAILTYPE_HTML)) {
                // create a HTML email
                CmsHtmlMail theMail = new CmsHtmlMail();
                theMail.setCharset(getCmsObject().getRequestContext().getEncoding());
                if (CmsStringUtil.isNotEmpty(getFormConfiguration().getConfirmationMailFrom())) {
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getFormConfiguration().getConfirmationMailFromName())) {
                        theMail.setFrom(
                            m_macroResolver.resolveMacros(getFormConfiguration().getConfirmationMailFrom()),
                            m_macroResolver.resolveMacros(getFormConfiguration().getConfirmationMailFromName()));
                    } else {
                        theMail.setFrom(m_macroResolver.resolveMacros(getFormConfiguration().getConfirmationMailFrom()));
                    }
                } else if (CmsStringUtil.isNotEmpty(getFormConfiguration().getMailFrom())) {
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getFormConfiguration().getMailFromName())) {
                        theMail.setFrom(
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()),
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFromName()));
                    } else {
                        theMail.setFrom(m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()));
                    }
                }
                theMail.setTo(createInternetAddresses(mailTo));
                theMail.setSubject(m_macroResolver.resolveMacros(getFormConfiguration().getMailSubjectPrefix()
                    + getFormConfiguration().getConfirmationMailSubject()));
                theMail.setHtmlMsg(createMailTextFromFields(true, true));
                // send the mail
                theMail.send();
            } else {
                // create a plain text email
                CmsSimpleMail theMail = new CmsSimpleMail();
                theMail.setCharset(getCmsObject().getRequestContext().getEncoding());
                if (CmsStringUtil.isNotEmpty(getFormConfiguration().getMailFrom())) {
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getFormConfiguration().getMailFromName())) {
                        theMail.setFrom(
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()),
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFromName()));
                    } else {
                        theMail.setFrom(m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()));
                    }
                }
                theMail.setTo(createInternetAddresses(mailTo));
                theMail.setSubject(m_macroResolver.resolveMacros(getFormConfiguration().getMailSubjectPrefix()
                    + getFormConfiguration().getConfirmationMailSubject()));
                theMail.setMsg(createMailTextFromFields(false, true));
                // send the mail
                theMail.send();
            }
        }
    }

    /**
     * Sends the collected data due to the configuration of the form 
     * (email, database or both).<p>
     * 
     * @return <code>true</code> if successful 
     */
    public boolean sendData() {

        boolean result = true;
        try {
            CmsForm data = getFormConfiguration();
            data.removeCaptchaField();
            // fill the macro resolver for resolving in subject and content: 
            List<I_CmsField> fields = data.getAllFields(false, true, true);
            Iterator<I_CmsField> itFields = fields.iterator();
            // add field values as macros
            while (itFields.hasNext()) {
                I_CmsField field = itFields.next();
                if (field instanceof CmsPagingField) {
                    continue;
                }
                String fValue = field.getValue();
                if ((field instanceof CmsDynamicField)
                    && !((field instanceof CmsDisplayField) || (field instanceof CmsHiddenDisplayField))) {
                    fValue = data.getFieldStringValueByName(field.getName());
                }
                if (field instanceof CmsFileUploadField) {
                    fValue = CmsFormHandler.getTruncatedFileItemName(fValue);
                }
                m_macroResolver.addMacro(field.getLabel(), fValue);
                if (!field.getLabel().equals(field.getDbLabel())) {
                    m_macroResolver.addMacro(field.getDbLabel(), fValue);
                }
                if (field instanceof CmsTableField) {
                    Iterator<CmsFieldItem> it = field.getItems().iterator();
                    while (it.hasNext()) {
                        CmsFieldItem item = it.next();
                        m_macroResolver.addMacro(item.getLabel(), item.getValue());
                        if (!item.getLabel().equals(item.getDbLabel())) {
                            m_macroResolver.addMacro(item.getDbLabel(), item.getValue());
                        }
                    }
                }
            }
            // add current date as macro
            m_macroResolver.addMacro(MACRO_DATE, CmsDateUtil.getDateTime(
                new Date(),
                DateFormat.LONG,
                getRequestContext().getLocale()));
            // send optional confirmation mail
            if (data.isConfirmationMailEnabled()) {
                if (!data.isConfirmationMailOptional()
                    || Boolean.valueOf(getParameter(CmsForm.PARAM_SENDCONFIRMATION)).booleanValue()) {
                    sendConfirmationMail();
                }
            }
            if (data.isTransportDatabase()) {
                // save submitted form to database and store the uploaded files
                m_entryId = sendDatabase();
                if (m_entryId > 0) {
                    // successful submit
                    result &= true;
                } else {
                    // error during submit
                    result &= false;
                }
            }
            if (data.isTransportEmail()) {
                result &= sendMail();
            }

        } catch (Exception e) {
            // an error occurred during mail creation
            if (LOG.isErrorEnabled()) {
                LOG.error("An unexpected error occured.", e);
            }
            getErrors().put("sendmail", e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * Returns if the optional check page should be displayed.<p>
     * 
     * @return <code>true</code> if the optional check page should be displayed, otherwise <code>false</code>
     */
    public boolean showCheck() {

        boolean result = false;

        if (getFormConfiguration().getShowCheck() && ACTION_SUBMIT.equals(getParameter(PARAM_FORMACTION))) {
            result = true;
        } else if (getFormConfiguration().captchaFieldIsOnCheckPage()
            && ACTION_CONFIRMED.equals(getParameter(PARAM_FORMACTION))
            && !validate()) {
            result = true;
        }

        return result;
    }

    /**
     * Returns if the data download page should be displayed.<p>
     * 
     * @return <code>true</code> if the data download page should be displayed, otherwise <code>false</code>
     */
    public boolean showDownloadData() {

        boolean result = false;

        String paramAction = getParameter(CmsFormHandler.PARAM_FORMACTION);
        if (CmsFormHandler.ACTION_DOWNLOAD_DATA_1.equals(paramAction)) {
            result = true;
        } else if (CmsFormHandler.ACTION_DOWNLOAD_DATA_2.equals(paramAction)) {
            result = true;
        }
        return result;
    }

    /**
     * Returns if the expiration text should be shown instead of the form.<p>
     * 
     * @return <code>true</code> if the expiration text should be shown, otherwise <code>false</code>
     */
    public boolean showExpired() {

        return (getFormConfiguration().getExpirationDate() > 0)
            && (System.currentTimeMillis() > getFormConfiguration().getExpirationDate());
    }

    /**
     * Returns if the input form should be displayed.<p>
     * 
     * @return <code>true</code> if the input form should be displayed, otherwise <code>false</code>
     */
    public boolean showForm() {

        boolean result = false;
        String formAction = getParameter(PARAM_FORMACTION);

        if (isInitial()) {
            // initial call
            result = true;
        } else if (ACTION_CORRECT_INPUT.equalsIgnoreCase(formAction)) {
            // user decided to modify his inputs
            result = true;
        } else if (ACTION_SUBMIT.equalsIgnoreCase(formAction) && !validate()) {
            // input field validation failed
            result = true;

            if (getFormConfiguration().hasCaptchaField() && getFormConfiguration().captchaFieldIsOnCheckPage()) {
                // if there is a captcha field and a check page configured, we do have to remove the already
                // initialized captcha field from the form again. the captcha field gets initialized together with
                // the form, in this moment it is not clear yet whether we have validation errors or and need to
                // to go back to the input form...
                getFormConfiguration().removeCaptchaField();
            }
        } else if (ACTION_CONFIRMED.equalsIgnoreCase(formAction)
            && getFormConfiguration().captchaFieldIsOnCheckPage()
            && !validate()) {
            // captcha field validation on check page failed: redisplay the check page, not the input page!
            result = false;
        } else if (ACTION_DOWNLOAD_DATA_1.equalsIgnoreCase(formAction)) {
            result = false;
        } else if (ACTION_DOWNLOAD_DATA_2.equalsIgnoreCase(formAction)) {
            result = false;
        } else if (CmsStringUtil.isNotEmpty(getParameter("back"))) {
            result = true;
        } else if ((CmsStringUtil.isNotEmpty(getParameter("page"))) && CmsStringUtil.isEmpty(getParameter("finalpage"))) {
            result = true;
        }

        return result;
    }

    /**
     * Returns if the template head or other HTML should be included (the page does not serve as a download page).<p>
     * 
     * @return <code>true</code> if the template head or other HTML should be included (the page does not serve as a download page)
     */
    public boolean showTemplate() {

        boolean result = true;

        if (CmsFormHandler.ACTION_DOWNLOAD_DATA_2.equals(getParameter(CmsFormHandler.PARAM_FORMACTION))) {
            result = false;
        }
        return result;
    }

    /**
     * Validation method that checks the given input fields.<p>
     * 
     * All errors are stored in the member m_errors Map, with the input field name as key
     * and the error message String as value.<p>
     * 
     * @return <code>true</code> if all necessary fields can be validated, otherwise <code>false</code>
     */
    public boolean validate() {

        if (m_isValidatedCorrect != null) {
            return m_isValidatedCorrect.booleanValue();
        }

        boolean allOk = true;

        // if the previous button was used, then no validation is necessary here
        if (CmsStringUtil.isNotEmpty(getParameter("back"))) {
            return true;
        }

        // iterate the form fields
        List<I_CmsField> fields = getFormConfiguration().getFields();

        int pagingPos = fields.size();
        if (CmsStringUtil.isNotEmpty(getParameter("page"))) {
            int value = new Integer(getParameter("page")).intValue();
            pagingPos = CmsPagingField.getLastFieldPosFromPage(this, value) + 1;
        }

        // validate each form field
        for (int i = 0, n = pagingPos; i < n; i++) {

            I_CmsField currentField = fields.get(i);

            if (CmsCaptchaField.class.isAssignableFrom(currentField.getClass())) {
                // the captcha field doesn't get validated here...
                continue;
            }

            // call the field validation
            if (!validateField(currentField)) {
                allOk = false;
            }
            if (currentField.hasCurrentSubFields()) {
                // also validate the current sub fields
                Iterator<I_CmsField> k = currentField.getCurrentSubFields().iterator();
                while (k.hasNext()) {
                    // call the sub field validation
                    if (!validateField(k.next())) {
                        allOk = false;
                    }
                }
            }
        }

        CmsCaptchaField captchaField = getFormConfiguration().getCaptchaField();
        if ((captchaField != null) && (pagingPos == fields.size())) {
            // captcha field is enabled and we are on the last page or check page
            boolean captchaFieldIsOnInputPage = getFormConfiguration().captchaFieldIsOnInputPage()
                && getFormConfiguration().isInputFormSubmitted();
            boolean captchaFieldIsOnCheckPage = getFormConfiguration().captchaFieldIsOnCheckPage()
                && getFormConfiguration().isCheckPageSubmitted();

            if (captchaFieldIsOnInputPage || captchaFieldIsOnCheckPage) {
                if (!captchaField.validateCaptchaPhrase(this, captchaField.getValue())) {
                    getErrors().put(captchaField.getName(), ERROR_VALIDATION);
                    allOk = false;
                }
            }
        }

        m_isValidatedCorrect = Boolean.valueOf(allOk);
        return allOk;
    }

    /**
     * Returns the HTML for the optional form check page, generated by using the given string template file.<p>
     * 
     * @return the HTML for the optional form check page, generated by using the given string template file
     */
    protected String buildCheckHtml() {

        // create the check page
        StringTemplate sTemplate = getOutputTemplate("checkpage");
        // set the necessary attributes to use in the string template
        sTemplate.setAttribute("formuri", OpenCms.getLinkManager().substituteLink(
            getCmsObject(),
            getCmsObject().getRequestContext().getUri()));
        sTemplate.setAttribute("formconfig", getFormConfiguration());
        sTemplate.setAttribute("checktext", getFormCheckText());

        CmsCaptchaField captchaField = getFormConfiguration().getCaptchaField();
        sTemplate.setAttribute("captchafield", captchaField);
        if (captchaField != null) {
            // do this only if a captcha field is configured
            String errorMessage = getErrors().get(captchaField.getName());
            if (errorMessage != null) {
                // create the error message for the field
                if (CmsFormHandler.ERROR_MANDATORY.equals(errorMessage)) {
                    errorMessage = getMessages().key("form.error.mandatory");
                } else {
                    errorMessage = getMessages().key("form.error.validation");
                }
            }
            sTemplate.setAttribute("captchaerror", errorMessage);
            sTemplate.setAttribute("captchaimagelink", OpenCms.getLinkManager().substituteLink(
                    getCmsObject(),
                    "/system/modules/com.alkacon.opencms.formgenerator/pages/captcha.jsp?"
                        + captchaField.getCaptchaSettings().toRequestParams(getCmsObject())));
        }

        List<I_CmsField> fields = getFormConfiguration().getAllFields(true, false, false);
        List<I_CmsField> checkFields = new ArrayList<I_CmsField>(fields.size());
        for (int i = 0, n = fields.size(); i < n; i++) {
            I_CmsField current = fields.get(i);
            if ((!CmsDynamicField.class.isAssignableFrom(current.getClass())
                && !CmsHiddenField.class.isAssignableFrom(current.getClass())
                && !CmsCaptchaField.class.isAssignableFrom(current.getClass()) && !CmsHiddenDisplayField.class.isAssignableFrom(current.getClass()))
                || (CmsDisplayField.class.isAssignableFrom(current.getClass()))) {
                if ((current instanceof CmsEmptyField) || (current instanceof CmsPagingField)) {
                    continue;
                }
                String label = current.getLabel();
                if (current instanceof CmsTableField) {
                    label = ((CmsTableField)current).buildLabel(this);
                }
                String value = current.toString();
                if (current instanceof CmsTableField) {
                    value = ((CmsTableField)current).buildRows(this);
                } else if (current instanceof CmsPasswordField) {
                    value = value.replaceAll(".", "*");
                } else if (current instanceof CmsFileUploadField) {
                    value = CmsFormHandler.getTruncatedFileItemName(value);
                    value = convertToHtmlValue(value);
                } else {
                    value = convertToHtmlValue(value);
                }
                I_CmsField checkField = new CmsTextField();
                checkField.setLabel(label);
                checkField.setValue(value);
                checkFields.add(checkField);
            }
        }
        sTemplate.setAttribute("checkfields", checkFields);
        sTemplate.setAttribute("hiddenfields", createHiddenFields());
        sTemplate.setAttribute("checkbutton", getMessages().key("form.button.checked"));
        sTemplate.setAttribute("correctbutton", getMessages().key("form.button.correct"));
        return sTemplate.toString();
    }

    /**
     * Returns the HTML for the form confirmation page, generated by using the given string template file.<p>
     * 
     * @return the HTML for the form confirmation page, generated by using the given string template file
     */
    protected String buildConfirmHtml() {

        // create the confirmation page
        StringTemplate sTemplate = getOutputTemplate("confirmationpage");
        // set the necessary attributes to use in the string template
        sTemplate.setAttribute("formconfig", getFormConfiguration());
        sTemplate.setAttribute("confirmtext", getFormConfirmationText());
        // generate the list of fields to display
        List<I_CmsField> fields = getFormConfiguration().getAllFields(true, false, true);
        List<I_CmsField> confirmFields = new ArrayList<I_CmsField>(fields.size());

        for (int i = 0, n = fields.size(); i < n; i++) {
            I_CmsField current = fields.get(i);
            if (CmsHiddenField.class.isAssignableFrom(current.getClass())
                || CmsPrivacyField.class.isAssignableFrom(current.getClass())
                || CmsCaptchaField.class.isAssignableFrom(current.getClass())
                || CmsPagingField.class.isAssignableFrom(current.getClass())) {
                continue;
            }
            String label = current.getLabel();
            if (current instanceof CmsTableField) {
                label = ((CmsTableField)current).buildLabel(this);
            } else if (current instanceof CmsEmptyField) {
                label = "";
            }
            String value = current.toString();
            if ((current instanceof CmsDisplayField)) {
                value = convertToHtmlValue(value);
            } else if ((current instanceof CmsHiddenDisplayField)) {
                continue;
            } else if ((current instanceof CmsDynamicField)) {
                if (!current.isMandatory()) {
                    // show dynamic fields only if they are marked as mandatory
                    continue;
                }
                // compute the value for the dynamic field
                value = getFormConfiguration().getFieldStringValueByName(current.getName());
                value = convertToHtmlValue(value);
            } else if (current instanceof CmsTableField) {
                value = ((CmsTableField)current).buildRows(this);
            } else if (current instanceof CmsPasswordField) {
                value = value.replaceAll(".", "*");
            } else if (current instanceof CmsFileUploadField) {
                value = CmsFormHandler.getTruncatedFileItemName(value);
                value = convertToHtmlValue(value);
            } else if (current instanceof CmsEmptyField) {
                // do nothing
            } else {
                value = convertToHtmlValue(value);
            }
            I_CmsField confirmField = new CmsTextField();
            confirmField.setLabel(label);
            confirmField.setValue(value);
            confirmFields.add(confirmField);
        }
        sTemplate.setAttribute("confirmfields", confirmFields);
        return sTemplate.toString();
    }

    /**
     * Returns the HTML for the input form, generated by using the given string template file.<p>
     * 
     * @return the HTML for the input form, generated by using the given string template file
     */
    protected String buildFormHtml() {

        // determine if form type has to be set to "multipart/form-data" in case of upload fields
        String encType = null;
        for (Iterator<I_CmsField> i = getFormConfiguration().getFields().iterator(); i.hasNext();) {
            I_CmsField field = i.next();
            if (field.getType().equals(CmsFileUploadField.getStaticType())) {
                encType = " enctype=\"multipart/form-data\"";
                break;
            }
        }

        // determine error message
        String errorMessage = null;
        if (hasValidationErrors()) {
            errorMessage = getMessages().key("form.error.message");
        }

        // determine mandatory message
        String mandatoryMessage = null;
        if (getFormConfiguration().isShowMandatory() && getFormConfiguration().hasMandatoryFields()) {
            mandatoryMessage = getMessages().key("form.message.mandatory");
        }

        // calculate fields to show (e.g. if paging is activated)
        boolean paging = false;
        int pos = 0;
        int fieldNr = 0;
        int place = 0;
        int currPage = 1;
        int pagingPos = 0;
        if (getParameterMap().containsKey(PARAM_BACK) && getParameterMap().containsKey(PARAM_PAGE)) {
            String[] pagingString = getParameterMap().get(PARAM_PAGE);
            currPage = new Integer(pagingString[0]).intValue();
            currPage = CmsPagingField.getPreviousPage(currPage);
        } else if (getParameterMap().containsKey("page") && !hasValidationErrors()) {
            String[] pagingString = getParameterMap().get(PARAM_PAGE);
            currPage = new Integer(pagingString[0]).intValue();
            currPage = CmsPagingField.getNextPage(currPage);
        } else if (getParameterMap().containsKey(PARAM_PAGE) && hasValidationErrors()) {
            String[] pagingString = getParameterMap().get(PARAM_PAGE);
            currPage = new Integer(pagingString[0]).intValue();
        }
        pagingPos = CmsPagingField.getFirstFieldPosFromPage(this, currPage);
        fieldNr = pagingPos;

        // generate HTML for the input fields
        StringBuffer fieldHtml = new StringBuffer(getFormConfiguration().getFields().size() * 256);
        StringBuffer subFieldJSBuf = new StringBuffer(512);
        for (int i = pagingPos, n = getFormConfiguration().getFields().size(); i < n; i++) {
            fieldNr += 1;
            // loop through all form input fields 
            I_CmsField field = getFormConfiguration().getFields().get(i);

            if (i == (n - 1)) {
                // the last one has to close the row
                place = 1;
            }
            field.setPlaceholder(place);
            field.setPosition(pos);
            field.setFieldNr(fieldNr);
            String infoMessage = getInfos().get(field.getName());
            // validate the file upload field here already because of the lost values in these fields
            if (field instanceof CmsFileUploadField) {
                infoMessage = field.validateForInfo(this);
            }
            fieldHtml.append(field.buildHtml(
                this,
                getMessages(),
                getErrors().get(field.getName()),
                getFormConfiguration().isShowMandatory(),
                infoMessage));
            subFieldJSBuf.append(field.getSubFieldScript());
            pos = field.getPosition();
            place = field.getPlaceholder();
            // if there is a paging field do not show the following fields
            if (field instanceof CmsPagingField) {
                paging = true;
                break;
            }
        }

        // determine if subfield JavaScript has to be added
        String subFieldJS = null;
        if (subFieldJSBuf.length() > 0) {
            subFieldJS = subFieldJSBuf.toString();
        }

        // determine if the submit and other buttons are shown
        String submitButton = null;
        String resetButton = null;
        String hiddenFields = null;
        String prevButton = null;

        if (!paging) {
            // show submit button on last form page
            submitButton = getMessages().key("form.button.submit");
            if (getParameterMap().containsKey(PARAM_PAGE)) {
                // add hidden fields and previous button on last form page
                StringBuffer hFieldsBuf = CmsPagingField.appendHiddenFields(
                    this,
                    getMessages(),
                    getFormConfiguration().getFields().size());
                hFieldsBuf.append("<input type=\"hidden\" name=\"page\" value=\"").append(currPage).append("\" />");
                hFieldsBuf.append("<input type=\"hidden\" name=\"finalpage\" value=\"true\" />");
                hiddenFields = hFieldsBuf.toString();
                prevButton = getMessages().key("form.button.prev");
            } else {
                // there is no paging, but do the file upload values in hidden values
                // otherwise the contents are forgotten in case of other wrong filled fields
                StringBuffer hFieldsBuf = CmsPagingField.appendHiddenUploadFields(this, getMessages());
                hiddenFields = hFieldsBuf.toString();
            }
            if (getFormConfiguration().isShowReset()) {
                // show reset button if configured
                resetButton = getMessages().key("form.button.reset");

            }
        }

        // determine if the download button has to be shown
        String downloadButton = null;
        if (!getRequestContext().currentProject().isOnlineProject() && getFormConfiguration().isTransportDatabase()) {
            downloadButton = getMessages().key("form.button.downloaddata");
        }

        // create the main form and pass the previously generated field HTML as attribute
        StringTemplate sTemplate = getOutputTemplate("form");
        // set the necessary attributes to use in the string template
        sTemplate.setAttribute("formuri", OpenCms.getLinkManager().substituteLink(
            getCmsObject(),
            getCmsObject().getRequestContext().getUri()));
        sTemplate.setAttribute("enctype", encType);
        sTemplate.setAttribute("errormessage", errorMessage);
        sTemplate.setAttribute("mandatorymessage", mandatoryMessage);
        sTemplate.setAttribute("formconfig", getFormConfiguration());
        sTemplate.setAttribute("fields", fieldHtml.toString());
        sTemplate.setAttribute("subfieldjs", subFieldJS);
        sTemplate.setAttribute("downloadbutton", downloadButton);
        sTemplate.setAttribute("submitbutton", submitButton);
        sTemplate.setAttribute("resetbutton", resetButton);
        sTemplate.setAttribute("hiddenfields", hiddenFields);
        sTemplate.setAttribute("prevbutton", prevButton);
        return sTemplate.toString();
    }

    /**
     * Returns the HTML for errors in the used string template group file.<p>
     * 
     * <b>Note</b>: Only generates output in an offline project
     * 
     * @return the HTML for errors in the used string template group file
     */
    protected String buildTemplateGroupCheckHtml() {

        String result = "";
        if (!getRequestContext().currentProject().isOnlineProject()) {
            // check template group errors
            StringTemplateErrorListener el = getOutputTemplateGroup().getErrorListener();
            if ((el != null) && CmsStringUtil.isNotEmptyOrWhitespaceOnly(el.toString())) {
                // errors(s) found, show error template
                try {
                    CmsFile stFile = getCmsObject().readFile(CmsForm.VFS_PATH_ERROR_TEMPLATEFILE);
                    String stContent = new String(
                        stFile.getContents(),
                        getCmsObject().getRequestContext().getEncoding());
                    StringTemplate st = new StringTemplate(stContent, DefaultTemplateLexer.class);
                    // set the error attributes of the template
                    st.setAttribute("errorheadline", "Error parsing output template group");
                    st.setAttribute("errortext", "Error output:<br/><pre>" + el.toString() + "</pre>");
                    st.setAttribute("errortemplatenames", getOutputTemplateGroup().getTemplateNames());
                    result = st.toString();
                } catch (Exception e) {
                    // something went wrong, log error
                    LOG.error("Error while getting error output template from file \""
                        + CmsForm.VFS_PATH_ERROR_TEMPLATEFILE
                        + "\".");
                }
            }
        }
        return result;
    }

    /**
     * Parses the form configuration and creates the necessary configuration objects.<p>
     * 
     * @param req the JSP request 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws Exception if creating the form configuration objects fails
     */
    protected void configureForm(HttpServletRequest req, String formConfigUri) throws Exception {

        // read the form configuration file from VFS
        if (CmsStringUtil.isEmpty(formConfigUri)) {
            formConfigUri = getRequestContext().getUri();
        }
        m_multipartFileItems = CmsRequestUtil.readMultipartFileItems(req);
        m_macroResolver = CmsMacroResolver.newInstance();
        m_macroResolver.setKeepEmptyMacros(true);
        m_macroResolver.setCmsObject(getCmsObject());
        m_macroResolver.addMacro(MACRO_URL, OpenCms.getSiteManager().getCurrentSite(getCmsObject()).getServerPrefix(
                getCmsObject(),
                getRequestContext().getUri())
                + link(getRequestContext().getUri()));
        m_macroResolver.addMacro(MACRO_LOCALE, getRequestContext().getLocale().toString());

        if (m_multipartFileItems != null) {
            m_parameterMap = CmsRequestUtil.readParameterMapFromMultiPart(
                getRequestContext().getEncoding(),
                m_multipartFileItems);
        } else {
            m_parameterMap = new HashMap<String, String[]>();
            m_parameterMap.putAll(getRequest().getParameterMap());
        }

        if (m_multipartFileItems != null) {
            Map<String, FileItem> fileUploads = (Map<String, FileItem>)req.getSession().getAttribute(
                ATTRIBUTE_FILEITEMS);
            if (fileUploads == null) {
                fileUploads = new HashMap<String, FileItem>();
            }
            // check, if there are any attachments
            Iterator<FileItem> i = m_multipartFileItems.iterator();
            while (i.hasNext()) {
                FileItem fileItem = i.next();
                if (CmsStringUtil.isNotEmpty(fileItem.getName())) {
                    // append file upload to the map of file items
                    fileUploads.put(fileItem.getFieldName(), fileItem);
                    m_parameterMap.put(fileItem.getFieldName(), new String[] {fileItem.getName()});
                }
            }
            req.getSession().setAttribute(ATTRIBUTE_FILEITEMS, fileUploads);
        } else {
            req.getSession().removeAttribute(ATTRIBUTE_FILEITEMS);
        }
        String formAction = getParameter(PARAM_FORMACTION);
        // security check: some form actions are not allowed for everyone:
        if (this.getCmsObject().getRequestContext().currentProject().isOnlineProject()) {
            if (CmsFormHandler.ACTION_DOWNLOAD_DATA_1.equals(formAction)) {
                LOG.error("Received an illegal request for download data of form "
                    + formConfigUri
                    + " (online request)");
                formAction = ACTION_SUBMIT;
            }
        }
        m_isValidatedCorrect = null;
        setInitial(CmsStringUtil.isEmpty(formAction));

        // get the localized messages
        initMessages(formConfigUri);

        // get the form configuration
        setFormConfiguration(new CmsForm(this, getMessages(), isInitial(), formConfigUri, formAction));
    }

    /**
     * Creates a list of Internet addresses (email) from a semicolon separated String.<p>
     * 
     * @param mailAddresses a semicolon separated String with email addresses
     * @return list of Internet addresses (email)
     * @throws AddressException if an email address is not correct
     */
    protected List<InternetAddress> createInternetAddresses(String mailAddresses) throws AddressException {

        if (CmsStringUtil.isNotEmpty(mailAddresses)) {
            // at least one email address is present, generate list
            StringTokenizer T = new StringTokenizer(mailAddresses, ";");
            List<InternetAddress> addresses = new ArrayList<InternetAddress>(T.countTokens());
            while (T.hasMoreTokens()) {
                InternetAddress address = new InternetAddress(T.nextToken());
                addresses.add(address);
            }
            return addresses;
        } else {
            // no address given, return empty list
            return Collections.emptyList();
        }
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
     * Initializes the localized messages for the web form.<p>
     * 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws CmsException if accessing the VFS fails
     */
    protected void initMessages(String formConfigUri) throws CmsException {

        // get the localized messages
        CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
        String para = module.getParameter("message", "/com/alkacon/opencms/formgenerator/workplace");

        // get the site message
        String siteroot = getCmsObject().getRequestContext().getSiteRoot();
        if (siteroot.startsWith(CmsResource.VFS_FOLDER_SITES)) {
            siteroot = siteroot.substring(CmsResource.VFS_FOLDER_SITES.length() + 1);
        }
        if (!CmsStringUtil.isEmptyOrWhitespaceOnly(siteroot)) {
            String fileSite = module.getParameter("message_" + siteroot);
            if (!CmsStringUtil.isEmptyOrWhitespaceOnly(fileSite)) {
                para = fileSite;
            }
        }
        // use the optional property file if configured
        String propertyFile = null;
        if (CmsStringUtil.isEmpty(formConfigUri)) {
            formConfigUri = getRequestContext().getUri();
        }
        CmsProperty cmsProperty = getCmsObject().readPropertyObject(formConfigUri, "webform.propertyfile", false);
        if (cmsProperty != null) {
            propertyFile = cmsProperty.getValue();
        }
        if (CmsStringUtil.isNotEmpty(propertyFile)) {
            addMessages(new CmsMessages(propertyFile, getRequestContext().getLocale()));
        } else {
            addMessages(new CmsMessages(para, getRequestContext().getLocale()));
        }
    }

    /**
     * Stores the given form data in the database.<p>
     * 
     * @return the entry id of the submitted form in the database or '-1' if something goes wrong 
     * 
     * @throws Exception if something goes wrong
     */
    protected int sendDatabase() throws Exception {

        return CmsFormDataAccess.getInstance().writeFormData(this);
    }

    /**
     * Sends the mail with the form data to the specified recipients.<p>
     * 
     * If configured, sends also a confirmation mail to the form submitter.<p>
     * 
     * @return true if the mail has been successfully sent, otherwise false
     */
    protected boolean sendMail() {

        try {
            // create the new mail message depending on the configured email type
            if (getFormConfiguration().getMailType().equals(CmsForm.MAILTYPE_HTML)) {
                // create a HTML email
                CmsHtmlMail theMail = new CmsHtmlMail();
                theMail.setCharset(getCmsObject().getRequestContext().getEncoding());
                if (CmsStringUtil.isNotEmpty(getFormConfiguration().getMailFrom())) {
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getFormConfiguration().getMailFromName())) {
                        theMail.setFrom(
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()),
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFromName()));
                    } else {
                        theMail.setFrom(m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()));
                    }
                }
                theMail.setTo(createInternetAddresses(getFormConfiguration().getMailTo()));
                List<InternetAddress> ccRec = createInternetAddresses(m_macroResolver.resolveMacros(getFormConfiguration().getMailCC()));
                if (ccRec.size() > 0) {
                    theMail.setCc(ccRec);
                }
                List<InternetAddress> bccRec = createInternetAddresses(m_macroResolver.resolveMacros(getFormConfiguration().getMailBCC()));
                if (bccRec.size() > 0) {
                    theMail.setBcc(bccRec);
                }
                theMail.setSubject(m_macroResolver.resolveMacros(getFormConfiguration().getMailSubjectPrefix()
                    + getFormConfiguration().getMailSubject()));
                theMail.setHtmlMsg(createMailTextFromFields(true, false));

                // attach file uploads
                Map<String, FileItem> fileUploads = (Map<String, FileItem>)getRequest().getSession().getAttribute(
                    ATTRIBUTE_FILEITEMS);
                if (fileUploads != null) {
                    Iterator<FileItem> i = fileUploads.values().iterator();
                    while (i.hasNext()) {
                        FileItem attachment = i.next();
                        if (attachment != null) {
                            String filename = attachment.getName().substring(
                                attachment.getName().lastIndexOf(File.separator) + 1);
                            theMail.attach(
                                new CmsByteArrayDataSource(
                                    filename,
                                    attachment.get(),
                                    OpenCms.getResourceManager().getMimeType(filename, null, "application/octet-stream")),
                                filename,
                                filename);
                        }
                    }
                }
                // send the mail
                theMail.send();
            } else {
                // create a plain text email
                CmsSimpleMail theMail = new CmsSimpleMail();
                theMail.setCharset(getCmsObject().getRequestContext().getEncoding());
                if (CmsStringUtil.isNotEmpty(getFormConfiguration().getMailFrom())) {
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getFormConfiguration().getMailFromName())) {
                        theMail.setFrom(
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()),
                            m_macroResolver.resolveMacros(getFormConfiguration().getMailFromName()));
                    } else {
                        theMail.setFrom(m_macroResolver.resolveMacros(getFormConfiguration().getMailFrom()));
                    }
                }
                theMail.setTo(createInternetAddresses(getFormConfiguration().getMailTo()));
                List<InternetAddress> ccRec = createInternetAddresses(m_macroResolver.resolveMacros(getFormConfiguration().getMailCC()));
                if (ccRec.size() > 0) {
                    theMail.setCc(ccRec);
                }
                List<InternetAddress> bccRec = createInternetAddresses(m_macroResolver.resolveMacros(getFormConfiguration().getMailBCC()));
                if (bccRec.size() > 0) {
                    theMail.setBcc(bccRec);
                }
                theMail.setSubject(m_macroResolver.resolveMacros(getFormConfiguration().getMailSubjectPrefix()
                    + getFormConfiguration().getMailSubject()));
                theMail.setMsg(createMailTextFromFields(false, false));
                // send the mail
                theMail.send();
            }
        } catch (Exception e) {
            // an error occured during mail creation
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            getErrors().put("sendmail", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Sets the form configuration.<p>
     * 
     * @param configuration the form configuration
     */
    protected void setFormConfiguration(CmsForm configuration) {

        m_formConfiguration = configuration;
    }

    /**
     * Sets if the form is displayed for the first time.<p>
     * 
     * @param initial true if the form is displayed for the first time, otherwise false
     */
    protected void setInitial(boolean initial) {

        m_initial = initial;
    }

    /**
    * Sets the localized messages.<p>
    *
    * @param messages the localized messages
    */
    protected void setMessages(CmsMessages messages) {

        addMessages(messages);
    }

    /**
     * Checks if the given field should be used in form data macros.<p>
     * 
     * @param field the field to check
     * 
     * @return if the given field should be used in form data macros
     */
    protected boolean useInFormDataMacro(I_CmsField field) {

        // don't show the letter of agreement (CmsPrivacyField) and captcha field value
        return !((field instanceof CmsPrivacyField) || (field instanceof CmsCaptchaField));
    }

    /**
     * Creates an object from data type I_CmsWebformActionHandler.<p>
     * 
     * @param className name from class to create an object from
     * 
     * @return object from data type I_CmsWebformActionHandler
     */
    private I_CmsWebformActionHandler getObject(String className) throws Exception {

        I_CmsWebformActionHandler object = null;
        Class<I_CmsWebformActionHandler> c = (Class<I_CmsWebformActionHandler>)Class.forName(className);
        object = c.newInstance();

        return object;
    }

    /**
     * Validates a single form field.<p>
     * 
     * @param field the field to validate
     * 
     * @return <code>true</code> if the field is validated, otherwise <code>false</code>
     */
    private boolean validateField(I_CmsField field) {

        if (field == null) {
            return true;
        }

        if (CmsCaptchaField.class.isAssignableFrom(field.getClass())) {
            // the captcha field doesn't get validated here...
            return true;
        }

        // check if a file upload field is empty, but it was filled out already
        String validationInfo = "";
        validationInfo = field.validateForInfo(this);
        if (CmsStringUtil.isNotEmpty(validationInfo)) {
            getInfos().put(field.getName(), validationInfo);
        }
        // check for validation errors
        String validationError = field.validate(this);
        if (CmsStringUtil.isNotEmpty(validationError)) {
            getErrors().put(field.getName(), validationError);
            return false;
        }
        return true;
    }
}
