/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.counter/src/com/alkacon/opencms/counter/CmsCounterDialog.java,v $
 * Date   : $Date: 2009/07/21 12:35:57 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.counter;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsCheckboxWidget;
import org.opencms.widgets.CmsInputWidget;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWidgetDialog;
import org.opencms.workplace.CmsWidgetDialogParameter;
import org.opencms.workplace.CmsWorkplaceSettings;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Dialog to create a list of counters based on the database-tables in the administration view.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.3
 */
public class CmsCounterDialog extends CmsWidgetDialog {

    /** localized messages Keys prefix. */
    public static final String COUNTER_KEY_PREFIX = "counter";

    /** Defines which pages are valid for this dialog. */
    public static final String[] PAGES = {"page1"};

    /** The log object for this class.<p>  */
    private static final Log LOG = CmsLog.getLog(CmsCounterDialog.class);

    /**
     * This function returns the CounterManager from the module action instance. If
     * it isn't exists, then it will be created.<p>
     * 
     * @return the CounterManager
     */
    public static CmsCounterManager getCounterManager() {

        // Get the module
        CmsModule module = OpenCms.getModuleManager().getModule(CmsCounterManager.MODULE_NAME);
        // Get the action class
        CmsCounterManager result = (CmsCounterManager)module.getActionInstance();
        if (result == null) {
            result = new CmsCounterManager();
        }
        return result;
    }

    /** Contains all available counters from the database. */
    private SortedMap m_counterList;

    /** The manager which is the gateway to the database. */
    private CmsCounterManager m_manager;

