package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message.Builder;

import protobuf.Radio.RadioProtocolWrapper;

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
    
    private transient Class<M> type = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    
    /**
     * Parses a {@link ByteArrayInputStream bytestream} to parameterized type.
     * 
     * @param byteStream
     *            stream to parse
     */
    public ProtoPacket(ByteArrayInputStream byteStream) {
        try {
            this.save(((M) Class.forName(type.getTypeName()).getMethod("getDefaultInstance").invoke(null))
                    .getParserForType().parseFrom(byteStream));
        }
        catch (InvalidProtocolBufferException | SecurityException | IllegalAccessException | ClassNotFoundException
                | IllegalArgumentException | InvocationTargetException | NoSuchMethodException exception) {
            ProtoPacket.LOG.exception(exception);
            exception.printStackTrace();
            ProtoPacket.LOG.warning("Could not parse data for %s", this.getClass().getSimpleName());
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
