package robocup.model.enums;

/**
 * An enumeration that describes possible roles of Robots<br>
 * Examples are: Keeper, Defender, Attacker and Blocker.
 * For documentation see "Tactiek_0.6.pdf" chapter "5. lowlevel behaviours"
 */
public enum RobotMode {
	KEEPER(1),
	KEEPERDEFENDER(2),
	PENALTYKEEPER(1),
	DISTURBER(4),
	COVERER(5),
	GOALPOSTCOVERER(6),
	ATTACKER(7),
	COUNTER(9),
	DISTURBER_COVERER(4),
	KEEPERDEFENDER_COVERER(3),
	RUNNER(8);
	
	private final int priority;
	
	RobotMode (int priority)
	{
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
}
