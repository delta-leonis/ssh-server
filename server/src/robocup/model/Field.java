package robocup.model;

import robocup.model.enums.FieldZone;

public class Field {
	private int length;
	private int width;

	private int lineWidth;
	private int boundaryWidth;
	private int refereeWidth;
	private int goalWidth;
	private int goalDepth;
	private int goalWallWidth;
	private int goalHeight;
	private int centerCircleRadius;
	private int defenceRadius;
	private int defenceStretch;
	private int freeKickFromDefenceDistance;
	private int penaltySpotFromFieldLineDistance;
	private int penaltyLineFromSpotDistance;
	private int cameraOverlapZoneWidth;
	private Goal eastGoal, westGoal;

	public Field(int fieldHeight, int fieldWidth) {
		this.length = fieldHeight;
		this.width = fieldWidth;
		this.lineWidth = 40;
		this.boundaryWidth = 100;
		this.refereeWidth = 425;
		this.centerCircleRadius = 500;
		this.defenceRadius = 800;
		this.defenceStretch = 350;
		this.freeKickFromDefenceDistance = 200;
		this.penaltySpotFromFieldLineDistance = 750;
		this.penaltyLineFromSpotDistance = 400;
		this.cameraOverlapZoneWidth = 400;
		
		createGoals(700, 180, 20, 180);
	}
	
	public void update(int lineWidth, int fieldLength, int fieldWidth, int boundaryWidth, int refereeWidth,
			int goalWidth, int goalDepth, int goalWallWidth, int centerCircleRadius, int defenseRadius,
			int defenseStretch, int freeKickFromDefenceDistance, int penaltySpotFromFieldLineDist,
			int penaltyLineFromSpotDistance) {
		boolean changed = false;
		if (this.lineWidth != lineWidth)
			this.lineWidth = lineWidth;
		if (this.length != fieldLength) {
			this.length = fieldLength;
			changed = true;
		}
		if (this.width != fieldWidth)
			this.width = fieldWidth;
		if (this.boundaryWidth != boundaryWidth)
			this.boundaryWidth = boundaryWidth;
		if (this.refereeWidth != refereeWidth)
			this.refereeWidth = refereeWidth;
		if (this.goalWidth != goalWidth) {
			this.goalWidth = goalWidth;
			changed = true;
		}
		if (this.goalDepth != goalDepth) {
			this.goalDepth = goalDepth;
			changed = true;
		}
		if (this.goalWallWidth != goalWallWidth) {
			this.goalWallWidth = goalWallWidth;
			changed = true;
		}
		if (this.centerCircleRadius != centerCircleRadius)
			this.centerCircleRadius = centerCircleRadius;
		if (this.defenceRadius != defenseRadius)
			this.defenceRadius = defenseRadius;
		if (this.defenceStretch != defenseStretch)
			this.defenceStretch = defenseStretch;
		if (this.freeKickFromDefenceDistance != freeKickFromDefenceDistance)
			this.freeKickFromDefenceDistance = freeKickFromDefenceDistance;
		if (this.penaltyLineFromSpotDistance != penaltyLineFromSpotDistance)
			this.penaltyLineFromSpotDistance = penaltyLineFromSpotDistance;
		if (this.penaltySpotFromFieldLineDistance != penaltySpotFromFieldLineDist)
			this.penaltySpotFromFieldLineDistance = penaltySpotFromFieldLineDist;
		if (changed)
			createGoals(goalWidth, goalDepth, goalWallWidth, goalHeight);
	}

