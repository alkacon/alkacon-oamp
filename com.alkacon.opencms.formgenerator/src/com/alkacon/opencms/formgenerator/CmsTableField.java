/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsTableField.java,v $
 * Date   : $Date: 2008/05/16 10:09:30 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.formgenerator;

import org.opencms.configuration.CmsConfigurationException;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;

/**
 * Represents a table with input fields.<p>
 * 
 * This field can not be shown in the two column design.<p>
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4 
 */
public class CmsTableField extends A_CmsField {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsTableField.class);

    /** HTML field type: table field. */
    private static final String TYPE = "table";

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /** contains the list of all columns. */
    private List m_cols;

    /** contains the items.*/
    private Map m_items;

    /** contains the list of all rows. */
    private List m_rows;

    /**
     * 
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(com.alkacon.opencms.formgenerator.CmsFormHandler, org.opencms.i18n.CmsMessages, java.lang.String, boolean)
     */
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer();
        String fieldLabel = getLabel();
        String errorMessage = "";

        if (CmsStringUtil.isNotEmpty(errorKey)) {

            // get the special error message
            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }

            // generate the error message in a new line
            StringBuffer tmpErrorMsg = new StringBuffer();
            tmpErrorMsg.append(messages.key("form.html.label.start"));
            tmpErrorMsg.append("&nbsp;");
            tmpErrorMsg.append(messages.key("form.html.label.end"));
            tmpErrorMsg.append(messages.key("form.html.field.start"));
            tmpErrorMsg.append(messages.key("form.html.error.start"));
            tmpErrorMsg.append(errorMessage);
            tmpErrorMsg.append(messages.key("form.html.end.end"));
            tmpErrorMsg.append(messages.key("form.html.field.end"));
            errorMessage = tmpErrorMsg.toString();
        }

        // build the label new if mandatory exist or an error key
        if ((isMandatory() && showMandatory) || CmsStringUtil.isNotEmpty(errorKey)) {
            fieldLabel = buildLabel(messages, (isMandatory() && showMandatory), CmsStringUtil.isNotEmpty(errorKey));
        }

        // if its two column design, then show this field alone in one row
        if (!showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

        // line #1
        buf.append(messages.key("form.html.row.start")).append("\n");

        // line #2
        buf.append(messages.key("form.html.table.label.start"));
        buf.append(fieldLabel);
        buf.append(messages.key("form.html.table.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.table.field.start"));
        buf.append(buildHtml(messages, true));
        buf.append(messages.key("form.html.table.field.end")).append("\n");

        // new line for the error message if exists
        if (CmsStringUtil.isNotEmpty(errorKey)) {
            buf.append(messages.key("form.html.row.end"));
            buf.append(messages.key("form.html.row.start"));
            buf.append(errorMessage).append("\n");
        }

        // line #4
        buf.append(messages.key("form.html.row.end")).append("\n");

        return buf.toString();
    }

    /**
     * Returns the HTML with a table where each cell is a input field.<p>
     * 
     * @param editable if the cells are editable or not
     * @param messages needed to create a user defined label HTML
     * 
     * @return the HTML with a table where each cell is a input field
     */
    public String buildHtml(CmsMessages messages, boolean editable) {

        StringBuffer result = new StringBuffer();
        String col, row, name;
        CmsFieldItem item;

        // append the head of the table
        result.append(messages.key("form.html.table.field.head.start"));
        result.append(messages.key("form.html.table.row.start"));
        for (int k = 0; k < m_cols.size(); k++) {
            col = (String)m_cols.get(k);
            result.append(messages.key("form.html.table.col.head.start"));
            result.append(CmsStringUtil.escapeHtml(col));
            result.append(messages.key("form.html.table.col.head.end"));
        }
        result.append(messages.key("form.html.table.row.end"));
        result.append(messages.key("form.html.table.field.head.end"));

        // append the body
        result.append(messages.key("form.html.table.field.body.start"));
        for (int i = 0; i < m_rows.size(); i++) {
            row = (String)m_rows.get(i);
            result.append(messages.key("form.html.table.row.start"));
            for (int j = 0; j < m_cols.size(); j++) {
                col = (String)m_cols.get(j);
                result.append(messages.key("form.html.table.col.body.start"));
                name = col + "_" + row;
                if (m_items.containsKey(name)) {
                    item = (CmsFieldItem)m_items.get(name);
                    if (editable) {
                        result.append("<input type=\"text\" name=\"").append(getName()).append(item.getLabel());
                        result.append("\" value=\"").append(CmsStringUtil.escapeHtml(item.getValue()));
                        result.append("\"").append(" class=\"table\"/>");
                    } else {
                        result.append("<p class=\"table\">");
                        result.append(CmsStringUtil.escapeHtml(item.getValue())).append("</p>");
                    }
                }
                result.append(messages.key("form.html.table.col.body.end"));
            }
            result.append(messages.key("form.html.table.row.end"));
        }
        result.append(messages.key("form.html.table.field.body.end"));
        return result.toString();
    }

    /**
     * This function builds the label with the mandatory text and marks the label if errors exists.<p>
     * 
     * @param messages needed to create a user defined label HTML
     * @param mandatory if an mandatory field or not
     * @param error if the labels mark as error or not
     * 
     * @return the HTML with the specific label
     */
    public String buildLabel(CmsMessages messages, boolean mandatory, boolean error) {

        StringBuffer label = new StringBuffer(messages.key("form.html.table.label.head.start"));
        label.append(messages.key("form.html.table.label.head.end"));
        label.append(messages.key("form.html.table.label.body.start"));
        String row;
        for (int i = 0; i < m_rows.size(); i++) {
            row = (String)m_rows.get(i);
            label.append(messages.key("form.html.table.row.start"));
            label.append(messages.key("form.html.table.col.body.start"));
            if (error) {
                label.append(messages.key("form.html.label.error.start"));
            }
            label.append(CmsStringUtil.escapeHtml(row));
            if (error) {
                label.append(messages.key("form.html.label.error.end"));
            }
            if (mandatory) {
                label.append(messages.key("form.html.mandatory"));
            }
            label.append(messages.key("form.html.table.col.body.end"));
            label.append(messages.key("form.html.table.row.end"));
        }
        label.append(messages.key("form.html.table.label.body.end"));
        return label.toString();
    }

    /**
     * Returns a table without HTML like "col_row:value".<p>
     * 
     * @return the table without using HTML needed for email
     */
    public String buildText() {

        StringBuffer result = new StringBuffer();
        String col, row, name;
        CmsFieldItem item;
        for (int i = 0; i < m_rows.size(); i++) {
            row = (String)m_rows.get(i);
            for (int j = 0; j < m_cols.size(); j++) {
                col = (String)m_cols.get(j);
                name = col + "_" + row;
                if (m_items.containsKey(name)) {
                    item = (CmsFieldItem)m_items.get(name);
                    result.append(name).append(":\t");
                    result.append(item.getValue()).append("\n");
                }
            }
        }
        return result.toString();
    }

    /**
     * 
     * @see com.alkacon.opencms.formgenerator.A_CmsField#getItems()
     */
    public List getItems() {

        return new ArrayList(m_items.values());
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * Reads from the default value the configuration of the rows and columns and fills 
     * it with the values from the parameter if its exists.<p>
     * 
     * @param defaultValue the default value with the configuration of the rows and columns
     * @param parameter the map of the requested parameter
     * @param messages needed to create a user defined label HTML
     * 
     * @throws CmsConfigurationException if no rows or columns are defined
     */
    public void parseDefault(CmsMessages messages, String defaultValue, Map parameter) throws CmsConfigurationException {

        m_items = new HashMap();

        // check if the default value is empty
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(defaultValue)) {
            throw new CmsConfigurationException(Messages.get().container(
                Messages.ERR_INIT_INPUT_FIELD_MISSING_ITEM_2,
                getName(),
                getType()));
        }

        String backend = defaultValue;
        String frontend = defaultValue;
        // parse the default value
        Pattern pat = Pattern.compile("^(%\\()(.*)(\\)).*");
        Matcher match = pat.matcher(backend);
        if (match.matches()) {
            // a frontend exists
            frontend = match.group(2);
            backend = backend.substring(match.end(2) + 1, backend.length());
            if (CmsStringUtil.isEmpty(backend)) {
                backend = frontend;
            }
        } else {
            frontend = backend;
        }

        List cells = CmsStringUtil.splitAsList(frontend, "|");
        List dbcells = CmsStringUtil.splitAsList(backend, "|");

        if (cells.size() > 1 && dbcells.size() > 1) {

            // get the columns and rows from the default value
            List testRow = new ArrayList();
            List testCol = new ArrayList();
            m_cols = CmsStringUtil.splitAsList((String)cells.get(0), ",");
            List dbcols = CmsStringUtil.splitAsList((String)dbcells.get(0), ",");
            m_rows = CmsStringUtil.splitAsList((String)cells.get(1), ",");
            List dbrows = CmsStringUtil.splitAsList((String)dbcells.get(1), ",");

            // test if the frontend and backend columns are in the size identical
            if (m_cols.size() != dbcols.size()) {
                throw new CmsConfigurationException(Messages.get().container(Messages.ERR_INIT_TABLE_FIELD_UNEQUAL_0));
            }

            // test if the frontend and backend rows are in the size identical
            if (m_rows.size() != dbrows.size()) {
                throw new CmsConfigurationException(Messages.get().container(Messages.ERR_INIT_TABLE_FIELD_UNEQUAL_0));
            }

            String[] value;
            Object param;
            String col, row, name, dbname, dbrow;
            for (int i = 0; i < m_rows.size(); i++) {

                // look if the row not already exists
                row = replaceValue(i, m_rows);
                dbrow = ((String)dbrows.get(i)).trim();
                if (testRow.contains(row)) {
                    throw new CmsConfigurationException(Messages.get().container(
                        Messages.ERR_INIT_TABLE_FIELD_UNIQUE_1,
                        row));
                }

                // for each column generate the item
                for (int j = 0; j < m_cols.size(); j++) {

                    // look if the column not already exists
                    col = replaceValue(j, m_cols);
                    if (i == 0 && testCol.contains(col)) {
                        throw new CmsConfigurationException(Messages.get().container(
                            Messages.ERR_INIT_TABLE_FIELD_UNIQUE_1,
                            col));
                    }

                    // get the parameter of the cell
                    dbname = ((String)dbcols.get(j)).trim() + "_" + dbrow;
                    name = col + "_" + row;
                    param = parameter.get(getName() + name);
                    if (param == null) {
                        value = new String[] {""};
                    } else {
                        value = (String[])param;
                    }

                    // add the cell
                    m_items.put(name, new CmsFieldItem(value[0], name, dbname, false, false));
                    testCol.add(col);
                }

                testRow.add(row);
            }
            testCol.clear();
            testRow.clear();

            // build the label with the current rows
            String label = buildLabel(messages, false, false);
            setLabel(label);
            setDbLabel(label);
        }

        if (m_items.size() <= 0) {
            throw new CmsConfigurationException(Messages.get().container(
                Messages.ERR_INIT_INPUT_FIELD_MISSING_ITEM_2,
                getName(),
                getType()));
        }

    }

    /**
     * Validate each item in the table.<p>
     * 
     * @see com.alkacon.opencms.formgenerator.A_CmsField#validateConstraints()
     */
    protected String validateConstraints() {

        if (isMandatory()) {
            String col, row, name;
            CmsFieldItem item;
            // check if the field has a value
            for (int i = 0; i < m_cols.size(); i++) {
                col = (String)m_cols.get(i);
                for (int j = 0; j < m_rows.size(); j++) {
                    row = (String)m_rows.get(j);
                    name = col + "_" + row;
                    if (!m_items.containsKey(name)) {
                        return CmsFormHandler.ERROR_MANDATORY;
                    }
                    item = (CmsFieldItem)m_items.get(name);
                    // check if the field has been filled out
                    if (CmsStringUtil.isEmpty(item.getValue())) {
                        return CmsFormHandler.ERROR_MANDATORY;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Validate each item in the table.<p>
     * 
     * @see com.alkacon.opencms.formgenerator.A_CmsField#validateValue()
     */
    protected String validateValue() {

        if (!"".equals(getValidationExpression())) {
            Pattern pattern = Pattern.compile(getValidationExpression());
            List items = getItems();
            CmsFieldItem item;
            for (int i = 0; i < items.size(); i++) {
                try {
                    item = (CmsFieldItem)items.get(i);
                    if (CmsStringUtil.isNotEmpty(item.getValue()) && !pattern.matcher(item.getValue()).matches()) {
                        return CmsFormHandler.ERROR_VALIDATION;
                    }
                } catch (PatternSyntaxException e) {
                    // syntax error in regular expression, log to opencms.log
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(Messages.LOG_ERR_PATTERN_SYNTAX_0), e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Replace the value on the given position on the list through an trimmed value.<p>
     * 
     * @param pos the position to trim
     * @param list the current list
     * 
     * @return the trimmed value or if something goes wrong the value on the position
     */
    private String replaceValue(int pos, List list) {

        String result = (String)list.get(pos);
        try {
            result = result.trim();
            list.remove(pos);
            list.add(pos, result);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERR_TABLEFIELD_REPLACE_0), e);
            }
        }
        return result;
    }

}
