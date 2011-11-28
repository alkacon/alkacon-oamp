/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.feeder/test/com/alkacon/opencms/v8/feeder/TestFeederWithXmlContents.java,v $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.CmsResourceTypeFolder;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.OpenCms;
import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;
import org.opencms.util.CmsFileUtil;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.xml.CmsXmlContentDefinition;
import org.opencms.xml.CmsXmlEntityResolver;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the feed generator with real life XML contents.<p>
 *
 * @author Alexander Kandzior
 *  
 * @version $Revision: 1.1 $
 */
public class TestFeederWithXmlContents extends OpenCmsTestCase {

    private static final String SCHEMA_SYSTEM_ID_1 = "http://www.opencms.org/xmlcontent-definition-1.xsd";
    private static final String SCHEMA_SYSTEM_ID_2 = "http://www.opencms.org/xmlcontent-definition-2.xsd";
    private static final String SCHEMA_SYSTEM_ID_FEED = "http://www.alkacon.com/xmlcontent-definition-feed.xsd";
    private static final String SCHEMA_SYSTEM_ID_FEED_MAPPINGS = "http://www.alkacon.com/xmlcontent-definition-feed-mappings.xsd";
    private static final String SCHEMA_SYSTEM_ID_FEED_IMAGE = "http://www.alkacon.com/xmlcontent-definition-feed-image.xsd";

    /** The path to the Alkacon OpenCms Feeder package (calculated from the package name). */
    private static final String PACKAGE_PATH = CmsFeedXmlContentHandler.FEED_PACKAGE_PATH;

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestFeederWithXmlContents(String arg0) {

        super(arg0);
    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        TestSuite suite = new TestSuite();
        suite.setName(TestFeederWithXmlContents.class.getName());

        suite.addTest(new TestFeederWithXmlContents("testFeedAppinfo"));
        suite.addTest(new TestFeederWithXmlContents("testSimpleFeedGeneration"));
        suite.addTest(new TestFeederWithXmlContents("testFeedSelectWidget"));
        suite.addTest(new TestFeederWithXmlContents("testFeedCreationFromXml"));
        suite.addTest(new TestFeederWithXmlContents("testFeedGenerationWithDefault"));

        TestSetup wrapper = new TestSetup(suite) {

            protected void setUp() {

                setupOpenCms("simpletest", "/sites/default/");
            }

            protected void tearDown() {

                removeOpenCms();
            }
        };

        return wrapper;
    }

