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

import java.text.DateFormat;
import java.util.Locale;

public class FormattedDate {

    long m_date;
    Locale m_locale;

    public FormattedDate(long date, Locale locale) {

        m_date = date;
        m_locale = locale;
    }

    private String getDateTime(int dateFormat, int timeFormat) {

        return CmsEncoder.escapeXml(CmsCommentsUtil.formatDateTime(m_date, m_locale, dateFormat, timeFormat));
    }

    private String getDate(int dateFormat) {

        return CmsEncoder.escapeXml(CmsCommentsUtil.formatDate(m_date, m_locale, dateFormat));
    }

    public String getDateLongTimeLong() {

        return getDateTime(DateFormat.LONG, DateFormat.LONG);
    }

    public String getDateLongTimeShort() {

        return getDateTime(DateFormat.LONG, DateFormat.SHORT);
    }

    public String getDateShortTimeLong() {

        return getDateTime(DateFormat.SHORT, DateFormat.LONG);
    }

    public String getDateShortTimeShort() {

        return getDateTime(DateFormat.SHORT, DateFormat.SHORT);
    }

    public String getDateLong() {

        return getDate(DateFormat.LONG);
    }

    public String getDateShort() {

        return getDate(DateFormat.SHORT);
    }

    public String toString() {

        return getDateLongTimeShort();
    }
}
