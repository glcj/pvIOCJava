/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.basic;

import org.epics.ioc.database.PVRecord;
import org.epics.ioc.database.PVRecordField;
import org.epics.ioc.install.AfterStart;
import org.epics.ioc.support.AbstractSupport;
import org.epics.ioc.support.Support;
import org.epics.ioc.support.SupportProcessRequester;
import org.epics.ioc.support.SupportState;
import org.epics.ioc.util.EventAnnounce;
import org.epics.ioc.util.EventScanner;
import org.epics.ioc.util.RequestResult;
import org.epics.ioc.util.ScannerFactory;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVField;
import org.epics.pvData.pv.PVScalar;
import org.epics.pvData.pv.PVString;
import org.epics.pvData.pv.ScalarType;
import org.epics.pvData.pv.Type;


/**
 * Support a field which must have type string.
 * The string value is an event name. Each time process is called the event is announced.
 * @author mrk
 *
 */
public class EventFactory {
    /**
     * Create the support for the field.
     * @param pvRecordField The field which must have type string.
     * @return The support instance.
     */
    public static Support create(PVRecordField pvRecordField) {
    	PVField pvField = pvRecordField.getPVField();
        if(pvField.getField().getType()!=Type.scalar) {
            pvField.message("illegal field type. Must be strinng", MessageType.error);
            return null;
        }
        PVScalar pvScalar = (PVScalar)pvField;
        if(pvScalar.getScalar().getScalarType()!=ScalarType.pvString) {
            pvField.message("illegal field type. Must be strinng", MessageType.error);
            return null;
        }
        return new EventImpl(pvRecordField);
    }
    
    
    static private class EventImpl extends AbstractSupport
    {
        /* (non-Javadoc)
         * @see org.epics.ioc.process.RecordSupport#processRecord(org.epics.ioc.process.RecordProcessRequester)
         */
        private static final String supportName = "org.epics.ioc.event";
        private static final EventScanner eventScanner = ScannerFactory.getEventScanner();
        private SupportState supportState = SupportState.readyForInitialize;
        private final PVString pvEventName;
        private final PVRecord pvRecord;
        private EventAnnounce eventAnnounce = null;
        private String eventName = null;
        
        private EventImpl(PVRecordField pvRecordField) {
            super(supportName,pvRecordField);
            this.pvEventName = (PVString)pvRecordField.getPVField();
            pvRecord = pvRecordField.getPVRecord();
        }
        
        /* (non-Javadoc)
         * @see org.epics.ioc.process.Support#start()
         */
        @Override
        public void start(AfterStart afterStart) {
            supportState = SupportState.ready;
            eventName = pvEventName.get();
            if(eventName!=null) {
                eventAnnounce = eventScanner.addEventAnnouncer(eventName, pvRecord.getRecordName());
            }
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.process.Support#stop()
         */
        @Override
        public void stop() {
            if(eventName!=null) {
                eventScanner.removeEventAnnouncer(eventAnnounce, pvRecord.getRecordName());
                eventAnnounce = null;
                eventName = null;
            }
            supportState = SupportState.readyForStart;
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.process.Support#uninitialize()
         */
        @Override
        public void uninitialize() {
            supportState = SupportState.readyForInitialize;
            setSupportState(SupportState.readyForInitialize);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.process.Support#process(org.epics.ioc.process.RecordProcessRequester)
         */
        @Override
        public void process(SupportProcessRequester supportProcessRequester) {
            if(supportState!=SupportState.ready) {
                super.message(
                        "process called but supportState is "
                        + supportState.toString(),
                        MessageType.error);
                supportProcessRequester.supportProcessDone(RequestResult.failure);
            }
            String newName = pvEventName.get();
            if(eventName!=null && !eventName.equals(newName)) {
                eventScanner.removeEventAnnouncer(eventAnnounce, pvRecord.getRecordName());
                eventAnnounce = null;
                eventName = newName;
                if(eventName!=null) {
                    eventAnnounce = eventScanner.addEventAnnouncer(eventName, pvRecord.getRecordName());
                }
            }
            if(eventAnnounce!=null) eventAnnounce.announce();
            supportProcessRequester.supportProcessDone(RequestResult.success);
        }
    }
}
