/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv;

/**
 * get/put a boolean array.
 * The caller must be prepared to get/put the array in chunks.
 * The return argument is always the number of elements that were transfered.
 * It may be less than the number requested.
 * @author mrk
 *
 */
public interface PVBooleanArray extends PVArray{
    /**
     * Get values from a <i>PVBooleanArray</i>
     * and put them into <i>boolean[]to</i>.
     * @param offset The offset to the first element to get.
     * @param len The maximum number of elements to transfer.
     * @param data The class containing the data and an offset into the data.
     * Get sets these values. The caller must do the actual data transfer.
     * @return The number of elements that can be transfered.
     * This is always less than or equal to len.
     * If the value is less then get should be called again.
     * If the return value is greater than 0 then data.data is
     * a reference to the array and data.offset is the offset into the
     * array.
     */
    int get(int offset, int len, BooleanArrayData data);
    /**
     * Put values into a <i>PVBooleanArray</i> from <i>boolean[]to</i>.
     * @param offset The offset to the first element to put.
     * @param len The maximum number of elements to transfer.
     * @param from The array from which to get the data.
     * @param fromOffset The offset into from.
     * @return The number of elements transfered.
     * This is always less than or equal to len.
     * If the value is less then put should be called again.
     * @throws IllegalStateException if the field is not mutable.
     */
    int put(int offset,int len, boolean[] from, int fromOffset);
    /**
     * Let this PVArray share an array value.
     * If the capacity is changed the value will no longer be shared.
     * @param value The data array.
     * @param length The initial length.
     * @return (false,true) if the request was successful.
     * The request will fail if isShareable() is false.
     */
    boolean share(boolean[] value, int length);
}
