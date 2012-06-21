/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarEntry.java,v $
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

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Represents a single calendar entry and provides information about the entry data and entry date.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarEntry {

    /** The calendar entry data. */
    private I_CmsCalendarEntryData m_entryData;

    /** The calendar entry date. */
    private CmsCalendarEntryDate m_entryDate;

    /**
     * Creates an empty calendar entry.<p>
     */
    public CmsCalendarEntry() {

        m_entryData = new CmsCalendarEntryData();
        m_entryDate = new CmsCalendarEntryDate(new GregorianCalendar(), new GregorianCalendar());
    }

    /**
     * Creates a configured calendar entry.<p>
     * 
     * @param entryData the entry data
     * @param entryDate the entry date information
     */
    public CmsCalendarEntry(I_CmsCalendarEntryData entryData, CmsCalendarEntryDate entryDate) {

        m_entryData = entryData;
        m_entryDate = entryDate;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {

        return new CmsCalendarEntry(
            (I_CmsCalendarEntryData)m_entryData.clone(),
            (CmsCalendarEntryDate)m_entryDate.clone());
    }

    /**
     * Returns the calendar entry data.<p>
     * 
     * @return the calendar entry data
     */
    public I_CmsCalendarEntryData getEntryData() {

        return m_entryData;
    }

    /**
     * Returns the calendar entry date.<p>
     * 
     * @return the calendar entry date
     */
    public CmsCalendarEntryDate getEntryDate() {

        return m_entryDate;
    }

    /**
     * Returns the list of matching entries for the calendar view.<p>
     * 
     * @param calendarView the calendar view 
     * @return the matching calendar entries
     */
    public List matchCalendarView(I_CmsCalendarView calendarView) {

        return m_entryDate.matchCalendarView(this, calendarView);
    }

    /**
     * Sets the calendar entry data.<p>
     * 
     * @param entryData the calendar entry data
     */
    public void setEntryData(I_CmsCalendarEntryData entryData) {

        m_entryData = entryData;
    }

    /**
     * Sets the calendar entry date.<p>
     * 
     * @param entryDate the calendar entry date
     */
    public void setEntryDate(CmsCalendarEntryDate entryDate) {

        m_entryDate = entryDate;
    }

}
