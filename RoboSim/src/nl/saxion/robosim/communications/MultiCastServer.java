package nl.saxion.robosim.communications;

import nl.saxion.robosim.controller.SSL_Field;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.Settings;
import nl.saxion.robosim.model.protobuf.SslDetection;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslReferee;
import nl.saxion.robosim.model.protobuf.SslWrapper;
import nl.saxion.robosim.model.protobuf.SslWrapper.SSL_WrapperPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.TimeUnit;

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
    private static String aiIp = "224.5.23.2", refIp = "224.5.23.1";
    // The port from ssl 10002
    private static int aiPort = 10002, refPort = 10003;


    /**
     * Constructor, initialises connection on given ip and port.
     *
     * @throws IOException
     */
    public MultiCastServer() throws IOException {
        Settings s = Settings.getInstance();
        aiIp = s.getOip();
        aiPort = Integer.parseInt(s.getOport());

        refIp = s.getRefIp();
        refPort = Integer.parseInt(s.getRefPort());
        set(aiIp, aiPort, refIp, refPort);
    }

    /**
     * continuously gets data from the model, and sends it.
     */
    public void send(SslWrapper.SSL_WrapperPacket p, SslReferee.SSL_Referee referee) {
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
            if(validRobot(blueRobot))
                System.out.println("adding bluebot");
                frameBuilders[cameraId] = frameBuilders[cameraId].addRobotsBlue(blueRobot);
        }

        for (SslDetection.SSL_DetectionRobot yellowRobot : frame.getRobotsYellowList()) {
            float robotX = yellowRobot.getX();
            float robotY = yellowRobot.getY();
            int cameraId = getQuadrant(robotX, robotY);
            if(validRobot(yellowRobot))
                frameBuilders[cameraId] = frameBuilders[cameraId].addRobotsYellow(yellowRobot);
        }

        try {
            for (SSL_DetectionFrame.Builder frameBuilder : frameBuilders) {
                byte[] bytePackage = SSL_WrapperPacket.newBuilder()
                        .setDetection(frameBuilder.build())
                        .setGeometry(p.getGeometry())
                        .build()
                        .toByteArray();

                DatagramPacket packet = new DatagramPacket(bytePackage, bytePackage.length, aiGroup, aiPort);
                aiSocket.send(packet);
            }
            if (referee != null ) {
                byte[] array = referee.toByteArray();
                refSocket.send(new DatagramPacket(array, array.length, refGroup, refPort));
            }
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

    private boolean validRobot(SslDetection.SSL_DetectionRobot robot) {
        SSL_Field f = Model.getInstance().getSSLField();
        double x = f.getBench_real_y();
        double y = f.getBench_real_x();
        double width = f.getBench_width();
        double heigth = f.getBench_height();

//        System.out.println(x + " - " + y + " - " + width + " - " + heigth);
//        System.out.println("robot: " + robot.getX() + " - " + robot.getY());

        return robot.getX() != y;
    }

    public void set(String ip, int port, String refip, int refport) throws IOException {
        aiIp = ip;
        aiPort = port;
        refIp = refip;
        refPort = refport;

        aiSocket = new MulticastSocket();
        aiGroup = InetAddress.getByName(aiIp);
        aiSocket.joinGroup(aiGroup);
        refGroup = InetAddress.getByName(refIp);
        refSocket = new MulticastSocket(refPort);
        refSocket.joinGroup(refGroup);
    }
}




