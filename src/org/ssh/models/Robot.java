package org.ssh.models;

import javafx.scene.paint.Color;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen
 *        
 */
@SuppressWarnings ("serial")
public class Robot extends FieldObject {
    
    /**
     * Unique robot id [0-15]
     */
    private transient Integer robotId;
    /**
     * teamcolor that controls this robot
     */
    private final Color       teamColor;
                              
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
    public Robot(final Integer robotId, final Color teamColor) {
        super("robot", ""); // TODO refactor this call
        // assign teamcolor
        this.teamColor = teamColor;
        this.robotId = robotId;
        // Set unique identifier for a class
        this.setSuffix(this.getTeamColorIdentifier() + robotId);
    }
    
    /**
     * example: org.ssh.models.RobotB2.json is a robot with ID 2 (RobotB-2) and teamColor Blue (Robot-B-2)
     * 
     * @see {@link Robot#getTeamColor()}
     * @return Config name for robot models.
     */
    @Override
    public String getConfigName() {
        return this.getClass().getName() + this.getTeamColorIdentifier() + this.robotId + ".json";
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
    public Color getTeamColor() {
        return this.teamColor;
    }
    
    /**
     * @return a char that identifies this robot as B(lue) or Y(ellow)
     */
    public String getTeamColorIdentifier() {
        return this.teamColor.getBlue() > 0 ? "B" : "Y";
    }
}