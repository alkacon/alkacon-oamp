/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.usagereport/src/com/alkacon/opencms/usagereport/CmsUpdatefeedConfigImage.java,v $
 * Date   : $Date: 2009/02/05 09:56:20 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2008 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.usagereport;

/**
 * Serves for saving the configured image of the XML content.<p>
 * 
 * @author Ruediger Kurz 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeedConfigImage {

    private String m_title;
    private String m_url;
    private String m_link;
    private String m_description;

    /**
     * Returns the title of the feed image.<p>
     *
     * @return the title
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * Sets the title of the feed image.<p>
     *
     * @param title the title to set
     */
    public void setTitle(String title) {

        m_title = title;
    }

    /**
     * Returns the url of the feed image.<p>
     *
     * @return the url
     */
    public String getUrl() {

        return m_url;
    }

    /**
     * Sets the url of the feed image.<p>
     *
     * @param url the url to set
     */
    public void setUrl(String url) {

        m_url = url;
    }

    /**
     * Returns the link to any resource.<p>
     *
     * @return the link
     */
    public String getLink() {

        return m_link;
    }

    /**
     * Sets the link to any resource.<p>
     *
     * @param link the link to set
     */
    public void setLink(String link) {

        m_link = link;
    }

    /**
     * Returns the description of the feed image.<p>
     *
     * @return the description
     */
    public String getDescription() {

        return m_description;
    }

    /**
     * Sets the description of the feed image.<p>
     *
     * @param description the description to set
     */
    public void setDescription(String description) {

        m_description = description;
    }

}
