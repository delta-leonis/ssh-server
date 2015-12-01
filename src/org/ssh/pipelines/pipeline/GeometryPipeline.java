package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.GeometryPacket;

/**
 * Pipeline for {@link GeometryPacket GeometryPackets}
 * 
 * @author Jeroen de Jong
 * @author Rimon Oz
 *         
 */
public class GeometryPipeline extends Pipeline<GeometryPacket> {
    
    /**
     * Instantiates a new geometry pipeline.
     *
     * @param name
     *            the name
     */
    public GeometryPipeline(final String name) {
        super(name);
        // TODO: build all the services here
    }
    
}
