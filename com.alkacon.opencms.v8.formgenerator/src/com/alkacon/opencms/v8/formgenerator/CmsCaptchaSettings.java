/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsCaptchaSettings.java,v $
 * Date   : $Date: 2010/12/07 17:02:24 $
 * Version: $Revision: 1.11 $
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

package com.alkacon.opencms.v8.formgenerator;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.awt.Color;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

/**
 * Stores the settings to render captcha images.<p>
 * 
 * @author Thomas Weckert
 * @author Achim Westermann
 * 
 * @version $Revision: 1.11 $
 * 
 * @since 7.0.4 
 */
public final class CmsCaptchaSettings implements Cloneable {

    /** Request parameter for the background color. */
    public static final String C_PARAM_BACKGROUND_COLOR = "bgcol";

    /** Request parameter for the characters to use for generation. */
    public static final String C_PARAM_CHARACTERS = "crs";

    /** Request parameter for the encoded captcha data. */
    public static final String C_PARAM_DATA = "data";

    /** Request parameter for the dictionary file to use for generation. */
    public static final String C_PARAM_DICTIONARY = "dict";

    /** Request parameter for the filter amplitude. */
    public static final String C_PARAM_FILTER_AMPLITUDE = "famplit";

    /** Request parameter for the filter amplitude. */
    public static final String C_PARAM_FILTER_WAVE_LENGTH = "fwavlen";

    /** Request parameter for the font color. */
    public static final String C_PARAM_FONT_COLOR = "fcol";

    /** Request parameter for the font color. */
    public static final String C_PARAM_HOLES_PER_GLYPH = "holes";

    /** Request parameter for the image height. */
    public static final String C_PARAM_IMAGE_HEIGHT = "h";

    /** Request parameter for the image width. */
    public static final String C_PARAM_IMAGE_WIDTH = "w";

    /** Request parameter for the math flag to use for generation. */
    public static final String C_PARAM_MATH = "math";

    /** Request parameter for the max. font size. */
    public static final String C_PARAM_MAX_FONT_SIZE = "maxfs";

    /** Request parameter for the max phrase length. */
    public static final String C_PARAM_MAX_PHRASE_LENGTH = "maxpl";

    /** Request parameter for the min. font size. */
    public static final String C_PARAM_MIN_FONT_SIZE = "minfs";

    /** Request parameter for the min phrase length. */
    public static final String C_PARAM_MIN_PHRASE_LENGTH = "minpl";

    /** Request parameter for the min phrase length. */
    public static final String C_PARAM_PRESET = "prst";

    /** Request parameter for the min phrase length. */
    public static final String C_PARAM_USE_BACKGROUND_IMAGE = "bgimg";

    /** Configuration node name for the optional captcha background color. */
    public static final String NODE_CAPTCHAPRESET_BACKGROUNDCOLOR = "BackgroundColor";

    /** Configuration node name for the optional captcha character pool. */
    public static final String NODE_CAPTCHAPRESET_CHARACTERS = "Characters";

    /** Configuration node name for the optional captcha max. font size. */
    public static final String NODE_CAPTCHAPRESET_DICTIONARY = "Dictionary";

    /** Configuration node name for the field value node. */
    public static final String NODE_CAPTCHAPRESET_FILTER_AMPLITUDE = "FilterAmplitude";

    /** Configuration node name for the optional captcha image holes per glyph. */
    public static final String NODE_CAPTCHAPRESET_FILTER_WAVELENGTH = "FilterWaveLength";

    /** Configuration node name for the optional captcha font color. */
    public static final String NODE_CAPTCHAPRESET_FONTCOLOR = "FontColor";

    /** Configuration node name for the optional captcha image holes per glyph. */
    public static final String NODE_CAPTCHAPRESET_HOLESPERGLYPH = "HolesPerGlyph";

    /** Configuration node name for the optional captcha image height. */
    public static final String NODE_CAPTCHAPRESET_IMAGEHEIGHT = "ImageHeight";

    /** Configuration node name for the optional captcha image width. */
    public static final String NODE_CAPTCHAPRESET_IMAGEWIDTH = "ImageWidth";

