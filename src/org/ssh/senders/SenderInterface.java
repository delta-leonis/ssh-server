package org.ssh.senders;

import org.ssh.models.enums.SendMethod;
import org.ssh.services.producers.Communicator;

import com.google.protobuf.Message;

/**
 * Interface that enforces a send method for generic protobuf messages.<br />
 * Note that implementations of the interface shouldn't implement any other send method,<br />
 * All different handlers for send should be implemented in {@link Communicator} so consistent
 * availability is present<br />
 * throughout the org.ssh.managers.<br />
 *
 *
 * @author Jeroen
 * @see SendMethod
 * @see Communicator
 *     
 */
public interface SenderInterface {
    
    /**
     * Try to send a pre-build protobuf {@link Message}.
     * 
     * @param genericMessage
     *            message to send
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
