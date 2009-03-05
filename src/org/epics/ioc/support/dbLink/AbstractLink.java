/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.dbLink;

import org.epics.ioc.support.AbstractSupport;
import org.epics.ioc.support.RecordProcess;
import org.epics.ioc.support.RecordSupport;
import org.epics.ioc.support.SupportDatabase;
import org.epics.ioc.support.SupportDatabaseFactory;
import org.epics.ioc.support.SupportState;
import org.epics.ioc.support.alarm.AlarmSupport;
import org.epics.ioc.support.alarm.AlarmSupportFactory;
import org.epics.pvData.factory.ConvertFactory;
import org.epics.pvData.factory.PVDatabaseFactory;
import org.epics.pvData.property.TimeStamp;
import org.epics.pvData.property.TimeStampFactory;
import org.epics.pvData.pv.Convert;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVDatabase;
import org.epics.pvData.pv.PVField;
import org.epics.pvData.pv.PVRecord;
import org.epics.pvData.pv.PVString;
import org.epics.pvData.pv.PVStructure;
import org.epics.pvData.pv.Type;

/**
 * Abstract Support for database Links.
 * This is nopt public since it is for use by this package.
 * @author mrk
 *
 */
abstract class AbstractLink extends AbstractSupport {
    /**
     * master database.
     */
    protected static final PVDatabase pvDatabase = PVDatabaseFactory.getMaster();
    /**
     * master support database
     */
    protected static final SupportDatabase supportDatabase = SupportDatabaseFactory.get(pvDatabase);
    /**
     * The convert implementation.
     */
    protected static final Convert convert = ConvertFactory.getConvert();
    /**
     * The interface for getting the pvName.
     */
    protected PVString pvnamePV = null;
    /**
     * The pvStructure that this link supports.
     */
    protected PVStructure pvDatabaseLink;
    /**
     * The pvRecord for pvStructure.
     */
    protected PVRecord pvRecord;
    /**
     * The timeStamp for pvRecord.
     */
    protected TimeStamp timeStamp = null;
    /**
     * The recordProcess for this record.
     */
    protected RecordProcess recordProcess = null;
    /**
     * The alarmSupport.
     */
    protected AlarmSupport alarmSupport = null;
    /**
     * The record for pvname
     */
    protected PVRecord linkPVRecord = null;
    /**
     * The record process for linkPVRecord.
     */
    protected RecordProcess linkRecordProcess = null;
    
    /**
     * Constructor.
     * @param supportName The support name.
     * @param pvField The field which is supported.
     */
    public AbstractLink(String supportName,PVField pvField) {
        super(supportName,pvField);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.AbstractSupport#initialize(org.epics.ioc.support.RecordSupport)
     */
    public void initialize(RecordSupport recordSupport) {
        if(!super.checkSupportState(SupportState.readyForInitialize,null)) return;
        pvnamePV = (PVString)super.getPVField();
        pvDatabaseLink = pvnamePV.getParent();
        pvRecord = pvnamePV.getPVRecord();
        PVField pvField = pvRecord.getSubField("timeStamp");
        if(pvField!=null && pvField.getField().getType()==Type.structure) {
            timeStamp = TimeStampFactory.getTimeStamp((PVStructure)pvField);
        }
        super.initialize(recordSupport);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.support.AbstractSupport#start()
     */
    public void start() {
        if(!super.checkSupportState(SupportState.readyForStart,null)) return;
        recordProcess = supportDatabase.getRecordSupport(pvRecord).getRecordProcess();
        alarmSupport = AlarmSupportFactory.findAlarmSupport(pvDatabaseLink,recordSupport);
        if(alarmSupport==null) {
            super.message("no alarmSupport", MessageType.error);
            return;
        }
        String name = pvnamePV.get();
        int ind = name.indexOf(".");
        if(ind>=0) name = name.substring(0,ind);
        linkPVRecord = pvDatabase.findRecord(name);
        if(linkPVRecord==null) {
            super.message("pvname not found", MessageType.error);
            return;
        }
        linkRecordProcess = supportDatabase.getRecordSupport(linkPVRecord).getRecordProcess();
        super.start();
    }
}
