/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/CmsFormDataAccess.java,v $
 * Date   : $Date: 2008/03/18 11:34:09 $
 * Version: $Revision: 1.5 $
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

import com.alkacon.opencms.formgenerator.CmsDynamicField;
import com.alkacon.opencms.formgenerator.CmsForm;
import com.alkacon.opencms.formgenerator.CmsFormHandler;
import com.alkacon.opencms.formgenerator.I_CmsField;

import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsRfsException;
import org.opencms.util.CmsStringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.fileupload.DefaultFileItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

/**
 * Implementation of the access layer of the form data.<p>
 * 
 * @author Achim Westermann
 * @author Michael Moossen
 * 
 * @version $Revision: 1.5 $
 * 
 * @since 7.0.4
 */
public final class CmsFormDataAccess {

    /** Column name of table "CMS_WEBFORM_ENTRIES".*/
    public static final String C_COLUM_CMS_WEBFORM_ENTRIES_COUNT = "COUNT";

    /** Column name of table "CMS_WEBFORM_ENTRIES".*/
    public static final String C_COLUM_CMS_WEBFORM_ENTRIES_FORMID = "FORMID";

    /** Name of the db-pool module parameter.  */
    public static final String MODULE_PARAM_DB_POOL = "db-pool";

    /** Name of the upload folder module parameter.  */
    public static final String MODULE_PARAM_UPLOADFOLDER = "uploadfolder";

    /** Query to check if webform persistence tables exist. */
    private static final String C_CHECK_TABLES = "SELECT * FROM CMS_WEBFORM_ENTRIES LIMIT 1";

    /** Column name of table "CMS_WEBFORM_DATA".*/
    private static final String C_COLUM_CMS_WEBFORM_DATA_FIELDNAME = "FIELDNAME";

    /** Column name of table "CMS_WEBFORM_DATA".*/
    private static final String C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE = "FIELDVALUE";

    /** Query to create the database table CMS_WEBFORM_DATA. */
    private static final String C_CREATE_TABLE_CMS_WEBFORM_DATA = "CREATE TABLE CMS_WEBFORM_DATA ("
        + "REF_ID INT(11) NOT NULL, "
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + " VARCHAR(256) NOT NULL,"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + " TEXT NOT NULL, PRIMARY KEY (REF_ID, "
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + "(256)),INDEX WFD_VALUE_IDX ("
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + "(256))) ENGINE=MYISAM DEFAULT CHARSET=UTF8;";

    /** Query to create the database table CMS_WEBFORM_ENTRIES. */
    private static final String C_CREATE_TABLE_CMS_WEBFORM_ENTRIES = "CREATE TABLE CMS_WEBFORM_ENTRIES ("
        + "ENTRY_ID INT(11) NOT NULL AUTO_INCREMENT,"
        + "FORM_ID VARCHAR(256) NOT NULL,"
        + "DATE_CREATED BIGINT(20) NOT NULL,"
        + "RESOURCE_PATH VARCHAR(256) NOT NULL,"
        + "PRIMARY KEY  (ENTRY_ID), "
        + "INDEX WFE_FORMID_IDX (FORM_ID(256)), "
        + "INDEX WFE_DATE_IDX (DATE_CREATED)) "
        + "ENGINE=MYISAM DEFAULT CHARSET=UTF8;";

    /** Query to delete all distinct form names. */
    private static final String C_DELETE_FORM_DATA = "DELETE FROM CMS_WEBFORM_DATA WHERE REF_ID=?;";

    /** Query to delete all distinct form names. */
    private static final String C_DELETE_FORM_ENTRIES = "DELETE FROM CMS_WEBFORM_ENTRIES WHERE ENTRY_ID=?;";

    /** Query to read all distinct form field names of a given form in time range. */
    private static final String C_READ_FORM_FIELD_NAMES = "SELECT DISTINCT(D."
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ") FROM CMS_WEBFORM_ENTRIES E, CMS_WEBFORM_DATA D WHERE E.ENTRY_ID=D.REF_ID AND E.FORM_ID=? AND E.DATE_CREATED>? AND E.DATE_CREATED<?;";

