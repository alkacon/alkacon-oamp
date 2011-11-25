/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.htmlcleaner/src/com/alkacon/opencms/v8/htmlcleaner/CmsHtmlCleanerConfiguration.java,v $
 * Date   : $Date: 2011/04/01 10:08:02 $
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

import org.opencms.file.CmsObject;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;

/**
 * The configuration for the HTML cleaner.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsHtmlCleanerConfiguration {

    /** Value for tag name indicating that all tags should be investigated. */
    protected static final String TAGS_ALL = "*";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsHtmlCleanerConfiguration.class);

    /** The node name of the "AttributeName" node. */
    private static final String N_ATTRIBUTENAME = "AttributeName";

    /** The node name of the "AttributeValue" node. */
    private static final String N_ATTRIBUTEVALUE = "AttributeValue";

    /** The node name of the "ExcludeTag" node. */
    private static final String N_EXCLUDETAG = "ExcludeTag";

    /** The node name of the "IgnoreTag" node. */
    private static final String N_IGNORETAG = "IgnoreTag";

    /** The node name of the "KeepTag" node. */
    private static final String N_KEEPTAG = "KeepTag";

    /** The node name of the "KeepTags" node. */
    private static final String N_KEEPTAGS = "KeepTags";

    /** The node name of the "RemoveAttribute" node. */
    private static final String N_REMOVEATTRIBUTE = "RemoveAttribute";

    /** The node name of the "RemoveAttributes" node. */
    private static final String N_REMOVEATTRIBUTES = "RemoveAttributes";

    /** The node name of the "ReplaceTag" node. */
    private static final String N_REPLACETAG = "ReplaceTag";

    /** The node name of the "ReplaceTags" node. */
    private static final String N_REPLACETAGS = "ReplaceTags";

    /** The node name of the "TagName" node. */
    private static final String N_TAGNAME = "TagName";

    /** The node name of the "TagReplace" node. */
    private static final String N_TAGREPLACE = "TagReplace";

    /** The map of attributes with exclusion definitions as values to remove from the tags. */
    private Map<String, Map<String, Pattern>> m_invalidAttributes;

    /** The list of elements on which to keep the invalid attributes. */
    private List<String> m_keepAttributeElements;

    /** The map of elements to replace. */
    private Map<String, String> m_replaceElements;

    /** The list of element names to keep in the result. */
    private List<String> m_validElementNames;

    /**
     * Empty constructor, the {@link #init(CmsObject, CmsXmlContent)} method has to be triggered manually after generating an instance.<p> 
     */
    public CmsHtmlCleanerConfiguration() {

        // nothing to do here
    }

    /**
     * Constructor, with parameters, that initializes the cleaner configuration.<p>
     * 
     * @param cms the current users context
     * @param content the configuration as XML content
     */
    public CmsHtmlCleanerConfiguration(CmsObject cms, CmsXmlContent content) {

        init(cms, content);
    }

    /**
     * Returns the map of attributes to remove from the tags.<p>
     * 
     * The keys are the names of the attributes,
     * values are maps of eventual exclusion definitions or <code>null</code> if there are no exclusions defined.<p>
     * 
     * @return the list of attributes to remove from the tags
     */
    public Map<String, Map<String, Pattern>> getInvalidAttributes() {

        return m_invalidAttributes;
    }

    /**
     * Returns the list of elements on which to keep the invalid attributes.<p>
     * 
     * @return the list of elements on which to keep the invalid attributes
     */
    public List<String> getKeepAttributeElements() {

        return m_keepAttributeElements;
    }

    /**
     * Returns the map of elements to replace.<p>
     * 
     * @return the map of elements to replace
     */
    public Map<String, String> getReplaceElements() {

        return m_replaceElements;
    }

    /**
     * Returns the list of element names to keep in the result.<p>
     * 
     * @return the list of element names to keep in the result
     */
    public List<String> getValidElementNames() {

        return m_validElementNames;
    }

    /**
     * Initializes the cleaner configuration using the given XML content.<p>
     * 
     * @param cms the current users context
     * @param content the configuration as XML content
     */
    public void init(CmsObject cms, CmsXmlContent content) {

        // initialize members
        m_validElementNames = new ArrayList<String>();
        m_replaceElements = new HashMap<String, String>();
        m_invalidAttributes = new HashMap<String, Map<String, Pattern>>();
        m_keepAttributeElements = new ArrayList<String>();

        // use first locale found in content for configuration
        Locale locale = content.getLocales().get(0);

        // get the tags to keep
        List<I_CmsXmlContentValue> keepTags = content.getValues(N_KEEPTAGS + "[1]/" + N_KEEPTAG, locale);
        for (Iterator<I_CmsXmlContentValue> i = keepTags.iterator(); i.hasNext();) {
            I_CmsXmlContentValue value = i.next();
            String tagName = value.getStringValue(cms);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(tagName)) {
                m_validElementNames.add(tagName.toLowerCase());
            }
        }

        // get the tags to replace
        List<I_CmsXmlContentValue> replaceTags = content.getValues(N_REPLACETAGS + "[1]/" + N_REPLACETAG, locale);
        for (Iterator<I_CmsXmlContentValue> i = replaceTags.iterator(); i.hasNext();) {
            I_CmsXmlContentValue value = i.next();
            String path = value.getPath() + "/";
            String tagName = content.getStringValue(cms, path + N_TAGNAME, locale);
            String tagReplace = content.getStringValue(cms, path + N_TAGREPLACE, locale);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(tagName)
                && CmsStringUtil.isNotEmptyOrWhitespaceOnly(tagReplace)) {
                m_replaceElements.put(tagName.toLowerCase(), tagReplace.toLowerCase());
            }
        }

        // get the attributes to remove
        List<I_CmsXmlContentValue> removeAttributes = content.getValues(
            N_REMOVEATTRIBUTES + "[1]/" + N_REMOVEATTRIBUTE,
            locale);
        for (Iterator<I_CmsXmlContentValue> i = removeAttributes.iterator(); i.hasNext();) {
            I_CmsXmlContentValue value = i.next();
            String path = value.getPath() + "/";
            String attrName = content.getStringValue(cms, path + N_ATTRIBUTENAME, locale);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(attrName)) {
                Map<String, Pattern> exclusions = null;
                // check if there are individual exclusion sub elements
                List<I_CmsXmlContentValue> excludeTags = content.getValues(path + N_EXCLUDETAG, locale);
                if (excludeTags.size() > 0) {
                    // found at least one exclusion, create map
                    exclusions = new HashMap<String, Pattern>(excludeTags.size());
                    for (Iterator<I_CmsXmlContentValue> k = excludeTags.iterator(); k.hasNext();) {
                        // get exclusions for the attribute
                        I_CmsXmlContentValue excludeValue = k.next();
                        path = excludeValue.getPath() + "/";
                        String tagName = content.getStringValue(cms, path + N_TAGNAME, locale);
                        String attrValue = content.getStringValue(cms, path + N_ATTRIBUTEVALUE, locale);
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(tagName)
                            && CmsStringUtil.isNotEmptyOrWhitespaceOnly(attrValue)) {
                            try {
                                // build pattern from regular expression
                                Pattern regExPattern = Pattern.compile(attrValue);
                                exclusions.put(tagName.toLowerCase(), regExPattern);
                            } catch (PatternSyntaxException e) {
                                // invalid regular expression found
                                LOG.error(
                                    "Invalid regular expression \"" + attrValue + "\" defined for HTML cleaner",
                                    e);
                            }
                        }
                    }
                }
                m_invalidAttributes.put(attrName.toLowerCase(), exclusions);
            }
        }
        // get the tags where to keep the attributes
        List<I_CmsXmlContentValue> ignoreTags = content.getValues(N_REMOVEATTRIBUTES + "[1]/" + N_IGNORETAG, locale);
        for (Iterator<I_CmsXmlContentValue> i = ignoreTags.iterator(); i.hasNext();) {
            I_CmsXmlContentValue value = i.next();
            String strValue = value.getStringValue(cms);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(strValue)) {
                m_keepAttributeElements.add(strValue.toLowerCase());
            }
        }
    }

    /**
     * Determines if the given attribute should be kept or not on the given tag.<p>
     * 
     * @param tagName the name of the tag containing the attribute
     * @param attrName the attribute name
     * @param attrValue the attribute value
     * 
     * @return <code>true</code> if the attribute should be kept according to the configuration, otherwise <code>false</code>
     */
    public boolean isKeepAttributeOnTag(String tagName, String attrName, String attrValue) {

        if (getKeepAttributeElements().contains(tagName)) {
            // the attribute has to be kept because the tag should be ignored
            return true;
        }
        if (getInvalidAttributes().containsKey(attrName)) {
            // this is an invalid attribute, check exclusions
            Map<String, Pattern> exclusions = getInvalidAttributes().get(attrName);
            if ((exclusions != null) && CmsStringUtil.isNotEmptyOrWhitespaceOnly(attrValue)) {
                // found exclusions, check if the attribute matches one
                if (exclusions.containsKey(tagName)) {
                    Pattern regExPattern = exclusions.get(tagName);
                    if (regExPattern.matcher(attrValue).matches()) {
                        // attribute value matches regular expression, keep tag
                        return true;
                    }
                }
                // also check if an exclusion is defined for every tag
                if (exclusions.containsKey(TAGS_ALL)) {
                    Pattern regExPattern = exclusions.get(TAGS_ALL);
                    if (regExPattern.matcher(attrValue).matches()) {
                        // attribute value matches regular expression, keep tag
                        return true;
                    }
                }
            }
            // attribute found in invalid attribute definitions, no exclusions are matching, remove attribute
            return false;
        }
        // attribute not found in invalid attribute definitions, keep it
        return true;
    }

}
