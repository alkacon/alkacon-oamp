/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsCalendarStyle.java,v $
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

import org.opencms.util.CmsStringUtil;

/**
 * Provides formatting CSS class names to generate the calendar frontend output.<p>
 * 
 * This class contains getters and setters to provide CSS class names that should be used to build the calendar side element.<p>
 * 
 *  @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarStyle {

    /** The CSS class name for the common day cells. */
    private String m_styleDay;

    /** The CSS class name to format the current day. */
    private String m_styleDayCurrent;

    /** The CSS class name for the empty cells in the day rows. */
    private String m_styleDayEmpty;

    /** The CSS class name for the link to an entry overview of a day. */
    private String m_styleDayEntryLink;

    /** The CSS class name to format a holiday day. */
    private String m_styleDayHoliday;

    /** The CSS class name to format a maybe holiday day. */
    private String m_styleDayMaybeHoliday;

    /** The CSS class name for the navigation elements of the displayed view. */
    private String m_styleNavigation;

    /** The CSS class name for the table element around the displayed view. */
    private String m_styleTable;

    /** The CSS class name for the week day name cells. */
    private String m_styleWeekdays;

    /**
     * Constructor to get a new instance of the style object.<p>
     */
    public CmsCalendarStyle() {

        // nothing to do here
    }

    /**
     * Returns the CSS class name for the common day cells.<p>
     *
     * @return the CSS class name for the common day cells
     */
    public String getStyleDay() {

        return checkStyleClass(m_styleDay);
    }

    /**
     * Returns the CSS class name to format the current day.<p>
     *
     * @return the CSS class name to format the current day
     */
    public String getStyleDayCurrent() {

        return checkStyleClass(m_styleDayCurrent);
    }

    /**
     * Returns the CSS class name for the empty cells in the day rows.<p>
     *
     * @return the CSS class name for the empty cells in the day rows
     */
    public String getStyleDayEmpty() {

        return checkStyleClass(m_styleDayEmpty);
    }

    /**
     * Returns the CSS class name for the link to an entry overview of a day.<p>
     *
     * @return the CSS class name for the link to an entry overview of a day
     */
    public String getStyleDayEntryLink() {

        return checkStyleClass(m_styleDayEntryLink);
    }

    /**
     * Returns the CSS class name to format a holiday day.<p>
     *
     * @return the CSS class name to format a holiday day
     */
    public String getStyleDayHoliday() {

        return checkStyleClass(m_styleDayHoliday);
    }

    /**
     * Returns the CSS class name to format a maybe holiday day.<p>
     *
     * @return the CSS class name to format a maybe holiday day
     */
    public String getStyleDayMaybeHoliday() {

        return checkStyleClass(m_styleDayMaybeHoliday);
    }

    /**
     * Returns the CSS class name for the navigation elements of the displayed view.<p>
     *
     * @return the CSS class name for the navigation elements of the displayed view
     */
    public String getStyleNavigation() {

        return checkStyleClass(m_styleNavigation);
    }

    /**
     * Returns the CSS class name for the table element around the displayed view.<p>
     *
     * @return the CSS class name for the table element around the displayed view
     */
    public String getStyleTable() {

        return checkStyleClass(m_styleTable);
    }

    /**
     * Returns the CSS class name for the week day name cells.<p>
     *
     * @return the CSS class name for the week day name cells
     */
    public String getStyleWeekdays() {

        return checkStyleClass(m_styleWeekdays);
    }

    /**
     * Sets the CSS class name for the common day cells.<p>
     *
     * @param styleDay the CSS class name for the common day cells
     */
    public void setStyleDay(String styleDay) {

        m_styleDay = styleDay;
    }

    /**
     * Sets the CSS class name to format the current day.<p>
     *
     * @param styleDayCurrent the CSS class name to format the current day
     */
    public void setStyleDayCurrent(String styleDayCurrent) {

        m_styleDayCurrent = styleDayCurrent;
    }

    /**
     * Sets the CSS class name for the empty cells in the day rows.<p>
     *
     * @param styleDayEmpty the CSS class name for the empty cells in the day rows
     */
    public void setStyleDayEmpty(String styleDayEmpty) {

        m_styleDayEmpty = styleDayEmpty;
    }

    /**
     * Sets the CSS class name for the link to an entry overview of a day.<p>
     *
     * @param styleDayEntryLink the CSS class name for the link to an entry overview of a day
     */
    public void setStyleDayEntryLink(String styleDayEntryLink) {

        m_styleDayEntryLink = styleDayEntryLink;
    }

    /**
     * Sets the CSS class name to format a holiday day.<p>
     *
     * @param styleDayHoliday the CSS class name to format a holiday day
     */
    public void setStyleDayHoliday(String styleDayHoliday) {

        m_styleDayHoliday = styleDayHoliday;
    }

    /**
     * Sets the CSS class name to format a maybe holiday day.<p>
     *
     * @param styleDayMaybeHoliday the CSS class name to format a maybe holiday day
     */
    public void setStyleDayMaybeHoliday(String styleDayMaybeHoliday) {

        m_styleDayMaybeHoliday = styleDayMaybeHoliday;
    }

    /**
     * Sets the CSS class name for the navigation elements of the displayed view.<p>
     *
     * @param styleNavigation the CSS class name for the navigation elements of the displayed view
     */
    public void setStyleNavigation(String styleNavigation) {

        m_styleNavigation = styleNavigation;
    }

    /**
     * Sets the CSS class name for the table element around the displayed view.<p>
     *
     * @param styleTable the CSS class name for the table element around the displayed view
     */
    public void setStyleTable(String styleTable) {

        m_styleTable = styleTable;
    }

    /**
     * Sets the CSS class name for the week day name cells.<p>
     *
     * @param styleWeekdays the CSS class name for the week day name cells
     */
    public void setStyleWeekdays(String styleWeekdays) {

        m_styleWeekdays = styleWeekdays;
    }

    /**
     * Checks the value of the specified style class and returns an empty String if the parameter is null.<p>
     * 
     * @param styleClass the value to check
     * @return the style class or an empty String if the parameter is null
     */
    private String checkStyleClass(String styleClass) {

        if (CmsStringUtil.isEmpty(styleClass)) {
            return "";
        }
        return styleClass;
    }

}
