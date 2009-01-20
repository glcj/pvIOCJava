/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.ca;

import org.epics.pvData.pv.*;
import org.epics.pvData.misc.*;
import org.epics.pvData.factory.*;
import org.epics.pvData.property.*;
import org.epics.ioc.support.*;
import org.epics.ioc.support.alarm.*;

import org.epics.ioc.util.*;


import org.epics.ioc.ca.*;


/**
 * Implementation for a channel access monitorNotify link.
 * @author mrk
 *
 */
public class MonitorNotifySupportBase extends AbstractLinkSupport
implements RecordProcessRequester,ChannelMonitorNotifyRequester
{
    private ChannelMonitorNotify channelMonitorNotify = null;
    private boolean isActive = false;
    
    /**
     * The constructor.
     * @param supportName The supportName.
     * @param pvStructure The pvStructure for the field being supported.
     */
    public MonitorNotifySupportBase(String supportName,PVStructure pvStructure) {
        super(supportName,pvStructure);
    }      
    /* (non-Javadoc)
     * @see org.epics.ioc.support.ca.AbstractLinkSupport#initialize(org.epics.ioc.support.RecordSupport)
     */
    public void initialize(RecordSupport recordSupport) {
        super.initialize(recordSupport);
        if(!super.checkSupportState(SupportState.readyForStart,null)) return;
        if(!recordProcess.setRecordProcessRequester(this)) {
            message("notifySupport but record already has recordProcessor",MessageType.error);
            uninitialize();
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.Support#start()
     */
    public void start() {
        super.start();
        if(!super.checkSupportState(SupportState.ready,null)) return;
        super.connect();
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.AbstractSupport#process(org.epics.ioc.process.SupportProcessRequester)
     */
    public void process(SupportProcessRequester supportProcessRequester) {
        supportProcessRequester.supportProcessDone(RequestResult.success);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.RecordProcessRequester#recordProcessComplete()
     */
    public void recordProcessComplete() {
        isActive = false;
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.RecordProcessRequester#recordProcessResult(org.epics.ioc.util.RequestResult)
     */
    public void recordProcessResult(RequestResult requestResult) {
        // nothing to do
        
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.ca.AbstractLinkSupport#connectionChange(boolean)
     */
    public void connectionChange(boolean isConnected) {
        if(isConnected) {
            String fieldName = channel.getFieldName();
            if(fieldName==null) fieldName = "value";
            ChannelField channelField = channel.createChannelField(fieldName);
            ChannelFieldGroup channelFieldGroup = channel.createFieldGroup(this);
            channelFieldGroup.addChannelField(channelField);
            channelMonitorNotify = ChannelMonitorNotifyFactory.create(channel, this);
            channelMonitorNotify.setFieldGroup(channelFieldGroup);
            channelMonitorNotify.start();
            
        } else {
            channelMonitorNotify.destroy();
            channelMonitorNotify = null;
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.ca.ChannelMonitorNotifyRequester#monitorEvent()
     */
    public void monitorEvent() {
        if(isActive) {
            pvRecord.lock();
            try {
                alarmSupport.setAlarm(
                    "channelMonitorNotify event but record already active", AlarmSeverity.minor);
            } finally {
                pvRecord.unlock();
            }
            return;
        }
        isActive = recordProcess.process(this, false, null);
    } 
}
