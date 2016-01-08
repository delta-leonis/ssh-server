package org.ssh.models;

import java.util.ArrayList;
import java.util.List;

import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Allegiance;

/**
 * Describes a team<br />
 * NOTE: teams don't contain a list with all robots, since the robots which a team controls can
 * change during runtime. The robots should be accessed from {@link Models} class.
 *
 * @author Jeroen de Jong
 * @see Models
 *
 */
public class Team extends Model {

    /** Whether this is an {@link Allegiance#ALLY} or  {@link Allegiance#OPPONENT}*/
    private transient Allegiance allegiance;

    /** teamname given by {@link Referee} */
    private String           teamName;

    /** log with received cards and score, index is the count-value and Object is the timestamp. */
    private List<Long> yellowCards,
            redCards,
            scores;

    /** team properties as time left for timeouts, number of timeouts and the goalie ID. */
    private Integer          goalieId, timeoutLeft, timeouts;

    /**
     * Instantiates a new team that plays on a specified field half.
     *
     * @param allegiance
     *            whether this team is {@link Allegiance#ALLY} or {@link Allegiance#OPPONENT}.
     */
    public Team(final Allegiance allegiance) {
        super("team", allegiance.name());
        this.allegiance = allegiance;
    }

    @Override
    public void initialize() {
        yellowCards = new ArrayList<>();
        redCards = new ArrayList<>();
        scores = new ArrayList<>();

    }

    @Override
    public String getConfigName(){
        return String.format("%s %s.json", this.getClass().getSimpleName(), allegiance.name()).replace(" ", "_");
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
     * @return Whether this is an {@link Allegiance#ALLY} or {@link Allegiance#OPPONENT}
     */
    public Allegiance getAllegiance() {
        return this.allegiance;
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
}