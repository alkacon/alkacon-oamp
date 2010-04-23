/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFileUploadField.java,v $
 * Date   : $Date: 2010/04/23 09:53:17 $
 * Version: $Revision: 1.5 $
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

import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a file upload field.<p>
 * 
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.5 $
 * 
 * @since 7.0.4 
 */
public class CmsFileUploadField extends A_CmsField {

    /** HTML field type: file. */
    private static final String TYPE = "file";

    /** The size of the uploaded file. */
    private int m_fileSize;

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, org.opencms.i18n.CmsMessages, String, boolean)
     */
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        StringBuffer buf = new StringBuffer();
        String fieldLabel = getLabel();
        String message = "";
        String mandatory = "";

        // info message
        if (CmsStringUtil.isNotEmpty(infoKey)) {

            String infoMessage = "";
            if (CmsFormHandler.INFO_UPLOAD_FIELD_MANDATORY_FILLED_OUT.equals(infoKey)) {
                String value = getValue();
                value = CmsFormHandler.getTruncatedFileItemName(value);
                infoMessage = messages.key("form.html.info.fileuploadname", value);
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                infoMessage = getInfoMessage();
            }
            infoMessage = messages.key("form.html.info.start") + infoMessage + messages.key("form.html.info.end");
            fieldLabel = messages.key("form.html.label.info.start")
                + fieldLabel
                + messages.key("form.html.label.info.end");
            message = message + infoMessage;
        }
        // error message
        if (CmsStringUtil.isNotEmpty(errorKey)) {
            String errorMessage = "";
            if (CmsFormHandler.ERROR_MANDATORY.equals(errorKey)) {
                errorMessage = messages.key("form.error.mandatory");
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                errorMessage = getErrorMessage();
            } else {
                errorMessage = messages.key("form.error.validation");
            }

            errorMessage = messages.key("form.html.error.start") + errorMessage + messages.key("form.html.error.end");
            fieldLabel = messages.key("form.html.label.error.start")
                + fieldLabel
                + messages.key("form.html.label.error.end");
            message = message + errorMessage;
        }

        if (isMandatory() && showMandatory) {
            mandatory = messages.key("form.html.mandatory");
        }

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(fieldLabel).append(mandatory).append(
            messages.key("form.html.label.end")).append("\n");

        // line #3
        String value = CmsStringUtil.escapeHtml(getValue());
        buf.append(messages.key("form.html.field.start")).append("<input type=\"file\" name=\"").append(getName()).append(
            "\" value=\"").append(CmsStringUtil.escapeHtml(getValue())).append("\"").append(
            formHandler.getFormConfiguration().getFormFieldAttributes()).append("/>").append(message).append(
            messages.key("form.html.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.end")).append("\n");
        }

        return buf.toString();
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * Sets the size of the uploaded file.<p>
     * 
     * @param fileSize the file size
     */
    public void setFileSize(int fileSize) {

        m_fileSize = fileSize;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#validate(CmsFormHandler)
     */
    public String validateForInfo(CmsFormHandler formHandler) {

        String validationInfo = "";
        String param = formHandler.getParameter(getName());
        if (CmsStringUtil.isNotEmpty(param)) {
            validationInfo = CmsFormHandler.INFO_UPLOAD_FIELD_MANDATORY_FILLED_OUT;
        }
        return validationInfo;
    }

    /**
     * Validates the input value of this field.<p>
     * 
     * @return {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    protected String validateValue() {

        // validate non-empty values with given regular expression
        if (CmsStringUtil.isNotEmpty(getValue()) && CmsStringUtil.isNotEmpty(getValidationExpression())) {

            // get the validation expressions for document type and file size
            String valExpDocType = "";
            String valExpFileSize = "";
            // check if there are validations for document type and file size
            if (getValidationExpression().contains("|")) {
                // validation expression for document type and file size
                int indexPipe = getValidationExpression().indexOf("|");
                valExpFileSize = getValidationExpression().substring(0, indexPipe);
                valExpDocType = getValidationExpression().substring(indexPipe + 1);
            } else {
                // only validation expression for file size
                valExpFileSize = getValidationExpression();
            }

            // document type
            if (CmsStringUtil.isNotEmpty(valExpDocType)) {
                boolean docTypeOk = false;
                // get the type of the file to upload
                String selDocType = "";
                if (getValue().contains(".")) {
                    int indType = getValue().lastIndexOf(".") + 1;
                    if (getValue().length() >= indType) {
                        selDocType = getValue().substring(indType).toUpperCase();
                    }
                }
                // get the allowed document types
                int confDocStart = getValidationExpression().indexOf("|") + 1;
                // check that there are entries after the pipe symbol
                if (getValidationExpression().length() >= confDocStart) {
                    String allowedDocTypes = getValidationExpression().substring(confDocStart);
                    // make the document type string to a list
                    List listDocTypes = CmsStringUtil.splitAsList(allowedDocTypes, ",");
                    // iterate over all allowed document types and check if on eof them is the selected one
                    if (listDocTypes != null) {
                        Iterator iter = listDocTypes.iterator();
                        while (iter.hasNext()) {
                            // get the next allowed document type
                            String nextDocType = (String)iter.next();
                            nextDocType = nextDocType.toUpperCase().trim();
                            // check the next allowed document type to the type of the file to upload
                            if (nextDocType.equals(selDocType)) {
                                docTypeOk = true;
                            }
                        }
                    }
                }
                // the document type is not allowed
                if (!docTypeOk) {
                    return CmsFormHandler.ERROR_VALIDATION;
                }
            }

            // file upload size
            if (CmsStringUtil.isNotEmpty(valExpFileSize)) {
                Map substitutions = new HashMap();
                substitutions.put("<", "");
                substitutions.put("kb", "");

                int maxSize = Integer.parseInt(CmsStringUtil.substitute(valExpFileSize, substitutions)) * 1024;
                try {
                    if (m_fileSize > maxSize) {
                        return CmsFormHandler.ERROR_VALIDATION;
                    }
                } catch (Exception e) {
                    // syntax error in regular expression, log to opencms.log
                    CmsLog.getLog(CmsFileUploadField.class).error(
                        Messages.get().getBundle().key(Messages.LOG_ERR_PATTERN_SYNTAX_0),
                        e);
                }
            }
        }
        return "";
    }
}
