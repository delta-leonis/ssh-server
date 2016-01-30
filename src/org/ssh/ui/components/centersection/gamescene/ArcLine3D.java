package org.ssh.ui.components.centersection.gamescene;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * @author Jeroen
 * @date 29-1-2016
 */
public class ArcLine3D extends MeshView {

    private static final int DIVISIONS = 100;

    private static final float[] TEXTURE_COORDINATES = {1, 1, 1, 0, 0, 1, 0, 0};

    public ArcLine3D(final float startAngleRad,
                     final float endAngleRad,
                     final float diameter,
                     final float x,
                     final float y,
                     final float thickness) {
        TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        // Calculate vertices
        final float[] vertices = ArcLine3D.calculateVertices(startAngleRad, endAngleRad, diameter, thickness);
        // Add vertices to the point list of the triangle mesh
        triangleMesh.getPoints().addAll(vertices);
        // Add texture coordinates to the triangle mesh
        triangleMesh.getTexCoords().addAll(ArcLine3D.TEXTURE_COORDINATES);

        // Calculate the faces of the mesh
        final int[] faces = ArcLine3D.calculateFaces();
        // Add faces to the mesh
        triangleMesh.getFaces().addAll(faces);

        this.setMesh(triangleMesh);

        this.setTranslateZ(y);
        this.setTranslateX(x);
    }

    /**
     * Calculate faces method. This method calculates the faces of the FlatArc3D.
     *
     * @return Returns an array of faces.
     */
    private static int[] calculateFaces() {

        final int[] indices = new int[(DIVISIONS * 2) * 6];
        int triangleCounter = 0;

        // Loop through faces, 2 faces(triangles) per division
        for (int i = 0; i < (DIVISIONS * 2); i++) {

            // Map faces counter-clockwise so it faces towards us
            if ((i % 2) == 0) {

                indices[i * 6] = i + 2;
                indices[(i * 6) + 2] = i + 1;
                indices[(i * 6) + 4] = i;

            } else {

                indices[i * 6] = i;
                indices[(i * 6) + 2] = i + 1;
                indices[(i * 6) + 4] = i + 2;
            }

            // Map texture coordinates
            if (triangleCounter == 0) {

                indices[(i * 6) + 1] = 2;
                indices[(i * 6) + 3] = 0;
                indices[(i * 6) + 5] = 3;

            } else if (triangleCounter == 1) {

                indices[(i * 6) + 1] = 0;
                indices[(i * 6) + 3] = 3;
                indices[(i * 6) + 5] = 1;

            } else if (triangleCounter == 2) {

                indices[(i * 6) + 1] = 3;
                indices[(i * 6) + 3] = 1;
                indices[(i * 6) + 5] = 2;

            } else if (triangleCounter == 3) {

                indices[(i * 6) + 1] = 1;
                indices[(i * 6) + 3] = 2;
                indices[(i * 6) + 5] = 0;

                triangleCounter = 0;
                continue;
            }

            triangleCounter++;
        }

        return indices;
    }
    /**
     * Calculate vertices method. This method calculates the vertices of the FlatArc3D.
     *
     * @param startRad     The starting angle of the arc in radians.
     * @param endRad       The ending angle of the arc in radians.
     * @param diameter     The diameter of the arc.
     * @param thickness    The thickness of the arc.
     * @return Returns an array with the vertices.
     */
    private static float[] calculateVertices(final float startRad,
                                             final float endRad,
                                             final float diameter,
                                             final float thickness) {

        // Calculating total angle of the arc
        final float totalAngleRad = endRad - startRad;
        // Calculating angle per step
        final float anglePerStepRad = totalAngleRad / DIVISIONS;
        // Setting current angle
        double curAngleRad = startRad;

        // Calculating half of the thickness
        final float halfOfThickness = thickness / 2.0f;
        // Calculating radius
        final float radius = diameter / 2.0f;


        // Calculating amount of vertices to generate
        final int amountOfVertices = DIVISIONS * 2 +2;
        // Creating array for the amount of vertices
        final float[] vertices = new float[amountOfVertices * 3];

        // Calculate radius of the inner circle
        final float radiusSubtracted = radius - halfOfThickness;
        // Calculate radius of the outer circle
        final float radiusAdded = radius + halfOfThickness;

        // Loop through edges
        for (int i = 0; i < DIVISIONS+1; i++) {

            // Calculate sinus & cosinus for the current angle in radians
            final float cosCalc = (float) Math.cos(curAngleRad);
            final float sinCalc = (float) Math.sin(curAngleRad);

            // Calculating first point
            final float b1x = cosCalc * radiusSubtracted;
            final float b1z = sinCalc * radiusSubtracted;
            // Calculating second point
            final float b11x = cosCalc * radiusAdded;
            final float b11z = sinCalc * radiusAdded;

            // Adding B' to vertices
            vertices[i * 6] = b1x;
            vertices[i * 6 + 1] = 0.0f;
            vertices[i * 6 + 2] = b1z;

            // Adding B'' to vertices
            vertices[i * 6 + 3] = b11x;
            vertices[i * 6 + 4] = 0.0f;
            vertices[i * 6 + 5] = b11z;

            // Increase angle in RAD
            curAngleRad += anglePerStepRad;
        }

        // Return the vertices
        return vertices;
    }
}
