/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.caV3;

import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import java.util.concurrent.atomic.AtomicBoolean;

import org.epics.ca.client.ChannelGet;
import org.epics.ca.client.ChannelGetRequester;
import org.epics.pvData.factory.StatusFactory;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVStructure;
import org.epics.pvData.pv.Status;
import org.epics.pvData.pv.StatusCreate;
import org.epics.pvData.pv.Status.StatusType;



/**
 * Base class that implements ChannelGet for communicating with a V3 IOC.
 * @author mrk
 *
 */


public class BaseV3ChannelGet
implements ChannelGet,GetListener,ConnectionListener
{
    private static final StatusCreate statusCreate = StatusFactory.getStatusCreate();
    private static final Status okStatus = statusCreate.getStatusOK();
    private static Status channelDestroyedStatus = statusCreate.createStatus(StatusType.ERROR, "channel destroyed", null);
    private static Status channelNotConnectedStatus = statusCreate.createStatus(StatusType.ERROR, "channel not connected", null);
    private static Status disconnectedWhileActiveStatus = statusCreate.createStatus(StatusType.ERROR, "disconnected while active", null);
    private static Status createChannelStructureStatus = statusCreate.createStatus(StatusType.ERROR, "createChannelStructure failed", null);
    
    private V3Channel v3Channel = null;
    private ChannelGetRequester channelGetRequester = null;
    private V3ChannelStructure v3ChannelStructure = null;
    
    private boolean isDestroyed = false;
    
    private AtomicBoolean isActive = new AtomicBoolean(false);
    /**
     * Constructor.
     * @param channelGetRequester The channelGetRequester.
     */
    public BaseV3ChannelGet(ChannelGetRequester channelGetRequester)
    {
        this.channelGetRequester = channelGetRequester;
    }
    /**
     * Initialize the channelGet.
     * @param v3Channel The V3Channel
     * @param pvRequest The request structure.
     */
    public void init(V3Channel v3Channel,PVStructure pvRequest)
    {
        this.v3Channel = v3Channel;
        v3Channel.add(this);
        v3ChannelStructure = new BaseV3ChannelStructure(v3Channel);
        if(v3ChannelStructure.createPVStructure(pvRequest,true)==null) {
            channelGetRequester.channelGetConnect(createChannelStructureStatus,null,null,null);
            destroy();
        } else {
            channelGetRequester.channelGetConnect(okStatus, this, v3ChannelStructure.getPVStructure(), v3ChannelStructure.getBitSet());
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.ca.ChannelGet#destroy()
     */
    public void destroy() {
        isDestroyed = true;
        v3Channel.remove(this);
    }
    /* (non-Javadoc)
     * @see org.epics.ca.client.ChannelGet#get(boolean)
     */
    @Override
    public void get(boolean lastRequest) {
        if(isDestroyed) {
            getDone(channelDestroyedStatus);
            return;
        }
        gov.aps.jca.Channel jcaChannel = v3Channel.getJCAChannel();
        if(jcaChannel.getConnectionState()!=Channel.ConnectionState.CONNECTED) {
            getDone(channelNotConnectedStatus);
            return;
        }
        isActive.set(true);
        try {
            jcaChannel.get(v3ChannelStructure.getRequestDBRType(),jcaChannel.getElementCount(), this);
        } catch (Throwable th) {
            getDone(statusCreate.createStatus(StatusType.ERROR, "failed to get", th));
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.util.Requester#getRequesterName()
     */
    public String getRequesterName() {
        return v3Channel.getRequesterName();
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.util.Requester#message(java.lang.String, org.epics.ioc.util.MessageType)
     */
    public void message(String message, MessageType messageType) {
        v3Channel.message(message, messageType);   
    }
    /* (non-Javadoc)
     * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
     */
    public void getCompleted(GetEvent getEvent) {
        DBR fromDBR = getEvent.getDBR();
        if(fromDBR==null) {
            CAStatus caStatus = getEvent.getStatus();
            getDone(statusCreate.createStatus(StatusType.ERROR, caStatus.toString(), null));
            return;
        }
        v3ChannelStructure.toStructure(fromDBR);
        getDone(okStatus);
    }
    /* (non-Javadoc)
     * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
     */
    public void connectionChanged(ConnectionEvent arg0) {
        if(!arg0.isConnected()) {
            getDone(disconnectedWhileActiveStatus);
        }
    }
    
    private void getDone(Status success) {
        if(!isActive.getAndSet(false)) return;
        channelGetRequester.getDone(success);
    }
}
