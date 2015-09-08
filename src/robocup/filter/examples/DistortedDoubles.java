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
package robocup.filter.examples;

import robocup.filter.DistortedDoublesUKF;
import java.util.Random;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * Runs a test implementation of a UKF for a three-dimensional system with linear
 * functions and Gaussian noise.
 * 
 * @author Rimon Oz
 */
public class DistortedDoubles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random randomGenerator = new Random();
        
        DenseMatrix64F predictionFunction = new DenseMatrix64F(3, 3, true, 
                1, 0, 0,    // This is the argument
                1, 0, 0,    // This is the argument distorted by gaussian noise
                2, 0, 0     // This is the argument doubled and distorted by gaussian noise
        );
        // We assume the measurement data to be reliable.
        DenseMatrix64F measurementFunction = CommonOps.identity(3);
        
        // Initialize the filter
        DistortedDoublesUKF filter = new DistortedDoublesUKF(
                2,                  // The scaling parameter (lambda)
                predictionFunction, 
                measurementFunction
        );
        
        // Run the filter on the distorted doubles model.
        for (int index = 0; index < 500; index += 1) {
            filter.run(
                new DenseMatrix64F(
                    3, 
                    1, 
                    true, 
                    2*index,                                    // the doubling index
                    2*index + randomGenerator.nextGaussian(),   // the distorted doubling index
                    4*index + 20*randomGenerator.nextGaussian() // the distorted quadrupling index
                )
            );
            // Print the results
            System.out.println(index-1 + " is half of " + filter.getMean().get(0, 0)
                + " which is approximately " + filter.getMean().get(1, 0)
                + " which is half of " + filter.getMean().get(2, 0) + ".");
        }
    }
}
