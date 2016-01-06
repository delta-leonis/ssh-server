package org.ssh.network.transmit.radio.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.models.enums.SendMethod;
import org.ssh.network.transmit.senders.SenderInterface;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractConsumer;
import org.ssh.util.Logger;

import com.google.protobuf.Message;

public class RadioPacketSender extends AbstractConsumer<RadioPacket> {
    
    // respective logger
    private static final Logger                    LOG         = Logger.getLogger();
    /**
     * Maps a {@link SenderInterface} to a {@link SendMethod} for easy management
     */
    private final Map<SendMethod, SenderInterface> senders     = new HashMap<SendMethod, SenderInterface>();
                                                               
    /**
     * Current selected sendMethods. First to register will be automaticaly added
     */
    private final List<SendMethod>                 sendMethods = new ArrayList<SendMethod>();
                                                               
    public RadioPacketSender() {
        super("RadioPacketSender");
        attachToCompatiblePipelines();
    }
    
    /**
     * Define the SendMethod to send messages, as used in {@link #send(Message)}<br />
     * A handler for the {@link SendMethod} should be {@link #register(SendMethod, SenderInterface)
     * registered} before setting new send method
     * 
     * @param newSendMethod
     * @return succes value
     */
    public boolean addDefault(final SendMethod... newSendMethods) {
        return Stream.of(newSendMethods).map(method -> this.addDefault(method)).reduce(true,
                (accumulator, result) -> accumulator && result);
    }
    
    public boolean addDefault(final SendMethod method) {
        // making sure sendMethod has a registered handler
        if (!this.senders.containsKey(method)) {
            RadioPacketSender.LOG.warning("SendMethod (%s) has no registered handler.\n", method);
            return false;
        }
        
        //
        if (this.sendMethods.contains(method)) {
            RadioPacketSender.LOG.info("%s allready is a default sendMethod");
            return false;
        }
        
        this.sendMethods.add(method);
        RadioPacketSender.LOG.info("SendMethod %s has been set as a default sendmethod.\n", method);
        return true;
    }
    
    @Override
    public boolean consume(final RadioPacket pipelinePacket) {
        // get default sendmethodss
        SendMethod[] methods = pipelinePacket.getSendMethods();
        // replace them by specified sendmethods if neccecary
        if (pipelinePacket.getSendMethods().length == 0)
            methods = this.sendMethods.toArray(methods);
            
        RadioPacketSender.LOG.info("Trying to send a consumed packet");
        // send the packet
        return this.send(pipelinePacket.read(), methods);
    }
    
    /**
     * Register a new handler for a given SendMethod. Note that only one {@link SenderInterface} per
     * {@link SendMethod} is allowed. New handlers will overwrite older ones.<br />
     * The first registered {@link SendMethod} will be set as default sendmethod.
     * 
     * @param key
     *            sendMethod to use
     * @param communicator
     *            communicator that implements given sendmethod
     */
    public void register(final SendMethod key, final SenderInterface communicator) {
        // give a notification that existing keys will be overriden
        if (this.senders.containsKey(key)) {
            RadioPacketSender.LOG.info("Sendmethod %s has a communicator, and will be overwritten.\n", key);
            
            // try to unregister the key
            this.unregister(key);
        }
        
        this.senders.put(key, communicator);
        RadioPacketSender.LOG.info("registered hook for %s.", key);
        
        // set a default communicator when we have communicators, but no send method
        if ((this.senders.size() == 1) && this.sendMethods.isEmpty()) this.addDefault(key);
    }
    
    public boolean removeDefault(final SendMethod method) {
        if (!this.sendMethods.contains(method)) {
            RadioPacketSender.LOG.info("%s isn't a default sendMethod");
            return false;
        }
        
        return this.sendMethods.remove(method);
    }
    
    private boolean send(final Message genericMessage, final SendMethod... sendMethods) {
        // check if sendMethod is set
        if ((sendMethods == null) || (sendMethods.length == 0)) {
            RadioPacketSender.LOG.severe("Sendmethod has not been set.");
            return false;
        }
        
        // get all the transmit that have been specified by the user and have implemented
        // send-methods
        final Map<Boolean, List<SendMethod>> filteredMethods = Stream.of(sendMethods)
                .collect(Collectors.partitioningBy(sender -> this.senders.containsKey(sender)));
                
        // print a warning for each sendMethod that was specified but had no implemented send method
        filteredMethods.get(false)
                .forEach(sendmethod -> RadioPacketSender.LOG.warning("Sendmethod (%s) has no registered handler.\n", sendmethod));
                
        // return success value
        return filteredMethods.get(true).stream()
                // send messages parallel
                .parallel()
                // send the message
                .map(sendmethod -> this.senders.get(sendmethod).send(genericMessage))
                // collect all success values and reduce to true if all transmit succeeded; false
                // otherwise
                .reduce(true, (accumulator, success) -> accumulator && success) && filteredMethods.get(false).isEmpty();
    }
    
    /**
     * Unregisters a hook to a {@link SendMethod}
     * 
     * @param sendmethod
     *            method to unhook
     * @return succes value
     */
    public boolean unregister(final SendMethod sendmethod) {
        // check if the key exists
        if (!this.senders.containsKey(sendmethod)) {
            RadioPacketSender.LOG.warning("Could not unregister %s, as it has no hook", sendmethod);
            return false;
        }
        
        // remove sender from list and call unregister
        if (!this.senders.remove(sendmethod).unregister()) {
            RadioPacketSender.LOG.warning("Could not unregister %s.", sendmethod);
            return false;
        }
        
        // unhooking and unregistering were a success!
        RadioPacketSender.LOG.info("Unregistered %s.", sendmethod);
        return true;
    }
    
}
