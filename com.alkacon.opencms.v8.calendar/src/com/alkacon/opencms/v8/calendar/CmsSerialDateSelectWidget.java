/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsSerialDateSelectWidget.java,v $
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

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsSelectWidget;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.xml.content.I_CmsXmlContentHandler;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides a widget for a serial date select box.<p>
 * 
 * This can be used to define changes in the serial date for specific dates.
 * The widget can be configured to read the serial date information from a property,
 * the maximum number of available select box options can be configured as well:
 * <code><layout element="Change" widget="V8SerialDateSelectWidget" configuration="property:calendar.startdate|count:25" /></code>.<p>
 *
 * @author Andreas Zahner 
 */
public class CmsSerialDateSelectWidget extends CmsSelectWidget {

    /** The prefix for the configuration of the count of entries that should be shown in the select box. */
    public static final String PREFIX_COUNT = "count:";

    /** The prefix for the configuration of the property name to look up for the serial date configuration. */
    public static final String PREFIX_PROPERTY = I_CmsXmlContentHandler.MAPTO_PROPERTY;

    /** The count of the entry dates that should be shown in the select box.  */
    private int m_entryCount;

    /** The name of the property to read the serial date configuration from. */
    private String m_propertyName;

    /**
     * Creates a new select widget.<p>
     */
    public CmsSerialDateSelectWidget() {

        // empty constructor is required for class registration
        super();
    }

    /**
     * Creates a select widget with the specified select option configuration parameters.<p>
     * 
     * @param configuration the configuration parameters needed for the select box creation
     */
    public CmsSerialDateSelectWidget(String configuration) {

        super(configuration);
    }

    /**
     * Returns the calculated calendar entries from the given serial date values.<p>
     * 
     * @param serialDateValues the serial date values to use
     * @param locale the locale for the calendar calculation
     * @param maxCount the maximum count of entries
     * @return the calculated calendar entries
     */
    public static List getCalendarEntries(Map serialDateValues, Locale locale, int maxCount) {

        CmsCalendarEntryDateSerial serialDate = CmsCalendarSerialDateFactory.getSerialDate(serialDateValues, locale);
        List entries = new ArrayList(maxCount);
        if (serialDate != null) {
            // calculate the entries
            List viewDates = new ArrayList(1);
            // set start date for view creation
            Calendar startDate = (Calendar)serialDate.getStartDate().clone();
            startDate = CmsCalendarDisplay.setDayTime(startDate, 0, 0, 0);
            // set end date for view creation
            Calendar endDate = (Calendar)startDate.clone();
            if (serialDate.getSerialOptions().getSerialType() == I_CmsCalendarSerialDateOptions.TYPE_YEARLY) {
                // for yearly series, increase the end date
                endDate.add(Calendar.YEAR, maxCount);
            }
            endDate.add(Calendar.YEAR, maxCount);
            endDate = CmsCalendarDisplay.setDayTime(endDate, 23, 59, 59);
            CmsCalendarEntryDate viewDate = new CmsCalendarEntryDate(startDate, endDate);
            viewDates.add(viewDate);
            // create the simple view
            CmsCalendarViewSimple view = new CmsCalendarViewSimple(viewDates);
            CmsCalendarEntry entry = new CmsCalendarEntry();
            entry.setEntryDate(serialDate);
            // get the entries
            entries = serialDate.matchCalendarView(entry, view, maxCount);
        }
        return entries;
    }

    /**
     * Returns the count of the entry dates that should be shown in the select box.<p>
     * 
     * @return the count of the entry dates that should be shown in the select box
     */
    public int getEntryCount() {

        return m_entryCount;
    }

    /**
     * Returns the name of the property to read the serial date configuration from.<p>
     * 
     * @return the name of the property to read the serial date configuration from
     */
    public String getPropertyName() {

        return m_propertyName;
    }

    /**
     * Sets the select widget configuration options from the given configuration string.<p>
     * 
     * @param configuration the configuration string
     */
    public void initConfiguration(String configuration) {

        // set default values before parsing the configuration String
        setEntryCount(20);
        setPropertyName(CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(configuration)) {
            List parts = CmsStringUtil.splitAsList(configuration, CmsProperty.VALUE_LIST_DELIMITER, true);
            Iterator i = parts.iterator();
            while (i.hasNext()) {
                String part = (String)i.next();
                if (part.startsWith(PREFIX_COUNT)) {
                    String countStr = part.substring(PREFIX_COUNT.length());
                    try {
                        setEntryCount(Integer.parseInt(countStr));
                    } catch (Exception e) {
                        // ignore, use default entry count
                    }
                } else if (part.startsWith(PREFIX_PROPERTY)) {
                    setPropertyName(part.substring(PREFIX_PROPERTY.length()));
                }
            }
        }
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    public I_CmsWidget newInstance() {

        return new CmsSerialDateSelectWidget(getConfiguration());
    }

    /**
     * Sets the count of the entry dates that should be shown in the select box.<p>
     * 
     * @param entryCount the count of the entry dates that should be shown in the select box
     */
    public void setEntryCount(int entryCount) {

        m_entryCount = entryCount;
    }

    /**
     * Sets the name of the property to read the serial date configuration from.<p>
     * 
     * @param propertyName the name of the property to read the serial date configuration from
     */
    public void setPropertyName(String propertyName) {

        m_propertyName = propertyName;
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#parseSelectOptions(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    protected List parseSelectOptions(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        List result = new ArrayList(getEntryCount() + 1);
        // cast param to I_CmsXmlContentValue
        I_CmsXmlContentValue value = (I_CmsXmlContentValue)param;
        // initialize configuration before calculating the serial date entries
        initConfiguration(getConfiguration());
        result.add(new CmsSelectWidgetOption(
            "",
            false,
            widgetDialog.getMessages().key("GUI_SERIALDATE_WIDGET_SELECT_0")));
        try {
            // read the configured property value
            Map serialDateValues = cms.readPropertyObject(value.getDocument().getFile(), getPropertyName(), false).getValueMap(
                Collections.EMPTY_MAP);
            // get the entries
            List entries = getCalendarEntries(serialDateValues, value.getLocale(), getEntryCount());
            // create formatting objects to format select box options
            SimpleDateFormat df = new SimpleDateFormat("EE, ", widgetDialog.getLocale());
            NumberFormat nf = NumberFormat.getIntegerInstance(widgetDialog.getLocale());
            nf.setMinimumIntegerDigits(2);
            for (int i = 0; i < entries.size(); i++) {
                CmsCalendarEntry currEntry = (CmsCalendarEntry)entries.get(i);
                Calendar entryStart = currEntry.getEntryDate().getStartDate();
                // create displayed option text
                StringBuffer displayDate = new StringBuffer(32);
                displayDate.append(nf.format(i + 1));
                displayDate.append(": ");
                displayDate.append(df.format(entryStart.getTime()));
                displayDate.append(widgetDialog.getMessages().getDate(entryStart.getTime(), DateFormat.MEDIUM));
                CmsSelectWidgetOption option = new CmsSelectWidgetOption(
                    String.valueOf(i + 1),
                    false,
                    displayDate.toString());
                // add option to result list
                result.add(option);
            }
        } catch (CmsException e) {
            // ignore
        }

        return result;
    }
}