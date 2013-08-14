/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsReportCheckFieldsWidget.java,v $
 * Date   : $Date: 2010/05/21 13:49:17 $
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

package com.alkacon.opencms.v8.formgenerator;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.A_CmsWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.widgets.I_CmsADEWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.content.I_CmsXmlContentHandler.DisplayType;
import org.opencms.xml.types.A_CmsXmlContentValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides a widget to check the fields to show on the form report page, for use on a widget dialog.<p>
 *
 * @author Andreas Zahner 
 */
public class CmsReportCheckFieldsWidget extends A_CmsWidget implements I_CmsADEWidget {

    /** Separator for fields used in XML content value. */
    public static final char SEPARATOR_FIELDS = '|';

    /**
     * Creates a new form report fields widget.<p>
     */
    public CmsReportCheckFieldsWidget() {

        // empty constructor is required for class registration
        this("");
    }

    /**
     * Creates a new form report fields widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsReportCheckFieldsWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getConfiguration(org.opencms.file.CmsObject, org.opencms.xml.types.A_CmsXmlContentValue, org.opencms.i18n.CmsMessages, org.opencms.file.CmsResource, java.util.Locale)
     */
    public String getConfiguration(
        CmsObject cms,
        A_CmsXmlContentValue contentValue,
        CmsMessages messages,
        CmsResource resource,
        Locale contentLocale) {

        // no special configuration needed
        return "";
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getCssResourceLinks(org.opencms.file.CmsObject)
     */
    public List<String> getCssResourceLinks(CmsObject cms) {

        return null;
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getDefaultDisplayType()
     */
    public DisplayType getDefaultDisplayType() {

        return DisplayType.singleline;
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    public String getDialogWidget(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        StringBuffer result = new StringBuffer(256);

        // cast param to I_CmsXmlContentValue
        I_CmsXmlContentValue contentValue = (I_CmsXmlContentValue)param;
        Locale locale = contentValue.getLocale();

        // on initial call, all fields should be checked
        boolean allChecked = CmsStringUtil.isEmptyOrWhitespaceOnly(param.getStringValue(cms));
        List<String> checkedFields = new ArrayList<String>();
        if (!allChecked) {
            checkedFields = CmsStringUtil.splitAsList(param.getStringValue(cms), SEPARATOR_FIELDS);
        }
        CmsXmlContent content = (CmsXmlContent)contentValue.getDocument();
        String uri = content.getStringValue(cms, "URI", locale);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(uri)) {
            // no URI set, show error message
            result.append("<td class=\"xmlTdError\">");
            result.append(widgetDialog.getMessages().key(Messages.ERR_REPORT_NO_FORM_URI_0));
            result.append("</td>");
        } else {
            // URI is set, generate check boxes for input fields
            result.append("<td class=\"xmlTd\">");

            try {
                // get the web form file
                CmsFile file = cms.readFile(uri);
                content = CmsXmlContentFactory.unmarshal(cms, file);

                // read the defined input fields
                List<I_CmsXmlContentValue> fields = CmsFormContentUtil.getContentValues(
                    content,
                    CmsForm.NODE_INPUTFIELD,
                    locale);
                Iterator<I_CmsXmlContentValue> i = fields.iterator();
                while (i.hasNext()) {
                    I_CmsXmlContentValue fieldValue = i.next();

                    // get the field type
                    String type = content.getStringValue(
                        cms,
                        fieldValue.getPath() + "/" + CmsForm.NODE_FIELDTYPE,
                        locale);
                    if (CmsPagingField.getStaticType().equals(type) || CmsEmptyField.getStaticType().equals(type)) {
                        // this is a paging field, skip it
                        continue;
                    }

                    // get the field label
                    String label = content.getStringValue(
                        cms,
                        fieldValue.getPath() + "/" + CmsForm.NODE_FIELDLABEL,
                        locale);
                    label = CmsForm.getConfigurationValue(label, "");
                    String dbLabel = label;
                    // extract DB label if present
                    int pos = label.indexOf('|');
                    if ((pos > -1) && ((pos + 1) < label.length())) {
                        dbLabel = label.substring(pos + 1);
                        label = label.substring(0, pos);
                    }
                    result.append("<input type=\"checkbox\" name=\"");
                    result.append(param.getId());
                    result.append("\" value=\"").append(CmsEncoder.escapeXml(dbLabel)).append("\"");
                    if (allChecked || checkedFields.contains(dbLabel)) {
                        result.append(" checked=\"checked\"");
                    }
                    result.append("/>&nbsp;");
                    result.append(dbLabel);
                    if (!dbLabel.equals(label)) {
                        // show the label text behind the database label
                        result.append(" (").append(CmsEncoder.escapeXml(label)).append(")");
                    }
                    if (i.hasNext()) {
                        result.append("<br/>\n");
                    }
                }
            } catch (Exception e) {
                // error reading form
                result.append(widgetDialog.getMessages().key(Messages.ERR_REPORT_NO_FORM_URI_0));
            }

            result.append("</td>");
        }

        return result.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getInitCall()
     */
    public String getInitCall() {

        return null;
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getJavaScriptResourceLinks(org.opencms.file.CmsObject)
     */
    public List<String> getJavaScriptResourceLinks(CmsObject cms) {

        return null;
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getWidgetName()
     */
    public String getWidgetName() {

        // use the basic string widget for new content editor
        return CmsInputWidget.class.getName();
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#isInternal()
     */
    public boolean isInternal() {

        return true;
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    public I_CmsWidget newInstance() {

        return new CmsReportCheckFieldsWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#setEditorValue(org.opencms.file.CmsObject, java.util.Map, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    public void setEditorValue(
        CmsObject cms,
        Map<String, String[]> formParameters,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        String[] values = formParameters.get(param.getId());
        if ((values != null) && (values.length > 0)) {
            StringBuffer newValue = new StringBuffer(values.length * 8);
            // loop the found values
            for (int i = 0; i < values.length; i++) {
                newValue.append(values[i]);
                if (i < (values.length - 1)) {
                    newValue.append(SEPARATOR_FIELDS);
                }
            }

            // set the value
            param.setStringValue(cms, newValue.toString());

        } else {
            // set empty String as value
            param.setStringValue(cms, "");
        }
    }
}