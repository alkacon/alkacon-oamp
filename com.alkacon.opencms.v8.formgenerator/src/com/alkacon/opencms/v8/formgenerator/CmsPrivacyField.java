/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsPrivacyField.java,v $
 * Date   : $Date: 2011/05/17 12:36:06 $
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

package com.alkacon.opencms.v8.formgenerator;

import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a confirmation check box with a link.<p>
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4 
 * 
 */
public class CmsPrivacyField extends CmsCheckboxField {

    /** HTML field type: checkbox. */
    private static final String TYPE = "privacy";

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

        String fieldLabel = getLabel();
        String errorMessage = null;
        String mandatory = "";
        boolean showMandatoryLabel = false;

        if (CmsStringUtil.isNotEmpty(errorKey)) {
            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }
        // show the text with the mandatory marker, if exists
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(fieldLabel)) {
            fieldLabel = fieldLabel + mandatory;
            showMandatoryLabel = true;
        } else {
            fieldLabel = "&nbsp;";
        }

        Map<String, Object> stAttributes = new HashMap<String, Object>();
        // set special label as additional attribute
        stAttributes.put("label", fieldLabel);

        // set the item values
        if (getItems().size() > 0) {
            CmsFieldItem curOption = getItems().get(0);
            //check if an internal link should be generated
            String link = curOption.getLabel();
            if (link.startsWith("/")) {
                link = formHandler.link(link);
            }
            // set link and link text as additional attributes
            stAttributes.put("link", link);
            stAttributes.put("linktext", curOption.getValue() + (showMandatoryLabel ? "" : mandatory));
        }

        return createHtml(formHandler, messages, stAttributes, getType(), null, errorMessage, showMandatory);
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#getType()
     */
    @Override
    public String getType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#toString()
     */
    @Override
    public String toString() {

        return getValue();
    }

}
