package org.ssh.models;

import org.ssh.models.enums.TeamColor;
import org.ssh.util.Alias;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen de Jong
 *         
 */
public class Robot extends FieldObject {
    
    /** The robot height. */
    public static final transient float ROBOT_HEIGHT = 150.0f;
                                           
    /** The robot radius */
    public static final transient float ROBOT_DIAMETER = 180.0f;
                                                       
    /**
     * Unique robot id [0-15]
     */
    @Alias ("robot_id")
    private transient Integer           robotId;
                                        
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
        super("robot");
        // assign teamcolor
        this.teamColor = teamColor;
        this.robotId = robotId;
        this.isSelected = false;
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
    
    @Override
    public String getSuffix() {
        return this.getTeamColorIdentifier() + robotId;
    }
    
    /**
     * @return color of team that controls this robot
     */
    public TeamColor getTeamColor() {
        return this.teamColor;
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