/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/Messages.java,v $
 * Date   : $Date: 2010/03/19 15:31:12 $
 * Version: $Revision: 1.4 $
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

package com.alkacon.opencms.comments;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4 
 */
public final class Messages extends A_CmsMessageBundle {

    /** Message constant for key in the resource bundle. */
    public static final String ERR_GROUP_DOESNOT_EXIST_1 = "ERR_GROUP_DOESNOT_EXIST_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_MANDATORY_FIELDS_MISSING_1 = "ERR_MANDATORY_FIELDS_MISSING_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_ORGUNIT_DOESNOT_EXIST_1 = "ERR_ORGUNIT_DOESNOT_EXIST_1";

    /** Message constant for key in the resource bundle. */
    public static final String ERR_NO_CONFIGURATION_FILES_FOUND_0 = "ERR_NO_CONFIGURATION_FILES_FOUND_0";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_CONFIG_1 = "LOG_INIT_CONFIG_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_PAGE_1 = "LOG_INIT_PAGE_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_PARAM_2 = "LOG_INIT_PARAM_2";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_PROJECT_1 = "LOG_INIT_PROJECT_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_RESOURCE_1 = "LOG_INIT_RESOURCE_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_SHOW_1 = "LOG_INIT_SHOW_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_SITE_1 = "LOG_INIT_SITE_1";

    /** Message constant for key in the resource bundle. */
    public static final String LOG_INIT_STATE_1 = "LOG_INIT_STATE_1";

    /** Name of the used resource bundle. */
    private static final String BUNDLE_NAME = "com.alkacon.opencms.comments.messages";

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