    /**
     * Test case for creating a feed from an XML content using {@link CmsFeed} with default mappings.<p>
     * 
     * @throws Exception in case the test fails
     */
    public void testFeedGenerationWithDefault() throws Exception {

        CmsObject cms = getCmsObject();
        echo("Testing simple syndication feed generation from XML contents");

        Locale en = Locale.ENGLISH;

        // unmarshal content definition
        CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cms);
        String cd = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-definition-2.xsd", CmsEncoder.ENCODING_UTF_8);
        CmsXmlContentDefinition contentDefinition = CmsXmlContentDefinition.unmarshal(cd, SCHEMA_SYSTEM_ID_2, resolver);
        // store content definition in entity resolver
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_2, cd.getBytes(CmsEncoder.ENCODING_UTF_8));

        String feedArticlePath = "/feeditems2/";
        String feedArticleName = "item_";
        String feedArticleSuffix = ".xml";
        cms.createResource(feedArticlePath, CmsResourceTypeFolder.getStaticTypeId());

        // create a number of files using the right XML content
        int loopSize = 10;
        for (int i = 0; i < loopSize; i++) {
            CmsXmlContent content = CmsXmlContentFactory.createDocument(
                cms,
                en,
                CmsEncoder.ENCODING_UTF_8,
                contentDefinition);

            // title is mandantory in XSD
            I_CmsXmlContentValue title = content.getValue("Title2", en, 0);
            title.setStringValue(cms, "[2] This is the title of article " + i + " [2]");
            I_CmsXmlContentValue author = content.addValue(cms, "Author2", en, 0);
            author.setStringValue(cms, "[2] I am the author " + i + " [2]");
            I_CmsXmlContentValue text = content.addValue(cms, "Text2", en, 0);
            String textValue = "<h1>[2] This is text in article " + i + " [2]</h1>";
            for (int j = -1; j < i; j++) {
                textValue += "<p> Line " + (j + 2) + " with more text... </p>";
            }
            text.setStringValue(cms, textValue);

            // marshal the XML content
            byte[] bytes = content.marshal();
            String resourceName = feedArticlePath + feedArticleName + i + feedArticleSuffix;

            int type = OpenCms.getResourceManager().getResourceType("xmlcontent").getTypeId();
            // wait one second so timestamps are different
            Thread.sleep(1000);
            cms.createResource(resourceName, type, bytes, Collections.EMPTY_LIST);
            echo("Created resource: " + resourceName);
        }

        contentDefinition = initFeedSchema(cms);

        // generate the default XML feed mapping "on the fly"
        CmsXmlContent content = CmsXmlContentFactory.createDocument(
            cms,
            en,
            CmsEncoder.ENCODING_UTF_8,
            contentDefinition);

        // set feed values
        I_CmsXmlContentValue title = content.getValue(CmsFeed.NODE_TITLE, en, 0);
        assertNotNull(title);
        title.setStringValue(cms, "[2] This is the feed creation test [2]");
        I_CmsXmlContentValue type = content.getValue(CmsFeed.NODE_TYPE, en, 0);
        assertEquals("rss_2.0", type.getStringValue(cms));
        assertNull(content.getValue(CmsFeed.NODE_DESCRIPTION, en, 0));
        I_CmsXmlContentValue description = content.addValue(cms, CmsFeed.NODE_DESCRIPTION, en, 0);
        description.setStringValue(cms, "[2] This is the description of the test feed. [2]");
        assertNull(content.getValue(CmsFeed.NODE_COPYRIGHT, en, 0));
        I_CmsXmlContentValue copyright = content.addValue(cms, CmsFeed.NODE_COPYRIGHT, en, 0);
        copyright.setStringValue(cms, "[2] Alkacon Software GmbH [2]");
        I_CmsXmlContentValue collector = content.getValue(CmsFeed.NODE_COLLECTOR, en, 0);
        assertEquals("allInFolder", collector.getStringValue(cms));
        I_CmsXmlContentValue parameter = content.getValue(CmsFeed.NODE_PARAMETER, en, 0);
        assertNotNull(parameter);

        content.addValue(cms, CmsFeed.NODE_MAPPING, en, 0);
        I_CmsXmlContentValue maptype1 = content.getValue("Mapping[1]/Field", en, 0);
        maptype1.setStringValue(cms, CmsFeedContentMapping.FEED_TITLE);
        I_CmsXmlContentValue mapxml1 = content.getValue("Mapping[1]/XmlNode", en, 0);
        mapxml1.setStringValue(cms, "Title2");

        content.addValue(cms, CmsFeed.NODE_MAPPING, en, 1);
        I_CmsXmlContentValue maptype2 = content.getValue("Mapping[2]/Field", en, 0);
        maptype2.setStringValue(cms, CmsFeedContentMapping.FEED_DESCRIPTION);
        I_CmsXmlContentValue mapxml2 = content.getValue("Mapping[2]/XmlNode", en, 0);
        mapxml2.setStringValue(cms, "Text2");

        content.addValue(cms, CmsFeed.NODE_IMAGE, en, 0);
        I_CmsXmlContentValue imageTitle = content.getValue("Image[1]/Title", en, 0);
        imageTitle.setStringValue(cms, "The image title!");
        I_CmsXmlContentValue imageLink = content.getValue("Image[1]/Url", en, 0);
        imageLink.setStringValue(cms, "http://www.opencms.org/");
        I_CmsXmlContentValue imageUrl = content.getValue("Image[1]/Link", en, 0);
        imageUrl.setStringValue(cms, "http://www.opencms.org/image.gif");

        // collect 7 entries from type XML content
        parameter.setStringValue(cms, feedArticlePath + "|xmlcontent|7");

        // now marshal the XML content
        byte[] bytes = content.marshal();
        String resourceName = "/my_feed2.xml";
        int typeId = OpenCms.getResourceManager().getResourceType("xmlcontent").getTypeId();
        cms.createResource(resourceName, typeId, bytes, Collections.EMPTY_LIST);
        echo("Created resource: " + resourceName);

        // now read the resource and create the test feed
        CmsResource feedRes = cms.readResource(resourceName);
        CmsFile feedFile = cms.readFile(feedRes);
        CmsXmlContent feedContent = CmsXmlContentFactory.unmarshal(cms, feedFile);
        System.out.println(feedContent.toString());

        CmsFeed feed = new CmsFeed(cms, en, feedRes, feedContent);
        feed.init();
        feed.write(System.out);

        // another test with a different constructor
        CmsFeed feed2 = new CmsFeed(cms, resourceName);
        feed2.write(System.out);
    }

    /**
     * Initializes the XML content feed schema.<p>
     * 
     * @param cms the current OpenCms user context
     * 
     * @return the XML content feed schema content definition
     * 
     * @throws Exception in case something goes wrong
     */
    private CmsXmlContentDefinition initFeedSchema(CmsObject cms) throws Exception {

        // mappings subschema
        CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cms);
        String cd = CmsFileUtil.readFile(
            PACKAGE_PATH + "/xmlcontent-definition-feed-mappings.xsd",
            CmsEncoder.ENCODING_UTF_8);
        CmsXmlContentDefinition contentDefinition = CmsXmlContentDefinition.unmarshal(
            cd,
            SCHEMA_SYSTEM_ID_FEED_MAPPINGS,
            resolver);
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_FEED_MAPPINGS, cd.getBytes(CmsEncoder.ENCODING_UTF_8));

        // image subschema
        cd = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-definition-feed-image.xsd", CmsEncoder.ENCODING_UTF_8);
        contentDefinition = CmsXmlContentDefinition.unmarshal(cd, SCHEMA_SYSTEM_ID_FEED_IMAGE, resolver);
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_FEED_IMAGE, cd.getBytes(CmsEncoder.ENCODING_UTF_8));

        // main feed schema
        cd = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-definition-feed.xsd", CmsEncoder.ENCODING_UTF_8);
        contentDefinition = CmsXmlContentDefinition.unmarshal(cd, SCHEMA_SYSTEM_ID_FEED, resolver);
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_FEED, cd.getBytes(CmsEncoder.ENCODING_UTF_8));

        return contentDefinition;
    }

    /**
     * Test case for creating a feed from an XML content using {@link CmsFeed}.<p>
     * 
     * @throws Exception in case the test fails
     */
    public void testFeedCreationFromXml() throws Exception {

        CmsObject cms = getCmsObject();
        echo("Testing feed creation from an XML content");

        // please note: this test must run AFTER the "testSimpleFeedGeneration" test 
        String feedArticlePath = "/feeditems/";
        Locale en = Locale.ENGLISH;

        CmsXmlContentDefinition contentDefinition = initFeedSchema(cms);
        CmsXmlContent content = CmsXmlContentFactory.createDocument(
            cms,
            en,
            CmsEncoder.ENCODING_UTF_8,
            contentDefinition);

        // set some values
        I_CmsXmlContentValue title = content.getValue(CmsFeed.NODE_TITLE, en, 0);
        assertNotNull(title);
        title.setStringValue(cms, "This is the feed creation test");
        I_CmsXmlContentValue type = content.getValue(CmsFeed.NODE_TYPE, en, 0);
        assertEquals("rss_2.0", type.getStringValue(cms));
        assertNull(content.getValue(CmsFeed.NODE_DESCRIPTION, en, 0));
        I_CmsXmlContentValue description = content.addValue(cms, CmsFeed.NODE_DESCRIPTION, en, 0);
        description.setStringValue(cms, "This is the description of the test feed.");
        assertNull(content.getValue(CmsFeed.NODE_COPYRIGHT, en, 0));
        I_CmsXmlContentValue copyright = content.addValue(cms, CmsFeed.NODE_COPYRIGHT, en, 0);
        copyright.setStringValue(cms, "Alkacon Software GmbH");
        I_CmsXmlContentValue collector = content.getValue(CmsFeed.NODE_COLLECTOR, en, 0);
        assertEquals("allInFolder", collector.getStringValue(cms));
        I_CmsXmlContentValue parameter = content.getValue(CmsFeed.NODE_PARAMETER, en, 0);
        assertNotNull(parameter);
        // collect 5 entries from type XML content
        parameter.setStringValue(cms, feedArticlePath + "|xmlcontent|5");

        // now marshal the XML content
        byte[] bytes = content.marshal();
        String resourceName = "/my_feed.xml";
        int typeId = OpenCms.getResourceManager().getResourceType("xmlcontent").getTypeId();
        cms.createResource(resourceName, typeId, bytes, Collections.EMPTY_LIST);
        echo("Created resource: " + resourceName);

        // now read the resource and create the test feed
        CmsResource feedRes = cms.readResource(resourceName);
        CmsFile feedFile = cms.readFile(feedRes);
        CmsXmlContent feedContent = CmsXmlContentFactory.unmarshal(cms, feedFile);
        System.out.println(feedContent.toString());

        CmsFeed feed = new CmsFeed(cms, en, feedRes, feedContent);
        feed.init();
        feed.write(System.out);

        // another test with a different constructor
        CmsFeed feed2 = new CmsFeed(cms, resourceName);
        feed2.write(System.out);
    }

    /**
     * Test case for the special feed select widget {@link CmsFeedSelectWidget}.<p>
     * 
     * @throws Exception in case the test fails
     */
    public void testFeedSelectWidget() throws Exception {

        CmsObject cms = getCmsObject();
        echo("Testing the custom feed select widget");

        echo("Testing 1st version of feed select widget with feed type names");
        CmsFeedSelectWidget widget = new CmsFeedSelectWidget("feedTypes");
        boolean foundRss1 = false;
        boolean foundAtom1 = false;
        List options = widget.parseSelectOptions(cms, null, null);
        Iterator i = options.iterator();
        while (i.hasNext()) {
            CmsSelectWidgetOption option = (CmsSelectWidgetOption)i.next();
            String opt = option.getOption();
            System.out.print(opt);
            System.out.println(option.isDefault() ? " [default]" : "");
            if ("rss_1.0".equals(opt)) {
                foundRss1 = true;
            }
            if ("atom_1.0".equals(opt)) {
                foundAtom1 = true;
            }
        }
        assertTrue(foundAtom1);
        assertTrue(foundRss1);

        echo("Testing 2nd version of feed select widget with collector names");
        widget = new CmsFeedSelectWidget("collectors");
        boolean foundAllInFolder = false;
        options = widget.parseSelectOptions(cms, null, null);
        i = options.iterator();
        while (i.hasNext()) {
            CmsSelectWidgetOption option = (CmsSelectWidgetOption)i.next();
            String opt = option.getOption();
            System.out.print(opt);
            System.out.println(option.isDefault() ? " [default]" : "");
            if ("allInFolder".equals(opt)) {
                foundAllInFolder = true;
            }
        }
        assertTrue(foundAllInFolder);
    }

    /**
     * Test case for unmarshalling XML contents with additional feed appinfo nodes.<p>
     * 
     * @throws Exception in case the test fails
     */
    public void testFeedAppinfo() throws Exception {

        CmsObject cms = getCmsObject();
        echo("Testing feed XML content appinfo handler");

        CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cms);

        String content;

        // unmarshal content definition
        content = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-definition-1.xsd", CmsEncoder.ENCODING_UTF_8);
        CmsXmlContentDefinition definition = CmsXmlContentDefinition.unmarshal(content, SCHEMA_SYSTEM_ID_1, resolver);
        // store content definition in entity resolver
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_1, content.getBytes(CmsEncoder.ENCODING_UTF_8));

        // now create the XML content
        content = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-1_1.xml", CmsEncoder.ENCODING_UTF_8);
        CmsXmlContent xmlcontent = CmsXmlContentFactory.unmarshal(content, CmsEncoder.ENCODING_UTF_8, resolver);

        assertTrue(xmlcontent.hasValue("Title", Locale.ENGLISH));
        assertTrue(xmlcontent.hasValue("Author", Locale.ENGLISH));
        assertSame(definition.getContentHandler().getClass().getName(), CmsFeedXmlContentHandler.class.getName());

        CmsFeedXmlContentHandler handler = (CmsFeedXmlContentHandler)definition.getContentHandler();
        CmsFeedContentMapping mapping = handler.getFeedMapping();

        assertEquals("Title[1]", mapping.getMappingForTitle());
        assertEquals("Author[1]", mapping.getMappingForAuthor());
    }

    /**
     * Test simple feed generation from a list of resources.<p>
     * 
     * @throws Exception in case the test fails
     */
    public void testSimpleFeedGeneration() throws Exception {

        CmsObject cms = getCmsObject();
        echo("Testing simple syndication feed generation from XML contents");

        Locale en = Locale.ENGLISH;

        // unmarshal content definition
        CmsXmlEntityResolver resolver = new CmsXmlEntityResolver(cms);
        String cd = CmsFileUtil.readFile(PACKAGE_PATH + "/xmlcontent-definition-1.xsd", CmsEncoder.ENCODING_UTF_8);
        CmsXmlContentDefinition contentDefinition = CmsXmlContentDefinition.unmarshal(cd, SCHEMA_SYSTEM_ID_1, resolver);
        // store content definition in entity resolver
        CmsXmlEntityResolver.cacheSystemId(SCHEMA_SYSTEM_ID_1, cd.getBytes(CmsEncoder.ENCODING_UTF_8));

        String feedArticlePath = "/feeditems/";
        String feedArticleName = "item_";
        String feedArticleSuffix = ".xml";
        cms.createResource(feedArticlePath, CmsResourceTypeFolder.getStaticTypeId());

        // create a number of files using the right XML content
        int loopSize = 10;
        for (int i = 0; i < loopSize; i++) {
            CmsXmlContent content = CmsXmlContentFactory.createDocument(
                cms,
                en,
                CmsEncoder.ENCODING_UTF_8,
                contentDefinition);

            // title is mandantory in XSD
            I_CmsXmlContentValue title = content.getValue("Title", en, 0);
            title.setStringValue(cms, "This is the title of article " + i);
            I_CmsXmlContentValue author = content.addValue(cms, "Author", en, 0);
            author.setStringValue(cms, "I am the author " + i);
            I_CmsXmlContentValue text = content.addValue(cms, "Text", en, 0);
            String textValue = "<h1> This is text in article " + i + " </h1>";
            for (int j = -1; j < i; j++) {
                textValue += "<p> Line " + (j + 2) + " with more text... </p>";
            }
            text.setStringValue(cms, textValue);

            // marshal the XML content
            byte[] bytes = content.marshal();
            String resourceName = feedArticlePath + feedArticleName + i + feedArticleSuffix;

            int type = OpenCms.getResourceManager().getResourceType("xmlcontent").getTypeId();
            cms.createResource(resourceName, type, bytes, Collections.EMPTY_LIST);
            echo("Created resource: " + resourceName);
        }

        // now read all the created resources
        List entries = cms.getResourcesInFolder(feedArticlePath, CmsResourceFilter.ALL);

        CmsFeedGenerator feed = new CmsFeedGenerator();

        feed.setFeedTitle("My first sample feed");
        feed.setFeedType("atom_1.0");
        feed.setFeedLink("http://www.opencms.org/feed.html");
        feed.setFeedDescription("This is a simple sample syndication feed!");
        feed.setFeedCopyright("Alkacon Software GmbH");
        feed.setContentEntries(entries);

        feed.write(cms, en, System.out);
    }
}