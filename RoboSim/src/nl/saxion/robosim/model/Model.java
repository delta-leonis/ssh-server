package nl.saxion.robosim.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import nl.saxion.robosim.communications.MultiCastServer;
import nl.saxion.robosim.controller.Renderer;
import nl.saxion.robosim.controller.SSL_Field;
import nl.saxion.robosim.controller.UIController;
import nl.saxion.robosim.model.protobuf.SslDetection;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryData;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;
import nl.saxion.robosim.model.protobuf.SslWrapper;

import java.io.IOException;
import java.util.*;

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
 * @author Joost van Dijk
 */
public class Model {
    //Singleton variables
    private static Model model;

    //Field variables
    private SSL_Field SSLField;
    private SSL_GeometryData geometryData;
    private Canvas canvas;

    //Frame variables
    private LinkedList<SSL_DetectionFrame> frames = new LinkedList<>();
    private ListIterator<SSL_DetectionFrame> frameIterator;
    private SSL_DetectionFrame lastFrame;

    //Referee variables
    private LinkedList<SSL_Referee> referees = new LinkedList<>();
    private ListIterator<SSL_Referee> refereeIterator;
    private SSL_Referee lastReferee;
    private MultiCastServer multicastServer;

    //Robot variables
    private int selectedRobot, selectedTeam;
    private Renderer renderer;
    private final List<AiRobot> aiRobots = new ArrayList<>();
    private boolean hasTeamYellow, hasTeamBlue;

    // Instance for ui controller
    private UIController controller;

    // Variables for the acceleration
    private double acceleration = 1;
    private double reversedAcceleration = 1;

