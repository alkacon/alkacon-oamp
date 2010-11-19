/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarEntryDate.java,v $
 * Date   : $Date: 2010/11/19 14:53:04 $
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

package com.alkacon.opencms.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Stores the date information of a single calendar entry.<p>
 * 
 * This is basically the start and end date of the entry with some helper methods to determine duration and time information
 * more easily.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.1 
 */
public class CmsCalendarEntryDate {

    /** Number of milliseconds per minute. */
    public static final long MILLIS_00_PER_MINUTE = 1000 * 60;

    /** Number of milliseconds per hour. */
    public static final long MILLIS_01_PER_HOUR = MILLIS_00_PER_MINUTE * 60;

    /** Number of milliseconds per day. */
    public static final long MILLIS_02_PER_DAY = MILLIS_01_PER_HOUR * 24;

    /** Number of milliseconds per week. */
    public static final long MILLIS_03_PER_WEEK = MILLIS_02_PER_DAY * 7;

    /** The duration of the entry (in days). */
    private int m_duration;

    /** The end date of the entry. */
    private Calendar m_endDate;

    /** The end time of the entry. */
    private Calendar m_endTime;

    /** The start date of the entry. */
    private Calendar m_startDate;

    /** The start day of the entry. */
    private long m_startDay;

    /** The start time of the entry. */
    private Calendar m_startTime;

    /**
     * Constructor that initializes the members using the given start and end date.<p>
     * 
     * @param startDate the start date of the entry
     * @param endDate the end date of the entry
     */
    public CmsCalendarEntryDate(Calendar startDate, Calendar endDate) {

        m_startDate = startDate;
        m_endDate = endDate;
        m_startTime = new GregorianCalendar(2010, 0, 10, 0, 0, 0);
        m_endTime = new GregorianCalendar(2010, 0, 10, 0, 0, 0);
        // calculate the time information for the other members
        calculateEntryTimes();
    }

    /**
     * Constructor that directly initializes the members.<p>
     * 
     * @param startDay the start day of the entry
     * @param startTime the start time of the entry
     * @param endTime the end time of the entry
     * @param duration the duration of the entry (in days)
     */
    public CmsCalendarEntryDate(long startDay, long startTime, long endTime, int duration) {

        m_startDay = startDay;
        m_startTime = getTimeCalendar(startTime);
        m_endTime = getTimeCalendar(endTime);
        m_duration = duration;
        // calculate start and end date
        calculateEntryDates();
    }

    private Calendar getTimeCalendar(long time) {

        long h = time % MILLIS_01_PER_HOUR;
        time = time - h;
        long m = time % MILLIS_00_PER_MINUTE;
        Calendar cal = new GregorianCalendar(2010, 0, 10, (int)h, (int)m, 0);
        return cal;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {

        return new CmsCalendarEntryDate(m_startDate, m_endDate);
    }

    /**
     * Returns the duration of the entry (in days).<p>
     *
     * @return the duration of the entry (in days)
     */
    public int getDuration() {

        return m_duration;
    }

    /**
     * Returns the end date of the entry.<p>
     *
     * @return the end date of the entry
     */
    public Calendar getEndDate() {

        return m_endDate;
    }

    /**
     * Returns the end time of the entry.<p>
     *
     * @return the end time of the entry
     */
    public long getEndTime() {

        return m_endDate.get(Calendar.HOUR_OF_DAY)
            * MILLIS_01_PER_HOUR
            + m_endDate.get(Calendar.MINUTE)
            * MILLIS_00_PER_MINUTE;
    }

    /**
     * Returns the start date of the entry.<p>
     *
     * @return the start date of the entry
     */
    public Calendar getStartDate() {

        return m_startDate;
    }

    /**
     * Returns the start day of the entry.<p>
     *
     * @return the start day of the entry
     */
    public long getStartDay() {

        return m_startDay;
    }

    /**
     * Returns the start time of the entry.<p>
     *
     * @return the start time of the entry
     */
    public long getStartTime() {

        return m_startDate.get(Calendar.HOUR_OF_DAY)
            * MILLIS_01_PER_HOUR
            + m_startDate.get(Calendar.MINUTE)
            * MILLIS_00_PER_MINUTE;
    }

    /**
     * Returns if the date entry is a serial date or not.<p>
     * 
     * @return true if the date entry is a serial date, otherwise false
     */
    public boolean isSerialDate() {

        return false;
    }

    /**
     * Returns the list of matching entries for the given calendar view.<p>
     * 
     * @param entry the calendar entry to check
     * @param calendarView the calendar view 
     * @return the matching calendar entries
     */
    public List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView) {

        List result = new ArrayList();
        Comparator comparator = calendarView.getComparator();

        for (int i = 0; i < calendarView.getDates().size(); i++) {
            CmsCalendarEntryDate viewDate = (CmsCalendarEntryDate)calendarView.getDates().get(i);

            // check if the entry date matches the view date, if so, add it to the list
            if (comparator.compare(viewDate, this) == 0) {
                result.add(entry);
            }
        }

        return result;
    }

