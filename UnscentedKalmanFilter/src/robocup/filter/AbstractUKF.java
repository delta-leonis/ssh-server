/* 
 * The MIT License
 *
 * Copyright 2015 Rimon Oz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package robocup.filter;

import robocup.model.StateModel;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.CholeskyDecomposition;
import org.ejml.ops.CommonOps;

/**
 * The Unscented Kalman Filter for Non-Linear Estimation.
 * <p>
 * This is a skeletal implementation of the Kalman filter, leaving the update()
 * and predict() exposed to the extending sub-class.
 * <p>
 * For an introduction to the Kalman Filter see 
 * <a href="http://greg.czerniak.info/guides/kalman1/">this guide</a>
 * <p>
 * The equations used were primarily found in
 * <a href="http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf">this paper</a>
 * Information on the determination of the scaling parameter(s) and weights for sigma points can be found in
 * <a href="https://www.seas.harvard.edu/courses/cs281/papers/unscented.pdf">this paper</a> and in
 * <a href="https://www.cs.unc.edu/~welch/kalman/media/pdf/ACC02-IEEE1357.PDF">this paper</a>.
 * 
 * @author Rimon Oz
 */
abstract public class AbstractUKF {
    /**
     * lambda is the scaling parameter given as
     * lambda = alpha^2 * (L + kappa) - L
     * (see https://www.cs.unc.edu/~welch/kalman/media/pdf/ACC02-IEEE1357.PDF)
     */
    private final double         lambda;
    private final int            dimension;
    private final DenseMatrix64F stateSigmaPoints[];
    private final DenseMatrix64F measurementSigmaPoints[];
    private final double         weights[];

    /**
     * These are the prediction and measurement functions.
     * The prediction function is used to predict the current state from
     * the previous state.
     * The measurement function is used to determine the current state from
     * measurement data.
     */
    private final DenseMatrix64F predictionFunction;
    private final DenseMatrix64F measurementFunction;

    /**
     * Constructor method.
     * <p>
     * The constructor initializes sigma points and weights.
     * 
     * @param lambda                Determines the impact of the sigma points.
     * @param predictionFunction    Matrix to predict current state from previous state
     * @param measurementFunction   Matrix to determine current state from measurement data
     */
    public AbstractUKF(double lambda, DenseMatrix64F predictionFunction, DenseMatrix64F measurementFunction) {
        this.lambda              = lambda;
        this.dimension           = predictionFunction.numRows;  // Assume this to be true
                                                                // in the case of state-estimation
                                                                // (not parameter estimation)
        this.predictionFunction  = predictionFunction;
        this.measurementFunction = measurementFunction;

        // Initialize sigma points
        this.stateSigmaPoints       = new DenseMatrix64F[2 * this.dimension + 1];
        this.measurementSigmaPoints = new DenseMatrix64F[2 * this.dimension + 1];
        for (int i = 0; i < 2 * this.dimension + 1; i += 1) {
            this.stateSigmaPoints[i]       = new DenseMatrix64F(this.dimension, 1);
            this.measurementSigmaPoints[i] = new DenseMatrix64F(this.dimension, 1);
        }

        // The weight for the initial sigma point (mean) differs by (1 - alpha^2 + beta)
        // for the true state and true covariance. The difference is considered negligible.
        // The weight for the intial sigma point is then given by lambda/(L + lambda).
        // The remaining weights are given by 1/(2*(L + lambda))
        // (see page 3 on https://www.seas.harvard.edu/courses/cs281/papers/unscented.pdf)
        this.weights    = new double[2 * this.dimension + 1];
        this.weights[0] = lambda / (this.dimension + lambda);
        double weight   = 1 / (2 * (this.dimension + lambda));
        for (int index = 1; index <= this.dimension; index += 1) {
            // This is the weight for the right sigma-point (positive)
            this.weights[index]                  = weight;
            // This is the weight for the left sigma-point (positive)
            this.weights[index + this.dimension] = weight;
        }
    }

