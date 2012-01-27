/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsFileUploadField.java,v $
 * Date   : $Date: 2011/03/09 15:14:35 $
 * Version: $Revision: 1.8 $
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

import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

/**
 * Represents a file upload field.<p>
 * 
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.8 $
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
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#buildHtml(CmsFormHandler, CmsMessages, String, boolean, String)
     */
    @Override
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        StringBuffer buf = new StringBuffer(256);
        String infoMessage = null;
        String errorMessage = createStandardErrorMessage(errorKey, messages);

        // info message
        if (CmsStringUtil.isNotEmpty(infoKey)) {
            if (CmsFormHandler.INFO_UPLOAD_FIELD_MANDATORY_FILLED_OUT.equals(infoKey)) {
                // try to read the file name of the upload field
                String value = getValue();
                if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
                    // try to read the file name from the session attribute
                    FileItem fileItem = formHandler.getUploadFile(this);
                    if (fileItem != null) {
                        value = fileItem.getName();
                    }
                }
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(value)) {
                    value = CmsFormHandler.getTruncatedFileItemName(value);
                    infoMessage = messages.key("form.html.info.fileuploadname", value);
                }
            } else if (CmsStringUtil.isNotEmpty(getErrorMessage())) {
                infoMessage = getInfoMessage();
            }
        }

        Map<String, Object> stAttributes = new HashMap<String, Object>();
        // set info message as additional attribute
        stAttributes.put("infomessage", infoMessage);

        buf.append(createHtml(formHandler, messages, stAttributes, getType(), null, errorMessage, showMandatory));
        return buf.toString();
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#getType()
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
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsField#validateForInfo(CmsFormHandler)
     */
    @Override
    public String validateForInfo(CmsFormHandler formHandler) {

        String validationInfo = "";
        // check the request parameter  
        String param = formHandler.getParameter(getName());
        // check the session attribute
        FileItem fileItem = formHandler.getUploadFile(this);

        if (CmsStringUtil.isNotEmpty(param) || (fileItem != null)) {
            // set the info message, if the upload field is found as parameter or in the session attribute
            validationInfo = CmsFormHandler.INFO_UPLOAD_FIELD_MANDATORY_FILLED_OUT;
        }
        return validationInfo;
    }

    /**
     * Validates the input value of this field.<p>
     * 
     * @return {@link CmsFormHandler#ERROR_VALIDATION} if validation of the input value failed
     */
    @Override
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
                    List<String> listDocTypes = CmsStringUtil.splitAsList(allowedDocTypes, ",");
                    // iterate over all allowed document types and check if on eof them is the selected one
                    if (listDocTypes != null) {
                        Iterator<String> iter = listDocTypes.iterator();
                        while (iter.hasNext()) {
                            // get the next allowed document type
                            String nextDocType = iter.next();
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
                Map<String, String> substitutions = new HashMap<String, String>();
                substitutions.put("<", "");
                substitutions.put("kb", "");
                try {
                    int maxSize = Integer.parseInt(CmsStringUtil.substitute(valExpFileSize.toLowerCase(), substitutions)) * 1024;
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
