package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message.Builder;

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
     * Parses a {@link ByteArrayInputStream bytestream} to parameterized type.
     * 
     * @param byteStream
     *            stream to parse
     */
    public ProtoPacket(ByteArrayInputStream byteStream) {
        try {
            // get parser, and parse data
            this.save(this.read().getParserForType().parseFrom(byteStream));
        }
        catch (InvalidProtocolBufferException exception) {
            ProtoPacket.LOG.exception(exception);
        }
    }
    
    /**
     * Instansiates a protopacket based on a message.
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
    
    public ProtoPacket<M> save(Builder data) {
        return (ProtoPacket<M>) this.save(data.build());
    }
}