/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsTableField.java,v $
 * Date   : $Date: 2011-03-09 15:14:34 $
 * Version: $Revision: 1.6 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.formgenerator;

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

import org.antlr.stringtemplate.StringTemplate;

/**
 * Represents a table with input fields.<p>
 * 
 * This field can not be shown in the two column design.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4 
 */
public class CmsTableField extends A_CmsField {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsTableField.class);

    /** HTML field type: table field. */
    private static final String TYPE = "table";

    /** The list of all column frontend labels. */
    private List<String> m_cols;

    /** The list of all column backend labels. */
    private List<String> m_dbcols;

    /** The list of all row backend labels. */
    private List<String> m_dbrows;

    /** The list of all row frontend labels. */
    private List<String> m_rows;

    /** The items. */
    private Map<String, CmsFieldItem> m_tableItems;

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * 
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

        Map<String, Object> stAttributes = new HashMap<String, Object>();
        stAttributes.put("rows", getRowsEscaped());
        stAttributes.put("cols", getColumnsEscaped());
        stAttributes.put("rowswithitems", getRowsWithItems());
        stAttributes.put("editable", Boolean.TRUE);

        return createHtml(formHandler, messages, stAttributes, getType(), null, errorMessage, showMandatory);
    }

    /**
     * Returns the labels of the table field for usage in email texts.<p>
     * 
     * @param formHandler the handler of the current form
     * @return the HTML with the specific label
     */
    public String buildLabel(CmsFormHandler formHandler) {

        StringTemplate sTemplate = formHandler.getOutputTemplate("field_table_labels");
        // set necessary template attributes
        sTemplate.setAttribute("field", this);
        sTemplate.setAttribute("formconfig", formHandler.getFormConfiguration());
        sTemplate.setAttribute("errormessage", null);
        sTemplate.setAttribute("mandatory", null);
        sTemplate.setAttribute("rows", getRowsEscaped());
        return sTemplate.toString();
    }

    /**
     * Returns the rows of the table field for usage in email texts.<p>
     * 
     * @param formHandler the handler of the current form
     * @return the HTML with a table where each cell is a input field
     */
    public String buildRows(CmsFormHandler formHandler) {

        StringTemplate sTemplate = formHandler.getOutputTemplate("field_table_fields");
        // set necessary template attributes
        sTemplate.setAttribute("formconfig", formHandler.getFormConfiguration());
        sTemplate.setAttribute("cols", getColumnsEscaped());
        sTemplate.setAttribute("rowswithitems", getRowsWithItems());
        sTemplate.setAttribute("editable", Boolean.FALSE);
        return sTemplate.toString();
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
        List<String> rows = frontend ? m_rows : m_dbrows;
        List<String> cols = frontend ? m_cols : m_dbcols;
        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i);
            for (int j = 0; j < cols.size(); j++) {
                String col = cols.get(j);
                String key = getKey(m_dbcols.get(j), m_dbrows.get(i), false);
                if (m_tableItems.containsKey(key)) {
                    CmsFieldItem item = m_tableItems.get(key);
                    result.append(getKey(col, row, frontend)).append(":\t");
                    result.append(item.getValue()).append("\n");
                }
            }
        }
        return result.toString();
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#getItems()
     */
    @Override
    public List<CmsFieldItem> getItems() {

        return new ArrayList<CmsFieldItem>(m_tableItems.values());
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#needsItems()
     */
    @Override
    public boolean needsItems() {

        return true;
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
    public void parseDefault(String defaultValue, Map<String, String[]> parameter) throws CmsConfigurationException {

        m_tableItems = new HashMap<String, CmsFieldItem>();
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

        List<String> cells = CmsStringUtil.splitAsList(frontend, "|");
        List<String> dbcells = CmsStringUtil.splitAsList(backend, "|");

        // get the columns and rows from the default value
        List<String> testRow = new ArrayList<String>();
        List<String> testCol = new ArrayList<String>();
        m_cols = CmsStringUtil.splitAsList(cells.get(0), ",");
        m_dbcols = CmsStringUtil.splitAsList(dbcells.get(0), ",", true);
        m_rows = CmsStringUtil.splitAsList(cells.get(1), ",");
        m_dbrows = CmsStringUtil.splitAsList(dbcells.get(1), ",", true);

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
            String dbrow = m_dbrows.get(i);
            if (testRow.contains(dbrow)) {
                throw new CmsConfigurationException(Messages.get().container(
                    Messages.ERR_INIT_TABLE_FIELD_UNIQUE_1,
                    dbrow));
            }
            // for each column generate the item
            for (int j = 0; j < m_dbcols.size(); j++) {
                // look if the column not already exists
                String dbcol = m_dbcols.get(j);
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
     * Returns the HTML escaped column names.<p>
     * 
     * @return the HTML escaped column names
     */
    protected List<String> getColumnsEscaped() {

        List<String> result = new ArrayList<String>(m_cols.size());
        for (int i = 0; i < m_cols.size(); i++) {
            String col = m_cols.get(i);
            result.add(CmsStringUtil.escapeHtml(col));
        }
        return result;
    }

    /**
     * Returns the HTML escaped row names.<p>
     * 
     * @return the HTML escaped row names
     */
    protected List<String> getRowsEscaped() {

        List<String> result = new ArrayList<String>(m_cols.size());
        for (int i = 0; i < m_rows.size(); i++) {
            String row = m_rows.get(i);
            result.add(CmsStringUtil.escapeHtml(row));
        }
        return result;
    }

    /**
     * Returns a list of the table field rows, containig a list of field items representing the column.<p>
     * 
     * @return a list of the table field rows, containig a list of field items representing the column
     */
    protected List<List<CmsFieldItem>> getRowsWithItems() {

        List<List<CmsFieldItem>> result = new ArrayList<List<CmsFieldItem>>();
        for (int i = 0; i < m_dbrows.size(); i++) {
            String row = m_dbrows.get(i);
            List<CmsFieldItem> items = new ArrayList<CmsFieldItem>(m_dbcols.size());
            for (int j = 0; j < m_dbcols.size(); j++) {
                String col = m_dbcols.get(j);
                String key = getKey(col, row, false);
                if (m_tableItems.containsKey(key)) {
                    CmsFieldItem item = m_tableItems.get(key);
                    item.setName(getName() + key);
                    items.add(item);
                }
            }
            result.add(items);
        }
        return result;
    }

    /**
     * Validate each item in the table.<p>
     * 
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#validateConstraints()
     */
    @Override
    protected String validateConstraints() {

        if (!isMandatory()) {
            return null;
        }
        // check if the field has a value
        for (int i = 0; i < m_dbcols.size(); i++) {
            String col = m_dbcols.get(i);
            for (int j = 0; j < m_dbrows.size(); j++) {
                String row = m_dbrows.get(j);
                String key = getKey(col, row, false);
                if (!m_tableItems.containsKey(key)) {
                    return CmsFormHandler.ERROR_MANDATORY;
                }
                CmsFieldItem item = m_tableItems.get(key);
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
     * @see com.alkacon.opencms.v8.formgenerator.A_CmsField#validateValue()
     */
    @Override
    protected String validateValue() {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getValidationExpression())) {
            return null;
        }
        Pattern pattern = Pattern.compile(getValidationExpression());
        List<CmsFieldItem> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            try {
                CmsFieldItem item = items.get(i);
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
}
