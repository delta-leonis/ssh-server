package org.ssh.services.pipeline.pipelines;

import org.ssh.services.Pipeline;
import org.ssh.services.pipeline.packets.RadioPacket;

/**
 * The Class RadioPipeline.
 *
 * @author Rimon Oz
 */
public class RadioPipeline extends Pipeline<RadioPacket> {
    
    /**
     * Instantiates a new radio org.ssh.services.pipeline.
     *
     * @param name
     *            the name
     */
    public RadioPipeline(final String name) {
        super(name);
    }
}
