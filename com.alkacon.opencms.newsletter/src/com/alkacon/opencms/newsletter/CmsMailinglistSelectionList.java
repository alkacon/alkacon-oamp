/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsMailinglistSelectionList.java,v $
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

package com.alkacon.opencms.newsletter;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.workplace.commons.CmsGroupSelectionList;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDefaultAction;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.I_CmsListDirectAction;
import org.opencms.workplace.tools.CmsToolMacroResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Mailing list selection dialog called from the mailing list widget.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision: 1.6 $ 
 * 
 * @since 7.0.3 
 */
public class CmsMailinglistSelectionList extends CmsGroupSelectionList {

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsMailinglistSelectionList(CmsJspActionElement jsp) {

        super(jsp);
        getList().setName(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_NAME_0));
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsMailinglistSelectionList(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.tools.CmsToolDialog#dialogTitle()
     */
    public String dialogTitle() {

        // build title
        StringBuffer html = new StringBuffer(512);
        html.append("<div class='screenTitle'>\n");
        html.append("\t<table width='100%' cellspacing='0'>\n");
        html.append("\t\t<tr>\n");
        html.append("\t\t\t<td>\n");
        html.append(Messages.get().getBundle(getLocale()).key(Messages.GUI_ALK_MAILINGLISTSELECTION_INTRO_TITLE_0));
        html.append("\n\t\t\t</td>");
        html.append("\t\t</tr>\n");
        html.append("\t</table>\n");
        html.append("</div>\n");
        return CmsToolMacroResolver.resolveMacros(html.toString(), this);
    }

    /**
     * Returns the mailing lists (groups) to show for selection.<p>
     * 
     * @return A list of mailing lists (group objects) 
     * 
     * @throws CmsException if womething goes wrong
     */
    protected List getGroups() throws CmsException {

        List ret = new ArrayList();
        Iterator i = CmsNewsletterManager.getOrgUnits(getCms()).iterator();
        while (i.hasNext()) {
            CmsOrganizationalUnit ou = (CmsOrganizationalUnit)i.next();
            ret.addAll(OpenCms.getOrgUnitManager().getGroups(getCms(), ou.getName(), false));
        }
        return ret;
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
     */
    protected void setColumns(CmsListMetadata metadata) {

        super.setColumns(metadata);
        // create column for icon display
        CmsListColumnDefinition iconCol = metadata.getColumnDefinition(LIST_COLUMN_ICON);
        iconCol.setName(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_COLS_ICON_0));
        iconCol.setHelpText(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_COLS_ICON_HELP_0));
        // set icon action
        I_CmsListDirectAction iconAction = iconCol.getDirectAction(LIST_ACTION_ICON);
        iconAction.setName(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_ICON_NAME_0));
        iconAction.setHelpText(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_ICON_HELP_0));
        iconAction.setIconPath("buttons/mailinglist.png");

        // create column for login
        CmsListColumnDefinition nameCol = metadata.getColumnDefinition(LIST_COLUMN_DISPLAY);
        nameCol.setName(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_COLS_NAME_0));
        CmsListDefaultAction selectAction = nameCol.getDefaultAction(LIST_ACTION_SELECT);
        selectAction.setName(Messages.get().container(Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_ACTION_SELECT_NAME_0));
        selectAction.setHelpText(Messages.get().container(
            Messages.GUI_ALK_MAILINGLISTSELECTION_LIST_ACTION_SELECT_HELP_0));
    }

}
