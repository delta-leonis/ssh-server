package org.ssh.controllers;

import org.ssh.models.Model;

/**
 * Contains all settings used by the {@link ControllerHandler}
 * 
 * @author Thomas Hakkers
 */
public class ControllerSettings extends Model{
    
    /**
     * The speed at which the robot is allowed to kick in percent 0 - 100% 
     */
    private Float maxFlatKickSpeed;
    /**
     * The speed at which the robot is allowed to chip in percent 0 - 100% 
     */
    private Float maxChipKickSpeed;
    /**
     * The speed the robot is allowed to rotate
     */
    private Float maxRotationSpeed;
    /**
     * The speed the robot is allowed to move in mm/s
     */
    private Float maxVelocity;
    /**
     * The speed the robot is allowed to dribble at
     */
    private Float maxDribbleSpeed;
    
    public ControllerSettings() {
        super("controllersettings");
    }
    
    /**
     * @return The speed at which the robot is allowed to kick in percent 0 - 100% 
     */
    public float getMaxFlatKickSpeed(){
        return maxFlatKickSpeed;
    }
    
    /**
     * @return The speed at which the robot is allowed to chip in percent 0 - 100% 
     */
    public float getMaxChipKickSpeed(){
        return maxChipKickSpeed;
    }
    
    /**
     * @return The speed the robot is allowed to rotate
     */
    public float getMaxRotationSpeed(){
        return maxRotationSpeed;
    }
    
    /**
     * @return The speed the robot is allowed to move in mm/s
     */
    public float getMaxVelocity(){
        return maxVelocity;
    }
    
    /**
     * @return The speed the robot is allowed to dribble at
     */
    public float getMaxDribbleSpeed() {
        return maxDribbleSpeed;
    }

    @Override
    public String getSuffix() {
        return "";
    }
}
