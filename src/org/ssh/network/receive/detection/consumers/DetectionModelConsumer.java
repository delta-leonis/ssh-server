package org.ssh.network.receive.detection.consumers;

import org.ssh.managers.manager.Models;
import org.ssh.models.Ball;
import org.ssh.models.Game;
import org.ssh.models.Robot;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.AbstractConsumer;
import protobuf.Detection.DetectionFrame;
import protobuf.Detection.DetectionRobot;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class for parsing all {@link DetectionPacket}s to their models.
 *
 * @author Jeroen de Jong
 */
public class DetectionModelConsumer extends AbstractConsumer<DetectionPacket> {

    /**
     * helperclass containing information about the game
     */
    private Game game;

    /**
     * Create a new consumer for {@link DetectionPacket}s.
     *
     * @param name name for this service
     */
    public DetectionModelConsumer(String name) {
        super(name);
    }

    @Override
    public boolean consume(DetectionPacket pipelinePacket) {

        // read the packet
        DetectionFrame frame = pipelinePacket.read();
        // create a reference for the yellow team
        List<DetectionRobot> yellowTeam = frame.getRobotsYellowList();

        // merge both list
        Stream.concat(yellowTeam.stream(), frame.getRobotsBlueList().stream()).forEach(robot ->
                    //return the updated robot model
                    Models.<Robot>get(getModelName(robot, yellowTeam))
                            // try to get the existing model, if that doesn't work (orElse) create a new one
                            .orElseGet(() ->
                                    //create robotclass
                                    Models.<Robot>create(Robot.class, robot.getRobotId(), getAllegiance(robot, yellowTeam))
                            ).update(robot)
        );

        // get all balls to update them
        List<Ball> balls = Models.<Ball> getAll("ball");

        // compare the balls available in the frame
        for(int i = 0; i < frame.getBallsCount(); ++i){
            if(i == balls.size())
                balls.add(Models.create(Ball.class));
            // update the balls
            balls.get(i).update(frame.getBalls(i));
        }

        // loop all robots that haven't been processed
        Models.<Robot>getAll("robot").forEach(robot ->
            robot.setVisible((frame.getTSent() - robot.lastUpdated() < 500))
        );

        return true;
    }

    /**
     * @param robot      robot to get {@link Allegiance} for
     * @param yellowTeam list of all detected yellow robots
     * @return the {@link Allegiance} of a robot (either {@link Allegiance#ALLY} or {@link Allegiance#OPPONENT}.
     */
    private Allegiance getAllegiance(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return game.getAllegiance(DetectionModelConsumer.getTeamColor(robot, yellowTeam));
    }

    /**
     * Get the teamcolor based on the presents of the robot in the provided list. <br /><br />
     * <p>
     * NOTE: list should be of the yellow team.
     *
     * @param robot      robot to get {@link TeamColor} for.
     * @param yellowTeam list of all detected yellow robots
     * @return {@link TeamColor} of given robot
     */
    private static TeamColor getTeamColor(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        return yellowTeam.contains(robot) ? TeamColor.YELLOW : TeamColor.BLUE;
    }

    /**
     * Get the proposed name of {@link DetectionRobot} based on it's present in the given list. <br /><br />
     * <p>
     * NOTE: list should be of the yellow team.
     *
     * @param robot      robot to generate modelname for
     * @param yellowTeam list of all detected yellow robots
     * @return proposed robotname (i.e. "robot A3")
     */
    private String getModelName(DetectionRobot robot, List<DetectionRobot> yellowTeam) {
        Optional<Game> oGame = Models.<Game>get("game");
        if (oGame.isPresent()) {
            this.game = oGame.get();
            return String.format("robot %s%d", game.getAllegiance(getTeamColor(robot, yellowTeam)).identifier(), robot.getRobotId());
        }
        AbstractConsumer.LOG.info("No Game model, could not determine robot name");
        return "robot ";
    }
}