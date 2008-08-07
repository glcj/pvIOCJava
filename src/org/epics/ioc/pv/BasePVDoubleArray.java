/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv;

import org.epics.ioc.util.MessageType;

/**
 * Base class for implementing PVDoubleArray.
 * @author mrk
 *
 */
public class BasePVDoubleArray  extends AbstractPVArray implements PVDoubleArray
{
    protected double[] value;
    protected DoubleArrayData doubleArrayData = new DoubleArrayData();
    
    /**
     * Constructor.
     * @param parent The parent.
     * @param array The Introspection interface.
     * @param capacity The initial capacity.
     * @param capacityMutable Can the capacity be changed?
     * @param defaultValue The default value.
     */
    public BasePVDoubleArray(PVField parent,Array array,
        int capacity,boolean capacityMutable,String defaultValue)
    {
        super(parent,array,capacity,capacityMutable);
        value = new double[capacity];
        if(defaultValue!=null && defaultValue.length()>0) {
            String[] values = commaSpacePattern.split(defaultValue);
            try {
                convert.fromStringArray(this,0,values.length,values,0);
            } catch (NumberFormatException e) {
            }
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.pv.PVDoubleArray#share(double[], int)
     */
    public boolean share(double[] value, int length) {
        if(!super.isSharable()) return false;
        if(value==null) return false;
        super.asynAccessCallListener(true);
        try {
            this.value = value;
            super.capacity = value.length;
            super.length = length;
            return true;
        } finally {
            super.asynAccessCallListener(false);
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.pv.AbstractPVField#toString(int)
     */
    public String toString(int indentLevel) {
        return convert.getString(this, indentLevel)
        + super.toString(indentLevel);
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.db.AbstractPVArray#setCapacity(int)
     */
    public void setCapacity(int len) {
        if(!capacityMutable) {
            super.message("not capacityMutable", MessageType.error);
            return;
        }
        super.asynAccessCallListener(true);
        try {
            if(length>len) length = len;
            double[]newarray = new double[len];
            if(length>0) System.arraycopy(value,0,newarray,0,length);
            value = newarray;
            capacity = len;
        } finally {
            super.asynAccessCallListener(false);
        }
    }       
    /* (non-Javadoc)
     * @see org.epics.ioc.pv.PVDoubleArray#get(int, int, org.epics.ioc.pv.DoubleArrayData)
     */
    public int get(int offset, int len, DoubleArrayData data) {
        super.asynAccessCallListener(true);
        try {
            int n = len;
            if(offset+len > length) n = length - offset;
            data.data = value;
            data.offset = offset;
            return n;
        } finally {
            super.asynAccessCallListener(false);
        }
    }
    /* (non-Javadoc)
     * @see org.epics.ioc.pv.PVDoubleArray#put(int, int, double[], int)
     */
    public int put(int offset, int len, double[]from, int fromOffset) {
        if(!super.isMutable()) {
            super.message("not isMutable", MessageType.error);
            return 0;
        }
        super.asynAccessCallListener(true);
        try {
            if(offset+len > length) {
                int newlength = offset + len;
                if(newlength>capacity) {
                    setCapacity(newlength);
                    newlength = capacity;
                    len = newlength - offset;
                    if(len<=0) return 0;
                }
                length = newlength;
            }
            System.arraycopy(from,fromOffset,value,offset,len);
            return len;
        } finally {
            super.asynAccessCallListener(false);
        }            
    }     
}