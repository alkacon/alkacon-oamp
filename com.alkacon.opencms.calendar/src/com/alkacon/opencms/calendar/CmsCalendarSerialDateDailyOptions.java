/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarSerialDateDailyOptions.java,v $
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for a daily serial calendar entry.<p>
 * 
 * Provides the necessary information about a daily serial calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0
 */
public class CmsCalendarSerialDateDailyOptions extends A_CmsCalendarSerialDateOptions {

    /** The daily interval for the serial calendar entry. */
    private int m_dailyInterval;

    /** Flag indicating if the entry should be shown on every working day. */
    private boolean m_everyWorkingDay;

    /** The working days Integer values as List. */
    private List m_workingDaysList = Arrays.asList(new Integer[] {
        new Integer(Calendar.MONDAY),
        new Integer(Calendar.TUESDAY),
        new Integer(Calendar.WEDNESDAY),
        new Integer(Calendar.THURSDAY),
        new Integer(Calendar.FRIDAY)});

    /**
     * Creates an initialized serial date daily options object with the standard daily interval options.<p>
     * 
     * Standard interval options are: every day.<p>
     */
    public CmsCalendarSerialDateDailyOptions() {

        m_dailyInterval = 1;
    }

    /**
     * Creates an initialized serial date daily options object with the given parameters.<p>
     * 
     * @param everyWorkingDay if true, the entry should occur every working day
     * @param dailyInterval the daily interval for the day series
     */
    public CmsCalendarSerialDateDailyOptions(boolean everyWorkingDay, int dailyInterval) {

        m_everyWorkingDay = everyWorkingDay;
        m_dailyInterval = 1;
        if (dailyInterval > 0) {
            m_dailyInterval = dailyInterval;
        }
        if (m_everyWorkingDay) {
            m_dailyInterval = 1;
        }
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public Map getConfigurationValuesAsMap() {

        // create the Map containing the date settings
        Map values = new HashMap();

        // put interval and working days flag
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, String.valueOf(getDailyInterval()));
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_EVERY_WORKING_DAY, String.valueOf(isEveryWorkingDay()));

        return values;
    }

    /**
     * Returns the daily interval for the calendar entry occurences.<p>
     *
     * @return the daily interval for the calendar entry occurences
     */
    public int getDailyInterval() {

        return m_dailyInterval;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public int getSerialType() {

        return I_CmsCalendarSerialDateOptions.TYPE_DAILY;
    }

    /**
     * Returns the working days Integer values.<p>
     * 
     * @return the working days Integer values
     */
    public List getWorkingDays() {

        return m_workingDaysList;
    }

    /**
     * Returns if the entry should occur on every working day.<p>
     * 
     * @return true if the entry should occur on every working day, otherwise false
     */
    public boolean isEveryWorkingDay() {

        return m_everyWorkingDay;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#matchCalendarView(com.alkacon.opencms.calendar.CmsCalendarEntry, com.alkacon.opencms.calendar.I_CmsCalendarView, int)
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
            if (getDailyInterval() == 1
                && (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_NEVER || entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_DATE)
                && entryStartDayDate.getTimeInMillis() < viewDate.getStartDay()) {
                // skip to current view start date to optimize performance
                runDate.setTimeInMillis(viewDate.getStartDay());
            }

            // occurences counter
            int occurences = 0;

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
                if (!isEveryWorkingDay() || (isEveryWorkingDay() && getWorkingDays().contains(runWeekDayInteger))) {
                    // the current day contains a series entry
                    occurences++;
                    long entryStart = runDate.getTimeInMillis() + entryDate.getStartTime();
                    // check if current entry is in view range
                    if (entryStart >= viewStart && entryStart <= viewEnd) {
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

                if (isEveryWorkingDay()) {
                    // increase run date to next day
                    runDate.add(Calendar.DAY_OF_YEAR, 1);
                    //runDate.setTimeInMillis(runDate.getTimeInMillis() + CmsCalendarEntryDate.MILLIS_02_PER_DAY);
                } else {
                    // increase run date with day interval for next test
                    runDate.add(Calendar.DAY_OF_YEAR, getDailyInterval());
                    //runDate.setTimeInMillis(runDate.getTimeInMillis()
                    //    + CmsCalendarEntryDate.MILLIS_02_PER_DAY
                    //    * getDailyInterval());
                }
            }
        }
        return result;
    }

    /**
     * Sets the daily interval for the calendar entry occurences.<p>
     *
     * @param dailyInterval the daily interval for the calendar entry occurences
     */
    public void setDailyInterval(int dailyInterval) {

        if (dailyInterval > 0) {
            m_dailyInterval = dailyInterval;
        }
    }

    /**
     * Sets if the entry should occur on every working day.<p>
     * 
     * @param everyWorkingDay true if the entry should occur on every working day, otherwise false
     */
    public void setEveryWorkingDay(boolean everyWorkingDay) {

        m_everyWorkingDay = everyWorkingDay;
    }

    /**
     * Sets the working days as Integer values.<p>
     * 
     * Use this to change the standard working days which are from {@link Calendar#MONDAY} to {@link Calendar#FRIDAY} as {@link Integer} objects.<p>
     * 
     * @param workingDays the working days as Integer values
     */
    public void setWorkingDays(List workingDays) {

        m_workingDaysList = workingDays;
    }

    /**
     * Checks if the loop to determine the view entries can be left depending on the serial type and dates.<p>
     * 
     * @param entryDate the date information of the serial entry
     * @param runDate the Date of the current loop
     * @param viewDate the view date
     * @param occurences the current number of occurences of the serial entry
     * @return true if the loop has to be interrupted, otherwise false
     */
    private boolean checkLeaveLoop(
        CmsCalendarEntryDateSerial entryDate,
        Calendar runDate,
        CmsCalendarEntryDate viewDate,
        int occurences) {

        // for the series end type: end date, check end date
        if (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_DATE
            && runDate.after(entryDate.getSerialEndDate())) {
            // run date is after series end date, interrupt
            return true;
        }

        // for the series end type: n-times, check occurences
        if (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_TIMES
            && occurences >= entryDate.getOccurences()) {
            // interrupt after reaching maximum number of occurences
            return true;
        }

        // check if the run date is before the end date
        if (runDate.after(viewDate.getEndDate())) {
            // the run date is after the view date, interrupt loop
            return true;
        }

        return false;
    }

}