/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.comments;

import com.alkacon.opencms.v8.comments.util.CmsCommentsUtil;
import com.alkacon.opencms.v8.comments.util.CmsStringTemplateMessageBundleWrapper;
import com.alkacon.opencms.v8.comments.util.SecurableString;
import com.alkacon.opencms.v8.formgenerator.CmsStringTemplateErrorListener;
import com.alkacon.opencms.v8.formgenerator.collector.CmsFormBean;

import org.opencms.cache.CmsVfsMemoryObjectCache;
import org.opencms.file.CmsFile;
import org.opencms.i18n.CmsResourceBundleLoader;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.workplace.CmsWorkplace;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

/** Handler for the stringtemplates for the last-comments list*/
public class CmsLastCommentStringTemplateHandler extends CmsJspActionElement {

    /** The stringtemplates read from the configured file */
    StringTemplateGroup m_outputTemplates = null;
    /** Wraps a message bundle. Securable Strings are used because they can be escaped, secured and rendered as StringTemplate */
    Map<String, SecurableString> m_messages = null;

    /** special field identifier for fields of the comment that are always present */
    public static final String FIELDNAME_CREATION_DATE = "_creationdate_";
    /** special field identifier for fields of the comment that are always present */
    public static final String FIELDNAME_PAGE_LINK = "_pagelink_";
    /** special field identifier for fields of the comment that are always present */
    public static final String FIELDNAME_PAGE_TITLE = "_pagetitle_";

