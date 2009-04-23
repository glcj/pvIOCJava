/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.pdrv.array;

import org.epics.ioc.install.AfterStart;
import org.epics.ioc.install.LocateSupport;
import org.epics.ioc.pdrv.Status;
import org.epics.ioc.pdrv.Trace;
import org.epics.ioc.pdrv.interfaces.Int32Array;
import org.epics.ioc.pdrv.interfaces.Int32ArrayInterruptListener;
import org.epics.ioc.pdrv.interfaces.Interface;
import org.epics.ioc.support.SupportState;
import org.epics.ioc.support.pdrv.AbstractPortDriverInterruptLink;
import org.epics.pvData.property.AlarmSeverity;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVArray;
import org.epics.pvData.pv.PVIntArray;
import org.epics.pvData.pv.PVStructure;
import org.epics.pvData.pv.Type;

/**
 * Implement Int32ArrayInterrupt.
 * @author mrk
 *
 */
public class BaseInt32ArrayInterrupt extends AbstractPortDriverInterruptLink
implements Int32ArrayInterruptListener
{
    /**
     * The constructor.
     * @param pvStructure The structure being supported.
     * @param supportName The name of the support.
     */
    public BaseInt32ArrayInterrupt(PVStructure pvStructure,String supportName) {
        super(supportName,pvStructure);
    }

    private PVArray valuePVArray = null;
    private Int32Array int32Array = null;
    /* (non-Javadoc)
     * @see org.epics.ioc.support.pdrv.AbstractPortDriverInterruptLink#initialize(org.epics.ioc.support.RecordSupport)
     */
    public void initialize(LocateSupport recordSupport) {
        super.initialize(recordSupport);
        if(!super.checkSupportState(SupportState.readyForStart,supportName)) return;
        if(valuePVField.getField().getType()==Type.scalarArray) {
            valuePVArray = (PVArray)valuePVField;
            if(valuePVArray.getArray().getElementType().isNumeric()) return;   
        }
        super.uninitialize();
        pvStructure.message("value field is not an array with numeric elements", MessageType.fatalError);
        return;
    }      
    /* (non-Javadoc)
     * @see org.epics.ioc.support.pdrv.AbstractPortDriverInterruptLink#uninitialize()
     */
    public void uninitialize() {
        super.uninitialize();
        valuePVArray = null;
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.pdrv.AbstractPortDriverInterruptLink#start()
     */
    public void start(AfterStart afterStart) {
        super.start(afterStart);
        if(!super.checkSupportState(SupportState.ready,supportName)) return;
        Interface iface = device.findInterface(user, "int32Array");
        if(iface==null) {
            pvStructure.message("interface int32Array not supported", MessageType.fatalError);
            super.stop();
            return;
        }
        int32Array = (Int32Array)iface;
        int32Array.addInterruptUser(user, this);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.pdrv.AbstractPortDriverInterruptLink#stop()
     */
    public void stop() {
        if(super.getSupportState()!=SupportState.ready) return;
        super.stop();
        int32Array.removeInterruptUser(user, this);
        int32Array = null;
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.pdrv.interfaces.Int32ArrayInterruptListener#interrupt(org.epics.ioc.pdrv.interfaces.Int32Array)
     */
    public void interrupt(Int32Array int32Array) {
        PVIntArray pvIntArray = int32Array.getPVIntArray();
        if(super.isProcess()) {
            recordProcess.setActive(this);
            Status status = int32Array.startRead(user);
            if(status==Status.success) {
                convert.copyArray(pvIntArray, 0, valuePVArray, 0, pvIntArray.getLength());
                int32Array.endRead(user);
            }
            recordProcess.process(this, false, null);
        } else {
            pvRecord.lock();
            try {
                Status status = int32Array.startRead(user);
                if(status==Status.success) {
                    convert.copyArray(pvIntArray, 0, valuePVArray, 0, pvIntArray.getLength());
                    int32Array.endRead(user);
                } else {
                    alarmSupport.setAlarm(user.getMessage(),AlarmSeverity.invalid);
                } 
            }finally {
                pvRecord.unlock();
            }
            if((deviceTrace.getMask()&Trace.SUPPORT)!=0) {
                deviceTrace.print(Trace.SUPPORT,
                    "pv %s support %s interrupt and record not processed",
                    fullName,supportName);
            }
        }
    }
}
