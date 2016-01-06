package org.ssh.managers.manager;

import java.util.ArrayList;

import org.ssh.managers.Manager;
import org.ssh.managers.controllers.NetworkController;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.network.transmit.radio.RadioPipeline;
import org.ssh.network.transmit.senders.SenderInterface;
import org.ssh.services.AbstractService;
import org.ssh.network.transmit.radio.consumers.RadioPacketSender;
import org.ssh.network.receive.receivers.UDPReceiver;
import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.util.Logger;

import com.google.protobuf.GeneratedMessage;

import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * Manages all networkconnections. Controls all {@link UDPReceivers}. This class also acts as a
 * interface for a {@link RadioPacketSender}.
 * 
 * @author Jeroen de Jong
 *         
 */
@AvailableInLua
public class Network implements Manager<AbstractService<? extends ProtoPacket<? extends GeneratedMessage>>> {
    
    /**
     * The network store has a controller that runs the store.
     */
    private static NetworkController networkController;
    /** The Constant LOG. */
    // respective logger
    private static final Logger      LOG      = Logger.getLogger();
    /** The instance. */
    private static final Object      instance = new Object();
                                              
    /**
     * Add one or multiple sendmethods as default in the {@link RadioPacketSender}
     * 
     * @param newSendMethods
     *            new sendmethod(s)
     * @return succes value
     */
    public static boolean addDefault(final SendMethod... newSendMethods) {
        return networkController.addDefault(newSendMethods);
    }
    
    /**
     * Gets the Singleton instance of Models.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Network.instance;
    }
    
    /**
     * Listen for a specific {@link ProtoPacket<?> ProtoPacket}. Note that all networksettings are
     * dynamically loaded by the {@link UDPReceiver} upon initialization based on the parameter
     * type.
     * 
     * @param type
     *            type to listen for
     */
    public static <M extends GeneratedMessage, P extends ProtoPacket<M>> void listenFor(Class<P> type) {
        networkController.listenFor(type);
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
    public static boolean register(final SendMethod key, final SenderInterface communicator) {
        return networkController.register(key, communicator);
    }
    
    /**
     * Removes this sendmethod as a default in {@link RadioPacketSender}
     * 
     * @param method
     *            SendMethod to remove as default
     * @return succes value
     */
    public static boolean removeDefault(final SendMethod method) {
        return networkController.removeDefault(method);
    }
    
    /**
     * Start a {@link RadioPipeline}, {@link RadiopacketSender} and instantiates a
     * {@link NetworkController}
     */
    public static void start() {
        Network.LOG.info("Starting Network...");
        
        // create pipeline
        new RadioPipeline("communication");
        
        // create 'the' sender
        new RadioPacketSender();
        
        // create networkController
        Network.networkController = new NetworkController();
        
    }
    
    /**
     * Kills the {@link UDPReceiver} for given type
     * 
     * @param type
     *            type to stop listening for
     */
    public static <M extends GeneratedMessage, P extends ProtoPacket<M>> void stopListening(Class<P> type) {
        networkController.stopListening(type);
    }
    
    /**
     * Builds a {@link RadioProtocolWrapper} from all commands supplied and tries to send through
     * {@link #send(com.google.protobuf.GeneratedMessage.Builder)}
     * 
     * @param commands
     *            Array with {@link RadioProtocolCommand} to send
     * @return success value
     */
    public static boolean transmit(final ArrayList<Radio.RadioProtocolCommand.Builder> commands) {
        // create a wrapper-builder
        final RadioProtocolWrapper.Builder wrapperBuilder = Radio.RadioProtocolWrapper.newBuilder();
        // and add all commands to the wrapper-builder
        commands.stream().forEach(command -> wrapperBuilder.addCommand(command));
        return transmit(wrapperBuilder);
    }
    
    /**
     * Send a packet alternatively through specified sendmethods
     * 
     * @param genericBuilder
     *            Packet to send
     * @param sendMethods
     *            sendmethods that should be used instead of the default ones
     * @return success value
     */
    public static boolean transmit(final protobuf.Radio.RadioProtocolWrapper.Builder genericBuilder,
            final SendMethod... sendMethods) {
        return networkController.transmit(genericBuilder, sendMethods);
    }
    
    /**
     * Wraps the command and sends it trough
     * {@link #transmit(protobuf.Radio.RadioProtocolWrapper.Builder, SendMethod...)}
     * 
     * @param command
     *            {@link RadioProtocolCommand} to send
     * @param methods
     *            sendmethods that should be used instead of the default ones
     * @return success value
     */
    public static boolean transmit(final RadioProtocolCommand.Builder command, final SendMethod... methods) {
        return transmit(RadioProtocolWrapper.newBuilder().addCommand(command), methods);
    }
    
    /**
     * Unregisters a hook to a {@link SendMethod}
     * 
     * @param sendmethod
     *            method to unhook
     * @return succes value
     */
    public static boolean unregister(final SendMethod sendmethod) {
        return networkController.unregister(sendmethod);
    }
    
    /**
     * Private constructor to hide the implicit public one.
     */
    private Network() {
    }
    
}
