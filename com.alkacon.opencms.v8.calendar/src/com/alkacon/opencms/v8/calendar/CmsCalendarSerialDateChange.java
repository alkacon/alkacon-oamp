/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarSerialDateChange.java,v $
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

import java.util.Calendar;

/**
 * Represents a changed entry in a date series at a certain date.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsCalendarSerialDateChange extends Object {

    /** The changed entry data. */
    private I_CmsCalendarEntryData m_entryData;

    /** The start date of the series entry that should be changed. */
    private Calendar m_startDate;

    /**
     * Constructor that fills the necessary members.<p>
     * 
     * If the entry data is initialized with <code>null</code>, the date will be removed from the series.<p>
     * 
     * @param startDate the start date of the change
     * @param entryData the changed entry data or null if the series should be removed
     */
    public CmsCalendarSerialDateChange(Calendar startDate, I_CmsCalendarEntryData entryData) {

        m_startDate = startDate;
        m_entryData = entryData;
    }

    /**
     * Checks if the start date (not the time!) of the objects to compare are the same.<p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof CmsCalendarSerialDateChange) {
            boolean check = CmsCalendarDisplay.isCurrentDay(
                getStartDate(),
                ((CmsCalendarSerialDateChange)obj).getStartDate());
            return check;
        }
        return false;
    }

    /**
     * Returns the changed entry data.<p>
     * 
     * @return the changed entry data or <code>null</code> if the date should be removed from the series
     */
    public I_CmsCalendarEntryData getEntryData() {

        return m_entryData;
    }

    /**
     * Returns the start date of the series entry that should be changed.<p>
     * 
     * @return the start date of the series entry that should be changed
     */
    public Calendar getStartDate() {

        return m_startDate;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return getStartDate().hashCode();
    }

    /**
     * Returns if the date entries should be changed in the series.<p>
     * 
     * @return true if the date entries should be changed in the series, otherwise false
     */
    public boolean isChanged() {

        return m_entryData != null;
    }

    /**
     * Returns if the date should be removed from the series.<p>
     * 
     * @return true if the date should be removed from the series, otherwise false
     */
    public boolean isRemoved() {

        return m_entryData == null;
    }
}