	/**
	 * Calculates goal dimensions and creates them. uses goalWidth, goalDepth,
	 * goalWallWidth, goalHeigth, length
	 */
	private void createGoals(int goalWidth, int goalDepth, int goalWallWidth, int goalHeigth) {
		FieldPoint frontNorth = new FieldPoint(width / 2, goalWidth / 2);
		FieldPoint frontSouth = new FieldPoint(width / 2, goalWidth / -2);
		FieldPoint backNorth = new FieldPoint(frontNorth.getX() + goalDepth, frontNorth.getY());
		FieldPoint backSouth = new FieldPoint(frontSouth.getX() + goalDepth, frontSouth.getY());

		// Adding Goal A
		eastGoal = new Goal(frontNorth, frontSouth, backNorth, backSouth, goalWallWidth, goalHeigth);

		// Adding Goal B
		westGoal = new Goal(frontSouth.diagMirror(), frontNorth.diagMirror(), backSouth.diagMirror(), backNorth
				.diagMirror(), goalWallWidth, goalHeigth);
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the boundaryWidth
	 */
	public int getBoundaryWidth() {
		return boundaryWidth;
	}

	/**
	 * @param boundaryWidth the boundaryWidth to set
	 */
	public void setBoundaryWidth(int boundaryWidth) {
		this.boundaryWidth = boundaryWidth;
	}

	/**
	 * @return the refereeWidth
	 */
	public int getRefereeWidth() {
		return refereeWidth;
	}

	/**
	 * @param refereeWidth the refereeWidth to set
	 */
	public void setRefereeWidth(int refereeWidth) {
		this.refereeWidth = refereeWidth;
	}

	/**
	 * @return the centerCircleRadius
	 */
	public int getCenterCircleRadius() {
		return centerCircleRadius;
	}

	/**
	 * @param centerCircleRadius the centerCircleRadius to set
	 */
	public void setCenterCircleRadius(int centerCircleRadius) {
		this.centerCircleRadius = centerCircleRadius;
	}

	/**
	 * @return the defenceRadius
	 */
	public int getDefenceRadius() {
		return defenceRadius;
	}

	/**
	 * @param defenceRadius the defenceRadius to set
	 */
	public void setDefenceRadius(int defenceRadius) {
		this.defenceRadius = defenceRadius;
	}

	/**
	 * @return the defenceStretch
	 */
	public int getDefenceStretch() {
		return defenceStretch;
	}

	/**
	 * @param defenceStretch the defenceStretch to set
	 */
	public void setDefenceStretch(int defenceStretch) {
		this.defenceStretch = defenceStretch;
	}

	/**
	 * @return the freeKickFromDefenceDistance
	 */
	public int getFreeKickFromDefenceDistance() {
		return freeKickFromDefenceDistance;
	}

	/**
	 * @param freeKickFromDefenceDistance the freeKickFromDefenceDistance to set
	 */
	public void setFreeKickFromDefenceDistance(int freeKickFromDefenceDistance) {
		this.freeKickFromDefenceDistance = freeKickFromDefenceDistance;
	}

	/**
	 * @return the penaltySpotFromFieldLineDistance
	 */
	public int getPenaltySpotFromFieldLineDistance() {
		return penaltySpotFromFieldLineDistance;
	}

	/**
	 * @param penaltySpotFromFieldLineDistance the penaltySpotFromFieldLineDistance to set
	 */
	public void setPenaltySpotFromFieldLineDistance(int penaltySpotFromFieldLineDistance) {
		this.penaltySpotFromFieldLineDistance = penaltySpotFromFieldLineDistance;
	}

	/**
	 * @return the penaltyLineFromSpotDistance
	 */
	public int getPenaltyLineFromSpotDistance() {
		return penaltyLineFromSpotDistance;
	}

	/**
	 * @param penaltyLineFromSpotDistance the penaltyLineFromSpotDistance to set
	 */
	public void setPenaltyLineFromSpotDistance(int penaltyLineFromSpotDistance) {
		this.penaltyLineFromSpotDistance = penaltyLineFromSpotDistance;
	}

	public Goal getEastGoal(){
		return eastGoal;
	}
	public Goal getWestGoal(){
		return westGoal;
	}

	public int getCameraOverlapZoneWidth() {
		return cameraOverlapZoneWidth;
	}
	
	public void setCameraOverlapZoneWidth(int cameraOverlapZoneWith) {
		this.cameraOverlapZoneWidth = cameraOverlapZoneWith;
	}
	
	public void setFieldProportions(int width, int length, int lineWidth, int boundaryWidth, int refereeWidth) {
		this.width = width;
		this.length = length;
		this.lineWidth = lineWidth;
		this.boundaryWidth = boundaryWidth;
		this.refereeWidth = refereeWidth;
	}
	
	public void setFieldZones(int centerCircleRadius, int defenceRadius, int defenceStretch) {
		this.centerCircleRadius = centerCircleRadius;
		this.defenceRadius = defenceRadius;
		this.defenceStretch = defenceStretch;
	}
	
	public void setRuleDistances(int freeKickFromDefenceDistance, int penaltySpotFromFieldLineDistance, int penaltyLineFromSpotDistance) {
		this.freeKickFromDefenceDistance = freeKickFromDefenceDistance;
		this.penaltySpotFromFieldLineDistance = penaltySpotFromFieldLineDistance;
		this.penaltyLineFromSpotDistance = penaltyLineFromSpotDistance;
	}
	
	public void setGoalProportions(int goalWidth, int goalDepth, int goalWallWidth, int goalHeight) {
		createGoals(goalWidth, goalDepth, goalWallWidth, goalHeight);
	}
	
	public FieldZone locateObject(FieldObject argObject) {
		for (FieldZone fieldZone : FieldZone.values()) {	
			if (fieldZone.contains(argObject.getPosition()))
				return fieldZone;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Field [length=" + length + "\r\n width=" + width + "\r\n lineWidth=" + lineWidth
				+ "\r\n boundaryWidth=" + boundaryWidth + "\r\n refereeWidth=" + refereeWidth + "\r\n goalWidth="
				+ goalWidth + "\r\n goalDepth=" + goalDepth + "\r\n goalWallWidth=" + goalWallWidth
				+ "\r\n goalHeight=" + goalHeight + "\r\n centerCircleRadius=" + centerCircleRadius
				+ "\r\n defenceRadius=" + defenceRadius + "\r\n defenceStretch=" + defenceStretch
				+ "\r\n freeKickFromDefenceDistance=" + freeKickFromDefenceDistance
				+ "\r\n penaltySpotFromFieldLineDistance=" + penaltySpotFromFieldLineDistance
				+ "\r\n penaltyLineFromSpotDistance=" + penaltyLineFromSpotDistance + "\r\n" + printGoals() + "]"
				+ "\r\n";
	}

	private String printGoals() {
		return eastGoal.toString() +  westGoal.toString();
	}
}
