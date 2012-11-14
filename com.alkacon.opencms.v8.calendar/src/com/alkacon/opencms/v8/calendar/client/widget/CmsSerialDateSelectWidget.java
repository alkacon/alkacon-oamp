/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.calendar.client.widget;

import com.alkacon.opencms.v8.calendar.shared.rpc.I_CmsSerialDateService;
import com.alkacon.opencms.v8.calendar.shared.rpc.I_CmsSerialDateServiceAsync;

import org.opencms.ade.contenteditor.client.widgets.CmsSelectWidget;
import org.opencms.gwt.CmsRpcException;
import org.opencms.gwt.client.CmsCoreProvider;
import org.opencms.gwt.client.rpc.CmsRpcAction;
import org.opencms.util.CmsStringUtil;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Provides a DHTML calendar widget, for use on a widget dialog.<p>
 * 
 * */
public class CmsSerialDateSelectWidget extends CmsSelectWidget {

    /** Test */
    private static I_CmsSerialDateServiceAsync SERVICE;

    /** The String of select configuration. */
    String m_selectValues = "";

    /** The locale. */
    private String m_locale;

    /** The entry count. */
    private int m_entryCount;

    /**
     * Constructs an CmsComboWidget with the in XSD schema declared configuration.<p>
     * @param config The configuration string given from OpenCms XSD.
     */
    public CmsSerialDateSelectWidget(String config) {

        super("Please Select");
        getNewValues();
        String[] configs = config.split(";");
        m_entryCount = Integer.parseInt(configs[0]);
        m_locale = configs[1];
        m_selectBox.addDomHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                getNewValues();

            }
        }, ClickEvent.getType());

    }

    /**
     * Returns the long value of the given String or the default value if parsing the String fails.<p>
     * 
     * @param strValue the String to parse
     * @param defaultValue the default value to use if parsing fails
     * @return the long value of the given String
     */
    protected static long getLongValue(String strValue, long defaultValue) {

        long result = defaultValue;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
            try {
                result = Long.parseLong(strValue);
            } catch (NumberFormatException e) {
                // no number, use default value
            }
        }
        return result;
    }

    /**
     * Checks if the select values have to be change.<p>
     * */
    protected void getNewValues() {

        String actualValue = getGlobalValue();
        if (!m_selectValues.equals(actualValue)) {
            m_selectValues = actualValue;
            if (!m_selectValues.isEmpty()) {
                generateNewSelection(m_selectValues);
            }
        }

    }

    /**
     * Returns the container-page RPC service.<p>
     * 
     * @return the container-page service
     */
    protected I_CmsSerialDateServiceAsync getService() {

        if (SERVICE == null) {
            SERVICE = GWT.create(I_CmsSerialDateService.class);
            String serviceUrl = CmsCoreProvider.get().link("com.alkacon.opencms.v8.calendar.CmsSerialDateService.gwt");
            ((ServiceDefTarget)SERVICE).setServiceEntryPoint(serviceUrl);
        }
        return SERVICE;
    }

    /**
     * Updates the selection of this select box.<p>
     * @param newValues Map of the select options
     * */
    protected void updateSelection(Map<String, String> newValues) {

        m_selectBox.setItems(newValues);

    }

    /**
     * Generates the new select values an sets them.<p>
     * @param selectValues the values to be selected
     * 
     * */
    private void generateNewSelection(String selectValues) {

        // generate a list of all configured categories.
        final String locale = m_locale;
        final String values = selectValues;
        final int entryCount = m_entryCount;
        // start request 
        CmsRpcAction<Map<String, String>> action = new CmsRpcAction<Map<String, String>>() {

            /**
             * @see org.opencms.gwt.client.rpc.CmsRpcAction#onResponse(java.lang.Object)
             */
            @Override
            public void execute() {

                try {
                    getService().getSeriaDateSelection(values, locale, entryCount, this);
                } catch (CmsRpcException e) {
                    // TODO: Auto-generated catch block
                    e.printStackTrace();
                }

            }

            /**
             * @see org.opencms.gwt.client.rpc.CmsRpcAction#onResponse(java.lang.Object)
             */
            @Override
            protected void onResponse(Map<String, String> result) {

                updateSelection(result);
            }

        };
        action.execute();
    }

    /**
     * Selects the value from the global variable.<p>
     * @return the value from the global variable
     */
    private native String getGlobalValue()/*-{
        return $wnd.cmsSerialDateWidgetValue;
    }-*/;
}
