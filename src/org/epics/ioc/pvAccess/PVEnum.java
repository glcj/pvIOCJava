/**
 * 
 */
package org.epics.ioc.pvAccess;

/**
 * PVEnum provides access to a an array of String choices
 * and an index specifying the current choice.
 * @author mrk
 *
 */
public interface PVEnum extends PVData{
    /**
     * get the index of the current selected choice
     * @return index of current choice
     */
    int getIndex();
    /**
     * set the choice
     * @param index for choice 
     */
    void setIndex(int index);
    /**
     * get the choice values
     * @return String[] specifying the choices.
     */
    String[] getChoices();
    /**
     * et the choice values. 
     * @param choice a String[] specifying the choices.
     * @return (true,false) if the choices were modified.
     * A value of false normally means the choice strings were readonly.
     */
    boolean setChoices(String[] choice);
}