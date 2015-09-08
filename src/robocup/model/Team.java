package robocup.model;

import java.util.ArrayList;

import robocup.controller.handlers.protohandlers.RefereeHandler;
import robocup.model.enums.TeamColor;

/**
 * Represents a Team in a Robocup match.
 */
public class Team {
	private String name;
	private TeamColor color;
	private int score;
	private int timeoutsLeft;
	private ArrayList<Integer> remainingCardTimes;
	private int yellowCards;
	private int redCards;
	private ArrayList<Robot> robots;
	private int goalie;		//id of the keeper robot
	
	@SuppressWarnings("unused")
	private int onsiteTeamSize;

	public Team(String name, TeamColor color, int onsiteTeamSize) {
		this.name = name;
		this.color = color;
		this.onsiteTeamSize = onsiteTeamSize;
		
		remainingCardTimes = new ArrayList<Integer>();
		robots = new ArrayList<Robot>();
	}
	
	/**
	 * Constructs a new Team
	 * @param name			name of a team
	 * @param color			{@link TeamColor} of a team
	 * @param yellowCards	number of yellow cards
	 * @param redCards		number of red cards
	 * @param score			score for the team
	 * @param timeoutsLeft	timeouts remaining
	 * @param goalie		{@link Robot} ID that represents the goalie
	 */
	public Team(String name, TeamColor color, int yellowCards, ArrayList<Integer> remainingYellowCardTimes, int redCards, int score, int timeoutsLeft, int goalie) {
		this.name = name;
		this.color = color;
		this.yellowCards = yellowCards;
		this.redCards = redCards;
		this.score = score;
		this.timeoutsLeft = timeoutsLeft;
		this.goalie = goalie;
		remainingCardTimes = remainingYellowCardTimes;
		robots = new ArrayList<Robot>();
	}

	/**
	 * update all fields (see {@link RefereeHandler})
	 * @param name			name of a team
	 * @param yellowCards	number of yellow cards
	 * @param redCards		number of red cards
	 * @param score			score for the team
	 * @param timeoutsLeft	timeouts remaining
	 * @param goalie		{@link Robot} ID that represents the goalie
	 */
	public void update(String name, int score, int redCards, int yellowCards, ArrayList<Integer> remainingYellowCardTimes, int timeoutsLeft, int goalie) {
		this.name = name;
		this.score = score;
		this.redCards = redCards;
		this.yellowCards = yellowCards;
		this.remainingCardTimes = remainingYellowCardTimes;
		this.timeoutsLeft = timeoutsLeft;
		this.goalie = goalie;
	}

	/**
	 * @return the id of the goalie of this team. (Robot keeper)
	 */
	public int getGoalie() {
		return goalie;
	}

	/**
	 * Sets the goalie of the Team. 
	 * @param goalie: the id of the goalie to of this team. (Robot keeper)
	 */
	public void setGoalie(int goalie) {
		this.goalie = goalie;
	}

	/**
	 * @return the name of the Team
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Team
	 * @param name: the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return color of this Team
	 */
	public TeamColor getColor() {
		return color;
	}

	/**
	 * Sets the Color of this team.
	 * @param color: The Color you want this Team to have.
	 */
	public void setColor(TeamColor color) {
		this.color = color;
	}

	/**
	 * @return the score this Team has currently.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Sets the score of this team
	 * @param score: the new score for this Team
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the amount of timeouts you may still call. Prevents games from lasting too long.
	 */
	public int getTimeoutsLeft() {
		return timeoutsLeft;
	}

	/**
	 * Sets the new amount of timeouts the Team may still call.
	 * @param timeoutsLeft: The timeouts the Team may still call.
	 */
	public void setTimeoutsLeft(int timeoutsLeft) {
		this.timeoutsLeft = timeoutsLeft;
	}

	/**
	 * Sets the yellow cards for this team. If this ArrayList contains two cards, a robot must be removed from the field.
	 * @param remainingCardTimes: the new ArrayList with yellow card. This ArrayList contains Longs representing microseconds.
	 * Suggestion: Rename to setYellowCards();
	 * TODO: It is not sure what these microseconds stand for. Perhaps the time a Robot is removed from the game?
	 */
	public void setRemainingCardTimes(ArrayList<Integer> remainingCardTimes) {
		this.remainingCardTimes = remainingCardTimes;
	}
	
