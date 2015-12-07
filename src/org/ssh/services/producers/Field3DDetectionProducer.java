package org.ssh.services.producers;

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
 * 3D Field Detection producer class. This class is responsible for generating random detection data.
 * 
 * @author marklef2
 * @see Producer
 */
public class Field3DDetectionProducer extends Producer<DetectionPacket> {
    
    /** The number of robots per team. */
    private static int NUM_ROBOTS_PER_TEAM = 11;
    
    /** The number of frames per second. */
    private static float FPS = 60.0f;
    
    /** The time to sleep */
    private static int TIME_TO_SLEEP = (int)((1.0f / FPS) * 1000.0f);
    
    
    /**
     * Instantiates a new 3D detection producer.
     */
    public Field3DDetectionProducer() {
       
        // Initialize super class
        super("detectionproducer", ProducerType.SINGLE);
        
        // Creating random
        Random random = new Random();
        
        // Setting callable
        this.setCallable(() -> {
            
            // Loop till infinity
            while(true) {
                
                // Creating lists for the robots
                final List<DetectionRobot> visionBlueRobots = new ArrayList<DetectionRobot>();
                final List<DetectionRobot> visionYellowRobots = new ArrayList<DetectionRobot>();
                
                // Generate random ball location
                final float xRandomBall = (random.nextFloat() * 9000.0f) - 4500.0f;
                final float yRandomBall = (random.nextFloat() * 6000.0f) - 3000.0f;
                
                // Creating new ball
                final DetectionBall visionBall = DetectionBall.newBuilder().setX(xRandomBall).setY(yRandomBall).build();
                
                // Loop through number of robots per team
                for (int i = 0; i < NUM_ROBOTS_PER_TEAM; i++) {
                    
                    // Generate random x coordinates
                    float xRandomBlue = (random.nextFloat() * 9000.0f) - 4500.0f;
                    float xRandomYellow = (random.nextFloat() * 9000.0f) - 4500.0f;
                    
                    // Generate random y coordinates
                    float yRandomBlue = (random.nextFloat() * 6000.0f) - 3000.0f;
                    float yRandomYellow = (random.nextFloat() * 6000.0f) - 3000.0f;
                    
                    // Creating robots
                    DetectionRobot robotBlue = DetectionRobot.newBuilder().setRobotId(i).setX(xRandomBlue).setY(yRandomBlue).setHeight(Robot.ROBOT_HEIGHT).build();
                    DetectionRobot robotYellow = DetectionRobot.newBuilder().setRobotId(i).setX(xRandomYellow).setY(yRandomYellow).setHeight(Robot.ROBOT_HEIGHT).build();
                    
                    // Add robots to list
                    visionBlueRobots.add(robotBlue);
                    visionYellowRobots.add(robotYellow);
                }
                
                // Create a detection frame
                DetectionFrame detectionFrame = DetectionFrame.newBuilder().addAllRobotsBlue(visionBlueRobots).addAllRobotsYellow(visionYellowRobots).addBalls(visionBall).build();
                // Create a detection packet
                DetectionPacket detectionPacket = new DetectionPacket(detectionFrame);
                
                // Put data on the pipeline
                Pipelines.getOfDataType(DetectionPacket.class).forEach((pipe -> pipe.addPacket(detectionPacket).processPacket()));
            
                // Sleep for a while
                Thread.sleep(TIME_TO_SLEEP);
            }
        });
    }
}
