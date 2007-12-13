/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.feeder/src/com/alkacon/opencms/feeder/CmsFeedSelectWidget.java,v $
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

package com.alkacon.opencms.feeder;

import org.opencms.file.CmsObject;
import org.opencms.file.collectors.I_CmsResourceCollector;
import org.opencms.main.OpenCms;
import org.opencms.widgets.CmsSelectWidget;
import org.opencms.widgets.CmsSelectWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Creates a select widget that contains either all available feed types,
 * or all configured collectors.<p>
 * 
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsFeedSelectWidget extends CmsSelectWidget {

    /**
     * Creates a new instance of the feed select widget.<p>
     */
    public CmsFeedSelectWidget() {

        super();
    }

    /**
     * Creates a new instance of the feed select widget.<p>
     * 
     * @param configuration the widget configuration
     */
    public CmsFeedSelectWidget(List configuration) {

        super(configuration);

    }

    /**
     * Creates a new instance of the feed select widget.<p>
     * 
     * @param configuration the widget configuration
     */
    public CmsFeedSelectWidget(String configuration) {

        super(configuration);
    }

    /**
     * @see org.opencms.widgets.CmsSelectWidget#newInstance()
     */
    public I_CmsWidget newInstance() {

        return new CmsFeedSelectWidget(getConfiguration());
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#getSelectOptions()
     */
    protected List getSelectOptions() {

        // for the test case this method needs to be in the feed package
        return super.getSelectOptions();
    }

    /**
     * @see org.opencms.widgets.A_CmsSelectWidget#parseSelectOptions(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    protected List parseSelectOptions(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        if (getSelectOptions() == null) {
            String configuration = getConfiguration();
            if (configuration == null) {
                // workaround: use the default value to parse the options
                configuration = param.getDefault(cms);
            }

            List options = new ArrayList();
            if ("collectors".equalsIgnoreCase(configuration)) {
                // we want to get the list of configured resource collectors
                Iterator i = OpenCms.getResourceManager().getRegisteredContentCollectors().iterator();
                while (i.hasNext()) {
                    // loop over all collectors and add all collector names
                    I_CmsResourceCollector collector = (I_CmsResourceCollector)i.next();
                    Iterator j = collector.getCollectorNames().iterator();
                    while (j.hasNext()) {
                        String name = (String)j.next();
                        // make "allInFolder" the default setting
                        boolean isDefault = "allInFolder".equals(name);
                        CmsSelectWidgetOption option = new CmsSelectWidgetOption(name, isDefault);
                        options.add(option);
                    }
                }
            } else {
                // we want the list of available feed types
                SyndFeedImpl feedImpl = new SyndFeedImpl();
                List feedTypes = new ArrayList(feedImpl.getSupportedFeedTypes());
                // sort by name, makes it look nicer in widget later
                Collections.sort(feedTypes);
                // now reverse the list, as the latest versions should be on top
                Collections.reverse(feedTypes);
                Iterator i = feedTypes.iterator();
                while (i.hasNext()) {
                    String type = (String)i.next();
                    // make RSS 2.0 the default setting
                    boolean isDefault = "rss_2.0".equals(type);
                    CmsSelectWidgetOption option = new CmsSelectWidgetOption(type, isDefault);
                    options.add(option);
                }
            }
            setSelectOptions(options);
        }
        return getSelectOptions();
    }
}