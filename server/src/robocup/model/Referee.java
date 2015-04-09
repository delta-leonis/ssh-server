package robocup.model;

import java.util.ArrayList;

import robocup.model.enums.Command;
import robocup.model.enums.Stage;
import robocup.model.enums.TeamColor;

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

	private final int PLAYING_TEAM_SIZE = 8;

	private Team allyTeam;
	private Team enemyTeam;
	private Team eastTeam;
	private Team westTeam;

	/**
	 * constructor that initializes the default values, takes no argument
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
		allyTeam = new Team("", TeamColor.BLUE, PLAYING_TEAM_SIZE);
		allyTeam.setRobots(teamRobots);
		eastTeam = allyTeam;
	}

	/**
 	 * Method that accepts a list with robots, and adds them to the robotlist of the enemy team
	 * @param teamRobots the robots from the world mode class
	 */
	public void initEnemyTeam(ArrayList<Robot> teamRobots) {
		enemyTeam = new Team("", TeamColor.YELLOW, PLAYING_TEAM_SIZE);
		enemyTeam.setRobots(teamRobots);
		westTeam = enemyTeam;
	}

	/**
	 * a method that is called every time a protobuff message arrives from the handler
	 * the arguments send the new declared values
	 * @param command the referee command enumeration,  every time there is a change it will be handled
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
	 * getter method that returns the id number of the last command
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
	 * getter method that returns the timestamp of the last command
	 * @return the lastCommandTimestamp
	 */
	public long getLastCommandTimestamp() {
		return lastCommandTimestamp;
	}

	/**
	 * setter method that sets the timestamp for the last command
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
	public Team getTeamByColor(TeamColor color) {
		if (allyTeam.isColor(color))
			return allyTeam;
		else if (enemyTeam.isColor(color))
			return enemyTeam;

		return null;
	}

	/**
	 * Sets the Color for our own Team.
	 * @param color
	 * 
	 */
	public void setAllyTeamColor(TeamColor color) {
		if (color == TeamColor.BLUE) {
			allyTeam.setColor(TeamColor.BLUE);
			enemyTeam.setColor(TeamColor.YELLOW);
		} else {
			allyTeam.setColor(TeamColor.YELLOW);
			enemyTeam.setColor(TeamColor.BLUE);
		}
	}

	/**
	 * Returns the color of your own team.
	 * @return 
	 */
	public TeamColor getAllyTeamColor() {
		return allyTeam.getColor();
	}

	/**
	 * getter for the team object of our own team
	 * @return the ally {@link Team} in the current match.
	 */
	public Team getAlly() {
		return allyTeam;
	}

	/**
	 * getter for the team object of our enemy team
	 * @return the enemy {@link Team} in the current match.
	 */
	public Team getEnemy() {
		return enemyTeam;
	}
	
	/**
	 * @param color The {@link TeamColor} that will be compared
	 * @return if the {@link Team} that plays east has the given {@link TeamColor}
	 */
	public boolean isEastTeamColor(TeamColor color) {
		return color == getEastTeam().getColor();
	}

	/**
	 * @param color The {@link TeamColor} that will be compared
	 * @return if the {@link Team} that plays west has the given {@link TeamColor}
	 */
	public boolean isWestTeamColor(TeamColor color) {
		return color == getWestTeam().getColor();
	}

	/**
	 * @return return {@link Team} that plays east
	 */
	public Team getEastTeam() {
		return eastTeam;
	}

	/**
	 * @return return {@link Team} that plays west
	 */
	public Team getWestTeam() {
		return westTeam;
	}

	/**
	 * Set {@link Team} that plays east
	 * @param Team to be set east
	 */
	public void setEastTeam(Team team){
		if(team.equals(allyTeam)){
			eastTeam = allyTeam;
			westTeam = enemyTeam;
		}
		else {
			eastTeam = enemyTeam;
			westTeam = allyTeam;
		}
	}

	/**
	 * Set {@link Team} that plays west
	 * @param Team to be set west
	 */
	public void setWestTeam(Team team){
		if(team.equals(allyTeam)){
			westTeam = allyTeam;
			eastTeam = enemyTeam;
		}
		else {
			westTeam = enemyTeam;
			eastTeam = allyTeam;
		}
	}

	//public TeamColor get
	@Override
	public String toString() {
		return "Referee [timeoutTimeLeft=" + timeoutTimeLeft + ", stagetimeLeft=" + stagetimeLeft + ", stage=" + stage
				+ ", command=" + command + ", commandCounter=" + commandCounter + ", lastCommandTimestamp="
				+ lastCommandTimestamp + "]" + "\r\n";
	}

}
