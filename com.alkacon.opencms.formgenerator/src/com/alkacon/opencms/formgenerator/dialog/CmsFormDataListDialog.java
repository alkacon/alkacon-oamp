/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormDataListDialog.java,v $
 * Date   : $Date: 2009/04/17 07:28:35 $
 * Version: $Revision: 1.6 $
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

package com.alkacon.opencms.formgenerator.dialog;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;

import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListCsvExportIAction;
import org.opencms.workplace.list.CmsListDateMacroFormatter;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListMultiAction;
import org.opencms.workplace.list.CmsListOrderEnum;
import org.opencms.workplace.list.CmsListSearchAction;
import org.opencms.workplace.tools.CmsToolDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Provides a dialog with dynamic and static columns to show the details of a form.<p> 
 * 
 * The dynamic columns are the configured fields of the current form.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4
 */
public class CmsFormDataListDialog extends A_CmsListDialog {

    /** the param with the entry id.*/
    public static final String PARAM_ENTRY_ID = "entryid";

    /** the length to cut the string text.*/
    public static final int STRING_TRIM_SIZE = 50;

    /** List column id constant. */
    private static final String LIST_ACTION_DELETE = "acd";

    /** List column id constant. */
    private static final String LIST_ACTION_EDIT = "aced";

    /** List column id constant. */
    private static final String LIST_ACTION_EDIT_DATE = "aced_date";

    /** List column id constant. */
    private static final String LIST_ACTION_EDIT_ID = "aced_id";

    /** List column id constant. */
    private static final String LIST_COLUMN_DATE = "coa";

    /** List column id constant. */
    private static final String LIST_COLUMN_DELETE = "cod";

    /** List column id constant. */
    private static final String LIST_COLUMN_EDIT = "coed";

    /** List column id constant. */
    private static final String LIST_COLUMN_ID = "coi";

    /** List column id constant. */
    private static final String LIST_COLUMN_RESOURCE = "cor";

    /** List id constant. */
    private static final String LIST_ID = "lsform2";

    /** List column id constant. */
    private static final String LIST_MACTION_DELETE = "mad";

