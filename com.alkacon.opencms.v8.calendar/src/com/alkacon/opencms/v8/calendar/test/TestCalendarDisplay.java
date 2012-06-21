/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/test/TestCalendarDisplay.java,v $
 * Date   : $Date: 2008/04/25 14:50:41 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.calendar.test;

import com.alkacon.opencms.v8.calendar.CmsCalendar;
import com.alkacon.opencms.v8.calendar.CmsCalendarEntry;
import com.alkacon.opencms.v8.calendar.CmsCalendarEntryData;
import com.alkacon.opencms.v8.calendar.CmsCalendarEntryDate;
import com.alkacon.opencms.v8.calendar.CmsCalendarEntryDateSerial;
import com.alkacon.opencms.v8.calendar.CmsCalendarMonthBean;
import com.alkacon.opencms.v8.calendar.CmsCalendarSerialDateWeeklyOptions;
import com.alkacon.opencms.v8.calendar.CmsCalendarViewSimple;
import com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions;

import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for the OpenCms OCEE calendar display methods.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 6.0.1
 */
public class TestCalendarDisplay extends OpenCmsTestCase {

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestCalendarDisplay(String arg0) {

        super(arg0, false);
    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.setName(TestCalendarDisplay.class.getName());
        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        suite.addTest(new TestCalendarDisplay("testDailyView"));
        suite.addTest(new TestCalendarDisplay("testDailyViewWeeklySeries"));

        return suite;
    }

    /**
     * Tests the daily calendar view output.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyView() throws Exception {

        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();

        // create first calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First test entry");
        data.setDescription("Set on Mon, 08/29/2005 09:10 AM to 10:10 AM.");
        CmsCalendarEntryDate date = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 29, 9, 10),
            new GregorianCalendar(2005, 7, 29, 10, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create second calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second test entry");
        data.setDescription("Set on Tue, 08/30/2005 11:10 AM to 12:10 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 30, 11, 10), new GregorianCalendar(
            2005,
            7,
            2,
            30,
            10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create third calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third test entry");
        data.setDescription("Set on Mon, 08/29/2005 08:00 AM to 08:30 AM.");
        date = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 29).getTimeInMillis(),
            CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8,
            (CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8) + (CmsCalendarEntryDate.MILLIS_00_PER_MINUTE * 30),
            0);
        //date = new CmsCalendarEntryDate(new Date(105, 8, 29, 8, 0), new Date(105, 8, 29, 8, 30));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create the list of view dates
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 1, 0, 0, 0),
            new GregorianCalendar(2005, 7, 31, 23, 59, 59));
        viewDates.add(viewDate);

        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);

        List result = testCal.getEntries(view);

        assertEquals(3, result.size());

        for (int i = 0; i < result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 0) {
                assertEquals("Third test entry", resEntry.getEntryData().getTitle());
            }
        }

        CmsCalendarMonthBean displayCalendar = new CmsCalendarMonthBean(null);
        displayCalendar.setEntries(testCal.getEntries());
        Map days = displayCalendar.getEntriesForDays(new GregorianCalendar(2005, 7, 1, 0, 0, 0), new GregorianCalendar(
            2005,
            7,
            31,
            23,
            59,
            59));
        Iterator i = days.keySet().iterator();
        System.out.println("Test simple entry dates:");
        while (i.hasNext()) {
            Date day = (Date)i.next();
            List entries = (List)days.get(day);
            System.out.println(DateFormat.getInstance().format(day) + ": " + entries.size());
        }
    }

    /**
     * Tests the daily calendar view output together with a series of weekly entries.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewWeeklySeries() throws Exception {

        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();

        // create first calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First test entry");
        data.setDescription("Set on Mon, 08/29/2005 09:10 AM to 10:10 AM.");
        CmsCalendarEntryDate date = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 29, 9, 10),
            new GregorianCalendar(2005, 7, 29, 10, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create second calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second test entry");
        data.setDescription("Set on Tue, 08/30/2005 11:10 AM to 12:10 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 30, 11, 10), new GregorianCalendar(
            2005,
            7,
            2,
            30,
            10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create third calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third test entry");
        data.setDescription("Set on Mon, 08/29/2005 08:00 AM to 08:30 AM.");
        date = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 29).getTimeInMillis(),
            CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8,
            (CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8) + (CmsCalendarEntryDate.MILLIS_00_PER_MINUTE * 30),
            0);
        //date = new CmsCalendarEntryDate(new Date(105, 8, 29, 8, 0), new Date(105, 8, 29, 8, 30));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);

        // create fourth (serial) calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Fourth serial test entry");
        data.setDescription("Start on Mon, 08/15/2005 08:40 AM to 08:50 AM, 3 occurences.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(
            new GregorianCalendar(2005, 7, 15, 8, 40),
            new GregorianCalendar(2005, 7, 15, 8, 50));
        serialDate.setOccurences(3);
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_TIMES);

        List weekDays = Arrays.asList(new Integer[] {new Integer(2)});
        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 1);

        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);

        // create the list of view dates
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(
            new GregorianCalendar(2005, 7, 1, 0, 0, 0),
            new GregorianCalendar(2005, 7, 31, 23, 59, 59));
        viewDates.add(viewDate);

        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);

        List result = testCal.getEntries(view);

        assertEquals(6, result.size());

        for (int i = 0; i < result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 3) {
                assertEquals("Fourth serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2005, 7, 29, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2005, 7, 29, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }

        CmsCalendarMonthBean displayCalendar = new CmsCalendarMonthBean(null);
        displayCalendar.setEntries(testCal.getEntries());
        //Map days = displayCalendar.getDisplayDays(new GregorianCalendar(2005, 7, 1, 0, 0, 0), new GregorianCalendar(2005, 7, 31, 23, 59, 59));
        Map days = displayCalendar.getEntriesForMonth(2005, 7);
        Iterator i = days.keySet().iterator();
        System.out.println("######################");
        System.out.println("Test serial entry dates:");
        while (i.hasNext()) {
            Date day = (Date)i.next();
            List entries = (List)days.get(day);
            System.out.println(DateFormat.getInstance().format(day) + ": " + entries.size());
        }

        Map monthDays = displayCalendar.getMonthDaysMatrix(2005, 7, Locale.ENGLISH);
        i = monthDays.keySet().iterator();
        System.out.println("######################");
        System.out.println("Test serial entry dates month rendering:");
        while (i.hasNext()) {
            Integer index = (Integer)i.next();
            Calendar curDate = (Calendar)monthDays.get(index);
            int size = -1;
            String dateStr = "(none)";
            if (curDate != null) {
                dateStr = DateFormat.getInstance().format(curDate.getTime());
                List entries = (List)days.get(curDate.getTime());
                if (entries != null) {
                    size = entries.size();
                }
            }

            System.out.println("Index: " + index + " - " + dateStr + ": " + size);
        }

    }

}