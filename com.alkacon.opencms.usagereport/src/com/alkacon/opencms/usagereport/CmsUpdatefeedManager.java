/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.usagereport/src/com/alkacon/opencms/usagereport/CmsUpdatefeedManager.java,v $
 * Date   : $Date: 2009/02/05 09:56:20 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2008 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.usagereport;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsObject;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;

/**
 * Saves an admin cms object to use for creating the update feeds.<p>
 * 
 * @author Ruediger Kurz 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeedManager extends A_CmsModuleAction {

    /**
     * The admin cms object.<p>
     */
    private CmsObject m_adminCms;

    /**
     * @see org.opencms.module.A_CmsModuleAction#initialize(org.opencms.file.CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)
     */
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        // store the admin CmsObject as member
        m_adminCms = adminCms;
    }

    /**
     * Returns the admin cms-object.<p>
     *
     * @return the admin cms-object
     */
    public CmsObject getAdminCms() {

        return m_adminCms;
    }
}