    /** Query to read all distinct form names. */
    private static final String C_READ_FORM_NAMES = "SELECT COUNT(*) AS "
        + C_COLUM_CMS_WEBFORM_ENTRIES_COUNT
        + ", FORM_ID AS "
        + C_COLUM_CMS_WEBFORM_ENTRIES_FORMID
        + " FROM CMS_WEBFORM_ENTRIES GROUP BY FORM_ID;";

    /** Query to read all fields and their values that have been submitted in a single webform submission. */
    private static final String C_READ_FORM_SUBMISSION_DATA = "SELECT "
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ","
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + " FROM CMS_WEBFORM_DATA WHERE REF_ID=?";

    /** Query to read all submission IDS of a given form in time range. */
    private static final String C_READ_FORM_SUBMISSION_IDS = "SELECT ENTRY_ID, DATE_CREATED, RESOURCE_PATH "
        + "FROM CMS_WEBFORM_ENTRIES "
        + "WHERE FORM_ID=? AND DATE_CREATED>? AND DATE_CREATED<?;";

    /** Query to read all submission IDS of a given form matching the field name/value pair. */
    private static final String C_READ_FORMS_FOR_FIELD_VALUE = "SELECT WFE.ENTRY_ID, WFE.DATE_CREATED, WFE.RESOURCE_PATH "
        + "FROM CMS_WEBFORM_ENTRIES WFE, CMS_WEBFORM_DATA WFD "
        + "WHERE WFE.FORM_ID=? AND WFE.ENTRY_ID = WFD.REF_ID AND WFD."
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + "=? AND WFD."
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + "=?;";

    /** Query to read the last auto - generated ID in this session. */
    private static final String C_READ_LAST_SUBMISSION_ID = "SELECT ENTRY_ID "
        + "FROM CMS_WEBFORM_ENTRIES "
        + "WHERE FORM_ID=? AND DATE_CREATED=?";

    /** Query to write a new field and value to CMS_WEBFORM_DATA that is related to a form submission in CMS_WEBFORM_ENTRIES (ref_id has to match entry_id). */
    private static final String C_WRITE_FORM_DATA = "INSERT INTO CMS_WEBFORM_DATA (REF_ID,"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ","
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + ") VALUES (?,?,?)";

