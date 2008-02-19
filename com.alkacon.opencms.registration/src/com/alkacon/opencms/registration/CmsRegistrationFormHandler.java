/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.registration/src/com/alkacon/opencms/registration/CmsRegistrationFormHandler.java,v $
 * Date   : $Date: 2008/02/19 13:22:30 $
 * Version: $Revision: 1.1 $
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

import com.alkacon.opencms.formgenerator.CmsDynamicField;
import com.alkacon.opencms.formgenerator.CmsEmailField;
import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.CmsFormHandler;
import com.alkacon.opencms.formgenerator.I_CmsField;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsDateUtil;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

/**
 * The form handler controls the html or mail output of a configured email form.<p>
 * 
 * Provides methods to determine the action that takes place and methods to create different
 * output formats of a submitted form.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4 
 */
public class CmsRegistrationFormHandler extends CmsFormHandler {

    /** Macro name for the activation uri macro that can be used in mail text fields. */
    public static final String MACRO_ACTURI = "acturi";

    /** Parameter name for the activation code. */
    public static final String PARAM_ACTCODE = "ac";

    /** Field name for email address. */
    private static final String FIELD_EMAIL = "email";

    /** Field name for login. */
    private static final String FIELD_LOGIN = "login";

    /** Field name for password. */
    private static final String FIELD_PASSWORD = "password";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsRegistrationFormHandler.class);

    /** The module name. */
    private static final String MODULE = "com.alkacon.opencms.registration";

    /** Reflection method prefix constant. */
    private static final String REFLECTION_GETTER_PREFIX = "get";

    /** Reflection method prefix constant. */
    private static final String REFLECTION_SETTER_PREFIX = "set";

    /**
     * Constructor, creates the necessary form configuration objects.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * 
     * @throws Exception if creating the form configuration objects fails
     */
    public CmsRegistrationFormHandler(PageContext context, HttpServletRequest req, HttpServletResponse res)
    throws Exception {

        super(context, req, res);
    }

    /**
     * Constructor, creates the necessary form configuration objects using a given configuration file URI.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws Exception if creating the form configuration objects fails
     */
    public CmsRegistrationFormHandler(
        PageContext context,
        HttpServletRequest req,
        HttpServletResponse res,
        String formConfigUri)
    throws Exception {

        super(context, req, res, formConfigUri);
    }

    /**
     * Returns the user name from the activation code.<p>
     * 
     * @param code the activation code
     * 
     * @return the user name
     */
    public static String getUserName(String code) {

        String reverse = "";
        for (int i = 0; i < code.length(); i++) {
            reverse = (code.charAt(i) + reverse);
        }
        return new String(Base64.decodeBase64(reverse.getBytes()));
    }

    /**
     * As test case.<p>
     * 
     * @param args not used
     */
    public static void main(String[] args) {

        CmsUser user = new CmsUser(null, "/mylongouname/m.moossen@alkacon.com", "", "", "", "", 0, 0, 0, null);
        String code = getActivationCode(user);
        System.out.println(code);
        System.out.println(getUserName(code));
    }

    /**
     * Returns the activation code for the given user.<p>
     * 
     * @param user the user to generate an activation code for
     * 
     * @return the activation code
     */
    private static String getActivationCode(CmsUser user) {

        String code = new String(Base64.encodeBase64(user.getName().getBytes()));
        String reverse = "";
        for (int i = 0; i < code.length(); i++) {
            reverse = (code.charAt(i) + reverse);
        }
        return reverse;
    }

    /**
     * Activates the user identified by the current activation code code.<p>
     * 
     * @throws CmsException if something goes wrong
     */
    public void activateUser() throws CmsException {

        CmsUser user = getUser();
        user.setEnabled(true);
        CmsRegistrationModuleAction.getAdminCms().writeUser(user);
    }

    /**
     * Checks if the current activation code identifies an existing user.<p>
     * 
     * @return if the user is exists
     */
    public boolean existUser() {

        return (getUser() != null);
    }

    /**
     * Fills the fields with the data of the current user.<p>
     */
    public void fillFields() {

        if (getRequestContext().currentUser().isGuestUser()) {
            // ignore if guest user
            return;
        }
        Iterator fields = getRegFormConfiguration().getFields().iterator();
        while (fields.hasNext()) {
            I_CmsField field = (I_CmsField)fields.next();
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(field.getDbLabel())) {
                continue;
            }
            String value = getUserValue(getRequestContext().currentUser(), field.getDbLabel());
            field.setValue(value);
        }
    }

    /**
     * Returns the text to be show in the profile page.<p>
     * 
     * @return the text to be show in the profile page
     */
    public String getEditFormText() {

        CmsMacroResolver macroResolver = getUserMacroResolver();
        return macroResolver.resolveMacros(getRegFormConfiguration().getFormText());
    }

    /**
     * Returns the text to be show when the user activates his account.<p>
     * 
     * @return the text to be show when the user activates his account
     */
    public String getFormActivatedText() {

        CmsMacroResolver macroResolver = getUserMacroResolver();
        return macroResolver.resolveMacros(getRegFormConfiguration().getFormActivationText());
    }

    /**
     * Returns the form configuration.<p>
     * 
     * @return the form configuration
     */
    public CmsRegistrationForm getRegFormConfiguration() {

        return (CmsRegistrationForm)super.getFormConfiguration();
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * @param req the JSP request 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * @throws Exception if creating the form configuration objects fails
     */
    public void init(HttpServletRequest req, String formConfigUri) throws Exception {

        m_mulipartFileItems = CmsRequestUtil.readMultipartFileItems(req);
        m_macroResolver = CmsMacroResolver.newInstance();
        m_macroResolver.setKeepEmptyMacros(true);

        if (m_mulipartFileItems != null) {
            m_parameterMap = CmsRequestUtil.readParameterMapFromMultiPart(
                getRequestContext().getEncoding(),
                m_mulipartFileItems);
        } else {
            m_parameterMap = new HashMap();
            m_parameterMap.putAll(getRequest().getParameterMap());
        }

        if (m_mulipartFileItems != null) {
            Map fileUploads = (Map)req.getSession().getAttribute(ATTRIBUTE_FILEITEMS);
            if (fileUploads == null) {
                fileUploads = new HashMap();
            }
            // check, if there are any attachments
            Iterator i = m_mulipartFileItems.iterator();
            while (i.hasNext()) {
                FileItem fileItem = (FileItem)i.next();
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
        m_isValidatedCorrect = null;
        setInitial(CmsStringUtil.isEmpty(formAction));
        // get the localized messages
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE);
        String para = module.getParameter("message", "/com/alkacon/opencms/registration/workplace");

        setMessages(new CmsMessages(para, getRequestContext().getLocale()));
        // get the form configuration
        setFormConfiguration(new CmsRegistrationForm(this, getMessages(), isInitial(), formConfigUri, formAction));
    }

    /**
     * Checks if the current activation code identifies an existing user and if it is already activated.<p>
     * 
     * @return if the user is activated
     */
    public boolean isUserActivated() {

        CmsUser user = getUser();
        if (user == null) {
            return false;
        }
        return user.isEnabled();
    }

    /**
     * Sends the collected data due to the configuration of the form 
     * (email, database or both).<p>
     * 
     * @return true if successful 
     */
    public boolean sendData() {

        boolean result = true;
        try {
            CmsRegistrationForm data = getRegFormConfiguration();
            data.removeCaptchaField();
            // fill the macro resolver for resolving in subject and content: 
            List fields = data.getAllFields();
            Iterator itFields = fields.iterator();
            // add field values as macros
            while (itFields.hasNext()) {
                I_CmsField field = (I_CmsField)itFields.next();
                String fValue = field.getValue();
                if (field instanceof CmsDynamicField) {
                    fValue = data.getFieldStringValueByName(field.getName());
                }
                m_macroResolver.addMacro(field.getLabel(), fValue);
                if (field instanceof CmsEmailField) {
                    if (data.isEmailAsLogin()) {
                        m_macroResolver.addMacro(FIELD_LOGIN, fValue);
                    }
                }
                if (!field.getLabel().equals(field.getDbLabel())) {
                    m_macroResolver.addMacro(field.getDbLabel(), fValue);
                }
            }
            // add current date as macro
            m_macroResolver.addMacro(MACRO_DATE, CmsDateUtil.getDateTime(
                new Date(),
                DateFormat.LONG,
                getRequestContext().getLocale()));
            if (getRequestContext().currentUser().isGuestUser()) {
                // create the user here
                createUser();
            } else {
                editUser();
            }
            // send optional confirmation mail
            if (data.isConfirmationMailEnabled()) {
                if (!data.isConfirmationMailOptional()
                    || Boolean.valueOf(getParameter(CmsForm.PARAM_SENDCONFIRMATION)).booleanValue()) {
                    sendConfirmationMail();
                }
            }
            if (data.isTransportEmail()) {
                result = sendMail();
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
     * @see com.alkacon.opencms.formgenerator.CmsFormHandler#useInFormDataMacro(com.alkacon.opencms.formgenerator.I_CmsField)
     */
    protected boolean useInFormDataMacro(I_CmsField field) {

        boolean ret = super.useInFormDataMacro(field);
        ret &= !(field instanceof CmsPasswordField);
        return ret;
    }

    /**
     * Creates the user.<p>
     * 
     * @throws CmsException if something goes wrong
     */
    private void createUser() throws CmsException {

        // first create the user with the basics
        CmsRegistrationForm form = getRegFormConfiguration();
        CmsObject cms = CmsRegistrationModuleAction.getAdminCms();
        String email = form.getFieldByDbLabel(FIELD_EMAIL).getValue();
        String login = form.getOrgUnit();
        if (form.isEmailAsLogin()) {
            login += email;
        } else {
            login += form.getFieldByDbLabel(FIELD_LOGIN).getValue();
        }
        String password = form.getFieldByDbLabel(FIELD_PASSWORD).getValue();
        // default description
        String description = Messages.get().getBundle(getCmsObject().getRequestContext().getLocale()).key(
            Messages.GUI_USER_DESCRIPTION_0);
        CmsUser user = cms.createUser(login, password, description, null);
        user.setEmail(email);
        user.setEnabled(false);
        cms.writeUser(user);
        // add activation uri macro
        String link = OpenCms.getSiteManager().getCurrentSite(getCmsObject()).getServerPrefix(
            cms,
            getRequestContext().getUri())
            + link(CmsRequestUtil.appendParameter(
                getRequestContext().getUri(),
                PARAM_ACTCODE,
                CmsEncoder.encode(getActivationCode(user))));
        m_macroResolver.addMacro(MACRO_ACTURI, "<a href=\"" + link + "\">" + link + "</a>");
        // now add additional information
        // iterate all fields except email, login and password
        List excludes = Arrays.asList(new String[] {FIELD_EMAIL, FIELD_PASSWORD, FIELD_LOGIN});
        Iterator it = form.getAllFields().iterator();
        while (it.hasNext()) {
            I_CmsField field = (I_CmsField)it.next();
            if (excludes.contains(field.getDbLabel())) {
                continue;
            }
            setUserValue(user, field);
        }
        cms.writeUser(user);
        // now assign the user to the given group only if needed
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(form.getGroup())) {
            cms.addUserToGroup(user.getName(), form.getGroup());
        }
    }

    /**
     * Sets all fields to the current user.<p>
     * 
     * @throws CmsException if something goes wrong
     */
    private void editUser() throws CmsException {

        CmsObject cms = CmsRegistrationModuleAction.getAdminCms();
        CmsUser user = getRequestContext().currentUser();
        CmsRegistrationForm form = getRegFormConfiguration();
        Iterator it = form.getAllFields().iterator();
        while (it.hasNext()) {
            I_CmsField field = (I_CmsField)it.next();
            setUserValue(user, field);
            if (field.getDbLabel().equals(FIELD_PASSWORD)) {
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(field.getValue())) {
                    cms.setPassword(user.getName(), field.getValue());
                }
            }
        }
        cms.writeUser(user);
    }

    /**
     * Checks if the current activation code identifies an existing user.<p>
     * 
     * @return if the user is exists
     */
    private CmsUser getUser() {

        String code = getJspContext().getRequest().getParameter(PARAM_ACTCODE);
        if (code == null) {
            return getRequestContext().currentUser();
        }
        String userName = getUserName(code);
        try {
            return getCmsObject().readUser(userName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the macro resolver for the activation and profile pages.<p>
     * 
     * @return the macro resolver for the activation and profile pages
     */
    private CmsMacroResolver getUserMacroResolver() {

        // TODO: create macros for properties and addinfo keys instead of fields!
        CmsUser user = getUser();
        CmsMacroResolver macroResolver = CmsMacroResolver.newInstance();
        macroResolver.setKeepEmptyMacros(true);
        List fields = getRegFormConfiguration().getFields();
        Iterator itFields = fields.iterator();
        // add field values as macros
        while (itFields.hasNext()) {
            I_CmsField field = (I_CmsField)itFields.next();
            String label = field.getDbLabel();
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(label)) {
                continue;
            }
            if (label.equals(FIELD_LOGIN)) {
                String value = user.getName();
                macroResolver.addMacro(label, value);
            }
            String value = getUserValue(user, label);
            macroResolver.addMacro(label, value);
            if (!field.getLabel().equals(label)) {
                macroResolver.addMacro(field.getLabel(), value);
            }
        }
        return macroResolver;
    }

    /**
     * Returns the value of the given field for the given user.<p>
     * 
     * @param user the user
     * @param label the field
     * 
     * @return the value of the given field for the given user
     */
    private String getUserValue(CmsUser user, String label) {

        String methodName = REFLECTION_GETTER_PREFIX;
        methodName += ("" + label.charAt(0)).toUpperCase();
        if (label.length() > 1) {
            methodName += label.substring(1);
        }
        Object value = null;
        try {
            // try to access the method
            Method method = CmsUser.class.getMethod(methodName, new Class[] {});
            value = method.invoke(user, new Object[] {});
        } catch (Exception e) {
            // get additional info
            value = user.getAdditionalInfo(label);
        }
        return value == null ? "" : value.toString();
    }

    /**
     * Sets the field value for the given user.<p>
     * 
     * @param user the user to set the field for
     * @param field the field to set
     */
    private void setUserValue(CmsUser user, I_CmsField field) {

        String methodName = REFLECTION_SETTER_PREFIX;
        methodName += ("" + field.getDbLabel().charAt(0)).toUpperCase();
        if (field.getDbLabel().length() > 1) {
            methodName += field.getDbLabel().substring(1);
        }
        try {
            // try to access the method
            Method method = CmsUser.class.getMethod(methodName, new Class[] {String.class});
            method.invoke(user, new String[] {field.getValue()});
        } catch (Exception e) {
            // set additional info
            user.setAdditionalInfo(field.getDbLabel(), field.getValue());
        }
    }
}
