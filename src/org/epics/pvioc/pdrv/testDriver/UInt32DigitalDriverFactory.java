/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.pvioc.pdrv.testDriver;

import org.epics.pvdata.misc.ThreadPriority;
import org.epics.pvdata.pv.PVDouble;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Structure;
import org.epics.pvioc.pdrv.Device;
import org.epics.pvioc.pdrv.DeviceDriver;
import org.epics.pvioc.pdrv.Factory;
import org.epics.pvioc.pdrv.Port;
import org.epics.pvioc.pdrv.PortDriver;
import org.epics.pvioc.pdrv.Status;
import org.epics.pvioc.pdrv.Trace;
import org.epics.pvioc.pdrv.User;
import org.epics.pvioc.pdrv.interfaces.AbstractUInt32Digital;
/**
 * The factory for uint32DigitalDriver.
 * uint32DigitalDriver is a portDriver for testing the uint32Digital support in org.epics.pvioc.pdrv.support.
 * It requires the uint32DigitalDriver structure, which holds the following configuration parameters:
 * <ul>
 *    <li>numberRegisters<br/>
 *       The number of uint32Digital registers to simulate.
 *     </li>
 *     <li>delay<br/>
 *       If 0.0 then uint32DigitalDriver is synchronous.
 *       If > 0.0 uint32DigitalDriver is asynchronous and delays delay seconds after each read/write request.
 *      </li>
 * </ul>
 * uint32DigitalDriver implements interface uint32Digital by keeping an internal int[] array
 * that simulates digital I/O registers.
 * A write request sets a value and a read request reads the current value.
 * @author mrk
 *
 */
public class UInt32DigitalDriverFactory {
    
    /**
     * Create a new instance of uint32DigitalDriver.
     * @param portName The portName.
     * @param autoConnect Initial value for autoConnect.
     * @param priority The thread priority if asynchronous, i.e. delay > 0.0.
     * @param pvStructure The interface for structure uint32DigitalDriver.
     */
    static public void create(
        String portName,boolean autoConnect,ThreadPriority priority,PVStructure pvStructure)
    {
        PVField[] pvFields = pvStructure.getPVFields();
        Structure structure = (Structure)pvStructure.getField();
        int index = structure.getFieldIndex("numberRegisters");
        if(index<0) {
            throw new IllegalStateException("field numberRegisters not found");
        }
        PVInt pvInt = (PVInt)pvFields[index];
        int numberRegisters = pvInt.get();
        index = structure.getFieldIndex("delay");
        if(index<0) {
            throw new IllegalStateException("field delay not found");
        }
        PVDouble pvDelay = (PVDouble)pvFields[index];
        double delay = pvDelay.get();
        boolean canBlock = ((delay>0.0) ? true : false);
        new UInt32DigitalDriver(portName,autoConnect,priority,numberRegisters,canBlock,delay);
    }
    
    static private class UInt32DigitalDriver implements PortDriver {
        private int[] register;
        private double delay;
        private long milliseconds;
        private Port port;
        
