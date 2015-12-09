package org.ssh.field3d.core.shapes;

import javafx.geometry.Point3D;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.VertexFormat;
import javafx.scene.shape.TriangleMesh;

/**
 * FlatArc3D class. This class creates a triangle mesh for a flat arc in 3d space.
 * 
 * @author marklef2
 */
public class FlatArc3D {
    
    /** The texture coordinates. */
    private static final float[] TEXTURE_COORDS    = { 1, 1, 1, 0, 0, 1, 0, 0 };
                                                   
    /** The values per coordinate. */
    private static final int     VALUES_PER_COORD  = 3;
                                                   
    /** The vertices per edge. */
    private static final int     VERTICES_PER_EDGE = 2;
                                                   
    /** The triangle mesh. */
    private final TriangleMesh   triangleMesh;
                                 
    /** The mesh view. */
    private final MeshView       meshView;
                                 
    /**
     * Instantiates a new flat arc 3d.
     *
     * @param startAngle
     *            The starting angle of the arc.
     * @param endAngle
     *            The ending angle of the arc.
     * @param diameter
     *            The diameter of the arc.
     * @param thickness
     *            The thickness of the arc.
     * @param numDivisions
     *            The number of divisions of the arc.
     */
    public FlatArc3D(final float startAngle,
            final float endAngle,
            final float diameter,
            final float thickness,
            final int numDivisions) {
            
        // Creating new triangle mesh
        this.triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        
        // Calculate vertices
        final float[] vertices = this.calculateVertices(startAngle, endAngle, diameter, thickness, numDivisions);
        // Add vertices to the point list of the triangle mesh
        this.triangleMesh.getPoints().addAll(vertices);
        // Add texture coordinates to the triangle mesh
        this.triangleMesh.getTexCoords().addAll(TEXTURE_COORDS);
        
        // Calculate the faces of the mesh
        final int faces[] = this.calculateFaces(numDivisions);
        // Add faces to the mesh
        this.triangleMesh.getFaces().addAll(faces);
        
        // Setting mesh view
        this.meshView = new MeshView(this.triangleMesh);
    }
    
    /**
     * Calculate faces.
     *
     * @param numDivisions
     *            The number of divisions of the arc.
     * @return Returns an array of faces.
     */
    private int[] calculateFaces(final int numDivisions) {
        
        final int indicies[] = new int[(numDivisions * 2) * 6];
        int triangleCounter = 0;
        
        // Loop through faces, 2 faces(triangles) per division
        for (int i = 0; i < (numDivisions * 2); i++) {
            
            // Map faces counter-clockwise so it faces towards us
            if ((i % 2) == 0) {
                
                indicies[i * 6] = i + 2;
                indicies[(i * 6) + 2] = i + 1;
                indicies[(i * 6) + 4] = i;
                
            }
            else {
                
                indicies[i * 6] = i;
                indicies[(i * 6) + 2] = i + 1;
                indicies[(i * 6) + 4] = i + 2;
            }
            
            // Map texture coords
            if (triangleCounter == 0) {
                
                indicies[(i * 6) + 1] = 2;
                indicies[(i * 6) + 3] = 0;
                indicies[(i * 6) + 5] = 3;
                
            }
            else if (triangleCounter == 1) {
                
                indicies[(i * 6) + 1] = 0;
                indicies[(i * 6) + 3] = 3;
                indicies[(i * 6) + 5] = 1;
                
            }
            else if (triangleCounter == 2) {
                
                indicies[(i * 6) + 1] = 3;
                indicies[(i * 6) + 3] = 1;
                indicies[(i * 6) + 5] = 2;
                
            }
            else if (triangleCounter == 3) {
                
                indicies[(i * 6) + 1] = 1;
                indicies[(i * 6) + 3] = 2;
                indicies[(i * 6) + 5] = 0;
                
                triangleCounter = 0;
                continue;
            }
            
            triangleCounter++;
        }
        
        return indicies;
    }
    
    /**
     * Calculate vertices.
     *
     * @param startRad
     *            The starting angle of the arc in radians.
     * @param endRad
     *            The ending angle of the arc in radians.
     * @param diameter
     *            The diameter of the arc.
     * @param thickness
     *            The thickness of the arc.
     * @param numDivisions
     *            The number of divisions in the arc.
     * @return Returns an array with the vertices.
     */
    private float[] calculateVertices(final float startRad,
            final float endRad,
            final float diameter,
            final float thickness,
            int numDivisions) {
            
        // Calculating total angle of the arc
        final float totalAngleRad = endRad - startRad;
        // Calculating angle per step
        final float anglePerStepRad = totalAngleRad / numDivisions;
        // Setting current angle
        double curAngleRad = startRad;
        
        // Increase number of divisions to add the last edge.
        numDivisions++;
        
        // Calculating half of the thickness
        final float halfOfThickness = thickness / 2.0f;
        // Calculating radius
        final float radius = diameter / 2.0f;
       
        
        // Calculating amount of vertices to generate
        final int amountOfVertices = numDivisions * VERTICES_PER_EDGE;
        // Creating array for the amount of vertices
        final float vertices[] = new float[amountOfVertices * VALUES_PER_COORD];

        final float radiusSubtracted = radius - halfOfThickness;
        final float radiusAdded = radius + halfOfThickness;
        
        // Loop through edges
        for (int i = 0; i < numDivisions; i++) {

            // Calculate sinus & cosinus for the current angle in radians
            final float cosCalc = (float)Math.cos(curAngleRad);
            final float sinCalc = (float)Math.sin(curAngleRad);
            
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
    
    /**
     * Gets the {@link MeshView} of the arc.
     *
     * @return The {@link MeshView} of the arc.
     */
    public MeshView getMeshView() {
        return this.meshView;
    }
    
    /**
     * Gets the {@link TriangleMesh} of the arc.
     *
     * @return The {@link TriangleMesh} of the arc.
     */
    public TriangleMesh geTriangleMesh() {
        return this.triangleMesh;
    }
}
