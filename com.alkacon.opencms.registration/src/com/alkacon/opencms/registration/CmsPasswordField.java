/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.registration/src/com/alkacon/opencms/registration/CmsPasswordField.java,v $
 * Date   : $Date: 2011/03/10 11:59:04 $
 * Version: $Revision: 1.2 $
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

import com.alkacon.opencms.formgenerator.A_CmsField;
import com.alkacon.opencms.formgenerator.CmsFormHandler;

import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

import org.antlr.stringtemplate.StringTemplate;

/**
 * Represents a password field.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4 
 */
public class CmsPasswordField extends A_CmsField {

    /** Input field name prefix for the password confirmation. */
    private static final String PREFIX_CONFIRMATION = "cnf";

    /** Input field name prefix for the old password. */
    private static final String PREFIX_PASSWORD = "old";

    /** Password does not match. */
    public static final String ERROR_LOGIN = "login";

    /** Confirmation does not match the password. */
    public static final String ERROR_CONFIRMATION = "confirmation";

    /** HTML field type: hidden field. */
    private static final String TYPE = "password";

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, CmsMessages, String, boolean, String)
     */
    @Override
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        StringBuffer buf = new StringBuffer(512);

        int pos = getPosition();

        if (isTwoCols() == null) {
            // get the two columns configuration template
            StringTemplate sTemplate = formHandler.getOutputTemplate("form_twocolumns");
            setTwoCols(Boolean.valueOf(sTemplate.toString()).booleanValue());
        }

        // get the localized mandatory marker if necessary
        String mandatory = null;
        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // zero row: old password, only if in profile mode

        if (!formHandler.getCmsObject().getRequestContext().currentUser().isGuestUser()) {

            String errorMessage = null;
            if (ERROR_LOGIN.equals(errorKey)) {
                errorMessage = messages.key("password.error.login");
            }

            com.alkacon.opencms.formgenerator.CmsPasswordField pwField = new com.alkacon.opencms.formgenerator.CmsPasswordField();
            pwField.setLabel(messages.key("password.label.oldpwd"));
            pwField.setName(PREFIX_PASSWORD + getName());
            pwField.setValue("");
            pwField.setPosition(pos);
            pwField.setTwoCols(isTwoCols().booleanValue());

            // get the form field template
            StringTemplate sTemplate = formHandler.getOutputTemplate("field_"
                + com.alkacon.opencms.formgenerator.CmsPasswordField.getStaticType());

            // set default template attributes for the field
            sTemplate.setAttribute("field", pwField);
            sTemplate.setAttribute("formconfig", formHandler.getFormConfiguration());
            sTemplate.setAttribute("attributes", null);
            sTemplate.setAttribute("errormessage", errorMessage);
            sTemplate.setAttribute("mandatory", mandatory);
            buf.append(sTemplate.toString());
            pos = pwField.getPosition();
        }

        // first row: password

        String errorMessage;
        if (CmsStringUtil.isNotEmpty(errorKey)) {
            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (ERROR_LOGIN.equals(errorKey)) {
                errorMessage = null; // will be shown in the old password row
            } else if (ERROR_CONFIRMATION.equals(errorKey)) {
                errorMessage = null; // will be shown in the confirmation row
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }
        } else {
            errorMessage = null;
        }

        com.alkacon.opencms.formgenerator.CmsPasswordField pwField = new com.alkacon.opencms.formgenerator.CmsPasswordField();
        pwField.setLabel(getLabel());
        pwField.setName(getName());
        pwField.setValue("");
        pwField.setPosition(pos);
        pwField.setTwoCols(isTwoCols().booleanValue());

        // get the form field template
        StringTemplate sTemplate = formHandler.getOutputTemplate("field_"
            + com.alkacon.opencms.formgenerator.CmsPasswordField.getStaticType());

        // set default template attributes for the field
        sTemplate.setAttribute("field", pwField);
        sTemplate.setAttribute("formconfig", formHandler.getFormConfiguration());
        sTemplate.setAttribute("attributes", null);
        sTemplate.setAttribute("errormessage", errorMessage);
        sTemplate.setAttribute("mandatory", mandatory);
        buf.append(sTemplate.toString());
        pos = pwField.getPosition();

        // second row: confirmation

        errorMessage = null;
        if (ERROR_CONFIRMATION.equals(errorKey)) {
            errorMessage = messages.key("password.error.login");
        }

        pwField = new com.alkacon.opencms.formgenerator.CmsPasswordField();
        pwField.setLabel(messages.key("password.label.confirmation"));
        pwField.setName(PREFIX_CONFIRMATION + getName());
        pwField.setValue("");
        pwField.setPosition(pos);
        pwField.setTwoCols(isTwoCols().booleanValue());

        // get the form field template
        sTemplate = formHandler.getOutputTemplate("field_"
            + com.alkacon.opencms.formgenerator.CmsPasswordField.getStaticType());

        // set default template attributes for the field
        sTemplate.setAttribute("field", pwField);
        sTemplate.setAttribute("formconfig", formHandler.getFormConfiguration());
        sTemplate.setAttribute("attributes", null);
        sTemplate.setAttribute("errormessage", errorMessage);
        sTemplate.setAttribute("mandatory", mandatory);
        buf.append(sTemplate.toString());
        pos = pwField.getPosition();

        setPosition(pos);
        return buf.toString();
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.A_CmsField#validate(com.alkacon.opencms.formgenerator.CmsFormHandler)
     */
    @Override
    public String validate(CmsFormHandler formHandler) {

        String validationError = super.validate(formHandler);
        if (CmsStringUtil.isEmpty(validationError)) {
            // old password, only if in profile mode
            if (!formHandler.getCmsObject().getRequestContext().currentUser().isGuestUser()) {
                // get old password value from request 
                String[] parameterValues = formHandler.getParameterMap().get(PREFIX_PASSWORD + getName());
                StringBuffer value = new StringBuffer();
                if (parameterValues != null) {
                    for (int j = 0; j < parameterValues.length; j++) {
                        if (j != 0) {
                            value.append(", ");
                        }
                        value.append(parameterValues[j]);
                    }
                }

                // only if the user entered a password
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(value.toString())) {
                    try {
                        // check password
                        formHandler.getCmsObject().readUser(
                            formHandler.getCmsObject().getRequestContext().currentUser().getName(),
                            value.toString());
                        // password is ok
                    } catch (CmsException e) {
                        // wrong password
                        return ERROR_LOGIN;
                    }
                }
            }
            // get confirmation value from request 
            String[] parameterValues = formHandler.getParameterMap().get(PREFIX_CONFIRMATION + getName());
            StringBuffer value = new StringBuffer();
            if (parameterValues != null) {
                for (int j = 0; j < parameterValues.length; j++) {
                    if (j != 0) {
                        value.append(", ");
                    }
                    value.append(parameterValues[j]);
                }
            }

            if (!getValue().equals(value.toString())) {
                return ERROR_CONFIRMATION;
            }
        }
        return validationError;
    }
}
