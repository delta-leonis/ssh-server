package org.ssh.services.consumers;

import java.util.List;
import java.util.stream.Stream;

import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Robot;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.service.Consumer;
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
    /** whether the robot-list has changed, and the 3Dfield should be updated */
    private boolean shouldUpdate;
                          
    /**
     * Create a new consumer for {@link DetectionPacket}s.
     * 
     * @param name
     *            name for this service
     */
    public DetectionModelConsumer(String name) {
        super(name);
    }
    
    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        if(fieldGame == null)
            // Getting reference to the main window
            UI.<MainWindow> get("main").ifPresent(main -> 
                fieldGame = main.field);
        
        // read the packet
        DetectionFrame frame = pipelinePacket.read();
        // create a reference for the yellow team
        List<DetectionRobot> yellowTeam = frame.getRobotsYellowList();

        boolean returnVal =
        // merge both list
        Stream.concat(yellowTeam.stream(), frame.getRobotsBlueList().stream()).map(robot -> 
            //return the updated robot model
             Models.<Robot> get(getModelName(robot, yellowTeam))
                    // try to get the existing model, if that doesnt work (orElse) create a new one
                    .orElseGet(() -> {
                        //update 3dfield
                        shouldUpdate = true;
                        //create robotclass
                        return Models.<Robot> create(Robot.class, robot.getRobotId(), getTeamColor(robot, yellowTeam));
                    }).update("x", robot.getX(), "y", robot.getY(), "height", robot.getHeight(), "lastUpdated", frame.getTSent())
            //reduce to a single succes value
        ).reduce(true, (accumulator, succes) -> succes && accumulator);

        // loop all robots that haven't been processed
        Models.<Robot> getAll().stream().filter(robot -> robot.getName().equals("robot")).forEach(robot -> {
            System.out.println(frame.getTSent() + " - " + robot.lastUpdated() + " = " + (frame.getTSent() - robot.lastUpdated()));
            if(frame.getTSent() - robot.lastUpdated() > 0.5){
                // remove models that aren't on the field
                Models.remove(robot);
                // update the 3d field
                shouldUpdate = true;
            }
        });

        //if any new robots are created,
        //or any robots are removed
        if (shouldUpdate && fieldGame != null) {
            //update all robots in fieldGame
            fieldGame.updateDetection();
            shouldUpdate = false;
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