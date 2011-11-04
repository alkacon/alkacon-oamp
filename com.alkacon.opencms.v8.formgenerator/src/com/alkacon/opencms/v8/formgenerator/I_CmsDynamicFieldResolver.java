/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/I_CmsDynamicFieldResolver.java,v $
 * Date   : $Date: 2010/03/19 15:31:11 $
 * Version: $Revision: 1.3 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.formgenerator;

/**
 * Interface to fill the value of a dynamic field.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.3 $
 * 
 * @since 7.0.4 
 */
public interface I_CmsDynamicFieldResolver {

    /**
     * Resolves the value of a given dynamic field.<p>
     * 
     * @param field the field to generate the value for 
     * @param form the whole form
     * 
     * @return the value of a given dynamic field
     */
    String resolveValue(CmsDynamicField field, CmsForm form);
}
