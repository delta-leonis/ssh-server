package org.ssh.managers.controllers;

import java.util.Optional;

import org.ssh.managers.ManagerController;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.senders.SenderInterface;
import org.ssh.services.Service;
import org.ssh.services.consumers.RadioPacketSender;
import org.ssh.services.producers.UDPReceiver;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;

public class NetworkController extends ManagerController<Service<? extends ProtoPacket<? extends GeneratedMessage>>> {
    private RadioPipeline pipeline;
    private RadioPacketSender sender;
    
    public NetworkController () { 
        super();
        pipeline = (RadioPipeline) Pipelines.get("communication").orElse(null);
        sender = (RadioPacketSender) Services.get("RadioPacketSender").orElse(null);
    }

    public <M extends GeneratedMessage, P extends ProtoPacket<M>> void listenFor(Class<P> type) {
        this.add(new UDPReceiver(type).start());
    }
    
    public <M extends GeneratedMessage, P extends ProtoPacket<M>> void stopListening(Class<P> type){
        this.get(type.getName()).ifPresent(service -> service.stop());
    }
    
    /**
     * Creates a {@link RadioPacket} and puts it in the {@link Pipeline Pipeline<RadioPacket>}
     * 
     * @param genericBuilder
     *            a RadioWrapper Builder<?>
     * @param sendMethod
     *            SendMethod that will be used to send message
     */
    public boolean transmit(final protobuf.Radio.RadioProtocolWrapper.Builder genericBuilder,
            final SendMethod... sendMethods) {
        if(hasPipeline())
            return pipeline.addPacket(new RadioPacket(genericBuilder.build(), sendMethods)).processPacket();
        return false;
    }
    
    private boolean hasPipeline(){
        if(pipeline == null)
            pipeline = (RadioPipeline)Pipelines.get("communication").orElse(null);
         return pipeline != null;
    }    
    private boolean hasSender(){
        if(sender == null)
            sender = (RadioPacketSender)Services.get("RadioPacketConsumer").orElse(null);
         return sender != null;
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
        if(hasSender())
            return sender.addDefault(newSendMethods);
        return false;
    }
    
    public boolean removeDefault(final SendMethod method) {
        if(hasSender())
            return sender.removeDefault(method);
        return false;
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
    public boolean register(final SendMethod key, final SenderInterface communicator) {
        if(hasSender())
            sender.register(key, communicator);
        return false;
    }
    
    /**
     * Unregisters a hook to a {@link SendMethod}
     * 
     * @param sendmethod
     *            method to unhook
     * @return succes value
     */
    public boolean unregister(final SendMethod sendmethod) {
        if(hasSender())
            sender.unregister(sendmethod);
        return false;
    }
}
