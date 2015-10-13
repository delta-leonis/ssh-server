package pipeline.packets;

import com.google.protobuf.MessageOrBuilder;

import pipeline.PipelinePacket;
import protobuf.Geometry.GeometryDataOrBuilder;

/**
 * The GeometryPacket class.
 *  
 * @author Rimon Oz
 */
public class GeometryPacket extends PipelinePacket {
	private GeometryDataOrBuilder data;

	@Override
	public MessageOrBuilder read() {
		return this.data;
	}

	@Override
	public <T extends PipelinePacket> T save(MessageOrBuilder data) {
		this.data = (GeometryDataOrBuilder) data;
		return (T)this;
	}
}
