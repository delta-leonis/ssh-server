package org.ssh.services.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Coupler;


/**
 * The Class ChangeCoupler.
 *
 * An example implemtation of a Coupler
 *
 * @author Rimon Oz
 */
public class ChangeCoupler extends Coupler<RadioPacket> {
    
    /**
     * Instantiates a new ChangeCoupler.
     *
     * @param name
     *            The name of the new Coupler
     */
    public ChangeCoupler(final String name) {
        super(name);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.Coupler#process(org.ssh.services.pipeline.PipelinePacket)
     */
    @Override
    public RadioPacket process(final RadioPacket pipelinePacket) {
        // modify the packet and return it
        return new RadioPacket(pipelinePacket.apply(content -> {
            Service.LOG.info("Change Coupler is updating some values ....");
            // get the data
             return pipelinePacket.read().toBuilder()
                     .setCommand(0, pipelinePacket.read().getCommand(0).toBuilder().setVelocityY(9000.3f))
                     .build();
        }));
    }
}