    /** the path to export a form with all submitted data. **/
    private static final String LIST_PATH_EXPORT = "/system/modules/com.alkacon.opencms.formgenerator/elements/formexport.jsp";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormDataListDialog.class);

    /** Contains the path of the show dialog. */
    private static final String WORKPLACE_PATH_FORM = "/edit";

    /** Contains the id of the current form.**/
    protected String m_paramFormid;

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsFormDataListDialog(CmsJspActionElement jsp) {

        super(
            jsp,
            LIST_ID,
            Messages.get().container(Messages.GUI_FIELD_LIST_NAME_0),
            LIST_COLUMN_DATE,
            CmsListOrderEnum.ORDER_ASCENDING,
            null);
        if (getList() != null) {
            getList().getMetadata().setVolatile(true);
        }
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsFormDataListDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListMultiActions()
     */
    public void executeListMultiActions() throws CmsRuntimeException {

        if (getParamListAction().equals(LIST_MACTION_DELETE)) {
            try {
                Iterator itItems = getSelectedItems().iterator();
                CmsListItem listItem;
                while (itItems.hasNext()) {
                    listItem = (CmsListItem)itItems.next();
                    CmsFormDataAccess.getInstance().deleteForm(Integer.parseInt(listItem.getId()));
                }
            } catch (Exception e) {
                throw new CmsRuntimeException(Messages.get().container(Messages.ERR_DELETE_SELECTED_FORM_0), e);
            }
        } else {
            throwListUnsupportedActionException();
        }

    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() throws CmsRuntimeException {

        CmsListItem item = getSelectedItem();
        if (item != null) {

            if (LIST_ACTION_DELETE.equals(getParamListAction())) {
                try {
                    CmsFormDataAccess.getInstance().deleteForm(Integer.parseInt(item.getId()));
                } catch (Exception e) {
                    throw new CmsRuntimeException(Messages.get().container(
                        Messages.ERR_DELETE_SELECTED_FORM_1,
                        item.getId()), e);
                }
            } else if (LIST_ACTION_EDIT.equals(getParamListAction())
                || LIST_ACTION_EDIT_DATE.equals(getParamListAction())
                || LIST_ACTION_EDIT_ID.equals(getParamListAction())) {
                try {
                    Map params = new HashMap();

                    // set style to display report in correct layout
                    params.put(PARAM_STYLE, CmsToolDialog.STYLE_NEW);
                    params.put(CmsFormListDialog.PARAM_FORM_ID, m_paramFormid);
                    params.put(PARAM_ENTRY_ID, item.getId());
                    params.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);

                    // redirect to the report output JSP
                    getToolManager().jspForwardTool(this, getCurrentToolPath() + WORKPLACE_PATH_FORM, params);

                } catch (Exception e) {
                    throw new CmsRuntimeException(Messages.get().container(
                        Messages.ERR_SHOW_SELECTED_FIELDS_1,
                        getSelectedItem().getId()), e);
                }

            } else {
                throwListUnsupportedActionException();
            }
        }

        listSave();
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
     * @see org.opencms.workplace.list.A_CmsListDialog#fillDetails(java.lang.String)
     */
    protected void fillDetails(String detailId) {

        // NOOP
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#getListItems()
     */
    protected List getListItems() {

        List result = new ArrayList();
        try {
            // get the data of the form fields
            CmsListMetadata meta = getList().getMetadata();
            if (meta.getColumnDefinitions().size() > 0) {
                List entries = CmsFormDataAccess.getInstance().readForms(m_paramFormid, 0, Long.MAX_VALUE);

                for (int i = 0; i < entries.size(); i++) {
                    // get the entry and fill the columns
                    CmsFormDataBean data = (CmsFormDataBean)entries.get(i);
                    CmsListItem item = new CmsListItem(meta, "" + data.getEntryId());

                    // set the static columns
                    item.set(LIST_COLUMN_ID, data.getFormId());
                    String path;
                    try {
                        path = getCms().readResource(data.getResourceId()).getRootPath();
                    } catch (Exception e) {
                        path = data.getResourceId().toString();
                    }
                    item.set(LIST_COLUMN_RESOURCE, path);
                    item.set(LIST_COLUMN_DATE, new Date(data.getDateCreated()));

                    // fill the dynamic columns
                    Iterator iterator = data.getAllFields().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        /*
                         * skip empty values: this is a hardening needed for previous versions of webform where 
                         * CmsEmptyField was stored in db with empty key and value. If not skipped list API will 
                         * throw an exception and the list will remain empty.  
                         */
                        String key = (String)entry.getKey();
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(key)) {
                            Object value = entry.getValue();
                            if ((value != null) && (value instanceof String)) {
                                value = CmsStringUtil.escapeHtml((String)value);
                                value = CmsStringUtil.trimToSize((String)value, STRING_TRIM_SIZE, " ...");
                            }
                            item.set(key, value);
                        }
                    }
                    result.add(item);

                }
            }

        } catch (CmsIllegalArgumentException e) {
            /* 
             * This exception is only thrown, when the dynamically generated
             * columns are to old. After this exception the form is refreshed 
             * with the new dynamically generated columns.<p>
            */
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.get().getBundle().key(Messages.ERR_READ_FORM_VALUES_1, m_paramFormid));
            }
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.ERR_READ_FORM_VALUES_1, m_paramFormid));
            }
        }

        return result;
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        String[] form = (String[])getParameterMap().get(CmsFormListDialog.PARAM_FORM_ID);

        if ((form != null) && (form.length > 0)) {
            m_paramFormid = form[0];

            // add the edit action
            CmsListColumnDefinition editCol = getColumnEdit();
            metadata.addColumn(editCol);
            editCol.setPrintable(false);

            // add the delete action
            CmsListColumnDefinition delCol = getColumnDelete();
            metadata.addColumn(delCol);
            delCol.setPrintable(false);

            // add the id column
            CmsListColumnDefinition idCol = new CmsListColumnDefinition(LIST_COLUMN_ID);
            idCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_ID_0));
            idCol.setWidth("30");
            CmsListDefaultAction idAction = new CmsListDefaultAction(LIST_ACTION_EDIT_ID);
            idAction.setName(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_0));
            idAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_HELP_0));
            idCol.addDefaultAction(idAction);
            metadata.addColumn(idCol);

            // add the date column
            CmsListColumnDefinition dateCol = new CmsListColumnDefinition(LIST_COLUMN_DATE);
            dateCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_DATE_0));
            dateCol.setFormatter(new CmsListDateMacroFormatter(Messages.get().container(
                Messages.GUI_COLUMN_FIELDS_DATE_FORMAT_1), Messages.get().container(
                org.opencms.workplace.list.Messages.GUI_LIST_DATE_FORMAT_NEVER_0)));
            dateCol.setWidth("50");
            CmsListDefaultAction dateAction = new CmsListDefaultAction(LIST_ACTION_EDIT_DATE);
            dateAction.setName(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_0));
            dateAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_HELP_0));
            dateCol.addDefaultAction(dateAction);
            metadata.addColumn(dateCol);

            try {
                // get the list of all fields 
                List columnNames = CmsFormDataAccess.getInstance().readFormFieldNames(m_paramFormid, 0, Long.MAX_VALUE);

                // create the search action
                CmsListSearchAction searchAction = new CmsListSearchAction(idCol);
                searchAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_SEARCH_HELP_0));

                for (int i = 0; i < columnNames.size(); i++) {
                    // add column for the form name
                    String name = (String)columnNames.get(i);
                    /*
                     * skip empty values: this is a hardening needed for previous versions of webform where 
                     * CmsEmptyField was stored in db with empty key and value. If not skipped list API will 
                     * throw an exception and the list will remain empty.  
                     */
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(name)) {
                        CmsListColumnDefinition nameCol = new CmsListColumnDefinition(name);
                        nameCol.setName(new CmsMessageContainer(null, CmsStringUtil.escapeHtml(name)));
                        nameCol.setWidth("*");
                        metadata.addColumn(nameCol);

                        // add the new column to the search action
                        searchAction.addColumn(nameCol);
                    }
                }

                // add the search action
                metadata.setSearchAction(searchAction);

            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(Messages.ERR_READ_FORM_FIELDS_1, m_paramFormid));
                }
            }

            // add the path column
            CmsListColumnDefinition resCol = new CmsListColumnDefinition(LIST_COLUMN_RESOURCE);
            resCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_RESOURCE_0));
            resCol.setWidth("*");
            metadata.addColumn(resCol);

        }

    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        metadata.addIndependentAction(new CmsListCsvExportIAction() {

            /**
             * @see org.opencms.workplace.list.A_CmsListIndependentJsAction#jsCode(CmsWorkplace)
             */
            public String jsCode(CmsWorkplace wp) {

                StringBuffer url = new StringBuffer();
                url.append(LIST_PATH_EXPORT).append("?");
                url.append(CmsFormListDialog.PARAM_FORM_ID).append("=").append(m_paramFormid);
                String jsurl = OpenCms.getLinkManager().substituteLink(wp.getCms(), url.toString());
                String windowname = "cvsexport";
                String opts = "toolbar=no,location=no,directories=no,status=yes,menubar=0,scrollbars=yes,resizable=yes,top=150,left=660,width=450,height=450";
                StringBuffer js = new StringBuffer(512);
                js.append("window.open('");
                js.append(jsurl);
                js.append("', '");
                js.append(windowname);
                js.append("', '");
                js.append(opts);
                js.append("');");
                return js.toString();
            }

        });
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // add the delete multi action
        CmsListMultiAction deleteCol = new CmsListMultiAction(LIST_MACTION_DELETE);
        deleteCol.setName(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_0));
        deleteCol.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_HELP_0));
        deleteCol.setConfirmationMessage(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_CONF_0));
        deleteCol.setIconPath(ICON_DELETE);
        metadata.addMultiAction(deleteCol);

    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_paramFormid)) {
            throw new Exception();
        }
    }

    /**
     * Returns the delete column with action to delete a form entry.<p>
     * 
     * @return the column definition with the delete action
     */
    private CmsListColumnDefinition getColumnDelete() {

        // create column for deletion
        CmsListColumnDefinition deleteCol = new CmsListColumnDefinition(LIST_COLUMN_DELETE);
        deleteCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_DELETE_0));
        deleteCol.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_HELP_0));
        deleteCol.setWidth("20");
        deleteCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
        deleteCol.setSorteable(false);

        // add delete action 
        CmsListDirectAction deleteAction = new CmsListDirectAction(LIST_ACTION_DELETE);
        deleteAction.setName(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_0));
        deleteAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_HELP_0));
        deleteAction.setConfirmationMessage(Messages.get().container(Messages.GUI_ACTION_FIELDS_DELETE_CONF_0));
        deleteAction.setIconPath(ICON_DELETE);
        deleteCol.addDirectAction(deleteAction);
        return deleteCol;
    }

    /**
     * Returns the edit column with action to edit a form entry.<p>
     * 
     * @return the column definition with the delete action
     */
    private CmsListColumnDefinition getColumnEdit() {

        // create column for editing
        CmsListColumnDefinition editCol = new CmsListColumnDefinition(LIST_COLUMN_EDIT);
        editCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_EDIT_0));
        editCol.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_HELP_0));
        editCol.setWidth("20");
        editCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
        editCol.setSorteable(false);

        // add edit action 
        CmsListDirectAction deleteAction = new CmsListDirectAction(LIST_ACTION_EDIT);
        deleteAction.setName(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_0));
        deleteAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FIELDS_EDIT_HELP_0));
        deleteAction.setIconPath(CmsFormListDialog.LIST_EDIT_BUTTON);
        editCol.addDirectAction(deleteAction);
        return editCol;
    }
}
