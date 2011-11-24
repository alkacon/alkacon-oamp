/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsCaptchaEngine.java,v $
 * Date   : $Date: 2011/03/09 15:14:35 $
 * Version: $Revision: 1.8 $
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

import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.jhlabs.image.WaterFilter;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.word.wordgenerator.DictionaryWordGenerator;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.engine.image.ImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;

/**
 * A captcha engine using a Gimpy factory to create captchas.
 * <p>
 * 
 * @author Thomas Weckert
 * @author Achim Westermann
 * 
 * @version $Revision: 1.8 $
 * 
 * @since 7.0.4 
 */
public class CmsCaptchaEngine extends ImageCaptchaEngine {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCaptchaEngine.class);

    /** The configured image captcha factory. */
    private ImageCaptchaFactory m_factory;

    /** The settings for this captcha engine. */
    private CmsCaptchaSettings m_settings;

    /** The string with default font prefix. */
    private static final String DEFAULT_FONTS_PREFIX = "Arial|Courier|Monospaced|SansSerif|Serif";

    /** The list with default font prefix. */
    private static final List<String> DEFAULT_FONTS_PREFIX_LIST = CmsStringUtil.splitAsList(DEFAULT_FONTS_PREFIX, "|");

    /**
     * Creates a new Captcha engine.
     * <p>
     * 
     * @param captchaSettings the settings to render captcha images
     */
    public CmsCaptchaEngine(CmsCaptchaSettings captchaSettings) {

        super();

        m_settings = captchaSettings;
        initGimpyFactory();
    }

    /**
     * Returns the hardcoded factory (array of length 1) that is used.
     * <p>
     * 
     * @return the hardcoded factory (array of length 1) that is used
     * 
     * @see com.octo.captcha.engine.CaptchaEngine#getFactories()
     */
    @Override
    public CaptchaFactory[] getFactories() {

        return new CaptchaFactory[] {m_factory};
    }

    /** This method build a ImageCaptchaFactory.
     *
     * @return a CaptchaFactory
     */
    @Override
    public com.octo.captcha.image.ImageCaptchaFactory getImageCaptchaFactory() {

        return m_factory;
    }

    /**
     * This does nothing. <p>
     * 
     * A hardcoded factory for deformation is used. 
     * <p>
     * 
     * @see com.octo.captcha.engine.CaptchaEngine#setFactories(com.octo.captcha.CaptchaFactory[])
     */
    @Override
    public void setFactories(CaptchaFactory[] arg0) throws CaptchaEngineException {

        // TODO Auto-generated method stub

    }

    /**
     * Sets the settings.
     * <p>
     * 
     * @param settings the settings to set
     */
    public void setSettings(CmsCaptchaSettings settings) {

        m_settings = settings;
        initGimpyFactory();
    }

    /**
     * Initializes a Gimpy captcha factory.
     * <p>
     */
    protected void initGimpyFactory() {

        WaterFilter water = new WaterFilter();
        water.setAmplitude(m_settings.getFilterAmplitude());
        water.setAntialias(true);
        water.setPhase(0);
        water.setWavelength(m_settings.getFilterWaveLength());

        ImageDeformation backgroundDeformation = new ImageDeformationByFilters(new ImageFilter[] {});
        ImageDeformation textDeformation = new ImageDeformationByFilters(new ImageFilter[] {});
        ImageDeformation postDeformation = new ImageDeformationByFilters(new ImageFilter[] {water});

        WordGenerator dictionary = null;
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(m_settings.getDictionary())) {
            // The argument denotes a java.util.ResourceBundle properties file: toddlist.properties e.g. in root of jcaptcha jar
            dictionary = new DictionaryWordGenerator(new FileDictionary(m_settings.getDictionary()));
        } else {
            dictionary = new RandomWordGenerator(m_settings.getCharacterPool());
        }
        // creates holes into image
        BaffleTextDecorator textDecorator = new BaffleTextDecorator(
            m_settings.getHolesPerGlyph(),
            m_settings.getFontColor());
        ColorGenerator colorGenerator = new SingleColorGenerator(m_settings.getFontColor());

        TextPaster paster = new DecoratedRandomTextPaster(new Integer(m_settings.getMinPhraseLength()), new Integer(
            m_settings.getMaxPhraseLength()), colorGenerator, new TextDecorator[] {textDecorator});

        BackgroundGenerator background;
        if (m_settings.isUseBackgroundImage()) {
            background = new FileReaderRandomBackgroundGenerator(new Integer(m_settings.getImageWidth()), new Integer(
                m_settings.getImageHeight()), OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebApplication(
                "resources/captchabackgrounds/v8/"));

        } else {
            background = new UniColorBackgroundGenerator(new Integer(m_settings.getImageWidth()), new Integer(
                m_settings.getImageHeight()), m_settings.getBackgroundColor());
        }

        // get the list of prefix for default fonts
        List<String> fontPrefix = new ArrayList<String>();
        String param = "";
        CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
        if (module == null) {
            fontPrefix = DEFAULT_FONTS_PREFIX_LIST;
        } else {
            param = module.getParameter(CmsForm.MODULE_PARAM_FONT_PREFIX, DEFAULT_FONTS_PREFIX);
            if (!CmsStringUtil.FALSE.equalsIgnoreCase(param.trim())) {
                fontPrefix = CmsStringUtil.splitAsList(param, "|", true);
            }
        }
        FontGenerator font;
        if (CmsStringUtil.FALSE.equalsIgnoreCase(param)) {

            font = new RandomFontGenerator(new Integer(m_settings.getMinFontSize()), new Integer(
                m_settings.getMaxFontSize()));
        } else {
            Font[] fonts = getFilteredFonts(fontPrefix);
            if (fonts.length > 0) {

                LOG.debug(Messages.get().getBundle().key(Messages.DEBUG_CAPTCHA_USE_FONT_0));

                font = new RandomFontGenerator(new Integer(m_settings.getMinFontSize()), new Integer(
                    m_settings.getMaxFontSize()), fonts);
            } else {
                font = new RandomFontGenerator(new Integer(m_settings.getMinFontSize()), new Integer(
                    m_settings.getMaxFontSize()));
            }
        }

        WordToImage wordToImage = new DeformedComposedWordToImage(
            font,
            background,
            paster,
            backgroundDeformation,
            textDeformation,
            postDeformation);

        m_factory = new GimpyFactory(dictionary, wordToImage);
    }

    /**
     * Filters the fonts available on the system. <p>
     * 
     * Only fonts, which start with one of the provided prefix are returned. 
     * These prefix list ensures, that font do not contain unreadable characters.
     * 
     * @param prefixList the list of prefix to filter the system fonts
     * 
     * @return an array of standard fonts
     */
    private Font[] getFilteredFonts(List<String> prefixList) {

        LOG.debug(Messages.get().getBundle().key(Messages.DEBUG_CAPTCHA_FONT_FILTERING_START_0));
        List<Font> filteredFontsList = new LinkedList<Font>();

        // Get all system fonts
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] systemFonts = e.getAllFonts();

        for (Font f : systemFonts) {
            for (String prefix : prefixList) {
                if (f.getFontName().toLowerCase().startsWith(prefix.toLowerCase())) {
                    filteredFontsList.add(f);
                    LOG.debug(Messages.get().getBundle().key(Messages.DEBUG_CAPTCHA_ADD_FONT_1, f.getFontName()));
                }
            }
        }
        Font[] filteredFonts = new Font[filteredFontsList.size()];
        int i = 0;
        for (Font f : filteredFontsList) {
            filteredFonts[i] = f;
            i++;
        }
        LOG.debug(Messages.get().getBundle().key(
            Messages.DEBUG_CAPTCHA_FONT_FILTERING_FINISH_1,
            Integer.valueOf(filteredFonts.length)));
        return filteredFonts;
    }

}
