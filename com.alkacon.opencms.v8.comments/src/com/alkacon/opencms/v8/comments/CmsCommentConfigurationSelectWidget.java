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

import org.opencms.ade.configuration.CmsADEManager;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsVfsException;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsSelectWidget;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsDialog;
import org.opencms.xml.types.A_CmsXmlContentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;

/**
 * Comment configuration file select widget.<p>
 * 
 * @since 8.0.3
 */
public class CmsCommentConfigurationSelectWidget extends CmsSelectWidget {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCommentConfigurationSelectWidget.class);

    /**
     * Creates a new instance of the comment configuration select widget.<p>
     */
    public CmsCommentConfigurationSelectWidget() {

        super();
    }

    /**
     * Creates a new instance of the comment configuration select widget.<p>
     * 
     * @param configuration the widget configuration
     */
    public CmsCommentConfigurationSelectWidget(List<CmsSelectWidgetOption> configuration) {

        super(configuration);

    }

    /**
     * Creates a new instance of the comment configuration select widget.<p>
     * 
     * @param configuration the widget configuration
     */
    public CmsCommentConfigurationSelectWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#getConfiguration(org.opencms.file.CmsObject, org.opencms.xml.types.A_CmsXmlContentValue, org.opencms.i18n.CmsMessages, org.opencms.file.CmsResource, java.util.Locale)
     */
    @Override
    public String getConfiguration(
        CmsObject cms,
        A_CmsXmlContentValue schemaType,
        CmsMessages messages,
        CmsResource resource,
        Locale contentLocale) {

        List<CmsSelectWidgetOption> options = getSelectOptionForRootPath(cms, resource.getRootPath());
        String result = "";
        int i = 0;
        for (CmsSelectWidgetOption option : options) {
            if (i > 0) {
                result += "|";
            }
            result += option.toString();
            i++;
        }
        return result;

    }

    /**
     * @see org.opencms.widgets.CmsSelectWidget#newInstance()
     */
    @Override
    public I_CmsWidget newInstance() {

        return new CmsCommentConfigurationSelectWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#getSelectOptions()
     */
    @Override
    protected List<CmsSelectWidgetOption> getSelectOptions() {

        // for the test case this method needs to be in the comments package
        return super.getSelectOptions();
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#parseSelectOptions(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    @Override
    protected List<CmsSelectWidgetOption> parseSelectOptions(
        CmsObject cms,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        if (getSelectOptions() == null) {
            String recourceRootPath = (widgetDialog instanceof CmsDialog)
            ? cms.addSiteRoot(((CmsDialog)widgetDialog).getParamResource())
            : cms.addSiteRoot(cms.getRequestContext().getUri());
            List<CmsSelectWidgetOption> options = getSelectOptionForRootPath(cms, recourceRootPath);
            setSelectOptions(options);
        }
        return getSelectOptions();
    }

    /**
     * Collects the available configuration files for the given root path.<p>
     * 
     * @param cms the cms context
     * @param currentRootPath the root path of the edited resource
     * 
     * @return the configuration file select options
     */
    private List<CmsSelectWidgetOption> getSelectOptionForRootPath(CmsObject cms, String currentRootPath) {

        List<CmsSelectWidgetOption> options = new ArrayList<CmsSelectWidgetOption>();
        // reading the available configuration files
        List<CmsResource> configResources = new ArrayList<CmsResource>();
        // first get the path of .content folder within the present sub-site
        String contentPath = CmsStringUtil.joinPaths(
            OpenCms.getADEManager().getSubSiteRoot(cms, currentRootPath),
            CmsADEManager.CONTENT_FOLDER_NAME + "/");
        contentPath = cms.getRequestContext().removeSiteRoot(contentPath);
        int configTypeId;
        try {
            configTypeId = OpenCms.getResourceManager().getResourceType("alkacon-v8-comment").getTypeId();
            CmsResourceFilter filter = CmsResourceFilter.ONLY_VISIBLE_NO_DELETED.addRequireType(configTypeId);
            try {
                configResources.addAll(cms.readResources(contentPath, filter));
            } catch (CmsVfsException e) {
                // may happen if content path does not exist, can be ignored
            }
            // also read from module folder
            configResources.addAll(cms.readResources("/system/modules/", filter));
            // generate options for resources
            for (CmsResource resource : configResources) {
                String path = cms.getSitePath(resource);
                String title = cms.readPropertyObject(resource, CmsPropertyDefinition.PROPERTY_TITLE, false).getValue(
                    path);
                CmsSelectWidgetOption option = new CmsSelectWidgetOption(path, options.isEmpty(), title, path);
                options.add(option);
            }
        } catch (CmsException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        if (options.isEmpty()) {
            options.add(new CmsSelectWidgetOption("", true, Messages.get().container(
                Messages.ERR_NO_CONFIGURATION_FILES_FOUND_0).key()));
        }
        return options;
    }
}
