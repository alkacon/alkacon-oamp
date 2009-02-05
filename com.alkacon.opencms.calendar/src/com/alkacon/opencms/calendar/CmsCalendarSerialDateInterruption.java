/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsCalendarSerialDateInterruption.java,v $
 * Date   : $Date: 2009/02/05 09:49:31 $
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

import java.util.Calendar;

/**
 * Represents an interruption of a date series.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsCalendarSerialDateInterruption extends Object {

    /** The end date of the interruption. */
    private Calendar m_endDate;

    /** The start date of the interruption. */
    private Calendar m_startDate;

    /**
     * Constructor that fills the necessary members.<p>
     * 
     * @param startDate the start date of the interruption
     * @param endDate the end date of the interruption
     */
    public CmsCalendarSerialDateInterruption(Calendar startDate, Calendar endDate) {

        m_startDate = startDate;
        m_endDate = endDate;
    }

    /**
     * Checks if the start date (not the time!) of the objects to compare are the same.<p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof CmsCalendarSerialDateInterruption) {
            CmsCalendarSerialDateInterruption interruption = (CmsCalendarSerialDateInterruption)obj;
            return m_startDate.equals(interruption.getStartDate()) && m_endDate.equals(interruption.getEndDate());
        }
        return false;
    }

    /**
     * Returns the start date of the series entry that should be changed.<p>
     * 
     * @return the start date of the series entry that should be changed
     */
    public Calendar getEndDate() {

        return m_endDate;
    }

    /**
     * Returns the start date of the series entry that should be changed.<p>
     * 
     * @return the start date of the series entry that should be changed
     */
    public Calendar getStartDate() {

        return m_startDate;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return (getStartDate().toString() + getEndDate().toString()).hashCode();
    }

}
