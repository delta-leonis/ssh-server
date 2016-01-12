package org.ssh.network.transmit.radio;

import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.RadioPacket;

/**
 * The Class RadioPipeline.
 *
 * @author Rimon Oz
 */
public class RadioPipeline extends AbstractPipeline<RadioPacket> {

    /**
     * Instantiates a new radio pipeline.
     *
     * @param name the name
     */
    public RadioPipeline(final String name) {
        super(name);
    }
}
