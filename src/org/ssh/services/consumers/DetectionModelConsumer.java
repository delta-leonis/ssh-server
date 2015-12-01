package org.ssh.services.consumers;

import java.util.List;
import java.util.stream.Stream;

import org.ssh.managers.manager.Models;
import org.ssh.models.Robot;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.service.Consumer;

import protobuf.Detection.DetectionFrame;
import protobuf.Detection.DetectionRobot;

public class DetectionModelConsumer extends Consumer<DetectionPacket> {
    
    public DetectionModelConsumer(String name) {
        super(name);
    }
    
    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        DetectionFrame frame = pipelinePacket.read();
        List<DetectionRobot> yellowTeam = frame.getRobotsYellowList();
        
        return Stream.concat(yellowTeam.stream(), frame.getRobotsBlueList().stream()).map(robot -> {
            return Models.<Robot> get(getModelName(robot, yellowTeam))
                    .orElse(Models.<Robot> create(Robot.class, robot.getRobotId(), getTeamColor(robot, yellowTeam)))
                    .update("x", robot.getX(), 
                            "y", robot.getY(), 
                            "height", robot.getHeight());
        }).reduce(true, (accumulator, succes) -> succes && accumulator);
    }
    
    private String getModelName(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return String.format("robot %s%d", yellowTeam.contains(robot) ? "Y" : "B", robot.getRobotId());
    }
    
    private TeamColor getTeamColor(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return yellowTeam.contains(robot) ? TeamColor.YELLOW : TeamColor.BLUE;
    }
    
}
