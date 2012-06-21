/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/test/TestCalendarSerialDateProperties.java,v $
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

import com.alkacon.opencms.v8.calendar.CmsCalendarDisplay;
import com.alkacon.opencms.v8.calendar.CmsCalendarEntryDateSerial;
import com.alkacon.opencms.v8.calendar.CmsCalendarSerialDateFactory;
import com.alkacon.opencms.v8.calendar.CmsCalendarSerialDateWeeklyOptions;
import com.alkacon.opencms.v8.calendar.I_CmsCalendarSerialDateOptions;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.types.CmsResourceTypePlain;
import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the calendar serial date property read and save capabilities.<p>
 * 
 * @author Andreas Zahner 
 */
public class TestCalendarSerialDateProperties extends OpenCmsTestCase {

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestCalendarSerialDateProperties(String arg0) {

        super(arg0);
    }

    /**
     * Test the read serial date from file property value on a file.<p>
     * 
     * @param tc the OpenCmsTestCase
     * @param cms the CmsObject
     * @param resource1 the resource to store and test
     * @param originalResType the original resource type
     * @throws Throwable if something goes wrong
     */
    public static void readSerialDateFromProperty(
        OpenCmsTestCase tc,
        CmsObject cms,
        String resource1,
        int originalResType) throws Throwable {

        Locale locale = Locale.GERMANY;
        // create a new resource with manually set serial date property
        StringBuffer propValue = new StringBuffer(2048);

        Calendar startDate = new GregorianCalendar(locale);
        startDate.set(2007, 7, 6, 8, 0);
        propValue.append(I_CmsCalendarSerialDateOptions.CONFIG_STARTDATE).append("=").append(
            startDate.getTimeInMillis());
        Calendar endDate = new GregorianCalendar(locale);
        endDate.set(2007, 7, 6, 9, 0);
        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_ENDDATE).append("=").append(
            endDate.getTimeInMillis());
        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_END_TYPE).append("=").append(
            I_CmsCalendarSerialDateOptions.END_TYPE_TIMES);
        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_OCCURENCES).append("=").append(3);

        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_TYPE).append("=").append(
            I_CmsCalendarSerialDateOptions.TYPE_WEEKLY);
        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL).append("=").append(2);
        propValue.append("|").append(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS).append("=").append(Calendar.MONDAY);

        // create a property with the manually set values
        CmsProperty prop = new CmsProperty(CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE, propValue.toString(), null);
        List props = new ArrayList();
        props.add(prop);

        // create and store the resource
        cms.createResource(resource1, originalResType, new byte[0], props);
        tc.storeResources(cms, resource1);

        // read the property from the resource
        CmsProperty property = cms.readPropertyObject(resource1, CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE, false);

        // get the serial date object from the property
        CmsCalendarEntryDateSerial serialDate = CmsCalendarSerialDateFactory.getSerialDate(
            property.getValueMap(),
            locale);

        // now check if the serial date was correctly initialized
        checkWeeklyDateEntry(startDate, endDate, serialDate);

        // create the property value and write it back to resource
        property = property.cloneAsProperty();
        property.setStructureValueMap(serialDate.getConfigurationValuesAsMap());
        cms.writePropertyObject(resource1, property);
        System.out.println("Property value: " + property.getStructureValue());

        // read the automatically generated value and create serial date object to check from it
        CmsProperty property2 = cms.readPropertyObject(resource1, CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE, false);
        serialDate = CmsCalendarSerialDateFactory.getSerialDate(property2.getValueMap(), locale);
        checkWeeklyDateEntry(startDate, endDate, serialDate);

    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        TestSuite suite = new TestSuite();
        suite.setName(TestCalendarSerialDateProperties.class.getName());

        suite.addTest(new TestCalendarSerialDateProperties("testSerialDateAndProperty"));

        TestSetup wrapper = new TestSetup(suite) {

            protected void setUp() {

                setupOpenCms("simpletest", "/sites/default/");
            }

            protected void tearDown() {

                removeOpenCms();
            }
        };

        return wrapper;
    }

    /**
     * Checks the created weekly serial date entry against the configured values.<p>
     * @param startDate the entry start date
     * @param endDate the entry end date
     * @param serialDate the generated serial date
     */
    protected static void checkWeeklyDateEntry(
        Calendar startDate,
        Calendar endDate,
        CmsCalendarEntryDateSerial serialDate) {

        assertEquals(startDate.getTimeInMillis(), serialDate.getStartDate().getTimeInMillis());
        assertEquals(endDate.getTimeInMillis(), serialDate.getEndDate().getTimeInMillis());
        assertEquals(I_CmsCalendarSerialDateOptions.END_TYPE_TIMES, serialDate.getSerialEndType());
        assertEquals(3, serialDate.getOccurences());
        assertEquals(I_CmsCalendarSerialDateOptions.TYPE_WEEKLY, serialDate.getSerialOptions().getSerialType());
        CmsCalendarSerialDateWeeklyOptions options = (CmsCalendarSerialDateWeeklyOptions)serialDate.getSerialOptions();
        assertEquals(Calendar.MONDAY, ((Integer)options.getWeekDays().get(0)).intValue());
        assertEquals(2, options.getWeeklyInterval());
    }

    /**
     * Test the serial date read and write back operations from OpenCms VFS files.<p>
     * 
     * @throws Throwable if something goes wrong
     */
    public void testSerialDateAndProperty() throws Throwable {

        CmsObject cms = getCmsObject();
        echo("Testing chtype on a new file");
        readSerialDateFromProperty(this, cms, "/calendar.txt", CmsResourceTypePlain.getStaticTypeId());
    }

}