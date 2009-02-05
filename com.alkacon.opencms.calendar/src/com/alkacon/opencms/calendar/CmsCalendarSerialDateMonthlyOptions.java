/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarSerialDateMonthlyOptions.java,v $
 * Date   : $Date: 2009/02/05 09:49:31 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for a monthly serial calendar entry.<p>
 * 
 * Provides the necessary information about a monthly serial calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarSerialDateMonthlyOptions extends A_CmsCalendarSerialDateOptions {

    /** The number of the day of the month for the serial calendar entry. */
    private int m_dayOfMonth;

    /** The monthly interval for the serial calendar entry. */
    private int m_monthlyInterval;

    /** Indicates if the specified week day should be used or only the number of the day of the month. */
    private boolean m_useWeekDay;

    /** The week day for the serial calendar entry. */
    private int m_weekDay;

    /**
     * Creates an initialized serial date monthly options object with the standard monthly interval options.<p>
     * 
     * Standard interval options are: the first day of month every month.<p>
     */
    public CmsCalendarSerialDateMonthlyOptions() {

        m_dayOfMonth = 1;
        m_weekDay = -1;
        m_monthlyInterval = 1;
        m_useWeekDay = false;

    }

    /**
     * Creates an initialized serial date monthly options object with the given parameters.<p>
     * 
     * @param dayOfMonth the number of the day of the month for the monthly series
     * @param weekDay the week day for the monthly series, if less than 1 or more than 7 the week day is not used
     * @param monthlyInterval the weekly interval for the monthly series
     */
    public CmsCalendarSerialDateMonthlyOptions(int dayOfMonth, int weekDay, int monthlyInterval) {

        m_dayOfMonth = dayOfMonth;
        m_weekDay = weekDay;
        if (weekDay < Calendar.SUNDAY || weekDay > Calendar.SATURDAY) {
            m_useWeekDay = false;
        } else {
            m_useWeekDay = true;
        }
        m_monthlyInterval = 1;
        if (monthlyInterval > 0) {
            m_monthlyInterval = monthlyInterval;
        }
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public Map getConfigurationValuesAsMap() {

        // create the Map containing the date settings
        Map values = new HashMap();

        // put interval, day of month and week days to Map
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, String.valueOf(getMonthlyInterval()));
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, String.valueOf(getDayOfMonth()));
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, String.valueOf(getWeekDay()));

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
     * Sets the monthly interval for the serial entry.<p>
     * 
     * @return the monthly interval for the serial entry
     */
    public int getMonthlyInterval() {

        return m_monthlyInterval;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public int getSerialType() {

        return I_CmsCalendarSerialDateOptions.TYPE_MONTHLY;
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
            if (getMonthlyInterval() == 1
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

                boolean foundWeekDay = false;
                // determine current day of the week and the number of the day in the current month
                if (isUseWeekDay() && runDate.get(Calendar.DAY_OF_WEEK) == getWeekDay()) {
                    // now check which week day is currently shown...
                    int number = runDate.get(Calendar.DAY_OF_MONTH) / 7;
                    if (runDate.get(Calendar.DAY_OF_MONTH) % 7 != 0) {
                        number += 1;
                    }
                    if (number == getDayOfMonth()) {
                        // we are on the matching week day of the month
                        foundWeekDay = true;
                    }
                }

                if ((!isUseWeekDay() && runDate.get(Calendar.DAY_OF_MONTH) == getDayOfMonth()) || foundWeekDay) {
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

                // increase run date with one month for next test depending on options
                if (isUseWeekDay()) {
                    // week day is specified, roll the run date accordingly
                    if (!foundWeekDay) {
                        // correct week day not found yet, roll to it
                        while (runDate.get(Calendar.DAY_OF_WEEK) != getWeekDay()) {
                            runDate.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        // calculate the number of the week day in the current month
                        int number = runDate.get(Calendar.DAY_OF_MONTH) / 7;
                        if (runDate.get(Calendar.DAY_OF_MONTH) % 7 != 0) {
                            number += 1;
                        }
                        if (number > getDayOfMonth()) {
                            // we are already past the specified date
                            runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                            runDate.add(Calendar.MONTH, getMonthlyInterval() - 1);
                        } else {
                            // try to go to the specified week day
                            int oldMonth = runDate.get(Calendar.MONTH);
                            for (int k = 0; k < (getDayOfMonth() - number); k++) {
                                runDate.add(Calendar.WEEK_OF_YEAR, 1);
                                if (runDate.get(Calendar.MONTH) != oldMonth) {
                                    // we have come to the next month, set day to the first day of the next month to check and leave loop
                                    runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                                    runDate.add(Calendar.MONTH, getMonthlyInterval() - 1);
                                    break;
                                }
                            }
                        }
                    } else {
                        // found week day, go to the first day of the next month to check
                        runDate.add(Calendar.DAY_OF_MONTH, -(runDate.get(Calendar.DAY_OF_MONTH) - 1));
                        runDate.add(Calendar.MONTH, getMonthlyInterval());
                    }
                } else {
                    // only the number of the day of the month is specified
                    if (runDate.getActualMaximum(Calendar.DAY_OF_MONTH) < getDayOfMonth()) {
                        runDate.add(Calendar.MONTH, getMonthlyInterval());
                    } else {
                        if (runDate.get(Calendar.DAY_OF_MONTH) < getDayOfMonth()) {
                            // go forward to the specified day of the month
                            runDate.add(Calendar.DAY_OF_MONTH, (getDayOfMonth() - runDate.get(Calendar.DAY_OF_MONTH)));
                        } else {
                            // we are exactly at the specified day or beyond it, skip months
                            if (runDate.get(Calendar.DAY_OF_MONTH) > getDayOfMonth()) {
                                runDate.set(Calendar.DAY_OF_MONTH, getDayOfMonth());
                            }
                            runDate.add(Calendar.MONTH, getMonthlyInterval());
                        }
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
     * Sets the monthly interval for the serial entry.<p>
     * 
     * @param monthlyInterval the monthly interval for the serial entry
     */
    public void setMonthlyInterval(int monthlyInterval) {

        m_monthlyInterval = monthlyInterval;
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