/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.mapwidget/src/com/alkacon/opencms/mapwidget/CmsGoogleMapWidgetValue.java,v $
 * Date   : $Date: 2009/12/17 19:38:28 $
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

package com.alkacon.opencms.mapwidget;

import org.opencms.json.JSONException;
import org.opencms.json.JSONObject;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.util.A_CmsModeStringEnumeration;
import org.opencms.util.CmsStringUtil;

import org.apache.commons.logging.Log;

/**
 * A value of the google map widget.<p>
 * 
 * This is the parsed value of an element of the type <code>String</code> using the widget <code>GoogleMapWidget</code>.
 * 
 * <code>lat:50.953412,lng:6.956534,zoom:13,width:400,height:300,mode:dynamic,type:hybrid</code><p>
 * 
 * Available options are:
 * <ul>
 * <li><code>lat:50.953412</code>: the latitude</li>
 * <li><code>lng:6.956534</code>: the longitude</li>
 * <li><code>zoom:7</code>: initial zoom level</li>
 * <li><code>width:300</code>: map width in pixels or %</li>
 * <li><code>height:200</code>: map height in pixels or %</li>
 * <li><code>mode:'static'</code>: the front-end map's mode should be dynamic or static</li>
 * <li><code>type:'hybrid'</code>: the map type, which can be normal, hybrid, satellite and physical</li>
 * </ul>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.5
 */
public class CmsGoogleMapWidgetValue {

    /**
     * Enumeration class for defining the map mode.<p>
     */
    public static final class CmsGoogleMapMode extends A_CmsModeStringEnumeration {

        /** Constant for the dynamic mode. */
        protected static final CmsGoogleMapMode MODE_DYNAMIC = new CmsGoogleMapMode("dynamic");

        /** Constant for the static mode. */
        protected static final CmsGoogleMapMode MODE_STATIC = new CmsGoogleMapMode("static");

        /** The serial version id. */
        private static final long serialVersionUID = -1082267243842151785L;

        /**
         * Returns the parsed mode object if the string representation matches, or <code>null</code> if not.<p>
         * 
         * @param mode the string representation to parse
         * 
         * @return the parsed mode object
         */
        public static CmsGoogleMapMode valueOf(String mode) {

            if (mode == null) {
                return null;
            }
            if (mode.equalsIgnoreCase(CmsGoogleMapMode.MODE_STATIC.getMode())) {
                return CmsGoogleMapMode.MODE_STATIC;
            }
            if (mode.equalsIgnoreCase(CmsGoogleMapMode.MODE_DYNAMIC.getMode())) {
                return CmsGoogleMapMode.MODE_DYNAMIC;
            }
            throw new CmsIllegalArgumentException(Messages.get().container(Messages.ERR_UNKNOWN_MAP_MODE_1, mode));
        }

        /**
         * Default constructor.<p>
         * 
         * @param mode string representation
         */
        private CmsGoogleMapMode(String mode) {

            super(mode);
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_DYNAMIC}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_DYNAMIC}
         */
        public boolean isDynamic() {

            return this == CmsGoogleMapMode.MODE_DYNAMIC;
        }

