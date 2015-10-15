package model;

import javafx.scene.paint.Color;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 * @author Jeroen
 *
 */
public class Robot extends FieldObject{
	/**
	 * Unique robot id [0-15]
	 */
	private int robotId;
	/**
	 * teamcolor that controls this robot
	 */
	private Color teamColor;
	/**
	 * State of the dribbler
	 */
	private float dribbleSpeed;
	
	/**
	 * Instansiates a new robot with specified properties
	 * 
	 * @param robotId	robot id
	 * @param teamColor	color that controls this robot
	 */
	public Robot(int robotId, Color teamColor) {
		super("robot", robotId + "");
		//assign teamcolor
		this.teamColor = teamColor;
	}

	/**
	 * @return robot ID [0-15]
	 */
	public int getRobotId(){
		return robotId;
	}

	/**
	 * @return color of team that controls this robot
	 */
	public Color getTeamColor(){
		return teamColor;
	}

	/**
	 * sets the current speed of the dribbler for a robot
	 * @param speed
	 */
	public void setDribbleSpeed(float speed){
		dribbleSpeed = speed;
	}
	
	/**
	 * gets the current speed of the dribbler for a robot
	 * @param speed
	 */
	public float getDribbleSpeed(){
		return dribbleSpeed;
	}
}