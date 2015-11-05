package examples;

import java.util.logging.Level;

import org.ssh.managers.Models;
import org.ssh.managers.Services;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.RadioPipeline;
import org.ssh.senders.DebugSender;
import org.ssh.senders.UDPSender;
import org.ssh.services.consumers.RadioPacketConsumer;
import org.ssh.services.couplers.RoundCoupler;
import org.ssh.services.producers.Communicator;

import protobuf.Radio.RadioProtocolCommand;

public class CommunicationExample {
	public static void main(String[] args) {
		// make models available
		Models.start();
		// make org.ssh.services available
		Services.start();
		// create a comminucation org.ssh.services.pipeline
		Services.addPipeline(new RadioPipeline("communication org.ssh.services.pipeline"));
		// create communicator (producer for RadioPackets)
		Services.addService(new Communicator());
		
		RadioPacketConsumer radioConsumer = new RadioPacketConsumer();
		radioConsumer.register(SendMethod.UDP, new UDPSender("192.168.1.10", 1337));
		radioConsumer.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
		radioConsumer.addDefault(SendMethod.DEBUG); //at this moment both UDP and DEBUG are default
		
		//add a consumer voor radiopackets
		Services.getPipeline("communication org.ssh.services.pipeline")
			.registerConsumer(radioConsumer);
		
		Services.getPipeline("communication org.ssh.services.pipeline")
			.registerCoupler(new RoundCoupler());
		

		//create an example packet that will be processed and send
		RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder()
				.setRobotId(4)
				.setVelocityR(0.2f)
				.setVelocityX(4.234f)
				.setVelocityY(92.939320f);
		
		//retrieve the communicator from org.ssh.services
		Communicator comm = (Communicator) Services.get("communicator");
		//send a packet with the default sendmethods
		comm.send(packet);
		//send a packet with specifeid sendmethods
		comm.send(packet, SendMethod.BLUETOOTH, SendMethod.DEBUG);
		
	}
}
