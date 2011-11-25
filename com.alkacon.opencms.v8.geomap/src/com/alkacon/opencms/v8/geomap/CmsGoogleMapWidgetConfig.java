/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.geomap/src/com/alkacon/opencms/v8/geomap/CmsGoogleMapWidgetConfig.java,v $
 * Date   : $Date: 2011/02/16 13:05:25 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2008 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.geomap;

import org.opencms.json.JSONArray;
import org.opencms.json.JSONException;
import org.opencms.json.JSONObject;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.util.A_CmsModeStringEnumeration;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Configuration of the google map widget.<p>
 * 
 * Options can be defined for each element of the type <code>String</code> using the widget <code>GoogleMapWidget</code>.
 * They have to be placed in the annotation section of a XSD describing an XML content. The <code>configuration</code> attribute 
 * in the <code>layout</code> node for the element must contain the activated options as a JSON string without the curly braces:<p>
 * 
 * <code><layout element="Text" widget="HtmlWidget" configuration="inline: ['coords'], popup: ['coords','address','zoom','size','type','mode','map'], edit: ['coords','address','zoom','size','type','mode','map']" /></code><p>
 * 
 * Available options are:
 * <ul>
 * <li><code>inline</code>: the properties to be displayed inline
 * <li><code>popup</code>: the properties to be displayed in a popup</li>
 * <li><code>edit</code>: the editable properties</li>
 * </ul>
 * 
 * All of them accept an array of map property names and valid map properties are:
 * <ul>
 * <li><code>coords</code></li>
 * <li><code>address</code></li>
 * <li><code>zoom</code></li>
 * <li><code>size</code></li>
 * <li><code>type</code></li>
 * <li><code>mode</code></li>
 * <li><code>map</code></li>
 * </ul>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.5
 */
public class CmsGoogleMapWidgetConfig {

    /**
     * Enumeration class for defining the map properties.<p>
     */
    public static final class CmsGoogleMapOption extends A_CmsModeStringEnumeration {

        /** Constant for the type property. */
        protected static final CmsGoogleMapOption PROPERTY_ADDRESS = new CmsGoogleMapOption("address");

        /** Constant for the coords property. */
        protected static final CmsGoogleMapOption PROPERTY_COORDS = new CmsGoogleMapOption("coords");

        /** Constant for the map property. */
        protected static final CmsGoogleMapOption PROPERTY_MAP = new CmsGoogleMapOption("map");

        /** Constant for the mode property. */
        protected static final CmsGoogleMapOption PROPERTY_MODE = new CmsGoogleMapOption("mode");

        /** Constant for the size property. */
        protected static final CmsGoogleMapOption PROPERTY_SIZE = new CmsGoogleMapOption("size");

        /** Constant for the type property. */
        protected static final CmsGoogleMapOption PROPERTY_TYPE = new CmsGoogleMapOption("type");

        /** Constant for the zoom property. */
        protected static final CmsGoogleMapOption PROPERTY_ZOOM = new CmsGoogleMapOption("zoom");

        /** The serial version id. */
        private static final long serialVersionUID = 4947546561403836577L;

