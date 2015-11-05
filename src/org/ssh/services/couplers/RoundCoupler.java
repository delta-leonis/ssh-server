package org.ssh.services.couplers;

import org.ssh.services.pipeline.Coupler;
import org.ssh.services.pipeline.PipelinePacket;
import org.ssh.services.pipeline.packets.RadioPacket;

public class RoundCoupler extends Coupler<RadioPacket> {

	public RoundCoupler() {
		super("roundcoupler");
	}

	@Override
	public PipelinePacket process(PipelinePacket pipelinePacket) {
		RadioPacket packet = (RadioPacket) pipelinePacket;
		packet.getBuilder().getCommandBuilderList()
			.forEach(command -> command.getAllFields()
				.entrySet().stream()
				.filter(entry -> entry.getValue() instanceof Float)
				.forEach(entry -> command.setField(
									entry.getKey(), 
									(float) Math.round((Float) entry.getValue())
						)));
		return packet;
	}
}
