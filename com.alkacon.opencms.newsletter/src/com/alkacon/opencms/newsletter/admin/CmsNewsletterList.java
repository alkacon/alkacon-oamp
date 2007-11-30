/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNewsletterList.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
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

package com.alkacon.opencms.newsletter.admin;

import org.opencms.db.CmsUserSettings;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.editors.CmsEditor;
import org.opencms.workplace.explorer.CmsExplorer;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.list.A_CmsListExplorerDialog;
import org.opencms.workplace.list.CmsListExplorerColumn;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListOrderEnum;
import org.opencms.workplace.list.I_CmsListResourceCollector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Mailing list management view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.6 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterList extends A_CmsListExplorerDialog {

    /** List column id constant. */
    public static final String LIST_COLUMN_SCORE = "cs";

    /** list item detail id constant. */
    public static final String LIST_DETAIL_EXCERPT = "de";

    /** list id constant. */
    public static final String LIST_ID = "anl";

    /** Path to the list buttons. */
    public static final String PATH_BUTTONS = "tools/newsletter/buttons/";

    /** The internal collector instance. */
    private I_CmsListResourceCollector m_collector;

    /** Stores the value of the request parameter for the organizational unit fqn. */
    private String m_paramOufqn;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsNewsletterList(CmsJspActionElement jsp) {

        super(
            jsp,
            LIST_ID,
            Messages.get().container(Messages.GUI_NEWSLETTER_LIST_NAME_0),
            A_CmsListExplorerDialog.LIST_COLUMN_NAME,
            CmsListOrderEnum.ORDER_ASCENDING,
            null);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsNewsletterList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListMultiActions()
     */
    public void executeListMultiActions() {

        throwListUnsupportedActionException();
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
     */
    public void executeListSingleActions() throws IOException, ServletException {

        if (getParamListAction().equals(LIST_ACTION_EDIT)) {
            // forward to the editor
            Map params = new HashMap();
            params.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);
            String link = CmsWorkplace.VFS_PATH_VIEWS + "workplace.jsp?oufqn=" + getParamOufqn();
            params.put(CmsEditor.PARAM_BACKLINK, link);
            params.put("oufqn", getParamOufqn());
            params.put(CmsDialog.PARAM_RESOURCE, getSelectedItem().get(LIST_COLUMN_NAME));
            getToolManager().jspForwardPage(this, "/system/workplace/admin/newsletter/edit.jsp", params);
        } else {
            throwListUnsupportedActionException();
        }
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListExplorerDialog#getCollector()
     */
    public I_CmsListResourceCollector getCollector() {

        if (m_collector == null) {
            m_collector = new CmsNewsletterResourcesCollector(this);

            // set the right resource util parameters
            CmsResourceUtil resUtil = getResourceUtil();
            resUtil.setAbbrevLength(50);
            resUtil.setSiteMode(CmsResourceUtil.SITE_MODE_MATCHING);
        }
        return m_collector;
    }

    /**
     * Returns the organizational unit fqn parameter value.<p>
     * 
     * @return the organizational unit fqn parameter value
     */
    public String getParamOufqn() {

        return m_paramOufqn;
    }

    /**
     * Sets the organizational unit fqn parameter value.<p>
     * 
     * @param ouFqn the organizational unit fqn parameter value
     */
    public void setParamOufqn(String ouFqn) {

        if (ouFqn == null) {
            ouFqn = "";
        }
        m_paramOufqn = ouFqn;
    }

    /**
     * Generates the dialog starting html code.<p>
     * 
     * @return html code
     */
    protected String defaultActionHtmlStart() {

        StringBuffer result = new StringBuffer(2048);
        result.append(htmlStart(null));
        result.append(getList().listJs());
        result.append(CmsListExplorerColumn.getExplorerStyleDef());
        result.append("<script language='JavaScript'>\n");
        result.append(new CmsExplorer(getJsp()).getInitializationHeader());
        result.append("\ntop.updateWindowStore();\n");
        result.append("top.displayHead(top.win.head, 0, 1);\n}\n");
        result.append("</script>");
        result.append(bodyStart("dialog", "onload='initialize();'"));
        result.append(dialogStart());
        result.append(dialogContentStart(getParamTitle()));
        return result.toString();
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#fillDetails(java.lang.String)
     */
    protected void fillDetails(String detailId) {

        // no details
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        // add default resource bundles
        super.initMessages();
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListExplorerDialog#setColumnVisibilities()
     */
    protected void setColumnVisibilities() {

        super.setColumnVisibilities();
        setColumnVisibility(LIST_COLUMN_EDIT.hashCode(), LIST_COLUMN_EDIT.hashCode());

        // set visibility of some columns to false, they are not required for the newsletter list
        setColumnVisibility(CmsUserSettings.FILELIST_TYPE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_SIZE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_PERMISSIONS, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_DATE_CREATED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_USER_CREATED, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_STATE, 0);
        setColumnVisibility(CmsUserSettings.FILELIST_LOCKEDBY, 0);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setMultiActions(CmsListMetadata metadata) {

        // no LMAs
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getParamOufqn())) {
            throw new Exception();
        }
    }
}
