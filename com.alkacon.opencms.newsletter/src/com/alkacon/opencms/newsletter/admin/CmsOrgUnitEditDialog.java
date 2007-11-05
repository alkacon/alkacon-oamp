/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsOrgUnitEditDialog.java,v $
 * Date   : $Date: 2007/11/05 14:03:01 $
 * Version: $Revision: 1.5 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsTextareaWidget;
import org.opencms.workplace.CmsWidgetDialogParameter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Newsletter organizational unit create dialog.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.5 $ 
 * 
 * @since 7.0.3 
 */
public class CmsOrgUnitEditDialog extends org.opencms.workplace.tools.accounts.CmsOrgUnitEditDialog {

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsOrgUnitEditDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsOrgUnitEditDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#actionCommit()
     */
    public void actionCommit() {

        List errors = new ArrayList();

        try {
            // create new organizational unit
            if (m_orgunit == null) {
                if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_orgUnitBean.getDescription())) {
                    // description must not be empty
                    throw new CmsException(Messages.get().container(Messages.EXC_NEWSLETTER_OU_NO_DESCRIPTION_0));
                }
                m_orgUnitBean.setFqn(m_orgUnitBean.getParentOu() + CmsNewsletterManager.NEWSLETTER_OU_SIMPLENAME);
                List resources = m_orgUnitBean.getResources();
                // create the newsletter OU
                OpenCms.getOrgUnitManager().createOrganizationalUnit(
                    getCms(),
                    m_orgUnitBean.getFqn(),
                    m_orgUnitBean.getDescription(),
                    CmsOrganizationalUnit.FLAG_HIDE + CmsOrganizationalUnit.FLAG_NO_DEFAULTS,
                    (String)resources.get(0));
            }
        } catch (Throwable t) {
            errors.add(t);
        }

        // set the list of errors to display when saving failed
        setCommitErrors(errors);
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#createDialogHtml(java.lang.String)
     */
    protected String createDialogHtml(String dialog) {

        StringBuffer result = new StringBuffer(1024);

        result.append(createWidgetTableStart());
        // show error header once if there were validation errors
        result.append(createWidgetErrorHeader());

        if (dialog.equals(PAGES[0])) {
            // create the widgets for the first dialog page
            result.append(dialogBlockStart(key(Messages.GUI_NEWSLETTER_ORGUNIT_EDITOR_LABEL_IDENTIFICATION_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(0, 0));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
        }

        result.append(createWidgetTableEnd());
        return result.toString();
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#defineWidgets()
     */
    protected void defineWidgets() {

        initOrgUnitObject();
        setKeyPrefix("newsletterorgunit");

        // widgets to display
        addWidget(new CmsWidgetDialogParameter(m_orgUnitBean, "description", PAGES[0], new CmsTextareaWidget()));
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
     * @see org.opencms.workplace.tools.accounts.CmsOrgUnitEditDialog#initOrgUnitObject()
     */
    protected void initOrgUnitObject() {

        // TODO: Auto-generated method stub
        super.initOrgUnitObject();
        m_orgUnitBean.setFqn(m_orgUnitBean.getParentOu() + CmsNewsletterManager.NEWSLETTER_OU_SIMPLENAME);
    }

    /**
     * Checks if the new organizational unit dialog has to be displayed.<p>
     * 
     * @return <code>true</code> if the new organizational unit dialog has to be displayed
     */
    protected boolean isNewOrgUnit() {

        return getCurrentToolPath().equals("/accounts/orgunit/mgmt/newnewsletter");
    }

}
