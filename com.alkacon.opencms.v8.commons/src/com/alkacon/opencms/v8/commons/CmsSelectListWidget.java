/*
 * File   : $Source$
 * Date   : $Date$
 * Version: $Revision$
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.commons;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.search.galleries.CmsGallerySearch;
import org.opencms.search.galleries.CmsGallerySearchResult;
import org.opencms.util.CmsMacroResolver;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsSelectWidget;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsWorkplace;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a widget to select the possible configured lists of XML contents.<p>
 * 
 * @see CmsSelectWidget
 */
public class CmsSelectListWidget extends CmsSelectWidget {

    /**
     * Creates a new list select widget.<p>
     */
    public CmsSelectListWidget() {

        // empty constructor is required for class registration
        super();
    }

    /**
     * Creates a list select widget with the select options specified in the given configuration List.<p>
     * 
     * The list elements must be of type <code>{@link CmsSelectWidgetOption}</code>.<p>
     * 
     * @param configuration the configuration (possible options) for the select widget
     * 
     * @see CmsSelectWidgetOption
     */
    public CmsSelectListWidget(List<CmsSelectWidgetOption> configuration) {

        super(configuration);
    }

    /**
     * Creates a list select widget with the specified select options.<p>
     * 
     * @param configuration the configuration (possible options) for the select box
     */
    public CmsSelectListWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    @Override
    public I_CmsWidget newInstance() {

        return new CmsSelectListWidget(getConfiguration());
    }

    /**
     * Returns the list of configured select options, parsing the configuration String if required.<p>
     * 
     * The list elements are of type <code>{@link CmsSelectWidgetOption}</code>.
     * The configuration String is parsed only once and then stored internally.<p>
     * 
     * @param cms the current users OpenCms context
     * @param widgetDialog the dialog of this widget
     * @param param the widget parameter of this dialog
     * 
     * @return the list of select options
     * 
     * @see CmsSelectWidgetOption
     */
    @Override
    protected List<CmsSelectWidgetOption> parseSelectOptions(
        CmsObject cms,
        I_CmsWidgetDialog widgetDialog,
        I_CmsWidgetParameter param) {

        List<CmsSelectWidgetOption> selectOptions = new ArrayList<CmsSelectWidgetOption>();
        String configuration = getConfiguration();
        if (configuration == null) {
            // workaround: use the default value to parse the options
            configuration = param.getDefault(cms);
        }
        configuration = CmsMacroResolver.resolveMacros(configuration, cms, widgetDialog.getMessages());
        try {
            // get the ID of the function page
            int functionPageId = OpenCms.getResourceManager().getResourceType("function").getTypeId();
            // get all module folders to iterate
            List<CmsResource> modules = cms.getSubFolders(
                CmsWorkplace.VFS_PATH_MODULES,
                CmsResourceFilter.IGNORE_EXPIRATION);
            for (int i = 0; i < modules.size(); i++) {
                // check the lists/ sub folder of each module
                List<CmsResource> listFiles = new ArrayList<CmsResource>();
                String folder = cms.getSitePath(modules.get(i));
                folder += "lists/";
                if (cms.existsResource(folder, CmsResourceFilter.IGNORE_EXPIRATION)) {
                    // found a lists/ folder, check for function pages
                    listFiles = cms.getFilesInFolder(
                        folder,
                        CmsResourceFilter.DEFAULT.addRequireType(functionPageId).addRequireVisible());
                }
                for (int j = 0; j < listFiles.size(); j++) {
                    // get the current list function file
                    CmsFile functionFile = (CmsFile)listFiles.get(j);
                    CmsGallerySearchResult sResult = CmsGallerySearch.searchById(
                        cms,
                        functionFile.getStructureId(),
                        widgetDialog.getLocale());
                    // determine the title of the function page
                    String title = null;
                    if (sResult != null) {
                        // get the localized title from the gallery search result
                        title = sResult.getTitle();
                    }
                    if (CmsStringUtil.isEmptyOrWhitespaceOnly(title)) {
                        title = functionFile.getRootPath();
                    }
                    // create an option and add it to the result
                    CmsSelectWidgetOption option = new CmsSelectWidgetOption(functionFile.getRootPath(), false, title);
                    selectOptions.add(option);
                }
            }
        } catch (CmsException e) {
            // something went wrong
        }

        return selectOptions;
    }

}
