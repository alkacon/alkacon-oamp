/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.excelimport/src/com/alkacon/opencms/excelimport/CmsResourceExcelImport.java,v $
 * Date   : $Date: 2009/04/30 10:52:08 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2008 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.excelimport;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.workplace.CmsWorkplaceSettings;
import org.opencms.workplace.explorer.CmsNewResource;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;

/**
 * The excel import dialog handles the excel import.<p>
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.5.0
 */
public class CmsResourceExcelImport extends CmsNewResource {

    /** The value for the resource name form submission action. */
    public static final int ACTION_SUBMITFORM2 = 130;

    /** The name for the resource form submission action. */
    public static final String DIALOG_SUBMITFORM2 = "submitform2";

    /** Request parameter excel file. */
    public static final String PARAM_EXCEL_FILE = "excelfile";

    /** Request parameter name for the publish flag. */
    public static final String PARAM_PUBLISH = "publish";

    /** Request parameter name for the upload folder name. */
    public static final String PARAM_UPLOADFOLDER = "uploadfolder";

    /** The current selected folder in the workplace. */
    private String m_currentFolder;

    /** The excel content. */
    private byte[] m_excelContent;

    /** The excel name. */
    private String m_excelName;

    /** The publish flag. */
    private String m_paramPublish;

    /**
     * Public constructor with JSP action element.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsResourceExcelImport(CmsJspActionElement jsp) {

        super(jsp);
        m_currentFolder = getSettings().getExplorerResource();
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsResourceExcelImport(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * Performs the publish report, will be called by the JSP page.<p>
     * 
     * @throws JspException if problems including sub-elements occur
     */
    public void actionReport() throws JspException {

        // save initialized instance of this class in request attribute for included sub-elements
        getJsp().getRequest().setAttribute(SESSION_WORKPLACE_CLASS, new CmsExcelImportReport(getJsp()));
        switch (getAction()) {
            case ACTION_REPORT_END:
                actionCloseDialog();
                break;
            case ACTION_REPORT_UPDATE:
                setParamAction(REPORT_UPDATE);
                try {
                    getJsp().include(FILE_REPORT_OUTPUT);
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case ACTION_REPORT_BEGIN:
            case ACTION_CONFIRMED:
            default:
                setParamAction(REPORT_BEGIN);
                getJsp().include(FILE_REPORT_OUTPUT);
        }
    }

    /**
     * Uploads the specified file and unzips it, if selected.<p>
     * 
     * @throws JspException if inclusion of error dialog fails
     */
    public void actionUpload() throws JspException {

        CmsExcelImportListExportReport cmsExcelImportListExportReport = new CmsExcelImportListExportReport(
            getJsp(),
            this);
        cmsExcelImportListExportReport.setParamTitle(getParamTitle());
        cmsExcelImportListExportReport.displayReport();

    }

    /**
     * Gets dialog text.<p>
     * 
     * @return dialog text
     */
    public String getDialogText() {

        CmsExcelImport cmsExcelImport = new CmsExcelImport(getCms(), this);
        String text = cmsExcelImport.getDialogText();
        return text;
    }

    /**
     * Gets excel file name.<p>
     * 
     * @return excel file name
     */
    public String getExcelName() {

        return m_excelName;
    }

    /**
     * Gets current selected folder in the workplace.<p>
     * 
     * @return current selected folder in the workplace
     */
    public String getFolder() {

        return m_currentFolder;
    }

    /**
     * Gets content from uploaded file.<p>
     * 
     * @return excel content
     */
    public byte[] getParamExcelContent() {

        return m_excelContent;
    }

    /**
     * Gets parameter value "publish" as string.<p>
     * 
     * @return parameter value "publish" as string
     */
    public String getParamPublish() {

        return m_paramPublish;
    }

    /**
     * Gets parameter value "publish" as boolean.<p>
     *     
     * @return parameter value "publish" as boolean
     */
    public boolean getPublish() {

        boolean publish = false;
        if (getParamPublish() != null) {
            publish = new Boolean(getParamPublish()).booleanValue();
        }
        return publish;
    }

    /**
     * Returns true if import can become started, otherwise false.<p>
     * 
     * @return true if import can become started, otherwise false
     */
    public boolean isValid() {

        CmsExcelImport cmsExcelImport = new CmsExcelImport(getCms(), this);
        boolean valid = cmsExcelImport.isValid();
        return valid;
    }

    /**
     * Read parameters into session.<p>
     */
    public void readParasFromSession() {

        // excel name
        if (getJsp().getRequest().getSession().getAttribute("excelName") != null) {
            m_excelName = (String)getJsp().getRequest().getSession().getAttribute("excelName");
        }
        // excel content
        if (getJsp().getRequest().getSession().getAttribute("excelContent") != null) {
            m_excelContent = (byte[])getJsp().getRequest().getSession().getAttribute("excelContent");
        }
        // publish
        if (getJsp().getRequest().getSession().getAttribute("publish") != null) {
            m_paramPublish = (String)getJsp().getRequest().getSession().getAttribute("publish");
        }
    }

    /**
     * Sets content from uploaded file.<p>
     * 
     * @param content the parameter value is only necessary for calling this method
     */
    public void setParamExcelContent(String content) {

        // use parameter value
        if (content == null) {
            // do nothing   
        }

        // get the file item from the multipart request
        if (getMultiPartFileItems() != null) {
            Iterator i = getMultiPartFileItems().iterator();
            FileItem fi = null;
            while (i.hasNext()) {
                fi = (FileItem)i.next();
                if (fi.getFieldName().equals(PARAM_EXCEL_FILE)) {
                    // found the file objectif (fi != null) {
                    long size = fi.getSize();
                    long maxFileSizeBytes = OpenCms.getWorkplaceManager().getFileBytesMaxUploadSize(getCms());
                    // check file size
                    if ((maxFileSizeBytes > 0) && (size > maxFileSizeBytes)) {
                        // file size is larger than maximum allowed file size, throw an error
                        //throw new CmsWorkplaceException();
                    }
                    m_excelContent = fi.get();
                    m_excelName = fi.getName();
                    fi.delete();
                } else {
                    continue;
                }
            }
        }
    }

    /**
     * Sets parameter value "publish".<p>
     * 
     * @param publish parameter "publish" value
     */
    public void setParamPublish(String publish) {

        if (publish != null) {
            m_paramPublish = publish;
        }
    }

    /**
     * Returns if security dialog should appear.<p>
     * 
     * @return true, if security dialog should appear, otherwise false
     */
    public boolean useConfirmationDialog() {

        boolean security = true;
        // get the module
        CmsModule module = OpenCms.getModuleManager().getModule(CmsExcelImport.MODULE_NAME);
        if (module != null) {
            String securityString = module.getParameter("securitydialog");
            security = new Boolean(securityString).booleanValue();
        }
        return security;
    }

    /**
     * Writes parameters into session.<p>
     */
    public void writeParasToSession() {

        // excel name
        getJsp().getRequest().getSession().removeAttribute("excelName");
        getJsp().getRequest().getSession().setAttribute("excelName", m_excelName);

        // excel content 
        getJsp().getRequest().getSession().removeAttribute("excelContent");
        getJsp().getRequest().getSession().setAttribute("excelContent", m_excelContent);

        // publish
        getJsp().getRequest().getSession().removeAttribute("publish");
        getJsp().getRequest().getSession().setAttribute("publish", m_paramPublish);
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceMembers(org.opencms.jsp.CmsJspActionElement)
     */
    protected void initWorkplaceMembers(CmsJspActionElement jsp) {

        String siteRoot = jsp.getRequestContext().getSiteRoot();
        // In case of the upload applet the site stored in the user preferences must NOT be made the current 
        // site even if we have a new session! Since the upload applet will create a new session for the upload itself, 
        // we must make sure to use the site of the request, NOT the site stored in the user preferences.
        // The default logic will erase the request site in case of a new session.
        // With this workaround the site from the request is made the current site as required.
        super.initWorkplaceMembers(jsp);
        if (!siteRoot.equals(getSettings().getSite())) {
            getSettings().setSite(siteRoot);
            jsp.getRequestContext().setSiteRoot(siteRoot);
        }
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings, javax.servlet.http.HttpServletRequest)
     */
    protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

        // fill the parameter values in the get/set methods
        fillParamValues(request);
        // set the dialog type
        setParamDialogtype(DIALOG_TYPE);
        // set the action for the JSP switch 
        if (DIALOG_OK.equals(getParamAction())) {
            setAction(ACTION_OK);
        } else if (DIALOG_SUBMITFORM2.equals(getParamAction())) {
            setAction(ACTION_SUBMITFORM2);
        } else if (DIALOG_CANCEL.equals(getParamAction())) {
            setAction(ACTION_CANCEL);
        } else if (DIALOG_CONFIRMED.equals(getParamAction())) {
            setAction(ACTION_CONFIRMED);
        } else if (REPORT_UPDATE.equals(getParamAction())) {
            setAction(ACTION_REPORT_UPDATE);
        } else if (REPORT_BEGIN.equals(getParamAction())) {
            setAction(ACTION_REPORT_BEGIN);
        } else if (REPORT_END.equals(getParamAction())) {
            setAction(ACTION_REPORT_END);
        } else if (DIALOG_CANCEL.equals(getParamAction())) {
            setAction(ACTION_CANCEL);
        } else {
            setAction(ACTION_DEFAULT);
            // build title for new resource dialog     
            setParamTitle(key(Messages.GUI_UPLOAD_EXCELFILE_TITLE_0));
        }
        paramsAsHidden();
    }

}