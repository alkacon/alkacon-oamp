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

package com.alkacon.opencms.v8.calendar;

import com.alkacon.opencms.v8.calendar.shared.rpc.I_CmsSerialDateService;

import org.opencms.gwt.CmsGwtService;
import org.opencms.gwt.CmsRpcException;
import org.opencms.widgets.CmsSelectWidgetOption;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Provides general core services.<p>
 * 
 * @since 8.0.0
 * 
 * @see org.opencms.gwt.CmsCoreService
 * @see org.opencms.gwt.shared.rpc.I_CmsCoreService
 * @see org.opencms.gwt.shared.rpc.I_CmsCoreServiceAsync
 */
public class CmsSerialDateService extends CmsGwtService implements I_CmsSerialDateService {

    /** Serialization uid. */
    private static final long serialVersionUID = 5915848952948986279L;

    /**
     * @see com.alkacon.opencms.v8.calendar.shared.rpc.I_CmsSerialDateService#getSeriaDateSelection(java.lang.String, java.lang.String, int)
     */
    public Map<String, String> getSeriaDateSelection(String selectValues, String locale, int maxCount)
    throws CmsRpcException {

        Map<String, String> result = null;
        try {
            LinkedList<CmsSelectWidgetOption> selectOptions = CmsSerialDateSelectWidget.parseOptions(
                selectValues,
                new Locale(locale),
                maxCount);
            result = new LinkedHashMap<String, String>();
            Iterator<CmsSelectWidgetOption> it = selectOptions.iterator();
            while (it.hasNext()) {
                CmsSelectWidgetOption selectOption = it.next();
                result.put(selectOption.getValue(), selectOption.getOption());
            }
        } catch (Throwable t) {
            error(t);
        }
        return result;
    }
}
