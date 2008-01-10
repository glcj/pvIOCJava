/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv;
/**
 * Class required by get/put PVStringArray methods.
 * Get will set data and offset.
 * @author mrk
 *
 */
public class StringArrayData {
    /**
     * The PVString[].
     * PVStringArray.get sets this value.
     * PVStringArray.put requires that the caller set the value. 
     */
    public String[] data;
    /**
     * The offset.
     * PVStringArray.get sets this value.
     * PVStringArray.put requires that the caller set the value. 
     */
    public int offset;
}
