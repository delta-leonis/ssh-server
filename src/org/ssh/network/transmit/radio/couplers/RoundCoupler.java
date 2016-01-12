package org.ssh.network.transmit.radio.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractCoupler;

public class RoundCoupler extends AbstractCoupler<RadioPacket> {

    public RoundCoupler() {
        super("roundcoupler", packet -> {
            packet.read().toBuilder().getCommandBuilderList()
                    .forEach(command -> command.getAllFields().entrySet().stream().filter(
                            entry -> entry.getValue() instanceof Float)
                            .forEach(entry -> command.setField(entry.getKey(), (float) Math.round((Float) entry.getValue()))));
            return packet;
        });
    }
}
