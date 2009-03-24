/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationJsErrorReporter.java,v $
 * Date   : $Date: 2009/03/24 12:52:42 $
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

package com.alkacon.opencms.weboptimization;

import org.opencms.main.CmsLog;

import org.apache.commons.logging.Log;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * Log4j based implementation.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationJsErrorReporter implements ErrorReporter {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsOptimizationJsErrorReporter.class);

    /**
     * @see org.mozilla.javascript.ErrorReporter#error(java.lang.String, java.lang.String, int, java.lang.String, int)
     */
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {

        LOG.error(getMessage(sourceName, message, line, lineOffset));
    }

    /**
     * @see org.mozilla.javascript.ErrorReporter#runtimeError(java.lang.String, java.lang.String, int, java.lang.String, int)
     */
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {

        LOG.error(getMessage(sourceName, message, line, lineOffset));
        return new EvaluatorException(message);
    }

    /**
     * @see org.mozilla.javascript.ErrorReporter#warning(java.lang.String, java.lang.String, int, java.lang.String, int)
     */
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {

        LOG.warn(getMessage(sourceName, message, line, lineOffset));
    }

    /**
     * Creates an error message for the given parameters.<p> 
     * 
     * @param source the source file
     * @param message the original error message
     * @param line the line number
     * @param lineOffset the column number
     * 
     * @return an error message
     */
    private String getMessage(String source, String message, int line, int lineOffset) {

        String logMessage;
        if (line < 0) {
            logMessage = (source != null) ? source + ":" : "" + message;
        } else {
            logMessage = (source != null) ? source + ":" : "" + line + ":" + lineOffset + ":" + message;
        }
        return logMessage;
    }
}
