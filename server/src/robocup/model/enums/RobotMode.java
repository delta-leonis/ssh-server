package robocup.model.enums;

/**
 * An enumeration that describes possible roles of Robots<br>
 * Examples are: Keeper, Defender, Attacker and Blocker.
 * For documentation see "Tactiek_0.6.pdf" chapter "5. lowlevel behaviours"
 */
public enum RobotMode {
	KEEPER,
	KEEPERDEFENDER,
	PENALTYKEEPER,
	DISTURBER,
	COVERER,
	GOALPOSTCOVERER,
	ATTACKER,
	COUNTER,
	DISTURBER_COVERER,
	KEEPERDEFENDER_COVERER,
	RUNNER;
}
