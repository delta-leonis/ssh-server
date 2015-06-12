package nl.saxion.robosim.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The AiData is data received from the Artificial Intelligence.
 * <p>
 * Created by Damon on 27-5-2015.
 *
 * @author Damon Daalhuisen
 */
public class AiData {
    private int messageType;
    private int robotID;
    private short direction;
    private short directionSpeed;
    private short rotationSpeed;
    private int shootKicker;
    private boolean dribble;

    /**
     * Creates an AiData object
     *
     * @param b byte array
     */
    public AiData(byte[] b) {
        ByteBuffer dataBuffer = ByteBuffer.wrap(b);
        dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        messageType = dataBuffer.get();
        robotID = dataBuffer.get();
        direction = dataBuffer.getShort();
        directionSpeed = dataBuffer.getShort();
        rotationSpeed = dataBuffer.getShort();
        shootKicker = dataBuffer.get();
        int dribble = dataBuffer.get();
        if (dribble == 0) {
            this.dribble = false;
        } else {
            this.dribble = true;
        }
    }

    public int getMessageType() {
        return messageType;
    }

    public int getRobotID() {
        return robotID;
    }

    public short getDirection() {
        return direction;
    }

    public short getDirectionSpeed() {
        return directionSpeed;
    }

    public short getRotationSpeed() {
        return rotationSpeed;
    }

    public int getShootKicker() {
        return shootKicker;
    }

    public Boolean getDribble() {
        return dribble;
    }

    @Override
    public String toString() {
        return "AiData{" +
                "messageType=" + messageType +
                ", robotID=" + robotID +
                ", direction=" + direction +
                ", directionSpeed=" + directionSpeed +
                ", rotationSpeed=" + rotationSpeed +
                ", shootKicker=" + shootKicker +
                ", dribble=" + dribble +
                '}';
    }
}


