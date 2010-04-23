/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsPagingField.java,v $
 * Date   : $Date: 2010/04/23 09:53:17 $
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

package com.alkacon.opencms.formgenerator;

import org.opencms.i18n.CmsMessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a new page field.<p>
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 * 
 */
public class CmsPagingField extends A_CmsField {

    /** The list with all paging fields. */
    private static ArrayList m_fields = null;

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

        StringBuffer buf = new StringBuffer();
        // get the parameter map
        Map parameters = formHandler.getParameterMap();
        // loop through all form input field before that paging field, otherwise there are doubled parameter values
        // as input fields and hidden fields, for example if there are invalid inputs
        CmsForm formConfiguration = formHandler.getFormConfiguration();
        List fields = formConfiguration.getFields();
        int page = CmsPagingField.getPageFromField(formHandler, position);
        for (int i = 0, n = fields.size(); i < n; i++) {
            // loop through all form input fields which are NOT in the current page
            I_CmsField field = (I_CmsField)fields.get(i);
            int fieldPage = CmsPagingField.getPageFromField(formHandler, i + 1);
            if ((fieldPage != page) || (field instanceof CmsFileUploadField)) {
                String name = field.getName();
                if (parameters.containsKey(name)) {
                    String[] values = (String[])parameters.get(name);
                    String value = values[0];
                    buf.append("<input type='hidden' name='" + name + "' value='" + value + "' />").append("\n");
                }
            } else {
                // specific handling for file upload fields
                // also write the hidden values for them if they are empty but there are values in the request parameters
                if (field instanceof CmsFileUploadField) {
                    String name = field.getName();
                    if (parameters.containsKey(name)) {
                        String[] values = (String[])parameters.get(name);
                        String value = values[0];
                        buf.append("<input type='hidden' name='" + name + "' value='" + value + "' />").append("\n");
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
            firstField = ((Integer)m_fields.get(page - 2)).intValue() + 1;
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
        lastField = ((Integer)m_fields.get(page - 1)).intValue();
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
        Iterator iter = m_fields.iterator();
        while (iter.hasNext()) {
            int pos = ((Integer)iter.next()).intValue();
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
        m_fields = new ArrayList();
        CmsForm formConfiguration = formHandler.getFormConfiguration();
        List fields = formConfiguration.getFields();
        for (int pos = 0, n = fields.size(); pos < n; pos++) {
            // loop through all form input fields 
            I_CmsField field = (I_CmsField)fields.get(pos);
            // only use the paging fields
            if (field instanceof CmsPagingField) {
                m_fields.add(pos);
            }
        }
        // add the last element as end element
        m_fields.add(fields.size() - 1);
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, org.opencms.i18n.CmsMessages, String, boolean)
     */
    public String buildHtml(CmsFormHandler formHandler, CmsMessages messages, String errorKey, boolean showMandatory) {

        StringBuffer buf = new StringBuffer();

        // line #1
        if (showRowStart(messages.key("form.html.col.two"))) {
            buf.append(messages.key("form.html.row.start")).append("\n");
        }

        // line #2
        buf.append(messages.key("form.html.label.start")).append(messages.key("form.html.label.end")).append("\n");

        // line #3
        buf.append(messages.key("form.html.field.start")).append("\n");
        // append the input fiels values from last pages as hidden values
        buf.append(appendHiddenFields(formHandler, messages, getFieldNr()));
        // append the back button if necessary
        if (isNotFirstPage(CmsPagingField.getPageFromField(formHandler, getFieldNr()))) {
            buf.append(
                "<input type='submit' value='"
                    + messages.key("form.button.prev")
                    + "' name='back' class='formbutton prevbutton' />").append("\n");
        }
        // append the next button
        buf.append(
            "<input type='submit' value='" + messages.key("form.button.next") + "' class='formbutton nextbutton' />").append(
            "\n");
        // append the hidden field with page info
        buf.append(
            "<input type='hidden' name='page' value='"
                + CmsPagingField.getPageFromField(formHandler, getFieldNr())
                + "' />").append("\n");
        buf.append(messages.key("form.html.field.end")).append("\n");
        buf.append(messages.key("form.html.label.end")).append("\n");

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
