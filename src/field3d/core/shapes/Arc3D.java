package field3d.core.shapes;


import field3d.core.math.Vector3f;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;


// TODO: needs implementation, javadoc, comment, testing & cleanup
public class Arc3D {

	private TriangleMesh _triangleMesh;
	

	public Arc3D(double startAngle, double endAngle, double radius, double thickness, int numDivisions) {
		
		_triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
		
		// Calculate vertices
		float[] vertices = calculateVertices(startAngle, endAngle, radius, thickness, numDivisions);
		_triangleMesh.getPoints().addAll(vertices); 
		
		
		// Calculate texture coords
		float[] texCoords = {
                1, 1, // idx t0
                1, 0, // idx t1
                0, 1, // idx t2
                0, 0  // idx t3
        };
		 
		 _triangleMesh.getTexCoords().addAll(texCoords);
		 
		 
		 int faces[] = calculateFaces(numDivisions);
		_triangleMesh.getFaces().addAll(faces);	 
	}
	
	public TriangleMesh TriangleMesh() { return _triangleMesh; }
	public MeshView MeshView() { return new MeshView(_triangleMesh); }

	
	private float[] calculateVertices(double startAngle, double endAngle, double diameter, double thickness, int numDivisions) {
		
		double totalAngle = endAngle - startAngle;
		double deltaAngle = totalAngle / numDivisions;
		double curAngle = startAngle;
		
		double deltaAngleRad = deltaAngle * Math.PI / 180.0;
		double curAngleRad = curAngle * Math.PI / 180.0;
		
		int amountOfVertices = (numDivisions + 1) * 2;
		float vertices[] = new float[amountOfVertices * 3];
		
		for (int i = 0; i < numDivisions + 1; i++) {
			
			double halfOfThickness = thickness / 2.0;
			double radius = diameter / 2.0;
			
			double b1x = Math.cos(curAngleRad) * (radius - halfOfThickness);
			double b1y = Math.sin(curAngleRad) * (radius - halfOfThickness);
			double b11x = Math.cos(curAngleRad) * (radius + halfOfThickness);
			double b11y = Math.sin(curAngleRad) * (radius + halfOfThickness);		
			
			Vector3f vectorB1 = new Vector3f((float)b1x, (float)b1y, 0.0f);
			Vector3f vectorB11 = new Vector3f((float)b11x, (float)b11y, 0.0f);
			
			System.arraycopy(vectorB1.getFloatArray(), 0, vertices, i * 6, 3);
			System.arraycopy(vectorB11.getFloatArray(), 0, vertices, (i * 6) + 3, 3);
			
			curAngle += deltaAngle;
			curAngleRad += deltaAngleRad;
		}
		

		return vertices;
	}	

	
	private int[] calculateFaces(int numDivisions) {
		
		int indicies[] = new int[(numDivisions * 2) * 6];
		int triangleCounter = 0;
		
		// Loop through faces, 2 faces(triangles) per division
		for (int i = 0; i < numDivisions * 2; i++) {
			
			// Map faces counter-clockwise so it faces towards us 
			if (i % 2 == 0) {
				
				indicies[i * 6] = i + 2;
				indicies[(i * 6) + 2] = i + 1;
				indicies[(i * 6) + 4] = i;
			
			} else {
			
				indicies[i * 6] = i;
				indicies[(i * 6) + 2] = i + 1;
				indicies[(i * 6) + 4] = i + 2;
			}
			
			
			// Map texture coords
			if (triangleCounter == 0) {
				
				indicies[(i * 6) + 1] = 2;
				indicies[(i * 6) + 3] = 0;
				indicies[(i * 6) + 5] = 3;
			
			} else if (triangleCounter == 1) {
			
				indicies[(i * 6) + 1] = 0;
				indicies[(i * 6) + 3] = 3;
				indicies[(i * 6) + 5] = 1;
				
			} else if (triangleCounter == 2) {
				
				indicies[(i * 6) + 1] = 3;
				indicies[(i * 6) + 3] = 1;
				indicies[(i * 6) + 5] = 2;
			
			} else if (triangleCounter == 3) {
				
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
}
