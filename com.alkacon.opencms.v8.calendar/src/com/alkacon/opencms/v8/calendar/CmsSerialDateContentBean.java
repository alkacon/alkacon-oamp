/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsSerialDateContentBean.java,v $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Provides methods to generate the detail page of the serial date, depending on the passed request parameter value
 * for the start date.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsSerialDateContentBean extends CmsJspActionElement implements I_CmsCalendarSerialDateContent {

    /** Node name for the "Location" element. */
    public static final String NODE_LOCATION = "Location";

    /** Node name for the "Showtime" element. */
    public static final String NODE_SHOWTIME = "Showtime";

    /** Node name for the "Teaser" element. */
    public static final String NODE_TEASER = "Teaser";

    /** Node name for the "Text" element. */
    public static final String NODE_TEXT = "Text";

    /** Node name for the "Title" element. */
    public static final String NODE_TITLE = "Title";

    /** The calendar start date parameter value. */
    private String m_calendarStartParam;

    /** The XML content to get the values from. */
    private CmsXmlContent m_content;

    /** The name of the detail file to render. */
    private String m_detailFile;

    /** The serial date entry. */
    private CmsCalendarEntryDateSerial m_serialDateEntry;

    /** The xpath prefix depending on the serial date start date and if a change is defined for that date. */
    private String m_xpathPrefix;

    /**
     * Empty constructor, needed for every Java bean.<p>
     */
    public CmsSerialDateContentBean() {

        super();
    }

    /**
     * Constructor, with parameters.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsSerialDateContentBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Returns the name of the detail file to render.<p>
     * 
     * @return the name of the detail file to render
     */
    public String getDetailFile() {

        return m_detailFile;
    }

    /**
     * Returns the end date of the serial date to show, depending on the start date.<p>
     * 
     * @return the end date of the serial date to show
     */
    public Date getEndDate() {

        Date result = new Date();
        CmsCalendarEntryDateSerial serialDate = getSerialDate();
        if (serialDate != null) {
            Calendar startDate = new GregorianCalendar(getRequestContext().getLocale());
            String calDatParam = getCalendarStartParam();
            if (CmsStringUtil.isNotEmpty(calDatParam)) {
                // calculate matching end date from given start date
                startDate.setTimeInMillis(Long.parseLong(calDatParam));
                // important: get duration before setting start date, otherwise it will be reset
                int duration = serialDate.getDuration();
                serialDate.setStartDate(startDate, true);
                serialDate.setStartDate(startDate);
                Calendar endDate = (GregorianCalendar)startDate.clone();
                endDate.add(Calendar.DATE, duration);
                long endTime = serialDate.getEndTime();

                endDate.set(Calendar.HOUR_OF_DAY, 0);
                endDate.set(Calendar.MINUTE, 0);
                endDate.set(Calendar.SECOND, 0);
                endDate.add(Calendar.MILLISECOND, (int)endTime);
                serialDate.setEndDate(endDate);
            }
            result = new Date(serialDate.getEndDate().getTimeInMillis());
        }
        getJspContext().setAttribute("enddate", result);
        return result;
    }

    /**
     * Returns the serial entry for the calendar generation of the passed resource.<p>
     * 
     * @param cms the current users context
     * @param resource the resource to generate the serial entry from
     * @return the serial entry
     */
    public CmsCalendarEntry getSerialEntryForCalendar(CmsObject cms, CmsResource resource) {

        // first create the entry data
        CmsCalendarEntryData entryData = new CmsCalendarEntryData();
        Map values = new HashMap();
        try {
            String title = cms.readPropertyObject(resource, CmsPropertyDefinition.PROPERTY_TITLE, false).getValue("");
            String showTimeStr = cms.readPropertyObject(
                resource,
                I_CmsCalendarEntryData.PROPERTY_CALENDAR_SHOWTIME,
                false).getValue(CmsStringUtil.TRUE);
            String type = OpenCms.getResourceManager().getResourceType(resource.getTypeId()).getTypeName();
            entryData.setTitle(title);
            entryData.setShowTime(Boolean.valueOf(showTimeStr).booleanValue());
            entryData.setType(type);
            entryData.setDetailUri(cms.getRequestContext().getSitePath(resource));
            entryData.setDescription(cms.readPropertyObject(resource, CmsPropertyDefinition.PROPERTY_DESCRIPTION, false).getValue(
                ""));
            // read serial date property value
            values = cms.readPropertyObject(resource, CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE, false).getValueMap(
                values);
        } catch (CmsException e) {
            // failed to read a property, no serial entry can be created, return null
            return null;
        }
        Locale locale = cms.getRequestContext().getLocale();
        // second create the serial date
        CmsCalendarEntryDateSerial serialDate = CmsCalendarSerialDateFactory.getSerialDate(values, locale);
        String serialChanges = null;
        try {
            serialChanges = cms.readPropertyObject(
                resource,
                CmsSerialDateXmlContentHandler.PROPERTY_SERIALDATE_CHANGE,
                false).getValue();
        } catch (CmsException e) {
            // failed to read property, ignore
            serialChanges = "";
        }
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(serialChanges)) {
            // there are some changes in the series we have to check
            CmsXmlContent content = null;
            if (serialChanges.indexOf(CmsSerialDateXmlContentHandler.SERIES_FLAG_CHANGED) != -1) {
                // there are changes, we have to unmarshal the content
                try {
                    content = CmsXmlContentFactory.unmarshal(cms, cms.readFile(resource));
                } catch (CmsException e) {
                    // ignore
                }
            }
            List changeList = CmsStringUtil.splitAsList(serialChanges, CmsProperty.VALUE_LIST_DELIMITER);
            for (int i = 0; i < changeList.size(); i++) {
                String change = (String)changeList.get(i);
                String[] changeEntry = CmsStringUtil.splitAsArray(change, CmsProperty.VALUE_MAP_DELIMITER);
                // get the start date from the string part
                Calendar startDate = new GregorianCalendar(locale);
                startDate.setTimeInMillis(Long.parseLong(changeEntry[0]));
                CmsCalendarEntryData entryDataClone = null;
                if (changeEntry[1].equals(CmsSerialDateXmlContentHandler.SERIES_FLAG_CHANGED)) {
                    // this is an entry that should be changed
                    entryDataClone = (CmsCalendarEntryData)entryData.clone();
                    String xPath = CmsSerialDateXmlContentHandler.NODE_CHANGE + "[" + (i + 1) + "]/";
                    if (content != null) {
                        if (content.hasValue(xPath + NODE_TITLE, locale)) {
                            entryDataClone.setTitle(content.getStringValue(cms, xPath + NODE_TITLE, locale));
                        }
                        if (content.hasValue(xPath + NODE_TEASER, locale)) {
                            entryDataClone.setDescription(content.getStringValue(cms, xPath + NODE_TEASER, locale));
                        }
                        if (content.hasValue(xPath + NODE_SHOWTIME, locale)) {
                            entryDataClone.setShowTime(Boolean.valueOf(
                                content.getStringValue(cms, xPath + NODE_SHOWTIME, locale)).booleanValue());
                        }
                    }
                }
                serialDate.getSerialOptions().addSerialDateChange(
                    new CmsCalendarSerialDateChange(startDate, entryDataClone));
            }
        }

        // check interruptions
        String serialInterruptions = null;
        try {
            serialInterruptions = cms.readPropertyObject(
                resource,
                CmsSerialDateXmlContentHandler.PROPERTY_SERIALDATE_INTERRUPTION,
                false).getValue();
        } catch (CmsException e) {
            // failed to read property, ignore
        }
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(serialInterruptions)) {
            // get the interruptions and analyze the String
            List interruptionList = CmsStringUtil.splitAsList(serialInterruptions, CmsProperty.VALUE_LIST_DELIMITER);
            for (int i = 0; i < interruptionList.size(); i++) {
                String interruption = (String)interruptionList.get(i);
                String[] interruptionEntry = CmsStringUtil.splitAsArray(interruption, CmsProperty.VALUE_MAP_DELIMITER);
                // get the start date of the interruption
                Calendar startDate = new GregorianCalendar(locale);
                startDate.setTimeInMillis(Long.parseLong(interruptionEntry[0]));
                // get the end date of the interruption
                Calendar endDate = new GregorianCalendar(locale);
                endDate.setTimeInMillis(Long.parseLong(interruptionEntry[1]));
                // add the interruption to the serial date
                serialDate.getSerialOptions().addSerialDateInterruption(
                    new CmsCalendarSerialDateInterruption(startDate, endDate));
            }
        }
        return new CmsCalendarEntry(entryData, serialDate);
    }

    /**
     * Returns the start date of the serial date to show.<p>
     * 
     * @return the start date of the serial date to show
     */
    public Date getStartDate() {

        Calendar startDate = new GregorianCalendar(getRequestContext().getLocale());
        String calDatParam = getCalendarStartParam();
        if (CmsStringUtil.isNotEmpty(calDatParam)) {
            // calculate matching end date from given start date
            startDate.setTimeInMillis(Long.parseLong(calDatParam));
        } else {
            CmsCalendarEntryDateSerial serialDate = getSerialDate();
            if (serialDate != null) {
                startDate = serialDate.getStartDate();
            }
        }
        Date result = new Date(startDate.getTimeInMillis());
        getJspContext().setAttribute("startdate", result);
        return result;
    }

    /**
     * Returns the value of the element with the provided path.<p>
     * 
     * @param path the path of the element
     * @return the value of the element
     */
    public String getStringValue(String path) {

        String xPath = getXpathPrefix() + path;
        if (!getContent().hasValue(xPath, getRequestContext().getLocale())) {
            // changed value not activated, switch back to base value
            xPath = path;
        }
        return getContent().getStringValue(getCmsObject(), xPath, getRequestContext().getLocale());
    }

    /**
     * Returns the value of the "Location" element of the serial date.<p>
     * 
     * @return the value of the "Location" element
     */
    public String getValueLocation() {

        return getStringValue(NODE_LOCATION);
    }

    /**
     * Returns the value of the "Text" element of the serial date.<p>
     * 
     * @return the value of the "Text" element
     */
    public String getValueText() {

        return getStringValue(NODE_TEXT);
    }

    /**
     * Returns the value of the "Title" element of the serial date.<p>
     * 
     * @return the value of the "Title" element
     */
    public String getValueTitle() {

        return getStringValue(NODE_TITLE);
    }

    /**
     * Returns if the value exists in the content.<p>
     * 
     * @param path the path of the element to check
     * @return true if the value exists in the content, otherwise false
     */
    public boolean hasValue(String path) {

        String xPath = getXpathPrefix() + path;
        if (!getContent().hasValue(xPath, getRequestContext().getLocale())) {
            // changed value not activated, switch back to base value
            xPath = path;
        }
        return getContent().hasValue(xPath, getRequestContext().getLocale());
    }

    /**
     * @see org.opencms.jsp.CmsJspBean#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super.init(context, req, res);
        m_detailFile = getRequestContext().getUri();
    }

    /**
     * Checks if the start and end date have the same date and differ only from their time.<p>
     * 
     * @return true if the two dates differ only in time, otherwise false
     */
    public boolean isSameDate() {

        return isSameDate(getStartDate(), getEndDate());
    }

    /**
     * Checks if two dates have the same date and differ only from their time.<p>
     * 
     * @param start the the start date
     * @param end the end date
     * @return true if the two dates differ only in time, otherwise false
     */
    public boolean isSameDate(Date start, Date end) {

        Calendar calStart = new GregorianCalendar();
        calStart.setTimeInMillis(start.getTime());

        Calendar calEnd = new GregorianCalendar();
        calEnd.setTimeInMillis(end.getTime());

        return ((calStart.get(Calendar.DAY_OF_YEAR) == calEnd.get(Calendar.DAY_OF_YEAR)) && (calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)));
    }

    /**
     * Checks if two dates in the page context attributes have the same date and differ only from their time.<p>
     * 
     * @param startDateAttrib the name of the page context attribute containing the start date Date
     * @param endDateAttrib the name of the page context attribute containing the end date Date
     * @return true if the two dates differ only in time, otherwise false
     */
    public boolean isSameDate(String startDateAttrib, String endDateAttrib) {

        Date start = (Date)getJspContext().getAttribute(startDateAttrib);
        Date end = (Date)getJspContext().getAttribute(endDateAttrib);
        return isSameDate(start, end);
    }

    /**
     * Returns if the time information should be shown.<p>
     * 
     * @return true if the time information should be shown, otherwise false
     */
    public boolean isShowTime() {

        String showTimeValue = getStringValue(NODE_SHOWTIME);
        return Boolean.valueOf(showTimeValue).booleanValue();
    }

    /**
     * Returns the calendar start date parameter value.<p>
     * 
     * @return the calendar start date parameter value
     */
    protected String getCalendarStartParam() {

        if (m_calendarStartParam == null) {
            m_calendarStartParam = getRequest().getParameter(CmsCalendarDisplay.PARAM_DATE);
        }
        return m_calendarStartParam;
    }

    /**
     * Returns the XML content to get the values from.<p>
     * 
     * @return the XML content
     */
    protected CmsXmlContent getContent() {

        if (m_content == null) {
            try {
                CmsFile file = getCmsObject().readFile(getDetailFile());
                m_content = CmsXmlContentFactory.unmarshal(getCmsObject(), file);
            } catch (CmsException e) {
                // ignore, should never happen
            }
        }
        return m_content;
    }

    /**
     * Returns the serial date generated from the serial date property values on the resource to show.<p>
     * 
     * @return the serial date or <code>null</code>, if generating the serial date fails
     */
    protected CmsCalendarEntryDateSerial getSerialDate() {

        if (m_serialDateEntry == null) {
            Map values = new HashMap();
            try {
                values = getCmsObject().readPropertyObject(
                    getDetailFile(),
                    CmsCalendarDisplay.PROPERTY_CALENDAR_STARTDATE,
                    false).getValueMap(values);
            } catch (CmsException e) {
                // failed to read property
            }
            m_serialDateEntry = CmsCalendarSerialDateFactory.getSerialDate(values, getRequestContext().getLocale());
        }
        return m_serialDateEntry;
    }

    /**
     * Returns the xpath prefix depending on the serial date start date and if a change is defined for that date.<p>
     * 
     * @return the xpath prefix for the content values
     */
    protected String getXpathPrefix() {

        if (m_xpathPrefix == null) {
            m_xpathPrefix = "";
            String calDatParam = getCalendarStartParam();
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(calDatParam)) {
                // found a calendar date parameter, check if a changed date should be shown
                try {
                    List changeDateValues = getCmsObject().readPropertyObject(
                        getDetailFile(),
                        CmsSerialDateXmlContentHandler.PROPERTY_SERIALDATE_CHANGE,
                        false).getValueList(Collections.EMPTY_LIST);
                    for (int i = 0; i < changeDateValues.size(); i++) {
                        String currentDate = (String)changeDateValues.get(i);
                        currentDate = currentDate.substring(0, currentDate.indexOf(CmsProperty.VALUE_MAP_DELIMITER));
                        if (calDatParam.equals(currentDate)) {
                            m_xpathPrefix = CmsSerialDateXmlContentHandler.NODE_CHANGE + "[" + (i + 1) + "]/";
                            break;
                        }
                    }
                } catch (CmsException e) {
                    // ignore
                }
            }
        }
        return m_xpathPrefix;
    }
}
