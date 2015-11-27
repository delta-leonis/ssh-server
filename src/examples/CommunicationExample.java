package examples;

import java.util.logging.Level;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.senders.DebugSender;
import org.ssh.senders.UDPSender;
import org.ssh.services.consumers.RadioPacketSender;
import org.ssh.services.couplers.RoundCoupler;
import protobuf.Radio.RadioProtocolCommand;

public class CommunicationExample {
    
    public static void main(final String[] args) {
        // make models available
        Models.start();
        // make services available
        Services.start();
        Network.start();
        

        Network.register(SendMethod.UDP, new UDPSender("192.168.1.10", 1337));
        Network.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
        Network.addDefault(SendMethod.DEBUG); // at this moment both UDP and DEBUG are default

        new RoundCoupler();
        
        // create an example packet that will be processed and send
        final RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f)
                .setVelocityX(4.234f).setVelocityY(92.939320f);

        // send a packet with the default sendmethods
        Network.transmit(packet);
        // send a packet with specifeid sendmethods
        Network.transmit(packet, SendMethod.BLUETOOTH, SendMethod.DEBUG);
        
    }
}
