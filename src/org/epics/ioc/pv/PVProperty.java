/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv;

/**
 * @author mrk
 *
 */
public interface PVProperty {
    /**
     * Find a field that is a subfield or a property of this field.
     * The fieldName is of the form item.item... where item is name or name[index].
     * 
     * The algorithm implemented by findProperty is:
     * <ul>
     *  <li>Start with the leftmost item and find it.</li>
     *  <li>find the next leftmost item and find it.</li>
     *  <li>Continue until all items have been found or a search fails.</li>
     *  <li>Return the interface for the last item or null if a search fails.</li>
     * </ul>
     * 
     *  An item is found as follows:
     *  <ul>
     *   <li>Find the name part of item. If no [index] is present then fail.</li>
     *   <li>If [index] is present than field must be a structure with elementType structure or array.
     *  If so then make sure the index element is found. If so it is the field.</li>
     *   <li>fail</li>
     *  </ul>
     *  
     *  A name is found as follows:
     *  <ol>
     *    <li>If the Field for the current PVField is named "value" back up one level in parent tree.</li>
     *    <li>The current PV must be a structure. If not fail.</li>
     *    <li>If the current PVField is type structure with a fieldName=name then use it.</li>
     *    <li>If the fieldName is not timeStamp than fail.</li>
     *    <li>If the parent tree is null then fail.</li>
     *    <li>Back up one level in the parent tree and go to 2).</li>
     *  </ol>
     *  
     * @param pvField The pvField.
     * @param fieldName A string of the form item.item... where item is name or name[index]
     * @return The PVField interface for the property or null if not found. 
     */
    PVField findProperty(PVField pvField,String fieldName);
    /**
     * Find a property by searching up the parent tree.
     * @param pvField The pvField.
     * @param propertyName The property name which is expected to match the name of a field.
     * @return The interface to the first field found that is not a null structure or null if not found.
     */
    PVField findPropertyViaParent(PVField pvField,String propertyName);
    /**
     * Get the names of all the properties for this PVField.
     * A property name is the field name.
     * If this PVfield is a structure then every field except null structures is a property.
     * If this PVField is the value field the parent is the starting point and the properties will
     * not include the value field itself. In addition a search up the parent tree is made for the timeStamp.
     * @param pvField The pvField.
     * @return The String array for the names of the properties.
     */
    String[] getPropertyNames(PVField pvField);
    /**
     * Replace the data implementation for a field.
     * @param newPVField The new implementation for this field.
     */
}
