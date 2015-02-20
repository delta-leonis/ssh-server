package robocup.model;

import java.util.ArrayList;
import java.util.Observable;

import robocup.migView.GUI;
import robocup.model.enums.Color;
import robocup.model.enums.Command;
/**
 * Model representation of the physical "world", including the field, 
 * all the robots (even non playing robots) and the ball
 */
public class World extends Observable {

	private static World instance;
	
	private Ball ball;
	private Referee referee;
	private Field field;
	
	ArrayList<Robot> allyTeam;
	ArrayList<Robot> enemyTeam;
	ArrayList<Robot> robotList;
	
	private static final int TOTAL_TEAM_SIZE = 11;
	private final int STOP_BALL_DISTANCE = 500; // in mm

	public GUI gui;
	
	/**
	 * Constructor for the {@link World}
	 * Can only be called as a singleton.
	 */
	private World() {
		ball = new Ball();
		ball.setPosition(new Point(400, 200)); // added starting point for ball
												// to remove nullpointer errors
		referee = new Referee();
		field = new Field();
		
		// initialize all robots
		allyTeam = new ArrayList<Robot>();
		enemyTeam = new ArrayList<Robot>();;
		
		for(int i=0; i < TOTAL_TEAM_SIZE; i++) 
		{	
			allyTeam.add(new Ally(i, false, 150));
		}
		for(int i=0; i < TOTAL_TEAM_SIZE; i++) 
		{	
			enemyTeam.add(new Enemy(i, false, 150));
		} 
		referee.initAllyTeam(allyTeam);
		referee.initEnemyTeam(enemyTeam);
		
		robotList = new ArrayList<Robot>();
		robotList.addAll(allyTeam);
		robotList.addAll(enemyTeam);
	}
		
	/**
	 * @return Singleton for the {@link World}
	 */
	public static World getInstance() {
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	/**
	 * Set used GUI
	 * @param current GUI object
	 */
	public void setGUI(GUI _gui){
		gui = _gui;
	}

	/**
	 * mainly used to push updates
	 * 
	 * @return the gui object
	 */
	public GUI getGUI(){
		return gui;
	}

	/**
	 * 
	 * @param message
	 */
	public void HandlerFinished(String message) {
		setChanged();
		notifyObservers(message + "HandlerFinished");
	}

	/**
	 * @return the {@link Ball} that is currently in the field.
	 */
	public Ball getBall() {
		return ball;
	}

	/**
	 * @return the referee
	 */
	public Referee getReferee() {
		return referee;
	}

	/**
	 * @return the {@link Field} that is used for this game.
	 */
	public Field getField() {
		return field;
	}
	
	/**
	 * Returns the {@link Team} with the given color.
	 * this methodd is an adapter from the old model, so its better to use referee getTeambyColor
	 * @param color the color of the {@link Team}
	 * @return the {@link Team} with the given color. Returns null if there is no {@link Team} with the given color.
	 */
	public Team getTeamByColor(Color color) {
		return referee.getTeamByColor(color);
	}

	/**
	 * gets our team, this is a method from the old model, it bypasses the referee
	 * @return the ally {@link Team} in the current match.
	 * @deprecated Get it from Referee
	 */
	public Team getAlly() {
		return referee.getAlly();
	}

	/**
	 * gets the enemy team, this is a method from the old model, it bypasses the referee
	 * @return the enemy {@link Team} in the current match.
	 * @deprecated Get it from Referee
	 */
	public Team getEnemy() {
		return referee.getEnemy();
	}

	/**
	 * 
	 * @return all the robots in the current match
	 */
	public ArrayList<Robot> getAllRobots() {
		return robotList;
	}
	
	/**
	 * @return all robots currently on the playing field
	 */
	public ArrayList<Robot> getAllRobotsOnSight(){
		ArrayList<Robot> onsight = new ArrayList<Robot>();
		for( Robot robot : robotList){
			if(robot.isOnSight())
				onsight.add(robot);
		}
		return onsight;
	}
	
	/**
	 * Sets the {@link Field} of the current match. 
	 * The {@link Field} contains all variables regarding the {@link Field}. (Think of field width, goal length etc.)
	 * @param field the {@link Field} to set for the current match.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "World \r\n[ball=" + ball + "\r\nreferee=" + referee + "\r\n"
				+ field + "]";
	}
	

	/**
	 * Helper function for referee commands, checks last command issued
	 * when the robot may move because of the "keep 50 cm clearance" rule, pathfinding must find a way from the ball
	 * @param robotID
	 * @return bool indicating if movement is allowed
	 */
	public boolean robotMayMove(int robotID) {
		// Halt = all robots stop
		if (referee.getCommand() == Command.HALT) {
			return false;
		}

		// Stop = keep 50cm from ball
		if (referee.getCommand() == Command.STOP) {
			// if the distance to ball is less then 50cm, is so return false
			if ((int) referee.getAlly().getRobotByID(robotID).getPosition().getDeltaDistance(ball.getPosition()) < STOP_BALL_DISTANCE) {
				return false;
			}

			// Goal = Should be treated the same as STOP
		} else if (referee.getCommand() == Command.GOAL_YELLOW || referee.getCommand() == Command.GOAL_BLUE) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the color of your own team.
	 * Suggestion: Rename to getAllyTeamColor()
	 * 
 	 * @Deprecated use Referee.getOwnTeamColor();
	 * @return
	 */
	@Deprecated
	public Color getOwnTeamColor() {
		return referee.getOwnTeamColor();
	}
}
