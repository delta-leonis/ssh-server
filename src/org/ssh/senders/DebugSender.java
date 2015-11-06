package org.ssh.senders;

import java.util.logging.Level;

import org.ssh.util.Logger;

import com.google.protobuf.Message;

/**
 * SendInterface used for debugging. This sender will log the contents of {@link Message} to
 * {@link Logger}
 *
 * @author Jeroen
 *        
 */
public class DebugSender implements SenderInterface {
    
    // respective logger
    private final Logger logger = Logger.getLogger();
    
    /**
     * Create a debug-sender
     * 
     * @param loggerLevel
     *            level to log to
     */
    public DebugSender(final Level loggerLevel) {
        this.logger.setLevel(loggerLevel);
    }
    
    @Override
    public boolean send(final Message genericMessage) {
        this.logger.log(this.logger.getLevel(), genericMessage.toString());
        return true;
    }
    
    @Override
    public boolean unregister() {
        return true;
    }
    
}
