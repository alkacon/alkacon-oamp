/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/database/CmsFormDataAccess.java,v $
 * Date   : $Date: 2011/03/21 11:43:46 $
 * Version: $Revision: 1.18 $
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

package com.alkacon.opencms.v8.formgenerator.database;

import com.alkacon.opencms.v8.formgenerator.CmsDisplayField;
import com.alkacon.opencms.v8.formgenerator.CmsDynamicField;
import com.alkacon.opencms.v8.formgenerator.CmsEmptyField;
import com.alkacon.opencms.v8.formgenerator.CmsFieldItem;
import com.alkacon.opencms.v8.formgenerator.CmsForm;
import com.alkacon.opencms.v8.formgenerator.CmsFormHandler;
import com.alkacon.opencms.v8.formgenerator.CmsHiddenDisplayField;
import com.alkacon.opencms.v8.formgenerator.CmsPagingField;
import com.alkacon.opencms.v8.formgenerator.CmsTableField;
import com.alkacon.opencms.v8.formgenerator.I_CmsField;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.types.CmsResourceTypeFolder;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsRfsException;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

/**
 * Implementation of the access layer of the form data.<p>
 * 
 * @author Achim Westermann
 * @author Michael Moossen
 * 
 * @version $Revision: 1.18 $
 * 
 * @since 7.0.4
 */
public final class CmsFormDataAccess {

    /** Database column name constant. */
    private static final String DB_COUNT = "COUNT";

    /** Database column name constant. */
    private static final String DB_DATE_CREATED = "DATE_CREATED";

    /** Database column name constant. */
    private static final String DB_ENTRY_ID = "ENTRY_ID";

    /** Database column name constant. */
    private static final String DB_FIELDNAME = "FIELDNAME";

    /** Database column name constant. */
    private static final String DB_FIELDVALUE = "FIELDVALUE";

    /** Database column name constant. */
    private static final String DB_FORM_ID = "FORM_ID";

    /** The generic db SQL properties file name. */
    private static final String DB_GENERIC = "generic";

    /** The oracle db SQL properties file name. */
    private static final String DB_ORACLE = "oracle";

    /** The postgreSQL db SQL properties file name. */
    private static final String DB_POSTGRESQL = "postgresql";

    /** The MSSQL db SQL properties file name. */
    private static final String DB_MSSQL = "mssql";

    /** The path to the SQL query properties. */
    private static final String DB_PATH = "com/alkacon/opencms/v8/formgenerator/database/";

    /** Database column name constant. */
    private static final String DB_RESOURCE_ID = "RESOURCE_ID";

