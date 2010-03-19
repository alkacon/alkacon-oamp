/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/A_CmsField.java,v $
 * Date   : $Date: 2010/03/19 15:31:10 $
 * Version: $Revision: 1.4 $
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

import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;

/**
 * Abstract base class for all input fields.<p>
 * 
 * @author Andreas Zahner 
 * @author Thomas Weckert
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 7.0.4 
 */
public abstract class A_CmsField implements I_CmsField {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormHandler.class);

    private String m_dbLabel;
    private String m_errorMessage;
    private List m_items;
    private String m_label;
    private boolean m_mandatory;
    private String m_name;
    private int m_placeholder;
    private int m_position;
    private String m_validationExpression;
    private String m_value;

    /**
     * Default constructor.<p>
     */
    public A_CmsField() {

        super();

        m_items = new ArrayList();
        m_mandatory = false;
        m_value = "";
        m_validationExpression = "";
        m_placeholder = 0;
        m_position = 0;
        m_dbLabel = "";
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getDbLabel()
     */
    public String getDbLabel() {

        return m_dbLabel;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getErrorMessage()
     */
    public String getErrorMessage() {

        return m_errorMessage;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getItems()
     */
    public List getItems() {

        return m_items;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getLabel()
     */
    public String getLabel() {

        return m_label;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getName()
     */
    public String getName() {

        return m_name;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getPlaceholder()
     */
    public int getPlaceholder() {

        return m_placeholder;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getPosition()
     */
    public int getPosition() {

        return m_position;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getValidationExpression()
     */
    public String getValidationExpression() {

        return m_validationExpression;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getValue()
     */
    public String getValue() {

        return m_value;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#isMandatory()
     */
    public boolean isMandatory() {

        return m_mandatory;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#needsItems()
     */
    public boolean needsItems() {

        return false;
    }

    /**
     * Sets the database label.<p>
     *
     * @param dbLabel the database label to set
     */
    public void setDbLabel(String dbLabel) {

        m_dbLabel = dbLabel;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setErrorMessage(java.lang.String)
     */
    public void setErrorMessage(String errorMessage) {

        m_errorMessage = errorMessage;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setItems(java.util.List)
     */
    public void setItems(List items) {

        m_items = items;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setLabel(java.lang.String)
     */
    public void setLabel(String description) {

        m_label = description;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setMandatory(boolean)
     */
    public void setMandatory(boolean mandatory) {

        m_mandatory = mandatory;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setName(java.lang.String)
     */
    public void setName(String name) {

        m_name = name;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setPlaceholder(int)
     */
    public void setPlaceholder(int placeholder) {

        m_placeholder = placeholder;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setPosition(int)
     */
    public void setPosition(int position) {

        m_position = position;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setValidationExpression(java.lang.String)
     */
    public void setValidationExpression(String expression) {

        m_validationExpression = expression;
    }

    /**
     * Returns the selected items.<p>
     * 
     * @return the selected items
     */
    protected List getSelectedItems() {

        List selected = new ArrayList();
        List values = (getValue() == null ? null : CmsStringUtil.splitAsList(getValue(), ",", true));
        Iterator i = getItems().iterator();
        while (i.hasNext()) {
            CmsFieldItem curOption = (CmsFieldItem)i.next();
            if (values != null) {
                if (values.contains(curOption.getValue())) {
                    selected.add(curOption);
                }
            } else if (curOption.isSelected()) {
                selected.add(curOption);
            }
        }
        return selected;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#setValue(java.lang.String)
     */
    public void setValue(String value) {

        m_value = value;
    }

    /**
     * Returns the field value as a String.<p>
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {

        String result;
        if (needsItems()) {
            // check which item has been selected
            StringBuffer fieldValue = new StringBuffer(8);
            Iterator k = getItems().iterator();
            boolean isSelected = false;
            while (k.hasNext()) {
                CmsFieldItem currentItem = (CmsFieldItem)k.next();
                if (currentItem.isSelected()) {
                    if (isSelected) {
                        fieldValue.append(", ");
                    }
                    fieldValue.append(currentItem.getLabel());
                    isSelected = true;
                }
            }
            result = fieldValue.toString();
        } else {
            // for other field types, append value
            result = getValue();
        }

        return result;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#validate(CmsFormHandler)
     */
    public String validate(CmsFormHandler formHandler) {

        // validate the constraints
        String validationError = validateConstraints();
        if (CmsStringUtil.isEmpty(validationError)) {

            // no constraint error- validate the input value
            validationError = validateValue();
        }

        return validationError;
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {

        try {
            if (m_items != null) {
                m_items.clear();
            }
        } catch (Throwable t) {
            // ignore
        }
        super.finalize();
    }

    /**
     * This function sets the cells of place holder. Its only work with 
     * a column size of 2.<p> 
     * 
     * @param message integer value of adding to the place holder value
     */
    protected void incrementPlaceholder(String message) {

        int parse = 0;
        if (!CmsStringUtil.isEmptyOrWhitespaceOnly(message) && !message.startsWith("?")) {
            parse = Integer.parseInt(message.trim());
        }
        m_placeholder += parse;
    }

    /**
     * This functions looks if the row should be end. By one colsize, its 
     * every time ending. By two colsize every second cell its ending.
     * 
     * @param colSizeTwo if two columns should be shown
     * 
     * @return true the row end must shown
     */
    protected boolean showRowEnd(String colSizeTwo) {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(colSizeTwo) || !colSizeTwo.trim().equalsIgnoreCase("true")) {
            return true;
        }

        boolean result = false;
        if (m_position != 0) {
            result = true;
        }
        if (m_position == 0) {
            m_position = 1;
        } else {
            m_position = 0;
        }
        //if its need a place holder
        if ((m_position == 1) && (m_placeholder >= 1)) {
            result = true;
            m_position = 0;
            m_placeholder--;
        }
        return result;
    }

    /**
     * This functions looks if the row should be start. By one colsize, its 
     * every time starting. By two colsize every second cell its starting.
     * 
     * @param colSizeTwo if two columns should be shown
     * @return true if the row should shown
     */
    protected boolean showRowStart(String colSizeTwo) {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(colSizeTwo) || !colSizeTwo.trim().equalsIgnoreCase("true")) {
            return true;
        }
        if (m_position == 0) {
            return true;
        }
        return false;
    }

    /**
     * Validates the constraints if this field is mandatory.<p>
     * 
     * @return {@link CmsFormHandler#ERROR_MANDATORY} if a constraint is violated
     */
    protected String validateConstraints() {

        if (isMandatory()) {
            // check if the field has a value
            if (needsItems()) {
                // check if at least one item has been selected
                Iterator k = m_items.iterator();
                boolean isSelected = false;
                while (k.hasNext()) {
                    CmsFieldItem currentItem = (CmsFieldItem)k.next();
                    if (currentItem.isSelected()) {
                        isSelected = true;
                        continue;
                    }
                }

                if (!isSelected) {
                    // no item has been selected, create an error message
                    return CmsFormHandler.ERROR_MANDATORY;
                }
            } else {
                // check if the field has been filled out
                if (CmsStringUtil.isEmpty(m_value)) {
                    return CmsFormHandler.ERROR_MANDATORY;
                }
            }
        }

        return null;
    }

    /**
     * Validates the input value of this field.<p>
     * 
     * @return {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    protected String validateValue() {

        // validate non-empty values with given regular expression
        if (CmsStringUtil.isNotEmpty(m_value) && (!"".equals(m_validationExpression))) {
            try {
                Pattern pattern = Pattern.compile(m_validationExpression);
                if (!pattern.matcher(m_value).matches()) {
                    return CmsFormHandler.ERROR_VALIDATION;
                }
            } catch (PatternSyntaxException e) {
                // syntax error in regular expression, log to opencms.log
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(Messages.LOG_ERR_PATTERN_SYNTAX_0), e);
                }
            }
        }

        return null;
    }
}