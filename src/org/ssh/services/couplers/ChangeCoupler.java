package org.ssh.services.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Coupler;


/**
 * The Class ChangeCoupler.
 *
 * An example implementation of a Coupler
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
    public ChangeCoupler() {
        super("changecoupler", packet -> {
            Service.LOG.info("Change Coupler is updating some values ....");
            // get the data
            return new RadioPacket(packet.read().toBuilder()
                    .setCommand(0, packet.read().getCommand(0).toBuilder().setVelocityY(9000.3f))
                    .build());
        });
    }

}
