/**
 * 
 */
package org.epics.ioc.dbDefinition;

import org.epics.ioc.pvAccess.*;

/**
 * structure and recordType database definition
 * @author mrk
 *
 */
public interface DBDStructure extends Structure{
    
    /**
     * get the field descriptions
     * @return array of DBDField describing each field
     */
    DBDField[] getDBDFields();
    /**
     * Get the index of the DBDField for the specified field
     * @param fieldName the name of the field
     * @return the index or -1 if the field does not exist
     */
    int getDBDFieldIndex(String fieldName);
}
