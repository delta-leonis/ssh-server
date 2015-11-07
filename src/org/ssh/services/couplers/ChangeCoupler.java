package org.ssh.services.couplers;

import org.ssh.services.Coupler;
import org.ssh.services.PipelinePacket;
import org.ssh.services.Service;
import org.ssh.services.pipeline.packets.RadioPacket;

import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolCommand.Builder;

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
    public PipelinePacket process(final PipelinePacket pipelinePacket) {
        // modify the packet and return it
        return pipelinePacket.apply(content -> {
            Service.LOG.info("Change Coupler is updating some values ....");
            // get the data
            final RadioProtocolCommand.Builder changeling = (Builder) ((RadioPacket) content).getData();
            // update it
            changeling.setVelocityY(300f);
            // return it
            return new RadioPacket(changeling);
        });
    }
}
