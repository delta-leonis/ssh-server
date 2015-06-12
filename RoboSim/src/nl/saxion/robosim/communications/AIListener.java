package nl.saxion.robosim.communications;

import nl.saxion.robosim.model.AiData;
import nl.saxion.robosim.model.AiRobot;
import nl.saxion.robosim.model.Model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * The AIListener listens to input from the Artificial Intelligence and parses the data for the {@link Model}.
 *
 * Created by Damon & Joost van Dijk on 20-5-2015.
 *
 * @author Damon Daalhuisen
 * @author Joost van Dijk
 */
public class AIListener extends Thread {
    Model model;
    private byte[] lastMessage;
    private MulticastSocket socket;
    private InetAddress group;
    private DatagramPacket packet;
    private ArrayList<byte[]> messages;
    long time = System.currentTimeMillis();


    /**
     * Initialize connections
     * @throws IOException
     */
    public AIListener() throws IOException {
        model = Model.getInstance();
        // The port from ssl 10002
        socket = new MulticastSocket(1337);
        // The address from ssl 224.5.23.2
        group = InetAddress.getByName("224.5.23.20");
        socket.joinGroup(group);
        System.out.println("start listening");
    }

    /**
     * Receive the package
     * FIXME The loop is never stopped
     */
    @Override
    public void run() {
        while (true) {
            if (System.currentTimeMillis() - time > 10000) {
                System.out.println(Model.getInstance().getAiData().size());
                time = System.currentTimeMillis();
            }
            int length = 1024;
            byte[] buffer = new byte[length];
            byte[] data;
            packet = new DatagramPacket(buffer, length);

            try {
                socket.receive(packet);
                data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                //System.out.println("UDPserver " + data.length + " bytes received");

                lastMessage = data;
                AiData info = new AiData(data);
                System.out.println(info);

               // AiRobot r = model.getAiRobots().get(info.getRobotID());
                for(AiRobot r : model.getAiRobots()) {
                    if(r.getId() == info.getRobotID()) {
                        r.setDribble(info.getDribble());
                        r.setOrientation(info.getDirection());
                        r.setRotationSpeed(info.getRotationSpeed());
                        r.setVelocity(info.getDirectionSpeed());
                        r.setShootkicker(info.getShootKicker());
                    }
                }




            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    public ArrayList<byte[]> getMessages() {
        return messages;
    }

    public byte[] getLastMessage() {
        return lastMessage;
    }

}



