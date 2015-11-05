package org.ssh.field3d.core.shapes;


import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;


/**
 * FlatLine3D class
 * 
 * 	This class represents a flat(2d plane) line in 3d space, it gets drawn on the x, z axis.
 * 
 * @author Mark Lefering - 33043
 * @date 5-11-2015
 */
public class FlatLine3D {
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private Statics
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static final float HALF_CIRCLE_DEG = 180.0f;
	private static final int FACES[] = { 0, 0, 1, 1, 2, 2,
										 1, 1, 3, 3, 2, 2  };
	private static final float TEXTURE_COORDS[] = {  1, 1,
												   	 1, 0,
												   	 0, 1,
												   	 0, 0  };
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private TriangleMesh _mesh;


	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Constructor
	 *
	 * @param startX Start of the line, x coordinate.
	 * @param startZ Start of the line, z coordinate.
	 * @param endX End of the line, x coordinate.
	 * @param endZ End of the line, z coordinate.
	 * @param thickness Thickness of the line.
	 */
	public FlatLine3D(double startX, double startZ, double endX, double endZ, double thickness) {
		
		// Create new mesh
		_mesh = new TriangleMesh();
		
		// Build the mesh
		buildMesh(startX, startZ, endX, endZ, thickness);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public TriangleMesh GetTriangleMesh() { return _mesh; }
	public MeshView GetMeshView() { return new MeshView(_mesh); }
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Build mesh method.
	 * 
	 * This method is responsible for building the triangle mesh of the line.
	 * 
	 * @param startX Start of the line, x coordinate.
	 * @param startZ Start of the line, z coordinate.
	 * @param endX End of the line, x coordinate.
	 * @param endZ End of the line, z coordinate.
	 * @param thickness Thickness of the line.
	 */
	private void buildMesh(double startX, double startZ, double endX, double endZ, double thickness) {
		
		// Calculate direction
		double directionX = endX - startX;
		double directionZ = endZ - startZ;
		
		// Calculate rotation
		double rotation = Math.atan2(directionZ, directionX) * HALF_CIRCLE_DEG / Math.PI;
		// Calculate alpha, and cos & sin values
		double alpha = rotation / HALF_CIRCLE_DEG * Math.PI;
		double calcCos = Math.cos(alpha) * thickness;
		double calcSin = Math.sin(alpha) * thickness;		
		
		// Calculate points
		float x1 = (float) (startX - calcSin);
		float z1 = (float) (startZ + calcCos);		
		float x2 = (float) (endX - calcSin);
		float z2 = (float) (endZ + calcCos);		
		float x3 = (float) (startX + calcSin);
		float z3 = (float) (startZ - calcCos);		
		float x4 = (float) (endX + calcSin);
		float z4 = (float) (endZ - calcCos);
		
		// Create vertices array
		float vertices[] = { x1, 0.0f, z1, x2, 0.0f, z2,
							 x3, 0.0f, z3, x4, 0.0f, z4 };
		
		// Clear mesh
		_mesh.getPoints().clear();
		_mesh.getTexCoords().clear();
		_mesh.getFaces().clear();
		
		// Add vertices
		_mesh.getPoints().addAll(vertices);
		// Add texture coords
		_mesh.getTexCoords().addAll(TEXTURE_COORDS);
		// Add faces
		_mesh.getFaces().addAll(FACES);
	}
}
