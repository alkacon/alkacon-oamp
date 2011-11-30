/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.registration/src/com/alkacon/opencms/v8/registration/CmsRegistrationXmlContentHandler.java,v $
 * Date   : $Date: 2011/03/10 11:59:04 $
 * Version: $Revision: 1.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.registration;

import com.alkacon.opencms.v8.formgenerator.CmsForm;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.security.CmsRole;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsDefaultXmlContentHandler;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Xml content handler for the webuser registration.<p>
 *  
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3 $
 *  
 * @since 7.0.3
 */
public class CmsRegistrationXmlContentHandler extends CmsDefaultXmlContentHandler {

    /**
     * Default constructor.<p>
     */
    public CmsRegistrationXmlContentHandler() {

        super();
    }

    /**
     * @see org.opencms.xml.content.CmsDefaultXmlContentHandler#prepareForWrite(org.opencms.file.CmsObject, org.opencms.xml.content.CmsXmlContent, org.opencms.file.CmsFile)
     */
    @Override
    public CmsFile prepareForWrite(CmsObject cms, CmsXmlContent content, CmsFile file) throws CmsException {

        // for each locale
        if (file.getName().indexOf("~") != 0) {
            Iterator<Locale> locales = content.getLocales().iterator();
            while (locales.hasNext()) {
                Locale locale = locales.next();
                // check the account manager role for the given organizational unit 
                CmsOrganizationalUnit ou = OpenCms.getOrgUnitManager().readOrganizationalUnit(
                    cms,
                    content.getStringValue(cms, CmsRegistrationForm.NODE_ACTION
                        + "/"
                        + CmsRegistrationForm.NODE_ORGANIZATIONALUNIT, locale));
                OpenCms.getRoleManager().checkRole(cms, CmsRole.ACCOUNT_MANAGER.forOrgUnit(ou.getName()));
                // check the account manager role for the organizational unit of the given group
                if (content.getValue(CmsRegistrationForm.NODE_ACTION + "/" + CmsRegistrationForm.NODE_GROUP, locale) != null) {
                    String groupName = content.getStringValue(cms, CmsRegistrationForm.NODE_ACTION
                        + "/"
                        + CmsRegistrationForm.NODE_GROUP, locale);
                    if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(groupName)) {
                        CmsGroup group = cms.readGroup(groupName);
                        OpenCms.getRoleManager().checkRole(cms, CmsRole.ACCOUNT_MANAGER.forOrgUnit(group.getOuFqn()));
                    }
                }
                // check mandatory fields
                List<String> mandatories = new ArrayList<String>(Arrays.asList(new String[] {
                    "email",
                    "password",
                    "login"}));
                if (Boolean.valueOf(
                    content.getStringValue(cms, CmsRegistrationForm.NODE_ACTION
                        + "/"
                        + CmsRegistrationForm.NODE_EMAILASLOGIN, locale)).booleanValue()) {
                    mandatories.remove(2);
                }
                Iterator<I_CmsXmlContentValue> fields = content.getValues(CmsForm.NODE_INPUTFIELD, locale).iterator();
                while (fields.hasNext()) {
                    I_CmsXmlContentValue inputField = fields.next();
                    String stringValue = content.getStringValue(cms, inputField.getPath()
                        + "/"
                        + CmsForm.NODE_FIELDLABEL, locale);
                    if (mandatories.contains(stringValue)) {
                        mandatories.remove(stringValue);
                    } else {
                        int pos = stringValue.lastIndexOf("|");
                        if ((pos >= 0) && (pos < (stringValue.length() - 1))) {
                            stringValue = stringValue.substring(pos + 1);
                            if (mandatories.contains(stringValue)) {
                                mandatories.remove(stringValue);
                            }
                        }
                    }
                }
                if (!mandatories.isEmpty()) {
                    throw new CmsException(Messages.get().container(
                        Messages.ERR_MANDATORY_FIELDS_MISSING_1,
                        mandatories));
                }
            }
        }
        return super.prepareForWrite(cms, content, file);
    }
}
