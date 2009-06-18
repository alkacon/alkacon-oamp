/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.counter/src/com/alkacon/opencms/counter/CmsCounterManager.java,v $
 * Date   : $Date: 2009/06/18 17:26:12 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.counter;

import com.alkacon.opencms.util.CmsDbUtil;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsRuntimeException;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;

/**
 * This is a helper class that provides methods used for the table counter in the database.<p>
 * 
 * @author Anja Roettgers
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.3
 */
public class CmsCounterManager extends A_CmsModuleAction {

    /** Constant for the module name. */
    public static final String MODULE_NAME = "com.alkacon.opencms.counter";

    /** Query to check if the table exists in the database. */
    private static final String C_CHECK_DB = "SELECT count(*) FROM CMS_COUNTER_MODULES";

    /** the name of the table of counters in the database.*/
    private static final String C_COUNTER_TABLE = "CMS_COUNTER_MODULES";

    /** Query to insert a new entry in the counter table.*/
    private static final String C_CREATE_COUNTER_ENTRY = "INSERT INTO "
        + C_COUNTER_TABLE
        + " (COUNTER_KEY, COUNTER_COUNT) VALUES(?,?)";

    /** Query to create the counter table in the database.*/
    private static final String C_CREATE_TABLE = "CREATE TABLE "
        + C_COUNTER_TABLE
        + " ("
        + "  COUNTER_KEY varchar(255) default NULL,"
        + "  COUNTER_COUNT int default NULL"
        + ") ";

    /** Query to update a counter entry.*/
    private static final String C_DELETE_COUNTER_ENTRY = "DELETE FROM " + C_COUNTER_TABLE + " WHERE COUNTER_KEY= ?";

    /** Query to get a counter entry.*/
    private static final String C_GET_COUNTER_ENTRY = "SELECT COUNTER_COUNT FROM "
        + C_COUNTER_TABLE
        + " WHERE COUNTER_KEY = ?";

    /** Query to get all counter entries from the database.*/
    private static final String C_GET_COUNTERS = "SELECT * FROM " + C_COUNTER_TABLE;

