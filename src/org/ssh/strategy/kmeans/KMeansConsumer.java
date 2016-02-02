package org.ssh.strategy.kmeans;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.ssh.managers.manager.Models;
import org.ssh.models.Game;
import org.ssh.models.Robot;
import org.ssh.models.Team;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.Direction;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.services.AbstractConsumer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Will be changed into a {@link org.ssh.services.AbstractTranslator} when the time comes.
 * Whenever a {@link DetectionPacket} is passed to this class, it will cluster robots
 *
 * @author Thomas Hakkers
 */
public class KMeansConsumer extends AbstractConsumer<DetectionPacket> {
    private RobotFuzzyKMeans fuzzler;
    private Allegiance allegiance;

    /**
     * Creates a new {@link KMeansConsumer} using the given {@link Allegiance} to cluster
     * @param name Service name
     * @param allegiance the {@link Allegiance} to be clustered
     */
    public KMeansConsumer(String name, Allegiance allegiance) {
        super(name);
        this.allegiance = allegiance;

        boolean eastSide = true;

        // Check whether the game is present
        Optional<Game> game = Models.<Game>get("game");
        if(game.isPresent()) {
            // Set the direction to play in
            eastSide = game.get().getSide(allegiance) == Direction.EAST;
        }
        // Create the clusterer
        fuzzler = new RobotFuzzyKMeans(5, 50, new EuclideanDistance(), eastSide);
    }


    @Override
    public boolean consume(DetectionPacket pipelinePacket) {
        try {
            // Cluster the robots and also update them
            fuzzler.clusterRobots(getRecentRobots());
            // use RobotFuzzyKMeans#getClusteredRobots() to cluster the robots, without instantly updating them
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Mainly used for clustering
     * @return A list of the 6 last updated {@link Robot robots}, with the keeper being the first in the list
     */
    public List<Robot> getRecentRobots(){
        // Retrieve all robots
        List<Robot> allRobots = Models.<Robot>getAll("robot");
        // Get the team we're clustering for
        Optional<Team> allyTeam = Models.<Team>getAll("team").stream().filter(team -> team.getAllegiance() == allegiance)
                .findAny();

        int goalieId = allyTeam.isPresent() ? allyTeam.get().getGoalieId() : 0;

        return allRobots.stream().filter(robot ->
                // Get the robots that belong to the given allegiance
                robot.getAllegiance() == allegiance)
                // Sort the list by last updated
                .sorted((robot1, robot2) -> Long.compare(robot1.lastUpdated(), robot2.lastUpdated()))
                // Make sure it's only 6 robots
                .limit(6)
                // Sort by ID
                .sorted((robot1, robot2) -> Integer.compare(robot1.getRobotId(), robot2.getRobotId()))
                // Sort the ids in such a way that the keeper is always the first in the list
                .sorted((robot1, robot2) -> {
                    if(robot1.getRobotId() == goalieId){
                        return -1;
                    }
                    else{
                        return 1;
                    }
                })
                .collect(Collectors.toList());
    }
}

