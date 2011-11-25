/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.htmlcleaner/src/com/alkacon/opencms/v8/htmlcleaner/CmsHtmlCleaner.java,v $
 * Date   : $Date: 2011/04/01 10:08:03 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2011 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.htmlcleaner;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.HTMLElements;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;

import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.module.I_CmsModuleAction;
import org.opencms.util.A_CmsHtmlConverter;
import org.opencms.util.CmsHtmlConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * HTML cleaner that removes invalid tags and attributes from HTML.<p>
 * 
 * Additionally, tags can be replaced with other tags.<p>
 * 
 * Configuration is done in the OpenCms VFS using the file
 * "/system/modules/com.alkacon.opencms.v8.htmlcleaner/configuration/config.xml".<p>
 * 
 * <b>Note</b>: only the Online version of the configuration file is used by the cleaner,
 * be sure to publish changes on it immediately.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsHtmlCleaner extends A_CmsHtmlConverter {

    /** Parameter value for "clean tags" mode. */
    public static final String PARAM_CLEANTAGS = "cleantags";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsHtmlCleaner.class);

    /** List of default modes if none were specified explicitly. */
    private static final List<String> MODES_DEFAULT = Collections.unmodifiableList(Arrays.asList(new String[] {PARAM_CLEANTAGS}));

    /** The VFS path to the configuration file for the cleaner. */
    private static final String MODULE_NAME = CmsHtmlCleaner.class.getPackage().getName();

    /** Marker used for valid tags. */
    private static final Boolean VALID_MARKER = Boolean.TRUE;

    /** The configuration for the cleaner, only use {@link #getCleanerConfiguration()} to get the configuration. */
    private CmsHtmlCleanerConfiguration m_cleanerConfig;

    /**
     * Constructor, creates a new CmsHtmlCleaner.<p>
     */
    public CmsHtmlCleaner() {

        super(null, MODES_DEFAULT);
    }

    /**
     * Constructor, creates a new CmsHtmlCleaner.<p>
     * 
     * Possible values for the conversion mode are:
     * <ul>
     * <li>{@link #PARAM_CLEANTAGS}: Tags are removed, other tags are replaced and unwanted attributes omitted.
     * </ul>
     * 
     * @param encoding the encoding used for the HTML code conversion
     * @param modes the conversion modes to use
     */
    public CmsHtmlCleaner(String encoding, List<String> modes) {

        super(encoding, modes);
    }

    /**
     * @see org.opencms.util.A_CmsHtmlConverter#convertToString(java.lang.String)
     */
    @Override
    public String convertToString(String htmlInput) {

        if (getModes().contains(PARAM_CLEANTAGS) || getModes().contains(CmsHtmlConverter.PARAM_XHTML)) {
            htmlInput = cleanHtml(htmlInput);
        }
        return htmlInput;
    }

    /**
     * Cleans the HTML using the cleaner configuration found in the module.<p>
     * 
     * @param htmlInput the HTML input stored in a string
     * 
     * @return the cleaned up HTML string
     */
    private String cleanHtml(String htmlInput) {

        // initialize source and output
        Source source = new Source(htmlInput);
        source.fullSequentialParse();
        OutputDocument outputDocument = new OutputDocument(source);
        List<Tag> tags = source.getAllTags();

        // iterate all tags found in source document
        for (Iterator<Tag> i = tags.iterator(); i.hasNext();) {
            Tag tag = i.next();
            if (processTag(tag, outputDocument)) {
                // tag is valid, mark it
                tag.setUserData(VALID_MARKER);
            } else {
                // tag is not valid, remove it from output
                outputDocument.remove(tag);
            }
        }

        // format the output that it can be read more easily, e.g. when editing the control code
        Source formatSource = new Source(outputDocument.toString());
        SourceFormatter formatter = new SourceFormatter(formatSource);
        formatter.setIndentString("    ");
        return formatter.toString();
    }

    /**
     * Returns the configuration for the cleaner, uses lazy initializing.<p>
     * 
     * @return the configuration for the cleaner
     */
    private CmsHtmlCleanerConfiguration getCleanerConfiguration() {

        if (m_cleanerConfig == null) {
            // read configuration using the module action class
            I_CmsModuleAction actionClass = OpenCms.getModuleManager().getModule(MODULE_NAME).getActionInstance();
            if (actionClass instanceof CmsHtmlCleanerModuleAction) {
                try {
                    m_cleanerConfig = ((CmsHtmlCleanerModuleAction)actionClass).getCmsHtmlCleanerConfiguration();
                } catch (CmsException e) {
                    LOG.error("HTML cleaner configuration file not found", e);
                }
            }
        }
        return m_cleanerConfig;
    }

    /**
     * Returns the HTML for the end tag.<p>
     * 
     * @param tagName the tag name to create the HTML for
     * 
     * @return the HTML for the end tag
     */
    private String getEndTagHtml(String tagName) {

        if (getCleanerConfiguration().getReplaceElements().containsKey(tagName)) {
            // tag has to be replaced, get replace tag name
            tagName = getCleanerConfiguration().getReplaceElements().get(tagName);
        }
        return "</" + tagName + '>';
    }

    /**
     * Returns the HTML for the start tag.<p>
     * 
     * @param tagName the tag name to create the HTML for
     * 
     * @return the HTML for the start tag
     */
    private CharSequence getStartTagHtml(StartTag startTag) {

        // tidies and filters out unwanted attributes 
        StringBuilder sb = new StringBuilder(64);
        String tagName = startTag.getName();
        if (getCleanerConfiguration().getReplaceElements().containsKey(tagName)) {
            // tag has to be replaced, get replace tag name to use
            tagName = getCleanerConfiguration().getReplaceElements().get(tagName);
        }
        sb.append('<').append(tagName);
        for (Iterator<Attribute> i = startTag.getAttributes().iterator(); i.hasNext();) {
            Attribute attribute = i.next();
            if (getCleanerConfiguration().isKeepAttributeOnTag(tagName, attribute.getKey(), attribute.getValue())) {
                // only create attribute if it is not configured to be removed or tag name is an exception from removal rules
                sb.append(' ').append(attribute.getName());
                if (attribute.getValue() != null) {
                    sb.append("=\"");
                    sb.append(CharacterReference.encode(attribute.getValue()));
                    sb.append('"');
                }
            }
        }
        if ((startTag.getElement().getEndTag() == null)
            && !HTMLElements.getEndTagOptionalElementNames().contains(startTag.getName())) {
            sb.append(" /");
        }
        sb.append('>');
        return sb;
    }

    /**
     * Checks if <code>&lt;li&gt;</code> tags are valid.<p>
     * 
     * @param tag the tag to check
     * 
     * @return <code>true</code> if the tag is a valid <code>&lt;li&gt;</code> tag, otherwise <code>false</code>
     */
    private boolean isValidLITag(Tag tag) {

        Element parentElement = tag.getElement().getParentElement();
        if (parentElement == null) {
            // ignore LI elements without a parent
            return false;
        }
        if (parentElement.getStartTag().getUserData() != VALID_MARKER) {
            // ignore LI elements where the parent is not valid
            return false;
        }
        // only accept LI tags where the immediate parent is UL or OL
        return (parentElement.getName() == HTMLElementName.UL) || (parentElement.getName() == HTMLElementName.OL);
    }

    /**
     * Processes a single tag of the document and inserts it in the output document, if valid.<p>
     * 
     * @param tag the tag to process
     * @param outputDocument the output document that is build as result
     * 
     * @return <code>true</code> if the tag is valid and has been inserted in the output document, otherwise <code>false</code>
     */
    private boolean processTag(Tag tag, OutputDocument outputDocument) {

        String elementName = tag.getName();

        if (!getCleanerConfiguration().getValidElementNames().contains(elementName)
            && !getCleanerConfiguration().getReplaceElements().containsKey(elementName)
            && (tag.getTagType() != StartTagType.COMMENT)) {
            // element is not valid, not an element to be replaced and no comment, it will be removed
            return false;
        }

        if (tag.getTagType() == StartTagType.NORMAL) {
            Element element = tag.getElement();
            if (HTMLElements.getEndTagRequiredElementNames().contains(elementName)) {
                if (element.getEndTag() == null) {
                    // reject start tag if its required end tag is missing
                    return false;
                }
            } else if (HTMLElements.getEndTagOptionalElementNames().contains(elementName)) {
                if ((elementName == HTMLElementName.LI) && !isValidLITag(tag)) {
                    // reject invalid LI tags
                    return false;
                }
                if (element.getEndTag() == null) {
                    // insert optional end tag if it is missing
                    outputDocument.insert(element.getEnd(), getEndTagHtml(elementName));
                }
            }
            outputDocument.replace(tag, getStartTagHtml(element.getStartTag()));
        } else if (tag.getTagType() == EndTagType.NORMAL) {
            if (tag.getElement() == null) {
                // reject end tags that are not associated with a start tag
                return false;
            }
            if ((elementName == HTMLElementName.LI) && !isValidLITag(tag)) {
                // reject invalid LI tags
                return false;
            }
            outputDocument.replace(tag, getEndTagHtml(elementName));
        } else if (tag.getTagType() != StartTagType.COMMENT) {
            // reject all other tags that are no comments
            return false;
        }

        // for valid tags, return true
        return true;
    }

}
