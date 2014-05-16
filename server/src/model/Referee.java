package model;

import model.enums.Command;
import model.enums.Stage;

public class Referee {

	private long timeoutTimeLeft;
	private int stagetimeLeft;
	private Stage stage;
	private Command command;
	private int commandCounter;
	private long lastCommandTimestamp;

	public Referee() {
		commandCounter = 0;
		lastCommandTimestamp = 0;
	}

	public void update(Command command, int commandCounter, long commandTimeStamp, Stage stage) {
		this.command = command;
		this.commandCounter = commandCounter;
		this.lastCommandTimestamp = commandTimeStamp;
		this.stage = stage;
	}

	/**
	 * @return the stage
	 */
	public Enum<Stage> getStage() {
		return stage;
	}

	/**
	 * @return the timeoutTimeLeft
	 */
	public long getTimeoutTimeLeft() {
		return timeoutTimeLeft;
	}

	/**
	 * @param timeoutTimeLeft
	 *            the timeoutTimeLeft to set
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
	 * @param stagetimeLeft
	 *            the stagetimeLeft to set
	 */
	public void setStagetimeLeft(int stagetimeLeft) {
		this.stagetimeLeft = stagetimeLeft;
	}

	/**
	 * @return the command
	 */
	public Enum<Command> getCommand() {
		return command;
	}

	/**
	 * @return the commandCounter
	 */
	public int getCommandCounter() {
		return commandCounter;
	}

	/**
	 * @param commandCounter
	 *            the commandCounter to set
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
	 * @param lastCommandTimestamp
	 *            the lastCommandTimestamp to set
	 */
	public void setLastCommandTimestamp(long lastCommandTimestamp) {
		this.lastCommandTimestamp = lastCommandTimestamp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Referee [timeoutTimeLeft=" + timeoutTimeLeft + ", stagetimeLeft=" + stagetimeLeft + ", stage=" + stage
				+ ", command=" + command + ", commandCounter=" + commandCounter + ", lastCommandTimestamp="
				+ lastCommandTimestamp + "]" + "\r\n";
	}
	
	

}
