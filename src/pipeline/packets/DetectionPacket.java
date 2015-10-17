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
    
    /** The data. */
    private DetectionRobotOrBuilder data;

    /* (non-Javadoc)
     * @see pipeline.PipelinePacket#read()
     */
    @Override
    public MessageOrBuilder read() {
        return this.data;
    }

    /* (non-Javadoc)
     * @see pipeline.PipelinePacket#save(com.google.protobuf.MessageOrBuilder)
     */
    @SuppressWarnings("unchecked")
	@Override
    public <T extends PipelinePacket> T save(MessageOrBuilder data) {
        this.data = (DetectionRobotOrBuilder) data;
        return (T) this;
    }

}
