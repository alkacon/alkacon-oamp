/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsTableField.java,v $
 * Date   : $Date: 2009/04/01 16:02:20 $
 * Version: $Revision: 1.3 $
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
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.3 $
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

    /** The list of all column frontend labels. */
    private List m_cols;

    /** The list of all column backend labels. */
    private List m_dbcols;

    /** The items. */
    private Map m_tableItems;

    /** The list of all row frontend labels. */
    private List m_rows;

    /** The list of all row backend labels. */
    private List m_dbrows;

    /**
     * 
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(com.alkacon.opencms.formgenerator.CmsFormHandler, org.opencms.i18n.CmsMessages, java.lang.String, boolean)
     */
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer();
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
            tmpErrorMsg.append(messages.key("form.html.error.start"));
            tmpErrorMsg.append(errorMessage);
            tmpErrorMsg.append(messages.key("form.html.error.end"));
            errorMessage = tmpErrorMsg.toString();
        }

        // if its two column design, then show this field alone in one row
        if (!showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }
        // line #1
        buf.append(messages.key("form.html.row.start")).append("\n");
        // line #2
        buf.append(messages.key("form.html.table.label.start"));
        buf.append(buildLabel(messages, (isMandatory() && showMandatory), CmsStringUtil.isNotEmpty(errorKey)));
        buf.append(messages.key("form.html.table.label.end")).append("\n");
        // line #3
        buf.append(messages.key("form.html.table.field.start"));
        buf.append(buildHtml(messages, true));
        // new line for the error message if exists
        buf.append(errorMessage);
        buf.append(messages.key("form.html.table.field.end")).append("\n");
        // line #4
        buf.append(messages.key("form.html.row.end")).append("\n");

        return buf.toString();
    }

    /**
     * Returns the HTML with a table where each cell is a input field.<p>
     * 
     * @param messages needed to create a user defined label HTML
     * @param editable if the cells are editable or not
     * 
     * @return the HTML with a table where each cell is a input field
     */
    public String buildHtml(CmsMessages messages, boolean editable) {

        StringBuffer result = new StringBuffer();
        // append the head of the table
        result.append(messages.key("form.html.table.field.head.start"));
        result.append(messages.key("form.html.table.row.start"));
        for (int k = 0; k < m_cols.size(); k++) {
            String col = (String)m_cols.get(k);
            result.append(messages.key("form.html.table.col.head.start"));
            result.append(CmsStringUtil.escapeHtml(col));
            result.append(messages.key("form.html.table.col.head.end"));
        }
        result.append(messages.key("form.html.table.row.end"));
        result.append(messages.key("form.html.table.field.head.end"));

        // append the body
        result.append(messages.key("form.html.table.field.body.start"));
        for (int i = 0; i < m_dbrows.size(); i++) {
            String row = (String)m_dbrows.get(i);
            result.append(messages.key("form.html.table.row.start"));
            for (int j = 0; j < m_dbcols.size(); j++) {
                String col = (String)m_dbcols.get(j);
                result.append(messages.key("form.html.table.col.body.start"));
                String key = getKey(col, row, false);
                if (m_tableItems.containsKey(key)) {
                    CmsFieldItem item = (CmsFieldItem)m_tableItems.get(key);
                    if (editable) {
                        result.append("<input type=\"text\" name=\"").append(getName()).append(key);
                        result.append("\" value=\"").append(CmsStringUtil.escapeHtml(item.getValue()));
                        result.append("\"").append(" class=\"table\"/>");
                    } else {
                        result.append("<span class=\"table\">");
                        result.append(CmsStringUtil.escapeHtml(item.getValue())).append("</span>");
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

        StringBuffer label = new StringBuffer();
        label.append(messages.key("form.html.table.label.head.start"));
        label.append(messages.key("form.html.table.row.start"));
        label.append(messages.key("form.html.table.name.start"));
        if (error) {
            label.append(messages.key("form.html.label.error.start"));
        }
        label.append(CmsStringUtil.escapeHtml(getLabel()));
        if (error) {
            label.append(messages.key("form.html.label.error.end"));
        }
        if (mandatory) {
            label.append(messages.key("form.html.mandatory"));
        }
        label.append(messages.key("form.html.table.name.end"));
        label.append(messages.key("form.html.table.row.end"));
        for (int i = 0; i < m_rows.size(); i++) {
            String row = (String)m_rows.get(i);
            label.append(messages.key("form.html.table.row.start"));
            label.append(messages.key("form.html.table.row.head.start"));
            label.append(CmsStringUtil.escapeHtml(row));
            label.append(messages.key("form.html.table.row.head.end"));
            label.append(messages.key("form.html.table.row.end"));
        }
        label.append(messages.key("form.html.table.label.head.end"));
        return label.toString();
    }

    /**
     * Returns a table without HTML like "col_row:value".<p>
     * 
     * @param frontend if frontend or backend labels should be used 
     * 
     * @return the table without using HTML needed for email
     */
    public String buildText(boolean frontend) {

        StringBuffer result = new StringBuffer();
        List rows = frontend ? m_rows : m_dbrows;
        List cols = frontend ? m_cols : m_dbcols;
        for (int i = 0; i < rows.size(); i++) {
            String row = (String)rows.get(i);
            for (int j = 0; j < cols.size(); j++) {
                String col = (String)cols.get(j);
                String key = getKey((String)m_dbcols.get(j), (String)m_dbrows.get(i), false);
                if (m_tableItems.containsKey(key)) {
                    CmsFieldItem item = (CmsFieldItem)m_tableItems.get(key);
                    result.append(getKey(col, row, frontend)).append(":\t");
                    result.append(item.getValue()).append("\n");
                }
            }
        }
        return result.toString();
    }

    /**
     * @see com.alkacon.opencms.formgenerator.A_CmsField#getItems()
     */
    public List getItems() {

        return new ArrayList(m_tableItems.values());
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
     * 
     * @throws CmsConfigurationException if no rows or columns are defined
     */
    public void parseDefault(String defaultValue, Map parameter) throws CmsConfigurationException {

        m_tableItems = new HashMap();
        // check if the default value is empty
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(defaultValue)) {
            throw new CmsConfigurationException(Messages.get().container(
                Messages.ERR_INIT_INPUT_FIELD_MISSING_ITEM_2,
                getName(),
                getType()));
        }

        String backend = defaultValue;
        String frontend = defaultValue;
        // parse the default value, it should look like '%(ColumnA,ColumnB|RowA,RowB)dbcola,dbcolb|dbrowa,dbrowb'
        Matcher regex = Pattern.compile("^(%\\()(.*)(\\)).*").matcher(backend);
        if (regex.matches()) {
            // a frontend exists
            frontend = regex.group(2);
            backend = backend.substring(regex.end(2) + 1, backend.length());
            if (CmsStringUtil.isEmpty(backend)) {
                backend = frontend;
            }
        } else {
            frontend = backend;
        }

        List cells = CmsStringUtil.splitAsList(frontend, "|");
        List dbcells = CmsStringUtil.splitAsList(backend, "|");

        // get the columns and rows from the default value
        List testRow = new ArrayList();
        List testCol = new ArrayList();
        m_cols = CmsStringUtil.splitAsList((String)cells.get(0), ",");
        m_dbcols = CmsStringUtil.splitAsList((String)dbcells.get(0), ",", true);
        m_rows = CmsStringUtil.splitAsList((String)cells.get(1), ",");
        m_dbrows = CmsStringUtil.splitAsList((String)dbcells.get(1), ",", true);

        // test if the frontend and backend columns are in the size identical
        if (m_cols.size() != m_dbcols.size()) {
            throw new CmsConfigurationException(Messages.get().container(Messages.ERR_INIT_TABLE_FIELD_UNEQUAL_0));
        }
        // test if the frontend and backend rows are in the size identical
        if (m_rows.size() != m_dbrows.size()) {
            throw new CmsConfigurationException(Messages.get().container(Messages.ERR_INIT_TABLE_FIELD_UNEQUAL_0));
        }

        for (int i = 0; i < m_dbrows.size(); i++) {
            // look if the row not already exists
            String dbrow = (String)m_dbrows.get(i);
            if (testRow.contains(dbrow)) {
                throw new CmsConfigurationException(Messages.get().container(
                    Messages.ERR_INIT_TABLE_FIELD_UNIQUE_1,
                    dbrow));
            }
            // for each column generate the item
            for (int j = 0; j < m_dbcols.size(); j++) {
                // look if the column not already exists
                String dbcol = (String)m_dbcols.get(j);
                if ((i == 0) && testCol.contains(dbcol)) {
                    throw new CmsConfigurationException(Messages.get().container(
                        Messages.ERR_INIT_TABLE_FIELD_UNIQUE_1,
                        dbcol));
                }
                // get the parameter of the cell
                String key = getKey(dbcol, dbrow, false);
                Object param = parameter.get(getName() + key);
                String[] value = new String[] {""};
                if (param != null) {
                    value = (String[])param;
                }
                // add the cell
                m_tableItems.put(key, new CmsFieldItem(value[0], getKey(dbcol, dbrow, true), key, false, false));
                testCol.add(dbcol);
            }
            testRow.add(dbrow);
        }
        if (m_tableItems.size() <= 0) {
            throw new CmsConfigurationException(Messages.get().container(
                Messages.ERR_INIT_INPUT_FIELD_MISSING_ITEM_2,
                getName(),
                getType()));
        }
    }

    /**
     * Returns the key used to identify a single field, like when accessing the {@link #m_tableItems} map.<p>
     * 
     * @param col the col label
     * @param row the row label
     * @param frontend if for frontend purposes
     * 
     * @return the key
     */
    private String getKey(String col, String row, boolean frontend) {

        return frontend ? col + " - " + row : col + "_" + row;
    }

    /**
     * Validate each item in the table.<p>
     * 
     * @see com.alkacon.opencms.formgenerator.A_CmsField#validateConstraints()
     */
    protected String validateConstraints() {

        if (!isMandatory()) {
            return null;
        }
        // check if the field has a value
        for (int i = 0; i < m_dbcols.size(); i++) {
            String col = (String)m_dbcols.get(i);
            for (int j = 0; j < m_dbrows.size(); j++) {
                String row = (String)m_dbrows.get(j);
                String key = getKey(col, row, false);
                if (!m_tableItems.containsKey(key)) {
                    return CmsFormHandler.ERROR_MANDATORY;
                }
                CmsFieldItem item = (CmsFieldItem)m_tableItems.get(key);
                // check if the field has been filled out
                if (CmsStringUtil.isEmpty(item.getValue())) {
                    return CmsFormHandler.ERROR_MANDATORY;
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

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getValidationExpression())) {
            return null;
        }
        Pattern pattern = Pattern.compile(getValidationExpression());
        List items = getItems();
        for (int i = 0; i < items.size(); i++) {
            try {
                CmsFieldItem item = (CmsFieldItem)items.get(i);
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
        return null;
    }
}
