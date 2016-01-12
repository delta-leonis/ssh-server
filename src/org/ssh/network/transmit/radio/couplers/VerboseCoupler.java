package org.ssh.network.transmit.radio.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractCoupler;
import org.ssh.services.AbstractService;

/**
 * The Class VerboseCoupler.
 * <p>
 * An example implemtation of a AbstractCoupler.
 *
 * @author Rimon Oz
 */
public class VerboseCoupler extends AbstractCoupler<RadioPacket> {

    /**
     * Instantiates a new VerboseCoupler.
     *
     * @param name The name of the new AbstractCoupler.
     */
    public VerboseCoupler() {
        super("verbosecoupler", packet -> new RadioPacket(packet.apply(content -> {
            // print the data
            AbstractService.LOG.info("The VerboseCoupler ate a packet that looked like: \n%s",
                    packet.read().toString());
            // and return it
            return content;
        })));
    }
}
