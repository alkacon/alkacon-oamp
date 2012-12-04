/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.calendar.client.input;

import com.alkacon.opencms.v8.calendar.client.input.serialdate.CmsPatternPanelDaily;
import com.alkacon.opencms.v8.calendar.client.input.serialdate.CmsPatternPanelMonthly;
import com.alkacon.opencms.v8.calendar.client.input.serialdate.CmsPatternPanelWeekly;
import com.alkacon.opencms.v8.calendar.client.input.serialdate.CmsPatternPanelYearly;
import com.alkacon.opencms.v8.calendar.client.widget.css.I_CmsLayoutBundle;

import org.opencms.gwt.client.I_CmsHasInit;
import org.opencms.gwt.client.Messages;
import org.opencms.gwt.client.ui.I_CmsAutoHider;
import org.opencms.gwt.client.ui.input.CmsErrorWidget;
import org.opencms.gwt.client.ui.input.CmsRadioButton;
import org.opencms.gwt.client.ui.input.CmsRadioButtonGroup;
import org.opencms.gwt.client.ui.input.CmsSelectBox;
import org.opencms.gwt.client.ui.input.I_CmsFormWidget;
import org.opencms.gwt.client.ui.input.datebox.CmsDateBox;
import org.opencms.gwt.client.ui.input.form.CmsWidgetFactoryRegistry;
import org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory;
import org.opencms.gwt.client.util.CmsDebugLog;
import org.opencms.util.CmsStringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Basic serial date widget.<p>
 * 
 * @since 8.5.0
 * 
 */
public class CmsSerialDate extends Composite implements I_CmsFormWidget, I_CmsHasInit, HasValueChangeHandlers<String> {

    /** Configuration key name for the serial date day of month. */
    public static final String CONFIG_DAY_OF_MONTH = "dayofmonth";

    /** Configuration key name for the serial date end type. */
    public static final String CONFIG_END_TYPE = "endtype";

    /** Configuration key name for the serial date end date and time (sets duration together with start date). */
    public static final String CONFIG_ENDDATE = "enddate";

    /** Configuration key name for the serial date daily configuration: every working day flag. */
    public static final String CONFIG_EVERY_WORKING_DAY = "everyworkingday";

    /** Configuration key name for the serial date interval. */
    public static final String CONFIG_INTERVAL = "interval";

    /** Configuration key name for the serial date month. */
    public static final String CONFIG_MONTH = "month";

    /** Configuration key name for the serial date number of occurences. */
    public static final String CONFIG_OCCURENCES = "occurences";

    /** Configuration key name for the serial date: series end date. */
    public static final String CONFIG_SERIAL_ENDDATE = "serialenddate";

    /** Configuration key name for the serial date start date and time. */
    public static final String CONFIG_STARTDATE = "startdate";

    /** Configuration key name for the serial date type. */
    public static final String CONFIG_TYPE = "type";

    /** Configuration key name for the serial date week day(s). */
    public static final String CONFIG_WEEKDAYS = "weekdays";

    /** Series end type: ends at specific date. */
    public static final int END_TYPE_DATE = 3;

    /** Series end type: ends never. */
    public static final int END_TYPE_NEVER = 1;

    /** Series end type: ends after n times. */
    public static final int END_TYPE_TIMES = 2;

    /** Serial type: daily series. */
    public static final int TYPE_DAILY = 1;

    /** Serial type: monthly series. */
    public static final int TYPE_MONTHLY = 3;

    /** Serial type: weekly series. */
    public static final int TYPE_WEEKLY = 2;

    /** Serial type: yearly series. */
    public static final int TYPE_YEARLY = 4;

    /** The key for daily. */
    public static final String KEY_DAILY = "1";

    /** The key for monthly. */
    public static final String KEY_MONTHLY = "3";

    /** The key for weekly. */
    public static final String KEY_WEEKLY = "2";

    /** The key for yearly. */
    public static final String KEY_YEARLY = "4";

    /** The widget type identifier for this widget. */
    private static final String WIDGET_TYPE = "SerialDate";

    /** Separator for the week days String. */
    public static final char SEPARATOR_WEEKDAYS = ',';

