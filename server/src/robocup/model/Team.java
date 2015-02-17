package robocup.model;

import java.util.ArrayList;

import robocup.model.enums.Color;

/**
 * Represents a Team in a Robocup match.
 */
public class Team {

	private String name;
	private Color color;
	private int score;
	private int timeoutsLeft;
	private ArrayList<Long> remainingCardTimes;
	private int yellowCards;
	private int redCards;
	private ArrayList<Robot> robots;
	private int goalie;		//id of the keeper robot

	public Team(String name, Color color) {
		this.name = name;
		this.color = color;
		remainingCardTimes = new ArrayList<Long>();
		robots = new ArrayList<Robot>();
	}

	public Team(String name, Color color, int yellowCards, int redCards, int score, int timeoutsLeft, int goalie) {
		this.name = name;
		this.color = color;
		this.yellowCards = yellowCards;
		this.redCards = redCards;
		this.score = score;
		this.timeoutsLeft = timeoutsLeft;
		this.goalie = goalie;
		remainingCardTimes = new ArrayList<Long>();
		robots = new ArrayList<Robot>();
	}

	public void update(String name, int score, int redCards, int yellowCards, int timeoutsLeft, int goalie) {
		this.name = name;
		this.score = score;
		this.redCards = redCards;
		this.yellowCards = yellowCards;
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
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the Color of this team.
	 * @param color: The Color you want this Team to have.
	 */
	public void setColor(Color color) {
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
	 * A Team can be penalized by giving them a yellow card.
	 * Two yellow cards means that a robot must be removed from the game.
	 * @return An ArrayList with Longs representing microseconds. 
	 * Suggestion: Rename to getYellowCards()
	 * TODO: It is not sure what these microseconds stand for. Perhaps the time a Robot is removed from the game?
	 */
	public ArrayList<Long> getRemainingCardTimes() {
		return remainingCardTimes;
	}

	/**
	 * Sets the yellow cards for this team. If this ArrayList contains two cards, a robot must be removed from the field.
	 * @param remainingCardTimes: the new ArrayList with yellow card. This ArrayList contains Longs representing microseconds.
	 * Suggestion: Rename to setYellowCards();
	 * TODO: It is not sure what these microseconds stand for. Perhaps the time a Robot is removed from the game?
	 */
	public void setRemainingCardTimes(ArrayList<Long> remainingCardTimes) {
		this.remainingCardTimes = remainingCardTimes;
	}

	/**
	 * @return the yellowcards
	 * Suggestion: Removal. getRemainingCardTimes does what this function should be doing, already
	 */
	public int getYelloCcards() {
		return yellowCards;
	}

	/**
	 * @param yellowcards the yellowcards to set
	 * Suggestion: Removal. setRemainingCardTimes does what this function should be doing, already.
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
			if(robot.isOnSight())
				onsight.add(robot);
		}
		return onsight;
	}
	
	/**
	 * @return all {@link Robot robots} in this Team. Online and offline !
	 */
	public ArrayList<Robot> getRobots() {
		return robots;
	}

	/**
	 * Adds a {@link Robot} to this Team.
	 * @param robot the Robot you would like to add.
	 * @deprecated Team members should not be added, only set to online
	 */
	public void addRobot(Robot robot) {
		robots.add(robot);
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
	 * Removes a {@link Robot} from this Team.
	 * @param robotId the ID of the {@link Robot} you would like to remove from this Team.
	 * @deprecated Team members should not be removed, only set to offline
	 */
	public void removeRobot(int robotId) {
		for (Robot robot : robots) {
			if (robot.getRobotId() == robotId) {
				robots.remove(robot);
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
	 * @return true if this Team is of the given color, false otherwise.  Used in {@link World#getTeamByColor(Color color) World.getTeamByColor(Color)}
	 */
	public boolean isTeamColor(Color color){
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
