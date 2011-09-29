/*
 * File   : $Source: /home/cvs/EbkModules/src-modules/org/opencms/frontend/templateone/modules/CmsTemplateModules.java,v $
 * Date   : $Date: 2011-07-13 14:08:12 $
 * Version: $Revision: 1.11 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.commons;

import com.alkacon.simapi.Simapi;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.CmsResourceTypeFolder;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspNavElement;
import org.opencms.loader.CmsImageScaler;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlVfsImageValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * A helper bean for the template one modules.<p>
 * 
 * Provides methods to create list views with category browsing and 
 * convenience methods to display date values and links.<p>
 * 
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.11 $ 
 * 
 * @since 6.0.0 
 */
public class CmsTemplateModules extends CmsTemplateBase {

    /** Image alignment right. */
    public static final String IMAGE_RIGHT = "imageright";

    /** Image width variant name for medium images. */
    public static final String IMG_WIDTH_MEDIUM = "medium";

    /** Xml content description node. */
    public static final String NODE_DESCRIPTION = "Description";

    /** Xml content paragraph node. */
    public static final String NODE_FAQPARAGRAPH = "NewFaqParagraph";

    /** Xml content image node. */
    public static final String NODE_IMAGE = "Image";

    /** Xml content text node. */
    public static final String NODE_TEXT = "Text";

    /** Request parameter name for the category folder. */
    public static final String PARAM_CATEGORYFOLDER = "categoryfolder";

