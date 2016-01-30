package org.ssh.models.enums;

/**
 * Describes a cardinal direction.
 *
 * @author Jeroen de Jong
 */
public enum Direction {
    NORTH(0),
    EAST(90),
    SOUTH(180),
    WEST(270);

    private int shiftDeg;

    /**
     * @return opposite direction (NORTH becomes SOUTH, WEST becomes EAST etc.).
     */
    public Direction getOpposite() {
        return Direction.values()[(this.ordinal() + 2) & 3];
    }

    Direction(int shiftDeg){
        this.shiftDeg = shiftDeg;
    }

    /**
     * @return shift in degrees based on NORTH
     */
    public int getDegreeShift(){
        return shiftDeg;
    }
}