/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.pvioc.pdrv.interfaces;

import org.epics.pvioc.pdrv.Device;

/**
 * Base for an interface to a port or device.
 * @author mrk
 *
 */
public interface Interface {
    /**
     * Get the interface name.
     * @return The name.
     */
    String getInterfaceName();
    /**
     * Get the device the interface supports.
     * @return The device interface.
     */
    Device getDevice();
}