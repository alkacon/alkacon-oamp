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

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.I_CmsResource;
import org.opencms.file.collectors.A_CmsResourceCollector;
import org.opencms.main.CmsException;
import org.opencms.relations.CmsCategory;
import org.opencms.relations.CmsCategoryService;
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

    /** Node name for XMLContent collector configuration file: the node(s) containing the categories. */
    public static final String NODE_CATEGORY = "Category";

    /** Node name for XMLContent collector configuration file: the node containing the VFS folder. */
    public static final String NODE_FOLDER = "Folder";

    /** Node name for XMLContent collector configuration file: the node(s) containing the mandatory properties. */
    public static final String NODE_PROPERTY = "Property";

    /** Node name for XMLContent collector configuration file: the node containing a configuration. */
    public static final String NODE_RESCONFIG = "ResConfig";

    /** Node name for XMLContent collector configuration file: the node containing the resource type name. */
    public static final String NODE_RESTYPE = "ResType";

    /** The collector configurations to use to collect the resources. */
    private final List<CmsCollectorConfiguration> m_collectorConfigurations;

    /** The path prefix to use when reading the collector configurations. */
    private String m_pathPrefix;

    /**
     * Constructor that initializes an empty collector configuration list.<p>
     */
    public CmsConfigurableCollector() {

        this("");
    }

    /**
     * Constructor that initializes an empty collector configuration list.<p>
     * 
     * @param pathPrefix the prefix to use when reading the collector configurations
     */
    public CmsConfigurableCollector(String pathPrefix) {

        super();
        setDefaultCollectorName(COLLECTOR_NAME);
        setDefaultCollectorParam("");
        m_collectorConfigurations = new ArrayList<CmsCollectorConfiguration>();
        m_pathPrefix = pathPrefix;
    }

    /**
     * Constructor that initializes the collector configuration list.<p>
     * 
     * @param collectorConfigurations the list of collector configurations to use
     */
    public CmsConfigurableCollector(List<CmsCollectorConfiguration> collectorConfigurations) {

        this();
        m_collectorConfigurations.addAll(collectorConfigurations);
    }

    /**
     * Returns the collector configurations to use to collect the resources.<p>
     *
     * @return the collector configurations to use to collect the resources
     */
    public List<CmsCollectorConfiguration> getCollectorConfigurations() {

        return m_collectorConfigurations;
    }

    @Override
    public List<String> getCollectorNames() {

        return Collections.singletonList(COLLECTOR_NAME);
    }

    /**
     * 
     * @param cms ignored
     * @param collectorName ignored
     * @param param ignored
     * @return <tt>null</tt>
     */
    @Override
    public String getCreateLink(CmsObject cms, String collectorName, String param) {

        // this collector does not support resource creation links
        return null;
    }

    /**
     * 
     * @param cms ignored
     * @param collectorName ignored
     * @param param ignored
     * @return <tt>null</tt>
     */
    @Override
    public String getCreateParam(CmsObject cms, String collectorName, String param) {

        // this collector does not support resource creation parameters
        return null;
    }

    /**
     * Returns all resources that fulfill the collector criteria defined in
     * configFilePath.
     * <p>
     * @param cms            the current OpenCms user context
     * @param ignored
     * @param configFilePath absolute path to the VFS resource that defines the
     *                       collector configuration
     * @return the list of resources that fulfill the collector criteria
     * @throws CmsXmlException if error reading the collector configuration
     *                         specified in configFilePath
     * @throws CmsException    if error reading any of the collected resources
     */
    @Override
    public List<CmsResource> getResults(CmsObject cms, String ignored,
            String configFilePath)
            throws CmsXmlException, CmsException {

        // if action is not set use default
        // XXX: AG 2015-04-22 - This doesn't feel good. Overwriting collectorName
        // does not have any effect.
        if (ignored == null) {
            ignored = COLLECTOR_NAME;
        }

        return getResourcesMatchingCollectorConfig(cms, configFilePath);
    }

    // Overriden for documenting purposes
    /**
     * 
     * @param configFilePath absolute path to the VFS resource that defines
     *          the collector configuration
     */
    @Override
    public void setDefaultCollectorParam(String configFilePath) {
        super.setDefaultCollectorParam(configFilePath);
    }

    /**
     * Sets the collector configurations to use to collect the resources.<p>
     *
     * @param collectorConfigurations the collector configurations to use to collect the resources
     */
    public void setCollectorConfigurations(List<CmsCollectorConfiguration> collectorConfigurations) {

        m_collectorConfigurations.clear();
        m_collectorConfigurations.addAll(collectorConfigurations);
    }

    /**
     * Returns all resources that fulfill the collector criteria defined
     * in <tt>configFilePath</tt>.<p>
     * 
     * @param cms the current OpenCms user context
     * @param configFilePath absolute path to the VFS resource that defines the
     *      collector configuration
     * 
     * @return all resources that fulfill the collector criteria
     * 
     * @throws CmsXmlException if error reading the collector configuration
     *      specified in <tt>configFilePath</tt>
     * @throws CmsException if error reading any of the collected resources
     * 
     */
    protected List<CmsResource> getResourcesMatchingCollectorConfig(CmsObject cms,
            String configFilePath)
            throws CmsXmlException, CmsException {

        List<CmsCollectorConfiguration> collectorConfigurations = getCollectorConfigurations();
        if (CmsStringUtil.isNotEmpty(configFilePath)) {
            // read configuration from param specifying config file in VFS
            try {
                collectorConfigurations = readConfigurationFromFile(cms, configFilePath);
            } catch (CmsException e) {
                // error reading collector configuration file
                throw new CmsXmlException(Messages.get().container(Messages.ERR_COLLECTOR_CONFIG_INVALID_1, configFilePath));
            }
        }

        Set<CmsResource> collected = new HashSet<CmsResource>();
        for (CmsCollectorConfiguration config : collectorConfigurations) {
            CmsResourceFilter filter = CmsResourceFilter.DEFAULT.addExcludeFlags(CmsResource.FLAG_TEMPFILE);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(config.getResourceType())) {
                filter = filter.addRequireType(config.getResourceTypeId());
            }
            List<CmsResource> resources = cms.readResources(config.getUri(), filter, config.isRecursive());

            if (config.getCategories().size() > 0) {
                // check the categories of each resource
                List<CmsResource> catResources = new ArrayList<CmsResource>(resources.size());
                CmsCategoryService service = CmsCategoryService.getInstance();
                for (CmsResource resource : resources) {
                    List<CmsCategory> categories = service.readResourceCategories(cms, cms.getSitePath(resource));
                    for (CmsCategory neededCategory : config.getCategories()) {
                        if (categories.contains(neededCategory)) {
                            catResources.add(resource);
                            break;
                        }
                    }
                }
                resources = catResources;
            }

            if (config.getProperties().size() > 0) {
                // check the properties of each resource
                for (int k = resources.size() - 1; k > -1; k--) {
                    CmsResource res = resources.get(k);
                    cms.readPropertyObjects(res, false);
                    boolean addToResult = true;
                    for (int m = config.getProperties().size() - 1; m > -1; m--) {
                        // loop all required properties
                        String propertyDef = config.getProperties().get(m);
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

        List<CmsResource> result = new ArrayList<CmsResource>(collected);

        Collections.sort(result, I_CmsResource.COMPARE_ROOT_PATH);
        Collections.reverse(result);

        return result;
    }

    /**
     * Returns the collector configuration that is read from an XmlContent resource.<p>
     * 
     * @param cms the current OpenCms user context
     * @param configFilePath the absolute path to the VFS resource to read
     * 
     * @return the collector configuration read from the XmlContent resource
     * 
     * @throws CmsException if error reading the config file, or if the config
     *          file declares categories that cannot be read
     */
    private List<CmsCollectorConfiguration> readConfigurationFromFile(CmsObject cms, String configFilePath)
    throws CmsException {

        List<CmsCollectorConfiguration> result = new ArrayList<CmsCollectorConfiguration>();
        Locale locale = cms.getRequestContext().getLocale();

        // get the resource
        CmsResource res = cms.readResource(configFilePath);
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, cms.readFile(res));
        // get the configuration nodes
        String prefix = "";
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(m_pathPrefix)) {
            prefix = m_pathPrefix + "/";
        }
        List<I_CmsXmlContentValue> configurations = xml.getValues(prefix + NODE_RESCONFIG, locale);
        int configurationSize = configurations.size();
        for (int i = 0; i < configurationSize; i++) {
            // loop all configuration nodes
            I_CmsXmlContentValue resConfig = configurations.get(i);
            String resConfigPath = resConfig.getPath() + "/";
            String resType = xml.getStringValue(cms, resConfigPath + NODE_RESTYPE, locale);
            String folder = xml.getStringValue(cms, resConfigPath + NODE_FOLDER, locale);
            // determine the properties to check
            List<I_CmsXmlContentValue> propertyValues = xml.getValues(resConfigPath + NODE_PROPERTY, locale);
            List<String> properties = new ArrayList<String>(propertyValues.size());
            for (int k = propertyValues.size() - 1; k > -1; k--) {
                I_CmsXmlContentValue value = propertyValues.get(k);
                properties.add(value.getStringValue(cms));
            }
            // determine the categories to check
            List<I_CmsXmlContentValue> categoryValues = xml.getValues(resConfigPath + NODE_CATEGORY, locale);
            List<CmsCategory> categories = new ArrayList<CmsCategory>(categoryValues.size());
            for (int k = categoryValues.size() - 1; k > -1; k--) {
                I_CmsXmlContentValue value = categoryValues.get(k);
                String categoryPath = value.getStringValue(cms);
                CmsResource catRes = cms.readResource(categoryPath);
                categories.add(CmsCategoryService.getInstance().getCategory(cms, catRes));
            }
            // add the configuration to the result
            result.add(new CmsCollectorConfiguration(folder, resType, properties, categories));
        }

        return result;
    }

    /** XXX: Workaround <a href="https://github.com/alkacon/alkacon-oamp/issues/31">#31</a> */
    @Override
    public List<CmsResource> getResults(CmsObject cms, String collectorName, String params, int numResults) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
