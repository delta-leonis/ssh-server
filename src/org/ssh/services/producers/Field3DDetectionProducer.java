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


public class Field3DDetectionProducer extends Producer<DetectionPacket> {
    
    private static int NUM_ROBOTS_PER_TEAM = 8;
    
    public Field3DDetectionProducer() {
       
        super("detectionproducer", ProducerType.SINGLE);
        
        
        Random random = new Random();
        
        this.setCallable(() -> {
            
            
            while(true) {
                final List<DetectionRobot> visionBlueRobots = new ArrayList<DetectionRobot>();
                final List<DetectionRobot> visionYellowRobots = new ArrayList<DetectionRobot>();
                
                final float xRandomBall = (random.nextFloat() * 9000.0f) - 4500.0f;
                final float yRandomBall = (random.nextFloat() * 6000.0f) - 3000.0f;
                
                final DetectionBall visionBall = DetectionBall.newBuilder().setX(xRandomBall).setY(yRandomBall).build();
                
                for (int i = 0; i < NUM_ROBOTS_PER_TEAM; i++) {
                    
                    
                    // (random.nextDouble() * (max - min)) + min
                    float xRandomBlue = (random.nextFloat() * 9000.0f) - 4500.0f;
                    float xRandomYellow = (random.nextFloat() * 9000.0f) - 4500.0f;
                    
                    float yRandomBlue = (random.nextFloat() * 6000.0f) - 3000.0f;
                    float yRandomYellow = (random.nextFloat() * 6000.0f) - 3000.0f;
                    
                    DetectionRobot robotBlue = DetectionRobot.newBuilder().setRobotId(i).setX(xRandomBlue).setY(yRandomBlue).setHeight(Robot.ROBOT_HEIGHT).build();
                    DetectionRobot robotYellow = DetectionRobot.newBuilder().setRobotId(i).setX(xRandomYellow).setY(yRandomYellow).setHeight(Robot.ROBOT_HEIGHT).build();
                    
                    visionBlueRobots.add(robotBlue);
                    visionYellowRobots.add(robotYellow);
                }
                
                DetectionFrame detectionFrame = DetectionFrame.newBuilder().addAllRobotsBlue(visionBlueRobots).addAllRobotsYellow(visionYellowRobots).addBalls(visionBall).build();
    
                DetectionPacket detectionPacket = new DetectionPacket(detectionFrame);
                
                Pipelines.getOfDataType(DetectionPacket.class).forEach((pipe -> pipe.addPacket(detectionPacket).processPacket()));
            
                Thread.sleep(18);
            }
        });
    }
}
