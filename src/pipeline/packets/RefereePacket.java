package pipeline.packets;

import com.google.protobuf.MessageOrBuilder;

import pipeline.PipelinePacket;
import protobuf.RefereeOuterClass.RefereeOrBuilder;

/**
 * The GeometryPacket class.
 *  
 * @author Rimon Oz
 */
public class RefereePacket extends PipelinePacket {
	private RefereeOrBuilder data;

	@Override
	public MessageOrBuilder read() {
		return this.data;
	}

	@Override
	public <T extends PipelinePacket> T save(MessageOrBuilder data) {
		this.data = (RefereeOrBuilder) data;
		return (T)this;
	}


}
