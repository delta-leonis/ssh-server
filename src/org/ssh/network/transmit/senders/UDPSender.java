package org.ssh.network.transmit.senders;

import com.google.protobuf.Message;
import org.ssh.models.enums.SendMethod;
import org.ssh.util.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Implements a way to send a Protobuf {@link Message} over {@link SendMethod UDP} to given
 * IPaddress and port
 *
 * @author Jeroen de Jong
 */
public class UDPSender implements SenderInterface {

    // respective logger
    private static final Logger LOG = Logger.getLogger();

    /**
     * IP address to send the packets to
     */
    private InetAddress ipAddress;
    /**
     * port that should be used for communication
     */
    private int port;
    /**
     * Socket that maintains the open connection
     */
    private DatagramSocket socket;

    /**
     * Creates a {@link DatagramSocket} to be used for {@link #send(Message)} method
     *
     * @param ip   ip to send to
     * @param port port to send to
     */
    public UDPSender(final String ip, final int port) {
        try {
            // set fields
            this.port = port;
            this.ipAddress = InetAddress.getByName(ip);

            // create socket
            this.socket = new DatagramSocket();

        } catch (final Exception exception) {
            UDPSender.LOG.exception(exception);
            UDPSender.LOG.warning("Could not create DatagramSocket.");
        }
    }

    /**
     * {@inheritDoc}<br />
     * Creates a {@link DatagramPacket} from given {@link Message}, and tries to send the message.
     */
    @Override
    public boolean send(final Message genericMessage) {
        try {

            final DatagramPacket udpPacket = new DatagramPacket(genericMessage.toByteArray(),
                    genericMessage.getSerializedSize(),
                    this.ipAddress,
                    this.port);
            this.socket.send(udpPacket);
            UDPSender.LOG.fine("Message has been sent over UDP.");
            return true;

        } catch (final IOException exception) {
            UDPSender.LOG.exception(exception);
            UDPSender.LOG.warning("Could not send packet to %s:%d.", this.ipAddress, this.port);
            return false;
        }
    }

    /**
     * {@inheritDoc} Closes the socket used for sending UDP packets
     */
    @Override
    public boolean unregister() {
        if (this.socket != null) {
            this.socket.close();

            return this.socket.isClosed();
        } else
            return true;
    }

}
