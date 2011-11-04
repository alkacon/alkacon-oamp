/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsMaptchaEngine.java,v $
 * Date   : $Date: 2010/05/21 13:49:15 $
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

package com.alkacon.opencms.v8.formgenerator;

import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.engine.GenericCaptchaEngine;

/**
 * A captcha engine using a Maptcha factory to create mathematical captchas.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsMaptchaEngine extends GenericCaptchaEngine {

    /** The configured mathematical captcha factory. */
    private CmsMaptchaFactory m_factory;

    /**
     * Creates a new Maptcha engine.<p>
     * 
     * @param captchaSettings the settings to create mathematical captchas
     */
    public CmsMaptchaEngine(CmsCaptchaSettings captchaSettings) {

        super(new CaptchaFactory[] {new CmsMaptchaFactory()});
        initMathFactory();
    }

    /** This method build a Maptcha Factory.<p>
     *
     * @return a Maptcha Factory
     */
    public CmsMaptchaFactory getFactory() {

        return m_factory;
    }

    /**
     * Initializes a Maptcha Factory.<p>
     */
    protected void initMathFactory() {

        m_factory = new CmsMaptchaFactory();
    }

    /**
     * Returns the hardcoded factory (array of length 1) that is used.<p>
     * 
     * @return the hardcoded factory (array of length 1) that is used
     * 
     * @see com.octo.captcha.engine.CaptchaEngine#getFactories()
     */
    @Override
    public CaptchaFactory[] getFactories() {

        return new CaptchaFactory[] {m_factory};
    }

    /**
     * This does nothing.<p>
     * 
     * A hardcoded factory is used.<p>
     * 
     * @see com.octo.captcha.engine.CaptchaEngine#setFactories(com.octo.captcha.CaptchaFactory[])
     */
    @Override
    public void setFactories(CaptchaFactory[] arg0) throws CaptchaEngineException {

        // TODO Auto-generated method stub

    }

}
