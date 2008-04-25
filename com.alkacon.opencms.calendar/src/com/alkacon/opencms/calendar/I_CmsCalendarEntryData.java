/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/I_CmsCalendarEntryData.java,v $
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

/**
 * Provides information about the data of a single calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public interface I_CmsCalendarEntryData {

    /** Name of the property that stores the flag to show time information in the calendar list. */
    String PROPERTY_CALENDAR_SHOWTIME = "calendar.showtime";

    /** The weekday status: holiday. */
    int WEEKDAYSTATUS_HOLIDAY = 2;

    /** The weekday status: maybe holiday. */
    int WEEKDAYSTATUS_MAYBEHOLIDAY = 1;

    /** The weekday status: workday. */
    int WEEKDAYSTATUS_WORKDAY = 0;

    /**
     * Returns a clone of this objects instance.<p>
     * 
     * @return a clone of this instance
     */
    Object clone();

    /**
     * Returns the description of the entry.<p>
     *
     * @return the description of the entry
     */
    String getDescription();

    /**
     * Returns the URI to the detail view of the entry.<p>
     *
     * @return the URI to the detail view of the entry
     */
    String getDetailUri();

    /**
     * Returns the title of the entry.<p>
     *
     * @return the title of the entry
     */
    String getTitle();

    /**
     * Returns the type of the entry.<p>
     *
     * @return the type of the entry
     */
    String getType();

    /**
     * Returns the weekday status: working day, maybe holiday, holiday.<p>
     *
     * @return the weekday status: working day, maybe holiday, holiday
     */
    int getWeekdayStatus();

    /**
     * Returns if the time should be shown in the calendar view.<p>
     * 
     * @return true if the time should be shown in the calendar view, otherwise false
     */
    boolean isShowTime();

    /**
     * Sets the description of the entry.<p>
     *
     * @param description the description of the entry
     */
    void setDescription(String description);

    /**
     * Sets the URI to the detail view of the entry.<p>
     *
     * @param detailUri the URI to the detail view of the entry
     */
    void setDetailUri(String detailUri);

    /**
     * Sets if the time should be shown in the calendar view.<p>
     * 
     * @param showTime true if the time should be shown in the calendar view, otherwise false
     */
    void setShowTime(boolean showTime);

    /**
     * Sets the title of the entry.<p>
     *
     * @param title the title of the entry
     */
    void setTitle(String title);

    /**
     * Sets the type of the entry.<p>
     *
     * @param type the type of the entry
     */
    void setType(String type);

    /**
     * Sets the weekday status: working day, maybe holiday, holiday.<p>
     *
     * @param weekdayStatus the weekday status: working day, maybe holiday, holiday
     */
    void setWeekdayStatus(int weekdayStatus);

}
