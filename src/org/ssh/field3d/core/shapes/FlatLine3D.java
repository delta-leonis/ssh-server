package org.ssh.field3d.core.shapes;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * FlatLine3D class
 *
 * This class represents a flat(2d plane) line in 3d space, it gets drawn on the x, z axis.
 *
 * @author Mark Lefering - 33043
 * @date 5-11-2015
 */
public class FlatLine3D {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private Statics
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final float HALF_CIRCLE_DEG  = 180.0f;
    private static final int   FACES[]          = { 0, 0, 1, 1, 2, 2, 1, 1, 3, 3, 2, 2 };
    private static final float TEXTURE_COORDS[] = { 1, 1, 1, 0, 0, 1, 0, 0 };
                                                
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private final TriangleMesh _mesh;
                               
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Constructor
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
    public FlatLine3D(final double startX,
            final double startZ,
            final double endX,
            final double endZ,
            final double thickness) {
            
        // Create new mesh
        this._mesh = new TriangleMesh();
        
        // Build the mesh
        this.buildMesh(startX, startZ, endX, endZ, thickness);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
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
        this._mesh.getPoints().clear();
        this._mesh.getTexCoords().clear();
        this._mesh.getFaces().clear();
        
        // Add vertices
        this._mesh.getPoints().addAll(vertices);
        // Add texture coords
        this._mesh.getTexCoords().addAll(FlatLine3D.TEXTURE_COORDS);
        // Add faces
        this._mesh.getFaces().addAll(FlatLine3D.FACES);
    }
    
    public MeshView GetMeshView() {
        return new MeshView(this._mesh);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public TriangleMesh GetTriangleMesh() {
        return this._mesh;
    }
}
