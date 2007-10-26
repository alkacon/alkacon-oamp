/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsSubscriberImportObject.java,v $
 * Date   : $Date: 2007/10/26 14:53:40 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Import object for importing email addresses to a mailing list as subscribers.<p>
 * 
 * @author Andreas Zahner  
 * 
 * @version $Revision $ 
 * 
 * @since 7.0.3
 */
public class CmsSubscriberImportObject {

    /** The converted lines of the subscriber emails that should be imported. */
    private List m_convertedLines;

    /** The email addresses to import. */
    private String m_importEmail;

    /** The invalid lines of the subscriber emails that should be imported. */
    private List m_invalidLines;

    /**
     * Returns the converted lines of the subscriber emails that should be imported.<p>
     * 
     * The list entry is a String array where the first value if the original line, the second the found email address.<p>
     * 
     * @return the converted lines of the subscriber emails that should be imported
     */
    public List getConvertedLines() {

        if (m_convertedLines == null) {
            return Collections.EMPTY_LIST;
        }
        return m_convertedLines;
    }

    /**
     * Returns the email addresses that should be imported.<p>
     * 
     * @return the email addresses that should be imported
     */
    public List getEmailAddresses() {

        List result = new ArrayList();
        m_convertedLines = new ArrayList();
        m_invalidLines = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new StringReader(getImportEmail()));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (CmsNewsletterManager.isValidEmail(line)) {
                    result.add(line);
                } else {
                    // first try to get a valid email address from the line
                    List lineEntries = CmsStringUtil.splitAsList(line, ' ');
                    Iterator i = lineEntries.iterator();
                    boolean foundEmail = false;
                    while (i.hasNext()) {
                        String testEntry = (String)i.next();
                        if (CmsNewsletterManager.isValidEmail(testEntry)) {
                            // found a valid entry, add it to result and to converted lines list
                            result.add(testEntry);
                            m_convertedLines.add(new String[] {line, testEntry});
                            foundEmail = true;
                            break;
                        }
                    }
                    if (!foundEmail) {
                        // found no valid email at all, add line to invalid lines list
                        m_invalidLines.add(line);
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            // should never happen
        }
        return result;

    }

    /**
     * Returns the email addresses to import.<p>
     * 
     * @return the email addresses to import
     */
    public String getImportEmail() {

        return m_importEmail;
    }

    /**
     * Returns the invalid lines of the subscriber emails that should be imported.<p>
     * 
     * @return the invalid lines of the subscriber emails that should be imported
     */
    public List getInvalidLines() {

        if (m_invalidLines == null) {
            return Collections.EMPTY_LIST;
        }
        return m_invalidLines;
    }

    /**
     * Sets the email addresses to import.<p>
     * 
     * @param importEmail the email addresses to import
     * @throws Exception if the email addresses String is empty
     */
    public void setImportEmail(String importEmail) throws Exception {

        m_importEmail = importEmail;
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(importEmail)) {
            // empty string, throw exception
            throw new CmsException(Messages.get().container(Messages.ERR_SUBSCRIBER_IMPORT_NO_CONTENT_0));
        }
    }

}
