package org.ssh.models;

import java.util.ArrayList;

import org.ssh.models.enums.RefereeCommand;
import org.ssh.models.enums.Stage;
import org.ssh.util.Logger;

import protobuf.RefereeOuterClass;

/**
 * Implements handling of the {@link RefereeOuterClass.Referee referee messages} , also updates
 * respective {@link Team teams}
 *
 * @author Jeroen
 *        
 */
public class Referee extends Model {
    
    // respective logger
    private final Logger                    logger         = Logger.getLogger();
                                                           
    /**
     * history of all the {@link Stage stages}, last index is last received {@link Stage stage}
     */
    private final ArrayList<Stage>          stageHistory   = new ArrayList<Stage>();
    /**
     * history of all the {@link RefereeCommand commands}, last index is last received
     * {@link RefereeCommand command}
     */
    private final ArrayList<RefereeCommand> commandHistory = new ArrayList<RefereeCommand>();
    /**
     * timestamp in ms of the last parsed {@link RefereeOuterClass.Referee referee message}
     */
    private long                            lastPacketTimestamp;
                                            
    private int                             stageTimeLeft, commandCounter;
                                            
    /**
     * Instantiates a new Referee
     */
    public Referee() {
        super("Referee");
    }
    
    /**
     * @return return number of commands
     */
    public int getCommandCounter() {
        return this.commandCounter;
    }
    
    /**
     * @return whole history of {@link RefereeCommand commands}
     */
    public ArrayList<RefereeCommand> getCommands() {
        return this.commandHistory;
    }
    
    /**
     * @return last received {@link Stage stage}, may return null
     */
    public Stage getCurrentStage() {
        if (this.stageHistory.isEmpty()) {
            this.logger.warning("There are no previous commands");
            return null;
        }
        return this.stageHistory.get(this.stageHistory.size() - 1);
    }
    
    /**
     * @return last received {@link RefereeCommand command}, may return null
     */
    public RefereeCommand getLastCommand() {
        if (this.commandHistory.isEmpty()) {
            this.logger.warning("There are no previous commands");
            return null;
        }
        return this.commandHistory.get(this.commandHistory.size() - 1);
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
    public ArrayList<Stage> getStages() {
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
     * @param commandCounter
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