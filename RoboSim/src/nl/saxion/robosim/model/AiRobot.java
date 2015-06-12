package nl.saxion.robosim.model;

/**
 * Represents a Robot controlled by the AI. Contains and updates location and other properties. The {@link Model} parses
 * this class to a {@link nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot SSL_DetectionRobot} with the
 * {@link Model#parseAiRobot(AiRobot)} method to make the Robot useable for the the rest of the system.
 * <p>
 * Created by Kris on 4-6-2015.
 *
 * @author Kris Minkjan
 */
public class AiRobot {

    private float velocity, orientation, currentX, currentY, rotationSpeed, shootkicker, scale = 1;
    private boolean dribble;
    private int id;

    /**
     * Creates a AiRobot with the specified id.
     * <br>
     * <i>Note: The Robot is also update according to the given id</i>
     *
     * @param id
     */
    public AiRobot(int id,  float currentX, float currentY) {
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

    public float getOrientation() {
        /* Translates it from degrees to RADIANS and offset by 90 */
        return (float) ((orientation - 90) / 57.295);
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public int getId() {
        return id;
    }

    /**
     * Gets called by the {@link Model} when de AiRobot should update it's dataset
     * TODO can be more accurate with a delta variable
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
}
