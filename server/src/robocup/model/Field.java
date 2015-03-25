package robocup.model;

import java.util.EnumMap;
import java.util.Map;

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
	private Map<FieldZone,Zone> zoneList;

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
		zoneList = new EnumMap<FieldZone, Zone>(FieldZone.class);
		

		// a      b     c
		// *------*-----*
		// |     /|     |
		// d   e/ |     |
		// *---*  |     |
		// |   \f g     h
 		// |gt  *-*-----*
		// |q  r| |     |
		// *---*| |     |
		// |gb  i j     k
		// |    *-*-----*
		// l   m/ |     |
		// *---*  |     |
		// |    \ |     |
		// n     \o     p
		// *------*-----*
		
		// single field
		//		x		y		index
		// a	0		0		0
		// b 	1400	0		1
		// c 	3025	0		2
		// d 	0		1000	3		
		// e 	400		1000	4		
		// f	700		1500	5		
		// g 	1400	1500	6		
		// h 	3025	1500	7		
		// i 	700		2550	8		
		// j 	1400	2550	9		
		// k 	3025	2550	10		
		// l 	0		3050	11		
		// m 	400		3050	12		
		// n 	0		4050	13		
		// o 	1400	4050	14		
		// p 	3025	4050	15
		// q    0	 	700	    16
		// r    2025    700  	17
		
		Point fieldLocs[] = new Point[18];
		fieldLocs[0] = new Point(0, 0);
		fieldLocs[1] = new Point(1400, 0);
		fieldLocs[2] = new Point(3025, 0);
		fieldLocs[3] = new Point(0, 1000);
		fieldLocs[4] = new Point(400, 1000);
		fieldLocs[5] = new Point(700, 1500);
		fieldLocs[6] = new Point(1400, 1500);
		fieldLocs[7] = new Point(3025, 1500);
		fieldLocs[8] = new Point(700, 2550);
		fieldLocs[9] = new Point(1400, 2550);
		fieldLocs[10] = new Point(3025, 2550);
		fieldLocs[11] = new Point(0, 3050);
		fieldLocs[12] = new Point(400, 3050);
		fieldLocs[13] = new Point(0, 4050);
		fieldLocs[14] = new Point(1400, 4050);
		fieldLocs[15] = new Point(3025, 4050);
		fieldLocs[16] = new Point(0, 2025);
		fieldLocs[17] = new Point(700, 2025);

		// sectoren
		// naam						p1        p2        P3		  P4
		// WEST_RIGHT_CORNER		a -  0	  b -  1    e -  4	  d -  3
		// WEST_RIGHT_SECOND_POST	b -  1	  g -  6    f -  5    e -  4
		// WEST_CENTER				f -  5	  g -  6    j -  9	  i -  8
		// WEST_LEFT_SECOND_POST	i -  8	  j -  9    o - 14    m - 12
		// WEST_LEFT_CORNER			l - 11	  m - 12    o - 14    n - 13
		// WEST_RIGHT_FRONT			b -  1	  c -  2    h -  7    g -  6
		// WEST_MIDDLE				g -  6	  h -  7    k - 10    j -  9
		// WEST_LEFT_FRONT			j -  9	  k - 10    p - 15    o - 14
		// WEST_GOAL_TOP			d    3    e    4    f   5     r   17    q   16
		// WEST_GOAL_BOT			q    16   r    17   i   8     m   12    l   11
		
		//west row one (goal)
		zoneList.put(FieldZone.WEST_RIGHT_CORNER,
				new Zone("rightCorner", 
				new Point[] { fieldLocs[0],fieldLocs[1],fieldLocs[4],fieldLocs[3] },length,width));
		zoneList.put(FieldZone.WEST_RIGHT_SECOND_POST,
				new Zone("rightSecondPost", 
				new Point[] { fieldLocs[1],fieldLocs[6],fieldLocs[5],fieldLocs[4] },length,width));
		zoneList.put(FieldZone.WEST_CENTER,
				new Zone("center", 
				new Point[] { fieldLocs[5],fieldLocs[6],fieldLocs[9],fieldLocs[8] },length,width));
		zoneList.put(FieldZone.WEST_LEFT_SECOND_POST,
				new Zone("leftSecondPost", 
				new Point[] { fieldLocs[8],fieldLocs[9],fieldLocs[14],fieldLocs[12] },length,width));
		zoneList.put(FieldZone.WEST_LEFT_CORNER,
				new Zone("leftCorner", 
				new Point[] { fieldLocs[11],fieldLocs[12],fieldLocs[14],fieldLocs[13] },length,width));
		//west row two (mid)
		zoneList.put(FieldZone.WEST_RIGHT_FRONT,
				new Zone("rightFront",
				new Point[] { fieldLocs[1],fieldLocs[2],fieldLocs[7],fieldLocs[6] },length,width));
		zoneList.put(FieldZone.WEST_MIDDLE,
				new Zone("middle", 
				new Point[] { fieldLocs[6],fieldLocs[7],fieldLocs[10],fieldLocs[9] },length,width));
		zoneList.put(FieldZone.WEST_LEFT_FRONT,
				new Zone("leftFront",
				new Point[] { fieldLocs[9],fieldLocs[10],fieldLocs[15],fieldLocs[14] },length,width));
		
		//goal area
		zoneList.put(FieldZone.WEST_GOAL_LEFT,
				new Zone("goal left", 
				new Point[] { fieldLocs[3],fieldLocs[4],fieldLocs[5],fieldLocs[17],fieldLocs[16] },length,width));
		zoneList.put(FieldZone.WEST_GOAL_RIGHT,
				new Zone("goal right",
				new Point[] { fieldLocs[16],fieldLocs[17],fieldLocs[8],fieldLocs[12], fieldLocs[11] },length,width));

		//east row one (goal)
		zoneList.put(FieldZone.EAST_LEFT_CORNER,
				new Zone("leftCorner",
				new Point[] {makeXReverse(fieldLocs[0]), makeXReverse(fieldLocs[1]), makeXReverse(fieldLocs[4]), makeXReverse(fieldLocs[3]) },length,width));
		zoneList.put(FieldZone.EAST_LEFT_SECOND_POST,
				new Zone("leftSecondPost",
				new Point[] {makeXReverse(fieldLocs[1]), makeXReverse(fieldLocs[6]), makeXReverse(fieldLocs[5]), makeXReverse(fieldLocs[4]) },length,width));
		zoneList.put(FieldZone.EAST_CENTER,
				new Zone("center",
				new Point[] {makeXReverse(fieldLocs[5]), makeXReverse(fieldLocs[6]), makeXReverse(fieldLocs[9]), makeXReverse(fieldLocs[8]) },length,width));
		zoneList.put(FieldZone.EAST_RIGHT_SECOND_POST,
				new Zone("rightSecondPost", 
				new Point[] {makeXReverse(fieldLocs[8]), makeXReverse(fieldLocs[9]), makeXReverse(fieldLocs[14]), makeXReverse(fieldLocs[12]) },length,width));
		zoneList.put(FieldZone.EAST_RIGHT_CORNER,
				new Zone("rightCorner", 
				new Point[] {makeXReverse(fieldLocs[11]), makeXReverse(fieldLocs[12]), makeXReverse(fieldLocs[14]), makeXReverse(fieldLocs[13]) },length,width));

		//east row two (mid)
		zoneList.put(FieldZone.EAST_LEFT_FRONT,
				new Zone("leftFront",
				new Point[] {makeXReverse(fieldLocs[1]), makeXReverse(fieldLocs[2]), makeXReverse(fieldLocs[7]), makeXReverse(fieldLocs[6]) },length,width));
		zoneList.put(FieldZone.EAST_MIDDLE,
				new Zone("middle",
				new Point[] {makeXReverse(fieldLocs[6]), makeXReverse(fieldLocs[7]), makeXReverse(fieldLocs[10]), makeXReverse(fieldLocs[9]) },length,width));
		zoneList.put(FieldZone.EAST_RIGHT_FRONT,
				new Zone("rightFront",
				new Point[] {makeXReverse(fieldLocs[9]), makeXReverse(fieldLocs[10]), makeXReverse(fieldLocs[15]), makeXReverse(fieldLocs[14]) },length,width));
		
		
		//goal area
		zoneList.put(FieldZone.EAST_GOAL_LEFT,
				new Zone("goal left", 
				new Point[] { makeXReverse(fieldLocs[3]),makeXReverse(fieldLocs[4]),makeXReverse(fieldLocs[5]),makeXReverse(fieldLocs[17]),makeXReverse(fieldLocs[16])},length,width));
		zoneList.put(FieldZone.EAST_GOAL_RIGHT,
				new Zone("goal right",
				new Point[] { makeXReverse(fieldLocs[16]),makeXReverse(fieldLocs[17]),makeXReverse(fieldLocs[8]),makeXReverse(fieldLocs[12]), makeXReverse(fieldLocs[11]) },length,width));

	}
	
	private Point makeXReverse(Point point) {
		return new Point(6050 - point.getX(), 4050 - point.getY()); 
	}
	
	/**
	 * constructor which declares all variables, makes it messy so its preferred to use the default constructor, and fill it in later with the given methods
	 * @param length
	 * @param width
	 * @param lineWidth
	 * @param boundaryWidth
	 * @param refereeWidth
	 * @param centerCircleRadius
	 * @param defenceRadius
	 * @param defenceStretch
	 * @param freeKickFromDefenceDistance
	 * @param penaltySpotFromFieldLineDistance
	 * @param penaltyLineFromSpotDistance
	 * @param goalWidth
	 * @param goalDepth
	 * @param goalWallWidth
	 * @param goalHeight
	 * @param cameraOverlapZoneWidth
	 */
	public Field(int length, int width, int lineWidth, int boundaryWidth, int refereeWidth, int centerCircleRadius,
			int defenceRadius, int defenceStretch, int freeKickFromDefenceDistance,
			int penaltySpotFromFieldLineDistance, int penaltyLineFromSpotDistance, int goalWidth, int goalDepth,
			int goalWallWidth, int goalHeight, int cameraOverlapZoneWidth) {

		this.length = length;
		this.width = width;
		this.lineWidth = lineWidth;
		this.boundaryWidth = boundaryWidth;
		this.refereeWidth = refereeWidth;
		this.centerCircleRadius = centerCircleRadius;
		this.defenceRadius = defenceRadius;
		this.defenceStretch = defenceStretch;
		this.freeKickFromDefenceDistance = freeKickFromDefenceDistance;
		this.penaltySpotFromFieldLineDistance = penaltySpotFromFieldLineDistance;
		this.penaltyLineFromSpotDistance = penaltyLineFromSpotDistance;
		this.goalWidth = goalWidth;
		this.goalDepth = goalDepth;
		this.goalWallWidth = goalWallWidth;
		this.goalHeight = goalHeight;
		this.cameraOverlapZoneWidth = cameraOverlapZoneWidth;
		createGoals(goalWidth, goalDepth, goalWallWidth, goalHeight);	
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
		Point frontLeft = new Point(length / 2, goalWidth / 2);
		Point frontRight = new Point(length / 2, goalWidth / -2);
		Point backLeft = new Point(frontLeft.getX() + goalDepth, frontLeft.getY());
		Point backRight = new Point(frontRight.getX() + goalDepth, frontRight.getY());

		// Adding Goal A
		eastGoal = new Goal(frontLeft, frontRight, backLeft, backRight, goalWallWidth, goalHeigth);

		// Adding Goal B
		westGoal = new Goal(frontLeft.diagMirror(), frontRight.diagMirror(), backLeft.diagMirror(), backRight
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

	public Map<FieldZone, Zone> getZones () {
		return zoneList;
	}

	public Zone getZone(FieldZone fieldZone) {
		return zoneList.get(fieldZone);
	}
	
	public FieldZone locateObject(FieldObject argObject) {
		for (Map.Entry<FieldZone, Zone> entry : zoneList.entrySet())
		{	
			if (entry.getValue().contains(argObject.getPosition()))
				return entry.getKey();
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
