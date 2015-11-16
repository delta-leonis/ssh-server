package org.ssh.field3d.core.shapes;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import protobuf.Geometry.Vector2f;

/**
 * FlatLine3D class
 *
 * This class represents a flat(2d plane) line in 3d space, it gets drawn on the x, z axis.
 *
 * @author Mark Lefering
 */
public class FlatLine3D {
    
    /** The constant for half of a circle. */
    private static final float HALF_CIRCLE_DEG  = 180.0f;
                                                
    /** The constant for the faces. */
    private static final int   FACES[]          = { 0, 0, 1, 1, 2, 2, 1, 1, 3, 3, 2, 2 };
                                                
    /** The constant for the texture coordinates. */
    private static final float TEXTURE_COORDS[] = { 1, 1, 1, 0, 0, 1, 0, 0 };
                                                
    /** The mesh. */
    private final TriangleMesh mesh;
                               
    /**
     * Constructor.
     *
     * @param startX
     *            Start of the line, x coordinate.
     * @param startZ
     *            Start of the line, z coordinate.
     * @param endX
     *            End of the line, x coordinate.
     * @param endZ
     *            End of the line, z coordinate.
     * @param thickness
     *            Thickness of the line.
     */
    public FlatLine3D(final Vector2f startPoint, final Vector2f endPoint, final double thickness) {
        
        // Create new mesh
        this.mesh = new TriangleMesh();
        
        // Build the mesh
        this.buildMesh(startPoint.getX(), startPoint.getY(), startPoint.getX(), startPoint.getY(), thickness);
    }
    
    /**
     * Build mesh method.
     * 
     * This method is responsible for building the triangle mesh of the line.
     * 
     * @param startX
     *            Start of the line, x coordinate.
     * @param startZ
     *            Start of the line, z coordinate.
     * @param endX
     *            End of the line, x coordinate.
     * @param endZ
     *            End of the line, z coordinate.
     * @param thickness
     *            Thickness of the line.
     */
    private void buildMesh(final double startX,
            final double startZ,
            final double endX,
            final double endZ,
            final double thickness) {
            
        // Calculate direction
        final double directionX = endX - startX;
        final double directionZ = endZ - startZ;
        
        // Calculate rotation
        final double rotation = (Math.atan2(directionZ, directionX) * FlatLine3D.HALF_CIRCLE_DEG) / Math.PI;
        // Calculate alpha, and cos & sin values
        final double alpha = (rotation / FlatLine3D.HALF_CIRCLE_DEG) * Math.PI;
        final double calcCos = Math.cos(alpha) * thickness;
        final double calcSin = Math.sin(alpha) * thickness;
        
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
        final float vertices[] = { x1, 0.0f, z1, x2, 0.0f, z2, x3, 0.0f, z3, x4, 0.0f, z4 };
        
        // Clear mesh
        this.mesh.getPoints().clear();
        this.mesh.getTexCoords().clear();
        this.mesh.getFaces().clear();
        
        // Add vertices
        this.mesh.getPoints().addAll(vertices);
        // Add texture coords
        this.mesh.getTexCoords().addAll(FlatLine3D.TEXTURE_COORDS);
        // Add faces
        this.mesh.getFaces().addAll(FlatLine3D.FACES);
    }
    
    /**
     * Gets the mesh view.
     *
     * @return The {@link MeshView} of the {@link TriangleMesh}.
     */
    public MeshView getMeshView() {
        return new MeshView(this.mesh);
    }
    
    /**
     * Gets the triangle mesh.
     *
     * @return The {@link TriangleMesh} of the line.
     */
    public TriangleMesh getTriangleMesh() {
        return this.mesh;
    }
}
