package nl.saxion.robosim.communications;

import nl.saxion.robosim.model.Target;

import java.util.LinkedList;

/**
 * Packet used to transport AI information.
 * Created by Kris on 17-6-2015.
 */
public class AIPacket {
    private final int id;
    private final double rotationSpeed, movementSpeed;
    private final String role;
    private LinkedList<Target> targetList;

    /**
     * Creates a AIPacket
     *
     * @param rotationSpeed The speed the robot is rotating in mm/s
     * @param movementSpeed The speed the robot is moving in mm/s
     * @param role          The robot's role
     */
    public AIPacket(int id, double rotationSpeed, double movementSpeed, String role) {
        this.id = id;
        this.rotationSpeed = rotationSpeed;
        this.movementSpeed = movementSpeed;
        this.role = role;
        this.targetList = new LinkedList<>();
    }

    public void addTarget(double x, double y) {
        //targetList.add(new AiRobot.Target(x,y));
    }

    public int getId() {
        return id;
    }

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public String getRole() {
        return role;
    }

    public LinkedList<Target> getTargetList() {
        return targetList;
    }

}


