/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/I_CmsCalendarView.java,v $
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

import java.util.Comparator;
import java.util.List;

/**
 * A calendar view is used to get user defined views on the entries of a calendar.<p>
 * 
 * It contains a list of view dates using the {@link com.alkacon.opencms.v8.calendar.CmsCalendarEntryDate} object to determine
 * the start and end date of a view. 
 * Additionally, a comparator to check if an entry is in the view range has to be defined, as well as a sort method to
 * sort the result list of matching calendar entries for the view.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 6.0.1
 */
public interface I_CmsCalendarView {

    /**
     * Returns the comparator to use to determine if a calendar entry belongs to the view.<p>
     * 
     * @return the comparator to use to determine if a calendar entry belongs to the view
     */
    Comparator getComparator();

    /**
     * Returns the dates for the view.<p>
     * 
     * @return the dates for the view
     */
    List getDates();

    /**
     * Sorts the returned matching calendar entries to display them on the frontend.<p>
     * 
     * @param entries the list of {@link CmsCalendarEntry} objects to sort
     */
    void sort(List entries);

}