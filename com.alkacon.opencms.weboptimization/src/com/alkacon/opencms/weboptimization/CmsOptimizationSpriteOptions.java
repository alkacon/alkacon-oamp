/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationSpriteOptions.java,v $
 * Date   : $Date: 2009/03/24 12:52:42 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.weboptimization;

/**
 * Bean for sprite optimization options.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationSpriteOptions {

    /** Css selector for the image. */
    private String m_selector;

    /** X position to insert the image. */
    private int m_x = 0;

    /** Y position to insert the image. */
    private int m_y = 0;

    /**
     * Default constructor.<p>
     */
    public CmsOptimizationSpriteOptions() {

        // empty
    }

    /**
     * Constructor.<p>
     * 
     * @param x the X position to insert the image
     * @param y the Y position to insert the image
     * @param selector the css selector for the image
     */
    public CmsOptimizationSpriteOptions(int x, int y, String selector) {

        m_x = x;
        m_y = y;
        m_selector = selector;
    }

    /**
     * Returns the css selector for the image
     * 
     * @return the css selector for the image
     */
    public String getSelector() {

        return m_selector;
    }

    /**
     * Returns the X position to insert the image.<p>
     * 
     * @return the X position to insert the image
     */
    public int getX() {

        return m_x;
    }

    /**
     * Returns the Y position to insert the image.<p>
     * 
     * @return the Y position to insert the image
     */
    public int getY() {

        return m_y;
    }

    /**
     * Sets the css selector for the image
     * 
     * @param selector the css selector to set
     */
    public void setSelector(String selector) {

        m_selector = selector;
    }

    /**
     * Sets the X position to insert the image.<p>
     * 
     * @param x the x position to set
     */
    public void setX(int x) {

        m_x = x;
    }

    /**
     * Sets the Y position to insert the image.<p>
     * 
     * @param y the y position to set
     */
    public void setY(int y) {

        m_y = y;
    }
}
