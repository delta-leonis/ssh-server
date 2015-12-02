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

/**
 * Class for parsing all {@link DetectionPacket}s to their models.
 * 
 * @author Jeroen de Jong
 *        
 */
public class DetectionModelConsumer extends Consumer<DetectionPacket> {
    
    /** Reference to FieldGame in GUI, used for updating */
    private FieldGame     fieldGame;
    /** List of all ID's that were updated last time */
    private List<Integer> lastRobotIDs;
                          
    /**
     * Create a new consumer for {@link DetectionPacket}s.
     * 
     * @param name
     *            name for this service
     */
    public DetectionModelConsumer(String name) {
        super(name);
        
        // Getting reference to the main window
        MainWindow mainWindow = UI.<MainWindow>get("main").get();
        
        // Setting the field game
        this.fieldGame = mainWindow.field;
        
        // Instantiate the arraylist
        lastRobotIDs = new ArrayList<Integer>();
    }
    
    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        // read the packet
        DetectionFrame frame = pipelinePacket.read();
        // create a reference for the yellow team
        List<DetectionRobot> yellowTeam = frame.getRobotsYellowList();
        
        // new list for capturing robot id's that have been created
        List<Integer> newRobotIDs = new ArrayList<Integer>();
        
        boolean returnVal =
        // merge both list
        Stream.concat(yellowTeam.stream(), frame.getRobotsBlueList().stream()).map(robot -> {
            // add this robot id
            newRobotIDs.add(robot.getRobotId());
            //return the updated robot model
            return Models.<Robot> get(getModelName(robot, yellowTeam))
                    // try to get the existing model, if that doesnt work (orElse) create a new one
                    .orElse(Models.<Robot> create(Robot.class, robot.getRobotId(), getTeamColor(robot, yellowTeam)))
                    //update the model that is retreived or created.
                    .update("x", robot.getX(), "y", robot.getY(), "height", robot.getHeight());
            //reduce to a single succes value
        }).reduce(true, (accumulator, succes) -> succes && accumulator);
        
        //if any new robots are created,
        //or any robots are removed
        if (!lastRobotIDs.containsAll(newRobotIDs) || !newRobotIDs.containsAll(lastRobotIDs)) {
            //update all robots in fieldGame
            fieldGame.updateDetection();
            lastRobotIDs = newRobotIDs;
        }
        
        return returnVal;
    }
    
    /**
     * Get the proposed name of {@link DetectionRobot} based on it's present in the given list. <br /><br />
     * 
     * NOTE: list should be of the yellow team.
     * 
     * @param robot         robot to generate modelname for
     * @param yellowTeam
     *          list of all yellow robots
     * @return proposed robotname (i.e. "robot Y3")
     */
    private String getModelName(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return String.format("robot %s%d", yellowTeam.contains(robot) ? "Y" : "B", robot.getRobotId());
    }
    
    /**
     * Get the teamcolor based on the presents of the robot in the provided list. <br /><br />
     * 
     * NOTE: list should be of the yellow team.
     * 
     * @param robot robot to get {@link TeamColor} for.
     * @param yellowTeam 
     *          list of all yellow robots
     * @return
     */
    private TeamColor getTeamColor(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return yellowTeam.contains(robot) ? TeamColor.YELLOW : TeamColor.BLUE;
    }
    
}
