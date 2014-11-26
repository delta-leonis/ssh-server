package robocup.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


import robocup.model.Point;

public class Kalman {
	
	RealMatrix A = new Array2DRowRealMatrix(new double[][] { 
			{1,0,1,0},
			{0,1,0,1},
			{0,0,1,0},
			{0,0,0,1}});
	RealMatrix B = new Array2DRowRealMatrix(new double[][] { 
			{1,0,0,0},
			{0,1,0,0},
			{0,0,1,0},
			{0,0,0,1}});
	RealMatrix H = new Array2DRowRealMatrix(new double[][] { 
			{1,0,0,0},
			{0,1,0,0},
			{0,0,1,0},
			{0,0,0,1}});
	RealMatrix Q = new Array2DRowRealMatrix(new double[][] { 
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0.1,0},
			{0,0,0,0.1}});
	RealMatrix R = new Array2DRowRealMatrix(new double[][] {
			{0.1,0,0,0},
			{0,0.1,0,0},
			{0,0,0.1,0},
			{0,0,0,0.1}});
	
	RealVector last_x;// = new ArrayRealVector(new double[] {300,300,5,5});
	
	RealMatrix last_P = new Array2DRowRealMatrix(new double[][] { 
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0,0},
			{0,0,0,0}});

	public Kalman(Point position, int xSpeed, int ySpeed) {
		last_x = new ArrayRealVector(new double[] {position.getX(),position.getY(),xSpeed,ySpeed});
	}	
	
	public Point filterPoint(Point measuredPoint, int xSpeed, int ySpeed)
	{
		/*
		 * see http://commons.apache.org/proper/commons-math/javadocs/api-3.3/index.html -- kalmanfilter 
		 * https://www.cs.utexas.edu/~teammco/misc/kalman_filter/
		 * http://en.wikipedia.org/wiki/Kalman_filter
		 * 
		 */

			RealVector measurement = new ArrayRealVector(new double[] {measuredPoint.getX(),measuredPoint.getY(),xSpeed,ySpeed});
			RealVector control = new ArrayRealVector(new double[] {0,0,0,0});
			
			
			//predict
			RealVector x = A.operate(last_x).add(B.operate(control));
			RealMatrix At = A.transpose();
			RealMatrix P = A.multiply(last_P).multiply(At).add(Q);
			
			//update
			RealMatrix Ht = H.transpose();
			RealMatrix S = H.multiply(P).multiply(Ht).add(R);
			RealMatrix iS = MatrixUtils.inverse(S);
			RealMatrix K = P.multiply(Ht).multiply(iS);
			RealVector y = measurement.subtract(H.operate(x));
			
			
			//updated values
			RealVector cur_x = x.add(K.operate(y));
			RealMatrix identity = MatrixUtils.createRealIdentityMatrix(4);
			RealMatrix cur_P = identity.subtract(K.multiply(H)).multiply(P);
			
			last_x = cur_x;
			last_P = cur_P;
			
			System.out.println("x: "+ cur_x.getEntry(0) + " y:" + cur_x.getEntry(1));
			return new Point((float)cur_x.getEntry(0), (float)cur_x.getEntry(1));
	}
	
	public double getLastY(){
		return last_x.getEntry(1);
	}
	
	public double getLastX(){
		return last_x.getEntry(0);
	}

}
