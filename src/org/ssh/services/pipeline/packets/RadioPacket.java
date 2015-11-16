package org.ssh.services.pipeline.packets;

import org.ssh.models.enums.SendMethod;
import org.ssh.services.PipelinePacket;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import protobuf.Radio.RadioProtocolWrapper;
import protobuf.Radio.RadioProtocolWrapper.Builder;

/**
 * The RadioPacket class.
 *
 * @author Rimon Oz
 */
public class RadioPacket extends PipelinePacket {
    
    /** The data. */
    private MessageOrBuilder   data;
    private final SendMethod[] sendMethods;
                               
    /**
     * Instantiates a new radio packet.
     *
     * @param builder
     *            the message
     * @param methods
     *            specify senders different than default
     */
    public RadioPacket(final MessageOrBuilder message, final SendMethod... methods) {
        this.data = message;
        this.sendMethods = methods;
    }
    
    /**
     * Will return {@link Builder} if {@link #getData()} is mutable (and thus an instance of
     * {@link Builder}). otherwise will return a new {@link Builder}
     * 
     * @return
     */
    public Builder getBuilder() {
        return this.isMutable() ? (Builder) this.getData() : ((RadioProtocolWrapper) this.getData()).toBuilder();
    }
    
    /**
     * Gets the data.
     *
     * @return the data
     */
    public MessageOrBuilder getData() {
        return this.data;
    }
    
    /**
     * if neccesary will builds a {@link Message}, otherwise just casts it
     * 
     * @return
     */
    public Message getMessage() {
        return this.isMutable() ? ((Builder) this.getData()).build() : (RadioProtocolWrapper) this.getData();
    }
    
    public SendMethod[] getSendMethods() {
        return this.sendMethods;
    }
    
    @Override
    public boolean isMutable() {
        return (this.data instanceof Builder);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.pipeline.PipelinePacket#read()
     */
    @Override
    public MessageOrBuilder read() {
        return this.data;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.pipeline.PipelinePacket#save(Object)
     */
    @Override
    @SuppressWarnings ("unchecked")
    public <T extends PipelinePacket> T save(final MessageOrBuilder message) {
        this.data = message;
        return (T) this;
    }
}