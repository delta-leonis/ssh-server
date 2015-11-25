package org.ssh.services.producers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.ssh.managers.Manageable;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.NetworkSettings;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.ProtoPacket;

public class UDPReceiver extends Manageable {
    
    private NetworkSettings           networkSettings;
    private transient MulticastSocket multicastSocket;
    private InetAddress multicastGroup;
                                      
    public UDPReceiver(Class<? extends ProtoPacket<?>> packetType) {
        super("UDPReceiver");
        this.networkSettings = Models.create(NetworkSettings.class, packetType);
        UDPReceiver.LOG.info("Created UDPReceiver for %s.", packetType.getSimpleName());

        Services.submitTask(this.getName(), () -> {
            // wait around while no settings are present
            while (!networkSettings.isComplete())
                try {
                    UDPReceiver.LOG.fine("Settings for %s not yet complete.", packetType.getSimpleName());
                    Thread.sleep(3000);
                }
                catch (InterruptedException ignored) {
                }

            // try to connect
            if (this.connect())
                UDPReceiver.LOG.info("Connection to %s:%s (%s) succesful.", networkSettings.getIP(), networkSettings.getPort(), packetType.getSimpleName());
            else{
                UDPReceiver.LOG.warning("Connection to %s:%s (%s) FAILED.", networkSettings.getIP(), networkSettings.getPort(), packetType.getSimpleName());
                return;
            }

            ByteArrayInputStream input = null;
            //while (multicastSocket.isConnected()) {
            while(true){
                try {
                    input = this.receive();

                    if(input != null){
                        UDPReceiver.LOG.fine("Received %s.",packetType.getSimpleName());
                        //Create a packet based on packetType
                        ProtoPacket<?> packet = packetType.getDeclaredConstructor(input.getClass()).newInstance(input);
                        // get all pipelines
                        for(Pipeline<PipelinePacket<? extends Object>> pipe : Pipelines.getOfDataType(packetType))
                            //put it in the pipa :D
                            pipe.addPacket(packet).processPacket();
                    }
                }
                catch( NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception){
                    UDPReceiver.LOG.exception(exception);
                    exception.printStackTrace();
                    UDPReceiver.LOG.info("Could not parse data, does '%s(ByteArrayInputStream)' exist?", packetType.getSimpleName());
                }
                catch (IOException exception) {
                    UDPReceiver.LOG.exception(exception);
                    exception.printStackTrace();
                    UDPReceiver.LOG.warning("Could not maintain connection with %s (ip: %s). closing connection.",
                            networkSettings.getSuffix(),
                            networkSettings.getIP());
                    this.disconnect();
                    return;
                }
            }
        });
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
        catch (UnknownHostException exception){
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not resolve host %s.",
                    networkSettings.getIP());
        }
        catch(SecurityException exception){
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not join multicastgroup on %s:%s for %s.", 
                    networkSettings.getIP(), 
                    networkSettings.getPort(),
                    networkSettings.getSuffix());
        }
        catch (IOException exception){
            UDPReceiver.LOG.exception(exception);
            UDPReceiver.LOG.info("Could not open connection to %s:%s for %s",
                    networkSettings.getIP(),
                    networkSettings.getPort(),
                    networkSettings.getSuffix());
        }
        return false;
    }
    
    public boolean disconnect(){
        try { 
            if(multicastSocket != null){
                multicastSocket.leaveGroup(multicastGroup);
                multicastSocket.close();
            }
            return true;
        } catch (Exception exception){
            UDPReceiver.LOG.warning("Could not close connection gracefully (ip: %s).", networkSettings.getIP()); 
        }
        return false;
    }
}
