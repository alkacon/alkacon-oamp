/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/I_CmsField.java,v $
 * Date   : $Date: 2008/01/17 15:24:55 $
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
package com.alkacon.opencms.formgenerator;

import org.opencms.i18n.CmsMessages;

import java.util.List;

/**
 * Defines the methods required for form fields.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4 
 */
public interface I_CmsField {

    /**
     * Returns the list of items for select boxes, radio buttons and checkboxes.<p>
     * 
     * The list contains CmsFieldItem objects with the following information:
     * <ol>
     * <li>the value of the item</li>
     * <li>the description of the item</li>
     * <li>the selection of the item (true or false)</li>
     * </ol>
     * 
     * @return the list of items for select boxes, radio buttons and checkboxes
     */
    List getItems();

    /**
     * Returns the description text of the input field.<p>
     * 
     * @return the description text of the input field
     */
    String getLabel();

    /**
     * Returns the name of the input field.<p>
     * 
     * @return the name of the input field
     */
    String getName();

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    String getType();

    /**
     * Returns the regular expression that is used for validation of the field.<p>
     * 
     * @return the regular expression that is used for validation of the field
     */
    String getValidationExpression();

    /**
     * Returns the initial value of the field.<p>
     * 
     * @return the initial value of the field
     */
    String getValue();
    
    /**
     * Returns a optional, custom error message to be displayed instead of the standard validation error message.<p>
     * 
     * @return a custom error message for validation errors, or null
     */
    String getErrorMessage();

    /**
     * Returns if this input field is mandatory.<p>
     * 
     * @return true if this input field is mandatory, otherwise false
     */
    boolean isMandatory();

    /**
     * Checks if an item list is needed for this field.<p>
     * 
     * @return true if an item list is needed for this field, otherwise false
     */
    boolean needsItems();

    /**
     * Validates this field by validating it's constraints and input value.<p>
     * 
     * @param formHandler the handler of the current form
     * @return null in case of no error, {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed, {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    String validate(CmsFormHandler formHandler);
    
    /**
     * Builds the HTML input element for this element to be used in a frontend JSP.<p>
     * 
     * @param formHandler the handler of the current form
     * @param messages a resource bundle containing HTML snippets to build the HTML element
     * @param errorKey the key of the current error message
     * @param showMandatory flag to determine if the mandatory mark should be shown or not
     * 
     * @return the HTML input element for this element to be used in a frontend JSP
     */
    String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory);
    
    
    /**
     * Returns the placeholder.<p>
     *
     * @return the placeholder
     */
    int getPlaceholder();

    /**
     * Sets the placeholder.<p>
     *
     * @param placeholder the placeholder to set
     */
    void setPlaceholder(int placeholder);

    /**
     * Returns the position.<p>
     *
     * @return the position
     */
    int getPosition();
    
    /**
     * Sets the position.<p>
     *
     * @param position the position to set
     */
    void setPosition(int position);
    
}