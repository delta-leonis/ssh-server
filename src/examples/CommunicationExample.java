package examples;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.SendMethod;
import org.ssh.network.transmit.radio.couplers.RoundCoupler;
import org.ssh.network.transmit.senders.DebugSender;
import org.ssh.network.transmit.senders.UDPSender;
import protobuf.Radio.RadioProtocolCommand;

import java.util.logging.Level;

public class CommunicationExample {

    public static void main(final String[] args) {
        // make models available
        Models.start();
        // make services available
        Services.start();
        // make network available
        Network.start();

        //register some transmit
        Network.register(SendMethod.UDP, new UDPSender("192.168.1.10", 1337));
        Network.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
        // add another default sendmethod
        Network.addDefault(SendMethod.DEBUG); // at this moment both UDP and DEBUG are default

        //instansiate a coupler that rounds the values
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
