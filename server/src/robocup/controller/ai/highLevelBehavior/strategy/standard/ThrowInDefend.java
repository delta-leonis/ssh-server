package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class ThrowInDefend extends Strategy {
	public ThrowInDefend () {
		super();
		roles.add(RobotMode.KEEPER);
		// Second pole defender
		roles.add(RobotMode.GOALPOSTCOVERER);
		// Covers the biggest theat of the enemy
		roles.add(RobotMode.DISTURBER);
		// covers the enemy that may kick the ball
		roles.add(RobotMode.COVERER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.EAST_SOUTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.EAST_SOUTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.EAST_NORTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.EAST_NORTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT);
			}
		} else {
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_SOUTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.WEST_SOUTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_NORTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.WEST_NORTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT);
			}
		}
	}

}
