/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.counter/src/com/alkacon/opencms/counter/CmsExampleUseBean.java,v $
 * Date   : $Date: 2008/02/28 08:16:45 $
 * Version: $Revision: 1.2 $
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

package com.alkacon.opencms.counter;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * This class shows in easy examples how to use this module.<p>
 * 
 * @author Anja Röttgers
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.3
 */
public class CmsExampleUseBean extends CmsJspActionElement {

    /** Describes the first component of the filename.<p>*/
    private static final String FILENAME_PREFIX = "filename_";

    /** Describes the last component of the filename.<p>*/
    private static final String FILENAME_SUFFIX = ".html";

    /** the title of the download.*/
    private String m_title;

    /**
     * Empty constructor.<p>
     */
    public CmsExampleUseBean() {

        super();
    }

    /**
     * Constructor, with parameters.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsExampleUseBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * This function generates a unique filename with following structure.<p>
     * 
     * Structure:   "filename_{xxxxx}.html" 
     *              {xxxxx} is a five-figure sum
     * 
     * @param counterKey the key of the counter
     * 
     * @return the filename with the structure "filename_{xxxxx}.html" 
     * 
     * @throws CmsException if something goes wrong
     */
    public String generateHtmlFilename(String counterKey) throws CmsException {

        StringBuffer result = new StringBuffer();
        String number = "00000";
        int dbNumber = getCounterManager().incrementCounter(counterKey);
        number = number + dbNumber;
        number = number.substring(number.length() - 5, number.length());

        //append the components of the fileName
        result.append(FILENAME_PREFIX).append(number).append(FILENAME_SUFFIX);

        return result.toString();
    }

    /**
     * Returns the counter manager from the action class of the module.<p>
     * 
     * @return the counter manager of the module or a new one
     */
    public CmsCounterManager getCounterManager() {

        CmsCounterManager result = null;
        // Get the module
        CmsModule module = OpenCms.getModuleManager().getModule(CmsCounterManager.MODULE_NAME);
        // Get the action class
        result = (CmsCounterManager)module.getActionInstance();
        if (result == null) {
            result = new CmsCounterManager();
        }
        return result;
    }

    /**
     * Increments the counter for the current download.<p>
     * 
     * 
     * Example of use in a jsp:
     * <%
     *  CmsExampleUseBean cms = new CmsExampleUseBean(pageContext, request, response);
     *  pageContext.setAttribute("cms", cms);
     * %>
     * ...
     * <c:out value="${cms.incrementCounter}"/>
     *  
     * 
     * @return the incremented counter 
     * 
     * @throws CmsException if an exception occurred.
     */
    public String getIncrementCounter() throws CmsException {

        String counterKey = getTitle();
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(counterKey)) {
            return String.valueOf(getCounterManager().incrementCounter(counterKey));
        }
        return null;
    }

    /**
     * Returns the title.<p>
     *
     * @return the title
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * Sets the title.<p>
     *
     * @param title the title to set
     */
    public void setTitle(String title) {

        m_title = title;
    }

}
