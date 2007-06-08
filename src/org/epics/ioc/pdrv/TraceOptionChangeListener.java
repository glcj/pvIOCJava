/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.pdrv;

/**
 * @author mrk
 *
 */
public interface TraceOptionChangeListener {
    /**
     * A trace option has changed.
     * The trace methods can be called to get the latest options.
     */
    void optionChange();
}
