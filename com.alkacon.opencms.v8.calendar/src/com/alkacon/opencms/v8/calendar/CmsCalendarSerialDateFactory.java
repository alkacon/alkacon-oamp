/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarSerialDateFactory.java,v $
 * Date   : $Date: 2008/12/09 14:27:12 $
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Factory class that provides methods to create serial date instances from a property value Map.<p>
 * 
 * @author Andreas Zahner
 */
public final class CmsCalendarSerialDateFactory {

    /** Property name for the serial date options property. */
    public static final String PROPERTY_SERIALDATE = "calendar.dateserial";

    /** Separator for the week days String. */
    public static final char SEPARATOR_WEEKDAYS = ',';

    /**
     * Hidden constructor.<p>
     */
    private CmsCalendarSerialDateFactory() {

        // hide constructor
    }

    /**
     * Creates a serial date entry from the given property value.<p>
     * 
     * If no matching serial date could be created, <code>null</code> is returned.<p>
     * 
     * @param values the Map containing the date configuration values
     * @param locale the locale to use for the calendar date objects
     * @return a serial date entry from the given property valu
     */
    public static CmsCalendarEntryDateSerial getSerialDate(Map values, Locale locale) {

        // first set serial date fields used by all serial types

        // fetch the start date and time
        Calendar start = new GregorianCalendar(locale);
        String startLong = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_STARTDATE);
        start.setTimeInMillis(getLongValue(startLong, 0));

        // the end date and time (this means the duration of a single entry)
        Calendar end = new GregorianCalendar(locale);
        String endLong = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_ENDDATE);
        end.setTimeInMillis(getLongValue(endLong, 0));

        // now create the entry
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(start, end);

        // determine the serial end type
        String endTypeStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_END_TYPE);
        int endType = getIntValue(endTypeStr, I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        serialDate.setSerialEndType(endType);
        if (endType == I_CmsCalendarSerialDateOptions.END_TYPE_TIMES) {
            // end type: after a number of occurences
            String occurStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_OCCURENCES);
            serialDate.setOccurences(getIntValue(occurStr, 0));
        } else if (endType == I_CmsCalendarSerialDateOptions.END_TYPE_DATE) {
            // end type: ends at a specified date
            String endDateStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_SERIAL_ENDDATE);
            long endDate = getLongValue(endDateStr, 0);
            Calendar endSerial = new GregorianCalendar(locale);
            endSerial.setTimeInMillis(endDate);
            serialDate.setSerialEndDate(endSerial);
        }

        // now determine the serial date options depending on the serial date type
        I_CmsCalendarSerialDateOptions options = null;
        String type = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_TYPE);
        int entryType = getIntValue(type, 0);

        switch (entryType) {
            case I_CmsCalendarSerialDateOptions.TYPE_DAILY:
                // daily series entry, get interval and working days flag
                String intervalStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL);
                int interval = getIntValue(intervalStr, 1);
                String workingDaysStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_EVERY_WORKING_DAY);
                boolean workingDays = Boolean.valueOf(workingDaysStr).booleanValue();
                options = new CmsCalendarSerialDateDailyOptions(workingDays, interval);
                break;
            case I_CmsCalendarSerialDateOptions.TYPE_WEEKLY:
                // weekly series entry
                intervalStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL);
                interval = getIntValue(intervalStr, 1);
                String weekDaysStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS);
                List weekDaysStrList = CmsStringUtil.splitAsList(weekDaysStr, SEPARATOR_WEEKDAYS, true);
                options = new CmsCalendarSerialDateWeeklyOptions(convertToInteger(weekDaysStrList), interval);
                break;
            case I_CmsCalendarSerialDateOptions.TYPE_MONTHLY:
                // monthly series entry
                intervalStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL);
                interval = getIntValue(intervalStr, 1);
                String dayOfMonthStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH);
                int dayOfMonth = getIntValue(dayOfMonthStr, 1);
                String weekDayStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS);
                int weekDay = getIntValue(weekDayStr, -1);
                options = new CmsCalendarSerialDateMonthlyOptions(dayOfMonth, weekDay, interval);
                break;
            case I_CmsCalendarSerialDateOptions.TYPE_YEARLY:
                // yearly series entry
                dayOfMonthStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH);
                dayOfMonth = getIntValue(dayOfMonthStr, 1);
                weekDayStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS);
                weekDay = getIntValue(weekDayStr, -1);
                String monthStr = (String)values.get(I_CmsCalendarSerialDateOptions.CONFIG_MONTH);
                int month = getIntValue(monthStr, 0);
                options = new CmsCalendarSerialDateYearlyOptions(dayOfMonth, weekDay, month);
                break;
            default:
                // no valid entry type specified
                return null;
        }
        serialDate.setSerialOptions(options);

        return serialDate;
    }

    /**
     * Returns a list of Integer objects from the given list of String values.<p>
     * 
     * @param source the source list with String values
     * @return a list of Integer objects
     */
    protected static List convertToInteger(List source) {

        List result = new ArrayList(source.size());
        Iterator i = source.iterator();
        while (i.hasNext()) {
            String current = (String)i.next();
            int value = getIntValue(current, -1);
            if (value != -1) {
                result.add(new Integer(value));
            }
        }
        return result;
    }

    /**
     * Returns the int value of the given String or the default value if parsing the String fails.<p>
     * 
     * @param strValue the String to parse
     * @param defaultValue the default value to use if parsing fails
     * @return the int value of the given String
     */
    protected static int getIntValue(String strValue, int defaultValue) {

        int result = defaultValue;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
            try {
                result = Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                // no number, use default value
            }
        }
        return result;
    }

    /**
     * Returns the long value of the given String or the default value if parsing the String fails.<p>
     * 
     * @param strValue the String to parse
     * @param defaultValue the default value to use if parsing fails
     * @return the long value of the given String
     */
    protected static long getLongValue(String strValue, long defaultValue) {

        long result = defaultValue;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
            try {
                result = Long.parseLong(strValue);
            } catch (NumberFormatException e) {
                // no number, use default value
            }
        }
        return result;
    }

}
