package org.ssh.network.transmit.radio.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractService;
import org.ssh.services.AbstractCoupler;


/**
 * The Class ChangeCoupler.
 *
 * An example implementation of a AbstractCoupler
 *
 * @author Rimon Oz
 */
public class ChangeCoupler extends AbstractCoupler<RadioPacket> {
    
    /**
     * Instantiates a new ChangeCoupler.
     *
     * @param name
     *            The name of the new AbstractCoupler
     */
    public ChangeCoupler() {
        super("changecoupler", packet -> {
            AbstractService.LOG.info("Change Coupler is updating some values ....");
            // get the data
            return new RadioPacket(packet.read().toBuilder()
                    .setCommand(0, packet.read().getCommand(0).toBuilder().setVelocityY(9000.3f))
                    .build());
        });
    }

}
