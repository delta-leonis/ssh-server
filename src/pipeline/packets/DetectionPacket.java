package pipeline.packets;

import com.google.protobuf.MessageOrBuilder;

import pipeline.PipelinePacket;
import protobuf.Detection.DetectionRobotOrBuilder;

/**
 * The DetectionPacket class.
 *  
 * @author Rimon Oz
 */
public class DetectionPacket extends PipelinePacket {
	private DetectionRobotOrBuilder data;

	@Override
	public com.google.protobuf.MessageOrBuilder read() {
		return this.data;
	}

	@Override
	public <T extends PipelinePacket> T save(MessageOrBuilder data) {
		this.data = (DetectionRobotOrBuilder) data;
		return (T)this;
	}


}
