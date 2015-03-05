package robocup.model;

import java.util.ArrayList;

import robocup.model.enums.Color;
import robocup.model.enums.Command;
import robocup.model.enums.Stage;

/**
 * Model representation of the "game", including the teams and rules
 */
public class Referee {

	private long timeoutTimeLeft;
	private int stagetimeLeft;
	private Stage stage;
	private Command command;
	private int commandCounter;
	private long lastCommandTimestamp;
	private boolean start;
	private boolean yellowTeamPlaysRight;

	private final int PLAYING_TEAM_SIZE = 8;

	private Team ourTeam;
	private Team enemyTeam;

	/**
	 * constructor that initialises the default values, takes no argument
	 * most variables remain undeclared
	 */
	public Referee() {
		commandCounter = 0;
		lastCommandTimestamp = 0;
		command = Command.STOP;
		stage = Stage.POST_GAME;
	}

	/**
	 * Method that accepts a list with robots, and adds them to the robotlist of the ally team
	 * @param teamRobots the robots from the world mode class
	 */
	public void initAllyTeam(ArrayList<Robot> teamRobots) {
		ourTeam = new Team("", Color.BLUE, PLAYING_TEAM_SIZE);
		ourTeam.setRobots(teamRobots);
	}

	/**
 	 * Method that accepts a list with robots, and adds them to the robotlist of the enemy team
	 * @param teamRobots the robots from the world mode class
	 */
	public void initEnemyTeam(ArrayList<Robot> teamRobots) {
		enemyTeam = new Team("", Color.YELLOW, PLAYING_TEAM_SIZE);
		enemyTeam.setRobots(teamRobots);
	}

	/**
	 * a method that is called every time a protobuff message arrives from the hadlerr
	 * the arguments send the new declared values
	 * @param command the referee command enumeration,  everytime there is a change it wil be handled
	 * @param commandCounter the id of the command, identifies commands from each other
	 * @param commandTimeStamp the time that the previous command had been send
	 * @param stage the current stage of the game
	 * @param stageTimeLeft the time left for the current stage
	 */
	public void update(Command command, int commandCounter, long commandTimeStamp, Stage stage, int stageTimeLeft) {
		this.command = command;
		this.commandCounter = commandCounter;
		this.lastCommandTimestamp = commandTimeStamp;
		this.stagetimeLeft = stageTimeLeft;
		this.stage = stage;
	}

	/**
	 * @return returns the current stage
	 */
	public Enum<Stage> getStage() {
		return stage;
	}

	/**
	 * checks the current stage with the given argument
	 * @param controlStage the argument to be compared to the stage
	 * @return if the stage is the same as the given argument
	 */
	public boolean isStage(Stage controlStage) {
		return controlStage == stage;
	}

	/**
	 * method that returns the current timeout time for given yellow cards
	 * @return the timeoutTimeLeft
	 */
	public long getTimeoutTimeLeft() {
		return timeoutTimeLeft;
	}

	/**
	 * @param timeoutTimeLeft the timeoutTimeLeft to set
	 */
	public void setTimeoutTimeLeft(long timeoutTimeLeft) {
		this.timeoutTimeLeft = timeoutTimeLeft;
	}

	/**
	 * getter that returns the time that remains for the current stage of the game
	 * @return the stagetimeLeft
	 */
	public int getStagetimeLeft() {
		return stagetimeLeft;
	}

	/**
	 * method that sets the time that remains for the current stage of the game
	 * @param stagetimeLeft the stagetimeLeft to set
	 */
	public void setStagetimeLeft(int stagetimeLeft) {
		this.stagetimeLeft = stagetimeLeft;
	}

	/**
	 * getter method that returns the last command given by the referee
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * getter method thhat returns the id number of the last command
	 * @return the commandCounter
	 */
	public int getCommandCounter() {
		return commandCounter;
	}

	/**
	 * sets the counter of the referee commands
	 * @param commandCounter the commandCounter to set
	 */
	public void setCommandCounter(int commandCounter) {
		this.commandCounter = commandCounter;
	}

	/**
	 * getter method that returns the time of the last commandstamp
	 * @return the lastCommandTimestamp
	 */
	public long getLastCommandTimestamp() {
		return lastCommandTimestamp;
	}

	/**
	 * setter method that sets the comandstamptime
	 * @param lastCommandTimestamp the lastCommandTimestamp to set
	 */
	public void setLastCommandTimestamp(long lastCommandTimestamp) {
		this.lastCommandTimestamp = lastCommandTimestamp;
	}

	/**
	 * method that returns if the game has started
	 * @return
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * method that set the game as started (or stopped)
	 * @param start
	 */
	public void setStart(boolean start) {
		this.start = start;
	}

	/**
	 * Returns the {@link Team} with the given color.
	 * @param color the color of the {@link Team}
	 * @return the {@link Team} with the given color. Returns null if there is no {@link Team} with the given color.
	 */
	public Team getTeamByColor(Color color) {
		if (ourTeam.isColor(color))
			return ourTeam;
		else if (enemyTeam.isColor(color))
			return enemyTeam;

		return null;
	}

	/**
	 * Sets the Color for our own Team.
	 * Suggestion: Rename to setAllyTeamColor()
	 * @param color
	 * 
	 */
	public void switchAllyTeamColor(Color color) {
		if (color == Color.BLUE) {
			ourTeam.setColor(Color.BLUE);
			enemyTeam.setColor(Color.YELLOW);
		} else {
			ourTeam.setColor(Color.YELLOW);
			enemyTeam.setColor(Color.BLUE);
		}
	}

	/**
	 * Returns the color of your own team.
	 * Suggestion: Rename to getAllyTeamColor()
	 * @return 
	 */
	public Color getOwnTeamColor() {
		return ourTeam.getColor();
	}

	/**
	 * getter for the team object of our own team
	 * @return the ally {@link Team} in the current match.
	 */
	public Team getAlly() {
		return ourTeam;
	}

	/**
	 * getter for the team object of our enemy team
	 * @return the enemy {@link Team} in the current match.
	 */
	public Team getEnemy() {
		return enemyTeam;
	}

	/**
	 * setter function that sets the team that plays on the right side, the color identifies the team
	 * @param color the color of the team that has to play on the right side of the field
	 */
	public void setRightTeamByColor(Color color) {
		yellowTeamPlaysRight = (color == Color.YELLOW);
	}

	/**
	 * comparison method that returns if the team that plays on the right side has the same color as the given argument
	 * @param color the color that will be compared
	 * @return whether or not the team on the right has the given color
	 */
	public boolean getDoesTeamPlaysRight(Color color) {
		boolean teamIsYellow = (color == Color.YELLOW);
		if (teamIsYellow) {
			return yellowTeamPlaysRight;
		} else {
			return !yellowTeamPlaysRight;
		}
	}

	/**
	 * comparison method that returns if the team that plays on the left side has the same color as the given argument
	 * @param color the color that will be compared
	 * @return whether or not the team on the right has the given color
	 */
	public boolean getDoesTeamPlaysLeft(Color color) {
		boolean teamIsYellow = (color == Color.YELLOW);
		if (teamIsYellow) {
			return !yellowTeamPlaysRight;
		} else {
			return yellowTeamPlaysRight;
		}
	}

	@Override
	public String toString() {
		return "Referee [timeoutTimeLeft=" + timeoutTimeLeft + ", stagetimeLeft=" + stagetimeLeft + ", stage=" + stage
				+ ", command=" + command + ", commandCounter=" + commandCounter + ", lastCommandTimestamp="
				+ lastCommandTimestamp + "]" + "\r\n";
	}

}
