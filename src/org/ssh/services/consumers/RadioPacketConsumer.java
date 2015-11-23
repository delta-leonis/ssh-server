package org.ssh.services.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.senders.SenderInterface;
import org.ssh.services.service.Consumer;
import org.ssh.util.Logger;

import com.google.protobuf.Message;

public class RadioPacketConsumer extends Consumer<RadioPacket> {
    
    /**
     * Maps a {@link SenderInterface} to a {@link SendMethod} for easy management
     */
    private final Map<SendMethod, SenderInterface> senders     = new HashMap<SendMethod, SenderInterface>();
    /**
     * Current selected sendMethods. First to register will be automaticaly added
     */
    private final List<SendMethod>                 sendMethods = new ArrayList<SendMethod>();
                                                               
    // respective logger
    private static final Logger                    LOG         = Logger.getLogger();
                                                               
    public RadioPacketConsumer() {
        super("RadioPacketConsumer");
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
            RadioPacketConsumer.LOG.warning("SendMethod (%s) has no registered handler.\n", method);
            return false;
        }
        
        //
        if (this.sendMethods.contains(method)) {
            RadioPacketConsumer.LOG.info("%s allready is a default sendMethod");
            return false;
        }
        
        this.sendMethods.add(method);
        RadioPacketConsumer.LOG.info("SendMethod %s has been set as a default sendmethod.\n", method);
        return true;
    }
    
    @Override
    public boolean consume(final RadioPacket pipelinePacket) {
        // cast to the right packet-genericType
        final RadioPacket packet = (RadioPacket) pipelinePacket;
        // get default sendmethods
        SendMethod[] methods = packet.getSendMethods();
        // replace them by specified sendmethods if neccecary
        if (packet.getSendMethods().length == 0)
            methods = this.sendMethods.toArray(methods);
            
        RadioPacketConsumer.LOG.info("Trying to send a consumed packet");
        // send the packet
        return this.send(packet.read(), methods);
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
            RadioPacketConsumer.LOG.info("Sendmethod %s has a communicator, and will be overwritten.\n", key);
            
            // try to unregister the key
            this.unregister(key);
        }
        
        this.senders.put(key, communicator);
        RadioPacketConsumer.LOG.info("registered hook for %s.", key);
        
        // set a default communicator when we have communicators, but no send method
        if ((this.senders.size() == 1) && this.sendMethods.isEmpty()) this.addDefault(key);
    }
    
    public boolean removeDefault(final SendMethod method) {
        if (!this.sendMethods.contains(method)) {
            RadioPacketConsumer.LOG.info("%s isn't a default sendMethod");
            return false;
        }
        
        return this.sendMethods.remove(method);
    }
    
    private boolean send(final Message genericMessage, final SendMethod... sendMethods) {
        // check if sendMethod is set
        if ((sendMethods == null) || (sendMethods.length == 0)) {
            RadioPacketConsumer.LOG.severe("Sendmethod has not been set.");
            return false;
        }
        
        // get all the senders that have been specified by the user and have implemented
        // send-methods
        final Map<Boolean, List<SendMethod>> filteredMethods = Stream.of(sendMethods)
                .collect(Collectors.partitioningBy(sender -> this.senders.containsKey(sender)));
                
        // print a warning for each sendMethod that was specified but had no implemented send method
        filteredMethods.get(false)
                .forEach(sendmethod -> RadioPacketConsumer.LOG.warning("Sendmethod (%s) has no registered handler.\n", sendmethod));
                
        // return success value
        return filteredMethods.get(true).stream()
                // send messages parallel
                .parallel()
                // send the message
                .map(sendmethod -> this.senders.get(sendmethod).send(genericMessage))
                // collect all success values and reduce to true if all senders succeeded; false
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
            RadioPacketConsumer.LOG.warning("Could not unregister %s, as it has no hook", sendmethod);
            return false;
        }
        
        // remove sender from list and call unregister
        if (!this.senders.remove(sendmethod).unregister()) {
            RadioPacketConsumer.LOG.warning("Could not unregister %s.", sendmethod);
            return false;
        }
        
        // unhooking and unregistering were a success!
        RadioPacketConsumer.LOG.info("Unregistered %s.", sendmethod);
        return true;
    }
    
}
