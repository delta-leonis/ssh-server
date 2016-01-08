package org.ssh.models;

/**
 * Describes a ball {@link FieldObject object}.
 * 
 * @author Jeroen de Jong
 *         
 */
public class Ball extends FieldObject {
    
    /** The diameter of the ball */
    public static final int BALL_DIAMETER = 43;
    
    /**
     * Height of the ball as provided by ssl-vision
     */
    @Alias ("z")
    private Float zPosition;
    
    /**
     * Instantiates a ball
     */
    public Ball() {
        super("ball", "");
    }

    @Override
    public void initialize(){
        super.initialize();
        //no default values
    }
    /**
     * @return Height of the ball as provided by ssl-vision
     */
    public Float getZPos() {
        return zPosition;
    }
    
}