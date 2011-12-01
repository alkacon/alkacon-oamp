/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.usagereport/src/com/alkacon/opencms/v8/usagereport/CmsUpdatefeedConfigFolderCompataror.java,v $
 * Date   : $Date: 2009/02/05 09:56:20 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2008 Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.usagereport;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares folders with the length of the path.<p>
 * 
 * @author Ruediger Kurz
 * 
 * @version $Revision: 1.1 $ 
 */
public class CmsUpdatefeedConfigFolderCompataror implements Serializable, Comparator {

    /** SerialVersionUID. */
    private static final long serialVersionUID = 353802134184849441L;

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {

        if ((arg0 == arg1)
            || !(arg0 instanceof CmsUpdatefeedConfigFolder)
            || !(arg1 instanceof CmsUpdatefeedConfigFolder)) {
            return 0;
        }

        CmsUpdatefeedConfigFolder fol0 = (CmsUpdatefeedConfigFolder)arg0;
        CmsUpdatefeedConfigFolder fol1 = (CmsUpdatefeedConfigFolder)arg1;

        int l0 = fol0.getStartfolder().length();
        int l1 = fol1.getStartfolder().length();

        if (l0 < l1) {
            return -1;
        } else if (l0 == l1) {
            return 0;
        } else {
            return 1;
        }
    }
}
