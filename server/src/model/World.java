package model;

import java.util.Observable;

import model.enums.Color;

public class World extends Observable {

	private static World instance;
	private Ball ball;
	private Referee referee;
	private Field field;
	private Team enemy;
	private Team ally;
	private Color ownTeamColor;

	private World() {
		//TODO: diameter in config file
		ball = new Ball(20);
		referee = new Referee();
	}

	public static World getInstance() {
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	public void HandlerFinished(String message){
		setChanged();
		notifyObservers(message + "HandlerFinished");
	}
	
	public void RobotAdded(){
		setChanged();
		notifyObservers("RobotAdded");
	}

	public void setOwnTeamCollor(Color color) {
		ownTeamColor = color;
	}

	public Color getOwnTeamColor() {
		return ownTeamColor;
	}

	/**
	 * @return the ball
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
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	public Team getTeamByColor(Color color){
		if(color.equals(this.getAlly().getColor()))
		{
			return this.getAlly();
		}
		else if(color.equals(this.getEnemy().getColor()))
		{
			return this.getEnemy();
		}
		return null;
	}
	
	public Team getAlly() {
		return ally;
	}

	public void setAlly(Team t) {
		ally = t;
	}

	public Team getEnemy() {
		return enemy;
	}

	public void setEnemy(Team t) {
		enemy = t;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(Field field) {
		this.field = field;
	}
	
	@Override
	public String toString() {
		return "World \r\n[ball=" + ball + "\r\nreferee=" + referee +  "\r\nownTeamColor=" + ownTeamColor + "\r\n" + field + "\r\n" + enemy + "\r\n"
				+ ally + "]";
	}
}
