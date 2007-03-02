/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.ca;

import org.epics.ioc.pv.*;

/**
 * ChannelDataBaseArrayArray - A CDRecord field that holds a PVArrayArray.
 * @author mrk
 *
 */
public interface CDArrayArray extends CDField  {
    /**
     * Get the CDField array for the elements.
     * @return
     */
    CDField[] getElementCDBArrays();
    /**
     * Replace the PVArrayArray.
     */
    void replacePVArrayArray();
    /**
     * Put to the PVArrayArray.
     * @param targetPVArrayArray The new values to put.
     */
    void dataPut(PVArrayArray targetPVArrayArray);
    /**
     * A put to a subfield has occured. 
     * @param requested The target field that has targetPVField as a subfield.
     * @param targetPVField The data that has been modified..
     */
    boolean fieldPut(PVField requested,PVField targetPVField);
    /**
     * A put to an enum subfield has occured. 
     * The enum index has been modified.
     * @param requested The target field that has targetPVEnum as a subfield.
     * @param targetPVEnum The enum interface.
     */
    boolean enumIndexPut(PVField requested,PVEnum targetPVEnum);
    /**
     * A put to an enum subfield has occured. 
     * The enum choices has been modified.
     * @param requested The target field that has targetPVEnum as a subfield.
     * @param targetPVEnum The enum interface.
     */
    boolean enumChoicesPut(PVField requested,PVEnum targetPVEnum);
    /**
     * A put to the supportName of a subfield has occured. 
     * The supportName has been modified.
     * @param requested The target field that has targetPVField as a subfield.
     * @param targetPVField The pvField in the structure.
     */
    boolean supportNamePut(PVField requested,PVField targetPVField);
    /**
     * A put to the configurationStructure of a pvLink subfield has occured. 
     * The link configration structure has been modified.
     * @param requested The target field that has targetPVLink as a subfield.
     * @param targetPVLink The link interface.
     */
    boolean configurationStructurePut(PVField requested,PVLink targetPVLink);
}
