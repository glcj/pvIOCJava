/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.dbDefinition;

import java.util.*;
import java.util.regex.*;
import java.util.concurrent.locks.*;


/**
 * DBDFactory creates a Database Definition Database (DBD) and gets the master DBD.
 * A DBD contains the description of Database Definitions:
 *  menu, structure, recordType, and support.
 * 
 * @author mrk
 * 
 */
public class DBDFactory {
    private static DBDInstance masterDBD = new DBDInstance("master");  
    /**
     * Creates and returns a DBD.
     * @param name The name for the new DBD.
     * @return The new DBD.
     */
    public static DBD create(String name) {
        if(name.equals("master")) return masterDBD;
        return new DBDInstance(name);
    }
    /**
     * Get the master DBD.
     * @return The master DBD.
     */
    public static DBD getMasterDBD() {
        return masterDBD;
    }
    

    private static class DBDInstance implements DBD {
        private String name;
        private TreeMap<String,DBDMenu> menuMap = new TreeMap<String,DBDMenu>();
        private TreeMap<String,DBDStructure> structureMap = new TreeMap<String,DBDStructure>();
        private TreeMap<String,DBDRecordType> recordTypeMap = new TreeMap<String,DBDRecordType>();
        private TreeMap<String,DBDSupport> supportMap = new TreeMap<String,DBDSupport>();
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        
        private DBDInstance(String name) {
            this.name = name;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getName()
         */
        public String getName() {
            return name;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getMasterDBD()
         */
        public DBD getMasterDBD() {
            return masterDBD;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#mergeIntoMaster()
         */
        public void mergeIntoMaster() {
            if(getMasterDBD()==this) return;
            rwLock.writeLock().lock();
            try {
                masterDBD.merge(menuMap,structureMap,recordTypeMap,supportMap);
                menuMap.clear();
                structureMap.clear();
                recordTypeMap.clear();
                supportMap.clear();
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        // merge allows master to be locked once
        private void merge(TreeMap<String,DBDMenu> menu,
                TreeMap<String,DBDStructure> structure,
                TreeMap<String,DBDRecordType> recordType,
                TreeMap<String,DBDSupport> support)
        {
            Set<String> keys;
            rwLock.writeLock().lock();
            try {
                keys = menu.keySet();
                for(String key: keys) {
                    menuMap.put(key,menu.get(key));
                }
                keys = structure.keySet();
                for(String key: keys) {
                    structureMap.put(key,structure.get(key));
                }
                keys = recordType.keySet();
                for(String key: keys) {
                    recordTypeMap.put(key,recordType.get(key));
                }
                keys = support.keySet();
                for(String key: keys) {
                    supportMap.put(key,support.get(key));
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#addMenu(org.epics.ioc.dbDefinition.DBDMenu)
         */
        public boolean addMenu(DBDMenu menu) {
            rwLock.writeLock().lock();
            try {
                String key = menu.getName();
                if(menuMap.containsKey(key)) return false;
                if(this!=masterDBD && masterDBD.getMenu(key)!=null) return false;
                menuMap.put(key,menu);
                return true;
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getMenu(java.lang.String)
         */
        public DBDMenu getMenu(String menuName) {
            rwLock.readLock().lock();
            try {
                DBDMenu dbdMenu = null;
                dbdMenu = menuMap.get(menuName);
                if(dbdMenu==null && this!=masterDBD) dbdMenu = masterDBD.getMenu(menuName);
                return dbdMenu;
            } finally {
                rwLock.readLock().unlock();
            }
         }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getMenuMap()
         */
        public Map<String, DBDMenu> getMenuMap() {
            rwLock.readLock().lock();
            try {
                return (Map<String, DBDMenu>)menuMap.clone();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#addStructure(org.epics.ioc.dbDefinition.DBDStructure)
         */
        public boolean addStructure(DBDStructure structure) {
            rwLock.writeLock().lock();
            try {
                String key = structure.getName();
                if(structureMap.containsKey(key)) return false;
                if(this!=masterDBD && masterDBD.getStructure(key)!=null) return false;
                structureMap.put(key,structure);
                return true;
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getStructure(java.lang.String)
         */
        public DBDStructure getStructure(String structureName) {
            rwLock.readLock().lock();
            try {
                DBDStructure dbdStructure = null;
                dbdStructure = structureMap.get(structureName);
                if(dbdStructure==null && this!=masterDBD) dbdStructure = masterDBD.getStructure(structureName);
                return dbdStructure;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getStructureMap()
         */
        public Map<String,DBDStructure> getStructureMap() {
            rwLock.readLock().lock();
            try {
                return (Map<String, DBDStructure>)structureMap.clone();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#addRecordType(org.epics.ioc.dbDefinition.DBDRecordType)
         */
        public boolean addRecordType(DBDRecordType recordType) {
            rwLock.writeLock().lock();
            try {
                String key = recordType.getName();
                if(recordTypeMap.containsKey(key)) return false;
                if(this!=masterDBD && masterDBD.getRecordType(key)!=null) return false;
                recordTypeMap.put(key,recordType);
                return true;
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getRecordType(java.lang.String)
         */
        public DBDRecordType getRecordType(String recordTypeName) {
            rwLock.readLock().lock();
            try {
                DBDRecordType dbdRecordType = null;
                dbdRecordType = recordTypeMap.get(recordTypeName);
                if(dbdRecordType==null && this!=masterDBD) dbdRecordType = masterDBD.getRecordType(recordTypeName);
                return dbdRecordType;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getRecordTypeMap()
         */
        public Map<String,DBDRecordType> getRecordTypeMap() {
            rwLock.readLock().lock();
            try {
                return (Map<String, DBDRecordType>)recordTypeMap.clone();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getSupport(java.lang.String)
         */
        public DBDSupport getSupport(String supportName) {
            rwLock.readLock().lock();
            try {
                DBDSupport dbdSupport = null;
                dbdSupport = supportMap.get(supportName);
                if(dbdSupport==null && this!=masterDBD) dbdSupport = masterDBD.getSupport(supportName);
                return dbdSupport;
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#addSupport(org.epics.ioc.dbDefinition.DBDSupport)
         */
        public boolean addSupport(DBDSupport support) {
            rwLock.writeLock().lock();
            try {
                String key = support.getSupportName();
                if(supportMap.containsKey(key)) return false;
                if(this!=masterDBD && masterDBD.getSupport(key)!=null) return false;
                supportMap.put(key,support);
                return true;
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#getSupportMap()
         */
        public Map<String,DBDSupport> getSupportMap() {
            rwLock.readLock().lock();
            try {
                return (Map<String, DBDSupport>)supportMap.clone();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#menuList(java.lang.String)
         */
        public String[] menuList(String regularExpression) {
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
                Set<String> keys = menuMap.keySet();
                for(String key: keys) {
                    DBDMenu dbdMenu = menuMap.get(key);
                    String name = dbdMenu.getName();
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
         * @see org.epics.ioc.dbDefinition.DBD#menuToString(java.lang.String)
         */
        public String menuToString(String regularExpression) {
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
                Set<String> keys = menuMap.keySet();
                for(String key: keys) {
                    DBDMenu dbdMenu = menuMap.get(key);
                    String name = dbdMenu.getName();
                    if(pattern.matcher(name).matches()) {
                        result.append(" " + dbdMenu.toString());
                    }
                }
                return result.toString();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#structureList(java.lang.String)
         */
        public String[] structureList(String regularExpression) {
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
                Set<String> keys = structureMap.keySet();
                for(String key: keys) {
                    DBDStructure dbdStructure = structureMap.get(key);
                    String name = dbdStructure.getName();
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
         * @see org.epics.ioc.dbDefinition.DBD#structureToString(java.lang.String)
         */
        public String structureToString(String regularExpression) {
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
                Set<String> keys = structureMap.keySet();
                for(String key: keys) {
                    DBDStructure dbdStructure = structureMap.get(key);
                    String name = dbdStructure.getName();
                    if(pattern.matcher(name).matches()) {
                        result.append(" " + dbdStructure.toString());
                    }
                }
                return result.toString();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#recordTypeList(java.lang.String)
         */
        public String[] recordTypeList(String regularExpression) {
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
                Set<String> keys = recordTypeMap.keySet();
                for(String key: keys) {
                    DBDRecordType dbdRecordType = recordTypeMap.get(key);
                    String name = dbdRecordType.getName();
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
         * @see org.epics.ioc.dbDefinition.DBD#recordTypeToString(java.lang.String)
         */
        public String recordTypeToString(String regularExpression) {
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
                Set<String> keys = recordTypeMap.keySet();
                for(String key: keys) {
                    DBDRecordType dbdRecordType = recordTypeMap.get(key);
                    String name = dbdRecordType.getName();
                    if(pattern.matcher(name).matches()) {
                        result.append(" " + dbdRecordType.toString());
                    }
                }
                return result.toString();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBD#supportList(java.lang.String)
         */
        public String[] supportList(String regularExpression) {
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
                Set<String> keys = supportMap.keySet();
                for(String key: keys) {
                    DBDSupport dbdSupport = supportMap.get(key);
                    String name = dbdSupport.getSupportName();
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
         * @see org.epics.ioc.dbDefinition.DBD#supportToString(java.lang.String)
         */
        public String supportToString(String regularExpression) {
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
                Set<String> keys = supportMap.keySet();
                for(String key: keys) {
                    DBDSupport dbdSupport = supportMap.get(key);
                    String name = dbdSupport.getSupportName();
                    if(pattern.matcher(name).matches()) {
                        result.append(" " + dbdSupport.toString());
                    }
                }
                return result.toString();
            } finally {
                rwLock.readLock().unlock();
            }
        }
        
    }

}
