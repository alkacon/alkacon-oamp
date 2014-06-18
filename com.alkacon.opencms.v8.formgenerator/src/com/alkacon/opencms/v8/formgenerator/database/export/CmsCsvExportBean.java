/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/database/export/CmsCvsExportBean.java,v $
 * Date   : $Date: 2011/03/24 16:33:49 $
 * Version: $Revision: 1.11 $
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

package com.alkacon.opencms.v8.formgenerator.database.export;

import com.alkacon.opencms.v8.formgenerator.CmsForm;
import com.alkacon.opencms.v8.formgenerator.CmsFormHandler;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.jsp.util.CmsJspContentAccessBean;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.xml.containerpage.CmsContainerElementBean;
import org.opencms.xml.containerpage.CmsContainerPageBean;
import org.opencms.xml.containerpage.CmsXmlContainerPage;
import org.opencms.xml.containerpage.CmsXmlContainerPageFactory;

import java.sql.SQLException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;

/**
 * Bean that supports the data export.<p>
 * 
 * Works together with 
 * /system/modules/com.alkacon.opencms.v8.formgenerator/elements/datadownload.jsp.<p>
 * 
 * @author Achim Westermann
 * @author Daniel Seidel
 * 
 * @version $Revision: 1.11 $
 * 
 * @since 7.0.4
 *
 */
public class CmsCsvExportBean {

    /** The default value delimiter for CSV files in Excel. */
    public static final String EXCEL_DEFAULT_CSV_DELMITER = ",";

    /** Request parameter for the start time of the data to export. */
    public static final String PARAM_EXPORT_DATA_TIME_END = "endtime";

