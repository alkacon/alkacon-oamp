/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.weboptimization/src/com/alkacon/opencms/v8/weboptimization/CmsOptimizationJsOptions.java,v $
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

package com.alkacon.opencms.v8.weboptimization;

/**
 * Bean for js optimization options.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationJsOptions {

    /** Insert a line break after the specified column number. */
    private int m_lineBreakPos = -1;

    /** if <code>false</code>, minify only, do not obfuscate. */
    private boolean m_munge = true;

    /** if <code>false</code>, disable all micro optimizations. */
    private boolean m_optimize = true;

    /** Preserve all semicolons. */
    private boolean m_preserveSemi = false;

    /**
     * Default constructor.<p>
     */
    public CmsOptimizationJsOptions() {

        // empty
    }

    /**
     * Constructor.<p>
     * 
     * @param lineBreakPos Insert a line break after the specified column number
     * @param preserveSemi Preserve all semicolons
     * @param munge if <code>false</code>, minify only, do not obfuscate
     * @param optimize if <code>false</code>, disable all micro optimizations
     */
    public CmsOptimizationJsOptions(int lineBreakPos, boolean preserveSemi, boolean munge, boolean optimize) {

        m_lineBreakPos = lineBreakPos;
        m_preserveSemi = preserveSemi;
        m_munge = munge;
        m_optimize = optimize;
    }

    /**
     * Returns the column number to insert a line break after.<p>
     * 
     * @return the column number to insert a line break after
     */
    public int getLineBreakPos() {

        return m_lineBreakPos;
    }

    /**
     * Checks if to minify only, and not to obfuscate.<p>
     * 
     * @return <code>false</code> minify only, do not obfuscate
     */
    public boolean isMunge() {

        return m_munge;
    }

    /**
     * Checks if to use micro optimizations.<p>
     * 
     * @return <code>true</code> if to use micro optimizations
     */
    public boolean isOptimize() {

        return m_optimize;
    }

    /**
     * Checks if to preserve all semicolons.<p>
     * 
     * @return <code>true</code> if to preserve all semicolons
     */
    public boolean isPreserveSemi() {

        return m_preserveSemi;
    }

    /**
     * Sets the column number to insert a line break after.<p>
     * 
     * @param lineBreakPos the column number to insert a line break after
     */
    public void setLineBreakPos(int lineBreakPos) {

        m_lineBreakPos = lineBreakPos;
    }

    /**
     * Sets the munge flag, ie. if to minify only, and not to obfuscate.<p>
     * 
     * @param munge the munge flag to set
     */
    public void setMunge(boolean munge) {

        m_munge = munge;
    }

    /**
     * Sets if to use micro optimizations.
     * 
     * @param optimize the optimization flag to set
     */
    public void setOptimize(boolean optimize) {

        m_optimize = optimize;
    }

    /**
     * Sets the flag to preserve all semicolons.<p>
     * 
     * @param preserveSemi the flag to preserve all semicolons
     */
    public void setPreserveSemi(boolean preserveSemi) {

        m_preserveSemi = preserveSemi;
    }
}
