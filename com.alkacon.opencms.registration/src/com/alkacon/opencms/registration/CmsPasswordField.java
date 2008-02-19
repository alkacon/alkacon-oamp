/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.registration/src/com/alkacon/opencms/registration/CmsPasswordField.java,v $
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

import com.alkacon.opencms.formgenerator.A_CmsField;
import com.alkacon.opencms.formgenerator.CmsFormHandler;

import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

/**
 * Represents a password field.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.1 $
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
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, org.opencms.i18n.CmsMessages, String, boolean)
     */
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer();

        String fieldLabel = getLabel();
        String errorMessage = "";
        String mandatory = "";

        if (CmsStringUtil.isNotEmpty(errorKey)) {
            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (ERROR_LOGIN.equals(errorKey)) {
                errorMessage = ""; // will be show in the old password row
            } else if (ERROR_CONFIRMATION.equals(errorKey)) {
                errorMessage = ""; // will be show in the confirmation row
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }
            if (!ERROR_LOGIN.equals(errorKey) && !ERROR_CONFIRMATION.equals(errorKey)) {
                errorMessage = messages.key("form.html.error.start")
                    + errorMessage
                    + messages.key("form.html.error.end");
                fieldLabel = messages.key("form.html.label.error.start")
                    + fieldLabel
                    + messages.key("form.html.label.error.end");
            }
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // zero row: confirmation, only if in profile mode

        if (!formHandler.getCmsObject().getRequestContext().currentUser().isGuestUser()) {
            // line #1
            if (showRowStart(messages.key("form.html.col.two"))) {
                buf.append(messages.key("form.html.row.start")).append("\n");
            }

            // line #2
            buf.append(messages.key("form.html.label.start"));
            if (ERROR_LOGIN.equals(errorKey)) {
                buf.append(messages.key("form.html.label.error.start"));
            }
            buf.append(messages.key("password.label.oldpwd"));
            if (ERROR_LOGIN.equals(errorKey)) {
                buf.append(messages.key("form.html.label.error.end"));
            }
            buf.append(mandatory).append(messages.key("form.html.label.end")).append("\n");

            // line #3
            buf.append(messages.key("form.html.field.start")).append("<input type=\"password\" name=\"").append(
                PREFIX_PASSWORD).append(getName()).append("\" value=\"").append("\"").append(
                formHandler.getFormConfiguration().getFormFieldAttributes()).append("/>");
            if (ERROR_LOGIN.equals(errorKey)) {
                buf.append(messages.key("form.html.error.start")).append(messages.key("password.error.login")).append(
                    messages.key("form.html.error.end"));
            }
            buf.append(messages.key("form.html.field.end")).append("\n");

            // line #4
            if (showRowEnd(messages.key("form.html.col.two"))) {
                buf.append(messages.key("form.html.row.end")).append("\n");
            }
        }

        // first row: password

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(fieldLabel).append(mandatory).append(
            messages.key("form.html.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.field.start")).append("<input type=\"password\" name=\"").append(getName()).append(
            "\" value=\"").append("\"").append(formHandler.getFormConfiguration().getFormFieldAttributes()).append("/>").append(
            messages.key("form.html.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

        // second row: confirmation

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.label.start"));
        if (ERROR_CONFIRMATION.equals(errorKey)) {
            buf.append(messages.key("form.html.label.error.start"));
        }
        buf.append(messages.key("password.label.confirmation"));
        if (ERROR_CONFIRMATION.equals(errorKey)) {
            buf.append(messages.key("form.html.label.error.end"));
        }
        buf.append(mandatory).append(messages.key("form.html.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.field.start")).append("<input type=\"password\" name=\"").append(
            PREFIX_CONFIRMATION).append(getName()).append("\" value=\"").append("\"").append(
            formHandler.getFormConfiguration().getFormFieldAttributes()).append("/>");
        if (ERROR_CONFIRMATION.equals(errorKey)) {
            buf.append(messages.key("form.html.error.start")).append(messages.key("password.error.login")).append(
                messages.key("form.html.error.end"));
        }
        buf.append(messages.key("form.html.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

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
    public String validate(CmsFormHandler formHandler) {

        String validationError = super.validate(formHandler);
        if (CmsStringUtil.isEmpty(validationError)) {
            // old password, only if in profile mode
            if (!formHandler.getCmsObject().getRequestContext().currentUser().isGuestUser()) {
                // get old password value from request 
                String[] parameterValues = (String[])formHandler.getParameterMap().get(PREFIX_PASSWORD + getName());
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
            String[] parameterValues = (String[])formHandler.getParameterMap().get(PREFIX_CONFIRMATION + getName());
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
