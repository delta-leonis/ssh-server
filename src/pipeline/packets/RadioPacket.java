package pipeline.packets;

import com.google.protobuf.MessageOrBuilder;

import pipeline.PipelinePacket;
import protobuf.Radio.RadioProtocolCommandOrBuilder;

/**
 * The RadioPacket class.
 * 
 * @author Rimon Oz
 */
public class RadioPacket extends PipelinePacket {
    
    /** The data. */
    private RadioProtocolCommandOrBuilder data;

    /**
     * Instantiates a new radio packet.
     *
     * @param builder the builder
     */
    public RadioPacket(MessageOrBuilder builder) {
        this.data = (RadioProtocolCommandOrBuilder) builder;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public RadioProtocolCommandOrBuilder getData() {
        return this.data;
    }

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
        this.data = (RadioProtocolCommandOrBuilder) data;
        return (T) this;
    }
}
