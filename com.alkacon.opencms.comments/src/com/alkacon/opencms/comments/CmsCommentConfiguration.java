/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.comments/src/com/alkacon/opencms/comments/CmsCommentConfiguration.java,v $
 * Date   : $Date: 2010/03/19 15:31:12 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.comments;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsOrganizationalUnit;
import org.opencms.util.A_CmsModeStringEnumeration;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Configuration options.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.3 $
 * 
 * @since 7.0.5 
 */
public class CmsCommentConfiguration {

    /**
     * Enumeration class for defining the comments security modes.<p>
     */
    public static final class CmsCommentSecurityMode extends A_CmsModeStringEnumeration {

        /** Constant for the comments security mode, no login needed. */
        protected static final CmsCommentSecurityMode MODE_NONE = new CmsCommentSecurityMode("none");

        /** Constant for the comments security mode, only write. */
        protected static final CmsCommentSecurityMode MODE_NOVIEW = new CmsCommentSecurityMode("noview");

        /** Constant for the comments security mode, only view without login. */
        protected static final CmsCommentSecurityMode MODE_VIEW = new CmsCommentSecurityMode("view");

        /** Constant for the comments security mode, login needed for view and write. */
        protected static final CmsCommentSecurityMode MODE_WRITE = new CmsCommentSecurityMode("write");

        /** The serial version id. */
        private static final long serialVersionUID = -3320578303788674690L;

        /**
         * Default constructor.<p>
         * 
         * @param mode string representation
         */
        private CmsCommentSecurityMode(String mode) {

            super(mode);
        }