        private UInt32DigitalDriver(String portName,boolean autoConnect,ThreadPriority priority,
            int numberRegisters,boolean canBlock,double delay)
        {
            register = new int[numberRegisters];
            this.delay = delay;
            milliseconds = (long)(delay * 1000.0);
            port = Factory.createPort(portName, this, "uint32DigitalDriver",canBlock, autoConnect,priority);
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.pdrv.PortDriver#report(boolean, int)
         */
        public String report(int details) {
            if(details==0) return null;
            return "delay " + delay;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.pdrv.PortDriver#connect(org.epics.pvioc.pdrv.User)
         */
        public Status connect(User user) {
            port.getTrace().print(Trace.FLOW ,port.getPortName() + " connect");
            if(port.isConnected()) {
                user.setMessage("already connected");
                port.getTrace().print(Trace.ERROR ,port.getPortName() + " already connected");
                return Status.error;
            }
            if(delay>0.0) {
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException ie) {

                }
            }
            port.exceptionConnect();
            return Status.success;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.pdrv.PortDriver#createDevice(org.epics.pvioc.pdrv.User, int)
         */
        public Device createDevice(User user, String deviceName) {
            int addr = Integer.parseInt(deviceName);
            if(addr>=register.length) {
                user.setMessage("illegal deviceName");
                return null;
            }
            UInt32DigitalDevice intDevice = new UInt32DigitalDevice(addr);
            Device device = port.createDevice(intDevice, deviceName);
            intDevice.init(device);
            return device;
        }
        /* (non-Javadoc)
         * @see org.epics.pvioc.pdrv.PortDriver#disconnect(org.epics.pvioc.pdrv.User)
         */
        public Status disconnect(User user) {
            port.getTrace().print(Trace.FLOW ,port.getPortName() + " disconnect");
            if(!port.isConnected()) {
                user.setMessage("not connected");
                port.getTrace().print(Trace.ERROR ,port.getPortName() + " not connected");
                return Status.error;
            }
            port.exceptionDisconnect();
            return Status.success;
        }
        private class UInt32DigitalDevice implements DeviceDriver {   
            private int addr;
            private Device device;
            private Trace trace;
            
            private UInt32DigitalDevice(int addr) {
                this.addr = addr;
            }
            
            private void init(Device device) {
                this.device = device;
                trace = device.getTrace();
                new UInt32DigitalInterface(device);
            }
            /* (non-Javadoc)
             * @see org.epics.pvioc.pdrv.DeviceDriver#report(int)
             */
            public String report(int details) {
                return null;
            }
            /* (non-Javadoc)
             * @see org.epics.pvioc.pdrv.DeviceDriver#connect(org.epics.pvioc.pdrv.User)
             */
            public Status connect(User user) {
                trace.print(Trace.FLOW ,device.getFullName() + " connect");
                if(device.isConnected()) {
                    user.setMessage("already connected");
                    trace.print(Trace.ERROR ,device.getFullName() + " already connected");
                    return Status.error;
                }
                if(delay>0.0) {
                    try {
                        Thread.sleep(milliseconds);
                    } catch (InterruptedException ie) {

                    }
                }
                device.exceptionConnect();
                return Status.success;
            }
            /* (non-Javadoc)
             * @see org.epics.pvioc.pdrv.DeviceDriver#disconnect(org.epics.pvioc.pdrv.User)
             */
            public Status disconnect(User user) {
                trace.print(Trace.FLOW ,device.getFullName() + " disconnect");
                if(!device.isConnected()) {
                    user.setMessage("not connected");
                    trace.print(Trace.ERROR ,device.getFullName() + " not connected");
                    return Status.error;
                }
                device.exceptionDisconnect();
                return Status.success;
            }
            
            private class UInt32DigitalInterface extends  AbstractUInt32Digital{
                private UInt32DigitalInterface(Device device) {
                    super(device);
                }               
                /* (non-Javadoc)
                 * @see org.epics.pvioc.pdrv.interfaces.AbstractUInt32Digital#read(org.epics.pvioc.pdrv.User, int)
                 */
                public Status read(User user, int mask) {
                    if(!device.isConnected()) {
                        trace.print(Trace.ERROR,device.getFullName() + " read but not connected");
                        return Status.error;
                    }
                    double timeout = user.getTimeout();
                    if(timeout>0.0 && delay>timeout) {
                        user.setMessage("timeout");
                        return Status.timeout;
                    }
                    if(delay>0.0) {
                        try {
                        Thread.sleep(milliseconds);
                        } catch (InterruptedException ie) {
                            
                        }
                    }
                    int value = register[addr]&mask;
                    user.setInt(value);
                    if((trace.getMask()&Trace.DRIVER)!=0) {
                        trace.print(Trace.DRIVER,device.getFullName() + " read value = " + value);
                    }
                    return Status.success;
                }
                /* (non-Javadoc)
                 * @see org.epics.pvioc.pdrv.interfaces.AbstractUInt32Digital#write(org.epics.pvioc.pdrv.User, int, int)
                 */
                public Status write(User user, int value, int mask) {
                    if(!device.isConnected()) {
                        trace.print(Trace.ERROR,device.getFullName() + " write but not connected");
                        return Status.error;
                    }
                    double timeout = user.getTimeout();
                    if(timeout>0.0 && delay>timeout) {
                        user.setMessage("timeout");
                        return Status.timeout;
                    }
                    if(delay>0.0) {
                        try {
                        Thread.sleep(milliseconds);
                        } catch (InterruptedException ie) {
                            
                        }
                    }
                    int newValue = register[addr]&~mask;
                    newValue |= value&mask;
                    register[addr] = newValue;
                    if((trace.getMask()&Trace.DRIVER)!=0) {
                        trace.print(Trace.DRIVER,device.getFullName() + " write value = " + register[addr]);
                    }
                    super.interruptOccurred(newValue);
                    return Status.success;
                }
            }
        }
    }
}
