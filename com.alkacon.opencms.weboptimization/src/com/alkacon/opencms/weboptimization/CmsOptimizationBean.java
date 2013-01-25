/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationBean.java,v $
 * Date   : $Date: 2010/01/08 09:46:05 $
 * Version: $Revision: 1.3 $
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

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsLocaleManager;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.loader.CmsTemplateLoaderFacade;
import org.opencms.loader.CmsXmlContentLoader;
import org.opencms.loader.I_CmsResourceLoader;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

/**
 * Base bean for optimization.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 7.0.6
 */
public abstract class CmsOptimizationBean extends CmsJspActionElement {

	/**
     * The inclusion mode.<p>
     */
    protected enum IncludeMode {
    	/** Optimized mode. */
    	OPTIMIZED, 
    	/** Original mode.*/
    	ORIGINAL, 
    	/** Automatic mode. */
    	AUTO;
    }

	/**
	 * The parsing results.<p>
	 */
	protected static class Resolution {
		
		/** List of original resources. */
		private List<CmsResource> m_resources = new ArrayList<CmsResource>();
		
		/** Are there any optimized resource left?. */
		private boolean m_hasOptimizedLeft;

		/**
		 * Adds the given resource to the list.<p>
		 * 
		 * @param resource the resource to add
		 */
		public void addResource(CmsResource resource) {
			
			if (!m_resources.contains(resource)) {
				m_resources.add(resource);
			}
		}

		/**
		 * Returns the list of resources.<p>
		 * 
		 * @return the list of resources
		 */
		public List<CmsResource> getResources() {
			
			return m_resources;
		}

		/**
		 * Checks if there is at least one optimized resource left.<p>
		 * 
		 * @return <code>true</code> if there is at least one optimized resource left
		 */
		public boolean hasOptimizedLeft() {
			
			return m_hasOptimizedLeft;
		}

		/**
		 * Merges the data with the given resolution.<p>
		 * 
		 * @param resolution the resolution to merge with
		 */
		public void merge(Resolution resolution) {
			
			if(resolution.hasOptimizedLeft()) {
				setOptimizedLeft();
			}
			m_resources.addAll(resolution.getResources());
		}
		
		/**
		 * Sets the flag indicating that there is at least one optimized
		 * resource left.<p>
		 */
		public void setOptimizedLeft() {
			
			m_hasOptimizedLeft = true;
		}
	}

    /** type attribute constant. */
    protected static final String ATTR_TYPE = "type";

    /** Node name constant. */
    protected static final String N_LINE_BREAK_POS = "LineBreakPos";

    /** Node name constant. */
    protected static final String N_OPTIONS = "Options";

    /** Node name constant. */
    protected static final String N_PATH = "Path";

    /** Node name constant. */
    protected static final String N_RESOURCE = "Resource";

    /** Node name constant. */
    protected static final String N_BEHAVIOUR = "Behaviour";

    /** Behaviour value constant. */
    protected static final String BEHAVIOUR_OPTIMIZED = "optimized";

    /** Node name constant. */
    protected static final String N_ONLINE = "Online";

