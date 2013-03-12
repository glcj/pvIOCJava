/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.pvioc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Requester;
import org.epics.pvioc.database.PVDatabase;
import org.epics.pvioc.database.PVDatabaseFactory;
import org.epics.pvioc.database.PVRecord;
import org.epics.pvioc.install.Install;
import org.epics.pvioc.install.InstallFactory;
import org.epics.pvioc.swtshell.SwtshellFactory;
/**
 * The main program to start a JavaIOC.
 * The program is started with a command line of
 * java org.epics.pvioc.JavaIOC 
 * The command line options are:
 * <pre>
 *     -structures list
 *             list is a list of xml files containing structure definitions. Each is parsed and put into the master database
 *     -records list
 *             list is a list of xml files containing records definitions. Each is parsed and started and put into the master database 
 *     -dumpStructures
 *             Dump all structures in the master database
 *     -dumpRecords
 *             Dump all record instances in the master database
 *     -server serverFile
 *             JavaIOC the server specified in the serverFile
 *     -run
 *             Starts the JavaIOC as a standalone process.
 *     -swtshell
 *             Starts the JavaIOC running under swtshell. THIS WILL NOT BE SUPPORTED IN THE FUTURE
 *            
 * @author mrk
 *
 */
public class JavaIOC {
    private enum State {
        structures,
        records,
        servers
    }

    private static final PVDatabase masterPVDatabase = PVDatabaseFactory.getMaster();
    private static final Install install = InstallFactory.get();
    /**
     * read and dump a database instance file.
     * @param  args is a sequence of flags and filenames.
     */
    public static void main(String[] args) {
        if(args.length==1 && args[0].equals("?")) {
            usage();
            return;
        }
        boolean runSWTShell = false;
        boolean runForever = false;
        Requester iocRequester = new Listener();
        int nextArg = 0;
        State state = null;
        while(nextArg<args.length) {
            String arg = args[nextArg++];
            if(arg.charAt(0) == '-') {
                if(arg.length()>1) {
                    arg = arg.substring(1);
                } else {
                    if(nextArg>=args.length) {
                        System.out.printf("last arg is - illegal\n");
                        return;
                    }
                    arg = args[nextArg++];
                }
                if(arg.equals("dumpStructures")) {
                    dumpStructures();
                } else if(arg.equals("dumpRecords")) {
                    dumpRecords();
                } else if(arg.equals("structures")){
                    state = State.structures;
                } else if(arg.equals("records")){
                    state = State.records;
                } else if(arg.equals("swtshell")) {
                	runSWTShell = true;
                } else if(arg.equals("run")) {
                	runForever = true;
                } else if(arg.equals("server")) {
                    state = State.servers;
                } else {
                    System.err.println("unknown arg: " + arg);
                    usage();
                    return;
                }
            } else if(state==State.structures){
                parseStructures(arg,iocRequester);
            } else if(state==State.records){
                parseRecords(arg,iocRequester);
            } else if(state==State.servers) {
                startServer(arg);
            } else {
                System.err.println("unknown arg: " + arg);
                usage();
                return;
            }
            if (runSWTShell) {
                SwtshellFactory.swtshell();
                return;
            }
            if (runForever) {
                while(true) {
                	try{
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                    	return;
                    }
                	
                }
            }
        }
    }
    
    static void usage() {
        System.out.println("Usage:"
                + " -structures fileList"
                + " -records fileList"
                + " -dumpStructures"
                + " -dumpRecords"
                + " -server file"
                + " -swtshell ");
    }
    
    static void printError(String message) {
        System.err.println(message);
    }
    
    static void startServer(String fileName) {
        System.out.println("starting servers fileName " + fileName);
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String factoryName = null;
            while((factoryName = in.readLine()) !=null) {
                System.out.println("starting server factoryName " + factoryName);
                Class<?> startClass;
                Method method = null;
                try {
                    startClass = Class.forName(factoryName);
                }catch (ClassNotFoundException e) {
                    printError("server factory "
                            + e.getLocalizedMessage()
                            + " class not found");
                    continue;
                }
                try {
                    method = startClass.getDeclaredMethod("start", (Class[])null);
                } catch (NoSuchMethodException e) {
                    printError("server factory "
                            + e.getLocalizedMessage()
                            + " method start not found");
                    continue;
                }
                if(!Modifier.isStatic(method.getModifiers())) {
                    printError("server factory "
                            + factoryName
                            + " start is not a static method ");
                    continue;
                }
                try {
                    method.invoke(null, new Object[0]);
                } catch(IllegalAccessException e) {
                    printError("server start IllegalAccessException "
                            + e.getLocalizedMessage());
                    continue;
                } catch(IllegalArgumentException e) {
                    printError("server start IllegalArgumentException "
                            + e.getLocalizedMessage());
                    continue;
                } catch(InvocationTargetException e) {
                    printError("server start InvocationTargetException "
                            + e.getLocalizedMessage());
                    continue;
                }
            }
            in.close();
        } catch (IOException e) {
            System.err.println("startServer error " + e.getMessage());
            return;
        }
    }
        
    static void dumpStructures() {
        PVStructure[] pvStructures = masterPVDatabase.getStructures();
        if(pvStructures.length>0) System.out.printf("\n\nstructures");
        for(PVStructure pvStructure : pvStructures) {
            System.out.print(pvStructure.toString());
        }
    }
    
    static void dumpRecords() {
        PVRecord[] pvRecords = masterPVDatabase.getRecords();
        if(pvRecords.length>0) System.out.printf("\n\nrecords");
        for(PVRecord pvRecord : pvRecords) {
            System.out.print(pvRecord.toString());
        }
    }

    static void parseStructures(String fileName,Requester iocRequester) {
    	long startTime = 0;
    	long endTime = 0;
    	startTime = System.nanoTime();
    	try {
    		install.installStructures(fileName,iocRequester);
    	}  catch (IllegalStateException e) {
    	    System.out.printf("parseStructures: %s%n",e.getMessage());
    	    e.printStackTrace();
    	}
    	endTime = System.nanoTime();
    	double diff = (double)(endTime - startTime)/1e9;
    	System.out.printf("\ninstalled structures %s time %f seconds\n",fileName,diff);
    }

    static void parseRecords(String fileName,Requester iocRequester) {
    	long startTime = 0;
    	long endTime = 0;
    	startTime = System.nanoTime();
    	try {
    		install.installRecords(fileName,iocRequester);
    	}  catch (IllegalStateException e) {
    	    System.out.printf("parseRecords: %s%n",e.getMessage());
    	    e.printStackTrace();
    	}
    	endTime = System.nanoTime();
    	double diff = (double)(endTime - startTime)/1e9;
    	System.out.printf("\ninstalled records %s time %f seconds\n",fileName,diff);
    }
     
    private static class Listener implements Requester {
        /* (non-Javadoc)
         * @see org.epics.pvioc.util.Requester#getRequesterName()
         */
        public String getRequesterName() {
            return "javaIOC";
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.util.Requester#message(java.lang.String, org.epics.pvioc.util.MessageType)
         */
        public void message(String message, MessageType messageType) {
            System.out.println(message);
            
        }
    }
}