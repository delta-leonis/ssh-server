package robocup.model.enums;

/**
 * Describes different Stages a game can have.<br>
 * For documentation see "Eindverslag versie 1.0.pdf" chapter "6.7 Referee", and see {@link robocup.input.protobuf.Referee Referee}
 */
public enum Stage {
	NORMAL_FIRST_HALF_PRE,
	NORMAL_FIRST_HALF,
	NORMAL_HALF_TIME,
	NORMAL_SECOND_HALF_PRE,
	NORMAL_SECOND_HALF,
	EXTRA_TIME_BREAK,
	EXTRA_FIRST_HALF_PRE,
	EXTRA_FIRST_HALF,
	EXTRA_HALF_TIME,
	EXTRA_SECOND_HALF_PRE,
	EXTRA_SECOND_HALF,
	PENALTY_SHOOTOUT_BREAK,
	PENALTY_SHOOTOUT,
	POST_GAME;
}
