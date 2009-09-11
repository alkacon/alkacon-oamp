/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationSprite.java,v $
 * Date   : $Date: 2009/09/11 07:39:52 $
 * Version: $Revision: 1.2 $
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

import com.alkacon.simapi.Simapi;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;

/**
 * Bean for optimizing images.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationSprite extends CmsOptimizationBean {

    /** Node name constant. */
    protected static final String N_POSITION = "Position";

    /** Node name constant. */
    protected static final String N_SELECTOR = "Selector";

    /** Node name constant. */
    protected static final String N_X = "X";

    /** Node name constant. */
    protected static final String N_Y = "Y";

    /** Image sprite resource type constant. */
    protected static final int RESOURCE_TYPE_SPRITE = 764;

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsOptimizationSprite.class);

    /**
     * Default constructor.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsOptimizationSprite(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }

    /**
     * Add a new image to the given sprite.<p>
     * 
     * @param sprite the sprite to modify
     * @param image the image to add
     * @param options the sprite options
     * 
     * @return the modified sprite
     */
    public BufferedImage addImage(BufferedImage sprite, BufferedImage image, CmsOptimizationSpriteOptions options) {

        // check the sprite size
        int newWidth = -1;
        if (sprite.getWidth() < options.getX() + image.getWidth()) {
            newWidth = options.getX() + image.getWidth();
        }
        int newHeight = -1;
        if (sprite.getHeight() < options.getY() + image.getHeight()) {
            newHeight = options.getY() + image.getHeight();
        }
        Graphics2D g2d = sprite.createGraphics();
        if ((newHeight > 0) || (newWidth > 0)) {
            // adjust sprite size if needed
            if (newHeight < 0) {
                newHeight = sprite.getHeight();
            }
            if (newWidth < 0) {
                newWidth = sprite.getWidth();
            }
            // clone the sprite
            BufferedImage s2 = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
            g2d = s2.createGraphics();
            g2d.drawImage(sprite, 0, 0, null);
            sprite = s2;
        }
        // put the image into the sprite
        g2d.drawImage(image, options.getX(), options.getY(), null);
        return sprite;
    }

    /**
     * Create css rules for the given optimized sprite, 
     * will create css rules for the original images 
     * in the offline project for debugging purposes.<p>
     * 
     * @param path the uri of the file to be included
     * 
     * @throws Exception if something goes wrong
     */
    public void includeDefault(String path) throws Exception {

        if (getCmsObject().getRequestContext().currentProject().isOnlineProject()) {
            includeOptimized(path);
        } else {
            includeOriginal(path);
        }
    }

    /**
     * Will create css rules for the optimized sprite.<p>
     * 
     * @param path the optimized sprite uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOptimized(String path) throws Exception {

        includeSprite(path, true, null);
    }

    /**
     * Will create css rules for the original images.<p>
     * 
     * @param path the optimized sprite uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOriginal(String path) throws Exception {

        includeSprite(path, false, null);
    }

    /**
     * Will optimize the resources taken from the underlying XML content.<p>
     * 
     * @throws Exception if something goes wrong
     */
    public void optimize() throws Exception {

        CmsObject cms = getCmsObject();
        CmsFile file = cms.readFile(cms.getRequestContext().getUri());

        // check the resource type
        String type = Simapi.getImageType(file.getRootPath());
        if ((file.getTypeId() != RESOURCE_TYPE_SPRITE) || (type == null)) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2,
                cms.getRequestContext().getUri(),
                new Integer(file.getTypeId())));
        }

        // read the XML content
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, file);

        // resolve the locale
        Locale locale = resolveLocale(cms, xml);

        BufferedImage sprite = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        // iterate the resources
        Iterator itPath = xml.getValues(N_RESOURCE, locale).iterator();
        while (itPath.hasNext()) {
            I_CmsXmlContentValue value = (I_CmsXmlContentValue)itPath.next();
            // get the path
            String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_PATH);
            String path = xml.getValue(xpath, locale).getStringValue(cms);
            // get the options
            CmsOptimizationSpriteOptions opts = new CmsOptimizationSpriteOptions();
            xpath = CmsXmlUtils.concatXpath(value.getPath(), N_POSITION);
            if (xml.hasValue(xpath, locale)) {
                I_CmsXmlContentValue value2 = xml.getValue(xpath, locale);
                xpath = CmsXmlUtils.concatXpath(value2.getPath(), N_X);
                opts.setX(Integer.parseInt(xml.getStringValue(cms, xpath, locale)));
                xpath = CmsXmlUtils.concatXpath(value2.getPath(), N_Y);
                opts.setY(Integer.parseInt(xml.getStringValue(cms, xpath, locale)));
            }
            xpath = CmsXmlUtils.concatXpath(value.getPath(), N_SELECTOR);
            opts.setSelector(xml.getStringValue(cms, xpath, locale));

            // retrieve the actual files to process
            CmsResource res = cms.readResource(path);
            if (res.isFolder() || (Simapi.getImageType(path) == null)) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_NOTHING_TO_PROCESS_1, path));
                continue;
            }

            // process this resource
            BufferedImage img = Simapi.read(getBinaryContent(cms, res));
            sprite = addImage(sprite, img, opts);
        }
        writeImage(sprite, type);
    }

    /**
     * Writes a new css sprite rule for the given resource.<p>
     * 
     * @param uri the resource to use
     * @param opts the options to use
     * 
     * @throws IOException if something goes wrong 
     */
    public void writeSpriteInclude(String uri, CmsOptimizationSpriteOptions opts) throws IOException {

        StringBuffer sb = new StringBuffer();

        sb.append(opts.getSelector());
        sb.append(" { background-image: url(");
        sb.append(link(uri));
        sb.append(");");
        if ((opts.getX() != 0) || (opts.getY() != 0)) {
            sb.append(" background-position: ");
            sb.append(-opts.getX());
            sb.append("px ");
            sb.append(-opts.getY());
            sb.append("px;");
        }
        sb.append(" }");

        getJspContext().getOut().println(sb.toString());
    }

    /**
     * Will create css rules for the given optimized sprite.<p>
     * 
     * @param path the optimized file uri
     * @param optimized if to write the rules for the optimized image or the originals
     * @param offset optional position offset, used when called recursively
     *  
     * @throws Exception if something goes wrong
     */
    protected void includeSprite(String path, boolean optimized, CmsOptimizationSpriteOptions offset) throws Exception {

        CmsObject cms = getCmsObject();
        CmsFile file = cms.readFile(path);

        // check the resource type
        if (file.getTypeId() != RESOURCE_TYPE_SPRITE) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_NOT_SUPPORTED_RESOURCE_TYPE_2,
                path,
                new Integer(file.getTypeId())));
        }

        // read the XML content
        CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, file);

        // resolve the locale
        Locale locale = resolveLocale(cms, xml);

        // iterate the resources
        Iterator itRes = xml.getValues(N_RESOURCE, locale).iterator();
        while (itRes.hasNext()) {
            I_CmsXmlContentValue value = (I_CmsXmlContentValue)itRes.next();
            // get the uri
            String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_PATH);
            String uri = xml.getValue(xpath, locale).getStringValue(cms);

            // retrieve the actual files to process
            CmsResource res = cms.readResource(uri);
            if (res.isFolder() || (Simapi.getImageType(uri) == null)) {
                LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_NOTHING_TO_PROCESS_1, uri));
                continue;
            }

            // get the options
            CmsOptimizationSpriteOptions opts = new CmsOptimizationSpriteOptions();
            xpath = CmsXmlUtils.concatXpath(value.getPath(), N_POSITION);
            if (xml.hasValue(xpath, locale)) {
                I_CmsXmlContentValue value2 = xml.getValue(xpath, locale);
                xpath = CmsXmlUtils.concatXpath(value2.getPath(), N_X);
                opts.setX(Integer.parseInt(xml.getStringValue(cms, xpath, locale)));
                xpath = CmsXmlUtils.concatXpath(value2.getPath(), N_Y);
                opts.setY(Integer.parseInt(xml.getStringValue(cms, xpath, locale)));
            }
            xpath = CmsXmlUtils.concatXpath(value.getPath(), N_SELECTOR);
            opts.setSelector(xml.getStringValue(cms, xpath, locale));

            // apply offset
            if (offset != null) {
                opts.setX(opts.getX() + offset.getX());
                opts.setY(opts.getY() + offset.getY());
            }
            if (res.getTypeId() == RESOURCE_TYPE_SPRITE) {
                // recurse in case of nested sprites
                includeSprite(uri, optimized, opts);
            } else {
                // handle this resource
                if (optimized) {
                    writeSpriteInclude(path, opts);
                } else {
                    opts.setX(0);
                    opts.setY(0);
                    writeSpriteInclude(uri, opts);
                }
            }
        }
    }

    /**
     * Writes the given image as of the given type to the servlet output stream.<p>
     * 
     * @param image the image to write
     * @param type the type
     * 
     * @throws IOException if something goes wrong
     */
    protected void writeImage(BufferedImage image, String type) throws IOException {

        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName(type).next();
        ImageOutputStream stream = ImageIO.createImageOutputStream(getJspContext().getResponse().getOutputStream());
        writer.setOutput(stream);
        writer.write(image);
        // We must close the stream now because if we are wrapping a ServletOutputStream,
        // a future gc can commit a stream that used in another thread (very very bad)
        stream.flush();
        stream.close();
        writer.dispose();
    }
}
