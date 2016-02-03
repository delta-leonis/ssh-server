package org.ssh.logs;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.pipelines.packets.RefereePacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.services.AbstractService;
import org.ssh.util.Logger;
import protobuf.RefereeOuterClass;
import protobuf.Wrapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads a proto log file. (takes a while) after the log file is loaded, log files can be retrieved using an index.
 *
 * @author Thomas Hakkers
 */
public class LogReader extends AbstractService<ProtoPacket<?>> {

    /** The {@link FileInputStream} used to read the *.log files */
    private FileInputStream fileStream;
    /** {@link TreeMap} containing all detection messages and timestamps in milliseconds. */
    private TreeMap<Long, byte[]> detectionMessages;
    /** {@link TreeMap} containing all referee messages and timestamps in milliseconds. */
    private TreeMap<Long, byte[]> refereeMessages;
    /** List with timeout timestamps in milliseconds */
    private List<Long> timeouts;
    /** List with timestamps when the blue team scored */
    private List<Long> blueGoals;
    /** List with timestamps when the yellow team scored */
    private List<Long> yellowGoals;
    /** Duration of the current log in milliseconds */
    private long duration = 0;
    /** Variables that keep track of the last packet sent. They're used to avoid duplicate package being sent. */
    private Long lastDetection;
    private Long lastReferee;

    private final static int
            MESSAGE_SSL_VISION_2010 = 2,
            MESSAGE_SSL_REFBOX_2013 = 3;


    /**
     * Constructor of the {@link LogReader}.
     * @param path The path to the log this class will load
     */
    public LogReader(String path){
        super("logreader");
        try {
            fileStream = new FileInputStream(path);
            detectionMessages = new TreeMap<>();
            refereeMessages = new TreeMap<>();
            timeouts = new ArrayList<>();
            blueGoals = new ArrayList<>();
            yellowGoals = new ArrayList<>();
        }catch(IOException exception){
            LOG.exception(exception);
        }
    }

    /**
     * Loads the file into the {@link LogReader}
     */
    public void load(){
        try {
            if(fileStream != null && prepareLog())
                loadFile();
            else
                LOG.fine("Filestream not initialized.");
        } catch (IOException exception) {
            LOG.exception(exception);
        }
    }

    /**
     * Sends a {@link WrapperPacket} that belongs to the given time in ms
     * @param time in ms
     */
    public synchronized void sendDetectionMessage(Long time){
        Services.submitTask("logreader-detection-message", () -> {
            // Figure out which packet the given key belongs to
            Long detectionTime = detectionMessages.floorKey(time);
            // If this key was used last frame, don't use it
            if(!detectionTime.equals(lastDetection)) {
                // Retrieve the detection message
                byte[] decto = detectionMessages.get(detectionTime);
                // And put it on the pipeline
                Pipelines.getOfDataType(WrapperPacket.class).forEach(pipe -> {
                            try {
                                pipe.addPacket(new WrapperPacket(Wrapper.WrapperPacket.parseFrom(decto))).processPacket();
                            } catch (InvalidProtocolBufferException exception) {
                                LogReader.LOG.exception(exception);
                            }
                        }
                );
                // Update the last time we updated detection.
                lastDetection = detectionTime;
            }
        });
    }

    /**
     * @param index A long representing a time in milliseconds from the current match log
     * @return a {@link protobuf.Wrapper.WrapperPacket WrapperPacket} at the given timestamp in the map
     */
    public Wrapper.WrapperPacket getWrapperPacket(long index){
        try {
            return Wrapper.WrapperPacket.parseFrom(detectionMessages.get(index));
        } catch (InvalidProtocolBufferException exception) {
            LogReader.LOG.exception(exception);
            return null;
        }
    }

    /**
     * @param index A long representing a time in milliseconds from the current match log
     * @return a {@link protobuf.RefereeOuterClass.Referee Referee} at the given timestamp in the map
     */
    public RefereeOuterClass.Referee getRefereePacket(long index){
        try {
            return RefereeOuterClass.Referee.parseFrom(refereeMessages.get(index));
        } catch (InvalidProtocolBufferException exception) {
            LogReader.LOG.exception(exception);
            return null;
        }
    }

    /**
     * Sends a {@link RefereePacket} that belongs to the given time in ms
     * @param time in ms
     */
    public synchronized void sendRefereeMessage(Long time){
        Services.submitTask("logreader-referee-message", () -> {
            Long refereeTime = refereeMessages.floorKey(time);
            // Only send if both this message isn't the same as the last one
            if(!refereeTime.equals(lastReferee)) {
                // Retrieve the referee message
                byte[] referee = refereeMessages.get(refereeTime);
                // Put it on the pipeline
                Pipelines.getOfDataType(RefereePacket.class).forEach(pipe -> {
                    try {
                        pipe.addPacket(new RefereePacket(RefereeOuterClass.Referee.parseFrom(referee))).processPacket();
                    } catch (InvalidProtocolBufferException exception) {
                        LogReader.LOG.exception(exception);
                    }
                });
                // Update the last time this frame was used, so frames don't get used twice.
                lastReferee = refereeTime;
            }
        });
    }


