/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsOrgUnitsSubList.java,v $
 * Date   : $Date: 2007/11/19 15:49:15 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Package.  
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

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Deletes the newsletter OU from the current organizational unit.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 7.0.3 
 */
public class CmsOrgUnitsSubList extends org.opencms.workplace.tools.accounts.CmsOrgUnitsSubList {

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsOrgUnitsSubList(CmsJspActionElement jsp) {

        super(jsp);
        getList().setName(Messages.get().container(Messages.GUI_NEWSLETTER_ORGUNITS_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsOrgUnitsSubList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * Deletes the given organizational unit.<p>
     * 
     * @throws Exception if something goes wrong 
     */
    public void actionDelete() throws Exception {

        List childOus = OpenCms.getOrgUnitManager().getOrganizationalUnits(getCms(), getParamOufqn(), false);
        Iterator i = childOus.iterator();
        while (i.hasNext()) {
            CmsOrganizationalUnit unit = (CmsOrganizationalUnit)i.next();
            if (unit.getSimpleName().startsWith(CmsNewsletterManager.NEWSLETTER_OU_NAMEPREFIX)) {
                // found a newsletter OU, we can delete it
                OpenCms.getOrgUnitManager().deleteOrganizationalUnit(getCms(), unit.getName());
            }
        }
        actionCloseDialog();
    }

}
