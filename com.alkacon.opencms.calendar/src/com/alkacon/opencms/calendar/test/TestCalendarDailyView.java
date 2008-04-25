/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/test/TestCalendarDailyView.java,v $
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

package com.alkacon.opencms.calendar.test;

import com.alkacon.opencms.calendar.CmsCalendar;
import com.alkacon.opencms.calendar.CmsCalendarEntry;
import com.alkacon.opencms.calendar.CmsCalendarEntryData;
import com.alkacon.opencms.calendar.CmsCalendarEntryDate;
import com.alkacon.opencms.calendar.CmsCalendarEntryDateSerial;
import com.alkacon.opencms.calendar.CmsCalendarSerialDateDailyOptions;
import com.alkacon.opencms.calendar.CmsCalendarSerialDateMonthlyOptions;
import com.alkacon.opencms.calendar.CmsCalendarSerialDateWeeklyOptions;
import com.alkacon.opencms.calendar.CmsCalendarSerialDateYearlyOptions;
import com.alkacon.opencms.calendar.CmsCalendarViewSimple;
import com.alkacon.opencms.calendar.I_CmsCalendarSerialDateOptions;

import org.opencms.test.OpenCmsTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for the OpenCms OCEE calendar date objects.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 6.0.1
 */
public class TestCalendarDailyView extends OpenCmsTestCase {

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestCalendarDailyView(String arg0) {

        super(arg0, false);
    }
    
    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.setName(TestCalendarDailyView.class.getName());
        //OpenCmsTestProperties.initialize(OceeAllTests.TEST_PROPERTIES_PATH);

