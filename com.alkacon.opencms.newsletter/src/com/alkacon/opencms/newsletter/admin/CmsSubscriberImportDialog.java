/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.newsletter/src/com/alkacon/opencms/newsletter/admin/CmsSubscriberImportDialog.java,v $
 * Date   : $Date: 2007/10/26 13:01:14 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2007 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.newsletter.admin;

import com.alkacon.opencms.newsletter.CmsNewsletterManager;

import org.opencms.file.CmsGroup;
import org.opencms.file.CmsUser;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;
import org.opencms.widgets.CmsTextareaWidget;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWidgetDialog;
import org.opencms.workplace.CmsWidgetDialogParameter;
import org.opencms.workplace.CmsWorkplaceSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Widget dialog that imports subscriber emails to a mailing list.<p>
 * 
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.3
 */
public class CmsSubscriberImportDialog extends CmsWidgetDialog {

    /** Localized message keys prefix. */
    public static final String KEY_PREFIX = "subscriber.import";

    /** Defines which pages are valid for this dialog. */
    public static final String[] PAGES = {"page1", "page2"};

    /** The dialog object. */
    private CmsSubscriberImportObject m_importObject;

    /** Stores the value of the request parameter for the group id. */
    private String m_paramGroupid;

    /** Stores the value of the request parameter for the organizational unit fqn. */
    private String m_paramOufqn;

    /**
     * Public constructor with JSP action element.
     * <p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsSubscriberImportDialog(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Public constructor with JSP variables.<p>
     * 
     * @param context the JSP page context
     * @param req the JSP request
     * @param res the JSP response
     */
    public CmsSubscriberImportDialog(PageContext context, HttpServletRequest req, HttpServletResponse res) {

        this(new CmsJspActionElement(context, req, res));
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#actionCommit()
     */
    public void actionCommit() {

        List errors = new ArrayList();
        List emailsToSubscribe = m_importObject.getEmailAddresses();

        if (emailsToSubscribe.size() == 0) {
            errors.add(new CmsException(Messages.get().container(Messages.ERR_SUBSCRIBER_IMPORT_NO_EMAIL_0)));
        } else {
            Iterator i = emailsToSubscribe.iterator();
            while (i.hasNext()) {
                String email = (String)i.next();
                String userName = getParamOufqn() + email;
                try {
                    try {
                        getCms().readUser(userName);
                    } catch (CmsException e) {
                        // user does not exist, create it
                        CmsUser user = getCms().createUser(
                            userName,
                            CmsNewsletterManager.getPassword(),
                            "",
                            new HashMap());
                        // set the flag so that the new user does not appear in the accounts management view
                        user.setFlags(user.getFlags() ^ CmsNewsletterManager.NEWSLETTER_PRINCIPAL_FLAG);
                        user.setEmail(email);
                        getCms().writeUser(user);
                    }
                    CmsGroup mailGroup = getCms().readGroup(new CmsUUID(getParamGroupid()));
                    if (!getCms().getGroupsOfUser(userName, true, false).contains(mailGroup)) {
                        getCms().addUserToGroup(userName, mailGroup.getName());
                    }
                } catch (CmsException e) {
                    // should never happen
                    errors.add(e);
                }
            }
        }
        setCommitErrors(errors);
    }

    /**
     * Returns the group id parameter value.<p>
     * 
     * @return the group id parameter value
     */
    public String getParamGroupid() {

        return m_paramGroupid;
    }

    /**
     * Returns the organizational unit fqn parameter value.<p>
     * 
     * @return the organizational unit fqn parameter value
     */
    public String getParamOufqn() {

        return m_paramOufqn;
    }

    /**
     * Sets the group id parameter value.<p>
     * 
     * @param paramGroupid the group id parameter value
     */
    public void setParamGroupid(String paramGroupid) {

        m_paramGroupid = paramGroupid;
    }

    /**
     * Sets the organizational unit fqn parameter value.<p>
     * 
     * @param ouFqn the organizational unit fqn parameter value
     */
    public void setParamOufqn(String ouFqn) {

        if (ouFqn == null) {
            ouFqn = "";
        }
        m_paramOufqn = ouFqn;
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#createDialogHtml(java.lang.String)
     */
    protected String createDialogHtml(String dialog) {

        StringBuffer result = new StringBuffer(1024);

        // create table
        result.append(createWidgetTableStart());

        // show error header once if there were validation errors
        result.append(createWidgetErrorHeader());

        // create export file name block
        if (dialog.equals(PAGES[0])) {
            result.append(dialogBlockStart(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_HINT_BLOCK_0)));
            result.append(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_HINT_TEXT_0));
            result.append(dialogBlockEnd());
            result.append(dialogBlockStart(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_DATA_BLOCK_0)));
            result.append(createWidgetTableStart());
            result.append(createDialogRowsHtml(0, 0));
            result.append(createWidgetTableEnd());
            result.append(dialogBlockEnd());
        } else if (dialog.equals(PAGES[1])) {
            // first get email addresses to import, important because of initialization of converted and invalid lines
            List emailAddresses = m_importObject.getEmailAddresses();

            // check presence of invalid lines
            if (m_importObject.getInvalidLines().size() > 0) {
                // create notification output
                result.append(dialogBlockStart(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_INVALIDLINES_BLOCK_0)));
                result.append(dialogSpacer());
                Iterator i = m_importObject.getInvalidLines().iterator();
                while (i.hasNext()) {
                    String line = (String)i.next();
                    result.append(line);
                    if (i.hasNext()) {
                        result.append("<br/>");
                    }
                }
                result.append(dialogBlockEnd());
            }

