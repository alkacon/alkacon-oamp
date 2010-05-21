/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsPagingField.java,v $
 * Date   : $Date: 2010/05/21 13:49:16 $
 * Version: $Revision: 1.2 $
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

import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a new page field.<p>
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.5.2
 * 
 */
public class CmsPagingField extends A_CmsField {

    /** The list with all paging fields. */
    private static List<Integer> m_fields;

    /** HTML field type: hidden field. */
    private static final String TYPE = "paging";

    /**
     * Appends the input field values from last pages as hidden values.<p>
     * 
     * @param formHandler the form handler
     * @param messages the message bundle
     * @param position the position of the paging field
     * 
     * @return the input field values from last pages as hidden values.
     */
    public static StringBuffer appendHiddenFields(CmsFormHandler formHandler, CmsMessages messages, int position) {

        StringBuffer buf = new StringBuffer(512);

        // loop through all form input field before that paging field, otherwise there are doubled parameter values
        // as input fields and hidden fields, for example if there are invalid inputs
        List<I_CmsField> fields = formHandler.getFormConfiguration().getFields();
        int page = CmsPagingField.getPageFromField(formHandler, position);
        for (int i = 0, n = fields.size(); i < n; i++) {
            // loop through all form input fields which are NOT in the current page
            I_CmsField field = fields.get(i);
            int fieldPage = CmsPagingField.getPageFromField(formHandler, i + 1);
            if ((fieldPage != page) || (field instanceof CmsFileUploadField)) {
                buf.append(createHiddenField(field));
                if (field.hasCurrentSubFields()) {
                    // check the sub fields of the field
                    Iterator<I_CmsField> it = field.getCurrentSubFields().iterator();
                    while (it.hasNext()) {
                        I_CmsField subField = it.next();
                        buf.append(createHiddenField(subField));
                    }
                }
            }
        }

        return buf;
    }

    /**
     * Returns the first input field position from the current page.<p>
     * 
     * @param formHandler the form handler
     * @param page the current page
     * 
     * @return the first input field position from the current page
     */
    public static int getFirstFieldPosFromPage(CmsFormHandler formHandler, int page) {

        // the first input field position from the current page
        int firstField = 0;
        // initialize the fields list
        CmsPagingField.initializeFields(formHandler);
        // get the first field
        if (page == 1) {
            firstField = 0;
        } else {
            firstField = (m_fields.get(page - 2)).intValue() + 1;
        }
        return firstField;
    }

    /**
     * Returns the last input field position from the current page.<p>
     * 
     * @param formHandler the form handler
     * @param page the current page
     * 
     * @return the last input field position from the current page
     */
    public static int getLastFieldPosFromPage(CmsFormHandler formHandler, int page) {

        // the last input field position from the current page
        int lastField = 0;
        // initialize the fields list
        CmsPagingField.initializeFields(formHandler);
        // get the last field
        lastField = (m_fields.get(page - 1)).intValue();
        return lastField;
    }

    /**
     * Returns the next page.<p>
     * 
     * @param page the next page
     * 
     * @return the next page
     */
    public static int getNextPage(int page) {

        return page + 1;
    }

    /**
     * Returns the previous page.<p>
     * 
     * @param page the current page
     * 
     * @return the previous page
     */
    public static int getPreviousPage(int page) {

        if (page == 1) {
            return 1;
        } else {
            return page - 1;
        }
    }