        suite.addTest(new TestCalendarDailyView("testDailyView"));
        suite.addTest(new TestCalendarDailyView("testDailyViewDailySeries"));
        suite.addTest(new TestCalendarDailyView("testDailyViewWeeklySeries"));
        suite.addTest(new TestCalendarDailyView("testDailyViewWeeklySeriesExtended"));
        suite.addTest(new TestCalendarDailyView("testDailyViewWeeklySeriesIntervals"));
        suite.addTest(new TestCalendarDailyView("testDailyViewMonthlySeries"));
        suite.addTest(new TestCalendarDailyView("testDailyViewMonthlySeriesExtended"));
        suite.addTest(new TestCalendarDailyView("testDailyViewYearlySeries"));

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
        CmsCalendarEntryDate date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29, 9, 10), new GregorianCalendar(2005, 7, 29, 10, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create second calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second test entry");
        data.setDescription("Set on Tue, 08/30/2005 11:10 AM to 12:10 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 30, 11, 10), new GregorianCalendar(2005, 7, 2, 30, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create third calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third test entry");
        data.setDescription("Set on Mon, 08/29/2005 08:00 AM to 08:30 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29).getTimeInMillis(), CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8, CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8 + CmsCalendarEntryDate.MILLIS_00_PER_MINUTE * 30, 0);
        //date = new CmsCalendarEntryDate(new Date(105, 8, 29, 8, 0), new Date(105, 8, 29, 8, 30));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create the list of view dates
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29, 0, 0, 0), new GregorianCalendar(2005, 7, 29, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        
        List result = testCal.getEntries(view);
        
        assertEquals(2, result.size());
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 0) {
                assertEquals("Third test entry", resEntry.getEntryData().getTitle());
            }
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
        CmsCalendarEntryDate date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29, 9, 10), new GregorianCalendar(2005, 7, 29, 10, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create second calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second test entry");
        data.setDescription("Set on Tue, 08/30/2005 11:10 AM to 12:10 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 30, 11, 10), new GregorianCalendar(2005, 7, 2, 30, 10));
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create third calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third test entry");
        data.setDescription("Set on Mon, 08/29/2005 08:00 AM to 08:30 AM.");
        date = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29).getTimeInMillis(), CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8, CmsCalendarEntryDate.MILLIS_01_PER_HOUR * 8 + CmsCalendarEntryDate.MILLIS_00_PER_MINUTE * 30, 0);
        entry.setEntryData(data);
        entry.setEntryDate(date);
        testCal.addEntry(entry);
        
        // create fourth (serial) calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Fourth serial test entry");
        data.setDescription("Start on Mon, 08/15/2005 08:40 AM to 08:50 AM, 3 occurences.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2005, 7, 15, 8, 40), new GregorianCalendar(2005, 7, 15, 8, 50));
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
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2005, 7, 29, 0, 0, 0), new GregorianCalendar(2005, 7, 29, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        
        List result = testCal.getEntries(view);
        
        assertEquals(3, result.size());
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 1) {
                assertEquals("Fourth serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2005, 7, 29, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2005, 7, 29, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }
    }
    
    /**
     * Tests the daily calendar view output together with some series of weekly entries.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewWeeklySeriesExtended() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create first serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Start on Mon, 08/01/2005 08:40 AM to 08:50 AM, every workday in a 2 week interval.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2005, 7, 1, 8, 40), new GregorianCalendar(2005, 7, 1, 8, 50));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        List weekDays = Arrays.asList(new Integer[] {new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6)});
        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 2);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create second serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second serial test entry");
        data.setDescription("Start on Wed, 08/10/2005 03:20 PM to 08:20 PM, every Saturday and Sunday in a 1 week interval, stopping on Sun, 10/30/2005.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2005, 7, 10, 15, 20), new GregorianCalendar(2005, 7, 10, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2005, 9, 30));
        
        weekDays = Arrays.asList(new Integer[] {new Integer(7), new Integer(1)});
        serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create third serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third serial test entry");
        data.setDescription("Start on Tur, 10/11/2005 09:30 PM to 02:30 AM next day, every day in a 1 week interval.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2005, 9, 11, 21, 30), new GregorianCalendar(2005, 9, 12, 2, 30));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        weekDays = Arrays.asList(new Integer[] {new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7)});
        serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2005, 8, 2, 0, 0, 0), new GregorianCalendar(2005, 8, 2, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        
        List result = testCal.getEntries(view);
        
        assertEquals(1, result.size());
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 0) {
                assertEquals("First serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2005, 8, 2, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2005, 8, 2, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2005, 9, 30, 0, 0, 0), new GregorianCalendar(2005, 9, 30, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(2, result.size());
        
        CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(0);
        assertEquals("Second serial test entry", resEntry.getEntryData().getTitle());
        resEntry = (CmsCalendarEntry)result.get(1);
        assertEquals("Third serial test entry", resEntry.getEntryData().getTitle());
        assertEquals(new GregorianCalendar(2005, 9, 30, 21, 30), resEntry.getEntryDate().getStartDate());
        assertEquals(new GregorianCalendar(2005, 9, 31, 2, 30), resEntry.getEntryDate().getEndDate());

    }
    
    /**
     * Tests the daily calendar view output together with some series of weekly interval entries.<p>
     * 
     * Aug 2007
     * Mo Di Mi Do Fr Sa So 
     *       1  2  3  4  5 
     * 6  7  8  9  10 11 12 
     * 13 14 15 16 17 18 19 
     * 20 21 22 23 24 25 26 
     * 27 28 29 30 31
     * 
     * Sep 2007
     * Mo Di Mi Do Fr Sa So 
     *                1  2 
     * 3  4  5  6  7  8  9 
     * 10 11 12 13 14 15 16 
     * 17 18 19 20 21 22 23 
     * 24 25 26 27 28 29 30 
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewWeeklySeriesIntervals() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create first serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Start on Mon, 08/06/2007 08:40 AM to 08:50 AM, every Monday and Tuesday in a 2 week interval.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 6, 8, 40), new GregorianCalendar(2007, 7, 6, 8, 50));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        List weekDays = Arrays.asList(new Integer[] {new Integer(Calendar.MONDAY), new Integer(Calendar.TUESDAY)});
        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 2);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create second serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second serial test entry");
        data.setDescription("Start on Sat, 08/11/2007 03:20 PM to 08:20 PM, every Saturday and Sunday in a 2 week interval, stopping on Sun, 09/02/2007.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 11, 15, 20), new GregorianCalendar(2007, 7, 11, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2007, 8, 2));
        
        weekDays = Arrays.asList(new Integer[] {new Integer(Calendar.SATURDAY), new Integer(Calendar.SUNDAY)});
        serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 2);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create third serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third serial test entry");
        data.setDescription("Start on Fri, 08/03/2007 09:30 PM to 02:30 AM next day, every Friday in a 3 week interval.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 3, 21, 30), new GregorianCalendar(2007, 7, 4, 2, 30));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        weekDays = Arrays.asList(new Integer[] {new Integer(Calendar.FRIDAY)});
        serialOptions = new CmsCalendarSerialDateWeeklyOptions(weekDays, 3);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates from Mon, 08/20/2007 to Tue, 08/21/2007
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 20, 0, 0, 0), new GregorianCalendar(2007, 7, 21, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        List result = testCal.getEntries(view);
        // there should be 2 entries, one on Mon, one on Tue
        assertEquals(2, result.size());
        // additional checks
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            assertEquals("First serial test entry", resEntry.getEntryData().getTitle());
            if (i == 0) {
                
                assertEquals(new GregorianCalendar(2007, 7, 20, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 20, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }
        
        // create the list of view dates from Mon, 08/13/2007 to Tue, 08/14/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 13, 0, 0, 0), new GregorianCalendar(2007, 7, 14, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be no entries, this is in the skipped week
        assertEquals(0, result.size());
        
        // create the list of view dates on Fri, 08/24/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 24, 0, 0, 0), new GregorianCalendar(2007, 7, 24, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        view.sort(result);
        // there should be one entry
        assertEquals(1, result.size());
        
        // create the list of view dates from Sat, 08/25/2007 to Sun, 08/26/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 25, 0, 0, 0), new GregorianCalendar(2007, 7, 26, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        view.sort(result);
        // there should be two entries
        assertEquals(2, result.size());
    }
    
    /**
     * Tests the daily calendar view output together with some series of monthly interval entries.<p>
     * 
     * Aug 2007
     * Mo Di Mi Do Fr Sa So 
     *       1  2  3  4  5 
     * 6  7  8  9  10 11 12 
     * 13 14 15 16 17 18 19 
     * 20 21 22 23 24 25 26 
     * 27 28 29 30 31
     * 
     * Sep 2007
     * Mo Di Mi Do Fr Sa So 
     *                1  2 
     * 3  4  5  6  7  8  9 
     * 10 11 12 13 14 15 16 
     * 17 18 19 20 21 22 23 
     * 24 25 26 27 28 29 30 
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewMonthlySeries() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create first serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Start on Mon, 08/20/2007 08:40 AM to 08:50 AM, every 3rd Monday of the month, every 2nd month.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 20, 8, 40), new GregorianCalendar(2007, 7, 20, 8, 50));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);

        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateMonthlyOptions(3, Calendar.MONDAY, 2);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create second serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second serial test entry");
        data.setDescription("Start on Mon, 01/15/2007 03:20 PM to 08:20 PM, every 15th day of the month, every month, stopping on Mon, 12/31/2007.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 0, 15, 15, 20), new GregorianCalendar(2007, 0, 15, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2007, 11, 31));
        
        serialOptions = new CmsCalendarSerialDateMonthlyOptions(15, -1, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create third serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third serial test entry");
        data.setDescription("Start on Thu, 03/29/2007 09:30 PM to 02:30 AM next day, every 5th Thursday of the month every 3rd month.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 2, 29, 21, 30), new GregorianCalendar(2007, 2, 30, 2, 30));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        serialOptions = new CmsCalendarSerialDateMonthlyOptions(5, Calendar.THURSDAY, 3);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates from Mon, 08/20/2007 to Tue, 08/21/2007
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 20, 0, 0, 0), new GregorianCalendar(2007, 7, 21, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        List result = testCal.getEntries(view);
        // there should be one entry on Mon
        assertEquals(1, result.size());
        // additional checks
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            assertEquals("First serial test entry", resEntry.getEntryData().getTitle());
            if (i == 0) {
                assertEquals(new GregorianCalendar(2007, 7, 20, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 20, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }
        
        // create the list of view dates from Sun, 09/16/2007 to Mon, 09/24/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 8, 16, 0, 0, 0), new GregorianCalendar(2007, 8, 24, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be no entries, this is in the skipped month
        assertEquals(0, result.size());
        
        // create the list of view dates for Mon, 10/15/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 9, 15, 0, 0, 0), new GregorianCalendar(2007, 9, 15, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be 2 entries
        assertEquals(2, result.size());
        
        // create the list of view dates for Thu, 03/29/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 2, 29, 0, 0, 0), new GregorianCalendar(2007, 2, 29, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be 1 entry
        assertEquals(1, result.size());
        
        // create the list of view dates for Thu, 05/31/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 4, 31, 0, 0, 0), new GregorianCalendar(2007, 4, 31, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be no entry
        assertEquals(0, result.size());
        
        // create the list of view dates from Thu, 05/31/2007 to 01/01/2009
        testCal.getEntries().remove(0);
        testCal.getEntries().remove(0);
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 4, 31, 0, 0, 0), new GregorianCalendar(2010, 0, 1, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        
        // there should be 1 entry
        assertEquals(1, result.size());
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            assertEquals(new GregorianCalendar(2009, 11, 31, 21, 30), resEntry.getEntryDate().getStartDate());
        }
    }
    
    /**
     * Tests the daily calendar view output together with some series of monthly interval entries.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewMonthlySeriesExtended() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create second serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Start on Wed, 01/31/2007 03:20 PM to 08:20 PM, every 31st of the month every 3rd month.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 0, 31, 15, 20), new GregorianCalendar(2007, 0, 31, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2007, 11, 31));
        
        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateMonthlyOptions(31, -1, 3);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates from Mon, 01/01/2007 to Mon, 12/31/2007
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 0, 1, 0, 0, 0), new GregorianCalendar(2007, 11, 31, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        List result = testCal.getEntries(view);
        // there should be three entries (Jan, Jul, Oct, month Apr only has 30 days!)
        assertEquals(3, result.size());
    }
    
    /**
     * Tests the daily calendar view output together with some series of daily entries.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewDailySeries() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create first serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Start on Wed, 08/01/2007 08:40 AM to 08:50 AM, every workday.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 1, 8, 40), new GregorianCalendar(2007, 7, 1, 8, 50));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);

        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateDailyOptions(true, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create second serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second serial test entry");
        data.setDescription("Start on Mon, 08/13/2007 03:20 PM to 08:20 PM, every 3 days, stopping on Fri, 09/07/2007.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 13, 15, 20), new GregorianCalendar(2007, 7, 13, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2007, 8, 7));

        serialOptions = new CmsCalendarSerialDateDailyOptions(false, 3);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create third serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Third serial test entry");
        data.setDescription("Start on Mon, 08/06/2007 09:30 PM to 02:30 AM next day, every day.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 6, 21, 30), new GregorianCalendar(2007, 7, 7, 2, 30));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        
        serialOptions = new CmsCalendarSerialDateDailyOptions(false, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create fourth serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Fourth serial test entry");
        data.setDescription("Start on Mon, 08/27/2007 11:30 PM to 11:40 PM, 2 times.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 7, 27, 23, 30), new GregorianCalendar(2007, 7, 27, 23, 40));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_TIMES);
        serialDate.setOccurences(2);
        
        serialOptions = new CmsCalendarSerialDateDailyOptions(false, 1);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 13, 0, 0, 0), new GregorianCalendar(2007, 7, 13, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        
        List result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(3, result.size());
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 0) {
                assertEquals("First serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2007, 7, 13, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 13, 8, 50), resEntry.getEntryDate().getEndDate());
            }
            if (i == 1) {
                assertEquals("Second serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2007, 7, 13, 15, 20), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 13, 20, 20), resEntry.getEntryDate().getEndDate());
            }
            if (i == 2) {
                assertEquals("Third serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2007, 7, 13, 21, 30), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 14, 2, 30), resEntry.getEntryDate().getEndDate());
            }
        }
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 15, 0, 0, 0), new GregorianCalendar(2007, 7, 15, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(2, result.size());
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 19, 0, 0, 0), new GregorianCalendar(2007, 7, 19, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(2, result.size());
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 0) {
                assertEquals("Second serial test entry", resEntry.getEntryData().getTitle());
                assertEquals(new GregorianCalendar(2007, 7, 19, 15, 20), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 7, 19, 20, 20), resEntry.getEntryDate().getEndDate());
            }
            
        }
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 7, 28, 0, 0, 0), new GregorianCalendar(2007, 7, 28, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(4, result.size()); 
        
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            if (i == 3) {
                assertEquals("Fourth serial test entry", resEntry.getEntryData().getTitle());
            }
        }
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 8, 6, 0, 0, 0), new GregorianCalendar(2007, 8, 6, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(3, result.size());
        
        // create the list of view dates
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 8, 9, 0, 0, 0), new GregorianCalendar(2007, 8, 9, 23, 59, 59));        
        viewDates.add(viewDate);
        
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        
        result = testCal.getEntries(view);
        view.sort(result);
        
        assertEquals(1, result.size());
    }
    
    /**
     * Tests the daily calendar view output together with some series of yearly interval entries.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void testDailyViewYearlySeries() throws Exception {
        
        // create a test calendar
        CmsCalendar testCal = new CmsCalendar();
        
        // create first serial calendar entry
        CmsCalendarEntry entry = new CmsCalendarEntry();
        CmsCalendarEntryData data = new CmsCalendarEntryData();
        data.setTitle("First serial test entry");
        data.setDescription("Every 3rd Monday of April.");
        CmsCalendarEntryDateSerial serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 0, 1, 8, 40), new GregorianCalendar(2007, 0, 1, 8, 50));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);

        I_CmsCalendarSerialDateOptions serialOptions = new CmsCalendarSerialDateYearlyOptions(3, Calendar.MONDAY, 3);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates from Jan 2007 to Dec 2009
        List viewDates = new ArrayList();
        CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 0, 1, 0, 0, 0), new GregorianCalendar(2009, 11, 31, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
        List result = testCal.getEntries(view);
        // there should be 3 entries, one in each year
        assertEquals(3, result.size());
        // additional checks
        for (int i=0; i<result.size(); i++) {
            CmsCalendarEntry resEntry = (CmsCalendarEntry)result.get(i);
            assertEquals("First serial test entry", resEntry.getEntryData().getTitle());
            if (i == 0) {
                assertEquals(new GregorianCalendar(2007, 3, 16, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2007, 3, 16, 8, 50), resEntry.getEntryDate().getEndDate());
            }
            if (i == 1) {
                assertEquals(new GregorianCalendar(2008, 3, 21, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2008, 3, 21, 8, 50), resEntry.getEntryDate().getEndDate());
            }
            if (i == 2) {
                assertEquals(new GregorianCalendar(2009, 3, 20, 8, 40), resEntry.getEntryDate().getStartDate());
                assertEquals(new GregorianCalendar(2009, 3, 20, 8, 50), resEntry.getEntryDate().getEndDate());
            }
        }
        
        // remove first entry
        testCal.getEntries().remove(0);
        
        // create second serial calendar entry
        entry = new CmsCalendarEntry();
        data = new CmsCalendarEntryData();
        data.setTitle("Second serial test entry");
        data.setDescription("Every 15th of November to 2015.");
        serialDate = new CmsCalendarEntryDateSerial(new GregorianCalendar(2007, 0, 15, 15, 20), new GregorianCalendar(2007, 0, 15, 20, 20));
        serialDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_DATE);
        serialDate.setSerialEndDate(new GregorianCalendar(2015, 11, 31));
        
        serialOptions = new CmsCalendarSerialDateYearlyOptions(15, -1, 10);
        
        serialDate.setSerialOptions(serialOptions);
        entry.setEntryData(data);
        entry.setEntryDate(serialDate);
        testCal.addEntry(entry);
        
        // create the list of view dates from Sun, 09/16/2007 to Mon, 09/24/2007
        viewDates = new ArrayList();
        viewDate = new CmsCalendarEntryDate(new GregorianCalendar(2007, 0, 1, 0, 0, 0), new GregorianCalendar(2032, 11, 31, 23, 59, 59));        
        viewDates.add(viewDate);
        // create the daily view
        view = new CmsCalendarViewSimple(viewDates);
        result = testCal.getEntries(view);
        // there should be 9 entries from 2007 to 2015
        assertEquals(9, result.size());
        
    }
}
