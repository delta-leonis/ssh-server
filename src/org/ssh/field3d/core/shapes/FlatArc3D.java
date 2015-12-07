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
    public FlatArc3D(final double startAngle,
            final double endAngle,
            final double diameter,
            final double thickness,
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
    private float[] calculateVertices(final double startRad,
            final double endRad,
            final double diameter,
            final double thickness,
            int numDivisions) {
            
        // Calculating total angle of the arc
        final double totalAngleRad = endRad - startRad;
        // Calculating angle per step
        final double anglePerStepRad = totalAngleRad / numDivisions;
        // Setting current angle
        double curAngleRad = startRad;
        
        // Increase number of divisions to add the last edge.
        numDivisions++;
        
        // Calculating half of the thickness
        final double halfOfThickness = thickness / 2.0;
        // Calculating radius
        final double radius = diameter / 2.0;
       
        
        // Calculating amount of vertices to generate
        final int amountOfVertices = numDivisions * VERTICES_PER_EDGE;
        // Creating array for the amount of vertices
        final float vertices[] = new float[amountOfVertices * VALUES_PER_COORD];
        
        // Loop through edges
        for (int i = 0; i < numDivisions; i++) {
            
            // Calculating first point
            final double b1x = Math.cos(curAngleRad) * (radius - halfOfThickness);
            final double b1z = Math.sin(curAngleRad) * (radius - halfOfThickness);
            // Calculating second point
            final double b11x = Math.cos(curAngleRad) * (radius + halfOfThickness);
            final double b11z = Math.sin(curAngleRad) * (radius + halfOfThickness);
            
            // Creating vectors for the points (B' & B'')
            final Point3D pointB1 = new Point3D(b1x, 0.0f, b1z);
            final Point3D pointB11 = new Point3D(b11x, 0.0f, b11z);
            
            // Adding B' to vertices 
            vertices[i * 6] = (float)pointB1.getX();
            vertices[i * 6 + 1] = (float)pointB1.getY();
            vertices[i * 6 + 2] = (float)pointB1.getZ();
            
            // Adding B'' to vertices
            vertices[i * 6 + 3] = (float)pointB11.getX();
            vertices[i * 6 + 4] = (float)pointB11.getY();
            vertices[i * 6 + 5] = (float)pointB11.getZ();
            
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
