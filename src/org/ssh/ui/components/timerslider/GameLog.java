package org.ssh.ui.components.timerslider;

import java.util.ArrayList;

/**
 * Dummy class for the game logs
 */
public class GameLog {
    
    private final String             filename;
    private final int                length;
    private final ArrayList<Integer> goalTimes;
    private final ArrayList<Integer> timeouts;
                                     
    /**
     * Constructor of the GameLog
     * 
     * @param filename
     *            Name of the file
     * @param length
     *            Length in seconds
     */
    public GameLog(final String filename, final int length) {
        this.filename = filename;
        this.length = length;
        this.goalTimes = new ArrayList<Integer>();
        this.goalTimes.add(2);
        this.goalTimes.add(10);
        this.goalTimes.add(4);
        this.goalTimes.add(3);
        this.goalTimes.add(18);
        this.timeouts = new ArrayList<Integer>();
        this.timeouts.add(130);
        this.timeouts.add(50);
        
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public ArrayList<Integer> getGoalTimes() {
        return this.goalTimes;
    }
    
    public String getLength() {
        final int hours = this.length / 3600;
        final int minutes = (this.length - (hours * 3600)) / 60;
        final int seconds = this.length % 60;
        return hours + ":" + minutes + ":" + seconds;
    }
    
    public ArrayList<Integer> getTimeouts() {
        return this.timeouts;
    }
    
    public int getTimeSeconds() {
        return this.length;
    }
}
