package nl.saxion.robosim.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Robot controlled by the AI. Contains and updates location and other properties. The {@link Model} parses
 * this class to a {@link nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot SSL_DetectionRobot} with the
 * {@link Model#parseAiRobot(AiRobot)} method to make the Robot useable for the the rest of the system.
 * <p>
 * This class uses <b>synchronized</b> methods because it can be accessed concurrently from the
 * {@link nl.saxion.robosim.communications.AIListener AIListener}.
 * </p>
 * Created by Kris on 4-6-2015.
 *
 * @author Kris Minkjan
 */
public class AiRobot {

    private LinkedList<Target> targetList = new LinkedList<>();
    private double velocity, orientation, currentX, currentY, rotationSpeed, shootkicker, scale = 1;
    private boolean dribble;
    private int id;
    private double velocity_X, velocity_Y;

    /**
     * Creates a AiRobot with the specified id.
     * <br>
     * <i>Note: The Robot is also update according to the given id</i>
     *
     * @param id
     */
    public AiRobot(int id, float currentX, float currentY) {
        System.out.println("X : " + currentX + " : Y : " + currentY);
        this.currentX = currentX;
        this.currentY = currentY;
        orientation = 270;
        velocity = 0;
        this.id = id;
    }

    public synchronized void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public synchronized void setShootkicker(float shootkicker) {
        this.shootkicker = shootkicker;
    }

    public synchronized void setDribble(boolean dribble) {
        this.dribble = dribble;
    }

    public synchronized void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public synchronized void setRotationSpeed(float rotation) {
        this.rotationSpeed = rotation;
    }

    public void setX(float x) {
        currentX = x;
    }

    public double getOrientation() {
        /* Translates it from degrees to RADIANS and offset by 90 */
        return (float) ((orientation - 90) / 57.295);
    }

    public void addDegrees(double degrees) {
        this.orientation += degrees;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public int getId() {
        return id;
    }

    /**
     * Gets called by the {@link Model} when de AiRobot should update it's dataset
     */
    public synchronized void update() {
        double velocity_X = (velocity / 60) * Math.cos(orientation);
        double velocity_Y = (velocity / 60) * Math.sin(orientation);

        currentX += velocity_X;
        currentY += velocity_Y;
    }

    public void setY(float y) {
        currentY = y;
    }

    @Override
    public String toString() {
        return "AiRobot{" +
                ", velocity=" + velocity +
                ", orientation=" + orientation +
                ", currentX=" + currentX +
                ", currentY=" + currentY +
                ", id=" + id +
                '}';
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /* ################### TARGETING STUFF ######################## */

    public synchronized void setTargetList(LinkedList<Target> list) {
        this.targetList = list;
    }

    public synchronized List<Target> targetList() {
        return targetList;
    }

    public synchronized void updateTarget() {
        if (!targetList.isEmpty()) {
            Target target = targetList.getFirst();
            // Check distance to target
            double distance = distanceTo(currentX, currentY, target.getX(), target.getY());
            if (distance < 50) {
                targetList.remove();
                if (!targetList.isEmpty()) {
                    target = targetList.getFirst();
                }
            }
            if (distance > 400) {
                // Calculate angle towards
                double targetDegrees = Math.toDegrees(Math.atan2(target.getY() - currentY, target.getX() - currentX));
                orientation = (float) targetDegrees + 90f;
                velocity_X = (1000 / 60) * Math.cos(targetDegrees);
                velocity_Y = (1000 / 60) * Math.sin(targetDegrees);
            }
            currentX += velocity_X;
            currentY += velocity_Y;
        }
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 x coordinate of point 1
     * @param y1 y coordinate of point 1
     * @param x2 x coordinate of point 2
     * @param y2 y coordinate of point 2
     * @return The distance in mm
     */
    private double distanceTo(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public synchronized void addTarget(double x, double y) {
        targetList.add(new Target(x, y));
    }
}
