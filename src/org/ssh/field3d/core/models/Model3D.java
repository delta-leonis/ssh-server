/**
 *
 * This class represents a 3d org.ssh.models
 *
 * @author marklef2
 * @date 30-9-2015
 */
package org.ssh.field3d.core.models;

import java.util.ArrayList;

import org.ssh.field3d.core.math.Vector3f;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Model3D {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private String              _name;
                                
    private ArrayList<Vector3f> _vertices;
    private ArrayList<Vector3f> _normals;
    private ArrayList<Vector3f> _textureCoords;
    private TriangleMesh        _mesh;
    private MeshView            _meshView;
                                
    private boolean             _isBuilded;
                                
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Default Constructor
     * 
     */
    public Model3D() {
        
        // Creating array lists
        this._vertices = new ArrayList<Vector3f>();
        this._normals = new ArrayList<Vector3f>();
        this._textureCoords = new ArrayList<Vector3f>();
        this._mesh = new TriangleMesh();
        
        this._isBuilded = false;
    }
    
    /**
     * 
     * Constructor
     * 
     * @param name
     *            Model name
     * @param vertices
     *            Array list of vertices
     * @param normals
     *            Array list of normals
     * @param textureCoords
     *            Array list of texture coords
     */
    public Model3D(final String name,
            final ArrayList<Vector3f> vertices,
            final ArrayList<Vector3f> normals,
            final ArrayList<Vector3f> textureCoords) {
            
        // Set the mesh
        this.SetMesh(vertices, normals, textureCoords);
    }
    
    public TriangleMesh getMesh() {
        return this._mesh;
    }
    
    public MeshView getMeshView() {
        return this._meshView;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getName() {
        return this._name;
    }
    
    public ArrayList<Vector3f> getNormals() {
        return this._normals;
    }
    
    public ArrayList<Vector3f> getTextureCoords() {
        return this._textureCoords;
    }
    
    public ArrayList<Vector3f> getVertices() {
        return this._vertices;
    }
    
    public boolean isBuilded() {
        return this._isBuilded;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void SetMesh() {
        
        this._mesh = new TriangleMesh();
        
        for (final Vector3f vertex : this._vertices) {
            
            this._mesh.getPoints().addAll(vertex.getFloatArray());
        }
        System.out.println(this._mesh.getPoints());
        
        for (final Vector3f normal : this._normals) {
            
            this._mesh.getNormals().addAll(normal.getFloatArray());
        }
        
        for (final Vector3f textureCoord : this._textureCoords) {
            
            this._mesh.getTexCoords().addAll(textureCoord.getFloatArray());
        }
        
        this._isBuilded = true;
    }
    
    public void SetMesh(final ArrayList<Vector3f> vertices,
            final ArrayList<Vector3f> normals,
            final ArrayList<Vector3f> textureCoords) {
            
        this._mesh = new TriangleMesh();
        
        // Setting values
        this._vertices = vertices;
        this._normals = normals;
        this._textureCoords = textureCoords;
        
        for (final Vector3f vertex : vertices) {
            
            this._mesh.getPoints().addAll(vertex.getFloatArray());
            
        }
        
        System.out.println(this._mesh.getPoints());
        
        for (final Vector3f normal : normals) {
            
            this._mesh.getNormals().addAll(normal.getFloatArray());
        }
        
        for (final Vector3f textureCoord : textureCoords) {
            
            this._mesh.getTexCoords().addAll(textureCoord.getFloatArray());
        }
        
        this._isBuilded = true;
    }
    
    public void setMeshView(final MeshView meshView) {
        this._meshView = meshView;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setNormals(final ArrayList<Vector3f> normals) {
        this._normals = normals;
    }
    
    public void setTextureCoords(final ArrayList<Vector3f> textureCoords) {
        this._textureCoords = textureCoords;
    }
    
    public void setVertices(final ArrayList<Vector3f> vertices) {
        this._vertices = vertices;
    }
}
