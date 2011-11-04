/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsSelectFieldWidget.java,v $
 * Date   : $Date: 2010/05/21 13:49:18 $
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

import org.opencms.file.CmsObject;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsSelectWidget;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Special select widget that generates options from the configured form input fields.<p>
 * 
 * The configuration contains <code>key=value</code> pairs, separated by a pipe <code>|</code>:
 * <ul>
 * <li><b>convertfieldindex</b>: if <code>true</code>, a found old field value which is a number is tried to be converted to the field label.</li>
 * <li><b>fieldtype</b>: the type of input fields that should be shown as select options.</li>
 * <li><b>referencebylabel</b>: if <code>true</code>, the field label is stored to point to the field, otherwise the xPath.</li>
 * </ul>
 * 
 * @author Andreas Zahner
 */
public class CmsSelectFieldWidget extends CmsSelectWidget {

    /** Configuration option key: convert the found field index. */
    protected static final String CONFIGURATION_CONVERTFIELDINDEX = "convertfieldindex";

    /** Configuration option key: field type. */
    protected static final String CONFIGURATION_FIELDTYPE = "fieldtype";

    /** Configuration option key: reference input field by label. */
    protected static final String CONFIGURATION_REFERENCEBYLABEL = "referencebylabel";

    /** Indicates to convert the found field index.  */
    private boolean m_convertFieldIndex;

    /** The field type to show as select options. */
    private String m_fieldType;

    /** Indicates to reference the input field by label. */
    private boolean m_referenceByLabel;

    /**
     * Creates a new select widget.<p>
     */
    public CmsSelectFieldWidget() {

        // empty constructor is required for class registration
        super();
    }

    /**
     * Creates a select widget with the specified select options.<p>
     * 
     * @param configuration the configuration (possible options) for the select box
     */
    public CmsSelectFieldWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    @Override
    public I_CmsWidget newInstance() {

        return new CmsSelectFieldWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#getSelectedValue(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    protected String getSelectedValue(CmsObject cms, I_CmsWidgetParameter param) {

        if (isConvertFieldIndex()) {
            try {
                Integer.parseInt(param.getStringValue(cms));
                param.setStringValue(cms, null);
            } catch (Exception e) {
                // failed, just proceed
            }
        }
        return super.getSelectedValue(cms, param);
    }

    /**
     * Returns the select options for the widget, generated from the configured input fields of the XML content.<p>
     * 
     * @see org.opencms.widgets.A_CmsSelectWidget#parseSelectOptions(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    protected List<CmsSelectWidgetOption> parseSelectOptions(
        CmsObject cms,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        // parse widget configuration
        parseConfiguration(cms, widgetDialog);

        // only create options if not already done
        if (getSelectOptions() == null) {

            List<CmsSelectWidgetOption> result = new ArrayList<CmsSelectWidgetOption>();

            int fieldIndex = -1;
            // field index conversion is switched on, try to parse index
            if (isConvertFieldIndex()) {
                try {
                    fieldIndex = Integer.parseInt(param.getStringValue(cms));
                } catch (Exception e) {
                    // failed to parse value, proceed
                }
            }

            // cast param to I_CmsXmlContentValue
            I_CmsXmlContentValue contentValue = (I_CmsXmlContentValue)param;
            Locale locale = contentValue.getLocale();
            CmsXmlContent content = (CmsXmlContent)contentValue.getDocument();
            List<I_CmsXmlContentValue> fieldValues = CmsFormContentUtil.getContentValues(
                content,
                CmsForm.NODE_INPUTFIELD,
                locale);
            int fieldValueSize = fieldValues.size();
            for (int i = 0; i < fieldValueSize; i++) {
                I_CmsXmlContentValue inputField = fieldValues.get(i);
                // get the xPath as value
                String value = inputField.getPath();
                // check the field type if configured
                if (CmsStringUtil.isNotEmpty(getFieldType())) {
                    String type = content.getStringValue(cms, value + "/" + CmsForm.NODE_FIELDTYPE, locale);
                    if (!getFieldType().equals(type)) {
                        // not the defined field type, skip field
                        continue;
                    }
                }
                // get the field label
                String label = content.getStringValue(cms, value + "/" + CmsForm.NODE_FIELDLABEL, locale);
                label = CmsForm.getConfigurationValue(label, "");
                if (isReferenceByLabel()) {
                    // reference by label, check label
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(label)) {
                        // skip fields without label text
                        continue;
                    }
                    // set the value to the label
                    value = label;
                }
                if (CmsStringUtil.isEmpty(label)) {
                    label = value;
                }
                // remove DB label if present
                int pos = label.indexOf('|');
                if (pos > -1) {
                    if (isReferenceByLabel() && (pos + 1 < label.length())) {
                        // only use DB label for reference
                        value = label.substring(pos + 1);
                    }
                    label = label.substring(0, pos);
                }
                boolean isDefault = false;
                // check if this field should be marked as default because of index conversion
                if ((fieldIndex > 0) && (fieldIndex == i + 1)) {
                    isDefault = true;
                }
                result.add(new CmsSelectWidgetOption(value, isDefault, label));
            }
            setSelectOptions(result);
        }
        return getSelectOptions();
    }

    /**
     * Returns the field type to show as select options.<p>
     * 
     * @return the field type to show as select options
     */
    private String getFieldType() {

        return m_fieldType;
    }

    /**
     * Returns if the found field index should be converted to a field label when opening the form in an editor.<p>
     * 
     * @return <code>true</code> if the found field index should be converted to a field label, otherwise <code>false</code>
     */
    private boolean isConvertFieldIndex() {

        return m_convertFieldIndex;
    }

    /**
     * Returns if an input field is referenced by its label.<p>
     * 
     * @return <code>true</code> if an input field is referenced by its label, otherwise <code>false</code>
     */
    private boolean isReferenceByLabel() {

        return m_referenceByLabel;
    }

    /**
     * Parses the widget configuration string.<p>
     * 
     * @param cms the current users OpenCms context
     * @param widgetDialog the dialog of this widget
     */
    private void parseConfiguration(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        String configString = CmsMacroResolver.resolveMacros(getConfiguration(), cms, widgetDialog.getMessages());
        Map<String, String> config = CmsStringUtil.splitAsMap(configString, "|", "=");
        m_referenceByLabel = Boolean.valueOf(config.get(CONFIGURATION_REFERENCEBYLABEL)).booleanValue();
        m_convertFieldIndex = Boolean.valueOf(config.get(CONFIGURATION_CONVERTFIELDINDEX)).booleanValue();
        m_fieldType = config.get(CONFIGURATION_FIELDTYPE);
    }
}
