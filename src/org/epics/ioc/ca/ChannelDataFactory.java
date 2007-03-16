/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.ca;

import org.epics.ioc.pv.*;
import org.epics.ioc.util.*;

/**
 * A factory for creating ChannelData and ChannelDataQueue.
 * @author mrk
 *
 */
public class ChannelDataFactory {
     /**
      * Create a ChannelData for the specified channel and ChannelFieldGroup.
     * @param channel The channel.
     * @param channelFieldGroup The field group defining what should be in the channelData.
     * @param supportAlso Should support be read/written?
     * @return The ChannelData interface.
     */
    public static ChannelData createChannelData(
         Channel channel,ChannelFieldGroup channelFieldGroup,boolean supportAlso)
     {
        return new BaseChannelData(channel,channelFieldGroup,
            FieldFactory.getFieldCreate(),PVDataFactory.getPVDataCreate(),supportAlso);
     }
    
     /**
      * Create a queue of ChannelData.
     * @param queueSize The queueSize. This is can not be changed after creation.
     * @param channel The channel.
     * @param channelFieldGroup The field group defining what should be in each channelDataField.
     * @param supportAlso Should support be read/written?
     * @return The ChannelDataQueue interface.
     */
    public static ChannelDataQueue createDataQueue(
             int queueSize,
             Channel channel,ChannelFieldGroup channelFieldGroup,boolean supportAlso)
     {
          if(queueSize<3) {
              channel.message("queueSize changed to 3", MessageType.warning);
              queueSize = 3;
          }
          ChannelData[] queue = new ChannelData[queueSize];
          for(int i = 0; i<queueSize; i++) {
              queue[i] = createChannelData(channel,channelFieldGroup,supportAlso);
          }
          return new BaseChannelDataQueue(queue);
     }
}
