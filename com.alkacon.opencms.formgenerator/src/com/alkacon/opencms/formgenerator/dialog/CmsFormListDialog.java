/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormListDialog.java,v $
 * Date   : $Date: 2009/04/17 07:28:54 $
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

package com.alkacon.opencms.formgenerator.dialog;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.tools.CmsToolDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Provides a dialog with a list of webforms which have the database transport activate.<p> 
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4
 */
public class CmsFormListDialog extends A_CmsListDialog {

    /** The path to the icon to edit. */
    public static final String LIST_EDIT_BUTTON = "tools/database/buttons/webform_edit.png";

    /** the param with the form id.*/
    public static final String PARAM_FORM_ID = "formid";

    /** List id constant. */
    private static final String LIST_ACTION_NAME = "ian";

    /** List id constant. */
    private static final String LIST_ACTION_SHOW = "ias";

    /** List column id constant. */
    private static final String LIST_COLUMN_FORM_COUNT = "ctc";

    /** List column id constant. */
    private static final String LIST_COLUMN_FORM_NAME = "ctn";

    /** List column id constant. */
    private static final String LIST_COLUMN_FORM_SHOW = "cts";

    /** List id constant. */
    private static final String LIST_ID = "lsform1";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormListDialog.class);

    /** Contains the path of the show dialog. */
    private static final String WORKPLACE_PATH_FORM = "/form";

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsFormListDialog(CmsJspActionElement jsp) {

        super(jsp, LIST_ID, Messages.get().container(Messages.GUI_FORM_LIST_NAME_0), null, null, null);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsFormListDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListMultiActions()
     */
    public void executeListMultiActions() throws CmsRuntimeException {

        // NOOP
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() throws CmsRuntimeException {

        if (LIST_ACTION_SHOW.equals(getParamListAction()) || LIST_ACTION_NAME.equals(getParamListAction())) {
            // add is clicked
            try {
                Map params = new HashMap();

                // set style to display report in correct layout
                params.put(PARAM_STYLE, CmsToolDialog.STYLE_NEW);
                params.put(PARAM_FORM_ID, getSelectedItem().getId());
                params.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);

                // redirect to the report output JSP
                getToolManager().jspForwardTool(this, getCurrentToolPath() + WORKPLACE_PATH_FORM, params);

            } catch (Exception e) {
                throw new CmsRuntimeException(Messages.get().container(
                    Messages.ERR_SHOW_SELECTED_FORM_1,
                    getSelectedItem().getId()), e);
            }
        } else {
            throwListUnsupportedActionException();
        }
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
            // read all form names
            CmsListMetadata meta = getList().getMetadata();
            Iterator it = CmsFormDataAccess.getInstance().countForms().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String name = (String)entry.getKey();
                CmsListItem item = new CmsListItem(meta, name);
                item.set(LIST_COLUMN_FORM_NAME, name);
                item.set(LIST_COLUMN_FORM_COUNT, entry.getValue());
                result.add(item);
            }
        } catch (Throwable e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.ERR_READ_FORM_0));
            }
        }

        return result;
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        super.initMessages();
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        // create column for show
        CmsListColumnDefinition showCol = new CmsListColumnDefinition(LIST_COLUMN_FORM_SHOW);
        showCol.setName(Messages.get().container(Messages.GUI_COLUMN_FORM_SHOW_0));
        showCol.setHelpText(Messages.get().container(Messages.GUI_ACTION_FORM_SHOW_HELP_0));
        showCol.setWidth("20");
        showCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
        showCol.setSorteable(false);

        // add show action 
        CmsListDirectAction showAction = new CmsListDirectAction(LIST_ACTION_SHOW);
        showAction.setName(Messages.get().container(Messages.GUI_COLUMN_FORM_SHOW_0));
        showAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FORM_SHOW_HELP_0));
        showAction.setIconPath(LIST_EDIT_BUTTON);
        showCol.addDirectAction(showAction);
        metadata.addColumn(showCol);
        showCol.setPrintable(false);

        // add column for the form name
        CmsListColumnDefinition nameCol = new CmsListColumnDefinition(LIST_COLUMN_FORM_NAME);
        nameCol.setName(Messages.get().container(Messages.GUI_COLUMN_FORM_NAME_0));
        nameCol.setWidth("80%");
        CmsListDefaultAction nameAction = new CmsListDefaultAction(LIST_ACTION_NAME);
        nameAction.setName(Messages.get().container(Messages.GUI_COLUMN_FORM_SHOW_0));
        nameAction.setHelpText(Messages.get().container(Messages.GUI_ACTION_FORM_SHOW_HELP_0));
        nameCol.addDefaultAction(nameAction);
        metadata.addColumn(nameCol);

        // add column for the count of the form name
        CmsListColumnDefinition countCol = new CmsListColumnDefinition(LIST_COLUMN_FORM_COUNT);
        countCol.setName(Messages.get().container(Messages.GUI_COLUMN_FORM_COUNT_0));
        countCol.setWidth("20%");
        metadata.addColumn(countCol);

    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setIndependentActions(CmsListMetadata metadata) {

        //NOOP
    }

    /**
     * 
     * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // NOOP
    }
}
