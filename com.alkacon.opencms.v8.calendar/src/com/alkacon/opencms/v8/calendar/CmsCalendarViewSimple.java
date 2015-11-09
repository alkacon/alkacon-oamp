/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarViewSimple.java,v $
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Filters calendar entries to get a sorted list of entries for a simple calendar view like daily, weekly or monthly views.<p>
 * 
 * Provides a comparator to filter entries for a given date range, returning 0 if the entry is inside the range, and another
 * comparator to sort the found entries by their start date ascending.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarViewSimple implements I_CmsCalendarView {

    /**
     * This comparator is used to sort the filtered entries for the views by their start dates ascending.<p>
     */
    public static final Comparator<CmsCalendarEntry> COMPARE_SORT_ENTRIES_DATE_ASC = new Comparator<CmsCalendarEntry>() {

        /**
         * Compares two {@link CmsCalendarEntry} objects by their start dates.<p>
         */
        @Override
        public int compare(CmsCalendarEntry arg0, CmsCalendarEntry arg1) {

            if (arg0 == arg1) {
                return -1;
            }

            long start0 = arg0.getEntryDate().getStartDate().getTimeInMillis();
            long start1 = arg1.getEntryDate().getStartDate().getTimeInMillis();

            if (start0 < start1) {
                return -1;
            }
            if (start0 > start1) {
                return 1;
            }

            return 0;
        }
    };

    /**
     * This comparator compares the date of the entry with the date of the view to decide if the entry is part of the view.<p>
     * 
     * Can be used for simple date comparisons that are not serial dates.<p>
     */
    public static final Comparator<CmsCalendarEntryDate> COMPARE_VIEW_DATE = new Comparator<CmsCalendarEntryDate>() {

        /**
         * Compares two {@link CmsCalendarEntryDate} objects.<p>
         * 
         * The first argument is the view date object, the second the entry date object.
         * If 0 is returned, the entry date object matches the view date object
         * and should be added to the view result entries.<p>
         */
        @Override
        public int compare(CmsCalendarEntryDate arg0, CmsCalendarEntryDate arg1) {

            if (arg0 == arg1) {
                return -1;
            }

            long viewStart = arg0.getStartDate().getTimeInMillis();
            long viewEnd = arg0.getEndDate().getTimeInMillis();
            long entryStart = arg1.getStartDate().getTimeInMillis();

            if ((entryStart >= viewStart) && (entryStart <= viewEnd)) {
                // entry is in view range
                return 0;
            }
            // entry is outside view range
            if (entryStart < viewStart) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    /** The dates for the view. */
    private final List<CmsCalendarEntryDate> m_dates;

    /**
     * Constructor that sets the required member variables.<p>
     * 
     * @param dates the list of dates to get for the view
     */
    public CmsCalendarViewSimple(List<CmsCalendarEntryDate> dates) {

        m_dates = dates;
    }

    @Override
    public Comparator<CmsCalendarEntryDate> getComparator() {

        return COMPARE_VIEW_DATE;
    }

    @Override
    public List<CmsCalendarEntryDate> getDates() {

        return m_dates;
    }

    @Override
    public void sort(List<CmsCalendarEntry> entries) {

        Collections.sort(entries, COMPARE_SORT_ENTRIES_DATE_ASC);
    }

}
