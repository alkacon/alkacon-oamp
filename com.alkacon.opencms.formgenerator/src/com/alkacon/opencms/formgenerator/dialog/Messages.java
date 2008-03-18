/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/Messages.java,v $
 * Date   : $Date: 2008/03/18 11:34:09 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator.dialog;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4 
 */
public final class Messages extends A_CmsMessageBundle {

    /** Message constant for key in the resource bundle. */
    public static final String ERR_DELETE_SELECTED_FORM_0 = "ERR_DELETE_SELECTED_FORM_0";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_DELETE_SELECTED_FORM_1 = "ERR_DELETE_SELECTED_FORM_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_READ_FORM_0 = "ERR_READ_FORM_0";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_READ_FORM_FIELDS_1 = "ERR_READ_FORM_FIELDS_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_READ_FORM_VALUES_1 = "ERR_READ_FORM_VALUES_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_SHOW_SELECTED_FORM_1 = "ERR_SHOW_SELECTED_FORM_1";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_ACTION_FIELDS_DELETE_0 = "GUI_ACTION_FIELDS_DELETE_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_ACTION_FIELDS_DELETE_CONF_0 = "GUI_ACTION_FIELDS_DELETE_CONF_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_ACTION_FIELDS_DELETE_HELP_0 = "GUI_ACTION_FIELDS_DELETE_HELP_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_ACTION_FORM_SHOW_0 = "GUI_ACTION_FORM_SHOW_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_ACTION_FORM_SHOW_HELP_0 = "GUI_ACTION_FORM_SHOW_HELP_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FIELDS_DATE_0 = "GUI_COLUMN_FIELDS_DATE_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FIELDS_DATE_FORMAT_1 = "GUI_COLUMN_FIELDS_DATE_FORMAT_1";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FIELDS_DELETE_0 = "GUI_COLUMN_FIELDS_DELETE_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FIELDS_ID_0 = "GUI_COLUMN_FIELDS_ID_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FIELDS_RESOURCE_0 = "GUI_COLUMN_FIELDS_RESOURCE_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FORM_COUNT_0 = "GUI_COLUMN_FORM_COUNT_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FORM_NAME_0 = "GUI_COLUMN_FORM_NAME_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COLUMN_FORM_SHOW_0 = "GUI_COLUMN_FORM_SHOW_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_FIELD_LIST_NAME_0 = "GUI_FIELD_LIST_NAME_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_FORM_LIST_NAME_0 = "GUI_FORM_LIST_NAME_0";

    /** Name of the used resource bundle. */
    private static final String BUNDLE_NAME = "com.alkacon.opencms.formgenerator.dialog.messages";

    /** Static instance member. */
    private static final I_CmsMessageBundle INSTANCE = new Messages();

    /**
     * Returns an instance of this localized message accessor.<p>
     * 
     * @return an instance of this localized message accessor
     */
    public static I_CmsMessageBundle get() {

        return INSTANCE;
    }

    /**
     * Hides the public constructor for this utility class.<p>
     */
    private Messages() {

        // hide the constructor
    }

    /**
     * Returns the bundle name for this OpenCms package.<p>
     * 
     * @return the bundle name for this OpenCms package
     */
    public String getBundleName() {

        return BUNDLE_NAME;
    }
}