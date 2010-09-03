/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.support.calc.example;

import org.epics.ioc.database.PVRecordStructure;
import org.epics.ioc.support.Support;
import org.epics.ioc.support.SupportProcessRequester;
import org.epics.ioc.support.calc.AbstractCalculatorSupport;
import org.epics.ioc.support.calc.ArgType;
import org.epics.ioc.util.RequestResult;
import org.epics.pvData.pv.DoubleArrayData;
import org.epics.pvData.pv.PVDoubleArray;
import org.epics.pvData.pv.PVField;
import org.epics.pvData.pv.ScalarType;
import org.epics.pvData.pv.Type;

/**
 * Example of how to extend AbstractCalculatorSupport.
 * This example accepts two 
 * @author mrk
 *
 */
public class ArrayAddCalculatorFactory {
    public static Support create(PVRecordStructure pvRecordStructure) {
        return new ArrayAddCalculator(pvRecordStructure);
    }

    private static String supportName = "arrayAddCalculator";

    private static class ArrayAddCalculator extends AbstractCalculatorSupport
    {
        private ArrayAddCalculator(PVRecordStructure pvRecordStructure) {
            super(supportName,pvRecordStructure);
        }


        private ArgType[] argTypes = new ArgType[] {
            new ArgType("a",Type.scalarArray,ScalarType.pvDouble),
            new ArgType("b",Type.scalarArray,ScalarType.pvDouble)
        };
        private PVDoubleArray aPV = null;
        private DoubleArrayData aData = new DoubleArrayData();
        private double[] a;
        private int aLength;
        private PVDoubleArray bPV = null;
        private DoubleArrayData bData = new DoubleArrayData();
        private double[] b;
        private int bLength;

        private PVDoubleArray valuePV = null;
        private DoubleArrayData valueData = new DoubleArrayData();
        private double[] value;
        private int valueLength;

        protected ArgType[] getArgTypes() { return argTypes;}

        protected Type getValueType() { return Type.scalarArray;}

        protected void setArgPVFields(PVField[] pvArgs) {
            aPV = (PVDoubleArray)pvArgs[0];
            bPV = (PVDoubleArray)pvArgs[1];
        };

        protected void setValuePVField(PVField pvValue) {
            valuePV = (PVDoubleArray)pvValue;
        };

        public void process(SupportProcessRequester supportProcessRequester) {
            valueLength = valuePV.getLength();
            valuePV.get(0,valueLength,valueData);
            value = valueData.data;
            aLength = aPV.getLength();
            aPV.get(0,aLength,aData);
            a = aData.data;
            bLength = bPV.getLength();
            bPV.get(0,bLength,bData);
            b = bData.data;
            compute();
            supportProcessRequester.supportProcessDone(RequestResult.success);
        }

        private void compute() {

           int len = aLength;
           if(len>bLength) len = bLength;
           if(valueLength!=len) {
               valueLength = len;
               valuePV.setLength(valueLength);
               valuePV.get(0,valueLength,valueData);
               value = valueData.data;
           }
           for(int i=0; i<valueLength; i++) {
               value[i] = Math.abs(a[i] + b[i]);
           }
           valuePV.postPut();
        }
    }
}