    /** Contains the flag to overwrite the values in the database. */
    private boolean m_overwrite;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsCounterDialog(CmsJspActionElement jsp) {

        super(jsp);

    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsCounterDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));

    }

    /**
     * 
     * @see org.opencms.workplace.CmsWidgetDialog#actionCommit()
     */
    public void actionCommit() {

        if (!hasCommitErrors()) {
            if (!m_counterList.isEmpty()) {
                try {

                    // update the database with the values from the dialog
                    updateCounterValues(m_counterList);

                } catch (Exception ex) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_UPDATE_DB_0), ex);
                    }
                    addCommitError(ex);
                }
            }
        }

    }

    /**
     * Returns a sorted Map.<p>
     *
     * @return the sorted map of counters
     */
    public SortedMap getCounters() {

        return m_counterList;
    }

    /**
     * Returns the overwrite.<p>
     *
     * @return the overwrite
     */
    public boolean isOverwrite() {

        return m_overwrite;
    }

    /**
     * Sets the counters.<p>
     *
     * @param counters the counters to set
     */
    public void setCounters(SortedMap counters) {

        m_counterList = counters;
    }

    /**
     * Sets the overwrite.<p>
     *
     * @param overwrite the overwrite to set
     */
    public void setOverwrite(boolean overwrite) {

        m_overwrite = overwrite;
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWidgetDialog#createDialogHtml(java.lang.String)
     */
    protected String createDialogHtml(String dialog) {

        StringBuffer result = new StringBuffer(1024);

        // show error header once if there were validation errors
        result.append(createWidgetTableStart());
        result.append(createWidgetErrorHeader());

        if (dialog == null || dialog.equals(PAGES[0])) {

            result.append(createWidgetBlockStart(key(Messages.GUI_COUNTERS_BLOCK_LABEL_0)));
            result.append(createDialogRowsHtml(0, 0));
            result.append(createWidgetBlockEnd());

            result.append(createWidgetBlockStart(key(Messages.GUI_OVERWRITE_BLOCK_LABEL_0)));
            result.append(createDialogRowHtml((CmsWidgetDialogParameter)getWidgets().get(getWidgets().size() - 1)));
            result.append(createWidgetBlockEnd());
        }

        result.append(createWidgetTableEnd());
        return result.toString();
    }

    /**
     * 
     * @see org.opencms.workplace.CmsWidgetDialog#defineWidgets()
     */
    protected void defineWidgets() {

        // initialize the counter list to use for the dialog
        initCounterObject();
        setKeyPrefix(COUNTER_KEY_PREFIX);

        // widgets to display
        addWidget(new CmsWidgetDialogParameter(this, "counters", PAGES[0], new CmsInputWidget()));
        addWidget(new CmsWidgetDialogParameter(this, "overwrite", PAGES[0], new CmsCheckboxWidget()));
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#getPageArray()
     */
    protected String[] getPageArray() {

        return PAGES;
    }

    /**
     * Initializes the counter object to work with depending on the dialog state and request parameters.<p>
     * 
     */
    protected void initCounterObject() {

        if (m_manager == null) {
            m_manager = getCounterManager();
        }

        if (CmsStringUtil.isEmpty(getParamAction()) || CmsDialog.DIALOG_INITIAL.equals(getParamAction())) {
            // initialize
            if (m_counterList == null) {
                m_counterList = m_manager.getCounters();
            }
        } else {
            // this is not the initial call, get module from session
            Object o = getDialogObject();
            if (o instanceof SortedMap) {
                m_counterList = (SortedMap)o;
            } else {
                m_counterList = m_manager.getCounters();
            }
        }
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings, javax.servlet.http.HttpServletRequest)
     */
    protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

        super.initWorkplaceRequestValues(settings, request);

        // save the current state of the module (may be changed because of the widget values)
        setDialogObject(m_counterList);
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#validateParamaters()
     */
    protected void validateParamaters() throws Exception {

        if (m_counterList != null && !m_counterList.isEmpty()) {
            Iterator iterator = m_counterList.values().iterator();
            while (iterator.hasNext()) {
                getIntValue(iterator.next());
            }
        }
    }

    /**
     * This functions parse the given value and return the value as integer value.<p>
     * 
     * @param value the value with numbers with the type of {@link String} or {@link Integer} 
     * 
     * @return the integer value of the given object or "0" if something goes wrong
     * 
     * @throws CmsException if an error occurred
     */
    private int getIntValue(Object value) throws CmsException {

        int result = 0;
        try {
            if (value != null) {
                if (value instanceof String) {
                    String string = (String)value;
                    result = Integer.parseInt(string);
                } else if (value instanceof Integer) {
                    Integer integ = (Integer)value;
                    result = integ.intValue();
                }
            }
        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().container(Messages.LOG_ERROR_PARSE_INTEGER_1, value), ex);
            }
            throw new CmsException(Messages.get().container(Messages.LOG_ERROR_PARSE_INTEGER_1, value), ex);
        }
        return result;
    }

    /**
     * This function compares the list from the dialog with the list from the database and
     * update the list from the database with the values from the dialog.<p>
     * 
     * @param counterList the list from the dialog
     *
     * @throws Exception if an Exception occurred.
     */
    private void updateCounterValues(SortedMap counterList) throws Exception {

        if (m_manager == null) {
            m_manager = getCounterManager();
        }
        // get the counters from the database
        TreeMap map = m_manager.getCounters();
        Iterator iteratork = map.keySet().iterator();
        Iterator iterator = map.values().iterator();

        // for each entry check if its changed or deleted
        int o_value;
        int new_value;
        String o_key;
        while (iterator.hasNext() && iteratork.hasNext()) {
            o_value = getIntValue(iterator.next());
            o_key = (String)iteratork.next();
            if (counterList.containsKey(o_key)) {
                // the value exits
                new_value = getIntValue(counterList.get(o_key));
                if (o_value != new_value) {
                    if ((o_value < new_value) || (o_value > new_value && m_overwrite)) {
                        m_manager.setCounter(o_key, new_value);
                    }
                    counterList.remove(o_key);
                } else {
                    counterList.remove(o_key);
                }
            } else {
                // the value is deleted
                m_manager.deleteCounter(o_key);
            }
        }

        // now the new values is adding to the database
        if (!counterList.isEmpty()) {
            iteratork = counterList.keySet().iterator();
            iterator = counterList.values().iterator();
            while (iterator.hasNext() && iteratork.hasNext()) {
                o_value = getIntValue(iterator.next());
                o_key = (String)iteratork.next();
                m_manager.setCounter(o_key, o_value);
            }
        }

    }
}
