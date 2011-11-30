/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.usagereport/src/com/alkacon/opencms/v8/usagereport/CmsUsageReportJob.java,v $
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

package com.alkacon.opencms.v8.usagereport;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.collectors.CmsDateResourceComparator;
import org.opencms.main.CmsLog;
import org.opencms.scheduler.I_CmsScheduledJob;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

/**
 * Sends a notification mail with the resources which are created new
 * or changed recently.<p>
 * 
 * @author Peter Bonrad
 * 
 * @version $Revision: 1.1 $
 */
public class CmsUsageReportJob implements I_CmsScheduledJob {

    /** The default amount of hours to find the resources for the report. */
    private static final int DEFAULT_HOURS = 24;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsUsageReportJob.class);

    /** The name of the schedule job parameter defining the amount of hours for the resources included. */
    private static final String PARAM_HOURS = "hours";

    /**
     * @see org.opencms.scheduler.I_CmsScheduledJob#launch(org.opencms.file.CmsObject, java.util.Map)
     */
    public String launch(CmsObject cms, Map parameters) throws Exception {

        // read the job parameter for the hours to include the resources for the report
        int hours = DEFAULT_HOURS;
        String hourStr = (String)parameters.get(PARAM_HOURS);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(hourStr)) {
            try {
                hours = Integer.parseInt(hourStr);
            } catch (NumberFormatException ex) {
                // noop -> use default amount
            }
        }

        // read all resources which are modified after the given time
        // which can be new or modified resources
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.HOUR_OF_DAY, hours * -1);
        long time = cal.getTimeInMillis();
        List resources = cms.readResources(
            cms.getRequestContext().getUri(),
            CmsResourceFilter.DEFAULT.addRequireFile().addRequireLastModifiedAfter(time),
            true);

        List newResources = new ArrayList();
        List changedResources = new ArrayList();

        // separate new/changed
        Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            CmsResource res = (CmsResource)iter.next();
            if (res.getDateCreated() >= time) {
                newResources.add(res);
            } else {
                changedResources.add(res);
            }
        }

        // sort the lists
        Collections.sort(newResources, new CmsDateResourceComparator(
            cms,
            Arrays.asList(new String[] {"dateCreated"}),
            false));

        Collections.sort(changedResources, new CmsDateResourceComparator(
            cms,
            Arrays.asList(new String[] {"dateLastModified"}),
            false));

        // send notification mail
        if ((newResources.size() > 0) || (changedResources.size() > 0)) {
            try {
                CmsUsageReportNotification notification = new CmsUsageReportNotification(
                    cms,
                    cms.getRequestContext().currentUser(),
                    newResources,
                    changedResources);

                notification.addMacro("hours", String.valueOf(hours));
                notification.send();
            } catch (Exception ex) {
                String msg = Messages.get().getBundle().key(
                    Messages.ERR_SEND_MAIL_1,
                    cms.getRequestContext().currentUser().getEmail());
                if (LOG.isErrorEnabled()) {
                    LOG.error(msg, ex);
                }
                return msg;
            }

            return Messages.get().getBundle().key(
                Messages.LOG_SEND_MAIL_SUCCESS_1,
                cms.getRequestContext().currentUser().getEmail());
        }

        return Messages.get().getBundle().key(Messages.LOG_NO_RESOURCES_FOR_REPORT_0);
    }

}
