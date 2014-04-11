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

package com.alkacon.opencms.v8.formgenerator.collector;

import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.v8.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.jsp.CmsJspBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collect forms with a special form id from the database
 * and expose them as a list of {@link CmsFormBean} elements.
 * 
 */
public class CmsFormCollectorBean extends CmsJspBean {

    /** The form id for which form data should be collected. */
    private String m_formId = null;

    /** 
     * The number of form data sets to collect.
     * 
     * -1 means collect as many form data sets as present.
     */
    private int m_numForms = -1;

    /**
     * Collect form data sets this the previously set restrictions.
     * 
     * @return List of form data sets
     */
    public List<CmsFormBean> getFormDataSets() {

        CmsFormDataAccess dataAccess = CmsFormDataAccess.getInstance();
        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterOrderDesc();
        if (m_formId != null) {
            filter = filter.filterFormId(m_formId);
        }
        List<CmsFormDataBean> forms;
        try {
            forms = dataAccess.readForms(filter);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<CmsFormBean>(0);
        }
        // TODO: For performance reasons, handle the number restriction with a filter.
        if (m_numForms == 0) {
            return new ArrayList<CmsFormBean>(0);
        }
        if ((m_numForms > 0) && (m_numForms < forms.size())) {
            forms = forms.subList(0, m_numForms);
        }
        return convertForms(forms);
    }

    /**
     * Convert the collected database entry into CmsFormBeans.
     * 
     * @param forms list of collected form database entries.
     * 
     * @return list of form data sets
     */
    private List<CmsFormBean> convertForms(List<CmsFormDataBean> forms) {

        List<CmsFormBean> list = new ArrayList<CmsFormBean>(forms.size());
        for (CmsFormDataBean form : forms) {
            list.add(new CmsFormBean(getCmsObject(), form));
        }
        return list;
    }

    /**
     * Returns the formId.<p>
     *
     * @return the formId
     */
    public String getFormId() {

        return m_formId;
    }

    /**
     * Sets the formId.<p>
     *
     * @param formId the formId to set
     */
    public void setFormId(String formId) {

        m_formId = formId;
    }

    /**
     * Returns the number of form datasets to collect maximally (-1 means to collect all).<p>
     *
     * @return the number of form datasets to collect maximally (-1 means to collect all)
     */
    public int getNumForms() {

        return m_numForms;
    }

    /**
     * Set the number of form data sets to grab maximally.<p>
     *
     * @param numForms the number of form data sets to grab maximally
     */
    public void setNumForms(int numForms) {

        m_numForms = numForms;
    }

    /**
     * Set the number of form data sets to grab maximally.<p>
     *
     * @param numForms the number of form data sets to grab maximally
     */
    public void setNumForms(String numForms) {

        try {
            m_numForms = Integer.parseInt(numForms);
        } catch (NumberFormatException e) {
            m_numForms = -1;
        }
    }

}