    /**
     * Returns the type of the input field, e.g. "text" or "select".<p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * Returns the HTML of a hidden field storing the field value.<p>
     * 
     * @param field the input field to create a hidden field from
     * 
     * @return the HTML of a hidden field storing the field value
     */
    private static StringBuffer createHiddenField(I_CmsField field) {

        StringBuffer buf = new StringBuffer(64);

        if (field.needsItems()) {
            // specific handling for check boxes, radio buttons and select boxes: get the items of this field
            List<CmsFieldItem> items = ((A_CmsField)field).getSelectedItems();
            Iterator<CmsFieldItem> iter = items.iterator();
            while (iter.hasNext()) {
                CmsFieldItem currItem = iter.next();
                // only create hidden fields for not empty values
                if (CmsStringUtil.isNotEmpty(currItem.getValue())) {
                    // put the current value in a hidden field
                    buf.append("<input type=\"hidden\" name=\"").append(field.getName()).append("\" value=\"").append(
                        CmsEncoder.escapeXml(currItem.getValue())).append("\" />").append("\n");
                }
            }
        } else {
            // only create hidden fields for not empty values
            if (CmsStringUtil.isNotEmpty(field.getValue())) {
                // put the current value in a hidden field
                buf.append("<input type=\"hidden\" name=\"").append(field.getName()).append("\" value=\"").append(
                    CmsEncoder.escapeXml(field.getValue())).append("\" />").append("\n");
            }
        }

        return buf;
    }

    /**
     * Gets the page to the current field.<p>
     * 
     * @param fieldNr the current field number
     * 
     * @return the page to the current field
     */
    private static int getPageFromField(CmsFormHandler formHandler, int fieldNr) {

        // the current page
        int page = 1;
        // the current field
        fieldNr -= 1;
        // the paging position from the last page
        int previousPos = -2;
        // initialize the fields list
        CmsPagingField.initializeFields(formHandler);
        // loop over the fields list
        Iterator<Integer> iter = m_fields.iterator();
        while (iter.hasNext()) {
            int pos = (iter.next()).intValue();
            if ((fieldNr > previousPos) && (fieldNr <= pos)) {
                return page;
            }
            page += 1;
            previousPos = pos;
        }
        return page;
    }

    /**
     * Initialize the fields.<p>
     */
    private static void initializeFields(CmsFormHandler formHandler) {

        // get the positions of all paging fields
        m_fields = new ArrayList<Integer>();
        CmsForm formConfiguration = formHandler.getFormConfiguration();
        List<I_CmsField> fields = formConfiguration.getFields();
        for (int pos = 0, n = fields.size(); pos < n; pos++) {
            // loop through all form input fields 
            I_CmsField field = fields.get(pos);
            // only use the paging fields
            if (field instanceof CmsPagingField) {
                m_fields.add(new Integer(pos));
            }
        }
        // add the last element as end element
        m_fields.add(new Integer(fields.size() - 1));
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, org.opencms.i18n.CmsMessages, String, boolean)
     */
    @Override
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer(256);

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            if (isSubField()) {
                buf.append(messages.key("form.html.row.subfield.start")).append("\n");
            } else {
                buf.append(messages.key("form.html.row.start")).append("\n");
            }
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(messages.key("form.html.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.field.start")).append("\n");
        // append the input field values from last pages as hidden values
        buf.append(appendHiddenFields(formHandler, messages, getFieldNr()));
        // append the back button if necessary
        if (isNotFirstPage(CmsPagingField.getPageFromField(formHandler, getFieldNr()))) {
            buf.append("<input type=\"submit\" value=\"").append(messages.key("form.button.prev")).append(
                "\" name=\"back\" class=\"formbutton prevbutton\" />\n");
        }
        // append the next button
        buf.append("<input type=\"submit\" value=\"").append(messages.key("form.button.next")).append(
            "\" class=\"formbutton nextbutton\" />\n");
        // append the hidden field with page info
        buf.append("<input type=\"hidden\" name=\"page\" value=\"").append(
            CmsPagingField.getPageFromField(formHandler, getFieldNr())).append("\" />\n");
        buf.append(messages.key("form.html.field.end")).append("\n");

        // line #4
        if (showRowEnd(messages.key("form.html.col.two"))) {
            if (isSubField()) {
                buf.append(messages.key("form.html.row.subfield.end")).append("\n");
            } else {
                buf.append(messages.key("form.html.row.end")).append("\n");
            }
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
     * Returns false, if the page is the first page, otherwise true.<p>
     * 
     * @param page the current page
     * 
     * @return false, if the page is the first page, otherwise true.
     */
    private boolean isNotFirstPage(int page) {

        if (page == 1) {
            return false;
        }
        return true;
    }
}