        /**
         * Checks if <code>this</code> is {@link #MODE_STATIC}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #MODE_STATIC}
         */
        public boolean isStatic() {

            return this == CmsGoogleMapMode.MODE_STATIC;
        }
    }

    /**
     * Enumeration class for defining the map types.<p>
     */
    public static final class CmsGoogleMapType extends A_CmsModeStringEnumeration {

        /** Constant for the hybrid property. */
        protected static final CmsGoogleMapType TYPE_HYBRID = new CmsGoogleMapType("hybrid");

        /** Constant for the normal type. */
        protected static final CmsGoogleMapType TYPE_MAP = new CmsGoogleMapType("map");

        /** Constant for the physical property. */
        protected static final CmsGoogleMapType TYPE_PHYSICAL = new CmsGoogleMapType("physical");

        /** Constant for the satellite property. */
        protected static final CmsGoogleMapType TYPE_SATELLITE = new CmsGoogleMapType("satellite");

        /** The serial version id. */
        private static final long serialVersionUID = 4648592639170665274L;

        /**
         * Returns the parsed type object if the string representation matches, or <code>null</code> if not.<p>
         * 
         * @param mapType the string representation to parse
         * 
         * @return the parsed type object
         */
        public static CmsGoogleMapType valueOf(String mapType) {

            if (mapType == null) {
                return null;
            }
            if (mapType.equalsIgnoreCase(CmsGoogleMapType.TYPE_PHYSICAL.getMode())) {
                return CmsGoogleMapType.TYPE_PHYSICAL;
            }
            if (mapType.equalsIgnoreCase(CmsGoogleMapType.TYPE_HYBRID.getMode())) {
                return CmsGoogleMapType.TYPE_HYBRID;
            }
            if (mapType.equalsIgnoreCase(CmsGoogleMapType.TYPE_MAP.getMode())) {
                return CmsGoogleMapType.TYPE_MAP;
            }
            if (mapType.equalsIgnoreCase(CmsGoogleMapType.TYPE_SATELLITE.getMode())) {
                return CmsGoogleMapType.TYPE_SATELLITE;
            }
            throw new CmsIllegalArgumentException(Messages.get().container(Messages.ERR_UNKNOWN_MAP_TYPE_1, mapType));
        }

        /**
         * Default constructor.<p>
         * 
         * @param mode string representation
         */
        private CmsGoogleMapType(String mode) {

            super(mode);
        }

        /**
         * Checks if <code>this</code> is {@link #TYPE_HYBRID}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #TYPE_HYBRID}
         */
        public boolean isHybrid() {

            return this == CmsGoogleMapType.TYPE_HYBRID;
        }

        /**
         * Checks if <code>this</code> is {@link #TYPE_MAP}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #TYPE_MAP}
         */
        public boolean isMap() {

            return this == CmsGoogleMapType.TYPE_MAP;
        }

        /**
         * Checks if <code>this</code> is {@link #TYPE_PHYSICAL}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #TYPE_PHYSICAL}
         */
        public boolean isPhysical() {

            return this == CmsGoogleMapType.TYPE_PHYSICAL;
        }

        /**
         * Checks if <code>this</code> is {@link #TYPE_SATELLITE}.<p>
         * 
         * @return <code>true</code>, if <code>this</code> is {@link #TYPE_SATELLITE}
         */
        public boolean isSatellite() {

            return this == CmsGoogleMapType.TYPE_SATELLITE;
        }
    }

    /** The default map height in pixels. */
    public static final int DEFAULT_HEIGHT = 300;

    /** The default latitude. */
    public static final float DEFAULT_LAT = 0;

    /** The default longitude. */
    public static final float DEFAULT_LNG = 0;

    /** The default map mode. */
    public static final CmsGoogleMapMode DEFAULT_MODE = CmsGoogleMapMode.MODE_DYNAMIC;

    /** The default map type. */
    public static final CmsGoogleMapType DEFAULT_TYPE = CmsGoogleMapType.TYPE_MAP;

    /** The default map width in pixels. */
    public static final int DEFAULT_WIDTH = 400;

    /** The default zoom level. */
    public static final int DEFAULT_ZOOM = 10;

    /** Constant for the dynamic mode. */
    public static final CmsGoogleMapMode MAPMODE_DYNAMIC = CmsGoogleMapMode.MODE_DYNAMIC;

    /** Constant for the static mode. */
    public static final CmsGoogleMapMode MAPMODE_STATIC = CmsGoogleMapMode.MODE_STATIC;

    /** Constant for the map type hybrid. */
    public static final CmsGoogleMapType MAPTYPE_HYBRID = CmsGoogleMapType.TYPE_HYBRID;

    /** Constant for the map type normal. */
    public static final CmsGoogleMapType MAPTYPE_MAP = CmsGoogleMapType.TYPE_MAP;

    /** Constant for the map type physical. */
    public static final CmsGoogleMapType MAPTYPE_PHYSICAL = CmsGoogleMapType.TYPE_PHYSICAL;

    /** Constant for the map type satellite. */
    public static final CmsGoogleMapType MAPTYPE_SATELLITE = CmsGoogleMapType.TYPE_SATELLITE;

    /** Option height. */
    public static final String OPTION_HEIGHT = "height";

    /** Option lat. */
    public static final String OPTION_LAT = "lat";

    /** Option lng. */
    public static final String OPTION_LNG = "lng";

    /** Option mode. */
    public static final String OPTION_MODE = "mode";

    /** Option type. */
    public static final String OPTION_TYPE = "type";

    /** Option width. */
    public static final String OPTION_WIDTH = "width";

    /** Option zoom. */
    public static final String OPTION_ZOOM = "zoom";

    /** The maximum zoom level. */
    public static final int ZOOM_MAX = 20;

    /** The minimum zoom level. */
    public static final int ZOOM_MIN = 0;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsGoogleMapWidgetValue.class);

    /** Map height value. */
    private int m_height;

    /** Map center latitude value. */
    private float m_lat;

    /** Map center longitude value. */
    private float m_lng;

    /** Map mode value. */
    private CmsGoogleMapMode m_mode;

    /** Map type value. */
    private CmsGoogleMapType m_type;

    /** Map width value. */
    private int m_width;

    /** Map zoom value. */
    private int m_zoom;

    /**
     * Creates a new empty widget option object.<p>
     */
    public CmsGoogleMapWidgetValue() {

        // initialize the members
        m_zoom = DEFAULT_ZOOM;
        m_height = DEFAULT_HEIGHT;
        m_width = DEFAULT_WIDTH;
        m_mode = DEFAULT_MODE;
        m_type = DEFAULT_TYPE;
        m_lat = DEFAULT_LAT;
        m_lng = DEFAULT_LNG;
    }

    /**
     * Creates a new widget option object, configured by the given configuration String.<p>
     * 
     * @param configuration configuration String to parse
     */
    public CmsGoogleMapWidgetValue(String configuration) {

        this();
        parseOptions(configuration);
    }

    /**
     * Returns the height.<p>
     *
     * @return the height
     */
    public int getHeight() {

        return m_height;
    }

    /**
     * Returns the lat.<p>
     *
     * @return the lat
     */
    public float getLat() {

        return m_lat;
    }

    /**
     * Returns the longitude.<p>
     *
     * @return the longitude
     */
    public float getLng() {

        return m_lng;
    }

    /**
     * Returns the mode.<p>
     *
     * @return the mode
     */
    public CmsGoogleMapMode getMode() {

        return m_mode;
    }

    /**
     * Returns the type.<p>
     *
     * @return the type
     */
    public CmsGoogleMapType getType() {

        return m_type;
    }

    /**
     * Returns the width.<p>
     *
     * @return the width
     */
    public int getWidth() {

        return m_width;
    }

    /**
     * Returns the zoom.<p>
     *
     * @return the zoom
     */
    public int getZoom() {

        return m_zoom;
    }

    /**
     * Sets the height.<p>
     *
     * @param height the height to set
     */
    public void setHeight(int height) {

        m_height = height;
    }

    /**
     * Sets the latitude.<p>
     *
     * @param lat the latitude to set
     */
    public void setLat(float lat) {

        m_lat = lat;
    }

    /**
     * Sets the longitude.<p>
     *
     * @param lng the longitude to set
     */
    public void setLng(float lng) {

        m_lng = lng;
    }

    /**
     * Sets the mode.<p>
     *
     * @param mode the mode to set
     */
    public void setMode(CmsGoogleMapMode mode) {

        m_mode = mode;
    }

    /**
     * Sets the type.<p>
     *
     * @param type the type to set
     */
    public void setType(CmsGoogleMapType type) {

        m_type = type;
    }

    /**
     * Sets the width.<p>
     *
     * @param width the width to set
     */
    public void setWidth(int width) {

        m_width = width;
    }

    /**
     * Sets the zoom.<p>
     *
     * @param zoom the zoom to set
     */
    public void setZoom(int zoom) {

        if ((zoom > ZOOM_MAX) || (zoom < ZOOM_MIN)) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_PARAMETER_OUT_OF_RANGE_2,
                OPTION_ZOOM,
                new Integer(zoom)));
        }
        m_zoom = zoom;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        JSONObject json = new JSONObject();

        try {
            json.put(OPTION_LAT, getLat());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_LNG, getLng());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_ZOOM, getZoom());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_WIDTH, getWidth());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_HEIGHT, getHeight());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_TYPE, getType().toString());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        try {
            json.put(OPTION_MODE, getMode().toString());
        } catch (JSONException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        // get the string
        String res = json.toString();
        // remove the curly braces
        return res.substring(1, res.length() - 1);
    }

    /**
     * Parses the given configuration String.<p>
     * 
     * @param configuration the configuration String to parse
     */
    protected void parseOptions(String configuration) {

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(configuration)) {
            return;
        }
        try {
            JSONObject json = new JSONObject("{" + configuration + "}");
            if (json.has(OPTION_LAT)) {
                setLat((float)json.getDouble(OPTION_LAT));
            }
            if (json.has(OPTION_LNG)) {
                setLng((float)json.getDouble(OPTION_LNG));
            }
            if (json.has(OPTION_ZOOM)) {
                setZoom(json.getInt(OPTION_ZOOM));
            }
            if (json.has(OPTION_WIDTH)) {
                setWidth(json.getInt(OPTION_WIDTH));
            }
            if (json.has(OPTION_HEIGHT)) {
                setHeight(json.getInt(OPTION_HEIGHT));
            }
            if (json.has(OPTION_TYPE)) {
                setType(CmsGoogleMapType.valueOf(json.getString(OPTION_TYPE)));
            }
            if (json.has(OPTION_MODE)) {
                setMode(CmsGoogleMapMode.valueOf(json.getString(OPTION_MODE)));
            }
        } catch (JSONException e) {
            // something went wrong
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            return;
        }
    }
}