	/**
	 * A Team can be penalized by giving them a yellow card.
	 * Two yellow cards means that a robot must be removed from the game.
	 * @return An ArrayList with Longs representing microseconds. 
	 * Suggestion: Rename to getYellowCards()
	 * TODO: It is not sure what these microseconds stand for. Perhaps the time a Robot is removed from the game?
	 */
	public ArrayList<Integer> getRemainingCardTimes() {
		return remainingCardTimes;
	}


	/**
	 * a function that returns the ammount of yellow cards that are currently in effect.
	 * if all the given yellow cards from the game are required, use the function getYellowCards
	 * @return ammount of yellow cards currently in play
	 */
	public int getCurrentYellowCards() {
		return remainingCardTimes.size();
	}
	
	/**
	 * a function that retrieves the remaining time of a specific yellow card 
	 * @param index the index of the location of the yellow card time in the arrayList
	 * @return the time that this card remains in effect
	 */
	public int getCurrentYellowCardTime(int index) {
		return remainingCardTimes.get(index);
	}

	/**
	 * @return the total ammount of yellow cards this team received
	 * Suggestion: Removal. getRemainingCardTimes does what this function should be doing, already
	 */
	public int getYellowCards() {
		return yellowCards;
	}

	/**
	 * @param yellowcards the setter of the total yellow card variable
	 */
	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	/**
	 * A red card can occur when a Robot breaks down and cannot be fixed within 20 seconds.
	 * @return the redcards assigned to this Team.
	 */
	public int getRedCards() {
		return redCards;
	}

	/**
	 * A red card can occur when a Robot breaks down and cannot be fixed within 20 seconds.
	 * @param redcards: The new amount of red cards this team has.
	 */
	public void setRedCards(int redCards) {
		this.redCards = redCards;
	}

	/**
	 * @return the {@link Robot robots} in this Team that are on sight 
	 */
	public ArrayList<Robot> getRobotsOnSight(){
		ArrayList<Robot> onsight = new ArrayList<Robot>();
		for( Robot robot : robots){
			if(robot.getPosition() != null){
				onsight.add(robot);
			}
			else if(robot.isOnSight())
				onsight.add(robot);
		}
		return onsight;
	}
	
	public void setRobots(ArrayList<Robot> robots) {
		this.robots = robots;
	}
	
	/**
	 * @return all {@link Robot robots} in this Team. Online and offline !
	 */
	public ArrayList<Robot> getRobots() {
		return robots;
	}
 
	/**
	 * Set a robot on- or offline
	 * @param robotId the ID of the {@link Robot} to set onsight
	 * @param onsight whether the robot is onsight
	 */
	public void setOnsight(int robotId, boolean onSight){
		for (Robot robot : robots) {
			if (robot.getRobotId() == robotId) {
				robot.setOnSight(onSight);
				return;
			}
		}
	}

	/**
	 * Returns the {@link Robot} with the given id.
	 * @param id The id of the {@link Robot} you want to obtain.
	 * @return The {@link Robot} with the given id. Returns null if the Robot doesn't exist.
	 */
	public Robot getRobotByID(int id) {
		for (Robot robot : robots) {
			if (robot.getRobotId() == id) {
				return robot;
			}
		}
		return null;
	}
	
	/**
	 * Checks whether this Team is of the given color
	 * @return true if this Team is of the given color, false otherwise.  Used in {@link World#getTeamByColor(TeamColor color) World.getTeamByColor(Color)}
	 */
	public boolean isColor(TeamColor color){
		return color.equals(this.color);
	}

	@Override
	public String toString() {
		return "Team [name=" + name + ", color=" + color + ", score=" + score + ", timeoutsLeft=" + timeoutsLeft
				+ ", remainingCardTimes=" + remainingCardTimes + ", yellowCards=" + yellowCards + ", redCards="
				+ redCards + ", robots=\r\n" + printRobots() + "]" + "\r\n";
	}

	/**
	 * @returns a string the description for every {@link Robot} in this Team.
	 * Suggestion: Rename to getRobotDescriptions(), since this function doesn't print.
	 */
	public String printRobots() {
		String robotString = "";

		for (Robot robot : robots) {
			robotString += "\n" + robot;
 		}

		return robotString;
	}
}
