package org.ssh.network.transmit.senders;

import com.google.protobuf.Message;
import org.ssh.models.enums.SendMethod;

/**
 * Interface that enforces a send method for generic protobuf messages.<br />
 * Note that implementations of the interface shouldn't implement any other send method,<br />
 * All different handlers for send should be implemented in {@link Communicator} so consistent
 * availability is present throughout the the code.<br />
 *
 * @author Jeroen de Jong
 * @see SendMethod
 */
public interface SenderInterface {

    /**
     * Try to send a pre-build protobuf {@link Message}.
     *
     * @param genericMessage message to send
     * @return success value
     */
    public boolean send(Message genericMessage);

    /**
     * Will be called when {@link Communicator} unregisters any {@link SenderInterface}.
     *
     * @return success value
     */
    public boolean unregister();
}
