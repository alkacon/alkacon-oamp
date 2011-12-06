/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.newsletter/src/com/alkacon/opencms/v8/newsletter/admin/CmsListSendNewsletterAction.java,v $
 * Date   : $Date: 2009/07/09 09:30:11 $
 * Version: $Revision: 1.7 $
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

package com.alkacon.opencms.v8.newsletter.admin;

import org.opencms.file.CmsResourceFilter;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.lock.CmsLock;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.list.A_CmsListExplorerDialog;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListItem;

/**
 * Action to send a newsletter depending on the resource lock state.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.7 $ 
 * 
 * @since 7.0.3 
 */
public class CmsListSendNewsletterAction extends CmsListDirectAction {

    /** Id of the column with the resource root path. */
    private final String m_resColumnPathId;

    /** The current resource util object. */
    private CmsResourceUtil m_resourceUtil;

    /**
     * Default Constructor.<p>
     * 
     * @param id the unique id
     * @param resColumnPathId the id of the column with the resource root path
     */
    public CmsListSendNewsletterAction(String id, String resColumnPathId) {

        super(id);
        m_resColumnPathId = resColumnPathId;
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsHtmlIconButton#getHelpText()
     */
    public CmsMessageContainer getHelpText() {

        CmsMessageContainer helptext = super.getHelpText();

        if (isEnabled()) {
            if (super.getHelpText() == null) {
                helptext = Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_HELP_0);
            }
        } else {
            helptext = Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_DISABLED_HELP_0);
        }

        return helptext;
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsHtmlIconButton#getIconPath()
     */
    public String getIconPath() {

        String iconpath = super.getIconPath();
        if (iconpath == null) {
            if (isEnabled()) {
                iconpath = "tools/v8-newsletter/buttons/v8-newsletter_send.png";
            } else {
                iconpath = "tools/v8-newsletter/buttons/v8-newsletter_send_disabled.png";
            }
        }
        return iconpath;
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsHtmlIconButton#getName()
     */
    public CmsMessageContainer getName() {

        CmsMessageContainer name = super.getName();
        if (name == null) {
            if (isEnabled()) {
                name = Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_0);
            } else {
                name = Messages.get().container(Messages.GUI_NEWSLETTER_LIST_ACTION_SEND_DISABLED_0);
            }
        }
        return name;
    }

    /**
     * @see org.opencms.workplace.tools.I_CmsHtmlIconButton#isVisible()
     */
    public boolean isVisible() {

        if (getResourceName() != null) {
            try {
                // check lock state
                CmsLock lock = getResourceUtil().getLock();
                boolean isUnChanged = getResourceUtil().getResource().getState().isUnchanged();
                if (!getWp().getCms().getRequestContext().currentProject().isOnlineProject()
                    && isUnChanged
                    && (lock.isNullLock() || lock.isOwnedBy(getWp().getCms().getRequestContext().currentUser()))) {
                    return isEnabled();
                }
            } catch (Throwable e) {
                // ignore
            }
        }
        return !isEnabled();
    }

    /**
     * @see org.opencms.workplace.list.I_CmsListDirectAction#setItem(org.opencms.workplace.list.CmsListItem)
     */
    public void setItem(CmsListItem item) {

        m_resourceUtil = ((A_CmsListExplorerDialog)getWp()).getResourceUtil(item);
        super.setItem(item);
    }

    /**
     * Returns the current resource utility.<p>
     *
     * @return the current resource utility
     */
    protected CmsResourceUtil getResourceUtil() {

        return m_resourceUtil;
    }

    /**
     * Returns the most possible right resource name.<p>
     * 
     * @return the most possible right resource name
     */
    private String getResourceName() {

        String resource = getItem().get(m_resColumnPathId).toString();
        if (!getWp().getCms().existsResource(resource, CmsResourceFilter.DEFAULT)) {
            String siteRoot = OpenCms.getSiteManager().getSiteRoot(resource);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(siteRoot)) {
                resource = resource.substring(siteRoot.length());
            }
            if (!getWp().getCms().existsResource(resource, CmsResourceFilter.DEFAULT)) {
                resource = null;
            }
        }
        return resource;
    }
}