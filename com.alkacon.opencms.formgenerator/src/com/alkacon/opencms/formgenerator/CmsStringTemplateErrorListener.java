/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsStringTemplateErrorListener.java,v $
 * Date   : $Date: 2011/03/09 15:14:33 $
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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.antlr.stringtemplate.StringTemplateErrorListener;

/**
 * An implementation of the error listener for the string templates.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsStringTemplateErrorListener implements StringTemplateErrorListener {

    /** The error output. */
    private StringBuffer m_errorOutput = new StringBuffer(512);

    /** The amount of warnings and errors. */
    private int m_number;

    /**
     * @see org.antlr.stringtemplate.StringTemplateErrorListener#error(java.lang.String, java.lang.Throwable)
     */
    public void error(String msg, Throwable e) {

        m_number++;
        if (m_number > 1) {
            m_errorOutput.append('\n');
        }
        if (e != null) {
            StringWriter st = new StringWriter();
            e.printStackTrace(new PrintWriter(st));
            m_errorOutput.append(msg + ": " + st.toString());
        } else {
            m_errorOutput.append(msg);
        }
    }

    /**
     * @see org.antlr.stringtemplate.StringTemplateErrorListener#warning(java.lang.String)
     */
    public void warning(String msg) {

        m_number++;
        m_errorOutput.append(msg);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        String me = toString();
        String them = o.toString();
        return me.equals(them);
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return m_errorOutput.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return m_errorOutput.toString();
    }
}