    /** Number of milliseconds per minute. */
    public static final long MILLIS_00_PER_MINUTE = 1000 * 60;

    /** Number of milliseconds per hour. */
    public static final long MILLIS_01_PER_HOUR = MILLIS_00_PER_MINUTE * 60;

    /** Number of milliseconds per day. */
    public static final long MILLIS_02_PER_DAY = MILLIS_01_PER_HOUR * 24;

    /** Number of milliseconds per week. */
    public static final long MILLIS_03_PER_WEEK = MILLIS_02_PER_DAY * 7;

    /** The daily pattern. */
    CmsPatternPanelDaily m_dailyPattern;

    /** The weekly pattern. */
    CmsPatternPanelWeekly m_weeklyPattern;

    /** The monthly pattern. */
    CmsPatternPanelMonthly m_monthlyPattern;

    /** The yearly pattern. */
    CmsPatternPanelYearly m_yearlyPattern;

    /** The begin datebox. */
    CmsDateBox m_dateboxbegin = new CmsDateBox();

    /** The end datebox. */
    CmsDateBox m_dateboxend = new CmsDateBox();

    /** The end date box. */
    TextBox m_endDate = new TextBox();

    /** The start date box. */
    TextBox m_startDate = new TextBox();

    /** The times text box. */
    TextBox m_times = new TextBox();

    /** The end date. */
    Date m_endDateValue = new Date();

    /** The start date. */
    Date m_startDateValue = new Date();

    /** Array of all radio button. */
    private CmsRadioButton[] m_arrayRadiobox;

    /** The error display for this widget. */
    private CmsErrorWidget m_error = new CmsErrorWidget();

    /** Value of the radio group duration. */
    private CmsRadioButtonGroup m_groupDuration = new CmsRadioButtonGroup();

    /** Value of the radio group pattern. */
    private CmsRadioButtonGroup m_groupPattern = new CmsRadioButtonGroup();

    /** The lower panel for detail duration information. */
    private Panel m_lowPanel = new FlowPanel();

    /** All radiobottons of the low panel. */
    private CmsRadioButton[] m_lowRadioButton = new CmsRadioButton[3];
    /** The mein panel for the table. */
    private Panel m_panel = new FlowPanel();
    /** The actual active pattern panel. */
    private Panel m_patterPanel;
    /** The duratioen selection. */
    CmsSelectBox m_duration;

    /** The serial panel. */
    private Panel m_serialPanel = new FlowPanel();
    /** The root panel containing the other components of this widget. */
    private FlexTable m_table = new FlexTable();
    /** The top Panel for detail time information. */
    private Panel m_topPanel = new FlowPanel();

    /** The used time format. */
    private DateTimeFormat m_timeFormat;

    /** JSON of all labels. */
    private JSONObject m_labels;

