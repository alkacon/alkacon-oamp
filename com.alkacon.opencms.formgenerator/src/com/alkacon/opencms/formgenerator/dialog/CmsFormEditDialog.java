/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormEditDialog.java,v $
 * Date   : $Date: 2008/03/25 17:01:42 $
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

package com.alkacon.opencms.formgenerator.dialog;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsDisplayWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.widgets.CmsTextareaWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.workplace.CmsWidgetDialog;
import org.opencms.workplace.CmsWidgetDialogParameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Dialog to edit existing submitted data in the administration view.<p>
 * 
 * @author Anja Röttgers 
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4 
 */
public class CmsFormEditDialog extends CmsWidgetDialog {

    /** the length to cut the string text.*/
    public static final int STRING_TRIM_SIZE = 200;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormEditDialog.class);

    /** Defines which pages are valid for this dialog. */
    private static final String[] PAGES = {"page1"};

    /** a map with all fields and values. */
    private HashMap m_fields;

    /** contains the original data of current entry.**/
    private CmsFormDataBean m_formData;

    /** Contains the id of the current entry.**/
    private String m_paramEntryid;

    /** Contains the id of the current form.**/
    private String m_paramFormid;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsFormEditDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsFormEditDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWidgetDialog#actionCommit()
     */
    public void actionCommit() {

        List errors = new ArrayList();
        try {
            // look if every parameter is set
            if (m_formData != null
                && !CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramFormid)
                && !CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramEntryid)) {

                // get the list of all fields 
                List columnNames = CmsFormDataAccess.getInstance().readAllFormFieldNames(
                    m_paramFormid,
                    new Date(1),
                    new Date(Long.MAX_VALUE));

                // for each field look if the value has changed and update the database
                String column = null;
                CmsFormDataEditBean data;
                String value = null;
                String orgValue;
                for (int i = 0; i < columnNames.size(); i++) {
                    try {

                        // get for the field the old and new value
                        column = (String)columnNames.get(i);
                        data = (CmsFormDataEditBean)m_fields.get(column);
                        orgValue = m_formData.getFieldValue(column);
                        value = data.getValue();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(Messages.get().getBundle().key(
                                Messages.LOG_COMPARE_FORM_FIELDS_4,
                                new String[] {column, value, orgValue, m_paramEntryid}));
                        }

                        // compares the old and new value and update the database if not identical
                        if (!compareValues(orgValue, value) || (value != null && value.trim().length() == 0)) {
                            CmsFormDataAccess.getInstance().updateFieldValue(m_paramEntryid, column, value);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(Messages.get().getBundle().key(
                                    Messages.LOG_WRITE_FORM_FIELDS_3,
                                    column,
                                    value,
                                    m_paramEntryid));
                            }
                        }
                    } catch (Exception e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(Messages.get().getBundle().key(
                                Messages.ERR_WRITE_FORM_FIELDS_3,
                                column,
                                value,
                                m_paramEntryid));
                        }
                        errors.add(new CmsException(Messages.get().container(
                            Messages.ERR_WRITE_FORM_FIELDS_3,
                            column,
                            value,
                            m_paramEntryid)));
                    }

                }
            }
        } catch (Exception ex) {
            errors.add(ex);
        }
        // set the list of errors to display when saving failed
        setCommitErrors(errors);

    }

    /**
     * Returns the paramEntryid.<p>
     *
     * @return the paramEntryid
     */
    public String getParamEntryid() {

        return m_paramEntryid;
    }

    /**
     * Returns the formId.<p>
     *
     * @return the formId
     */
    public String getParamFormid() {

        return m_paramFormid;
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWorkplace#keyDefault(java.lang.String, java.lang.String)
     */
    public String keyDefault(String keyName, String defaultValue) {

        return getMessages().keyDefault(keyName, CmsStringUtil.escapeHtml(defaultValue));
    }

    /**
     * Sets the paramEntryid.<p>
     *
     * @param paramEntryid the paramEntryid to set
     */
    public void setParamEntryid(String paramEntryid) {

        m_paramEntryid = paramEntryid;
    }

    /**
     * Sets the formId.<p>
     *
     * @param formId the formId to set
     */
    public void setParamFormid(String formId) {

        if (formId == null) {
            formId = "";
        }
        m_paramFormid = formId;
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWidgetDialog#defineWidgets()
     */
    protected void defineWidgets() {

        if (!CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramEntryid)
            && !CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramFormid)) {

            try {
                m_formData = CmsFormDataAccess.getInstance().readOneFormData(m_paramEntryid);
                if (m_formData != null) {
                    addStaticWidgets();
                    addDynamicWidgets();
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().container(Messages.ERR_SHOW_EDIT_FORM_FIELDS_1, m_paramEntryid));
                }
            }

        }
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#getPageArray()
     */
    protected String[] getPageArray() {

        return PAGES;
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        super.initMessages();
    }

    /**
     * Creates the dynamic widgets for the current submitted data for each field.<p>
     * 
     * @throws Exception if something goes wrong
     */
    private void addDynamicWidgets() throws Exception {

        if (m_fields == null) {
            m_fields = new HashMap();
        }

        // get the list of all fields 
        List columnNames = CmsFormDataAccess.getInstance().readAllFormFieldNames(
            m_paramFormid,
            new Date(1),
            new Date(Long.MAX_VALUE));

        // for each column create a widget
        String column;
        String value;
        CmsFormDataEditBean edit;
        for (int i = 0; i < columnNames.size(); i++) {

            // get the entry and fill the columns
            column = (String)columnNames.get(i);
            value = m_formData.getFieldValue(column);
            edit = createEditEntry(value);
            addWidget(new CmsWidgetDialogParameter(edit, "value", column, "", PAGES[0], edit.getWidget(), 0, 1));
            m_fields.put(column, edit);
        }
    }

    /**
     * Creates the static widgets for the current submitted data.<p>
     * 
     * @throws Exception if something goes wrong
     */
    private void addStaticWidgets() throws Exception {

        // add the id widget
        CmsFormDataEditBean edit = new CmsFormDataEditBean(m_formData.getFormId(), null);
        addWidget(new CmsWidgetDialogParameter(
            edit,
            "value",
            key(Messages.GUI_COLUMN_FIELDS_ID_0),
            "",
            PAGES[0],
            new CmsDisplayWidget(),
            1,
            1));

        // add the created date widget
        edit = new CmsFormDataEditBean(Messages.get().getBundle().getDateTime(m_formData.getDateCreated()), null);
        addWidget(new CmsWidgetDialogParameter(
            edit,
            "value",
            key(Messages.GUI_COLUMN_FIELDS_DATE_0),
            "",
            PAGES[0],
            new CmsDisplayWidget(),
            1,
            1));

        // add the resource widget
        edit = new CmsFormDataEditBean(m_formData.getResourcePath(), null);
        addWidget(new CmsWidgetDialogParameter(
            edit,
            "value",
            key(Messages.GUI_COLUMN_FIELDS_RESOURCE_0),
            "",
            PAGES[0],
            new CmsDisplayWidget(),
            1,
            1));
    }

    /**
     * Compares the given values if they are identical.<p>
     * 
     * @param value1 the first string can also be <code>null</code>
     * @param value2 the second string can also be <code>null</code>
     * 
     * @return <code>true</code>if identical otherwise <code>false</code>
     */
    private boolean compareValues(Object value1, Object value2) {

        boolean result = false;
        if (value1 == null && value2 == null) {
            return !result;
        }
        if (value1 != null && value1.equals(value2)) {
            return !result;
        }
        return result;
    }

    /**
     * Creates the Objects to edit the dynamic columns.<p>
     * 
     * @param value the current value
     * 
     * @return the Object contains the current value and the widget to edit
     */
    private CmsFormDataEditBean createEditEntry(String value) {

        I_CmsWidget widget;

        if (isTextareaWidget(value)) {
            widget = new CmsTextareaWidget(5);
        } else {
            widget = new CmsInputWidget();
        }
        return new CmsFormDataEditBean(value, widget);
    }

    /**
     * Checks if the given String value is needed a textarea or a normal input field.<p>
     * 
     * @param value the value to check if a textarea widget needed
     * 
     * @return <code>true</code>if a textarea widget is needed otherwise <code>false</code>
     */
    private boolean isTextareaWidget(String value) {

        boolean result = false;

        if (value != null) {

            String escape = CmsStringUtil.escapeHtml(value);
            boolean length = (value.length() > STRING_TRIM_SIZE);
            length &= escape.matches(".*(<.{1,5}>|[ \\t\\n\\x0B\\f\\r]).*");

            Pattern pat = Pattern.compile(".*(<.{1,5}>|[\\t\\n\\x0B\\f\\r]).*", Pattern.DOTALL);
            Matcher match = pat.matcher(escape);
            result = (length || match.matches());
        }
        return result;
    }

}