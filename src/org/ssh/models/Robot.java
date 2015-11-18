package org.ssh.models;

import org.ssh.models.enums.TeamColor;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen de Jong
 *         
 */
public class Robot extends FieldObject {
    
    /** The robot height. */
    public static final float ROBOT_HEIGHT = 200.0f;
                                           
    /** The robot radius */
    public static final float ROBOT_RADIUS = 250.0f;
                                           
    /**
     * Unique robot id [0-15]
     */
    private transient Integer robotId;
                              
    /**
     * teamcolor that controls this robot
     */
    private final TeamColor       teamColor;
                              
    /**
     * current dribble speed (-1 is max backward, 1 is max forward)
     */
    private float             dribbleSpeed;
                              
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
    }
    
    /**
     * @return current dribblespeed
     */
    public float getDribbleSpeed() {
        return this.dribbleSpeed;
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
     * @return a char that identifies this robot as B(lue) or Y(ellow)
     */
    public String getTeamColorIdentifier() {
        return this.teamColor.name().substring(0, 1);
    }

    @Override
    public String getSuffix() {
        return this.getTeamColorIdentifier() + robotId;
    }
}