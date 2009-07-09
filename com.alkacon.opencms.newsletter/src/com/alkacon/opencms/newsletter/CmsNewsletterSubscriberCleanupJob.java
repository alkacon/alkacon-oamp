/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/CmsNewsletterSubscriberCleanupJob.java,v $
 * Date   : $Date: 2009/07/09 09:30:12 $
 * Version: $Revision: 1.5 $
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

package com.alkacon.opencms.newsletter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.scheduler.I_CmsScheduledJob;
import org.opencms.security.CmsOrganizationalUnit;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

/**
 * A schedulable OpenCms job that deletes inactive newsletter subscriber users.<p>
 * 
 * Job parameters:<p>
 * <dl>
 * <dt><code>maxage={time in hours}</code></dt>
 * <dd>Specifies the maximum age (in hours) subscribers can be inactive before they are removed from the system.</dd>
 * </dl>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.5 $ 
 * 
 * @since 7.0.3 
 */
public class CmsNewsletterSubscriberCleanupJob implements I_CmsScheduledJob {

    /** Maximum age parameter. */
    public static final String PARAM_MAXAGE = "maxage";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsNewsletterSubscriberCleanupJob.class);

    /**
     * @see org.opencms.scheduler.I_CmsScheduledJob#launch(CmsObject, Map)
     */
    public String launch(CmsObject cms, Map parameters) throws Exception {

        String maxAgeStr = (String)parameters.get(PARAM_MAXAGE);
        float maxAge;
        try {
            maxAge = Float.parseFloat(maxAgeStr);
        } catch (Exception e) {
            // in case of an error, use maxage of one week
            maxAge = 24f * 7f;
        }

        // calculate oldest possible date for the unconfirmed subscribers
        long expireDate = System.currentTimeMillis() - (long)(maxAge * 60f * 60f * 1000f);

        // now perform the image cache cleanup
        int count = removeInactiveSubscribers(cms, expireDate);

        return Messages.get().getBundle().key(Messages.LOG_NEWSLETTER_CLEANUP_FINISHED_COUNT_1, new Integer(count));
    }

    /**
     * Removes the inactive subscribers from the newsletter organizational units that are not activated.<p>
     * 
     * @param cms the configured job users context
     * @param expireDate the expiration date, if a subscriber user was created before that date, it will be deleted
     * @return the number of deleted subscribers
     */
    private int removeInactiveSubscribers(CmsObject cms, long expireDate) {

        int count = 0;
        try {
            List newsletterUnits = CmsNewsletterManager.getOrgUnits(cms);
            Iterator i = newsletterUnits.iterator();
            // loop the newsletter organizational units
            while (i.hasNext()) {
                CmsOrganizationalUnit ou = (CmsOrganizationalUnit)i.next();
                List users = OpenCms.getOrgUnitManager().getUsers(cms, ou.getName(), false);
                Iterator k = users.iterator();
                // loop subscribers found in the current newsletter ou
                while (k.hasNext()) {
                    CmsUser user = (CmsUser)k.next();
                    // get the additional info value for the subscriber
                    Boolean active = (Boolean)user.getAdditionalInfo(CmsNewsletterManager.USER_ADDITIONALINFO_ACTIVE);
                    if ((active != null) && !active.booleanValue()) {
                        // found additional info & value is false, check creation date
                        if (user.getDateCreated() < expireDate) {
                            // user is older than specified maximum age, delete it
                            cms.deleteUser(user.getName());
                            count++;
                        }
                    }
                }
            }
        } catch (CmsException e) {
            // error removing users, log error
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_NEWSLETTER_CLEANUP_ERROR_DELETING_0), e);
            }
        }
        return count;
    }
}