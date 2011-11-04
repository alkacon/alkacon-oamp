/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsTextareaField.java,v $
 * Date   : $Date: 2011/03/09 15:14:34 $
 * Version: $Revision: 1.8 $
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

import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.I_CmsMacroResolver;

/**
 * Represents a text area.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.8 $
 * 
 * @since 7.0.4 
 */
public class CmsTextareaField extends A_CmsField {

    /** HTML field type: text area. */
    private static final String TYPE = "textarea";

    /**
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#getType()
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
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#buildHtml(CmsFormHandler, CmsMessages, String, boolean, String)
     */
    @Override
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        String errorMessage = createStandardErrorMessage(errorKey, messages);
        String attributes = null;

        if (CmsStringUtil.isNotEmpty(errorKey)
            && !CmsFormHandler.ERROR_MANDATORY.equals(errorKey)
            && CmsStringUtil.isNotEmpty(getErrorMessage())
            && (getErrorMessage().indexOf(I_CmsMacroResolver.MACRO_DELIMITER) == 0)) {
            // there are additional field attributes defined in the error message of the field
            attributes = " " + getErrorMessage().substring(2, getErrorMessage().length() - 1);
            errorMessage = null;
        }

        String result = createHtml(formHandler, messages, null, getType(), attributes, errorMessage, showMandatory);
        // sets the cell numbers for the place holders in two column layout
        incrementPlaceholder(messages.key("form.html.multiline.placeholder"));
        return result;
    }

    /**
     * Returns the XML escaped value of the field.<p>
     * 
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#getValueEscaped()
     */
    @Override
    public String getValueEscaped() {

        return CmsEncoder.escapeXml(getValue());
    }

}
