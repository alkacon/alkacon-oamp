/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/dialog/CmsFormFileWidget.java,v $
 * Date   : $Date: 2010/05/21 13:49:30 $
 * Version: $Revision: 1.1 $
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

package com.alkacon.opencms.v8.formgenerator.dialog;

import com.alkacon.opencms.v8.formgenerator.CmsForm;

import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsDisplayWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;

/**
 * Provides a widget to view files uploaded by a web form, for use on a widget dialog.<p>
 *
 * @author Andreas Zahner
 */
public class CmsFormFileWidget extends CmsDisplayWidget {

    /**
     * Creates a new input widget.<p>
     */
    public CmsFormFileWidget() {

        // empty constructor is required for class registration
        this("");
    }

    /**
     * Creates a new input widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsFormFileWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    public String getDialogWidget(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        String value = param.getStringValue(cms);
        String id = param.getId();

        // create the link to the download JSP
        StringBuffer link = new StringBuffer(64);
        link.append("<a href=\"");
        // determine upload target: RFS (default) or VFS
        CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
        String vfsUpload = module.getParameter(CmsForm.MODULE_PARAM_UPLOADVFS, CmsStringUtil.FALSE);
        String triggerJSP = "/system/workplace/admin/database/v8-formgenerator/v8-downloadTrigger.jsp";
        if (Boolean.valueOf(vfsUpload).booleanValue()) {
            // VFS folder: use workplace JSP to show resource
            triggerJSP = "/system/workplace/commons/displayresource.jsp";
        }
        link.append(OpenCms.getLinkManager().substituteLink(cms, triggerJSP));

        link.append("?").append(CmsDialog.PARAM_RESOURCE).append("=");
        link.append(CmsEncoder.escapeXml(value));
        link.append("\" target=\"_blank\">");

        // create the widget HTML
        StringBuffer result = new StringBuffer(256);
        result.append("<td class=\"xmlTd\">");
        result.append(link);
        result.append("<img src=\"");
        result.append(CmsWorkplace.getSkinUri()).append("tools/database/buttons/v8-webform_download.png");
        result.append("\" width=\"16\" height=\"16\" alt=\"\" style=\"float: left; padding-right: 8px;\"/></a>");
        result.append("<span class=\"xmlInput textInput\" style=\"border: none;\">");
        result.append(link);
        result.append(value);
        result.append("</a></span>");
        result.append("<input type=\"hidden\"");
        result.append(" name=\"");
        result.append(id);
        result.append("\" id=\"");
        result.append(id);
        result.append("\" value=\"");
        result.append(CmsEncoder.escapeXml(value));
        result.append("\">");
        result.append("</td>");

        return result.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    @Override
    public I_CmsWidget newInstance() {

        return new CmsFormFileWidget(getConfiguration());
    }
}