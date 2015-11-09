package examples;

import java.util.logging.Level;

import org.ssh.Models;
import org.ssh.Services;
import org.ssh.models.enums.SendMethod;
import org.ssh.senders.DebugSender;
import org.ssh.senders.UDPSender;
import org.ssh.services.consumers.RadioPacketConsumer;
import org.ssh.services.couplers.RoundCoupler;
import org.ssh.services.pipeline.pipelines.RadioPipeline;
import org.ssh.services.producers.Communicator;

import protobuf.Radio.RadioProtocolCommand;

public class CommunicationExample {
    
    public static void main(final String[] args) {
        // make models available
        Models.start();
        // make org.ssh.services available
        Services.start();
        // create a comminucation org.ssh.services.pipeline
        Services.addPipeline(new RadioPipeline("communication org.ssh.services.pipeline"));
        // create communicator (producer for RadioPackets)
        Services.addService(new Communicator());
        
        final RadioPacketConsumer radioConsumer = new RadioPacketConsumer();
        radioConsumer.register(SendMethod.UDP, new UDPSender("192.168.1.10", 1337));
        radioConsumer.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
        radioConsumer.addDefault(SendMethod.DEBUG); // at this moment both UDP and DEBUG are default
        
        // add a consumer voor radiopackets
        Services.getPipeline("communication org.ssh.services.pipeline").get().registerConsumer(radioConsumer);
        
        Services.getPipeline("communication org.ssh.services.pipeline").get().registerCoupler(new RoundCoupler());
        
        // create an example packet that will be processed and send
        final RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f)
                .setVelocityX(4.234f).setVelocityY(92.939320f);
                
        // retrieve the communicator from org.ssh.services
        final Communicator comm = (Communicator) Services.get("communicator").get();
        // send a packet with the default sendmethods
        comm.send(packet);
        // send a packet with specifeid sendmethods
        comm.send(packet, SendMethod.BLUETOOTH, SendMethod.DEBUG);
        
    }
}
