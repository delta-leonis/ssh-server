package org.ssh.logs;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.pipelines.packets.RefereePacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.services.AbstractService;
import org.ssh.ui.components.bottomsection.Timeslider;
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
 * Variation on the code of RoboSim
 */
public class LogReader extends AbstractService<ProtoPacket<?>> {
    // a logger for good measure
//    protected static final Logger LOG         = Logger.getLogger();
    private FileInputStream fileStream;
    private TreeMap<Long, byte[]> detectionMessages;
    private TreeMap<Long, byte[]> refereeMessages;
    private List<Long> timeouts;
    private List<Long> blueGoals;
    private List<Long> yellowGoals;

    private double averageDelay = 0;

    private long duration = 0;
    // Variables that keep track of the last packet sent. They're used to avoid duplicate package being sent.
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
                // Parse the message
                GeneratedMessage message = parseMessage(decto);
                // And put it on the pipeline
                Pipelines.getOfDataType(WrapperPacket.class).forEach(pipe ->
                        pipe.addPacket(new WrapperPacket((Wrapper.WrapperPacket) message)).processPacket()
                );
                // Update the last time we updated detection.
                lastDetection = detectionTime;
            }
        });
    }

    public Wrapper.WrapperPacket getWrapperPacket(long index){
        return (Wrapper.WrapperPacket)parseMessage(detectionMessages.get(index));
    }

    public RefereeOuterClass.Referee getRefereePacket(long index){
        return (RefereeOuterClass.Referee)parseMessage(refereeMessages.get(index));
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
                // Parse it to a to a GeneratedMessage
                GeneratedMessage message = parseMessage(referee);
                // Put it on the pipeline
                Pipelines.getOfDataType(RefereePacket.class).forEach(pipe ->
                        pipe.addPacket(new RefereePacket((RefereeOuterClass.Referee) message)).processPacket()
                );
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
        long count = 0;
        // The time of the previous log record. Used to calculate the average time in between records.
        long lastTime = 0;

        while (fileStream.available() != 0) {
            count++;
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

            ByteBuffer result = ByteBuffer.wrap(new byte[type.length + message.length]);
            result.put(type);
            result.put(message);

            GeneratedMessage parsedMessage = parseMessage(result.array());

            long timeDiff = (time - startTime)/1000000;
            // Calculate average delay
            averageDelay = ((timeDiff - lastTime) + averageDelay*count) / count;
            lastTime = timeDiff;

            if (parsedMessage != null) {
                // If the current message is a wrapper
                if (parsedMessage instanceof Wrapper.WrapperPacket)
                    // Just add it to the detection messages
                    detectionMessages.put(timeDiff, result.array());
                else if(parsedMessage instanceof RefereeOuterClass.Referee){
                    // If not, it's a referee message
                    RefereeOuterClass.Referee refereeMessage = (RefereeOuterClass.Referee) parsedMessage;
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
                    refereeMessages.put(timeDiff, result.array());
                }
            }
        }
        duration = detectionMessages.lastKey();
    }

    /**
     * Parses the given byte array to a {@link GeneratedMessage}.
     * This message can be either a {@link protobuf.Wrapper.WrapperPacket} or a {@link protobuf.RefereeOuterClass.Referee}
     * @param rawData Data in a byte array form. Generated by {@link #load()}
     * @return The {@link GeneratedMessage} parse from the raw byte data
     */
    public GeneratedMessage parseMessage(byte[] rawData) {
        try {
            int messageType = ByteBuffer.wrap(Arrays.copyOfRange(rawData, 0, 4)).getInt();
            byte[] array = Arrays.copyOfRange(rawData, 4, rawData.length);

            switch (messageType) {
                case MESSAGE_SSL_VISION_2010:
                    //update bytes that describe a frame
                    return Wrapper.WrapperPacket.parseFrom(array);

                case MESSAGE_SSL_REFBOX_2013:
                    //update bytes that describe referee
                    return RefereeOuterClass.Referee.parseFrom(array);
            }
        } catch(InvalidProtocolBufferException exception){
            LOG.exception(exception);
        }
        return null;
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
        LOG.fine("Filestream not initialized.");
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
        LOG.fine("Filestream not initialized.");
        return 0;
    }

    /**
     * @return the average delay between frames. Used for {@link Timeslider#updateKeyFrame()}
     */
    public double getAverageDelay(){
        return averageDelay;
    }
}