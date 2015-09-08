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
package robocup.model;

import org.ejml.data.DenseMatrix64F;

/**
 * Model for a given state containing a mean and covariance matrix.
 * 
 * @author Rimon Oz
 */
public class StateModel {
    private final DenseMatrix64F mean;
    private final DenseMatrix64F covariance;
    
    /**
     * Constructor method.
     * @param mean
     * @param covariance
     */
    public StateModel(DenseMatrix64F mean, DenseMatrix64F covariance) {
        this.mean       = new DenseMatrix64F(mean);
        this.covariance = new DenseMatrix64F(covariance);
    }
    
    /**
     * 
     * @return
     */
    public DenseMatrix64F getMean() {
        return mean;
    }

    /**
     *
     * @return
     */
    public DenseMatrix64F getCovariance() {
        return covariance;
    }
}
