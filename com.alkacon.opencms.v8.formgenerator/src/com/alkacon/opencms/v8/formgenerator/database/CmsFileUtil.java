/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/database/CmsFileUtil.java,v $
 * Date   : $Date: 2010/03/19 15:31:14 $
 * Version: $Revision: 1.2 $
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

package com.alkacon.opencms.v8.formgenerator.database;

import org.opencms.util.CmsRfsException;

import java.io.File;
import java.io.IOException;

/**
 * <code>{@link java.io.File}</code> handling utilities with extended verification 
 * for files and folders in the RFS.
 * <p>
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4
 * 
 */
public final class CmsFileUtil {

    /** Constant for read access to files. */
    public static final int MODE_READ = 1;

    /** Constant for write access to files. */
    public static final int MODE_WRITE = 3;

    /**
     * Utility class constructor: No instances is needed.
     * <p>
     * 
     */
    private CmsFileUtil() {

        super();
    }

    /**
     * Asserts that the given file is not null, points to a valid file (no folder) and is accessible
     * in the given mode.
     * <p>
     * 
     * @param file the file to assert access to.
     * 
     * @param mode {@link #MODE_READ}, {@link #MODE_WRITE} or
     *            <code>{@link #MODE_READ} | {@link #MODE_WRITE}</code> (binary OR).
     * 
     * @param create if true this call will create the file if non-existant.
     * 
     * @throws CmsRfsException if the file does not exist, cannot be read, is no file (but a
     *             folder), or cannot be accessed in the given mode.
     */
    public static void assertFile(File file, int mode, boolean create) throws CmsRfsException {

        if (file == null) {
            throw new CmsRfsException(Messages.get().container(Messages.ERR_FILE_ARG_NULL_0));
        }
        if (!file.exists()) {
            if (create) {
                try {
                    file.createNewFile();
                } catch (IOException ioex) {
                    throw new CmsRfsException(Messages.get().container(
                        Messages.ERR_FILE_ARG_CREATE_1,
                        file.getAbsolutePath()), ioex);
                }
            } else {
                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_EXISTS_1,
                    file.getAbsolutePath()));
            }

        }
        if (file.isDirectory()) {
            throw new CmsRfsException(Messages.get().container(
                Messages.ERR_FILE_ARG_IS_FOLDER_1,
                new Object[] {file.getAbsolutePath()}));
        } else if (!file.isFile()) {
            throw new CmsRfsException(Messages.get().container(
                Messages.ERR_FILE_ARG_NOT_FOUND_1,
                new Object[] {file.getAbsolutePath()}));

        }

        if ((mode & MODE_READ) != 0) {
            if (!file.canRead()) {
                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_NOT_READ_1,
                    new Object[] {String.valueOf(file.getAbsolutePath())}));

            }
        }
        if ((mode & MODE_WRITE) != 0) {
            if (!file.canWrite()) {

                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_NOT_WRITE_1,
                    new Object[] {String.valueOf(file.getAbsolutePath())}));
            }
        }
    }

    /**
     * Asserts that the given file is not null, points to a valid folder (no file) and is accessible
     * in the given mode.
     * <p>
     * 
     * @param file the file to assert access to.
     * 
     * @param create if true this call will create the file if non-existant.
     * 
     * @param mode {@link #MODE_READ}, {@link #MODE_WRITE} or
     *            <code>{@link #MODE_READ} | {@link #MODE_WRITE}</code> (binary OR).
     * 
     * @throws CmsRfsException if the file does not exist, cannot be read, is no file (but a
     *             folder), or cannot be accessed in the given mode.
     */
    public static void assertFolder(File file, int mode, boolean create) throws CmsRfsException {

        if (file == null) {
            throw new CmsRfsException(Messages.get().container(Messages.ERR_FILE_ARG_NULL_0));
        }
        if (!file.exists()) {
            if (create) {
                try {
                    if (!file.mkdirs()) {
                        throw new CmsRfsException(Messages.get().container(
                            Messages.ERR_FILE_ARG_CREATE_1,
                            file.getAbsolutePath()));
                    }
                } catch (SecurityException secex) {
                    throw new CmsRfsException(Messages.get().container(
                        Messages.ERR_FILE_ARG_CREATE_1,
                        file.getAbsolutePath()), secex);
                }
            } else {
                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_EXISTS_1,
                    file.getAbsolutePath()));
            }

        }
        if (file.isFile()) {
            throw new CmsRfsException(Messages.get().container(
                Messages.ERR_FILE_ARG_IS_FILE_1,
                new Object[] {file.getAbsolutePath()}));
        }
        if ((mode & MODE_READ) != 0) {
            if (!file.canRead()) {
                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_NOT_READ_1,
                    new Object[] {String.valueOf(file.getAbsolutePath())}));

            }
        }
        if ((mode & MODE_WRITE) != 0) {
            if (!file.canWrite()) {

                throw new CmsRfsException(Messages.get().container(
                    Messages.ERR_FILE_ARG_NOT_WRITE_1,
                    new Object[] {String.valueOf(file.getAbsolutePath())}));
            }
        }
    }
}
