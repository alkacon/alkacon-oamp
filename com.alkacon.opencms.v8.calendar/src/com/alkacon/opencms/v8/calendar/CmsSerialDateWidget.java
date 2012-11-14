/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.calendar/src/com/alkacon/opencms/v8/calendar/CmsSerialDateWidget.java,v $
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

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsMessages;
import org.opencms.json.JSONException;
import org.opencms.json.JSONObject;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsCalendarWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.editors.CmsXmlContentEditor;
import org.opencms.xml.types.A_CmsXmlContentValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;

/**
 * Provides a serial date widget, for use on a widget dialog.<p>
 *
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.2 
 */
public class CmsSerialDateWidget extends CmsCalendarWidget {

    /** Attribute "checked" to set seleected options in form. */
    public static final String ATTR_CHECKED = "checked=\"checked\"";

    /** Macro prefix for request parameter values. */
    public static final String MACRO_PREFIX_PARAMVALUE = "val.";

    /** The request parameter name for the day interval parameter. */
    public static final String PARAM_DAY_DAILYINTERVAL = "sd_dailyinterval";

    /** The request parameter name for the day everyworkingday parameter. */
    public static final String PARAM_DAY_EVERYWORKINGDAY = "sd_everyworkingday";

    /** The request parameter name for the duration days parameter. */
    public static final String PARAM_DURATIONDAYS = "durationdays";

    /** The request parameter name for the serial enddate parameter. */
    public static final String PARAM_ENDDATE = "serialenddate";

    /** The request parameter name for the endtime parameter. */
    public static final String PARAM_ENDTIME = "endtime";

    /** The request parameter name for the endtype parameter. */
    public static final String PARAM_ENDTYPE = "endtype";

    /** The request parameter name for the month dayofmonth parameter. */
    public static final String PARAM_MONTH_DAYOFMONTH = "sm_dayofmonth";

    /** The request parameter name for the month interval parameter. */
    public static final String PARAM_MONTH_MONTHLYINTERVAL = "sm_monthlyinterval";

    /** The request parameter name for the month weekday interval parameter. */
    public static final String PARAM_MONTH_MONTHLYINTERVALWEEKDAY = "sm_monthlyinterval2";

    /** The request parameter name for the month number of weekdayofmonth parameter. */
    public static final String PARAM_MONTH_NUMBEROFWEEKDAYOFMONTH = "sm_dayofmonth2";

    /** The request parameter name for the month serialmonthday parameter. */
    public static final String PARAM_MONTH_SERIALMONTHDAY = "sm_serialmonthday";

    /** The request parameter name for the month weekday parameter. */
    public static final String PARAM_MONTH_WEEKDAY = "sm_weekday";

    /** The request parameter name for the occurences parameter. */
    public static final String PARAM_OCCURENCES = "occurences";

    /** The request parameter name for the serialtype parameter. */
    public static final String PARAM_SERIALTYPE = "serialtype";

    /** The request parameter name for the startdate parameter. */
    public static final String PARAM_STARTDATE = "startdate";

    /** The request parameter name for the starttime parameter. */
    public static final String PARAM_STARTTIME = "starttime";

    /** The request parameter name for the week weekday parameter. */
    public static final String PARAM_WEEK_WEEKDAY = "sw_weekday";

    /** The request parameter name for the week interval parameter. */
    public static final String PARAM_WEEK_WEEKLYINTERVAL = "sw_weeklyinterval";

    /** The request parameter name for the year dayofmonth parameter. */
    public static final String PARAM_YEAR_DAYOFMONTH = "sy_dayofmonth";

    /** The request parameter name for the year month parameter. */
    public static final String PARAM_YEAR_MONTH = "sy_month";

    /** The request parameter name for the year serialyearday parameter. */
    public static final String PARAM_YEAR_SERIALYEARDAY = "sy_serialyearday";

    /** The request parameter name for the year weekday parameter. */
    public static final String PARAM_YEAR_WEEKDAY = "sy_weekday";

    /** The request parameter name for the year weekdaymonth parameter. */
    public static final String PARAM_YEAR_WEEKDAYMONTH = "sy_month2";

    /** The request parameter name for the year weekdayofmonth parameter. */
    public static final String PARAM_YEAR_WEEKDAYOFMONTH = "sy_dayofmonth2";

    /** The possible numbers of the week day to select (from 1 to 5). */
    public static final String[] VALUES_NUMBEROFWEEKDAYOFMONTH = {"1", "2", "3", "4", "5"};

