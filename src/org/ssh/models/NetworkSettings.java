package org.ssh.models;

import org.ssh.pipelines.packets.ProtoPacket;

/**
 * Class that contains all settings for a certain network connection based on a {@link ProtoPacket
 * ProtoPacket<?>}.
 * 
 * @author Jeroen de Jong
 *        
 */
public class NetworkSettings extends Model {
    /** IP to connect to */
    private String                                    IP;
    /** Port to connect to */
    private Integer                                   port;
    /** Preferred size of the buffer */
    private Integer                                   bufferSize;
    /** Whether this connection should be closed or not */
    private transient Boolean                         closed;
                                                      
    /** Type of {@link ProtoPacket ProtoPacket<?>} which these settings are for */
    private transient Class<? extends ProtoPacket<?>> packetType;
                                                      
    /**
     * Create a new isntance of settings
     * 
     * @param type
     *            {@link ProtoPacket ProtoPacket<?>} which these settings are for
     */
    public NetworkSettings(Class<? extends ProtoPacket<?>> type) {
        super("NetworkSettings", type.getSimpleName());
        this.packetType = type;
    }

    @Override
    public void initialize(){
        // standard this sockets should be open
        this.closed = Boolean.FALSE;
    }
    
    /** Preferred size of the buffer */
    public Integer getBufferSize() {
        return this.bufferSize;
    }
    
    /** IP to connect to */
    public String getIP() {
        return this.IP;
    }
    
    /**
     * @return Type of {@link ProtoPacket ProtoPacket<?>} which these settings are for
     */
    public Class<? extends ProtoPacket<?>> getPacketType() {
        return packetType;
    }
    
    /** Port to connect to */
    public Integer getPort() {
        return this.port;
    }
    
    /**
     * @return Whether this connection should be closed or not
     */
    public boolean isClosed() {
        return closed;
    }
    
    /**
     * @return Whether these settings are complete, and ready for use
     */
    public boolean isComplete() {
        return getIP() != null && getPort() != null && this.getBufferSize() != null;
    }
    
}
