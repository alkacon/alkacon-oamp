/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.survey/src/com/alkacon/opencms/v8/survey/CmsFormReportingBean.java,v $
 * Date   : $Date: 2010/03/19 15:31:15 $
 * Version: $Revision: 1.8 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.survey;

import com.alkacon.opencms.v8.formgenerator.CmsCheckboxField;
import com.alkacon.opencms.v8.formgenerator.CmsFieldFactory;
import com.alkacon.opencms.v8.formgenerator.CmsRadioButtonField;
import com.alkacon.opencms.v8.formgenerator.CmsSelectionField;
import com.alkacon.opencms.v8.formgenerator.I_CmsField;

import org.opencms.file.CmsGroup;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.logging.Log;

/**
 * Contains functions to show the reporting of the webform.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.8 $
 * 
 * @since 7.0.4
 */
public class CmsFormReportingBean extends CmsJspActionElement {

    /** the separator between the parameters.*/
    public static final String PARAM_SEPARATOR = "?_?";

    /** the separator between light and dark colors.**/
    public static final int SEP_DARK_LIGHT = (3 * 255) / 2;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormReportingBean.class);

    /** 
     * Till now, only special types can be reported.<p>
     * 
     * Types:<p>
     * <ul>
     *  <li> {@link CmsCheckboxField} </li>
     *  <li> {@link CmsRadioButtonField} </li>
     * </ul>
     * 
     * @param fieldType the type to check
     * 
     * @return <code>true</code> is from the correct type or otherwise <code>false</code> 
     */
    public static boolean isFieldTypeCorrect(String fieldType) {

        boolean result = false;
        I_CmsField field = CmsFieldFactory.getSharedInstance().getField(fieldType);
        if ((field instanceof CmsCheckboxField)
            || (field instanceof CmsRadioButtonField)
            || (field instanceof CmsSelectionField)) {
            result = true;
        }
        return result;
    }

    /** Lazy map which checks if the type can be reported or not. */
    private Map m_checktype;

    /** Lazy map with the color of the text if white or black.*/
    private Map m_color;

    /** Lazy map with the groups.*/
    private Map m_group;

    /** Lazy map with label of the fields.*/
    private Map m_label;

    /** Lazy map with the reporting bean.*/
    private Map m_reporting;

    /**
     * Constructor, creates the necessary form configuration objects.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsFormReportingBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);

    }

    /**
     * Checks if the given type is from the correct type to display in the report.<p>
     * 
     * @return <code>true</code> is from the correct type or otherwise <code>false</code> 
     */
    public Map getFieldTypeCorrect() {

        if (m_checktype == null) {
            m_checktype = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String value = String.valueOf(input);
                    return Boolean.valueOf(isFieldTypeCorrect(value));
                }
            });
        }
        return m_checktype;
    }

    /**
     * Returns a lazy initialized map that provides the label and database label for each value used as a key in the Map.<p> 
     *  
     * @return a lazy initialized map
     */
    public Map getLabeling() {

        if (m_label == null) {
            m_label = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String value = String.valueOf(input);
                    String[] result = new String[] {value, value};
                    if (!CmsStringUtil.isEmpty(value)) {
                        String[] array = CmsStringUtil.splitAsArray(value, '|');
                        if (array.length > 1) {
                            result = array;
                        }
                    }
                    return result;
                }
            });
        }
        return m_label;
    }

    /**
     * Returns the PARAM_SEPARATOR.<p>
     *
     * @return the PARAM_SEPARATOR
     */
    public String getSeparator() {

        return PARAM_SEPARATOR;
    }

    /**
     * Returns a lazy initialized map that provides if the user can see the detail page or not 
     * for each group used as a key in the Map.<p> 
     * 
     * @return a lazy initialized map
     */
    public Map getShowDetail() {

        if (m_group == null) {
            m_group = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    String value = String.valueOf(input);
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
                        return new Boolean(true);
                    }
                    try {
                        CmsUser user = getCmsObject().getRequestContext().currentUser();
                        List list = getCmsObject().getGroupsOfUser(user.getName(), false);
                        CmsGroup group;
                        for (int i = 0; i < list.size(); i++) {
                            group = (CmsGroup)list.get(i);
                            if (group.getName().equals(value)) {
                                return new Boolean(true);
                            }
                        }
                    } catch (Exception e) {
                        // NOOP
                    }

                    return new Boolean(false);
                }
            });
        }
        return m_group;
    }

    /**
     * Returns a lazy initialized map that provides the color of the label for each background color used as a key in the Map.<p> 
     * 
     * Dark background color returns white.<p>
     * Light background color returns black.<p>
     * 
     * @return a lazy initialized map
     */
    public Map getTextColor() {

        if (m_color == null) {
            m_color = LazyMap.decorate(new HashMap(), new Transformer() {

                /**
                 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
                 */
                public Object transform(Object input) {

                    try {
                        // get the color from the given value
                        String value = String.valueOf(input);
                        Color color = Color.decode(value);

                        // look if its a dark color or a light
                        int dezColor = color.getBlue() + color.getGreen() + color.getRed();
                        if (dezColor < SEP_DARK_LIGHT) {
                            return "#FFF";
                        }

                    } catch (Exception e) {
                        // NOOP
                    }
                    return "#000";
                }
            });
        }
        return m_color;
    }

    /**
     * Returns the needed {@link CmsFormWorkBean} with the given parameters.<p>
     * 
     * @param formid the form id
     * 
     * @param formPath the path of the form
     * 
     * @return the work bean
     */
    public CmsFormWorkBean getReporting(String formid, String formPath) {

        CmsFormWorkBean result = new CmsFormWorkBean();
        String[] parameters = CmsStringUtil.splitAsArray(formid, PARAM_SEPARATOR);

        if (parameters.length > 0) {
            String formId = parameters[0];
            String resId = null;
            if (parameters.length > 1) {
                resId = parameters[1];
            }
            result.init(formId, resId, formPath, this);
        }
        return result;

    }
}
