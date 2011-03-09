/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsCaptchaField.java,v $
 * Date   : $Date: 2011/03/09 15:14:35 $
 * Version: $Revision: 1.12 $
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

package com.alkacon.opencms.formgenerator;

import org.opencms.flex.CmsFlexController;
import org.opencms.i18n.CmsMessages;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.text.TextCaptchaService;

/**
 * Creates captcha images and validates the pharses submitted by a request parameter.
 * <p>
 * 
 * @author Thomas Weckert
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.12 $
 * 
 * @since 7.0.4 
 */
public class CmsCaptchaField extends A_CmsField {

    /** Request parameter name of the captcha phrase. */
    public static final String C_PARAM_CAPTCHA_PHRASE = "captchaphrase";

    /** Session parameter name to store the webform captcha settings. */
    protected static final String SESSION_PARAM_CAPTCHASETTINGS = "__oamp_webform_captchasettings";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCaptchaField.class);

    /** HTML field type: captcha image. */
    private static final String TYPE = "captcha";

    /** The settings to render captcha images. */
    private CmsCaptchaSettings m_captchaSettings;

    /**
     * Creates a new captcha field.
     * <p>
     * 
     * @param captchaSettings the settings to render captcha images
     * @param fieldLabel the localized label of this field
     * @param fieldValue the submitted value of this field
     */
    public CmsCaptchaField(CmsCaptchaSettings captchaSettings, String fieldLabel, String fieldValue) {

        super();

        m_captchaSettings = captchaSettings;

        setName(C_PARAM_CAPTCHA_PHRASE);
        setValue(fieldValue);
        setLabel(fieldLabel);
        setMandatory(true);
    }

    /**
     * Returns the type of the input field, e.g. "text" or "select".
     * <p>
     * 
     * @return the type of the input field
     */
    public static String getStaticType() {

        return TYPE;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#buildHtml(CmsFormHandler, CmsMessages, String, boolean, String)
     */
    @Override
    public String buildHtml(
        CmsFormHandler formHandler,
        CmsMessages messages,
        String errorKey,
        boolean showMandatory,
        String infoKey) {

        StringBuffer captchaHtml = new StringBuffer(256);
        String errorMessage = createStandardErrorMessage(errorKey, messages);

        CmsCaptchaSettings captchaSettings = getCaptchaSettings();

        if (m_captchaSettings.isMathField()) {
            // this is a math captcha, print the challenge directly
            String sessionId = formHandler.getRequest().getSession(true).getId();
            TextCaptchaService service = (TextCaptchaService)CmsCaptchaServiceCache.getSharedInstance().getCaptchaService(
                m_captchaSettings,
                formHandler.getCmsObject());
            captchaHtml.append("<div style=\"margin: 0 0 2px 0;\">");
            captchaHtml.append(service.getTextChallengeForID(
                sessionId,
                formHandler.getCmsObject().getRequestContext().getLocale()));
            captchaHtml.append("</div>\n");
        } else {
            // image captcha, insert image
            captchaHtml.append("<img id=\"form_captcha_id\" src=\"").append(
                formHandler.link("/system/modules/com.alkacon.opencms.formgenerator/pages/captcha.jsp?"
                    + captchaSettings.toRequestParams(formHandler.getCmsObject())
                    + "#"
                    + System.currentTimeMillis())).append("\" width=\"").append(captchaSettings.getImageWidth()).append(
                "\" height=\"").append(captchaSettings.getImageHeight()).append("\" alt=\"\"/>").append("\n");
            captchaHtml.append("<br/>\n");
        }

        Map<String, Object> stAttributes = new HashMap<String, Object>();
        // set captcha HTML code as additional attribute
        stAttributes.put("captcha", captchaHtml.toString());

        return createHtml(formHandler, messages, stAttributes, getType(), null, errorMessage, showMandatory);
    }

    /**
     * Returns the captcha settings of this field.
     * <p>
     * 
     * @return the captcha settings of this field
     */
    public CmsCaptchaSettings getCaptchaSettings() {

        return m_captchaSettings;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.I_CmsField#getType()
     */
    public String getType() {

        return TYPE;
    }

    /**
     * Validates the captcha phrase entered by the user.
     * <p>
     * 
     * @param jsp the Cms JSP
     * @param captchaPhrase the captcha phrase to be validate
     * @return true, if the captcha phrase entered by the user is correct, false otherwise
     */
    public boolean validateCaptchaPhrase(CmsJspActionElement jsp, String captchaPhrase) {

        boolean result = false;
        CmsCaptchaSettings settings = m_captchaSettings;
        // check if there are changed captcha settings stored in the session (true if first image generation failed)
        CmsCaptchaSettings sessionSettings = (CmsCaptchaSettings)jsp.getRequest().getSession().getAttribute(
            SESSION_PARAM_CAPTCHASETTINGS);
        if (sessionSettings != null) {
            // use captcha settings from session to validate the response
            settings = sessionSettings;
            jsp.getRequest().getSession().removeAttribute(SESSION_PARAM_CAPTCHASETTINGS);
        }
        String sessionId = jsp.getRequest().getSession().getId();

        if (CmsStringUtil.isNotEmpty(captchaPhrase)) {
            // try to validate the phrase
            try {
                CaptchaService captchaService = CmsCaptchaServiceCache.getSharedInstance().getCaptchaService(
                    settings,
                    jsp.getCmsObject());
                if (captchaService != null) {
                    result = captchaService.validateResponseForID(sessionId, captchaPhrase).booleanValue();
                }
            } catch (CaptchaServiceException cse) {
                // most often this will be
                // "com.octo.captcha.service.CaptchaServiceException: Invalid ID, could not validate unexisting or already validated captcha"
                // in case someone hits the back button and submits again
            }
        }

        return result;
    }

    /**
     * Writes a Captcha JPEG image to the servlet response output stream.
     * <p>
     * 
     * @param cms an initialized Cms JSP action element
     * @throws IOException if something goes wrong
     */
    public void writeCaptchaImage(CmsJspActionElement cms) throws IOException {

        // remove eventual session attribute containing captcha settings
        cms.getRequest().getSession().removeAttribute(SESSION_PARAM_CAPTCHASETTINGS);
        String sessionId = cms.getRequest().getSession().getId();
        Locale locale = cms.getRequestContext().getLocale();
        BufferedImage captchaImage = null;
        int maxTries = 10;
        do {
            try {
                maxTries--;
                captchaImage = ((ImageCaptchaService)CmsCaptchaServiceCache.getSharedInstance().getCaptchaService(
                    m_captchaSettings,
                    cms.getCmsObject())).getImageChallengeForID(sessionId, locale);
            } catch (CaptchaException cex) {
                // image size is too small, increase dimensions and try it again
                if (LOG.isInfoEnabled()) {
                    LOG.info(cex);
                    LOG.info(Messages.get().getBundle().key(
                        Messages.LOG_ERR_CAPTCHA_CONFIG_IMAGE_SIZE_2,
                        new Object[] {m_captchaSettings.getPresetPath(), new Integer(maxTries)}));
                }
                m_captchaSettings.setImageHeight((int)(m_captchaSettings.getImageHeight() * 1.1));
                m_captchaSettings.setImageWidth((int)(m_captchaSettings.getImageWidth() * 1.1));
                // IMPORTANT: store changed captcha settings in session, they have to be used when validating the phrase
                cms.getRequest().getSession().setAttribute(SESSION_PARAM_CAPTCHASETTINGS, m_captchaSettings.clone());
            }
        } while ((captchaImage == null) && (maxTries > 0));

        ServletOutputStream out = null;
        try {
            CmsFlexController controller = CmsFlexController.getController(cms.getRequest());
            HttpServletResponse response = controller.getTopResponse();
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");

            ByteArrayOutputStream captchaImageOutput = new ByteArrayOutputStream();
            ImageIO.write(captchaImage, "jpg", captchaImageOutput);
            out = cms.getResponse().getOutputStream();
            out.write(captchaImageOutput.toByteArray());
            out.flush();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            cms.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Throwable t) {
                // intentionally left blank
            }
        }
    }

}
