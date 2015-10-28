package model;

import javafx.scene.paint.Color;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 * 
 * @author Jeroen
 *
 */
public class Robot extends FieldObject {
	/**
	 * Unique robot id [0-15]
	 */
	private transient Integer robotId;
	/**
	 * teamcolor that controls this robot
	 */
	private Color teamColor;
	
	/**
	 * Instansiates a new robot with specified properties
	 * 
	 * @param robotId	robot id
	 * @param teamColor	color that controls this robot
	 */
	public Robot(Integer robotId, Color teamColor) {
		super("robot", ""); //TODO refactor this call
		//assign teamcolor
		this.teamColor = teamColor;
		this.robotId = robotId;
		setSuffix(getTeamColorIdentifier() + robotId);
	}

	/**
	 * @return robot ID [0-15]
	 */
	public Integer getRobotId(){
		return robotId;
	}

	/**
	 * @return color of team that controls this robot
	 */
	public Color getTeamColor(){
		return teamColor;
	}

	/**
	 * @return a char that identifies this robot as B(lue) or Y(ellow)
	 */
	public String getTeamColorIdentifier(){
		return teamColor.getBlue() > 0 ? "B" : "Y";
	}
	
	/**
	 * example: model.RobotB2.json for robot with ID 2 and teamColor Blue
	 * @see {@link Robot#getTeamColor()}
	 * @return Config name for robot models. 
	 */
	@Override
	public String getConfigName(){
		return this.getClass().getName() + getTeamColorIdentifier() + robotId + ".json";
	}
}