package org.ssh.pipelines.packets;

import protobuf.Geometry.GeometryData;

/**
 * Geometry packet class
 * 
 * @author Jeroen
 * 
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link org.ssh.pipelines.PipelinePacket PipelinePackets}
 * @see {@link org.ssh.pipelines.packets.ProtoPacket ProtoPacket}
 */
public class GeometryPacket extends ProtoPacket<GeometryData> {
    public GeometryPacket(GeometryData geometryData) {
        super(geometryData);
    }
}
