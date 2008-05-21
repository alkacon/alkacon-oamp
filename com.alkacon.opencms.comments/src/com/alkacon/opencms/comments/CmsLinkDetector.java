/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/CmsLinkDetector.java,v $
 * Date   : $Date: 2008/05/21 11:58:05 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
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

package com.alkacon.opencms.comments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides link recognition and substitution.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.5
 */
public final class CmsLinkDetector {

    /**
     * Private constructor.<p>
     */
    private CmsLinkDetector() {

        // empty
    }

    /**
     * As test case.<p>
     * 
     * @param args not used
     */
    public static void main(String[] args) {

        System.out.println(substituteLinks("see our webpage at http://www.opencms.org/en/... (You can also download OCEE from ftp://ftp.opencms.org/ocee/). Have Fun."));
    }

    /**
     * The method check if the text contains a url in plain text, and if
     * so, it substitutes it by an hyperlink.<p>
     * 
     * @param text text that may contain a url in plain text
     * 
     * @return the text with the replaced link
     */
    public static String substituteLinks(String text) {

        Matcher matcher = Pattern.compile("(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)").matcher(
            text);

        String ret = "";
        int end = 0;
        while (matcher.find()) {
            String url = matcher.group(1);
            // String prefix = matcher.group(2);
            String endingSpaces = matcher.group(3);

            Matcher dotEndMatcher = Pattern.compile("([\\W&&[^/]]+)$").matcher(url);

            // Ending non alpha characters like [.,?%] shouldn't be included
            // in the url.
            String endingDots = "";
            if (dotEndMatcher.find()) {
                endingDots = dotEndMatcher.group(1);
                url = dotEndMatcher.replaceFirst("");
            }

            ret += text.substring(end, matcher.start());
            ret += "<a href='" + url + "'>" + url + "</a>" + endingDots + endingSpaces;
            end = matcher.end();
        }
        ret += text.substring(end);
        return ret;
    }
}
