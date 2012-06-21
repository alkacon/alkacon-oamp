/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/A_CmsCalendarSerialDateOptions.java,v $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements the basic methods of serial date options needed for serial date changes and interruptions.<p>
 * 
 * @author Andreas Zahner
 */
public abstract class A_CmsCalendarSerialDateOptions implements I_CmsCalendarSerialDateOptions {

    /** The serial date changes. */
    private List m_serialDateChanges;

    /** The serial date interruptions. */
    private List m_serialDateInterruptions;

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#addSerialDateChange(com.alkacon.opencms.v8.calendar.CmsCalendarSerialDateChange)
     */
    public void addSerialDateChange(CmsCalendarSerialDateChange change) {

        if (m_serialDateChanges == null) {
            m_serialDateChanges = new ArrayList();
        }
        m_serialDateChanges.add(change);

    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#addSerialDateInterruption(com.alkacon.opencms.v8.calendar.CmsCalendarSerialDateInterruption)
     */
    public void addSerialDateInterruption(CmsCalendarSerialDateInterruption interruption) {

        if (m_serialDateInterruptions == null) {
            m_serialDateInterruptions = new ArrayList();
        }
        m_serialDateInterruptions.add(interruption);

    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getConfigurationValuesAsMap()
     */
    public abstract Map getConfigurationValuesAsMap();

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getSerialDateChanges()
     */
    public List getSerialDateChanges() {

        return m_serialDateChanges;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getSerialDateInterruptions()
     */
    public List getSerialDateInterruptions() {

        return m_serialDateInterruptions;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#getSerialType()
     */
    public abstract int getSerialType();

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#matchCalendarView(com.alkacon.opencms.v8.calendar.CmsCalendarEntry, com.alkacon.opencms.v8.calendar.I_CmsCalendarView, int)
     */
    public abstract List matchCalendarView(CmsCalendarEntry entry, I_CmsCalendarView calendarView, int maxCount);

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#setSerialDateChanges(java.util.List)
     */
    public void setSerialDateChanges(List serialDateChanges) {

        m_serialDateChanges = serialDateChanges;
    }

    /**
     * @see com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions#setSerialDateInterruptions(java.util.List)
     */
    public void setSerialDateInterruptions(List serialDateInterruptions) {

        m_serialDateInterruptions = serialDateInterruptions;
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

        if ((getSerialDateChanges() == null) && (getSerialDateInterruptions() == null)) {
            // no changes or interruptions configured, return unmodified entry
            return entry;
        }

        if (getSerialDateInterruptions() != null) {
            // check if the entry is in an interruption time interval
            Iterator i = getSerialDateInterruptions().iterator();
            while (i.hasNext()) {
                CmsCalendarSerialDateInterruption intrpt = (CmsCalendarSerialDateInterruption)i.next();
                Calendar entryStartDate = entry.getEntryDate().getStartDate();
                if (intrpt.getStartDate().before(entryStartDate) && intrpt.getEndDate().after(entryStartDate)) {
                    // entry is in interruption time interval, return null to remove it
                    return null;
                }
            }
        }

        if (getSerialDateChanges() != null) {
            // check if the serial entry is changed or removed
            CmsCalendarSerialDateChange changeTest = new CmsCalendarSerialDateChange(
                entry.getEntryDate().getStartDate(),
                null);
            int changeIndex = getSerialDateChanges().indexOf(changeTest);
            if (changeIndex != -1) {
                // found a match, use the changed entry data
                CmsCalendarSerialDateChange change = (CmsCalendarSerialDateChange)getSerialDateChanges().get(
                    changeIndex);
                if (change.isRemoved()) {
                    // entry has to be removed, return null
                    return null;
                } else {
                    // change the data for this entry
                    entry.setEntryData(change.getEntryData());
                    entry.getEntryDate().setStartDate(change.getStartDate(), true);
                }
            }
        }
        return entry;

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
    protected boolean checkLeaveLoop(
        CmsCalendarEntryDateSerial entryDate,
        Calendar runDate,
        CmsCalendarEntryDate viewDate,
        int occurences) {

        // for the series end type: end date, check end date
        if ((entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_DATE)
            && runDate.after(entryDate.getSerialEndDate())) {
            // run date is after series end date, interrupt
            return true;
        }

        // for the series end type: n-times, check occurences
        if ((entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_TIMES)
            && (occurences >= entryDate.getOccurences())) {
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
