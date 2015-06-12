package nl.saxion.robosim.communications;

import nl.saxion.robosim.model.protobuf.SslDetection;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslReferee;
import nl.saxion.robosim.model.protobuf.SslWrapper;
import nl.saxion.robosim.model.protobuf.SslWrapper.SSL_WrapperPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Sends udp messages to the given ip and port.
 * Created by Damon & Joost van Dijk on 20-5-2015.
 *
 * @author Damon Daalhuisen
 * @author Joost van Dijk
 */
public class MultiCastServer {

    MulticastSocket aiSocket, refSocket;
    InetAddress aiGroup, refGroup;
    // The address from ssl 224.5.23.2
    private final static String aiIp = "224.5.23.2", refIp = "224.5.23.1";
    // The port from ssl 10002
    private final static int aiPort = 10002, refPort = 10003;


    /**
     * Constructor, initialises connection on given ip and port.
     *
     * @throws IOException
     */
    public MultiCastServer() throws IOException {
        aiSocket = new MulticastSocket(aiPort);
        aiGroup = InetAddress.getByName(aiIp);
        aiSocket.joinGroup(aiGroup);
        refGroup = InetAddress.getByName(refIp);
        refSocket = new MulticastSocket(refPort);
        refSocket.joinGroup(refGroup);
    }

    /**
     * continuously gets data from the model, and sends it.
     */
    public void send(SslWrapper.SSL_WrapperPacket p, SslReferee.SSL_Referee referee) {
        System.out.println("Sending...\n" + p + "\n" + referee);

        SSL_DetectionFrame frame = p.getDetection();
        SSL_DetectionFrame.Builder[] frameBuilders = new SSL_DetectionFrame.Builder[4];
        for (int i = 0; i < frameBuilders.length; i++) {
            frameBuilders[i] = SSL_DetectionFrame
                    .newBuilder()
                    .setFrameNumber(frame.getFrameNumber())
                    .setTCapture(frame.getTCapture())
                    .setTSent(frame.getTSent())
                    .setCameraId(i);
        }

        for (SslDetection.SSL_DetectionBall ball : frame.getBallsList()) {
            float ballX = ball.getX();
            float ballY = ball.getY();
            int cameraId = getQuadrant(ballX, ballY);
            frameBuilders[cameraId] = frameBuilders[cameraId].addBalls(ball);
        }

        for (SslDetection.SSL_DetectionRobot blueRobot : frame.getRobotsBlueList()) {
            float robotX = blueRobot.getX();
            float robotY = blueRobot.getY();
            int cameraId = getQuadrant(robotX, robotY);
            frameBuilders[cameraId] = frameBuilders[cameraId].addRobotsBlue(blueRobot);
        }

        for (SslDetection.SSL_DetectionRobot yellowRobot : frame.getRobotsYellowList()) {
            float robotX = yellowRobot.getX();
            float robotY = yellowRobot.getY();
            int cameraId = getQuadrant(robotX, robotY);
            frameBuilders[cameraId] = frameBuilders[cameraId].addRobotsYellow(yellowRobot);
        }

        try {
            for (int i = 0; i < frameBuilders.length; i++) {
                byte[] bytePackage = SSL_WrapperPacket.newBuilder()
                        .setDetection(frameBuilders[i].build())
                        .setGeometry(p.getGeometry())
                        .build()
                        .toByteArray();

                DatagramPacket packet = new DatagramPacket(bytePackage, bytePackage.length, aiGroup, aiPort);
                aiSocket.send(packet);
            }
            byte[] array = referee.toByteArray();
            refSocket.send(new DatagramPacket(array, array.length, refGroup, refPort));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getQuadrant(float x, float y) {
        if (x < 0) {
            if (y > 0) {
                return 0;
            } else {
                return 3;
            }
        } else {
            if (y > 0) {
                return 2;
            } else {
                return 1;
            }
        }
    }
}




