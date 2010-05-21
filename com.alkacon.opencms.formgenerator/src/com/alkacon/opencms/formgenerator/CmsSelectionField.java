/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsSelectionField.java,v $
 * Date   : $Date: 2010/05/21 13:49:15 $
 * Version: $Revision: 1.6 $
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

import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a selection of options.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4 
 */
public class CmsSelectionField extends A_CmsField {

    /** HTML field type: select box. */
    private static final String TYPE = "select";

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
    @Override
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer(128);
        String fieldLabel = getLabel();
        String errorMessage = "";
        String mandatory = "";

        if (CmsStringUtil.isNotEmpty(errorKey)) {

            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }

            errorMessage = messages.key("form.html.error.start") + errorMessage + messages.key("form.html.error.end");
            fieldLabel = messages.key("form.html.label.error.start")
                + fieldLabel
                + messages.key("form.html.label.error.end");
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            if (isSubField()) {
                buf.append(messages.key("form.html.row.subfield.start")).append("\n");
            } else {
                buf.append(messages.key("form.html.row.start")).append("\n");
            }
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(fieldLabel).append(mandatory).append(
            messages.key("form.html.label.end")).append("\n");

        // line #3
        String attrOnChange = "";
        if (hasSubFields()) {
            attrOnChange = " onchange=\"toggleWebformSubFields(this);\"";
        }

        buf.append(messages.key("form.html.field.start")).append("<select name=\"").append(getName()).append("\"").append(
            formHandler.getFormConfiguration().getFormFieldAttributes()).append(attrOnChange).append(">").append("\n");

        List<CmsFieldItem> selected = getSelectedItems();
        // add the options
        Iterator<CmsFieldItem> i = getItems().iterator();
        while (i.hasNext()) {

            CmsFieldItem curOption = i.next();
            String selStr = "";
            if (selected.contains(curOption)) {
                selStr = " selected=\"selected\"";
            }

            buf.append("<option value=\"").append(curOption.getValue()).append("\"").append(selStr).append(">").append(
                curOption.getLabel()).append("</option>\n");
        }

        buf.append("</select>").append(errorMessage).append("\n");

        buf.append(messages.key("form.html.field.end")).append("\n");

        if (showRowEnd(messages.key("form.html.col.two"))) {
            if (isSubField()) {
                buf.append(messages.key("form.html.row.subfield.end")).append("\n");
            } else {
                buf.append(messages.key("form.html.row.end")).append("\n");
            }
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
     * @see com.alkacon.opencms.formgenerator.A_CmsField#needsItems()
     */
    @Override
    public boolean needsItems() {

        return true;
    }

}
