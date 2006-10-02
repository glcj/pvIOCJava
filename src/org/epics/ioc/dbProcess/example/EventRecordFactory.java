/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.dbProcess.example;

import org.epics.ioc.dbProcess.*;
import org.epics.ioc.dbAccess.*;
import org.epics.ioc.pvAccess.*;
import org.epics.ioc.util.*;

/**
 * Record that holds a double value, an input link, and an array of process or output links.
 * @author mrk
 *
 */
public class EventRecordFactory {
    public static Support create(DBStructure dbStructure) {
        return new EventRecordSupport(dbStructure);
    }
    
    
    static private class EventRecordSupport extends AbstractSupport {
        private static String supportName = "eventRecord";
        private SupportState supportState = SupportState.readyForInitialize;
        private DBRecord dbRecord;
        private DBString value = null;
        private ProcessResult result = ProcessResult.success;
        private EventScanner eventScanner = null;
        private EventAnnounce eventAnnounce = null;
        private String eventName = null;
        
        private EventRecordSupport(DBStructure dbStructure) {
            super(supportName,dbStructure);
            dbRecord = dbStructure.getRecord();
            eventScanner = ScannerFactory.getEventScanner();
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#initialize()
         */
        public void initialize() {
            IOCDB iocdb = dbRecord.getIOCDB();
            DBAccess dbAccess = iocdb.createAccess(dbRecord.getRecordName());
            DBData dbData;
            AccessSetResult result = dbAccess.setField("value");
            if(result!=AccessSetResult.thisRecord) {
                dbRecord.message(
                        "field value does not exist",
                        IOCMessageType.error);
                return;
            }
            dbData = dbAccess.getField();
            if(dbData.getField().getType()!=Type.pvString) {
                dbRecord.message(
                        "field value is not a string",
                        IOCMessageType.error);
                return;
            }
            value = (DBString)dbData;
            supportState = SupportState.readyForStart;
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#start()
         */
        public void start() {
            supportState = SupportState.ready;
            eventName = value.get();
            if(eventName!=null) {
                eventAnnounce = eventScanner.addEventAnnouncer(eventName, dbRecord.getRecordName());
            }
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#stop()
         */
        public void stop() {
            if(eventName!=null) {
                eventScanner.removeEventAnnouncer(eventAnnounce, dbRecord.getRecordName());
                eventAnnounce = null;
                eventName = null;
            }
            supportState = SupportState.readyForStart;
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#uninitialize()
         */
        public void uninitialize() {
            supportState = SupportState.readyForInitialize;
            setSupportState(SupportState.readyForInitialize);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#process(org.epics.ioc.dbProcess.ProcessCompleteListener)
         */
        public ProcessReturn process(ProcessCompleteListener listener) {
            if(supportState!=SupportState.ready) {
                dbRecord.message(
                        "process called but supportState is "
                        + supportState.toString(),
                        IOCMessageType.error);
                return ProcessReturn.failure;
            }
            String newName = value.get();
            if(newName!=eventName) {
                eventScanner.removeEventAnnouncer(eventAnnounce, dbRecord.getRecordName());
                eventAnnounce = null;
                eventName = newName;
                if(eventName!=null) {
                    eventAnnounce = eventScanner.addEventAnnouncer(eventName, dbRecord.getRecordName());
                }
            }
            if(eventAnnounce!=null) eventAnnounce.announce();
            return ProcessReturn.success;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbProcess.Support#processContinue()
         */
        public void processContinue() {
            // nothing to do
        }
    }
}
