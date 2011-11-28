/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.excelimport/src/com/alkacon/opencms/v8/excelimport/CmsExcelXmlConfiguration.java,v $
 * Date   : $Date: 2009/04/30 10:52:07 $
 * Version: $Revision: 1.1 $
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

package com.alkacon.opencms.v8.excelimport;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;

/**
 * Includes the content from an excel import configuration file. Here is saved for which resource
 * new XML content shall become created. Here are saved the mappings between excel file and XML content.
 * Here is saved the weight which says when an XML content belongs to an excel record.<p>
 * 
 * On read content items can become accessed.<p> 
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.5.0
 */
public class CmsExcelXmlConfiguration {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsExcelXmlConfiguration.class);

    /** XML configuration content interface name. */
    private static final String NODE_INTERFACE_NAME = "Interface";

    /** XML configuration content mappings. */
    private static final String NODE_MAPPING = "Mapping";

    /** XML configuration content mappings excel column name. */
    private static final String NODE_MAPPING_EXCEL_COLUMN_NAME = "ExcelColumn";

    /** XML configuration content resource type. */
    private static final String NODE_MAPPING_MANDATORY = "Mandatory";

    /** XML configuration content mappings weight. */
    private static final String NODE_MAPPING_WEIGHT = "Weight";

    /** XML configuration content mapping XML tag name. */
    private static final String NODE_MAPPING_XML_TAG_NAME = "XmlTag";

    /** XML configuration content minimum weight for existing XML content. */
    private static final String NODE_MINIMUM_WEIGHT = "MinWeight";

    /** XML configuration content resource type. */
    private static final String NODE_RESOURCE_TYPE = "ResourceType";

    /** The config file name. */
    private String m_configName;

    /** The interface name to use while import. */
    private String m_interfaceName;

    /** The mappings to use while import. */
    private List m_mappings = new ArrayList();

    /** The minimum of sum of weights to have an already existing XML content. */
    private int m_minimumSumWeightsForExistingXmlContent = -1;

    /** The resource type to read configuration file for. */
    private String m_resourceType;

    /**
     * Gets configuration file name.<p>
     * 
     * @return configuration file name
     */
    public String getConfigFileName() {

        if (CmsStringUtil.isNotEmpty(m_configName)) {
            return m_configName;
        } else {
            return "";
        }
    }

    /**
     * Gets the interface name to use while import.<p>
     * 
     * @return interface name to use while import
     */
    public String getInterfaceName() {

        if (CmsStringUtil.isNotEmpty(m_interfaceName)) {
            return m_interfaceName;
        } else {
            return "";
        }
    }

    /**
     * Gets list with mandatory excel column names set by user.<p>
     * 
     * @return list with mandatory excel user row names
     */
    public List getMandatoryUserColumnNames() {

        List mandatoryRows = new ArrayList();
        if (m_mappings != null) {
            Iterator iter = m_mappings.iterator();
            while (iter.hasNext()) {
                CmsExcelXmlConfigurationMapping singleMapping = (CmsExcelXmlConfigurationMapping)iter.next();
                if (singleMapping.getMandatory()) {
                    mandatoryRows.add(singleMapping.getExcelColumnName());
                }
            }
        }
        return mandatoryRows;
    }

    /**
     * Gets the mappings to use while import.<p>
     * 
     * @return mappings to use while import
     */
    public List getMappings() {

        return m_mappings;
    }

    /**
     * Gets the minimum of sum of weights to have an already existing XML content.<p>
     * 
     * @return minimum of sum of weights to have an already existing XML content
     */
    public int getMinWeight() {

        return m_minimumSumWeightsForExistingXmlContent;
    }

    /**
     * Gets the resource type set in configuration file.<p>
     * 
     * @return resource type set in configuration file
     */
    public String getResourceType() {

        if (CmsStringUtil.isNotEmpty(m_resourceType)) {
            return m_resourceType;
        } else {
            return "";
        }
    }

    /**
     * Get weight belonging to excel column name set in configuration file.<p>
     * 
     * @param excelColumnName Excel column name set by user
     * 
     * @return weight belonging to excel column name
     */
    public int getWeightToExcelColumnName(String excelColumnName) {

        int weight = 0;

        Iterator iter = m_mappings.iterator();
        while (iter.hasNext()) {

            CmsExcelXmlConfigurationMapping mapping = (CmsExcelXmlConfigurationMapping)iter.next();
            if (mapping.getExcelColumnName().equals(excelColumnName)) {
                weight = mapping.getWeight();
                return weight;
            }
        }
        return weight;
    }

    /**
     * Get XML tag name belonging to excel column name.<p>
     * 
     * @param excelColumnName Excel column name set by user
     * 
     * @return XML tag name belonging to excel column name
     */
    public String getXmlTagNameToExcelColumnName(String excelColumnName) {

        String xmlTagName = "";

        Iterator iter = m_mappings.iterator();
        while (iter.hasNext()) {

            CmsExcelXmlConfigurationMapping mapping = (CmsExcelXmlConfigurationMapping)iter.next();
            if (mapping.getExcelColumnName().equals(excelColumnName)) {
                xmlTagName = mapping.getXmlTagName();
                return xmlTagName;
            }
        }
        return xmlTagName;
    }

    /**
     * Reads configuration file.<p>
     * 
     * @param cmsObject current CmsObject
     * @param fileName name from configuration file to read
     */
    public void readConfigurationFile(CmsObject cmsObject, String fileName) {

        CmsXmlContent xmlContent = null;

        try {
            CmsFile xmlFile = cmsObject.readFile(fileName);
            xmlContent = CmsXmlContentFactory.unmarshal(cmsObject, xmlFile);

            Locale locale = cmsObject.getRequestContext().getLocale();
            // check for current request locale
            boolean localeFound = false;
            Iterator iterLocale = xmlContent.getLocales().iterator();
            while (iterLocale.hasNext()) {
                Locale localeCheck = (Locale)iterLocale.next();
                if (locale.getLanguage().equals(localeCheck.getLanguage())) {
                    localeFound = true;
                }
            }
            if (!localeFound) {
                locale = xmlContent.getLocales().get(0);
            }

            // resource type
            if (xmlContent.hasValue(NODE_RESOURCE_TYPE, locale)) {
                m_resourceType = xmlContent.getValue(NODE_RESOURCE_TYPE, locale).getStringValue(cmsObject);
            }
            // interface name
            if (xmlContent.hasValue(NODE_INTERFACE_NAME, locale)) {
                m_interfaceName = xmlContent.getValue(NODE_INTERFACE_NAME, locale).getStringValue(cmsObject);
            }
            // minimum weight for existing XML content
            if (xmlContent.hasValue(NODE_MINIMUM_WEIGHT, locale)) {
                String minimum = xmlContent.getValue(NODE_MINIMUM_WEIGHT, locale).getStringValue(cmsObject);
                if (CmsStringUtil.isNotEmpty(minimum)) {
                    m_minimumSumWeightsForExistingXmlContent = Integer.parseInt(minimum);
                }
            }
            // mappings
            Iterator iterImages = xmlContent.getValues(NODE_MAPPING, locale).iterator();
            while (iterImages.hasNext()) {
                // loop all mapping nodes
                I_CmsXmlContentValue value = (I_CmsXmlContentValue)iterImages.next();
                CmsExcelXmlConfigurationMapping mapping = new CmsExcelXmlConfigurationMapping();

                String xPath = value.getPath();
                // XML tag name
                if (xmlContent.hasValue(xPath + "/" + NODE_MAPPING_XML_TAG_NAME, locale)) {
                    String xmlTagName = xmlContent.getValue(xPath + "/" + NODE_MAPPING_XML_TAG_NAME, locale).getStringValue(
                        cmsObject);
                    mapping.setXmlTagName(xmlTagName);
                }
                // excel column name
                if (xmlContent.hasValue(xPath + "/" + NODE_MAPPING_EXCEL_COLUMN_NAME, locale)) {
                    String excelColumnName = xmlContent.getValue(xPath + "/" + NODE_MAPPING_EXCEL_COLUMN_NAME, locale).getStringValue(
                        cmsObject);
                    mapping.setExcelColumnName(excelColumnName);
                }
                // weight
                if (xmlContent.hasValue(xPath + "/" + NODE_MAPPING_WEIGHT, locale)) {
                    String valueString = xmlContent.getValue(xPath + "/" + NODE_MAPPING_WEIGHT, locale).getStringValue(
                        cmsObject);
                    if (CmsStringUtil.isNotEmpty(valueString)) {
                        int weight = Integer.parseInt(valueString);
                        mapping.setWeight(weight);
                    }
                }
                // mandatory
                if (xmlContent.hasValue(xPath + "/" + NODE_MAPPING_MANDATORY, locale)) {
                    String valueString = xmlContent.getValue(xPath + "/" + NODE_MAPPING_MANDATORY, locale).getStringValue(
                        cmsObject);
                    boolean mandatory = new Boolean(valueString).booleanValue();
                    mapping.setMandatory(mandatory);
                }
                m_mappings.add(mapping);
                m_configName = fileName;
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.toString());
            }
        }
    }
}