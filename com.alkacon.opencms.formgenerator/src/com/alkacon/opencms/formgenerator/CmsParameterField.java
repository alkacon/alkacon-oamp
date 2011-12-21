/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsEmptyField.java,v $
 * Date   : $Date: 2011/03/09 15:14:35 $
 * Version: $Revision: 1.6 $
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

package com.alkacon.opencms.formgenerator;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;

import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a empty field.<p>
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4 
 * 
 */
public class CmsParameterField extends A_CmsField {

    /** HTML field type: parameter field. */
    private static final String TYPE = "parameter";

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * Returns the configured form field values as hidden input fields.<p>
     * @param reqParams Map holding the http request parameters
     * @return the configured form field values as hidden input fields
     */
    public String requestParamsToHiddenFields(Map<String, String[]> reqParams) {

        // Write request parameters to hidden fields
        StringBuffer result = new StringBuffer(reqParams.size() * 8);
        Set<String> keys = reqParams.keySet();
        for (String key : keys) {
            String[] paramArray = reqParams.get(key);
            for (int i = 0; i < paramArray.length; i++) {
                result.append("<input type=\"hidden\" name=\"");
                result.append(CmsEncoder.escapeXml(key));
                result.append("\" value=\"");
                result.append(CmsEncoder.escapeXml(paramArray[i]));
                result.append("\" />\n");
            }
        }
        // return generated result list
        return result.toString();
    }

    /**
     * Returns the configured form field values as hidden input fields.<p>
     * @param reqParams Map holding the http request parameters
     * @param params Map holding the http request parameters
     * @return the configured form field values as hidden input fields
     */
    public static String createHiddenFields(Map<String, String[]> reqParams, Map<String, String> params) {

        // Write request parameters to hidden fields
        StringBuffer result = new StringBuffer(reqParams.size() * 8);
        Set<String> keys = params.keySet();

        //check if parameter(s) have been set in webform
        if (!params.isEmpty() && !(reqParams.isEmpty())) {
            //Iterate over parameters
            //            Set<String> keys = params.keySet();
            for (String key : keys) {
                String[] paramArray = reqParams.get(key);
                if (paramArray != null) {
                    for (int i = 0; i < paramArray.length; i++) {
                        result.append("<input type=\"hidden\" name=\"");
                        result.append(CmsEncoder.escapeXml(key));
                        result.append("\" value=\"");
                        result.append(CmsEncoder.escapeXml(paramArray[i]));
                        result.append("\" />\n");
                    }
                }
            }
        }

        // return generated result list
        return result.toString();
    }