    /** Request parameter for the start time of the data to export. */
    public static final String PARAM_EXPORT_DATA_TIME_START = "starttime";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCsvExportBean.class);

    /** Placeholder name for default column */
    private static final String DEFAULT_COLUMN_PLACEHOLDER_CREATION_DATE = "_creationdate_";
    /** Placeholder name for default column */
    private static final String DEFAULT_COLUMN_PLACEHOLDER_RESOURCE_PATH = "_resourcepath_";
    /** Placeholder name for default column */
    private static final String DEFAULT_COLUMN_PLACEHOLDER_RESOURCE_UUID = "_resourceuuid_";

    /** Name of a default column */
    private static final String DEFAULT_COLUMN_NAME_CREATION_DATE = "Creation date";
    /** Name of a default column */
    private static final String DEFAULT_COLUMN_NAME_RESOURCE_PATH = "Resource path";
    /** Name of a default column */
    private static final String DEFAULT_COLUMN_NAME_RESOURCE_UUID = "Resource UUID";

    /** The end time for data sets to export. */
    private Date m_endTime;

    /** The form that was used to input the data to export. */
    private CmsFormHandler m_formHandler;

    /** Needed to read the resource for the UUID. */
    private CmsObject m_cms;

    /** The start time for data sets to export. */
    private Date m_startTime;

    /** The module where parameters are read from */
    private CmsModule m_module;

    /** Cached line separator parameter */
    private String m_lineSeparatorParam = null;

    /** Cached numbers as strings flag */
    private Boolean m_numbersAsStrings = null;

    /** The locale for the CSV output */
    private Locale m_locale = null;

    /** The form id for which CSV output is generated */
    private String m_formId = null;

    /** The optional export configuration */
    private List<String> m_exportConfiguration = null;

    /**
     * Creates an instance based upon data that was entered with the given form.<p> 
     * 
     * @param formHandler the origin of the data 
     */
    public CmsCsvExportBean(final CmsFormHandler formHandler) {

        init(formHandler.getCmsObject());
        m_formHandler = formHandler;
        setFormId(getForm().getFormConfiguration().getFormId());
        setLocale(getForm().getRequestContext().getLocale());
        setExportConfiguration(getForm().getFormConfiguration().getCsvExportConfiguration());
    }

    /**
     * Creates an instance that is not backed by a form but still offers 
     * export functionality via.<p>
     * 
     * @param cms Needed to read the resource for the UUID
     * @param formId The form id of the data entries that should be exported
     */
    public CmsCsvExportBean(final CmsObject cms, String formId) {

        init(cms);
        setFormId(formId);
        reconstructPossibleExportConfigurationAndLocale();

    }

    /**
     * Common initialization for all constructors.
     * @param cms The cms object
     */
    private void init(final CmsObject cms) {

        m_cms = cms;
        m_startTime = new Date(0);
        m_endTime = new Date(Long.MAX_VALUE);
        m_module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
    }

    /**
     * Returns the csv export file content.<p> 
     * 
     * @return the csv export file content
     * 
     * @throws SQLException if sth goes wrong 
     */
    public String exportData() throws SQLException {

        StringBuffer result = new StringBuffer();

        // add the column headers
        result = addColumnHeaders(result);

        // add the data entries
        result = addDataEntries(result);
        return result.toString();
    }

    /**
     * @param formId form id that will be used for database access
     */
    private void setFormId(final String formId) {

        m_formId = formId;

    }

    /**
     * @param locale locale used for database access
     */
    private void setLocale(final Locale locale) {

        m_locale = locale;

    }

    /**
     * @param csv StringBuffer where to write the headers in
     * @return The argument StringBuffer with headers added
     * @throws SQLException propagated from {@link #getColumnNames()}
     */
    private StringBuffer addColumnHeaders(final StringBuffer csv) throws SQLException {

        // loop 1 - write the headers:
        String csvDelimiter = getCsvDelimiter();
        List<String> columnNames = getColumnNames();
        Iterator<String> itColumns = columnNames.iterator();
        boolean hasPrevious = false;
        while (itColumns.hasNext()) {
            if (hasPrevious) {
                csv.append(csvDelimiter);
            } else {
                hasPrevious = true;
            }
            String columnName = itColumns.next();
            // skip empty columns (previous versions saved CmsEmptyField with empty values which will not be deleted):
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(columnName)) {
                columnName = escapeExcelCsv(columnName);
                csv.append(columnName);
            }
        }
        csv.append("\r\n");
        return csv;
    }

    /**
     * @param csv StringBuffer where to write the data entries in
     * @return The argument StringBuffer with data entries added
     * @throws SQLException propagated from {@link #getColumnNames()}
     */
    private StringBuffer addDataEntries(final StringBuffer csv) throws SQLException {

        // get the entries
        List<CmsFormDataBean> dataEntries = CmsFormDataAccess.getInstance().readForms(
            m_formId,
            getStartTime().getTime(),
            getEndTime().getTime());
        // loop 2 - write the data:
        Iterator<CmsFormDataBean> itRows = dataEntries.iterator();
        String path;
        CmsUUID uuid = null;
        while (itRows.hasNext()) {
            CmsFormDataBean row = itRows.next();
            // create an entry for each column, even if some rows (data sets) 
            // do not contain the field value because it was 
            // a) not entered 
            // b) the form was changed in structure over time 
            // c) developer errors,  hw /sw problems... 
            uuid = row.getResourceId();
            Iterator<String> itColumns = getColumnNames().iterator();
            boolean hasPrevious = false; // useful to handle possible empty colum as last column.
            while (itColumns.hasNext()) {
                String columnName = itColumns.next();
                // skip empty columns (previous versions saved CmsEmptyField with empty values which will not be deleted):
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(columnName)) {
                    // add column separator if previous column is present
                    if (hasPrevious) {
                        csv.append(getCsvDelimiter());
                    } else { // tell that this column will be the previous of the next column
                        hasPrevious = true;
                    }
                    // handle default columns
                    if (columnName.equals(DEFAULT_COLUMN_NAME_CREATION_DATE)) {
                        Date creationDate = new Date(row.getDateCreated());
                        DateFormat dateTimeFormat = getDateTimeFormat();
                        if (dateTimeFormat == null) {
                            csv.append(creationDate);
                        } else {
                            csv.append(dateTimeFormat.format(creationDate));
                        }
                    } else if (columnName.equals(DEFAULT_COLUMN_NAME_RESOURCE_PATH)) {
                        try {
                            path = m_cms.readResource(uuid).getRootPath();
                        } catch (Exception e) {
                            path = uuid.toString();
                        }
                        csv.append(path);
                    } else if (columnName.equals(DEFAULT_COLUMN_NAME_RESOURCE_UUID)) {
                        csv.append(String.valueOf(uuid));
                        //next: handle custom columns
                    } else {
                        String value = row.getFieldValue(columnName);
                        if (value != null) {
                            value = transformLineSeparators(value);
                            value = escapeExcelCsv(value);
                            csv.append(value);
                        }
                    }
                }
            }
            csv.append("\r\n");
        }
        return csv;
    }

    /**
     * @return The date format specified as module parameter
     */
    private DateFormat getDateTimeFormat() {

        String formatString = m_module.getParameter(CmsForm.MODULE_PARAM_EXPORT_TIMEFORMAT);
        DateFormat dateFormat = null;
        if (CmsStringUtil.isNotEmpty(formatString)) {
            try {
                dateFormat = new SimpleDateFormat(formatString);
            } catch (IllegalArgumentException iae) {
                LOG.warn(Messages.get().getBundle().key(
                    Messages.LOG_WARN_EXPORT_DATEFORMAT_ILLEGAL_2,
                    new Object[] {CmsForm.MODULE_PARAM_EXPORT_TIMEFORMAT, formatString}));
            }
        }
        return dateFormat;
    }

    /**
     * 
     * 
     * @return The CSV delimiter used to separate values
     */
    private String getCsvDelimiter() {

        String delimiter = EXCEL_DEFAULT_CSV_DELMITER;
        String configuredDelimiter = m_module.getParameter(CmsForm.MODULE_PARAM_CSV_DELIMITER);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(configuredDelimiter)) {

            delimiter = configuredDelimiter.trim();
        }
        return delimiter;

    }

    /**
     * @return flag to determine if numbers should be marked as Strings by prepending a <code>=</code>
     */
    private boolean isNumbersAsStrings() {

        if (m_numbersAsStrings != null) {
            return m_numbersAsStrings.booleanValue();
        }
        String nasParam = m_module.getParameter(CmsForm.MODULE_PARAM_EXPORT_NUMBERASSTRING, CmsStringUtil.FALSE);
        return Boolean.valueOf(nasParam).booleanValue();
    }

    /**
     * @return The names of the columns to export in the configured order
     * @throws SQLException propagated from {@link com.alkacon.opencms.v8.formgenerator.database.CmsFormDataAccess#readFormFieldNames(String, long, long)}
     */
    private List<String> getColumnNames() throws SQLException {

        List<String> columnNames, allColumnNames;
        List<String> fieldColumnNames = CmsFormDataAccess.getInstance().readFormFieldNames(
            m_formId,
            getStartTime().getTime(),
            getEndTime().getTime());
        Collections.sort(fieldColumnNames, Collator.getInstance(m_locale));
        allColumnNames = getDefaultColumnNames();
        allColumnNames.addAll(fieldColumnNames);

        List<String> exportConfiguration = getExportConfiguration();
        if (exportConfiguration != null) {
            columnNames = exportConfiguration;
            if (!allColumnNames.containsAll(columnNames)) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_EXPORT_NONEXISTING_COLUMNS_CONFIGURED_0));
            }
        } else {
            columnNames = allColumnNames;
        }
        return columnNames;
    }

    /**
     * @param configuration pipe-separated column names, possibly containing placeholders for default columns
     */
    private void setExportConfiguration(final String configuration) {

        if (configuration == null) {
            m_exportConfiguration = null;
        } else {
            List<String> conf = Arrays.asList(configuration.split("\\|"));
            m_exportConfiguration = getWithPlaceholdersReplacedByNames(conf);
        }
    }

    /**
     * @return if set, an export configuration setting the column layout for the csv file
     */
    private List<String> getExportConfiguration() {

        return m_exportConfiguration;
    }

    /**
     * Sets m_locale and m_exportConfiguration by reconstructing the values from the form id.
     * Use only if no form handler is given.
     */
    private void reconstructPossibleExportConfigurationAndLocale() {

        String exportConfiguration = null;
        if (m_formId != null) {
            try {
                List<CmsFormDataBean> forms = CmsFormDataAccess.getInstance().readForms(
                    m_formId,
                    getStartTime().getTime(),
                    getEndTime().getTime());
                if (!forms.isEmpty()) {
                    CmsUUID pageStructureId = forms.get(0).getResourceId();
                    if (!pageStructureId.equals(CmsUUID.getNullUUID())) {
                        try {
                            CmsResource pageResource = m_cms.readResource(pageStructureId);
                            if (pageResource != null) {
                                setLocale(CmsLocaleManager.getMainLocale(m_cms, pageResource));
                                CmsXmlContainerPage xmlContainerPage = CmsXmlContainerPageFactory.unmarshal(
                                    m_cms,
                                    pageResource);
                                if (xmlContainerPage != null) {
                                    CmsContainerPageBean containerPage = xmlContainerPage.getContainerPage(m_cms);
                                    if (containerPage != null) {
                                        Iterator<CmsContainerElementBean> pageElements = containerPage.getElements().iterator();
                                        while (pageElements.hasNext()) {
                                            CmsUUID elemResourceId = pageElements.next().getId();
                                            CmsResource elem = m_cms.readResource(elemResourceId);
                                            if (elem.getTypeId() == OpenCms.getResourceManager().getResourceType(
                                                CmsForm.TYPE_NAME).getTypeId()) {
                                                CmsJspContentAccessBean config = new CmsJspContentAccessBean(
                                                    m_cms,
                                                    m_locale,
                                                    elem);
                                                if (config.getHasValue().get(
                                                    CmsForm.NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION).booleanValue()) {
                                                    exportConfiguration = config.getValue().get(
                                                        CmsForm.NODE_OPTIONAL_CSV_EXPORT_CONFIGURATION).getStringValue();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (CmsException e) {
                            LOG.warn(Messages.get().getBundle().key(
                                Messages.LOG_WARN_EXPORT_RESOURCE_NOT_PRESENT_1,
                                e.toString()));
                        }
                    }
                }
            } catch (SQLException e) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_EXPORT_RESOURCE_NOT_PRESENT_1, e.toString()));
            }
        }

        setExportConfiguration(exportConfiguration);
    }

    /**
     * @param columnPlaceholderNames the column's names with placeholders
     * @return a copy of the argument with placeholders replaced by names
     */
    private List<String> getWithPlaceholdersReplacedByNames(final List<String> columnPlaceholderNames) {

        List<String> result = new LinkedList<String>();
        for (String entry : columnPlaceholderNames) {
            if (entry.equals(DEFAULT_COLUMN_PLACEHOLDER_CREATION_DATE)) {
                result.add(DEFAULT_COLUMN_NAME_CREATION_DATE);
            } else if (entry.equals(DEFAULT_COLUMN_PLACEHOLDER_RESOURCE_PATH)) {
                result.add(DEFAULT_COLUMN_NAME_RESOURCE_PATH);
            } else if (entry.equals(DEFAULT_COLUMN_PLACEHOLDER_RESOURCE_UUID)) {
                result.add(DEFAULT_COLUMN_NAME_RESOURCE_UUID);
            } else {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * @return The names of the default columns
     */
    private List<String> getDefaultColumnNames() {

        List<String> defaultColumns = new LinkedList<String>();
        defaultColumns.add(DEFAULT_COLUMN_NAME_CREATION_DATE);
        defaultColumns.add(DEFAULT_COLUMN_NAME_RESOURCE_PATH);
        defaultColumns.add(DEFAULT_COLUMN_NAME_RESOURCE_UUID);
        return defaultColumns;
    }

    /**
     * @param value the String where the line separator should be transformed
     * @return the argument with transformed line separators
     */
    private String transformLineSeparators(String value) {

        if (isWindowsLineSeparator()) {
            value = transformWindowsLineseparator(value);
        } else if (isUnixLineSeparator()) {
            value = transformUnixLineseparator(value);
        }
        return value;
    }

    /**
     * @return flag indicating if a windows line separator should be used
     */
    private boolean isWindowsLineSeparator() {

        String lineSeparatorParam = getLineSeparatorParam();
        if (lineSeparatorParam.equals(CmsForm.MODULE_PARAMVALUE_EXPORTLINESEPARATOR_WINDOWS)) {
            return true;
        }
        return false;
    }

    /**
     * @return flag indicating if a unix line separator should be used
     */
    private boolean isUnixLineSeparator() {

        String lineSeparatorParam = getLineSeparatorParam();
        if (lineSeparatorParam.equals(CmsForm.MODULE_PARAMVALUE_EXPORTLINESEPARATOR_UNIX)
            || lineSeparatorParam.equals(CmsForm.MODULE_PARAMVALUE_EXPORTLINESEPARATOR_EXCEL)) {
            return true;
        }
        return false;
    }

    /**
     * @return get the configured line separator for the export
     */
    private String getLineSeparatorParam() {

        String lineSeparatorParam;
        if (m_lineSeparatorParam != null) {
            lineSeparatorParam = m_lineSeparatorParam;
        } else {
            lineSeparatorParam = m_module.getParameter(CmsForm.MODULE_PARAM_EXPORTLINESEPARATOR);
            lineSeparatorParam = lineSeparatorParam.trim();
        }
        return lineSeparatorParam;
    }

    /**
     * Returns the endTime.<p>
     *
     * @return the endTime
     */
    public Date getEndTime() {

        return m_endTime;
    }

    /**
     * Returns the form handler.<p>
     *
     * @return the form handler
     */
    public CmsFormHandler getForm() {

        return m_formHandler;
    }

    /**
     * Returns the startTime.<p>
     *
     * @return the startTime
     */
    public Date getStartTime() {

        return m_startTime;
    }

    /**
     * Sets the endTime.<p>
     *
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {

        m_endTime = endTime;
    }

    /**
     * Sets the startTime.<p>
     *
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {

        m_startTime = startTime;
    }

    /** 
     * Escapes CSV values for Excel.<p> 
     * 
     * @param value the value to escape 
     * @return the escaped Excel value
     */
    private String escapeExcelCsv(final String value) {

        String result = value;
        StringBuffer buffer = new StringBuffer(value.length() + 8);
        // support for Microsoft Excel: If Excel detects numbers, it reformats the numbers 
        // (stealing leading zeros or displaying large numbers in +E syntax:
        if (isNumbersAsStrings()) {
            boolean isNumber = false;
            try {
                Double.valueOf(result);
                isNumber = true;
            } catch (Exception e) {
                // this is no double value
            }
            if (!isNumber) {
                try {
                    Long.valueOf(result);
                    isNumber = true;
                } catch (Exception e) {
                    // this is no long value
                }
            }

            if (isNumber) {
                buffer.append("=");
            }
        }
        buffer.append("\"");
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            // escape double quote escape delimiter within value: 
            if ('"' == chars[i]) {
                buffer.append("\"");
            }
            buffer.append(chars[i]);
        }
        buffer.append("\"");
        result = buffer.toString();
        return result;
    }

    /**
     * Replaces all "\r\n" to "\n".<p>
     * 
     * @param value the value to transform 
     * 
     * @return the input with Unix line separators
     */
    private String transformUnixLineseparator(String value) {

        return value.replaceAll("\r\n", "\n");
    }

    /**
     * Replaces all "\n" to "\r\n".<p>
     * 
     * @param value the value to transform 
     * 
     * @return the input with windows line separators
     */
    private String transformWindowsLineseparator(String value) {

        return value.replaceAll("\n", "\r\n");

    }

}