    /** The resourcebundle used for localization and initialized with the default resource bundle */
    private String m_resourcebundle = null;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsLastCommentStringTemplateHandler.class);

    /** Path of the default stringtemplate file for the last-comments list */
    public static final String VFS_PATH_DEFAULT_TEMPLATEFILE = CmsWorkplace.VFS_PATH_MODULES
        + CmsCommentsUtil.MODULE_NAME
        + "/resources/templates/default-last-comments.st";

    /** Module parameter to change the stringtemplate file for comments */
    public static final String MODULE_PARAM_TEMPLATE_FILE = "template-last-comments";

    /**
     * @return the resource bundle used for localization
     */
    public String getResourceBundle() {

        if (m_resourcebundle != null) {
            return m_resourcebundle;
        }
        String bundle = OpenCms.getModuleManager().getModule(CmsCommentsUtil.MODULE_NAME).getParameter(
            CmsCommentsUtil.MODULE_PARAM_LAST_COMMENTS_BUNDLE,
            CmsCommentsUtil.LAST_COMMENTS_DEFAULT_BUNDLE);
        if (CmsResourceBundleLoader.getBundle(bundle, getRequestContext().getLocale()) == null) {
            LOG.warn(Messages.get().getBundle().key(
                Messages.WARN_MESSAGE_BUNDLE_NOT_FOUND_3,
                bundle,
                CmsCommentsUtil.MODULE_PARAM_LAST_COMMENTS_BUNDLE,
                CmsCommentsUtil.LAST_COMMENTS_DEFAULT_BUNDLE));
            bundle = CmsCommentsUtil.LAST_COMMENTS_DEFAULT_BUNDLE;
        }
        return bundle;
    }

    /**
     * @param resourcebundle the resource bundle that should be used to localize the last-comments list
     */
    public void setResourceBundle(final String resourcebundle) {

        m_resourcebundle = resourcebundle;
    }

    /**
     * @return Messagebundle wrapped for StringTemplate usage.
     */
    private Map<String, SecurableString> getMessages() {

        if (m_messages == null) {
            String resourceBundle = getResourceBundle();
            CmsStringTemplateMessageBundleWrapper bundleWrapper = new CmsStringTemplateMessageBundleWrapper(
                resourceBundle,
                getRequestContext().getLocale());
            m_messages = bundleWrapper.getMessageMap();
        }
        return m_messages;
    }

    /**
     * Returns the output template group that generates the web form HTML output.<p>
     * 
     * @return the output template group that generates the web form HTML output
     */
    public StringTemplateGroup getOutputTemplateGroup() {

        if (m_outputTemplates == null) {
            // read default output templates from module parameter
            String vfsPath = OpenCms.getModuleManager().getModule(CmsCommentsUtil.MODULE_NAME).getParameter(
                MODULE_PARAM_TEMPLATE_FILE,
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
     * Returns the template for the comment's HTML output.<p>
     * 
     * @param templateName the name of the template to return
     * 
     * @return the template for the comment's HTML output
     */
    public StringTemplate getOutputTemplate(String templateName) {

        StringTemplate result = getOutputTemplateGroup().getInstanceOf(templateName);
        if (!getRequestContext().getCurrentProject().isOnlineProject() && (result == null)) {
            // no template with the specified name found, return initialized error template
            try {
                CmsFile stFile = getCmsObject().readFile(CmsCommentsUtil.VFS_PATH_ERROR_TEMPLATEFILE);
                String stContent = new String(stFile.getContents(), getCmsObject().getRequestContext().getEncoding());
                result = new StringTemplate(stContent, DefaultTemplateLexer.class);
                // set the error attributes of the template
                result.setAttribute("errorheadline", "Error getting output template");
                result.setAttribute("errortext", "The desired template \"" + templateName + "\" was not found.");
                result.setAttribute("errortemplatenames", getOutputTemplateGroup().getTemplateNames());
            } catch (Exception e) {
                // something went wrong, log error
                LOG.error("Error while getting error output template from file \""
                    + CmsCommentsUtil.VFS_PATH_ERROR_TEMPLATEFILE
                    + "\".");
            }
        }
        return result;
    }

    /**
     * @param key message key
     * @return message for the key (from the bundle set as m_resourcebundle)
     */
    public String getMessage(final String key) {

        return getMessage(key, null);
    }

    /**
     * @param key message key
     * @param vals arguments filled in the message
     * @return message for the key with the arguments filled in (message from the bundle set as m_resourcebundle)
     */
    public String getMessage(final String key, final String[] vals) {

        String message = getMessages(getResourceBundle(), getRequestContext().getLocale()).key(key);
        if ((vals != null) && (vals.length > 0)) {
            MessageFormat messageform = new MessageFormat(message);
            return messageform.format(vals);
        } else {
            return message;
        }

    }

    /**
     * @param entries the list's body with all appearing elements already rendered
     * @param title the title of the list
     * @return The HTML rendered by the stringtemplate "last_comments_list"
     */
    public String buildLastCommentsListHtml(final String entries, final String title) {

        StringTemplate sTemplate = getOutputTemplate("last_comments_list");
        sTemplate.setAttribute("title", title);
        sTemplate.setAttribute("entries", entries);
        return sTemplate.toString();
    }

    /**
     * @param comment the comment to render
     * @param fields the fields of the content that should be shown
     * @param boxColor the color configured as element-setting
     * @return The HTML rendered by the stringtemplate "last_comments_entry"
     */
    public String buildLastCommentsEntryHtml(final CmsFormBean comment, final List<String> fields, final String boxColor) {

        List<String> allFields = new ArrayList<String>(comment.getFields().size() + 3);
        allFields.addAll(comment.getFields().keySet());
        allFields.add(FIELDNAME_CREATION_DATE);
        allFields.add(FIELDNAME_PAGE_LINK);
        allFields.add(FIELDNAME_PAGE_TITLE);
        Map<String, Boolean> fieldsToShow = listToMapWithAllValuesTrue(fields == null ? allFields : fields);
        StringTemplate sTemplate = getOutputTemplate("last_comments_entry");
        sTemplate.setAttribute("boxColor", boxColor);
        sTemplate.setAttribute("commentFields", comment.getFields());
        sTemplate.setAttribute("commentCreationDate", comment.getDateCreated());
        sTemplate.setAttribute("commentPageLink", comment.getUri());
        sTemplate.setAttribute("commentPageTitle", comment.getTitle());
        sTemplate.setAttribute("fieldsToShow", fieldsToShow);
        sTemplate.setAttribute("messages", getMessages());
        return sTemplate.toString();
    }

    /**
     * The conversion is just made to provide a suitable datastructure for Stringtemplates to check, if something is in a list or not.
     * 
     * @param keyList the list to convert
     * @return a map with the list entries as keys and the Boolean true as value for each key.
     */
    private Map<String, Boolean> listToMapWithAllValuesTrue(final List<String> keyList) {

        Map<String, Boolean> result = new HashMap<String, Boolean>(keyList.size());
        for (String key : keyList) {
            result.put(key, Boolean.valueOf(true));
        }
        return result;
    }

    /**
     * @return The HTML rendered by the stringtemplate "last_comments_no_entry"
     */
    public String buildLastCommentsNoEntryHtml() {

        StringTemplate sTemplate = getOutputTemplate("last_comments_no_entry");
        sTemplate.setAttribute("messages", getMessages());
        return sTemplate.toString();
    }
}
