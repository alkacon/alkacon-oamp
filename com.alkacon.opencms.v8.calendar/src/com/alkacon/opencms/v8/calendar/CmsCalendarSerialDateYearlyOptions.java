/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarSerialDateYearlyOptions.java,v $
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for a yearly serial calendar entry.<p>
 * 
 * Provides the necessary information about a yearly serial calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarSerialDateYearlyOptions extends A_CmsCalendarSerialDateOptions {

    /** The number of the day of the month for the serial calendar entry. */
    private int m_dayOfMonth;

    /** The monthfor the serial calendar entry. */
    private int m_month;

    /** Indicates if the specified week day should be used or only the number of the day of the month. */
    private boolean m_useWeekDay;

    /** The week day for the serial calendar entry. */
    private int m_weekDay;

    /**
     * Creates an initialized serial date monthly options object with the standard yearly interval options.<p>
     * 
     * Standard interval options are: the first day of January.<p>
     */
    public CmsCalendarSerialDateYearlyOptions() {

        m_dayOfMonth = 1;
        m_weekDay = -1;
        m_month = 0;
        m_useWeekDay = false;

    }

    /**
     * Creates an initialized serial date yearly options object with the given parameters.<p>
     * 
     * @param dayOfMonth the number of the day of the month for the yearly series
     * @param weekDay the week day for the yearly series, if less than 1 or more than 7 the week day is not used
     * @param month the month for the yearly series
     */
    public CmsCalendarSerialDateYearlyOptions(int dayOfMonth, int weekDay, int month) {

        m_dayOfMonth = dayOfMonth;
        m_weekDay = weekDay;
        if ((weekDay < Calendar.SUNDAY) || (weekDay > Calendar.SATURDAY)) {
            m_useWeekDay = false;
        } else {
            m_useWeekDay = true;
        }
        m_month = 0;
        if ((month > -1) && (month < 12)) {
            m_month = month;
        }
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public Map getConfigurationValuesAsMap() {

        // create the Map containing the date settings
        Map values = new HashMap();

        // put day of month, week days and month to Map
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, String.valueOf(getDayOfMonth()));
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, String.valueOf(getWeekDay()));
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_MONTH, String.valueOf(getMonth()));

        return values;
    }

    /**
     * Returns the day of month for the serial entry.<p>
     * 
     * @return the day of month for the serial entry
     */
    public int getDayOfMonth() {

        return m_dayOfMonth;
    }

    /**
     * Sets the month for the serial entry.<p>
     * 
     * @return the month for the serial entry
     */
    public int getMonth() {

        return m_month;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public int getSerialType() {

        return I_CmsCalendarSerialDateOptions.TYPE_YEARLY;
    }

    /**
     * Returns the week day used for the serial entry.<p>
     * 
     * @return the week day used for the serial entry
     */
    public int getWeekDay() {

        return m_weekDay;
    }

    /**
     * Returns if the week day is used for the serial entry.<p>
     * 
     * @return true if the week day is used for the serial entry, otherwise false
     */
    public boolean isUseWeekDay() {

        return m_useWeekDay;
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
            if (((entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_NEVER) || (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_DATE))
                && (entryStartDayDate.getTimeInMillis() < viewDate.getStartDay())) {
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

                boolean foundWeekDay = false;
                // determine current day of the week and the number of the day in the current month
                if (isUseWeekDay()
                    && (runDate.get(Calendar.MONTH) == getMonth())
                    && (runDate.get(Calendar.DAY_OF_WEEK) == getWeekDay())) {
                    // now check which week day is currently shown...
                    int number = runDate.get(Calendar.DAY_OF_MONTH) / 7;
                    if ((runDate.get(Calendar.DAY_OF_MONTH) % 7) != 0) {
                        number += 1;
                    }
                    if (number == getDayOfMonth()) {
                        // we are on the matching week day of the month
                        foundWeekDay = true;
                    }
                }

                if ((!isUseWeekDay() && (runDate.get(Calendar.DAY_OF_MONTH) == getDayOfMonth()) && (runDate.get(Calendar.MONTH) == getMonth()))
                    || foundWeekDay) {
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

                // increase run date with one month for next test depending on options
                if (isUseWeekDay()) {
                    // week day is specified, roll the run date accordingly
                    if (!foundWeekDay) {
                        // correct month is not found yet
                        if (runDate.get(Calendar.MONTH) < getMonth()) {
                            runDate.set(Calendar.MONTH, getMonth());
                            runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                        } else if (runDate.get(Calendar.MONTH) > getMonth()) {
                            runDate.set(Calendar.MONTH, getMonth());
                            runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                            runDate.add(Calendar.YEAR, 1);
                        }
                        // correct week day not found yet, roll to it
                        while (runDate.get(Calendar.DAY_OF_WEEK) != getWeekDay()) {
                            runDate.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        // calculate the number of the week day in the current month
                        int number = runDate.get(Calendar.DAY_OF_MONTH) / 7;
                        if ((runDate.get(Calendar.DAY_OF_MONTH) % 7) != 0) {
                            number += 1;
                        }
                        if (number > getDayOfMonth()) {
                            // we are already past the specified date
                            runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                            runDate.add(Calendar.YEAR, 1);
                        } else {
                            // try to go to the specified week day
                            int oldMonth = runDate.get(Calendar.MONTH);
                            for (int k = 0; k < (getDayOfMonth() - number); k++) {
                                runDate.add(Calendar.WEEK_OF_YEAR, 1);
                                if (runDate.get(Calendar.MONTH) != oldMonth) {
                                    // we have come to the next month, set day to the first day of the previous month to check next year and leave loop
                                    runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                                    runDate.add(Calendar.MONTH, -1);
                                    runDate.add(Calendar.YEAR, 1);
                                    break;
                                }
                            }
                        }
                    } else {
                        // found week day, set month day to first day of the month and go to the next year
                        runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                        runDate.add(Calendar.YEAR, 1);
                    }
                } else {
                    // only the number of the day of the month is specified
                    if (runDate.get(Calendar.MONTH) < getMonth()) {
                        // we are before the month, go forward to it and set the correct day
                        runDate.add(Calendar.MONTH, getMonth() - runDate.get(Calendar.MONTH));
                    } else {
                        // we are at specified month or beyond it, go to next year
                        runDate.add(Calendar.YEAR, 1);
                        runDate.set(Calendar.MONTH, getMonth());
                    }
                    if (runDate.get(Calendar.DAY_OF_MONTH) != getDayOfMonth()) {
                        runDate.set(Calendar.DAY_OF_MONTH, getDayOfMonth());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Sets the day of month for the serial entry.<p>
     * 
     * @param dayOfMonth the day of month for the serial entry
     */
    public void setDayOfMonth(int dayOfMonth) {

        m_dayOfMonth = dayOfMonth;
    }

    /**
     * Sets the month for the serial entry.<p>
     * 
     * @param month the month for the serial entry
     */
    public void setMonthlyInterval(int month) {

        m_month = month;
    }

    /**
     * Sets if the week day is used for the serial entry.<p>
     * 
     * @param useWeekDay true if the week day is used for the serial entry, otherwise false
     */
    public void setUseWeekDay(boolean useWeekDay) {

        m_useWeekDay = useWeekDay;
    }

    /**
     * Sets the week day used for the serial entry.<p>
     * 
     * @param weekDay the week day used for the serial entry
     */
    public void setWeekDay(int weekDay) {

        m_weekDay = weekDay;
    }

}