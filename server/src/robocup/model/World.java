package robocup.model;

import java.util.Observable;

import robocup.model.enums.Color;

public class World extends Observable {

	private static World instance;
	private Ball ball;
	private Referee referee;
	private Field field;
	private Team enemy;
	private Team ally;
	private Color ownTeamColor;

	/**
	 * Constructor for the {@link World}
	 * Can only be called as a singleton.
	 */
	private World() {
		ball = new Ball();
		ball.setPosition(new Point(400, 200)); // added starting point for ball
												// to remove nullpointer errors
		referee = new Referee();
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

	public void HandlerFinished(String message) {
		setChanged();
		notifyObservers(message + "HandlerFinished");
	}

	public void RobotAdded() {
		setChanged();
		notifyObservers("RobotAdded");
	}

	/**
	 * Sets the Color for our own Team.
	 * Suggestion: Rename to setAllyTeamColor()
	 * @param color
	 */
	public void setOwnTeamColor(Color color) {
		ownTeamColor = color;
	}

	/**
	 * Returns the color of your own team.
	 * Suggestion: Rename to getAllyTeamColor()
	 * @return 
	 */
	public Color getOwnTeamColor() {
		return ownTeamColor;
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
	 * @param color the color of the {@link Team}
	 * @return the {@link Team} with the given color. Returns null if there is no {@link Team} with the given color.
	 */
	public Team getTeamByColor(Color color) {
		if (ally.isTeamColor(color))
			return ally;
		else if (enemy.isTeamColor(color))
			return enemy;

		return null;
	}

	/**
	 * @return the ally {@link Team} in the current match.
	 */
	public Team getAlly() {
		return ally;
	}

	/**
	 * Sets which {@link Team} is our ally in the current match.
	 * @param t the {@link Team} that is our ally.
	 */
	public void setAlly(Team t) {
		ally = t;
	}

	/**
	 * @return the enemy {@link Team} in the current match.
	 */
	public Team getEnemy() {
		return enemy;
	}

	/**
	 * Sets which {@link Team} is our enemy in the current match.
	 * @param t the {@link Team} that is our enemy
	 */
	public void setEnemy(Team t) {
		enemy = t;
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
		return "World \r\n[ball=" + ball + "\r\nreferee=" + referee + "\r\nownTeamColor=" + ownTeamColor + "\r\n"
				+ field + "\r\n" + enemy + "\r\n" + ally + "]";
	}
}
