/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFormContentUtil.java,v $
 * Date   : $Date: 2010/02/10 09:25:31 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator;

import org.opencms.file.CmsObject;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.List;
import java.util.Locale;

/**
 * Utility class for accessing form content elements. 
 */
public class CmsFormContentUtil {

    public static final String NODE_NESTED_FORM = "Form";

    /**
     * Creates an xpath prefix which is either empty or consists of a given parent node path, depending on
     * whether the parent node path exists in the XML content.<p>
     *  
     * @param content the XML content
     * @param parentNode the parent node path
     * @param locale the locale to use
     * @return the path prefix (either the empty string or parentNode + "/")
     */
    public static String getNestedPathPrefix(CmsXmlContent content, String parentNode, Locale locale) {

        if (content.hasValue(parentNode, locale)) {
            return parentNode + "/";
        }
        return "";
    }

    /**
     * Returns a content value from the given content, but from a nested path if a NODE_NESTED_FORM node
     * is present.<p>
     * 
     * @param content the XML content
     * @param cms the CmsObject to be used for VFS operations
     * @param path the path of the content value
     * @param locale the locale to use
     * @return the content value from the given path, or a nested path if the NODE_NESTED_FORM content value is present.
     */
    public static String getContentStringValue(CmsXmlContent content, CmsObject cms, String path, Locale locale) {

        String p = getNestedPathPrefix(content, NODE_NESTED_FORM, locale) + path;
        return content.getStringValue(cms, getNestedPathPrefix(content, NODE_NESTED_FORM, locale) + path, locale);
    }

    /**
     * Returns a content value from the given content, but from a nested path if a NODE_NESTED_FORM node is
     * present.<p>
     * 
     * @param content the XML content
     * @param path the path of the content value
     * @param locale the locale to use
     * @return the content value from a given path, or a nested path if the NODE_NESTED_FORM content value is present.
     */
    public static I_CmsXmlContentValue getContentValue(CmsXmlContent content, String path, Locale locale) {

        return content.getValue(getNestedPathPrefix(content, NODE_NESTED_FORM, locale) + path, locale);
    }

    /**
     * Returns a list of content values from the given content, but from a nested path if a NODE_NESTED_FORM node
     * is present.<p>
     *  
     * @param content
     * @param path
     * @param locale
     * @return
     */
    public static List getContentValues(CmsXmlContent content, String path, Locale locale) {

        return content.getValues(getNestedPathPrefix(content, NODE_NESTED_FORM, locale) + path, locale);
    }

}
