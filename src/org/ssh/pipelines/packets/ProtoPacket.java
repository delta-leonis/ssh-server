package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message.Builder;

public class ProtoPacket<M extends GeneratedMessage> extends PipelinePacket<M> {
    
    public ProtoPacket(ByteArrayInputStream byteStream) {
        try {
            this.save(this.read().getParserForType().parseFrom(byteStream));
        }
        catch (InvalidProtocolBufferException exception) {
            ProtoPacket.LOG.exception(exception);
        }
    }
    
    public Builder asBuilder(){
        return this.read().toBuilder();
    }

    public ProtoPacket<M> save(Builder data) {
        return (ProtoPacket<M>) this.save(data.build());
    }
}
