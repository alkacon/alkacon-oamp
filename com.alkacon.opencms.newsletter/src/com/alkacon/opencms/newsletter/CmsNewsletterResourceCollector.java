/*
 * File   : $Source: /home/cvs/EbkModules/de.erzbistumkoeln.newsletter/src/de/erzbistumkoeln/newsletter/CmsNewsletterResourceCollector.java,v $
 * Date   : $Date: 2010-10-14 12:44:53 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter;

import org.opencms.file.CmsDataAccessException;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.collectors.A_CmsResourceCollector;
import org.opencms.file.collectors.CmsExtendedCollectorData;
import org.opencms.file.collectors.Messages;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A default resource collector that supports flexible sorting based on resource dates.<p>
 *
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.2
 */
public class CmsNewsletterResourceCollector extends A_CmsResourceCollector {

    /** Static array of the collectors implemented by this class. */
    private static final String[] COLLECTORS = {"allNewslettersInFolder", "allNewslettersInSubTree"};

    /** Array list for fast collector name lookup. */
    private static final List<String> COLLECTORS_LIST = Collections.unmodifiableList(Arrays.asList(COLLECTORS));

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCollectorNames()
     */
    public List<String> getCollectorNames() {

        return COLLECTORS_LIST;
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCreateLink(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public String getCreateLink(CmsObject cms, String collectorName, String param)
    throws CmsDataAccessException, CmsException {

        // if action is not set, use default action
        if (collectorName == null) {
            collectorName = COLLECTORS[0];
        }

        switch (COLLECTORS_LIST.indexOf(collectorName)) {
            case 0:
            case 1:
                // "allNewslettersInFolder", "allNewslettersInSubTree"
                return null;
            default:
                throw new CmsDataAccessException(Messages.get().container(
                    Messages.ERR_COLLECTOR_NAME_INVALID_1,
                    collectorName));
        }
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getCreateParam(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public String getCreateParam(CmsObject cms, String collectorName, String param) throws CmsDataAccessException {

        // if action is not set, use default action
        if (collectorName == null) {
            collectorName = COLLECTORS[0];
        }

        switch (COLLECTORS_LIST.indexOf(collectorName)) {
            case 0:
            case 1:
                // "allNewslettersInFolder", "allNewslettersInSubTree"
                return null;
            default:
                throw new CmsDataAccessException(Messages.get().container(
                    Messages.ERR_COLLECTOR_NAME_INVALID_1,
                    collectorName));
        }
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getResults(org.opencms.file.CmsObject, java.lang.String, java.lang.String)
     */
    public List<CmsResource> getResults(CmsObject cms, String collectorName, String param)
    throws CmsDataAccessException, CmsException {

        return getResults(cms, collectorName, param, -1);
    }

    /**
     * @see org.opencms.file.collectors.I_CmsResourceCollector#getResults(org.opencms.file.CmsObject, java.lang.String, java.lang.String, int)
     */
    public List<CmsResource> getResults(CmsObject cms, String collectorName, String param, int numResults)
    throws CmsDataAccessException, CmsException {

        // if action is not set use default
        if (collectorName == null) {
            collectorName = COLLECTORS[0];
        }

        switch (COLLECTORS_LIST.indexOf(collectorName)) {
            case 0:
                // "allNewslettersInFolder"
                return allNewslettersInFolder(cms, param, false, numResults);
            case 1:
                // "allNewslettersInSubTree"
                return allNewslettersInFolder(cms, param, true, numResults);
            default:
                throw new CmsDataAccessException(Messages.get().container(
                    Messages.ERR_COLLECTOR_NAME_INVALID_1,
                    collectorName));
        }
    }

    /**
     * Returns a list of all newsletter resources in the folder pointed to by the parameter 
     * sorted by the send dates.<p>
     * 
     * @param cms the current CmsObject
     * @param param must contain an extended collector parameter set as described by {@link CmsExtendedCollectorData}
     * @param tree if true, look in folder and all child folders, if false, look only in given folder
     *  @param numResults the number of results that should be returned
     * 
     * @return a list of all newsletter resources in the folder pointed to by the parameter sorted by the send dates
     * 
     * @throws CmsException if something goes wrong
     */
    protected List<CmsResource> allNewslettersInFolder(CmsObject cms, String param, boolean tree, int numResults)
    throws CmsException {

        CmsExtendedCollectorData data = new CmsExtendedCollectorData(param);
        String foldername = CmsResource.getFolderPath(data.getFileName());
        List<String> extendedParameters = data.getAdditionalParams();
        boolean onlySent = true;
        boolean asc = false;
        if (extendedParameters.size() > 0) {
            onlySent = Boolean.valueOf(extendedParameters.get(0)).booleanValue();
        }
        if (extendedParameters.size() > 1) {
            asc = Boolean.valueOf(extendedParameters.get(1)).booleanValue();
        }

        CmsResourceFilter filter = CmsResourceFilter.DEFAULT.addRequireType(data.getType()).addExcludeFlags(
            CmsResource.FLAG_TEMPFILE);
        List<CmsResource> result = cms.readResources(foldername, filter, tree);

        if (onlySent) {
            List<CmsResource> sentResources = new ArrayList<CmsResource>(result.size());
            for (Iterator<CmsResource> i = result.iterator(); i.hasNext();) {
                CmsResource currRes = i.next();
                String propValue = "";
                try {
                    propValue = cms.readPropertyObject(currRes, CmsNewsletterManager.PROPERTY_NEWSLETTER_DATA, false).getValue();
                } catch (CmsException e) {
                    // failed to read the property, ignore
                }
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(propValue)) {
                    sentResources.add(currRes);
                }
            }
            result = sentResources;
        }

        // a special date comparator is used to sort the resources
        CmsNewsletterDateResourceComparator comparator = new CmsNewsletterDateResourceComparator(cms, asc);
        Collections.sort(result, comparator);

        if (numResults > data.getCount()) {
            return shrinkToFit(result, numResults);
        }
        return shrinkToFit(result, data.getCount());
    }
}