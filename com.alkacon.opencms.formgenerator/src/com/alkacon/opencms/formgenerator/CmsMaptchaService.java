/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsMaptchaService.java,v $
 * Date   : $Date: 2009/04/17 15:31:54 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
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

import com.octo.captcha.service.captchastore.MapCaptchaStore;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

/**
 * Provides the facility to create and cache the maptchas.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsMaptchaService extends GenericManageableCaptchaService {

    /**
     * Creates a new maptcha service.<p>
     * 
     * The following settings are used: 
     * <pre>
     * minGuarantedStorageDelayInSeconds = 180s 
     * maxCaptchaStoreSize = 100000
     * captchaStoreLoadBeforeGarbageCollection = 75000
     * </pre>
     * <p>
     * 
     * @param captchaSettings the settings to display maptcha challenges
     */
    public CmsMaptchaService(CmsCaptchaSettings captchaSettings) {

        super(new MapCaptchaStore(), new CmsMaptchaEngine(captchaSettings), 180, 100000, 75000);
    }

}
