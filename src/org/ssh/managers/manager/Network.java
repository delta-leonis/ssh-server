package org.ssh.managers.manager;

import java.util.ArrayList;

import org.ssh.managers.Manager;
import org.ssh.managers.controllers.NetworkController;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.senders.SenderInterface;
import org.ssh.services.Service;
import org.ssh.services.consumers.RadioPacketSender;
import org.ssh.util.Logger;

import com.google.protobuf.GeneratedMessage;

import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

public class Network implements Manager<Service<? extends ProtoPacket<? extends GeneratedMessage>>>{
    /**
     * The network store has a controller that runs the store.
     */
    private static NetworkController networkController;
    /** The Constant LOG. */
    // respective logger
    private static final Logger    LOG      = Logger.getLogger();
    /** The instance. */
    private static final Object    instance = new Object();
    

    /**
     * Gets the Singleton instance of Models.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Network.instance;
    }
    
    /**
     * Private constructor to hide the implicit public one.
     */
    private Network() { }
    
    public static void start() {
        Network.LOG.info("Starting Network...");
        
        // create pipeline 
        new RadioPipeline("communication");

        //create 'the' sender
        new RadioPacketSender();

        // create networkController
        Network.networkController = new NetworkController();

    }

    public static <M extends GeneratedMessage, P extends ProtoPacket<M>>  void listenFor(Class<P> type) {
        networkController.listenFor(type);
    }

    public static <M extends GeneratedMessage, P extends ProtoPacket<M>>  void stopListening(Class<P> type) {
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
   
    
    public static boolean transmit(final RadioProtocolCommand.Builder command, final SendMethod... methods) {
        return transmit(RadioProtocolWrapper.newBuilder().addCommand(command), methods);
    }
    
    public static boolean transmit(final protobuf.Radio.RadioProtocolWrapper.Builder genericBuilder,
            final SendMethod... sendMethods) {
        return networkController.transmit(genericBuilder, sendMethods);
    }

    public static boolean addDefault(final SendMethod... newSendMethods) {
        return networkController.addDefault(newSendMethods);
    }
    
    public static boolean removeDefault(final SendMethod method) {
        return networkController.removeDefault(method);
    }
    
    public static boolean register(final SendMethod key, final SenderInterface communicator) {
        return networkController.register(key, communicator);
    }

    public static boolean unregister(final SendMethod sendmethod) {
        return networkController.unregister(sendmethod);
    }

    

}
