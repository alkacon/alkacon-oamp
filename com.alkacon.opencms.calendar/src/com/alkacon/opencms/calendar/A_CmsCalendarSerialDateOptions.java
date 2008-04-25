/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/A_CmsCalendarSerialDateOptions.java,v $
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
import java.util.List;
import java.util.Map;

/**
 * Implements the basic methods of serial date options needed for serial date changes.<p>
 * 
 * @author Andreas Zahner
 */
public abstract class A_CmsCalendarSerialDateOptions implements I_CmsCalendarSerialDateOptions {

    /** The serial date changes. */
    private List m_serialDateChanges;

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#addSerialDateChange(com.alkacon.opencms.calendar.CmsCalendarSerialDateChange)
     */
    public void addSerialDateChange(CmsCalendarSerialDateChange change) {

        if (m_serialDateChanges == null) {
            m_serialDateChanges = new ArrayList();
        }
        m_serialDateChanges.add(change);

    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public abstract Map getConfigurationValuesAsMap();

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getSerialDateChanges()
     */
    public List getSerialDateChanges() {

        return m_serialDateChanges;
    }

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public abstract int getSerialType();

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#matchCalendarView(com.alkacon.opencms.calendar.CmsCalendarEntry, com.alkacon.opencms.calendar.I_CmsCalendarView, int)
     */
    public abstract List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView, int maxCount);

    /**
     * @see com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions#setSerialDateChanges(java.util.List)
     */
    public void setSerialDateChanges(List serialDateChanges) {

        m_serialDateChanges = serialDateChanges;
    }

    /**
     * Checks if the entry has to be changed by doing a lookup in the list of serial date changes.<p>
     * 
     * If the entry should not be shown in the date series, <code>null</code> is returned.<p>
     * 
     * @param entry the entry to check
     * @return the modified entry or <code>null</code>, if the entry should not be shown
     */
    protected CmsCalendarEntry checkChanges(CmsCalendarEntry entry) {

        if (getSerialDateChanges() == null) {
            // no changes configured, return unmodified entry
            return entry;
        }
        CmsCalendarSerialDateChange changeTest = new CmsCalendarSerialDateChange(
            entry.getEntryDate().getStartDate(),
            null);
        int changeIndex = getSerialDateChanges().indexOf(changeTest);
        if (changeIndex != -1) {
            // found a match, use the changed entry data
            CmsCalendarSerialDateChange change = (CmsCalendarSerialDateChange)getSerialDateChanges().get(changeIndex);
            if (change.isRemoved()) {
                // entry has to be removed, return null
                return null;
            } else {
                // change the data for this entry
                entry.setEntryData(change.getEntryData());
                entry.getEntryDate().setStartDate(change.getStartDate(), true);
            }
        }
        return entry;
    }

}
