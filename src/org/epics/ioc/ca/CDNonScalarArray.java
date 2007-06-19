/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.ca;

/**
 * CDNonScalarArray - A CRRecord array field that contains non-scalar elements.
 * This means the elementType is pvArray, pvEnum, pvMenu, pvLink, or pvStructure.
 * @author mrk
 *
 */
public interface CDNonScalarArray extends CDField {
    /**
     * Get the CDField array.
     * @return The array of elements.
     * An element is null if the corresponding pvArray element is null.
     */
    CDField[] getElementCDFields();
    /**
     * Replace the current PVArray.
     */
    void replacePVArray();
}