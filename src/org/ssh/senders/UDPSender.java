package org.ssh.senders;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

import org.ssh.models.enums.SendMethod;
import org.ssh.services.producers.Communicator;

import com.google.protobuf.Message;

/**
 * Implements a way to send a Protobuf {@link Message} over {@link SendMethod UDP} to given
 * IPaddress and port
 *
 * @author Jeroen
 * @see {@link Communicator}
 *     
 */
public class UDPSender implements SenderInterface {
    
    private final Logger   logger = Logger.getLogger(UDPSender.class.toString());
                                  
    private InetAddress    ipAddress;
    private int            port;
    private DatagramSocket socket;
                           
    /**
     * Creates a {@link DatagramSocket} to be used for {@link #send(Message)} method
     * 
     * @param ip
     *            ip to send to
     * @param port
     *            port to send to
     */
    public UDPSender(final String ip, final int port) {
        try {
            // set fields
            this.port = port;
            this.ipAddress = InetAddress.getByName(ip);
            
            // create socket
            this.socket = new DatagramSocket();
            
        }
        catch (final Exception e) {
            this.logger.severe("Could not create DatagramSocket.");
        }
    }
    
    /**
     * {@inheritDoc}<br />
     * Creates a {@link DatagramPacket} from given {@link Message}, and tries to send the message.
     */
    @Override
    public boolean send(final Message genericMessage) {
        try {
            
            final DatagramPacket UDPpacket = new DatagramPacket(genericMessage.toByteArray(),
                    genericMessage.getSerializedSize(),
                    this.ipAddress,
                    this.port);
            this.socket.send(UDPpacket);
            this.logger.info("Message has been sent over UDP");
            return true;
            
        }
        catch (final IOException e) {
            this.logger.warning(String.format("Could not send packet to %s:%d.\n", this.ipAddress, this.port));
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
        }
        else
            return true;
    }
    
}
