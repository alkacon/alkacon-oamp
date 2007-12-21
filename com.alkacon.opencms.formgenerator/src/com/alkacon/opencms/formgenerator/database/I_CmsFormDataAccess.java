/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/Attic/I_CmsFormDataAccess.java,v $
 * Date   : $Date: 2007/12/21 15:24:23 $
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
package com.alkacon.opencms.formgenerator.database;

import com.alkacon.opencms.formgenerator.CmsFormHandler;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Defines an API to access form data that was stored (to the database).<p>
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4
 *
 */
public interface I_CmsFormDataAccess {

    /**
     * Creates the database tables for the webform data if they 
     * do not exist.<p>
     * 
     * @throws SQLException if sth goes wrong
     * 
     */
    void ensureDBTablesExistance() throws SQLException;

    /**
     * Returns true if the db tables for the webform data exist.<p> 
     * 
     * @return true if the db tables for the webform data exist
     * 
     * @throws SQLException if problems with the db connectivity occur
     */
    boolean existsDBTables() throws SQLException;

    /**
     * Read a <code>List&lt;{@link String}&gt;</code> with all 
     * distinct form field names submitted with the given form in the 
     * given time range.<p>
     * 
     * @param form use <code>{@link CmsFormHandler#getFormConfiguration()}</code> 
     *      and <code>{@link com.alkacon.opencms.formgenerator.CmsForm#getFormId()}</code> to find the form data in the database 
     * 
     * @param start the start time to find data 
     * 
     * @param end the end time to find data 
     * 
     * @return a <code>List&lt;{@link String}&gt;</code> with all 
     *      distinct form field names submitted with the given form in the 
     *      given time range
     *      
     * @throws SQLException if sth goes wrong 
     */
    List readAllFormFieldNames(CmsFormHandler form, Date start, Date end) throws SQLException;

    /**
     * Read a <code>List&lt;{@link CmsFormDataBean}&gt;</code> with  all 
     * data submitted with the given form in the given time range.<p>
     * 
     * Each <code>{@link CmsFormDataBean}</code> is a set of field values 
     * that was entered to the webform in a single submit.<p>
     * 
     * @param form use <code>{@link CmsFormHandler#getFormConfiguration()}</code> 
     *      and <code>{@link com.alkacon.opencms.formgenerator.CmsForm#getFormId()}</code> to find the form data in the database 
     * 
     * @param start the start time to find data 
     * 
     * @param end the end time to find data 
     * 
     * @return a <code>List&lt;{@link CmsFormDataBean}&gt;</code> for all 
     *      data submitted with the given form.
     *      
     * @throws SQLException if sth goes wrong 
     *      
     */
    List readFormData(CmsFormHandler form, Date start, Date end) throws SQLException;

    /**
     * Persists the values of the given form.<p>
     * 
     * Implementations should log underlying exceptions.<p>
     * 
     * @param form the form handler containing the form to persist. 
     * 
     * @see com.alkacon.opencms.formgenerator.CmsForm#getFields()
     * 
     * @return true if successful 
     * 
     * @throws SQLException if sth goes wrong 
     */
    boolean writeFormData(CmsFormHandler form) throws SQLException;
}
