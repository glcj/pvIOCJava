/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pv.test;

import junit.framework.TestCase;

import org.epics.ioc.pv.Array;
import org.epics.ioc.pv.BooleanArrayData;
import org.epics.ioc.pv.ByteArrayData;
import org.epics.ioc.pv.Convert;
import org.epics.ioc.pv.ConvertFactory;
import org.epics.ioc.pv.Field;
import org.epics.ioc.pv.FieldAttribute;
import org.epics.ioc.pv.FieldCreate;
import org.epics.ioc.pv.FieldFactory;
import org.epics.ioc.pv.PVArray;
import org.epics.ioc.pv.PVArrayArray;
import org.epics.ioc.pv.PVBoolean;
import org.epics.ioc.pv.PVBooleanArray;
import org.epics.ioc.pv.PVByte;
import org.epics.ioc.pv.PVByteArray;
import org.epics.ioc.pv.PVDataCreate;
import org.epics.ioc.pv.PVDataFactory;
import org.epics.ioc.pv.PVDouble;
import org.epics.ioc.pv.PVDoubleArray;
import org.epics.ioc.pv.PVField;
import org.epics.ioc.pv.PVFloat;
import org.epics.ioc.pv.PVFloatArray;
import org.epics.ioc.pv.PVInt;
import org.epics.ioc.pv.PVIntArray;
import org.epics.ioc.pv.PVLong;
import org.epics.ioc.pv.PVLongArray;
import org.epics.ioc.pv.PVRecord;
import org.epics.ioc.pv.PVShort;
import org.epics.ioc.pv.PVShortArray;
import org.epics.ioc.pv.PVString;
import org.epics.ioc.pv.PVStringArray;
import org.epics.ioc.pv.PVStructure;
import org.epics.ioc.pv.PVStructureArray;
import org.epics.ioc.pv.StringArrayData;
import org.epics.ioc.pv.Structure;
import org.epics.ioc.pv.Type;

/**
 * JUnit test for pvAccess.
 * It also provides examples of how to use the pvAccess interfaces.
 * @author mrk
 *
 */
public class DatabaseExampleTest extends TestCase {
    
    private static FieldCreate fieldCreate = FieldFactory.getFieldCreate();
    private static PVDataCreate dataCreate = PVDataFactory.getPVDataCreate();
    /**
     * test boolean.
     */
    public static void testBoolean() {
        DatabaseExample database = new DatabaseExample("test");
        PVBoolean booleanData = (PVBoolean)
            database.createField("boolean",Type.pvBoolean);
        boolean booleanValue = true;
        booleanData.put(booleanValue);
        assertTrue(booleanData.get());
        booleanData.setSupportName("booleanSupport");
        Field field = booleanData.getField();
        assertEquals(field.getFieldName(),"boolean");
        assertEquals(field.getType(),Type.pvBoolean);
        System.out.printf("%s%nvalue %b %s%n",
            	field.toString(),
            	booleanData.get(),
                booleanData.toString());
    }
        
