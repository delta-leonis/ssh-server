package org.ssh.models;

import java.util.ArrayList;

import org.ssh.models.enums.RefereeCommand;
import org.ssh.models.enums.Stage;
import org.ssh.util.Logger;

/**
 * Implements handling of the {@link RefereeOuterClass.Referee referee messages}
 * , also updates respective {@link Team teams}
 * 
 * @author Jeroen
 *
 */
public class Referee extends Model {
	// respective logger
	private Logger logger = Logger.getLogger();

	/**
	 * history of all the {@link Stage stages}, last index is last received
	 * {@link Stage stage}
	 */
	private ArrayList<Stage> stageHistory = new ArrayList<Stage>();
	/**
	 * history of all the {@link RefereeCommand commands}, last index is last
	 * received {@link RefereeCommand command}
	 */
	private ArrayList<RefereeCommand> commandHistory = new ArrayList<RefereeCommand>();
	/**
	 * timestamp in ms of the last parsed {@link RefereeOuterClass.Referee
	 * referee message}
	 */
	private long lastPacketTimestamp;

	private int stageTimeLeft, commandCounter;

	/**
	 * Instantiates a new Referee
	 */
	public Referee() {
		super("Referee");
	}

	/**
	 * @return last received {@link RefereeCommand command}, may return null
	 */
	public RefereeCommand getLastCommand() {
		if (commandHistory.isEmpty()) {
			logger.warning("There are no previous commands");
			return null;
		}
		return commandHistory.get(commandHistory.size() - 1);
	}

	/**
	 * @return whole history of {@link RefereeCommand commands}
	 */
	public ArrayList<RefereeCommand> getCommands() {
		return commandHistory;
	}

	/**
	 * @return last received {@link Stage stage}, may return null
	 */
	public Stage getCurrentStage() {
		if (stageHistory.isEmpty()) {
			logger.warning("There are no previous commands");
			return null;
		}
		return stageHistory.get(stageHistory.size() - 1);
	}

	/**
	 * @return whole history of {@link Stage stages}
	 */
	public ArrayList<Stage> getStages() {
		return stageHistory;
	}

	/**
	 * @return return number of commands
	 */
	public int getCommandCounter() {
		return commandCounter;
	}

	/**
	 * sets the number of commands
	 * 
	 * @param commandCounter
	 */
	public void setCommandCounter(int commandCounter) {
		this.commandCounter = commandCounter;
	}

	/**
	 * @return timestamp of last received packet in ms
	 */
	public long getLastPacketTimestamp() {
		return lastPacketTimestamp;
	}

	/**
	 * sets timestamp of last received packet in ms
	 * 
	 * @param lastPacketTimestamp
	 */
	public void setLastPacketTimestamp(long lastPacketTimestamp) {
		this.lastPacketTimestamp = lastPacketTimestamp;
	}

	/**
	 * @return time left in current stage
	 */
	public int getStageTimeLeft() {
		return stageTimeLeft;
	}

	/**
	 * sets time left in current stage
	 * 
	 * @param stageTimeLeft
	 */
	public void setStageTimeLeft(int stageTimeLeft) {
		this.stageTimeLeft = stageTimeLeft;
	}
}