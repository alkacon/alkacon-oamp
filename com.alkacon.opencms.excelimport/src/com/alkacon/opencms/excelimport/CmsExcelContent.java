/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.excelimport/src/com/alkacon/opencms/excelimport/CmsExcelContent.java,v $
 * Date   : $Date: 2010/09/07 11:03:14 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.excelimport;

import org.opencms.file.CmsObject;
import org.opencms.main.CmsLog;
import org.opencms.search.extractors.CmsExtractorMsOfficeOLE2;
import org.opencms.search.extractors.I_CmsExtractionResult;
import org.opencms.search.extractors.I_CmsTextExtractor;
import org.opencms.util.CmsStringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Includes contents from an excel file. Here are saved the full content and property values.
 * Also the on the content column wise sorted can become accessed.<p>
 * 
 * On read content items can become accessed.<p> 
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 7.5.0
 */
public class CmsExcelContent {

    /** Key to access the category property. */
    public static String PROPERTY_CATEGORY = "category";

    /** Key to access the keywords property. */
    public static String PROPERTY_KEYWORDS = "keywords";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsExcelContent.class);

    /** The column contents. */
    private Map m_colContents = new HashMap();

    /** The column names. */
    private Map m_colNames = new HashMap();

    /** The extracted individual content items. */
    private Map m_contentItems;

    /** The name from excel file. */
    private String m_excelName;

    /** The number of rows without headline. */
    private int m_rowNumber;

    /**
     * Gets the category property value from excel file.<p>
     * 
     * @return the folder property value from excel file
     */
    public String getCategoryProperty() {

        String categoryProperty = "";
        if (m_contentItems != null) {
            categoryProperty = (String)m_contentItems.get(CmsExcelContent.PROPERTY_CATEGORY);
        }
        return categoryProperty;
    }

    /**
     * Get cell content from given column and row.<p>
     * 
     * @param userColName name from column set by user where cell is
     * @param row number of row where cell is
     * 
     * @return cell content from given column and raw
     */
    public String getCellContent(String userColName, int row) {

        String cellContent = "";
        // get list with all raw entries from given raw name
        if ((m_colNames != null) && (m_colContents != null) && m_colNames.containsKey(userColName)) {
            Integer excelColName = (Integer)m_colNames.get(userColName);
            CmsExcelColumn cmsExcelRow = (CmsExcelColumn)m_colContents.get(excelColName);
            if ((cmsExcelRow != null)) {
                // get from row list given number entry 
                cellContent = cmsExcelRow.getCellStringValue(row);
            }
        }
        return cellContent;
    }

    /**
     * Gets map with column names with key as column name set by user and value as column name by excel internal.<p>
     * 
     * @return map with column names with key as column name set by user and value as column name by excel internal
     */
    public Map getColumnNames() {

        return m_colNames;
    }

    /**
     * Gets full content from excel file.<p>
     * 
     * @return full content from excel file
     */
    public String getContent() {

        String content = "";
        if (m_contentItems != null) {
            content = (String)m_contentItems.get(I_CmsExtractionResult.ITEM_CONTENT);
        }
        return content;
    }

    /**
     * Gets name from excel file.<p>
     * 
     * @return name from excel file
     */
    public String getExcelName() {

        if (CmsStringUtil.isNotEmpty(m_excelName)) {
            return m_excelName;
        } else {
            return "";
        }
    }

    /**
     * Gets the folder property value from excel file.<p>
     * 
     * @return the folder property value from excel file
     */
    public String getFolderProperty() {

        String folderProperty = "";
        if (m_contentItems != null) {
            folderProperty = (String)m_contentItems.get(CmsExcelContent.PROPERTY_KEYWORDS);
        }
        return folderProperty;
    }

    /**
     * Get number of rows in excel file.<p>
     * 
     * @return number of rows in excel file
     */
    public int getNumberOfRecords() {

        return m_rowNumber;
    }

    /**
     * Get one row from excel file with values as map with column name as key and value as value.<p>
     * 
     * @param row number from row to get values to
     * 
     * @return one row with values as map with column name as key and value as value
     */
    public Map getRecord(int row) {

        HashMap record = new HashMap();
        Iterator iter = m_colNames.keySet().iterator();
        while (iter.hasNext()) {
            String userRowName = (String)iter.next();
            String value = getCellContent(userRowName, row);
            record.put(userRowName, value);
        }
        return record;
    }

    /**
     * Checks if excel record is empty.<p>
     * 
     * @param row Number of current row
     * 
     * @return True, if excel current is empty, otherwise false 
     */
    public boolean isEmptyRecord(int row) {

        boolean isEmpty = true;
        Map contents = getRecord(row);
        Iterator iter = contents.values().iterator();
        while (iter.hasNext()) {
            String content = (String)iter.next();
            if (CmsStringUtil.isNotEmpty(content)) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    /**
     * Reads content from excel file. Reads the category property value and also reads contents per column.<p>
     * 
     * @param cmsObject current CmsObject
     * @param excelName name from excel file
     * @param excelContent content from excel file to read
     */
    public void readExcelFile(CmsObject cmsObject, String excelName, byte[] excelContent) {

        if ((cmsObject != null) && (excelContent != null)) {
            m_excelName = excelName;
            String encoding = cmsObject.getRequestContext().getEncoding();
            I_CmsTextExtractor cmsTextExtractorMsExcel = CmsExtractorMsOfficeOLE2.getExtractor();
            try {
                // read content per rows
                readExcelRows(excelContent);

                // read content
                // this is necessary because in search input stream is buffered
                byte[] emptyByte = "".getBytes();
                I_CmsExtractionResult cmsExtractionsResult = null;
                try {
                    cmsExtractionsResult = cmsTextExtractorMsExcel.extractText(emptyByte);
                } catch (Exception e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(e.toString());
                    }
                }
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(excelContent);
                cmsExtractionsResult = cmsTextExtractorMsExcel.extractText(byteArrayInputStream);
                m_contentItems = cmsExtractionsResult.getContentItems();
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.toString());
                }
            }
        }
    }

    /**
     * Reads the excel file row by row. Fills the excel import internal structure which is column wise.<p>
     * 
     * @param in the document input stream
     * 
     * @throws IOException if something goes wring
     */
    private void readExcelColumnContents(InputStream in) throws IOException {

        HSSFWorkbook excelWb = new HSSFWorkbook(in);
        HSSFSheet sheet = excelWb.getSheetAt(0);
        int rowsNumber = sheet.getPhysicalNumberOfRows();
        if (rowsNumber > 0) {

            // loop over all rows from excel
            // do not read first column, because here are only user raw names
            for (int rowCounter = 1; rowCounter < sheet.getPhysicalNumberOfRows(); rowCounter++) {
                HSSFRow row = sheet.getRow(rowCounter);

                if ((row != null)) {
                    // get number of rows in excel
                    if ((rowCounter) > m_rowNumber) {
                        m_rowNumber = rowCounter;
                    }
                    // loop over all columns in this row
                    for (int columnCounter = 0; columnCounter < row.getLastCellNum(); columnCounter++) {
                        CmsExcelColumn cmsExcelCol = (CmsExcelColumn)m_colContents.get(new Integer(columnCounter));
                        if (cmsExcelCol != null) {
                            // read cell
                            HSSFCell cell = row.getCell((short)columnCounter);
                            if (cell != null) {
                                String text = null;
                                try {
                                    // read cell content from excel
                                    switch (cell.getCellType()) {
                                        case Cell.CELL_TYPE_BLANK:
                                        case Cell.CELL_TYPE_ERROR:
                                            // ignore all blank or error cells
                                            break;
                                        case Cell.CELL_TYPE_NUMERIC:
                                            // check for date
                                            double d = cell.getNumericCellValue();
                                            if (DateUtil.isCellDateFormatted(cell) && DateUtil.isValidExcelDate(d)) {
                                                // valid date
                                                Date date = DateUtil.getJavaDate(d);
                                                text = new Long(date.getTime()).toString();
                                            } else {
                                                // no valid date
                                                text = "" + d;
                                                if (text.endsWith(".0")) {
                                                    text = text.substring(0, text.lastIndexOf(".0"));
                                                }
                                            }
                                            break;
                                        case Cell.CELL_TYPE_BOOLEAN:
                                            text = Boolean.toString(cell.getBooleanCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                        default:
                                            text = cell.getStringCellValue();
                                            break;
                                    }
                                    // add to column list
                                    cmsExcelCol.addNewCellValue(rowCounter, text);
                                    m_colContents.put(new Integer(columnCounter), cmsExcelCol);
                                } catch (Exception e) {
                                    if (LOG.isErrorEnabled()) {
                                        LOG.error(e.toString());
                                    }
                                }
                            } else {
                                // add to column list
                                cmsExcelCol.addNewCellValue(rowCounter, "");
                                m_colContents.put(new Integer(columnCounter), cmsExcelCol);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates mapping between excel column names set by user and excel column names from excel internal.<p>
     * 
     * @param in the document input stream
     * 
     * @throws IOException if something goes wring
     */
    private void readExcelColumnMappings(InputStream in) throws IOException {

        HSSFWorkbook excelWb = new HSSFWorkbook(in);
        HSSFSheet sheet = excelWb.getSheetAt(0);
        int numberOfRows = sheet.getPhysicalNumberOfRows();
        if (numberOfRows > 0) {

            HSSFRow firstRow = sheet.getRow(0);
            // loop over all columns in first excel row
            Iterator rowIter = firstRow.cellIterator();
            while (rowIter.hasNext()) {
                // get cell
                HSSFCell cell = (HSSFCell)rowIter.next();
                if (cell != null) {
                    // get user column name
                    String userColName = cell.getStringCellValue();
                    // get excel column name
                    int excelColName = cell.getCellNum();
                    CmsExcelColumn excelCol = new CmsExcelColumn(userColName, excelColName);
                    m_colNames.put(userColName, new Integer(excelColName));
                    m_colContents.put(new Integer(excelColName), excelCol);
                }
            }
        }
    }

    /**
     * Reads the column names set by user and from excel internal and the content from excel file.<p>
     * 
     * @param fileBytes excel file as file bytes
     * 
     * @throws IOException if something goes wring
     */
    private void readExcelRows(byte[] fileBytes) throws IOException {

        readExcelColumnMappings(new ByteArrayInputStream(fileBytes));
        readExcelColumnContents(new ByteArrayInputStream(fileBytes));
    }
}
