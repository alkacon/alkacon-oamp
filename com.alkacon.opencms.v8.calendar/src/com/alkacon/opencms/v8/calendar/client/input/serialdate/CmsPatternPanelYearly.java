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

package com.alkacon.opencms.v8.calendar.client.input.serialdate;

import com.alkacon.opencms.v8.calendar.client.widget.css.I_CmsLayoutBundle;

import org.opencms.gwt.client.ui.input.CmsRadioButton;
import org.opencms.gwt.client.ui.input.CmsRadioButtonGroup;
import org.opencms.gwt.client.ui.input.CmsSelectBox;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * */
public class CmsPatternPanelYearly extends FlowPanel implements HasValueChangeHandlers<String> {

    /** Group off all radio buttons. */
    private CmsRadioButtonGroup m_group = new CmsRadioButtonGroup();

    /** The panel for all values of 'every'. */
    private FlowPanel m_everyPanel = new FlowPanel();

    /** The panel for all values of 'at'. */
    private FlowPanel m_atPanel = new FlowPanel();

    /** The text box for the date input. */
    private TextBox m_everyDay = new TextBox();
    /** The select box for the month selection. */
    private CmsSelectBox m_everyMonth = new CmsSelectBox();

    /** The select box for the nummeric selection. */
    private CmsSelectBox m_atNummer = new CmsSelectBox();
    /** The select box for the day selection. */
    private CmsSelectBox m_atDay = new CmsSelectBox();
    /** The select box for the month selection. */
    private CmsSelectBox m_atMonth = new CmsSelectBox();

    /** The array of all radio button. */
    private CmsRadioButton[] m_radio = new CmsRadioButton[2];

    /** The value change handler. */
    private ValueChangeHandler<String> m_handler;

    /** JSON of all needed labels. */
    private JSONObject m_labels;

