package org.ssh.pipelines.packets;

import org.ssh.pipelines.PipelinePacket;

import protobuf.Geometry;
import protobuf.Geometry.GeometryDataOrBuilder;

/**
 * The GeometryPacket class.
 *
 * @author Rimon Oz
 */
public class GeometryPacket extends PipelinePacket<Geometry> {
    
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
}
