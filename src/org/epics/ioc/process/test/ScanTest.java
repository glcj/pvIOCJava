/**
 * Copyright - See the COPYRIGHT that is included with this disctibution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.process.test;

import junit.framework.TestCase;

import org.epics.ioc.pv.*;
import org.epics.ioc.dbd.*;
import org.epics.ioc.db.*;
import org.epics.ioc.util.*;

/**
 * JUnit test for scan test.
 * @author mrk
 *
 */
public class ScanTest extends TestCase {
    private static Requestor iocRequestor = null;
    /**
     * test scan.
     */
    private static MessageType maxError = MessageType.info;
    public static void testScan() {
        iocRequestor = new Listener();
        DBD dbd = DBDFactory.getMasterDBD();
        XMLToDBDFactory.convert(dbd,
                "dbd/menuStructureSupport.xml",
                iocRequestor);
        XMLToDBDFactory.convert(dbd,
                "src/org/epics/ioc/process/test/exampleDBD.xml",
                iocRequestor);
        IOCDB iocdbMaster = IOCDBFactory.getMaster();
        boolean initOK = IOCFactory.initDatabase("src/org/epics/ioc/process/test/scanDB.xml",iocRequestor);
        if(!initOK) return;
        
//        Map<String,DBRecord> recordMap  recordMap = iocdbAdd.getRecordMap();
//        Set<String> keys = recordMap.keySet();
//        for(String key: keys) {
//            RecordProcess recordProcess = 
//                recordMap.get(key).getRecordProcess();
//            recordProcess.setTrace(true);
//        }
        PeriodicScanner periodicScanner = ScannerFactory.getPeriodicScanner();
        String list = periodicScanner.toString();
        System.out.println(list);
        EventScanner eventScanner = ScannerFactory.getEventScanner();
        list = eventScanner.toString();
        System.out.println(list);
        
        DBRecord dbRecord = null;
        dbRecord = iocdbMaster.findRecord("counterPush");
        assertNotNull(dbRecord);
        Structure structure = (Structure)dbRecord.getField();
        PVData[] pvData = dbRecord.getFieldPVDatas();        
        int index = structure.getFieldIndex("value");
        PVData counterPushValue = pvData[index];
        dbRecord = iocdbMaster.findRecord("doubleReceive09");
        assertNotNull(dbRecord);
        pvData = dbRecord.getFieldPVDatas();        
        index = structure.getFieldIndex("value");
        PVData doubleReceive09Value = pvData[index];
        dbRecord = iocdbMaster.findRecord("counterEvent0");
        assertNotNull(dbRecord);
        pvData = dbRecord.getFieldPVDatas();        
        index = structure.getFieldIndex("value");
        PVData counterEvent0Value = pvData[index];
        dbRecord = iocdbMaster.findRecord("counterEvent1");
        assertNotNull(dbRecord);
        pvData = dbRecord.getFieldPVDatas();        
        index = structure.getFieldIndex("value");
        PVData counterEvent1Value = pvData[index];
        for(int i=0; i<5; i++) {
            try {
                Thread.sleep(1000);
                System.out.println("    counterPush " + counterPushValue.toString());
                System.out.println("doubleReceive09 " + doubleReceive09Value.toString());
                System.out.println("  counterEvent0 " + counterEvent0Value.toString());
                System.out.println("  counterEvent1 " + counterEvent1Value.toString());
                System.out.println();
            } catch (InterruptedException e) {
            }
        }
        initOK = IOCFactory.initDatabase("src/org/epics/ioc/process/test/scanDB.xml",iocRequestor);
        if(!initOK) {
            System.out.println("IOCFactory.initDatabase failed");
        }
        initOK = IOCFactory.initDatabase("src/org/epics/ioc/process/test/scanAddDB.xml",iocRequestor);
        if(!initOK) {
            System.out.println("IOCFactory.initDatabase failed");
        }
        String[] recordList = iocdbMaster.recordList(".*");
        System.out.print("record list");
        for(int i=0; i<recordList.length; i++) {
            if((i+1)%5 == 0) {
                System.out.println();
                System.out.print("    ");
            } else {
                System.out.print(" ");
            }
            System.out.print(recordList[i]);
        }
        System.out.println();
        dbRecord = iocdbMaster.findRecord("counter");
        assertNotNull(dbRecord);
        pvData = dbRecord.getFieldPVDatas();        
        index = structure.getFieldIndex("value");
        PVData counterValue = pvData[index];
        dbRecord = iocdbMaster.findRecord("double02");
        assertNotNull(dbRecord);
        pvData = dbRecord.getFieldPVDatas();        
        index = structure.getFieldIndex("value");
        PVData double02Value = pvData[index];
        list = periodicScanner.toString();
        System.out.println(list);
        while(true) {
            try {
                Thread.sleep(1000);
                System.out.println("    counterPush " + counterPushValue.toString());
                System.out.println("doubleReceive09 " + doubleReceive09Value.toString());
                System.out.println("  counterEvent0 " + counterEvent0Value.toString());               
                System.out.println("  counterEvent1 " + counterEvent1Value.toString());
                System.out.println(" counter " + counterValue.toString());
                System.out.println("double02 " + double02Value.toString());
                System.out.println();
            } catch (InterruptedException e) {
            }
        }
    }
       
    private static class Listener implements Requestor {
        /* (non-Javadoc)
         * @see org.epics.ioc.util.Requestor#getRequestorName()
         */
        public String getRequestorName() {
            return "ScanTest";
        }

        /* (non-Javadoc)
         * @see org.epics.ioc.util.Requestor#message(java.lang.String, org.epics.ioc.util.MessageType)
         */
        public void message(String message, MessageType messageType) {
            System.out.println(message);
            if(messageType.ordinal()>maxError.ordinal()) maxError = messageType;
        }
    }
}