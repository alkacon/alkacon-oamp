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

package com.alkacon.opencms.comments;

import org.opencms.ade.configuration.CmsADEManager;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsPropertyDefinition;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
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

import java.util.ArrayList;
import java.util.List;

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
            List<CmsSelectWidgetOption> options = new ArrayList<CmsSelectWidgetOption>();
            // reading the available configuration files
            List<CmsResource> configResources = new ArrayList<CmsResource>();
            // first get the path of .content folder within the present sub-site
            String currentRootPath = cms.addSiteRoot(((CmsDialog)widgetDialog).getParamResource());
            String contentPath = CmsStringUtil.joinPaths(
                OpenCms.getADEManager().getSubSiteRoot(cms, currentRootPath),
                CmsADEManager.CONFIG_FOLDER_NAME + "/");
            contentPath = cms.getRequestContext().removeSiteRoot(contentPath);
            int configTypeId;
            try {
                configTypeId = OpenCms.getResourceManager().getResourceType("alkacon-comment").getTypeId();
                CmsResourceFilter filter = CmsResourceFilter.ONLY_VISIBLE_NO_DELETED.addRequireType(configTypeId);
                configResources.addAll(cms.readResources(contentPath, filter));
                // also read from module folder
                configResources.addAll(cms.readResources("/system/modules/com.alkacon.opencms.comments/", filter));
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
            setSelectOptions(options);
        }
        return getSelectOptions();
    }
}
