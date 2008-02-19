/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.registration/src/com/alkacon/opencms/registration/CmsRegistrationInfo.java,v $
 * Date   : $Date: 2008/02/19 13:22:30 $
 * Version: $Revision: 1.1 $
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

package com.alkacon.opencms.registration;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class can be used to display statistical information.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4 
 */
public class CmsRegistrationInfo {

    private static CmsObject m_cms = CmsRegistrationModuleAction.getAdminCms();
    private CmsOrganizationalUnit m_ou;

    /**
     * Bean Constructor.<p>
     */
    public CmsRegistrationInfo() {

        // empty
    }

    /**
     * Returns the activated users.<p>
     * 
     * @return the activated users
     * 
     * @throws CmsException if something goes wrong
     */
    public List getActivatedUsers() throws CmsException {

        List users = new ArrayList(getUsers());
        Iterator itUsers = users.iterator();
        while (itUsers.hasNext()) {
            CmsUser user = (CmsUser)itUsers.next();
            if (!user.isEnabled()) {
                itUsers.remove();
            }
        }
        return users;
    }

    /**
     * Returns the registered but not activated users.<p>
     * 
     * @return the registered but not activated users
     * 
     * @throws CmsException if something goes wrong
     */
    public List getNotActivatedUsers() throws CmsException {

        List users = new ArrayList(getUsers());
        Iterator itUsers = users.iterator();
        while (itUsers.hasNext()) {
            CmsUser user = (CmsUser)itUsers.next();
            if (user.isEnabled()) {
                itUsers.remove();
            }
        }
        return users;
    }

    /**
     * Returns the total number of online users.<p>
     * 
     * @return the total number of online users
     * 
     * @throws CmsException if something goes wrong
     */
    public int getNumOnlineUsers() throws CmsException {

        return getOnlineUsers().size();
    }

    /**
     * Returns the total number of users.<p>
     * 
     * @return the total number of users
     * 
     * @throws CmsException if something goes wrong
     */
    public int getNumUsers() throws CmsException {

        return getUsers().size();
    }

    /**
     * Returns the logged in users.<p>
     * 
     * @return the logged in users
     * 
     * @throws CmsException if something goes wrong
     */
    public List getOnlineUsers() throws CmsException {

        List users = new ArrayList(getUsers());
        Iterator itUsers = users.iterator();
        while (itUsers.hasNext()) {
            CmsUser user = (CmsUser)itUsers.next();
            if (OpenCms.getSessionManager().getSessionInfos(user.getId()).isEmpty()) {
                itUsers.remove();
            }
        }
        return users;
    }

    /**
     * Returns the users.<p>
     * 
     * @return the users
     * 
     * @throws CmsException if something goes wrong
     */
    public List getUsers() throws CmsException {

        return OpenCms.getOrgUnitManager().getUsers(m_cms, m_ou.getName(), false);
    }

    /**
     * Sets the organizational unit fqn.<p>
     *
     * @param oufqn the organizational unit fqn to set
     * 
     * @throws CmsException if something goes wrong 
     */
    public void setOu(String oufqn) throws CmsException {

        m_ou = OpenCms.getOrgUnitManager().readOrganizationalUnit(m_cms, oufqn);
    }
}
