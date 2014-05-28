package output;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RobotCom implements ComInterface {
	private DatagramSocket serverSocket;
	private InetAddress ipAddress;
	private int port;

	public RobotCom(String ipAddress, int port) throws SocketException {
		try {
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = port;
		serverSocket = new DatagramSocket(port);
	}

	/**
	 * @see Output.ComInterface#send(int, int, int, int, int, int, int, boolean,
	 *      boolean)
	 * 
	 * 
	 */
	public void send(int messageType, int robotID, int direction, int directionSpeed, int travelDistance,
			int rotationAngle, int rotationSpeed, int shootKicker, boolean dribble) {
		
//		if(robotID == 0xB || robotID == 0x3){
//		System.out.println("Message:");
//		System.out.println("Robot: " + robotID);
//		System.out.println("Direction: " + direction);
//		System.out.println("DirectionSpeed: " + directionSpeed);
//		System.out.println("travelDistance: " + travelDistance);
//		System.out.println("rotationAngle: " + rotationAngle);
//		System.out.println("rotationSpeed: " + rotationSpeed);
//		System.out.println("shootKicker: " + shootKicker);
//		System.out.println("dribble: " + dribble);
//		}
		
		byte[] dataPacket = createByteArray(messageType, robotID, direction, directionSpeed, travelDistance,
				rotationAngle, rotationSpeed, shootKicker, dribble);
		DatagramPacket sendPacket = new DatagramPacket(dataPacket, dataPacket.length, ipAddress, port);

		try {
			serverSocket.send(sendPacket);
			// System.out.println("Send");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] createByteArray(int messageType, int robotID, int direction, int directionSpeed, int travelDistance,
			int rotationAngle, int rotationSpeed, int shootKicker, boolean dribble) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(15);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.put((byte)messageType);
		dataBuffer.put((byte) robotID);
		dataBuffer.putShort((short)direction);
		dataBuffer.putShort((short)directionSpeed);
		dataBuffer.putShort((short)travelDistance);
		dataBuffer.putShort((short)rotationAngle);
		dataBuffer.putShort((short)rotationSpeed);
		dataBuffer.put((byte)shootKicker);
		if(dribble){
			dataBuffer.put((byte)1);
		} else {
			dataBuffer.put((byte)0);
		}
		addChecksum(dataBuffer);
		return dataBuffer.array();
	}
	// byte[] dataPacket = { (byte) messageType, (byte) robotID };
	// // TODO: change this to bytebuffer
	// dataPacket = appendByteArray(dataPacket, intToTwoBytes(direction));
	// dataPacket = appendByteArray(dataPacket, intToTwoBytes(directionSpeed));
	// dataPacket = appendByteArray(dataPacket, intToTwoBytes(travelDistance));
	// dataPacket = appendByteArray(dataPacket, intToTwoBytes(rotationAngle));
	// dataPacket = appendByteArray(dataPacket, intToTwoBytes(rotationSpeed));
	// dataPacket = appendByteArray(dataPacket, new byte[] { (byte) shootKicker
	// });
	// if (dribble) {
	// dataPacket = appendByteArray(dataPacket, new byte[] { (byte) 1 });
	// } else {
	// dataPacket = appendByteArray(dataPacket, new byte[] { (byte) 0 });
	// }


	/**
	 * calculate checksum and add to array
	 * 
	 * @param array
	 * @return
	 */
	private void addChecksum(ByteBuffer bytebuffer) {
		int checksum = 0;
		for (int i = 0; i < bytebuffer.capacity() - 1; i++) {
			checksum ^= bytebuffer.get(i);
		}
		checksum &= 0xff;
		bytebuffer.put((byte) checksum);
	}

	/**
	 * Converts an integer to a bytearray of size 2
	 * 
	 * @param value
	 *            integer to convert
	 * @return
	 */
	private byte[] intToTwoBytes(int value) {
		byte[] array = new byte[2];
		array[1] = (byte) (value & 0xFF);
		array[0] = (byte) ((value >> 8) & 0xFF);

		return array;
	}

	/**
	 * Appends a bytearray with another bytearray
	 * 
	 * @param array
	 *            to be appended
	 * @param toAppend
	 *            array to be appened to the other array
	 * @return new combined bytearray
	 */
	private byte[] appendByteArray(byte[] array, byte[] toAppend) {
		byte[] combined = new byte[array.length + toAppend.length];

		System.arraycopy(array, 0, combined, 0, array.length);
		System.arraycopy(toAppend, 0, combined, array.length, toAppend.length);

		return combined;
	}
}
