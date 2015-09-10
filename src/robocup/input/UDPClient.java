package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

import robocup.Main;

/**
 * Abstract class UDPClient for receiving UDP packets via multicast group 
 * Method startListining has to be overridden
 */
public abstract class UDPClient implements Runnable {

	protected ProtoParser protoParser;
	private String multicastHost;
	private int multipcastPort;
	private MulticastSocket multicastSocket;
	private InetAddress multicastGroup;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Constructs a new UDP client
	 * @param multicasthost	current multicasthost
	 * @param multicastport	port to listen to
	 */
	public UDPClient(String multicasthost, int multicastport) {
		protoParser = ProtoParser.getInstance();
		this.multicastHost = multicasthost;
		this.multipcastPort = multicastport;
	}

	/**
	 * Connect when the Thread is started.
	 * When something fails the Thread is stopped.
	 */
	@Override
	public void run() {
		try {
			connect();
			startListening();
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
	}

	/**
	 * This method has to be override
	 * @throws IOException 
	 */
	public abstract void startListening() throws IOException;

	public void connect() throws IOException {
		multicastGroup = InetAddress.getByName(multicastHost);
		multicastSocket = new MulticastSocket(multipcastPort);
		multicastSocket.joinGroup(multicastGroup);
	}

	/**
	 * Leave the multicast group
	 */
	public void disconnect() {
		try {
			multicastSocket.leaveGroup(multicastGroup);
		} catch (IOException e) {
			LOGGER.warning("Disconnecting not possible:");
			LOGGER.warning(e.toString());
		}
	}

	/**
	 * Receive an UDP packet
	 * @return A ByteArrayInputStream of the received UDP Packet
	 * @throws IOException
	 */
	public ByteArrayInputStream receive() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(recv);
		ByteArrayInputStream input = new ByteArrayInputStream(buf, 0, recv.getLength());
		return input;
	}

	/**
	 * @return the multicasthost name
	 */
	public String getHost() {
		return multicastHost;
	}

	/**
	 * Changes the host name.
	 * @param multicasthost the multicasthost name to set
	 */
	public void setHost(String multicasthost) {
		this.multicastHost = multicasthost;
	}

	/**
	 * @return the multicastport
	 */
	public int getPort() {
		return multipcastPort;
	}

	/**
	 * Changes the port number.
	 * @param multicastport The new port number.
	 */
	public void setPort(int multicastport) {
		this.multipcastPort = multicastport;
	}

}