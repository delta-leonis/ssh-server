package org.ssh.services.couplers;

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
    public VerboseCoupler() {
        super("verbosecoupler", packet -> new RadioPacket(packet.apply(content -> {
            // print the data
            Service.LOG.info("The VerboseCoupler ate a packet that looked like: \n%s",
                    packet.read().toString());
            // and return it
            return content;
        })));
    }
}
