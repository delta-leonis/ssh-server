package org.ssh.models.enums;

/**
 * Describes a cardinal direction.
 *
 * @author Jeroen de Jong
 *         
 */
public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;
    
    /**
     * @return opposite direction (NORTH becomes SOUTH, WEST becomes EAST etc.).
     */
    public Direction getOpposite() {
        return Direction.values()[(this.ordinal() + 2) & 3];
    }
}