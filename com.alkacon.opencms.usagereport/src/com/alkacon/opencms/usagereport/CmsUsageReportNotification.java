/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.usagereport/src/com/alkacon/opencms/usagereport/CmsUsageReportNotification.java,v $
 * Date   : $Date: 2008/12/10 14:03:45 $
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

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.notification.A_CmsNotification;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class to send a notification email to an OpenCms user with a list of resources
 * which are newly created or recently changed.<p>
 * 
 * @author Peter Bonrad
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUsageReportNotification extends A_CmsNotification {

    /** The path to the xml content with the subject, header and footer of the notification e-mail.<p> */
    public static final String NOTIFICATION_CONTENT = "/system/workplace/admin/notification/usage-report-notification";

    /** Server name and opencms context. */
    private static final String SERVER_CONTEXT = OpenCms.getSiteManager().getWorkplaceServer()
        + OpenCms.getSystemInfo().getOpenCmsContext();

    /** Uri of the workplace folder. */
    private static final String WORKPLACE_URI = SERVER_CONTEXT + CmsWorkplace.VFS_PATH_WORKPLACE;

    /** The list with modified resources. */
    private List m_modifiedResources;

    /** The list with new resources. */
    private List m_newResources;

    /**
     * Default constructor.<p>
     * 
     * @param cms the cms object to use
     * @param receiver the notification receiver
     * @param newResources the list with the new resources to list in the notification of the usage report
     * @param modifiedResources the list with the modified resources to list in the notification of the usage report
     */
    public CmsUsageReportNotification(CmsObject cms, CmsUser receiver, List newResources, List modifiedResources) {

        super(cms, receiver);
        m_newResources = newResources;
        m_modifiedResources = modifiedResources;
    }

    /**
     * @see org.opencms.notification.A_CmsNotification#generateHtmlMsg()
     */
    protected String generateHtmlMsg() {

        StringBuffer result = new StringBuffer();

        CmsMessages messages = Messages.get().getBundle(getLocale());

        // add list with new resources to the mail body
        if ((m_newResources != null) && (m_newResources.size() > 0)) {
            result.append("<h2>");
            result.append(messages.key("mail.resources.new"));
            result.append("</h2>");

            result.append(renderResourcesList(m_newResources, false));
        }

        // add list with modified resources to the mail body
        if ((m_modifiedResources != null) && (m_modifiedResources.size() > 0)) {
            result.append("<h2>");
            result.append(messages.key("mail.resources.modified"));
            result.append("</h2>");

            result.append(renderResourcesList(m_modifiedResources, true));
        }

        // add macro for current time
        addMacro("jobStart", Messages.get().getBundle(getLocale()).getDateTime(System.currentTimeMillis()));

        return result.toString();
    }

    /**
     * @see org.opencms.notification.A_CmsNotification#getNotificationContent()
     */
    protected String getNotificationContent() {

        return NOTIFICATION_CONTENT;
    }

    /**
     * Renders the list with the given resources.<p>
     * 
     * @param resources the resources to render
     * @param useModifiedDate if to use the last modification date or the creation date
     * 
     * @return the (HTML)text to place in the notification 
     */
    protected String renderResourcesList(List resources, boolean useModifiedDate) {

        StringBuffer result = new StringBuffer();
        result.append("<table>");

        CmsMessages messages = Messages.get().getBundle(getLocale());

        // print table header
        result.append("<tr>");
        result.append("<th style='text-align:left;'>");
        result.append(messages.key("mail.resources"));
        result.append("</th>");
        result.append("<th style='text-align:left;'>");
        result.append(messages.key("mail.user"));
        result.append("</th>");
        result.append("<th style='text-align:left;'>");
        result.append(messages.key("mail.date"));
        result.append("</th>");
        result.append("</tr>");

        Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            result.append("<tr>");
            CmsResource res = (CmsResource)iter.next();

            String resourcePath = res.getRootPath();
            String siteRoot = OpenCms.getSiteManager().getSiteRoot(resourcePath);
            String sitePath = resourcePath;
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(siteRoot)) {
                sitePath = resourcePath.substring(siteRoot.length());
            }

            // the path of the resource with the link to open
            Map params = new HashMap();
            params.put(CmsWorkplace.PARAM_WP_SITE, siteRoot);
            params.put(CmsDialog.PARAM_RESOURCE, sitePath);
            result.append("<td><a href=\"");
            result.append(CmsRequestUtil.appendParameters(WORKPLACE_URI + "commons/displayresource.jsp", params, false));
            result.append("\">");
            result.append(resourcePath);
            result.append("</a></td>");

            // the user who changed the resource
            result.append("<td>");
            try {
                CmsUUID userId = res.getUserLastModified();
                CmsUser user = getCmsObject().readUser(userId);
                result.append(user.getName());
            } catch (CmsException ex) {
                result.append("&nbsp;");
            }
            result.append("</td>");

            // the last modification date
            result.append("<td>");
            if (useModifiedDate) {
                result.append(messages.getDateTime(res.getDateLastModified()));
            } else {
                result.append(messages.getDateTime(res.getDateCreated()));
            }
            result.append("</td>");

            result.append("</tr>");
        }

        result.append("</table>");
        return result.toString();
    }

}