    /**
     * Predicts the current state using provided (true) state.
     * <p>
     * This is the first step of the (Unscented) Kalman Filter. Using the provided
     * statistics and accompanying sigma-points the characteristics (distribution) of the 
     * input are preserved as the data is propagated through the prediction function.
     * <p>
     * The given state-object is mutated to reflect the new prediction.
     * <p>
     * The prediction function is an L*L matrix whose rows are the characteristic
     * equations of the model.
     * 
     * @param state
     */
    public void predict(StateModel state) {
        // Get references
        DenseMatrix64F mean       = state.getMean();
        DenseMatrix64F covariance = state.getCovariance();
        // Calculate the sigma points first for given state.
        calculateSigmaPoints(state);

        // First we compute the mean, the mean is given by
        // Sum of (w^[index]_m * stateSigmaPoints[index]) for index = 0 .. 2n
        // (see formula 4, page 2 on http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        mean.zero();
        for (int index = 0; index < this.stateSigmaPoints.length; index += 1) {
            DenseMatrix64F sigmaPoint = this.stateSigmaPoints[index];

            // Here we calculate the impact of each sigma point (weighted and added)
            DenseMatrix64F sigmaPointPrediction = new DenseMatrix64F(this.dimension, 1);
            CommonOps.mult(this.predictionFunction, sigmaPoint, sigmaPointPrediction);
            CommonOps.add(mean, this.weights[index], sigmaPointPrediction, mean);
        }
        // With the predicted mean we can calculate the covariance of the prediction.
        // The covariance is given by
        // Sum of (w^[index]_c * (stateSigmaPoints[index] - mean) * transpose(stateSigmaPoints[index] - mean) for index = 0 .. 2n
        // (see formula 5, page 2 on http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        for (int index = 0; index < stateSigmaPoints.length; index += 1) {
            DenseMatrix64F sigmaPoint = stateSigmaPoints[index];

            // Here we calculate the impact of each sigma point (weighted and added)
            CommonOps.subtract(sigmaPoint, mean, sigmaPoint);
            CommonOps.multAddTransB(weights[index], sigmaPoint, sigmaPoint, covariance);
        }
    }

    /**
     * Updates the current state based on adjusted measurement and provided state.
     * <p>
     * This is the second step of the Kalman Filter. First, the measurement and state
     * are both adjusted by the measurement function. The cross-covariance is then used to
     * compute the Kalman gain after which the true state and true covariance can be
     * estimated. 
     * <p>
     * The given state-object is mutated to reflect
     * the new measurement and previous state.
     * <p>
     * The measurement function is an L*L matrix whose rows are the characteristic
     * equations of the measurement from raw sensor input (eg. type/sigfig conversion happens here).
     * 
     * @param state         The state to be updated
     * @param measurement   The measurement to be adjusted for
     */
    public void update(StateModel state, StateModel measurement) {
        // Get references.
        DenseMatrix64F stateMean          = state.getMean();
        DenseMatrix64F stateCovariance    = state.getCovariance();
        DenseMatrix64F measuredMean       = measurement.getMean();
        DenseMatrix64F measuredCovariance = measurement.getCovariance();
        // Calculate the sigma points first for given state.
        calculateSigmaPoints(state);
        
        // Calculate the measurement mean and the measurement mean update for each
        // sigma point.
        // (see page 2, formula 8 http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F predictedMeasurementMean = new DenseMatrix64F(this.dimension, 1);
        predictedMeasurementMean.zero();
        for (int index = 0; index < this.stateSigmaPoints.length; index += 1) {
            DenseMatrix64F sigmaPoint = this.stateSigmaPoints[index];

            // Here we propagate the sigma point through the measurement function
            // and update it ...
            DenseMatrix64F measurementSigmaPoint = new DenseMatrix64F(this.dimension, 1);
            CommonOps.mult(this.measurementFunction, sigmaPoint, measurementSigmaPoint);
            this.measurementSigmaPoints[index].set(measurementSigmaPoint);
            // ... and add the weighted portion to the mean.
            CommonOps.add(predictedMeasurementMean, this.weights[index], measurementSigmaPoint, predictedMeasurementMean);
        }

        // Calculate the measurement covariance and measurement covariance update for each sigma point.
        // sigma point.
        // (see page 2, formula 9 http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F predictedMeasurementCovariance = new DenseMatrix64F(this.dimension, this.dimension);
        predictedMeasurementCovariance.set(measuredCovariance);
        for (int index = 0; index < this.measurementSigmaPoints.length; index += 1) {
            DenseMatrix64F measurementSigmaPoint = this.measurementSigmaPoints[index];
            // First we take the difference ...
            CommonOps.subtract(measurementSigmaPoint, predictedMeasurementMean, measurementSigmaPoint);
            // ... and add the weighted portion (multiplied with its tranpose) to the covariance.
            CommonOps.multAddTransB(this.weights[index], 
                    measurementSigmaPoint, 
                    measurementSigmaPoint, 
                    predictedMeasurementCovariance
            );
        }

        // Calculate the cross covariance
        // (see page 2, formula 10 http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F crossCovariance = new DenseMatrix64F(this.dimension, this.dimension);
        crossCovariance.zero();
        for (int index = 0; index < this.measurementSigmaPoints.length; index += 1) {
            // Get references.
            DenseMatrix64F stateSigmaPoint = this.stateSigmaPoints[index];
            DenseMatrix64F measurementSigmaPoint = this.measurementSigmaPoints[index];
            
            // First we find the difference ...
            CommonOps.subtract(stateSigmaPoint, stateMean, stateSigmaPoint);
            // ... and add the weighted portion (multiplied with its tranpose) to the cross covariance.
            CommonOps.multAddTransB(this.weights[index], stateSigmaPoint, measurementSigmaPoint, crossCovariance);
        }
        
        // Calculate the Kalman gain.
        // The Kalman gain is given by
        // K = crossCovariance * inverse(predictedMeasurementCovariance)
        // (see formula 11, page 2 on http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F inversion = new DenseMatrix64F(this.dimension, this.dimension);
        DenseMatrix64F kalmanGain = new DenseMatrix64F(this.dimension, this.dimension);
        CommonOps.invert(predictedMeasurementCovariance, inversion);
        CommonOps.mult(crossCovariance, inversion, kalmanGain);

        // Calculate the true mean of the state.
        // The true mean of the state is given by
        // stateMean = predictedStateMean + K*(measurementMean - predictedMeasurementMean)
        // (see formula 12, page 2 on http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F kalmanMeanDifference = new DenseMatrix64F(measuredMean);
        CommonOps.subtract(kalmanMeanDifference, predictedMeasurementMean, kalmanMeanDifference);
        CommonOps.multAdd(kalmanGain, kalmanMeanDifference, stateMean);

        // Calculate the true covariance of the state.
        // The true covariance of the state is given by
        // stateCovariance = predictedStateCovariance - K * predictedMeasurementCovariance * transpose(K)
        // (see formula 13, page 2 on http://www.cs.washington.edu/robotics/postscripts/gp-ukf-iros-07.pdf)
        DenseMatrix64F kalmanCovarianceProduct = new DenseMatrix64F(this.dimension, this.dimension);
        CommonOps.mult(kalmanGain, predictedMeasurementCovariance, kalmanCovarianceProduct);
        CommonOps.multAddTransB(-1, kalmanCovarianceProduct, kalmanGain, stateCovariance);
    }
    
    /**
     * Calculates sigma points spread around mean (characterized by covariance) from given state.
     * 
     * Sigma points are drawn in a pre-configured manner from the distribution. These points are
     * spread equally on the left and right to maintain the characteristics of the state distribution
     * when propagated through the measurement/prediction function (also known as the Unscented Transform,
     * see http://ais.informatik.uni-freiburg.de/teaching/ws13/mapping/pdf/slam06-ukf-4.pdf for a read
     * how this works in the unscented Kalman filter).
     * 
     * The sigma points are mutated internally (this.stateSigmaPoints).
     * 
     * @param state 
     */
    private void calculateSigmaPoints(StateModel state) {
        DenseMatrix64F mean       = state.getMean();
        DenseMatrix64F covariance = state.getCovariance();

        /**
         * The sigma points (for state k) are given by
         * chi_k = [ mu_k  mu_k+sqrt((L + lambda)*covariance) mu_k-sqrt((L + lambda)*covariance) ]
         * where sqrt(k*covariance) is given by (lower) Cholesky decomposition.
         * Left and right sigma points are split into column vectors.
         * (see http://en.wikipedia.org/wiki/Cholesky_decomposition#Statement)
         */
        DenseMatrix64F sigmaMatrix = new DenseMatrix64F(this.dimension, this.dimension);
        sigmaMatrix.set(covariance);
        CommonOps.scale(dimension + lambda, sigmaMatrix);
        
        // Attempt to decompose ...
        CholeskyDecomposition<DenseMatrix64F> cholesky = DecompositionFactory.chol(this.dimension, true);
        
        // ... handle errors.
        if (!cholesky.decompose(sigmaMatrix))
            throw new RuntimeException("Cholesky failed");
        // Split the result of the decomposition into column vectors.
        DenseMatrix64F[] decomposition = CommonOps.columnsToVector(cholesky.getT(null), null);
        
        // The first sigma point is the mean.
        this.stateSigmaPoints[0].set(mean);
        
        // Since there are 2*L+1 sigma points there will be two sigma points for every
        // column vector (without the mean).
        for (int index = 0; index < this.dimension; index += 1) {
            DenseMatrix64F leftSigmaPoint  = this.stateSigmaPoints[index + 1];
            DenseMatrix64F rightSigmaPoint = this.stateSigmaPoints[index + this.dimension + 1];
            // Reset the sigma points.
            leftSigmaPoint.zero();
            rightSigmaPoint.zero();
            // These are equal to; stateSigmaPoints[index (+dimension+) + 1] = mean + decomposition[index]
            CommonOps.add(mean, decomposition[index], leftSigmaPoint);
            CommonOps.subtract(mean, decomposition[index], rightSigmaPoint);
        }
    }
}