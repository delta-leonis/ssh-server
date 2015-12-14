package org.ssh.models;

import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Allegiance;
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

    /** allegiance of this robot */
    private Allegiance allegiance;

    /**  Unique robot id [0-15] */
    @Alias ("robot_id")
    private transient Integer           robotId;
                                        
    /** timestamp of last update for this model */
    private transient Double            lastUpdated;

    /** current dribble speed (-1 is max backward, 1 is max forward) */
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
     * @param allegiance
     *            allegiance of this robot ({@link Allegiance#ALLY} of {@link Allegiance#OPPONENT})
     */
    public Robot(final Integer robotId, final Allegiance allegiance) {
        super("robot", allegiance.identifier() + robotId);
        this.allegiance = allegiance;
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
    
    /**
     * @return team allegiance of this robot (Ally / Opponent)
     */
    public Allegiance getAllegiance() {
        return this.allegiance;
    }
    
    /**
     * @return timestamp of last update for this model
     */
    public Double lastUpdated(){
        return lastUpdated;
    }
    
    /**
     * @return True, if the robot is selected.
     */
    public boolean isSelected() {
        return this.isSelected;
    }
}