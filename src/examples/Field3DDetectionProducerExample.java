package examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ssh.managers.manager.Pipelines;
import org.ssh.models.Robot;
import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.service.Producer;

import protobuf.Detection.DetectionBall;
import protobuf.Detection.DetectionFrame;
import protobuf.Detection.DetectionRobot;

/**
 * 3D Field Detection producer class. This class is responsible for generating 1000
 * {@link DetectionPacket DetectionPackets}, the last packet is being send twice.
 *
 * @see Producer
 * @author marklef2
 */
public class Field3DDetectionProducerExample extends Producer<DetectionPacket> {
    
    /** The number of robots per team. */
    private static int NUM_ROBOTS_PER_TEAM = 11;
    
    /**
     * Constructor. Instantiates a new {@link Field3DDetectionProducerExample}.
     */
    public Field3DDetectionProducerExample() {
       
        // Initialize super class
        super("detectionproducer", ProducerType.SINGLE);

        // Setting callable
        this.setCallable(() -> {

            // Creating random
            Random random = new Random();
            DetectionPacket lastDetectionPacket = null;
            
            // Generate 1000 packets
            for (int i = 0; i < 1000; i++) {
                
                // Creating lists for the robots
                final List<DetectionRobot> visionBlueRobots = new ArrayList<>();
                final List<DetectionRobot> visionYellowRobots = new ArrayList<>();
                
                // Generate random ball location
                final float xRandomBall = (random.nextFloat() * 9000.0f) - 4500.0f;
                final float yRandomBall = (random.nextFloat() * 6000.0f) - 3000.0f;
                
                // Creating new ball
                final DetectionBall visionBall = DetectionBall.newBuilder().setX(xRandomBall).setY(yRandomBall).build();
                
                // Loop through number of robots per team
                for (int j = 0; j < NUM_ROBOTS_PER_TEAM; j++) {
                    
                    // Generate random x coordinates
                    float xRandomBlue = (random.nextFloat() * 9000.0f) - 4500.0f;
                    float xRandomYellow = (random.nextFloat() * 9000.0f) - 4500.0f;
                    
                    // Generate random y coordinates
                    float yRandomBlue = (random.nextFloat() * 6000.0f) - 3000.0f;
                    float yRandomYellow = (random.nextFloat() * 6000.0f) - 3000.0f;
                    
                    // Creating robots
                    DetectionRobot robotBlue = DetectionRobot.newBuilder().setRobotId(j).setX(xRandomBlue).setY(yRandomBlue).setHeight(Robot.ROBOT_HEIGHT).build();
                    DetectionRobot robotYellow = DetectionRobot.newBuilder().setRobotId(j).setX(xRandomYellow).setY(yRandomYellow).setHeight(Robot.ROBOT_HEIGHT).build();
                    
                    // Add robots to list
                    visionBlueRobots.add(robotBlue);
                    visionYellowRobots.add(robotYellow);
                }
                
                // Create a detection frame
                DetectionFrame detectionFrame = DetectionFrame.newBuilder().addAllRobotsBlue(visionBlueRobots).addAllRobotsYellow(visionYellowRobots).addBalls(visionBall).build();

                // Create a detection packet
                DetectionPacket detectionPacket = new DetectionPacket(detectionFrame);
                // Setting last detection packet
                lastDetectionPacket = detectionPacket;

                // Put data on the pipeline
                Pipelines.getOfDataType(DetectionPacket.class).forEach((pipe -> pipe.addPacket(detectionPacket).processPacket()));
            }

            // Return the last detection packet again (packet 1001)
            return lastDetectionPacket;
        });
    }
}