    /** The possible numbers of the week day to select (from 1 to 5) as List. */
    public static final List<String> VALUES_NUMBEROFWEEKDAYOFMONTH_LIST = Arrays.asList(VALUES_NUMBEROFWEEKDAYOFMONTH);

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsSerialDateWidget.class);

    /** The VFS path to the file containing the form HTML. */
    private static final String VFS_PATH_FORMHTML = CmsWorkplace.VFS_PATH_RESOURCES
        + "components/widgets/v8-serialdate_form.html";

    /**
     * Creates a new calendar widget.<p>
     */
    public CmsSerialDateWidget() {

        // empty constructor is required for class registration
        this("");
    }

    /**
     * Creates a new calendar widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsSerialDateWidget(String configuration) {

        super(configuration);
    }

    /**
     * Creates the time in milliseconds from the given parameter.<p>
     * 
     * @param messages the messages that contain the time format definitions
     * @param dateString the String representation of the date
     * @param useDate true, if the date should be parsed, otherwise false
     * @param useTime true if the time should be parsed, too, otherwise false
     * 
     * @return the time in milliseconds
     * 
     * @throws ParseException if something goes wrong
     */
    public static long getCalendarDate(CmsMessages messages, String dateString, boolean useDate, boolean useTime)
    throws ParseException {

        long dateLong = 0;

        // substitute some chars because calendar syntax != DateFormat syntax
        String dateFormat = "";
        if (useDate) {
            dateFormat = messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0);
        }
        if (useTime) {
            if (useDate) {
                dateFormat += " ";
            }
            dateFormat += messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0);
        }
        dateFormat = CmsCalendarWidget.getCalendarJavaDateFormat(dateFormat);

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        dateLong = df.parse(dateString).getTime();
        return dateLong;
    }

    /**
     * Returns the given timestamp as String formatted in a localized pattern.<p>
     * 
     * @param locale the locale for the time format
     * @param messages the messages that contain the time format definitions
     * @param timestamp the time to format
     * @param showDate flag to show the date in the formatted String
     * @param showTime flag to show the time in the formatted String
     * 
     * @return the given timestamp as String formatted in a localized pattern
     */
    public static String getCalendarLocalizedTime(
        Locale locale,
        CmsMessages messages,
        long timestamp,
        boolean showDate,
        boolean showTime) {

        // get the current date & time 
        TimeZone zone = TimeZone.getDefault();
        GregorianCalendar cal = new GregorianCalendar(zone, locale);
        cal.setTimeInMillis(timestamp);
        String datePattern = "";
        if (showDate) {
            datePattern = messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0);
            if (showTime) {
                datePattern += " ";
            }
        }
        if (showTime) {
            datePattern += messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0);
        }
        // format it nicely according to the localized pattern
        DateFormat df = new SimpleDateFormat(CmsCalendarWidget.getCalendarJavaDateFormat(datePattern));
        return df.format(cal.getTime());
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getConfiguration(org.opencms.file.CmsObject, org.opencms.xml.types.A_CmsXmlContentValue, org.opencms.i18n.CmsMessages, org.opencms.file.CmsResource, java.util.Locale)
     */
    @Override
    public String getConfiguration(
        CmsObject cms,
        A_CmsXmlContentValue schemaType,
        CmsMessages messages,
        CmsResource resource,
        Locale contentLocale) {

        JSONObject message = new JSONObject();

        try {

            message.put("GUI_SERIALDATE_TIME_STARTTIME_0", messages.key("GUI_SERIALDATE_TIME_STARTTIME_0"));
            message.put("GUI_SERIALDATE_TIME_ENDTIME_0", messages.key("GUI_SERIALDATE_TIME_ENDTIME_0"));

            message.put("GUI_SERIALDATE_TIME_STARTDATE_0", messages.key("GUI_SERIALDATE_TIME_STARTDATE_0"));

            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_SAMEDAY_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_SAMEDAY_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_FIRST_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_FIRST_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_SECOND_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_SECOND_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_THIRD_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_THIRD_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_FOURTH_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_FOURTH_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_FIFTH_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_FIFTH_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_SIXTH_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_SIXTH_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_ONEWEEK_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_ONEWEEK_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_DURATION_TWOWEEK_0",
                messages.key("GUI_SERIALDATE_DURATION_DURATION_TWOWEEK_0"));

            message.put("GUI_SERIALDATE_TYPE_DAILY_0", messages.key("GUI_SERIALDATE_TYPE_DAILY_0"));
            message.put("GUI_SERIALDATE_TYPE_WEEKLY_0", messages.key("GUI_SERIALDATE_TYPE_WEEKLY_0"));
            message.put("GUI_SERIALDATE_TYPE_MONTHLY_0", messages.key("GUI_SERIALDATE_TYPE_MONTHLY_0"));
            message.put("GUI_SERIALDATE_TYPE_YEARLY_0", messages.key("GUI_SERIALDATE_TYPE_YEARLY_0"));

            message.put("GUI_SERIALDATE_DAILY_EVERY_0", messages.key("GUI_SERIALDATE_DAILY_EVERY_0"));
            message.put("GUI_SERIALDATE_DAILY_DAYS_0", messages.key("GUI_SERIALDATE_DAILY_DAYS_0"));
            message.put(
                "GUI_SERIALDATE_DAILY_EVERYWORKINGDAY_0",
                messages.key("GUI_SERIALDATE_DAILY_EVERYWORKINGDAY_0"));

            message.put("GUI_SERIALDATE_WEEKLY_EVERY_0", messages.key("GUI_SERIALDATE_WEEKLY_EVERY_0"));
            message.put("GUI_SERIALDATE_WEEKLY_WEEK_AT_0", messages.key("GUI_SERIALDATE_WEEKLY_WEEK_AT_0"));

            message.put("GUI_SERIALDATE_DAY_MONDAY_0", messages.key("GUI_SERIALDATE_DAY_MONDAY_0"));
            message.put("GUI_SERIALDATE_DAY_TUESDAY_0", messages.key("GUI_SERIALDATE_DAY_TUESDAY_0"));
            message.put("GUI_SERIALDATE_DAY_WEDNESDAY_0", messages.key("GUI_SERIALDATE_DAY_WEDNESDAY_0"));
            message.put("GUI_SERIALDATE_DAY_THURSDAY_0", messages.key("GUI_SERIALDATE_DAY_THURSDAY_0"));
            message.put("GUI_SERIALDATE_DAY_FRIDAY_0", messages.key("GUI_SERIALDATE_DAY_FRIDAY_0"));
            message.put("GUI_SERIALDATE_DAY_SATURDAY_0", messages.key("GUI_SERIALDATE_DAY_SATURDAY_0"));
            message.put("GUI_SERIALDATE_DAY_SUNDAY_0", messages.key("GUI_SERIALDATE_DAY_SUNDAY_0"));

            message.put("GUI_SERIALDATE_MONTHLY_MONTHDAY_AT_0", messages.key("GUI_SERIALDATE_MONTHLY_MONTHDAY_AT_0"));
            message.put(
                "GUI_SERIALDATE_MONTHLY_MONTHDAY_DAY_EVERY_0",
                messages.key("GUI_SERIALDATE_MONTHLY_MONTHDAY_DAY_EVERY_0"));
            message.put("GUI_SERIALDATE_MONTHLY_MONTH_0", messages.key("GUI_SERIALDATE_MONTHLY_MONTH_0"));

            message.put("GUI_SERIALDATE_MONTHLY_WEEKDAY_AT_0", messages.key("GUI_SERIALDATE_MONTHLY_WEEKDAY_AT_0"));
            message.put(
                "GUI_SERIALDATE_MONTHLY_WEEKDAY_EVERY_0",
                messages.key("GUI_SERIALDATE_MONTHLY_WEEKDAY_EVERY_0"));

            message.put("GUI_SERIALDATE_YEARLY_EVERY_0", messages.key("GUI_SERIALDATE_YEARLY_EVERY_0"));

            message.put("GUI_SERIALDATE_YEARLY_AT_0", messages.key("GUI_SERIALDATE_YEARLY_AT_0"));
            message.put("GUI_SERIALDATE_YEARLY_IN_0", messages.key("GUI_SERIALDATE_YEARLY_IN_0"));

            message.put("GUI_SERIALDATE_YEARLY_JAN_0", messages.key("GUI_SERIALDATE_YEARLY_JAN_0"));
            message.put("GUI_SERIALDATE_YEARLY_FEB_0", messages.key("GUI_SERIALDATE_YEARLY_FEB_0"));
            message.put("GUI_SERIALDATE_YEARLY_MAR_0", messages.key("GUI_SERIALDATE_YEARLY_MAR_0"));
            message.put("GUI_SERIALDATE_YEARLY_APR_0", messages.key("GUI_SERIALDATE_YEARLY_APR_0"));
            message.put("GUI_SERIALDATE_YEARLY_MAY_0", messages.key("GUI_SERIALDATE_YEARLY_MAY_0"));
            message.put("GUI_SERIALDATE_YEARLY_JUN_0", messages.key("GUI_SERIALDATE_YEARLY_JUN_0"));
            message.put("GUI_SERIALDATE_YEARLY_JUL_0", messages.key("GUI_SERIALDATE_YEARLY_JUL_0"));
            message.put("GUI_SERIALDATE_YEARLY_AUG_0", messages.key("GUI_SERIALDATE_YEARLY_AUG_0"));
            message.put("GUI_SERIALDATE_YEARLY_SEP_0", messages.key("GUI_SERIALDATE_YEARLY_SEP_0"));
            message.put("GUI_SERIALDATE_YEARLY_OCT_0", messages.key("GUI_SERIALDATE_YEARLY_OCT_0"));
            message.put("GUI_SERIALDATE_YEARLY_NOV_0", messages.key("GUI_SERIALDATE_YEARLY_NOV_0"));
            message.put("GUI_SERIALDATE_YEARLY_DEC_0", messages.key("GUI_SERIALDATE_YEARLY_DEC_0"));

            message.put("GUI_SERIALDATE_WEEKDAYNUMBER_1_0", messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_1_0"));
            message.put("GUI_SERIALDATE_WEEKDAYNUMBER_2_0", messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_2_0"));
            message.put("GUI_SERIALDATE_WEEKDAYNUMBER_3_0", messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_3_0"));
            message.put("GUI_SERIALDATE_WEEKDAYNUMBER_4_0", messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_4_0"));
            message.put("GUI_SERIALDATE_WEEKDAYNUMBER_5_0", messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_5_0"));

            message.put("GUI_SERIALDATE_DURATION_BEGIN_0", messages.key("GUI_SERIALDATE_DURATION_BEGIN_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_ENDTYPE_NEVER_0",
                messages.key("GUI_SERIALDATE_DURATION_ENDTYPE_NEVER_0"));
            message.put("GUI_SERIALDATE_DURATION_ENDTYPE_OCC_0", messages.key("GUI_SERIALDATE_DURATION_ENDTYPE_OCC_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_ENDTYPE_OCC_TIMES_0",
                messages.key("GUI_SERIALDATE_DURATION_ENDTYPE_OCC_TIMES_0"));
            message.put(
                "GUI_SERIALDATE_DURATION_ENDTYPE_DATE_0",
                messages.key("GUI_SERIALDATE_DURATION_ENDTYPE_DATE_0"));

        } catch (JSONException e) {
            LOG.error(e);
        }

        return message.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogIncludes(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogIncludes(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        StringBuffer result = new StringBuffer(4096);

        // check if the calendar widget is already used to avoid including the calendar scripts twice
        boolean hasCalendarIncludes = false;
        CmsXmlContentEditor editor = (CmsXmlContentEditor)widgetDialog;
        Iterator<I_CmsWidget> i = editor.getWidgetCollector().getUniqueWidgets().iterator();
        while (i.hasNext()) {
            I_CmsWidget widget = i.next();
            if ((widget instanceof CmsCalendarWidget) && !(widget instanceof CmsSerialDateWidget)) {
                hasCalendarIncludes = true;
                break;
            }
        }
        if (!hasCalendarIncludes) {
            // no calendar includes found, include them now
            result.append(calendarIncludes(widgetDialog.getLocale()));
        }

        String widgetPath = CmsWorkplace.getSkinUri() + "components/widgets/";
        result.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        result.append(widgetPath);
        result.append("v8-serialdate_styles.css\">\n");
        result.append("<script type=\"text/javascript\" src=\"");
        result.append(widgetPath);
        result.append("v8-serialdate_script.js\"></script>\n");
        return result.toString();
    }

    /**
     * @see org.opencms.widgets.A_CmsWidget#getDialogInitCall(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogInitCall(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        return "\tsetTimeout(\"initSerialTab()\", 100);\n";
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    public String getDialogWidget(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        StringBuffer result = new StringBuffer(16384);
        result.append("<td class=\"xmlTd\">");

        // first get html with macros to resolve for form from VFS cache
        String formContent = "";
        Object cachedContent = CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().getCachedObject(cms, VFS_PATH_FORMHTML);
        if (cachedContent == null) {
            try {
                formContent = new String(
                    cms.readFile(VFS_PATH_FORMHTML).getContents(),
                    cms.getRequestContext().getEncoding());
                CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().putCachedObject(cms, VFS_PATH_FORMHTML, formContent);
            } catch (Exception e) {
                // form html file not found, log error
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_CALENDAR_WIDGETHTML_MISSING_1,
                        VFS_PATH_FORMHTML));
                }
            }
        } else {
            formContent = (String)cachedContent;
        }

        // create macro resolver with macros for form field value replacement
        CmsMacroResolver resolver = getMacroResolverForForm(cms, widgetDialog, param);
        // resolve the macros
        formContent = resolver.resolveMacros(formContent);
        result.append(formContent);
        result.append("</td>");
        return result.toString();
    }

    /**
     * @see org.opencms.widgets.CmsCalendarWidget#getInitCall()
     */
    @Override
    public String getInitCall() {

        return "initSerialDateWidget";
    }

    /**
     * @see org.opencms.widgets.CmsCalendarWidget#getJavaScriptResourceLinks(org.opencms.file.CmsObject)
     */
    @Override
    public List<String> getJavaScriptResourceLinks(CmsObject cms) {

        String link = "/system/modules/com.alkacon.opencms.v8.calendar/resources/com.alkacon.opencms.v8.calendar.SerialDateWidget/com.alkacon.opencms.v8.calendar.SerialDateWidget.nocache.js";
        return Collections.singletonList(OpenCms.getLinkManager().substituteLink(cms, link));
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#getWidgetName()
     */
    @Override
    public String getWidgetName() {

        return CmsSerialDateWidget.class.getName();
    }

    /**
     * @see org.opencms.widgets.I_CmsADEWidget#isInternal()
     */
    @Override
    public boolean isInternal() {

        return false;
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    @Override
    public I_CmsWidget newInstance() {

        return new CmsSerialDateWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#setEditorValue(org.opencms.file.CmsObject, java.util.Map, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    public void setEditorValue(
        CmsObject cms,
        Map<String, String[]> formParameters,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        String[] values = formParameters.get(PARAM_SERIALTYPE);
        if ((values != null) && (values.length > 0)) {
            // set serial date options from request parameter values
            Map<String, String> params = new HashMap<String, String>();
            CmsMessages messages = widgetDialog.getMessages();
            String serialType = values[0];
            params.put(I_CmsCalendarSerialDateOptions.CONFIG_TYPE, serialType);
            int type = Integer.parseInt(serialType);
            switch (type) {
                case I_CmsCalendarSerialDateOptions.TYPE_DAILY:
                    // daily series
                    params.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, getParameterValue(
                        PARAM_DAY_DAILYINTERVAL,
                        formParameters));
                    params.put(I_CmsCalendarSerialDateOptions.CONFIG_EVERY_WORKING_DAY, getParameterValue(
                        PARAM_DAY_EVERYWORKINGDAY,
                        formParameters));
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_WEEKLY:
                    // weekly series
                    params.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, getParameterValue(
                        PARAM_WEEK_WEEKLYINTERVAL,
                        formParameters));
                    String[] weekDays = formParameters.get(PARAM_WEEK_WEEKDAY);
                    // create list of week days
                    StringBuffer weekDaysList = new StringBuffer(14);
                    boolean isFirst = true;
                    for (int i = 0; i < weekDays.length; i++) {
                        // create comma separated week day string
                        if (!isFirst) {
                            weekDaysList.append(CmsCalendarSerialDateFactory.SEPARATOR_WEEKDAYS);
                        }
                        weekDaysList.append(weekDays[i]);
                        isFirst = false;
                    }
                    params.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, weekDaysList.toString());
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_MONTHLY:
                    // monthly series
                    boolean isMonthDay = Boolean.valueOf(getParameterValue(PARAM_MONTH_SERIALMONTHDAY, formParameters)).booleanValue();
                    if (!isMonthDay) {
                        // no week day selected
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, getParameterValue(
                            PARAM_MONTH_DAYOFMONTH,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, getParameterValue(
                            PARAM_MONTH_MONTHLYINTERVAL,
                            formParameters));
                    } else {
                        // special week day selected
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, getParameterValue(
                            PARAM_MONTH_NUMBEROFWEEKDAYOFMONTH,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, getParameterValue(
                            PARAM_MONTH_WEEKDAY,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_INTERVAL, getParameterValue(
                            PARAM_MONTH_MONTHLYINTERVALWEEKDAY,
                            formParameters));
                    }
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_YEARLY:
                    // yearly series
                    boolean isYearday = Boolean.valueOf(getParameterValue(PARAM_YEAR_SERIALYEARDAY, formParameters)).booleanValue();
                    if (!isYearday) {
                        // no week day selected
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, getParameterValue(
                            PARAM_YEAR_DAYOFMONTH,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_MONTH, getParameterValue(
                            PARAM_YEAR_MONTH,
                            formParameters));
                    } else {
                        // special week day selected
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_DAY_OF_MONTH, getParameterValue(
                            PARAM_YEAR_WEEKDAYOFMONTH,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_WEEKDAYS, getParameterValue(
                            PARAM_YEAR_WEEKDAY,
                            formParameters));
                        params.put(I_CmsCalendarSerialDateOptions.CONFIG_MONTH, getParameterValue(
                            PARAM_YEAR_WEEKDAYMONTH,
                            formParameters));
                    }
                    break;
                default:
                    // nothing to do here, does not happen
            }

            // put start and end date to map
            String startTimeStr = "";
            String startDateStr = "";
            String endTimeStr = "";
            long startTime = 0;
            long startDate = 0;
            long endTime = 0;
            int durationDays = 0;
            try {
                // get the start date
                startTimeStr = getParameterValue(PARAM_STARTTIME, formParameters);
                if (CmsStringUtil.isEmptyOrWhitespaceOnly(startTimeStr)) {
                    startTimeStr = "1:00";
                }
                startDateStr = getParameterValue(PARAM_STARTDATE, formParameters);
                startDate = getCalendarDate(messages, startDateStr + " " + startTimeStr, true);
                params.put(I_CmsCalendarSerialDateOptions.CONFIG_STARTDATE, String.valueOf(startDate + startTime));
            } catch (ParseException e) {
                // error parsing date values, write to log
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_CALENDAR_WIDGET_PARSE_DATE_1,
                        getParameterValue(PARAM_STARTDATE, formParameters)
                            + " "
                            + getParameterValue(PARAM_STARTTIME, formParameters)));
                }
            }
            try {
                // get the end date
                endTimeStr = getParameterValue(PARAM_ENDTIME, formParameters);
                if (CmsStringUtil.isEmptyOrWhitespaceOnly(endTimeStr)) {
                    // empty end time means the end time is equal to the start time
                    endTimeStr = startTimeStr;
                }
                long startDateWithoutTime = getCalendarDate(messages, startDateStr, false);
                durationDays = Integer.parseInt(getParameterValue(PARAM_DURATIONDAYS, formParameters));
                endTime = startDateWithoutTime + (durationDays * CmsCalendarEntryDate.MILLIS_02_PER_DAY);
                String endDate = getCalendarLocalizedTime(widgetDialog.getLocale(), messages, endTime, true, false);
                endTime = getCalendarDate(messages, endDate + " " + endTimeStr, true);
                params.put(I_CmsCalendarSerialDateOptions.CONFIG_ENDDATE, String.valueOf(endTime));
            } catch (ParseException e) {
                // error parsing date values, write to log
                if (LOG.isErrorEnabled()) {
                    LOG.error(Messages.get().getBundle().key(Messages.LOG_CALENDAR_WIDGET_PARSE_DATE_1, endTimeStr));
                }
            }

            // put serial end options to map
            String endType = getParameterValue(PARAM_ENDTYPE, formParameters);
            params.put(I_CmsCalendarSerialDateOptions.CONFIG_END_TYPE, endType);
            if (String.valueOf(I_CmsCalendarSerialDateOptions.END_TYPE_TIMES).equals(endType)) {
                params.put(I_CmsCalendarSerialDateOptions.CONFIG_OCCURENCES, getParameterValue(
                    PARAM_OCCURENCES,
                    formParameters));
            }
            String serialEndDateStr = getParameterValue(PARAM_ENDDATE, formParameters);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(serialEndDateStr)) {
                try {
                    long endDate = getCalendarDate(messages, serialEndDateStr, false);
                    params.put(I_CmsCalendarSerialDateOptions.CONFIG_SERIAL_ENDDATE, String.valueOf(endDate));
                } catch (Exception e) {
                    // error parsing date value
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(
                            Messages.LOG_CALENDAR_WIDGET_PARSE_DATE_1,
                            serialEndDateStr));
                    }
                }
            }

            // set the value of the XML content element, create a key-value String from the parameter map
            param.setStringValue(cms, CmsStringUtil.mapAsString(
                params,
                String.valueOf(CmsProperty.VALUE_LIST_DELIMITER),
                String.valueOf(CmsProperty.VALUE_MAP_DELIMITER)));
        }
    }

    /**
     * Builds the HTML for a date input field with calendar date picker.<p>
     * 
     * @param name the name of the input field
     * @param widgetDialog the dialog where the widget is used on
     * @param displayDate the date to display as start value
     * @return the HTML for a date input field with calendar date picker
     */
    protected String buildDateInput(String name, I_CmsWidgetDialog widgetDialog, Calendar displayDate) {

        StringBuffer result = new StringBuffer(2048);
        result.append("<input class=\"xmlInputSmall");
        result.append("\" style=\"width: 100px;\" value=\"");
        String dateTimeValue = getCalendarLocalizedTime(
            widgetDialog.getLocale(),
            widgetDialog.getMessages(),
            displayDate.getTimeInMillis(),
            true,
            false);
        result.append(dateTimeValue);
        result.append("\" name=\"");
        result.append(name);
        result.append("\" id=\"");
        result.append(name);
        result.append("\"></td>");
        result.append(widgetDialog.dialogHorizontalSpacer(10));
        result.append("<td>");
        result.append("<table class=\"editorbuttonbackground\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"");
        result.append(name);
        result.append(".calendar\"><tr>");
        result.append(widgetDialog.button(
            "#",
            null,
            "calendar",
            org.opencms.workplace.Messages.GUI_CALENDAR_CHOOSE_DATE_0,
            widgetDialog.getButtonStyle()));
        result.append("</tr></table>");
        result.append("</td></tr></table>");

        result.append(calendarInit(
            widgetDialog.getMessages(),
            name,
            name + ".calendar",
            "cR",
            false,
            false,
            true,
            null,
            false));
        return result.toString();
    }

    /**
     * Returns the HTML for a select box for choosing the duration in days.<p>
     * 
     * @param name the name of the select box
     * @param messages localized messages for localizing the options
     * @param duration the duration in days
     * @return the HTML for a select box for choosing the duration in days
     */
    protected String buildSelectDurationDays(String name, CmsMessages messages, int duration) {

        List<String> options = new ArrayList<String>(9);
        List<String> values = new ArrayList<String>(9);
        int selectedIndex = 0;
        for (int i = 0; i < 7; i++) {
            // iterate the week days and check which one is selected
            values.add(String.valueOf(i));
            if (i != 1) {
                options.add(i + " " + messages.key(Messages.GUI_SERIALDATE_TIME_DURATION_DAYS_0));
            } else {
                options.add(i + " " + messages.key(Messages.GUI_SERIALDATE_TIME_DURATION_DAY_0));
            }
            if (duration == i) {
                selectedIndex = i;
            }
        }
        // add the week durations to the value and option lists
        values.add(String.valueOf(7));
        values.add(String.valueOf(14));
        options.add("1 " + messages.key(Messages.GUI_SERIALDATE_TIME_DURATION_WEEK_0));
        options.add("2 " + messages.key(Messages.GUI_SERIALDATE_TIME_DURATION_WEEKS_0));
        if (duration == 7) {
            selectedIndex = 7;
        } else if (duration == 14) {
            selectedIndex = 8;
        }

        return CmsWorkplace.buildSelect("name=\"" + name + "\"", options, values, selectedIndex, true);
    }

    /**
     * Returns the HTML for a select box for choosing the month.<p>
     * 
     * @param name the name of the select box
     * @param parameters optional additional parameters
     * @param messages localized messages for localizing the options
     * @param selectedIndex the selected index of the month
     * @return the HTML for a select box for choosing the month
     */
    protected String buildSelectMonth(String name, String parameters, CmsMessages messages, int selectedIndex) {

        SimpleDateFormat df = new SimpleDateFormat("MMMM", messages.getLocale());
        Calendar cal = new GregorianCalendar(messages.getLocale());
        cal.set(2000, Calendar.JANUARY, 1);
        List<String> options = new ArrayList<String>(12);
        List<String> values = new ArrayList<String>(12);
        for (int i = 0; i <= Calendar.DECEMBER; i++) {
            // iterate the months
            values.add(String.valueOf(cal.get(Calendar.MONTH)));
            options.add(df.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
        }
        // add the name to the parameters
        if (CmsStringUtil.isNotEmpty(parameters)) {
            parameters += " ";
        }
        parameters += "name=\"" + name + "\"";
        return CmsWorkplace.buildSelect(parameters, options, values, selectedIndex, true);
    }

    /**
     * Returns the HTML for a select box for choosing the week day number of the month.<p>
     * 
     * @param name the name of the select box
     * @param parameters optional additional parameters
     * @param messages localized messages for localizing the options
     * @param selectedIndex the index of the selected option
     * @return the HTML for a select box for choosing the week day number of the month
     */
    protected String buildSelectNumberOfWeekDayOfMonth(
        String name,
        String parameters,
        CmsMessages messages,
        int selectedIndex) {

        List<String> options = new ArrayList<String>(VALUES_NUMBEROFWEEKDAYOFMONTH_LIST.size());
        Iterator<String> i = VALUES_NUMBEROFWEEKDAYOFMONTH_LIST.iterator();
        while (i.hasNext()) {
            String option = i.next();
            options.add(messages.key("GUI_SERIALDATE_WEEKDAYNUMBER_" + option + "_0"));
        }
        // add the name to the parameters
        if (CmsStringUtil.isNotEmpty(parameters)) {
            parameters += " ";
        }
        parameters += "name=\"" + name + "\"";
        return CmsWorkplace.buildSelect(parameters, options, VALUES_NUMBEROFWEEKDAYOFMONTH_LIST, selectedIndex, true);
    }

    /**
     * Returns the HTML for a select box for choosing the week day.<p>
     * 
     * @param name the name of the select box
     * @param parameters optional additional parameters
     * @param messages localized messages for localizing the options
     * @param selectedWeekday the selected week day number
     * @return the HTML for a select box for choosing the week day
     */
    protected String buildSelectWeekDay(String name, String parameters, CmsMessages messages, int selectedWeekday) {

        SimpleDateFormat df = new SimpleDateFormat("EEEE", messages.getLocale());
        Calendar cal = new GregorianCalendar(messages.getLocale());
        List<String> options = new ArrayList<String>(7);
        List<String> values = new ArrayList<String>(7);
        int selectedIndex = 0;
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            // iterate the week days and check which one is selected
            values.add(String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
            options.add(df.format(cal.getTime()));
            if (selectedWeekday == cal.get(Calendar.DAY_OF_WEEK)) {
                selectedIndex = i;
            }
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        // add the name to the parameters
        if (CmsStringUtil.isNotEmpty(parameters)) {
            parameters += " ";
        }
        parameters += "name=\"" + name + "\"";
        return CmsWorkplace.buildSelect(parameters, options, values, selectedIndex, true);
    }

    /**
     * Returns the macro resolver with initialized macros to generate the serial date input form.<p>
     * 
     * @param cms an initialized instance of a CmsObject
     * @param widgetDialog the dialog where the widget is used on
     * @param param the widget parameter to generate the widget for
     * @return the macro resolver with initialized macros
     */
    protected CmsMacroResolver getMacroResolverForForm(
        CmsObject cms,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        CmsMacroResolver resolver = CmsMacroResolver.newInstance();
        // set cms object and localized messages in resolver
        resolver.setCmsObject(cms);
        CmsMessages messages = new CmsMessages(
            CmsSerialDateWidget.class.getPackage().getName() + ".messages",
            widgetDialog.getLocale());
        resolver.setMessages(messages);
        // delete empty macros which were not replaced
        resolver.setKeepEmptyMacros(false);

        // create the serial entry date object
        String paramValue = param.getStringValue(cms);
        Map<String, String> params = CmsStringUtil.splitAsMap(
            paramValue,
            String.valueOf(CmsProperty.VALUE_LIST_DELIMITER),
            String.valueOf(CmsProperty.VALUE_MAP_DELIMITER));
        // create the entry date from the field values
        CmsCalendarEntryDateSerial entryDate = CmsCalendarSerialDateFactory.getSerialDate(
            params,
            widgetDialog.getLocale());
        if (entryDate == null) {
            // no entry date created yet, build an empty default date
            Calendar start = new GregorianCalendar(widgetDialog.getLocale());
            Calendar end = (Calendar)start.clone();
            entryDate = new CmsCalendarEntryDateSerial(start, end);
            entryDate.setSerialEndType(I_CmsCalendarSerialDateOptions.END_TYPE_NEVER);
        }

        // vars for daily options
        String dayDailyInterval = "1";
        boolean dayEveryWorkingDay = false;
        // vars for weekly options
        String weekWeeklyInterval = "1";
        // vars for monthly options
        int monthSelectedIndexWeekDayOfMonth = 0;
        int monthSelectedWeekDay = -1;
        String monthDayOfMonth = "1";
        String monthMonthlyInterval = "1";
        String monthMonthlyIntervalWeekDay = "1";
        boolean monthUseWeekday = false;
        // vars for yearly options
        String yearDayOfMonth = "1";
        boolean yearUseWeekday = false;
        int yearSelectedIndexMonth = 0;
        int yearSelectedIndexWeekDayOfMonth = 0;
        int yearSelectedWeekDayDay = -1;
        int yearSelectedIndexWeekDayMonth = 0;

        if (entryDate.getSerialOptions() != null) {
            // fill the variables depending on the selected serial date type
            String serialTypeMacroPrefix = MACRO_PREFIX_PARAMVALUE + PARAM_SERIALTYPE + ".";
            // set serial type radio selection
            resolver.addMacro(serialTypeMacroPrefix + entryDate.getSerialOptions().getSerialType(), ATTR_CHECKED);
            switch (entryDate.getSerialOptions().getSerialType()) {
            // set values for the selected serial type
                case I_CmsCalendarSerialDateOptions.TYPE_DAILY:
                    CmsCalendarSerialDateDailyOptions dailyOptions = (CmsCalendarSerialDateDailyOptions)entryDate.getSerialOptions();
                    dayEveryWorkingDay = dailyOptions.isEveryWorkingDay();
                    dayDailyInterval = String.valueOf(dailyOptions.getDailyInterval());
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_WEEKLY:
                    CmsCalendarSerialDateWeeklyOptions weeklyOptions = (CmsCalendarSerialDateWeeklyOptions)entryDate.getSerialOptions();
                    weekWeeklyInterval = String.valueOf(weeklyOptions.getWeeklyInterval());
                    // check the chosen week day checkboxes
                    serialTypeMacroPrefix = MACRO_PREFIX_PARAMVALUE + PARAM_WEEK_WEEKDAY + ".";
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.MONDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.MONDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.TUESDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.TUESDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.WEDNESDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.WEDNESDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.THURSDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.THURSDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.FRIDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.FRIDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.SATURDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.SATURDAY, ATTR_CHECKED);
                    }
                    if (weeklyOptions.getWeekDays().contains(new Integer(Calendar.SUNDAY))) {
                        resolver.addMacro(serialTypeMacroPrefix + Calendar.SUNDAY, ATTR_CHECKED);
                    }
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_MONTHLY:
                    CmsCalendarSerialDateMonthlyOptions monthlyOptions = (CmsCalendarSerialDateMonthlyOptions)entryDate.getSerialOptions();
                    monthUseWeekday = monthlyOptions.isUseWeekDay();
                    if (!monthlyOptions.isUseWeekDay()) {
                        monthDayOfMonth = String.valueOf(monthlyOptions.getDayOfMonth());
                        monthMonthlyInterval = String.valueOf(monthlyOptions.getMonthlyInterval());
                    } else {
                        // set selected index of select boxes
                        monthSelectedIndexWeekDayOfMonth = monthlyOptions.getDayOfMonth() - 1;
                        monthSelectedWeekDay = monthlyOptions.getWeekDay();
                        monthMonthlyIntervalWeekDay = String.valueOf(monthlyOptions.getMonthlyInterval());
                    }
                    break;
                case I_CmsCalendarSerialDateOptions.TYPE_YEARLY:
                    CmsCalendarSerialDateYearlyOptions yearlyOptions = (CmsCalendarSerialDateYearlyOptions)entryDate.getSerialOptions();
                    yearUseWeekday = yearlyOptions.isUseWeekDay();
                    if (!yearlyOptions.isUseWeekDay()) {
                        yearDayOfMonth = String.valueOf(yearlyOptions.getDayOfMonth());
                        yearSelectedIndexMonth = yearlyOptions.getMonth();
                    } else {
                        yearSelectedIndexWeekDayOfMonth = yearlyOptions.getDayOfMonth() - 1;
                        yearSelectedWeekDayDay = yearlyOptions.getWeekDay();
                        yearSelectedIndexWeekDayMonth = yearlyOptions.getMonth();
                    }
                    break;
                default:
                    // nothing do do here, should never happen
            }
        } else {
            // no serial entry created yet, add some defaults
            resolver.addMacro(MACRO_PREFIX_PARAMVALUE
                + PARAM_SERIALTYPE
                + "."
                + I_CmsCalendarSerialDateOptions.TYPE_DAILY, ATTR_CHECKED);
            resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_WEEK_WEEKDAY + "." + Calendar.MONDAY, ATTR_CHECKED);

        }
        // set time settings
        String startTime = getCalendarLocalizedTime(
            widgetDialog.getLocale(),
            widgetDialog.getMessages(),
            entryDate.getStartDate().getTimeInMillis(),
            false,
            true);
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_STARTTIME, startTime);

        String endTime = "";
        if (!entryDate.getStartDate().equals(entryDate.getEndDate())) {
            // end time is different from start time, get localized value
            endTime = getCalendarLocalizedTime(
                widgetDialog.getLocale(),
                widgetDialog.getMessages(),
                entryDate.getEndDate().getTimeInMillis(),
                false,
                true);
        }
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_ENDTIME, endTime);

        resolver.addMacro("select.durationdays", buildSelectDurationDays(
            PARAM_DURATIONDAYS,
            messages,
            entryDate.getDuration()));

        // set found values to serial option tabs
        // daily options
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_DAY_DAILYINTERVAL, dayDailyInterval);
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_DAY_EVERYWORKINGDAY + "." + dayEveryWorkingDay, ATTR_CHECKED);

        // weekly options
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_WEEK_WEEKLYINTERVAL, weekWeeklyInterval);

        // monthly options
        // mark the correct radio
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_MONTH_SERIALMONTHDAY + "." + monthUseWeekday, ATTR_CHECKED);
        // set the macros for the day of month options
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_MONTH_DAYOFMONTH, monthDayOfMonth);
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_MONTH_MONTHLYINTERVAL, monthMonthlyInterval);
        // set the macros for the week day of month options
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_MONTH_MONTHLYINTERVALWEEKDAY, monthMonthlyIntervalWeekDay);
        // build the select boxes
        resolver.addMacro("select.monthnumberofweekday", buildSelectNumberOfWeekDayOfMonth(
            PARAM_MONTH_NUMBEROFWEEKDAYOFMONTH,
            "onfocus=\"document.getElementById('" + PARAM_MONTH_SERIALMONTHDAY + ".true').checked = true;\"",
            messages,
            monthSelectedIndexWeekDayOfMonth));
        resolver.addMacro("select.monthweekday", buildSelectWeekDay(
            PARAM_MONTH_WEEKDAY,
            "onfocus=\"document.getElementById('" + PARAM_MONTH_SERIALMONTHDAY + ".true').checked = true;\"",
            messages,
            monthSelectedWeekDay));

        // yearly options
        // mark the correct radio
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_YEAR_SERIALYEARDAY + "." + yearUseWeekday, ATTR_CHECKED);
        // set the macros for the day of month options
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_YEAR_DAYOFMONTH, yearDayOfMonth);
        resolver.addMacro("select.yearmonth", buildSelectMonth(PARAM_YEAR_MONTH, "onfocus=\"document.getElementById('"
            + PARAM_YEAR_SERIALYEARDAY
            + ".false').checked = true;\"", messages, yearSelectedIndexMonth));
        // set the macros for the week day of month options
        resolver.addMacro("select.yearnumberofweekday", buildSelectNumberOfWeekDayOfMonth(
            PARAM_YEAR_WEEKDAYOFMONTH,
            "onfocus=\"document.getElementById('" + PARAM_YEAR_SERIALYEARDAY + ".true').checked = true;\"",
            messages,
            yearSelectedIndexWeekDayOfMonth));
        resolver.addMacro("select.yearweekday", buildSelectWeekDay(
            PARAM_YEAR_WEEKDAY,
            "onfocus=\"document.getElementById('" + PARAM_YEAR_SERIALYEARDAY + ".true').checked = true;\"",
            messages,
            yearSelectedWeekDayDay));
        resolver.addMacro("select.yearmonthweekday", buildSelectMonth(
            PARAM_YEAR_WEEKDAYMONTH,
            "onfocus=\"document.getElementById('" + PARAM_YEAR_SERIALYEARDAY + ".true').checked = true;\"",
            messages,
            yearSelectedIndexWeekDayMonth));

        // set serial duration values

        // set start date
        resolver.addMacro("calendar.startdate", buildDateInput("startdate", widgetDialog, entryDate.getStartDate()));
        Calendar serialEndDate = entryDate.getSerialEndDate();
        if (serialEndDate == null) {
            serialEndDate = entryDate.getStartDate();
        }
        resolver.addMacro("calendar.serialenddate", buildDateInput("serialenddate", widgetDialog, serialEndDate));

        // set occurences
        int occurences = 10;
        if (entryDate.getSerialEndType() == I_CmsCalendarSerialDateOptions.END_TYPE_TIMES) {
            occurences = entryDate.getOccurences();
        }
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_OCCURENCES, String.valueOf(occurences));

        // set the end type radio buttons
        resolver.addMacro(MACRO_PREFIX_PARAMVALUE + PARAM_ENDTYPE + "." + entryDate.getSerialEndType(), ATTR_CHECKED);

        return resolver;
    }

    /**
     * Returns the value of the first parameter from the form parameter map.<p>
     * 
     * @param parameterName the name of the parameter to get the value for
     * @param formParameters the map containing the form parameters
     * @return the value of the first parameter from the form parameter map
     */
    protected String getParameterValue(String parameterName, Map<String, String[]> formParameters) {

        String[] values = formParameters.get(parameterName);
        if (CmsStringUtil.isNotEmpty(values[0])) {
            return values[0];
        }
        return "";
    }
}