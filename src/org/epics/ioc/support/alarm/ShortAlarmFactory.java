/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.alarm;

import org.epics.ioc.database.PVRecordStructure;
import org.epics.ioc.support.AbstractSupport;
import org.epics.ioc.support.Support;
import org.epics.ioc.support.SupportProcessRequester;
import org.epics.ioc.support.SupportState;
import org.epics.ioc.util.RequestResult;
import org.epics.pvData.property.AlarmSeverity;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVBoolean;
import org.epics.pvData.pv.PVInt;
import org.epics.pvData.pv.PVShort;
import org.epics.pvData.pv.PVStructure;

/**
 * Support for an shortAlarm link.
 * @author mrk
 *
 */
public class ShortAlarmFactory {
    /**
     * Create support for an byteAlarm structure.
     * @param pvRecordStructure The structure.
     * @return An interface to the support.
     */
    public static Support create(PVRecordStructure pvRecordStructure) {
        return new ShortAlarmImpl(pvRecordStructure);
    }
    
    private static class ShortAlarmImpl extends AbstractSupport
    {
        private static final String supportName = "org.epics.ioc.shortAlarm";
        private PVRecordStructure pvRecordStructure;
        private PVStructure pvStructure;
        private AlarmSupport alarmSupport;
        private PVShort pvValue;
        
        private PVBoolean pvActive;
        private PVShort pvHystersis;
        private PVShort pvLowAlarmLimit;
        private PVInt pvLowAlarmSeverity;
        private PVShort pvLowWarningLimit;
        private PVInt pvLowWarningSeverity;
        private PVShort pvHighWarningLimit;
        private PVInt pvHighWarningSeverity;
        private PVShort pvHighAlarmLimit;
        private PVInt pvHighAlarmSeverity;
        
        private short lastAlarmIntervalValue;
        private int lastAlarmSeverity = 0;
        private String lastAlarmMessage = null;

       
        private ShortAlarmImpl(PVRecordStructure pvRecordStructure) {
            super(supportName,pvRecordStructure);
            this.pvRecordStructure = pvRecordStructure;
            pvStructure = pvRecordStructure.getPVStructure();
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.support.AbstractSupport#initialize()
         */
        @Override
        public void initialize() {
            if(!super.checkSupportState(SupportState.readyForInitialize,supportName)) return;
            SupportState supportState = SupportState.readyForStart;
            pvValue = pvStructure.getParent().getShortField("value");
            if(pvValue==null) return;
            alarmSupport = AlarmSupportFactory.findAlarmSupport(pvRecordStructure);
            if(alarmSupport==null) {
                super.message("no alarmSupport", MessageType.error);
                return;
            }
            pvActive = pvStructure.getBooleanField("active");
            if(pvActive==null) return;
            pvLowAlarmSeverity = pvStructure.getIntField("lowAlarmSeverity");
            if(pvLowAlarmSeverity==null) return;
            pvLowAlarmLimit = pvStructure.getShortField("lowAlarmLimit");
            if(pvLowAlarmLimit==null) return;
            pvLowWarningSeverity = pvStructure.getIntField("lowWarningSeverity");
            if(pvLowWarningSeverity==null) return;
            pvLowWarningLimit = pvStructure.getShortField("lowWarningLimit");
            if(pvLowWarningLimit==null) return;
            
            pvHighWarningSeverity = pvStructure.getIntField("highWarningSeverity");
            if(pvHighWarningSeverity==null) return;
            pvHighWarningLimit = pvStructure.getShortField("highWarningLimit");
            if(pvHighWarningLimit==null) return;
            pvHighAlarmSeverity = pvStructure.getIntField("highAlarmSeverity");
            if(pvHighAlarmSeverity==null) return;
            pvHighAlarmLimit = pvStructure.getShortField("highAlarmLimit");
            if(pvHighAlarmLimit==null) return;                       
            pvHystersis = pvStructure.getShortField("hystersis");
            if(pvHystersis==null) return;
            setSupportState(supportState);
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.process.Support#process(org.epics.ioc.process.RecordProcessRequester)
         */
        @Override
        public void process(SupportProcessRequester supportProcessRequester) {
            if(pvActive.get()) checkAlarm();
            supportProcessRequester.supportProcessDone(RequestResult.success);
        }                

        private void checkAlarm() {
        	boolean active = pvActive.get();
            if(!active) return;
            short  val = pvValue.get();
            int severity = pvHighAlarmSeverity.get();
            short level = pvHighAlarmLimit.get();
            if(severity>0 && (val>=level)) {
            	raiseAlarm(level,val,severity,"highAlarm");
            	return;
            }
            severity = pvLowAlarmSeverity.get();
            level = pvLowAlarmLimit.get();
            if(severity>0 && (val<=level)) {
            	raiseAlarm(level,val,severity,"lowAlarm");
            	return;
            }
            severity = pvHighWarningSeverity.get();
            level = pvHighWarningLimit.get();
            if(severity>0 && (val>=level)) {
            	raiseAlarm(level,val,severity,"highWarning");
            	return;
            }
            severity = pvLowWarningSeverity.get();
            level = pvLowWarningLimit.get();
            if(severity>0 && (val<=level)) {
            	raiseAlarm(level,val,severity,"lowWarning");
            	return;
            }
            raiseAlarm((short)0,val,0,"");
        }
        
        private void raiseAlarm(short intervalValue,short val,int severity,String message) {
        	AlarmSeverity alarmSeverity = AlarmSeverity.getSeverity(severity);
            if(severity<lastAlarmSeverity) {
                int diff = lastAlarmIntervalValue - val;
                if(diff<0) diff = -diff;
                if(diff<pvHystersis.get()) {
                    alarmSeverity = AlarmSeverity.getSeverity(lastAlarmSeverity);
                    intervalValue = lastAlarmIntervalValue;
                    message = lastAlarmMessage;
                }
            }
            if(alarmSeverity==AlarmSeverity.none) {
                lastAlarmSeverity = severity;
                return;
            }
            alarmSupport.setAlarm(message, alarmSeverity);
            lastAlarmIntervalValue = intervalValue;
            lastAlarmSeverity = severity;
            lastAlarmMessage = message;

        }
    }
}
