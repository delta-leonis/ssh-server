package robocup.model;

import java.util.ArrayList;

import robocup.model.enums.Color;
import robocup.model.enums.Command;
import robocup.model.enums.Stage;

/**
 * Model representation of the "game", including the teams and rules
 * @author jasper
 *
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
	

	public Referee() {
		commandCounter = 0;
		lastCommandTimestamp = 0;
		command = Command.STOP;
		stage = Stage.POST_GAME;
	}
	
	public void initAllyTeam(ArrayList<Robot> teamRobots) {
		ourTeam = new Team("", Color.BLUE, PLAYING_TEAM_SIZE);
		ourTeam.setRobots(teamRobots);
	}
	public void initEnemyTeam(ArrayList<Robot> teamRobots) {
		enemyTeam = new Team("", Color.YELLOW, PLAYING_TEAM_SIZE);
		enemyTeam.setRobots(teamRobots);
	}
	
	public void update(Command command, int commandCounter, long commandTimeStamp, Stage stage, int stageTimeLeft) {
		this.command = command;
		this.commandCounter = commandCounter;
		this.lastCommandTimestamp = commandTimeStamp;
		this.stagetimeLeft = stageTimeLeft;
		this.stage = stage;
	}

	/**
	 * @return the stage
	 */
	public Enum<Stage> getStage() {
		return stage;
	}

	public boolean isStage(Stage controlStage) {
		return controlStage == stage;
	}
	
	/**
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
	 * @return the stagetimeLeft
	 */
	public int getStagetimeLeft() {
		return stagetimeLeft;
	}

	/**
	 * @param stagetimeLeft the stagetimeLeft to set
	 */
	public void setStagetimeLeft(int stagetimeLeft) {
		this.stagetimeLeft = stagetimeLeft;
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @return the commandCounter
	 */
	public int getCommandCounter() {
		return commandCounter;
	}

	/**
	 * @param commandCounter the commandCounter to set
	 */
	public void setCommandCounter(int commandCounter) {
		this.commandCounter = commandCounter;
	}

	/**
	 * @return the lastCommandTimestamp
	 */
	public long getLastCommandTimestamp() {
		return lastCommandTimestamp;
	}

	/**
	 * @param lastCommandTimestamp the lastCommandTimestamp to set
	 */
	public void setLastCommandTimestamp(long lastCommandTimestamp) {
		this.lastCommandTimestamp = lastCommandTimestamp;
	}

	public boolean isStart() {
		return start;
	}

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
	 * @return the ally {@link Team} in the current match.
	 */
	public Team getAlly() {
		return ourTeam;
	}

	/**
	 * @return the enemy {@link Team} in the current match.
	 */
	public Team getEnemy() {
		return enemyTeam;
	}
	
	public void setRightTeamByColor(Color color) {
		yellowTeamPlaysRight = (color == Color.YELLOW);
	}

	public boolean getDoesTeamPlaysRight(Color color) {
		boolean teamIsYellow = (color == Color.YELLOW);
		if (teamIsYellow) {
			return yellowTeamPlaysRight;
		} else {
			return !yellowTeamPlaysRight;
		}
	}
	
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
