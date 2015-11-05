/**
 * 
 * This class represents a 3d org.ssh.models
 * 
 * @author marklef2
 * @date 30-9-2015
 */
package org.ssh.field3d.core.models;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import java.util.ArrayList;

import org.ssh.field3d.core.math.Vector3f;


public class Model3D {
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	private String _name;
	
	private ArrayList<Vector3f> _vertices;
	private ArrayList<Vector3f> _normals;
	private ArrayList<Vector3f> _textureCoords;
	private TriangleMesh _mesh;
	private MeshView _meshView;

	private boolean _isBuilded;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 
	 * Default Constructor
	 * 
	 */
	public Model3D() {
		
		// Creating array lists
		_vertices = new ArrayList<Vector3f>();
		_normals = new ArrayList<Vector3f>();
		_textureCoords = new ArrayList<Vector3f>();
		_mesh = new TriangleMesh();
		
		_isBuilded = false;
	}
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param name Model name
	 * @param vertices Array list of vertices
	 * @param normals Array list of normals
	 * @param textureCoords Array list of texture coords
	 */
	public Model3D(String name, ArrayList<Vector3f> vertices, ArrayList<Vector3f> normals, ArrayList<Vector3f> textureCoords) {
		
		// Set the mesh
		SetMesh(vertices, normals, textureCoords);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	public String getName() { return _name; }
	public TriangleMesh getMesh() { return _mesh; }
	public MeshView getMeshView() { return _meshView; }
	public ArrayList<Vector3f> getVertices() { return _vertices; }
	public ArrayList<Vector3f> getNormals() { return _normals; }
	public ArrayList<Vector3f> getTextureCoords() { return _textureCoords; }
	
	public boolean isBuilded() { return _isBuilded; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	public void setName(String name) { _name = name; }
	public void setVertices(ArrayList<Vector3f> vertices) { _vertices = vertices; }
	public void setNormals(ArrayList<Vector3f> normals) { _normals = normals; }
	public void setTextureCoords(ArrayList<Vector3f> textureCoords) { _textureCoords = textureCoords; }
	public void setMeshView(MeshView meshView) { _meshView = meshView; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	public void SetMesh() {
		
		_mesh = new TriangleMesh();
		
		for (Vector3f vertex : _vertices) {
		
			_mesh.getPoints().addAll(vertex.getFloatArray());
		}
		System.out.println(_mesh.getPoints());
		
		for (Vector3f normal : _normals) {
			
			_mesh.getNormals().addAll(normal.getFloatArray());
		}
		
		for (Vector3f textureCoord : _textureCoords) {
			
			_mesh.getTexCoords().addAll(textureCoord.getFloatArray());
		}
		
		_isBuilded = true;
	}
	public void SetMesh(ArrayList<Vector3f> vertices, ArrayList<Vector3f> normals, ArrayList<Vector3f> textureCoords) {
		
		_mesh = new TriangleMesh();
		
		// Setting values
		_vertices = vertices;
		_normals = normals;
		_textureCoords = textureCoords;
		
		
		for (Vector3f vertex : vertices) {
			
			
			_mesh.getPoints().addAll(vertex.getFloatArray());
			
		}
		
		System.out.println(_mesh.getPoints());
		
		for (Vector3f normal : normals) {
			
			_mesh.getNormals().addAll(normal.getFloatArray());
		}
		
		for (Vector3f textureCoord : textureCoords) {
			
			_mesh.getTexCoords().addAll(textureCoord.getFloatArray());
		}
		
		_isBuilded = true;
	}
}
