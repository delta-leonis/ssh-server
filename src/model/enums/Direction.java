package model.enums;

public enum Direction{
	NORTH, EAST, SOUTH, WEST;

	public Direction getOpposite(){
        return Direction.values()[ (this.ordinal() + 2) & 3 ];
	}
}