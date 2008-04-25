/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarEntryData.java,v $
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

/**
 * Stores information about the data of a single calendar entry.<p>
 * 
 * This is the entry title, description, type, the detail URI in the OpenCms VFS and the weekday status.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public class CmsCalendarEntryData implements I_CmsCalendarEntryData {

    /** The description of the entry. */
    private String m_description;

    /** The URI to the detail view of the entry. */
    private String m_detailUri;

    /** The flag to determine if the time should be shown in the calendar view. */
    private boolean m_showTime;

    /** The title of the entry. */
    private String m_title;

    /** The type of the entry. */
    private String m_type;

    /** The weekday status: working day, maybe holiday, holiday. */
    private int m_weekdayStatus;

    /**
     * Creates an empty entry data.<p>
     */
    public CmsCalendarEntryData() {

        // creates an empty data object
    }

    /**
     * Creates an initialized entry data.<p>
     * 
     * @param title the title of the entry
     * @param description the description of the entry
     * @param type the type of the entry
     */
    public CmsCalendarEntryData(String title, String description, String type) {

        m_title = title;
        m_description = description;
        m_type = type;
    }

    /**
     * Creates an initialized entry data.<p>
     * 
     * @param title the title of the entry
     * @param description the description of the entry
     * @param type the type of the entry
     * @param detailUri the detail URI to link to when clicking on an entry
     * @param weekdayStatus the weekday status: working day, maybe holiday or holiday
     */
    public CmsCalendarEntryData(String title, String description, String type, String detailUri, int weekdayStatus) {

        m_title = title;
        m_description = description;
        m_type = type;
        m_detailUri = detailUri;
        m_weekdayStatus = weekdayStatus;
    }

    /**
     * Creates an initialized entry data.<p>
     * 
     * @param title the title of the entry
     * @param description the description of the entry
     * @param type the type of the entry
     * @param detailUri the detail URI to link to when clicking on an entry
     * @param weekdayStatus the weekday status: working day, maybe holiday or holiday
     * @param showTime the flag to determine if the time should be shown in the calendar view
     */
    public CmsCalendarEntryData(
        String title,
        String description,
        String type,
        String detailUri,
        int weekdayStatus,
        boolean showTime) {

        m_title = title;
        m_description = description;
        m_type = type;
        m_detailUri = detailUri;
        m_weekdayStatus = weekdayStatus;
        m_showTime = showTime;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {

        return new CmsCalendarEntryData(m_title, m_description, m_type, m_detailUri, m_weekdayStatus, m_showTime);
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#getDescription()
     */
    public String getDescription() {

        return m_description;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#getDetailUri()
     */
    public String getDetailUri() {

        return m_detailUri;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#getTitle()
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#getType()
     */
    public String getType() {

        return m_type;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#getWeekdayStatus()
     */
    public int getWeekdayStatus() {

        return m_weekdayStatus;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#isShowTime()
     */
    public boolean isShowTime() {

        return m_showTime;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setDescription(java.lang.String)
     */
    public void setDescription(String description) {

        m_description = description;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setDetailUri(java.lang.String)
     */
    public void setDetailUri(String detailUri) {

        m_detailUri = detailUri;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setShowTime(boolean)
     */
    public void setShowTime(boolean showTime) {

        m_showTime = showTime;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setTitle(java.lang.String)
     */
    public void setTitle(String title) {

        m_title = title;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setType(java.lang.String)
     */
    public void setType(String type) {

        m_type = type;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarEntryData#setWeekdayStatus(int)
     */
    public void setWeekdayStatus(int weekdayStatus) {

        m_weekdayStatus = weekdayStatus;
    }

}
