/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarMonthBean.java,v $
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

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;

/**
 * Provides help methods to display monthly views of calendar entries.<p>
 * 
 * This includes methods to build the complete HTML output for a single month and CSS style settings that are used by
 * the build methods.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarMonthBean extends CmsCalendarDisplay {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCalendarMonthBean.class);

    /** The URI to the view which is displayed when clicking on a day. */
    private String m_viewUri;

    /**
     * Constructor with an initialized calendar object and JSP action element.<p>
     * 
     * @param jsp the JSP action element to use
     */
    public CmsCalendarMonthBean(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Builds the HTML output to create a basic calendar overview of the current month or the month based on request parameters
     * if present including a month navigation.<p>
     * 
     * The calendar Locale to use is determined from the current request context.<p>
     * 
     * @return the HTML output to create a basic calendar overview of the current month
     */
    public String buildCalendarMonth() {

        return buildCalendarMonth(getJsp().getRequestContext().getLocale());
    }

    /**
     * Builds the HTML output to create a basic calendar month overview with month navigation.<p>
     * 
     * This method serves as a simple example to create a basic html calendar monthly view.<p>
     * 
     * @param year the year of the month to display
     * @param month the month to display 
     * @param calendarLocale the Locale for the calendar to determine the start day of the weeks
     * @return the HTML output to create a basic calendar month overview
     */
    public String buildCalendarMonth(int year, int month, Locale calendarLocale) {

        return buildCalendarMonth(year, month, calendarLocale, true);
    }

    /**
     * Builds the HTML output to create a basic calendar month overview.<p>
     * 
     * This method serves as a simple example to create a basic html calendar monthly view.<p>
     * 
     * @param year the year of the month to display
     * @param month the month to display 
     * @param calendarLocale the Locale for the calendar to determine the start day of the weeks
     * @param showNavigation if true, navigation links to switch the month are created, otherwise not
     * @return the HTML output to create a basic calendar month overview
     */
    public String buildCalendarMonth(int year, int month, Locale calendarLocale, boolean showNavigation) {

        StringBuffer result = new StringBuffer(1024);

        Map dates = getMonthDaysMatrix(year, month, calendarLocale);
        Map monthEntries = getEntriesForMonth(year, month);

        // calculate the start day of the week
        Calendar calendar = new GregorianCalendar(calendarLocale);
        int weekStart = calendar.getFirstDayOfWeek();
        // store current calendar date
        Calendar currentCalendar = (Calendar)calendar.clone();

        // init the date format symbols
        DateFormatSymbols calendarSymbols = new DateFormatSymbols(calendarLocale);

        // open the table
        result.append("<table class=\"");
        result.append(getStyle().getStyleTable());
        result.append("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");

        // create the calendar navigation row
        result.append(buildMonthNavigation(year, month, currentCalendar, calendarLocale, showNavigation));

        // create the week day row
        result.append("<tr>\n");
        int currWeekDay = weekStart;
        for (int i = 1; i <= 7; i++) {

            result.append("\t<td class=\"");
            result.append(getStyle().getStyleWeekdays());
            result.append("\">");
            result.append(calendarSymbols.getShortWeekdays()[currWeekDay]);
            result.append("</td>\n");
            // check if we are at end of week
            if (currWeekDay == Calendar.SATURDAY) {
                currWeekDay = 0;
            }

            currWeekDay++;
        }
        result.append("</tr>\n");

        // now create the entry rows
        result.append("<tr>\n");

        // iterate the index entries of the matrix
        Iterator i = dates.keySet().iterator();
        while (i.hasNext()) {
            Integer index = (Integer)i.next();
            result.append("\t<td class=\"");
            Calendar currDay = (Calendar)dates.get(index);
            if (currDay != null) {
                // current index represents a day, create day output
                String styleDayCell = getStyle().getStyleDay();
                if (isCurrentDay(currentCalendar, currDay)) {
                    // for the current day, use another cell style
                    styleDayCell = getStyle().getStyleDayCurrent();
                }
                // get entries for the day
                List dayEntries = (List)monthEntries.get(currDay.getTime());
                if (dayEntries.size() > 0) {
                    // current day has calendar entries
                    int weekdayStatus = 0;
                    int holidayEntries = 0;
                    int commonEntries = dayEntries.size();
                    StringBuffer dayText = new StringBuffer(128);
                    // check all entries for special weekday status entries
                    for (int k = 0; k < commonEntries; k++) {
                        CmsCalendarEntry entry = (CmsCalendarEntry)dayEntries.get(k);
                        int entryWeekdayStatus = entry.getEntryData().getWeekdayStatus();
                        if (entryWeekdayStatus > 0) {
                            // entry is a special weekday
                            holidayEntries++;
                            // append special day info to title info
                            dayText.append(entry.getEntryData().getTitle());
                            dayText.append(" - ");
                            if (entryWeekdayStatus > weekdayStatus) {
                                // increase the status of the weekday
                                weekdayStatus = entryWeekdayStatus;
                            }
                        }
                    }
                    // calculate the count of common calendar entries
                    commonEntries = commonEntries - holidayEntries;
                    // determine the CSS class to use
                    String dayStyle = getWeekdayStyle(currDay.get(Calendar.DAY_OF_WEEK), weekdayStatus);
                    result.append(styleDayCell);
                    result.append("\" title=\"");
                    result.append(dayText);
                    // check the number of common entries and generate output of entry count
                    if (commonEntries <= 0) {
                        // no entry found
                        result.append(getMessages().key("calendar.entries.count.none"));
                    } else if (commonEntries == 1) {
                        // one entry found
                        result.append(getMessages().key("calendar.entries.count.one"));
                    } else {
                        // more than one entry found
                        result.append(getMessages().key(
                            "calendar.entries.count.more",
                            new String[] {String.valueOf(commonEntries)}));
                    }
                    result.append("\">");
                    if (commonEntries > 0) {
                        // common entries present, create link to the overview page 
                        result.append("<a href=\"");
                        result.append(createLink(currDay, m_viewUri, true, -1));
                        result.append("\" class=\"");
                        result.append(getStyle().getStyleDayEntryLink());
                        result.append("\">");
                    }
                    result.append("<span class=\"");
                    result.append(dayStyle);
                    result.append("\">");
                    result.append(currDay.get(Calendar.DAY_OF_MONTH));
                    result.append("</span>");
                    if (commonEntries > 0) {
                        // common entries present, close link
                        result.append("</a>");
                    }
                } else {
                    // current day has no entries
                    result.append(styleDayCell);
                    result.append("\" title=\"");
                    result.append(getMessages().key("calendar.entries.count.none"));
                    result.append("\">");
                    result.append("<span class=\"");
                    result.append(getWeekdayStyle(
                        currDay.get(Calendar.DAY_OF_WEEK),
                        I_CmsCalendarEntryData.WEEKDAYSTATUS_WORKDAY));
                    result.append("\">");
                    result.append(currDay.get(Calendar.DAY_OF_MONTH));
                    result.append("</span>");
                }
            } else {
                // this is an empty cell
                result.append(getStyle().getStyleDayEmpty());
                result.append("\">");
            }
            result.append("</td>\n");
            if ((index.intValue() % 7) == 0) {
                // append closing row tag
                result.append("</tr>\n");
                if (i.hasNext()) {
                    // open next row if more elements are present
                    result.append("<tr>\n");
                }
            }
        }

        // close the table
        result.append("</table>");
        return result.toString();
    }

    /**
     * Builds the HTML output to create a basic calendar overview of the current month or the month based on request parameters
     * if present, including a month navigation row.<p>
     * 
     * @param calendarLocale the Locale for the calendar to determine the start day of the weeks
     * @return the HTML output to create a basic calendar overview of the current month
     */
    public String buildCalendarMonth(Locale calendarLocale) {

        Calendar calendar = new GregorianCalendar(calendarLocale);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        // get date parameters from request
        String yearParam = getJsp().getRequest().getParameter(PARAM_YEAR);
        String monthParam = getJsp().getRequest().getParameter(PARAM_MONTH);
        if (CmsStringUtil.isNotEmpty(yearParam) && CmsStringUtil.isNotEmpty(monthParam)) {
            // build calendar of month specified by given request parameters
            try {
                year = Integer.parseInt(yearParam);
                month = Integer.parseInt(monthParam);
            } catch (NumberFormatException e) {
                // wrong parameters given, log error
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_CALENDAR_REQUESTPARAMS_1,
                        getJsp().getRequestContext().getUri()));
                }
            }
        }
        return buildCalendarMonth(year, month, calendarLocale, true);
    }

    /**
     * Returns the days of a month to display in a matrix, depending on the start day of the week.<p>
     * 
     * The month matrix starts with index "1" and uses 7 columns per row to display one week in a row.
     * The value returns null if no date should be shown at the current index position.<p>
     * 
     * @param year the year of the month to display
     * @param month the month to display
     * @param calendarLocale the Locale for the calendar to determine the start day of the week
     * @return the days of a month to display in a matrix, depending on the start day of the week
     */
    public Map getMonthDaysMatrix(int year, int month, Locale calendarLocale) {

        Map monthDays = new TreeMap();
        Calendar startDay = new GregorianCalendar(year, month, 1);

        Calendar runDay = startDay;
        int index = 1;

        // calculate the start day of the week
        Calendar calendar = new GregorianCalendar(calendarLocale);
        int weekStart = calendar.getFirstDayOfWeek();

        // create empty indexes before the first day of the month
        while (runDay.get(Calendar.DAY_OF_WEEK) != weekStart) {
            monthDays.put(new Integer(index), null);
            index++;

            if (weekStart == Calendar.SATURDAY) {
                weekStart = Calendar.SUNDAY;
            } else {
                weekStart++;
            }
        }

        // create the indexes for the month dates
        while (true) {
            monthDays.put(new Integer(index), runDay.clone());
            // increase day to next day
            runDay.roll(Calendar.DAY_OF_MONTH, true);
            index++;
            if (runDay.get(Calendar.DAY_OF_MONTH) == 1) {
                // runDay has switched to the next month, stop loop
                break;
            }
        }

        // create empty indexes after the last day of the month
        int rest = (index - 1) % 7;
        if (rest > 0) {
            rest = 7 - rest;
        }
        for (int i = 0; i < rest; i++) {
            monthDays.put(new Integer(index), null);
            index++;
        }

        return monthDays;
    }

    /**
     * Sets the view URI and the default view period.<p>
     * 
     * @see com.alkacon.opencms.calendar.CmsCalendarDisplay#init(org.opencms.jsp.CmsJspActionElement)
     */
    public void init(CmsJspActionElement jsp) {

        // call super initialisation
        super.init(jsp);
        setViewPeriod(CmsCalendarDisplay.PERIOD_DAY);
        setViewUri(jsp.property(CmsCalendarDisplay.PROPERTY_CALENDAR_URI, "search", ""));
    }

    /**
     * Sets the URI to the view which is displayed when clicking on a day.<p>
     *
     * @param viewUri the URI to the view which is displayed when clicking on a day
     */
    protected void setViewUri(String viewUri) {

        m_viewUri = viewUri;
    }

    /**
     * Builds the HTML for the calendar month navigation row.<p>
     * 
     * @param year the year of the month to display
     * @param month the month to display
     * @param currentCalendar the current calendar date 
     * @param calendarLocale the Locale to use to display the calendar information
     * @param showNavigation if true, navigation links to switch the month are created, otherwise not
     * @return the HTML for the calendar month navigation row
     */
    private String buildMonthNavigation(
        int year,
        int month,
        Calendar currentCalendar,
        Locale calendarLocale,
        boolean showNavigation) {

        StringBuffer result = new StringBuffer(256);
        StringBuffer navLink = new StringBuffer(64);
        Calendar calendar;
        int monthSpan = 7;
        result.append("<tr>\n");

        if (showNavigation) {
            // create the navigation to the previous month
            monthSpan -= 2;
            result.append("\t<td class=\"");
            result.append(getStyle().getStyleNavigation());
            result.append("\" title=\"");
            result.append(getMessages().key("calendar.navigation.month.previous"));
            result.append("\"><a class=\"");
            result.append(getStyle().getStyleNavigation());
            result.append("\" href=\"");
            calendar = getPreviousPeriod(new GregorianCalendar(year, month, 1), CmsCalendarDisplay.PERIOD_MONTH);
            navLink.append(getJsp().getRequestContext().getUri());
            navLink.append("?").append(PARAM_YEAR).append("=").append(calendar.get(Calendar.YEAR));
            navLink.append("&amp;").append(PARAM_MONTH).append("=").append(calendar.get(Calendar.MONTH));
            result.append(getJsp().link(navLink.toString()));
            result.append("\">&laquo;</a></td>\n");
        }

        // create the navigation to the current month
        result.append("\t<td class=\"");
        result.append(getStyle().getStyleNavigation());
        result.append("\" title=\"");
        result.append(getMessages().key("calendar.navigation.month.current"));
        result.append("\" colspan=\"");
        result.append(monthSpan);
        result.append("\">");

        DateFormat df = new SimpleDateFormat(getMessages().key("calendar.format.headline.month"), calendarLocale);
        calendar = new GregorianCalendar(year, month, 1);

        if (showNavigation) {
            // create the link to the current month
            result.append("<a class=\"");
            result.append(getStyle().getStyleNavigation());
            result.append("\" href=\"");
            navLink = new StringBuffer(64);
            navLink.append(getJsp().getRequestContext().getUri());
            navLink.append("?").append(PARAM_YEAR).append("=").append(currentCalendar.get(Calendar.YEAR));
            navLink.append("&amp;").append(PARAM_MONTH).append("=").append(currentCalendar.get(Calendar.MONTH));
            result.append(getJsp().link(navLink.toString()));
            result.append("\">");
            result.append(df.format(calendar.getTime()));
            result.append("</a>");
        } else {
            // create only the viewed months String
            result.append(df.format(calendar.getTime()));
        }

        result.append("</td>\n");

        if (showNavigation) {
            // create the navigation to the previous month
            result.append("\t<td class=\"");
            result.append(getStyle().getStyleNavigation());
            result.append("\" title=\"");
            result.append(getMessages().key("calendar.navigation.month.next"));
            result.append("\"><a class=\"");
            result.append(getStyle().getStyleNavigation());
            result.append("\" href=\"");
            calendar = getNextPeriod(new GregorianCalendar(year, month, 1), CmsCalendarDisplay.PERIOD_MONTH);
            navLink = new StringBuffer(64);
            navLink.append(getJsp().getRequestContext().getUri());
            navLink.append("?").append(PARAM_YEAR).append("=").append(calendar.get(Calendar.YEAR));
            navLink.append("&amp;").append(PARAM_MONTH).append("=").append(calendar.get(Calendar.MONTH));
            result.append(getJsp().link(navLink.toString()));
            result.append("\">&raquo;</a></td>\n");
        }

        result.append("</tr>\n");

        return result.toString();
    }

    /**
     * Returns the CSS class name to use to format the weekday depending on the status and localized settings.<p>
     * 
     * @param currentWeekday the current weekday
     * @param currentWeekdayStatus the weekday status of the current weekday
     * @return the CSS class name to use to format the weekday depending on the status and localized settings
     */
    private String getWeekdayStyle(int currentWeekday, int currentWeekdayStatus) {

        if (currentWeekdayStatus < I_CmsCalendarEntryData.WEEKDAYSTATUS_MAYBEHOLIDAY) {
            // check the localized week days to mark specially as maybe holiday days
            if (currentWeekday == getWeekdayMaybeHoliday()) {
                currentWeekdayStatus = I_CmsCalendarEntryData.WEEKDAYSTATUS_MAYBEHOLIDAY;
            }
        }
        if (currentWeekdayStatus < I_CmsCalendarEntryData.WEEKDAYSTATUS_HOLIDAY) {
            // check the localized week days to mark specially as holiday days
            if (currentWeekday == getWeekdayHoliday()) {
                currentWeekdayStatus = I_CmsCalendarEntryData.WEEKDAYSTATUS_HOLIDAY;
            }
        }
        String dayStyle;
        switch (currentWeekdayStatus) {
            case I_CmsCalendarEntryData.WEEKDAYSTATUS_HOLIDAY:
                dayStyle = getStyle().getStyleDayHoliday();
                break;
            case I_CmsCalendarEntryData.WEEKDAYSTATUS_MAYBEHOLIDAY:
                dayStyle = getStyle().getStyleDayMaybeHoliday();
                break;
            case I_CmsCalendarEntryData.WEEKDAYSTATUS_WORKDAY:
            default:
                dayStyle = getStyle().getStyleDay();
        }
        return dayStyle;
    }

}