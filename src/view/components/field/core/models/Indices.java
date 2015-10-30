package view.components.field.core.models;


import view.components.field.core.math.Vector3f;



public class Indices {
	
	
	private Vector3f _vertexIndex;
	private Vector3f _textureIndex;
	private Vector3f _normalIndex;
	

	public Indices() {
		
		_vertexIndex = new Vector3f();
		_textureIndex = new Vector3f();
		_normalIndex = new Vector3f();
	}
	
	public Indices(Vector3f vertexIndex, Vector3f textureIndex, Vector3f normalIndex) {
		
		_vertexIndex = vertexIndex;
		_textureIndex = textureIndex;
		_normalIndex = normalIndex;
	}
	
	
	public Vector3f getVerticeIndex() { return _vertexIndex; }
	public Vector3f getTextureIndex() { return _textureIndex; }
	public Vector3f getNormalIndex() { return _normalIndex; }

	
	public void setVertexIndex(Vector3f vertexIndex) { _vertexIndex = vertexIndex; }
	public void setTextureIndex(Vector3f textureIndex) { _textureIndex = textureIndex; }
	public void setNormalIndex(Vector3f normalIndex) { _normalIndex = normalIndex; }
}