    /** Query to write a new form submission into CMS_WEBFORM_ENTRIES. */
    private static final String C_WRITE_FORM_SUBMISSION = "INSERT INTO CMS_WEBFORM_ENTRIES(FORM_ID,DATE_CREATED,RESOURCE_PATH) VALUES (?,?,?)";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormDataAccess.class);

    /** The singleton object. */
    private static CmsFormDataAccess m_instance;

    /** The corresponding module name to read parameters of.  */
    private static final String MODULE = "com.alkacon.opencms.formgenerator";

    /**
     * Singleton access.<p>
     * 
     * @return the singleton object
     */
    public static synchronized CmsFormDataAccess getInstance() {

        if (m_instance == null) {
            m_instance = new CmsFormDataAccess();
        }
        return m_instance;
    }

    /** The connection pool id. */
    private String m_connectionPool;

    /**
     * Default constructor.<p>
     */
    private CmsFormDataAccess() {

        CmsModule module = OpenCms.getModuleManager().getModule(MODULE);
        if (module == null) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {MODULE}));
        }
        m_connectionPool = module.getParameter(MODULE_PARAM_DB_POOL);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_connectionPool)) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                new Object[] {MODULE_PARAM_DB_POOL, MODULE}));
        }
    }

    /**
     * Delete's the form with all fields and data's.<p>
     * 
     * @param formId to find the form data in the database 
     * 
     * @throws SQLException if something goes wrong
     */
    public void deleteFormEntries(final String formId) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            // delete the entries
            con = getConnection();
            stmt = con.prepareStatement(C_DELETE_FORM_ENTRIES);
            stmt.setString(1, formId);
            stmt.executeUpdate();

            // delete the data
            closeAll(null, stmt, null);
            stmt = con.prepareStatement(C_DELETE_FORM_DATA);
            stmt.setString(1, formId);
            stmt.executeUpdate();

        } finally {
            closeAll(null, stmt, con);
        }

    }

    /**
     * Creates the database tables for the webform data if they 
     * do not exist.<p>
     * 
     * @throws SQLException if sth goes wrong
     */
    public void ensureDBTablesExistance() throws SQLException {

        if (!existsDBTables()) {
            createDBTables();
        }
    }

    /**
     * Returns true if the db tables for the webform data exist.<p> 
     * 
     * @return true if the db tables for the webform data exist
     * 
     * @throws SQLException if problems with the db connectivity occur
     */
    public boolean existsDBTables() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement(C_CHECK_TABLES);
            try {
                stmt.executeQuery();
                return true;
            } catch (Exception ex) {
                LOG.info(Messages.get().getBundle().key(Messages.LOG_INFO_DATAACESS_SQL_TABLE_NOTEXISTS_0), ex);
            }
        } finally {
            closeAll(null, stmt, con);
        }
        return false;
    }

    /**
     * Read a <code>List&lt;{@link CmsFormDataBean}&gt;</code> with  all 
     * data submitted with the given form with the given field name/value pair.<p>
     * 
     * Each <code>{@link CmsFormDataBean}</code> is a set of field values 
     * that was entered to the webform in a single submit.<p>
     * 
     * @param formId to find the form data in the database 
     * @param fieldName the name of the field to match
     * @param fieldValue the value of the field to match
     * 
     * @return a <code>List&lt;{@link CmsFormDataBean}&gt;</code> for all 
     *      data submitted with the given form.
     *      
     * @throws SQLException if sth goes wrong 
     */
    public List getFormsForFieldValue(String formId, String fieldName, String fieldValue) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List result = new ArrayList();

        try {
            con = getConnection();
            stmt = con.prepareStatement(C_READ_FORMS_FOR_FIELD_VALUE);

            stmt.setString(1, formId);
            stmt.setString(2, fieldName);
            stmt.setString(3, fieldValue);

            rs = stmt.executeQuery();

            // collect the submissions with the given values: 
            Map forms = new HashMap();
            while (rs.next()) {
                String entryId = rs.getString("ENTRY_ID");
                CmsFormDataBean formData = new CmsFormDataBean();
                formData.setFormId(entryId);
                formData.setDateCreated(Long.parseLong(rs.getString("DATE_CREATED")));
                formData.setResourcePath(rs.getString("RESOURCE_PATH"));
                forms.put(entryId, formData);
            }
            // close result set and statement, connection is needed for next statement:
            closeAll(rs, stmt, null);

            // 2) Read all Data sets that have been submitted:
            Iterator itForms = forms.entrySet().iterator();
            // reuse the same statement with different variables: 
            stmt = con.prepareStatement(C_READ_FORM_SUBMISSION_DATA);
            while (itForms.hasNext()) {
                Map.Entry entry = (Entry)itForms.next();
                String submissionId = (String)entry.getKey();
                stmt.setString(1, submissionId);

                CmsFormDataBean formData = (CmsFormDataBean)entry.getValue();
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String fName = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDNAME);
                    String fValue = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE);
                    formData.addField(fName, fValue);
                }
                result.add(formData);
            }
            return result;
        } finally {
            closeAll(rs, stmt, con);
        }
    }

    /**
     * Read a <code>List&lt;{@link String}&gt;</code> with all 
     * distinct form field names submitted with the given form in the 
     * given time range.<p>
     * 
     * @param formId to find the form data in the database 
     * @param start the start time to find data 
     * @param end the end time to find data 
     * 
     * @return a <code>List&lt;{@link String}&gt;</code> with all 
     *      distinct form field names submitted with the given form in the 
     *      given time range
     *      
     * @throws SQLException if sth goes wrong 
     */
    public List readAllFormFieldNames(final String formId, Date start, Date end) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List result = new ArrayList();
        try {
            con = getConnection();
            stmt = con.prepareStatement(C_READ_FORM_FIELD_NAMES);
            // FORM_ID <-> CmsForm.formId:
            stmt.setString(1, formId);
            // DATE_CREATED cp. with start time:
            stmt.setLong(2, start.getTime());
            // DATE_CREATED cp. with end time:
            stmt.setLong(3, end.getTime());

            rs = stmt.executeQuery();

            // collect the submissions in timerange: 
            String fieldName;
            while (rs.next()) {
                fieldName = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDNAME);
                result.add(fieldName);
            }
        } finally {
            closeAll(rs, stmt, con);
        }
        return result;
    }

    /**
     * Read a <code>List&lt;{@link CmsFormDataBean}&gt;</code> with all distinct form names.<p>
     * 
     * 
     * @return a <code>List&lt;{@link CmsFormDataBean}&gt;</code> with all distinct form field names 
     *      
     * @throws SQLException if something goes wrong 
     */
    public List readAllFormNames() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List result = new ArrayList();
        try {
            con = getConnection();
            stmt = con.prepareStatement(C_READ_FORM_NAMES);
            rs = stmt.executeQuery();

            // collect the submissions: 
            CmsFormDataBean data;
            while (rs.next()) {
                data = new CmsFormDataBean();
                data.addField(C_COLUM_CMS_WEBFORM_ENTRIES_COUNT, rs.getString(C_COLUM_CMS_WEBFORM_ENTRIES_COUNT));
                data.addField(C_COLUM_CMS_WEBFORM_ENTRIES_FORMID, rs.getString(C_COLUM_CMS_WEBFORM_ENTRIES_FORMID));
                result.add(data);
            }
        } finally {
            closeAll(rs, stmt, con);
        }
        return result;
    }

    /**
     * Read a <code>List&lt;{@link CmsFormDataBean}&gt;</code> with  all 
     * data submitted with the given form in the given time range.<p>
     * 
     * Each <code>{@link CmsFormDataBean}</code> is a set of field values 
     * that was entered to the webform in a single submit.<p>
     * 
     * @param formId to find the form data in the database 
     * @param start the start time to find data 
     * @param end the end time to find data 
     * 
     * @return a <code>List&lt;{@link CmsFormDataBean}&gt;</code> for all 
     *      data submitted with the given form.
     *      
     * @throws SQLException if sth goes wrong 
     */
    public List readFormData(final String formId, Date start, Date end) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List result = new ArrayList();

        try {
            // 1) Read all submission ids in time range: 
            con = getConnection();
            stmt = con.prepareStatement(C_READ_FORM_SUBMISSION_IDS);
            // FORM_ID <-> CmsForm.formId:
            stmt.setString(1, formId);
            // DATE_CREATED cp. with start time:
            stmt.setLong(2, start.getTime());
            // DATE_CREATED cp. with end time:
            stmt.setLong(3, end.getTime());

            rs = stmt.executeQuery();

            // collect the submissions with the given values: 
            Map forms = new HashMap();
            while (rs.next()) {
                String entryId = rs.getString("ENTRY_ID");
                CmsFormDataBean formData = new CmsFormDataBean();
                formData.setFormId(entryId);
                formData.setDateCreated(Long.parseLong(rs.getString("DATE_CREATED")));
                formData.setResourcePath(rs.getString("RESOURCE_PATH"));
                forms.put(entryId, formData);
            }
            // close result set and statement, connection is needed for next statement:
            closeAll(rs, stmt, null);

            // 2) Read all Data sets that have been submitted:
            Iterator itForms = forms.entrySet().iterator();
            // reuse the same statement with different variables: 
            stmt = con.prepareStatement(C_READ_FORM_SUBMISSION_DATA);
            while (itForms.hasNext()) {
                Map.Entry entry = (Entry)itForms.next();
                String submissionId = (String)entry.getKey();
                stmt.setString(1, submissionId);

                CmsFormDataBean formData = (CmsFormDataBean)entry.getValue();
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String fName = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDNAME);
                    String fValue = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE);
                    formData.addField(fName, fValue);
                }
                result.add(formData);
            }
        } finally {
            closeAll(rs, stmt, con);
        }
        return result;
    }

    /**
     * Persists the values of the given form.<p>
     * 
     * Implementations should log underlying exceptions.<p>
     * 
     * @param formHandler the form handler containing the form to persist. 
     * 
     * @return true if successful 
     * 
     * @throws SQLException if sth goes wrong 
     * 
     * @see com.alkacon.opencms.formgenerator.CmsForm#getAllFields()
     */
    public boolean writeFormData(CmsFormHandler formHandler) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            // 1) Write a new entry for the submission with form id, path and time stamp 
            // get the form id -> PK in database
            stmt = con.prepareStatement(C_WRITE_FORM_SUBMISSION);

            CmsForm form = formHandler.getFormConfiguration();
            String formId = form.getFormId();
            long dateCreated = System.currentTimeMillis();
            String resourcePath = formHandler.getRequestContext().addSiteRoot(formHandler.getRequestContext().getUri());

            stmt.setString(1, formId);
            stmt.setLong(2, dateCreated);
            stmt.setString(3, resourcePath);
            int rc = stmt.executeUpdate();
            if (rc != 1) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATAACCESS_SQL_WRITE_SUBMISSION_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}));
                return false;
            }

            // connection is still needed, so only close statement
            closeAll(null, stmt, null);
            // 2) Read the ID of the new submission entry to relate all data in the data table to: 

            int lastSubmissionId = -1;
            stmt = con.prepareStatement(C_READ_LAST_SUBMISSION_ID);
            stmt.setString(1, formId);
            stmt.setLong(2, dateCreated);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATACCESS_SQL_READ_SUBMISSION_ID_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}));
                return false;
            }
            lastSubmissionId = rs.getInt(1);
            // connection is still needed, so only close statement and result set 
            closeAll(rs, stmt, null);

            // 3) Now insert the data values for this submission with that ref_id: 
            stmt = con.prepareStatement(C_WRITE_FORM_DATA);
            // loop over all form fields: 
            List formFields = form.getAllFields();
            Iterator itFormFields = formFields.iterator();
            while (itFormFields.hasNext()) {
                I_CmsField field = (I_CmsField)itFormFields.next();
                String fieldName = field.getDbLabel();
                // returns null if we do not deal with a CmsUploadFileItem: 
                DefaultFileItem fileItem = (DefaultFileItem)formHandler.getUploadFile(field);
                String fieldValue;
                if (fileItem != null) {
                    // save the location of the file and 
                    // store it from the temp file to a save place: 
                    File uploadFile = storeFile(fileItem, formHandler);
                    fieldValue = uploadFile.getAbsolutePath();
                } else if (field instanceof CmsDynamicField) {
                    fieldValue = formHandler.getFormConfiguration().getFieldStringValueByName(field.getName());
                } else {
                    fieldValue = field.getValue();
                }

                stmt.setInt(1, lastSubmissionId);
                stmt.setString(2, fieldName);
                stmt.setString(3, fieldValue);

                /*
                 * At this level we can allow to loose a field value and try 
                 * to save the others instead of failing everything. 
                 */
                try {
                    rc = stmt.executeUpdate();
                } catch (SQLException sqlex) {
                    LOG.error(
                        Messages.get().getBundle().key(
                            Messages.LOG_ERR_DATAACCESS_SQL_WRITE_FIELD_3,
                            new Object[] {fieldName, fieldValue, formHandler.createMailTextFromFields(false, false)}),
                        sqlex);

                }
                if (rc != 1) {
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERR_DATAACCESS_SQL_WRITE_FIELD_3,
                        new Object[] {fieldName, fieldValue, formHandler.createMailTextFromFields(false, false)}));
                }
            }
        } finally {
            closeAll(rs, stmt, con);
        }
        return true;
    }

    /**
     * This method closes the result sets and statement and connections.<p>
     * 
     * @param res The result set.
     * @param statement The statement.
     * @param con The connection.
     */
    private void closeAll(ResultSet res, Statement statement, Connection con) {

        // result set
        if (res != null) {
            try {
                res.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(CmsException.getStackTraceAsString(e));
                }
            }
        }
        // statement
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(CmsException.getStackTraceAsString(e));
                }
            }
        }
        // connection
        if (con != null) {
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(CmsException.getStackTraceAsString(e));
                }
            }
        }
    }

    /**
     * Unconditionally tries to create the db tables needed for form data.<p>
     * 
     * @throws SQLException if sth goes wrong 
     */
    private void createDBTables() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(C_CREATE_TABLE_CMS_WEBFORM_ENTRIES);
            int rc = stmt.executeUpdate();
            if (rc != 0) {
                LOG.warn(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATACCESS_SQL_CREATE_TABLE_RETURNCODE_2,
                    new Object[] {new Integer(rc), "CMS_WEBFORM_ENTRIES"}));
            }
            closeAll(null, stmt, null);
            stmt = con.prepareStatement(C_CREATE_TABLE_CMS_WEBFORM_DATA);
            rc = stmt.executeUpdate();
            if (rc != 0) {
                LOG.warn(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATACCESS_SQL_CREATE_TABLE_RETURNCODE_2,
                    new Object[] {new Integer(rc), "CMS_WEBFORM_DATA"}));
            }
        } finally {
            closeAll(null, stmt, con);
        }
    }

    /**
     * Returns a connection to the db pool configured in parameter "db-pool" of module 
     * "com.alkacon.opencms.formgenerator".<p>
     * 
     * @return a connection to the db pool configured in parameter "db-pool" of module 
     *      "com.alkacon.opencms.formgenerator"
     *      
     * @throws SQLException if sth goes wrong 
     */
    private Connection getConnection() throws SQLException {

        return OpenCms.getSqlManager().getConnection(m_connectionPool);
    }

    /**
     * Stores the content of the given file to a 
     * place specified by the module parameter "uploadfolder".<p>
     * 
     * The content of the upload file item is only inside a temporary file. 
     * This must be called, when the form submission is stored to the database 
     * as the content would be lost.<p>
     * 
     * @param item the upload file item to store 
     * @param formHandler only used for exception logging 
     * 
     * @return the file were the content is stored 
     */
    private File storeFile(FileItem item, CmsFormHandler formHandler) {

        File storeFile = null;
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE);
        if (module == null) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {MODULE}));
        }
        String filePath = module.getParameter(MODULE_PARAM_UPLOADFOLDER);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(filePath)) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                new Object[] {MODULE_PARAM_UPLOADFOLDER, MODULE}));
        }
        try {
            File folder = new File(filePath);
            CmsFileUtil.assertFolder(folder, CmsFileUtil.MODE_READ, true);
            storeFile = new File(folder, item.getName());
            byte[] contents = item.get();
            try {
                OutputStream out = new FileOutputStream(storeFile);
                out.write(contents);
                out.flush();
                out.close();
            } catch (IOException e) {
                // should never happen
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATAACCESS_UPLOADFILE_LOST_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}), e);
            }
        } catch (CmsRfsException ex) {
            LOG.error(Messages.get().getBundle().key(
                Messages.LOG_ERR_DATAACCESS_UPLOADFILE_LOST_1,
                new Object[] {formHandler.createMailTextFromFields(false, false)}), ex);
        }
        return storeFile;
    }
}
