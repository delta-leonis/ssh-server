package nl.saxion.robosim.model;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.Platform;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslWrapper.SSL_WrapperPacket;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * Constructor for Logreader
     * Sets the filename to "RoboSim\\2013-06-29-133738_odens_mrl.log"
     */
    public LogReader() {
        this("RoboSim\\2013-06-29-133738_odens_mrl.log");
    }

    /**
     * Constructor for Logreader
     * Sets the filename to the given fileName
     */
    public LogReader(String fileName) {
        assert fileName != null : "File name can't be null";
        Model model = Model.getInstance();
        model.clear();
        System.out.println("FILEDIALOG");

        blueTimes = new HashMap<>();
        yellowTimes = new HashMap<>();

        frames = new LinkedList<>();
        referees = new LinkedList<>();

        try {
            System.out.println("FILEDIALOG");
            fileStream = new FileInputStream(fileName);
            readFrames();
            model.setFrames(frames);
            model.setReferees(referees);
            Platform.runLater(model::update);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read from file");
            e.printStackTrace();
        }
    }

    /**
     * Reads the SslDetection and SslReferee objects from the file
     *
     * @throws IOException
     */
    public void readFrames() throws IOException {
        System.out.println("Read Frames");
        long oldTime = 0, newTime, totalTime = 0, bytesRead = 0;

        //fileStream to read the bytes from

        //The first 12 bytes of the file must be a String with value "SSL_LOG_FILE"
        byte[] byteArray = new byte[12];
        bytesRead = fileStream.read(byteArray);
        assert bytesRead == 12 : "File header not long enough!";
        assert new String(byteArray).equals("SSL_LOG_FILE") : "This file is not a SSL log file";

        //The next 4 bytes must be a int. This int is a version number, we can only read version 1
        byteArray = new byte[4];
        bytesRead = fileStream.read(byteArray);
        assert bytesRead == 4 : "File version not long enough!";
        assert ByteBuffer.wrap(byteArray).getInt() == 1 : "This file is not version 1";

        //The rest of the file are the messages
        byte[] array;
        int messageType;
        int lengthProtoBuf;
        while (fileStream.available() != 0) {
            //Read new time
            array = new byte[8];
            bytesRead = fileStream.read(array);
            assert bytesRead == 8 : "File time was not long enough";
            newTime = ByteBuffer.wrap(array).getLong();
            if (oldTime == 0) {
                oldTime = newTime;
            }
            totalTime += (newTime - oldTime);
            oldTime = newTime;

            //Read messagetype
            array = new byte[4];
            bytesRead = fileStream.read(array);
            assert bytesRead == 4 : "File messageType was not long enough";
            messageType = ByteBuffer.wrap(array).getInt();

            //Read length of protobuf
            array = new byte[4];
            bytesRead = fileStream.read(array);
            assert bytesRead == 4 : "File protobuf length was not long enough";
            lengthProtoBuf = ByteBuffer.wrap(array).getInt();

            //Read message
            array = new byte[lengthProtoBuf];
            bytesRead = fileStream.read(array);
            assert bytesRead == lengthProtoBuf : "File message length was not long enough";

            switch (messageType) {
                case MESSAGE_BLANK:
                    System.out.println("Blank message");
                    break;
                case MESSAGE_UNKNOWN:
                    System.out.println("Unknown message");
                    break;
                case MESSAGE_SSL_VISION_2010:
                    //update bytes that describe a frame
                    updateFrame(array);
                    updateCount++;
                    break;
                case MESSAGE_SSL_REFBOX_2013:
                    //update bytes that describe referee
                    curReferee = SSL_Referee.parseFrom(array);
                    break;
            }

            //If it's the right time, add frame to model
            if (totalTime >= FRAMETIME) {
                removeGhostRobots();
                frames.add(curFrame);
                curFrame = SSL_DetectionFrame.parseFrom(curFrame.toByteArray());
                referees.add(curReferee);
                totalTime -= FRAMETIME;
//                System.out.println("total time " + totalTime );
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

        //frame with new information
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
