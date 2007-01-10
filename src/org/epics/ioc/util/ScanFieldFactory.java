/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.util;

import org.epics.ioc.db.*;
import org.epics.ioc.pv.*;
import org.epics.ioc.pv.Type;


/**
 * 
 * A factory to create a ScanField interface.
 * @author mrk
 *
 */
public class ScanFieldFactory {
    /**
     * Create a ScanField.
     * The record instance must have a top level field named "scan"
     * that must be a "scan" structure as defined in the
     * menuStructureSupportDBD.xml file that appears in package
     * org.epics.ioc.support.
     * ScanFieldFactory does no locking so code that uses it must be thread safe.
     * In general this means that the record instance must be locked when any method is called. 
     * @param dbRecord The record instance.
     * @return The ScanField interface or null of the record instance does not have
     * a valid scan field.
     */
    public static ScanField create(DBRecord dbRecord) {
        Structure structure = (Structure)dbRecord.getField();
        PVData[] datas = dbRecord.getFieldPVDatas();
        int index;
        PVData data;  
        index = structure.getFieldIndex("scan");
        if(index<0) {
            dbRecord.message("field scan does not exist", MessageType.fatalError);
            return null;
        }
        data = datas[index];
        if(data.getField().getType()!=Type.pvStructure){
            dbRecord.message("field scan is not a structure", MessageType.fatalError);
            return null;
        }
        PVStructure scan = (PVStructure)data;
        structure = (Structure)scan.getField();
        DBData dbData = (DBData)scan;
        if(!structure.getStructureName().equals("scan")) {
            dbData.message("is not a scan structure", MessageType.fatalError);
            return null;
        }
        datas = scan.getFieldPVDatas(); 
        index = structure.getFieldIndex("priority");
        if(index<0) {
            dbData.message("does not have field priority", MessageType.fatalError);
            return null;
        }
        data = datas[index];
        if(data.getField().getType()!=Type.pvMenu) {
            dbData.message("is not a menu", MessageType.fatalError);
            return null;
        }
        PVMenu priorityField = (PVMenu)data;
        if(!isPriorityMenu(priorityField)) {
            dbData.message("is not a priority menu", MessageType.fatalError);
            return null;     
        }
        index = structure.getFieldIndex("scan");
        if(index<0) {
            dbData.message("does not have a field scan", MessageType.fatalError);
            return null;
        }
        data = datas[index];
        if(data.getField().getType()!=Type.pvMenu) {
            ((DBData)data).message("is not a menu", MessageType.fatalError);
            return null;
        }
        PVMenu scanField = (PVMenu)data;
        if(!isScanMenu(scanField)) {
            ((DBData)scanField).message("is not a scan menu", MessageType.fatalError);
            return null;        
        }
        index = structure.getFieldIndex("rate");
        if(index<0) {
            dbData.message("does not have a field rate", MessageType.fatalError);
            return null;
        }
        data = datas[index];
        if(data.getField().getType()!=Type.pvDouble) {
            ((DBData)data).message("is not a double", MessageType.fatalError);
            return null;
        }
        PVDouble rateField = (PVDouble)data;
        index = structure.getFieldIndex("eventName");
        if(index<0) {
            dbData.message("does not have a field eventName", MessageType.fatalError);
            return null;
        }
        data = datas[index];
        if(data.getField().getType()!=Type.pvString) {
            ((DBData)data).message("is not a string", MessageType.fatalError);
            return null;
        }
        PVString eventNameField = (PVString)data;
        return new ScanFieldInstance(priorityField,scanField,rateField,eventNameField);
    }
    
    /**
     * Does the menu define the scan types?
     * @param pvMenu The menu.
     * @return (false,true) is the menu defined the scan types.
     */
    public static boolean isScanMenu(PVMenu pvMenu) {
        Menu menu = (Menu)pvMenu.getField();
        if(!menu.getMenuName().equals("scan")) return false;
        String[] choices = pvMenu.getChoices();
        if(choices.length!=3) return false;
        for(int i=0; i<choices.length; i++) {
            try {
                if(ScanType.valueOf(choices[i]).ordinal()!=i) return false;
            } catch(IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
    /**
     * Does the menu define the thread priorities.
     * @param pvMenu The menu.
     * @return (false,true) is the menu defined the thread priorities.
     */
    public static boolean isPriorityMenu(PVMenu pvMenu) {
        Menu menu = (Menu)pvMenu.getField();
        if(!menu.getMenuName().equals("priority")) return false;
        String[] choices = pvMenu.getChoices();
        if(choices.length!=7) return false;
        for(int i=0; i<choices.length; i++) {
            try {
                if(ScanPriority.valueOf(choices[i]).ordinal()!=i) return false;
            } catch(IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
    
    private static class ScanFieldInstance implements ScanField {
        private PVMenu priority;
        private PVMenu scan;
        private PVDouble rate;
        private PVString eventName;
        
        private ScanFieldInstance(PVMenu priority, PVMenu scan, PVDouble rate, PVString eventName) {
            super();
            this.priority = priority;
            this.scan = scan;
            this.rate = rate;
            this.eventName = eventName;
        }
        public String getEventName() {
            return eventName.get();
        }
        public ScanPriority getPriority() {
            return ScanPriority.valueOf(priority.getChoices()[priority.getIndex()]);
        }
        public double getRate() {
            return rate.get();
        }
        public ScanType getScanType() {
            return ScanType.valueOf(scan.getChoices()[scan.getIndex()]);
        }
    }
}