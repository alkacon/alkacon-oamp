/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarEntryDateSerial.java,v $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the serial date information of a single calendar entry.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarEntryDateSerial extends CmsCalendarEntryDate {

    /** The occurences of a series interval. */
    private int m_occurences;

    /** The end date of a series. */
    private Calendar m_serialEndDate;

    /** The end type of a series. */
    private int m_serialEndType;

    /** The serial date options. */
    private I_CmsCalendarSerialDateOptions m_serialOptions;

    /**
     * Constructor that initializes the members using the given start and end date.<p>
     * 
     * @param startDate the start date of the entry
     * @param endDate the end date of the entry
     */
    public CmsCalendarEntryDateSerial(Calendar startDate, Calendar endDate) {

        super(startDate, endDate);
        m_serialEndType = -1;
    }

    /**
     * Constructor that directly initializes the members.<p>
     * 
     * @param startDay the start day of the entry
     * @param startTime the start time of the entry
     * @param endTime the end time of the entry
     * @param duration the duration of the entry (in days)
     */
    public CmsCalendarEntryDateSerial(long startDay, long startTime, long endTime, int duration) {

        super(startDay, startTime, endTime, duration);
        m_serialEndType = -1;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {

        CmsCalendarEntryDateSerial clone = new CmsCalendarEntryDateSerial(getStartDate(), getEndDate());
        clone.setSerialEndType(m_serialEndType);
        clone.setOccurences(m_occurences);
        clone.setSerialOptions(m_serialOptions);
        return clone;
    }

    /**
     * Returns the configuration values for the serial date as Map, including the individual serial options depending on the type.<p>
     * 
     * This Map can be used to store the configured options as property value on VFS resources.<p>
     * 
     * @return the configuration values for the serial date as Map
     */
    public Map<String, String> getConfigurationValuesAsMap() {

        // create the Map containing the date settings
        Map<String, String> values = new HashMap<String, String>();

        // first put the values of serial date fields used by all serial types

        // fetch the start date and time
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_STARTDATE, String.valueOf(getStartDate().getTimeInMillis()));
        // the end date and time (this means the duration of a single entry)
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_ENDDATE, String.valueOf(getEndDate().getTimeInMillis()));

        // store the serial type
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_TYPE, String.valueOf(getSerialOptions().getSerialType()));

        // set the end type specific values
        int endType = getSerialEndType();
        values.put(I_CmsCalendarSerialDateOptions.CONFIG_END_TYPE, String.valueOf(endType));
        if (endType == I_CmsCalendarSerialDateOptions.END_TYPE_TIMES) {
            // end type: after a number of occurences
            values.put(I_CmsCalendarSerialDateOptions.CONFIG_OCCURENCES, String.valueOf(getOccurences()));
        } else if (endType == I_CmsCalendarSerialDateOptions.END_TYPE_DATE) {
            // end type: ends at a specified date
            values.put(
                I_CmsCalendarSerialDateOptions.CONFIG_SERIAL_ENDDATE,
                String.valueOf(getSerialEndDate().getTimeInMillis()));
        }

        // now put the individual serial date options to the Map
        values.putAll(getSerialOptions().getConfigurationValuesAsMap());

        return values;
    }

    /**
     * Returns the occurences of a defined series interval, used for the series end type.<p>
     *
     * @return the occurences of a defined series interval, used for the series end type
     */
    public int getOccurences() {

        return m_occurences;
    }

    /**
     * Returns the serial end date if the series is of type: ending at specific date.<p>
     *
     * @return the serial end date if the series is of type: ending at specific date
     */
    public Calendar getSerialEndDate() {

        return m_serialEndDate;
    }

    /**
     * Returns the end type of the date series (never, n times, specific date).<p>
     * 
     * @return the end type of the date series
     */
    public int getSerialEndType() {

        return m_serialEndType;
    }

    /**
     * Returns the serial date options for the date series.<p>
     * 
     * @return the serial date options for the date series
     */
    public I_CmsCalendarSerialDateOptions getSerialOptions() {

        return m_serialOptions;
    }

    /**
     * Initializes the serial date options with the given values.<p>
     * 
     * @param endType the end type of the serial date
     * @param options the serial date options
     */
    public void initSerialDate(int endType, I_CmsCalendarSerialDateOptions options) {

        m_serialEndType = endType;
        m_serialOptions = options;
    }

    /**
     * Initializes the serial date options with the given values.<p>
     * 
     * @param endType the end type of the serial date
     * @param occurences the number of occurences for the serial type that defines n occurences of the series
     * @param options the serial date options
     */
    public void initSerialDate(int endType, int occurences, I_CmsCalendarSerialDateOptions options) {

        m_serialEndType = endType;
        m_occurences = occurences;
        m_serialOptions = options;
    }

    /**
     * Returns if the date entry is a serial date or not.<p>
     * 
     * @return true if the date entry is a serial date, otherwise false
     */
    public boolean isSerialDate() {

        return ((m_serialEndType != -1) && (m_serialOptions != null));
    }

    /**
     * Returns the list of matching entries for the calendar view.<p>
     * 
     * @param entry the calendar entry to check
     * @param calendarView the calendar view
     * @return the matching calendar entries
     */
    public List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView) {

        return matchCalendarView(entry, calendarView, Integer.MAX_VALUE);
    }

    /**
     * Returns the list of matching entries for the calendar view.<p>
     * 
     * @param entry the calendar entry to check
     * @param calendarView the calendar view
     * @param maxCount the maximum count of returned serial entries
     * @return the matching calendar entries
     */
    public List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView, int maxCount) {

        if (isSerialDate()) {
            return m_serialOptions.matchCalendarView(entry, calendarView, maxCount);
        } else {
            return super.matchCalendarView(entry, calendarView);
        }
    }

    /**
     * Sets the occurences of a defined series interval, used for the series end type.<p>
     *
     * @param occurences the occurences of a defined series interval, used for the series end type
     */
    public void setOccurences(int occurences) {

        m_occurences = occurences;
    }

    /**
     * Sets the serial end date if the series is of type: ending at specific date.<p>
     *
     * @param serialEndDate the serial end date if the series is of type: ending at specific date
     */
    public void setSerialEndDate(Calendar serialEndDate) {

        m_serialEndDate = serialEndDate;
    }

    /**
     * Sets the end type of the date series (never, n times, specific date).<p>
     * 
     * @param endType the end type of the date series
     */
    public void setSerialEndType(int endType) {

        m_serialEndType = endType;
    }

    /**
     * Sets the serial date options for the date series.<p>
     * 
     * @param serialOptions the serial date options for the date series
     */
    public void setSerialOptions(I_CmsCalendarSerialDateOptions serialOptions) {

        m_serialOptions = serialOptions;
    }
}