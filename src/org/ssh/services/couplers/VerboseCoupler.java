package org.ssh.services.couplers;

import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Coupler;

/**
 * The Class VerboseCoupler.
 *
 * An example implemtation of a Coupler.
 *
 * @author Rimon Oz
 */
public class VerboseCoupler extends Coupler<RadioPacket> {
    
    /**
     * Instantiates a new VerboseCoupler.
     *
     * @param name
     *            The name of the new Coupler.
     */
    public VerboseCoupler(final String name) {
        super(name);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.Coupler#process(org.ssh.services.pipeline.PipelinePacket)
     */
    @Override
    public PipelinePacket process(final PipelinePacket radioPacket) {
        // modify the packet and return it
        return radioPacket.apply(content -> {
            // print the data
            Service.LOG.info("The VerboseCoupler ate a packet that looked like: \n%s",
                    ((RadioPacket) radioPacket).getData().toString());
            // and return it
            return content;
        });
    }
}
