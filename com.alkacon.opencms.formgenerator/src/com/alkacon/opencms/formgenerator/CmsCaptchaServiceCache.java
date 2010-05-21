/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsCaptchaServiceCache.java,v $
 * Date   : $Date: 2010/05/21 13:49:16 $
 * Version: $Revision: 1.6 $
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

import org.opencms.file.CmsObject;
import org.opencms.main.CmsEvent;
import org.opencms.main.I_CmsEventListener;
import org.opencms.main.OpenCms;

import java.util.HashMap;
import java.util.Map;

import com.octo.captcha.service.CaptchaService;

/**
 * Caches captcha services, otherwise the mechanism will not work.<p>
 * 
 * @author Thomas Weckert 
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4 
 */
public final class CmsCaptchaServiceCache implements I_CmsEventListener {

    /** The shared instance of the captcha service cache. */
    private static CmsCaptchaServiceCache sharedInstance;

    /** Stores the captcha services. */
    private Map<String, CmsCaptchaService> m_captchaServices;

    /** Stores the maptcha services. */
    private Map<String, CmsMaptchaService> m_maptchaServices;

    /**
     * Default constructor.
     * <p>
     */
    private CmsCaptchaServiceCache() {

        super();

        // add this class as an event handler to the Cms event listener
        OpenCms.addCmsEventListener(this, new int[] {
            I_CmsEventListener.EVENT_CLEAR_CACHES,
            I_CmsEventListener.EVENT_CLEAR_ONLINE_CACHES,
            I_CmsEventListener.EVENT_CLEAR_OFFLINE_CACHES,
            I_CmsEventListener.EVENT_PUBLISH_PROJECT});

        m_captchaServices = new HashMap<String, CmsCaptchaService>();
        m_maptchaServices = new HashMap<String, CmsMaptchaService>();
    }

    /**
     * Returns the shared instance of the captcha service cache.
     * <p>
     * 
     * @return the shared instance of the captcha service cache
     */
    public static synchronized CmsCaptchaServiceCache getSharedInstance() {

        if (sharedInstance == null) {
            sharedInstance = new CmsCaptchaServiceCache();
        }

        return sharedInstance;
    }

    /**
     * @see org.opencms.main.I_CmsEventListener#cmsEvent(org.opencms.main.CmsEvent)
     */
    public void cmsEvent(CmsEvent event) {

        switch (event.getType()) {
            case I_CmsEventListener.EVENT_CLEAR_CACHES:
            case I_CmsEventListener.EVENT_CLEAR_ONLINE_CACHES:
            case I_CmsEventListener.EVENT_CLEAR_OFFLINE_CACHES:
            case I_CmsEventListener.EVENT_PUBLISH_PROJECT:
                clearCaptchaServices();
                break;

            default:
                // noop
                break;
        }
    }

    /**
     * Returns the captcha service specified by the settings.
     * <p>
     * 
     * @param captchaSettings the settings to render captcha images.
     * 
     * @param cms needed for context information when getting the key for caching.
     * 
     * @return the captcha service.
     */
    public synchronized CaptchaService getCaptchaService(CmsCaptchaSettings captchaSettings, CmsObject cms) {

        if (m_captchaServices == null) {
            m_captchaServices = new HashMap<String, CmsCaptchaService>();
        }
        if (m_maptchaServices == null) {
            m_maptchaServices = new HashMap<String, CmsMaptchaService>();
        }

        String key = null;
        if (captchaSettings.getPresetPath() != null) {
            key = captchaSettings.getPresetPath();
        } else {
            key = captchaSettings.toRequestParams(cms);
        }
        if (captchaSettings.isMathField()) {
            CmsMaptchaService maptchaService = m_maptchaServices.get(key);
            if (maptchaService == null) {
                maptchaService = new CmsMaptchaService(captchaSettings);
                m_maptchaServices.put(key, maptchaService);
            }
            return maptchaService;
        } else {
            CmsCaptchaService captchaService = m_captchaServices.get(key);
            if (captchaService == null) {
                captchaService = new CmsCaptchaService(captchaSettings);
                m_captchaServices.put(key, captchaService);
            } else {
                // install the parameters to the internal engine
                captchaService.setSettings(captchaSettings);
            }

            return captchaService;
        }
    }

    /**
     * Clears the map storing the captcha services.
     * <p>
     */
    private synchronized void clearCaptchaServices() {

        if (m_captchaServices != null) {
            m_captchaServices.clear();
        }

        if (m_maptchaServices != null) {
            m_maptchaServices.clear();
        }
    }
}