    /**
     * @see com.alkacon.opencms.formgenerator.A_CmsField#validateConstraints()
     * @param formHandler Map holding the http request parameters
     * @return the configured form field values as hidden input fields
     */
    protected String validateConstraints(CmsFormHandler formHandler) {

        // check first, if mandatory flag has to be evaluated
        if (evaluateMandatory(formHandler)) {
            return super.validateConstraints();
        } else {
            return null;
        }
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#validate(CmsFormHandler)
     */
    @Override
    public String validate(CmsFormHandler formHandler) {

        // validate the constraints
        String validationError = validateConstraints(formHandler);

        if (formHandler.getFormConfiguration().isTransportDatabase() && CmsStringUtil.isEmpty(validationError)) {
            // no constraint error, check if value is unique if necessary
            String uniqueStr = getParameters().get(PARAM_FIELD_UNIQUE);
            if (CmsStringUtil.TRUE.equals(uniqueStr)) {
                try {
                    List<CmsFormDataBean> entries = CmsFormDataAccess.getInstance().readFormsForFieldValue(
                        formHandler.getFormConfiguration().getFormId(),
                        getDbLabel(),
                        getValue());
                    if (entries.size() > 0) {
                        // value already exists in the database
                        validationError = CmsFormHandler.ERROR_UNIQUE;
                        setErrorMessage(formHandler.getMessages().key("form.error.unique"));
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        if (CmsStringUtil.isEmpty(validationError)) {
            // no constraint or unique error, validate the input value
            validationError = validateValue();
        }

        return validationError;
    }

    /**
     * Checks if the mandatory field has to be evaluated<p>
     * @param formHandler the handler of the current form
     * @return if the mandatory flag has to be evaluated (if select box is displayed) 
     */
    protected boolean evaluateMandatory(CmsFormHandler formHandler) {

        boolean mandatory = false;
        // get predefined parameter(s) from webform
        Map<String, String> params = getParameters();
        // get parameters from HTTP request
        Map<String, String[]> reqParams = formHandler.getParameterMap();
        //check if parameter(s) have been set in webform
        if (!params.isEmpty()) {
            //Iterate over parameters
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (reqParams.containsKey(key)) {
                    String[] paramArray = reqParams.get(key);
                    // check if a single value has been passed or multiple (or empty)
                    if (paramArray.length > 1) {
                        mandatory = true;
                    } else {
                        // set mandatory to false
                        mandatory = false;
                    }
                } else {
                    mandatory = false;
                }
            }
        }

        return mandatory;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, CmsMessages, String, boolean, String)
     */
    @Override
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        // Preset stringTemplate type to "empty" form field, if parameter is not defined or has no value
        String type = CmsEmptyField.getStaticType();

        // get predefined parameter(s) from webform
        Map<String, String> params = getParameters();
        // get parameters from HTTP request
        Map<String, String[]> reqParams = formHandler.getParameterMap();
        // New map to take only the request parameters matching the "parameter" webform field
        Map<String, String[]> paramsForHiddenFields = new HashMap<String, String[]>();
        //check if parameter(s) have been set in webform
        if (!params.isEmpty()) {
            //Iterate over parameters
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (reqParams.containsKey(key)) {
                    String[] paramsRequest = reqParams.get(key);
                    paramsForHiddenFields.put(key, paramsRequest);
                    // check if a single value has been passed or multiple (or empty)
                    int length = paramsRequest.length;
                    List<String> paramValues = new ArrayList<String>(length);
                    for (int i = 0; i < length; i++) {
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(paramsRequest[i])
                            && !paramValues.contains(paramsRequest[i])) {
                            paramValues.add(paramsRequest[i]);
                        }
                    }

                    if (paramValues.size() > 1) {
                        String[] selectedValue = null;
                        if (reqParams.containsKey(getName())) {
                            selectedValue = reqParams.get(getName());
                        }
                        // parameter has more than one value -> SelectBox
                        type = CmsSelectionField.getStaticType();
                        String value = "";
                        String label = Messages.get().getBundle().key(Messages.PARAMETER_FIELD_SELECTBOX);
                        List<CmsFieldItem> items = new ArrayList<CmsFieldItem>(length + 1);
                        items.add(new CmsFieldItem(value, label, false, false));
                        // while 
                        for (String paramValue : paramValues) {

                            value = CmsEncoder.escapeXml(paramValue);
                            label = CmsEncoder.escapeXml(paramValue);
                            boolean isSelected = false;
                            if (selectedValue != null) {
                                if (value.equals(selectedValue[0])) {
                                    isSelected = true;
                                }
                            }
                            items.add(new CmsFieldItem(value, label, isSelected, false));

                        }
                        setItems(items);
                    } else {
                        // parameter has a single value -> display as non-editable text
                        type = CmsDisplayField.getStaticType();
                        setValue(CmsEncoder.escapeXml(paramValues.get(0)));
                        setMandatory(false);
                    }
                } else {
                    // pre-defined parameter not found in request parameters. 
                    type = CmsDisplayField.getStaticType();
                    setValue(CmsEncoder.escapeXml(""));
                    setMandatory(false);
                }
            }
        }
        // create error message
        String errorMessage = createStandardErrorMessage(errorKey, messages);

        // create HTML and append hidden fields to pass request params;
        String result = createHtml(formHandler, messages, null, type, null, errorMessage, showMandatory);
        result += requestParamsToHiddenFields(paramsForHiddenFields);
        return result;
    }
}
