package nl.saxion.robosim.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import nl.saxion.robosim.communications.MultiCastServer;
import nl.saxion.robosim.controller.Renderer;
import nl.saxion.robosim.controller.SSL_Field;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryData;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;
import nl.saxion.robosim.model.protobuf.SslWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.assertNotNull;

/**
 * The model for this project. Functions as a a central hub between the different modules. All the data about the
 * simulation is stored in the Model. This data is rendered by the {@link nl.saxion.robosim.controller.Renderer Renderer}
 * , filled by the {@link LogReader} and manipulated by the {@link nl.saxion.robosim.controller.UIController UIController}.
 * <p>
 * When the Game Loop is running, the next {@link SSL_DetectionFrame} is computed by the {@link #nextFrame()} method. When the
 * {@link nl.saxion.robosim.controller.Renderer Renderer} is ready to render, it is collected with the
 * {@link #getLastFrame()} method.
 * </p>
 *
 * @author Daan Veldhof
 * @author Kris Minkjan
 * @author Ken Sleebos
 */
public class Model {

    //Singleton variables
    private static Model model;

    //Field variables
    private SSL_Field SSLField;
    private SSL_GeometryData geometryData;

    //Frame variables
    private LinkedList<SSL_DetectionFrame> frames = new LinkedList<>();
    private ListIterator<SSL_DetectionFrame> frameIterator;
    private int totalFrames;
    private SSL_DetectionFrame lastFrame;

    //Referee variables
    private LinkedList<SSL_Referee> referees = new LinkedList<>();
    private ListIterator<SSL_Referee> refereeIterator;
    private SSL_Referee lastReferee;
    MultiCastServer m;

    //Robot variables
    private int selectedRobot, selectedTeam;

    public List<AiRobot> getAiRobots() {
        return aiRobots;
    }

    private List<AiRobot> aiRobots = new ArrayList<>();

    //Accelerate variables
    private double acceleration = 1;

    //Settings
    private LinkedList<AiData> aiData = new LinkedList<>();


