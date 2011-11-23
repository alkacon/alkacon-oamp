/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.comments/src/com/alkacon/opencms/v8/comments/CmsCommentDynamicFields.java,v $
 * Date   : $Date: 2010/03/19 15:31:12 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.comments;

import com.alkacon.opencms.v8.formgenerator.CmsDynamicField;
import com.alkacon.opencms.v8.formgenerator.CmsForm;
import com.alkacon.opencms.v8.formgenerator.I_CmsDynamicFieldResolver;

/**
 * Resolves the values of the dynamic fields in the comments form.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.5
 */
public class CmsCommentDynamicFields implements I_CmsDynamicFieldResolver {

    /**
     * @see com.alkacon.opencms.v8.formgenerator.I_CmsDynamicFieldResolver#resolveValue(com.alkacon.opencms.v8.formgenerator.CmsDynamicField, com.alkacon.opencms.v8.formgenerator.CmsForm)
     */
    public String resolveValue(CmsDynamicField field, CmsForm form) {

        if (field.getDbLabel().equals(CmsCommentFormHandler.FIELD_IPADDRESS)) {
            return ((CmsCommentForm)form).getFormHandler().getRequestContext().getRemoteAddress();
        } else if (field.getDbLabel().equals(CmsCommentFormHandler.FIELD_LOCALE)) {
            return ((CmsCommentForm)form).getFormHandler().getRequestContext().getLocale().toString();
        } else if (field.getDbLabel().equals(CmsCommentFormHandler.FIELD_USERNAME)) {
            return ((CmsCommentForm)form).getFormHandler().getRequestContext().getCurrentUser().getName();
        } else {
            return null;
        }
    }
}
