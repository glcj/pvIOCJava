/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.install;

import java.util.concurrent.atomic.AtomicBoolean;

import org.epics.ioc.database.PVDatabase;
import org.epics.ioc.database.PVDatabaseFactory;
import org.epics.ioc.database.PVRecord;
import org.epics.ioc.database.PVReplaceFactory;
import org.epics.ioc.pvAccess.ChannelServerFactory;
import org.epics.ioc.xml.XMLToPVDatabaseFactory;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVStructure;
import org.epics.pvData.pv.Requester;

/**
 * Factory that implements Install
 * @author mrk
 *
 */
public class InstallFactory {
    /**
     * Get the single instance of Install.
     * @return The instance.
     */
    public static Install get() {
        return InstallImpl.getInstall();
    }
   
    private static class InstallImpl implements Install {
        private static final PVDatabase master = PVDatabaseFactory.getMaster();
        private static InstallImpl singleImplementation = null;
        private static MessageType maxError;
        private static AtomicBoolean isInUse = new AtomicBoolean(false);
        private static synchronized InstallImpl getInstall() {
        	 if (singleImplementation==null) {
                 singleImplementation = new InstallImpl();
                 // Make ChannelServer register itself.
                 ChannelServerFactory.getChannelServer();
             }
             return singleImplementation;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installRecords(org.epics.pvData.pv.PVDatabase, org.epics.pvData.pv.Requester)
         */
        public boolean installRecords(PVDatabase pvDatabase, Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                return records(pvDatabase,requester);
            } finally {
                isInUse.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installRecords(java.lang.String, org.epics.pvData.pv.Requester)
         */
        public boolean installRecords(String xmlFile, Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                return records(xmlFile,requester);
            } finally {
                isInUse.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installRecortd(org.epics.pvData.pv.PVRecord, org.epics.pvData.pv.Requester)
         */
        public boolean installRecord(PVRecord pvRecord, Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                PVDatabase pvDatabaseAdd = PVDatabaseFactory.create("beingInstalled");
                pvDatabaseAdd.addRecord(pvRecord);
                return records(pvDatabaseAdd,requester);
            } finally {
                isInUse.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installStructure(org.epics.pvData.pv.PVStructure, java.lang.String, org.epics.pvData.pv.Requester)
         */
        public boolean installStructure(PVStructure pvStructure,Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                if(master.findStructure(pvStructure.getField().getFieldName())!=null) {
                    requester.message("structure already in master",
                            MessageType.fatalError);
                    return false;
                }
                master.addStructure(pvStructure);
                return true;
            } finally {
                isInUse.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installStructures(org.epics.pvData.pv.PVDatabase, org.epics.pvData.pv.Requester)
         */
        public boolean installStructures(PVDatabase pvDatabase,Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                return structures(pvDatabase,requester);
            } finally {
                isInUse.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.install.Install#installStructures(java.lang.String, org.epics.pvData.pv.Requester)
         */
        public boolean installStructures(String xmlFile, Requester requester) {
            boolean gotIt = isInUse.compareAndSet(false,true);
            if(!gotIt) {
                requester.message("InstallFactory is already active",
                        MessageType.fatalError);
                return false;
            }
            try {
                maxError = MessageType.info;
                return structures(xmlFile,requester);
            } finally {
                isInUse.set(false);
            }
        }

       

        private boolean structures(String file,Requester requester) {
                PVDatabase pvDatabaseAdd = PVDatabaseFactory.create("beingInstalled");
                XMLToPVDatabaseFactory.convert(pvDatabaseAdd,file,requester,false,null,null,null);
                if(maxError!=MessageType.info) {
                    requester.message("installStructures failed because of xml errors.",
                            MessageType.fatalError);
                    return false;
                }
                return structures(pvDatabaseAdd,requester);
        }
        
        private boolean structures(PVDatabase pvDatabase,Requester requester) {
            PVRecord[] pvRecords = pvDatabase.getRecords();
            if(pvRecords.length!=0) {
                requester.message("installStructures failed because new database contained record definitions",
                        MessageType.fatalError);
                return false;
            }
            String[] beingAdded = pvDatabase.getStructureNames();
            String[] master = PVDatabaseFactory.getMaster().getStructureNames();
            for(String add : beingAdded) {
                for(String fromMaster : master) {
                    if(add.equals(fromMaster)) return false;
                }
            }
            pvDatabase.mergeIntoMaster();
            return true;
        }

        private boolean records(String file,Requester requester) {
                PVDatabase pvDatabaseAdd = PVDatabaseFactory.create("beingInstalled");
                XMLToPVDatabaseFactory.convert(pvDatabaseAdd,file,requester);
                if(maxError!=MessageType.info) {
                    requester.message("installRecords failed because of xml errors.",
                            MessageType.fatalError);
                    return false;
                }
                return records(pvDatabaseAdd,requester);
        }
        
        private boolean records(PVDatabase pvDatabaseAdd,Requester requester) {
            PVStructure[] pvStructures = pvDatabaseAdd.getStructures();
            if(pvStructures.length!=0) {
                requester.message("installRecords failed because file contained structure definitions",
                        MessageType.fatalError);
                return false;
            }
            PVReplaceFactory.replace(pvDatabaseAdd);
            SupportCreation supportCreation = SupportCreationFactory.create(pvDatabaseAdd, requester);
            boolean gotSupport = supportCreation.createSupport();
            if(!gotSupport) {
                requester.message("Did not find all support.",MessageType.fatalError);
                return false;
            }
            boolean readyForStart = supportCreation.initializeSupport();
            if(!readyForStart) {
                requester.message("initializeSupport failed",MessageType.fatalError);
                return false;
            }
            AfterStart afterStart = AfterStartFactory.create();
            boolean ready = supportCreation.startSupport(afterStart);
            if(!ready) {
                requester.message("startSupport failed",MessageType.fatalError);
                return false;
            }
            afterStart.callRequesters(false);
            pvDatabaseAdd.mergeIntoMaster();
            afterStart.callRequesters(true);
            afterStart = null;
            supportCreation = null;
            pvDatabaseAdd = null;
            return true;
        }
    }
}