    /**
     * Default constructor to create the panel.<p>
     * @param labels JSON of all needed labels
     */
    public CmsPatternPanelYearly(JSONObject labels) {

        m_labels = labels;

        addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateYear());
        CmsRadioButton sel1 = new CmsRadioButton(
            "sel1",
            m_labels.get("GUI_SERIALDATE_YEARLY_EVERY_0").isString().stringValue());
        m_radio[0] = sel1;
        sel1.setGroup(m_group);
        sel1.setChecked(true);
        sel1.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateYearSelection());
        sel1.addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
        createEverPanel();
        CmsRadioButton sel2 = new CmsRadioButton(
            "sel2",
            m_labels.get("GUI_SERIALDATE_YEARLY_AT_0").isString().stringValue());
        m_radio[1] = sel2;
        sel2.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().serialDateYearSelection());
        sel2.addStyleName(org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().radioButtonlabel());
        sel2.setGroup(m_group);
        createAtPanel();
        this.add(sel1);
        this.add(m_everyPanel);

        this.add(sel2);
        this.add(m_atPanel);

    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        m_handler = handler;
        m_atNummer.addValueChangeHandler(m_handler);
        m_atDay.addValueChangeHandler(m_handler);
        m_atMonth.addValueChangeHandler(m_handler);
        m_everyDay.addValueChangeHandler(m_handler);
        m_everyMonth.addValueChangeHandler(m_handler);
        for (int i = 0; i < m_radio.length; i++) {
            m_radio[i].addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {

                    fireValueChange();

                }
            });
        }
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Represents a value change event.<p>
     */
    public void fireValueChange() {

        ValueChangeEvent.fire(this, getWeekDays());
    }

    /**
     * Returns the day of month.<p>
     * @return the day of month
     * */
    public String getDayOfMonth() {

        if (m_group.getSelectedButton().equals(m_radio[0])) {
            return m_everyDay.getText();
        } else {
            return m_atNummer.getFormValueAsString();
        }
    }

    /**
     * Returns the month.<p>
     * @return the month
     * */
    public String getMonth() {

        if (m_group.getSelectedButton().equals(m_radio[0])) {
            return m_everyMonth.getFormValueAsString();
        } else {
            return m_atMonth.getFormValueAsString();
        }

    }

    /**
     * Returns the week day.<p>
     * @return the week day
     * */
    public String getWeekDays() {

        if (m_group.getSelectedButton().equals(m_radio[0])) {
            return "-1";
        } else {
            return m_atDay.getFormValueAsString();
        }

    }

    /**
     * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
     */
    @Override
    public Iterator<Widget> iterator() {

        Iterator<Widget> result = getChildren().iterator();
        return result;
    }

    /**
     * @see com.google.gwt.user.client.ui.Panel#remove(com.google.gwt.user.client.ui.Widget)
     */
    @Override
    public boolean remove(Widget child) {

        return remove(child);
    }

    /**
     * Sets the interval.<p>
     * 
     * @param dayOfMonth the interval
     * */
    public void setDayOfMonth(int dayOfMonth) {

        if (m_group.getSelectedButton().equals(m_radio[0])) {
            m_everyDay.setText(dayOfMonth + "");
        } else {
            m_atNummer.selectValue(dayOfMonth + "");
        }

    }

    /**
     * Sets the month.<p>
     * @param month the month
     * */
    public void setMonth(int month) {

        if (m_group.getSelectedButton().equals(m_radio[0])) {
            m_everyMonth.selectValue(month + "");
        } else {
            m_atMonth.selectValue(month + "");
        }

    }

    /**
     * Sets the week day.<p>
     * 
     *  @param weekDay the week day
     * */
    public void setWeekDay(int weekDay) {

        if (weekDay == -1) {
            m_group.selectButton(m_radio[0]);
        } else {
            m_group.selectButton(m_radio[1]);
            m_atDay.selectValue(weekDay + "");
        }

    }

    /**
     * Creates the 'at' selection view.<p>
     * */
    private void createAtPanel() {

        m_atPanel.add(m_atNummer);
        m_atNummer.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxPanel());
        m_atNummer.getOpener().setStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxSelected());
        m_atNummer.getSelectorPopup().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());
        m_atNummer.setWidth("80px");
        m_atNummer.addOption("1", m_labels.get("GUI_SERIALDATE_WEEKDAYNUMBER_1_0").isString().stringValue());
        m_atNummer.addOption("2", m_labels.get("GUI_SERIALDATE_WEEKDAYNUMBER_2_0").isString().stringValue());
        m_atNummer.addOption("3", m_labels.get("GUI_SERIALDATE_WEEKDAYNUMBER_3_0").isString().stringValue());
        m_atNummer.addOption("4", m_labels.get("GUI_SERIALDATE_WEEKDAYNUMBER_4_0").isString().stringValue());
        m_atNummer.addOption("5", m_labels.get("GUI_SERIALDATE_WEEKDAYNUMBER_5_0").isString().stringValue());
        m_atPanel.add(m_atDay);
        m_atDay.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxPanel());
        m_atDay.getOpener().setStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxSelected());
        m_atDay.getSelectorPopup().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());
        m_atDay.setWidth("100px");
        m_atDay.addOption("1", m_labels.get("GUI_SERIALDATE_DAY_SUNDAY_0").isString().stringValue());
        m_atDay.addOption("2", m_labels.get("GUI_SERIALDATE_DAY_MONDAY_0").isString().stringValue());
        m_atDay.addOption("3", m_labels.get("GUI_SERIALDATE_DAY_TUESDAY_0").isString().stringValue());
        m_atDay.addOption("4", m_labels.get("GUI_SERIALDATE_DAY_WEDNESDAY_0").isString().stringValue());
        m_atDay.addOption("5", m_labels.get("GUI_SERIALDATE_DAY_THURSDAY_0").isString().stringValue());
        m_atDay.addOption("6", m_labels.get("GUI_SERIALDATE_DAY_FRIDAY_0").isString().stringValue());
        m_atDay.addOption("7", m_labels.get("GUI_SERIALDATE_DAY_SATURDAY_0").isString().stringValue());

        m_atPanel.add(new Label(m_labels.get("GUI_SERIALDATE_YEARLY_IN_0").isString().stringValue()));
        m_atPanel.add(m_atMonth);
        m_atMonth.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxPanel());
        m_atMonth.getOpener().setStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxSelected());
        m_everyMonth.getSelectorPopup().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());
        m_atMonth.setWidth("100px");
        m_atMonth.addOption("0", m_labels.get("GUI_SERIALDATE_YEARLY_JAN_0").isString().stringValue());
        m_atMonth.addOption("1", m_labels.get("GUI_SERIALDATE_YEARLY_FEB_0").isString().stringValue());
        m_atMonth.addOption("2", m_labels.get("GUI_SERIALDATE_YEARLY_MAR_0").isString().stringValue());
        m_atMonth.addOption("3", m_labels.get("GUI_SERIALDATE_YEARLY_APR_0").isString().stringValue());
        m_atMonth.addOption("4", m_labels.get("GUI_SERIALDATE_YEARLY_MAY_0").isString().stringValue());
        m_atMonth.addOption("5", m_labels.get("GUI_SERIALDATE_YEARLY_JUN_0").isString().stringValue());
        m_atMonth.addOption("6", m_labels.get("GUI_SERIALDATE_YEARLY_JUL_0").isString().stringValue());
        m_atMonth.addOption("7", m_labels.get("GUI_SERIALDATE_YEARLY_AUG_0").isString().stringValue());
        m_atMonth.addOption("8", m_labels.get("GUI_SERIALDATE_YEARLY_SEP_0").isString().stringValue());
        m_atMonth.addOption("9", m_labels.get("GUI_SERIALDATE_YEARLY_OCT_0").isString().stringValue());
        m_atMonth.addOption("10", m_labels.get("GUI_SERIALDATE_YEARLY_NOV_0").isString().stringValue());
        m_atMonth.addOption("11", m_labels.get("GUI_SERIALDATE_YEARLY_DEC_0").isString().stringValue());

    }

    /**
     * Creates the 'every' selection view.<p>
     * 
     * */
    private void createEverPanel() {

        m_everyPanel.add(m_everyDay);
        m_everyDay.setStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().textBoxSerialDate());
        m_everyDay.setText("1");
        m_everyDay.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                fireValueChange();

            }
        });
        m_everyPanel.add(new Label(""));
        m_everyPanel.add(m_everyMonth);
        m_everyMonth.addStyleName(I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxPanel());
        m_everyMonth.getOpener().setStyleName(
            org.opencms.ade.contenteditor.client.css.I_CmsLayoutBundle.INSTANCE.widgetCss().selectBoxSelected());
        m_everyMonth.getSelectorPopup().addStyleName(I_CmsLayoutBundle.INSTANCE.globalWidgetCss().selectBoxPopup());
        m_everyMonth.setWidth("100px");
        m_everyMonth.addOption("0", m_labels.get("GUI_SERIALDATE_YEARLY_JAN_0").isString().stringValue());
        m_everyMonth.addOption("1", m_labels.get("GUI_SERIALDATE_YEARLY_FEB_0").isString().stringValue());
        m_everyMonth.addOption("2", m_labels.get("GUI_SERIALDATE_YEARLY_MAR_0").isString().stringValue());
        m_everyMonth.addOption("3", m_labels.get("GUI_SERIALDATE_YEARLY_APR_0").isString().stringValue());
        m_everyMonth.addOption("4", m_labels.get("GUI_SERIALDATE_YEARLY_MAY_0").isString().stringValue());
        m_everyMonth.addOption("5", m_labels.get("GUI_SERIALDATE_YEARLY_JUN_0").isString().stringValue());
        m_everyMonth.addOption("6", m_labels.get("GUI_SERIALDATE_YEARLY_JUL_0").isString().stringValue());
        m_everyMonth.addOption("7", m_labels.get("GUI_SERIALDATE_YEARLY_AUG_0").isString().stringValue());
        m_everyMonth.addOption("8", m_labels.get("GUI_SERIALDATE_YEARLY_SEP_0").isString().stringValue());
        m_everyMonth.addOption("9", m_labels.get("GUI_SERIALDATE_YEARLY_OCT_0").isString().stringValue());
        m_everyMonth.addOption("10", m_labels.get("GUI_SERIALDATE_YEARLY_NOV_0").isString().stringValue());
        m_everyMonth.addOption("11", m_labels.get("GUI_SERIALDATE_YEARLY_DEC_0").isString().stringValue());

    }

}