    /** Node name constant. */
    protected static final String N_OFFLINE = "Offline";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsOptimizationBean.class);

    /**
     * Default constructor.
     * 
     * @param context the JSP page context object
     * @param req the JSP request 
     * @param res the JSP response 
     */
    public CmsOptimizationBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        super(context, req, res);
    }
    
    /**
     * Creates a new HTML tag with the given name and attributes.<p>
     * 
     * @param tag the tag name
     * @param attr the attributes, can be <code>null</code>
     * @param xmlStyle if <code>true</code>,the tag will be closed in the xml style
     * 
     * @return the HTML code
     */
    protected String createTag(String tag, Map<String,String> attr, boolean xmlStyle) {

        StringBuffer sb = new StringBuffer();
        sb.append("<");
        sb.append(tag);
        if (attr != null) {
            Iterator<Map.Entry<String,String>> it = attr.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("=\"");
                sb.append(entry.getValue());
                sb.append("\"");
            }
        }
        sb.append(" ");
        if (xmlStyle) {
            sb.append("/");
        }
        sb.append(">");
        if (!xmlStyle) {
            sb.append("</");
            sb.append(tag);
            sb.append(">");
        }
        return sb.toString();
    }
    
    /**
     * Returns the content of all files of the given path with the 'right' extension.<p> 
     * 
     * @param cms the cms context
     * @param path the path to process, can be a folder
     * 
     * @return the content of all files
     * 
     * @throws Exception if something goes wrong 
     */
    protected String getAllContent(CmsObject cms, String path) throws Exception {

        // get the extension (should be .css or .js)
        String ext = cms.getRequestContext().getUri().substring(cms.getRequestContext().getUri().lastIndexOf('.'));

        // retrieve the actual files to process
        List<CmsResource> resources = resolveResource(cms, path, ext);
        if (resources.isEmpty()) {
            LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_NOTHING_TO_PROCESS_1, path));
            return "";
        }

        // merge file contents
        StringBuffer sb = new StringBuffer();
        Iterator<CmsResource> itRes = resources.iterator();
        while (itRes.hasNext()) {
            CmsResource res = itRes.next();
            byte[] data = getBinaryContent(cms, res);
            sb.append(new String(data, getRequestContext().getEncoding()));
        }
        return sb.toString();
    }

    /**
     * Returns the processed output of an OpenCms resource.<p>
     * 
     * @param cms the cms context
     * @param resource the resource to process
     * 
     * @return the processed output
     * 
     * @throws Exception if something goes wrong
     */
    protected byte[] getBinaryContent(CmsObject cms, CmsResource resource) throws Exception {

        I_CmsResourceLoader loader = OpenCms.getResourceManager().getLoader(resource);
        // We need to handle separately xmlcontent files since we want the rendered content and not the xml data
        if (loader.getLoaderId() == CmsXmlContentLoader.RESOURCE_LOADER_ID) {
            // HACK: the A_CmsXmlDocumentLoader.getTemplatePropertyDefinition() method is not accessible :(
            String templatePropertyDef = CmsPropertyDefinition.PROPERTY_TEMPLATE_ELEMENTS;
            CmsTemplateLoaderFacade loaderFacade = OpenCms.getResourceManager().getTemplateLoaderFacade(
                cms,
                resource,
                templatePropertyDef);
            loader = loaderFacade.getLoader();
            String oldUri = cms.getRequestContext().getUri();
            try {
                cms.getRequestContext().setUri(cms.getSitePath(resource));
                resource = loaderFacade.getLoaderStartResource();
                return loader.dump(cms, resource, null, null, getRequest(), getResponse());
            } finally {
                cms.getRequestContext().setUri(oldUri);
            }
        }
        return loader.dump(cms, resource, null, null, getRequest(), getResponse());
    }

    /**
     * Creates code for the given optimized resource, 
     * by default it will use the original code in the 
     * offline project for debugging purposes, and 
     * optimized code in the online project for optimal 
     * performance.<p>
     * 
     * @param path the uri of the file to be included
     * 
     * @throws Exception if something goes wrong
     */
    public void includeDefault(String path) throws Exception {

    	includeDefault(path, IncludeMode.AUTO);
    }

    /**
     * Creates optimized code for the optimized resource, depending on the mode.<p>
     * 
     * @param path the optimized resource uri
     * @param mode the mode to use
     *  
     * @throws Exception if something goes wrong
     */
    protected abstract void includeDefault(String path, IncludeMode mode) throws Exception;

	/**
     * Creates optimized code for the optimized resource.<p>
     * 
     * @param path the optimized resource uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOptimized(String path) throws Exception {

    	includeDefault(path, IncludeMode.OPTIMIZED);
    }

    /**
     * Creates original code for the optimized resource.<p>
     * 
     * @param path the optimized resource uri
     *  
     * @throws Exception if something goes wrong
     */
    public void includeOriginal(String path) throws Exception {

    	includeDefault(path, IncludeMode.ORIGINAL);
    }

    /**
	 * Checks if the given resource node has to be optimized.<p>
	 * 
	 * @param cms the current cms context
	 * @param xml the xml content to use
	 * @param value the resource xml node
	 * @param locale the locale to use
	 * @param online if online or offline
	 * 
	 * @return <code>true</code> if the given resource node has to be optimized
	 */
	protected boolean isOptimized(CmsObject cms, CmsXmlContent xml, I_CmsXmlContentValue value, Locale locale, boolean online) {

		boolean optimized = false;
		String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_BEHAVIOUR);
		I_CmsXmlContentValue behaviour = xml.getValue(xpath, locale);
		if (behaviour == null) {
			if (online) {
				optimized = true;
			}
		} else {
			if (online) {
				xpath = CmsXmlUtils.concatXpath(behaviour.getPath(), N_ONLINE);
			} else {
				xpath = CmsXmlUtils.concatXpath(behaviour.getPath(), N_OFFLINE);
			}
			I_CmsXmlContentValue p = xml.getValue(xpath, locale);
			optimized = p.getStringValue(cms).equals(BEHAVIOUR_OPTIMIZED);
		}
		return optimized;
	}

    /**
	 * Resolves the given file.<p>
	 * 
	 * @param cms the current context
	 * @param file the file to resolve
	 * @param mode the mode to use
	 * @param extension the file extension to restrict the inclusion
	 * @param type the resource type id for recursion
	 * 
	 * @return the resolution
	 * 
	 * @throws CmsException if something goes wrong
	 */
	protected Resolution resolveInclude(CmsObject cms, CmsFile file, IncludeMode mode, String extension, int type) throws CmsException {

		Resolution resolution = new Resolution();
		// read the XML content
		CmsXmlContent xml = CmsXmlContentFactory.unmarshal(cms, file);

		// resolve the locale
		Locale locale = resolveLocale(cms, xml);

		// cache the current project
		boolean online = cms.getRequestContext().currentProject().isOnlineProject();

		// iterate the resources
		Iterator<I_CmsXmlContentValue> itPath = xml.getValues(N_RESOURCE, locale).iterator();
		while (itPath.hasNext()) {
			I_CmsXmlContentValue value = itPath.next();
			// get the uri
			String xpath = CmsXmlUtils.concatXpath(value.getPath(), N_PATH);
			String uri = xml.getValue(xpath, locale).getStringValue(cms);

			// retrieve the actual files to process
			List<CmsResource> resorces = resolveResource(cms, uri, extension);
			if (resorces.isEmpty()) {
				LOG.warn(Messages.get().getBundle().key(
						Messages.LOG_WARN_NOTHING_TO_PROCESS_1, uri));
				continue;
			}

			if (mode == IncludeMode.AUTO) {
				// compute the mode to use
				boolean optimized = isOptimized(cms, xml, value, locale, online);
				if (optimized) {
					resolution.setOptimizedLeft();
					// if optimized it will be handled while rendering
					continue;
				}
			}
			
			Iterator<CmsResource> itRes = resorces.iterator();
			while (itRes.hasNext()) {
				CmsResource res = itRes.next();
				if (res.getTypeId() == type) {
					// recurse in case of nested optimized resources
					resolution.merge(resolveInclude(cms, cms.readFile(res), mode,extension, type));
				} else {
					// handle this resource
					resolution.addResource(res);
				}
			}
		}
		return resolution;
	}

    /**
     * Resolves the right locale to use.<p>
     * 
     * @param cms the cms context
     * @param xml the xmlcontent
     * 
     * @return the locale to use
     */
    protected Locale resolveLocale(CmsObject cms, CmsXmlContent xml) {

        Locale locale = cms.getRequestContext().getLocale();
        if (!xml.hasLocale(locale)) {
            locale = OpenCms.getLocaleManager().getDefaultLocale(cms, cms.getRequestContext().getUri());
            if (!xml.hasLocale(locale)) {
                locale = CmsLocaleManager.getDefaultLocale();
                if (!xml.hasLocale(locale)) {
                    locale = (Locale)xml.getLocales().get(0);
                }
            }
        }
        return locale;
    }

    /**
     * Will check the extension, and if the path is a folder retrieve 
     * all files with the same extension in the folder.<p>
     * 
     * @param cms the cms context
     * @param path the resource path, can be a folder!
     * @param ext the file extension to filter
     * 
     * @return the list of files
     * 
     * @throws CmsException if something goes wrong
     */
    protected List<CmsResource> resolveResource(CmsObject cms, String path, String ext) throws CmsException {

        List<CmsResource> resorces = new ArrayList<CmsResource>();
        
        // An empty path is most probably a bug/unintentionally included. Ignoring it.
        if (StringUtils.isBlank(path)) {
            LOG.warn(Messages.get().getBundle().key(Messages.LOG_WARN_RESOLVE_EMPTY_PATH_1, cms.getRequestContext().getUri()));
            return resorces;
        }
        
        CmsResource res = cms.readResource(path);

        if (res.isFolder()) {
            // if folder, get all files with the given extension in the folder
            List<CmsResource> files = cms.readResources(path, CmsResourceFilter.DEFAULT_FILES);
            Iterator<CmsResource> itFiles = files.iterator();
            while (itFiles.hasNext()) {
                CmsResource file = itFiles.next();
                if (file.getRootPath().endsWith(ext)) {
                    resorces.add(file);
                }
            }
        } else {
            if (res.getRootPath().endsWith(ext)) {
                resorces.add(res);
            }
        }
        return resorces;
    }
}
