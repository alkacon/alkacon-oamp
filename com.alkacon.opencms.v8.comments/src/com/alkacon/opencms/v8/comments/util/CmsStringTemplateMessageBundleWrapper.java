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

import org.opencms.i18n.CmsMessages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Thic class wraps a whole message bundle in a Map from message-keys to the messages as SecurableString.
 * Using SecurableString facilitates to convert the messages to StringTemplates that can be filled with parameters.
 * 
 */
public class CmsStringTemplateMessageBundleWrapper {

    /** The messages read from the bundle */
    private CmsMessages m_messages;
    /** The cached message map after converting the bundle */
    Map<String, SecurableString> m_messageMap = null;

    /**
     * @param bundle The message bundle to wrap
     * @param locale The locale to wrap
     */
    public CmsStringTemplateMessageBundleWrapper(final String bundle, final Locale locale) {

        m_messages = new CmsMessages(bundle, locale);
        createMessageMap();

    }

    @SuppressWarnings("javadoc")
    private void createMessageMap() {

        m_messageMap = new HashMap<String, SecurableString>(m_messages.getResourceBundle().keySet().size());
        for (String key : m_messages.getResourceBundle().keySet()) {
            m_messageMap.put(key, getTransformedMessage(key));
        }
    }

    @SuppressWarnings("javadoc")
    private SecurableString getTransformedMessage(final String key) {

        String message = m_messages.key(key);
        return transformMessageToStringOrStringTemplate(message);
    }

    /**
     * Here possible parameters {0}, {1}, ... get converted to $p0$, $p1$, ...
     * This allows to insert the parameters via the StringTemplate engine.
     * 
     * @param message The message to convert
     * @return the converted message
     */
    private SecurableString transformMessageToStringOrStringTemplate(final String message) {

        String regexParameterPlaceHolderMessage = "\\{([0-9]+)\\}";
        String regexParameterPlaceHolderStringTemplate = "\\$p$1\\$";
        String msg = message.replaceAll(regexParameterPlaceHolderMessage, regexParameterPlaceHolderStringTemplate);
        return new SecurableString(msg);
    }

    /**
     * @return The messages map created from the bundle.
     */
    public Map<String, SecurableString> getMessageMap() {

        return m_messageMap;
    }
}
