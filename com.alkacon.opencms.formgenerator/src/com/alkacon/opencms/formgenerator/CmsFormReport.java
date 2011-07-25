/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFormReport.java,v $
 * Date   : $Date: 2011-05-24 13:42:21 $
 * Version: $Revision: 1.5 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator;

import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.file.CmsFile;
import org.opencms.i18n.CmsMessages;
import org.opencms.json.JSONArray;
import org.opencms.json.JSONException;
import org.opencms.json.JSONObject;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Provides the methods to generate the form report output page.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsFormReport extends CmsJspActionElement {

    /**
     * This comparator compares the values of the field columns, it is needed for dynamic data load.<p>
     */
    private class FieldComparator implements Comparator<CmsFormDataBean> {

        /** Needed for String comparison. */
        private Collator m_collator;

        /** The name of the field to compare. */
        private String m_field;

        /** Holds the collation keys for faster String comparison. */
        private Map<String, CollationKey> m_keys;

        /** The current sort order, either ascending or descending. */
        private String m_sortOrder;

        /**
         * Constructor, with parameters.<p>
         * 
         * @param field the name of the field to compare
         * @param sortOrder the current sort order, either ascending or descending
         * @param locale the current Locale, used for String comparison
         */
        public FieldComparator(String field, String sortOrder, Locale locale) {

            m_field = field;
            m_sortOrder = sortOrder;
            m_collator = Collator.getInstance(locale);
            m_keys = new HashMap<String, CollationKey>();
        }

        /**
         * Compares the two form data beans according to the field and sort state settings.<p>
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         * 
         * @param o1 the first object to be compared
         * @param o2 the second object to be compared
         * 
         * @return a negative integer, zero, or a positive integer 
         */
        public int compare(CmsFormDataBean o1, CmsFormDataBean o2) {

            if (o1 == o2) {
                return 0;
            }

            if (COLUMN_ID_DATE.equals(m_field)) {
                // sort by creation date
                Long date1 = Long.valueOf(o1.getDateCreated());
                Long date2 = Long.valueOf(o2.getDateCreated());
                if ("asc".equals(m_sortOrder)) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            } else {
                // sort by specific column (treated as String)
                String val1 = o1.getFieldValue(m_field);
                String val2 = o2.getFieldValue(m_field);
                // get or generate collation keys for the values
                CollationKey key1, key2;
                if (m_keys.containsKey(val1)) {
                    key1 = m_keys.get(val1);
                } else {
                    key1 = m_collator.getCollationKey(val1);
                    m_keys.put(val1, key1);
                }
                if (m_keys.containsKey(val2)) {
                    key2 = m_keys.get(val2);
                } else {
                    key2 = m_collator.getCollationKey(val2);
                    m_keys.put(val2, key2);
                }

                if ("asc".equals(m_sortOrder)) {
                    return key1.compareTo(key2);
                } else {
                    return key2.compareTo(key1);
                }
            }
        }
    }

    /** Column ID of the creation date column. */
    public static final String COLUMN_ID_DATE = "creationdate";

    /** The node name for the node: show menu. */
    public static final String NODE_COLWIDTH = "ColWidth";

    /** The node name for the node: entries. */
    public static final String NODE_ENTRIES = "Entries";

    /** The node name for the node: fields. */
    public static final String NODE_FIELDS = "Fields";

    /** The node name for the node: grid height. */
    public static final String NODE_GRIDHEIGHT = "GridHeight";

    /** The node name for the node: grid height. */
    public static final String NODE_LOADDYNAMIC = "LoadDynamic";

    /** The node name for the node: show date. */
    public static final String NODE_SHOWDATE = "ShowDate";

    /** The node name for the node: show labels. */
    public static final String NODE_SHOWLABELS = "ShowLabels";

    /** The node name for the node: show menu. */
    public static final String NODE_SHOWMENU = "ShowMenu";

    /** The node name for the node: skin. */
    public static final String NODE_SKIN = "Skin";

    /** The node name for the node: URI. */
    public static final String NODE_URI = "URI";

    /** Request parameter name for the action to get report data. */
    public static final String PARAM_ACTION = "_gt_json";

    /** Value for automatic height calculation of the report data grid. */
    public static final String VALUE_HEIGHT_AUTO = "auto";

    /** The VFS path to the folder containing the static resources to generate the report grid. */
    public static final String VFS_PATH_GRIDRESOURCES = CmsWorkplace.VFS_PATH_MODULES
        + CmsForm.MODULE_NAME
        + "/resources/grid/grid/";

    /** The displayed form report columns generated from the form fields, with the column ID as key. */
    private Map<String, CmsFormReportColumn> m_columnMappings;

    /** The displayed columns generated from the form fields, including sub fields. */
    private List<CmsFormReportColumn> m_columns;

    /** The initial width of a report column. */
    private int m_columnWidth;

    /** The XML content that configures the report output. */
    private CmsXmlContent m_content;

    /** The PageContext that calls the report output. */
    private PageContext m_pageContext;

    /** The number of entries per page that is preselected. */
    private int m_entriesPerPage;

    /** The form configuration object. */
    private CmsFormHandler m_formHandler;

    /** The submitted form data. */
    private List<CmsFormDataBean> m_forms;

    /** Indicates if the report output height should be calculated automatically. */
    private Boolean m_heightAuto;

    /** Indicates if the report data should be loaded dynamically. */
    private Boolean m_loadDynamic;

    /** Indicates if the submission date column should be shown. */
    private Boolean m_showDate;

    /** Indicates if the labels of the fields should be shown. */
    private Boolean m_showLabels;

    /** Indicates if the main report output should be generated. */
    private Boolean m_showReport;

    /**
     * Constructor, creates the necessary form report configuration objects.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsFormReport(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
        initReport(context, req, res, getRequestContext().getUri());

    }

    /**
     * Constructor, creates the necessary form report configuration objects.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * @param reportUri URI of the report configuration file
     */
    public CmsFormReport(PageContext context, HttpServletRequest req, HttpServletResponse res, String reportUri) {

        super(context, req, res);
        initReport(context, req, res, reportUri);
    }

    /**
     * Returns the initial column with for the report.<p>
     * 
     * @return the initial column with for the report
     */
    public int getColumnWidth() {

        if (m_columnWidth < 1) {
            String widthStr = getReportContent().getStringValue(
                getCmsObject(),
                NODE_COLWIDTH,
                getRequestContext().getLocale());
            m_columnWidth = 80;
            try {
                m_columnWidth = Integer.parseInt(widthStr);
            } catch (Exception e) {
                // no number specified, fall back to default value
            }
        }
        return m_columnWidth;
    }

    /**
     * Returns the preselected number of entries per page of the report.<p>
     * 
     * @return the preselected number of entries per page of the report
     */
    public int getEntriesPerPage() {

        if (m_entriesPerPage < 1) {
            m_entriesPerPage = 100;
            if (isHeightAuto()) {
                m_entriesPerPage = getForms().size();
            } else {
                String entriesStr = getReportContent().getStringValue(
                    getCmsObject(),
                    NODE_ENTRIES,
                    getRequestContext().getLocale());
                try {
                    m_entriesPerPage = Integer.parseInt(entriesStr);
                } catch (Exception e) {
                    // no number specified, fall back to default value
                }
            }
        }
        return m_entriesPerPage;
    }

    /**
     * Returns the form configuration.<p>
     * 
     * @return the form configuration
     */
    public CmsForm getFormConfiguration() {

        return m_formHandler.getFormConfiguration();
    }

    /**
     * Returns the height of the report grid.<p>
     * 
     * If the height is set to {@link #VALUE_HEIGHT_AUTO} in the report configuration, the height is calculated
     * for the current number of form items.<p>
     * 
     * @return the height of the report grid
     */
    public int getGridHeight() {

        String heightStr = getReportContent().getStringValue(
            getCmsObject(),
            NODE_GRIDHEIGHT,
            getRequestContext().getLocale());
        int height = 350;

        if (VALUE_HEIGHT_AUTO.equalsIgnoreCase(heightStr)) {
            // automatic height calculation
            m_heightAuto = Boolean.TRUE;
            // height: header + footer + eventual scroll bar + height of items (each item: 22px)
            height = 57 + 28 + 15 + (getForms().size() * 22);
        } else {
            // fixed height
            try {
                height = Integer.parseInt(heightStr);
            } catch (Exception e) {
                // no number specified, fall back to default value
            }
        }
        return height;
    }

    /**
     * Returns the localized messages necessary to create the report output.<p>
     * 
     * @return the localized messages necessary to create the report output
     */
    public CmsMessages getMessages() {

        return m_formHandler.getMessages();
    }

    /** 
     * Returns the report data as JSON array String.<p>
     * 
     * Note: <i>all</i> data entries are returned, should not be used for large result sets.<p>
     * 
     * @return the report data as JSON array String
     */
    public String getReportData() {

        return getDataRows(getForms()).toString();
    }

    /** 
     * Returns the dynamic report data for the current page as JSON array String.<p>
     * 
     * Note: only currently visible data entries are returned, should be used for large result sets.<p>
     * 
     * @return the dynamic report data as JSON array String
     */
    public String getReportDataDynamic() {

        JSONObject data = new JSONObject();
        try {
            JSONObject allInfo = new JSONObject(getRequest().getParameter(PARAM_ACTION));
            JSONObject pageInfoReceived = allInfo.getJSONObject("pageInfo");
            JSONObject sortInfo = new JSONObject();
            try {
                // the sort info is provided as array containing an object, really strange...
                sortInfo = allInfo.getJSONArray("sortInfo").getJSONObject(0);
            } catch (JSONException e) {
                // sort info is not provided, this is the case on initial call
            }
            int pageSize = pageInfoReceived.getInt("pageSize");
            int startRow = pageInfoReceived.getInt("startRowNum");
            int endRow = pageInfoReceived.getInt("endRowNum");
            if (endRow < 0) {
                endRow = (startRow + pageSize) - 1;
            }
            // determine the sort column ID
            String sortColumn = "";
            if (sortInfo.has("columnId")) {
                sortColumn = sortInfo.getString("columnId");
            }
            // determine the sort order
            String sortOrder = "asc";
            if (sortInfo.has("sortOrder")) {
                sortOrder = sortInfo.getString("sortOrder");
            }
            // get all stored forms
            List<CmsFormDataBean> forms = getForms();
            // calculate start and end index
            int startIndex = startRow - 1;
            int endIndex = endRow - 1;
            if (endIndex > (forms.size() - 1)) {
                endIndex = forms.size() - 1;
            }
            if (CmsStringUtil.isNotEmpty(sortColumn)) {
                // sort the data according to column and order
                String fieldName = sortColumn;
                if (!COLUMN_ID_DATE.equals(sortColumn)) {
                    // determine the matching field DB label to the given column ID
                    CmsFormReportColumn col = getShownColumnMappings().get(sortColumn);
                    if (col != null) {
                        fieldName = col.getColumnDbLabel();
                    }
                }
                Collections.sort(forms, new FieldComparator(fieldName, sortOrder, getRequestContext().getLocale()));
            }
            // fill the returned object with data
            data.put("data", getDataRows(forms.subList(startIndex, endIndex + 1)));
            JSONObject pageInfo = new JSONObject();
            pageInfo.put("totalRowNum", forms.size());
            data.put("pageInfo", pageInfo);
            data.put("recordType", "array");
        } catch (Exception e) {
            // error creating data
        }
        return data.toString();
    }

    /**
     * Returns the displayed form report columns generated from the form fields, with the column ID as key.<p>
     * 
     * @return the displayed form report columns
     */
    public Map<String, CmsFormReportColumn> getShownColumnMappings() {

        if (m_columnMappings == null) {
            m_columnMappings = new HashMap<String, CmsFormReportColumn>(getShownColumns().size());
            for (Iterator<CmsFormReportColumn> i = getShownColumns().iterator(); i.hasNext();) {
                CmsFormReportColumn col = i.next();
                m_columnMappings.put(col.getColumnId(), col);
            }
        }
        return m_columnMappings;
    }

    /**
     * Returns the displayed columns generated from the form fields, including sub fields.<p>
     * 
     * @return the displayed columns
     */
    public List<CmsFormReportColumn> getShownColumns() {

        return m_columns;
    }

    /**
     * Returns the name of the skin to use, either <code>default</code>, <code>mac</code>, <code>vista</code> or <code>pink</code>.<p>
     * 
     * @return the name of the skin to use
     */
    public String getSkin() {

        String skin = getReportContent().getStringValue(getCmsObject(), NODE_SKIN, getRequestContext().getLocale());
        if (CmsStringUtil.isEmpty(skin)) {
            skin = "mac";
        }
        return skin;
    }

    /**
     * Returns the unsubstituted VFS path to the localized messages JS for the grid.<p>
     * 
     * @return the unsubstituted VFS path to the localized messages JS
     */
    public String getVfsPathGridMessages() {

        String locale = getRequestContext().getLocale().toString();
        String fileName = VFS_PATH_GRIDRESOURCES + "gt_msg_" + locale + ".js";
        if (getCmsObject().existsResource(fileName)) {
            // found JS messages for current locale
            return fileName;
        }
        // use default English localization
        return VFS_PATH_GRIDRESOURCES + "gt_msg_en.js";
    }

    /**
     * Returns if the report output height should be calculated automatically.<p>
     * 
     * @return <code>true</code> if the report output height should be calculated automatically, otherwise <code>false</code>
     */
    public boolean isHeightAuto() {

        if (m_heightAuto == null) {
            m_heightAuto = Boolean.valueOf(VALUE_HEIGHT_AUTO.equalsIgnoreCase(getReportContent().getStringValue(
                getCmsObject(),
                NODE_GRIDHEIGHT,
                getRequestContext().getLocale())));
        }
        return m_heightAuto.booleanValue();
    }

    /**
     * Returns if the CSS style sheets can be included in the form output.<p>
     * 
     * <b>Important</b>: to generate valid XHTML code, specify <code>false</code> as module
     * parameter value of {@link CmsForm#MODULE_PARAM_CSS} and include the CSS in your template head manually.<br/>
     * In this case, you can include the JSP <code>report_css.jsp</code>
     * in the <code>elements/</code> sub folder of the form generator module.<p>
     * 
     * @return <code>true</code> if the CSS style sheets can be included in the form output, otherwise <code>false</code>
     */
    public boolean isIncludeStyleSheet() {

        String cssParam = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME).getParameter(
            CmsForm.MODULE_PARAM_CSS);
        if (CmsStringUtil.FALSE.equalsIgnoreCase(cssParam)) {
            return false;
        }
        return true;
    }

    /**
     * Returns if the report data should be loaded dynamically.<p>
     * 
     * @return <code>true</code> if the report data should be loaded dynamically, otherwise <code>false</code>
     */
    public boolean isLoadDynamic() {

        if (m_loadDynamic == null) {
            m_loadDynamic = Boolean.valueOf(getReportContent().getStringValue(
                getCmsObject(),
                NODE_LOADDYNAMIC,
                getRequestContext().getLocale()));
        }
        return m_loadDynamic.booleanValue();
    }

    /**
     * Returns if the labels of the fields should be shown.<p>
     * 
     * @return <code>true</code> if the labels of the fields should be shown, otherwise <code>false</code>
     */
    public boolean isShowDate() {

        if (m_showDate == null) {
            m_showDate = Boolean.valueOf(getReportContent().getStringValue(
                getCmsObject(),
                NODE_SHOWDATE,
                getRequestContext().getLocale()));
        }
        return m_showDate.booleanValue();
    }

    /**
     * Returns if the labels of the fields should be shown.<p>
     * 
     * @return <code>true</code> if the labels of the fields should be shown, otherwise <code>false</code>
     */
    public boolean isShowLabels() {

        if (m_showLabels == null) {
            m_showLabels = Boolean.valueOf(getReportContent().getStringValue(
                getCmsObject(),
                NODE_SHOWLABELS,
                getRequestContext().getLocale()));
        }
        return m_showLabels.booleanValue();
    }

    /**
     * Returns if the grid menu button should be shown.<p>
     * 
     * @return <code>true</code> if the grid menu button should be shown, otherwise <code>false</code>
     */
    public boolean isShowMenu() {

        String menu = getReportContent().getStringValue(getCmsObject(), NODE_SHOWMENU, getRequestContext().getLocale());
        return Boolean.valueOf(menu).booleanValue();
    }

    /**
     * Returns if the main report output should be generated.<p>
     * 
     * @return <code>true</code> if the main report output should be generated, otherwise <code>false</code>
     */
    public boolean isShowReport() {

        //        return getRequest().getParameter(PARAM_ACTION) == null;
        m_showReport = (getRequest().getParameter(PARAM_ACTION) == null);
        return m_showReport;

    }

    /**
     * Returns the JSON array with data generated from the given list of forms.<p>
     * 
     * @param forms the forms to generate the data from
     * 
     * @return the JSON array with data
     */
    protected JSONArray getDataRows(List<CmsFormDataBean> forms) {

        JSONArray data = new JSONArray();

        for (Iterator<CmsFormDataBean> i = forms.iterator(); i.hasNext();) {
            CmsFormDataBean dataBean = i.next();
            JSONArray row = new JSONArray();
            if (isShowDate()) {
                // add submission date to row data
                row.put(dataBean.getDateCreated());
            }
            for (Iterator<CmsFormReportColumn> k = getShownColumns().iterator(); k.hasNext();) {
                String val = dataBean.getFieldValue(k.next().getColumnDbLabel());
                if (CmsStringUtil.isEmpty(val)) {
                    // also store empty values
                    row.put("");
                } else {
                    row.put(val);
                }
            }
            data.put(row);
        }

        return data;
    }

    /**
     * Returns all forms stored in the database.<p>
     * 
     * @return all forms stored in the database
     */
    protected List<CmsFormDataBean> getForms() {

        if (m_forms == null) {
            try {
                CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
                filter = filter.filterFormId(getFormConfiguration().getFormId());
                m_forms = CmsFormDataAccess.getInstance().readForms(filter);
            } catch (Exception e) {
                // error reading form data
                m_forms = Collections.emptyList();
            }
        }
        return m_forms;
    }

    /**
     * Returns the report XML content.<p>
     * 
     * @return the report XML content
     */
    protected CmsXmlContent getReportContent() {

        if (m_content == null) {
            try {
                CmsFile file = getCmsObject().readFile((String)m_pageContext.getAttribute("uri"));
                m_content = CmsXmlContentFactory.unmarshal(getCmsObject(), file);
            } catch (CmsException e) {
                // should not happen
            }
        }
        return m_content;
    }

    /**
     * Initializes the report configuration.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * @param reportUri URI of the report configuration file
     */
    protected void initReport(PageContext context, HttpServletRequest req, HttpServletResponse res, String reportUri) {

        m_columns = new ArrayList<CmsFormReportColumn>();
        m_pageContext = context;
        try {
            CmsFile file = getCmsObject().readFile(reportUri);
            m_content = CmsXmlContentFactory.unmarshal(getCmsObject(), file);
            // get the web form URI
            String formUri = m_content.getStringValue(getCmsObject(), NODE_URI, getRequestContext().getLocale());
            m_formHandler = CmsFormHandlerFactory.create(context, req, res, formUri);
            String checkedFieldsStr = m_content.getStringValue(
                getCmsObject(),
                NODE_FIELDS,
                getRequestContext().getLocale());
            List<String> checkedFields = new ArrayList<String>();
            boolean showAllFields = CmsStringUtil.isEmptyOrWhitespaceOnly(checkedFieldsStr);
            if (!showAllFields) {
                checkedFields = CmsStringUtil.splitAsList(checkedFieldsStr, CmsReportCheckFieldsWidget.SEPARATOR_FIELDS);
            }
            // loop configured form fields to get the displayed fields
            for (Iterator<I_CmsField> i = getFormConfiguration().getFields().iterator(); i.hasNext();) {
                I_CmsField field = i.next();
                if (field.getType().equals(CmsPagingField.getStaticType())
                    || field.getType().equals(CmsEmptyField.getStaticType())) {
                    continue;
                }
                if (showAllFields || checkedFields.contains(field.getDbLabel())) {
                    // this field has to be shown, add it to the columns and check sub fields
                    m_columns.add(new CmsFormReportColumn(field));
                    if (field.isHasSubFields()) {
                        Iterator<Entry<String, List<I_CmsField>>> k = field.getSubFields().entrySet().iterator();
                        while (k.hasNext()) {
                            Map.Entry<String, List<I_CmsField>> entry = k.next();
                            m_columns.addAll(CmsFormReportColumn.getColumnsFromFields(entry.getValue()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // something went wrong, create a new exception
            throw new CmsRuntimeException(Messages.get().container(Messages.ERR_REPORT_NO_FORM_URI_0), e);
        }
    }
}
