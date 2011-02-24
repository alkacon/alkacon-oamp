/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.excelimport/src/com/alkacon/opencms/excelimport/CmsExcelImport.java,v $
 * Date   : $Date: 2011/02/24 15:33:36 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.excelimport;

import org.opencms.db.CmsPublishList;
import org.opencms.db.CmsResourceState;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.CmsResourceTypeXmlContent;
import org.opencms.loader.CmsLoaderException;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.report.A_CmsReportThread;
import org.opencms.report.I_CmsReport;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlDateTimeValue;
import org.opencms.xml.types.CmsXmlHtmlValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;

/**
 * Imports an excel file and creates XML contents. Therefore the configuration file for import is located an validated.
 * It is checked if an existing XML content belongs to an excel record. If yes, so this XML content is updated if 
 * excel record changed. Otherwise a new XML content is created.<p>
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 7.5.0
 */
public class CmsExcelImport extends A_CmsReportThread {

    /** The name of the current name.*/
    public static final String MODULE_NAME = "com.alkacon.opencms.excelimport";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsExcelImport.class);

    /** The upload object. */
    private CmsResourceExcelImport m_cmsImport;

    /** The interface to get additional configuration values. */
    private I_CmsVfsSettings m_configParas;

    /** The number of empty records. */
    private int m_emptyRecords;

    /** The number of not successful imported records. */
    private int m_errImportedRecords;

    /** The number of not successful updated records. */
    private int m_errUpdatedRecords;

    /** The list with found resources to excel file. */
    private List m_foundResources = new ArrayList();

    /** The number of successful imported records. */
    private int m_importedRecords;

    /** True, if there was an initialization error, otherwise false. */
    private boolean m_initError;

    /** The number of invalid records. */
    private int m_invalidRecords;

    /** The locale. */
    private Locale m_locale;

    /** The resources to publish.*/
    private List m_publishList = new ArrayList();

    /** The number of unchanged records. */
    private int m_unchangedRecords;

    /** The number of successful updated records. */
    private int m_updatedRecords;

    /**
     * Constructor: Creates an import excel file thread.<p>
     * 
     * @param cmsObject the current CmsObject action element
     * @param cmsImport the import object
     */
    public CmsExcelImport(CmsObject cmsObject, CmsResourceExcelImport cmsImport) {

        super(cmsObject, "");
        initHtmlReport(getCms().getRequestContext().getLocale());
        m_cmsImport = cmsImport;
    }

    /**
     * Gets dialog text for security dialog. This dialog can become made on/off with module parameter.
     * It includes informations about errors in configurations and actions which will become executed.<p>
     * 
     * @return dialog text content for security dialog
     */
    public String getDialogText() {

        StringBuffer buffer = new StringBuffer(4096);
        Locale locale = getCms().getRequestContext().getLocale();
        // configuration path
        buffer.append(Messages.get().container(Messages.LOG_CONFIG_PATH_1, getPathFromConfigurationFiles()).key(locale)
            + "<br>");
        // excel name
        buffer.append(Messages.get().container(Messages.LOG_EXCEL_NAME_1, m_cmsImport.getExcelName()).key(locale)
            + "<br>");
        // check configuration path 
        if (!checkConfigPath(getPathFromConfigurationFiles())) {
            // invalid config path
            buffer.append(Messages.get().container(Messages.LOG_ERR_INVALID_CONFIGPATH_0).key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }

        // read excel file
        CmsExcelContent cmsExcelContent = new CmsExcelContent();
        cmsExcelContent.readExcelFile(getCms(), m_cmsImport.getExcelName(), m_cmsImport.getParamExcelContent());
        if ((cmsExcelContent.getNumberOfRecords() > 0)
            && (CmsStringUtil.isNotEmpty(cmsExcelContent.getCategoryProperty()))) {
            // excel properties
            buffer.append(Messages.get().container(
                Messages.LOG_EXCEL_CATEGORY_PROPERTY_1,
                cmsExcelContent.getCategoryProperty()).key(locale)
                + "<br>");
            buffer.append(Messages.get().container(
                Messages.LOG_EXCEL_FOLDER_PROPERTY_1,
                cmsExcelContent.getFolderProperty()).key(locale)
                + "<br>");
        } else {
            // empty excel file
            buffer.append(Messages.get().container(Messages.LOG_ERR_EMPTY_EXCEL_1, "").key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }

        // get configuration file to excel file
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration = getConfigurationFileToExcelFile(cmsExcelContent);
        if (cmsExcelXmlConfiguration != null) {
            buffer.append(Messages.get().container(
                Messages.LOG_CONFIG_NAME_1,
                cmsExcelXmlConfiguration.getConfigFileName()).key(locale)
                + "<br>");
        } else {
            // no configuration file
            buffer.append(Messages.get().container(Messages.LOG_ERR_NO_CONFIGFILE_0, "").key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }

        // check for valid configuration file
        boolean validConfig = isValidConfiguration(cmsExcelXmlConfiguration);
        if (!validConfig) {
            // invalid configuration file
            buffer.append(Messages.get().container(Messages.LOG_INVALID_CONFIGFILE_1, "").key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }

        // check for valid excel file, all mandatory rows must be available
        boolean validExcel = isValidExcel(cmsExcelContent, cmsExcelXmlConfiguration);
        if (!validExcel) {
            // invalid excel file
            buffer.append(Messages.get().container(Messages.LOG_INVALID_EXCELFILE_1, "").key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }

        // get path to handle existing XML contents in
        String interfaceName = cmsExcelXmlConfiguration.getInterfaceName();
        try {
            if (CmsStringUtil.isNotEmpty(interfaceName)) {
                m_configParas = getObject(interfaceName);
            } else {
                m_configParas = new CmsDefaultVfsSettings();
            }
        } catch (Exception e) {
            // no interface
            buffer.append(Messages.get().container(Messages.LOG_NO_INTERFACE_1, "").key(locale) + "<br><br>");
            buffer.append(Messages.get().container(Messages.GUI_DIALOG_FAIL_QUESTION_0).key(locale));
            return buffer.toString();
        }
        // get dialog text from interface
        buffer.append(getDialogText(
            getCms(),
            m_cmsImport.getFolder(),
            m_cmsImport.getExcelName(),
            cmsExcelXmlConfiguration.getResourceType(),
            m_cmsImport.getPublish(),
            cmsExcelContent));

        return buffer.toString();
    }

    /**
     * @see org.opencms.report.A_CmsReportThread#getReportUpdate()
     */
    public String getReportUpdate() {

        return getReport().getReportUpdate();
    }

    /**
     * Checks for valid settings in excel file and configuration file. It is proved if excel file is not
     * empty and if there is set the excel property with OpenCms resource type. It is also proved
     * if in configuration file is set a resource type and if there is at least one mapping.<p>
     * 
     * @return true, if valid settings, otherwise false.
     */
    public boolean isValid() {

        // check config path
        if (!checkConfigPath(getPathFromConfigurationFiles())) {
            // invalid config path
            return false;
        }

        // read excel file
        CmsExcelContent cmsExcelContent = new CmsExcelContent();
        cmsExcelContent.readExcelFile(getCms(), m_cmsImport.getExcelName(), m_cmsImport.getParamExcelContent());
        if (!((cmsExcelContent.getNumberOfRecords() > 0) && (CmsStringUtil.isNotEmpty(cmsExcelContent.getCategoryProperty())))) {
            // empty excel file
            return false;
        }

        // get configuration file to excel file
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration = getConfigurationFileToExcelFile(cmsExcelContent);
        if (cmsExcelXmlConfiguration == null) {
            // no configuration file
            return false;
        }

        // check for valid configuration file
        boolean validConfig = isValidConfiguration(cmsExcelXmlConfiguration);
        if (!validConfig) {
            // invalid configuration file
            return false;
        }

        // check for valid excel file, all mandatory rows must be available
        boolean validExcel = isValidExcel(cmsExcelContent, cmsExcelXmlConfiguration);
        if (!validExcel) {
            // invalid excel file
            return false;
        }

        // get path to handle existing XML contents in
        String interfaceName = cmsExcelXmlConfiguration.getInterfaceName();
        try {
            if (CmsStringUtil.isNotEmpty(interfaceName)) {
                m_configParas = getObject(interfaceName);
            } else {
                m_configParas = new CmsDefaultVfsSettings();
            }
        } catch (Exception e) {
            // get dialog text from interface
            return false;
        }
        // no interface
        return true;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        I_CmsReport report = getReport();
        report.println(Messages.get().container(Messages.LOG_START_THREAD_EXCELIMPORT_0), I_CmsReport.FORMAT_HEADLINE);
        if ((getCms() != null) && (m_cmsImport != null)) {
            // read current user project
            CmsProject currentProject = getCms().getRequestContext().currentProject();
            try {
                // sets import project
                setProject(report, currentProject);
                // import excel file
                importExcelFile(report);
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e);
                }
            } finally {
                // switch back to user project
                getCms().getRequestContext().setCurrentProject(currentProject);
            }
        } else {
            report.println(Messages.get().container(Messages.LOG_UPLOAD_FAILED_0), I_CmsReport.FORMAT_ERROR);
        }

        // report status
        report.println();
        report.print(
            Messages.get().container(Messages.LOG_STATUS_0, new Integer(m_unchangedRecords)),
            I_CmsReport.FORMAT_NOTE);
        if ((m_errImportedRecords > 0) || (m_errUpdatedRecords > 0) || (m_invalidRecords > 0) || m_initError) {
            // failed   
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
        } else {
            // success
            report.println(
                org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                I_CmsReport.FORMAT_OK);
        }

        report.println();
        report.println(Messages.get().container(Messages.LOG_END_THREAD_EXCELIMPORT_0), I_CmsReport.FORMAT_HEADLINE);
    }

    /**
     * Check if configuration path is valid in OpenCms.<p>
     * 
     * @param configPath configuration path
     * 
     * @return true, if configuration path exists, otherwise false.
     */
    private boolean checkConfigPath(String configPath) {

        try {
            // try to read path in OpenCms
            getCms().readFolder(configPath);
        } catch (CmsException e) {
            // there was an error, so the path does not exist in OpenCms
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if excel record belongs to any existing XML content.
     * For every excel record is looped over all existing XML contents
     * and it is checked if excel record belongs to any from them.
     * Excel record can only belong to one existing XML content. After
     * the first match the prove for this excel record is canceled.<p>
     * 
     * @param allXmlContents list with all existing XML contents
     * @param cmsExcelContent excel content 
     * @param cmsExcelXmlConfiguration configuration content
     * @param pathXmlContents path to XML contents
     * @param recordCounter current excel record number
     * @param report report for user
     * 
     * @return true, if existing XML content was found, otherwise false
     */
    private boolean checkExcelRecordAgainstAllXmlContents(
        List allXmlContents,
        CmsExcelContent cmsExcelContent,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        String pathXmlContents,
        int recordCounter,
        I_CmsReport report) {

        int rowsToevaluate = getNumberOfColumnsToEvaluate(cmsExcelContent, cmsExcelXmlConfiguration);
        Map currentRecord = cmsExcelContent.getRecord(recordCounter);

        if (allXmlContents != null) {
            // loop over all existing XML contents
            Iterator iterXmlContents = allXmlContents.iterator();
            while (iterXmlContents.hasNext()) {
                CmsResource cmsResource = (CmsResource)iterXmlContents.next();
                // resource did not change already in this import
                if (!m_foundResources.contains(cmsResource.getResourceId().getStringValue())) {
                    boolean isXml = false;
                    try {
                        isXml = CmsResourceTypeXmlContent.isXmlContent(cmsResource);
                    } catch (Exception e) {
                        // no XML content
                    }
                    if (isXml) {
                        try {
                            CmsFile cmsFile = getCms().readFile(cmsResource);
                            CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(getCms(), cmsFile);
                            // check current excel record against one XML content
                            boolean xmlContentFound = checkExcelRecordAgainstSingleXmlContent(
                                cmsResource,
                                cmsFile,
                                xmlContent,
                                cmsExcelContent,
                                pathXmlContents,
                                recordCounter,
                                cmsExcelXmlConfiguration,
                                currentRecord,
                                rowsToevaluate,
                                report);
                            if (xmlContentFound) {
                                // excel record is in existing XML content
                                m_foundResources.add(cmsResource.getResourceId().getStringValue());
                                return true;
                            }

                        } catch (CmsException e) {
                            if (LOG.isErrorEnabled()) {
                                LOG.error(e);
                            }
                        } catch (RuntimeException e) {
                            // to catch "could not unmarshal XML content"
                            if (LOG.isErrorEnabled()) {
                                LOG.error(e);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check single excel record against single XML content. This methode includes logic
     * to prove if excel record is in existing XML content included already.<p> 
     * If all column contents are the same in the mapped XML content, so the excel record
     * is unchanged and nothing is to do with the XML content.<p> 
     * Every mapped column in excel record has a weight. All weights belongin to all unchanged 
     * column contents are added. If the sum from this weights is higher or equal than another
     * configured weight, so the excel record belongs to this XML content, but changed.
     * In this case the XML content is updated with the excel column values.<p>
     * If the sum of weight from unchanged fields is lower than the other configured weight
     * than the excel record does not belong to this XML content.<p>
     * 
     * @param cmsResource current CmsResource 
     * @param cmsFile current CmsFile
     * @param xmlContent current XML content
     * @param currentRecord current record data as map
     * @param cmsExcelContent excel object
     * @param pathXmlContents path to XML contents
     * @param numberExcelColumn number of excel columns
     * @param cmsExcelXmlConfiguration configuration object
     * @param rowsToevaluate number of rows in excel to evaluate
     * @param report report object
     * 
     * @return true, if existing XML content to excel record was found, otherwise false.
     */
    private boolean checkExcelRecordAgainstSingleXmlContent(
        CmsResource cmsResource,
        CmsFile cmsFile,
        CmsXmlContent xmlContent,
        CmsExcelContent cmsExcelContent,
        String pathXmlContents,
        int numberExcelColumn,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        Map currentRecord,
        int rowsToevaluate,
        I_CmsReport report) {

        CmsXmlContent xmlContentCopy = null;
        try {
            xmlContentCopy = CmsXmlContentFactory.unmarshal(getCms(), cmsFile);
        } catch (CmsException e) {
            // do nothing, because the original XML content exists, so this error should not happen...
        }

        // sum of weight for not changed fields
        int notChangedWeight = 0;
        // number of fields with unchanged fields
        int numberSameContent = 0;
        // loop over all columns from row
        Iterator iterCol = currentRecord.keySet().iterator();
        while (iterCol.hasNext()) {
            // get user row name
            String userColName = (String)iterCol.next();
            if (CmsStringUtil.isNotEmpty(userColName)) {
                // get XML tag name
                String xmlTagName = cmsExcelXmlConfiguration.getXmlTagNameToExcelColumnName(userColName);
                if (CmsStringUtil.isNotEmpty(xmlTagName)) {
                    // get XML content
                    try {
                        I_CmsXmlContentValue xmlTagContent = xmlContent.getValue(xmlTagName, m_locale);

                        // get excel content
                        String excelContent = (String)currentRecord.get(userColName);
                        if (CmsStringUtil.isEmpty(excelContent)) {
                            excelContent = "";
                        }
                        // get weight
                        int weight = cmsExcelXmlConfiguration.getWeightToExcelColumnName(userColName);

                        if ((xmlTagContent != null) && (xmlContentCopy != null)) {
                            String xmlTagContentValue = xmlTagContent.getStringValue(getCms());

                            // use a XML tag content copy to get value after setting in XML content
                            I_CmsXmlContentValue xmlTagContentCopy = xmlContentCopy.getValue(xmlTagName, m_locale);
                            xmlTagContentCopy.setStringValue(getCms(), excelContent);
                            String xmlTagContentCopyValue = xmlTagContentCopy.getStringValue(getCms());
                            // default value for XML tag to prove if empty excel column 
                            String xmlTagContentCopyValueDefault = xmlTagContentCopy.getDefault(m_locale);

                            // compare excel content and XML tag content
                            if (xmlTagContentValue.equals(xmlTagContentCopyValue)) {
                                // excel content and XML tag content is same
                                notChangedWeight += weight;
                                numberSameContent += 1;
                            } else if (CmsStringUtil.isEmpty(excelContent)
                                && (xmlTagContentValue.equals(xmlTagContentCopyValueDefault))) {
                                // empty excel content, is default value set in XML content so it is also the same
                                notChangedWeight += weight;
                                numberSameContent += 1;
                            } else if (CmsStringUtil.isEmpty(excelContent) && xmlTagContentValue.equals("(none)")) {
                                // select box with no selected value after user changed XML content
                                notChangedWeight += weight;
                                numberSameContent += 1;
                            } else {
                                // prove for data type CmsHtml 
                                if (xmlContent.getContentDefinition().getSchemaType(xmlTagName).getTypeName().equals(
                                    CmsXmlHtmlValue.TYPE_NAME)
                                    && xmlTagContentValue.startsWith("<p>")
                                    && xmlTagContentValue.endsWith("</p>")) {
                                    String valueWithoutHtml = xmlTagContentValue.substring(
                                        3,
                                        xmlTagContentValue.length() - 4);
                                    if (valueWithoutHtml.equals(excelContent)) {
                                        // after user changed XML content, filled CmsXmlHtmlValues can become surrounded with html <p> tags
                                        notChangedWeight += weight;
                                        numberSameContent += 1;
                                    }
                                } else if (CmsStringUtil.isEmpty(excelContent)
                                    && xmlContent.getContentDefinition().getSchemaType(xmlTagName).getTypeName().equals(
                                        CmsXmlHtmlValue.TYPE_NAME)
                                    && xmlTagContentValue.equals("<br />")) {
                                    // after user changed XML content, empty CmsXmlHtmlValues can be <br /> tag
                                    notChangedWeight += weight;
                                    numberSameContent += 1;
                                }
                            }
                        } else if ((xmlTagContent == null) && CmsStringUtil.isEmpty(excelContent)) {
                            // empty excel content and optional tag is disabled, so it is the same
                            notChangedWeight += weight;
                            numberSameContent += 1;
                        }
                    } catch (Exception e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(e);
                        }

                        return false;
                    }
                }
            }
        }
        // evaluation
        if (numberSameContent == rowsToevaluate) {
            // not changed record ---> do nothing
            report.print(Messages.get().container(
                Messages.LOG_UNCHANGED_XML_2,
                new Integer(numberExcelColumn + 1).toString(),
                getCms().getSitePath(cmsFile)), I_CmsReport.FORMAT_NOTE);
            report.println(
                org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                I_CmsReport.FORMAT_OK);
            m_unchangedRecords += 1;
            return true;
        } else if (notChangedWeight >= cmsExcelXmlConfiguration.getMinWeight()) {
            // changed record, so update it 
            report.print(Messages.get().container(
                Messages.LOG_UPDATE_XML_2,
                new Integer(numberExcelColumn + 1).toString(),
                getCms().getSitePath(cmsFile)), I_CmsReport.FORMAT_NOTE);

            //lock file
            boolean locked = false;
            try {
                locked = lockResource(cmsFile);
            } catch (CmsException e) {
                report.print(Messages.get().container(Messages.LOG_ERR_NO_LOCK_0), I_CmsReport.FORMAT_NOTE);
                report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
                m_errUpdatedRecords += 1;
                return true;
            }
            // locking file was successful
            if (locked) {
                // update XML content
                if (setValuesInXmlContent(
                    cmsResource,
                    cmsFile,
                    xmlContent,
                    currentRecord,
                    cmsExcelXmlConfiguration,
                    true,
                    report)) {
                    // updating XML content was successful
                    report.println(
                        org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                        I_CmsReport.FORMAT_OK);
                    m_updatedRecords += 1;
                } else {
                    // updating XML content failed
                    report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
                    m_errUpdatedRecords += 1;
                }
                return true;
            } else {
                // locking resource failed
                report.print(Messages.get().container(Messages.LOG_ERR_NO_LOCK_0), I_CmsReport.FORMAT_NOTE);
                report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
                m_errUpdatedRecords += 1;
                return true;
            }
        } else {
            // XML content is too different from excel content
            return false;
        }
    }

    /**
     * Creates a new XML content from given OpenCms resource type and contents from current excel record.<p>
     * 
     * @param pathXmlContents path to XML contents
     * @param resourceType resource type to create
     * @param newFileName new filename
     * @param currentRecord current record from excel
     * @param cmsExcelXmlConfiguration configuration
     * @param report report 
     * 
     * @return true, if creating new XML content was successful, otherwise false.
     */
    private boolean createNewXmlContentFile(
        String pathXmlContents,
        String resourceType,
        String newFileName,
        Map currentRecord,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        I_CmsReport report) {

        int resourceTypeInt = 0;
        try {
            resourceTypeInt = OpenCms.getResourceManager().getResourceType(resourceType).getTypeId();
        } catch (CmsLoaderException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
            return false;
        }
        String completeNewFileName = "";
        if (resourceTypeInt > 0) {
            try {
                completeNewFileName = pathXmlContents.concat(newFileName);
                getCms().createResource(completeNewFileName, resourceTypeInt);
                CmsFile cmsFile = getCms().readFile(completeNewFileName);
                CmsResource cmsResource = getCms().readResource(completeNewFileName);
                CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(getCms(), cmsFile);
                // set values from excel file into file
                if (setValuesInXmlContent(
                    cmsResource,
                    cmsFile,
                    xmlContent,
                    currentRecord,
                    cmsExcelXmlConfiguration,
                    false,
                    report)) {
                    return true;
                } else {
                    getCms().deleteResource(completeNewFileName, CmsResource.DELETE_PRESERVE_SIBLINGS);
                }
            } catch (CmsException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e);
                }
                return false;
            }
        }
        return false;
    }

    /**
     * In this method is looped over all excel records and it is proved if an excel record
     * belongs to an existing XML content. If an excel record does not belong to an existing
     * XML content so a new XML content is created.<p>
     * 
     * @param cmsExcelContent excel content object
     * @param cmsExcelXmlConfiguration configuration object
     * @param pathXmlContents path to XML contents
     * @param report report object
     */
    private void evaluateExcelRecords(
        CmsExcelContent cmsExcelContent,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        String pathXmlContents,
        I_CmsReport report) {

        // read all existing XML contents
        List allXmlContents = null;
        try {
            allXmlContents = getCms().readResources(pathXmlContents, CmsResourceFilter.IGNORE_EXPIRATION, false);
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
        }

        // loop over all excel records
        for (int recordCounter = 1; recordCounter <= cmsExcelContent.getNumberOfRecords(); recordCounter++) {
            // check for not empty excel record
            boolean emptyRecord = cmsExcelContent.isEmptyRecord(recordCounter);
            if (emptyRecord) {
                m_emptyRecords += 1;
            } else {
                // check for valid excel content -> all mandatory fields have to become filled
                boolean validExcelRecord = isValidExcelRecord(cmsExcelContent, cmsExcelXmlConfiguration, recordCounter);
                if (validExcelRecord) {
                    // loop over all existing XML contents if there exists a XML content belonging to current excel record
                    boolean xmlContentFound = checkExcelRecordAgainstAllXmlContents(
                        allXmlContents,
                        cmsExcelContent,
                        cmsExcelXmlConfiguration,
                        pathXmlContents,
                        recordCounter,
                        report);
                    if (!xmlContentFound) {
                        // no existing XML content found -> create new XML content
                        String newFileName = m_configParas.getNewFileName(
                            getCms(),
                            m_cmsImport.getFolder(),
                            cmsExcelXmlConfiguration.getResourceType(),
                            cmsExcelContent);
                        // output to the report
                        report.print(Messages.get().container(
                            Messages.LOG_CREATE_XML_2,
                            String.valueOf(recordCounter + 1),
                            pathXmlContents.concat(newFileName)), I_CmsReport.FORMAT_NOTE);
                        // create new XML content
                        if (createNewXmlContentFile(
                            pathXmlContents,
                            cmsExcelXmlConfiguration.getResourceType(),
                            newFileName,
                            cmsExcelContent.getRecord(recordCounter),
                            cmsExcelXmlConfiguration,
                            report)) {
                            // creating XML content was successful
                            report.println(org.opencms.report.Messages.get().container(
                                org.opencms.report.Messages.RPT_OK_0), I_CmsReport.FORMAT_OK);
                            m_importedRecords += 1;
                        } else {
                            // creating resource failed
                            report.println(
                                Messages.get().container(Messages.LOG_ERR_FAILED_0),
                                I_CmsReport.FORMAT_ERROR);
                            m_errImportedRecords += 1;
                        }
                    }
                } else {
                    // invalid excel record
                    report.print(Messages.get().container(
                        Messages.LOG_INVALID_EXCEL_RECORD_1,
                        String.valueOf(recordCounter + 1)), I_CmsReport.FORMAT_NOTE);
                    report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
                    m_invalidRecords += 1;
                }
            }
        }
    }

    /**
     * Gets configuration file belonging to excel file. The mapping between excel file and configuration
     * file is between a property from excel file and a setting in configuration file in form from OpenCms
     * resource type name.<p>
     * 
     * @param cmsExcelContent content from excel file
     * @param report report content for user
     * 
     * @return configuration file content belonging to excel file
     */
    private CmsExcelXmlConfiguration getConfigurationFileToExcelFile(CmsExcelContent cmsExcelContent) {

        CmsExcelXmlConfiguration cmsExcelXmlConfiguration = null;
        // get property content for resource type
        String excelProperty = cmsExcelContent.getCategoryProperty();
        if (CmsStringUtil.isNotEmpty(excelProperty)) {
            // get path to configuration files
            String configFilesPath = getPathFromConfigurationFiles();
            if (configFilesPath != null) {
                try {
                    // get all configuration files
                    List allConfigFiles = getCms().readResources(configFilesPath, CmsResourceFilter.ALL, false);
                    if (allConfigFiles != null) {
                        // loop over all existing configuration files
                        Iterator iter = allConfigFiles.iterator();
                        while (iter.hasNext()) {
                            // get next configuration file
                            CmsResource cmsResource = (CmsResource)iter.next();
                            cmsExcelXmlConfiguration = new CmsExcelXmlConfiguration();
                            // read configuration file
                            cmsExcelXmlConfiguration.readConfigurationFile(getCms(), getCms().getSitePath(cmsResource));
                            // prove if setting from OpenCms resource type in excel file is the same like in the
                            // configuration file
                            if (CmsStringUtil.isNotEmpty(cmsExcelXmlConfiguration.getResourceType())
                                && cmsExcelXmlConfiguration.getResourceType().equals(excelProperty)) {
                                // if a belonging configuration file is found, the configuration file is returned
                                return cmsExcelXmlConfiguration;
                            }
                        }
                    }
                } catch (CmsException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the dialog text for security dialog, which is configurable in module parameters.
     * This method includes informations about the actions which will become executed, for
     * example the excel file name, the path where XML contents are created or updated.<p>
     * 
     * @param cmsObject current CmsObject
     * @param workplacePath current path in workplace
     * @param excelName name from excel file
     * @param resourceType selected resource type
     * @param publish true, if resources shall become published, otherwise false 
     * @param cmsExcelContent the excel content
     * 
     * @return the dialog text for security dialog
     */
    private String getDialogText(
        CmsObject cmsObject,
        String workplacePath,
        String excelName,
        String resourceType,
        boolean publish,
        CmsExcelContent cmsExcelContent) {

        StringBuffer buffer = new StringBuffer(4096);
        // here choose user locale and not workplace directory locale
        Locale locale = getCms().getRequestContext().getLocale();
        // excel name
        buffer.append(Messages.get().container(Messages.GUI_DIALOG_EXCEL_NAME_1, excelName).key(locale) + "<br>");
        // info where new resources are created and where resources are updated
        buffer.append(Messages.get().container(
            Messages.GUI_DIALOG_ACTION_2,
            resourceType,
            m_configParas.getPathToXmlContents(cmsObject, workplacePath, resourceType, cmsExcelContent)).key(locale)
            + "<br>");
        // publish info
        if (publish) {
            buffer.append(Messages.get().container(Messages.LOG_PUBLISH_0).key(locale) + "<br><br>");
        } else {
            buffer.append(Messages.get().container(Messages.LOG_NOT_PUBLISH_0).key(locale) + "<br><br>");
        }
        // Okay to start message
        buffer.append(Messages.get().container(Messages.GUI_DIALOG_SUCC_QUESTION_0).key(locale));
        return buffer.toString();
    }

    /**
     * Gets number of columns in excel file to evaluate.<p>
     * 
     * @param cmsExcelContent current excel content
     * @param cmsExcelXmlConfiguration current configuration
     * 
     * @return number of columns in excel file to evaluate
     */
    private int getNumberOfColumnsToEvaluate(
        CmsExcelContent cmsExcelContent,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration) {

        int rows = 0;
        // all excel user column names
        Map excelRowNames = cmsExcelContent.getColumnNames();
        // loop over all excel column names
        Iterator iter = excelRowNames.keySet().iterator();
        while (iter.hasNext()) {
            String userRowName = (String)iter.next();
            // get XML tag name from config file to excel user column name
            String xmlTagName = cmsExcelXmlConfiguration.getXmlTagNameToExcelColumnName(userRowName);
            // if there is a XML tag name so this excel user column name must become evaluated 
            if (CmsStringUtil.isNotEmpty(xmlTagName)) {
                rows += 1;
            }
        }
        return rows;
    }

    /**
     * Creates an object from data type I_CmsConfigurationParameters.
     * This is necessary for the interface which can become used in configuration file.<p>
     * 
     * @param className name from class to create an object from
     * 
     * @return object from data type I_CmsConfigurationParameters
     */
    private I_CmsVfsSettings getObject(String className) throws Exception {

        I_CmsVfsSettings object = null;
        Class c = Class.forName(className);
        object = (I_CmsVfsSettings)c.newInstance();

        return object;
    }

    /**
     * Returns the path of configuration files from the module parameter.<p>
     * 
     * @return  the path of configuration files from the module parameter
     */
    private String getPathFromConfigurationFiles() {

        String path = "";
        // get the module
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE_NAME);
        if (module != null) {
            path = module.getParameter("configurationpath", CmsWorkplace.VFS_PATH_MODULES
                + MODULE_NAME
                + "/configurations/");
        }
        return path;
    }

    /**
     * Returns the project name which shall become used for excel import. Project is configurable
     * in module parameter.<p>
     * 
     * @return the project name which shall become used for excel import
     */
    private String getUploadProject() {

        String path = "";
        // get the module
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE_NAME);
        if (module != null) {
            path = module.getParameter("uploadproject");
            if (path == null) {
                path = "";
            }
        }
        return path;
    }

    /**
     * Here the excel file import starts.. Before start is proved if all configurations in excel file are correct. It
     * is proved if there is a configuration file belonging to excel file. It is proved if configuration file settings are 
     * correct. Only if settings are correct, the import from excel file can become started. After importing
     * the publishing from changed or new created resources is executed, if necessary.<p>
     * 
     * @param report report for user
     */
    private void importExcelFile(I_CmsReport report) {

        // check configuration path
        report.print(
            Messages.get().container(Messages.LOG_CONFIG_PATH_1, getPathFromConfigurationFiles()),
            I_CmsReport.FORMAT_NOTE);
        if (checkConfigPath(getPathFromConfigurationFiles())) {
            report.println(
                org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                I_CmsReport.FORMAT_OK);
        } else {
            // invalid configuration path
            report.print(Messages.get().container(Messages.LOG_ERR_INVALID_CONFIGPATH_0), I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            return;
        }

        // read excel file
        report.println(
            Messages.get().container(Messages.LOG_EXCEL_NAME_1, m_cmsImport.getExcelName()),
            I_CmsReport.FORMAT_NOTE);
        CmsExcelContent cmsExcelContent = new CmsExcelContent();
        cmsExcelContent.readExcelFile(getCms(), m_cmsImport.getExcelName(), m_cmsImport.getParamExcelContent());
        if ((cmsExcelContent.getNumberOfRecords() > 0)
            && (CmsStringUtil.isNotEmpty(cmsExcelContent.getCategoryProperty()))) {
            // excel properties
            report.println(Messages.get().container(
                Messages.LOG_EXCEL_CATEGORY_PROPERTY_1,
                cmsExcelContent.getCategoryProperty()), I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(
                Messages.LOG_EXCEL_FOLDER_PROPERTY_1,
                cmsExcelContent.getFolderProperty()), I_CmsReport.FORMAT_NOTE);
        } else {
            // empty excel file
            report.print(
                Messages.get().container(Messages.LOG_ERR_EMPTY_EXCEL_1, m_cmsImport.getExcelName()),
                I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            m_initError = true;
            return;
        }

        // get configuration file to excel file
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration = getConfigurationFileToExcelFile(cmsExcelContent);
        if (cmsExcelXmlConfiguration != null) {
            report.println(Messages.get().container(
                Messages.LOG_CONFIG_NAME_1,
                cmsExcelXmlConfiguration.getConfigFileName()), I_CmsReport.FORMAT_NOTE);
        } else {
            // no configuration file
            report.print(Messages.get().container(Messages.LOG_ERR_NO_CONFIGFILE_0, ""), I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            m_initError = true;
            return;
        }

        // check for valid configuration file
        boolean validConfig = isValidConfiguration(cmsExcelXmlConfiguration);
        if (!validConfig) {
            // invalid configuration file
            report.print(Messages.get().container(
                Messages.LOG_INVALID_CONFIGFILE_1,
                cmsExcelXmlConfiguration.getConfigFileName()), I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            m_initError = true;
            return;
        }

        // check for valid excel file, all mandatory rows must be available
        boolean validExcel = isValidExcel(cmsExcelContent, cmsExcelXmlConfiguration);
        if (!validExcel) {
            // invalid excel file
            report.print(
                Messages.get().container(Messages.LOG_INVALID_EXCELFILE_1, cmsExcelContent.getExcelName()),
                I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            m_initError = true;
            return;
        }

        // get path to handle existing XML contents in
        String interfaceName = cmsExcelXmlConfiguration.getInterfaceName();
        try {
            if (CmsStringUtil.isNotEmpty(interfaceName)) {
                m_configParas = getObject(interfaceName);
            } else {
                m_configParas = new CmsDefaultVfsSettings();
            }
        } catch (Exception e) {
            // no interface
            report.print(Messages.get().container(Messages.LOG_NO_INTERFACE_1, interfaceName), I_CmsReport.FORMAT_NOTE);
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            m_initError = true;
            return;
        }

        String pathXmlContents = m_configParas.getPathToXmlContents(
            getCms(),
            m_cmsImport.getFolder(),
            cmsExcelXmlConfiguration.getResourceType(),
            cmsExcelContent);
        m_locale = OpenCms.getLocaleManager().getDefaultLocale(getCms(), pathXmlContents);

        // check path to import the xml contents in
        if (!getCms().existsResource(pathXmlContents)) {
            // could not read the path
            report.println(
                Messages.get().container(Messages.LOG_EXCEL_IMPORT_PATH_NOT_FOUND_1, pathXmlContents),
                I_CmsReport.FORMAT_ERROR);
            return;
        }

        // evaluate excel contents
        report.println();
        report.println(Messages.get().container(Messages.LOG_START_EXCELIMPORT_0, ""), I_CmsReport.FORMAT_NOTE);
        evaluateExcelRecords(cmsExcelContent, cmsExcelXmlConfiguration, pathXmlContents, report);
        report.println(Messages.get().container(Messages.LOG_END_EXCELIMPORT_0, ""), I_CmsReport.FORMAT_NOTE);

        // publish resources
        if (m_cmsImport.getPublish()) {
            publish(report);
        }

        // write import status to report 
        reportImportStatus(report, cmsExcelContent.getNumberOfRecords());
    }

    /**
     * Checks configuration file is valid. It is checked if there is at least one mapping set in this file.<p>
     * 
     * @param cmsExcelXmlConfiguration Configuration content
     * 
     * @return true, if configuration file is valid, otherwise false
     */
    private boolean isValidConfiguration(CmsExcelXmlConfiguration cmsExcelXmlConfiguration) {

        // at least one mapping must become set
        List mappings = cmsExcelXmlConfiguration.getMappings();
        if (mappings.size() < 1) {
            return false;
        }
        return true;
    }

    /**
     * Checks excel file is valid. It is checked that file is not empty and that the property
     * with OpenCms resource type is set.<p>
     * 
     * @param cmsExcelContent excel content
     * @param cmsExcelXmlConfiguration configuration content
     * 
     * @return true, if excel file is valid, otherwise false
     */
    private boolean isValidExcel(CmsExcelContent cmsExcelContent, CmsExcelXmlConfiguration cmsExcelXmlConfiguration) {

        // list with all mandatory columns
        List mandatoryCols = cmsExcelXmlConfiguration.getMandatoryUserColumnNames();
        // list with available excel columns
        Map userCols = cmsExcelContent.getColumnNames();

        // prove that all mandatory columns are included in excel content
        Iterator iter = mandatoryCols.iterator();
        while (iter.hasNext()) {
            String mandatoryCol = (String)iter.next();
            boolean valid = userCols.containsKey(mandatoryCol);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if excel record is valid. An excel record is valid if the mandatory column values are not empty.<p>
     * 
     * @param cmsExcelContent Excel content
     * @param cmsExcelXmlConfiguration Configuration content
     * @param recordCounter Number of current excel record
     * 
     * @return true, if excel file record is valid
     */
    private boolean isValidExcelRecord(
        CmsExcelContent cmsExcelContent,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        int recordCounter) {

        // list with all mandatory rows
        List mandatoryRows = cmsExcelXmlConfiguration.getMandatoryUserColumnNames();

        // prove that to every mandatory row is a filled excel cell
        Iterator iter = mandatoryRows.iterator();
        while (iter.hasNext()) {
            // get next mandatory column name
            String mandatoryRow = (String)iter.next();
            // get content from this column
            String excelContent = cmsExcelContent.getCellContent(mandatoryRow, recordCounter);
            if (CmsStringUtil.isEmpty(excelContent)) {
                // mandatory column value is empty -> record is not valid
                return false;
            }
        }
        return true;
    }

    /**
     * Locks the current resource.<p>
     * 
     * @param cms the current CmsObject
     * @param cmsFile the file to lock
     * @param report the report
     * 
     * @throws CmsException if some goes wrong
     */
    private boolean lockResource(CmsFile cmsFile) throws CmsException {

        CmsLock lock = getCms().getLock(getCms().getSitePath(cmsFile));
        // check the lock
        if ((lock != null)
            && lock.isOwnedBy(getCms().getRequestContext().currentUser())
            && lock.isOwnedInProjectBy(
                getCms().getRequestContext().currentUser(),
                getCms().getRequestContext().currentProject())) {
            // prove is current lock from current user in current project
            return true;
        } else if ((lock != null) && !lock.isUnlocked() && !lock.isOwnedBy(getCms().getRequestContext().currentUser())) {
            // the resource is not locked by the current user, so can not lock it
            return false;
        } else if ((lock != null)
            && !lock.isUnlocked()
            && lock.isOwnedBy(getCms().getRequestContext().currentUser())
            && !lock.isOwnedInProjectBy(
                getCms().getRequestContext().currentUser(),
                getCms().getRequestContext().currentProject())) {
            // prove is current lock from current user but not in current project
            // file is locked by current user but not in current project
            // change the lock 
            getCms().changeLock(getCms().getSitePath(cmsFile));
        } else if ((lock != null) && lock.isUnlocked()) {
            // lock resource from current user in current project
            getCms().lockResource(getCms().getSitePath(cmsFile));
        }
        lock = getCms().getLock(getCms().getSitePath(cmsFile));
        if ((lock != null)
            && lock.isOwnedBy(getCms().getRequestContext().currentUser())
            && !lock.isOwnedInProjectBy(
                getCms().getRequestContext().currentUser(),
                getCms().getRequestContext().currentProject())) {
            // resource could not be locked
            return false;
        }
        // resource is locked successfully
        return true;
    }

    /**
     * Publish resources. All XML contents are published which are new created from this thread 
     * and which wre changed from this thread. All resources to publish are saved in a publish list.
     * This list is become published here.<p>
     * 
     * @param report report for user
     * 
     */
    private void publish(I_CmsReport report) {

        report.println();
        report.print(Messages.get().container(Messages.LOG_RECORDS_PUBLISH_0), I_CmsReport.FORMAT_NOTE);

        try {
            // create OpenCms publish list with publish list from all new created and updated XML contents
            CmsPublishList pubList = OpenCms.getPublishManager().getPublishList(getCms(), m_publishList, false);
            OpenCms.getPublishManager().publishProject(getCms(), null, pubList);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
            report.println(Messages.get().container(Messages.LOG_ERR_FAILED_0), I_CmsReport.FORMAT_ERROR);
            return;
        }
        report.println(
            org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
            I_CmsReport.FORMAT_OK);
    }

    /**
     * Writes status from to report. Includes the output from number of number of excel records, the
     * number of new created XML contents, from updated XML contents and from not changed XML contents.<p>
     * 
     * @param report report for user
     * @param records number of records to import
     */
    private void reportImportStatus(I_CmsReport report, int records) {

        report.println();
        // records in excel
        report.println(
            Messages.get().container(Messages.LOG_RECORDS_IN_EXCEL_1, new Integer(records - m_emptyRecords)),
            I_CmsReport.FORMAT_NOTE);
        // invalid records
        report.println(
            Messages.get().container(Messages.LOG_INVALID_RECORDS_1, new Integer(m_invalidRecords)),
            I_CmsReport.FORMAT_NOTE);
        // successful imported records
        report.println(
            Messages.get().container(Messages.LOG_SUCC_IMPORTED_1, new Integer(m_importedRecords)),
            I_CmsReport.FORMAT_NOTE);
        // not successful imported records
        report.println(
            Messages.get().container(Messages.LOG_FAIL_IMPORTED_1, new Integer(m_errImportedRecords)),
            I_CmsReport.FORMAT_NOTE);
        // successful updated records
        report.println(
            Messages.get().container(Messages.LOG_SUCC_UPDATED_1, new Integer(m_updatedRecords)),
            I_CmsReport.FORMAT_NOTE);
        // not successful updated records
        report.println(
            Messages.get().container(Messages.LOG_FAIL_UPDATED_1, new Integer(m_errUpdatedRecords)),
            I_CmsReport.FORMAT_NOTE);
        // unchanged records
        report.println(
            Messages.get().container(Messages.LOG_UNCHANGED_1, new Integer(m_unchangedRecords)),
            I_CmsReport.FORMAT_NOTE);
    }

    /**
     * Sets import project. It is proved if project which is configured in module parameters is valid. 
     * If project can not become set so is used the current project from user for import.<p>
     * 
     * @param report report for user
     * @param currentProject Current project from user
     */
    private void setProject(I_CmsReport report, CmsProject currentProject) {

        String uploadProject = getUploadProject();
        if (CmsStringUtil.isNotEmpty(uploadProject)) {
            // use upload project
            report.print(
                Messages.get().container(Messages.LOG_UPLOAD_PROJECT_1, uploadProject),
                I_CmsReport.FORMAT_NOTE);
            try {
                // try to set upload project as current project
                CmsProject cmsUploadProject = getCms().readProject(uploadProject);
                getCms().getRequestContext().setCurrentProject(cmsUploadProject);
                report.println(
                    org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                    I_CmsReport.FORMAT_OK);
            } catch (CmsException e) {
                // no valid upload project, so use current user project
                getCms().getRequestContext().setCurrentProject(currentProject);
                report.print(
                    Messages.get().container(Messages.LOG_ERR_UPLOAD_PROJECT_1, currentProject.getName()),
                    I_CmsReport.FORMAT_NOTE);
                report.println(
                    org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                    I_CmsReport.FORMAT_OK);
            }
        } else {
            // use current user project
            report.print(
                Messages.get().container(Messages.LOG_NO_UPLOAD_PROJECT_1, currentProject.getName()),
                I_CmsReport.FORMAT_NOTE);
            report.println(
                org.opencms.report.Messages.get().container(org.opencms.report.Messages.RPT_OK_0),
                I_CmsReport.FORMAT_OK);
        }
    }

    /**
     * Set values in a XML content. The values come from the current excel record.<p>
     * 
     * @param cmsResource current CmsResource
     * @param cmsFile current CmsFile
     * @param xmlContent XML content
     * @param currentRecord current excel record
     * @param cmsExcelXmlConfiguration configuration
     * @param provePublish prove if publish
     * @param report report for user
     * 
     * @return true, if setting values in XML content was successful, otherwise false
     */
    private boolean setValuesInXmlContent(
        CmsResource cmsResource,
        CmsFile cmsFile,
        CmsXmlContent xmlContent,
        Map currentRecord,
        CmsExcelXmlConfiguration cmsExcelXmlConfiguration,
        boolean provePublish,
        I_CmsReport report) {

        // prove locale in XML content
        boolean hasLocale = false;
        // get all locales from XML content
        List locales = xmlContent.getLocales();
        // loop over all these locales
        Iterator iterLocale = locales.iterator();
        while (iterLocale.hasNext()) {
            Locale localeXml = (Locale)iterLocale.next();
            // prove current locale from XML content to locale from workplace directory 
            if (localeXml.getLanguage().equals(m_locale.getLanguage())) {
                // locale from workplace directory is included in XML content, so use it
                hasLocale = true;
            }
        }
        // locale from workplace directory is not included in XML content, so take first locale from XML content
        if (!hasLocale) {
            try {
                xmlContent.addLocale(getCms(), m_locale);
            } catch (CmsException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e);
                }
                return false;
            }
        }

        // get data from current excel record
        Iterator iterRows = currentRecord.keySet().iterator();
        while (iterRows.hasNext()) {
            // get user row name
            String userRowName = (String)iterRows.next();
            if (CmsStringUtil.isNotEmpty(userRowName)) {
                // get XML tag name
                String xmlTagName = cmsExcelXmlConfiguration.getXmlTagNameToExcelColumnName(userRowName);
                if (CmsStringUtil.isNotEmpty(xmlTagName)) {
                    try {
                        // get excel content
                        String excelContent = (String)currentRecord.get(userRowName);
                        boolean hasValue = xmlContent.hasValue(xmlTagName, m_locale);

                        // do nothing if excel content is empty and content has this tag
                        if (CmsStringUtil.isEmpty(excelContent) && !hasValue) {
                            // if empty excel content and tag is disabled do nothing
                        } else {

                            I_CmsXmlContentValue contentValue = null;
                            // if there is an optional nested content in XML tag, so this content must become created at first
                            // thats why the following operations are necessary
                            // split XML tag by slash
                            String[] elements = xmlTagName.split("/");
                            // full xPath is added step by step by single parts from XML tag
                            String fullXPath = "";
                            // loop over all parts from XML tag, which were splitted by slash
                            // for every part is to prove if tag is included in XML content already, if not so
                            // it must become created
                            for (int elementCounter = 0; elementCounter < elements.length; elementCounter++) {
                                if (fullXPath.isEmpty()) {
                                    // fill xPath with the first element from XML tag
                                    fullXPath = elements[elementCounter];
                                } else {
                                    // fill xPath with next element from XML tag
                                    fullXPath = fullXPath.concat("/" + elements[elementCounter]);
                                }
                                // prove if XML content included the xPath
                                if (!xmlContent.hasValue(fullXPath, m_locale)) {
                                    // XML content still does not contain this xPath, so create it
                                    contentValue = xmlContent.addValue(getCms(), fullXPath, m_locale, 0);
                                } else {
                                    // XML content already does contain this xPath, so get it
                                    contentValue = xmlContent.getValue(fullXPath, m_locale);
                                }
                            }
                            if (contentValue != null) {
                                if (CmsStringUtil.isEmpty(excelContent)) {
                                    // empty excel content, so set default value
                                    String defaultValue = contentValue.getDefault(m_locale);
                                    if (CmsStringUtil.isEmpty(defaultValue)) {
                                        defaultValue = "";
                                    }
                                    // date value, so the "0" is also empty
                                    if (xmlContent.getContentDefinition().getSchemaType(xmlTagName).getTypeName().equals(
                                        CmsXmlDateTimeValue.TYPE_NAME)
                                        && defaultValue.equals("0")
                                        && (contentValue.getMinOccurs() == 0)) {
                                        xmlContent.removeValue(xmlTagName, m_locale, 0);
                                    } else if (CmsStringUtil.isEmpty(defaultValue)
                                        && (contentValue.getMinOccurs() == 0)) {
                                        xmlContent.removeValue(xmlTagName, m_locale, 0);
                                    } else {
                                        contentValue.setStringValue(getCms(), defaultValue);
                                    }
                                } else {
                                    // set excel content
                                    contentValue.setStringValue(getCms(), excelContent);
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(e);
                        }
                        report.print(Messages.get().container(
                            Messages.LOG_ERR_INVALID_XMLTAG_1,
                            xmlTagName,
                            getCms().getSitePath(cmsFile)), I_CmsReport.FORMAT_NOTE);
                        return false;
                    }
                }
            }
        }
        // write to file
        if (writeToFile(xmlContent, cmsResource, cmsFile, provePublish)) {
            return true;
        }
        return false;
    }

    /**
     * Writes XML content to file. <p>
     * 
     * @param xmlContent current XML content
     * @param cmsResource current CmsResource
     * @param cmsFile current file
     * @param provePublish prove if publish resource
     * 
     * @return true, if writing to file was successful, otherwise false
     */
    private boolean writeToFile(CmsXmlContent xmlContent, CmsResource cmsResource, CmsFile cmsFile, boolean provePublish) {

        try {
            // set XML content to file
            cmsFile.setContents(xmlContent.marshal());
            // write file
            cmsFile = getCms().writeFile(cmsFile);
            // unlock file
            CmsLock lock = getCms().getLock(getCms().getSitePath(cmsFile));
            if (!lock.getType().isInherited()) {
                getCms().unlockResource(getCms().getSitePath(cmsFile));
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
            return false;
        }
        // if resources shall become published, so the resources come into the publish list
        if (m_cmsImport.getPublish()) {
            // get correct resource status
            if (cmsResource.getState().equals(CmsResourceState.STATE_UNCHANGED)) {
                try {
                    cmsResource = getCms().readResource(getCms().getSitePath(cmsResource));
                } catch (CmsException e) {
                    // should not happen because resource exists
                    if (LOG.isErrorEnabled()) {
                        LOG.error(e);
                    }
                    return false;
                }
            }
            // read resource again to have correct status
            m_publishList.add(cmsFile);
        }
        return true;
    }
}