    private Model() {
        try {
            m = new MultiCastServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }


    //-------------Frame methods-------------

    /**
     * Gets all the framesnumbers. Used to display and select frame numbers in the user interface.
     *
     * @return A {@link ObservableList} with all the frame numbers
     */
    public ObservableList<String> getAllFrameNumbers() {
        int index = 0;
        ObservableList<String> frameNumbers = FXCollections.observableArrayList();
        for (SSL_DetectionFrame ignored : frames) frameNumbers.add((index++) + "");
        return frameNumbers;
    }

    public void addFrame(SSL_DetectionFrame frame) {
        assertNotNull(frame);
        frames.add(frame);

        /* Refresh the iterator */
        frameIterator = frames.listIterator();
    }

    /**
     * Calculates the next {@link SSL_DetectionFrame} that will be rendered. The {@link SSL_DetectionFrame}
     * be retrieved by {@link #getLastFrame()}. Also assigns the next {@link SSL_Referee}, which is
     * returned by {@link #getLastReferee()}.
     */
    public void nextFrame() {
        if (acceleration >= 1) {
            for (int i = 1; i < acceleration; i++) {
                /* Skip some frames */
                if (frameIterator.hasNext()) {
                    frameIterator.next();
                }
                if (refereeIterator.hasNext()) {
                    refereeIterator.next();
                }
            }
            lastReferee = refereeIterator.hasNext() ? refereeIterator.next() : lastReferee;
            lastFrame = frameIterator.hasNext() ? frameIterator.next() : lastFrame;
        } else {
            for (int i = -1; i > acceleration; i--) {
                /* Slow down */
                if (frameIterator.hasPrevious()) {
                    frameIterator.previous();
                }
                if (refereeIterator.hasPrevious()) {
                    refereeIterator.previous();
                }
            }
            lastReferee = refereeIterator.hasPrevious() ? refereeIterator.previous() : lastReferee;
            lastFrame = frameIterator.hasPrevious() ? frameIterator.previous() : lastFrame;
        }

        aiRobots.forEach(AiRobot::update);
//        aiRobots.forEach(System.out::println);
        addAiToSSLFrame();
        m.send(nextPacket(), lastReferee);

    }

    public SSL_DetectionFrame getLastFrame() {
        return lastFrame;
    }

    public void setCurrentFrame(int frameNumber) {
        frameIterator = frames.listIterator(frameNumber);
        refereeIterator = referees.listIterator(frameNumber);
    }

    public int getFrameNumber() {
        return frameIterator.nextIndex();
    }

    public LinkedList<SSL_DetectionFrame> getFrames() {
        return frames;
    }


    //-------------Referee methods-------------
    public void addReferee(SSL_Referee referee) {
        referees.add(referee);
        refereeIterator = referees.listIterator();
    }

    public SSL_Referee getLastReferee() {
        return lastReferee;
    }


    //-------------Field methods-------------
    public SSL_Field getSSLField() {
        return SSLField;
    }

    public void setSSLField(Canvas canvas) {
        assert canvas != null : "Canvas in null";
        this.SSLField = new SSL_Field(canvas, geometryData);
        for (int i = 0; i < 6; i++) {
            aiRobots.add(new AiRobot(i,(float) (SSLField.getBench_real_x()),(float) (SSLField.getBench_real_y() + SSLField.getRobot_real_size()* i)));
        }
    }

    public void setGeometry(SSL_GeometryData geometry) {
        assert geometry != null : "Setting null SSLField";
        this.geometryData = geometry;
    }


    //-------------Communication methods-------------
    public void addAiData(AiData a) {
        aiData.add(a);
    }

    public LinkedList<AiData> getAiData() {
        return aiData;
    }


    //-------------Timing methods-------------
    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Get the percentual position of the simulation.
     * Result is between 0(Start) and 100(End)
     *
     * @return Double with the position value
     */
    public double getSimulationPosition() {
        if (totalFrames == 0) {
            totalFrames = frames.size();
        }
        double position = (double) frameIterator.nextIndex() / (double) totalFrames;
        return position * 100;
    }


    //-------------Robot methods-------------

    /**
     * Checks if the x and y position is <b>in</b> the given {@link SSL_DetectionRobot Robot}.
     *
     * @param x     x-position
     * @param y     y-position
     * @param robot Robot that should be checked
     * @return True if the position is in the robot, else false
     */
    private boolean clickIsInRobot(double x, double y, SSL_DetectionRobot robot) {
        double xDifference = x - robot.getX();
        double yDifference = y - robot.getY();
        double size = 500 * SSLField.getScale();
        return xDifference < size && xDifference > -size && yDifference > -size && yDifference < size;
    }

    public int getSelectedRobotId() {
        return selectedRobot;
    }

    public int getSelectedTeam() {
        return selectedTeam;
    }

    /**
     * Returns the currently selected robot.
     *
     * @return A {@link SSL_DetectionRobot}. Returns null if no robot is selected.
     */
    public SSL_DetectionRobot getSelectedRobot() {
        for (SSL_DetectionRobot robot : selectedTeam == 0 ? lastFrame.getRobotsBlueList() : lastFrame.getRobotsYellowList()) {
            if (robot.getRobotId() == selectedRobot) {
                return robot;
            }
        }
        return null;
    }

    /**
     * Checks if there is a {@link SSL_DetectionRobot DetectionRobot} at the point that is clicked on on the canvas and if this is the case, it
     * is marked as the selected Robot. Uses the {@link #clickIsInRobot(double, double, SSL_DetectionRobot)} to determine if the
     * click is <b>in</b> a {@link SSL_DetectionRobot Robot}.
     *
     * @param x The {@link javafx.scene.input.MouseEvent MouseEvent's} x
     * @param y The {@link javafx.scene.input.MouseEvent MouseEvent's} y
     */
    public void selectRobotByPosition(double x, double y) {
        double scale = SSLField.getScale();
        x = x / scale - SSLField.getCenter_x();
        y = y / scale - SSLField.getCenter_y();

        if (lastFrame != null) {
            for (SSL_DetectionRobot robot : lastFrame.getRobotsBlueList()) {
                if (clickIsInRobot(x, y, robot)) {
                    System.out.println(robot);
                    selectedRobot = robot.getRobotId();
                    selectedTeam = 0;
                    return;
                }
            }
            for (SSL_DetectionRobot robot : lastFrame.getRobotsYellowList()) {
                if (clickIsInRobot(x, y, robot)) {
                    System.out.println(robot);
                    selectedRobot = robot.getRobotId();
                    selectedTeam = 1;
                    return;
                }
            }
        }
        /* Default: no Robot selected */
        selectedRobot = -1;
    }

    /**
     * Sets the position of the given Robot.
     *
     * @param x     x-position
     * @param y     y-position
     */
    public void setRobotPosition(double x, double y) {
        if (selectedTeam == 1) { // FIXME this should take te ai team. This is not always this team
            AiRobot robot = aiRobots.get(selectedRobot);
            double scale = SSLField.getScale();
            robot.setX((float) (x / scale - SSLField.getCenter_x()));
            robot.setY((float) (y / scale - SSLField.getCenter_y()));
            addAiToSSLFrame();
        }
    }


    //-------------Other methods-------------

    /**
     * Resets the entire game data
     */
    public void clear() {
        SSLField = null;
        frames.clear();
        referees.clear();
//        aiRobots.clear();
        totalFrames = 0;
        lastFrame = null;
        lastReferee = null;
        selectedRobot = -1;
        selectedRobot = -1;
    }

    //-----------------AI Methods------------------------

    /**
     * Parse an {@link AiRobot} to an {@link SSL_DetectionRobot SSL_DetectionRobot} so it is useable for the
     * {@link Renderer Renderer} and the AI.
     * @param aiRobot   The {@link AiRobot} to be parsed.
     * @return  An {@link SSL_DetectionRobot SSL_DetectionRobot} from the AiRobot
     */
    private SSL_DetectionRobot parseAiRobot(AiRobot aiRobot) {
        return SSL_DetectionRobot.newBuilder()
                .setOrientation(aiRobot.getOrientation())
                .setConfidence(0.9f)
                .setRobotId(aiRobot.getId())
                .setPixelX(aiRobot.getCurrentX())
                .setPixelY(aiRobot.getCurrentY())
                .setX(aiRobot.getCurrentX())
                .setY(aiRobot.getCurrentY())
                .setHeight(145.0f)
                .build();
    }

    /**
     * Adds all the {@link AiRobot AiRobots} to the {@link SSL_DetectionFrame SSL_DetectionFrame}. Uses the
     * {@link #parseAiRobot(AiRobot)} to add all the {@link AiRobot AiRobots} to a Frame.
     */
    private void addAiToSSLFrame() {
        SSL_DetectionFrame.Builder builder = lastFrame.toBuilder();
        builder.clearRobotsYellow();
        for (AiRobot robot : aiRobots) {
            builder.addRobotsYellow(parseAiRobot(robot));
        }
        lastFrame = builder.build();
    }

    public SslWrapper.SSL_WrapperPacket nextPacket() {
//        System.out.println("Blue:");
//        lastFrame.getRobotsBlueList().forEach(System.out::println);
//        System.out.println("Yellow:");
//        lastFrame.getRobotsYellowList().forEach(System.out::println);

        return SslWrapper.SSL_WrapperPacket.newBuilder()
                .setGeometry(geometryData)
                .setDetection(lastFrame)
                .build();
    }

}
