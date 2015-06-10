/*
 * The MIT License
 *
 * Copyright 2015 romnous.
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
import org.ejml.ops.CommonOps;

/**
 * A test implementation of a AbstractUKF for a three-dimensional system with linear
 * functions and Gaussian noise.
 * 
 * @author Rimon Oz
 */
public class DistortedDoublesUKF extends AbstractUKF {
    
    private final StateModel state;
    @SuppressWarnings("unused")
	private final StateModel measurement;
    
    /**
     * Constructor method
     * @param lambda                Scaling parameter
     * @param predictionFunction    Prediction matrix
     * @param measurementFunction   Measurement matrix
     */
    public DistortedDoublesUKF(double lambda, DenseMatrix64F predictionFunction, DenseMatrix64F measurementFunction) {
        super(lambda, predictionFunction, measurementFunction);
        // Initialize the state and measurement ...
        state        = new StateModel(new DenseMatrix64F(3, 1, true, 0, 0, 0), CommonOps.identity(3));
        measurement  = new StateModel(new DenseMatrix64F(3, 1, true, 1, 1, 2), CommonOps.identity(3));
        // ... and make the first prediction
        super.predict(state);
    }
    
    /**
     * Runs a single iteration over the AbstractUKF; updating the current state based on
     * given measurement and then predicting the true state.
     * <p>
     * The state-object is mutated to reflect the prediction.
     * 
     * @param measurement The newest (measured) data from DetectionFrame
     */
    public void run(DenseMatrix64F measurement) {
        // Estimate the current state based on measurement and previously estimated covariance ...
        super.update(this.state, new StateModel(measurement, this.state.getCovariance()));
        // ... and then predict the true state
        super.predict(state);
    }
    
    /**
     *
     * @return mean
     */
    public DenseMatrix64F getMean() {
        return state.getMean();
    }
    /**
     *
     * @return covariance
     */
    public DenseMatrix64F getCovariance() {
        return state.getCovariance();
    }
}
