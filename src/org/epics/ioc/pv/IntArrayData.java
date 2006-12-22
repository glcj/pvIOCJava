/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv;
/**
 * Class required by get/put PVIntArray methods.
 * Get will set data and offset.
 * Put requires that the caller set data and offset.
 * @author mrk
 *
 */
public class IntArrayData {
    /**
     * The int[].
     * PVIntArray.get sets this value.
     * PVIntArray.put requires that the caller set the value. 
     */
    public int[] data;
    /**
     * The offset.
     * PVIntArray.get sets this value.
     * PVIntArray.put requires that the caller set the value. 
     */
    public int offset;
}
