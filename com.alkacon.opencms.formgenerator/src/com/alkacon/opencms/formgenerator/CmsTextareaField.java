/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsTextareaField.java,v $
 * Date   : $Date: 2008/07/01 11:03:00 $
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

package com.alkacon.opencms.formgenerator;

import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.I_CmsMacroResolver;

/**
 * Represents a text area.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4 
 */
public class CmsTextareaField extends A_CmsField {

    /** HTML field type: text area. */
    private static final String TYPE = "textarea";

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

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
        String attributes = "";

        if (CmsStringUtil.isNotEmpty(errorKey)) {

            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())
                && getErrorMessage().indexOf(I_CmsMacroResolver.MACRO_DELIMITER) != 0) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }

            errorMessage = messages.key("form.html.error.start") + errorMessage + messages.key("form.html.error.end");
            fieldLabel = messages.key("form.html.label.error.start")
                + fieldLabel
                + messages.key("form.html.label.error.end");
        }

        if (CmsStringUtil.isNotEmpty(getErrorMessage())
            && getErrorMessage().indexOf(I_CmsMacroResolver.MACRO_DELIMITER) == 0) {
            attributes = " " + getErrorMessage().substring(2, getErrorMessage().length() - 1);
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.multiline.label.start")).append(fieldLabel).append(mandatory).append(
            messages.key("form.html.multiline.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.multiline.field.start")).append("<textarea cols=\"\" rows=\"\" name=\"").append(getName()).append(
            "\"").append(formHandler.getFormConfiguration().getFormFieldAttributes()).append(attributes).append(">").append(
            CmsEncoder.escapeXml(getValue())).append("</textarea>").append(errorMessage).append(
            messages.key("form.html.multiline.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

        incrementPlaceholder(messages.key("form.html.multiline.placeholder"));

        return buf.toString();
    }

}
