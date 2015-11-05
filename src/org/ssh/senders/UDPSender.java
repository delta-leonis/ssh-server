package org.ssh.senders;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

import org.ssh.services.producers.Communicator;

import com.google.protobuf.Message;

/**
 * Implements a way to send a Protobuf {@link Message} over {@link SendMethod UDP} to given IPaddress and port
 * 
 * @author Jeroen
 * @see {@link Communicator}
 *
 */
public class UDPSender implements SenderInterface{
	private Logger 			logger = Logger.getLogger(UDPSender.class.toString());

	private InetAddress 	ipAddress;
	private int 			port;
	private DatagramSocket 	socket;

	/**
	 * Creates a {@link DatagramSocket} to be used for {@link #send(Message)} method
	 * 
	 * @param ip	ip to send to
	 * @param port	port to send to
	 */
	public UDPSender(String ip, int port)  {
		try{
			//set fields
			this.port 		= port;
			this.ipAddress	= InetAddress.getByName(ip);
		
			//create socket
			socket = new DatagramSocket();

		} catch(Exception e){
			logger.severe("Could not create DatagramSocket.");
		}
	} 

	/**
	 * {@inheritDoc}<br />
	 * Creates a {@link DatagramPacket} from given {@link Message}, and tries to send the message.
	 */
	@Override
	public boolean send(Message genericMessage) {
		try {

			DatagramPacket UDPpacket = new DatagramPacket(genericMessage.toByteArray(), genericMessage.getSerializedSize(), ipAddress, port);
			socket.send(UDPpacket);
			logger.info("Message has been sent over UDP");
			return true;

		} catch (IOException e) {
			logger.warning(String.format("Could not send packet to %s:%d.\n", ipAddress, port));
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * Closes the socket used for sending UDP packets
	 */
	@Override
	public boolean unregister(){
		if(socket != null){
			socket.close();

			return socket.isClosed();
		}
		else return true;
	}
	
}
