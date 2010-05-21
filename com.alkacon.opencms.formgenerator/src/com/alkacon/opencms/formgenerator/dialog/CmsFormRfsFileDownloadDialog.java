/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/dialog/CmsFormRfsFileDownloadDialog.java,v $
 * Date   : $Date: 2010/05/21 13:49:30 $
 * Version: $Revision: 1.1 $
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

package com.alkacon.opencms.formgenerator.dialog;

import org.opencms.flex.CmsFlexController;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.workplace.CmsDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Generates a download for an RSF file uploaded by the web form.<p>
 * 
 * @author Andreas Zahner  
 */
public class CmsFormRfsFileDownloadDialog extends CmsDialog {

    /** Defines which pages are valid for this dialog. */
    public static final String[] PAGES = {"page1"};

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsFormRfsFileDownloadDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsFormRfsFileDownloadDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * Generates the output of the file.<p>
     * 
     * @throws IOException if something goes wrong 
     */
    public void generateOutput() throws IOException {

        HttpServletResponse res = CmsFlexController.getController(getJsp().getRequest()).getTopResponse();
        File downloadFile = getDownloadFile(getParamResource());
        String mimeType = OpenCms.getResourceManager().getMimeType(
            downloadFile.getName(),
            null,
            "application/octet-stream");
        res.setContentType(mimeType);
        res.setHeader(
            "Content-Disposition",
            new StringBuffer("attachment; filename=\"").append(downloadFile.getName()).append("\"").toString());
        res.setContentLength((int)downloadFile.length());

        // important: getOutputStream() throws IllegalStateException if the JSP directive buffer="none" is set. 
        ServletOutputStream outStream = res.getOutputStream();
        InputStream in = new BufferedInputStream(new FileInputStream(downloadFile));

        try {
            // don't write the last '-1'
            int bit = in.read();
            while ((bit) >= 0) {
                outStream.write(bit);
                bit = in.read();
            }
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (SocketException soe) {
                    // ignore
                }
            }
            in.close();
        }
    }

    /**
     * Returns the file that will be downloaded.<p>
     * 
     * @param filePath the RFS file path to download
     * 
     * @return the file that will be downloaded
     *         
     * @throws CmsRuntimeException if access to the chosen file to download fails
     */
    protected File getDownloadFile(String filePath) throws CmsRuntimeException {

        File downloadFile = new File(filePath);
        try {
            // check: it is impossible to set an invalid path to that class
            downloadFile = downloadFile.getCanonicalFile();
        } catch (IOException e) {
            throw new CmsRuntimeException(Messages.get().container(Messages.ERR_FILE_ACCESS_0), e);
        }
        return downloadFile;
    }
}
