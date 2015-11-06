package org.ssh.field3d.core.models;

import org.ssh.field3d.core.math.Vector3f;

public class Indices {
    
    private Vector3f _vertexIndex;
    private Vector3f _textureIndex;
    private Vector3f _normalIndex;
                     
    public Indices() {
        
        this._vertexIndex = new Vector3f();
        this._textureIndex = new Vector3f();
        this._normalIndex = new Vector3f();
    }
    
    public Indices(final Vector3f vertexIndex, final Vector3f textureIndex, final Vector3f normalIndex) {
        
        this._vertexIndex = vertexIndex;
        this._textureIndex = textureIndex;
        this._normalIndex = normalIndex;
    }
    
    public Vector3f getNormalIndex() {
        return this._normalIndex;
    }
    
    public Vector3f getTextureIndex() {
        return this._textureIndex;
    }
    
    public Vector3f getVerticeIndex() {
        return this._vertexIndex;
    }
    
    public void setNormalIndex(final Vector3f normalIndex) {
        this._normalIndex = normalIndex;
    }
    
    public void setTextureIndex(final Vector3f textureIndex) {
        this._textureIndex = textureIndex;
    }
    
    public void setVertexIndex(final Vector3f vertexIndex) {
        this._vertexIndex = vertexIndex;
    }
}
