/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.dbd;

import java.util.Map;

import org.epics.ioc.pv.*;

/**
 * Factory that creates an implementation of the various
 * Database Definition interfaces.
 * @author mrk
 *
 */
public final class  DBDFieldFactory {
    /**
     * Get the Type from a map of attributes.
     * @param attributes The map of attributes.
     * @return The Type.
     * If the attributes does not have a key "type" the result will be Type.pvUnknown.
     */
    public static Type getType(Map<String,String> attributes) {
        return getType(attributes.get("type"));
    }
    /**
     * Get the element Type from a map of attributes.
     * @param attributes The map of attributes.
     * @return The Type.
     * If the attributes does not have a key "elementType" the result will be Type.pvUnknown.
     */
    public static Type getElementType(Map<String,String> attributes) {
        return getType(attributes.get("elementType"));
    }
    /**
     * Get the Type from string.
     * @param value A string with the name of the type.
     * @return The Type.
     * If the string is null or is not the name of a Type, null is returned.
     */
    public static Type getType(String value) {
        if(value==null)  return null;
        if(value.equals("boolean")) return Type.pvBoolean;
        if(value.equals("byte")) return Type.pvByte;
        if(value.equals("short")) return Type.pvShort;
        if(value.equals("int")) return Type.pvInt;
        if(value.equals("long")) return Type.pvLong;
        if(value.equals("float")) return Type.pvFloat;
        if(value.equals("double")) return Type.pvDouble;
        if(value.equals("string")) return Type.pvString;
        if(value.equals("enum")) return Type.pvEnum;
        if(value.equals("menu")) return Type.pvMenu;
        if(value.equals("structure")) return Type.pvStructure;
        if(value.equals("array")) return Type.pvArray;
        if(value.equals("link")) return Type.pvLink;
        return null;
    }
    /**
     * Create a DBDMenu.
     * @param menuName The name of the menu.
     * @param choices The menu choices.
     * @return The menu or null if it already existed.
     */
    public static DBDMenu createMenu(String menuName, String[] choices)
    {
        return new MenuInstance(menuName,choices);
    }

    /**
     * Create a DBDStructure.
     * @param name The name of the structure.
     * @param dbdField An array of DBDField for the fields of the structure.
     * @param property An array of properties for the structure.
     * @return The interface for the newly created structure.
     */
    public static DBDStructure createStructure(String name,
        Field[] field,Property[] property,FieldAttribute fieldAttribute)
    {
        return new StructureInstance(name,field,property,fieldAttribute);
    }
    
    /**
     * Create a DBDRecordType.
     * @param name The recordType name.
     * @param dbdField An array of DBDField for the fields of the structure.
     * @param property An array of properties for the structure.
     * @return interface The for the newly created structure.
     */
    public static DBDRecordType createRecordType(String name,
        Field[] field,Property[] property,FieldAttribute fieldAttribute)
    {
        return new RecordTypeInstance(name,field,property,fieldAttribute);
    }

    /**
     * Create a DBDSupport.
     * @param supportName The name of the support.
     * @param configurationStructureName The name of the configuration structure.
     * @param factoryName The name of the factory for creating support instances.
     * @return the DBDSupport or null if it already existed.
     */
    public static DBDSupport createSupport(String supportName,String factoryName)
    {
        return new SupportInstance(supportName,factoryName);
    }
    
    /**
     * Create a DBDLinkSupport.
     * @param supportName The name of the support.
     * @param factoryName The name of the factory for creating support instances.
     * @param configurationStructureName The name of the configuration structure.
     * @return the DBDSupport or null if it already existed.
     */
    public static DBDLinkSupport createLinkSupport(
        String supportName,String factoryName,String configurationStructureName)
    {
        return new LinkSupportInstance(supportName,factoryName,configurationStructureName);
    }
    
