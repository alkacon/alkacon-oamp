/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.comments/src/com/alkacon/opencms/v8/comments/CmsCommentFormHandler.java,v $
 * Date   : $Date: 2011/03/10 11:56:34 $
 * Version: $Revision: 1.9 $
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

package com.alkacon.opencms.v8.comments;

import com.alkacon.opencms.v8.formgenerator.CmsFileUploadField;
import com.alkacon.opencms.v8.formgenerator.CmsForm;
import com.alkacon.opencms.v8.formgenerator.CmsFormHandler;
import com.alkacon.opencms.v8.formgenerator.CmsStringTemplateErrorListener;
import com.alkacon.opencms.v8.formgenerator.I_CmsField;

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsHtmlStripper;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsWorkplace;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.htmlparser.util.ParserException;

/**
 * The form handler controls the html or mail output of a configured comment form.<p>
 * 
 * Provides methods to determine the action that takes place and methods to create different
 * output formats of a submitted form.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.9 $
 * 
 * @since 7.0.5
 */
public class CmsCommentFormHandler extends CmsFormHandler {

    /** The comment field name constant. */
    public static final String FIELD_COMMENT = "comment";

    /** The ip address field name constant. */
    public static final String FIELD_IPADDRESS = "ipaddress";

    /** The locale field name constant. */
    public static final String FIELD_LOCALE = "locale";

    /** The name field name constant. */
    public static final String FIELD_NAME = "name";

    /** The user field name constant. */
    public static final String FIELD_USERNAME = "username";

    /** The module name. */
    public static final String MODULE_NAME = "com.alkacon.opencms.v8.comments";

    /** Module parameter name prefix constant. */
    public static final String MODULE_PARAM_CONFIG_PREFIX = "config:";

    /** Module parameter name for default mail to address. */
    public static final String MODULE_PARAM_DEFAUL_MAIL_TO = "default_mail_to";

