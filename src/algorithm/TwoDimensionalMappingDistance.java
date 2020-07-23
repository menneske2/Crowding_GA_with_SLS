/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.math.BigInteger;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import statistics.BenchmarkLoader;

/**
 *
 * @author Fredrik-Oslo
 */
public class TwoDimensionalMappingDistance implements DistanceMeasure{

	@Override
	public double compute(double[] ind1, double[] ind2) throws DimensionMismatchException {
		boolean[][] bitstrings = new boolean[2][ind1.length];
		for(int i=0; i<ind1.length; i++){
			bitstrings[0][i] = ind1[i] == 1;
			bitstrings[1][i] = ind2[i] == 1;
		}
		double[][] positions = new double[2][2];
		for(int i=0; i<bitstrings.length; i++){
			BigInteger[] partitioned = BenchmarkLoader.partitionBitstring(bitstrings[i], 2);
			positions[i] = BenchmarkLoader.normalize(partitioned, ind1.length/2, 0, 1);
		}
		EuclideanDistance dist = new EuclideanDistance();
		return dist.compute(positions[0], positions[1]);
	}
	
}
