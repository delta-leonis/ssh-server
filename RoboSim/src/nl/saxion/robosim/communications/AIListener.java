package nl.saxion.robosim.communications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import nl.saxion.robosim.model.AiData;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.Settings;

/**
 * The AIListener listens to input from the Artificial Intelligence and parses the data for the {@link Model}.
 *
 * Created by Damon & Joost van Dijk on 20-5-2015.
 *
 * @author Damon Daalhuisen
 * @author Joost van Dijk
 * @author Kris Minkjan
 */
public class AIListener extends Thread {
    Model model;
    private MulticastSocket socket;
    long time = System.currentTimeMillis();
    private volatile boolean running = true;

    /**
     * Closes the socket and tries to terminate the Thread
     */
    public void terminate() {
        running = false;
        socket.close();
    }

    /**
     * Initialize connections
     * @throws IOException
     */
    public AIListener() throws IOException {
        model = Model.getInstance();
        Settings s = Settings.getInstance();
        // The port from ssl 10002
        socket = new MulticastSocket(Integer.parseInt(s.getIport()));
        // The address from ssl 224.5.23.2
        InetAddress group = InetAddress.getByName(s.getIip());
        socket.joinGroup(group);
//        System.out.println("Start Listening");
    }

    /**
     * Receive the package
     */
    @Override
    public void run() {
        while (running) {
            int length = 1024;
            byte[] buffer = new byte[length];
            byte[] data;
            DatagramPacket packet = new DatagramPacket(buffer, length);

            try {
                socket.receive(packet);
                data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                AiData info = new AiData(data);
//                System.out.println(info);

                // Update the AiRobots
                model.getAiRobots().stream().filter(r -> r.getId() == info.getRobotID()).forEach(r -> {
                    r.setDirection(info.getDirection());
                    r.setVelocity(info.getDirectionSpeed());
                });
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("Listener Terminated");
    }
}



