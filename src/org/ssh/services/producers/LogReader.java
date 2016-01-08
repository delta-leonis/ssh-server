package org.ssh.services.producers;

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
 * Created by Thomas on 6-1-2016.
 * Loads a proto log file. (takes a while) after the log file is loaded, log files can be retrieved using an index.
 * TODO fix timeouts
 */
public class LogReader extends AbstractService<ProtoPacket<?>> {
    // a logger for good measure
    protected static final Logger LOG         = Logger.getLogger();
    private FileInputStream fileStream;
    private Map<Long, byte[]> detectionMessages;
    private Map<Long, byte[]> refereeMessages;
    private List<Long> timeouts;
    private List<Long> blueGoals;
    private List<Long> yellowGoals;

    private long duration = 0;


    private final int MESSAGE_BLANK = 0,
            MESSAGE_UNKNOWN = 1,
            MESSAGE_SSL_VISION_2010 = 2,
            MESSAGE_SSL_REFBOX_2013 = 3;


    /**
     * Constructor of the {@LogReader}.
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
     * Loads the file into the {@LogReader}
     */
    public void load(){
            try {
                if(prepareLog())
                    loadFile();
            } catch (IOException exception) {
                LOG.exception(exception);
            }
    }

    /**
     * Sends a {@WrapperPacket} that belongs to the given time in ms
     * @param time in ms
     */
    public void sendDetectionMessage(Long time){
        Services.submitTask("Log Reader", () -> {
            GeneratedMessage message = parseMessage(detectionMessages.get(time));
                Pipelines.getOfDataType(WrapperPacket.class).forEach(pipe ->
                    pipe.addPacket(new WrapperPacket((Wrapper.WrapperPacket) message)).processPacket()
                );

        });
    }

    /**
     * Sends a {@RefereePacket} that belongs to the given time in ms
     * @param time in ms
     */
    public void sendRefereeMessage(Long time){
        Services.submitTask("Log Reader", () -> {
            GeneratedMessage message = parseMessage(refereeMessages.get(time));
            Pipelines.getOfDataType(WrapperPacket.class).forEach(pipe ->
                    pipe.addPacket(new RefereePacket((RefereeOuterClass.Referee) message)).processPacket()
            );
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
        fileStream.read(byteArray);
        if(!new String(byteArray).equals("SSL_LOG_FILE")) {
            LOG.warning("Log file doesn't begin with \"SSL_LOG_FILE\"");
            return false;
        }

        //The next 4 bytes must be a int. This int is a version number, we can only read version 1
        byteArray = new byte[4];
        fileStream.read(byteArray);
        if(ByteBuffer.wrap(byteArray).getInt() != 1) {
            LOG.warning("Log file isn't version 1");
            return false;
        }
        return true;
    }

    /**
     * Reads the SslDetection and SslReferee objects from the SSL Log File file
     *
     * @throws IOException
     */
    private void loadFile() throws IOException{
        byte[] type;
        byte[] length;
        byte[] message;
        int lengthProtoBuf;
        long time;
        boolean firstTime = true;
        long startTime = 0;
        while (fileStream.available() != 0) {
            //Read new time
            type = new byte[8]; // Reuse type
            fileStream.read(type);
            time = ByteBuffer.wrap(type).getLong();
            if (firstTime) {
                startTime = time;
                firstTime = false;
            }

            //Read messagetype
            type = new byte[4];
            fileStream.read(type);

            //Read length of protobuf
            length = new byte[4];
            fileStream.read(length);
            lengthProtoBuf = ByteBuffer.wrap(length).getInt();

            //Read message
            message = new byte[lengthProtoBuf];
            fileStream.read(message);

            ByteBuffer result = ByteBuffer.wrap(new byte[type.length + message.length]);
            result.put(type);
            result.put(message);

            GeneratedMessage parsedMessage = parseMessage(result.array());

            long timeDiff = (time - startTime)/1000000;

            if (parsedMessage != null) {
                if (parsedMessage instanceof Wrapper.WrapperPacket)
                    detectionMessages.put(timeDiff, result.array());
                else {
                    RefereeOuterClass.Referee refereeMessage = (RefereeOuterClass.Referee) parsedMessage;
                    if (refereeMessage.getBlue().getScore() > blueGoals.size()) {
                        blueGoals.add(timeDiff);
                    }
                    if (refereeMessage.getYellow().getScore() > yellowGoals.size()) {
                        yellowGoals.add(timeDiff);
                    }
                    if(refereeMessage.getYellow().getTimeouts() + refereeMessage.getBlue().getTimeouts() > timeouts.size())
                        timeouts.add(timeDiff);

                    refereeMessages.put(timeDiff, result.array());
                }
            }
        }
        duration = ((TreeMap<Long, byte[]>)detectionMessages).lastKey();
    }

    /**
     * Parses the given byte array to a {@link GeneratedMessage}.
     * This message can be either a {@link protobuf.Wrapper.WrapperPacket} or a {@link protobuf.RefereeOuterClass.Referee}
     * @param rawData Data in a byte array form. Generated by {@link #load()}
     * @return The {@GeneratedMessaged} parse from the raw byte data
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
     * @returns a list of times (in ms) the blue team has scored.
     */
    public List<Long> getBlueGoalTimes(){
        return blueGoals;
    }

    /**
     * @returns a list of times (in ms) the yellow team has scored.
     */
    public List<Long> getYellowGoalTimes(){
        return yellowGoals;
    }

    /**
     * @returns a list of times (in ms) the a timeout has occurred.
     * TODO Currently not working properly
     */
    public List<Long> getTimeouts(){
        return timeouts;
    }

    /**
     * @returns the duration of the game.
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
        return blueGoals.stream().filter(time -> time <= millis).collect(Collectors.toList()).size();
    }

    /**
     * Returns the score yellow team had at the given time in milliseconds
     * @param millis Time in milliseconds
     * @return the score yellow team had at the given time in milliseconds
     */
    public int getYellowScoreAtTimeMillis(long millis){
        return yellowGoals.stream().filter(time -> time <= millis).collect(Collectors.toList()).size();
    }
}
