package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.RadioPacket;

/**
 * The Class RadioPipeline.
 *
 * @author Rimon Oz
 */
public class RadioPipeline extends Pipeline<RadioPacket> {
    
    /**
     * Instantiates a new radio pipeline.
     *
     * @param name
     *            the name
     */
    public RadioPipeline(final String name) {
        super(name);
    }
}
