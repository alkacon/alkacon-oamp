/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/CmsFormDataAccess.java,v $
 * Date   : $Date: 2007/12/21 14:34:01 $
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
package com.alkacon.opencms.formgenerator.database;

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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.DefaultFileItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

/**
 * Implementation of the access layer of the form data.<p>
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 * 
 */
public class CmsFormDataAccess implements I_CmsFormDataAccess {

    /**
     * Query to check if webform persistence tables exist.
     */
    private static final String C_CHECK_TABLES = "SELECT * FROM CMS_WEBFORM_ENTRIES LIMIT 1";

    /** Column name of table "CMS_WEBFORM_DATA".*/
    private static final String C_COLUM_CMS_WEBFORM_DATA_FIELDNAME = "FIELDNAME";

    /** Column name of table "CMS_WEBFORM_DATA".*/
    private static final String C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE = "FIELDVALUE";

    /**
     * Query to create the database table CMS_WEBFORM_DATA.
     */
    private static final String C_CREATE_TABLE_CMS_WEBFORM_DATA = "CREATE TABLE `CMS_WEBFORM_DATA` (`REF_ID` int(11) NOT NULL COMMENT 'References CMS_WEBFORM_ENTRIES.ENTRY_ID',`"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + "` varchar(256) NOT NULL,`"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + "` text NOT NULL,PRIMARY KEY  (`REF_ID`,`"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + "`(256))) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Contains data of submitted Webforms'";

    /**
     * Query to create the database table CMS_WEBFORM_ENTRIES.
     */
    private static final String C_CREATE_TABLE_CMS_WEBFORM_ENTRIES = "CREATE TABLE `CMS_WEBFORM_ENTRIES` (`ENTRY_ID` int(11) NOT NULL auto_increment COMMENT 'Defines the specific submission of data on the form.',`FORM_ID` text NOT NULL,`DATE_CREATED` bigint(20) NOT NULL default '0',`RESOURCE_PATH` text character set utf8 collate utf8_bin NOT NULL,PRIMARY KEY  (`ENTRY_ID`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Contains information about submitted Webforms'";

    /**
     * Query to read all distinct form field names of a given form in time range.
     */
    private static final String C_READ_FORM_FIELD_NAMES = "SELECT DISTINCT(D."
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ") FROM CMS_WEBFORM_ENTRIES E, CMS_WEBFORM_DATA D WHERE E.ENTRY_ID=D.REF_ID AND E.FORM_ID=? AND E.DATE_CREATED>? AND E.DATE_CREATED<?;";

    /**
     * Query to read all fields and their values that have been submitted in a single webform submission.
     */
    private static final String C_READ_FORM_SUBMISSION_DATA = "SELECT "
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ","
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + " FROM CMS_WEBFORM_DATA WHERE REF_ID=?";

    /**
     * Query to read all submission IDS of a given form in time range.
     */
    private static final String C_READ_FORM_SUBMISSION_IDS = "SELECT ENTRY_ID FROM CMS_WEBFORM_ENTRIES WHERE FORM_ID=? AND DATE_CREATED>? AND DATE_CREATED<?;";

    /**
     * Query to read the last auto - generated ID in this session . 
     */
    private static final String C_READ_LAST_SUBMISSION_ID = "SELECT ENTRY_ID FROM CMS_WEBFORM_ENTRIES WHERE FORM_ID=? AND DATE_CREATED=?";

    /**
     * Query to write a new field and value to CMS_WEBFORM_DATA that is related to a form submission in CMS_WEBFORM_ENTRIES (ref_id has to match entry_id). 
     */
    private static final String C_WRITE_FORM_DATA = "INSERT INTO CMS_WEBFORM_DATA (REF_ID,"
        + C_COLUM_CMS_WEBFORM_DATA_FIELDNAME
        + ","
        + C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE
        + ") VALUES (?,?,?)";

