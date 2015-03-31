package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;

/**
 * BlockerDekker
 * Bezet lijn tussen midden van eigen goal en de bal met offset opzij en offset naar voren (blijft op rand strafschopgebied). 
 * Bezet lijn tussen tegenstander en bal als er een tegenstander aanwezig is in zone ‘2ePaal’ of ‘Hoek’.
 * TODO: English-fy
 * TODO: Thomas: Deze rol is dus in principe hetzelfde als KeeperDefender, 
 * maar in plaats van het verdedigen van een punt, gaat hij heen en weer over een lijn?
 */
public class BlockerDekker extends KeeperDefender{

	private FieldPoint offset;

	public BlockerDekker(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick, FieldPoint ballPosition,
			FieldPoint keeperPosition, FieldPoint centerGoalPosition, FieldPoint offset, int yMax) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, keeperPosition, centerGoalPosition, offset, yMax);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param keeperPosition
	 * @param offset the offset Position
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint keeperPosition, FieldPoint offset) {
		super.update(distanceToGoal, goToKick, ballPosition, keeperPosition);
		this.offset = offset;
	}

	@Override
	protected FieldPoint getNewKeeperDestination() {
		FieldPoint newDestination = super.getNewKeeperDestination();

		if (newDestination != null) {
			newDestination.setX(newDestination.getX() + offset.getX());
			newDestination.setY(newDestination.getY() + offset.getY());
		}

		return newDestination;
	}

}
