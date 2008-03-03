/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.commons/src/com/alkacon/opencms/commons/CmsConfigurableCollector.java,v $
 * Date   : $Date: 2008/03/03 08:27:23 $
 * Version: $Revision: 1.4 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.commons;

import org.opencms.file.CmsDataAccessException;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.collectors.A_CmsResourceCollector;
import org.opencms.main.CmsException;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.CmsXmlException;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Collects VFS resources as configured.<p>
 * 
 * This configurable collector can be used to collect different resource types in different VFS folders. Use 
 * {@link com.alkacon.opencms.commons.CmsCollectorConfiguration} objects to configure the collector.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 6.0.1
 */
public class CmsConfigurableCollector extends A_CmsResourceCollector {

    /** The collector name. */
    public static final String COLLECTOR_NAME = "configurableCollector";

    /** Node name for XMLContent collector configuration file: the node containing the VFS folder. */
    public static final String NODE_FOLDER = "Folder";

    /** Node name for XMLContent collector configuration file: the node(s) containing the mandatory properties. */
    public static final String NODE_PROPERTY = "Property";

    /** Node name for XMLContent collector configuration file: the node containing a configuration. */
    public static final String NODE_RESCONFIG = "ResConfig";

    /** Node name for XMLContent collector configuration file: the node containing the resource type name. */
    public static final String NODE_RESTYPE = "ResType";

    /** The collector configurations to use to collect the resources. */
    private final List m_collectorConfigurations;

    /**
     * Constructor that initializes an empty collector configuration list.<p>
     */
    public CmsConfigurableCollector() {

        super();
        setDefaultCollectorName(COLLECTOR_NAME);
        setDefaultCollectorParam("");
        m_collectorConfigurations = new ArrayList();
    }

    /**
     * Constructor that initializes the collector configuration list.<p>
     * 
     * @param collectorConfigurations the list of collector configurations to use
     */
    public CmsConfigurableCollector(List collectorConfigurations) {

        this();
        m_collectorConfigurations.addAll(collectorConfigurations);
    }

    /**
     * Returns the collector configurations to use to collect the resources.<p>
     *
     * @return the collector configurations to use to collect the resources
     */
    public List getCollectorConfigurations() {

        return m_collectorConfigurations;
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCollectorNames()
     */
    public List getCollectorNames() {

        return Collections.singletonList(COLLECTOR_NAME);
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCreateLink(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public String getCreateLink(CmsObject cms, String collectorName, String param) {

        // this collector does not support resource creation links
        return null;
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCreateParam(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public String getCreateParam(CmsObject cms, String collectorName, String param) {

        // this collector does not support resource creation parameters
        return null;
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getResults(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public List getResults(CmsObject cms, String collectorName, String param)
    throws CmsDataAccessException, CmsException {

        // if action is not set use default
        if (collectorName == null) {
            collectorName = COLLECTOR_NAME;
        }

        return getAllInFolder(cms, param);
    }

    /**
     * Sets the collector configurations to use to collect the resources.<p>
     *
     * @param collectorConfigurations the collector configurations to use to collect the resources
     */
    public void setCollectorConfigurations(List collectorConfigurations) {

        m_collectorConfigurations.clear();
        m_collectorConfigurations.addAll(collectorConfigurations);
    }

    /**
     * Returns all resources in the folder pointed to by the parameter.<p>
     * 
     * @param cms the current OpenCms user context
     * @param param the folder name to use
     * 
     * @return all resources in the folder matching the given criteria
     * 
     * @throws CmsException if something goes wrong
     * @throws CmsIllegalArgumentException if the given param argument is not a link to a single file
     * 
     */
    protected List getAllInFolder(CmsObject cms, String param) throws CmsException, CmsIllegalArgumentException {

        List collectorConfigurations = getCollectorConfigurations();
        if (CmsStringUtil.isNotEmpty(param)) {
            // read configuration from param specifying config file in VFS
            try {
                collectorConfigurations = readConfigurationFromFile(cms, param);
            } catch (CmsException e) {
                // error reading collector configuration file
                throw new CmsXmlException(Messages.get().container(Messages.ERR_COLLECTOR_CONFIG_INVALID_1, param));
            }
        }

        Set collected = new HashSet();
        for (int i = 0; i < collectorConfigurations.size(); i++) {
            // loop all configurations and collect the resources
            CmsCollectorConfiguration config = (CmsCollectorConfiguration)collectorConfigurations.get(i);
            CmsResourceFilter filter = CmsResourceFilter.DEFAULT.addExcludeFlags(CmsResource.FLAG_TEMPFILE);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(config.getResourceType())) {
                filter = filter.addRequireType(config.getResourceTypeId());
            }
            List resources = cms.readResources(config.getUri(), filter, config.isRecursive());

            if (config.getProperties().size() > 0) {
                // check the properties of each resource
                for (int k = resources.size() - 1; k > -1; k--) {
                    CmsResource res = (CmsResource)resources.get(k);
                    cms.readPropertyObjects(res, false);
                    boolean addToResult = true;
                    for (int m = config.getProperties().size() - 1; m > -1; m--) {
                        // loop all required properties
                        String propertyDef = (String)config.getProperties().get(m);
                        if (CmsStringUtil.isEmptyOrWhitespaceOnly(cms.readPropertyObject(res, propertyDef, false).getValue())) {
                            addToResult = false;
                            break;
                        }
                    }
                    if (addToResult) {
                        collected.add(res);
                    }
                }
            } else {
                // no properties to check, add all resources
                collected.addAll(resources);
            }
        }

        List result = new ArrayList(collected);

        Collections.sort(result, CmsResource.COMPARE_ROOT_PATH);
        Collections.reverse(result);

        return result;
    }

    /**
     * Returns the collector configuration that is read from an XmlContent resource.<p>
     * 
     * @param cms the current OpenCms user context
     * @param resourceName the absolute path to the VFS resource to read
     * 
     * @return the collector configuration that is read from an XmlContent resource
     * 
     * @throws CmsException if something goes wrong
     */
    private List readConfigurationFromFile(CmsObject cms, String resourceName) throws CmsException {

        List result = new ArrayList();
        Locale locale = cms.getRequestContext().getLocale();

        // get the resource
        CmsResource res = cms.readResource(resourceName);
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, cms.readFile(res));
        // get the configuration nodes
        List configurations = xml.getValues(NODE_RESCONFIG, locale);
        int configurationSize = configurations.size();
        for (int i = 0; i < configurationSize; i++) {
            // loop all configuration nodes
            I_CmsXmlContentValue resConfig = (I_CmsXmlContentValue)configurations.get(i);
            String resConfigPath = resConfig.getPath() + "/";
            String resType = xml.getStringValue(cms, resConfigPath + NODE_RESTYPE, locale);
            String folder = xml.getStringValue(cms, resConfigPath + NODE_FOLDER, locale);
            // determine the properties to check
            List propertyValues = xml.getValues(resConfigPath + NODE_PROPERTY, locale);
            List properties = new ArrayList(propertyValues.size());
            for (int k = propertyValues.size() - 1; k > -1; k--) {
                I_CmsXmlContentValue value = (I_CmsXmlContentValue)propertyValues.get(k);
                properties.add(value.getStringValue(cms));
            }
            // add the configuration to the result
            result.add(new CmsCollectorConfiguration(folder, resType, properties));
        }

        return result;
    }

}
