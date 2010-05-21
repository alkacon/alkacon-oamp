/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsEmptyField.java,v $
 * Date   : $Date: 2010/05/21 13:49:14 $
 * Version: $Revision: 1.5 $
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

/**
 * Represents a empty field.<p>
 * 
 * @version $Revision: 1.5 $
 * 
 * @since 7.0.4 
 * 
 */
public class CmsEmptyField extends A_CmsField {

    /** HTML field type: hidden field. */
    private static final String TYPE = "empty";

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

        StringBuffer buf = new StringBuffer(64);
        //line #1 start
        if (showRowStart(messages.key("form.html.col.two"))) {
            if (isSubField()) {
                buf.append(messages.key("form.html.row.subfield.start")).append("\n");
            } else {
                buf.append(messages.key("form.html.row.start")).append("\n");
            }
        }

        //line #2 start
        buf.append(messages.key("form.html.label.start")).append("&nbsp;").append(messages.key("form.html.label.end")).append(
            "\n");

        //line #3 start
        buf.append(messages.key("form.html.field.start"));
        String value = getValue();
        //if the default value is set, then the text is output
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
            buf.append("&nbsp;");
        } else {
            buf.append(value);
        }

        buf.append(messages.key("form.html.field.end")).append("\n");

        //line #1 end
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

}
