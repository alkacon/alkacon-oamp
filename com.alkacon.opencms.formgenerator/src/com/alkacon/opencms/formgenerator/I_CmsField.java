/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/I_CmsField.java,v $
 * Date   : $Date: 2008/02/19 11:55:26 $
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

import org.opencms.i18n.CmsMessages;

import java.util.List;

/**
 * Defines the methods required for form fields.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4 
 */
public interface I_CmsField {

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
     * Returns the database label.<p>
     *
     * @return the database label
     */
    String getDbLabel();

    /**
     * Returns a optional, custom error message to be displayed instead of the standard validation error message.<p>
     * 
     * @return a custom error message for validation errors, or null
     */
    String getErrorMessage();

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
     * Returns the place holder.<p>
     *
     * @return the place holder
     */
    int getPlaceholder();

    /**
     * Returns the position.<p>
     *
     * @return the position
     */
    int getPosition();

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
     * Sets the database label.<p>
     *
     * @param dbLabel the database label to set
     */
    void setDbLabel(String dbLabel);

    /**
     * Sets the error message if validation failed.<p>
     * 
     * @param errorMessage the error message if validation failed
     */
    void setErrorMessage(String errorMessage);

    /**
     * Sets the list of items for select boxes, radio buttons and checkboxes.<p>
     * 
     * The list contains CmsFieldItem objects with the following information:
     * <ol>
     * <li>the value of the item</li>
     * <li>the description of the item</li>
     * <li>the selection flag of the item (true or false)</li>
     * </ol>
     * 
     * @param items the list of items for select boxes, radio buttons and checkboxes
     */
    void setItems(List items);

    /**
     * Sets the description text of the input field.<p>
     * 
     * @param description the description text of the input field
     */
    void setLabel(String description);

    /**
     * Sets if this input field is mandatory.<p>
     * 
     * @param mandatory true if this input field is mandatory, otherwise false
     */
    void setMandatory(boolean mandatory);

    /**
     * Sets the name of the input field.<p>
     * 
     * @param name the name of the input field
     */
    void setName(String name);

    /**
     * Sets the place holder.<p>
     *
     * @param placeholder the place holder to set
     */
    void setPlaceholder(int placeholder);

    /**
     * Sets the position.<p>
     *
     * @param position the position to set
     */
    void setPosition(int position);

    /**
     * Sets the regular expression that is used for validation of the field.<p>
     * 
     * @param expression the regular expression that is used for validation of the field
     */
    void setValidationExpression(String expression);

    /**
     * Sets the initial value of the field.<p>
     * 
     * @param value the initial value of the field
     */
    void setValue(String value);

    /**
     * Validates this field by validating it's constraints and input value.<p>
     * 
     * @param formHandler the handler of the current form
     * 
     * @return null in case of no error, {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed, {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    String validate(CmsFormHandler formHandler);

}