package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;

import org.ssh.pipelines.packets.ProtoPacket;

/**
 * Pipelinepacket for {@link protobuf.Wrapper.Wrapperpacket wrapperpackets}, it baiscly wraps the
 * {@link #hasDetection()} and {@link #hasGeometry()} methods as provided in the generated protobuf
 * class.
 * 
 * @author Jeroen de Jong
 *         
 */
public class WrapperPacket extends ProtoPacket<protobuf.Wrapper.WrapperPacket> {
    
    /**
     * {@inheritDoc}
     */
    public WrapperPacket(ByteArrayInputStream data) {
        super(data);
    }
    
    /**
     * @return true if packet contains {@link protobuf.Detection.DetectionFrame DetectionFrame}
     */
    public boolean hasDetection() {
        return read().hasDetection();
    }
    
    /**
     * @return true if packet contains {@link protobuf.Geometry.GeometryData GeometryData}
     */
    public boolean hasGeometry() {
        return read().hasGeometry();
    }
}
