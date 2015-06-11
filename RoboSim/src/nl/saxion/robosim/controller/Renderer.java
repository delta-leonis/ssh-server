package nl.saxion.robosim.controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionBall;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;

import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for rendering the canvas and thus the game itself. Every second, the {@link #render()}
 * method is called and will get a new frame from the model. This frame will then be rendered.
 * <p>
 * Created by Kris on 20-5-2015.
 *
 * @author Kris Minkjan
 * @author Ken Sleebos
 */
@SuppressWarnings("SuspiciousNameCombination")
public class Renderer {
    private final GraphicsContext graphicsContext;
    private final Model model;
    private final Image image_yellow, image_blue, image_bench;
    private final Font font;
    private final SSL_Field field;

    /**
     * Creates a Renderer, used for rendering the canvas. This constructor retrieves the {@link GraphicsContext} from the
     * {@link Canvas} parameter and sets it's properties.
     *
     * @param canvas The canvas that will be rendered on.
     */
    public Renderer(Canvas canvas) {
        assert canvas != null : "Canvas is null";
        model = Model.getInstance();
        field = model.getSSLField();

         /* Set drawing properties */
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.DARKGREEN);
        graphicsContext.setLineWidth(field.line_width);
        graphicsContext.setStroke(Color.WHITE);

        /* Load assets */
        font = new Font("SanSerif", 20);
        image_blue = new Image("/resources/robot_blue.png");
        image_yellow = new Image("/resources/robot_yellow.png");
        image_bench = new Image("/resources/bench.png");
    }

    /**
     * Renders the canvas and all the {@link SSL_DetectionRobot Robots} that are in a {@link SSL_DetectionFrame}. A {@link SSL_DetectionFrame} is retrieved
     * from the {@link Model#getLastFrame()} method. Then all the contents of it are rendered.
     */
    public void render() {
        drawField();
        /* Get a frame to draw */
        SSL_DetectionFrame frame = model.getLastFrame();

        /* Draw the content of the frame*/
        graphicsContext.setFill(Color.WHITE);

        for (SSL_DetectionRobot robot : frame.getRobotsBlueList()) {
            renderSSLRobot(robot, image_blue, 0);
        }

        for (SSL_DetectionRobot robot : frame.getRobotsYellowList()) {
            renderSSLRobot(robot, image_yellow, 1);
        }

        graphicsContext.setFill(Color.ORANGE);
        for (SSL_DetectionBall ball : frame.getBallsList()) {
            graphicsContext.fillOval((ball.getX() + field.center_x - 5) * field.scale,
                    (ball.getY() + field.center_y - 5) * field.scale, 10, 10);
        }

        /* Draw in-canvas ui */
        drawScore(model.getLastReferee());
        drawBench();
    }

    /**
     * Renders a single robot. If the robot is selected, a white circle is draw behind the robot, indicating it is
     * "selected".
     *
     * @param robot  The robot to be rendered
     * @param image  The image that will be rendered
     * @param teamId The teamid of the team the robot is on
     */
    private void renderSSLRobot(SSL_DetectionRobot robot, Image image, int teamId) {
        graphicsContext.save();
        graphicsContext.translate((robot.getX() + field.center_x) * field.scale, (robot.getY() + field.center_y) * field.scale);
        graphicsContext.rotate(Math.toDegrees(robot.getOrientation()) - 90);

        /* Check if this Robot is selected */
        if (model.getSelectedTeam() == teamId && model.getSelectedRobotId() == robot.getRobotId()) {
            graphicsContext.setFill(Color.WHITE);;
            graphicsContext.fillOval(-field.selection_radius / 2, -field.selection_radius / 2, field.selection_radius,
                    field.selection_radius);
        }

        graphicsContext.drawImage(image, -field.robot_size / 2, -field.robot_size / 2, field.robot_size, field.robot_size);
        graphicsContext.restore();
        graphicsContext.fillText(robot.getRobotId() + "", (robot.getX() + field.center_x + 180 + 2) * field.scale,
                (robot.getY() + field.center_y + 180 / 2) * field.scale);
    }

    /**
     * Clears the screen and draws an empty field on the right scale. This method gets the measurements from an
     * {@link SSL_Field}.
     */
    public void drawField() {
        graphicsContext.clearRect(0, 0, field.canvasX, field.canvasY);

        /* Draw playing field */
        graphicsContext.setFill(Color.DARKGREEN);
        graphicsContext.fillRect(0, 0, field.canvasX, field.canvasY);

        /* Draw the rows on the field */
        int rowCount = 9;
        double sizer = field.field_length / rowCount;
        for (int i = 0; i < rowCount; i++) {
            graphicsContext.fillRect(i * sizer + field.boundary_width, field.boundary_width, sizer, field.field_width);
            if (i % 2 == 0) {
                graphicsContext.setFill(Color.GREEN);
            } else {
                graphicsContext.setFill(Color.DARKGREEN);
            }
        }

        graphicsContext.strokeRect(field.boundary_width, field.boundary_width, field.field_length, field.field_width);

        /* Draw the middle lines */
        graphicsContext.strokeLine(field.line_x, field.boundary_width, field.line_x, field.boundary_width + field.field_width / 2 - field.circle_radius / 2);
        graphicsContext.strokeLine(field.line_x, field.boundary_width + field.field_width / 2 + field.circle_radius / 2, field.line_x, field.canvasY - field.boundary_width);

        graphicsContext.strokeOval(field.circle_x, field.circle_y, field.circle_radius, field.circle_radius);

        /* Draw the defence radius */
        graphicsContext.strokeRoundRect(field.boundary_width + field.field_length - field.defence_radius, field.defence_width, field.defence_radius * 2, 2 * field.scaled_meter, 200, 200);
        graphicsContext.strokeRoundRect(field.boundary_width - field.defence_radius, field.defence_width, field.defence_radius * 2, 2 * field.scaled_meter, 200, 200);

        /* Hide the overflow */
        graphicsContext.setFill(Color.DARKGREEN);
        graphicsContext.fillRect(field.boundary_width + field.field_length + field.line_width / 2, field.defence_width - 2, field.scaled_meter, 2 * field.scaled_meter + 4);
        graphicsContext.fillRect(0, field.defence_width - 2, field.boundary_width - field.line_width / 2, 2 * field.scaled_meter + 4);

        /* Draw the goals */
        graphicsContext.strokeRect(field.boundary_width - field.goal_depth, field.field_width / 2 + field.boundary_width - field.goal_width / 2, field.goal_depth, field.goal_width);
        graphicsContext.strokeRect(field.boundary_width + field.field_length, field.field_width / 2 + field.boundary_width - field.goal_width / 2, field.goal_depth, field.goal_width);
    }

    /**
     * Draws the scoreboard on the canvas. Data is retrieved form a {@link SSL_Referee}.
     *
     * @param referee The referee object that contains match information
     */
    private void drawScore(SSL_Referee referee) {
        if(referee == null) {
            return;
        }
        Stop[] stops = new Stop[]{new Stop(0, Color.web("3f3ff6")), new Stop(1, Color.web("060686"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

        graphicsContext.setFill(lg1);
        graphicsContext.fillRect(20, 20, 280, 50);
        graphicsContext.setFill(Color.web("000000", 0.7));
        graphicsContext.fillRect(300, 23, 100, 47);
        graphicsContext.setFill(Color.DARKGRAY);
        graphicsContext.fillRect(20, 65, 380, 5);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(298, 30, 4, 30);
        graphicsContext.setFont(font);

        graphicsContext.fillText(referee.getBlue().getName(), 35, 53);
        graphicsContext.fillText(referee.getBlue().getScore() + ":" + referee.getYellow().getScore(), 140, 53);
        graphicsContext.fillText(referee.getYellow().getName(), 175, 53);

        /* Draw the time that is left */
        long microsecondsLeft = referee.getStageTimeLeft();
        long minutes = TimeUnit.MINUTES.convert(microsecondsLeft, TimeUnit.MICROSECONDS);
        long seconds = TimeUnit.SECONDS.convert(microsecondsLeft, TimeUnit.MICROSECONDS) % 60;
        graphicsContext.fillText(String.format("%02d:%02d", minutes, seconds), 330, 53);
    }

    private void drawChairs() {

    }

    private void drawBench(){
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.drawImage(image_bench,field.bench_x,field.bench_y,field.bench_width,field.bench_height);
    }

}
