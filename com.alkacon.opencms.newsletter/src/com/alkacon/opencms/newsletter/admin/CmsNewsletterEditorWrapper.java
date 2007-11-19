/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNewsletterEditorWrapper.java,v $
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

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.workplace.CmsDialog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 * Editor wrapper for editing newsletter resources in the administration view.<p>
 * 
 * Sets the <code>backlink</code> parameter when opening the editor containing the organizational unit information.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3  
 */
public class CmsNewsletterEditorWrapper extends CmsDialog {

    /** The backlink request parameter. */
    private String m_paramBacklink;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsNewsletterEditorWrapper(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsNewsletterEditorWrapper(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * Performs the dialog actions depending on the initialized action and displays the dialog form.<p>
     * 
     * @throws Exception if writing to the JSP out fails
     */
    public void displayDialog() throws Exception {

        initAdminTool();

        JspWriter out = getJsp().getJspContext().getOut();
        out.print(htmlStart());
        out.print(bodyStart(null));
        out.print("<form name='editor' method='post' target='_top' action='");
        out.print(getJsp().link("/system/workplace/editors/editor.jsp"));
        out.print("'>\n");
        out.print(paramsAsHidden());
        out.print("</form>\n");
        out.print("<script type='text/javascript'>\n");
        out.print("document.forms['editor'].submit();\n");
        out.print("</script>\n");
        out.print(bodyEnd());
        out.print(htmlEnd());
    }

    /**
     * Returns the backlink request parameter.<p>
     * 
     * @return the backlink request parameter
     */
    public String getParamBacklink() {

        return m_paramBacklink;
    }

    /**
     * Sets the backlink request parameter.<p>
     * 
     * @param paramBacklink the backlink request parameter
     */
    public void setParamBacklink(String paramBacklink) {

        m_paramBacklink = paramBacklink;
    }
}