/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsNewsletterResourcesCollector.java,v $
 * Date   : $Date: 2007/11/30 11:57:27 $
 * Version: $Revision: 1.8 $
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

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterMailData;
import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.list.A_CmsListExplorerDialog;
import org.opencms.workplace.list.A_CmsListResourceCollector;
import org.opencms.workplace.list.CmsListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Collector for newsletters.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3 
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

        String typeName = CmsNewsletterMailData.RESOURCETYPE_NEWSLETTER_NAME;
        try {
            typeName = CmsNewsletterManager.getMailDataResourceTypeName();
        } catch (Exception e) {
            // should never happen
        }
        int typeId = OpenCms.getResourceManager().getResourceType(typeName).getTypeId();
        CmsResourceFilter filter = CmsResourceFilter.ONLY_VISIBLE_NO_DELETED.addRequireType(typeId);
        return cms.readResources("/", filter, true);
    }

    /**
     * @see org.opencms.workplace.list.A_CmsListResourceCollector#setAdditionalColumns(org.opencms.workplace.list.CmsListItem, org.opencms.workplace.explorer.CmsResourceUtil)
     */
    protected void setAdditionalColumns(CmsListItem item, CmsResourceUtil resUtil) {

        // set the column data for the newsletter send info if present
        String value = "";
        try {
            CmsProperty property = resUtil.getCms().readPropertyObject(
                (String)item.get(A_CmsListExplorerDialog.LIST_COLUMN_NAME),
                CmsNewsletterManager.PROPERTY_NEWSLETTER_DATA,
                false);
            value = property.getValue();
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(value)) {
                // format the information
                List vals = CmsStringUtil.splitAsList(value, CmsProperty.VALUE_LIST_DELIMITER);
                Date date = new Date(Long.parseLong((String)vals.get(0)));
                String groupId = (String)vals.get(1);
                String groupName = "";
                try {
                    CmsGroup group = resUtil.getCms().readGroup(new CmsUUID(groupId));
                    groupName = group.getSimpleName();
                } catch (CmsException e) {
                    // group does not exist
                    groupName = Messages.get().getBundle(getWp().getLocale()).key(Messages.GUI_NEWSLETTER_LIST_DATA_SEND_GROUPDUMMY_0);
                }
                value = Messages.get().getBundle(getWp().getLocale()).key(Messages.GUI_NEWSLETTER_LIST_DATA_SEND_AT_2, date, groupName);
            } else {
                // show the "never sent" message
                value = Messages.get().getBundle(getWp().getLocale()).key(Messages.GUI_NEWSLETTER_LIST_DATA_SEND_NEVER_0);
            }
        } catch (CmsException e) {
            // should never happen
        }
        try {
            item.set(CmsNewsletterListSend.LIST_COLUMN_DATA, value);
        } catch (CmsIllegalArgumentException e) {
            // column not present, ignore
        }
    }
}