    /**
     * Query to write a new form submission into CMS_WEBFORM_ENTRIES.
     */
    private static final String C_WRITE_FORM_SUBMISSION = "INSERT INTO CMS_WEBFORM_ENTRIES(FORM_ID,DATE_CREATED,RESOURCE_PATH) VALUES (?,?,?)";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormDataAccess.class);

    /** The corresponding module name to read parameters of.  */
    private static final String MODULE = "com.alkacon.opencms.formgenerator";

    /** Name of the db-pool module parameter.  */
    public static final String MODULE_PARAM_DB_POOL = "db-pool";

    /** Name of the upload folder module parameter.  */
    public static final String MODULE_PARAM_UPLOADFOLDER = "uploadfolder";

    /** The connection pool id. */
    private String m_connectionPool;

    /**
     * Default constructor.<p>
     */
    public CmsFormDataAccess() {

        super();
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE);
        if (module == null) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {MODULE}));
        }
        this.m_connectionPool = module.getParameter(MODULE_PARAM_DB_POOL);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(this.m_connectionPool)) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                new Object[] {MODULE_PARAM_DB_POOL, MODULE}));
        }
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

        CmsFormDataAccess access = new CmsFormDataAccess();
        Connection con = null;
        Statement stmt = null;
        try {
            con = access.getConnection();
            stmt = con.createStatement();
            int rc = stmt.executeUpdate(C_CREATE_TABLE_CMS_WEBFORM_ENTRIES);
            if (rc != 0) {
                LOG.warn(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATACCESS_SQL_CREATE_TABLE_RETURNCODE_2,
                    new Object[] {new Integer(rc), "CMS_WEBFORM_ENTRIES"}));
            }
            rc = stmt.executeUpdate(C_CREATE_TABLE_CMS_WEBFORM_DATA);
            if (rc != 0) {
                LOG.warn(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATACCESS_SQL_CREATE_TABLE_RETURNCODE_2,
                    new Object[] {new Integer(rc), "CMS_WEBFORM_DATA"}));
            }
        } finally {
            this.closeAll(null, stmt, con);
        }
    }

    /**
     * @see com.alkacon.opencms.formgenerator.database.I_CmsFormDataAccess#ensureDBTablesExistance()
     */
    public void ensureDBTablesExistance() throws SQLException {

        boolean existsDBTables = this.existsDBTables();
        if (!existsDBTables) {
            this.createDBTables();
        }

    }

    /**
     * @see com.alkacon.opencms.formgenerator.database.I_CmsFormDataAccess#existsDBTables()
     */
    public boolean existsDBTables() throws SQLException {

        boolean result = false;
        CmsFormDataAccess access = new CmsFormDataAccess();
        Connection con = null;
        Statement stmt = null;
        try {
            con = access.getConnection();
            stmt = con.createStatement();
            try {
                stmt.executeQuery(C_CHECK_TABLES);
                result = true;
            } catch (Exception ex) {
                LOG.info(Messages.get().getBundle().key(Messages.LOG_INFO_DATAACESS_SQL_TABLE_NOTEXISTS_0), ex);
            }
        } finally {
            this.closeAll(null, stmt, con);
        }
        return result;
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
    Connection getConnection() throws SQLException {

        return OpenCms.getSqlManager().getConnection(this.m_connectionPool);
    }

    /**
     * @see com.alkacon.opencms.formgenerator.database.I_CmsFormDataAccess#readAllFormFieldNames(com.alkacon.opencms.formgenerator.CmsFormHandler, java.util.Date, java.util.Date)
     */
    public List readAllFormFieldNames(final CmsFormHandler formHandler, Date start, Date end) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List result = new ArrayList();
        CmsForm form = formHandler.getFormConfiguration();
        try {
            con = this.getConnection();
            stmt = con.prepareStatement(C_READ_FORM_FIELD_NAMES);
            // FORM_ID <-> CmsForm.formId:
            stmt.setString(1, form.getFormId());
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
            return result;
        } finally {
            this.closeAll(null, stmt, con);
        }
    }

    /**
     * @see com.alkacon.opencms.formgenerator.database.I_CmsFormDataAccess#readFormData(com.alkacon.opencms.formgenerator.CmsFormHandler, java.util.Date, java.util.Date)
     */
    public List readFormData(final CmsFormHandler formHandler, Date start, Date end) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List result = new ArrayList();
        CmsForm form = formHandler.getFormConfiguration();
        try {
            // 1) Read all submission ids in time range: 
            con = this.getConnection();
            stmt = con.prepareStatement(C_READ_FORM_SUBMISSION_IDS);
            // FORM_ID <-> CmsForm.formId:
            stmt.setString(1, form.getFormId());
            // DATE_CREATED cp. with start time:
            stmt.setLong(2, start.getTime());
            // DATE_CREATED cp. with end time:
            stmt.setLong(3, end.getTime());

            rs = stmt.executeQuery();

            // collect the submissions in time range: 
            List submissionIds = new ArrayList();
            String submissionId;
            while (rs.next()) {
                submissionId = rs.getString("ENTRY_ID");
                submissionIds.add(submissionId);
            }
            // close result set and statement, connection is needed for next statement:
            this.closeAll(rs, stmt, null);

            // 2) Read all Data sets that have been submitted:
            Iterator itSubmissionId = submissionIds.iterator();
            // reuse the same statement with different variables: 
            stmt = con.prepareStatement(C_READ_FORM_SUBMISSION_DATA);
            CmsFormDataBean formSubmission;
            CmsFormDataEntry formField;
            String fieldName;
            String fieldValue;
            while (itSubmissionId.hasNext()) {
                formSubmission = new CmsFormDataBean();
                submissionId = (String)itSubmissionId.next();
                stmt.setString(1, submissionId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    fieldName = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDNAME);
                    fieldValue = rs.getString(C_COLUM_CMS_WEBFORM_DATA_FIELDVALUE);
                    formField = new CmsFormDataEntry(fieldName, fieldValue);
                    formSubmission.addEntry(formField);
                }
                result.add(formSubmission);
            }
            return result;
        } finally {
            this.closeAll(null, stmt, con);
        }
    }

    /**
     * Stores the content of the given file to a 
     * place specified by the module parameter "uploadfolder".
     * <p>
     * The content of the upload file item is only inside a temporary file. 
     * This must be called, when the form submission is stored to the database 
     * as the content would be lost. 
     * <p>
     * 
     * @param item the upload file item to store 
     * 
     * @param formHandler only used for exception logging 
     * 
     * @return the file were the content is stored 
     */
    private File storeFile(FileItem item, CmsFormHandler formHandler) {

        File storeFile = null;
        CmsModule module = OpenCms.getModuleManager().getModule(MODULE);
        if (module != null) {
            String filePath = module.getParameter(MODULE_PARAM_UPLOADFOLDER);
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(filePath)) {
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
            } else {
                throw new CmsRuntimeException(Messages.get().container(
                    Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                    new Object[] {MODULE_PARAM_UPLOADFOLDER, MODULE}));
            }
        } else {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {MODULE}));
        }
        return storeFile;
    }

    /**
     * @see com.alkacon.opencms.formgenerator.database.I_CmsFormDataAccess#writeFormData(com.alkacon.opencms.formgenerator.CmsFormHandler)
     */
    public boolean writeFormData(CmsFormHandler formHandler) throws SQLException {

        // get the form id -> PK in database
        CmsForm form = formHandler.getFormConfiguration();
        String formId = form.getFormId();
        // 1) Write a new entry for the submission with form id, path and time stamp 
        long dateCreated = System.currentTimeMillis();
        String resourcePath = formHandler.getRequestContext().getUri();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = true;
        try {
            con = this.getConnection();
            stmt = con.prepareStatement(C_WRITE_FORM_SUBMISSION);
            stmt.setString(1, formId);
            stmt.setLong(2, dateCreated);
            stmt.setString(3, resourcePath);
            int rc = stmt.executeUpdate();
            if (rc != 1) {
                result = false;
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATAACCESS_SQL_WRITE_SUBMISSION_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}));
            } else {
                // connection is still needed, so only close statement
                this.closeAll(null, stmt, null);
                // 2) Read the ID of the new submission entry to relate all data in the data table to: 

                int lastSubmissionId = -1;
                stmt = con.prepareStatement(C_READ_LAST_SUBMISSION_ID);
                stmt.setString(1, formId);
                stmt.setLong(2, dateCreated);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    lastSubmissionId = rs.getInt(1);
                    // connection is still needed, so only close statement and result set 
                    this.closeAll(rs, stmt, null);

                    // 3) Now insert the data values for this submission with that ref_id: 
                    stmt = con.prepareStatement(C_WRITE_FORM_DATA);
                    // loop over all form fields: 
                    List formFields = form.getFields();
                    Iterator itFormFields = formFields.iterator();
                    I_CmsField field;
                    String fieldName;
                    String fieldValue;
                    DefaultFileItem fileItem;
                    while (itFormFields.hasNext()) {
                        field = (I_CmsField)itFormFields.next();
                        fieldName = field.getLabel();
                        // returns null if we do not deal with a CmsUploadFileItem: 
                        fileItem = (DefaultFileItem)formHandler.getUploadFile(field);
                        if (fileItem != null) {
                            // save the location of the file and 
                            // store it from the temp file to a save place: 
                            File uploadFile = storeFile(fileItem, formHandler);
                            fieldValue = uploadFile.getAbsolutePath();

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
                                    new Object[] {
                                        fieldName,
                                        fieldValue,
                                        formHandler.createMailTextFromFields(false, false)}),
                                sqlex);

                        }
                        if (rc != 1) {
                            result = false;
                            Messages.get().getBundle().key(
                                Messages.LOG_ERR_DATAACCESS_SQL_WRITE_FIELD_3,
                                new Object[] {fieldName, fieldValue, formHandler.createMailTextFromFields(false, false)});
                        }

                    }
                } else {
                    result = false;
                    LOG.error(Messages.get().getBundle().key(
                        Messages.LOG_ERR_DATACCESS_SQL_READ_SUBMISSION_ID_1,
                        new Object[] {formHandler.createMailTextFromFields(false, false)}));
                }
            }
            return result;
        } finally {
            this.closeAll(rs, stmt, null);
        }
    }
}
