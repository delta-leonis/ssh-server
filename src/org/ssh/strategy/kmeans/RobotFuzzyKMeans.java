

package org.ssh.strategy.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.ssh.models.Robot;



//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

/**
 * Decompiled from {@link org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer
 * and edited, because it was lacking a few features we needed.
 *
 * Mainly the fact that we want each cluster to have a specific meaning.
 * In this case cluster0 = keeper
 *              cluster1 = defender
 *              cluster2 = attacker
 */
public class RobotFuzzyKMeans extends Clusterer<DoublePoint> {
    private final int k;
    private final int maxIterations;
    private final double fuzziness;
    private final double epsilon;
    /** A matrix depicting the current clusters in the form of double[cluster][member] */
    private double[][] membershipMatrix;
    /** All {@link DoublePoint points} currently being used for clustering */
    private List<DoublePoint> points;
    /** The current {@link CentroidCluster clusters} */
    private List<CentroidCluster<DoublePoint>> clusters;
    /** true if we're playing on the east side of the field, false otherwise. */
    private boolean eastSide;

    /**
     * Roles used to determine the initial membership matrix
     */
    enum Role{
        ATTACKER(new double[]{
                0.0,    // Keeper
                0.30,   // Defender
                0.70    // Attacker
        }),
        DEFENDER(new double[]{
                0.0,    // Keeper
                0.70,   // Defender
                0.30    // Attacker
        }),
        KEEPER(new double[]{
                0.95,    // Keeper
                0.05,   // Defender
                0.0    // Attacker
        });

        double[] matrix;

        /**
         * @param matrix The initial membership for the {@link Role}
         */
        Role(double[] matrix){
            this.matrix = matrix;
        }

        /**
         * @return the initial membership matrix
         */
        public double[] getInitialMatrix(){
            return matrix;
        }
    }

    /**
     * Naive function that checks whether the given point "belongs" to a certain group
     * Used to initialize the memberships, giving the K-means a push in the right direction
     * @param point Position of the Role we want to determine
     * @param eastSide True if playing on the east map. (if keeper positionX is positive)
     * @return The {@link Role} that describes the given location (ONLY RETURNS ATTACKER OR DEFENDER)
     */
    private Role getRoleBasedOnPoint(DoublePoint point, boolean eastSide){
        // If we're on the same eastSide as the keeper, we're defensive
        if(eastSide && point.getPoint()[0] > 0 || !eastSide && point.getPoint()[0] < 0)
            return Role.DEFENDER;
        // Else, we're an attacker
        return Role.ATTACKER;
    }

    public RobotFuzzyKMeans(double fuzziness, int maxIterations, DistanceMeasure measure, boolean eastSide) throws NumberIsTooSmallException {
        this(fuzziness, maxIterations, measure, 0.001D, eastSide);
    }

    public RobotFuzzyKMeans(double fuzziness, int maxIterations, DistanceMeasure measure, double epsilon, boolean eastSide) throws NumberIsTooSmallException {
        super(measure);
        if(fuzziness <= 1.0D) {
            throw new NumberIsTooSmallException(fuzziness, 1.0D, false);
        } else {
            this.k = 3;
            this.fuzziness = fuzziness;
            this.maxIterations = maxIterations;
            this.epsilon = epsilon;
            this.membershipMatrix = null;
            this.points = null;
            this.clusters = null;
            this.eastSide = eastSide;
        }
    }

    /**
     * @return The amount of clusters value in the Fuzzy k-means clusterer
     */
    public int getK() { return this.k; }

    /**
     * @return a {@link List} of {@link CentroidCluster clusters}, each of these {@link CentroidCluster clusters}
     * contains an amount of {@link DoublePoint DoublePoints} representing the {@link DoublePoint points} that belong
     * to the cluster.
     */
    public List<CentroidCluster<DoublePoint>> getClusters() {
        return this.clusters;
    }

