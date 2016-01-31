package org.ssh.models;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.ssh.ui.components.centersection.gamescene.shapes.ArcLine3D;
import org.ssh.ui.components.centersection.gamescene.shapes.SimpleLine3D;
import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryFieldSize;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Describes a fieldSize with {@link Goal goals} and a {@link GeometryFieldSize fieldSize size}.
 *
 * @author Jeroen de Jong
 */
public class Field extends FieldObject {

    /**
     * The file path for the grass texture.
     */
    private static final String GRASS_TEXTURE_FILE = "/org/ssh/view/textures/field/grass.png";

    /**
     * Field object from protobuf packet
     */
    private GeometryFieldSize fieldSize;
    private static final double TILE_WIDTH = 500d;

    /**
     * Instantiates a fieldSize.
     */
    public Field() {
        super("field", "");


    }

    @Override
    public void initialize() {
        //no default values
    }

    /**
     * @return Width of the boundary around the fieldSize.
     */
    public int getBoundaryWidth() {
        return this.fieldSize.getBoundaryWidth();
    }

    /**
     * Get a specific {@link FieldCicularArc arc segment}.
     *
     * @param index index of the {@link FieldCicularArc arc segment}
     * @return a specific {@link FieldCicularArc arc segment}.
     */
    public FieldCicularArc getFieldArc(final int index) {
        return this.fieldSize.getFieldArcs(index);
    }

    /**
     * @return all {@link FieldCicularArc arc segments} on the fieldSize.
     */
    public List<FieldCicularArc> getFieldArcs() {
        return this.fieldSize.getFieldArcsList();
    }

    /**
     * @return Length of the fieldSize.
     */
    public int getFieldLength() {
        return this.fieldSize.getFieldLength();
    }

    /**
     * Get a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     *
     * @param index index of the {@link protobuf.Geometry.FieldLineSegment line segment}
     * @return a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     */
    public FieldLineSegment getFieldLine(final int index) {
        return this.fieldSize.getFieldLines(index);
    }

    /**
     * @return all {@link protobuf.Geometry.FieldLineSegment line segments} on the fieldSize.
     */
    public List<FieldLineSegment> getFieldLines() {
        return this.fieldSize.getFieldLinesList() == null ? new ArrayList<>(): this.fieldSize.getFieldLinesList();
    }

    /**
     * @return Width of the fieldSize.
     */
    public int getFieldWidth() {
        return this.fieldSize.getFieldWidth();
    }

    /**
     * @return Protobuf object containing latest information about fieldSize/goals.
     */
    public GeometryFieldSize getFieldSize() {
        return fieldSize;
    }


    /**
     * Generate tiles method. This method generates the field tiles.
     *
     * @author Mark Leferink
     */
    private Group generateTiles(Group fieldGroup) {
        PhongMaterial grassMaterial = loadTexture();

        fieldGroup.getChildren().add(new AmbientLight(Color.WHITE));

        for (int i = -1; i < (this.getFieldLength() / this.TILE_WIDTH) + 1; i++)
            for (int j = -1; j < (this.getFieldWidth() / this.TILE_WIDTH) + 1; j++) {

                // Create new box
                final Box tmpBox = new Box(this.TILE_WIDTH, 1, this.TILE_WIDTH);

                // Translate tile into position
                tmpBox.setTranslateX(-(this.getFieldLength() / 2.0)
                        + ((i * this.TILE_WIDTH) + (this.TILE_WIDTH / 2.0)));
                tmpBox.setTranslateZ(-(this.getFieldWidth() / 2.0)
                        + ((j * this.TILE_WIDTH) + (this.TILE_WIDTH / 2.0)));

                // Set box material
                tmpBox.setMaterial(grassMaterial);

                // Add box to field
                fieldGroup.getChildren().add(tmpBox);
            }
        return fieldGroup;
    }

    /**
     * Generate lines method. This method generates the lines on the field.
     */
    private Group generateLines(Group field) {
        Group lines = new Group();
        // Getting line segments
        getFieldArcs().forEach(arcSegment ->
            lines.getChildren().add(new ArcLine3D(arcSegment.getA1(), arcSegment.getA2(), arcSegment.getRadius()*2, arcSegment.getCenter().getX(), arcSegment.getCenter().getY(), arcSegment.getThickness())));
        getFieldLines().forEach(lineSegment ->
                lines.getChildren().add(new SimpleLine3D(lineSegment.getP1().getX(), lineSegment.getP1().getY(),
                        lineSegment.getP2().getX(), lineSegment.getP2().getY(),
                        lineSegment.getThickness())));

        lines.setTranslateY(1);
        field.getChildren().add(lines);
        return field;
    }

    private PhongMaterial loadTexture(){
        // Getting resource
        InputStream textureInputStream = this.getClass().getResourceAsStream(GRASS_TEXTURE_FILE);
        PhongMaterial grassMaterial = new PhongMaterial(Color.LAWNGREEN);

        // Check if the texture file exists
        if (textureInputStream != null) {
            // Setting diffuse map
            grassMaterial.setDiffuseMap(new Image(textureInputStream));
        } else {
            // Log error
            Field.LOG.info("Could not load " + GRASS_TEXTURE_FILE);
        }

        return grassMaterial;
    }

    @Override
    public boolean update(final Map<String, ?> changes) {
        boolean returnvalue = super.update(changes);
        recreateMeshView();
        return returnvalue;
    }

    @Override
    public Group createMeshView() {
        return generateLines(generateTiles(new Group()));
    }
}