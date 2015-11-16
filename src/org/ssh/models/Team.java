package org.ssh.models;

import java.util.ArrayList;
import java.util.List;

import org.ssh.managers.Models;
import org.ssh.models.enums.Direction;

import javafx.scene.paint.Color;

/**
 * Describes a team<br />
 * NOTE: teams don't contain a list with all robots, since the robots which a team controls can
 * change during runtime. The robots should be accessed from {@link Models} class or via the
 * {@link World}.
 *
 * @author Jeroen
 * @see Models
 * @see World
 *      
 */
@SuppressWarnings ("serial")
public class Team extends Model {
    
    /**
     * Side that this team plays on as a cardinal direction.
     */
    private Direction        direction;
    /**
     * color that this team controls
     */
    private Color            teamColor;
                             
    /**
     * teamname given by {@link Referee}
     */
    private String           teamName;
                             
    /**
     * log with received cards and score, index is the count-value and Object is the timestamp.
     */
    private final List<Long> yellowCards = new ArrayList<Long>(),
                                     redCards = new ArrayList<Long>(), scores = new ArrayList<Long>();
                                     
    /**
     * team properties as time left for timeouts, number of timeouts and the goalie ID.
     */
    private int              goalieId, timeoutLeft, timeouts;
                             
    /**
     * Instantiates a new team that plays on a specified field half.
     * 
     * @param direction
     *            field half that this team plays on.
     * @param teamcolor
     *            the color of the {@link Robot robots} that this team controls.
     */
    public Team(final Direction direction, final Color teamColor) {
        super("team", String.format("%s %s", direction.name(), teamColor.toString()));
        this.direction = direction;
        this.teamColor = teamColor;
    }
    
    /**
     * adds 1 red card to the count with current timestamp
     */
    public void addRedCard() {
        this.redCards.add(System.currentTimeMillis());
    }
    
    /**
     * adds 1 red card to the count with current timestamp
     */
    public void addScore() {
        this.scores.add(System.currentTimeMillis());
    }
    
    /**
     * adds 1 yellow card to the count with current timestamp
     */
    public void addYellowCard() {
        this.yellowCards.add(System.currentTimeMillis());
    }
    
    /**
     * @return Side that this team plays on as a cardinal direction
     */
    public Direction getDirection() {
        return this.direction;
    }
    
    /**
     * @return returns goalie id for this team
     */
    public int getGoalieId() {
        return this.goalieId;
    }
    
    /**
     * gets all redcards. Value represents <em>System.currentTimeMillis();</em>.
     */
    public List<Long> getRedCards() {
        return this.redCards;
    }
    
    /**
     * gets all scores. Value represents <em>System.currentTimeMillis();</em>.
     */
    public List<Long> getScores() {
        return this.scores;
    }
    
    /**
     * @return color that this team controls
     */
    public Color getTeamColor() {
        return this.teamColor;
    }
    
    /**
     * @return teamname as provided by the refbox
     */
    public String getTeamName() {
        return this.teamName;
    }
    
    /**
     * @return time left for timeouts for this team
     */
    public int getTimeoutLeft() {
        return this.timeoutLeft;
    }
    
    /**
     * @return gets number of timeouts
     */
    public int getTimeouts() {
        return this.timeouts;
    }
    
    /**
     * gets all yellowcards. Value represents <em>System.currentTimeMillis();</em>.
     */
    public List<Long> getYellowCards() {
        return this.yellowCards;
    }
    
    /**
     * sets a goalie id for this team
     * 
     * @param goalieId
     */
    public void setGoalieId(final int goalieId) {
        this.goalieId = goalieId;
    }
    
    /**
     * sets a new teamname as provided by the refbox
     * 
     * @param teamName
     */
    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }
    
    /**
     * sets time left for timeouts for this team
     * 
     * @param timeoutLeft
     */
    public void setTimeoutLeft(final int timeoutLeft) {
        this.timeoutLeft = timeoutLeft;
    }
    
    /**
     * sets number of timeouts
     * 
     * @param timeouts
     */
    public void setTimeouts(final int timeouts) {
        this.timeouts = timeouts;
    }
    
    /**
     * Inverts the teamcolor, so {@link Color.BLUE} becomes {@link Color.YELLOW} and vice versa.
     */
    public void swapColor() {
        this.teamColor = this.teamColor == Color.BLUE ? Color.YELLOW : Color.BLUE;
    }
    
    /**
     * inverts the side that this team plays on based on {@link Direction Direction.getOpposite()}
     */
    public void swapDirection() {
        this.direction = this.direction.getOpposite();
    }
}