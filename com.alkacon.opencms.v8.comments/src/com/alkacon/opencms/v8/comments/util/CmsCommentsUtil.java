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

package com.alkacon.opencms.v8.comments.util;

import org.opencms.i18n.CmsEncoder;
import org.opencms.util.CmsHtmlStripper;
import org.opencms.workplace.CmsWorkplace;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/** Utilities used in the module */
public final class CmsCommentsUtil {

    /** Name of the comments module */
    public static final String MODULE_NAME = "com.alkacon.opencms.v8.comments";

    /** Default resource bundle for comments list */
    public static final String COMMENTS_DEFAULT_BUNDLE = CmsCommentsUtil.MODULE_NAME + ".comments";

    /** Module Parameter for comments list resource bundle */
    public static final String MODULE_PARAM_COMMENTS_BUNDLE = "bundle-comments";

    /** Default resource bundle for last-comments list */
    public static final String LAST_COMMENTS_DEFAULT_BUNDLE = CmsCommentsUtil.MODULE_NAME + ".lastcomments";

    /** Module Parameter for last-comments list resource bundle */
    public static final String MODULE_PARAM_LAST_COMMENTS_BUNDLE = "bundle-last-comments";

    /** Path to the error file used, when a stringtemplate cannot be loaded */
    public static final String VFS_PATH_ERROR_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + CmsCommentsUtil.MODULE_NAME
        + "/resources/templates/error.st";

    /**
     * @param longDate Date as long
     * @param locale The locale to format the date in
     * @param dateFormat Format of the date, e.g., DateFormat.LONG 
     * @param timeFormat Format of the time, e.g., DateFormat.SHORT
     * @return The formatted date
     */
    public static final String formatDateTime(
        final long longDate,
        final Locale locale,
        final int dateFormat,
        final int timeFormat) {

        Date date = new Date(longDate);
        DateFormat df = DateFormat.getDateInstance(dateFormat, locale);
        DateFormat tf = DateFormat.getTimeInstance(timeFormat, locale);
        StringBuffer buf = new StringBuffer();
        buf.append(df.format(date));
        buf.append(" ");
        buf.append(tf.format(date));
        return buf.toString();
    }

    public static final String formatDate(final long longDate, final Locale locale, final int dateFormat) {

        Date date = new Date(longDate);
        DateFormat df = DateFormat.getDateInstance(dateFormat, locale);
        StringBuffer buf = new StringBuffer();
        buf.append(df.format(date));
        return buf.toString();
    }

    /**
     * Checks if a string contains script, form or input tags. If yes, then all HTML gets stripped.
     * Finally, for each string HTML is escaped. 
     * 
     * @param content The string to secure
     * @return the "secured" string
     */
    public static final String secureContent(final String content) {

        String result = content;
        String testContent = result.replace(" ", "");
        if (testContent.contains("<script") || testContent.contains("<form") || testContent.contains("<input")) {
            CmsHtmlStripper stripper = new CmsHtmlStripper();
            try {
                result = stripper.stripHtml(result);
            } catch (org.htmlparser.util.ParserException e) {
                result = "Content was removed because of invalid format.";
            }
        }
        return CmsEncoder.escapeXml(result);
    }

}
