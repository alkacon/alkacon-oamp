/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsDocumentList.java,v $
 * Date   : $Date: 2010/03/19 15:31:13 $
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

package com.alkacon.opencms.v8.documentcenter;

import org.opencms.main.CmsLog;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Provides methods to create a special list of CmsDocument objects regarding language and attachment functionality.<p>
 * 
 * After adding all documents to the list, the {@link #closeList()} method has to be called once
 * before working with the list contents, e.g. before sorting the list or iterating it.<p>
 * 
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.2.0 
 */
public class CmsDocumentList extends AbstractList<CmsDocument> {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsDocumentList.class);

    private List<CmsDocument> m_attachments;

    private CmsDocument[] m_documents;

    private boolean m_onlyVersions;

    private int m_size;

    private boolean m_useAttachments;

    private boolean m_useLanguages;

    private boolean m_useTypes;

    private String m_defaultType;

    /**
     * Creates an empty document list with an initial capacity of 16 items.<p>
     */
    public CmsDocumentList() {

        this(16);
    }

    /**
     * Creates an empty document list with the flags to use language versions and attachments, to show only versions and 
     * the given initial capacity.<p>
     * 
     * @param useTypes the flag if different types of a document should be considered
     * @param defaultType if list uses types, the default type is specified here
     * @param useAttachments the flag if attachments of a document should be conbsidered
     * @param useLanguages the flag if language versions of a document should be considered
     * @param onlyVersions the flag if only versions of a specific document should be added to the list
     * @param initialCapacity the initial capacity of the document list
     */
    public CmsDocumentList(
        boolean useTypes,
        String defaultType,
        boolean useAttachments,
        boolean useLanguages,
        boolean onlyVersions,
        int initialCapacity) {

        m_documents = new CmsDocument[initialCapacity];
        m_size = 0;
        m_attachments = new ArrayList<CmsDocument>();
        m_useLanguages = useLanguages;
        m_onlyVersions = onlyVersions;
        m_useAttachments = useAttachments;
        m_useTypes = useTypes;
        m_defaultType = defaultType;
    }

    /**
     * Creates an empty document list with the flags to use language versions and attachments, to show only versions and 
     * the given initial capacity.<p>
     * 
     * @param useAttachments the flag is attachments of a document should be conbsidered
     * @param useLanguages the flag if language versions of a document should be considered
     * @param onlyVersions the flag if only versions of a specific document should be added to the list
     * @param initialCapacity the initial capacity of the document list
     */
    public CmsDocumentList(boolean useAttachments, boolean useLanguages, boolean onlyVersions, int initialCapacity) {

        this(false, null, useAttachments, useLanguages, onlyVersions, initialCapacity);
    }

    /**
     * Creates an empty document list with the flags to use language versions, to show only versions and the given initial capacity.<p>
     * 
     * @param useLanguages the flag if language versions of a document should be considered
     * @param onlyVersions the flag if only versions of a specific document should be added to the list
     * @param initialCapacity the initial capacity of the document list
     */
    public CmsDocumentList(boolean useLanguages, boolean onlyVersions, int initialCapacity) {

        this(true, useLanguages, onlyVersions, initialCapacity);
    }

    /**
     * Creates an empty document list with the given initial capacity.<p>
     * 
     * @param initialCapacity the initial capacity of the document list
     */
    public CmsDocumentList(int initialCapacity) {

        this(false, false, initialCapacity);
    }

    /**
     * @see java.util.AbstractList#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, CmsDocument element) {

        rangeCheck(index);
        CmsDocument doc = convertDocument(element);
        if (doc.isNullDocument() || checkContainer(doc)) {
            return;
        }

        ensureCapacity(m_size + 1); // Increments modCount!!
        System.arraycopy(m_documents, index, m_documents, index + 1, m_size - index);
        m_documents[index] = doc;
        m_size++;
    }

    /**
     * @see java.util.AbstractList#add(java.lang.Object)
     */
    @Override
    public boolean add(CmsDocument element) {

        CmsDocument doc = convertDocument(element);
        if (doc.isNullDocument() || checkContainer(doc)) {
            return true;
        }

        ensureCapacity(m_size + 1);
        m_documents[m_size++] = doc;
        return true;
    }

    /**
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear() {

        modCount++;
        for (int i = 0; i < m_size; i++) {
            m_documents[i] = null;
        }
        m_size = 0;
    }

    /**
     * Closes the list after adding all documents to it, this is needed to assign the found attachments to the documents.<p>
     */
    public void closeList() {

        for (int i = 0; i < m_attachments.size(); i++) {
            // loop through all attachments
            CmsDocument att = m_attachments.get(i);
            boolean added = false;
            for (int k = 0; k < m_size; k++) {
                // check if the attachment belongs to a document
                if (m_documents[k].isVersionOf(att)) {
                    // found a document for the attachment
                    if (m_documents[k].addAttachment(att)) {
                        added = true;
                        break;
                    }
                }
            }
            if (!added) {
                // found no document for the attachment, add it as separate attachment to the list
                ensureCapacity(m_size + 1);
                m_documents[m_size++] = att;
            }
        }
        // empty the attachment list
        m_attachments = new ArrayList<CmsDocument>();
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object elem) {

        return indexOf(elem) >= 0;
    }

    /**
     * Increases the capacity of this <tt>CmsDocumentList</tt> instance, if
     * necessary, to ensure  that it can hold at least the number of elements
     * specified by the minimum capacity argument. 
     *
     * @param   minCapacity   the desired minimum capacity.
     */
    public void ensureCapacity(int minCapacity) {

        modCount++;
        int oldCapacity = m_documents.length;
        if (minCapacity > oldCapacity) {
            CmsDocument[] oldData = m_documents;
            int newCapacity = ((oldCapacity * 3) / 2) + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            m_documents = new CmsDocument[newCapacity];
            System.arraycopy(oldData, 0, m_documents, 0, m_size);
        }
    }

    /**
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public CmsDocument get(int index) {

        rangeCheck(index);
        return m_documents[index];
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object elem) {

        if (elem == null) {
            for (int i = 0; i < m_size; i++) {
                if (m_documents[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < m_size; i++) {
                if (elem.equals(m_documents[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @see java.util.AbstractList#remove(int)
     */
    @Override
    public CmsDocument remove(int index) {

        rangeCheck(index);
        modCount++;
        CmsDocument oldValue = m_documents[index];
        int numMoved = m_size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(m_documents, index + 1, m_documents, index, numMoved);
        }
        m_documents[--m_size] = null;
        return oldValue;
    }

    /**
     * @see java.util.AbstractList#set(int, java.lang.Object)
     */
    @Override
    public CmsDocument set(int index, CmsDocument element) {

        rangeCheck(index);
        CmsDocument newValue = element;
        CmsDocument oldValue = m_documents[index];
        m_documents[index] = newValue;
        return oldValue;
    }

    /**
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {

        return m_size;
    }

    /**
     * Checks if the document is added as a subdocument or attachment.<p>
     * 
     * If the document was not merged, it is a new document that has to be added to the list depending on the only versions flag.<p>
     * 
     * @param doc the document to check
     * @return true if the document was added as subdocument, attachment or if the document should not be added, otherwise false
     */
    private boolean checkContainer(CmsDocument doc) {

        boolean docnameWithoutPostfixEquality;
        for (int i = 0; i < m_size; i++) {
            docnameWithoutPostfixEquality = doc.getDocumentNameFullWithoutPostfix().equals(
                m_documents[i].getDocumentNameFullWithoutPostfix());
            if (m_documents[i].isVersionOf(doc) || (m_useTypes && docnameWithoutPostfixEquality)) {
                if (m_useTypes) {

                    if ((m_defaultType != null) && (m_defaultType.equals(doc.getPostfix()))) {
                        if (m_documents[i].getTypes().size() > 0) {
                            doc.setTypes(m_documents[i].getTypes());
                        }
                        doc.addType(m_documents[i]);
                        m_documents[i] = doc;
                    } else {
                        m_documents[i].addType(doc);
                    }
                    return true;
                } else if ((m_useAttachments) && (doc.isAttachment()) && (!m_documents[i].isAttachment())) {
                    // store attachment in temporary list to assign later
                    m_attachments.add(doc);
                    return true;
                } else if ((m_useAttachments) && (m_documents[i].isAttachment()) && (!doc.isAttachment())) {
                    if (isOnlyVersions()) {
                        return true;
                    } else {
                        m_attachments.add(m_documents[i]);
                        m_documents[i] = doc;
                        return true;
                    }
                } else if (m_useLanguages) {
                    // merge the document if languages are used
                    m_documents[i] = m_documents[i].mergeDocuments(doc);
                    return true;
                }
            } else if (isOnlyVersions()) {
                // this is no version of a present document, do not add it at all
                return true;
            }
        }
        return false;
    }

    /**
     * Converts an object to a document.<p>
     * 
     * @param element the object to convert
     * @return the document object representation
     */
    private CmsDocument convertDocument(Object element) {

        try {
            return (CmsDocument)element;
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Tried adding wrong object to document list " + e);
            }
        }
        return CmsDocument.getNullDocument();
    }

    /**
     * Returns the flag if only versions of a specific document should be added to the list.<p>
     *
     * @return the flag if only versions of a specific document should be added to the list
     */
    private boolean isOnlyVersions() {

        return m_onlyVersions;
    }

    /**
     * Check if the given index is in range.  If not, throw an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     * 
     * @param index the index to check
     */
    private void rangeCheck(int index) {

        if ((index > m_size) || (index < 0)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + m_size);
        }
    }
}