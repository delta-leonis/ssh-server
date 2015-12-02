package org.ssh.services.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Robot;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.service.Consumer;
import org.ssh.ui.UIController;
import org.ssh.ui.windows.MainWindow;

import protobuf.Detection.DetectionFrame;
import protobuf.Detection.DetectionRobot;

public class DetectionModelConsumer extends Consumer<DetectionPacket> {
    
    private FieldGame     fieldGame;
    private List<Integer> lastRobotIDs;
                          
    public DetectionModelConsumer(String name) {
        super(name);
        
        // Getting reference to the main window
        MainWindow mainWindow = (MainWindow) ((UIController<?>) UI.get("main").get());
        
        // Setting the field game
        this.fieldGame = mainWindow.field;
        lastRobotIDs = new ArrayList<Integer>();
    }
    
    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        DetectionFrame frame = pipelinePacket.read();
        List<DetectionRobot> yellowTeam = frame.getRobotsYellowList();
        
        System.out.println("consume detection packet");
        System.out.println(frame.getRobotsBlueList());
        System.out.println(frame.getRobotsYellowList());
        
        List<Integer> newRobotIDs = new ArrayList<Integer>();
        
        boolean returnVal = Stream.concat(yellowTeam.stream(), frame.getRobotsBlueList().stream()).map(robot -> {
            newRobotIDs.add(robot.getRobotId());
            return Models.<Robot> get(getModelName(robot, yellowTeam))
                    .orElse(Models.<Robot> create(Robot.class, robot.getRobotId(), getTeamColor(robot, yellowTeam)))
                    .update("x", robot.getX(), "y", robot.getY(), "height", robot.getHeight());
        }).reduce(true, (accumulator, succes) -> succes && accumulator);
        
        if (!lastRobotIDs.containsAll(newRobotIDs) || newRobotIDs.containsAll(lastRobotIDs)) {
            fieldGame.updateGeometry();
            lastRobotIDs = newRobotIDs;
        }
        
        return returnVal;
    }
    
    private String getModelName(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return String.format("robot %s%d", yellowTeam.contains(robot) ? "Y" : "B", robot.getRobotId());
    }
    
    private TeamColor getTeamColor(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return yellowTeam.contains(robot) ? TeamColor.YELLOW : TeamColor.BLUE;
    }
    
}
