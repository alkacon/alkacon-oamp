/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.commons/src/com/alkacon/opencms/commons/CmsStringCrypter.java,v $
 * Date   : $Date: 2007/11/19 15:49:15 $
 * Version: $Revision: 1.2 $
 *
 * This file is part of the Alkacon OpenCms Add-On Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.commons;

import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This class is responsible for encrypting and decrypting Strings.<p>
 * 
 * The result can be be used as URL parameters.<p>
 * 
 * @author Peter Bonrad
 * @author Andreas Zahner
 */
public final class CmsStringCrypter {

    /** The encryption to be used. */
    private static final String ENCRYPTION = "DES";

    /** The format of the key and the values to be crypted. */
    private static final String FORMAT = "UTF8";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsStringCrypter.class);

    /** The default password to be used. */
    private static final String PASSWORD_DEFAULT = "fuZe-6jK";

    /**
     * Hides the public constructor for this utility class.<p>
     */
    private CmsStringCrypter() {

        // hide constructor
    }
    
    /**
     * Decrypts the given value which was encrypted with the encrypt method with a default password.<p>
     * 
     * @param value the value to be decrypted
     * @return the decrypted string of the value or null if something went wrong
     */
    public static String decrypt(String value) {
        
        return decrypt(value, PASSWORD_DEFAULT);
    }

    /**
     * Decrypts the given value which was encrypted with the encrypt method.<p>
     * 
     * @param value the value to be decrypted
     * @param password the passsword used for decryption, has to be the same as used for encryption
     * @return the decrypted string of the value or null if something went wrong
     */
    public static String decrypt(String value, String password) {

        // check if given value is valid
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_INVALID_DECRYPT_STRING_1, value));
            }
            return null;
        }

        try {

            // create key
            Key key = new SecretKeySpec(getKey(password), ENCRYPTION);
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, key);

            // decode from base64
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] cleartext = base64decoder.decodeBuffer(value);

            // decrypt text
            byte[] ciphertext = cipher.doFinal(cleartext);
            return CmsEncoder.decode(new String(ciphertext));
        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_DECRPYT_0), ex);
            }
        }

        return null;
    }
    
    /**
     * Encrypts the given value with a default password.<p>
     * 
     * @param value the string which should be encrypted
     * @return the encrypted string of the value or null if something went wrong
     */
    public static String encrypt(String value) {
        
        return encrypt(value, PASSWORD_DEFAULT);
    }

    /**
     * Encrypts the given value.<p>
     * 
     * @param value the string which should be encrypted
     * @param password the passsword used for encryption, use the same password for decryption
     * @return the encrypted string of the value or null if something went wrong
     */
    public static String encrypt(String value, String password) {

        // check if given value is valid
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(value)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_INVALID_ENCRYPT_STRING_1, value));
            }
            return null;
        }

        try {

            // create key
            byte[] k = getKey(password);
            Key key = new SecretKeySpec(k, ENCRYPTION);
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // encrypt text
            byte[] cleartext = value.getBytes(FORMAT);
            byte[] ciphertext = cipher.doFinal(cleartext);

            // encode with base64 to be used as a url parameter
            BASE64Encoder base64encoder = new BASE64Encoder();
            return CmsEncoder.encode(base64encoder.encode(ciphertext));
        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_ENCRYPT_0), ex);
            }
        }

        return null;
    }

    /**
     * Converts the given password to machine readable form.<p>
     * 
     * @param password the password to convert to a machine readable key
     * @return the password in machine readable form
     */
    private static byte[] getKey(String password) {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.toString().getBytes());
            byte[] key = md5.digest();
            // now get the first 8 bytes
            byte[] finalKey = new byte[8];
            for (int i = 0; i <= 7; i++) {
                finalKey[i] = key[i];
            }
            return finalKey;
        } catch (NoSuchAlgorithmException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_CREATE_KEY_0), ex);
            }
        }
        return null;
    }

}
