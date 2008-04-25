/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/Messages.java,v $
 * Date   : $Date: 2008/04/25 14:50:41 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2008 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.calendar;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1 
 */
public final class Messages extends A_CmsMessageBundle {

    /** Message constant for key in the resource bundle. */
    public static final String GUI_SERIALDATE_TIME_DURATION_DAY_0 = "GUI_SERIALDATE_TIME_DURATION_DAY_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_SERIALDATE_TIME_DURATION_DAYS_0 = "GUI_SERIALDATE_TIME_DURATION_DAYS_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_SERIALDATE_TIME_DURATION_WEEK_0 = "GUI_SERIALDATE_TIME_DURATION_WEEK_0";

    /** Message constant for key in the resource bundle. */
    public static final String GUI_SERIALDATE_TIME_DURATION_WEEKS_0 = "GUI_SERIALDATE_TIME_DURATION_WEEKS_0";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_CALENDAR_REQUESTPARAMS_1 = "LOG_CALENDAR_REQUESTPARAMS_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_CALENDAR_RESOURCES_1 = "LOG_CALENDAR_RESOURCES_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_CALENDAR_SERIALDATE_CLASS_3 = "LOG_CALENDAR_SERIALDATE_CLASS_3";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_CALENDAR_WIDGET_PARSE_DATE_1 = "LOG_CALENDAR_WIDGET_PARSE_DATE_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_CALENDAR_WIDGETHTML_MISSING_1 = "LOG_CALENDAR_WIDGETHTML_MISSING_1";

    /** Name of the used resource bundle. */
    private static final String BUNDLE_NAME = "com.alkacon.opencms.calendar.messages";

    /** Static instance member. */
    private static final I_CmsMessageBundle INSTANCE = new Messages();

    /**
     * Hides the public constructor for this utility class.<p>
     */
    private Messages() {

        // hide the constructor
    }

    /**
     * Returns an instance of this localized message accessor.<p>
     * 
     * @return an instance of this localized message accessor
     */
    public static I_CmsMessageBundle get() {

        return INSTANCE;
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