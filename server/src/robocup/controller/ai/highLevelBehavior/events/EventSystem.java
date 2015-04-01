package robocup.controller.ai.highLevelBehavior.events;

import robocup.model.enums.Event;

/**
 * Class used to determine events which happen on the field
 * See tactics documentation for an explanation on every event
 * All possible events are declared in Event.java
 */
public class EventSystem {

	/**
	 * Create an event system.
	 */
	public EventSystem() {

	}

	/**
	 * Get the current event. Returns null when no event has happened.
	 */
	public Event getNewEvent() {
		return null;
	}

	/**
	 * Get the previous event, previous event cannot be null
	 */
	public Event getPreviousEvent() {
		return null;
	}
}
