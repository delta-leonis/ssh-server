package examples;

import java.net.UnknownHostException;

import jssc.SerialPort;
import model.enums.BaudRate;
import model.enums.SendMethod;
import output.Communicator;
import output.UDPSender;
import output.USBSender;
import protobuf.Radio.RadioProtocolCommand;

/**
 * Demonstrates usage of the Communicator.class<br>
 * See in-code comments for explanation of all dem magic.<br>
 * 
 * @author Jeroen
 * @see Communicator
 * @see SenderInterface
 * @see UDPSender
 * @see USBSender
 * @see SendMethod
 * @see BaudRate
 * @see SerialPort 
 *
 */
public class CommunicatorExample {
	public static void main(String[] args) throws UnknownHostException {
		//construct a minimal protobuf message
		RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f).setVelocityX(4.0f).setVelocityY(9293932.0f);

		//Let's create and register a new SenderInterface to a SendMethod
		//note how it will set the Communicator.sendMethod to be the first one in the list
		Communicator.register(SendMethod.UDP, new UDPSender("127.0.0.1", 9292));
		
		//here we reregister the SendMethod.UDP
		Communicator.register(SendMethod.UDP, new UDPSender("127.0.0.1", 2222));
		
		//also register an implementation for SendMethod.USB
		Communicator.register(SendMethod.USB, new USBSender("COM3", BaudRate.BAUD_115200));

		//send the packet to both UDP and USB implementation
		Communicator.send(packet, SendMethod.UDP, SendMethod.USB);
		
		//this will send the packet with Communicator.getSendMethod as method
		Communicator.send(packet);
		
		//since SendMethod.HOMING_PIGEON isn't implemented, the packet will only be
		//send to UDP and USB
		Communicator.send(packet, SendMethod.HOMING_PIGEON, SendMethod.UDP, SendMethod.USB);

		//unregister USB
		Communicator.unregister(SendMethod.USB);
	}
}
