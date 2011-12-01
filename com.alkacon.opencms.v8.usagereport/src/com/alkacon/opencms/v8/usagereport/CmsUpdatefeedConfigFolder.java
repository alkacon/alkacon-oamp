/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.usagereport/src/com/alkacon/opencms/v8/usagereport/CmsUpdatefeedConfigFolder.java,v $
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

package com.alkacon.opencms.v8.usagereport;

import java.util.List;

/**
 * Serves for saving the configured folders of the XML content.<p>
 * 
 * @author Ruediger Kurz 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeedConfigFolder {

    /**
     * Is the path of the folder should be considered by generating the feed.<p>
     */
    private String m_startfolder;

    /**
     * Defines the resource types which should be considered in the start-folder.<p>
     */
    private List m_resTypes;

    /**
     * Defines the content state (added or new and added) which should be considered in the start-folder.<p>
     */
    private String m_contentState;

    /**
     *  Checks if a given folder is a sub-folder of this folder.<p>
     *  
     *  If the folder argument occurs as a subfolder within this folder
     *  then returns true otherwise returns false.<p>
     *  
     * @param folder any CmsUpdatefeedConfigFolder object
     * @return if the folder argument occurs as a subfolder within this folder
     *  then returns true otherwise returns false
     */
    public boolean isSubFolder(CmsUpdatefeedConfigFolder folder) {

        if (this.equals(folder)) {
            return false;
        }
        if (m_startfolder.startsWith(folder.getStartfolder())) {
            return true;
        }
        return false;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {

        if (o instanceof CmsUpdatefeedConfigFolder) {
            CmsUpdatefeedConfigFolder folder = (CmsUpdatefeedConfigFolder)o;
            if (folder.getStartfolder().equals(m_startfolder)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return getStartfolder().hashCode();
    }

    /**
     * Returns the start folder.<p>
     *
     * @return the start folder
     */
    public String getStartfolder() {

        return m_startfolder;
    }

    /**
     * Sets the start folder.<p>
     *
     * @param startfolder the start folder to set
     */
    public void setStartfolder(String startfolder) {

        m_startfolder = startfolder;
    }

    /**
     * Returns the resource types which should be included in the feed.<p>
     *
     * @return theresource types
     */
    public List getResTypes() {

        return m_resTypes;
    }

    /**
     * Sets the resource types which should be included in the feed.<p>
     *
     * @param resTypes the resource types to set
     */
    public void setResTypes(List resTypes) {

        m_resTypes = resTypes;
    }

    /**
     * Returns the content state (added or new and added).<p>
     *
     * @return the content state
     */
    public String getContentState() {

        return m_contentState;
    }

    /**
     * Sets the content state (added or new and added).<p>
     *
     * @param contentState the content state to set
     */
    public void setContentState(String contentState) {

        m_contentState = contentState;
    }

}
