package org.ssh.models;

import org.ssh.models.enums.Allegiance;

/**
 * Describes a goal on the {@link Field}
 *
 * @author Jeroen de Jong
 */
public class Goal extends FieldObject {

    /**
     * Defending team for this goal
     */
    private transient Allegiance allegiance;

    /**
     * Dimensions of the goal
     */
    private Integer goalDepth, goalWidth;
    public static final transient Integer GOAL_HEIGHT = 160;

    /**
     * Creates a goal on a specified field half
     *
     * @param allegiance Defending team for this goal
     */
    public Goal(final Allegiance allegiance) {
        super("goal", allegiance.name());

        this.allegiance = allegiance;
    }

    @Override
    public void initialize() {
        super.initialize();
        goalDepth = 0;
        goalWidth = 0;
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
     * @return Defending team for this goal
     */
    public Allegiance getAllegiance() {
        return this.allegiance;
    }
}