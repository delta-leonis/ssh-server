package examples;

import java.util.logging.Level;

import application.Models;
import application.Services;
import model.enums.SendMethod;
import output.RoundCoupler;
import output.Communicator;
import output.Debug;
import output.RadioPacketConsumer;
import output.UDPSender;
import pipelines.RadioPipeline;
import protobuf.Radio.RadioProtocolCommand;

public class CommunicationExample {
	public static void main(String[] args) {
		// make models available
		Models.start();
		// make services available
		Services.start();
		// create a comminucation pipeline
		Services.addPipeline(new RadioPipeline("communication pipeline"));
		// create communicator (producer for RadioPackets)
		Services.addService(new Communicator());
		
		RadioPacketConsumer radioConsumer = new RadioPacketConsumer();
		radioConsumer.register(SendMethod.UDP, new UDPSender("192.168.1.10", 1337));
		radioConsumer.register(SendMethod.DEBUG, new Debug(Level.INFO));
		radioConsumer.addDefault(SendMethod.DEBUG); //at this moment both UDP and DEBUG are default
		
		//add a consumer voor radiopackets
		Services.getPipeline("communication pipeline")
			.registerConsumer(radioConsumer);
		
		Services.getPipeline("communication pipeline")
			.registerCoupler(new RoundCoupler());
		

		//create an example packet that will be processed and send
		RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder()
				.setRobotId(4)
				.setVelocityR(0.2f)
				.setVelocityX(4.234f)
				.setVelocityY(92.939320f);
		
		//retrieve the communicator from services
		Communicator comm = (Communicator) Services.get("communicator");
		//send a packet with the default sendmethods
		comm.send(packet);
		//send a packet with specifeid sendmethods
		comm.send(packet, SendMethod.BLUETOOTH, SendMethod.DEBUG);
		
	}
}
