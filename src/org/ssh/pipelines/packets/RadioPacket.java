package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;

import org.ssh.models.enums.SendMethod;

import protobuf.Radio.RadioProtocolCommand.Builder;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * The RadioPacket class.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 */
public class RadioPacket extends ProtoPacket<RadioProtocolWrapper> {
    
    private final SendMethod[] sendMethods;
                               
    /**
     * Instantiates a new radio packet.
     *
     * @param builder
     *            the message
     * @param methods
     *            specify senders different than default
     */
    public RadioPacket(final RadioProtocolWrapper message, final SendMethod... methods) {
        super(null);
        this.sendMethods = methods;
    }


    public RadioPacket(Builder packetBuilder) {
        super(new ByteArrayInputStream(packetBuilder.build().toByteArray()));
        this.sendMethods = null;
    }


    public SendMethod[] getSendMethods() {
        return this.sendMethods;
    }

}