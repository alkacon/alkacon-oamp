/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.htmlcleaner/src/com/alkacon/opencms/htmlcleaner/CmsHtmlCleanerModuleAction.java,v $
 * Date   : $Date: 2011/04/01 10:08:03 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2011 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.htmlcleaner;

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

/**
 * The module action class is used to read the HTML cleaner configuration from the VFS.<p>
 * 
 * This class is needed to get a {@link CmsObject} instance to read the HTML cleaner configuration file.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsHtmlCleanerModuleAction extends A_CmsModuleAction {

    /** The VFS path to the configuration file for the cleaner. */
    private static final String VFS_PATH_CONFIGURATION = CmsWorkplace.VFS_PATH_MODULES
        + CmsHtmlCleanerModuleAction.class.getPackage().getName()
        + "/configuration/config.xml";

    /** The OpenCms administrator users context that is used to read the configuration file. */
    private CmsObject m_adminCms;

    /**
     * @see org.opencms.module.A_CmsModuleAction#initialize(org.opencms.file.CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)
     */
    @Override
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        m_adminCms = adminCms;
    }

    /**
     * Returns the initialized HTML cleaner for the conversion.<p>
     * 
     * @return the initialized HTML cleaner for the conversion
     * 
     * @throws CmsException if reading the configuration from VFS fails
     */
    protected CmsHtmlCleanerConfiguration getCmsHtmlCleanerConfiguration() throws CmsException {

        CmsVfsMemoryObjectCache cache = CmsVfsMemoryObjectCache.getVfsMemoryObjectCache();
        // try to get cached configuration
        CmsHtmlCleanerConfiguration config = (CmsHtmlCleanerConfiguration)cache.getCachedObject(
            m_adminCms,
            VFS_PATH_CONFIGURATION);
        if (config == null) {
            // no configuration found in cache, read it from VFS
            CmsFile file = m_adminCms.readFile(VFS_PATH_CONFIGURATION);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(m_adminCms, file);
            config = new CmsHtmlCleanerConfiguration(m_adminCms, content);
            // store initialized configuration in cache
            cache.putCachedObject(m_adminCms, VFS_PATH_CONFIGURATION, config);
        }
        return config;
    }

}
