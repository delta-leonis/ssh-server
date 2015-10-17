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
    
    /** The data. */
    private RefereeOrBuilder data;

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
        this.data = (RefereeOrBuilder) data;
        return (T) this;
    }
}
