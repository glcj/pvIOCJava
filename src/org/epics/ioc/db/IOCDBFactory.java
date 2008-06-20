/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.epics.ioc.util.IOCExecutor;
import org.epics.ioc.util.IOCExecutorFactory;
import org.epics.ioc.util.MessageNode;
import org.epics.ioc.util.MessageQueue;
import org.epics.ioc.util.MessageQueueFactory;
import org.epics.ioc.util.MessageType;
import org.epics.ioc.util.Requester;
import org.epics.ioc.util.ScanPriority;

/**
 * Factory for creating an IOCDB.
 * @author mrk
 *
 */
public class IOCDBFactory {
    private static IOCDBInstance master = new IOCDBInstance("master");
    /**
     * Create an IOCDB.
     * @param name The name for the IOCDB.
     * @return The newly created IOCDB or the master IOCDB if master is requested.
     */
    public static IOCDB create(String name) {
        if(name.equals("master")) return master;
        return new IOCDBInstance(name);
    }
    /**
     * Get the master IOC Database.
     * @return The master.
     */
    public static IOCDB getMaster() {
        return master;
    }
    
    private static class IOCDBInstance implements IOCDB,Runnable
    {
        private String name;
        private boolean isMaster = false;
        private LinkedHashMap<String,DBRecord> recordMap = new LinkedHashMap<String,DBRecord>();
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private ReentrantLock messageRequestListLock = new ReentrantLock();
        private ArrayList<Requester> messageRequesterList = new ArrayList<Requester>();
        // following are only used by master
        private static final int messageQueueSize = 300;
        private MessageQueue messageQueue = MessageQueueFactory.create(messageQueueSize);
        private IOCExecutor iocExecutor = null;
        