    public static Menu createMenuField(String fieldName,Property[]property,FieldAttribute fieldAttribute,String menuName)
    {
        return new MenuBase(fieldName,property,fieldAttribute,menuName);
    }
    public static Array createArrayField(String fieldName,Property[]property,FieldAttribute fieldAttribute,Type elementType)
    {
        return new ArrayBase(fieldName,property,fieldAttribute,elementType);
    }
    public static Structure createStructureField(String fieldName,Property[]property,FieldAttribute fieldAttribute,
            DBDStructure dbdStructure,DBD dbd)
    {
        if(dbdStructure==null) {
            return new StructureBase(fieldName,property,fieldAttribute,null, new Field[0]);
        }
        // Must create a copy of all fields so that parent will be correct.
        Field[] original = dbdStructure.getFields();
        int length = original.length;
        Field[] fields = new Field[length];
        for(int i=0; i<length; i++) {
            Field field = original[i];
            Field copy = null;
            String name = field.getFieldName();
            Property[] prop = field.getPropertys();
            Type type = field.getType();
            FieldAttribute attr = field.getFieldAttribute();
            switch(type) {
            case pvMenu:
                Menu menu = (Menu)field;
                copy = createMenuField(name,prop,attr,menu.getMenuName());
                break;
            case pvArray:
                Array arrayField = (Array)field;
                Type elementType = arrayField.getElementType();
                copy = createArrayField(name,prop,attr,elementType);
                break;
            case pvStructure:
                Structure structure = (Structure)field;
                DBDStructure dbdStruct = dbd.getStructure(structure.getStructureName());
                if(dbdStruct==null) {
                    DBD dbdMaster = DBDFactory.getMasterDBD();
                    dbdStruct = dbdMaster.getStructure(structure.getStructureName());
                }
                copy = createStructureField(name,prop,attr,dbdStruct,dbd);
                break;
            default:
                copy = createField(name,prop,attr,type);
            }
            if(copy==null) {
                throw new IllegalStateException("logic error");
            }
            fields[i] = copy;
        }
        return new StructureBase(fieldName,property,fieldAttribute,dbdStructure.getStructureName(),fields);
    }
    
    public static Field createField(String fieldName,
        Property[]property,FieldAttribute fieldAttribute,Type type)
    {
        if(type==Type.pvArray||type==Type.pvMenu||type==Type.pvStructure) {
            throw new IllegalStateException("Illegal type");
        }
        if(type==Type.pvEnum) {
            return new EnumBase(fieldName,property,fieldAttribute,true); 
        } else {
            return new FieldBase(fieldName,type,property,fieldAttribute);
        }
    }
    
    static private class MenuInstance implements DBDMenu
    {

        private String menuName;
        private String[] choices;

        private MenuInstance(String menuName, String[] choices)
        {
            this.menuName = menuName;
            this.choices = choices;
        } 
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBDMenu#getChoices()
         */
        public String[] getChoices() {
            return choices;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBDMenu#getName()
         */
        public String getName() {
            return menuName;
        }        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() { return getString(0);}
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBDMenu#toString(int)
         */
        public String toString(int indentLevel) {
            return getString(indentLevel);
        }

        private String getString(int indentLevel) {
            StringBuilder builder = new StringBuilder();
            FieldBase.newLine(builder,indentLevel);
            builder.append(String.format("menu %s { ",menuName));
            for(String value: choices) {
                builder.append(String.format("\"%s\" ",value));
            }
            builder.append("}");
            return builder.toString();
        }
    }
    
    static private class StructureInstance extends StructureBase implements DBDStructure
    {   
        private StructureInstance(String name,
            Field[] field,Property[] property,FieldAttribute fieldAttribute)
        {
            super(name,property,fieldAttribute,name,field);
        }
    }

    static private class RecordTypeInstance extends StructureInstance implements DBDRecordType
    {   
        private RecordTypeInstance(String name,
            Field[] field,Property[] property,FieldAttribute fieldAttribute)
        {
            super(name,field,property,fieldAttribute);
        }
    }
    
    static private class SupportInstance implements DBDSupport
    {
        private String supportName;
        private String factoryName;

        private SupportInstance(String supportName, String factoryName)
        {
            this.supportName = supportName;
            this.factoryName = factoryName;
        }
        
        /* (non-Javadoc)
         * @see org.epics.ioc.dbd.DBDSupport#getSupportName()
         */
        public String getSupportName() {
            return supportName;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbDefinition.DBDSupport#getFactoryName()
         */
        public String getFactoryName() {
            return factoryName;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() { return getString(0);}
        
        /* (non-Javadoc)
         * @see org.epics.ioc.dbd.DBDSupport#toString(int)
         */
        public String toString(int indentLevel) {
            return getString(indentLevel);
        }
        
        private String getString(int indentLevel) {
            StringBuilder builder = new StringBuilder();
            FieldBase.newLine(builder,indentLevel);
            builder.append(String.format(
                    "supportName %s factoryName %s",
                    supportName,factoryName));
            return builder.toString();
        }
    }
    
    static private class LinkSupportInstance extends SupportInstance implements DBDLinkSupport {
        private String configurationStructureName;
        
        private LinkSupportInstance(String supportName, String factoryName,
                String configurationStructureName)
        {
            super(supportName,factoryName);
            this.configurationStructureName = configurationStructureName;
        }

        /* (non-Javadoc)
         * @see org.epics.ioc.dbd.DBDLinkSupport#getConfigurationStructureName()
         */
        public String getConfigurationStructureName() {
            return configurationStructureName;
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.dbd.DBDSupport#toString(int)
         */
        public String toString(int indentLevel) {
            return super.getString(indentLevel) +
                " configurationStructureName " + configurationStructureName;
        }
    }
}
