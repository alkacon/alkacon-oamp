/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormDataListDialog.java,v $
 * Date   : $Date: 2008/03/18 11:34:09 $
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

import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDateMacroFormatter;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListMultiAction;
import org.opencms.workplace.list.CmsListOrderEnum;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Provides a dialog with dynamic and static columns to show the details of a form.<p> 
 * 
 * The dynamic columns are the configured fields of the current form.<p>
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 */
public class CmsFormDataListDialog extends A_CmsListDialog {

    /** List column id constant. */
    private static final String LIST_ACTION_DELETE = "acd";

    /** List column id constant. */
    private static final String LIST_COLUMN_DATE = "coa";

    /** List column id constant. */
    private static final String LIST_COLUMN_DELETE = "cod";

    /** List column id constant. */
    private static final String LIST_COLUMN_ID = "coi";

    /** List column id constant. */
    private static final String LIST_COLUMN_RESOURCE = "cor";

    /** List id constant. */
    private static final String LIST_ID = "lsform2";

    /** List column id constant. */
    private static final String LIST_MACTION_DELETE = "mad";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormDataListDialog.class);

    /** Contains the id of the current form.**/
    private String m_paramFormid;

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
        getList().getMetadata().setVolatile(true);

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
                    CmsFormDataAccess.getInstance().deleteFormEntries(listItem.getId());
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
        if (LIST_ACTION_DELETE.equals(getParamListAction()) && item != null) {
            try {
                CmsFormDataAccess.getInstance().deleteFormEntries(item.getId());
            } catch (Exception e) {
                throw new CmsRuntimeException(Messages.get().container(
                    Messages.ERR_DELETE_SELECTED_FORM_1,
                    item.getId()), e);
            }
        } else {
            throwListUnsupportedActionException();
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
            if (m_paramFormid != null) {

                // get the data of the form fields
                CmsListMetadata meta = getList().getMetadata();
                if (meta.getColumnDefinitions().size() > 0) {
                    List entries = CmsFormDataAccess.getInstance().readFormData(
                        m_paramFormid,
                        new Date(1),
                        new Date(Long.MAX_VALUE));

                    Entry entry;
                    CmsListItem item;
                    Iterator iterator;
                    CmsFormDataBean data;
                    for (int i = 0; i < entries.size(); i++) {

                        // get the entry and fill the columns
                        data = (CmsFormDataBean)entries.get(i);
                        item = new CmsListItem(meta, data.getFormId());

                        // set the static columns
                        item.set(LIST_COLUMN_ID, data.getFormId());
                        item.set(LIST_COLUMN_RESOURCE, data.getResourcePath());
                        item.set(LIST_COLUMN_DATE, new Date(data.getDateCreated()));

                        // fill the dynamic columns
                        iterator = data.getAllFields().iterator();
                        while (iterator.hasNext()) {
                            entry = (Entry)iterator.next();
                            item.set((String)entry.getKey(), entry.getValue());
                        }
                        result.add(item);

                    }
                }
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

        if (form != null && form.length > 0) {
            m_paramFormid = form[0];

            // add the delete action
            CmsListColumnDefinition delCol = getColumnDelete();
            metadata.addColumn(delCol);
            delCol.setPrintable(false);

            // add the id column
            CmsListColumnDefinition idCol = new CmsListColumnDefinition(LIST_COLUMN_ID);
            idCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_ID_0));
            idCol.setWidth("30");
            metadata.addColumn(idCol);

            // add the date column
            CmsListColumnDefinition dateCol = new CmsListColumnDefinition(LIST_COLUMN_DATE);
            dateCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_DATE_0));
            dateCol.setFormatter(new CmsListDateMacroFormatter(Messages.get().container(
                Messages.GUI_COLUMN_FIELDS_DATE_FORMAT_1), Messages.get().container(
                org.opencms.workplace.list.Messages.GUI_LIST_DATE_FORMAT_NEVER_0)));
            dateCol.setWidth("50");
            metadata.addColumn(dateCol);
            int width = 100;
            try {
                // get the list of all fields 
                List columnNames = CmsFormDataAccess.getInstance().readAllFormFieldNames(
                    m_paramFormid,
                    new Date(1),
                    new Date(Long.MAX_VALUE));

                String name;

                if (columnNames.size() > 0) {
                    width = 100 / (columnNames.size() + 1);
                }

                CmsListColumnDefinition nameCol;
                for (int i = 0; i < columnNames.size(); i++) {

                    // add column for the form name
                    name = (String)columnNames.get(i);
                    nameCol = new CmsListColumnDefinition(name);
                    nameCol.setName(new CmsMessageContainer(null, name));
                    nameCol.setWidth(width + "%");
                    metadata.addColumn(nameCol);
                }

            } catch (SQLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(Messages.ERR_READ_FORM_FIELDS_1, m_paramFormid));
                }
            }

            // add the path column
            CmsListColumnDefinition resCol = new CmsListColumnDefinition(LIST_COLUMN_RESOURCE);
            resCol.setName(Messages.get().container(Messages.GUI_COLUMN_FIELDS_RESOURCE_0));
            resCol.setWidth(width + "%");
            metadata.addColumn(resCol);

        }

    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        // NOOP
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

}