    /** Database column name constant. */
    private static final String DB_STATE = "STATE";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsFormDataAccess.class);

    /** The singleton object. */
    private static CmsFormDataAccess m_instance;

    /** The properties file extension. */
    private static final String PROPERTIES_EXTENSION = ".properties";

    /** The admin cms context. */
    private CmsObject m_cms;

    /** The connection pool id. */
    private String m_connectionPool;

    /** The current used db SQL properties file name. */
    private String m_db;

    /** A map holding all SQL queries. */
    private Map<Object, Object> m_queries;

    /**
     * Default constructor.<p>
     */
    private CmsFormDataAccess() {

        CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
        if (module == null) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {CmsForm.MODULE_NAME}));
        }
        m_connectionPool = module.getParameter(CmsForm.MODULE_PARAM_DB_POOL);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_connectionPool)) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                new Object[] {CmsForm.MODULE_PARAM_DB_POOL, CmsForm.MODULE_NAME}));
        }
        m_queries = new HashMap<Object, Object>();
        loadQueryProperties(DB_PATH + DB_GENERIC + PROPERTIES_EXTENSION);
        m_db = DB_GENERIC;
        String db = module.getParameter(CmsForm.MODULE_PARAM_DB_PROVIDER);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(db)) {
            loadQueryProperties(DB_PATH + db + PROPERTIES_EXTENSION);
            m_db = db;
        }
    }

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

    /**
     * Counts the number of forms for each form.<p>
     * 
     * @return <code>{@link Map}&lt;{@link String}, {@link Integer}&gt;</code> with all form id as keys and the count as value
     *      
     * @throws SQLException if something goes wrong 
     */
    public Map<String, Integer> countForms() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Map<String, Integer> result = new HashMap<String, Integer>();
        try {
            con = getConnection();
            stmt = con.prepareStatement(getQuery("READ_FORM_NAMES"));
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString(DB_FORM_ID), new Integer(rs.getInt(DB_COUNT)));
            }
        } finally {
            closeAll(con, stmt, rs);
        }
        return result;
    }

    /**
     * Counts all submitted forms matching the given filter.<p>
     * 
     * @param filter the filter to match 
     * 
     * @return the number of all submitted forms matching the given filter
     *      
     * @throws SQLException if sth goes wrong 
     */
    public int countForms(CmsFormDatabaseFilter filter) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        int result = 0;

        try {
            List<Object> params = new ArrayList<Object>();
            con = getConnection();
            stmt = con.prepareStatement(getReadQuery(filter, params, true));
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, ((Integer)params.get(i)).intValue());
                } else if (params.get(i) instanceof Long) {
                    stmt.setLong(i + 1, ((Long)params.get(i)).longValue());
                } else {
                    stmt.setString(i + 1, (String)params.get(i));
                }
            }
            res = stmt.executeQuery();
            if (res.next()) {
                result = res.getInt(DB_COUNT);
            }
        } finally {
            closeAll(con, stmt, res);
        }
        return result;
    }

    /**
     * Deletes the form with all fields and data.<p>
     * 
     * @param entryId to find the form data in the database 
     * 
     * @throws SQLException if something goes wrong
     */
    public void deleteForm(int entryId) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            // delete the entries
            con = getConnection();
            stmt = con.prepareStatement(getQuery("DELETE_FORM_ENTRY"));
            stmt.setInt(1, entryId);
            stmt.executeUpdate();

            // delete the data
            closeAll(null, stmt, null);
            stmt = con.prepareStatement(getQuery("DELETE_FORM_DATA"));
            stmt.setInt(1, entryId);
            stmt.executeUpdate();
        } finally {
            closeAll(con, stmt, null);
        }

    }

    /**
     * Creates the database tables for the webform data if they 
     * do not exist.<p>
     * 
     * @throws SQLException if sth goes wrong
     */
    public void ensureDBTablesExistance() throws SQLException {

        switch (existsDBTables()) {
            case -1:
                createDBTables();
                break;
            case 1:
                updateDBTables();
                break;
            default:
                return;
        }
    }

    /**
     * Read a <code>{@link CmsFormDataBean}</code> with  all fields and values with the given data id.<p>
     * 
     * @param entryId to find the form entry in the database 
     * 
     * @return a <code>{@link CmsFormDataBean}</code> with the given data id or <code>null</code>
     *      
     * @throws SQLException if something goes wrong 
     */
    public CmsFormDataBean readForm(int entryId) throws SQLException {

        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterEntryId(entryId);
        List<CmsFormDataBean> forms = readForms(filter);
        if (forms.isEmpty()) {
            return null;
        }
        return forms.get(0);
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
    public List<String> readFormFieldNames(final String formId, long start, long end) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<String> result = new ArrayList<String>();
        try {
            con = getConnection();
            stmt = con.prepareStatement(getQuery("READ_FORM_FIELD_NAMES"));
            stmt.setString(1, formId);
            stmt.setLong(2, start);
            stmt.setLong(3, end);

            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(DB_FIELDNAME));
            }
        } finally {
            closeAll(con, stmt, rs);
        }
        return result;
    }

    /**
     * Read all submitted forms matching the given filter.<p>
     * 
     * @param filter the filter to match 
     * 
     * @return a <code>List&lt;{@link CmsFormDataBean}&gt;</code> for all 
     *      data submitted matching the given filter
     *      
     * @throws SQLException if sth goes wrong 
     */
    public List<CmsFormDataBean> readForms(CmsFormDatabaseFilter filter) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        List<CmsFormDataBean> result = new ArrayList<CmsFormDataBean>();

        try {
            List<Object> params = new ArrayList<Object>();
            con = getConnection();
            stmt = con.prepareStatement(getReadQuery(filter, params, false));
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, ((Integer)params.get(i)).intValue());
                } else if (params.get(i) instanceof Long) {
                    stmt.setLong(i + 1, ((Long)params.get(i)).longValue());
                } else {
                    stmt.setString(i + 1, (String)params.get(i));
                }
            }
            res = stmt.executeQuery();
            CmsFormDataBean bean = null;
            while (res.next()) {
                int entryId = res.getInt(DB_ENTRY_ID);
                if ((bean == null) || (bean.getEntryId() != entryId)) {
                    bean = new CmsFormDataBean();
                    bean.setEntryId(entryId);
                    bean.setDateCreated(res.getLong(DB_DATE_CREATED));
                    bean.setFormId(res.getString(DB_FORM_ID));
                    bean.setState(res.getInt(DB_STATE));
                    // set the id
                    CmsUUID resId;
                    try {
                        // assume it is an id
                        resId = new CmsUUID(res.getString(DB_RESOURCE_ID));
                    } catch (NumberFormatException e) {
                        try {
                            // it could also be a path
                            resId = m_cms.readResource(res.getString(DB_RESOURCE_ID)).getStructureId();
                        } catch (Throwable e1) {
                            resId = CmsUUID.getNullUUID();
                        }
                    }
                    bean.setResourceId(resId);
                    result.add(bean);
                }
                if (!filter.isHeadersOnly()) {
                    bean.addField(res.getString(DB_FIELDNAME), res.getString(DB_FIELDVALUE));
                }
            }
        } finally {
            closeAll(con, stmt, res);
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
    public List<CmsFormDataBean> readForms(String formId, long start, long end) throws SQLException {

        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterFormId(formId);
        filter = filter.filterDate(start, end);
        return readForms(filter);
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
    public List<CmsFormDataBean> readFormsForFieldValue(String formId, String fieldName, String fieldValue)
    throws SQLException {

        CmsFormDatabaseFilter filter = CmsFormDatabaseFilter.DEFAULT;
        filter = filter.filterFormId(formId);
        filter = filter.filterField(fieldName, fieldValue);
        return readForms(filter);
    }

    /**
     * Updates the field with the new value for the given form.<p>
     * 
     * @param formEntryId to find the form entry in the database 
     * @param field the name of the field which should update
     * @param value the new value of the field
     * 
     * @throws SQLException if something goes wrong
     */
    public void updateFieldValue(int formEntryId, String field, String value) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {

            // delete the current field in the web form
            con = getConnection();
            stmt = con.prepareStatement(getQuery("DELETE_FORM_FIELD"));
            stmt.setInt(1, formEntryId);
            stmt.setString(2, field);
            int rowCount = stmt.executeUpdate();

            // add the new entry if its not empty or if database row was present before
            if (!CmsStringUtil.isEmptyOrWhitespaceOnly(value) || (rowCount > 0)) {
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("WRITE_FORM_DATA"));
                stmt.setInt(1, formEntryId);
                stmt.setString(2, field);
                if (value == null) {
                    value = "";
                }
                stmt.setString(3, value);
                stmt.executeUpdate();
            }
        } finally {
            closeAll(con, stmt, null);
        }
    }

    /**
     * Updates the state for the given form.<p>
     * 
     * @param entryId to find the form entry in the database 
     * @param state new state value
     * 
     * @throws SQLException if something goes wrong
     */
    public void updateState(int entryId, int state) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {

            // delete the current field in the webform
            con = getConnection();
            stmt = con.prepareStatement(getQuery("UPDATE_FORM_STATE"));
            stmt.setInt(1, state);
            stmt.setInt(2, entryId);
            stmt.executeUpdate();
        } finally {
            closeAll(con, stmt, null);
        }
    }

    /**
     * Persists the values of the given form.<p>
     * 
     * Implementations should log underlying exceptions.<p>
     * 
     * @param formHandler the form handler containing the form to persist. 
     * 
     * @return the entry id of the submitted form in the database or '-1' if something goes wrong 
     * 
     * @throws SQLException if something goes wrong 
     * 
     * @see com.alkacon.opencms.v8.formgenerator.CmsForm#getAllFields()
     */
    public int writeFormData(CmsFormHandler formHandler) throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int newId = 0;

        try {
            con = getConnection();
            // 1) Compute next id
            stmt = con.prepareStatement(getQuery("READ_NEXT_ENTRY_ID"));
            rs = stmt.executeQuery();
            if (rs.next()) {
                newId = rs.getInt("MAXID");
            }
            newId++;

            // 2) Write a new entry 
            stmt = con.prepareStatement(getQuery("WRITE_FORM_ENTRY"));

            CmsForm form = formHandler.getFormConfiguration();
            String formId = form.getFormId();
            long dateCreated = System.currentTimeMillis();
            CmsUUID resourceId;
            try {
                resourceId = formHandler.getCmsObject().readResource(formHandler.getRequestContext().getUri()).getStructureId();
            } catch (CmsException e) {
                resourceId = CmsUUID.getNullUUID();
            }
            stmt.setInt(1, newId);
            stmt.setString(2, formId);
            stmt.setLong(3, dateCreated);
            stmt.setString(4, resourceId.toString());
            stmt.setInt(5, 0); // initial state
            int rc = stmt.executeUpdate();
            if (rc != 1) {
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATAACCESS_SQL_WRITE_SUBMISSION_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}));
                newId = -1;
                return newId;
            }

            // connection is still needed, so only close statement
            closeAll(null, stmt, null);

            // 3) Now insert the data values for this submission with that ref_id: 
            stmt = con.prepareStatement(getQuery("WRITE_FORM_DATA"));
            // loop over all form fields: 
            List<I_CmsField> formFields = form.getAllFields(false, true, true);
            Iterator<I_CmsField> itFormFields = formFields.iterator();
            while (itFormFields.hasNext()) {
                I_CmsField field = itFormFields.next();
                // do not store empty fields: users will not be able to enter something and "duplicate entry" errors may happen
                // do not store paging fields as well
                if ((field instanceof CmsPagingField) || (field instanceof CmsEmptyField)) {
                    continue;
                }

                // returns null if we do not deal with a CmsUploadFileItem: 
                FileItem fileItem = formHandler.getUploadFile(field);
                List<String> fieldNames = new ArrayList<String>();
                List<String> fieldValues = new ArrayList<String>();
                if (fileItem != null) {
                    // save the location of the file and 
                    // store it from the temp file to a save place: 
                    String absPath = storeFile(fileItem, formHandler);
                    fieldNames.add(field.getDbLabel());
                    fieldValues.add(absPath);
                } else if ((field instanceof CmsDisplayField) || (field instanceof CmsHiddenDisplayField)) {
                    fieldNames.add(field.getDbLabel());
                    fieldValues.add(field.getValue());
                } else if (field instanceof CmsDynamicField) {
                    fieldNames.add(field.getDbLabel());
                    fieldValues.add(formHandler.getFormConfiguration().getFieldStringValueByName(field.getName()));
                } else if (field instanceof CmsTableField) {
                    for (int i = 0; i < field.getItems().size(); i++) {
                        CmsFieldItem fieldItem = field.getItems().get(i);
                        fieldNames.add(fieldItem.getDbLabel());
                        fieldValues.add(fieldItem.getValue());
                    }
                } else {
                    fieldNames.add(field.getDbLabel());
                    fieldValues.add(field.getValue());
                }

                // a field can contain more than one value (e.g. table field), so for all values one entry is created
                for (int i = 0; i < fieldValues.size(); i++) {
                    String fieldName = fieldNames.get(i);
                    String fieldValue = fieldValues.get(i);
                    stmt.setInt(1, newId);
                    stmt.setString(2, fieldName);
                    stmt.setString(3, fieldValue);

                    /*
                     * at this level we can allow to loose a field value and try 
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
            }
        } finally {
            closeAll(con, stmt, rs);
        }
        return newId;
    }

    /**
     * Sets the cms context.<p>
     * 
     * @param adminCms the admin cms context to set
     */
    protected void setCms(CmsObject adminCms) {

        try {
            m_cms = OpenCms.initCmsObject(adminCms);
            m_cms.getRequestContext().setSiteRoot("");
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * This method closes the result sets and statement and connections.<p>
     * 
     * @param con The connection.
     * @param statement The statement.
     * @param res The result set.
     */
    private void closeAll(Connection con, Statement statement, ResultSet res) {

        // result set
        if (res != null) {
            try {
                res.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage());
                }
            }
        }
        // statement
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getLocalizedMessage());
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
                    LOG.error(e.getLocalizedMessage());
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

        String indexTablespace = "";
        if (m_db.equals(DB_ORACLE)) {
            CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
            indexTablespace = module.getParameter(CmsForm.MODULE_PARAM_DB_INDEXTABLESPACE, "users");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getQuery("CREATE_TABLE_CMS_WEBFORM_ENTRIES", indexTablespace));
            stmt.executeUpdate();
            closeAll(null, stmt, null);
            stmt = con.prepareStatement(getQuery("CREATE_TABLE_CMS_WEBFORM_DATA", indexTablespace));
            stmt.executeUpdate();
            if (m_db.equals(DB_ORACLE)) {
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_FORMID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_STATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFD_VALUE", indexTablespace));
                stmt.executeUpdate();
            } else if (m_db.equals(DB_POSTGRESQL)) {
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_FORMID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_STATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFD_VALUE", indexTablespace));
                stmt.executeUpdate();
            } else if (m_db.equals(DB_MSSQL)) {
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_FORMID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_RESID_STATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFE_DATE", indexTablespace));
                stmt.executeUpdate();
                closeAll(null, stmt, null);
                stmt = con.prepareStatement(getQuery("CREATE_INDEX_WFD_VALUE", indexTablespace));
                stmt.executeUpdate();
            }
        } finally {
            closeAll(con, stmt, null);
        }
    }

    /**
     * Checks if the db tables for the webform data exist and is up-to-date.<p> 
     * 
     * @return -1 if the db tables do not exist,
     *          0 if the db tables do exist, in the current version, or
     *          1 if the db tables do exist, in an old version
     * 
     * @throws SQLException if problems with the db connectivity occur
     */
    private int existsDBTables() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getQuery("CHECK_TABLES"));
            try {
                res = stmt.executeQuery();
                try {
                    res.findColumn(DB_RESOURCE_ID);
                    return 0;
                } catch (Exception ex) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(Messages.get().getBundle().key(Messages.LOG_INFO_DATAACESS_SQL_TABLE_OLD_0), ex);
                    }
                }
                return 1;
            } catch (Exception ex) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(Messages.get().getBundle().key(Messages.LOG_INFO_DATAACESS_SQL_TABLE_NOTEXISTS_0), ex);
                }
            }
        } finally {
            closeAll(con, stmt, res);
        }
        return -1;
    }

    /**
     * Returns a connection to the db pool configured in parameter "db-pool" of module 
     * "com.alkacon.opencms.v8.formgenerator".<p>
     * 
     * @return a connection to the db pool configured in parameter "db-pool" of module 
     *      "com.alkacon.opencms.v8.formgenerator"
     *      
     * @throws SQLException if sth goes wrong 
     */
    private Connection getConnection() throws SQLException {

        return OpenCms.getSqlManager().getConnection(m_connectionPool);
    }

    /**
     * Searches for the SQL query with the specified key.<p>
     * 
     * @param queryKey the SQL query key
     * 
     * @return the the SQL query in this property list with the specified key
     */
    private String getQuery(String queryKey) {

        return (String)m_queries.get(queryKey);
    }

    /**
     * Searches for the SQL query with the specified key.<p>
     * 
     * @param queryKey the SQL query key
     * @param placeHolder will replace the ${ph} macro
     * 
     * @return the the SQL query in this property list with the specified key
     */
    private String getQuery(String queryKey, String placeHolder) {

        String query = getQuery(queryKey);
        if (placeHolder != null) {
            query = CmsStringUtil.substitute(query, "${ph}", placeHolder);
        }
        return query;
    }

    /**
     * Build the whole sql statement for the given form filter.<p>
     * 
     * @param filter the filter
     * @param params the parameter values (return parameter)
     * @param count if true it selects no row, just the number of rows
     * 
     * @return the sql statement string
     */
    private String getReadQuery(CmsFormDatabaseFilter filter, List<Object> params, boolean count) {

        StringBuffer sql = new StringBuffer(128);
        params.clear(); // be sure the parameters list is clear

        if (count) {
            sql.append(getQuery("COUNT_FORM_ENTRIES"));
        } else {
            if (filter.isHeadersOnly()) {
                sql.append(getQuery("READ_FORM_ENTRY"));
            } else {
                sql.append(getQuery("READ_FORM_DATA"));
            }
        }
        StringBuffer where = new StringBuffer(128);
        if (!filter.getFields().isEmpty()) {
            int fields = filter.getFields().size();
            for (int i = 0; i < fields; i++) {
                sql.append(",").append(getQuery("COND_FIELD_FROM", "" + i));
            }
        }
        if (!filter.isHeadersOnly()) {
            where.append(getQuery("COND_JOIN"));
        }
        if (filter.getEntryId() > 0) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            where.append(getQuery("FILTER_ENTRY_ID"));
            params.add(new Integer(filter.getEntryId()));
        }
        if (filter.getFormId() != null) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            where.append(getQuery("FILTER_FORM_ID"));
            params.add(filter.getFormId());
        }
        if (filter.getDateEnd() != CmsFormDatabaseFilter.DATE_IGNORE_TO) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            where.append(getQuery("FILTER_DATE_END"));
            params.add(new Long(filter.getDateEnd()));
        }
        if (filter.getStartDate() != CmsFormDatabaseFilter.DATE_IGNORE_FROM) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            where.append(getQuery("FILTER_DATE_START"));
            params.add(new Long(filter.getStartDate()));
        }
        if (filter.getResourceId() != null) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            where.append(getQuery("FILTER_RESOURCE_ID"));
            params.add(filter.getResourceId().toString());
        }

        // states filter
        Set<Integer> states = filter.getStates();
        if (!states.isEmpty()) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            String ph = "";
            for (int i = 0; i < states.size(); i++) {
                ph += "?";
                if (i < (states.size() - 1)) {
                    ph += ", ";
                }
            }
            where.append(getQuery("FILTER_STATES", ph));
            Iterator<Integer> it = states.iterator();
            while (it.hasNext()) {
                Integer state = it.next();
                params.add(state);
            }
        }
        // fields filter
        Map<String, String> fields = filter.getFields();
        if (!fields.isEmpty()) {
            if (where.length() > 0) {
                where.append(" ").append(getQuery("COND_AND")).append(" ");
            }
            int i = 0;
            Iterator<Entry<String, String>> it = fields.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> field = it.next();
                where.append(getQuery("FILTER_FIELD", "" + i));
                params.add(field.getKey());
                params.add(field.getValue());
                if (it.hasNext()) {
                    where.append(" ").append(getQuery("COND_AND")).append(" ");
                }
                i++;
            }
        }
        if (where.length() > 0) {
            sql.append(" ").append(getQuery("COND_WHERE")).append(" ").append(where);
        }
        if (!count) {
            if (filter.isOrderAsc()) {
                sql.append(" ").append(getQuery("COND_ORDER_ASC"));
            } else {
                sql.append(" ").append(getQuery("COND_ORDER_DESC"));
            }
        }
        if ((filter.getIndexFrom() != CmsFormDatabaseFilter.INDEX_IGNORE_FROM)
            || (filter.getIndexTo() != CmsFormDatabaseFilter.INDEX_IGNORE_TO)) {
            int rows = filter.getIndexTo() - filter.getIndexFrom();
            if (m_db.equals(DB_ORACLE)) {
                rows = filter.getIndexTo();
            }
            sql.insert(0, " ").insert(0, getQuery("FILTER_LIMIT_PREFIX"));
            sql.append(" ").append(getQuery("FILTER_LIMIT_POSTFIX")).append(" ");
            sql.append(" ").append(getQuery("FILTER_LIMIT", "" + rows));
            if (filter.getIndexFrom() != 0) {
                sql.append(" ").append(getQuery("FILTER_OFFSET", "" + filter.getIndexFrom()));
            }
        }
        return sql.toString();
    }

    /**
     * Loads a Java properties hash containing SQL queries.<p>
     * 
     * @param propertyFilename the package/filename of the properties hash
     */
    private void loadQueryProperties(String propertyFilename) {

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(propertyFilename));
            m_queries.putAll(properties);
        } catch (Throwable t) {
            if (LOG.isErrorEnabled()) {
                LOG.error(t.getLocalizedMessage(), t);
            }
            properties = null;
        }
    }

    /**
     * Stores the content of the given file to a 
     * place specified by the module parameter "uploadfolder".<p>
     * 
     * Also the parameters "uploadvfs" and "uploadproject" can be used to store the file inside the OpenCms VFS.<p>
     * 
     * The content of the upload file item is only inside a temporary file. 
     * This must be called, when the form submission is stored to the database 
     * as the content would be lost.<p>
     * 
     * @param item the upload file item to store 
     * @param formHandler only used for exception logging 
     * 
     * @return the absolute path of the created file 
     */
    private String storeFile(FileItem item, CmsFormHandler formHandler) {

        String fullResourceName = "";
        CmsModule module = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
        if (module == null) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_MISSING_1,
                new Object[] {CmsForm.MODULE_NAME}));
        }
        // read the path to store the files from module parameters
        String filePath = module.getParameter(CmsForm.MODULE_PARAM_UPLOADFOLDER);
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(filePath)) {
            throw new CmsRuntimeException(Messages.get().container(
                Messages.LOG_ERR_DATAACCESS_MODULE_PARAM_MISSING_2,
                new Object[] {CmsForm.MODULE_PARAM_UPLOADFOLDER, CmsForm.MODULE_NAME}));
        }
        // get the sub folder to store the files
        String formName = formHandler.getFormConfiguration().getFormId();
        if ((formName != null) && formName.startsWith(formHandler.getRequestContext().getSiteRoot())) {
            // the configuration root path is used as (default) form ID, do NOT use this as sub folder
            formName = null;
        }
        formName = m_cms.getRequestContext().getFileTranslator().translateResource(formName);

        // generate file name
        String itemName = item.getName();
        // In most cases, this will be the base file name, without path information. However, 
        // some clients, such as the Opera browser, do include path information. 
        // That is why here is to assure that the base name is used.
        itemName = CmsFormHandler.getTruncatedFileItemName(itemName);
        // add an (almost) unique prefix to the file to prevent overwriting of files with the same name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_");
        itemName = sdf.format(new Date()) + itemName;
        // determine upload target: RFS (default) or VFS
        String vfsUpload = module.getParameter(CmsForm.MODULE_PARAM_UPLOADVFS, CmsStringUtil.FALSE);
        if (Boolean.valueOf(vfsUpload).booleanValue()) {
            // upload to OpenCms VFS
            if (!filePath.endsWith("/")) {
                filePath += "/";
            }
            // translate resource name to valid VFS resource name
            itemName = m_cms.getRequestContext().getFileTranslator().translateResource(itemName);

            // store current project
            CmsProject currProject = m_cms.getRequestContext().getCurrentProject();
            try {
                // switch to an offline project
                String projectName = module.getParameter(CmsForm.MODULE_PARAM_UPLOADPROJECT, "Offline");
                m_cms.getRequestContext().setCurrentProject(m_cms.readProject(projectName));
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(formName)) {
                    // check if the sub folder exists and create it if necessary
                    String subFolder = filePath + formName;
                    if (!m_cms.existsResource(subFolder)) {
                        try {
                            m_cms.createResource(subFolder, CmsResourceTypeFolder.getStaticTypeId(), null, null);
                            m_cms.unlockResource(subFolder);
                            // publish the folder
                            OpenCms.getPublishManager().publishResource(m_cms, subFolder);
                            // wait a little bit to avoid problems when publishing the uploaded file afterwards
                            OpenCms.getPublishManager().waitWhileRunning(3000);
                            // set the file path to sub folder
                            filePath = subFolder + "/";
                        } catch (Exception e) {
                            // error creating the folder in VFS
                            LOG.error(e);
                        }
                    }
                }
                // create full resource name
                fullResourceName = filePath + itemName;
                // determine the resource type id from the given information
                int resTypeId = OpenCms.getResourceManager().getDefaultTypeForName(itemName).getTypeId();
                // create the resource in VFS
                m_cms.createResource(fullResourceName, resTypeId, item.get(), null);
                m_cms.unlockResource(fullResourceName);
                try {
                    // publish the resource
                    OpenCms.getPublishManager().publishResource(m_cms, fullResourceName);
                } catch (Exception e) {
                    // error publishing the created file
                    LOG.error(e);
                }
            } catch (CmsException e) {
                // error creating the file in VFS
                LOG.error(Messages.get().getBundle().key(
                    Messages.LOG_ERR_DATAACCESS_UPLOADFILE_LOST_1,
                    new Object[] {formHandler.createMailTextFromFields(false, false)}), e);
            } finally {
                // switch back to stored project
                m_cms.getRequestContext().setCurrentProject(currProject);
            }
        } else {
            // upload to server RFS
            try {
                File folder = new File(filePath);
                CmsFileUtil.assertFolder(folder, CmsFileUtil.MODE_READ, true);
                if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(formName)) {
                    File subFolder = new File(folder, formName);
                    CmsFileUtil.assertFolder(subFolder, CmsFileUtil.MODE_READ, true);
                    folder = subFolder;
                }
                File storeFile = new File(folder, itemName);
                fullResourceName = storeFile.getAbsolutePath();
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
        }
        return fullResourceName;
    }

    /**
     * Unconditionally tries to update the db tables needed for form data.<p>
     * 
     * @throws SQLException if sth goes wrong 
     */
    private void updateDBTables() throws SQLException {

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getQuery("UPDATE_FORM_ENTRY_STATE"));
            stmt.executeUpdate();
            closeAll(null, stmt, null);
            stmt = con.prepareStatement(getQuery("UPDATE_FORM_ENTRY_RESID"));
            stmt.executeUpdate();
            stmt = con.prepareStatement(getQuery("UPDATE_FORM_ENTRY_ID"));
            stmt.executeUpdate();
        } finally {
            closeAll(con, stmt, null);
        }
    }
}
