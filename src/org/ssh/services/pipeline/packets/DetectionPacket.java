package org.ssh.services.pipeline.packets;

import org.ssh.services.PipelinePacket;

import com.google.protobuf.MessageOrBuilder;

import protobuf.Detection.DetectionRobotOrBuilder;

/**
 * The DetectionPacket class.
 *
 * @author Rimon Oz
 */
public class DetectionPacket extends PipelinePacket {

    /** The data. */
    private DetectionRobotOrBuilder data;
    
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
        this.data = (DetectionRobotOrBuilder) data;
        return (T) this;
    }
    
}
