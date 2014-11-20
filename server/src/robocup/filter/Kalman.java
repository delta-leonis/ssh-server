package robocup.filter;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class Kalman {


	public Kalman() {
		/*
		double constantVoltage = 10d;
		double measurementNoise = 0.1d;
		double processNoise = 1e-5d;
		double accelNoise = 0.2d;
		
		double dt = 0.1d;
		//filter for x and y positions + their respective speeds

		RealMatrix A = new Array2DRowRealMatrix(new double[][] { 
				{1, dt,0,0 },
				{ 0,1,0,0},
				{0,0,1d,dt},
				{0,0,0,1d} });
		RealMatrix B = new Array2DRowRealMatrix(new double[][]{	
				{0,0,0,0 }	,	
				{0,0,0,0 }, 
				{0,0,1d,0},
				{0,0,0,1d} });
		RealMatrix H = new Array2DRowRealMatrix(new double[][] {
				{ 1d,0,0,0},
				{0,1d,0,0},
				{0,0,1d,0},
				{0,0,0,1d} });
		
		
		
		RealVector x = new ArrayRealVector(new double[] { 0,0,0,0 });
		RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { 
				{ 1,0,0,0 }, 
				{ 0,1,0,0 },
				{ 0,0,1,0},
				{ 0,0,0,1} });
		
		RealMatrix R = new Array2DRowRealMatrix(new double[][] { 
				{0.2, 0,0,0},
				{0,0.2,0,0},
				{0,0,0.2,0},
				{0,0,0,0.2}
		});
		
		RealMatrix Q = new Array2DRowRealMatrix(new double[][] {
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0}
		});
		
		
		
		
		
		// A = [ 1 ]
//		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		RealMatrix A = new Array2DRowRealMatrix(new double[][] { {1, dt }, { 0,1} });
		// B = null
//		RealMatrix B = null;
		RealMatrix B = new Array2DRowRealMatrix(new double[][]{	{Math.pow(dt,  2d) / 2d }	,	{ dt }	});
		// H = [ 1 ]
//		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		RealMatrix H = new Array2DRowRealMatrix(new double[][] {{ 1d , 0}});
		// x = [ 10 ]
//		RealVector x = new ArrayRealVector(new double[] { constantVoltage });
		RealVector x = new ArrayRealVector(new double[] { 0,0 });
		
		RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
			    { Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
			    { Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });
		
		

		// Q = [ 1e-5 ]
//		RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
		RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));
		// P = [ 1 ]
//		RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 1d });
		RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });
		// R = [ 0.1 ]
//		RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });
		RealMatrix R = new Array2DRowRealMatrix(new double[] { Math.pow(measurementNoise, 2) });

		
		//optional
		RealVector u = new ArrayRealVector(new double[] { 0.1d });
		
		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		KalmanFilter filter = new KalmanFilter(pm, mm);  
		
		

		// process and measurement noise vectors
//		RealVector pNoise = new ArrayRealVector(1);
//		RealVector mNoise = new ArrayRealVector(1);
		
		RealVector tmpNoise = new ArrayRealVector(new double[]{Math.pow(dt, 2d)/2d, dt});
		RealVector mNoise = new ArrayRealVector(1);


		
		RandomGenerator rand = new JDKRandomGenerator();
		// iterate 60 steps
		for (int i = 0; i < 60; i++) {
//		System.out.println("beginning");
		filter.predict(u);
		RealVector pNoise = tmpNoise.mapMultiply(accelNoise * rand.nextGaussian());

//		System.out.println("after Predict");
		// simulate the process
//		pNoise.setEntry(0, processNoise * rand.nextGaussian());

//		System.out.println("after setEntry");
		// x = A * x + p_noise
		x = A.operate(x).add(B.operate(u)).add(pNoise);

//		System.out.println("after adding pNoise");
		// simulate the measurement
		mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

//		System.out.println("after 2nd setEntry");
		// z = H * x + m_noise
		RealVector z = H.operate(x).add(mNoise);

//		System.out.println("after adding mNoise");
		filter.correct(z);

//		System.out.println("after correct");
		double position = filter.getStateEstimation()[0];
		double velocity = filter.getStateEstimation()[1];
		System.out.println("position: "+position);
		System.out.println("velocity: "+velocity);
//		System.out.println("at the end");

 
 
		} */

	}

}
