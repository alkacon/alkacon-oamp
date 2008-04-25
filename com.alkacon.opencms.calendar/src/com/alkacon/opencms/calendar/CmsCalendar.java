/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendar.java,v $
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

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a calendar data structure usable to display different calendar views on the frontend.<p>
 * 
 * A calendar contains a list of {@link com.alkacon.opencms.calendar.CmsCalendarEntry} objects and a method to filter
 * entries using an initialized {@link com.alkacon.opencms.calendar.I_CmsCalendarView} object.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendar {

    /** Contains the configured calendar entries. */
    private List m_entries;

    /**
     * Default constructor, creates an empty calendar without entries.<p>
     */
    public CmsCalendar() {

        m_entries = new ArrayList();
    }

    /**
     * Constructor with parameter, creates a calendar with provided list of entries.<p>
     * 
     * @param entries the list of entries for the calendar day
     */
    public CmsCalendar(List entries) {

        m_entries = entries;
    }

    /**
     * Adds a calendar entry to the list of entries for the day.<p>
     * 
     * @param entry the calendar entry to add
     */
    public void addEntry(CmsCalendarEntry entry) {

        m_entries.add(entry);
    }

    /**
     * Returns all calendar entries.<p>
     * 
     * @return all calendar entries
     */
    public List getEntries() {

        return m_entries;
    }

    /**
     * Returns the entries for the given calendar view, sorted by the view's sort method.<p>
     * 
     * @param calendarView the given calendar view
     * 
     * @return the matching entries for the calendar view
     */
    public List getEntries(I_CmsCalendarView calendarView) {

        List result = new ArrayList();

        for (int i = 0; i < m_entries.size(); i++) {
            CmsCalendarEntry entry = (CmsCalendarEntry)m_entries.get(i);
            List matchedEntries = entry.matchCalendarView(calendarView);
            if (matchedEntries.size() > 0) {
                // add matching entries to result list
                result.addAll(matchedEntries);
            }
        }

        // sort the result using the view's sort method
        calendarView.sort(result);

        return result;
    }

    /**
     * Sets the sorted list of entries for the calendar day.<p>
     * 
     * The list has to be created using {@link CmsCalendarEntry} objects.<p>
     * 
     * @param entries the list of entries for the calendar day
     */
    public void setEntries(List entries) {

        m_entries = entries;
    }

}
