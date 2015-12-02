package org.ssh.models;

import org.ssh.models.enums.Direction;

/**
 * Describes a goal on the {@link Field}
 *
 * @author Jeroen
 */
public class Goal extends FieldObject {
    
    /**
     * Location of this goal
     */
    private final Direction                fieldHalf;
                                           
    /**
     * Dimensions of the goal
     */
    private Integer                        goalDepth, goalWidth;
    public transient static final Integer GOAL_HEIGHT = 160;
                                                       
    /**
     * Creates a goal on a specified field half
     * 
     * @param fieldHalf
     *            location of the goal
     */
    public Goal(final Direction fieldHalf) {
        
        super("goal");
        
        this.fieldHalf = fieldHalf;
        this.goalDepth = 0;
        this.goalWidth = 0;
    }
    
    /**
     * @return depth of the goal in mm
     */
    public int getGoalDepth() {
        return this.goalDepth;
    }
    
    /**
     * @return width of the goal in mm
     */
    public int getGoalWidth() {
        return this.goalWidth;
    }

    /**
     * @return side that the goal is on
     */
    public Direction getSide() {
        return this.fieldHalf;
    }
    
    @Override
    public String getSuffix() {
        return getSide().name();
    }
}