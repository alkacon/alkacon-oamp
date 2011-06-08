/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormEditDialog.java,v $
 * Date   : $Date: 2011/06/08 13:35:02 $
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

package com.alkacon.opencms.formgenerator.dialog;

import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;

import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsPermissionSet;
import org.opencms.security.CmsRole;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsDisplayWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.widgets.CmsTextareaWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.workplace.CmsWidgetDialog;
import org.opencms.workplace.CmsWidgetDialogParameter;

import java.util.ArrayList;
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
 * @author Anja Roettgers 
 * 
 * @version $Revision: 1.8 $
 * 
 * @since 7.0.4 
 */
public class CmsFormEditDialog extends CmsWidgetDialog {

    /** The length to cut the string text.*/
    public static final int STRING_TRIM_SIZE = 200;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormEditDialog.class);

    /** Defines which pages are valid for this dialog. */
    private static final String[] PAGES = {"page1"};

    /** Localized messages keys prefix. */
    private static final String WEBFORM_KEY_PREFIX = "webform_prefix";

    /** Constant indicating that no upload folder was defined. */
    private static final String WEBFORM_UPLOADFOLDER_NONE = "none";

    /** a map with all fields and values. */
    private HashMap<String, CmsFormDataEditBean> m_fields;

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
    @Override
    public void actionCommit() {

        List<Exception> errors = new ArrayList<Exception>();
        try {
            // get the list of all fields 
            List<String> columnNames = CmsFormDataAccess.getInstance().readFormFieldNames(
                m_paramFormid,
                0,
                Long.MAX_VALUE);

            // for each field look if the value has changed and update the database
            String column = null;
            CmsFormDataEditBean data;
            String value = null;
            String orgValue;
            for (int i = 0; i < columnNames.size(); i++) {
                try {
                    // get for the field the old and new value
                    column = columnNames.get(i);
                    data = m_fields.get(column);
                    orgValue = m_formData.getFieldValue(column);
                    value = data.getValue();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(Messages.get().getBundle().key(
                            Messages.LOG_COMPARE_FORM_FIELDS_4,
                            new String[] {column, value, orgValue, m_paramEntryid}));
                    }

                    // compares the old and new value and update the database if not identical
                    if (!compareValues(orgValue, value) || ((value != null) && (value.trim().length() == 0))) {
                        CmsFormDataAccess.getInstance().updateFieldValue(
                            Integer.parseInt(m_paramEntryid),
                            column,
                            value);
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
    @Override
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
    @Override
    protected void defineWidgets() {

        try {
            setKeyPrefix(WEBFORM_KEY_PREFIX);
            addStaticWidgets();
            addDynamicWidgets();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().container(Messages.ERR_SHOW_EDIT_FORM_FIELDS_1, m_paramEntryid));
            }
        }
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#getPageArray()
     */
    @Override
    protected String[] getPageArray() {

        return PAGES;
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    @Override
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        super.initMessages();
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#validateParamaters()
     */
    @Override
    protected void validateParamaters() throws Exception {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramEntryid)
            || CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramFormid)) {
            throw new Exception();
        }
        m_formData = CmsFormDataAccess.getInstance().readForm(Integer.parseInt(m_paramEntryid));
    }

    /**
     * Creates the dynamic widgets for the current submitted data for each field.<p>
     * 
     * @throws Exception if something goes wrong
     */
    private void addDynamicWidgets() throws Exception {

        if (m_fields == null) {
            m_fields = new HashMap<String, CmsFormDataEditBean>();
        }

        // get the list of all fields 
        List<String> columnNames = CmsFormDataAccess.getInstance().readFormFieldNames(m_paramFormid, 0, Long.MAX_VALUE);

        // determine if the columns can be edited by the current user
        boolean editable;
        try {
            CmsResource formFile = getCms().readResource(m_formData.getResourceId());
            editable = OpenCms.getRoleManager().hasRole(getCms(), CmsRole.DATABASE_MANAGER)
                || getCms().hasPermissions(formFile, CmsPermissionSet.ACCESS_WRITE, false, CmsResourceFilter.ALL);
        } catch (CmsException e) {
            // error reading form resource, only check roles of current user
            editable = OpenCms.getRoleManager().hasRole(getCms(), CmsRole.DATABASE_MANAGER);
        }

        String uploadFolder = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME).getParameter(
            CmsForm.MODULE_PARAM_UPLOADFOLDER,
            WEBFORM_UPLOADFOLDER_NONE);

        // for each column create a widget
        String column;
        String value;
        CmsFormDataEditBean edit;
        for (int i = 0; i < columnNames.size(); i++) {

            // get the entry and fill the columns
            column = columnNames.get(i);
            value = m_formData.getFieldValue(column);
            if (CmsStringUtil.isEmpty(value)) {
                value = "";
            }
            edit = createEditEntry(value, uploadFolder, editable);
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
        String path;
        try {
            path = getCms().readResource(m_formData.getResourceId()).getRootPath();
        } catch (Exception e) {
            path = m_formData.getResourceId().toString();
        }
        edit = new CmsFormDataEditBean(path, null);
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
        if ((value1 == null) && (value2 == null)) {
            return !result;
        }
        if ((value1 != null) && value1.equals(value2)) {
            return !result;
        }
        return result;
    }

    /**
     * Creates the Objects to edit the dynamic columns.<p>
     * 
     * @param value the current value
     * @param uploadFolder the upload folder path
     * @param editable indicates if the entry can be edited by the current user
     * 
     * @return the Object contains the current value and the widget to edit
     */
    private CmsFormDataEditBean createEditEntry(String value, String uploadFolder, boolean editable) {

        I_CmsWidget widget;

        if (!uploadFolder.equals(WEBFORM_UPLOADFOLDER_NONE) && (value != null) && value.startsWith(uploadFolder)) {
            widget = new CmsFormFileWidget();
        } else if (!editable) {
            widget = new CmsDisplayWidget();
        } else if (isTextareaWidget(value)) {
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