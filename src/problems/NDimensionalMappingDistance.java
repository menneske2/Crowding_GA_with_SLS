/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import jsat.linear.Vec;
import jsat.linear.distancemetrics.DistanceMetric;
import smile.math.distance.Distance;

/**
 *
 * @author Fredrik-Oslo
 */
public class NDimensionalMappingDistance implements Distance<double[]>{
	
	private final int dims;
	
	public NDimensionalMappingDistance(int dimensionality){
		dims = dimensionality;
	}

	@Override
	public double d(double[] arg0, double[] arg1) {
		boolean[][] bitstrings = new boolean[2][arg0.length];
		for(int i=0; i<arg0.length; i++){
			bitstrings[0][i] = arg0[i] == 1;
			bitstrings[1][i] = arg1[i] == 1;
		}
		double[][] positions = new double[2][dims];
		for(int i=0; i<bitstrings.length; i++){
			BigInteger[] partitioned = BenchmarkProblem.partitionBitstring(bitstrings[i], dims);
			positions[i] = BenchmarkProblem.normalize(partitioned, arg0.length/dims, 0, 1);
//			positions[i] = BenchmarkProblem.translateToCoordinates(bitstrings[i], dims, ind1.length, 0, 1);
		}
		
		// Calculating euclidean distance.
		double dist = 0;
		for(int i=0; i<dims; i++){
			dist += Math.pow(positions[0][i] - positions[1][i], 2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}
	
}
