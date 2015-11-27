package org.ssh.services.producers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.NetworkSettings;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.services.Service;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;

public class UDPReceiver extends Service<ProtoPacket<?>> {
    
    private InetAddress                 multicastGroup;
    private NetworkSettings             networkSettings;
    private MulticastSocket             multicastSocket;
    private Type                        packetType, messageType;
    private Parser<?>                   parser;
    private Constructor<ProtoPacket<?>> packetConstructor;
                                        
    public <M extends GeneratedMessage, P extends ProtoPacket<M>> UDPReceiver(Class<P> packetType) {
        super("UDPReceiver");
        this.packetType = packetType;
        this.messageType = ((ParameterizedType) packetType.getGenericSuperclass()).getActualTypeArguments()[0];
        this.networkSettings = Models.create(NetworkSettings.class, packetType);
        UDPReceiver.LOG.info("Created UDPReceiver for %s<%s>.", packetType.getSimpleName(), messageType.getTypeName());
        try {
            packetConstructor = (Constructor<ProtoPacket<?>>) packetType
                    .getDeclaredConstructor(Class.forName(messageType.getTypeName()));
        }
        catch (ClassCastException | NoSuchMethodException | SecurityException | ClassNotFoundException  exception) {
            UDPReceiver.LOG.exception(exception);
            exception.printStackTrace();
            UDPReceiver.LOG.warning(
                    "Wrong type as parameter, Type should extend ProtoPacket<P> and must inherit `ProtoPacket<P>(P data)` constructor.");
        }
    }
    
    @Override
    public Service<ProtoPacket<?>> start() {
        if (!networkSettings.isComplete()) {
            UDPReceiver.LOG.warning("Settings for UDPReceiver(%s) not yet complete.", packetType.getTypeName());
            return this;
        }
        
        Services.submitTask(packetType.getTypeName(), () -> {
            // try to connect
            if (!this.connect()) UDPReceiver.LOG.warning("Connection to %s:%s (%s) FAILED.",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    packetType.getTypeName());
                    
            ByteArrayInputStream input = null;
            
            UDPReceiver.LOG.info("Started listening on %s:%d", networkSettings.getIP(), networkSettings.getPort());
            while (!networkSettings.isClosed()) {
                try {
                    input = this.receive();
                    
                    if (input != null) {
                        UDPReceiver.LOG.fine("Received %s.", packetType.getTypeName());
                        // Create a packet based on packetType
                        ProtoPacket<?> packet = packetConstructor.newInstance(parser.parseFrom(input));
                        // put it in the pipa :D
                        Pipelines.getOfDataType(messageType).forEach(pipe -> pipe.addPacket(packet).processPacket());
                    }
                }
                catch (ReflectiveOperationException exception) {
                    UDPReceiver.LOG.exception(exception);
                    exception.printStackTrace();
                    UDPReceiver.LOG.info("Could not parse data, does '%s(ByteArrayInputStream)' exist?",
                            packetType.getTypeName());
                }
                catch (IOException exception) {
                    UDPReceiver.LOG.exception(exception);
                    exception.printStackTrace();
                    UDPReceiver.LOG.warning("Could not maintain connection with %s (ip: %s). closing connection.",
                            networkSettings.getSuffix(),
                            networkSettings.getIP());
                    networkSettings.update("closed", Boolean.TRUE);
                }
            }
            this.disconnect();
        });
        return this;
    }
    
    private ByteArrayInputStream receive() throws IOException {
        byte[] buffer = new byte[networkSettings.getBufferSize()];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(packet);
        ByteArrayInputStream input = new ByteArrayInputStream(buffer, 0, packet.getLength());
        return input;
    }
    
    private boolean connect() {
        try {
            multicastGroup = InetAddress.getByName(networkSettings.getIP());
            multicastSocket = new MulticastSocket(networkSettings.getPort());
            multicastSocket.joinGroup(multicastGroup);
            return true;
        }
        catch (UnknownHostException exception) {
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not resolve host %s.", networkSettings.getIP());
        }
        catch (SecurityException exception) {
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not join multicastgroup on %s:%s for %s.",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    networkSettings.getSuffix());
        }
        catch (IOException exception) {
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not open connection to %s:%s for %s",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    networkSettings.getSuffix());
        }
        return false;
    }
    
    public boolean disconnect() {
        try {
            if (multicastSocket != null) {
                multicastSocket.leaveGroup(multicastGroup);
                multicastSocket.close();
            }
            return true;
        }
        catch (Exception exception) {
            UDPReceiver.LOG.warning("Could not close connection gracefully (ip: %s).", networkSettings.getIP());
        }
        return false;
    }
}