    /**
     * test byte.
     */
    public static void testByte() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVByte byteData = (PVByte)
            database.createField("byte",Type.pvByte);
        byte byteValue = -128;
        byteData.put(byteValue);
        assertEquals(byteValue,byteData.get());
        Field field = byteData.getField();
        assertEquals(field.getFieldName(),"byte");
        assertEquals(field.getType(),Type.pvByte);
        System.out.printf("%s%nvalue %d %s%n",
            	field.toString(),
            	byteData.get(),
                byteData.toString());
        assertEquals(byteValue,convert.toByte(byteData));
        short shortValue = byteValue;
        assertEquals(shortValue,convert.toShort(byteData));
        int intValue = byteValue;
        assertEquals(intValue,convert.toInt(byteData));
        long longValue = byteValue;
        assertEquals(longValue,convert.toLong(byteData));
        float floatValue = byteValue;
        assertEquals(floatValue,convert.toFloat(byteData));
        double doubleValue = byteValue;
        assertEquals(doubleValue,convert.toDouble(byteData));
    }

    /**
     * test short.
     */
    public static void testShort() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVShort shortData = (PVShort)
            database.createField("short",Type.pvShort);
        short shortValue = 127;
        shortData.put(shortValue);
        assertEquals(shortValue,shortData.get());
        Field field = shortData.getField();
        assertEquals(field.getFieldName(),"short");
        assertEquals(field.getType(),Type.pvShort);
        System.out.printf("%s%nvalue %d %s%n",
            	field.toString(),
            	shortData.get(),
                shortData.toString());
        byte byteValue = (byte)shortValue;
        assertEquals(byteValue,convert.toByte(shortData));
        assertEquals(shortValue,convert.toShort(shortData));
        int intValue = shortValue;
        assertEquals(intValue,convert.toInt(shortData));
        long longValue = shortValue;
        assertEquals(longValue,convert.toLong(shortData));
        float floatValue = shortValue;
        assertEquals(floatValue,convert.toFloat(shortData));
        double doubleValue = shortValue;
        assertEquals(doubleValue,convert.toDouble(shortData));
    }

    /**
     * test int.
     */
    public static void testInt() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVInt intData = (PVInt)
            database.createField("int",Type.pvInt);
        int intValue = 64;
        intData.put(intValue);
        assertEquals(intValue,intData.get());
        Field field = intData.getField();
        assertEquals(field.getFieldName(),"int");
        assertEquals(field.getType(),Type.pvInt);
        System.out.printf("%s%nvalue %d %s%n",
            	field.toString(),
            	intData.get(),
                intData.toString());
        byte byteValue = (byte)intValue;
        assertEquals(byteValue,convert.toByte(intData));
        short shortValue = (short)intValue;
        assertEquals(shortValue,convert.toShort(intData));
        assertEquals(intValue,convert.toInt(intData));
        long longValue = intValue;
        assertEquals(longValue,convert.toLong(intData));
        float floatValue = intValue;
        assertEquals(floatValue,convert.toFloat(intData));
        double doubleValue = intValue;
        assertEquals(doubleValue,convert.toDouble(intData));
    }

    /**
     * test long.
     */
    public static void testLong() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVLong longData = (PVLong)
            database.createField("long",Type.pvLong);
        long longValue = -64;
        longData.put(longValue);
        assertEquals(longValue,longData.get());
        Field field = longData.getField();
        assertEquals(field.getFieldName(),"long");
        assertEquals(field.getType(),Type.pvLong);
        System.out.printf("%s%nvalue %d %s%n",
            	field.toString(),
            	longData.get(),
                longData.toString());
        byte byteValue = (byte)longValue;
        assertEquals(byteValue,convert.toByte(longData));
        short shortValue = (short)longValue;
        assertEquals(shortValue,convert.toShort(longData));
        int intValue = (int)longValue;
        assertEquals(intValue,convert.toInt(longData));
        assertEquals(longValue,convert.toLong(longData));
        float floatValue = longValue;
        assertEquals(floatValue,convert.toFloat(longData));
        double doubleValue = longValue;
        assertEquals(doubleValue,convert.toDouble(longData));
    }

    /**
     * test float.
     */
    public static void testFloat() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVFloat floatData = (PVFloat)
            database.createField("float",Type.pvFloat);
        float floatValue = 32.0F;
        floatData.put(floatValue);
        assertEquals(floatValue,floatData.get());
        Field field = floatData.getField();
        assertEquals(field.getFieldName(),"float");
        assertEquals(field.getType(),Type.pvFloat);
        System.out.printf("%s%nvalue %f %s%n",
            	field.toString(),
            	floatData.get(),
                floatData.toString());
        byte byteValue = (byte)floatValue;
        assertEquals(byteValue,convert.toByte(floatData));
        short shortValue = (short)floatValue;
        assertEquals(shortValue,convert.toShort(floatData));
        int intValue = (int)floatValue;
        assertEquals(intValue,convert.toInt(floatData));
        long longValue = (long)floatValue;
        assertEquals(longValue,convert.toLong(floatData));
        assertEquals(floatValue,convert.toFloat(floatData));
        double doubleValue = floatValue;
        assertEquals(doubleValue,convert.toDouble(floatData));
    }

    /**
     * test double.
     */
    public static void testDouble() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVDouble doubleData = (PVDouble)
            database.createField("double",Type.pvDouble);
        double doubleValue = 32.0;
        doubleData.put(doubleValue);
        assertEquals(doubleValue,doubleData.get());
        Field field = doubleData.getField();
        assertEquals(field.getFieldName(),"double");
        assertEquals(field.getType(),Type.pvDouble);
        System.out.printf("%s%nvalue %f %s%n",
            	field.toString(),
            	doubleData.get(),
                doubleData.toString());
        byte byteValue = (byte)doubleValue;
        assertEquals(byteValue,convert.toByte(doubleData));
        short shortValue = (short)doubleValue;
        assertEquals(shortValue,convert.toShort(doubleData));
        int intValue = (int)doubleValue;
        assertEquals(intValue,convert.toInt(doubleData));
        long longValue = (long)doubleValue;
        assertEquals(longValue,convert.toLong(doubleData));
        float floatValue = (float)doubleValue;
        assertEquals(floatValue,convert.toFloat(doubleData));
        assertEquals(doubleValue,convert.toDouble(doubleData));
    }

    /**
     * test string.
     */
    public static void testString() {
        DatabaseExample database = new DatabaseExample("test");
        PVString stringData = (PVString)
            database.createField("string",Type.pvString);
        String stringValue = "string";
        stringData.put(stringValue);
        assertEquals(stringValue,stringData.get());
        Field field = stringData.getField();
        assertEquals(field.getFieldName(),"string");
        assertEquals(field.getType(),Type.pvString);
        System.out.printf("%s%nvalue %s %s%n",
            	field.toString(),
            	stringData.get(),
                stringData.toString());
    }

    /**
     * test structure.
     */
    public static void testStructureAndProperty() {
        DatabaseExample database = new DatabaseExample("test");
        // value has property displayLimit
        Field lowField = fieldCreate.createField("low", Type.pvDouble);
        Field highField = fieldCreate.createField("high", Type.pvDouble);
        Field[] fields = new Field[]{lowField,highField};
        PVStructure displayLimit = database.createStructureData(
            "displayLimit","DisplayLimit",fields);
        PVDouble valueData = (PVDouble)database.createField(
            "value",Type.pvDouble);
        // discard data now obtained via valueData
        PVField[] structFieldData = displayLimit.getPVFields();
        // set displayLimits
        assertTrue(structFieldData.length==2);
        double value = 0.0;
        for( PVField pvField : structFieldData) {
            Field field = pvField.getField();
            assertTrue(field.getType()==Type.pvDouble);
            PVDouble doubleData = (PVDouble)pvField;
            doubleData.put(value);
            value += 10.0;
        }

        Field valueField = valueData.getField();
        System.out.printf("%s%nvalue %f %s%n",
             valueField.toString(),
             valueData.get(),
             valueData.toString());
        Structure structure = (Structure)displayLimit.getField();
        System.out.printf("%s%nvalue %s%n",
             structure.toString(),
             displayLimit.toString());
    }

    /**
     * test array of boolean.
     */
    public static void testBooleanArray() {
        DatabaseExample database = new DatabaseExample("test");
        PVBooleanArray pvbooleanArray = (PVBooleanArray)
            database.createArrayData("booleanArray",Type.pvBoolean);
        int len;
        boolean[] arrayValue = new boolean[] {true,false,true};
        int nput = pvbooleanArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvbooleanArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvbooleanArray.getCapacity(),arrayValue.length);
        Field field = pvbooleanArray.getField();
        assertEquals(field.getFieldName(),"booleanArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvBoolean);
        BooleanArrayData data = new BooleanArrayData();
        pvbooleanArray.get(0,arrayValue.length,data);
        System.out.printf("%s%nvalue %s%n",
            	array.toString(),
                pvbooleanArray.toString());
        PVBooleanArray share = (PVBooleanArray)database.createArrayData("shareBooleanArray",Type.pvBoolean);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue, 2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
    }

    /**
     * test array of byte.
     */
    public static void testByteArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVByteArray pvbyteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int len;
        byte[] arrayValue = new byte[] {3,4,5};
        int nput = pvbyteArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvbyteArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvbyteArray.getCapacity(),arrayValue.length);
        Field field = pvbyteArray.getField();
        assertEquals(field.getFieldName(),"byteArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        ByteArrayData data = new ByteArrayData();
        int numback = pvbyteArray.get(0,arrayValue.length,data);
        byte[] readback = data.data;
        assertEquals(data.offset,0);
        assertEquals(numback,readback.length);
        for(int i=0; i < readback.length; i++) {
            assertEquals(arrayValue[i],readback[i]);
        }
        assertEquals(array.getElementType(),Type.pvByte);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvbyteArray.toString());
        PVByteArray share = (PVByteArray)database.createArrayData("shareByteArray",Type.pvByte);
        share.setSharable(true);
        boolean isSharable = share.share(data.data, 2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(pvbyteArray,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(pvbyteArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVShortArray shortArrayData = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(shortArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(shortArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVIntArray intArrayData = (PVIntArray)
            database.createArrayData("intArray",Type.pvInt);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(intArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(intArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVLongArray longArrayData = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(longArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(longArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVFloatArray floatArrayData = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(floatArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(floatArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVDoubleArray doubleArrayData = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromByteArray(doubleArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toByteArray(doubleArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);
    }

    /**
     * test array of short.
     */
    public static void testShortArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVShortArray pvshortArray = (PVShortArray)
        database.createArrayData("shortArray",Type.pvShort);
        int len;
        short[] arrayValue = new short[] {3,4,5};
        int nput = pvshortArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvshortArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvshortArray.getCapacity(),arrayValue.length);
        Field field = pvshortArray.getField();
        assertEquals(field.getFieldName(),"shortArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvShort);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvshortArray.toString());
        PVShortArray share = (PVShortArray)database.createArrayData("shareShortArray",Type.pvShort);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue, 2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        PVByteArray byteArrayData = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(byteArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(byteArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(pvshortArray,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(pvshortArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVIntArray intArrayData = (PVIntArray)
            database.createArrayData("intArray",Type.pvInt);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(intArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(intArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVLongArray longArrayData = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(longArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(longArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVFloatArray floatArrayData = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(floatArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(floatArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVDoubleArray doubleArrayData = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromShortArray(doubleArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toShortArray(doubleArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);
    }

    /**
     * test array of int.
     */
    public static void testIntArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVIntArray pvintArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        int len;
        int[] arrayValue = new int[] {3,4,5};
        int nput = pvintArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvintArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvintArray.getCapacity(),arrayValue.length);
        Field field = pvintArray.getField();
        assertEquals(field.getFieldName(),"intArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvInt);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvintArray.toString());
        PVIntArray share = (PVIntArray)database.createArrayData("shareIntArray",Type.pvInt);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue, 2);
        share.setLength(2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        PVByteArray byteArrayData = (PVByteArray)
        database.createArrayData("byteArray",Type.pvByte);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(byteArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(byteArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVShortArray shortArrayData = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(shortArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(shortArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(pvintArray,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(pvintArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVLongArray longArrayData = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(longArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(longArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVFloatArray floatArrayData = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(floatArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(floatArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVDoubleArray doubleArrayData = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromIntArray(doubleArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toIntArray(doubleArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);
    }

    /**
     * test array of long.
     */
    public static void testLongArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVLongArray pvlongArray = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        int len;
        long[] arrayValue = new long[] {3,4,5};
        int nput = pvlongArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvlongArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvlongArray.getCapacity(),arrayValue.length);
        Field field = pvlongArray.getField();
        assertEquals(field.getFieldName(),"longArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvLong);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvlongArray.toString());
        PVLongArray share = (PVLongArray)database.createArrayData("shareLongArray",Type.pvLong);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue, 2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        PVByteArray byteArrayData = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(byteArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(byteArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVShortArray shortArrayData = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(shortArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(shortArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVIntArray intArrayData = (PVIntArray)
            database.createArrayData("intArray",Type.pvInt);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(intArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(intArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(pvlongArray,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(pvlongArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVFloatArray floatArrayData = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(floatArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(floatArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);

        PVDoubleArray doubleArrayData = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        arrayValue[0] = 0; arrayValue[1] = 1; arrayValue[2] = 2;
        convert.fromLongArray(doubleArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0; arrayValue[1] = 0; arrayValue[2] = 0;
        convert.toLongArray(doubleArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0 && arrayValue[1]==1 && arrayValue[2]==2);
    }

    /**
     * test array of float.
     */
    public static void testFloatArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVFloatArray pvfloatArray = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        int len;
        float[] arrayValue = new float[] {3.0F,4.0F,5.0F};
        int nput = pvfloatArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvfloatArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvfloatArray.getCapacity(),arrayValue.length);
        Field field = pvfloatArray.getField();
        assertEquals(field.getFieldName(),"floatArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvFloat);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvfloatArray.toString());
        PVFloatArray share = (PVFloatArray)database.createArrayData("shareFloatArray",Type.pvFloat);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue, 2);
        share.setLength(2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        PVByteArray byteArrayData = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(byteArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(byteArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);

        PVShortArray shortArrayData = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(shortArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(shortArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);

        PVIntArray intArrayData = (PVIntArray)
            database.createArrayData("intArray",Type.pvInt);
        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(intArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(intArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);

        PVLongArray longArrayData = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(longArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(longArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);

        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(pvfloatArray,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(pvfloatArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);

        PVDoubleArray doubleArrayData = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        arrayValue[0] = 0.0F; arrayValue[1] = 1.0F; arrayValue[2] = 2.0F;
        convert.fromFloatArray(doubleArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0F; arrayValue[1] = 0.0F; arrayValue[2] = 0.0F;
        convert.toFloatArray(doubleArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0F && arrayValue[1]==1.0F && arrayValue[2]==2.0F);
    }

    /**
     * test array of double.
     */
    public static void testDoubleArray() {
        DatabaseExample database = new DatabaseExample("test");
        Convert convert = ConvertFactory.getConvert();
        PVDoubleArray pvdoubleArray = (PVDoubleArray)
            database.createArrayData("doubleArray",Type.pvDouble);
        int len;
        double[] arrayValue = new double[] {3.0,4.0,5.0};
        int nput = pvdoubleArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvdoubleArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvdoubleArray.getCapacity(),arrayValue.length);
        Field field = pvdoubleArray.getField();
        assertEquals(field.getFieldName(),"doubleArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvDouble);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvdoubleArray.toString());
        PVDoubleArray share = (PVDoubleArray)database.createArrayData("shareDoubleArray",Type.pvDouble);
        share.setSharable(true);
        boolean isSharable = share.share(arrayValue,2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
        PVByteArray byteArrayData = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(byteArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(byteArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);

        PVShortArray shortArrayData = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(shortArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(shortArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);

        PVIntArray intArrayData = (PVIntArray)
            database.createArrayData("intArray",Type.pvInt);
        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(intArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(intArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);

        PVLongArray longArrayData = (PVLongArray)
            database.createArrayData("longArray",Type.pvLong);
        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(longArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(longArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);

        PVFloatArray floatArrayData = (PVFloatArray)
            database.createArrayData("floatArray",Type.pvFloat);
        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(floatArrayData,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(floatArrayData,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);

        arrayValue[0] = 0.0; arrayValue[1] = 1.0; arrayValue[2] = 2.0;
        convert.fromDoubleArray(pvdoubleArray,0,len,arrayValue,0);
        arrayValue[0] = 0.0; arrayValue[1] = 0.0; arrayValue[2] = 0.0;
        convert.toDoubleArray(pvdoubleArray,0,len,arrayValue,0);
        assertTrue(arrayValue[0]==0.0 && arrayValue[1]==1.0 && arrayValue[2]==2.0);
    }

    /**
     * test array of string.
     */
    public static void testStringArray() {
        DatabaseExample database = new DatabaseExample("test");
        PVStringArray pvstringArray = (PVStringArray)
            database.createArrayData("stringArray",Type.pvString);
        int len;
        String[] arrayValue = new String[] {"string0","string2","string3"};
        int nput = pvstringArray.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = pvstringArray.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(pvstringArray.getCapacity(),arrayValue.length);
        Field field = pvstringArray.getField();
        assertEquals(field.getFieldName(),"stringArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvString);
        StringArrayData data = new StringArrayData();
        int retLength = pvstringArray.get(0,arrayValue.length,data);
        String[]readback = data.data;
        assertEquals(data.offset,0);
        assertEquals(retLength,readback.length);
        assertEquals(readback[0],arrayValue[0]);
        assertEquals(readback[1],arrayValue[1]);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                pvstringArray.toString());
        PVStringArray share = (PVStringArray)database.createArrayData("shareStringArray",Type.pvString);
        share.setSharable(true);
        boolean isSharable = share.share(data.data, 2);
        System.out.printf("share isSharable %b length %d%nvalue %s%n",
                isSharable,share.getLength(),share.toString());
    }

    /**
     * test array of structure.
     */
    public static void testStructureArray() {
        DatabaseExample database = new DatabaseExample("test");
        PVStructureArray structureArrayData = (PVStructureArray)
            database.createArrayData("structureArray",Type.pvStructure);
        int len;
        PVStructure[] arrayValue = new PVStructure[3];
        for(int i = 0; i < arrayValue.length; i+=2) {
            Field lowField = fieldCreate.createField("low", Type.pvDouble);
            Field highField = fieldCreate.createField("high", Type.pvDouble);
            Field[] fields = new Field[]{lowField,highField};
            arrayValue[i] = database.createStructureData(
                "displayLimit","DisplayLimit",fields);
            PVField[] datas = (PVField[])arrayValue[i].getPVFields();
            ((PVDouble)datas[0]).put(0.0);
            ((PVDouble)datas[1]).put(10.0);
        }
        int nput = structureArrayData.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = structureArrayData.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(structureArrayData.getCapacity(),arrayValue.length);
        Field field = structureArrayData.getField();
        assertEquals(field.getFieldName(),"structureArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvStructure);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                structureArrayData.toString());
    }

    /**
     * test array of array.
     */
    public static void testArrayArray() {
        DatabaseExample database = new DatabaseExample("test");
        PVArrayArray arrayArrayData = (PVArrayArray)
            database.createArrayData("arrayArray",Type.pvArray);
        int len;
        PVArray[] arrayValue = new PVArray[3];
        for(int i = 0; i < arrayValue.length; i+=2) {
            PVDoubleArray doubleArrayData = (PVDoubleArray)
                database.createArrayData("doubleArray",Type.pvDouble);
            double[] value = new double[] {1.0*i,2.0*i,3.0*i};
            doubleArrayData.put(0,arrayValue.length,value,0);
            arrayValue[i] = doubleArrayData;
        }
        int nput = arrayArrayData.put(0,arrayValue.length,arrayValue,0);
        assertEquals(nput,arrayValue.length);
        len = arrayArrayData.getLength();
        assertEquals(len,arrayValue.length);
        assertEquals(arrayArrayData.getCapacity(),arrayValue.length);
        Field field = arrayArrayData.getField();
        assertEquals(field.getFieldName(),"arrayArray");
        assertEquals(field.getType(),Type.pvArray);
        Array array = (Array)field;
        assertEquals(array.getElementType(),Type.pvArray);
        System.out.printf("%s%nvalue %s%n",
                array.toString(),
                arrayArrayData.toString());
    }
    
    /**
     * Test structure copy.
     */
    public static void testStructureCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        // value has property displayLimit
        Field lowField = fieldCreate.createField("low", Type.pvDouble);
        Field highField = fieldCreate.createField("high", Type.pvDouble);
        Field[] fields = new Field[]{lowField,highField};
        PVStructure displayLimit = database.createStructureData(
            "displayLimit","DisplayLimit",fields);
        PVField[] datas = displayLimit.getPVFields();
        ((PVDouble)datas[0]).put(-10.0);
        ((PVDouble)datas[1]).put(10.0);
        
        PVStructure displayLimit1 = database.createStructureData(
                "displayLimit1","DisplayLimit1",fields);
        convert.copyStructure(displayLimit,displayLimit1);
        System.out.printf("%ntestStructureCopy %s%s%n",
                displayLimit.toString(),displayLimit1.toString());
    }
    
    /**
     * Test BooleanArray copy.
     */
    public static void testBooleanArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVBooleanArray sourceArray = (PVBooleanArray)
            database.createArrayData("sourceArray",Type.pvBoolean);
        boolean[] arrayValue = new boolean[] {true,false,true};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestBooleanArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVBooleanArray booleanArray = (PVBooleanArray)
            database.createArrayData("booleanArray",Type.pvBoolean);
        int ncopy = convert.copyArray(sourceArray,0,booleanArray,0,nput);
        System.out.printf("booleanArray ncopy %d %s%n",ncopy,booleanArray.toString());
    }
    
    /**
     * Test ByteArray copy.
     */
    public static void testByteArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVByteArray sourceArray = (PVByteArray)
            database.createArrayData("sourceArray",Type.pvByte);
        byte[] arrayValue = new byte[] {-127,0,127};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestByteArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test ShortArray copy.
     */
    public static void testShortArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVShortArray sourceArray = (PVShortArray)
            database.createArrayData("sourceArray",Type.pvShort);
        short[] arrayValue = new short[] {-32767,0,32767};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestShortArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test IntArray Copy.
     */
    public static void testIntArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVIntArray sourceArray = (PVIntArray)
            database.createArrayData("sourceArray",Type.pvInt);
        int[] arrayValue = new int[] {-100000,0,100000};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestIntArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test LongArray copy.
     */
    public static void testLongArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVLongArray sourceArray = (PVLongArray)
            database.createArrayData("sourceArray",Type.pvLong);
        long[] arrayValue = new long[] {-100,0,100};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestLongArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test FloatArray copy.
     */
    public static void testFloatArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVFloatArray sourceArray = (PVFloatArray)
            database.createArrayData("sourceArray",Type.pvFloat);
        float[] arrayValue = new float[] {-127,0,127};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestFloatArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test DoubleArray copy.
     */
    public static void testDoubleArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVDoubleArray sourceArray = (PVDoubleArray)
            database.createArrayData("sourceArray",Type.pvDouble);
        double[] arrayValue = new double[] {-127,0,127};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestDoubleArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());        
    }
    
    /**
     * Test StringArray copy.
     */
    public static void testStringArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVStringArray sourceArray = (PVStringArray)
            database.createArrayData("sourceArray",Type.pvString);
        String[] arrayValue = new String[] {"-127","0","127"};
        int nput = sourceArray.put(0,arrayValue.length,arrayValue,0);
        System.out.printf("%ntestStringArrayCopy nput %d sourceArray %s%n",
            nput,sourceArray.toString());
        
        PVStringArray stringArray = (PVStringArray)
        database.createArrayData("stringArray",Type.pvString);
        
        PVByteArray byteArray = (PVByteArray)
            database.createArrayData("byteArray",Type.pvByte);
        int ncopy = convert.copyArray(sourceArray,0,byteArray,0,nput);
        System.out.printf("byteArray ncopy %d %s%n",ncopy,byteArray.toString());
        ncopy = convert.copyArray(byteArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());
    
        PVShortArray shortArray = (PVShortArray)
            database.createArrayData("shortArray",Type.pvShort);
        ncopy = convert.copyArray(sourceArray,0,shortArray,0,nput);
        System.out.printf("shortArray ncopy %d %s%n",ncopy, shortArray.toString());
        ncopy = convert.copyArray(shortArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());

        PVIntArray intArray = (PVIntArray)
        database.createArrayData("intArray",Type.pvInt);
        ncopy = convert.copyArray(sourceArray,0,intArray,0,nput);
        System.out.printf("intArray ncopy %d %s%n",ncopy,intArray.toString()); 
        ncopy = convert.copyArray(intArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());
        
        PVLongArray longArray = (PVLongArray)
        database.createArrayData("longArray",Type.pvLong);
        ncopy = convert.copyArray(sourceArray,0,longArray,0,nput);
        System.out.printf("longArray ncopy %d %s%n",ncopy,longArray.toString());
        ncopy = convert.copyArray(longArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());
        
        PVFloatArray floatArray = (PVFloatArray)
        database.createArrayData("floatArray",Type.pvFloat);
        ncopy = convert.copyArray(sourceArray,0,floatArray,0,nput);
        System.out.printf("floatArray ncopy %d %s%n",ncopy,floatArray.toString());
        ncopy = convert.copyArray(floatArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());
        
        PVDoubleArray doubleArray = (PVDoubleArray)
        database.createArrayData("doubleArray",Type.pvDouble);
        ncopy = convert.copyArray(sourceArray,0,doubleArray,0,nput);
        System.out.printf("doubleArray ncopy %d %s%n",ncopy,doubleArray.toString());  
        ncopy = convert.copyArray(doubleArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d %s%n",ncopy,stringArray.toString());
    }
    /**
     * Test StructureArray copy.
     */
    public static void testStructureArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVStructureArray structureArray = (PVStructureArray)
            database.createArrayData("structureArray",Type.pvStructure);
        PVStructure[] arrayValue = new PVStructure[3];
        for(int i = 0; i < arrayValue.length; i+=2) {
            Field lowField = fieldCreate.createField("low", Type.pvDouble);
            Field highField = fieldCreate.createField("high", Type.pvDouble);
            Field[] fields = new Field[]{lowField,highField};
            PVStructure displayLimit = database.createStructureData(
                "displayLimit","DisplayLimit",fields);
            PVField[] datas = displayLimit.getPVFields();
            ((PVDouble)datas[0]).put(-10.0);
            ((PVDouble)datas[1]).put(10.0);
            arrayValue[i] = displayLimit;
        }
        int nput = structureArray.put(0,arrayValue.length,arrayValue,0);
        PVStructureArray structureArray1 = (PVStructureArray)
            database.createArrayData("structureArray1",Type.pvStructure);
        int ncopy = convert.copyArray(structureArray,0,structureArray1,0,nput);
        System.out.printf("%ntestStructureArrayCopy ncopy %d %s%s%n",
            ncopy,structureArray.toString(),structureArray1.toString());
        PVStringArray stringArray = (PVStringArray) database.createArrayData(
             "stringArray",Type.pvString);
        ncopy = convert.copyArray(structureArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d%n%s%n",ncopy,stringArray.toString());
        
    }
    /**
     * Test ArrayArray copy.
     */
    public static void testArrayArrayCopy() {
        Convert convert = ConvertFactory.getConvert();
        DatabaseExample database = new DatabaseExample("test");
        PVArrayArray arrayArray = (PVArrayArray)
            database.createArrayData("arrayArray",Type.pvArray);
        PVArray[] arrayValue = new PVArray[3];
        for(int i = 0; i < arrayValue.length; i+=2) {
            PVDoubleArray doubleArrayData = (PVDoubleArray)
                database.createArrayData("doubleArray",Type.pvDouble);
            double[] value = new double[] {1.0*(i+1),2.0*(i+1),3.0*(i+1)};
            doubleArrayData.put(0,arrayValue.length,value,0);
            arrayValue[i] = doubleArrayData;
        }
        int nput = arrayArray.put(0,arrayValue.length,arrayValue,0);
        PVArrayArray arrayArray1 = (PVArrayArray)
        database.createArrayData("arrayArray1",Type.pvArray);
        int ncopy = convert.copyArray(arrayArray,0,arrayArray1,0,nput);
        System.out.printf("%ntestArrayArrayCopy ncopy %d %s%s%n",
                ncopy,arrayArray.toString(),arrayArray1.toString());
        PVStringArray stringArray = (PVStringArray) database.createArrayData(
             "stringArray",Type.pvString);
        ncopy = convert.copyArray(arrayArray,0,stringArray,0,nput);
        System.out.printf("stringArray ncopy %d%n%s%n",ncopy,stringArray.toString());
        
    }
    
    
    
    
    static private  class DatabaseExample {
        private String name;
        

        public DatabaseExample(String name) {
            this.name = name;
        }
        

        public String getName() {
            return name;
        }

        public PVField createField(String name,Type type) {
            FieldAttribute fieldAttribute = fieldCreate.createFieldAttribute();
            fieldAttribute.setAttribute("key1", "value1");
            fieldAttribute.setAttribute("key2", "value2");
            Field field = fieldCreate.createField(name,type,fieldAttribute);
            Field[] fields = new Field[]{field};
            Structure structure = fieldCreate.createStructure(name, name, fields);
            PVRecord pvRecord = dataCreate.createPVRecord(name, structure);
            return pvRecord.getPVFields()[0];
        }

        public PVStructure createStructureData(String name, String structureName,Field[] field)
        {
            FieldAttribute fieldAttribute = fieldCreate.createFieldAttribute();
            Structure structure = fieldCreate.createStructure(
                name,structureName,field,fieldAttribute);
            return (PVStructure)dataCreate.createPVRecord(name, structure);
        }
        
        public PVArray createArrayData(String name,Type type) {
            FieldAttribute fieldAttribute = fieldCreate.createFieldAttribute();
            Field field = fieldCreate.createArray(name, type,fieldAttribute);
            Field[] fields = new Field[]{field};
            Structure structure = fieldCreate.createStructure(name, name, fields);
            PVRecord pvRecord = dataCreate.createPVRecord(name, structure);
            return (PVArray)pvRecord.getPVFields()[0];
        }
    }
}

