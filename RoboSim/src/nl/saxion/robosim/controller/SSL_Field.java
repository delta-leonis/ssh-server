package nl.saxion.robosim.controller;

import javafx.scene.canvas.Canvas;
import nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryData;

import static nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryFieldSize;

/**
 * Contains the data about the field sizes and calculates the scaled field size to be used by the {@link nl.saxion.robosim.controller.Renderer}.
 * All the adjusted and scaled sizes are doubles with the <b>virtual</b> prefix.
 * <i>e.g. line_width</i> is the size if the line in pixels.
 * <p>
 * Created by Fieldhof on 20-5-2015.
 *
 * @author Minkjan
 * @author Fieldhof
 */
public class SSL_Field{

    /* Pre-computed values */
    protected final double canvasX, canvasY;
    protected final double scale, line_width, boundary_width, field_length, field_width,
            scaled_meter, defence_width, circle_x, circle_y, line_x,
            circle_radius, center_x, center_y, goal_width, goal_depth,
            defence_radius, robot_size, selection_radius;

    protected final double bench_x, bench_y, bench_width, bench_height, bench_real_x, bench_real_y, robot_real_size;

    /**
     * Constructor for SSL_Field
     * Creates a SSL_Field object with the given SSL_GeometryFieldSize object
     * Update the precalcuated sizes so the interface is scaled correctly. .
     *
     * @param canvas
     * @param geometryData
     */
    public SSL_Field(Canvas canvas, SSL_GeometryData geometryData) {

        canvasY = canvas.getHeight();
        canvasX = canvas.getWidth();

        /* Compute field values */
        SSL_GeometryFieldSize field = geometryData.getField();
        scale = canvasY / (field.getFieldWidth() + field.getBoundaryWidth() * 2);
        line_width = field.getLineWidth() * scale;
        boundary_width = field.getBoundaryWidth() * scale;
        field_width = field.getFieldWidth() * scale;
        field_length = field.getFieldLength() * scale;
        circle_radius = field.getCenterCircleRadius() * scale;
        goal_width = field.getGoalWidth() * scale;
        goal_depth = field.getGoalDepth() * scale;
        defence_radius = field.getDefenseRadius() * scale;
        scaled_meter = 1000 * scale;
        defence_width = boundary_width + field_width / 2 - scaled_meter;
        circle_x = boundary_width + field_length / 2 - circle_radius / 2;
        circle_y = boundary_width + field_width / 2 - circle_radius / 2;
        line_x = boundary_width + field_length / 2 - line_width / 2;
        center_x = field.getFieldLength() / 2 + field.getBoundaryWidth();
        center_y = field.getFieldWidth() / 2 + field.getBoundaryWidth();

        robot_real_size = 180;
        robot_size = robot_real_size * scale;
        selection_radius = 200 * scale;

        bench_x = field_length + 2.1 * boundary_width;
        bench_y = boundary_width + field_width / 1.5;
        bench_width = robot_real_size * 1.4f * scale;
        bench_height = (7 * robot_real_size) * scale;

        bench_real_x = (bench_x) / scale - center_x;
        bench_real_y = (bench_y + robot_size) / scale - center_y;
    }

    public double getRobot_real_size() {
        return robot_real_size;
    }

    public double getBench_real_y() {
        return bench_real_y;
    }

    public double getBench_real_x() {
        return bench_real_x;
    }

    public double getScale() {
        return scale;
    }

    public double getCenter_x() {
        return center_x;
    }

    public double getCenter_y() {
        return center_y;
    }
}