    /** Configuration node name for the optional captcha math field flag. */
    public static final String NODE_CAPTCHAPRESET_MATHFIELD = "MathField";

    /** Configuration node name for the optional captcha max. font size. */
    public static final String NODE_CAPTCHAPRESET_MAX_FONT_SIZE = "MaxFontSize";

    /** Configuration node name for the optional captcha max. phrase length. */
    public static final String NODE_CAPTCHAPRESET_MAX_PHRASE_LENGTH = "MaxPhraseLength";

    /** Configuration node name for the optional captcha min. font size. */
    public static final String NODE_CAPTCHAPRESET_MIN_FONT_SIZE = "MinFontSize";

    /** Configuration node name for the optional captcha min. phrase length. */
    public static final String NODE_CAPTCHAPRESET_MIN_PHRASE_LENGTH = "MinPhraseLength";

    /** The encryption to be used. */
    private static final String ENCRYPTION = "DES";

    /** The format of the key and the values to be crypted. */
    private static final String FORMAT = "UTF8";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCaptchaSettings.class);

    /** The delimiter for the encrypted parameters. */
    private static final String PARAM_DELIM = "&";

    /** The key-value separator for the encrypted parameters. */
    private static final String PARAM_KV_SEPARATOR = "=";

    /** The password to be used for encryption and decryption. */
    private static final String PASSWORD = "oamp-9aX";

    /** The the background color. */
    private Color m_backgroundColor = Color.WHITE;

    /** The string containing the characters to use for word generation. */
    private String m_characterPool = "";

    /** The string containing the dictionary to use for word generation. */
    private String m_dictionary;

    /** The filter amplitude for the water filter that bends the text. */
    private int m_filterAmplitude = 2;

    /** The filter wave length for the water filter that bends the text. */
    private int m_filterWaveLength = 100;

    /** The font color. */
    private Color m_fontColor = Color.BLACK;

    /** The amount of holes per glyph. */
    private Integer m_holesPerGlyph = new Integer(0);

    /** The image height in pixels. */
    private int m_imageHeight = 55;

    /** The image width in pixels. */
    private int m_imageWidth = 220;

    /** The math field flag. */
    private boolean m_mathField;

    /** The maximum font size in pixels. */
    private int m_maxFontSize = 40;

    /** The maximum phrase length. */
    private int m_maxPhraseLength = 5;

    /** minimum font size in pixels. */
    private int m_minFontSize = 30;

    /** The minimum phrase length. */
    private int m_minPhraseLength = 5;

    /** The map of request parameters. */
    private Map<String, String[]> m_parameterMap;

    /**
     * The path to the preset configuration (captchapreset) that has been used to initialize these
     * settings. This is read only, as the path is internally read from a nested CmsForm/FormCaptcha
     * XML content.
     */
    private String m_presetPath = "factory_defaults_(classfile)";

    /** The flag that decides wethter a background image or a background color is used. */
    private boolean m_useBackgroundImage = true;

    /**
     * Private constructor for the clone method.<p>
     * 
     * May only be called from {@link #clone()} as that method guarantees to install the default
     * value from the master captcha settings.<p>
     */
    private CmsCaptchaSettings() {

        // nop
    }

    /**
     * Constructor that will use request parameters to init theses settings.<p>
     * 
     * @param jsp the jsp context object
     */
    private CmsCaptchaSettings(CmsJspActionElement jsp) {

        init(jsp);
    }

    /**
     * Returns a clone of the singleton instance of the
     * <em>"master"</em>  <code>CmsCaptchaSettings</code> and potential overridden values from
     * the request context.<p>
     * 
     * The <em>"master"</em>  <code>CmsCaptchaSettings</code> are read from an XML content that
     * contains the global defaults.<p>
     * 
     * @param jsp used to potentially access the XML content with the default captcha settings and
     *            to read overriden values from the request parameters.
     * 
     * @return a clone of the singleton instance of the
     *         <em>"master"</em> <code>CmsCaptchaSettings</code>.
     */
    public static CmsCaptchaSettings getInstance(CmsJspActionElement jsp) {

        CmsCaptchaSettings result = new CmsCaptchaSettings(jsp);
        return (CmsCaptchaSettings)result.clone();
    }

    /**
     * Returns the background color.<p>
     * 
     * @return the background color
     */
    public Color getBackgroundColor() {

        return m_backgroundColor;
    }

    /**
     * Returns the background color as a hex string.<p>
     * 
     * @return the background color as a hex string
     */
    public String getBackgroundColorString() {

        StringBuffer buf = new StringBuffer();

        buf.append("#");
        buf.append(toHexString(m_backgroundColor.getRed()));
        buf.append(toHexString(m_backgroundColor.getGreen()));
        buf.append(toHexString(m_backgroundColor.getBlue()));

        return buf.toString();
    }

    /**
     * Returns the dictionary for word generation.<p>
     * 
     * @return the dictionary for word generation
     */
    public String getDictionary() {

        return m_dictionary;
    }

    /**
     * Returns the filter amplitude for the water filter that bends the text.<p>
     * 
     * @return the filter amplitude for the water filter that bends the text.
     */
    public int getFilterAmplitude() {

        return m_filterAmplitude;
    }

    /**
     * Returns the filter wave length for the water filter that bends the text.<p>
     * 
     * @return the filter wave length for the water filter that bends the text.
     */
    public int getFilterWaveLength() {

        return m_filterWaveLength;
    }

    /**
     * Returns the font color.<p>
     * 
     * @return the font color
     */
    public Color getFontColor() {

        return m_fontColor;
    }

    /**
     * Returns the font color as a hex string.<p>
     * 
     * @return the font color as a hex string
     */
    public String getFontColorString() {

        StringBuffer buf = new StringBuffer();

        buf.append("#");
        buf.append(toHexString(m_fontColor.getRed()));
        buf.append(toHexString(m_fontColor.getGreen()));
        buf.append(toHexString(m_fontColor.getBlue()));

        return buf.toString();
    }

    /**
     * Returns the holes per glyph for a captcha image text (distortion).<p>
     * 
     * @return the holes per glyph for a captcha image text
     */
    public Integer getHolesPerGlyph() {

        return m_holesPerGlyph;
    }

    /**
     * Returns the image height.<p>
     * 
     * @return the image height
     */
    public int getImageHeight() {

        return m_imageHeight;
    }

    /**
     * Returns the image width.<p>
     * 
     * @return the image width
     */
    public int getImageWidth() {

        return m_imageWidth;
    }

    /**
     * Returns the max. font size.<p>
     * 
     * @return the max. font size
     */
    public int getMaxFontSize() {

        return m_maxFontSize;
    }

    /**
     * Returns the max. phrase length.<p>
     * 
     * @return the max. phrase length
     */
    public int getMaxPhraseLength() {

        return m_maxPhraseLength;
    }

    /**
     * Returns the min. font size.<p>
     * 
     * @return the min. font size
     */
    public int getMinFontSize() {

        return m_minFontSize;
    }

    /**
     * Returns the min. phrase length.<p>
     * 
     * @return the min. phrase length
     */
    public int getMinPhraseLength() {

        return m_minPhraseLength;
    }

    /**
     * Configures the instance with values overridden from the the request parameters.<p>
     * 
     * @param jsp a Cms JSP page
     * 
     * @see #C_PARAM_BACKGROUND_COLOR
     * @see #C_PARAM_FILTER_AMPLITUDE
     */
    public void init(CmsJspActionElement jsp) {

        List<FileItem> multipartFileItems = CmsRequestUtil.readMultipartFileItems(jsp.getRequest());
        m_parameterMap = new HashMap<String, String[]>();
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        if (multipartFileItems != null) {
            parameters = CmsRequestUtil.readParameterMapFromMultiPart(
                jsp.getRequestContext().getEncoding(),
                multipartFileItems);
        } else {
            parameters = jsp.getRequest().getParameterMap();
        }
        if (parameters.containsKey(C_PARAM_DATA)) {
            // found encrypted data parameter, decrypt it
            String data = decrypt((parameters.get(C_PARAM_DATA))[0]);
            if (data != null) {
                // split the data into parameters
                Map<String, String> dataParameters = CmsStringUtil.splitAsMap(data, PARAM_DELIM, PARAM_KV_SEPARATOR);
                m_parameterMap = new HashMap<String, String[]>(dataParameters.size());
                for (Iterator<Entry<String, String>> i = dataParameters.entrySet().iterator(); i.hasNext();) {
                    // store values as String array
                    Map.Entry<String, String> entry = i.next();
                    m_parameterMap.put(entry.getKey(), new String[] {entry.getValue()});
                }
            }
        } else {
            // no encrypted parameters found, create empty map to use defaults
            m_parameterMap = new HashMap<String, String[]>();
        }

        // image width
        String stringValue = getParameter(C_PARAM_IMAGE_WIDTH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_imageWidth = Integer.parseInt(stringValue);
        }

        // image height
        stringValue = getParameter(C_PARAM_IMAGE_HEIGHT);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_imageHeight = Integer.parseInt(stringValue);
        }

        // min. phrase length
        stringValue = getParameter(C_PARAM_MIN_PHRASE_LENGTH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_minPhraseLength = Integer.parseInt(stringValue);
        }

        // max. phrase length
        stringValue = getParameter(C_PARAM_MAX_PHRASE_LENGTH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_maxPhraseLength = Integer.parseInt(stringValue);
        }

        // min. font size
        stringValue = getParameter(C_PARAM_MIN_FONT_SIZE);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_minFontSize = Integer.parseInt(stringValue);
        }

        // max. font size
        stringValue = getParameter(C_PARAM_MAX_FONT_SIZE);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_maxFontSize = Integer.parseInt(stringValue);
        }

        // font color
        stringValue = getParameter(C_PARAM_FONT_COLOR);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            stringValue = CmsEncoder.unescape(stringValue, jsp.getRequestContext().getEncoding());
            setFontColor(stringValue);
        }

        // background color
        stringValue = getParameter(C_PARAM_BACKGROUND_COLOR);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            stringValue = CmsEncoder.unescape(stringValue, jsp.getRequestContext().getEncoding());
        }
        setBackgroundColor(stringValue);

        // holes per glyph
        stringValue = getParameter(C_PARAM_HOLES_PER_GLYPH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setHolesPerGlyph(Integer.parseInt(stringValue));
        }

        // filter amplitude
        stringValue = getParameter(C_PARAM_FILTER_AMPLITUDE);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setFilterAmplitude(Integer.parseInt(stringValue));
        }

        // filter wave length
        stringValue = getParameter(C_PARAM_FILTER_WAVE_LENGTH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setFilterWaveLength(Integer.parseInt(stringValue));
        }
        // flag for generation of background image (vs. background color)
        stringValue = getParameter(C_PARAM_USE_BACKGROUND_IMAGE);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setUseBackgroundImage(Boolean.valueOf(stringValue).booleanValue());
        }

        // characters to use for word generation:
        stringValue = getParameter(C_PARAM_CHARACTERS);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setCharacterPool(stringValue);
        }
        // dictionary to use for word generation:
        stringValue = getParameter(C_PARAM_DICTIONARY);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setDictionary(stringValue);
        }

        // math field flag
        stringValue = getParameter(C_PARAM_MATH);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            setMathField(Boolean.valueOf(stringValue).booleanValue());
        }

        // just for logging comfort (find misconfigured presets):
        stringValue = getParameter(C_PARAM_PRESET);
        if (CmsStringUtil.isNotEmpty(stringValue)) {
            m_presetPath = stringValue;
        }
    }

    /**
     * Configures the instance with overridden values from the given XML content.<p>
     * 
     * <h3>Xmlcontent configuration notes</h3>
     * <ol>
     *   <li>
     *     <ul>
     *       <li> If the xmlcontent contains no node for BackgroundColor ({@link CmsCaptchaSettings#NODE_CAPTCHAPRESET_BACKGROUNDCOLOR}),
     *          a background image will be used. </li>
     *       <li> If the xmlcontent node contains an empty node (trimmable to the empty String), the
     *          default background colour {@link Color#WHITE}) will be used as background. </li>
     *       <li> Else the chosen background color will be used. </li>
     *     </ul>
     *   </li>
     * </ol>
     * <p>
     * 
     * @param cms the current user's Cms object
     * @param content the XML content of the form
     * @param locale the current locale
     */
    public void init(CmsObject cms, CmsXmlContent content, Locale locale) {

        try {
            String captchaSettingsPath = CmsFormContentUtil.getContentStringValue(content, cms, new StringBuffer(
                CmsForm.NODE_CAPTCHA).append("/").append(CmsForm.NODE_CAPTCHA_PRESET).toString(), locale);
            if (CmsStringUtil.isNotEmpty(captchaSettingsPath)) {
                m_presetPath = captchaSettingsPath;
                CmsFile captchaSettingsFile = cms.readFile(captchaSettingsPath);
                CmsXmlContent preset = CmsXmlContentFactory.unmarshal(cms, captchaSettingsFile);

                Locale captchaSettingsLocale = Locale.ENGLISH;

                // image width
                String stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_IMAGEWIDTH,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_imageWidth = Integer.parseInt(stringValue);
                }

                // image height
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_IMAGEHEIGHT,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_imageHeight = Integer.parseInt(stringValue);
                }

                // min. phrase length
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_MIN_PHRASE_LENGTH,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_minPhraseLength = Integer.parseInt(stringValue);
                }

                // max. phrase length
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_MAX_PHRASE_LENGTH,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_maxPhraseLength = Integer.parseInt(stringValue);
                }

                // min. font size
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_MIN_FONT_SIZE,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_minFontSize = Integer.parseInt(stringValue);
                }

                // max. font size
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_MAX_FONT_SIZE,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    m_maxFontSize = Integer.parseInt(stringValue);
                }

                // font color
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_FONTCOLOR,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setFontColor(stringValue);
                }

                // background color
                // if the field is defined but left blank, the default background color will be used
                // if the field is not defined a gimpy background image will be used
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_BACKGROUNDCOLOR,
                    captchaSettingsLocale);
                setBackgroundColor(stringValue);

                // holes per glyph
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_HOLESPERGLYPH,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setHolesPerGlyph(Integer.parseInt(stringValue));
                }

                // filter amplitude
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_FILTER_AMPLITUDE,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setFilterAmplitude(Integer.parseInt(stringValue));
                }

                // filter wave length
                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_FILTER_WAVELENGTH,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setFilterWaveLength(Integer.parseInt(stringValue));
                }

                stringValue = preset.getStringValue(cms, NODE_CAPTCHAPRESET_CHARACTERS, captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setCharacterPool(stringValue);
                }

                stringValue = preset.getStringValue(
                    cms,
                    CmsCaptchaSettings.NODE_CAPTCHAPRESET_DICTIONARY,
                    captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setDictionary(stringValue);
                }

                // math field flag
                stringValue = preset.getStringValue(cms, NODE_CAPTCHAPRESET_MATHFIELD, captchaSettingsLocale);
                if (CmsStringUtil.isNotEmpty(stringValue)) {
                    setMathField(Boolean.valueOf(stringValue).booleanValue());
                }

            } else {
                // the optional preset selector is missing...
            }

        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(ex.getLocalizedMessage());
            }

        }
    }

    /**
     * Returns the math field flag.<p>
     * 
     * @return the math field flag
     */
    public boolean isMathField() {

        return m_mathField;
    }

    /**
     * Returns the flag that decides wethter a background image or a background color is used.<p>
     * 
     * @return the flag that decides wethter a background image or a background color is used
     */
    public boolean isUseBackgroundImage() {

        return m_useBackgroundImage;
    }

    /**
     * Sets the background color.<p>
     * 
     * @param backgroundColor the background color to set
     */
    public void setBackgroundColor(Color backgroundColor) {

        m_backgroundColor = backgroundColor;
    }

    /**
     * Sets the background color as a hex string.<p>
     * 
     * @param backgroundColor the background color to set as a hex string
     */
    public void setBackgroundColor(String backgroundColor) {

        if (CmsStringUtil.isNotEmpty(backgroundColor)) {
            if (backgroundColor.startsWith("#")) {
                backgroundColor = backgroundColor.substring(1);
            }
            m_backgroundColor = new Color(Integer.valueOf(backgroundColor, 16).intValue());
            m_useBackgroundImage = false;
        } else if (backgroundColor != null) {
            // not totally empty but consists of whitespaces only: use default value
            // this happens e.g. if the XML content to configure did contain the node but left the
            // value empty
            // in this case the default background color will be used
            m_useBackgroundImage = false;
            m_backgroundColor = Color.WHITE;
        } else {
            // really empty and null - not even defined in XML content:
            // don't use background color but a gimpy background image
            m_useBackgroundImage = true;
            // the color is not used but we have to avoid NPE in getBackgroundColorString()
            m_backgroundColor = Color.WHITE;
        }
    }

    /**
     * Sets the dictionary for word generation.<p>
     *
     * @param dictionary the dictionary to set
     */
    public void setDictionary(String dictionary) {

        m_dictionary = dictionary;
    }

    /**
     * Sets the filter amplitude for the water filter that will bend the text.<p>
     * 
     * @param i the filter amplitude for the water filter that will bend the text to set.
     */
    public void setFilterAmplitude(int i) {

        m_filterAmplitude = i;

    }

    /**
     * Sets the filter wave length for the water filter that bends the text.<p>
     * 
     * @param filterWaveLength the filter wave length for the water filter that bends the text to  set
     */
    public void setFilterWaveLength(int filterWaveLength) {

        m_filterWaveLength = filterWaveLength;
    }

    /**
     * Sets the font color.<p>
     * 
     * @param fontColor the font color to set
     */
    public void setFontColor(Color fontColor) {

        m_fontColor = fontColor;
    }

    /**
     * Sets the font color as a hex string.<p>
     * 
     * @param fontColor the font color to set as a hex string
     */
    public void setFontColor(String fontColor) {

        if (CmsStringUtil.isNotEmpty(fontColor)) {
            if (fontColor.startsWith("#")) {
                fontColor = fontColor.substring(1);
            }
            m_fontColor = new Color(Integer.valueOf(fontColor, 16).intValue());
        } else {
            m_fontColor = Color.BLACK;
        }
    }

    /**
     * Sets the holes per glyph for a captcha image text (distortion).<p>
     * 
     * @param holes the holes per glyph for a captcha image text to set.
     */
    public void setHolesPerGlyph(int holes) {

        m_holesPerGlyph = new Integer(holes);
    }

    /**
     * Sets the image height.<p>
     * 
     * @param imageHeight the image height to set
     */
    public void setImageHeight(int imageHeight) {

        m_imageHeight = imageHeight;
    }

    /**
     * Sets the image width.<p>
     * 
     * @param imageWidth the image width to set
     */
    public void setImageWidth(int imageWidth) {

        m_imageWidth = imageWidth;
    }

    /**
     * Sets the math field flag.<p>
     * 
     * @param mathField the math field flag
     */
    public void setMathField(boolean mathField) {

        this.m_mathField = mathField;
    }

    /**
     * Sets the max. font size.<p>
     * 
     * @param maxFontSize the max. font size to set
     */
    public void setMaxFontSize(int maxFontSize) {

        m_maxFontSize = maxFontSize;
    }

    /**
     * Sets the max. phrase length.<p>
     * 
     * @param maxPhraseLength the max. phrase length to set
     */
    public void setMaxPhraseLength(int maxPhraseLength) {

        m_maxPhraseLength = maxPhraseLength;
    }

    /**
     * Sets the min. font size.<p>
     * 
     * @param minFontSize the min. font size to set
     */
    public void setMinFontSize(int minFontSize) {

        m_minFontSize = minFontSize;
    }

    /**
     * Sets the minimum phrase length.<p>
     * 
     * @param minPhraseLength the minimum phrase length to set
     */
    public void setMinPhraseLength(int minPhraseLength) {

        m_minPhraseLength = minPhraseLength;
    }

    /**
     * Returns the flag that decides whether a background image or a background color is used.<p>
     * 
     * @param useBackgroundImage the flag that decides whether a background image or a background
     *            color is used.
     */
    public void setUseBackgroundImage(boolean useBackgroundImage) {

        m_useBackgroundImage = useBackgroundImage;
    }

    /**
     * Creates a request parameter string from including all captcha settings.<p>
     * 
     * @param cms needed for the context / encoding
     * @return a request parameter string from including all captcha settings
     */
    public String toRequestParams(CmsObject cms) {

        StringBuffer buf = new StringBuffer(2048);

        buf.append(C_PARAM_IMAGE_WIDTH).append(PARAM_KV_SEPARATOR).append(m_imageWidth);
        buf.append(PARAM_DELIM).append(C_PARAM_IMAGE_HEIGHT).append(PARAM_KV_SEPARATOR).append(m_imageHeight);
        buf.append(PARAM_DELIM).append(C_PARAM_MIN_FONT_SIZE).append(PARAM_KV_SEPARATOR).append(m_minFontSize);
        buf.append(PARAM_DELIM).append(C_PARAM_MAX_FONT_SIZE).append(PARAM_KV_SEPARATOR).append(m_maxFontSize);
        buf.append(PARAM_DELIM).append(C_PARAM_MIN_PHRASE_LENGTH).append(PARAM_KV_SEPARATOR).append(m_minPhraseLength);
        buf.append(PARAM_DELIM).append(C_PARAM_MAX_PHRASE_LENGTH).append(PARAM_KV_SEPARATOR).append(m_maxPhraseLength);
        buf.append(PARAM_DELIM).append(C_PARAM_FONT_COLOR).append(PARAM_KV_SEPARATOR).append(
            CmsEncoder.escape(getFontColorString(), cms.getRequestContext().getEncoding()));
        buf.append(PARAM_DELIM).append(C_PARAM_BACKGROUND_COLOR).append(PARAM_KV_SEPARATOR).append(
            CmsEncoder.escape(getBackgroundColorString(), cms.getRequestContext().getEncoding()));
        buf.append(PARAM_DELIM).append(C_PARAM_HOLES_PER_GLYPH).append(PARAM_KV_SEPARATOR).append(m_holesPerGlyph);
        buf.append(PARAM_DELIM).append(C_PARAM_FILTER_AMPLITUDE).append(PARAM_KV_SEPARATOR).append(m_filterAmplitude);
        buf.append(PARAM_DELIM).append(C_PARAM_FILTER_WAVE_LENGTH).append(PARAM_KV_SEPARATOR).append(m_filterWaveLength);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getCharacterPool())) {
            buf.append(PARAM_DELIM).append(C_PARAM_CHARACTERS).append(PARAM_KV_SEPARATOR).append(getCharacterPool());
        } else {
            buf.append(PARAM_DELIM).append(C_PARAM_DICTIONARY).append(PARAM_KV_SEPARATOR).append(m_dictionary);
        }
        buf.append(PARAM_DELIM).append(C_PARAM_PRESET).append(PARAM_KV_SEPARATOR).append(m_presetPath);
        buf.append(PARAM_DELIM).append(C_PARAM_USE_BACKGROUND_IMAGE).append(PARAM_KV_SEPARATOR).append(
            Boolean.toString(m_useBackgroundImage));

        String result = "";
        // encrypt the parameters
        String encValues = encrypt(buf.toString());
        if (encValues != null) {
            result = C_PARAM_DATA + PARAM_KV_SEPARATOR + encValues;
        }
        return result;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() {

        CmsCaptchaSettings result = new CmsCaptchaSettings();
        // copy all members here:
        result.m_backgroundColor = m_backgroundColor;
        result.m_filterAmplitude = m_filterAmplitude;
        result.m_filterWaveLength = m_filterWaveLength;
        result.m_fontColor = m_fontColor;
        result.m_holesPerGlyph = m_holesPerGlyph;
        result.m_imageHeight = m_imageHeight;
        result.m_imageWidth = m_imageWidth;
        result.m_maxFontSize = m_maxFontSize;
        result.m_maxPhraseLength = m_maxPhraseLength;
        result.m_minFontSize = m_minFontSize;
        result.m_useBackgroundImage = m_useBackgroundImage;
        result.m_minPhraseLength = m_minPhraseLength;
        result.m_characterPool = m_characterPool;
        result.m_presetPath = m_presetPath;
        result.m_dictionary = m_dictionary;
        result.m_mathField = m_mathField;
        return result;
    }

    /**
     * Returns the character Pool.<p>
     * 
     * @return the character Pool
     */
    String getCharacterPool() {

        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(getDictionary())) {
            // dictionary has priority
            return "";
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_characterPool)) {
            // default value
            return "abcdefghijklmnopqrstuvwxyz";
        }
        return m_characterPool;
    }

    /**
     * Returns the preset path that was used to configure these settings.<p>
     * 
     * This is read only, as the path is internally read from a nested CmsForm/FormCaptcha XML
     * content.<p>
     * 
     * @return the preset path that was used to configure these settings
     */
    String getPresetPath() {

        return m_presetPath;
    }

    /**
     * Sets the character Pool.<p>
     * 
     * @param characterPool the character Pool to set
     */
    void setCharacterPool(String characterPool) {

        m_characterPool = characterPool;
    }

    /**
     * Decrypts the given value which was encrypted with the encrypt method.<p>
     * 
     * @param value the value to be decrypted
     * @return the decrypted string of the value or null if something went wrong
     */
    private String decrypt(String value) {

        // check if given value is valid
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
            // no input available
            return null;
        }

        try {
            // create key
            Key key = new SecretKeySpec(getKey(), ENCRYPTION);
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, key);

            // decode from base64
            byte[] cleartext = Base64.decodeBase64(value.getBytes());

            // decrypt text
            byte[] ciphertext = cipher.doFinal(cleartext);
            return CmsEncoder.decode(new String(ciphertext));
        } catch (Exception ex) {
            // error while decrypting
        }

        return null;
    }

    /**
     * Encrypts the given value.<p>
     * 
     * @param value the string which should be encrypted
     * @return the encrypted string of the value or null if something went wrong
     */
    private String encrypt(String value) {

        // check if given value is valid
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
            // no input available
            return null;
        }

        try {
            // create key
            byte[] k = getKey();
            Key key = new SecretKeySpec(k, ENCRYPTION);
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // encrypt text
            byte[] cleartext = value.getBytes(FORMAT);
            byte[] ciphertext = cipher.doFinal(cleartext);

            // encode with base64 to be used as a url parameter
            return CmsEncoder.encode(new String(Base64.encodeBase64(ciphertext)));
        } catch (Exception ex) {
            // error while encrypting
        }

        return null;
    }

    /**
     * Converts the password to machine readable form.<p>
     * 
     * @return the password in machine readable form
     */
    private byte[] getKey() {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(PASSWORD.toString().getBytes());
            byte[] key = md5.digest();
            // now get the first 8 bytes
            byte[] finalKey = new byte[8];
            for (int i = 0; i <= 7; i++) {
                finalKey[i] = key[i];
            }
            return finalKey;
        } catch (NoSuchAlgorithmException ex) {
            // found no matching algorithm
        }
        return null;
    }

    /**
     * Returns the request parameter with the specified name.<p>
     * 
     * @param parameter the parameter to return
     * 
     * @return the parameter value
     */
    private String getParameter(String parameter) {

        try {
            return (m_parameterMap.get(parameter))[0];
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Converts a color range of a color into a hex string.<p>
     * 
     * @param colorRange the color range of a color
     * @return the hex string of the color range
     */
    private String toHexString(int colorRange) {

        if (colorRange < 10) {
            return "0" + Integer.toHexString(colorRange);
        } else {
            return Integer.toHexString(colorRange);
        }
    }
}
