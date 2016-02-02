package org.ssh.models;

import org.ssh.models.enums.RefereeCommand;
import org.ssh.models.enums.Stage;
import protobuf.RefereeOuterClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements handling of the {@link RefereeOuterClass.Referee referee messages} , also updates
 * respective {@link Team teams}.
 *
 * @author Jeroen de Jong
 */
public class Referee extends AbstractModel {

    /**
     * History of all the {@link Stage stages}, last index is last received {@link Stage stage}
     */
    private List<RefereeOuterClass.Referee.Stage>          stageHistory;
    /**
     * History of all the {@link RefereeOuterClass.Referee.Command commands}, last index is last received
     * {@link RefereeOuterClass.Referee.Command command}
     */
    private List<RefereeOuterClass.Referee.Command> commandHistory;
    /**
     * Timestamp in ms of the last parsed {@link RefereeOuterClass.Referee referee message}
     */
    private Long lastPacketTimestamp;
    /**
     * Time until the current {@link Stage} is finished
     */
    private Integer stageTimeLeft;
    /**
     * The amount of {@link RefereeCommand commands} we've currently received.
     */
    private Integer commandCounter;

    /**
     * Instantiates a new Referee
     */
    public Referee() {
        super("Referee", "");
    }

    @Override
    public void initialize() {
        stageHistory = new ArrayList<>();
        commandHistory = new ArrayList<>();
    }

    /**
     * @return return number of commands
     */
    public int getCommandCounter() {
        return this.commandCounter;
    }

    /**
     * @return whole history of {@link RefereeOuterClass.Referee.Command commands}
     */
    public List<RefereeOuterClass.Referee.Command> getCommands() {
        return this.commandHistory;
    }
    /**
     * @return last received {@link Stage stage}, may return null
     */
    public RefereeOuterClass.Referee.Stage getCurrentStage() {
        if (this.stageHistory.isEmpty()) {
            Referee.LOG.warning("There are no previous commands");
            return null;
        }
        return this.stageHistory.get(this.stageHistory.size() - 1);
    }

    /**
     * Updates the {@link List} containing all recent {@link Stage stage}
     * @param stage The stage to be added to the list
     */
    public void updateCurrentStage(RefereeOuterClass.Referee.Stage stage){
        this.stageHistory.add(stage);
    }

    /**
     * @return last received {@link RefereeCommand command}, may return null
     */
    public RefereeOuterClass.Referee.Command getLastCommand() {
        if (this.commandHistory.isEmpty()) {
            Referee.LOG.warning("There are no previous commands");
            return null;
        }
        return this.commandHistory.get(this.commandHistory.size() - 1);
    }

    /**
     * Updates the {@link List} containing all recent {@link RefereeCommand commands}
     * @param command The command to be added to the list
     */
    public void updateCommand(RefereeOuterClass.Referee.Command command){
        this.commandHistory.add(command);
    }

    /**
     * @return timestamp of last received packet in ms
     */
    public long getLastPacketTimestamp() {
        return this.lastPacketTimestamp;
    }

    /**
     * @return whole history of {@link Stage stages}
     */
    public List<RefereeOuterClass.Referee.Stage> getStages() {
        return this.stageHistory;
    }

    /**
     * @return time left in current stage
     */
    public int getStageTimeLeft() {
        return this.stageTimeLeft;
    }

    /**
     * sets the number of commands
     *
     * @param {@link #commandCounter}
     */
    public void setCommandCounter(final int commandCounter) {
        this.commandCounter = commandCounter;
    }

    /**
     * sets timestamp of last received packet in ms
     *
     * @param lastPacketTimestamp
     */
    public void setLastPacketTimestamp(final long lastPacketTimestamp) {
        this.lastPacketTimestamp = lastPacketTimestamp;
    }

    /**
     * sets time left in current stage
     *
     * @param stageTimeLeft
     */
    public void setStageTimeLeft(final int stageTimeLeft) {
        this.stageTimeLeft = stageTimeLeft;
    }
}