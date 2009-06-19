/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.survey/src/com/alkacon/opencms/survey/CmsFormWorkBean.java,v $
 * Date   : $Date: 2009/06/19 09:41:31 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.survey;

import com.alkacon.opencms.formgenerator.CmsCheckboxField;
import com.alkacon.opencms.formgenerator.CmsFieldFactory;
import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.I_CmsField;
import com.alkacon.opencms.formgenerator.database.CmsFormDataAccess;
import com.alkacon.opencms.formgenerator.database.CmsFormDataBean;
import com.alkacon.opencms.formgenerator.database.CmsFormDatabaseFilter;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.logging.Log;

/**
 * Contains the list with all results from the database.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 7.0.4
 */
public class CmsFormWorkBean {

    /**
     * A comparator for the created date of two resources.<p>
     * 
     */
    private static final Comparator<CmsFormDataBean> COMPARE_DATE_CREATED = new Comparator<CmsFormDataBean>() {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(CmsFormDataBean o1, CmsFormDataBean o2) {

        
            long date1 = o1.getDateCreated();
            long date2 = o2.getDateCreated();

            return (date1 > date2) ? -1 : (date1 < date2) ? 1 : 0;
        }
    };

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormWorkBean.class);

    /** Lazy map with single answers. */
    private Map m_answer;

    /** Lazy map with the answers. */
    private Map m_answers;

    /** the list with the filtered data. */
    private List m_list;

    /** contains the current detail page. **/
    private int m_page;

    /** Mapping from String dbfieldname to I_CmsField class. */
    private Map<String, I_CmsField> m_fieldTypes = new HashMap<String, I_CmsField>();

    /**
     * default constructor.<p>
     */
    public CmsFormWorkBean() {

        super();

    }

    /**
     * Returns a lazy initialized map that provides the answer for each field
     * used as a key in the Map.<p>
     * 
     * @return a lazy initialized map
     */
    public Map getAnswerByField() {

        if (m_answer == null) {
            m_answer = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String value = String.valueOf(input);
                    return getAnswerByField(value);
                }
            });
        }
        return m_answer;
    }

    /**
     * Returns the value of the field of the selected data.<p>
     * 
     * The parameter must contain three values separated with
     * {@link CmsFormReportingBean#PARAM_SEPARATOR}:<p>
     * 
     * <ul>
     * <li>the field name</li>
     * <li>the current page (the selected data)</li>
     * <li>the current type of the field</li>
     * </ul>
     * 
     * @param parameters a string value with three parameters
     * 
     * @return a List of {@link String} contains the values
     */
    public List getAnswerByField(String parameters) {

        // split the parameter with the separator
        List result = new ArrayList();
        String[] param = CmsStringUtil.splitAsArray(parameters, CmsFormReportingBean.PARAM_SEPARATOR);
        if ((param.length <= 2) || (m_list == null)) {
            return result;
        }

        // first look if the page parameter is set correct
        m_page = Integer.parseInt(param[1]);
        if (m_page <= 0) {
            m_page = 1;
        } else if (m_page >= m_list.size()) {
            m_page = m_list.size();
        }

        // get the form bean and get the value
        CmsFormDataBean data = (CmsFormDataBean)getList().get(m_page - 1);
        String value = data.getFieldValue(param[0]);
        if (CmsStringUtil.isEmpty(value)) {
            value = "";
        }

        // only the correct types can be splitted
        I_CmsField formField = (I_CmsField)this.m_fieldTypes.get(param[2]);
        if (CmsFormReportingBean.isFieldTypeCorrect(param[2])) {
            if (isMultiSelectField(formField)) {
                result = CmsStringUtil.splitAsList(value, ',');
            } else {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Returns a lazy initialized map that provides the answers with the count
     * for each field used as a key in the Map.<p>
     * 
     * @return a lazy initialized map
     */
    public Map getAnswers() {

        if (m_answers == null) {
            m_answers = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String value = String.valueOf(input);
                    return getAnswersWithCount(value);
                }
            });
        }
        return m_answers;
    }

    /**
     * Look in the list how much entries have selected this answer.<p>
     * 
     * @param field the answer looking for
     * 
     * @return the count of all answers
     */
    public Map getAnswersWithCount(String field) {

        Map result = new HashMap();
        CmsFormDataBean data;
        String value, answer;
        List answers;
        Integer count;
        I_CmsField formField;
        for (int i = 0; i < m_list.size(); i++) {
            data = (CmsFormDataBean)m_list.get(i);
            value = data.getFieldValue(field);

            // a value can be null if its not a mandatory field
            if (value != null) {
                formField = (I_CmsField)this.m_fieldTypes.get(field);
                if (isMultiSelectField(formField)) {
                    answers = CmsStringUtil.splitAsList(value, ',');
                } else {
                    answers = new ArrayList(1);
                    answers.add(value);
                }
                for (int j = 0; j < answers.size(); j++) {
                    answer = (String)answers.get(j);
                    answer = answer.trim();
                    if (result.containsKey(answer)) {
                        count = (Integer)result.get(answer);
                        result.remove(answer);
                        result.put(answer, new Integer(count.intValue() + 1));
                    } else {
                        result.put(answer, new Integer(1));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the list.<p>
     * 
     * @return the list
     */
    public List getList() {

        return m_list;
    }

    /**
     * Returns the page.<p>
     * 
     * @return the page
     */
    public int getPage() {

        return m_page;
    }

    /**
     * Initialize this work bean and filter the data with the form id and if
     * exists the resource path.<p>
     * 
     * @param formId  the form id
     * @param resourceId the resource id if null then nothing is filtered
     * @param formPath the form path of the web form
     */
    public void init(String formId, String resourceId, String formPath, CmsJspActionElement jsp) {

        CmsObject cms = jsp.getCmsObject();
        Locale locale = jsp.getRequestContext().getLocale();
        m_list = new ArrayList();
        m_page = 1;
        try {
            if (formId != null) {

                CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
                filter = filter.filterFormId(formId);
                if (resourceId != null) {
                    filter = filter.filterResourceId(new CmsUUID(resourceId));
                }
                m_list = CmsFormDataAccess.getInstance().readForms(filter);
                Collections.sort(m_list, COMPARE_DATE_CREATED);

                // get the field types from the form xml content
                CmsResource resource = cms.readResource(formPath);
                CmsFile file = cms.readFile(resource);
                CmsXmlContent xmlContent = CmsXmlContentFactory.unmarshal(cms, file);

                List fieldValues = xmlContent.getValues(CmsForm.NODE_INPUTFIELD, locale);
                int fieldValueSize = fieldValues.size();
                CmsFieldFactory fieldFactory = CmsFieldFactory.getSharedInstance();
                String dbFieldName;

                for (int i = 0; i < fieldValueSize; i++) {
                    I_CmsXmlContentValue inputField = (I_CmsXmlContentValue)fieldValues.get(i);
                    String inputFieldPath = inputField.getPath() + "/";
                    I_CmsField field = null;

                    // get the field from the factory for the specified type
                    String stringValue = xmlContent.getStringValue(cms, inputFieldPath + CmsForm.NODE_FIELDTYPE, locale);
                    field = fieldFactory.getField(stringValue);

                    // get the field label
                    stringValue = xmlContent.getStringValue(cms, inputFieldPath + CmsForm.NODE_FIELDLABEL, locale);
                    dbFieldName = CmsForm.getConfigurationValue(stringValue, "");
                    m_fieldTypes.put(dbFieldName, field);
                }

            }
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.ERR_INIT_WORK_BEAN_2, formId, resourceId), e);
            }
        }
    }

    /**
     * Returns true if the given field (type) allows multi selection.
     * <p>
     * 
     * @param field
     *            the field to check
     * @return true if the given field (type) allows multi selection.
     */

    private boolean isMultiSelectField(I_CmsField field) {

        boolean result = false;
        result = (field instanceof CmsCheckboxField);

        return result;
    }

    /**
     * Sets the list.
     * <p>
     * 
     * @param list
     *            the list to set
     */
    public void setList(List list) {

        m_list = list;
    }

    /**
     * Sets the page.
     * <p>
     * 
     * @param page
     *            the page to set
     */
    public void setPage(int page) {

        m_page = page;
    }

}
