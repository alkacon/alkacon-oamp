/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsEditSubscriberDialog.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.8 $
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

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsCheckboxWidget;
import org.opencms.widgets.CmsDisplayWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.workplace.CmsWidgetDialogParameter;
import org.opencms.workplace.tools.accounts.A_CmsEditUserDialog;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Dialog to edit new or existing subscriber in the administration view.<p>
 * 
 * @author Michael Moossen
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.8 $ 
 * 
 * @since 7.0.3 
 */
public class CmsEditSubscriberDialog extends A_CmsEditUserDialog {

    /** localized messages Keys prefix. */
    public static final String SB_KEY_PREFIX = "subscriber";

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsEditSubscriberDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsEditSubscriberDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#actionCommit()
     */
    public void actionCommit() {

        m_user.setName(getParamOufqn() + m_user.getEmail());
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_user.getFirstname())) {
            m_user.setFirstname("_");
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_user.getLastname())) {
            m_user.setLastname("_");
        }
        getPwdInfo().setNewPwd(CmsNewsletterManager.getPassword());
        getPwdInfo().setConfirmation(CmsNewsletterManager.getPassword());

        super.actionCommit();
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#createDialogHtml(java.lang.String)
     */
    protected String createDialogHtml(String dialog) {

        StringBuffer result = new StringBuffer(1024);

        result.append(createWidgetTableStart());
        // show error header once if there were validation errors
        result.append(createWidgetErrorHeader());

        if (dialog.equals(PAGES[0])) {
            // create the widgets for the first dialog page
            result.append(dialogBlockStart(key(org.opencms.workplace.tools.accounts.Messages.GUI_USER_EDITOR_LABEL_IDENTIFICATION_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(0, 3));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
        }

        result.append(createWidgetTableEnd());
        return result.toString();
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#createUser(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    protected CmsUser createUser(String name, String pwd, String desc, Map info) throws CmsException {

        return getCms().createUser(name, pwd, desc, info);
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#defineWidgets()
     */
    protected void defineWidgets() {

        // initialize the user object to use for the dialog
        initUserObject();

        setKeyPrefix(SB_KEY_PREFIX);

        // widgets to display
        if (isNewUser()) {
            addWidget(new CmsWidgetDialogParameter(m_user, "email", PAGES[0], new CmsInputWidget()));
        } else {
            addWidget(new CmsWidgetDialogParameter(m_user, "email", PAGES[0], new CmsDisplayWidget()));
        }
        addWidget(new CmsWidgetDialogParameter(m_user, "lastname", "", PAGES[0], new CmsInputWidget(), 0, 1));
        addWidget(new CmsWidgetDialogParameter(m_user, "firstname", "", PAGES[0], new CmsInputWidget(), 0, 1));
        addWidget(new CmsWidgetDialogParameter(m_user, "enabled", PAGES[0], new CmsCheckboxWidget()));
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#getListClass()
     */
    protected String getListClass() {

        return CmsSubscribersList.class.getName();
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#getListRootPath()
     */
    protected String getListRootPath() {

        return "/newsletter/orgunit/subscribers";
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
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#isEditable(CmsUser)
     */
    protected boolean isEditable(CmsUser user) {

        return true;
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        super.validateParamaters();
        // this is to prevent the switch to the root ou 
        // if the oufqn param get lost (by reloading for example)
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(getParamOufqn())) {
            throw new Exception();
        }
    }

    /**
     * @see org.opencms.workplace.tools.accounts.A_CmsEditUserDialog#writeUser(org.opencms.file.CmsUser)
     */
    protected void writeUser(CmsUser user) throws CmsException {

        getCms().writeUser(user);
    }
}