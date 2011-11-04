/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsMaptcha.java,v $
 * Date   : $Date: 2010/03/19 15:31:10 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2010 Alkacon Software (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.formgenerator;

import com.octo.captcha.text.TextCaptcha;

/**
 * Extends the text captcha for generating math challenges.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsMaptcha extends TextCaptcha {

    /** Serial version ID. */
    private static final long serialVersionUID = 3866109424461928101L;

    /** The response String. */
    private String m_response;

    /**
     * Constructor with parameters.<p>
     * 
     * @param questionMath the question
     * @param challengeMath the challenge
     * @param response the response
     */
    CmsMaptcha(String questionMath, String challengeMath, String response) {

        super(questionMath, challengeMath);
        this.m_response = response;
    }

    /**
     * Validation routine from the CAPTCHA interface. this methods verify if the response is not null and a String and
     * then compares the given response to the internal string.<p>
     *
     * @param response the response
     * @return true if the given response equals the internal response, false otherwise
     */
    public final Boolean validateResponse(final Object response) {

        return ((null != response) && (response instanceof String))
        ? validateResponse((String)response)
        : Boolean.FALSE;
    }

    /**
     * Very simple validation routine that compares the given response to the internal string.<p>
     *
     * @param response the response
     * @return true if the given response equals the internal response, false otherwise
     */
    private Boolean validateResponse(final String response) {

        return Boolean.valueOf(response.equals(this.m_response));
    }
}
