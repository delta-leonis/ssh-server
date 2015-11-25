package org.ssh.models;

import org.ssh.pipelines.packets.ProtoPacket;

public class NetworkSettings extends Model {
    private String IP;
    private Integer port;
    private Integer bufferSize;

    private transient Class<? extends ProtoPacket<?>> packetType;
    
    
    public NetworkSettings(Class<? extends ProtoPacket<?>> type) {
        super("NetworkSettings");
        this.packetType = type;
    }
    
    @Override
    public String getSuffix(){
        return packetType.getSimpleName();
    }
    
    public Class<? extends ProtoPacket<?>> getPacketType(){
        return packetType;
    }
    
    public Integer getPort(){
        return this.port;
    }
    
    public String getIP(){
        return this.IP;
    }

    public Integer getBufferSize() {
        return this.bufferSize;
    }

    public boolean isComplete() {
        return getIP() != null && getPort() != null && this.getBufferSize() != null;
    }

}