    private Model() {
        try {
            multicastServer = new MultiCastServer();
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

    public void setFrames(LinkedList<SSL_DetectionFrame> frames) {
        assert frames != null : "frames null";

        this.frames = frames;
        this.frameIterator = frames.listIterator();
    }

    public void setUIController(UIController controller) {
        assert controller != null : "controller null";

        this.controller = controller;
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
            // When there isn't a next frame, change acceleration and set to pause
            if (frameIterator.hasNext()) {
                lastFrame = frameIterator.next();
            } else { // Set image to normal
                model.setAcceleration(1);
                controller.acceleration();
                controller.startClick();
            }
            // When there isn't a next referee frame change, acceleration and set to pause
            if (refereeIterator.hasNext()) {
                lastReferee = refereeIterator.next();
            } else { // Set image to normal
                model.setAcceleration(1);
                controller.acceleration();
            }
        } else {
            for (int i = 1; i < reversedAcceleration; i++) {
                if (frameIterator.hasPrevious()) {
                    frameIterator.previous();
                }
                if (refereeIterator.hasPrevious()) {
                    refereeIterator.previous();
                }
            }
            // When there isn't a previous frame change acceleration and set to pause
            if (frameIterator.hasPrevious()) {
                lastFrame = frameIterator.previous();
            } else { // Set image to normal
                model.setAcceleration(1);
                controller.acceleration();
                controller.startClick();
            }
            // When there isn't a previous frame change acceleration and set to pause
            if (refereeIterator.hasPrevious()) {
                lastReferee = refereeIterator.previous();
            } else { // Set image to normal
                model.setAcceleration(1);
                controller.acceleration();
            }
        }


        float x = lastFrame.getBalls(0).getX(), y = lastFrame.getBalls(0).getY();
        aiRobots.forEach(robot -> {
            robot.setOrientationToBall(x, y);
                robot.update();
        });
        if (!hasTeamYellow || !hasTeamBlue) addAiToSSLFrame();
        multicastServer.send(nextPacket(), lastReferee);
        if (getSelectedRobot() != null) {
            SSL_DetectionRobot robot = getSelectedRobot();
            controller.updateRobotUI(robot.getRobotId(), robot.getX(), -robot.getY(), robot.getOrientation());
        }

        double position = (double) frameIterator.nextIndex() / (double) frames.size() * 100;
        controller.updateUI(lastReferee != null ? lastReferee.getCommand().toString() : "", position, frameIterator.nextIndex());
    }

    public SSL_DetectionFrame getLastFrame() {
        return lastFrame;
    }

    /**
     * Set the simulation position to the specified position.
     *
     * @param frameNumber Position to move to
     */
    public void setCurrentFrame(int frameNumber) {
        assert frameNumber < frames.size() : "frameNumber is larger then amount of frames";
        frameIterator = frames.listIterator(frameNumber);
        refereeIterator = referees.listIterator(frameNumber);
    }

    public LinkedList<SSL_DetectionFrame> getFrames() {
        return frames;
    }


    //-------------Referee methods-------------

    /**
     * Sets the list of {@link SSL_Referee Referees}. The extracts all the commands that exist in the loaded log.
     *
     * @param referees The list with referees that will be set.
     */
    public void setReferees(LinkedList<SSL_Referee> referees) {
        assert referees != null : "referees null";

        this.referees = referees;
        this.refereeIterator = referees.listIterator();
        long frameNumber = 0;
        String lastCommand = "";
        Map<Long, String> refereeCommands = new TreeMap<>();
        TreeSet<String> commandSet = new TreeSet<>();

        for (SSL_Referee ref : referees) {
            if (ref != null) {
                String command = ref.getCommand().toString();
                if (!command.equals(lastCommand)) {
                    commandSet.add(command);
                    refereeCommands.put(frameNumber, command);
                    lastCommand = command;
                }
            }
            frameNumber++;
        }
        controller.setCommands(commandSet, refereeCommands);
    }

    public SSL_Referee getLastReferee() {
        return lastReferee;
    }

    //-------------Field methods-------------
    public SSL_Field getSSLField() {
        return SSLField;
    }

    public void setSSLField() {
        assert canvas != null : "Canvas null";

        this.SSLField = new SSL_Field(canvas, geometryData);
        if (aiRobots.isEmpty()) {
            for (int i = 0; i < 6; i++) { // TODO 10
                aiRobots.add(new AiRobot(i, (float) (SSLField.getBench_real_x()), (float) (SSLField.getBench_real_y() + SSLField.getRobot_real_size() * i)));
            }
        }
    }

    /**
     * Sets a new {link SSL_GeometryData} object. this triggers the recalculation of all the field's sizes as they
     * meight have been changed.
     *
     * @param geometry Da geometry data containing field mesurements
     */
    public void setGeometry(SSL_GeometryData geometry) {
        assert geometry != null : "Setting null SSLField";

        if (this.SSLField != null) {
            System.out.println("Updating SSLField");
            this.SSLField.update(geometry);
        } else {
            this.SSLField = new SSL_Field(canvas, geometry);
        }
        this.geometryData = geometry;
    }

    /**
     * Set the canvas
     */
    public void setCanvas(Canvas canvas) {
        assert canvas != null : "Canvas null";

        this.canvas = canvas;
    }

    //-------------Timing methods-------------

    /**
     * Get acceleration for the speed
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * Set acceleration for the speed
     */
    public void setAcceleration(double acceleration) {
        assert acceleration > 0 : "acceleration kleinder dan 0";

        this.acceleration = acceleration;
    }

    /**
     * Set reversed acceleration
     */
    public void setReversedAcceleration(double backwards) {
        assert backwards > 0 : "backwards kleiner dan 0";

        this.reversedAcceleration = backwards;
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

    /**
     * Get selected robot's id
     */
    public int getSelectedRobotId() {
        return selectedRobot;
    }

    /**
     * Get selected team id
     */
    public int getSelectedTeam() {
        return selectedTeam;
    }

    /**
     * Returns the currently selected robot.
     *
     * @return A {@link SSL_DetectionRobot}. Returns null if no robot is selected.
     */
    private SSL_DetectionRobot getSelectedRobot() {
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
     * @param x      The {@link javafx.scene.input.MouseEvent MouseEvent's} x
     * @param y      The {@link javafx.scene.input.MouseEvent MouseEvent's} y
     * @param render True if the {@link Renderer} needs to be re-rendered
     */
    public void selectRobotByPosition(double x, double y, boolean render) {
        if (SSLField == null) return;
        double scale = SSLField.getScale();
        x = x / scale - SSLField.getCenter_x();
        y = y / scale - SSLField.getCenter_y();

        if (lastFrame != null) {
            for (SSL_DetectionRobot robot : lastFrame.getRobotsBlueList()) {
                if (clickIsInRobot(x, -y, robot)) {
                    controller.updateRobotUI(robot.getRobotId(), robot.getX(), robot.getY(), robot.getOrientation());
                    selectedRobot = robot.getRobotId();
                    selectedTeam = 0;
                    if (render) renderer.render();
                    return;
                }
            }
            for (SSL_DetectionRobot robot : lastFrame.getRobotsYellowList()) {
                if (clickIsInRobot(x, -y, robot)) {
                    controller.updateRobotUI(robot.getRobotId(), robot.getX(), robot.getY(), robot.getOrientation());
                    selectedRobot = robot.getRobotId();
                    selectedTeam = 1;
                    if (render) renderer.render();
                    return;
                }
            }
        }
        /* Default: no Robot selected */
        selectedRobot = -1;
        controller.unselect();
        if (render) renderer.render();
    }

    /**
     * Sets the position of the given Robot.
     *
     * @param x      x-position
     * @param y      y-position
     * @param render True if the {@link Renderer} needs to be re-rendered
     */
    public void setRobotPosition(double x, double y, boolean render) {
        if (!aiRobots.isEmpty() && (selectedTeam != 0) == Settings.getInstance().hasTeamBlue() && selectedRobot >= 0) {
            AiRobot robot = aiRobots.get(selectedRobot);
            double scale = SSLField.getScale();
            robot.setX((float) (x / scale - SSLField.getCenter_x()));
            robot.setY((float) -(y / scale - SSLField.getCenter_y()));

            robot.setOrientationToBall(lastFrame.getBalls(0).getX(), lastFrame.getBalls(0).getY());
            if (!hasTeamYellow || !hasTeamBlue) addAiToSSLFrame();
            if (render) renderer.render();
        }
    }

    /**
     * Checks if the robot should be snapped in his bench and if so; it is snapped in the bench.
     *
     * @param x      x-position
     * @param y      y-position
     * @param render True if the {@link Renderer} needs to be re-rendered
     */
    public void setInBench(double x, double y, boolean render) {
        if (!aiRobots.isEmpty() && positionInBench(x, y) && (selectedTeam != 0) == Settings.getInstance().hasTeamBlue() && selectedRobot >= 0) {
            AiRobot robot = aiRobots.get(selectedRobot);
            robot.setX((float) SSLField.getBench_real_x());
            robot.setY((float) -SSLField.getBenchPosition(robot.getId()));
            if (!hasTeamYellow || !hasTeamBlue) addAiToSSLFrame();
            if (render) renderer.render();
            model.setTargetBench();
        }
    }

    /**
     * Rotates the currently selected {@link AiRobot}.
     *
     * @param deltaY The amount it should me rotated.
     * @param render True if the {@link Renderer} needs to be re-rendered
     */
    public void rotateSelectedRobot(double deltaY, boolean render) {
        if (!aiRobots.isEmpty() && (selectedTeam != 0) == Settings.getInstance().hasTeamBlue() && selectedRobot >= 0) {
            AiRobot robot = aiRobots.get(selectedRobot);
            robot.addDegrees(deltaY / 10);
            if (!hasTeamYellow || !hasTeamBlue) addAiToSSLFrame();
            if (render) renderer.render();
        }
    }

    /**
     * Checks if the given positin is in the bench.
     *
     * @return True if the position is in the bench
     */
    private boolean positionInBench(double x, double y) {
        double xDifference = x - SSLField.getBench_x();
        double yDifference = y - SSLField.getBench_y();
        return xDifference > -50 && xDifference < SSLField.getBench_width() && yDifference > -50 && yDifference < SSLField.getBench_height();
    }

    //-------------Other methods-------------

    /**
     * Resets the entire game data
     */
    public void clear() {
        System.out.println("MODEL - clear()");
        SSLField = null;
        frames.clear();
        referees.clear();
        lastFrame = null;
        lastReferee = null;
        selectedRobot = -1;
        selectedTeam = -1;
        acceleration = 1;
    }

    /**
     * Updates the model, should be called when a new log is loaded.
     */
    public void update() {
        System.out.println("MODEL - update()");
        Settings s = Settings.getInstance();
        hasTeamYellow = s.hasTeamYellow();
        hasTeamBlue = s.hasTeamBlue();
        setSSLField();
        aiRobots.forEach(robot -> {
                    robot.setX((float) (SSLField.getBench_real_x()));
                    robot.setY((float) -(SSLField.getBench_real_y() + SSLField.getRobot_real_size() * robot.getId()));
                }
        );
        nextFrame();
        renderer.render();
        controller.updateUI("", 0, 0);
    }

    //-----------------AI Methods------------------------

    /**
     * Set target bench
     */
    private void setTargetBench() {
        for (AiRobot robot : aiRobots) {
            robot.addTarget(SSLField.getBench_real_x(), SSLField.getBenchPosition(robot.getId()));
        }
    }

    /**
     * Get the ai robots
     */
    public List<AiRobot> getAiRobots() {
        return aiRobots;
    }

    /**
     * Parse an {@link AiRobot} to an {@link SSL_DetectionRobot SSL_DetectionRobot} so it is useable for the
     * {@link Renderer Renderer} and the AI.
     *
     * @param aiRobot The {@link AiRobot} to be parsed.
     * @return An {@link SSL_DetectionRobot SSL_DetectionRobot} from the AiRobot
     */
    private SSL_DetectionRobot parseAiRobot(AiRobot aiRobot) {
        assert aiRobot != null : "aiRobot null";

        return SSL_DetectionRobot.newBuilder()
                .setOrientation((float) aiRobot.getOrientation())
                .setConfidence(0.9f)
                .setRobotId(aiRobot.getId())
                .setPixelX((float) aiRobot.getCurrentX())
                .setPixelY((float) aiRobot.getCurrentY())
                .setX((float) aiRobot.getCurrentX())
                .setY((float) aiRobot.getCurrentY())
                .setHeight(145.0f)
                .build();
    }

    /**
     * Adds all the {@link AiRobot AiRobots} to the {@link SSL_DetectionFrame SSL_DetectionFrame}. Uses the
     * {@link #parseAiRobot(AiRobot)} to add all the {@link AiRobot AiRobots} to a Frame.
     */
    private void addAiToSSLFrame() {
        SSL_DetectionFrame.Builder builder = lastFrame.toBuilder();
        if (Settings.getInstance().hasTeamBlue()) {
            builder.clearRobotsYellow();
            for (AiRobot robot : aiRobots) {
                builder.addRobotsYellow(parseAiRobot(robot));
            }
        } else {
            builder.clearRobotsBlue();
            for (AiRobot robot : aiRobots) {
                builder.addRobotsBlue(parseAiRobot(robot));
            }
        }

        lastFrame = builder.build();
    }

    /**
     * Go to next packet
     */
    private SslWrapper.SSL_WrapperPacket nextPacket() {
        return SslWrapper.SSL_WrapperPacket.newBuilder()
                .setGeometry(geometryData)
                .setDetection(lastFrame)
                .build();
    }

    /**
     * Set renderer
     */
    public void setRenderer(Renderer renderer) {
        assert renderer != null : "renderer null";

        this.renderer = renderer;
    }

    public long size() {
        return frames.size();
    }

    /**
     * Destroys the singleton instance of this class.
     */
    public static void destroy() {
        model = null;
    }

    public void setMulticastIps() {
        Settings s = Settings.getInstance();
        try {
            multicastServer.set(s.getOip(), Integer.parseInt(s.getOport()), s.getRefIp(), Integer.parseInt(s.getRefPort()));
        } catch (IOException e) {
            //TODO idk
            e.printStackTrace();
        }
    }

    public void moveAiToBench() {
        aiRobots.forEach(robot -> {
                    robot.setX((float) (SSLField.getBench_real_x()));
                    robot.setY((float) (SSLField.getBench_real_y() + SSLField.getRobot_real_size() * robot.getId()));
                }
        );
        this.nextFrame();
        renderer.render();
    }
}
