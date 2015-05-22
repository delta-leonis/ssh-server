package robocup.model.enums;

/**
 * Events that can be thrown during the game.<br>
 * All possible events are described in "Tactieken_0.6.pdf" in chapter "4. Event system".
 * @see {@link robocup.controller.ai.highLevelBehaviour.events.EventSystem EventSystem.java}
 */
public enum Event {
	BALL_ENEMY_CHANGEOWNER,
	BALL_ALLY_CHANGEOWNER,
	BALL_ALLY_CAPTURE,
	BALL_ENEMY_CAPTURE,
	BALL_MOVESPAST_NORTHSOUTH,
	BALL_MOVESPAST_MIDLINE,
	REFEREE_NEWCOMMAND,
	ROBOT_ENEMY_ATTACKCOUNT_CHANGE;
}
