package org.ssh.models;

import org.ssh.util.Alias;

/**
 * Describes a ball {@link FieldObject object}.
 * 
 * @author Jeroen de Jong
 *         
 */
public class Ball extends FieldObject {
    
    /**
     * Height of the ball as provided by ssl-vision
     */
    @Alias("z")
    private Float zPosition;
    
    /**
     * Instantiates a ball
     */
    public Ball() {
        super("ball");
    }
    
    @Override
    public String getSuffix(){
        return "";
    }
    
    
    /**
     * @return Height of the ball as provided by ssl-vision
     */
    public Float getZPos(){
        return zPosition;
    }
    
}