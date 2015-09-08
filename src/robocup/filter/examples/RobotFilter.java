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

import robocup.filter.RobotUKF;
import java.util.Random;
import robocup.model.FieldPoint;

/**
 * Example use of implementation of UKF for a robot on a quadratic path with
 * measurement noise.
 * 
 * @author Rimon Oz
 */
public class RobotFilter {
    
    /**
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Random randomGenerator = new Random();
        
        FieldPoint initialPosition = new FieldPoint(0, 0);
        
        // Initialize the filter.
        RobotUKF filter = new RobotUKF(initialPosition);
        
        // Generate a distorted quadratic path and print the coordinates.
        for (int index = 1; index < 500; index += 1) {
            
            filter.run(
                new FieldPoint(
                    index, 
                    (float) (index*index + randomGenerator.nextGaussian())),
                Math.cos(index)
            );
            System.out.println(index-1 + ": " + filter.getX() + ", " + filter.getY() 
                    + ". Difference between true value is " 
                    + (filter.getX()*filter.getX()-filter.getY()) + ".");
        }
    }
}
