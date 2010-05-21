/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsMaptchaFactory.java,v $
 * Date   : $Date: 2010/05/21 13:49:14 $
 * Version: $Revision: 1.3 $
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

package com.alkacon.opencms.formgenerator;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import com.octo.captcha.text.TextCaptcha;
import com.octo.captcha.text.TextCaptchaFactory;

/**
 * A factory for mathematical operations to display as maptcha on the form.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsMaptchaFactory extends TextCaptchaFactory {

    /** The used random int generator. */
    private Random m_ramdom = new SecureRandom();

    /**
     * Default constructor.<p>
     */
    public CmsMaptchaFactory() {

        // nothing to do
    }

    /***
     * Returns the text captcha.<p>
     * 
     * @return the text captcha
     */
    @Override
    public TextCaptcha getTextCaptcha() {

        return getTextCaptcha(Locale.getDefault());
    }

    /***
     * Returns the text captcha for the current Locale.<p>
     *
     * @param locale the current Locale
     * @return a localized text captcha
     */
    @Override
    public TextCaptcha getTextCaptcha(Locale locale) {

        // build the challenge: get 2 random int values
        int one = 0;
        int two = 0;
        while (one == 0) {
            one = m_ramdom.nextInt(11);
        }
        while (two == 0) {
            two = m_ramdom.nextInt(11);
        }

        String operator = "+";
        String result;
        // choose randomly the operation (plus, minus, times)
        int opInt = m_ramdom.nextInt(3);
        if (opInt == 1) {
            operator = "-";
            result = String.valueOf(one - two);
        } else if (opInt == 2) {
            operator = "*";
            result = String.valueOf(one * two);
        } else {
            result = String.valueOf(one + two);
        }

        TextCaptcha captcha = new CmsMaptcha(getQuestion(locale), one + " " + operator + " " + two, result);

        return captcha;
    }

    /**
     * Returns the localized question for the text captcha.<p>
     * 
     * @param locale the current Locale
     * @return the localized question for the text captcha
     */
    protected String getQuestion(Locale locale) {

        return "";
    }
}