    /** Request parameter name for the xmlcontent folder. */
    public static final String PARAM_FOLDER = "folder";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsTemplateModules.class);

    /** FAQ category folder. */
    private String m_categoryFolder;

    /** FAQ flag if category folders are present. */
    private boolean m_hasCategoryFolders;

    /** Image alignment. */
    private String m_imageAlignment;

    /** Main image uri for image group in new image format. */
    private String m_mainImageUri;

    /** FAQ paragraph number. */
    private int m_paragraphNumber;

    /**
     * @see org.opencms.jsp.CmsJspActionElement#CmsJspActionElement(PageContext, HttpServletRequest, HttpServletResponse)
     */
    public CmsTemplateModules(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Creates a html breadcrumb navigation.<p>
     * 
     * Instead of the NavText properties the title properties are used as anchor texts in this breadcrumb navigation! 
     * The navigation starts from a folder as specified in the request parameter "folder" and 
     * goes to the folder specified in the request parameter "categoryfolder".<p>
     * 
     * @param separator a String to separate the anchors in the breadcrumb navigation
     * @return the html for the breadcrumb navigation
     */
    public String buildHtmlNavBreadcrumb(String separator) {

        StringBuffer result = new StringBuffer(16);
        // get the value of the start folder request parameter
        String startfolder = getRequest().getParameter(PARAM_FOLDER);
        // calculate levels to go down
        int displayLevels = -((CmsResource.getPathLevel(getCategoryFolder()) - CmsResource.getPathLevel(startfolder)) + 1);
        // get the navigation list
        List breadcrumb = getNavigation().getNavigationBreadCrumb(getCategoryFolder(), displayLevels, -1, true);

        for (int i = 0, n = breadcrumb.size(); i < n; i++) {
            CmsJspNavElement navElement = (CmsJspNavElement)breadcrumb.get(i);
            // get the title of the current navigation element
            String title = navElement.getTitle();
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(Messages.get().getBundle().key(
                        Messages.LOG_ERR_MISSING_PROP_2,
                        navElement.getResourceName(),
                        CmsPropertyDefinition.PROPERTY_TITLE));
                }
                title = CmsResource.getName(navElement.getResourceName());
            }
            // generate the link for the navigation element
            String folderUri = link(getRequestContext().getUri()
                + "?"
                + PARAM_CATEGORYFOLDER
                + "="
                + navElement.getResourceName());

            // append the anchor
            result.append("<a href=\"");
            result.append(folderUri);
            result.append("\">");
            result.append(title);
            result.append("</a>");

            // append the separator
            if (i < (n - 1)) {
                result.append(separator);
            }
        }

        return result.toString();
    }

    /**
     * Creates a html &lt;li&gt; list of all folders inside the current folder.<p>
     * 
     * Additionally, behind each folder the number of resources of a specified resource type gets listed.<p>
     * 
     * @param resourceTypeId the resource type to count resources inside folders
     * @param attrs optional html attributes to use in the &lt;ul&gt; tag
     * @return a html &lt;li&gt; list of all folders inside the current folder
     * @throws CmsException if something goes wrong
     */
    public String buildHtmlNavList(int resourceTypeId, String attrs) throws CmsException {

        // get the start folder from request
        String startfolder = getCategoryFolder();

        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.get().getBundle().key(
                Messages.LOG_DEBUG_BUILD_HTML_NAVLIST_2,
                startfolder,
                new Integer(resourceTypeId)));
        }

        // read the resource tree
        CmsResourceFilter filter = CmsResourceFilter.DEFAULT.addRequireType(CmsResourceTypeFolder.RESOURCE_TYPE_ID);
        List resourceTree = getCmsObject().readResources(startfolder, filter, true);

        String indent = "&nbsp;&nbsp;";
        StringBuffer result = new StringBuffer(32);

        if (resourceTree.size() > 0) {

            // open the list
            result.append("<ul");
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(attrs)) {
                // append additional attributes
                result.append(" ").append(attrs);
            }

            result.append(">\n");

            int indentCount = 0;
            int startLevel = CmsResource.getPathLevel(getCmsObject().getSitePath((CmsResource)resourceTree.get(0)));
            int lastLevel = startLevel;
            // set flag that category folders are present
            m_hasCategoryFolders = true;

            for (int i = 0, n = resourceTree.size(); i < n; i++) {

                CmsResource resource = (CmsResource)resourceTree.get(i);
                String resourceName = getCmsObject().getSitePath(resource);

                // skip files
                if (!resource.isFolder()) {
                    continue;
                }

                // count resources of the specified type inside folder
                int faqCount = getResourceCount(resourceName, resourceTypeId);

                int level = CmsResource.getPathLevel(resourceName);

                if (lastLevel < level) {
                    // increase indentation level
                    indentCount++;
                } else if (lastLevel > level) {
                    // decrease indentation level
                    indentCount--;
                }

                // open a new list item (by closing a previous list item first eventually)
                if (level == startLevel) {
                    if (i == 0) {
                        result.append("<li>\n");
                    } else {
                        result.append("<br>&nbsp;&nbsp;\n");
                        result.append("</li>\n");
                        result.append("<li>\n");
                    }
                }

                // append a line break on sub-FAQs
                if (level > startLevel) {
                    result.append("<br>");
                }

                // append indentation for sub-FAQs
                for (int j = 0; j < indentCount; j++) {
                    result.append(indent);
                }

                String faqUri = link(getRequestContext().getUri() + "?" + PARAM_CATEGORYFOLDER + "=" + resourceName);

                String title = getCmsObject().readPropertyObject(
                    resourceName,
                    CmsPropertyDefinition.PROPERTY_TITLE,
                    false).getValue(null);

                if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(Messages.get().getBundle().key(
                            Messages.LOG_ERR_MISSING_PROP_2,
                            resourceName,
                            CmsPropertyDefinition.PROPERTY_TITLE));
                    }
                    title = resource.getName();
                }

                // append the anchor
                result.append("<a href=\"");
                result.append(faqUri);
                result.append("\">");
                if (level == startLevel) {
                    result.append("<b>").append(title).append("</b>");
                } else {
                    result.append(title);
                }
                result.append("</a>");

                // append number of FAQ articles on "top-level" FAQs
                if (level == startLevel) {
                    result.append("&nbsp;&nbsp;(");
                    result.append(faqCount);
                    result.append(")\n");
                }

                result.append("\n");
                lastLevel = level;
            }

            // close the last open list item
            result.append("</li>\n");

            // close the list
            result.append("</ul>\n");
        }

        return result.toString();
    }

    /**
     * Creates a html &lt;li&gt; list of all folders inside the current folder.<p>
     * 
     * Additionally, behind each folder the number of resources of a specified resource type gets listed.<p>
     * 
     * @param resourceTypeName the resource type name to count resources inside folders
     * @param attrs optional html attributes to use in the &lt;ul&gt; tag
     * @return a html &lt;li&gt; list of all folders inside the current folder
     * @throws CmsException if something goes wrong
     */
    public String buildHtmlNavList(String resourceTypeName, String attrs) throws CmsException {

        I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(resourceTypeName);
        return buildHtmlNavList(resType.getTypeId(), attrs);
    }

    /**
     * Creates a HTML anchor from the values of three page context attribute names.
     * 
     * @param hrefAttrib the name of the page context attribute containing the link URL
     * @param descrAttrib the name of the page context attribute containing the link description
     * @param targetAttrib the name of the page context attribute containing the link target
     * @return an HTML anchor
     */
    public String getAnchor(String hrefAttrib, String descrAttrib, String targetAttrib) {

        String attribHref = (String)getJspContext().getAttribute(hrefAttrib);
        String attribDescr = (String)getJspContext().getAttribute(descrAttrib);
        boolean openBlank = Boolean.valueOf((String)getJspContext().getAttribute(targetAttrib)).booleanValue();

        String description = attribDescr;
        if (CmsStringUtil.isEmpty(attribDescr) || attribDescr.startsWith("???")) {
            description = attribHref;
        }

        String href = attribHref;
        if (!attribHref.toLowerCase().startsWith("http")) {
            href = link(attribHref);
        }

        String target = "";
        if (openBlank) {
            target = "_blank";
        }

        StringBuffer anchor = new StringBuffer();
        anchor.append("<a href=\"").append(href).append("\"");

        if (CmsStringUtil.isNotEmpty(description)) {
            anchor.append(" title=\"").append(description).append("\"");
        }

        if (CmsStringUtil.isNotEmpty(target)) {
            anchor.append(" target=\"").append(target).append("\"");
        }

        anchor.append(">").append(description).append("</a>");

        return anchor.toString();
    }

    /**
     * Returns the URI of the currently displayed category folder.<p>
     * 
     * @return the URI of the currently displayed category folder
     */
    public String getCategoryFolder() {

        if (m_categoryFolder == null) {
            // get the category folder from request
            m_categoryFolder = getRequest().getParameter(PARAM_CATEGORYFOLDER);
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_categoryFolder)) {
                // no folder found in request, use folder parameter
                m_categoryFolder = getRequest().getParameter(PARAM_FOLDER);
            }
        }
        return m_categoryFolder;
    }

    /**
     * Get Html code for images in modules: event, job and news.<p>
     * 
     * @param showTextContent True if show text content
     * 
     * @return Html code for images in modules: event, job and news
     */
    public String getHtmlCodeToShowImageInPhotoGalleryInEventModuleOrJobModuleOrNewsModule(boolean showTextContent) {

        String contentPath = getCmsObject().getRequestContext().getUri();
        if (isUriChanged()) {
            contentPath = getOriginalUri();
        }
        return getHtmlCodeToShowImageInPhotoGalleryInEventModuleOrJobModuleOrNewsModule(showTextContent, contentPath);
    }

    /**
     * Get Html code for images in modules: event, job and news.<p>
     * 
     * @param showTextContent True if show text content
     * @param contentPath the path to the XML content with the image
     * 
     * @return Html code for images in modules: event, job and news
     */
    public String getHtmlCodeToShowImageInPhotoGalleryInEventModuleOrJobModuleOrNewsModule(
        boolean showTextContent,
        String contentPath) {

        StringBuffer htmlTag = new StringBuffer(1024);
        CmsXmlContent xmlContent = null;
        Locale locale = getCmsObject().getRequestContext().getLocale();
        boolean hide = false;

        //setLayoutValues();

        try {
            CmsFile xmlFile = getCmsObject().readFile(contentPath);
            xmlContent = CmsXmlContentFactory.unmarshal(getCmsObject(), xmlFile);
            // get the image nodes from the XML content

            // get text content
            String textContent = "";
            if (showTextContent) {
                String xPath = NODE_TEXT + "/";
                if (xmlContent.hasValue(xPath, locale)) {
                    textContent = xmlContent.getValue(xPath, locale).getStringValue(getCmsObject());
                }
            }

            // get images
            Iterator iterImages = xmlContent.getValues(NODE_IMAGE, locale).iterator();
            while (iterImages.hasNext()) {
                // loop all image nodes
                I_CmsXmlContentValue value = (I_CmsXmlContentValue)iterImages.next();
                String xPath = value.getPath() + "/";
                xPath += NODE_IMAGE + "/";

                if (xmlContent.hasValue(xPath, locale)) {
                    htmlTag.append(createHtmlCodePerImage(getCmsObject(), xPath, xmlContent, locale, hide, textContent));
                }
                hide = true;
                textContent = "";
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                // TODO, create message resource bundle key
                LOG.error(Messages.get().getBundle().key(org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1), e);
            }
        }
        return htmlTag.toString();
    }

    /**
     * Get Html code for images in faq module.<p>
     * 
     * @return Html code for images in faq module
     */
    public String getHtmlCodeToShowImageInPhotoGalleryInFaqModule() {

        String contentPath = getCmsObject().getRequestContext().getUri();
        if (isUriChanged()) {
            contentPath = getOriginalUri();
        }
        return getHtmlCodeToShowImageInPhotoGalleryInFaqModule(contentPath);
    }

    /**
     * Get Html code for images in faq module.<p>
     * 
     * @param contentPath the path to the XML content with the image
     * 
     * @return Html code for images in faq module
     */
    public String getHtmlCodeToShowImageInPhotoGalleryInFaqModule(String contentPath) {

        StringBuffer htmlTag = new StringBuffer(1024);
        CmsObject cmsObject = getCmsObject();
        CmsXmlContent xmlContent = null;
        Locale locale = cmsObject.getRequestContext().getLocale();
        boolean hide = false;
        int paragraphNumber = getParagraphNumber(); // current number of used paragraph
        int paragraphCounter = 0; // counter to get the current used paragraph

        //setLayoutValues();

        try {
            CmsFile xmlFile = cmsObject.readFile(contentPath);
            xmlContent = CmsXmlContentFactory.unmarshal(cmsObject, xmlFile);
            // get the paragraph nodes from the XML content

            Iterator iterParagraphs = xmlContent.getValues(NODE_FAQPARAGRAPH, locale).iterator();
            while (iterParagraphs.hasNext()) {
                // loop all paragraph nodes
                I_CmsXmlContentValue valueParagraph = (I_CmsXmlContentValue)iterParagraphs.next();

                if (paragraphCounter == paragraphNumber) {
                    String xPathParagraph = valueParagraph.getPath();

                    Iterator iterImages = xmlContent.getValues(xPathParagraph + "/" + NODE_IMAGE, locale).iterator();
                    while (iterImages.hasNext()) {
                        // loop all image nodes
                        I_CmsXmlContentValue valueImage = (I_CmsXmlContentValue)iterImages.next();
                        String xPathImage = valueImage.getPath() + "/";
                        xPathImage += NODE_IMAGE + "/";

                        if (xmlContent.hasValue(xPathImage, locale)) {
                            htmlTag.append(createHtmlCodePerImage(cmsObject, xPathImage, xmlContent, locale, hide, ""));
                        }
                        hide = true;
                    }
                }
                paragraphCounter += 1;
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                // TODO, create message resource bundle key
                LOG.error(Messages.get().getBundle().key(org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1), e);
            }
        }
        setParagraphNumber(paragraphNumber + 1);
        return htmlTag.toString();
    }

    /**
     * Gets image alignment.<p>
     * 
     * @return Image alignment
     */
    public String getImageAlignment() {

        return m_imageAlignment;
    }

    /**
     * Gets image uri from first picture.<p>
     * 
     * @return Image uri from first picture
     */
    public String getMainImageUri() {

        return m_mainImageUri;
    }

    /**
     * Gets current paragraph number from faq module.<p>
     * 
     * @return Current paragraph number from faq module
     */
    public int getParagraphNumber() {

        return m_paragraphNumber;
    }

    /**
     * Returns the number of resources with a given resource type inside a folder.<p>
     * 
     * @param foldername the folder to read resources
     * @param resourceTypeId the desired resource type ID
     * 
     * @return the number of resources
     */
    public int getResourceCount(String foldername, int resourceTypeId) {

        int result = -1;

        try {
            // filter the resources with the specified id
            CmsResourceFilter filter = CmsResourceFilter.DEFAULT.addRequireType(resourceTypeId);
            List resources = getCmsObject().readResources(foldername, filter, false);
            result = resources.size();
        } catch (CmsException e) {
            // error reading the resources
            if (LOG.isErrorEnabled()) {
                LOG.error(
                    org.opencms.db.Messages.get().getBundle().key(
                        org.opencms.db.Messages.ERR_READ_RESOURCES_WITH_TYPE_2,
                        new Integer(resourceTypeId),
                        foldername),
                    e);
            }
            result = -1;
        }

        return result;
    }

    /**
     * Returns the number of resources with a given resource type inside a folder.<p>
     * 
     * @param foldername the folder to read resources
     * @param resourceTypeName the desired resource type name
     * 
     * @return the number of resources
     */
    public int getResourceCount(String foldername, String resourceTypeName) {

        try {
            I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(resourceTypeName);
            return getResourceCount(foldername, resType.getTypeId());
        } catch (CmsException e) {
            // error getting resource type ID
            if (LOG.isErrorEnabled()) {
                LOG.error(
                    Messages.get().getBundle().key(
                        org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1,
                        resourceTypeName),
                    e);
            }
            return -1;
        }
    }

    /**
     * Returns HTML code to show top messages in modules: event and news.<p>
     * 
     * @param file File to show top message for
     * @param localeString Locale in string format
     * @param showDate Show date
     * @param uri Current uri
     * 
     * @return HTML code to show top messages in modules: event and news
     */
    public String getScaledImageTag(String file, String localeString, int imgWidth, int imgHeight, String imgClass) {

        CmsObject cmsObject = getCmsObject();
        CmsXmlContent xmlContent = null;
        Locale locale = new Locale(localeString);

        try {
            CmsFile xmlFile = cmsObject.readFile(file);
            xmlContent = CmsXmlContentFactory.unmarshal(cmsObject, xmlFile);

            // image
            String xPath = NODE_IMAGE + "/";
            if (xmlContent.hasValue(xPath, locale)) {
                if (!xmlContent.hasValue(xPath + NODE_DESCRIPTION, locale)) {
                    I_CmsXmlContentValue value = xmlContent.getValue(xPath, locale);
                    xPath = value.getPath() + "/" + NODE_IMAGE + "/";
                    return getHtmlTagForNewImageInTopMessage(
                        cmsObject,
                        xPath,
                        xmlContent,
                        locale,
                        imgWidth,
                        imgHeight,
                        imgClass).toString();
                } else {
                    // old image format
                    I_CmsXmlContentValue value = xmlContent.getValue(xPath, locale);
                    xPath = value.getPath() + "/" + NODE_IMAGE + "/";
                    return getHtmlTagForNewImageInTopMessage(
                        cmsObject,
                        xPath,
                        xmlContent,
                        locale,
                        imgWidth,
                        imgHeight,
                        imgClass).toString();
                }
            }

        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                // TODO, create message resource bundle key
                LOG.error(Messages.get().getBundle().key(org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1), e);
            }
        }
        return "";
    }

    /**
     * Returns true if the currently displayed folder contains subfolders which are used as category folders.<p>
     * 
     * This method has to be called after the method {@link CmsTemplateModules#buildHtmlNavList(int, String)}.<p> 
     * 
     * @return true if the currently displayed folder contains subfolders which are used as category folders
     */
    public boolean hasCategoryFolders() {

        return m_hasCategoryFolders;
    }

    /**
     * Checks for new version of image code in modules: event, job and news.<p>
     * 
     * @return True, if new version of image code in modules: event, job and news
     */
    public boolean isNewImageCodeInEventModuleOrJobModuleOrNewsModule() {

        CmsObject cmsObject = getCmsObject();
        String imgUri = cmsObject.getRequestContext().getUri();
        if (isUriChanged()) {
            imgUri = getOriginalUri();
        }
        CmsXmlContent xmlContent = null;
        Locale locale = cmsObject.getRequestContext().getLocale();

        try {
            CmsFile xmlFile = cmsObject.readFile(imgUri);
            xmlContent = CmsXmlContentFactory.unmarshal(cmsObject, xmlFile);

            Iterator iterImages = xmlContent.getValues(NODE_IMAGE, locale).iterator();

            while (iterImages.hasNext()) {
                // loop all paragraph nodes
                I_CmsXmlContentValue value = (I_CmsXmlContentValue)iterImages.next();
                String xPath = value.getPath() + "/";
                xPath += NODE_DESCRIPTION;

                if (!xmlContent.hasValue(xPath, locale)) {
                    return true;
                }
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                // TODO, create message resource bundle key
                LOG.error(Messages.get().getBundle().key(org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1), e);
            }
        }
        return false;
    }

    /**
     * Checks for new version of image code in faq module.<p>
     * 
     * @return True, if new version of image code in faq module
     */
    public boolean isNewImageCodeInFaqModule() {

        CmsObject cmsObject = getCmsObject();
        String imgUri = cmsObject.getRequestContext().getUri();
        if (isUriChanged()) {
            imgUri = getOriginalUri();
        }
        CmsXmlContent xmlContent = null;
        Locale locale = cmsObject.getRequestContext().getLocale();

        try {
            CmsFile xmlFile = cmsObject.readFile(imgUri);
            xmlContent = CmsXmlContentFactory.unmarshal(cmsObject, xmlFile);

            Iterator iterParagraphs = xmlContent.getValues(NODE_FAQPARAGRAPH, locale).iterator();
            while (iterParagraphs.hasNext()) {
                // loop all paragraph nodes
                I_CmsXmlContentValue valueParagraph = (I_CmsXmlContentValue)iterParagraphs.next();
                String xPathParagraph = valueParagraph.getPath();

                Iterator iterImages = xmlContent.getValues(xPathParagraph + "/" + NODE_IMAGE, locale).iterator();
                while (iterImages.hasNext()) {
                    // loop all image nodes
                    I_CmsXmlContentValue valueImage = (I_CmsXmlContentValue)iterImages.next();
                    String xPath = valueImage.getPath() + "/";
                    xPath += NODE_DESCRIPTION;

                    if (!xmlContent.hasValue(xPath, locale)) {
                        return true;
                    }
                }
            }
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                // TODO, create message resource bundle key
                LOG.error(Messages.get().getBundle().key(org.opencms.loader.Messages.ERR_UNKNOWN_RESTYPE_NAME_REQ_1), e);
            }
        }
        return false;
    }

    /**
     * Checks if two dates in the page context attributes have the same date and differ only from their time.<p>
     * 
     * @param startDateAttrib the name of the page context attribute containing the start date string
     * @param endDateAttrib the name of the page context attribute containing the end date string
     * @return true if the two dates differ only in time, otherwise false
     */
    public boolean isSameDate(String startDateAttrib, String endDateAttrib) {

        String timeString = (String)getJspContext().getAttribute(startDateAttrib);
        long timestamp = (new Long(timeString)).longValue();
        Calendar calStart = new GregorianCalendar();
        calStart.setTimeInMillis(timestamp);

        timeString = (String)getJspContext().getAttribute(endDateAttrib);
        timestamp = (new Long(timeString)).longValue();
        Calendar calEnd = new GregorianCalendar();
        calEnd.setTimeInMillis(timestamp);

        return ((calStart.get(Calendar.DAY_OF_YEAR) == calEnd.get(Calendar.DAY_OF_YEAR)) && (calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)));
    }

    /**
     * Saves a {@link Date} object in the page context that was created from the value of
     * a specified page context attribute.<p>
     * 
     * @param dateAttrib the name of the page context attribute containing the date string
     */
    public void setDate(String dateAttrib) {

        String timeString = (String)getJspContext().getAttribute(dateAttrib);
        long timestamp = 0;
        try {
            timestamp = (new Long(timeString)).longValue();
        } catch (Exception e) {
            // no valid date given            
        }
        Date date = new Date(timestamp);
        getJspContext().setAttribute("date", date);
    }

    /**
     * Sets image alignment.<p>
     * 
     * @param alignment Image alignment
     */
    public void setImageAlignment(String alignment) {

        m_imageAlignment = alignment;
    }

    /**
     * Sets uri from first picture.<p>
     * 
     * @param mainImageUri Image uri from first picture
     */
    public void setMainImageUri(String mainImageUri) {

        m_mainImageUri = mainImageUri;
    }

    /**
     * Sets current paragraph number in faq module.<p>
     * 
     * @param paragraphNumber Current paragraph number in faq module
     */
    public void setParagraphNumber(int paragraphNumber) {

        m_paragraphNumber = paragraphNumber;
    }

    /**
     * Returns true if the bread crumb navigation should be shown.<p>
     * 
     * This method has to be called after the method {@link CmsTemplateModules#buildHtmlNavList(int, String)}.<p> 
     * 
     * @return true if the bread crumb navigation should be shown
     */
    public boolean showNavBreadCrumb() {

        return hasCategoryFolders() || !getRequest().getParameter(PARAM_FOLDER).equals(getCategoryFolder());
    }

    /**
     * Creates Html code in new format for one image.<p>
     * 
     *  @param cmsObject Current CmsObject
     *  @param xPath Path to current xml node
     *  @param xmlContent Current Xml content
     *  @param locale Current locale
     *  @param hide True, if current image is not to show
     *  @param textContent Text content
     *  
     *  @return Html code in new format for one image
     */
    private StringBuffer createHtmlCodePerImage(
        CmsObject cmsObject,
        String xPath,
        CmsXmlContent xmlContent,
        Locale locale,
        boolean hide,
        String textContent) {

        // process optional image
        String imgDesc = "";
        String paragraphType = "";
        StringBuffer imgTagBuf = new StringBuffer(1024);
        String imgUri = xmlContent.getStringValue(getCmsObject(), xPath, locale);
        Map imgParams = new HashMap();
        if (CmsStringUtil.isNotEmpty(imgUri)) {
            int pos = imgUri.indexOf(CmsRequestUtil.URL_DELIMITER);
            if (pos >= 0) {
                imgParams = CmsRequestUtil.createParameterMap(imgUri.substring(pos + 1));
            }
            // remove eventual parameters
            imgUri = CmsRequestUtil.getRequestLink(imgUri);
        }

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(imgUri)) {
            imgUri = "";
        }
        // main image uri for image group
        if (!hide) {
            setMainImageUri(imgUri);
        }

        String imgSize = null;
        try {
            imgSize = getCmsObject().readPropertyObject(imgUri, CmsPropertyDefinition.PROPERTY_IMAGE_SIZE, false).getValue();
        } catch (CmsException e) {
            // file property not found, ignore
        }
        if (imgSize != null) {
            // image exists, create image tag to show
            String crop = null;
            // get description from parameter value
            String[] descParam = (String[])imgParams.get(CmsXmlVfsImageValue.PARAM_DESCRIPTION);
            if (descParam != null) {
                imgDesc = CmsEncoder.unescape(descParam[0], CmsEncoder.ENCODING_UTF_8);
            }
            String[] typeParam = (String[])imgParams.get(CmsXmlVfsImageValue.PARAM_FORMAT);
            if (typeParam != null) {
                paragraphType = typeParam[0];
            }
            String[] cropParam = (String[])imgParams.get(CmsImageScaler.PARAM_SCALE);
            if (cropParam != null) {
                crop = cropParam[0];
            }
            // image alignment
            setImageAlignment(paragraphType);

            // get initialized image scaler for the image                   
            CmsImageScaler scaler = getImageScaler(paragraphType, imgSize, crop);
            scaler.setQuality(75);
            scaler.setRenderMode(Simapi.RENDER_QUALITY);
            // show image or image in photo gallery
            imgTagBuf = createHtmlTagToShowImageInPhotoGallery(
                cmsObject,
                imgUri,
                imgDesc,
                scaler,
                hide,
                getMainImageUri(),
                textContent);
        }
        return imgTagBuf;
    }

    /**
     * Create single HTML tag to show image in photo gallery.<p>
     * 
     * @param cmsResource Current resource
     * @param imgDescr Image description
     * @param mainImgUri Main image URI
     * @param imgUri Current image URI
     * @param scaler OpenCms scaler
     * @param cmsObject Current CmsObject
     * @param hide True if to hide image 
     * @param textContent Text context
     * 
     * @return Single HTML tag to show image in photo gallery
     */
    private StringBuffer createHtmlTagForNewImageInTopMessage(
        CmsResource cmsResource,
        String imgDescr,
        String imgUri,
        String imgClass,
        CmsImageScaler scaler,
        CmsObject cmsObject) {

        StringBuffer htmlImgTagBuf = new StringBuffer(512);
        String imgLinkWithScaleParams = OpenCms.getLinkManager().substituteLink(
            cmsObject,
            imgUri + scaler.toRequestParam());

        if (CmsStringUtil.isEmpty(imgClass)) {
            imgClass = "right";
        }

        //htmlImgTagBuf.append("<p>");
        htmlImgTagBuf.append("<img class=\"").append(imgClass).append("\" src=\"").append(imgLinkWithScaleParams);
        htmlImgTagBuf.append("\" alt=\"").append(imgDescr).append("\"");
        htmlImgTagBuf.append("\" title=\"").append(imgDescr).append("\"");
        htmlImgTagBuf.append(" border=\"0\" width=\"").append(scaler.getWidth()).append("\"");
        htmlImgTagBuf.append(" height=\"").append(scaler.getHeight()).append("\"");
        htmlImgTagBuf.append(" align=\"top\" /> \n");
        return htmlImgTagBuf;
    }

    /**
     * Create HTML tag to show image in photo gallery in event module.<p>
     * 
     * @param cmsObject Current CmsObject
     * @param imgUri Image uri
     * @param imgDescr Image description
     * @param scaler Image scaler
     * @param hide True if hide current image
     * @param mainImageUri Image uri from first picture
     * @param textContent Text content
     * 
     * @return HTML tag to show image in photo gallery
     */
    private StringBuffer createHtmlTagToShowImageInPhotoGallery(
        CmsObject cmsObject,
        String imgUri,
        String imgDescr,
        CmsImageScaler scaler,
        boolean hide,
        String mainImageUri,
        String textContent) {

        StringBuffer htmlImgTagBuf = new StringBuffer(512);

        try {
            CmsResource cmsMainImageResource = cmsObject.readResource(imgUri);
            //String imgName = cmsMainImageResource.getName();
            StringBuffer singleHmtlTag = createSingleHtmlTagToShowImageInPhotoGallery(
                cmsMainImageResource,
                imgDescr,
                mainImageUri,
                imgUri,
                scaler,
                cmsObject,
                hide,
                textContent);
            htmlImgTagBuf.append(singleHmtlTag);
        } catch (CmsException e) {
            // ignore
        }
        return htmlImgTagBuf;
    }

    /**
     * Create single Html tag to show image in photo gallery.<p>
     * 
     * @param cmsResource Current resource
     * @param imgDescr Image description
     * @param mainImgUri Main image URI
     * @param imgUri Current image URI
     * @param scaler OpenCms scaler
     * @param cmsObject Current CmsObject
     * @param hide True if to hide image 
     * @param textContent Text context
     * 
     * @return Single Html tag to show image in photo gallery
     */
    private StringBuffer createSingleHtmlTagToShowImageInPhotoGallery(
        CmsResource cmsResource,
        String imgDescr,
        String mainImgUri,
        String imgUri,
        CmsImageScaler scaler,
        CmsObject cmsObject,
        boolean hide,
        String textContent) {

        StringBuffer htmlImgTagBuf = new StringBuffer(512);
        String imgLink = OpenCms.getLinkManager().substituteLink(cmsObject, imgUri);
        String imgLinkWithScaleParams = OpenCms.getLinkManager().substituteLink(
            cmsObject,
            imgUri + scaler.toRequestParam());
        String imgPropertyTitle = ""; //getTitleFromImageToShowImageInPhotoGallery(cmsObject, cmsResource);

        if (!hide) {
            htmlImgTagBuf.append("<p> \n");
        }
        htmlImgTagBuf.append("<a href=\"").append(imgLink);
        if (!CmsStringUtil.isEmpty(imgDescr)) {
            htmlImgTagBuf.append("\" title=\"").append(imgDescr);
        } else {
            htmlImgTagBuf.append("\" title=\"").append(imgPropertyTitle);
        }
        htmlImgTagBuf.append("\" class=\"thickbox\" rel=\"").append(mainImgUri);

        if (getImageAlignment().equals(CmsTemplateModules.IMAGE_RIGHT)) {
            htmlImgTagBuf.append("\" ><img class=\"right\" src=\"").append(imgLinkWithScaleParams);
        } else {
            htmlImgTagBuf.append("\" ><img class=\"left\" src=\"").append(imgLinkWithScaleParams);
        }
        htmlImgTagBuf.append("\" alt=\"").append(imgDescr).append("\" title=\"").append(imgDescr).append("\"");
        htmlImgTagBuf.append(" border=\"0\" width=\"").append(scaler.getWidth()).append("\"");
        htmlImgTagBuf.append(" height=\"").append(scaler.getHeight()).append("\"");
        if (hide) {
            htmlImgTagBuf.append(" style=\"display:none;\"");
        }
        htmlImgTagBuf.append("/>").append("</a> \n");
        if (CmsStringUtil.isNotEmpty(textContent)) {
            htmlImgTagBuf.append(textContent);
        }
        return htmlImgTagBuf;
    }

    /**
     * Creates HTML code in new format for one image in top message.<p>
     * 
     *  @param cmsObject Current CmsObject
     *  @param xPath Path to current xml node
     *  @param xmlContent Current Xml content
     *  @param locale Current locale
     *  @param file Uri to current top message
     *  @param textContent Text content
     *  
     *  @return HTML code in new format for one image
     */
    private StringBuffer getHtmlTagForNewImageInTopMessage(
        CmsObject cmsObject,
        String xPath,
        CmsXmlContent xmlContent,
        Locale locale,
        int imgWidth,
        int imgHeight,
        String imgClass) {

        // process optional image
        String imgDesc = "";
        String paragraphType = "";
        StringBuffer imgTagBuf = new StringBuffer(1024);
        String imgUri = xmlContent.getStringValue(getCmsObject(), xPath, locale);
        Map imgParams = new HashMap();
        if (CmsStringUtil.isNotEmpty(imgUri)) {
            int pos = imgUri.indexOf(CmsRequestUtil.URL_DELIMITER);
            if (pos >= 0) {
                imgParams = CmsRequestUtil.createParameterMap(imgUri.substring(pos + 1));
            }
            // remove eventual parameters
            imgUri = CmsRequestUtil.getRequestLink(imgUri);
        }

        if (CmsStringUtil.isEmptyOrWhitespaceOnly(imgUri)) {
            imgUri = "";
        }

        String imgSize = null;
        try {
            imgSize = getCmsObject().readPropertyObject(imgUri, CmsPropertyDefinition.PROPERTY_IMAGE_SIZE, false).getValue();
        } catch (CmsException e) {
            // file property not found, ignore
        }
        if (imgSize != null) {
            // image exists, create image tag to show
            String crop = null;
            // get description from parameter value
            String[] descParam = (String[])imgParams.get(CmsXmlVfsImageValue.PARAM_DESCRIPTION);
            if (descParam != null) {
                imgDesc = CmsEncoder.unescape(descParam[0], CmsEncoder.ENCODING_UTF_8);
            }
            String[] typeParam = (String[])imgParams.get(CmsXmlVfsImageValue.PARAM_FORMAT);
            if (typeParam != null) {
                paragraphType = typeParam[0];
            }
            String[] cropParam = (String[])imgParams.get(CmsImageScaler.PARAM_SCALE);
            if (cropParam != null) {
                crop = cropParam[0];
            }
            // image alignment
            setImageAlignment(paragraphType);

            // get initialized image scaler for the image                   
            CmsImageScaler scaler = getImageScalerForImagesInTopMessages(
                paragraphType,
                imgSize,
                imgWidth,
                imgHeight,
                crop);
            scaler.setQuality(75);
            scaler.setRenderMode(Simapi.RENDER_QUALITY);
            try {
                CmsResource cmsMainImageResource = cmsObject.readResource(imgUri);
                //String imgName = cmsMainImageResource.getName();
                StringBuffer singleHmtlTag = createHtmlTagForNewImageInTopMessage(
                    cmsMainImageResource,
                    imgDesc,
                    imgUri,
                    imgClass,
                    scaler,
                    cmsObject);
                imgTagBuf.append(singleHmtlTag);
            } catch (CmsException e) {
                // ignore
            }
        }
        return imgTagBuf;
    }

    /**
     * Returns an initialized image scaler depending on the image align to use.<p>
     * 
     * @param paragraphType the paragraph type to show
     * @param imgSize the image size property value containing the original image information
     * @param crop the crop scale parameters
     * 
     * @return an initialized image scaler depending on the image align to use
     */
    private CmsImageScaler getImageScaler(String paragraphType, String imgSize, String crop) {

        int imgWidth = 250;

        // create scaler instance of original image
        CmsImageScaler origImage = new CmsImageScaler(imgSize);
        // create scaler with desired image width
        CmsImageScaler scaler = new CmsImageScaler();
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(crop)) {
            origImage = new CmsImageScaler(crop);
            scaler = new CmsImageScaler(crop);
        }
        scaler.setWidth(imgWidth);
        scaler.setType(3);
        // return scaler with result image width
        return origImage.getWidthScaler(scaler);
    }

    /**
     * Returns an initialized image scaler depending on the image align to use.<p>
     * 
     * @param paragraphType the paragraph type to show
     * @param imgSize the image size property value containing the original image information
     * @param crop the crop scale parameters
     * 
     * @return an initialized image scaler depending on the image align to use
     */
    private CmsImageScaler getImageScalerForImagesInTopMessages(
        String paragraphType,
        String imgSize,
        int imgMaxWidth,
        int imgMaxHeight,
        String crop) {

        int imgWidth = 100;
        if (imgMaxWidth > 0) {
            imgWidth = imgMaxWidth;
        }
        int imgHeight = 75;
        if (imgMaxHeight > 0) {
            imgHeight = imgMaxHeight;
        }

        // create scaler instance of original image
        CmsImageScaler origImage = new CmsImageScaler(imgSize);
        // create scaler with desired image width
        CmsImageScaler scaler = new CmsImageScaler();
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(crop)) {
            origImage = new CmsImageScaler(crop);
            scaler = new CmsImageScaler(crop);
        }

        scaler.setType(3);

        float factorWidth = (float)imgWidth / (float)origImage.getWidth();
        float factorHeight = (float)imgHeight / (float)origImage.getHeight();

        if ((factorWidth < 1) && (factorWidth <= factorHeight)) {
            scaler.setWidth(imgWidth);
            // return scaler with result image width
            return origImage.getWidthScaler(scaler);
        } else if ((factorHeight < 1) && (factorHeight < factorWidth)) {
            // only image height is fixed
            int width = 0;
            int height = 0;
            if (origImage.getHeight() > imgHeight) {
                // height is too large, re-calculate height
                float scale = (float)imgHeight / (float)origImage.getHeight();
                width = Math.round(origImage.getWidth() * scale);
                height = imgHeight;
            } else {
                // height is ok
                width = origImage.getWidth();
                height = origImage.getHeight();
            }
            scaler.setHeight(height);
            scaler.setWidth(width);
            return scaler;
        } else {
            scaler.setWidth(imgWidth);
            return origImage.getWidthScaler(scaler);
        }
    }

}