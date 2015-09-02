package nl.saxion.robosim.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.saxion.robosim.exception.InvalidLogFileException;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee.TeamInfo;
import nl.saxion.robosim.model.protobuf.SslWrapper.SSL_WrapperPacket;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Reads Small Soccer League logs en parses the so they are usable for the {@link Model} by creating
 * {@link SSL_DetectionFrame Frames} and {@link SSL_Referee Referees}.
 * <p>
 * Created by Fieldhof on 20-5-2015.
 *
 * @author Daan Veldhof
 * @author Daan Brandt
 */
public class LogReader {

    private final int FRAMETIME = 16000000,
            MESSAGE_BLANK = 0,
            MESSAGE_UNKNOWN = 1,
            MESSAGE_SSL_VISION_2010 = 2,
            MESSAGE_SSL_REFBOX_2013 = 3;


    private FileInputStream fileStream;

    private LinkedList<SSL_DetectionFrame> frames;
    private LinkedList<SSL_Referee> referees;

    private SSL_DetectionFrame curFrame;
    private SSL_Referee curReferee;

    private Map<Integer, Long> blueTimes, yellowTimes;
    private long updateCount = 0;
    private boolean fieldIsSet = false;

    /**
     * Constructor for LogReader
     * @param fileName
     * @throws FileNotFoundException, InvalidProtocolBufferException, InvalidLogFileException, IOException
     */
    public LogReader(String fileName) throws IOException{
        Model model = Model.getInstance();
        model.clear();
        blueTimes = new HashMap<>();
        yellowTimes = new HashMap<>();

        frames = new LinkedList<>();
        referees = new LinkedList<>();

        fileStream = new FileInputStream(fileName);

        readFrames();
        model.setFrames(frames);
        model.setReferees(referees);
    }

    /**
     * Reads the SslDetection and SslReferee objects from the SSL Log File file
     *
     * @throws IOException
     */
    public void readFrames() throws IOException {
//        System.out.println("Read Frames");
        long oldTime = 0, newTime, totalTime = 0;

        //The first 12 bytes of the file must be a String with value "SSL_LOG_FILE"
        byte[] byteArray = new byte[12];
        fileStream.read(byteArray);
        if(!new String(byteArray).equals("SSL_LOG_FILE")) {
            throw new InvalidLogFileException("Log file doesn't begin with \"SSL_LOG_FILE\"");
        }

        //The next 4 bytes must be a int. This int is a version number, we can only read version 1
        byteArray = new byte[4];
        fileStream.read(byteArray);
        if(ByteBuffer.wrap(byteArray).getInt() != 1) {
            throw new InvalidLogFileException("Log file isn't version 1");
        }

        //The rest of the file are the messages
        byte[] array;
        int messageType;
        int lengthProtoBuf;
        while (fileStream.available() != 0) {
            //Read new time
            array = new byte[8];
            fileStream.read(array);

            newTime = ByteBuffer.wrap(array).getLong();
            if (oldTime == 0) {
                oldTime = newTime;
            }
            totalTime += (newTime - oldTime);
            oldTime = newTime;

            //Read messagetype
            array = new byte[4];
            fileStream.read(array);
            messageType = ByteBuffer.wrap(array).getInt();

            //Read length of protobuf
            array = new byte[4];
            fileStream.read(array);
            lengthProtoBuf = ByteBuffer.wrap(array).getInt();

            //Read message
            array = new byte[lengthProtoBuf];
            fileStream.read(array);

            switch (messageType) {
                case MESSAGE_BLANK:
//                    System.out.println("Blank message");
                    break;
                case MESSAGE_UNKNOWN:
//                    System.out.println("Unknown message");
                    break;
                case MESSAGE_SSL_VISION_2010:
                    //update bytes that describe a frame
                    updateFrame(array);
                    updateCount++;
                    break;
                case MESSAGE_SSL_REFBOX_2013:
                    //update bytes that describe referee
                    curReferee = parseReferee(SSL_Referee.parseFrom(array));
                    break;
            }

            //If it's the right time, add frame to model
            if (totalTime >= FRAMETIME) {
                removeGhostRobots();
                frames.add(curFrame);
                curFrame = SSL_DetectionFrame.parseFrom(curFrame.toByteArray());
                referees.add(curReferee);
                totalTime -= FRAMETIME;
            }
        }
        fileStream.close();
    }