            // check presence of converted lines
            if (m_importObject.getConvertedLines().size() > 0) {
                // create notification output
                result.append(dialogBlockStart(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_CONVERTEDLINES_BLOCK_0)));
                result.append(dialogSpacer());
                Iterator i = m_importObject.getConvertedLines().iterator();
                while (i.hasNext()) {
                    String[] line = (String[])i.next();
                    result.append(line[0]);
                    result.append(" ");
                    result.append(key(Messages.GUI_SUBSCRIBER_IMPORT_CONVERTEDLINES_TO_0));
                    result.append(" ");
                    result.append(line[1]);
                    if (i.hasNext()) {
                        result.append("<br/>");
                    }
                }
                result.append(dialogBlockEnd());
            }

            // show the email addresses that will be subscribed
            result.append(dialogBlockStart(key(Messages.GUI_SUBSCRIBER_IMPORT_LABEL_EMAILS_BLOCK_0)));
            result.append(dialogSpacer());
            if (emailAddresses.size() > 0) {
                // found at least one email address
                Iterator i = emailAddresses.iterator();
                while (i.hasNext()) {
                    String email = (String)i.next();
                    result.append(email);
                    if (i.hasNext()) {
                        result.append("<br/>");
                    }
                }

            } else {
                // no valid email address found
                result.append(key(Messages.ERR_SUBSCRIBER_IMPORT_NO_EMAIL_0));
            }
            result.append(dialogBlockEnd());
        }

        // close table
        result.append(createWidgetTableEnd());

        return result.toString();
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#defineWidgets()
     */
    protected void defineWidgets() {

        // initialize the import object
        initImportObject();

        // set localized key prefix
        setKeyPrefix(KEY_PREFIX);
        addWidget(new CmsWidgetDialogParameter(m_importObject, "importEmail", PAGES[0], new CmsTextareaWidget(8)));
    }

    /**
     * @see org.opencms.workplace.CmsWidgetDialog#getPageArray()
     */
    protected String[] getPageArray() {

        return PAGES;
    }

    /**
     * Initializes the import object to work with depending on the dialog state and request parameters.<p>
     */
    protected void initImportObject() {

        Object o;

        if (CmsStringUtil.isEmpty(getParamAction()) || CmsDialog.DIALOG_INITIAL.equals(getParamAction())) {
            // this is the initial dialog call, create a new object for the dialog
            o = null;
        } else {
            // this is not the initial call, get the object from session
            o = getDialogObject();
        }

        if (!(o instanceof CmsSubscriberImportObject)) {
            // create a new import object
            m_importObject = new CmsSubscriberImportObject();
        } else {
            // reuse import object stored in session
            m_importObject = (CmsSubscriberImportObject)o;
        }
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initMessages()
     */
    protected void initMessages() {

        // add specific dialog resource bundle
        addMessages(Messages.get().getBundleName());
        // add default resource bundles
        super.initMessages();
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings,
     *      javax.servlet.http.HttpServletRequest)
     */
    protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

        // initialize parameters and dialog actions in super implementation
        super.initWorkplaceRequestValues(settings, request);
        // set the dialog object that stores the email addresses to import
        setDialogObject(m_importObject);
    }

}