    /**
     * Uses the given list of {@link Robot robots} to cluster them, and return a list of clusters
     * in the form of double[][]
     * @param robots The list of {@link Robot robots} to be clustered.
     */
    public double[][] getClusteredRobots(List<Robot> robots) {
        // Cluster the robots
        cluster(robots.stream()
                // Convert Collection<Robot> to Collection<DoublePoint>
                .map(robot -> new DoublePoint(new double[]{robot.getXPosition(), robot.getYPosition()}))
                .collect(Collectors.toList()));
        return membershipMatrix;
    }

    /**
     * Clusters the given list of {@link Robot robots} and updates their clusters
     * @param robots The list of robots to cluster
     */
    public void clusterRobots(List<Robot> robots) {
        getClusteredRobots(robots); // updates membershipMatrix[][]
        // Map return values back the the reference list
        for (int i = 0; i < robots.size(); ++i)
            robots.get(i).setClusterGroup(membershipMatrix[i]);
    }

    /**
     * Clusters the given dataPoints using the Fuzzy K-means algorithm
     * @param dataPoints The {@link DoublePoint datapoints} to be clustered
     * @return A {@link List} of {@link CentroidCluster clusters}, each containing a bunch of {@link DoublePoint points}
     *         that belong to the {@link CentroidCluster cluster}
     * @throws MathIllegalArgumentException
     */
    public List<CentroidCluster<DoublePoint>> cluster(Collection<DoublePoint> dataPoints) throws MathIllegalArgumentException {
        MathUtils.checkNotNull(dataPoints);
        int size = dataPoints.size();
        if(size < this.k) {
            throw new NumberIsTooSmallException(size, this.k, false);
        } else {
            this.points = new ArrayList(dataPoints);
            this.clusters = new ArrayList();
            this.membershipMatrix = new double[size][this.k];
            double[][] oldMatrix = new double[size][this.k];
            if(size == 0) {
                return this.clusters;
            } else {
                this.initializeMembershipMatrix();
                int pointDimension = ((Clusterable)this.points.get(0)).getPoint().length;

                int iteration;
                for(iteration = 0; iteration < this.k; ++iteration) {
                    this.clusters.add(new CentroidCluster(new DoublePoint(new double[pointDimension])));
                }

                iteration = 0;
                int max = this.maxIterations < 0?2147483647:this.maxIterations;
                double difference = 0.0D;

                do {
                    this.saveMembershipMatrix(oldMatrix);
                    this.updateClusterCenters();
                    this.updateMembershipMatrix();
                    difference = this.calculateMaxMembershipChange(oldMatrix);
                    if(difference <= this.epsilon) {
                        break;
                    }

                    ++iteration;
                } while(iteration < max);

                return this.clusters;
            }
        }
    }

    // TODO Update centroids? Try without centroid first
    private synchronized void updateClusterCenters() {
        // Counter for the points
        int membershipCounter = 0;
        // Create a list for the new clusters
        ArrayList newClusters = new ArrayList(this.k);

        // For each cluster
        for(Iterator centroidClusterIterator = this.clusters.iterator(); centroidClusterIterator.hasNext(); ++membershipCounter) {
            // Get the current centroid
            CentroidCluster cluster = (CentroidCluster)centroidClusterIterator.next();
            Clusterable center = cluster.getCenter();
            // Counter for the membership
            int pointCounter = 0;
            double[] clusterPoint = new double[center.getPoint().length];
            double sum = 0.0D;
            // Iterate through every point
            for(Iterator<DoublePoint> pointIterator = this.points.<DoublePoint>iterator(); pointIterator.hasNext(); ++pointCounter) {
                // Retrieve a point
                DoublePoint point = pointIterator.next();
                // Add (power of) some fuzziness to the point we're currenty dealing with
                double fuzziness = FastMath.pow(this.membershipMatrix[pointCounter][membershipCounter], this.fuzziness);
                double[] pointArr = point.getPoint();
                // Mutate the clusterPoint to represent the middle of the cluster
                for(int idx = 0; idx < clusterPoint.length; ++idx) {
                    clusterPoint[idx] += fuzziness * pointArr[idx];
                }

                sum += fuzziness;
            }
            // After iterating through every point, the center of the cluster will have been updated
            MathArrays.scaleInPlace(1.0D / sum, clusterPoint);
            newClusters.add(new CentroidCluster(new DoublePoint(clusterPoint)));
        }
        // Update the current clusters
        this.clusters.clear();
        this.clusters = newClusters;
    }