    /** The path to the default HTML templates for the form. */
    public static final String VFS_PATH_DEFAULT_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + MODULE_NAME
        + "/resources/formtemplates/default.st";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCommentFormHandler.class);

    /** The cloned cms context. */
    private CmsObject m_cms;

    /** Some predefined comment substitutions. */
    private Map<String, String> m_substitutions;

    /**
     * Empty constructor, be sure to call one of the available initialization methods afterwards.<p>
     * 
     * Possible initialization methods are:<p>
     * <ul>
     * <li>{@link #init(PageContext, HttpServletRequest, HttpServletResponse)}</li>
     * <li>{@link #init(PageContext, HttpServletRequest, HttpServletResponse, String)}</li>
     * </ul>
     */
    public CmsCommentFormHandler() {

        super();
    }

    /**
     * Special form output for the comments.<p>
     * 
     * @see com.alkacon.opencms.v8.formgenerator.CmsFormHandler#createForm()
     */
    @Override
    public void createForm() throws IOException {

        // the output writer
        Writer out = getJspContext().getOut();

        // check the template group syntax and show eventual errors
        out.write(buildTemplateGroupCheckHtml());

        boolean showForm = showForm();
        if (!showForm) {
            // form has been submitted with correct values
            // try to send a notification email with the submitted form field values
            if (sendData()) {
                // successfully submitted
                if (getFormConfirmationText().equals("")) {
                    // and no confirmation required
                    out.write("ok");
                    return;
                }
                // confirmation output
                StringTemplate sTemplate = getOutputTemplate("confirmationoutput");
                sTemplate.setAttribute("formconfig", getCommentFormConfiguration());
                sTemplate.setAttribute("closebutton", getMessages().key("form.button.close"));
                out.write(sTemplate.toString());
            } else {
                // problem while submitting
                StringTemplate sTemplate = getOutputTemplate("emailerror");
                sTemplate.setAttribute("headline", getMessages().key("form.error.mail.headline"));
                sTemplate.setAttribute("text", getMessages().key("form.error.mail.text"));
                sTemplate.setAttribute("error", getErrors().get("sendmail"));
                out.write(sTemplate.toString());
            }
            return;
        }

        // create the form
        out.write(buildFormHtml());

        // add additional JS
        StringTemplate sTemplate = getOutputTemplate("formscript");
        sTemplate.setAttribute(
            "formlink",
            link("/system/modules/com.alkacon.opencms.v8.comments/elements/comment_form.jsp"));
        sTemplate.setAttribute("isguest", new Boolean(getRequestContext().getCurrentUser().isGuestUser()));
        sTemplate.setAttribute(
            "username",
            ("" + getRequestContext().getCurrentUser().getFirstname() + " " + getRequestContext().getCurrentUser().getLastname()).trim());
        sTemplate.setAttribute("useremail", getRequestContext().getCurrentUser().getEmail());
        sTemplate.setAttribute("namefield", getCommentFormConfiguration().getFieldByDbLabel("name"));
        sTemplate.setAttribute("emailfield", getCommentFormConfiguration().getFieldByDbLabel("email"));
        sTemplate.setAttribute("commentfield", getCommentFormConfiguration().getFieldByDbLabel("comment"));
        sTemplate.setAttribute("charleft", getMessages().key("form.comment.char.left"));
        out.write(sTemplate.toString());
    }

    /**
     * Overriding to work with a cloned context only.<p>
     * 
     * @see org.opencms.jsp.CmsJspBean#getCmsObject()
     */
    @Override
    public CmsObject getCmsObject() {

        if (m_cms == null) {
            try {
                m_cms = OpenCms.initCmsObject(super.getCmsObject());
            } catch (CmsException e) {
                LOG.error(e);
            }
        }
        return m_cms;
    }

    /**
     * Returns the form configuration.<p>
     * 
     * @return the form configuration
     */
    public CmsCommentForm getCommentFormConfiguration() {

        return (CmsCommentForm)super.getFormConfiguration();
    }

    /**
     * Returns the output template group that generates the web form HTML output.<p>
     * 
     * @return the output template group that generates the web form HTML output
     */
    @Override
    public StringTemplateGroup getOutputTemplateGroup() {

        if (m_outputTemplates == null) {
            // first get super template group of web form module
            StringTemplateGroup superGroup = super.getOutputTemplateGroup();

            // read default output templates from module parameter
            String vfsPath = OpenCms.getModuleManager().getModule(MODULE_NAME).getParameter(
                CmsForm.MODULE_PARAM_TEMPLATE_FILE,
                VFS_PATH_DEFAULT_TEMPLATEFILE);
            try {
                // first try to get the initialized template group from VFS cache
                String rootPath = getRequestContext().addSiteRoot(vfsPath);
                StringTemplateGroup stGroup = (StringTemplateGroup)CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().getCachedObject(
                    getCmsObject(),
                    rootPath);
                if (stGroup == null) {
                    // template group is not in cache, read the file and generate template group
                    CmsFile stFile = getCmsObject().readFile(vfsPath);
                    String stContent = new String(
                        stFile.getContents(),
                        getCmsObject().getRequestContext().getEncoding());
                    StringTemplateErrorListener errors = new CmsStringTemplateErrorListener();
                    stGroup = new StringTemplateGroup(new StringReader(stContent), DefaultTemplateLexer.class, errors);
                    stGroup.setSuperGroup(superGroup);
                    // store the template group in cache
                    CmsVfsMemoryObjectCache.getVfsMemoryObjectCache().putCachedObject(getCmsObject(), rootPath, stGroup);
                }
                m_outputTemplates = stGroup;
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while creating web form HTML output templates from file \"" + vfsPath + "\".");
            }
        }
        return m_outputTemplates;
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * Internally used by {@link CmsCommentsAccess}.<p>
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     * @param access the comment configuration bean
     * 
     * @throws CmsException if creating the form configuration object fails
     */
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res, CmsCommentsAccess access)
    throws CmsException {

        super.init(context, req, res, access.getConfig().getConfigUri());
        // set URI to access URI
        getCmsObject().getRequestContext().setUri(access.getUri());
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * @see com.alkacon.opencms.v8.formgenerator.CmsFormHandler#init(javax.servlet.jsp.PageContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
     */
    @Override
    public void init(PageContext context, HttpServletRequest req, HttpServletResponse res, String formConfigUri)
    throws CmsException {

        CmsCommentsAccess access = new CmsCommentsAccess(context, req, res, formConfigUri);
        init(context, req, res, access);
    }

    /**
     * @see com.alkacon.opencms.v8.formgenerator.CmsFormHandler#sendData()
     */
    @Override
    public boolean sendData() {

        I_CmsField field = getFormConfiguration().getFieldByDbLabel(FIELD_COMMENT);
        if (field != null) {
            String value = field.getValue();
            try {
                value = new CmsHtmlStripper(false).stripHtml(value);
            } catch (ParserException e) {
                // ignore
            }
            value = CmsLinkDetector.substituteLinks(value);
            value = CmsStringUtil.substitute(value, getSubstitutions());
            field.setValue(value);
        }
        // only trigger error handling for the offline project
        boolean result = super.sendData() || getCmsObject().getRequestContext().getCurrentProject().isOnlineProject();
        return result;
    }

    /**
     * Returns the HTML for the input form, generated by using the given string template file.<p>
     * 
     * @return the HTML for the input form, generated by using the given string template file
     */
    @Override
    protected String buildFormHtml() {

        // determine if form type has to be set to "multipart/form-data" in case of upload fields
        String encType = null;

        // determine error message
        String errorMessage = null;
        if (hasValidationErrors()) {
            errorMessage = getMessages().key("form.error.message");
        }

        // determine mandatory message
        String mandatoryMessage = null;
        if (getFormConfiguration().isShowMandatory() && getFormConfiguration().hasMandatoryFields()) {
            mandatoryMessage = getMessages().key("form.message.mandatory");
        }

        // calculate fields to show (e.g. if paging is activated)
        int pos = 0;
        int place = 0;

        // generate HTML for the input fields
        StringBuffer fieldHtml = new StringBuffer(getFormConfiguration().getFields().size() * 256);
        for (int i = 0, n = getFormConfiguration().getFields().size(); i < n; i++) {
            // loop through all form input fields 
            I_CmsField field = getFormConfiguration().getFields().get(i);

            if (i == (n - 1)) {
                // the last one has to close the row
                place = 1;
            }

            field.setPlaceholder(place);
            field.setPosition(pos);
            String infoMessage = getInfos().get(field.getName());
            // validate the file upload field here already because of the lost values in these fields
            if (field instanceof CmsFileUploadField) {
                infoMessage = field.validateForInfo(this);
            }
            fieldHtml.append(field.buildHtml(
                this,
                getMessages(),
                getErrors().get(field.getName()),
                getFormConfiguration().isShowMandatory(),
                infoMessage));
            pos = field.getPosition();
            place = field.getPlaceholder();
        }

        // determine if subfield JavaScript has to be added
        String subFieldJS = null;

        // determine if the submit and other buttons are shown
        String submitButton = getMessages().key("form.button.submit");
        String resetButton = getMessages().key("form.button.cancel");
        String prevButton = null;
        String downloadButton = null;

        // create necessary hidden fields
        String hiddenFields = null;
        StringBuffer hFieldsBuf = new StringBuffer(256);
        hFieldsBuf.append("<input type=\"hidden\" name=\"cmturi\" value=\"").append(getRequest().getParameter("cmturi")).append(
            "\" />");
        hFieldsBuf.append("<input type=\"hidden\" name=\"cmtminimized\" value=\"").append(
            getRequest().getParameter("cmtminimized}")).append("\" />");
        hFieldsBuf.append("<input type=\"hidden\" name=\"cmtlist\" value=\"").append(
            getRequest().getParameter("cmtlist")).append("\" />");
        hFieldsBuf.append("<input type=\"hidden\" name=\"cmtsecurity\" value=\"").append(
            getRequest().getParameter("cmtsecurity")).append("\" />");
        hFieldsBuf.append("<input type=\"hidden\" name=\"configUri\" value=\"").append(
            getCommentFormConfiguration().getConfigUri()).append("\" />");
        hFieldsBuf.append("<input type=\"hidden\" name=\"__locale\" value=\"").append(
            getRequest().getParameter("__locale")).append("\" />");
        hiddenFields = hFieldsBuf.toString();

        // create the main form and pass the previously generated field HTML as attribute
        StringTemplate sTemplate = getOutputTemplate("form");
        // set the necessary attributes to use in the string template
        sTemplate.setAttribute("formuri", getRequest().getParameter("cmturi"));
        sTemplate.setAttribute("enctype", encType);
        sTemplate.setAttribute("errormessage", errorMessage);
        sTemplate.setAttribute("mandatorymessage", mandatoryMessage);
        sTemplate.setAttribute("formconfig", getFormConfiguration());
        sTemplate.setAttribute("fields", fieldHtml.toString());
        sTemplate.setAttribute("subfieldjs", subFieldJS);
        sTemplate.setAttribute("downloadbutton", downloadButton);
        sTemplate.setAttribute("submitbutton", submitButton);
        sTemplate.setAttribute("resetbutton", resetButton);
        sTemplate.setAttribute("hiddenfields", hiddenFields);
        sTemplate.setAttribute("prevbutton", prevButton);
        return sTemplate.toString();
    }

    /**
     * Initializes the form handler and creates the necessary configuration objects.<p>
     * 
     * @param req the JSP request 
     * @param formConfigUri URI of the form configuration file, if not provided, current URI is used for configuration
     * 
     * @throws Exception if creating the form configuration objects fails
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void configureForm(HttpServletRequest req, String formConfigUri) throws Exception {

        if (formConfigUri == null) {
            return;
        }
        m_parameterMap = new HashMap();
        m_parameterMap.putAll(getRequest().getParameterMap());

        m_macroResolver = CmsMacroResolver.newInstance();
        m_macroResolver.setKeepEmptyMacros(false);
        m_macroResolver.addMacro(
            MACRO_URL,
            OpenCms.getSiteManager().getCurrentSite(getCmsObject()).getServerPrefix(
                getCmsObject(),
                getRequestContext().getUri())
                + link(getRequestContext().getUri()));
        m_macroResolver.addMacro(MACRO_LOCALE, getRequestContext().getLocale().toString());

        String formAction = getParameter(PARAM_FORMACTION);
        m_isValidatedCorrect = null;
        setInitial(CmsStringUtil.isEmpty(formAction));
        // get the localized messages
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE_NAME);
        String para = module.getParameter("message", "/com/alkacon/opencms/v8/comments/workplace");

        setMessages(new CmsMessages(para, getRequestContext().getLocale()));
        // get the form configuration
        setFormConfiguration(new CmsCommentForm(this, getMessages(), isInitial(), formConfigUri, formAction));
    }

    /**
     * Returns some predefined comment substitutions.<p>
     * 
     * @return some predefined comment substitutions
     */
    protected Map<String, String> getSubstitutions() {

        if (m_substitutions == null) {
            m_substitutions = new HashMap<String, String>();
            m_substitutions.put("\n\n", "<p>");
            m_substitutions.put("\n", "<br>");
        }
        return m_substitutions;
    }
}
