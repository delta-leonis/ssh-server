package robocup.filter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import robocup.model.FieldPoint;

/**
 * The Kalman filter is used to filter out camera noise, based on the data of previous frames.
 * Basically makes sure that Robots don't randomly teleport across the screen
 */
public class Kalman {

	RealMatrix stateMatrix = new Array2DRowRealMatrix(new double[][] { { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 0, 0, 1, 0 },
			{ 0, 0, 0, 1 } }); // A
	RealMatrix controlMatrix = new Array2DRowRealMatrix(new double[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 },
			{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } }); // B
	RealMatrix observationMatrix = new Array2DRowRealMatrix(new double[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 },
			{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } }); // H
	RealMatrix processCovariance = new Array2DRowRealMatrix(new double[][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
			{ 0, 0, 0.1, 0 }, { 0, 0, 0, 0.1 } }); // Q
	RealMatrix measurementCovariance = new Array2DRowRealMatrix(new double[][] { { 0.1, 0, 0, 0 }, { 0, 0.1, 0, 0 },
			{ 0, 0, 0.1, 0 }, { 0, 0, 0, 0.1 } }); // R

	RealVector last_x;// = new ArrayRealVector(new double[] {300,300,5,5});

	RealMatrix last_P = new Array2DRowRealMatrix(new double[][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
			{ 0, 0, 0, 0 } });

	RealVector control = new ArrayRealVector(new double[] { 0, 0, 0, 0 });

	FieldPoint predictedPoint;
	RealVector predicted_x;

	/**
	 * Kalman filter constructor, init begin values of filter
	 * @param position Starting position of the FieldPoint we wish to track
	 * @param xSpeed Current speed on the x-axis
	 * @param ySpeed Current speed on the y-axis
	 */
	public Kalman(FieldPoint position, int xSpeed, int ySpeed) {
		last_x = new ArrayRealVector(new double[] { position.getX(), position.getY(), xSpeed, ySpeed });
		predictedPoint = position;
	}

	/**
	 * Filter noise out of robot location data, by comparing measured data with predicted data with
	 * @param position Starting position of the FieldPoint we wish to track
	 * @param xSpeed Current speed on the x-axis
	 * @param ySpeed Current speed on the y-axis
	 * @return filtered point of robot
	 */
	public FieldPoint filterPoint(FieldPoint measuredPoint, double xSpeed, double ySpeed) {
		/* see
		 * http://commons.apache.org/proper/commons-math/javadocs/api-3.3/index
		 * .html -- kalmanfilter
		 * https://www.cs.utexas.edu/~teammco/misc/kalman_filter/
		 * http://en.wikipedia.org/wiki/Kalman_filter */

		RealVector measurement = new ArrayRealVector(new double[] { measuredPoint.getX(), measuredPoint.getY(), xSpeed,
				ySpeed });

		// predict
		RealVector x = stateMatrix.operate(last_x).add(controlMatrix.operate(control));
		RealMatrix At = stateMatrix.transpose();
		RealMatrix P = stateMatrix.multiply(last_P).multiply(At).add(processCovariance);

		// update
		RealMatrix Ht = observationMatrix.transpose();
		RealMatrix S = observationMatrix.multiply(P).multiply(Ht).add(measurementCovariance);
		RealMatrix iS = MatrixUtils.inverse(S);
		RealMatrix K = P.multiply(Ht).multiply(iS);
		RealVector y = measurement.subtract(observationMatrix.operate(x));

		// updated values
		RealVector cur_x = x.add(K.operate(y));
		RealMatrix identity = MatrixUtils.createRealIdentityMatrix(4);
		RealMatrix cur_P = identity.subtract(K.multiply(observationMatrix)).multiply(P);

		last_x = cur_x;
		last_P = cur_P;

		return new FieldPoint(cur_x.getEntry(0), cur_x.getEntry(1));
	}

	/**
	 * @return last Y position
	 */
	public double getLastY() {
		return last_x.getEntry(1);
	}

	/**
	 * @return last X position
	 */
	public double getLastX() {
		return last_x.getEntry(0);
	}

	/**
	 * @return A FieldPoint with the predicted robot location.
	 */
	public FieldPoint getPredictPoint() {
		return predictedPoint;
	}

	/**
	 * predict future Point for robotLocation 
	 */
	public void predictPoint() {
		RealVector pred_x = stateMatrix.operate(last_x).add(controlMatrix.operate(control));
		predictedPoint = new FieldPoint(pred_x.getEntry(0), pred_x.getEntry(1));
	}
}
