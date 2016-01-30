package org.ssh.ui.components.centersection.gamescene.shapes;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * @author Jeroen
 * @date 30-1-2016
 */
public class SimpleLine3D extends MeshView {


    /**
     * The constant for the faces.
     */
    private static final int[] FACES = {0, 0, 1, 1, 2, 2, 1, 1, 3, 3, 2, 2};

    /**
     * The constant for the texture coordinates.
     */
    private static final float[] TEXTURE_COORDINATES = {1, 1, 1, 0, 0, 1, 0, 0};

    /**
     * Constructor. This instantiates a new FlatLine3D object.
     *
     * @param thickness  The thickness of the line.
     */
    public SimpleLine3D(float x1, float y1, float x2, float y2, final double thickness) {
        this.setMesh(createLine(x1, y1, x2, y2, thickness));
    }


    /**
     * Build mesh method. This method is responsible for building the triangle mesh of the line.
     *
     * @param startX    Start of the line, x coordinate.
     * @param startZ    Start of the line, z coordinate.
     * @param endX      End of the line, x coordinate.
     * @param endZ      End of the line, z coordinate.
     * @param thickness Thickness of the line.
     */
    private TriangleMesh createLine(final double startX,
                           final double startZ,
                           final double endX,
                           final double endZ,
                           final double thickness) {

        // Calculate direction
        final double directionX = endX - startX;
        final double directionZ = endZ - startZ;

        // Calculate rotation
        final double rotation = (Math.atan2(directionZ, directionX) * 180f) / Math.PI;
        // Calculate alpha, and cos & sin values
        final double alpha = (rotation / 180f) * Math.PI;
        final double calcCos = Math.cos(alpha) * (thickness / 2.0);
        final double calcSin = Math.sin(alpha) * (thickness / 2.0);

        // Calculate points
        final float x1 = (float) (startX - calcSin);
        final float z1 = (float) (startZ + calcCos);
        final float x2 = (float) (endX - calcSin);
        final float z2 = (float) (endZ + calcCos);
        final float x3 = (float) (startX + calcSin);
        final float z3 = (float) (startZ - calcCos);
        final float x4 = (float) (endX + calcSin);
        final float z4 = (float) (endZ - calcCos);

        // Create vertices array
        final float[] vertices = {x1, 0.0f, z1, x2, 0.0f, z2, x3, 0.0f, z3, x4, 0.0f, z4};

        // Add vertices
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(vertices);
        // Add texture coordinates
        mesh.getTexCoords().addAll(SimpleLine3D.TEXTURE_COORDINATES);
        // Add faces
        mesh.getFaces().addAll(SimpleLine3D.FACES);
        return mesh;
    }
}