    /**
     * Prepares the class for logging.
     * The function basically checks whether it's a valid log file.
     * @throws IOException
     */
    private boolean prepareLog() throws IOException{
        //The first 12 bytes of the file must be a String with value "SSL_LOG_FILE"
        byte[] byteArray = new byte[12];
        int returnValue = fileStream.read(byteArray);
        // If returnValue == -1 : We've reached the EOF.
        // If the first string isn't SSL_LOG_FILE, the file doesn't follow the protocol
        if(!new String(byteArray).equals("SSL_LOG_FILE") || returnValue == -1) {
            LOG.fine("Log file doesn't begin with \"SSL_LOG_FILE\". returnValue = " + returnValue);
            return false;
        }

        //The next 4 bytes must be a int. This int is a version number, we can only read version 1
        byteArray = new byte[4];
        returnValue = fileStream.read(byteArray);
        // If returnValue == -1 : We've reached the EOF.
        // If the logfile version isn't version 1, this logreader won't work.
        // (At the time of writing, the protocol only has a version 1)
        if(ByteBuffer.wrap(byteArray).getInt() != 1 || returnValue == -1) {
            LOG.fine("Log file isn't version 1. returnValue = " + returnValue);
            return false;
        }
        return true;
    }

    /**
     * Reads the {@link protobuf.Wrapper.WrapperPacket WrapperPackets} and
     * {@link protobuf.RefereeOuterClass.Referee RefereePackets} objects from the SSL Log File file
     *
     * @throws IOException
     */
    private void loadFile() throws IOException{
        // If it's the first time, make sure to save the startTime. (First time value occurring in log)
        boolean firstTime = true;
        // The exact time at which the game started
        long startTime = 0;

        while (fileStream.available() != 0) {
            //Read new time
            byte[] type = new byte[8]; // Reuse type
            fileStream.read(type);
            long time = ByteBuffer.wrap(type).getLong();
            if (firstTime) {
                startTime = time;
                firstTime = false;
            }

            //Read messagetype
            type = new byte[4];
            fileStream.read(type);

            //Read length of protobuf
            byte[] length = new byte[4];
            fileStream.read(length);
            int lengthProtoBuf = ByteBuffer.wrap(length).getInt();
            //Read message
            byte[] message = new byte[lengthProtoBuf];
            fileStream.read(message);

            long timeDiff = (time - startTime)/1000000;

            // If the current message is a wrapper
            if (ByteBuffer.wrap(type).getInt() == MESSAGE_SSL_VISION_2010)
                // Just add it to the detection messages
                detectionMessages.put(timeDiff, message);
            else if(ByteBuffer.wrap(type).getInt() == MESSAGE_SSL_REFBOX_2013){
                // If not, it's a referee message
                RefereeOuterClass.Referee refereeMessage = RefereeOuterClass.Referee.parseFrom(message);
                // Save when blue scored
                if (refereeMessage.getBlue().getScore() > blueGoals.size()) {
                    blueGoals.add(timeDiff);
                }
                // Save when yellow scored
                if (refereeMessage.getYellow().getScore() > yellowGoals.size()) {
                    yellowGoals.add(timeDiff);
                }
                // Save when timeouts happened
                if(refereeMessage.getYellow().getTimeouts() + refereeMessage.getBlue().getTimeouts() > timeouts.size())
                    timeouts.add(timeDiff);
                // Save the referee message
                refereeMessages.put(timeDiff, message);
            }
        }
        duration = detectionMessages.lastKey();
    }

    /**
     * @return a list of times (in ms) the blue team has scored.
     */
    public List<Long> getBlueGoalTimes(){
        return blueGoals;
    }

    /**
     * @return a list of times (in ms) the yellow team has scored.
     */
    public List<Long> getYellowGoalTimes(){
        return yellowGoals;
    }

    /**
     * @return a list of times (in ms) the a timeout has occurred.
     */
    public List<Long> getTimeouts(){
        return timeouts;
    }

    /**
     * @return the duration of the game.
     */
    public long getDuration(){
        return duration;
    }

    /**
     * Returns the score blue team had at the given time in milliseconds
     * @param millis Time in milliseconds
     * @return the score blue team had at the given time in milliseconds
     */
    public int getBlueScoreAtTimeMillis(long millis){
        if(fileStream != null)
            return blueGoals.stream().filter(time -> time <= millis).collect(Collectors.toList()).size();
        LogReader.LOG.fine("Filestream not initialized.");
        return 0;
    }

    /**
     * Returns the score yellow team had at the given time in milliseconds
     * @param millis Time in milliseconds
     * @return the score yellow team had at the given time in milliseconds
     */
    public int getYellowScoreAtTimeMillis(long millis){
        if(fileStream != null)
            return yellowGoals.stream().filter(time -> time <= millis).collect(Collectors.toList()).size();
        LogReader.LOG.fine("Filestream not initialized.");
        return 0;
    }
}