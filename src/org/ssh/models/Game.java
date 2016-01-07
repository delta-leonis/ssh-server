package org.ssh.models;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Direction;
import org.ssh.models.enums.TeamColor;
import org.ssh.models.enums.Allegiance;
import org.ssh.util.Logger;

/**
 * Helper class used for abstracting {@link TeamColor} and {@link Direction} out of {@link Robot}, {@link Goal}
 * and {@link Field}, but keeping this information available for drawing purposed (i.e. {@link org.ssh.field3d.FieldGame})
 * and recoginision of vision data stream (i.e. {@link org.ssh.services.consumers.DetectionModelConsumer})
 *      
 * @author Jeroen de Jong
 */
public class Game extends Model {

    /** Color of the {@link Allegiance#ALLY} team */
    private TeamColor allyColor;

    /** {@link Direction defending side} of the {@link Allegiance#ALLY} team  */
    private Direction allySide;

    /**
     * Instantiates a new model.
     */
    public Game() {
        super("game", "");
    }

    @Override
    public void initialize() {
        allyColor = TeamColor.YELLOW;
        allySide = Direction.EAST;
    }
    /**
     * @return the {@link Team} of the if {@link Allegiance#ALLY allies} found
     */
    public Optional<Team> getAllies(){
        return Models.<Team>get("team " + Allegiance.ALLY);
    }

    /**
     * @return the {@link Team} of the if {@link Allegiance#OPPONENT opponents} found
     */
    public Optional<Team> getOpponent(){
        return Models.<Team>get("team " + Allegiance.OPPONENT);
    }

    /**
     * @return {@link TeamColor} of the {@link Allegiance#ALLY allies}
     */
    public TeamColor getAllyColor(){
        return allyColor;
    }

    /**
     * @return {@link TeamColor} of the {@link Allegiance#OPPONENT opponents}
     */
    public TeamColor getOpponentColor(){
        return allyColor.getOpposite();
    }

    /**
     * Retrieves a teams {@link Direction defending side} based on a {@link TeamColor}
     * @param color color of team
     * @return a teams {@link Direction defending side} based on {@link TeamColor}
     */
    public Direction getSide(TeamColor color){
        return allyColor.equals(color) ? getAllySide() : getOpponentSide();
    }

    /**
     * Retrieves a teams {@link Direction defending side} based on a teams {@link Allegiance}
     * @param allegiance allegiance of a team
     * @return {@link Direction defending side} of matching team
     */
    public Direction getSide(Allegiance allegiance){
        return allegiance.equals(Allegiance.ALLY) ? getAllySide() : getOpponentSide();
    }

    /*
     * @return {@link Direction defending side} of the {@link Allegiance#ALLY allies}
     */
    public Direction getAllySide(){
        return allySide;
    }

    /**
     * @return {@link Direction defending side} of the {@link Allegiance#OPPONENT opponents}
     */
    public Direction getOpponentSide(){
        return allySide.getOpposite();
    }

    /**
     * get team based on the {@link Direction defending side} of a team
     * 
     * @param direction
     *            defending fieldhalf
     * @return a team
     */
    public Optional<Team> getTeam(final Direction direction) {
        return Models.<Team>get("team " + getAllegiance(direction).name());
    }

    /**
     * Retrieves a teams {@link Allegiance} based on the {@link Direction defending side} of a team
     * @param direction {@link Direction defending side} of a team
     * @return the {@link Allegiance} of matching team
     * @see #getSide(Allegiance)
     * @see #getSide(TeamColor)
     */
    public Allegiance getAllegiance(Direction direction){
        return allySide.equals(direction) ? Allegiance.ALLY : Allegiance.OPPONENT;
    }

    /**
     * Retrieve a teamcolor based on a teams {@link Allegiance}
     *
     * @param color color of a team
     * @return the teamcolor based on a teams {@link Allegiance}
     *
     * @see #getTeamColor(Allegiance)
     * @see #getTeamColor(Direction)
     */
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
        return Models.<Team>get("team " + lostAndDamnedRobot.getAllegiance().name());
    }

    /**
     * Swap the colors that the teams control.
     */
    public void swapColors() {
        allyColor = allyColor.getOpposite();
    }
    
    /**
     * Swap the sides for the teams.
     */
    public void swapSides() {
        allySide = allySide.getOpposite();
    }

    /**
     * Retrieves the teamcolor based the {@link TeamColor} based on the {@link Direction defending side} of a team
     * @param direction fieldside defended by team
     * @return the teamcolor based on the side which this team defends
     * @see #getSide(Allegiance)
     * @see #getSide(TeamColor)
     */
    public TeamColor getTeamColor(Direction direction) {
        return getTeamColor(getAllegiance(direction));
    }

    /**
     * Retrieves the teamcolor based on the {@link Allegiance} of a team
     * @param allegiance the allegiance of a team
     * @return teamcolor of the team with matching {@link Allegiance}
     * @see #getAllegiance(Direction)
     * @see #getAllegiance(TeamColor)
     */
    public TeamColor getTeamColor(Allegiance allegiance) {
        return allegiance.equals(Allegiance.ALLY) ? getAllyColor() : getOpponentColor();
    }

    public List<Robot> getRobots(Allegiance allegiance){
        return Models.<Robot>getAll("robot").stream().filter(robot -> robot.getAllegiance().equals(allegiance)).collect(Collectors.toList());
    }
}