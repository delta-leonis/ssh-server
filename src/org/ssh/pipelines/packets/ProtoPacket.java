package org.ssh.pipelines.packets;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.Parser;

/**
 * PipelinePacket for protobuf packets, class implements some usefull methods for use with protobuf
 * messages.
 * 
 * @author Jeroen de Jong
 *        
 * @param <M>
 *            type of protobuf message.
 *            
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link org.ssh.pipelines.PipelinePacket PipelinePackets}
 */
public class ProtoPacket<M extends GeneratedMessage> extends PipelinePacket<M> {
    
    /**
     * Instantiates a protopacket based on a message.
     * 
     * @param data
     *            initial message data.
     */
    public ProtoPacket(M data) {
        this.save(data);
    }
    
    /**
     * @return this message as a builder.
     */
    public Builder asBuilder() {
        return this.read().toBuilder();
    }
    
    /**
     * Save new data for this packet
     * 
     * @param data
     *            new data
     * @return the new packet
     */
    public ProtoPacket<M> save(Builder data) {
        return (ProtoPacket<M>) this.save(data.build());
    }
}
