package org.ssh.pipelines.packets;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.MessageOrBuilder;

import protobuf.Geometry.GeometryDataOrBuilder;

/**
 * The GeometryPacket class.
 *
 * @author Rimon Oz
 */
public class GeometryPacket extends PipelinePacket {
    
    /** The data. */
    private GeometryDataOrBuilder data;
    
    /**
     * Gets the data.
     *
     * @return the data
     */
    public GeometryDataOrBuilder getData() {
        return this.data;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.pipeline.PipelinePacket#read()
     */
    @Override
    public MessageOrBuilder read() {
        return this.data;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.pipeline.PipelinePacket#save(com.google.protobuf.MessageOrBuilder)
     */
    @SuppressWarnings ("unchecked")
    @Override
    public <T extends PipelinePacket> T save(final MessageOrBuilder data) {
        this.data = (GeometryDataOrBuilder) data;
        return (T) this;
    }
}
