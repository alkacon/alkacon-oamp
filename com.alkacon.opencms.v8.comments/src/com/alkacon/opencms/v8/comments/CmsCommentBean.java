/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.comments/src/com/alkacon/opencms/v8/comments/CmsCommentBean.java,v $
 * Date   : $Date: 2010/03/19 15:31:12 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
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

package com.alkacon.opencms.v8.comments;

import java.util.Locale;

/**
 * Provides direct access to comments.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4
 */
public class CmsCommentBean {

    /** The author. */
    private String m_author;

    /** The content. */
    private String m_content;

    /** The date. */
    private long m_date;

    /** The locale. */
    private Locale m_locale;

    /** The uri. */
    private String m_uri;

    /**
     * Empty constructor.<p>
     */
    public CmsCommentBean() {

        super();
    }

    /**
     * Returns the author.<p>
     *
     * @return the author
     */
    public String getAuthor() {

        return m_author;
    }

    /**
     * Returns the content.<p>
     *
     * @return the content
     */
    public String getContent() {

        return m_content;
    }

    /**
     * Returns the date.<p>
     *
     * @return the date
     */
    public long getDate() {

        return m_date;
    }

    /**
     * Returns the locale.<p>
     *
     * @return the locale
     */
    public Locale getLocale() {

        return m_locale;
    }

    /**
     * Returns the uri.<p>
     *
     * @return the uri
     */
    public String getUri() {

        return m_uri;
    }

    /**
     * Sets the author.<p>
     *
     * @param author the author to set
     */
    public void setAuthor(String author) {

        m_author = author;
    }

    /**
     * Sets the content.<p>
     *
     * @param content the content to set
     */
    public void setContent(String content) {

        m_content = content;
    }

    /**
     * Sets the date.<p>
     *
     * @param date the date to set
     */
    public void setDate(long date) {

        m_date = date;
    }

    /**
     * Sets the locale.<p>
     *
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {

        m_locale = locale;
    }

    /**
     * Sets the uri.<p>
     *
     * @param uri the uri to set
     */
    public void setUri(String uri) {

        m_uri = uri;
    }

}
