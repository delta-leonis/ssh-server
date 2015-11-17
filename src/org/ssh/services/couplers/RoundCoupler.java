package org.ssh.services.couplers;

import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.service.Coupler;

public class RoundCoupler extends Coupler<RadioPacket> {
    
    public RoundCoupler() {
        super("roundcoupler");
    }
    
    @Override
    public PipelinePacket process(final PipelinePacket pipelinePacket) {
        final RadioPacket packet = (RadioPacket) pipelinePacket;
        packet.getBuilder().getCommandBuilderList()
                .forEach(command -> command.getAllFields().entrySet().stream().filter(
                        entry -> entry.getValue() instanceof Float)
                .forEach(entry -> command.setField(entry.getKey(), (float) Math.round((Float) entry.getValue()))));
        return packet;
    }
}
