package org.ssh.network.transmit.senders;

import com.google.protobuf.Message;
import org.ssh.util.Logger;

import java.util.logging.Level;

/**
 * SendInterface used for debugging. This sender will log the contents of {@link Message} to
 * {@link Logger}
 *
 * @author Jeroen de Jong
 *         
 */
public class DebugSender implements SenderInterface {
    
    // respective logger
    private final static Logger LOG = Logger.getLogger();
    
    /**
     * Create a debug-sender
     * 
     * @param loggerLevel
     *            level to log to
     */
    public DebugSender(final Level loggerLevel) {
        DebugSender.LOG.setLevel(loggerLevel);
    }
    
    @Override
    public boolean send(final Message genericMessage) {
        DebugSender.LOG.log(DebugSender.LOG.getLevel(), genericMessage.toString());
        return true;
    }
    
    @Override
    public boolean unregister() {
        return true;
    }
    
}