    /**
     * Category field widgets for ADE forms.<p>
     * @param labels a JSON of all needed labels
     */
    public CmsSerialDate(JSONObject labels) {

        super();

        m_labels = labels;

        m_dailyPattern = new CmsPatternPanelDaily(m_labels);
        m_weeklyPattern = new CmsPatternPanelWeekly(m_labels);
        m_monthlyPattern = new CmsPatternPanelMonthly(m_labels);
        m_yearlyPattern = new CmsPatternPanelYearly(m_labels);
        m_patterPanel = m_dailyPattern;
        try {
            m_timeFormat = DateTimeFormat.getFormat(Messages.get().key(Messages.GUI_DATEBOX_TIME_PATTERN_0));
        } catch (Exception e) {
            // in case the pattern is not available, fall back to standard en pattern
            m_timeFormat = DateTimeFormat.getFormat("hh:mm aa");
        }
        m_endDate.setValue(m_timeFormat.format(new Date()));
        m_startDate.setValue(m_timeFormat.format(new Date()));
        m_dateboxbegin.setValue(new Date());

        setSelectVaues();
        setTopPanel();
        setLowPanel();

        m_dateboxbegin.getTextField().getTextBoxContainer().addStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().calendarStyle());
        m_dateboxend.getTextField().getTextBoxContainer().addStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().calendarStyle());

        m_table.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDataTabel());
        m_table.insertRow(0);

        // Table for top panel
        FlexTable topPanel = new FlexTable();
        topPanel.insertRow(0);
        // add the Time view.
        topPanel.setWidget(0, 0, m_topPanel);
        topPanel.getCellFormatter().getElement(0, 0).getStyle().setWidth(200, Unit.PX);
        // add the date view.
        topPanel.setWidget(0, 1, m_lowPanel);
        m_table.setWidget(0, 0, topPanel);
        m_table.getFlexCellFormatter().setColSpan(0, 0, 3);
        m_table.getCellFormatter().addStyleName(
            0,
            0,
            I_CmsLayoutBundle.INSTANCE.widgetCss().serialDataTabelBorderBottom());

        // the selection view
        m_table.insertRow(1);
        m_table.setWidget(1, 0, m_serialPanel);
        m_table.getCellFormatter().getElement(1, 0).getStyle().setWidth(1, Unit.PX);
        m_table.setWidget(1, 1, new SimplePanel());
        m_table.getCellFormatter().addStyleName(
            1,
            1,
            I_CmsLayoutBundle.INSTANCE.widgetCss().serialDataTabelBorderRight());
        m_table.setWidget(1, 2, m_patterPanel);

        for (int i = 0; i < m_arrayRadiobox.length; i++) {
            m_arrayRadiobox[i].addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
            m_arrayRadiobox[i].addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {

                    changePattern();
                }
            });

            m_serialPanel.add(m_arrayRadiobox[i]);
        }
        initWidget(m_panel);
        m_panel.add(m_table);
        m_panel.add(m_error);

    }

    /**
     * Initializes this class.<p>
     */
    public static void initClass() {

        // registers a factory for creating new instances of this widget
        CmsWidgetFactoryRegistry.instance().registerFactory(WIDGET_TYPE, new I_CmsFormWidgetFactory() {

            /**
             * @see org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory#createWidget(java.util.Map)
             */
            public I_CmsFormWidget createWidget(Map<String, String> widgetParams) {

                return new CmsSerialDate(null);
            }
        });
    }

    /**
     * Returns the int value of the given String or the default value if parsing the String fails.<p>
     * 
     * @param strValue the String to parse
     * @param defaultValue the default value to use if parsing fails
     * @return the int value of the given String
     */
    protected static int getIntValue(String strValue, int defaultValue) {

        int result = defaultValue;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
            try {
                result = Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                // no number, use default value
            }
        }
        return result;
    }

    /**
     * Returns the long value of the given String or the default value if parsing the String fails.<p>
     * 
     * @param strValue the String to parse
     * @param defaultValue the default value to use if parsing fails
     * @return the long value of the given String
     */
    protected static long getLongValue(String strValue, long defaultValue) {

        long result = defaultValue;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
            try {
                result = Long.parseLong(strValue);
            } catch (NumberFormatException e) {
                // no number, use default value
            }
        }
        return result;
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Cleared all fields for the inactive view.<p>
     */
    public void clearFealds() {

        m_dateboxbegin.setFormValueAsString("");
        m_dateboxend.setFormValueAsString("");
        m_startDate.setValue("");
        m_endDate.setValue("");
    }

    /**
     * Represents a value change event.<p>
     */
    public void fireValueChange() {

        CmsDebugLog.getInstance().printLine(getFormValueAsString());
        ValueChangeEvent.fire(this, getFormValueAsString());
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getApparentValue()
     */
    public String getApparentValue() {

        return null;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFieldType()
     */
    public FieldType getFieldType() {

        return null;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValue()
     */
    public Object getFormValue() {

        return getFormValueAsString();
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValueAsString()
     */
    public String getFormValueAsString() {

        return selectValues();
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#isEnabled()
     */
    public boolean isEnabled() {

        return false;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#reset()
     */
    public void reset() {

        //nothing to do

    }

    /** Selects the right ending element. 
     * @param element the element that should be checked
     * 
     * */
    public void selectEnding(int element) {

        m_groupDuration.selectButton(m_lowRadioButton[element]);
    }

    /**
     * Sets the radio buttons active or inactive.<p>
     * @param active true or false to activate or deactivate
     * */
    public void setActive(boolean active) {

        // Iterate about all radio button.
        for (int i = 0; i < m_arrayRadiobox.length; i++) {
            // set the radio button active / inactive.
            m_arrayRadiobox[i].setEnabled(active);
            // if this widget is set inactive.
            if (!active) {
                // deselect all radio button.
                m_arrayRadiobox[i].setChecked(active);
            }
            if (active) {
                if (i == 0) {
                    m_arrayRadiobox[i].setChecked(active);
                }
            }
        }
        for (int i = 0; i < m_lowRadioButton.length; i++) {
            // set the radio button active / inactive.
            m_lowRadioButton[i].setEnabled(active);
            // if this widget is set inactive.
            if (!active) {
                // deselect all radio button.
                m_lowRadioButton[i].setChecked(active);
            }
            if (active) {
                if (i == 0) {
                    m_lowRadioButton[i].setChecked(active);
                }
            }
        }

        for (int i = 0; i < m_dailyPattern.getSelection().length; i++) {
            // set the radio button active / inactive.
            m_dailyPattern.getSelection()[i].setEnabled(active);
            // if this widget is set inactive.
            if (!active) {
                // deselect all radio button.
                m_dailyPattern.getSelection()[i].setChecked(active);
            }
            if (active) {
                if (i == 0) {
                    m_dailyPattern.getSelection()[i].setChecked(active);
                }
            }
        }

    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setAutoHideParent(org.opencms.gwt.client.ui.I_CmsAutoHider)
     */
    public void setAutoHideParent(I_CmsAutoHider autoHideParent) {

        // nothing to do
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {

        // nothing to do
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setErrorMessage(java.lang.String)
     */
    public void setErrorMessage(String errorMessage) {

        //m_error.setText(errorMessage);

    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setFormValueAsString(java.lang.String)
     */
    public void setFormValueAsString(String value) {

        if (!value.isEmpty()) {
            Map<String, String> values = new HashMap<String, String>();
            String[] split = value.split("\\|");
            for (int i = 0; i < split.length; i++) {
                int pars = split[i].indexOf("=");
                String key = split[i].substring(0, pars);
                String val = split[i].substring(pars + 1);
                values.put(key, val);
            }
            setValues(values);
        }

    }

    /**
     * Creates a serial date entry from the given property value.<p>
     * 
     * If no matching serial date could be created, <code>null</code> is returned.<p>
     * 
     * @param values the Map containing the date configuration values
     */
    public void setValues(Map<String, String> values) {

        // first set serial date fields used by all serial types

        // fetch the start date and time

        String startLong = values.get(CONFIG_STARTDATE);
        m_startDateValue = new Date(getLongValue(startLong, 0));
        m_startDate.setValue(m_timeFormat.format(m_startDateValue));

        m_dateboxbegin.setValue(m_startDateValue);
        // the end date and time (this means the duration of a single entry)

        String endLong = values.get(CONFIG_ENDDATE);
        m_endDateValue = new Date(getLongValue(endLong, 0));
        m_endDate.setValue(m_timeFormat.format(m_endDateValue));

        if (getLongValue(endLong, 0) > getLongValue(startLong, 0)) {
            // duration at least one day, calculate it
            long delta = getLongValue(endLong, 0) - getLongValue(startLong, 0);
            int test = new Long(delta / MILLIS_02_PER_DAY).intValue();
            m_duration.selectValue((test + 1) + "");
        }

        // determine the serial end type
        String endTypeStr = values.get(CONFIG_END_TYPE);
        int endType = getIntValue(endTypeStr, END_TYPE_NEVER);
        m_groupDuration.selectButton(m_lowRadioButton[endType - 1]);
        if (endType == END_TYPE_TIMES) {
            // end type: after a number of occurences
            String occurStr = values.get(CONFIG_OCCURENCES);
            m_times.setText(occurStr);
        } else if (endType == END_TYPE_DATE) {
            // end type: ends at a specified date
            String endDateStr = values.get(CONFIG_SERIAL_ENDDATE);
            long endDate = getLongValue(endDateStr, 0);
            m_dateboxend.setValue(new Date(endDate));

        }

        // now determine the serial date options depending on the serial date type

        String type = values.get(CONFIG_TYPE);
        int entryType = getIntValue(type, 1);
        m_groupPattern.selectButton(m_arrayRadiobox[entryType - 1]);
        changePattern();
        switch (entryType) {
            case TYPE_DAILY:
                // daily series entry, get interval and working days flag
                String intervalStr = values.get(CONFIG_INTERVAL);
                String workingDaysStr = values.get(CONFIG_EVERY_WORKING_DAY);
                boolean workingDays = Boolean.valueOf(workingDaysStr).booleanValue();
                m_dailyPattern.setInterval(intervalStr);
                if (workingDays) {
                    m_dailyPattern.setSelection(2);
                } else {
                    m_dailyPattern.setSelection(1);
                }

                break;
            case TYPE_WEEKLY:
                // weekly series entry
                intervalStr = values.get(CONFIG_INTERVAL);
                String weekDaysStr = values.get(CONFIG_WEEKDAYS);
                List<String> weekDaysStrList = CmsStringUtil.splitAsList(weekDaysStr, SEPARATOR_WEEKDAYS, true);
                m_weeklyPattern.setInterval(intervalStr);
                m_weeklyPattern.setWeekDays(weekDaysStrList);
                break;
            case TYPE_MONTHLY:
                // monthly series entry
                intervalStr = values.get(CONFIG_INTERVAL);
                String dayOfMonthStr = values.get(CONFIG_DAY_OF_MONTH);
                int dayOfMonth = getIntValue(dayOfMonthStr, 1);
                String weekDayStr = values.get(CONFIG_WEEKDAYS);
                int weekDay = getIntValue(weekDayStr, -1);
                m_monthlyPattern.setWeekDay(weekDay);
                m_monthlyPattern.setInterval(intervalStr);
                m_monthlyPattern.setDayOfMonth(dayOfMonth);

                break;
            case TYPE_YEARLY:
                // yearly series entry
                dayOfMonthStr = values.get(CONFIG_DAY_OF_MONTH);
                dayOfMonth = getIntValue(dayOfMonthStr, 1);
                weekDayStr = values.get(CONFIG_WEEKDAYS);
                weekDay = getIntValue(weekDayStr, -1);
                String monthStr = values.get(CONFIG_MONTH);
                int month = getIntValue(monthStr, 0);
                m_yearlyPattern.setWeekDay(weekDay);
                m_yearlyPattern.setDayOfMonth(dayOfMonth);
                m_yearlyPattern.setMonth(month);

                break;
            default:

        }
        selectValues();
    }

    /**
     * Selects the right view for the selected pattern.<p>
     */
    protected void changePattern() {

        if (m_groupPattern.getSelectedButton() != null) {
            String buttonName = m_groupPattern.getSelectedButton().getName();
            // m_patterPanel.removeFromParent();
            if (buttonName.equals(KEY_DAILY)) {
                m_patterPanel = m_dailyPattern;
                m_dailyPattern.addValueChangeHandler(new ValueChangeHandler<String>() {

                    public void onValueChange(ValueChangeEvent<String> event) {

                        fireValueChange();

                    }
                });
            }
            if (buttonName.equals(KEY_WEEKLY)) {
                m_patterPanel = m_weeklyPattern;
                m_weeklyPattern.addValueChangeHandler(new ValueChangeHandler<String>() {

                    public void onValueChange(ValueChangeEvent<String> event) {

                        fireValueChange();

                    }

                });

            }
            if (buttonName.equals(KEY_MONTHLY)) {
                m_patterPanel = m_monthlyPattern;
                m_monthlyPattern.addValueChangeHandler(new ValueChangeHandler<String>() {

                    public void onValueChange(ValueChangeEvent<String> event) {

                        fireValueChange();

                    }

                });
            }
            if (buttonName.equals(KEY_YEARLY)) {
                m_patterPanel = m_yearlyPattern;
                m_yearlyPattern.addValueChangeHandler(new ValueChangeHandler<String>() {

                    public void onValueChange(ValueChangeEvent<String> event) {

                        fireValueChange();

                    }

                });
            }
            m_table.setWidget(1, 2, m_patterPanel);
            fireValueChange();
        }

    }

    /**
     * Selects all needed information an build the result string.<p>
     * 
     * @return the result string
     * */
    @SuppressWarnings("deprecation")
    private String selectValues() {

        String result = "";
        String type = "1";
        if (m_groupPattern.getSelectedButton() != null) {
            type = m_groupPattern.getSelectedButton().getName();
        }
        result += CONFIG_TYPE + "=" + type + "|";
        switch (Integer.parseInt(type)) {
            case (1):
                result += CONFIG_INTERVAL + "=" + m_dailyPattern.getIterval() + "|";
                result += CONFIG_EVERY_WORKING_DAY + "=" + m_dailyPattern.getWorkingDay() + "|";
                break;
            case (2):
                result += CONFIG_INTERVAL + "=" + m_weeklyPattern.getInterval() + "|";
                result += CONFIG_WEEKDAYS + "=" + m_weeklyPattern.getWeekDays() + "|";

                break;
            case (3):
                result += CONFIG_INTERVAL + "=" + m_monthlyPattern.getInterval() + "|";
                result += CONFIG_DAY_OF_MONTH + "=" + m_monthlyPattern.getDayOfMonth() + "|";
                if (!m_monthlyPattern.getWeekDays().equals("-1")) {
                    result += CONFIG_WEEKDAYS + "=" + m_monthlyPattern.getWeekDays() + "|";
                }
                break;
            case (4):
                result += CONFIG_DAY_OF_MONTH + "=" + m_yearlyPattern.getDayOfMonth() + "|";
                result += CONFIG_MONTH + "=" + m_yearlyPattern.getMonth() + "|";
                if (!m_yearlyPattern.getWeekDays().equals("-1")) {
                    result += CONFIG_WEEKDAYS + "=" + m_yearlyPattern.getWeekDays() + "|";
                }
                break;
            default:
                break;

        }
        Date startDate = new Date();
        Date endDate = new Date();
        m_startDateValue = m_dateboxbegin.getValue();
        if (m_startDateValue == null) {
            m_startDateValue = new Date();
            m_startDate.setText(m_timeFormat.format(m_startDateValue));
            m_endDate.setText(m_timeFormat.format(m_startDateValue));
        }
        startDate = m_timeFormat.parse(m_startDate.getText());
        m_startDateValue.setHours(startDate.getHours());
        m_startDateValue.setMinutes(startDate.getMinutes());

        endDate = m_timeFormat.parse(m_endDate.getText());
        m_endDateValue.setHours(endDate.getHours());
        m_endDateValue.setMinutes(endDate.getMinutes());

        result += CONFIG_STARTDATE + "=" + m_startDateValue.getTime() + "|";
        result += CONFIG_ENDDATE + "=" + m_endDateValue.getTime() + "|";
        String endtype = "1";
        if (m_groupDuration.getSelectedButton() != null) {
            endtype = m_groupDuration.getSelectedButton().getName();
        }
        switch (Integer.parseInt(endtype)) {
            case (1):
                break;
            case (END_TYPE_TIMES):
                if (!m_times.getText().isEmpty()) {
                    result += CONFIG_OCCURENCES + "=" + m_times.getText() + "|";
                }
                break;
            case (END_TYPE_DATE):
                if (!m_dateboxend.getValueAsFormatedString().isEmpty()) {
                    result += CONFIG_SERIAL_ENDDATE + "=" + m_dateboxend.getFormValueAsString() + "|";
                }
                break;
            default:
                break;

        }

        result += CONFIG_END_TYPE + "=" + endtype;
        return result;
    }

    /**
     * Private function to set all the end selections.<p>
     * */
    private void setLowPanel() {

        FlexTable table = new FlexTable();
        table.insertRow(0);
        FlowPanel cell1 = new FlowPanel();
        Label startDate = new Label(m_labels.get("GUI_SERIALDATE_TIME_STARTDATE_0").isString().stringValue());
        startDate.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateLable());
        cell1.add(startDate);
        cell1.getElement().getStyle().setWidth(100, Unit.PCT);
        cell1.add(m_dateboxbegin);
        m_dateboxbegin.setDateOnly(true);
        m_dateboxbegin.getElement().getStyle().setWidth(108, Unit.PX);
        m_dateboxbegin.getElement().getStyle().setFloat(Float.RIGHT);
        m_dateboxbegin.addValueChangeHandler(new ValueChangeHandler<Date>() {

            public void onValueChange(ValueChangeEvent<Date> event) {

                fireValueChange();

            }
        });
        table.setWidget(0, 0, cell1);
        table.getCellFormatter().getElement(0, 0).getStyle().setWidth(176, Unit.PX);
        table.getCellFormatter().getElement(0, 0).getStyle().setVerticalAlign(VerticalAlign.TOP);

        FlowPanel cell2 = new FlowPanel();
        CmsRadioButton sel1 = new CmsRadioButton(
            "1",
            m_labels.get("GUI_SERIALDATE_DURATION_ENDTYPE_NEVER_0").isString().stringValue());
        m_lowRadioButton[0] = sel1;
        sel1.setGroup(m_groupDuration);
        sel1.setChecked(true);
        sel1.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDatelowPanelSelection());
        sel1.addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
        sel1.getElement().getStyle().setMarginLeft(13, Unit.PX);
        sel1.getElement().getStyle().setMarginTop(0, Unit.PX);
        sel1.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                fireValueChange();

            }
        });

        CmsRadioButton sel2 = new CmsRadioButton(
            "2",
            m_labels.get("GUI_SERIALDATE_DURATION_ENDTYPE_OCC_0").isString().stringValue());
        m_lowRadioButton[1] = sel2;
        sel2.setGroup(m_groupDuration);
        sel2.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDatelowPanelSelection());
        sel2.addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
        sel2.getElement().setAttribute("style", "clear:left");
        sel2.getElement().getStyle().setMarginLeft(13, Unit.PX);
        sel2.getElement().getStyle().setMarginTop(6, Unit.PX);
        sel2.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                fireValueChange();

            }
        });

        CmsRadioButton sel3 = new CmsRadioButton(
            "3",
            m_labels.get("GUI_SERIALDATE_DURATION_ENDTYPE_DATE_0").isString().stringValue());
        m_lowRadioButton[2] = sel3;
        sel3.setGroup(m_groupDuration);
        sel3.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDatelowPanelSelection());
        sel3.addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
        sel3.getElement().setAttribute("style", "clear:left");
        sel3.getElement().getStyle().setMarginLeft(13, Unit.PX);
        sel3.getElement().getStyle().setMarginTop(6, Unit.PX);
        sel3.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                fireValueChange();

            }
        });

        cell2.add(sel1);
        cell2.add(sel2);

        m_times.setStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().textBoxSerialDate());
        m_times.getElement().getStyle().setMarginTop(5, Unit.PX);
        m_times.getElement().getStyle().setWidth(82, Unit.PX);
        cell2.add(m_times);
        m_times.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                fireValueChange();

            }
        });
        m_times.addFocusHandler(new FocusHandler() {

            public void onFocus(FocusEvent event) {

                selectEnding(1);

            }
        });
        Label times = new Label(m_labels.get("GUI_SERIALDATE_DURATION_ENDTYPE_OCC_TIMES_0").isString().stringValue());
        times.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateLable());
        times.getElement().getStyle().setMarginTop(7, Unit.PX);
        times.getElement().getStyle().setMarginLeft(2, Unit.PX);
        cell2.add(times);
        cell2.add(sel3);

        cell2.add(m_dateboxend);
        m_dateboxend.setDateOnly(true);
        m_dateboxend.getElement().getStyle().setWidth(90, Unit.PX);
        m_dateboxend.getElement().getStyle().setMarginTop(5, Unit.PX);
        m_dateboxend.getElement().getStyle().setMarginLeft(22, Unit.PX);
        m_dateboxend.addValueChangeHandler(new ValueChangeHandler<Date>() {

            public void onValueChange(ValueChangeEvent<Date> event) {

                fireValueChange();

            }
        });
        m_dateboxend.addDomHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                selectEnding(2);

            }
        }, ClickEvent.getType());
        table.setWidget(0, 1, cell2);
        m_lowPanel.add(table);
        m_lowPanel.setStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDatelowPanel());
    }

    /**
     * Private function to set all possible selections.<p>
     * */
    private void setSelectVaues() {

        m_arrayRadiobox = new CmsRadioButton[4];
        m_arrayRadiobox[0] = new CmsRadioButton(
            KEY_DAILY,
            m_labels.get("GUI_SERIALDATE_TYPE_DAILY_0").isString().stringValue());
        m_arrayRadiobox[0].setGroup(m_groupPattern);
        m_arrayRadiobox[0].setChecked(true);
        m_arrayRadiobox[1] = new CmsRadioButton(
            KEY_WEEKLY,
            m_labels.get("GUI_SERIALDATE_TYPE_WEEKLY_0").isString().stringValue());
        m_arrayRadiobox[1].setGroup(m_groupPattern);
        m_arrayRadiobox[2] = new CmsRadioButton(
            KEY_MONTHLY,
            m_labels.get("GUI_SERIALDATE_TYPE_MONTHLY_0").isString().stringValue());
        m_arrayRadiobox[2].setGroup(m_groupPattern);
        m_arrayRadiobox[3] = new CmsRadioButton(
            KEY_YEARLY,
            m_labels.get("GUI_SERIALDATE_TYPE_YEARLY_0").isString().stringValue());
        m_arrayRadiobox[3].setGroup(m_groupPattern);
    }

    /**
     * Private function to set all the time selections.<p>
     * */
    private void setTopPanel() {

        Label l_start = new Label(m_labels.get("GUI_SERIALDATE_TIME_STARTTIME_0").isString().stringValue());
        l_start.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateLable());
        l_start.getElement().getStyle().setFloat(Float.LEFT);
        m_startDate.setStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().textBoxSerialDate());
        m_startDate.getElement().getStyle().setWidth(110, Unit.PX);
        m_startDate.getElement().getStyle().setMarginRight(1, Unit.PX);
        m_startDate.getElement().getStyle().setFloat(Float.RIGHT);
        m_startDate.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                fireValueChange();

            }
        });

        Label l_end = new Label(m_labels.get("GUI_SERIALDATE_TIME_ENDTIME_0").isString().stringValue());
        l_end.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateLable());
        l_end.getElement().getStyle().clearLeft();
        l_end.getElement().getStyle().setFloat(Float.LEFT);
        l_end.getElement().getStyle().setMarginTop(8, Unit.PX);
        m_endDate.setStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().textBoxSerialDate());
        m_endDate.getElement().getStyle().setWidth(110, Unit.PX);
        m_endDate.getElement().getStyle().setMarginRight(1, Unit.PX);
        m_endDate.getElement().getStyle().setMarginTop(5, Unit.PX);
        m_endDate.getElement().getStyle().setFloat(Float.RIGHT);
        m_endDate.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                fireValueChange();

            }
        });

        m_duration = new CmsSelectBox();
        m_duration.getElement().getStyle().setMarginTop(5, Unit.PX);
        m_duration.getElement().getStyle().setFloat(Float.RIGHT);
        m_duration.getElement().getStyle().setMarginRight(1, Unit.PX);
        m_duration.getElement().getStyle().setMarginBottom(3, Unit.PX);
        m_duration.getOpener().setWidth("118px");
        m_duration.getOpener().setStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxSelected());
        m_duration.getSelectorPopup().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());
        m_duration.addValueChangeHandler(new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {

                fireValueChange();

            }
        });

        m_duration.addOption("0", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_SAMEDAY_0").isString().stringValue());
        m_duration.addOption("1", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_FIRST_0").isString().stringValue());
        m_duration.addOption("2", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_SECOND_0").isString().stringValue());
        m_duration.addOption("3", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_THIRD_0").isString().stringValue());
        m_duration.addOption("4", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_FOURTH_0").isString().stringValue());
        m_duration.addOption("5", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_FIFTH_0").isString().stringValue());
        m_duration.addOption("6", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_SIXTH_0").isString().stringValue());
        m_duration.addOption("7", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_ONEWEEK_0").isString().stringValue());
        m_duration.addOption("8", m_labels.get("GUI_SERIALDATE_DURATION_DURATION_TWOWEEK_0").isString().stringValue());

        m_topPanel.add(l_start);
        m_topPanel.add(m_startDate);

        m_topPanel.add(l_end);
        m_topPanel.add(m_endDate);

        m_topPanel.add(m_duration);

    }

}