    /** Query to update a counter entry from the database.*/
    private static final String C_UPDATE_COUNTER_ENTRY = "UPDATE "
        + C_COUNTER_TABLE
        + " SET COUNTER_COUNT=? WHERE COUNTER_KEY = ?";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsCounterManager.class);

    /** The connection pool id. */
    private String m_connectionPool;

    /**
     * default constructor.<p>
     */
    public CmsCounterManager() {

        // NOOP
    }

    /**
     * This function deletes a counter entry in the database.<p>
     * 
     * @param entryKey the counter entry which should be deleted
     * 
     * @return <code>true</code> if successfully deleting otherwise <code>false</code>
     * 
     * @throws CmsException if an error occurred
     */
    public boolean deleteCounter(String entryKey) throws CmsException {

        boolean result = false;
        Connection con = null;
        PreparedStatement statement = null;

        // check if the parameters are empty
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey)) {
            return result;
        }

        try {
            // get connection and execute the delete query
            con = CmsDbUtil.getInstance().getConnection(m_connectionPool);
            statement = con.prepareStatement(C_DELETE_COUNTER_ENTRY);
            statement.setString(1, entryKey);
            result = statement.execute();

        } catch (SQLException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_ACCESSING_DB_1, C_COUNTER_TABLE), ex);
            }
            throw new CmsException(Messages.get().container(Messages.LOG_ERROR_DELETE_COUNTER_1, entryKey));
        } finally {
            CmsDbUtil.getInstance().closeConnection(null, statement, con);
        }
        return result;
    }

    /**
     * This function returns the value of the counter entry.<p> 
     * 
     * @param entryKey the id of the counter entry
     * 
     * @return return the value of the counter entry otherwise <code>null</code> if nothing is found or an exception occurred
     */
    public Integer getCounter(String entryKey) {

        Integer result = null;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet res = null;

        // check if the parameters are empty
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey)) {
            return result;
        }

        try {
            // get the connection and create the statement
            con = CmsDbUtil.getInstance().getConnection(m_connectionPool);
            statement = con.prepareStatement(C_GET_COUNTER_ENTRY);
            statement.setString(1, entryKey);

            // execute the query and return the value of the entry
            res = statement.executeQuery();
            if (res.next()) {
                result = new Integer(res.getInt(1));
            }

            // make a log entry
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.get().getBundle().key(Messages.LOG_DEBUG_GET_COUNTER_2, entryKey, result));
            }

        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_ACCESSING_DB_1, C_COUNTER_TABLE), ex);
            }
        } finally {
            CmsDbUtil.getInstance().closeConnection(res, statement, con);
        }
        return result;
    }

    /** 
     * This functions returns all counter entries from the database.<p>
     * 
     * @return a Map of all counter entries with id as key and {@link Integer} as value
     */
    public TreeMap getCounters() {

        TreeMap result = new TreeMap();
        PreparedStatement statement = null;
        Connection con = null;
        ResultSet res = null;
        try {

            // get connection and set the statements
            con = CmsDbUtil.getInstance().getConnection(m_connectionPool);
            statement = con.prepareStatement(C_GET_COUNTERS);
            res = statement.executeQuery();

            // the query result is parsing into key and value
            String key;
            int value;
            while (res.next()) {
                key = res.getString(1);
                value = res.getInt(2);
                result.put(key, new Integer(value));
            }

        } catch (Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_ACCESSING_DB_1, C_COUNTER_TABLE), ex);
            }
        } finally {
            CmsDbUtil.getInstance().closeConnection(res, statement, con);
        }
        return result;
    }

    /**
     * Returns the count value of the counter entry incremented. <p>
     * 
     * Important: the incremented value is saved in the database.<p>
     * 
     * @param entryKey the id of the counter entry
     * 
     * @return return the incremented value of the counter entry otherwise it returns <code>1</code>
     * 
     * @throws CmsException if an exception occurred
     */
    public int incrementCounter(String entryKey) throws CmsException {

        int result = 0;
        Integer value = getCounter(entryKey);
        if (value != null) {
            result = value.intValue();
        }
        result++;
        setCounter(entryKey, result);
        return result;
    }

    /**
     * @see org.opencms.module.I_CmsModuleAction#initialize(org.opencms.file.CmsObject, CmsConfigurationManager, CmsModule)
     */
    public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {

        super.initialize(adminCms, configurationManager, module);

        try {
            m_connectionPool = CmsDbUtil.getInstance().getConnectionPoolParameter(module.getName());
        } catch (CmsRuntimeException ex) {
            if (LOG.isInfoEnabled()) {
                LOG.info(ex);
            }
            m_connectionPool = "default";
        }

        // check if the required database tables exists and create its
        try {
            List createScript = new ArrayList();
            createScript.add(C_CREATE_TABLE);
            CmsDbUtil.getInstance().ensureDBTablesExistance(m_connectionPool, C_CHECK_DB, createScript);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_CREATING_TABLE_1, C_COUNTER_TABLE), e);
            }
        }

    }

    /**
     * This functions adds a new counter entry to the database table or if its exists then
     * the value is updated. <p>
     * 
     * @param entryKey the id of the counter entry
     * @param newValue the value of the counter entry
     * 
     * @throws CmsException if an exception occurred
     */
    public void setCounter(String entryKey, int newValue) throws CmsException {

        Connection con = null;
        PreparedStatement statement = null;

        // check if the values are empty
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(entryKey) || newValue < 0) {
            throw new CmsRuntimeException(Messages.get().container(Messages.LOG_ERROR_MISSING_VALUES_1, "update"));
        }

        try {
            // gets the connection and check if the Counter-Entry exists
            con = CmsDbUtil.getInstance().getConnection(m_connectionPool);
            Integer count = getCounter(entryKey);

            // create the statement to create or update
            if (count == null) {
                statement = con.prepareStatement(C_CREATE_COUNTER_ENTRY);
                statement.setString(1, entryKey);
                statement.setInt(2, newValue);
            } else {
                statement = con.prepareStatement(C_UPDATE_COUNTER_ENTRY);
                statement.setInt(1, newValue);
                statement.setString(2, entryKey);
            }

            // execute the statement
            statement.executeUpdate();

        } catch (SQLException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_ERROR_ACCESSING_DB_1, C_COUNTER_TABLE), ex);
            }
            throw new CmsException(Messages.get().container(
                Messages.LOG_ERROR_SET_COUNTER_2,
                String.valueOf(newValue),
                entryKey));
        } finally {
            CmsDbUtil.getInstance().closeConnection(null, statement, con);
        }
    }

}
