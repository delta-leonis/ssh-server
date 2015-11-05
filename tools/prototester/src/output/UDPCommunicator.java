package org.ssh.senders;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import protobuf.Radio.RadioProtocolWrapper;

public class UDPCommunicator {
	private InetAddress ipAddress;
	private int port;
	private DatagramSocket socket;

	public UDPCommunicator(InetAddress ip, int port) throws SocketException {
		this.port = port;
		this.ipAddress = ip;
		socket = new DatagramSocket();
	}

	public void send(RadioProtocolWrapper pbPacket, int timeout){
		System.out.printf("Sending wrapper with %d commands (total of %d bytes) to %s:%d\n", pbPacket.getCommandCount(), pbPacket.getSerializedSize(), ipAddress, port);
		DatagramPacket UDPpacket = new DatagramPacket(pbPacket.toByteArray(), pbPacket.getSerializedSize(), ipAddress, port);
		try {
			socket.send(UDPpacket);
			Thread.sleep(timeout);
		} catch (IOException e) {
			System.err.printf("Could not send packet to %s:%d.\n", ipAddress, port);
			//e.printStackTrace(System.err);
		} catch (InterruptedException e) {
			System.err.println("Interrupt error occured while waiting.");
		}
	}
}