        /**
         * Returns the parsed property object if the string representation matches, or <code>null</code> if not.<p>
         * 
         * @param property the string representation to parse
         * 
         * @return the parsed property object
         */
        public static CmsGoogleMapOption valueOf(final String property) {

            if (property == null) {
                return null;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_ZOOM.getMode())) {
                return CmsGoogleMapOption.PROPERTY_ZOOM;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_MODE.getMode())) {
                return CmsGoogleMapOption.PROPERTY_MODE;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_TYPE.getMode())) {
                return CmsGoogleMapOption.PROPERTY_TYPE;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_SIZE.getMode())) {
                return CmsGoogleMapOption.PROPERTY_SIZE;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_ADDRESS.getMode())) {
                return CmsGoogleMapOption.PROPERTY_ADDRESS;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_MAP.getMode())) {
                return CmsGoogleMapOption.PROPERTY_MAP;
            }
            if (property.equalsIgnoreCase(CmsGoogleMapOption.PROPERTY_COORDS.getMode())) {
                return CmsGoogleMapOption.PROPERTY_COORDS;
            }
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_UNKNOWN_MAP_PROPERTY_1,
                property));
        }

        /**
         * Default constructor.<p>
         * 
         * @param mode string representation
         */
        private CmsGoogleMapOption(final String mode) {

            super(mode);
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_ADDRESS}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_ADDRESS}
         */
        public boolean isAddress() {

            return this == CmsGoogleMapOption.PROPERTY_ADDRESS;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_COORDS}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_COORDS}
         */
        public boolean isCoords() {

            return this == CmsGoogleMapOption.PROPERTY_COORDS;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_MAP}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_MAP}
         */
        public boolean isMap() {

            return this == CmsGoogleMapOption.PROPERTY_MAP;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_MODE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_MODE}
         */
        public boolean isMode() {

            return this == CmsGoogleMapOption.PROPERTY_MODE;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_SIZE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_SIZE}
         */
        public boolean isSize() {

            return this == CmsGoogleMapOption.PROPERTY_SIZE;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_TYPE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_TYPE}
         */
        public boolean isType() {

            return this == CmsGoogleMapOption.PROPERTY_TYPE;
        }

        /**
         * Checks if <code>this</code> is {@link #PROPERTY_ZOOM}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #PROPERTY_ZOOM}
         */
        public boolean isZoom() {

            return this == CmsGoogleMapOption.PROPERTY_ZOOM;
        }
    }

    /** Default edit. */
    public static final List<CmsGoogleMapOption> DEFAULT_EDIT;

    /** Default show. */
    public static final List<CmsGoogleMapOption> DEFAULT_INLINE;

    /** Default popup. */
    public static final List<CmsGoogleMapOption> DEFAULT_POPUP;

    /** Option edit. */
    public static final String OPTION_EDIT = "edit";

    /** Option show. */
    public static final String OPTION_INLINE = "inline";

    /** Option popup. */
    public static final String OPTION_POPUP = "popup";

    /** Constant for the property address. */
    public static final CmsGoogleMapOption PROPERTY_ADDRESS = CmsGoogleMapOption.PROPERTY_ADDRESS;

    /** Constant for the property coords. */
    public static final CmsGoogleMapOption PROPERTY_COORDS = CmsGoogleMapOption.PROPERTY_COORDS;

    /** Constant for the property map. */
    public static final CmsGoogleMapOption PROPERTY_MAP = CmsGoogleMapOption.PROPERTY_MAP;

    /** Constant for the property mode. */
    public static final CmsGoogleMapOption PROPERTY_MODE = CmsGoogleMapOption.PROPERTY_MODE;

    /** Constant for the property size. */
    public static final CmsGoogleMapOption PROPERTY_SIZE = CmsGoogleMapOption.PROPERTY_SIZE;

    /** Constant for the property type. */
    public static final CmsGoogleMapOption PROPERTY_TYPE = CmsGoogleMapOption.PROPERTY_TYPE;

    /** Constant for the property zoom. */
    public static final CmsGoogleMapOption PROPERTY_ZOOM = CmsGoogleMapOption.PROPERTY_ZOOM;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsGoogleMapWidgetConfig.class);

    /** Static initialization. */
    static {
        final List<CmsGoogleMapOption> edit = new ArrayList<CmsGoogleMapOption>();
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_COORDS);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_ADDRESS);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_ZOOM);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_SIZE);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_TYPE);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_MODE);
        edit.add(CmsGoogleMapWidgetConfig.PROPERTY_MAP);
        DEFAULT_EDIT = Collections.unmodifiableList(edit);
        final List<CmsGoogleMapOption> popup = new ArrayList<CmsGoogleMapOption>();
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_COORDS);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_ADDRESS);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_ZOOM);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_SIZE);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_TYPE);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_MODE);
        popup.add(CmsGoogleMapWidgetConfig.PROPERTY_MAP);
        DEFAULT_POPUP = Collections.unmodifiableList(popup);
        final List<CmsGoogleMapOption> inline = new ArrayList<CmsGoogleMapOption>();
        inline.add(CmsGoogleMapWidgetConfig.PROPERTY_COORDS);
        DEFAULT_INLINE = Collections.unmodifiableList(inline);
    }

    /** Editable properties value. */
    private final List<CmsGoogleMapOption> m_edit;

    /** Inline displayed properties value. */
    private final List<CmsGoogleMapOption> m_inline;

    /** Popup displayed properties value. */
    private final List<CmsGoogleMapOption> m_popup;

    /**
     * Creates a new empty widget option object.<p>
     */
    public CmsGoogleMapWidgetConfig() {

        // initialize the members
        m_edit = new ArrayList<CmsGoogleMapOption>(CmsGoogleMapWidgetConfig.DEFAULT_EDIT);
        m_inline = new ArrayList<CmsGoogleMapOption>(CmsGoogleMapWidgetConfig.DEFAULT_INLINE);
        m_popup = new ArrayList<CmsGoogleMapOption>(CmsGoogleMapWidgetConfig.DEFAULT_POPUP);
    }

    /**
     * Creates a new widget option object, configured by the given configuration String.<p>
     * 
     * @param configuration configuration String to parse
     */
    public CmsGoogleMapWidgetConfig(final String configuration) {

        this();
        parseOptions(configuration);
    }

    /**
     * Adds a editable property.<p>
     *
     * @param property the property to add
     */
    public void addEdit(final CmsGoogleMapOption property) {

        if (!m_edit.contains(property)) {
            m_edit.add(property);
        }
    }

    /**
     * Adds an inline displayed property.<p>
     *
     * @param property the property to add
     */
    public void addInline(final CmsGoogleMapOption property) {

        if (!m_inline.contains(property)) {
            m_inline.add(property);
        }
    }

    /**
     * Adds a popup displayed property.<p>
     *
     * @param property the property to add
     */
    public void addPopup(final CmsGoogleMapOption property) {

        if (!m_popup.contains(property)) {
            m_popup.add(property);
        }
    }

    /**
     * Returns the editable properties.<p>
     *
     * @return the editable properties
     */
    public List<CmsGoogleMapOption> getEdit() {

        return Collections.unmodifiableList(m_edit);
    }

    /**
     * Returns only the editable properties configuration string.<p>
     * 
     * @return JSON string
     */
    public String getEditString() {

        final JSONObject json = new JSONObject();

        final Iterator<CmsGoogleMapOption> itEdit = getEdit().iterator();
        while (itEdit.hasNext()) {
            final CmsGoogleMapOption property = itEdit.next();
            try {
                json.accumulate(CmsGoogleMapWidgetConfig.OPTION_EDIT, property.toString());
            } catch (final JSONException e) {
                if (CmsGoogleMapWidgetConfig.LOG.isErrorEnabled()) {
                    CmsGoogleMapWidgetConfig.LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        // get the string
        final String res = json.toString();
        // remove the curly braces
        return res.substring(1, res.length() - 1);
    }

    /**
     * Returns the properties to be displayed inline.<p>
     *
     * @return the properties to be displayed inline
     */
    public List<CmsGoogleMapOption> getInline() {

        return Collections.unmodifiableList(m_inline);
    }

    /**
     * Returns the properties to be displayed in a popup.<p>
     *
     * @return the properties to be displayed in a popup
     */
    public List<CmsGoogleMapOption> getPopup() {

        return Collections.unmodifiableList(m_popup);
    }

    /**
     * Checks if the given property is editable.<p>
     * 
     * @param property the property to check
     * 
     * @return true if the specified property is editable, otherwise false
     */
    public boolean isEdit(final CmsGoogleMapOption property) {

        return m_edit.contains(property);
    }

    /**
     * Checks if the given property is displayed inline.<p>
     * 
     * @param property the property to check
     * 
     * @return true if the specified property is displayed inline, otherwise false
     */
    public boolean isInline(final CmsGoogleMapOption property) {

        return m_inline.contains(property);
    }

    /**
     * Checks if the given property is displayed in a popup.<p>
     * 
     * @param property the property to check
     * 
     * @return true if the specified property is displayed in a popup, otherwise false
     */
    public boolean isPopup(final CmsGoogleMapOption property) {

        return m_popup.contains(property);
    }

    /**
     * Removes a editable property.<p>
     *
     * @param property the property to remove
     */
    public void removeEdit(final CmsGoogleMapOption property) {

        m_edit.remove(property);
    }

    /**
     * Removes an inline displayed property.<p>
     *
     * @param property the property to remove
     */
    public void removeInline(final CmsGoogleMapOption property) {

        m_inline.remove(property);
    }

    /**
     * Removes a in a popup displayed property.<p>
     *
     * @param property the property to remove
     */
    public void removePopup(final CmsGoogleMapOption property) {

        m_popup.remove(property);
    }

    /**
     * Returns a widget configuration string created from this widget option.<p>
     * 
     * @return a widget configuration string created from this widget option
     */
    @Override
    public String toString() {

        final JSONObject json = new JSONObject();

        final Iterator<CmsGoogleMapOption> itInline = getInline().iterator();
        while (itInline.hasNext()) {
            final CmsGoogleMapOption property = itInline.next();
            try {
                json.accumulate(CmsGoogleMapWidgetConfig.OPTION_INLINE, property.toString());
            } catch (final JSONException e) {
                if (CmsGoogleMapWidgetConfig.LOG.isErrorEnabled()) {
                    CmsGoogleMapWidgetConfig.LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        final Iterator<CmsGoogleMapOption> itPopup = getPopup().iterator();
        while (itPopup.hasNext()) {
            final CmsGoogleMapOption property = itPopup.next();
            try {
                json.accumulate(CmsGoogleMapWidgetConfig.OPTION_POPUP, property.toString());
            } catch (final JSONException e) {
                if (CmsGoogleMapWidgetConfig.LOG.isErrorEnabled()) {
                    CmsGoogleMapWidgetConfig.LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        final Iterator<CmsGoogleMapOption> itEdit = getEdit().iterator();
        while (itEdit.hasNext()) {
            final CmsGoogleMapOption property = itEdit.next();
            try {
                json.accumulate(CmsGoogleMapWidgetConfig.OPTION_EDIT, property.toString());
            } catch (final JSONException e) {
                if (CmsGoogleMapWidgetConfig.LOG.isErrorEnabled()) {
                    CmsGoogleMapWidgetConfig.LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
        // get the string
        final String res = json.toString();
        // remove the curly braces
        return res.substring(1, res.length() - 1);
    }

    /**
     * Parses the given configuration String.<p>
     * 
     * @param configuration the configuration String to parse
     */
    protected void parseOptions(final String configuration) {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(configuration)) {
            return;
        }

        try {
            final JSONObject json = new JSONObject("{" + configuration + "}");
            if (json.has(CmsGoogleMapWidgetConfig.OPTION_INLINE)) {
                m_inline.clear();
                final JSONArray inline = json.getJSONArray(CmsGoogleMapWidgetConfig.OPTION_INLINE);
                for (int i = 0; i < inline.length(); i++) {
                    final String value = inline.getString(i);
                    addInline(CmsGoogleMapOption.valueOf(value));
                }
            }
            if (json.has(CmsGoogleMapWidgetConfig.OPTION_POPUP)) {
                m_popup.clear();
                final JSONArray popup = json.getJSONArray(CmsGoogleMapWidgetConfig.OPTION_POPUP);
                for (int i = 0; i < popup.length(); i++) {
                    final String value = popup.getString(i);
                    addPopup(CmsGoogleMapOption.valueOf(value));
                }
            }
            if (json.has(CmsGoogleMapWidgetConfig.OPTION_EDIT)) {
                m_edit.clear();
                final JSONArray edit = json.getJSONArray(CmsGoogleMapWidgetConfig.OPTION_EDIT);
                for (int i = 0; i < edit.length(); i++) {
                    final String value = edit.getString(i);
                    addEdit(CmsGoogleMapOption.valueOf(value));
                }
            }
        } catch (final JSONException e) {
            // something went wrong
            if (CmsGoogleMapWidgetConfig.LOG.isErrorEnabled()) {
                CmsGoogleMapWidgetConfig.LOG.error(e.getLocalizedMessage(), e);
            }
            return;
        }
    }
}