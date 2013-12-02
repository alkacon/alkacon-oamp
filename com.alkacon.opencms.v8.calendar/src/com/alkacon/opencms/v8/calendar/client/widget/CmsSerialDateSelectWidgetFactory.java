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

import com.alkacon.acacia.client.widgets.FormWidgetWrapper;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.acacia.client.widgets.I_FormEditWidget;

import org.opencms.ade.contenteditor.widgetregistry.client.A_NativeWidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for the serial date widget.<p>
 */
public class CmsSerialDateSelectWidgetFactory extends A_NativeWidgetFactory {

    /**
     * @see com.alkacon.acacia.client.I_WidgetFactory#createFormWidget(java.lang.String)
     */
    public I_FormEditWidget createFormWidget(String configuration) {

        return new FormWidgetWrapper(new CmsSerialDateSelectWidget(configuration));
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetFactory#createInlineWidget(java.lang.String, com.google.gwt.dom.client.Element)
     */
    public I_EditWidget createInlineWidget(String configuration, Element element) {

        return null;
    }

    /**
     * @see org.opencms.ade.contenteditor.widgetregistry.client.A_NativeWidgetFactory#getInitCallName()
     */
    @Override
    protected String getInitCallName() {

        return "initSerialDateSelectWidget";
    }

    /**
     * @see org.opencms.ade.contenteditor.widgetregistry.client.A_NativeWidgetFactory#getWidgetName()
     */
    @Override
    protected String getWidgetName() {

        return "com.alkacon.opencms.v8.calendar.CmsSerialDateSelectWidget";
    }
}