        private IOCDBInstance(String name) {
            this.name = name;
            if(name.equals("master")) {
                isMaster = true;
                iocExecutor = IOCExecutorFactory.create(
                        "IOCDBMessage", ScanPriority.lowest);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#getMaster()
         */
        public IOCDB getMaster() {
            return master;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#getName()
         */
        public String getName() {
            return name;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#mergeIntoMaster()
         */
        public void mergeIntoMaster() {
            if(this==master) return;
            rwLock.writeLock().lock();
            try {
                master.merge(recordMap);
                recordMap.clear();
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        
        // merge allows master to be locked once instead of for each record instance
        private void merge(LinkedHashMap<String,DBRecord> from) {
            Set<String> keys;
            rwLock.writeLock().lock();
            try {
                keys = from.keySet();
                for(String key: keys) {
                    ImplDBRecord dbRecord = (ImplDBRecord)from.get(key);
                    dbRecord.setIOCDB(this);
                    dbRecord.getPVRecord().addRequester(this);
                    recordMap.put(key,dbRecord);
                }
            }  finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#findRecord(java.lang.String)
         */
        public DBRecord findRecord(String recordName) {
            rwLock.readLock().lock();
            try {
                DBRecord record = null;
                record = recordMap.get(recordName);
                if(record==null && this!=master) record = master.findRecord(recordName);
                return record;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#addRecord(org.epics.ioc.db.DBRecord)
         */
        public boolean addRecord(DBRecord record) {
            rwLock.writeLock().lock();
            try {
                String key = record.getPVRecord().getRecordName();
                if(recordMap.containsKey(key)) {
                    message("record already exists",MessageType.warning);
                    return false;
                }
                if(this!=master && master.findRecord(key)!=null) {
                    message("record already exists in master",MessageType.warning);
                    return false;
                }
                recordMap.put(key,record);
                ImplDBRecord impl = (ImplDBRecord)record;
                impl.setIOCDB(this);
            } finally {
                rwLock.writeLock().unlock();
            }
            if(isMaster) {
                record.lock();
                try {
                    record.getPVRecord().addRequester(this);
                } finally {
                    record.unlock();
                }
            }
            return true;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#removeRecord(org.epics.ioc.db.DBRecord)
         */
        public boolean removeRecord(DBRecord record) {
            if(isMaster) {
                record.lock();
                try {
                    record.getPVRecord().removeRequester(this);
                } finally {
                    record.unlock();
                }
            }
            rwLock.writeLock().lock();
            try {
                String key = record.getPVRecord().getRecordName();
                if(recordMap.remove(key)!=null) return true;
                return false;
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#getRecordMap()
         */
        public DBRecord[] getDBRecords() {
            rwLock.readLock().lock();
            try {
                DBRecord[] array = new DBRecord[recordMap.size()];
                recordMap.values().toArray(array);
                return array;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.util.Requester#getRequesterName()
         */
        public String getRequesterName() {
            return name;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#message(java.lang.String, org.epics.ioc.util.MessageType)
         */
        public void message(String message, MessageType messageType) {
            if(!isMaster) {
                messageRequestListLock.lock();
                try {
                    if(messageRequesterList.size()<=0) {
                        PrintStream printStream;
                        if(messageType==MessageType.info) {
                            printStream = System.out;
                        } else {
                            printStream = System.err;
                        }
                        printStream.println(messageType.toString() + " " + message);
                       
                    } else {
                        Iterator<Requester> iter = messageRequesterList.iterator();
                        while(iter.hasNext()) {
                            Requester requester = iter.next();
                            requester.message(message, messageType);
                        }
                    }
                } finally {
                    messageRequestListLock.unlock();
                }
                return;
            }
            boolean execute = false;
            messageQueue.lock();
            try {
                if(messageQueue.isEmpty()) execute = true;
                if(messageQueue.isFull()) {
                    messageQueue.replaceLast(message, messageType);
                } else {
                    messageQueue.put(message, messageType);
                }
            } finally {
                messageQueue.unlock();
            }
            if(execute) {
                iocExecutor.execute(this);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#addRequester(org.epics.ioc.util.Requester)
         */
        public void addRequester(Requester requester) {
            messageRequestListLock.lock();
            try {
                messageRequesterList.add(requester);
            } finally {
                messageRequestListLock.unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#removeRequester(org.epics.ioc.util.Requester)
         */
        public void removeRequester(Requester requester) {
            messageRequestListLock.lock();
            try {
                messageRequesterList.remove(requester);
            } finally {
                messageRequestListLock.unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#recordList(java.lang.String)
         */
        public String[] recordList(String regularExpression) {
            ArrayList<String> list = new ArrayList<String>();
            if(regularExpression==null) regularExpression = ".*";
            Pattern pattern;
            try {
                pattern = Pattern.compile(regularExpression);
            } catch (PatternSyntaxException e) {
                return new String[0];
            }
            rwLock.readLock().lock();
            try {
                Set<Map.Entry<String,DBRecord>> recordSet = recordMap.entrySet();
                Iterator<Map.Entry<String,DBRecord>> iter = recordSet.iterator();
                while(iter.hasNext()) {
                    Map.Entry<String,DBRecord> entry = iter.next();
                    String name = entry.getKey();
                    if(pattern.matcher(name).matches()) {
                        list.add(name);
                    }
                }
                String[] result = new String[list.size()];
                for(int i=0; i< list.size(); i++) result[i] = list.get(i);
                return result;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.db.IOCDB#recordToString(java.lang.String)
         */
        public String recordToString(String regularExpression) {
            StringBuilder result = new StringBuilder();
            if(regularExpression==null) regularExpression = ".*";
            Pattern pattern;
            try {
                pattern = Pattern.compile(regularExpression);
            } catch (PatternSyntaxException e) {
                return "PatternSyntaxException: " + e;
            }
            rwLock.readLock().lock();
            try {
                Set<Map.Entry<String,DBRecord>> recordSet = recordMap.entrySet();
                Iterator<Map.Entry<String,DBRecord>> iter = recordSet.iterator();
                while(iter.hasNext()) {
                    Map.Entry<String,DBRecord> entry = iter.next();
                    String name = entry.getKey();
                    if(pattern.matcher(name).matches()) {
                        result.append(String.format("%nrecord %s%s",name,entry.getValue().toString()));
                    }
                }
                return result.toString();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() { // handles messages
            while(true) {
                String message = null;
                MessageType messageType = null;
                int numOverrun = 0;
                messageQueue.lock();
                try {
                    MessageNode messageNode = messageQueue.get();
                    numOverrun = messageQueue.getClearOverrun();
                    if(messageNode==null) break;
                    message = messageNode.message;
                    messageType = messageNode.messageType;
                    messageNode.message = null;
                } finally {
                    messageQueue.unlock();
                }
                messageRequestListLock.lock();
                try {
                    
                    if(messageRequesterList.size()<=0) {
                        PrintStream printStream;
                        if(messageType==MessageType.info) {
                            printStream = System.out;
                        } else {
                            printStream = System.err;
                        }
                        if(numOverrun>0) {
                            System.err.println(MessageType.error.toString() + " " + numOverrun + " dropped messages ");
                        }
                        if(message!=null) {
                            printStream.println(messageType.toString() + " " + message);
                        }
                    } else {
                        Iterator<Requester> iter = messageRequesterList.iterator();
                        while(iter.hasNext()) {
                            Requester requester = iter.next();
                            requester.message(message, messageType);
                            if(numOverrun>0) {
                                requester.message(numOverrun + " dropped messages",MessageType.error);
                            }
                        }
                    }
                } finally {
                    messageRequestListLock.unlock();
                }
            }
        }
    }
}
