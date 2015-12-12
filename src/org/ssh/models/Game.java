package org.ssh.models;

import java.util.Optional;

import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Direction;
import org.ssh.models.enums.TeamColor;
import org.ssh.models.enums.Allegiance;
import org.ssh.util.Logger;

/**
 * World contains generic models for a match-setup
 *      
 * @author Jeroen
 */
public class Game extends Model {
    // respective logger
    private final Logger     logger = Logger.getLogger();

    private TeamColor allyColor;

    /** Side that ally team plays on as a cardinal direction. */
    private Direction allySide;

    /**
     * Instantiates a new model.
     */
    public Game() {
        super("game", "");

        // Default settings
        allyColor = TeamColor.YELLOW;
        allySide = Direction.EAST;
    }

    public Optional<Team> getAllies(){
        return Models.<Team>get("team " + Allegiance.ALLY);
    }

    public Optional<Team> getOpponent(){
        return Models.<Team>get("team " + Allegiance.OPPONENT);
    }

    public TeamColor getAllyColor(){
        return allyColor;
    }

    public TeamColor getEnemyColor(){
        return allyColor.getOpposite();
    }

    /**
     * get team based on a specific direction
     * 
     * @param direction
     *            play half
     * @return a team
     */
    public Optional<Team> getTeam(final Direction direction) {
        return Models.<Team>get("team " + getAllegiance(direction).name());
    }

    public Allegiance getAllegiance(Direction direction){
        return allySide.equals(direction) ? Allegiance.ALLY : Allegiance.OPPONENT;
    }

    public Allegiance getAllegiance(TeamColor color){
        return allyColor.equals(color) ? Allegiance.ALLY : Allegiance.OPPONENT;
    }


    /**
     * Gets the Team that is assigned to control a specific robot.
     * 
     * @param lostAndDamnedRobot
     *            robot to get the team from
     * @return team that controls this robot.
     */
    public Optional<Team> getTeam(final Robot lostAndDamnedRobot) {
        return Models.<Team>get("team " + getAllegiance(lostAndDamnedRobot.getTeamColor()));
    }

    /**
     * Swap the colors that the teams control.
     */
    public void swapColors() {
        this.logger.info("Swapped team colors");
        allyColor = allyColor.getOpposite();
    }
    
    /**
     * Swap the sides for the teams.
     */
    public void swapSides() {
        this.logger.info("Swapped team sides");
        allySide = allySide.getOpposite();
    }

    public TeamColor getTeamColor(Direction direction) {
        return getTeamColor(getAllegiance(direction));
    }

    public TeamColor getTeamColor(Allegiance allegiance) {
        return allegiance.equals(Allegiance.ALLY) ? getAllyColor() : getEnemyColor();
    }
}