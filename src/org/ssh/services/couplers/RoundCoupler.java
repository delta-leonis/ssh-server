package org.ssh.services.couplers;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.service.Coupler;

public class RoundCoupler extends Coupler<RadioPacket> {
    
    public RoundCoupler() {
        super("roundcoupler");
    }
    
    @Override
    public RadioPacket process(RadioPacket radioPacket) {
        RadioPacket packet = (RadioPacket) radioPacket;
        packet.read().toBuilder().getCommandBuilderList()
                .forEach(command -> command.getAllFields().entrySet().stream().filter(
                        entry -> entry.getValue() instanceof Float)
                .forEach(entry -> command.setField(entry.getKey(), (float) Math.round((Float) entry.getValue()))));
        return packet;
    }
}
