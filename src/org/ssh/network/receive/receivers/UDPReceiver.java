package org.ssh.network.receive.receivers;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import javafx.concurrent.Task;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.NetworkSettings;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.services.AbstractService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * This receiver class listens to a multicast IP as presented by the {@link NetworkSettings}, which
 * are dynamicly loaded by {@link Models} based on the type of {@link ProtoPacket ProtoPacket<?>}
 * this receiver is for. Every {@link ByteArrayInputStream} that is received by this class will
 * create a {@link ProtoPacket ProtoPacket<?>} of the type that is passed through to the constructor
 * of this class (@see {@link #UDPReceiver(Class)}). That packet will be put in all pipelines of the
 * type (of {@link ProtoPacket ProtoPacket<?>} as declared in the constructor.
 * 
 * 
 * @author Jeroen de Jong
 */
public class UDPReceiver extends AbstractService<ProtoPacket<?>> {
    
    /** Multicast group that this class connects to */
    private InetAddress                       multicastGroup;
    /** Multicast socket that this class uses */
    private MulticastSocket                   multicastSocket;
    /** Settings containing all information for this connection */
    private NetworkSettings                   networkSettings;
    /** Type of packet that is being listened for */
    private Type                              packetType;
    /** Type of message this packetType has */
    private Class<? extends GeneratedMessage> messageType;
    /** (cached) Parser for the the packetType<messageType> */
    private Parser<?>                         parser;
    /** (cached) Constructor to create a packet of PacketType<messageType> */
    private Constructor<ProtoPacket<?>>       packetConstructor;
                                              
    /**
     * Create a new instance of a UDP receiver that creates packets based on given type (
     * {@link ProtoPacket ProtoPacket<?>})
     * 
     * @param packetType
     *            packettype to create
     */
    @SuppressWarnings ("unchecked")
    public <M extends GeneratedMessage, P extends ProtoPacket<M>> UDPReceiver(Class<P> packetType) {
        super("UDPReceiver");
        this.packetType = packetType;
        // retreive messagetype from packettype based on generic super class
        
        try { // getting generic supertype
            this.messageType = (Class<M>) Class.forName(((ParameterizedType) packetType.getGenericSuperclass())
                    // get actual types for this generic superclass
                    .getActualTypeArguments()[0].getTypeName());
                    
            // get the method to create a default instance, invoke this method
            // and get the parser from the default instance.
            this.parser = ((M) messageType.getMethod("getDefaultInstance").invoke(null)).getParserForType();
        }
        catch (ReflectiveOperationException | SecurityException exception) {
            AbstractService.LOG.exception(exception);
            AbstractService.LOG.warning("Could not reflect messageType or the parser.");
            return;
        }
        
        try {
            // try to get the constructor that accepts messagetypes for a packettype
            packetConstructor = (Constructor<ProtoPacket<?>>) packetType.getDeclaredConstructor(messageType);
        }
        catch (ClassCastException | NoSuchMethodException | SecurityException exception) {
            AbstractService.LOG.exception(exception);
            AbstractService.LOG.warning(
                    "Wrong type as parameter, Type should extend ProtoPacket<P> and must inherit `ProtoPacket<P>(P data)` constructor.");
            return;
        }
        
        // create networkSettings
        this.networkSettings = Models.create(NetworkSettings.class, packetType);
        AbstractService.LOG.info("Created UDPReceiver for %s<%s>.", packetType.getSimpleName(), messageType.getTypeName());
    }
    
    /**
     * Create a socket based on data in {@link #networkSettings}, and connet to that socket as if it
     * is a multicast socket.
     * 
     * @return succes value
     */
    private boolean connect() {
        try {
            // create socket on port as provided by #networkSettings
            multicastSocket = new MulticastSocket(networkSettings.getPort());
            // get multicastGroup from IP string
            multicastGroup = InetAddress.getByName(networkSettings.getIP());
            // join group as previously determined
            multicastSocket.joinGroup(multicastGroup);
            return true;
        }
        catch (UnknownHostException exception) {
            AbstractService.LOG.exception(exception);
            AbstractService.LOG.info("Could not resolve host %s.", networkSettings.getIP());
        }
        catch (SecurityException exception) {
            AbstractService.LOG.exception(exception);
            AbstractService.LOG.info("Could not join multicastgroup on %s:%s for %s.",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    networkSettings.getPacketType().getTypeName());
        }
        catch (IOException exception) {
            AbstractService.LOG.exception(exception);
            AbstractService.LOG.info("Could not open connection to %s:%s for %s",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    networkSettings.getPacketType().getTypeName());
        }
        return false;
    }
    
    /**
     * Leave the {@link #multicastGroup multicast group}, and closes the {@link #multicastSocket
     * socket}
     * 
     * @return
     */
    public boolean disconnect() {
        try {
            // check if socket exists
            if (multicastSocket != null) {
                // leave group
                multicastSocket.leaveGroup(multicastGroup);
                // close socket
                multicastSocket.close();
            }
            return true;
        }
        catch (Exception exception) {
            AbstractService.LOG.warning("Could not close connection gracefully (ip: %s).", networkSettings.getIP());
            AbstractService.LOG.exception(exception);
        }
        return false;
    }
    
    /**
     * Receive a new packet
     * 
     * @return new packet
     * @throws IOException
     *             when connection is suddenly lost
     */
    private ByteArrayInputStream receive() throws IOException {
        // create buffer
        byte[] buffer = new byte[networkSettings.getBufferSize()];
        // reserve datagrampacket
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // receive packet from open socket
        multicastSocket.receive(packet);
        // convert to a ByteArrayInputStream
        return new ByteArrayInputStream(buffer, 0, packet.getLength());
    }
    
    /**
     * Creates the connection according to information in {@link #networkSettings}, and submits a
     * {@link Task} to {@link Services} containing a while-loop that creates {@link ProtoPacket
     * ProtoPacket<?>s} of the type that is specified in the constructor (
     * {@link #UDPReceiver(Class)}) and puts them in the communication pipeline.
     * 
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings ("unchecked")
    public <S extends AbstractService<?>> S start() {
        // check if connection is possible
        if (!networkSettings.isComplete()) {
            AbstractService.LOG.warning("Settings for UDPReceiver(%s) not yet complete.", packetType.getTypeName());
            return (S) this;
        }
        
        Services.submitTask(packetType.getTypeName(), () -> {
            // try to connect
            if (!this.connect()) AbstractService.LOG.warning("Connection to %s:%s (%s) FAILED.",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    packetType.getTypeName());
                    
            // define input variable
            ByteArrayInputStream input = null;
            
            AbstractService.LOG.info("Started listening on %s:%d", networkSettings.getIP(), networkSettings.getPort());
            
            // while this connection should not be closed
            while (!networkSettings.isClosed()) {
                try {
                    // receive a new packet
                    input = this.receive();
                    
                    if (input != null) {
                        AbstractService.LOG.fine("Received %s.", packetType.getTypeName());
                        // Create a packet based on packetType
                        ProtoPacket<?> packet = packetConstructor.newInstance(parser.parseFrom(input));
                        // put it in the pipa :D
                        Pipelines.getOfDataType(packetType).forEach(pipe -> pipe.addPacket(packet).processPacket());
                    }
                }
                catch (ReflectiveOperationException exception) {
                    AbstractService.LOG.exception(exception);
                    AbstractService.LOG.info("Could not parse data, does '%s(ByteArrayInputStream)' exist?",
                            packetType.getTypeName());
                }catch (InvalidProtocolBufferException exception){
                    AbstractService.LOG.exception(exception);
                    AbstractService.LOG.info("Truncated message that caused an InvalidProtocolBufferException.");
                } catch (IOException exception) {
                    AbstractService.LOG.exception(exception);
                    AbstractService.LOG.warning("Could not maintain connection with %s (ip: %s). closing connection.",
                            networkSettings.getPacketType().getTypeName(),
                            networkSettings.getIP());
                    networkSettings.update("closed", Boolean.TRUE);
                }
            }
            // disconnect as soon as the socket is closed
            this.disconnect();
        });
        return (S) this;
    }
}
