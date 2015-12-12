package org.ssh.models;

import org.ssh.managers.manager.Models;
import org.ssh.models.enums.TeamColor;
import org.ssh.util.Alias;

import java.util.Optional;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen de Jong
 *         
 */
public class Robot extends FieldObject {
    
    /** The robot height. */
    public static final transient float ROBOT_HEIGHT   = 150.0f;
                                                       
    /** The robot radius */
    public static final transient float ROBOT_DIAMETER = 180.0f;
                                                       
    /**
     * Unique robot id [0-15]
     */
    @Alias ("robot_id")
    private transient Integer           robotId;
                                        
    /** timestamp of last update for this model */
    private transient Double            lastUpdated;
                                        
    /**
     * teamcolor that controls this robot
     */
    private final TeamColor             teamColor;
                                        
    /**
     * current dribble speed (-1 is max backward, 1 is max forward)
     */
    private Float                       dribbleSpeed;
                                        
    /** The boolean for the selected state */
    private Boolean                     isSelected;
                                        
    /** Orientation which the robot is facing, not driving */
    private Float                       orientation;
                                        
    /** height of this robot as provided by ssl-vision */
    private Float                       height;
                                        
    /**
     * Instantiates a new robot with specified properties
     * 
     * @param robotId
     *            robot id
     * @param teamColor
     *            color that controls this robot
     */
    public Robot(final Integer robotId, final TeamColor teamColor) {
        super("robot", Robot.identifierOf(robotId, teamColor));
        // assign teamcolor
        this.teamColor = teamColor;
        this.robotId = robotId;
        this.isSelected = false;
    }

    public static String identifierOf(final Integer robotId, TeamColor teamColor){
        Optional<Game> oGame = Models.<Game>get("game");
        return oGame.isPresent() ?
                String.format("%s%d", oGame.get().getAllegiance(teamColor).identifier(), robotId) :
                "A" + robotId;
    }
    
    /**
     * @return current dribblespeed
     */
    public float getDribbleSpeed() {
        return this.dribbleSpeed;
    }
    
    /**
     * @return presumed height of this robot as provided by ssl-vision
     */
    public Float getHeight() {
        return height;
    }
    
    /**
     * @return the Orientation which the robot is facing, not driving
     */
    public Float getOrientation() {
        return this.orientation;
    }
    
    /**
     * @return robot ID [0-15]
     */
    public Integer getRobotId() {
        return this.robotId;
    }
    
    /**
     * @return color of team that controls this robot
     */
    public TeamColor getTeamColor() {
        return this.teamColor;
    }
    
    /**
     * @return timestamp of last update for this model
     */
    public Double lastUpdated(){
        return lastUpdated;
    }
    
    /**
     * @return a char that identifies this robot as B(lue) or Y(ellow)
     */
    public String getTeamColorIdentifier() {
        return this.teamColor.name().substring(0, 1);
    }
    
    /**
     * @return True, if the robot is selected.
     */
    public boolean isSelected() {
        return this.isSelected;
    }
}