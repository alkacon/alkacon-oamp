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

import org.antlr.stringtemplate.StringTemplate;

/**
 * A String wrapper that is useful in StringTemplates.
 * 
 * It allows to 
 *   - Escape XML
 *   - Secure HTML by escaping HTML with "dangerous" tags
 *   - Render a String as a StringTemplate
 *   
 * Note that escaping and securing Strings can be combined with other operations.
 */
public class SecurableString {

    @SuppressWarnings("javadoc")
    private String m_string;

    /**
     * @param string The String to wrap
     */
    public SecurableString(final String string) {

        m_string = string;
    }

    /**
     * Get the string unwrapped.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return m_string;
    }

    /**
     * @return The wrapped String converted to a StringTemplate
     */
    public StringTemplate getStringTemplate() {

        return new StringTemplate(m_string);
    }

    @SuppressWarnings("javadoc")
    public SecurableString getEscapeXml() {

        return new SecurableString(CmsEncoder.escapeXml(m_string));
    }

    @SuppressWarnings("javadoc")
    public SecurableString getSecureContent() {

        return new SecurableString(CmsCommentsUtil.secureContent(m_string));
    }
}