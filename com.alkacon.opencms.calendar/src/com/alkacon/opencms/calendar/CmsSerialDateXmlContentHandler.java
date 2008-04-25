/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.calendar/src/com/alkacon/opencms/calendar/CmsSerialDateXmlContentHandler.java,v $
 * Date   : $Date: 2008/04/25 14:50:41 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2008 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.calendar;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.CmsCalendarWidget;
import org.opencms.workplace.CmsWorkplaceMessages;
import org.opencms.xml.CmsXmlContentDefinition;
import org.opencms.xml.CmsXmlException;
import org.opencms.xml.content.CmsDefaultXmlContentHandler;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentErrorHandler;
import org.opencms.xml.content.Messages;
import org.opencms.xml.types.CmsXmlNestedContentDefinition;
import org.opencms.xml.types.I_CmsXmlContentValue;
import org.opencms.xml.types.I_CmsXmlSchemaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Special XML content handler that validates serial date series changes and
 * writes changed occurences to a configurable property value.<p>
 * 
 * @author Andreas Zahner
 * 
 */
public class CmsSerialDateXmlContentHandler extends CmsDefaultXmlContentHandler {

    /** The node name for the Change node. */
    public static final String NODE_CHANGE = "Change";

    /** The node name for the Serialdate node. */
    public static final String NODE_SERIALDATE = "Serialdate";

    /** Name of the property to write the serial date change information to. */
    public static final String PROPERTY_SERIALDATE_CHANGE = "calendar.dateserialchange";

    /** The flag inidicating that a series entry is changed. */
    public static final String SERIES_FLAG_CHANGED = "chg";

    /** The flag inidicating that a series entry is removed. */
    public static final String SERIES_FLAG_REMOVED = "rem";

    /** The xpath for the first Change sub node. */
    public static final String XPATH_CHANGE = NODE_CHANGE + "[1]/" + NODE_CHANGE + "[1]";

    /**
     * Empty constructor.<p>
     */
    public CmsSerialDateXmlContentHandler() {

        super();
    }

