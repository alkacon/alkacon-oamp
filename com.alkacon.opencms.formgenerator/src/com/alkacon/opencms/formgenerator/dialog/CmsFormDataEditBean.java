/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormDataEditBean.java,v $
 * Date   : $Date: 2008/03/25 17:01:42 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.formgenerator.dialog;

import org.opencms.widgets.I_CmsWidget;

/**
 * This object is needed to create dynamically editable fields.<p>
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 */
public class CmsFormDataEditBean {

    /** Contains the value of the current dynamic column. */
    private String m_value;

    /** The widget of the entry to edit. */
    private I_CmsWidget m_widget;

    /**
     * Default constructor with parameters.<p>
     * 
     * @param value the value of the editable field
     * @param widget the widget to show the needed field in the correct form
     */
    public CmsFormDataEditBean(String value, I_CmsWidget widget) {

        super();
        m_value = value;
        m_widget = widget;
    }

    /**
     * Returns the value.<p>
     *
     * @return the value
     */
    public String getValue() {

        return m_value;
    }

    /**
     * Returns the widget.<p>
     *
     * @return the widget
     */
    public I_CmsWidget getWidget() {

        return m_widget;
    }

    /**
     * Sets the value.<p>
     *
     * @param value the value to set
     */
    public void setValue(String value) {

        m_value = value;
    }

    /**
     * Sets the widget.<p>
     *
     * @param widget the widget to set
     */
    public void setWidget(I_CmsWidget widget) {

        m_widget = widget;
    }

}
