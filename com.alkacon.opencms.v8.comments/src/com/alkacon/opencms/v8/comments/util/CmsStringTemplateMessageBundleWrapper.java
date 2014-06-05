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

public class CmsStringTemplateMessageBundleWrapper {

    CmsMessages m_messages;
    Map<String, SecurableString> m_messageMap = null;

    public CmsStringTemplateMessageBundleWrapper(String bundle, Locale locale) {

        m_messages = new CmsMessages(bundle, locale);
        createMessageMap();

    }

    private void createMessageMap() {

        m_messageMap = new HashMap<String, SecurableString>(m_messages.getResourceBundle().keySet().size());
        for (String key : m_messages.getResourceBundle().keySet()) {
            m_messageMap.put(key, getTransformedMessage(key));
        }
    }

    private SecurableString getTransformedMessage(String key) {

        String message = m_messages.key(key);
        return transformMessageToStringOrStringTemplate(message);
    }

    private SecurableString transformMessageToStringOrStringTemplate(final String message) {

        String regexParameterPlaceHolderMessage = "\\{([0-9]+)\\}";
        String regexParameterPlaceHolderStringTemplate = "\\$p$1\\$";
        String msg = message.replaceAll(regexParameterPlaceHolderMessage, regexParameterPlaceHolderStringTemplate);
        return new SecurableString(msg);
    }

    public Map<String, SecurableString> getMessageMap() {

        return m_messageMap;
    }
}
