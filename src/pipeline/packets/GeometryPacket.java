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
    
    /** The data. */
    private GeometryDataOrBuilder data;

    /**
     * Gets the data.
     *
     * @return the data
     */
    public GeometryDataOrBuilder getData() {
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
        this.data = (GeometryDataOrBuilder) data;
        return (T) this;
    }
}
