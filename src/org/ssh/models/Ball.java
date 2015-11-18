package org.ssh.models;

/**
 * Describes a ball {@link FieldObject object}.
 * 
 * @author Jeroen de Jong
 *         
 */
public class Ball extends FieldObject {
    
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
    
}