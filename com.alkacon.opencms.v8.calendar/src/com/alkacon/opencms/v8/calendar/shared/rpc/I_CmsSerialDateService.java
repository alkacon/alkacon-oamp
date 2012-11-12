/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.calendar.shared.rpc;

import org.opencms.gwt.CmsRpcException;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Provides general core services.<p>
 * 
 * @since 8.0.0
 * 
 * @see org.opencms.gwt.CmsCoreService
 * @see org.opencms.gwt.shared.rpc.I_CmsCoreService
 * @see org.opencms.gwt.shared.rpc.I_CmsCoreServiceAsync
 */
public interface I_CmsSerialDateService extends RemoteService {

    /**
     * Returns the serial date selection option.<p>
     * 
     * @param selectValues the parameter to generate the selection option
     * @param locale the locale
     * @param maxCount 
     * 
     * @return the serial date selection option
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    Map<String, String> getSeriaDateSelection(String selectValues, String locale, int maxCount) throws CmsRpcException;
}
