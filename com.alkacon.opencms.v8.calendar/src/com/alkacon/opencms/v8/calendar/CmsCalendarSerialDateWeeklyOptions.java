/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarSerialDateWeeklyOptions.java,v $
 * Date   : $Date: 2009/02/05 09:49:31 $
 * Version: $Revision: 1.2 $
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

import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for a weekly serial calendar entry.<p>
 * 
 * Provides the necessary information about a weekly serial calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarSerialDateWeeklyOptions extends A_CmsCalendarSerialDateOptions {

    /** The week days on which the serial calendar entry occurs. */
    private List m_weekDays;

    /** The weekly interval for the serial calendar entry. */
    private int m_weeklyInterval;

    /**
     * Creates an initialized serial date weekly options object with the standard daily interval options.<p>
     * 
     * Standard interval options are: every week day in a weekly interval.<p>
     */
    public CmsCalendarSerialDateWeeklyOptions() {

        m_weekDays = Arrays.asList(new Integer[] {
            new Integer(1),
            new Integer(2),
            new Integer(3),
            new Integer(4),
            new Integer(5),
            new Integer(6),
            new Integer(7)});
        m_weeklyInterval = 1;
    }

    /**
     * Creates an initialized serial date weekly options object with the given parameters.<p>
     * 
     * @param weekDays the week days for the week series
     * @param weeklyInterval the weekly interval for the week day series
     */
    public CmsCalendarSerialDateWeeklyOptions(List weekDays, int weeklyInterval) {

        m_weekDays = weekDays;
        m_weeklyInterval = 1;
        if (weeklyInterval > 0) {
            m_weeklyInterval = weeklyInterval;
        }
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public Map getConfigurationValuesAsMap() {

        // create the Map containing the date settings
        Map values = new HashMap();

        // put interval and week days to Map
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, String.valueOf(getWeeklyInterval()));
        String weekDaysStr = CmsStringUtil.collectionAsString(getWeekDays(), ",");
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, weekDaysStr);

        return values;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public int getSerialType() {

        return I_CmsCalendarSerialDateOptions.TYPE_WEEKLY;
    }

    /**
     * Returns the week days on which the calendar entry occurs.<p>
     *
     * @return the week days on which the calendar entry occurs
     */
    public List getWeekDays() {

        return m_weekDays;
    }

    /**
     * Returns the weekly interval for the calendar entry occurences.<p>
     *
     * @return the weekly interval for the calendar entry occurences
     */
    public int getWeeklyInterval() {

        return m_weeklyInterval;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#matchCalendarView(com.alkacon.opencms.v8.calendar.CmsCalendarEntry, com.alkacon.opencms.v8.calendar.I_CmsCalendarView, int)
     */
    public List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView, int maxCount) {

        List result = new ArrayList();
        int matches = 0;

        CmsCalendarEntryDateSerial entryDate = (CmsCalendarEntryDateSerial)entry.getEntryDate();
        Calendar entryStartDayDate = (Calendar)entry.getEntryDate().getStartDate().clone();
        entryStartDayDate.setTimeInMillis(entryDate.getStartDay());

        // loop the view date ranges
        for (int i = 0; i < calendarView.getDates().size(); i++) {
            // get the current view date object
            CmsCalendarEntryDate viewDate = (CmsCalendarEntryDate)calendarView.getDates().get(i);
            // get the start and end times of the view
            long viewStart = viewDate.getStartDate().getTimeInMillis();
            long viewEnd = viewDate.getEndDate().getTimeInMillis();

            // set the date for the current run
            Calendar runDate = entryStartDayDate;
            if ((getWeeklyInterval() == 1)
                && ((entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_NEVER) || (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_DATE))
                && (entryStartDayDate.getTimeInMillis() < viewDate.getStartDay())) {
                // skip to current view start date to optimize performance
                runDate.setTimeInMillis(viewDate.getStartDay());
            }

            // occurences counter
            int occurences = 0;
            int oldWeekNumber = runDate.get(Calendar.WEEK_OF_YEAR);

            while (runDate.before(viewDate.getEndDate())) {

                // check conditions to leave date series loop
                if (checkLeaveLoop(entryDate, runDate, viewDate, occurences)) {
                    break;
                }
                if (matches >= maxCount) {
                    break;
                }

                // get the current week day
                int runWeekDay = runDate.get(Calendar.DAY_OF_WEEK);
                Integer runWeekDayInteger = new Integer(runWeekDay);
                if (getWeekDays().contains(runWeekDayInteger)) {
                    // the current day contains a series entry
                    occurences++;
                    long entryStart = runDate.getTimeInMillis() + entryDate.getStartTime();
                    // check if current entry is in view range
                    if ((entryStart >= viewStart) && (entryStart <= viewEnd)) {
                        // the entry is in the view time range, clone the entry 
                        CmsCalendarEntry cloneEntry = (CmsCalendarEntry)entry.clone();
                        cloneEntry.getEntryDate().setStartDay(runDate.getTimeInMillis());
                        cloneEntry = checkChanges(cloneEntry);
                        if (cloneEntry != null) {
                            // add the cloned entry to the result list
                            result.add(cloneEntry);
                            matches += 1;
                        }
                    }
                }

                // increase run date with one week day for next test
                runDate.add(Calendar.DAY_OF_YEAR, 1);
                // skip weeks according to setting if necessary (when the week of year has changed)
                if ((getWeeklyInterval() > 1) && (runDate.get(Calendar.WEEK_OF_YEAR) > oldWeekNumber)) {
                    // increase week depending on weekly interval
                    runDate.add(Calendar.WEEK_OF_YEAR, getWeeklyInterval() - 1);
                    oldWeekNumber = runDate.get(Calendar.WEEK_OF_YEAR);
                }
            }
        }
        return result;
    }

    /**
     * Sets the week days on which the calendar entry occurs.<p>
     *
     * @param weekDays the week days on which the calendar entry occurs
     */
    public void setWeekDays(List weekDays) {

        m_weekDays = weekDays;
    }

    /**
     * Sets the weekly interval for the calendar entry occurences.<p>
     *
     * @param weeklyInterval the weekly interval for the calendar entry occurences
     */
    public void setWeeklyInterval(int weeklyInterval) {

        if (weeklyInterval > 0) {
            m_weeklyInterval = weeklyInterval;
        }
    }

}