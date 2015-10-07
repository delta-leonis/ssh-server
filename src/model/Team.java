package model;

import java.util.ArrayList;

import javafx.scene.paint.Color;

import model.enums.Direction;
import protobuf.RefereeOuterClass.Referee.TeamInfo;

/**
 * Describes a team<br />
 * NOTE: teams don't contain a list with all robots, since the robots which a team controls can change during runtime. 
 * The robots should be accessed from {@link Models} class or via the {@link World}
 * 
 * @author Jeroen
 * @see Models
 * @see World
 *
 */
public class Team extends Model{
	/**
	 * Side that this team plays on as a cardinal direction
	 */
	private Direction direction;
	/**
	 * color that this team controls 
	 */
	private Color teamColor;

	/**
	 * teamname given by {@link Referee}
	 */
	private String teamName;

	/**
	 * log with recieved cards and score, index is the count-value and Object is the timestamp
	 */
	private ArrayList<Long> yellowCards = new ArrayList<Long>(),
							redCards 	= new ArrayList<Long>(),
							score 		= new ArrayList<Long>();

	/**
	 * team properties as tiem left for timeouts, number of timeouts and the goalie ID
	 */
	private int goalieId, timeoutLeft, timeouts;
	
	
	/**
	 * Instantiates a new team that plays on a specified field half
	 * 
	 * @param direction field half that this team plays on
	 * @param teamcolor the color of the {@link Robot robots} that this team controls
	 */
	public Team(Direction direction, Color teamColor) {
		super("team", String.format("%s %s",  direction.name(), teamColor.toString()));
		this.direction = direction;
		this.teamColor = teamColor;
	}

	/**
	 * @return Side that this team plays on as a cardinal direction
	 */
	public Direction getDirection(){
		return direction;
	}
	
	/**
	 * inverts the side that this team plays on based on {@link Direction Direction.getOpposite()}
	 */
	public void swapDirection(){
		this.direction = direction.getOpposite();
	}
	
	/**
	 * Inverst the teamcolor, so BLUE becomes YELLOW and vice versa
	 */
	public void swapColor(){
		this.teamColor = teamColor == Color.BLUE ? Color.YELLOW : Color.BLUE;
	}
	
	/**
	 * @return color that this team controls
	 */
	public Color getTeamColor(){
		return teamColor;
	}

	/**
	 * update a team with the new data
	 * 
	 * @param newData new data provided by refbox
	 */
	public void update(TeamInfo newData) {
		if(yellowCards.size() < newData.getYellowCards())
			yellowCards.add(System.currentTimeMillis());
		if(redCards.size() < newData.getRedCards())
			redCards.add(System.currentTimeMillis());
		if(score.size() < newData.getScore())
			score.add(System.currentTimeMillis());

		setTeamName(newData.getName());
		setGoalieId(newData.getGoalie());
		setTimeouts(newData.getTimeouts());
		setTimeoutLeft(newData.getTimeoutTime());
	}

	/**
	 * @return returns goalie id for this team
	 */
	public int getGoalieId() {
		return goalieId;
	}

	/**
	 * sets a goalie id for this team
	 * @param goalieId
	 */
	public void setGoalieId(int goalieId) {
		this.goalieId = goalieId;
	}

	/**
	 * @return time left for timeouts for this team
	 */
	public int getTimeoutLeft() {
		return timeoutLeft;
	}

	/**
	 * sets time left for timeouts for this team
	 * @param timeoutLeft
	 */
	public void setTimeoutLeft(int timeoutLeft) {
		this.timeoutLeft = timeoutLeft;
	}

	/**
	 * @return gets number of timeouts
	 */
	public int getTimeouts() {
		return timeouts;
	}

	/**
	 * sets number of timeouts
	 * @param timeouts
	 */
	public void setTimeouts(int timeouts) {
		this.timeouts = timeouts;
	}

	/**
	 * @return teamname as provided by the refbox
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * sets a new teamname as provided by the refbox
	 * @param teamName
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
}