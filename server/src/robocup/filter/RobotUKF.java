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

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import robocup.model.FieldPoint;
import robocup.model.StateModel;

/**
 * Implementation of AbstractUKF for a robot given only x, y, and orientation data
 * and calculating x and y velocities.
 * 
 * @author Rimon Oz
 */
public class RobotUKF extends AbstractUKF {
    private final StateModel state;
    private final StateModel measurement;
    
    /**
     * Initializes a AbstractUKF for a robot in a RobocupSSL match.
     * 
     * @param initialPoint 
     */
    public RobotUKF(FieldPoint initialPoint) {
        /*
         *  The prediction function is an L*L matrix whose rows are the characteristic
         * equations of the model.
         *  The measurement function is an L*L matrix whose rows are the characteristic
         * equations of the measurement from raw sensor input (eg. type/sigfig conversion happens here).
         */
        super(
            2,                  // The scaling parameter (lambda) 
            new DenseMatrix64F(    // The prediction function
                5,                 //   #rows
                5,                 //   #columns
                true,              //   row-major
                                   // Now follows the matrix. Note that the
                                   // frequency is arbitrarily chosen as 40 times per second.
                                   // The acutual frequency will depend on the underlying system specs
                                   // and performance.
                1, 0, 1/30, 0, 0,  // x_pos       = x_pos + v_x*frequency
                0, 1, 0, 1/30, 0,  // y_pos       = y_pos + v_y*frequency
                0, 0, 1, 0, 0,     // v_x         = v_x
                0, 0, 0, 1, 0,     // v_y         = v_y
                0, 0, 0, 0, 1      // orientation = orientation
            ),
            CommonOps.identity(5)   // The measurement function
                                    // It is set to the identity matrix since
                                    // we are assuming the data is trust-worthy ;).
        );
        // Convert the Point to matrix-form.
        DenseMatrix64F initialState = new DenseMatrix64F(5, 1, true, initialPoint.getX(), initialPoint.getY(), 0, 0, 0);
        // Initialize the state and measurement ...
        state        = new StateModel(initialState, CommonOps.identity(5));
        measurement  = new StateModel(initialState, CommonOps.identity(5));
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
     * @param orientation The newest (measured) orientation
     */
    public void run(FieldPoint measurement, double orientation) {
        // Estimate the current state based on measurement and previously estimated covariance ...
        super.update(
            this.state, 
            new StateModel(
                new DenseMatrix64F(
                    5, 
                    1, 
                    true, 
                    measurement.getX(),                        // measured x-position
                    measurement.getY(),                        // measured y-position
                    (measurement.getX() - this.getX()) / 30,   // estimated x-velocity
                    (measurement.getY() - this.getY()) / 30,   // estimated y-velocity
                    orientation
                ), 
                this.state.getCovariance()
            )
        );
        // ... and then predict the true state
        super.predict(state);
    }
    
    /**
     * Get the current state mean.
     * @return DenseMatrix64F stateMean
     */
    public DenseMatrix64F getMean() {
        return state.getMean();
    }
    
    /**
     * Get the current state covariance.
     * @return DenseMatrix64F stateCovariance
     */    
    public DenseMatrix64F getCovariance() {
        return state.getCovariance();
    }
    
    /**
     * Returns the X-coordinate.
     * @return double X-position
     */
    public double getX() {
        return state.getMean().get(0, 0);
    }
    
    /**
     * Returns the Y-coordinate.
     * @return double Y-position
     */
    public double getY() {
        return state.getMean().get(1, 0);
    }
    
    /**
     * Returns the X-velocity.
     * @return double X-velocity
     */
    public double getXVelocity() {
        return state.getMean().get(2, 0);
    }
    
    /**
     * Returns the Y-velocity.
     * @return double Y-velocity
     */
    public double getYVelocity() {
        return state.getMean().get(3, 0);
    }
    
    /**
     * Returns the orientation.
     * @return double orientation
     */
    public double getOrientation() {
        return state.getMean().get(4, 0);
    }
}
