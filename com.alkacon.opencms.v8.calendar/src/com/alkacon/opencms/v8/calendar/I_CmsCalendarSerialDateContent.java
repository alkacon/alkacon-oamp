/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/I_CmsCalendarSerialDateContent.java,v $
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

package com.alkacon.opencms.v8.calendar;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;

/**
 * This can be used to get serial date entries from XML content resources.<p>
 * 
 * The serial date entries have to provide the class name of the implementing class
 * in the property {@link CmsCalendarDisplay#PROPERTY_CALENDAR_ENDDATE} value.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.4
 */
public interface I_CmsCalendarSerialDateContent {

    /**
     * Returns the serial entry for the calendar generation of the passed resource.<p>
     * 
     * @param cms the current users context
     * @param resource the resource to generate the serial entry from
     * @return the serial entry
     */
    CmsCalendarEntry getSerialEntryForCalendar(CmsObject cms, CmsResource resource);

}