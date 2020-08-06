/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.math.BigInteger;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 *
 * @author Fredrik-Oslo
 */
public class NDimensionalMappingDistance implements DistanceMeasure{
	
	private final BenchmarkProblem prob;
	private final int dims;
	
	public NDimensionalMappingDistance(BenchmarkProblem prob, int dimensionality){
		this.prob = prob;
		dims = dimensionality;
	}

	@Override
	public double compute(double[] ind1, double[] ind2) throws DimensionMismatchException {
		boolean[][] bitstrings = new boolean[2][ind1.length];
		for(int i=0; i<ind1.length; i++){
			bitstrings[0][i] = ind1[i] == 1;
			bitstrings[1][i] = ind2[i] == 1;
		}
		double[][] positions = new double[2][dims];
		for(int i=0; i<bitstrings.length; i++){
			BigInteger[] partitioned = BenchmarkProblem.partitionBitstring(bitstrings[i], dims);
			positions[i] = BenchmarkProblem.normalize(partitioned, ind1.length/dims, 0, 1);
		}
		
		// Calculating euclidean distance.
		double dist = 0;
		for(int i=0; i<dims; i++){
			dist += Math.pow(positions[0][i] - positions[1][i], 2);
		}
		dist = Math.sqrt(dist);
		return dist;
//		EuclideanDistance dist = new EuclideanDistance();
//		return dist.compute(positions[0], positions[1]);
	}
	
}
