/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.feeder/src/com/alkacon/opencms/v8/feeder/CmsFeedXmlContentHandler.java,v $
 * Date   : $Date: 2007/12/13 15:48:47 $
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

package com.alkacon.opencms.v8.feeder;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsRuntimeException;
import org.opencms.util.CmsFileUtil;
import org.opencms.xml.CmsXmlContentDefinition;
import org.opencms.xml.CmsXmlEntityResolver;
import org.opencms.xml.CmsXmlException;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsDefaultXmlContentHandler;
import org.opencms.xml.content.Messages;
import org.opencms.xml.types.I_CmsXmlSchemaType;

import java.util.Collections;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Adds syndication feed mapping rules to the default XML content handler.<p>
 * 
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsFeedXmlContentHandler extends CmsDefaultXmlContentHandler {

    /** Constant for the "feedrule" element name. */
    public static final String APPINFO_FEEDRULE = "feedrule";

    /** Constant for the "feedrules" element name. */
    public static final String APPINFO_FEEDRULES = "feedrules";

    /** Constant for the "default" attribute. */
    public static final String ATTR_DEFAULT = "default";

    /** Constant for the "maxLength" attribute. */
    public static final String ATTR_MAXLENGTH = "maxLength";

    /** The path to the Alkacon OpenCms Feeder package (calculated from the package name). */
    public static final String FEED_PACKAGE_PATH = CmsFeed.class.getPackage().getName().replace('.', '/');

    /** The file where the extended feed schema is located. */
    public static final String FEED_APPINFO_SCHEMA_FILE = FEED_PACKAGE_PATH + "/FeedAppinfo.xsd";

    /** The XML system id for the extended feed schema. */
    public static final String FEED_APPINFO_SCHEMA_SYSTEM_ID = CmsConfigurationManager.DEFAULT_DTD_PREFIX
        + FEED_APPINFO_SCHEMA_FILE;

    /** The feed mapping this class is all about. */
    protected CmsFeedContentMapping m_feedMapping;

    /**
     * Static initializer for caching the extended feed validation schema.<p>
     */
    static {

        // the schema definition is located in a separate file for easier editing
        byte[] appinfoSchema;
        try {
            appinfoSchema = CmsFileUtil.readFile(FEED_APPINFO_SCHEMA_FILE);
        } catch (Exception e) {
            throw new CmsRuntimeException(Messages.get().container(
                org.opencms.xml.types.Messages.ERR_XMLCONTENT_LOAD_SCHEMA_1,
                FEED_APPINFO_SCHEMA_FILE), e);
        }
        CmsXmlEntityResolver.cacheSystemId(FEED_APPINFO_SCHEMA_SYSTEM_ID, appinfoSchema);
    }

    /**
     * Returns the initialized feed mapping for this XML content handler.<p>
     * 
     * @return the initialized feed mapping for this XML content handler
     */
    public CmsFeedContentMapping getFeedMapping() {

        return m_feedMapping;
    }

    /**
     * @see org.opencms.xml.content.CmsDefaultXmlContentHandler#initialize(org.dom4j.Element, org.opencms.xml.CmsXmlContentDefinition)
     */
    public synchronized void initialize(Element appInfoElement, CmsXmlContentDefinition contentDefinition)
    throws CmsXmlException {

        if (appInfoElement != null) {
            // first initialize all the default values
            super.initialize(appInfoElement, contentDefinition);

            Element feedElement = appInfoElement.element(APPINFO_FEEDRULES);
            if (feedElement != null) {
                initFeedRules(feedElement, contentDefinition);
            }
        }
    }

    /**
     * Adds a feed rule mapping.<p>
     * 
     * @param contentDefinition the XML content definition this XML content handler belongs to
     * @param xmlField the element name to map to the feed
     * @param feedField the feed field to map the XML field to
     * @param maxLength the optional max length of the field
     * @param defaultValue the optional default value for the mapping
     * 
     * @throws CmsXmlException in case an unknown element name is used
     */
    protected void addFeedRule(
        CmsXmlContentDefinition contentDefinition,
        String xmlField,
        String feedField,
        String maxLength,
        String defaultValue) throws CmsXmlException {

        if (contentDefinition.getSchemaType(xmlField) == null) {
            throw new CmsXmlException(
                Messages.get().container(Messages.ERR_XMLCONTENT_INVALID_ELEM_MAPPING_1, xmlField));
        }

        m_feedMapping.addFeedFieldMapping(
            Collections.singletonList(CmsXmlUtils.createXpath(xmlField, 1)),
            feedField,
            maxLength,
            defaultValue);
    }

    /**
     * @see org.opencms.xml.content.CmsDefaultXmlContentHandler#init()
     */
    protected void init() {

        super.init();
        m_feedMapping = new CmsFeedContentMapping();
    }

    /**
     * Initializes the feed rule mappings for this content handler.<p>
     * 
     * @param root the "feedrules" element from the appinfo node of the XML content definition
     * @param contentDefinition the content definition the mappings belong to
     * 
     * @throws CmsXmlException if something goes wrong
     */
    protected void initFeedRules(Element root, CmsXmlContentDefinition contentDefinition) throws CmsXmlException {

        Iterator i = root.elementIterator(APPINFO_FEEDRULE);
        while (i.hasNext()) {
            // iterate all "mapping" elements in the "mappings" node
            Element element = (Element)i.next();
            // this is a mapping node
            String xmlField = element.attributeValue(APPINFO_ATTR_ELEMENT);
            String maptoName = element.attributeValue(APPINFO_ATTR_MAPTO);
            String maxLength = element.attributeValue(ATTR_MAXLENGTH);
            String defaultValue = element.attributeValue(ATTR_DEFAULT);
            if ((xmlField != null) && (maptoName != null)) {
                // add the element mapping 
                addFeedRule(contentDefinition, xmlField, maptoName, maxLength, defaultValue);
            }
        }
    }

    /**
     * @see org.opencms.xml.content.CmsDefaultXmlContentHandler#validateAppinfoElement(org.dom4j.Element)
     */
    protected void validateAppinfoElement(Element appinfoElement) throws CmsXmlException {

        // create a document to validate
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement(APPINFO_APPINFO);
        // attach the default appinfo schema
        root.add(I_CmsXmlSchemaType.XSI_NAMESPACE);
        root.addAttribute(I_CmsXmlSchemaType.XSI_NAMESPACE_ATTRIBUTE_NO_SCHEMA_LOCATION, FEED_APPINFO_SCHEMA_SYSTEM_ID);
        // append the content from the appinfo node in the content definition 
        root.appendContent(appinfoElement);
        // now validate the document with the default appinfo schema
        CmsXmlUtils.validateXmlStructure(doc, CmsEncoder.ENCODING_UTF_8, new CmsXmlEntityResolver(null));
    }
}
