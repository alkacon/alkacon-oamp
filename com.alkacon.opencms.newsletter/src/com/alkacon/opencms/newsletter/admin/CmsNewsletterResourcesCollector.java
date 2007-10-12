/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNewsletterResourcesCollector.java,v $
 * Date   : $Date: 2007/10/12 15:19:08 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.list.A_CmsListExplorerDialog;
import org.opencms.workplace.list.A_CmsListResourceCollector;
import org.opencms.workplace.list.CmsListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collector for newsletters.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.1.0 
 */
public class CmsNewsletterResourcesCollector extends A_CmsListResourceCollector {

    /** Parameter of the default collector name. */
    public static final String COLLECTOR_NAME = "newsletterresources";

    /**
     * Constructor, creates a new instance.<p>
     * 
     * @param wp the workplace object
     */
    public CmsNewsletterResourcesCollector(A_CmsListExplorerDialog wp) {

        super(wp);
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCollectorNames()
     */
    public List getCollectorNames() {

        List names = new ArrayList();
        names.add(COLLECTOR_NAME);
        return names;
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListResourceCollector#getResources(org.opencms.file.CmsObject, java.util.Map)
     */
    public List getResources(CmsObject cms, Map params) throws CmsException {

        int typeId = OpenCms.getResourceManager().getResourceType(CmsNewsletterManager.RESOURCETYPE_NEWSLETTER_NAME).getTypeId();
        CmsResourceFilter filter = CmsResourceFilter.ONLY_VISIBLE_NO_DELETED.addRequireType(typeId);
        return cms.readResources("/", filter, true);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListResourceCollector#setAdditionalColumns(org.opencms.workplace.list.CmsListItem, org.opencms.workplace.explorer.CmsResourceUtil)
     */
    protected void setAdditionalColumns(CmsListItem item, CmsResourceUtil resUtil) {

        // 
    }
}
