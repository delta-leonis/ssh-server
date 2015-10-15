package model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.Models;
import model.enums.Direction;
import util.Logger;

//TODO singleton

/**
 * World contains generic models for a match-setup
 * 
 * @author Jeroen
 */
public class World {
	//respective logger
	private Logger			 logger = Logger.getLogger();

	/**
	 * All teams in the game
	 */
	private ArrayList<Team>	 teams  = new ArrayList<Team>();

	/**
	 * Swap the sides for the teams
	 */
	public void swapSides(){
		logger.info("Swapped team sides");
		teams.stream().forEach(team -> team.swapDirection());
	}

	/**
	 * Swap the colors that the teams control
	 */
	public void swapColors(){
		logger.info("Swapped team colors");
		teams.stream().forEach(team -> team.swapColor());
	}

	/**
	 * Gets the Team that is assigned to control a specific robot
	 * 
	 * @param Lost_and_damned_robot	robot to get the team from
	 * @return team that controls this robot
	 */
	public Team getTeam(Robot Lost_and_damned_robot){
		return (Team) Models.getAll("team")
				// Get all team models as a stream
				.stream()
				// Compare individual teamcolor to robots teamcolor
				.filter(team -> ((Team)team).getTeamColor().equals(Lost_and_damned_robot.getTeamColor()))
				// Return the first match
				.findFirst().get();
	}

	/**
	 * Gets a {@link Robot} object, specified by these parameters
	 * 
	 * @param id		robot ID
	 * @param teamColor	teamColor of the Robot
	 * @return
	 */
	public Robot getRobot(int id, Color teamColor){
		return getRobots(teamColor)
				.stream()
				.filter(robot -> robot.getRobotId() == id)
				.findFirst().get();
	}

	/**
	 * Gets all robots of a specified teamcolor
	 * 
	 * @param teamColor	specific teamcolor to sort on
	 * @return list with all robots of a specific color
	 */
	public List<Robot> getRobots(Color teamColor){
		return Models.getAll("robot")
				.stream()
				.filter(robot -> ((Robot)robot).getTeamColor().equals(teamColor))
				.map(model -> (Robot)model)
				.collect(Collectors.toList());
	}

	/**
	 * get team based on a specific direction
	 * @param direction play half
	 * @return a team
	 */
	private Team getTeam(Direction direction) {
		return teams.stream()
				.filter(team -> team.getDirection().equals(direction))
				.findFirst()
				.get();
	}
}