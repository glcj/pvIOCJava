/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.caLink;

import org.epics.ioc.channelAccess.*;
import org.epics.pvData.channelAccess.*;

import org.epics.ioc.install.AfterStart;
import org.epics.ioc.support.ProcessContinueRequester;
import org.epics.ioc.support.SupportProcessRequester;
import org.epics.ioc.support.*;
import org.epics.ioc.util.RequestResult;
import org.epics.pvData.misc.Executor;
import org.epics.pvData.misc.ExecutorFactory;
import org.epics.pvData.misc.ExecutorNode;
import org.epics.pvData.misc.ThreadPriority;
import org.epics.pvData.property.AlarmSeverity;
import org.epics.pvData.pv.PVField;
/**
 * Implementation for a channel access output link.
 * @author mrk
 *
 */
public class ProcessLinkBase extends AbstractLink
implements ProcessCallbackRequester,ProcessContinueRequester, ChannelProcessRequester
{
    /**
     * The constructor.
     * @param supportName The supportName.
     * @param pvField The field being supported.
     */
    public ProcessLinkBase(String supportName,PVField pvField) {
        super(supportName,pvField);
    }
    
    private ChannelProcess channelProcess = null;
    private SupportProcessRequester supportProcessRequester = null;
    private boolean success = true;

    /* (non-Javadoc)
     * @see org.epics.ioc.support.ca.AbstractLinkSupport#connectionChange(boolean)
     */
    public void connectionChange(boolean isConnected) {
        if(isConnected) {
            channel.createChannelProcess(super.channel,this);
        } else {
            ChannelProcess channelProcess = this.channelProcess;
            pvRecord.lock();
            try {
                this.channelProcess = null;
            } finally {
                pvRecord.unlock();
            }
            if(channelProcess!=null) channelProcess.destroy();
        }
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.channelAccess.ChannelProcessRequester#channelProcessConnect(org.epics.pvData.channelAccess.ChannelProcess)
     */
    @Override
    public void channelProcessConnect(ChannelProcess channelProcess) {
        pvRecord.lock();
        try {
            this.channelProcess = channelProcess;
        } finally {
            pvRecord.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.Support#stop()
     */
    public void stop() {
        channelProcess.destroy();
        channelProcess = null;
        super.stop();
    }        
    /* (non-Javadoc)
     * @see org.epics.ioc.process.AbstractSupport#process(org.epics.ioc.process.RecordProcessRequester)
     */
    public void process(SupportProcessRequester supportProcessRequester) {
        if(channelProcess==null) {
            if(alarmSupport!=null) alarmSupport.setAlarm(
                    pvStructure.getFullFieldName() + " not connected",
                    AlarmSeverity.major);
            supportProcessRequester.supportProcessDone(RequestResult.success);
            return;
        }
        this.supportProcessRequester = supportProcessRequester;
        recordProcess.requestProcessCallback(this);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.ProcessCallbackRequester#processCallback()
     */
    @Override
    public void processCallback() {
        channelProcess.process(false);
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.channelAccess.ChannelProcessRequester#processDone(boolean)
     */
    @Override
    public void processDone(boolean success) {
        this.success = success;
        recordProcess.processContinue(this);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.process.ProcessContinueRequester#processContinue()
     */
    public void processContinue() {
        supportProcessRequester.supportProcessDone((success ? RequestResult.success : RequestResult.failure));
    }        
}