    /**
     * @see org.opencms.xml.content.I_CmsXmlContentHandler#resolveMapping(org.opencms.file.CmsObject, org.opencms.xml.content.CmsXmlContent, org.opencms.xml.types.I_CmsXmlContentValue)
     */
    public void resolveMapping(CmsObject cms, CmsXmlContent content, I_CmsXmlContentValue value) throws CmsException {

        // first resolve usual mapping by calling super implementation
        super.resolveMapping(cms, content, value);

        // get locale of value to check
        Locale locale = value.getLocale();

        // check if the serial date change property has to be deleted
        boolean deletePropertyValue = false;
        if (value.getName().equals(NODE_SERIALDATE)) {
            if (!content.hasValue(XPATH_CHANGE, locale)) {
                deletePropertyValue = true;
            }
        }

        if (deletePropertyValue || value.getPath().equals(XPATH_CHANGE)) {

            // get the original VFS file from the content to check if it is present
            if (content.getFile() == null) {
                throw new CmsXmlException(Messages.get().container(Messages.ERR_XMLCONTENT_RESOLVE_FILE_NOT_FOUND_0));
            }

            // create OpenCms user context initialized with "/" as site root to read all siblings
            CmsObject rootCms = OpenCms.initCmsObject(cms);
            rootCms.getRequestContext().setSiteRoot("/");
            // read all siblings of the file
            List siblings = rootCms.readSiblings(content.getFile().getRootPath(), CmsResourceFilter.IGNORE_EXPIRATION);

            // for multiple language mappings, we need to ensure 
            // a) all siblings are handled
            // b) only the "right" locale is mapped to a sibling
            for (int i = (siblings.size() - 1); i >= 0; i--) {
                // get filename
                String filename = ((CmsResource)siblings.get(i)).getRootPath();
                Locale fileLocale = OpenCms.getLocaleManager().getDefaultLocale(rootCms, filename);

                if (!fileLocale.equals(value.getLocale())) {
                    // only map property if the locale fits
                    continue;
                }

                // set the property value
                String propValue = CmsProperty.DELETE_VALUE;
                if (!deletePropertyValue) {
                    // collect information about serial date changes                
                    StringBuffer collectedChanges = new StringBuffer(256);

                    // get the type sequence for the nested change definitions
                    I_CmsXmlSchemaType type = content.getContentDefinition().getSchemaType(NODE_CHANGE);
                    CmsXmlNestedContentDefinition nestedSchema = (CmsXmlNestedContentDefinition)type;
                    CmsXmlContentDefinition nestedDefinition = nestedSchema.getNestedContentDefinition();
                    List typeSequence = nestedDefinition.getTypeSequence();

                    // get the change values to check
                    List changeValues = content.getValues(NODE_CHANGE, locale);
                    int changesSize = changeValues.size();
                    boolean validChangeFound = false;

                    // set the list of serial date entries
                    List possibleEntries = new ArrayList();
                    if (changesSize > 0) {
                        possibleEntries = getPossibleEntries(cms, content, locale);
                    }

                    for (int k = 0; k < changesSize; k++) {
                        // loop the change values
                        I_CmsXmlContentValue changeValue = (I_CmsXmlContentValue)changeValues.get(k);
                        String xPath = changeValue.getPath() + "/";
                        // get the value for the change
                        String changeIndex = content.getStringValue(cms, xPath + NODE_CHANGE, locale);

                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(changeIndex)) {
                            // we have a valid change defined, now check if there are any values active overwriting entry data
                            Iterator it = typeSequence.iterator();
                            String flag = SERIES_FLAG_REMOVED;
                            while (it.hasNext()) {
                                I_CmsXmlSchemaType nestedType = (I_CmsXmlSchemaType)it.next();
                                if (!nestedType.getName().equals(NODE_CHANGE)
                                    && content.hasValue(xPath + nestedType.getName(), locale)) {
                                    // found a field overwriting the series value
                                    flag = SERIES_FLAG_CHANGED;
                                    break;
                                }
                            }
                            if (k > 0 && validChangeFound) {
                                // appent delimiter to property value
                                collectedChanges.append(CmsProperty.VALUE_LIST_DELIMITER);
                            }
                            // determine serial date that is changed
                            CmsCalendarEntry entry = (CmsCalendarEntry)possibleEntries.get(Integer.parseInt(changeIndex) - 1);
                            if (entry != null) {
                                // write changed date and flag to property value
                                long time = entry.getEntryDate().getStartDate().getTimeInMillis();
                                if (content.hasValue(xPath + "Time", locale)) {
                                    try {
                                        CmsWorkplaceMessages messages = OpenCms.getWorkplaceManager().getMessages(
                                            locale);
                                        String dateStr = CmsSerialDateWidget.getCalendarLocalizedTime(
                                            locale,
                                            messages,
                                            entry.getEntryDate().getStartDate().getTimeInMillis(),
                                            true,
                                            false);
                                        time = CmsCalendarWidget.getCalendarDate(messages, dateStr
                                            + " "
                                            + content.getStringValue(cms, xPath + "Time", locale), true);
                                    } catch (Exception e) {
                                        // ignore, changed time will not be considered
                                    }
                                }
                                String dateValue = String.valueOf(time);
                                collectedChanges.append(dateValue);
                                collectedChanges.append(CmsProperty.VALUE_MAP_DELIMITER);
                                collectedChanges.append(flag);
                                validChangeFound = true;
                            }

                        }
                    }
                    propValue = collectedChanges.toString();
                }

                // map to individual value
                CmsProperty p = new CmsProperty(PROPERTY_SERIALDATE_CHANGE, propValue, null, true);

                // just store the string value in the selected property
                rootCms.writePropertyObject(filename, p);
            }
        }
    }

    /**
     * Returns the possible change entries for the configured serial date.<p>
     * 
     * Be sure that at least one change is available in the content before calling this method.<p>
     * 
     * @param cms the current OpenCms user context
     * @param content the XML content to use
     * @param locale the locale to get the values for
     * @return the possible change entries for the configured serial date
     */
    protected List getPossibleEntries(CmsObject cms, CmsXmlContent content, Locale locale) {

        List possibleEntries = new ArrayList();
        try {
            // get the serial date select widget, initialize it
            I_CmsXmlContentValue changeDateValue = content.getValue(XPATH_CHANGE, locale);
            CmsSerialDateSelectWidget widget = (CmsSerialDateSelectWidget)changeDateValue.getContentDefinition().getContentHandler().getWidget(
                changeDateValue);
            widget.initConfiguration(widget.getConfiguration());
            Map serialDateValues = CmsStringUtil.splitAsMap(
                content.getStringValue(cms, NODE_SERIALDATE, locale),
                String.valueOf(CmsProperty.VALUE_LIST_DELIMITER),
                String.valueOf(CmsProperty.VALUE_MAP_DELIMITER));
            // get the number of calendar entries
            possibleEntries = CmsSerialDateSelectWidget.getCalendarEntries(
                serialDateValues,
                locale,
                widget.getEntryCount());
        } catch (CmsException e) {
            // error getting the entries
        }
        return possibleEntries;
    }

    /**
     * @see org.opencms.xml.content.CmsDefaultXmlContentHandler#validateValue(org.opencms.file.CmsObject, org.opencms.xml.types.I_CmsXmlContentValue, org.opencms.xml.content.CmsXmlContentErrorHandler, java.util.Map, boolean)
     */
    protected CmsXmlContentErrorHandler validateValue(
        CmsObject cms,
        I_CmsXmlContentValue value,
        CmsXmlContentErrorHandler errorHandler,
        Map rules,
        boolean isWarning) {

        if (errorHandler == null) {
            // initialize error handler if not yet available
            errorHandler = new CmsXmlContentErrorHandler();
        }

        if (value.getPath().equals(XPATH_CHANGE)) {
            // check the defined changes
            CmsXmlContent content = (CmsXmlContent)value.getDocument();
            Locale locale = value.getLocale();
            List changeValues = content.getValues(NODE_CHANGE, locale);
            int changesSize = changeValues.size();
            List storedIndexes = new ArrayList(changesSize);
            // calculate the possible number of entries
            int possibleEntriesSize = -1;
            if (changesSize > 0) {
                possibleEntriesSize = getPossibleEntries(cms, content, locale).size();
            }

            for (int k = 0; k < changesSize; k++) {
                // loop the change values
                I_CmsXmlContentValue changeValue = (I_CmsXmlContentValue)changeValues.get(k);
                String xPath = changeValue.getPath() + "/";
                // get the value for the exception
                String changeIndex = content.getStringValue(cms, xPath + NODE_CHANGE, locale);
                I_CmsXmlContentValue changeDateValue = content.getValue(xPath + NODE_CHANGE, locale);

                if (CmsStringUtil.isEmptyOrWhitespaceOnly(changeIndex)) {
                    // no date selected, show error
                    errorHandler.addError(changeDateValue, key("GUI_VALIDATION_SERIALDATE_NOTSELECTED_0", locale));
                } else if (Integer.parseInt(changeIndex) > possibleEntriesSize) {
                    // entry index larger than number of possible entries, show error
                    errorHandler.addError(changeDateValue, key("GUI_VALIDATION_SERIALDATE_ENTRYNOTFOUND_0", locale));
                } else {
                    if (storedIndexes.contains(changeIndex)) {
                        // date was already selected earlier, show error
                        errorHandler.addError(changeDateValue, key("GUI_VALIDATION_SERIALDATE_DEFINED_0", locale));
                    } else {
                        // add selected date to list
                        storedIndexes.add(changeIndex);
                    }
                }

            }
        } else {
            // other values use the default validation
            super.validateValue(cms, value, errorHandler, rules, isWarning);
        }

        return errorHandler;
    }

}