    /**
     * Updates the memberships of each {@link DoublePoint point}
     */
    private void updateMembershipMatrix() {
        // For each point
        for(int pointCounter = 0; pointCounter < this.points.size(); ++pointCounter) {
            DoublePoint point = this.points.get(pointCounter);
            double maxMembership = 4.9E-324D;
            int newCluster = -1;
            // For each cluster
            for(int clusterCounter = 0; clusterCounter < this.clusters.size(); ++clusterCounter) {
                double sum = 0.0D;
                // Calculate the distance between the current point and the current cluster centroid
                double distA = FastMath.abs(this.distance(point, ((CentroidCluster)this.clusters.get(clusterCounter)).getCenter()));
                double distB;
                // If the point isn't right on top of the centroid
                if(distA != 0.0D) {
                    // For each centroid
                    for(Iterator membership = this.clusters.iterator();
                        membership.hasNext();
                        sum += FastMath.pow(distA / distB, 2.0D / (this.fuzziness - 1.0D))) {
                        // Get the centroid
                        CentroidCluster c = (CentroidCluster)membership.next();
                        // Get the distance from point to centroid
                        distB = FastMath.abs(this.distance(point, c.getCenter()));
                        if(distB == 0.0D) {
                            // NaN
                            sum = 1.0D / 0.0;
                            break;
                        }
                    }
                }

                double retValue;
                if(sum == 0.0D) {
                    retValue = 1.0D;
                } else if(sum == 1.0D / 0.0) {
                    retValue = 0.0D;
                } else {
                    retValue = 1.0D / sum;
                }
                // Update membership
                this.membershipMatrix[pointCounter][clusterCounter] = retValue;
                // If the membership exceeds the max membership, update the max membership
                if(this.membershipMatrix[pointCounter][clusterCounter] > maxMembership) {
                    maxMembership = this.membershipMatrix[pointCounter][clusterCounter];
                    newCluster = clusterCounter;
                }
            }
            // Update the clusters
            ((CentroidCluster)this.clusters.get(newCluster)).addPoint(point);
        }

    }

    /**
     * Main part of the code that was edited to suit our needs.
     * Only works when k = 3
     *
     * Makes sure that the keeper cluster will always be around the keeper (first robot in list), by
     */
    private void initializeMembershipMatrix() {
        // Create an initial matrix for the keeper
        this.membershipMatrix[0] = Role.KEEPER.getInitialMatrix();

        // For each robot
        for(int pointCounter = 1; pointCounter < this.points.size(); ++pointCounter) {
            this.membershipMatrix[pointCounter] = this.getRoleBasedOnPoint(this.points.get(pointCounter), eastSide).getInitialMatrix();
            this.membershipMatrix[pointCounter] = MathArrays.normalizeArray(this.membershipMatrix[pointCounter], 1.0D);
        }
    }

    private double calculateMaxMembershipChange(double[][] matrix) {
        double maxMembership = 0.0D;

        for(int i = 0; i < this.points.size(); ++i) {
            for(int j = 0; j < this.clusters.size(); ++j) {
                double v = FastMath.abs(this.membershipMatrix[i][j] - matrix[i][j]);
                maxMembership = FastMath.max(v, maxMembership);
            }
        }

        return maxMembership;
    }

    private void saveMembershipMatrix(double[][] matrix) {
        for(int i = 0; i < this.points.size(); ++i) {
            System.arraycopy(this.membershipMatrix[i], 0, matrix[i], 0, this.clusters.size());
        }

    }
}