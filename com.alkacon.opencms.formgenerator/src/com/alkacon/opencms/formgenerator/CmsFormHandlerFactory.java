/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFormHandlerFactory.java,v $
 * Date   : $Date: 2011/03/09 15:14:34 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator;

import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class CmsFormHandlerFactory {

    /** Context attribute name for the form handler attribute. */
    public static final String ATTRIBUTE_FORMHANDLER = "cmsF";

    /** Context attribute name for the add message attribute. */
    public static final String ATTRIBUTE_ADDMESSAGE = "addMessage";

    public static CmsFormHandler create(PageContext context, HttpServletRequest req, HttpServletResponse res)
    throws Exception {

        return create(context, req, res, null, null);
    }

    public static CmsFormHandler create(
        PageContext context,
        HttpServletRequest req,
        HttpServletResponse res,
        String formConfigUri) throws Exception {

        return create(context, req, res, null, formConfigUri);
    }

    public static CmsFormHandler create(
        PageContext context,
        HttpServletRequest req,
        HttpServletResponse res,
        String clazz,
        String formConfigUri) throws Exception {

        CmsFormHandler formHandler = null;
        CmsFormHandler formHandlerFromContext = null;
        // check if there is a form handler instance in the page context, necessary for the survey module
        if (context != null) {
            formHandlerFromContext = (CmsFormHandler)context.getAttribute(ATTRIBUTE_FORMHANDLER);
        }
        if (formHandlerFromContext == null) {
            // no handler found in page context
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(clazz)) {
                // no specific class name specified, create default form handler
                formHandler = new CmsFormHandler();
            } else {
                // create instance of specified form handler class name
                formHandler = (CmsFormHandler)Class.forName(clazz).newInstance();
            }
            if ((context != null) && (req != null) && (res != null)) {
                // initialize the form handler
                formHandler.init(context, req, res, formConfigUri);
            }

        } else {
            // use form handler found in page context
            formHandler = formHandlerFromContext;
        }

        // get localized messages from context to create the form
        if ((context != null) && (context.getAttribute(ATTRIBUTE_ADDMESSAGE) != null)) {
            formHandler.addMessages(new CmsMessages(
                (String)context.getAttribute(ATTRIBUTE_ADDMESSAGE),
                formHandler.getRequestContext().getLocale()));
        }

        return formHandler;
    }

}
