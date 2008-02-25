/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.counter/src/com/alkacon/opencms/counter/Messages.java,v $
 * Date   : $Date: 2008/02/25 16:29:38 $
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

package com.alkacon.opencms.counter;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.0 
 */
public final class Messages extends A_CmsMessageBundle {

    /** Message constant for key in the resource bundle. */
    public static final String GUI_COUNTERS_BLOCK_LABEL_0 = "GUI_COUNTERS_BLOCK_LABEL_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_OVERWRITE_BLOCK_LABEL_0 = "GUI_OVERWRITE_BLOCK_LABEL_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_WIDGET_COUNTER_HELP_0 = "GUI_WIDGET_COUNTER_HELP_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_WIDGET_COUNTER_NAME_0 = "GUI_WIDGET_COUNTER_NAME_0";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_DEBUG_GET_COUNTER_2 = "LOG_DEBUG_GET_COUNTER_2";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_ACCESSING_DB_1 = "LOG_ERROR_ACCESSING_DB_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_CREATING_TABLE_1 = "LOG_ERROR_CREATING_TABLE_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_DELETE_COUNTER_1 = "LOG_ERROR_DELETE_COUNTER_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_MISSING_VALUES_1 = "LOG_ERROR_MISSING_VALUES_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_PARSE_INTEGER_1 = "LOG_ERROR_PARSE_INTEGER_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_SET_COUNTER_2 = "LOG_ERROR_SET_COUNTER_2";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_ERROR_UPDATE_DB_0 = "LOG_ERROR_UPDATE_DB_0";

    /** Name of the used resource bundle. */
    private static final String BUNDLE_NAME = "com.alkacon.opencms.counter.messages";

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