    /**
     * Updates the curFrame with the information in the array
     * @param array byte[] with the newest information
     * @throws InvalidProtocolBufferException
     */
    void updateFrame(byte[] array) throws InvalidProtocolBufferException {
        SSL_WrapperPacket message = SSL_WrapperPacket.parseFrom(array);
        if (!fieldIsSet && message.hasGeometry()) {
            fieldIsSet = true;
            Model.getInstance().setGeometry(message.getGeometry());
        }
        if(!message.hasDetection()) {
            return;
        }
        Settings settings = Settings.getInstance();
        if(curFrame == null) {
            SSL_DetectionFrame.Builder builder = message.getDetection().toBuilder();
            if(!settings.hasTeamBlue()) {
                builder = builder.clearRobotsBlue();
            }
            if(!settings.hasTeamYellow()) {
                builder = builder.clearRobotsYellow();
            }
            curFrame = builder.build();
            return;
        }

        SSL_DetectionFrame newFrame = message.getDetection();

        //create a builder from the current frame to edit it.
        SSL_DetectionFrame.Builder builder = curFrame.toBuilder();

        //add balls to builder
        if (!newFrame.getBallsList().isEmpty()) {
            builder = builder.clearBalls();
        }
        builder = builder.addAllBalls(newFrame.getBallsList());

        //add blue robots to builder
        if(settings.hasTeamBlue()) {
            for (SSL_DetectionRobot newRobot : newFrame.getRobotsBlueList()) {

                //get index of the new robot
                int index = getIndexOfRobot(newRobot.getRobotId(), builder.getRobotsBlueList());

                //if robot already exists
                if (index >= 0) {
                    builder = builder.removeRobotsBlue(index);
                }
                builder = builder.addRobotsBlue(newRobot);
                blueTimes.put(newRobot.getRobotId(), updateCount);
            }
        }

        //add yellow robots to builder
        if(settings.hasTeamYellow()) {
            for (SSL_DetectionRobot newRobot : newFrame.getRobotsYellowList()) {
                //get index of the new robot
                int index = getIndexOfRobot(newRobot.getRobotId(), builder.getRobotsYellowList());

                //if robot already exists
                if (index >= 0) {
                    builder = builder.removeRobotsYellow(index);
                }
                builder = builder.addRobotsYellow(newRobot);
                yellowTimes.put(newRobot.getRobotId(), updateCount);
            }
        }

        //update other components
        builder = builder.setFrameNumber(newFrame.getFrameNumber())
                .setTCapture(newFrame.getTCapture())
                .setTSent(newFrame.getTSent());

        //build the updated frame
        curFrame = builder.build();
    }

    /**
     * Helper method to retrieve index of Robot with id id
     * @param id id of the Robot to search for
     * @param robots list of the Robots to search through
     * @return index of Robot with id id, -1 if robot doesn't exist
     */
    private int getIndexOfRobot(int id, List<SSL_DetectionRobot> robots) {
        for(SSL_DetectionRobot robot : robots) {
            if(id == robot.getRobotId()) {
                return robots.indexOf(robot);
            }
        }
        return -1;
    }

    /**
     * Method for setting the keeper id
     * @param referee referee object to change
     * @return the changed referee object
     */
    private SSL_Referee parseReferee (SSL_Referee referee) {
        Settings settings = Settings.getInstance();

        SSL_Referee.Builder builder = referee.toBuilder();
        int keeperID = settings.getKeeperId();

        if (!settings.hasTeamYellow()) {
            TeamInfo teamInfo = builder.getYellowBuilder().setGoalie(keeperID).build();
            builder = builder.setYellow(teamInfo);
        }
        if (!settings.hasTeamBlue()) {
            TeamInfo teamInfo = builder.getBlueBuilder().setGoalie(keeperID).build();
            builder = builder.setBlue(teamInfo);
        }

        return builder.build();
    }

    /**
     * Helper method for removing Robots that are not in the field anymore
     * A robot is not in the field anymore if the updateCount of the Robot is 200 lower than
     * the updateCount of the LogReader. This means that the Robot is deleted from the list if
     * it's not updated anymore.
     */
    private void removeGhostRobots() {
        SSL_DetectionFrame.Builder builder = curFrame.toBuilder();
        //remove blue ghosts
        for(int i = 0; i < builder.getRobotsBlueList().size(); i++) {
            SSL_DetectionRobot robot = builder.getRobotsBlue(i);
            if(blueTimes.get(robot.getRobotId()) + 200 < updateCount) {
                builder.removeRobotsBlue(i);
                i--;
            }
        }

        //remove yellow ghosts
        for(int i = 0; i < builder.getRobotsYellowList().size(); i++) {
            SSL_DetectionRobot robot = builder.getRobotsYellow(i);
            if(yellowTimes.get(robot.getRobotId()) + 200 < updateCount) {
                builder.removeRobotsYellow(i);
                i--;
            }
        }

        curFrame = builder.build();
    }
}
