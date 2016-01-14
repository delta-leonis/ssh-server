package org.ssh.network.transmit.senders;

import com.google.protobuf.Message;
import org.ssh.models.enums.SendMethod;
import org.ssh.util.Logger;
import protobuf.Radio.RadioProtocolWrapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Implements a way to send a Legacy {@link Message} over {@link SendMethod UDP} to given
 * IPaddress and port
 *
 * @author Thomas Hakkers
 */
public class LegacyUDPSender implements SenderInterface {

    // respective logger
    private static final Logger LOG = Logger.getLogger();

    /** max kickspeed in percent/100 */
    private static final double MAX_KICKSPEED = 0.7;

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
    public LegacyUDPSender(final String ip, final int port) {
        try {
            // set fields
            this.port = port;
            this.ipAddress = InetAddress.getByName(ip);

            // create socket
            this.socket = new DatagramSocket();

        } catch (final Exception exception) {
            LegacyUDPSender.LOG.exception(exception);
            LegacyUDPSender.LOG.warning("Could not create DatagramSocket.");
        }
    }

    /**
     * {@inheritDoc}<br />
     * Creates a {@link DatagramPacket} from given {@link Message},
     * converts it into the legacy protocol and sends it out.
     */
    @Override
    public boolean send(final Message genericMessage) {
            if (!(genericMessage instanceof RadioProtocolWrapper)) {
                LegacyUDPSender.LOG.warning("Incoming message not of type RadioProtocolWrapper");
                return false;
            }
            // Get the command we're converting
            return ((RadioProtocolWrapper) genericMessage).getCommandList().stream().map(command -> {
                try {
                    // Save these variables for further use
                    float x = command.getVelocityX();
                    float y = command.getVelocityY();
                    // Convert the values according to old protocol
                    int messageType = 1;
                    int robotID = command.getRobotId();
                    int direction = (int) Math.toDegrees(Math.atan(y / x));

                    if (x < 0)
                        direction += 180;

                    direction *= -1;
                    direction += 90;

                    int directionSpeed = (int) Math.sqrt(x * x + y * y);
                    int rotationSpeed = (int) command.getVelocityR();
                    int shootKicker = (int) command.getFlatKick();
                    if(command.getChipKick() > 0f)
                        shootKicker = (int) (command.getChipKick() * -1);

                    boolean dribble = Math.abs(command.getDribblerSpin()) > 0.0f;

                    // Put the values into a datagrampacket
                    byte[] dataPacket = LegacyUDPSender.createByteArray(messageType, robotID, direction, directionSpeed, rotationSpeed, (int) (shootKicker * MAX_KICKSPEED), dribble);
                    final DatagramPacket udpPacket = new DatagramPacket(dataPacket,
                            dataPacket.length,
                            this.ipAddress,
                            this.port);
                    // And send it
                    this.socket.send(udpPacket);
                    LegacyUDPSender.LOG.info("Message has been sent over UDP using the legacy protocol.");
                    return true;
                } catch (final IOException exception) {
                    LegacyUDPSender.LOG.exception(exception);
                    LegacyUDPSender.LOG.warning("Could not send packet to %s:%d.", this.ipAddress, this.port);
                return false;
               }
            }).reduce(true, (accumulator, success) -> success & accumulator);
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

    /**
     * @param messageType,   MessageType 0 => robot instruction
     * @param robotID        The ID of the robot we want to send to.
     * @param direction      The direction we want our robot to move towards.
     * @param directionSpeed The speed we want our robot to move at in mm/s
     * @param rotationSpeed  The speed we want our robot to turn at in mm/s
     * @param shootKicker         -1 to -100 for chipping, 1 to 100 for kicking.
     * @param dribble        true to start the dribbler, false otherwise
     * @return bytearray to send to the Basestation
     */
    private static byte[] createByteArray(int messageType, int robotID, int direction, int directionSpeed,
                                          int rotationSpeed, int shootKicker, boolean dribble) {
        ByteBuffer dataBuffer = ByteBuffer.allocate(15);
        dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        dataBuffer.put((byte) messageType);
        dataBuffer.put((byte) robotID);
        dataBuffer.putShort((short) direction);
        dataBuffer.putShort((short) directionSpeed);
        dataBuffer.putShort((short) rotationSpeed);
        dataBuffer.put((byte) shootKicker);
        if (dribble) {
            dataBuffer.put((byte) 1);
        } else {
            dataBuffer.put((byte) 0);
        }
        addChecksum(dataBuffer);
        return dataBuffer.array();
    }

    /**
     * Calculates checksum and appends it to the {@link ByteBuffer}
     *
     * @param bytebuffer array that will be modified
     */
    private static void addChecksum(ByteBuffer bytebuffer) {
        int checksum = 0;
        for (int i = 0; i < bytebuffer.capacity() - 1; i++) {
            checksum ^= bytebuffer.get(i);
        }
        checksum &= 0xff;
        bytebuffer.put((byte) checksum);
    }

}