    /**
     * Sets the duration of the entry (in days).<p>
     *
     * @param duration the duration of the entry (in days)
     */
    public void setDuration(int duration) {

        m_duration = duration;
        calculateEntryDates();
    }

    /**
     * Sets the end date of the entry.<p>
     *
     * @param endDate the end date of the entry
     */
    public void setEndDate(Calendar endDate) {

        m_endDate = endDate;
        calculateEntryTimes();
    }

    /**
     * Sets the end time of the entry.<p>
     *
     * @param endTime the end time of the entry
     */
    public void setEndTime(long endTime) {

        m_endTime = getTimeCalendar(endTime);
        calculateEntryDates();
    }

    /**
     * Sets the start date of the entry.<p>
     *
     * @param startDate the start date of the entry
     */
    public void setStartDate(Calendar startDate) {

        m_startDate = startDate;
        calculateEntryTimes();
    }

    /**
     * Sets the start date of the entry and adjusts only the end time if required.<p>
     *
     * @param startDate the start date of the entry
     * @param adjustEndTimeOnly flag indicating that only the end time should be updated according to the new start time
     */
    public void setStartDate(Calendar startDate, boolean adjustEndTimeOnly) {

        if (adjustEndTimeOnly) {
            long newStartTime = (startDate.get(Calendar.HOUR_OF_DAY) * MILLIS_01_PER_HOUR)
                + (startDate.get(Calendar.MINUTE) * MILLIS_00_PER_MINUTE);
            if (newStartTime != getStartTime()) {
                setEndTime(getEndTime() + (newStartTime - getStartTime()));
            }
            m_startDate = startDate;
        } else {
            m_startDate = startDate;
            calculateEntryTimes();
        }

    }

    /**
     * Sets the start day of the entry.<p>
     *
     * @param startDay the start day of the entry
     */
    public void setStartDay(long startDay) {

        m_startDay = startDay;
        calculateEntryDates();
    }

    /**
     * Sets the start time of the entry.<p>
     *
     * @param startTime the start time of the entry
     */
    public void setStartTime(long startTime) {

        m_startTime = getTimeCalendar(startTime);
        calculateEntryDates();
    }

    /**
     * Calculates the start and end date members from the given time information.<p>
     */
    private void calculateEntryDates() {

        // create the start date
        m_startDate = new GregorianCalendar();
        m_startDate.setTimeInMillis(m_startDay);
        m_startDate.set(Calendar.HOUR_OF_DAY, m_startTime.get(Calendar.HOUR_OF_DAY));
        m_startDate.set(Calendar.MINUTE, m_startTime.get(Calendar.MINUTE));
        // create the end date
        m_endDate = new GregorianCalendar();
        m_endDate.setTimeInMillis(m_startDay);
        m_endDate.set(Calendar.HOUR_OF_DAY, m_endTime.get(Calendar.HOUR_OF_DAY));
        m_endDate.set(Calendar.MINUTE, m_endTime.get(Calendar.MINUTE));
        m_endDate.roll(Calendar.DAY_OF_MONTH, m_duration);
    }

    /**
     * Calculates the start and end time members from the given date information.<p>
     */
    private void calculateEntryTimes() {

        // calculate the time information for the other members
        Calendar startDay = new GregorianCalendar(
            m_startDate.get(Calendar.YEAR),
            m_startDate.get(Calendar.MONTH),
            m_startDate.get(Calendar.DATE));
        m_startDay = startDay.getTimeInMillis();
        m_startTime.set(Calendar.HOUR_OF_DAY, m_startDate.get(Calendar.HOUR_OF_DAY));
        m_startTime.set(Calendar.MINUTE, m_startDate.get(Calendar.MINUTE));
        m_endTime.set(Calendar.HOUR_OF_DAY, m_endDate.get(Calendar.HOUR_OF_DAY));
        m_endTime.set(Calendar.MINUTE, m_endDate.get(Calendar.MINUTE));
        m_duration = 0;
        // calculate the duration of the entry
        Calendar endDay = new GregorianCalendar(
            m_endDate.get(Calendar.YEAR),
            m_endDate.get(Calendar.MONTH),
            m_endDate.get(Calendar.DATE));
        if (endDay.getTimeInMillis() > startDay.getTimeInMillis()) {
            // duration at least one day, calculate it
            long delta = endDay.getTimeInMillis() - startDay.getTimeInMillis();
            m_duration = new Long(delta / MILLIS_02_PER_DAY).intValue();
        }
    }

}