        /**
         * Returns the parsed mode object if the string representation matches, or <code>null</code> if not.<p>
         * 
         * @param commentSecurityMode the string representation to parse
         * 
         * @return the parsed mode object
         */
        public static CmsCommentSecurityMode valueOf(String commentSecurityMode) {

            if (commentSecurityMode == null) {
                return null;
            }
            if (commentSecurityMode.equalsIgnoreCase(MODE_NONE.getMode())) {
                return MODE_NONE;
            }
            if (commentSecurityMode.equalsIgnoreCase(MODE_VIEW.getMode())) {
                return MODE_VIEW;
            }
            if (commentSecurityMode.equalsIgnoreCase(MODE_NOVIEW.getMode())) {
                return MODE_NOVIEW;
            }
            if (commentSecurityMode.equalsIgnoreCase(MODE_WRITE.getMode())) {
                return MODE_WRITE;
            }
            return null;
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_NONE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_NONE}
         */
        public boolean isNone() {

            return this == MODE_NONE;
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_NOVIEW}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_NOVIEW}
         */
        public boolean isNoView() {

            return this == MODE_NOVIEW;
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_VIEW}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_VIEW}
         */
        public boolean isView() {

            return this == MODE_VIEW;
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_WRITE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_WRITE}
         */
        public boolean isWrite() {

            return this == MODE_WRITE;
        }
    }

    /** Constant for the comments security mode, no login needed. */
    public static final CmsCommentSecurityMode SECURITY_MODE_NONE = CmsCommentSecurityMode.MODE_NONE;

    /** Constant for the comments security mode, only write. */
    public static final CmsCommentSecurityMode SECURITY_MODE_NOVIEW = CmsCommentSecurityMode.MODE_NOVIEW;

    /** Constant for the comments security mode, only view without login. */
    public static final CmsCommentSecurityMode SECURITY_MODE_VIEW = CmsCommentSecurityMode.MODE_VIEW;

    /** Constant for the comments security mode, login needed for view and write. */
    public static final CmsCommentSecurityMode SECURITY_MODE_WRITE = CmsCommentSecurityMode.MODE_WRITE;

    /** Configuration node name for the group. */
    private static final String NODE_GROUP = "Group";

    /** Configuration node name for the list setting. */
    private static final String NODE_LIST = "List";

    /** Configuration node name for the minimized flag. */
    private static final String NODE_MINIMIZED = "Minimized";

    /** Configuration node name for the moderated flag. */
    private static final String NODE_MODERATED = "Moderated";

    /** Configuration node name for the 'offer login' flag. */
    private static final String NODE_OFFERLOGIN = "OfferLogin";

    /** Configuration node name for the comment options. */
    private static final String NODE_OPTIONS = "Options";

    /** Configuration node name for the organizational unit. */
    private static final String NODE_ORGUNIT = "OrgUnit";

    /** Configuration node name for the resource bundle. */
    private static final String NODE_RESOURCEBUNDLE = "ResourceBundle";

    /** Configuration node name for the security level. */
    private static final String NODE_SECURITY = "Security";

    /** Configuration node name for the style sheet. */
    private static final String NODE_STYLESHEET = "StyleSheet";

    /** Configuration Uri. */
    private String m_configUri;

    /** The groups, the users have to be members of. */
    private List<CmsGroup> m_groups;

    /** The list setting. */
    private int m_list;

    /** The minimized flag. */
    private boolean m_minimized;

    /** The moderated flag. */
    private boolean m_moderated;

    /** The 'offer login' flag. */
    private boolean m_offerLogin;

    /** The organizational units, the users have to be members of. */
    private List<CmsOrganizationalUnit> m_orgUnits;

    /** The resource bundle. */
    private String m_resourceBundle;

    /** The security level. */
    private CmsCommentSecurityMode m_security;

    /** The style sheet. */
    private String m_styleSheet;

    /**
     * Default constructor which parses the configuration file.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * 
     * @throws Exception if parsing the configuration fails
     */
    public CmsCommentConfiguration(CmsJspActionElement jsp)
    throws Exception {

        this(jsp, null);
    }

    /**
     * Constructor which parses the configuration file using a given configuration file URI.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param configUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws Exception if parsing the configuration fails
     */
    public CmsCommentConfiguration(CmsJspActionElement jsp, String configUri)
    throws Exception {

        init(jsp, configUri);
    }

    /**
     * Internal constructor just used for cloning.
     * 
     * @param configUri VFS URI of the comment' config file
     * @param groups The groups, the users have to be members of
     * @param minimized Flag, indicating if to start minimized
     * @param moderated Flag, indicating if comments are moderated
     * @param offerLogin Flag, indicating if a login possibility should be provided if needed
     * @param orgUnits The groups, the users have to be members of
     * @param resourceBundle The resource bundle used for localization
     * @param security The security level
     * @param styleSheet The styleSheet to use
     */
    private CmsCommentConfiguration(
        final String configUri,
        final List<CmsGroup> groups,
        final boolean minimized,
        final boolean moderated,
        final boolean offerLogin,
        final List<CmsOrganizationalUnit> orgUnits,
        final String resourceBundle,
        final CmsCommentSecurityMode security,
        final String styleSheet) {

        m_configUri = configUri;
        m_groups = groups;
        m_minimized = minimized;
        m_moderated = moderated;
        m_offerLogin = offerLogin;
        m_orgUnits = orgUnits;
        m_resourceBundle = resourceBundle;
        m_security = security;
        m_styleSheet = styleSheet;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public CmsCommentConfiguration clone() {

        return new CmsCommentConfiguration(
            m_configUri,
            Collections.unmodifiableList(m_groups),
            m_minimized,
            m_moderated,
            m_offerLogin,
            Collections.unmodifiableList(m_orgUnits),
            m_resourceBundle,
            m_security,
            m_styleSheet);
    }

    /**
     * Returns the configuration Uri.<p>
     *
     * @return the configuration Uri
     */
    public String getConfigUri() {

        return m_configUri;
    }

    /**
     * Returns the groups.<p>
     *
     * @return the groups
     */
    public List<CmsGroup> getGroups() {

        return Collections.unmodifiableList(m_groups);
    }

    /**
     * Returns the list setting.<p>
     *
     * @return the list setting
     */
    public int getList() {

        return m_list;
    }

    /**
     * Returns the organizational Units.<p>
     *
     * @return the organizational Units
     */
    public List<CmsOrganizationalUnit> getOrgUnits() {

        return Collections.unmodifiableList(m_orgUnits);
    }

    /**
     * Returns the resource Bundle.<p>
     *
     * @return the resource Bundle
     */
    public String getResourceBundle() {

        return m_resourceBundle;
    }

    /**
     * Returns the security level.<p>
     *
     * @return the security level
     */
    public CmsCommentSecurityMode getSecurity() {

        return m_security;
    }

    /**
     * Returns the style Sheet.<p>
     *
     * @return the style Sheet
     */
    public String getStyleSheet() {

        return m_styleSheet;
    }

    /**
     * Returns the minimized flag.<p>
     *
     * @return the minimized flag
     */
    public boolean isMinimized() {

        return m_minimized;
    }

    /**
     * Returns the moderated flag.<p>
     *
     * @return the moderated flag
     */
    public boolean isModerated() {

        return m_moderated;
    }

    /**
     * Returns the 'offer Login' flag.<p>
     *
     * @return the 'offer Login' flag
     */
    public boolean isOfferLogin() {

        return m_offerLogin;
    }

    /**
     * Sets the list setting.<p>
     *
     * @param list the list setting
     */
    public void setList(String list) {

        try {
            m_list = Integer.valueOf(list).intValue();
        } catch (Throwable e) {
            m_list = -1;
        }
    }

    /**
     * Sets the minimized flag.<p>
     *
     * @param minimized the minimized flag to set
     */
    public void setMinimized(boolean minimized) {

        m_minimized = minimized;
    }

    /**
     * Sets the security mode.<p>
     *
     * @param security the security mode to set
     */
    public void setSecurity(CmsCommentSecurityMode security) {

        m_security = security;
    }

    /**
     * Initializes the configuration.<p>
     * 
     * @param jsp the initialized CmsJspActionElement to access the OpenCms API
     * @param configUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws Exception if parsing the configuration fails
     */
    private void init(CmsJspActionElement jsp, String configUri) throws Exception {

        // read the configuration file from VFS
        if (CmsStringUtil.isEmpty(configUri)) {
            configUri = jsp.getRequestContext().getUri();
        }
        m_configUri = configUri;
        CmsFile file = jsp.getCmsObject().readFile(configUri);
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(jsp.getCmsObject(), file);

        // get current Locale
        Locale locale = jsp.getRequestContext().getLocale();

        // read configuration
        initCommentOptions(content, jsp.getCmsObject(), locale);
    }

    /**
     * Initializes the comments settings.<p>
     * 
     * @param content the XML configuration content
     * @param cms the CmsObject to access the content values
     * @param locale the currently active Locale
     */
    private void initCommentOptions(CmsXmlContent content, CmsObject cms, Locale locale) {

        String path = NODE_OPTIONS + "/";

        String stringValue = content.getStringValue(cms, path + NODE_MODERATED, locale);
        m_moderated = Boolean.valueOf(stringValue).booleanValue();

        stringValue = content.getStringValue(cms, path + NODE_LIST, locale);
        setList(stringValue);

        stringValue = content.getStringValue(cms, path + NODE_SECURITY, locale);
        m_security = CmsCommentSecurityMode.valueOf(stringValue);

        m_orgUnits = new ArrayList<CmsOrganizationalUnit>();
        Iterator<I_CmsXmlContentValue> itOrgUnits = content.getValues(path + NODE_ORGUNIT, locale).iterator();
        while (itOrgUnits.hasNext()) {
            I_CmsXmlContentValue value = itOrgUnits.next();
            stringValue = value.getStringValue(cms);
            try {
                m_orgUnits.add(OpenCms.getOrgUnitManager().readOrganizationalUnit(cms, stringValue));
            } catch (Throwable e) {
                throw new CmsIllegalArgumentException(Messages.get().container(
                    Messages.ERR_ORGUNIT_DOESNOT_EXIST_1,
                    stringValue));
            }
        }

        m_groups = new ArrayList<CmsGroup>();
        Iterator<I_CmsXmlContentValue> itGroups = content.getValues(path + NODE_GROUP, locale).iterator();
        while (itGroups.hasNext()) {
            I_CmsXmlContentValue value = itGroups.next();
            stringValue = value.getStringValue(cms);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(stringValue)) {
                try {
                    m_groups.add(cms.readGroup(stringValue));
                } catch (Throwable e) {
                    throw new CmsIllegalArgumentException(Messages.get().container(
                        Messages.ERR_GROUP_DOESNOT_EXIST_1,
                        stringValue));
                }
            }
        }

        stringValue = content.getStringValue(cms, path + NODE_MINIMIZED, locale);
        m_minimized = Boolean.valueOf(stringValue).booleanValue();

        stringValue = content.getStringValue(cms, path + NODE_OFFERLOGIN, locale);
        m_offerLogin = Boolean.valueOf(stringValue).booleanValue();

        stringValue = content.getStringValue(cms, path + NODE_STYLESHEET, locale);
        m_styleSheet = stringValue;

        stringValue = content.getStringValue(cms, path + NODE_RESOURCEBUNDLE, locale);
        m_resourceBundle = stringValue;
    }
}
