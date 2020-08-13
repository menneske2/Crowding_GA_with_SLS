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

/**
 *
 * @author Fredrik-Oslo
 */
public class NDimensionalMappingDistance implements DistanceMetric{
	
	private final int dims;
	
	public NDimensionalMappingDistance(int dimensionality){
		dims = dimensionality;
	}

	@Override
	public double dist(Vec arg0, Vec arg1) {
		double[] ind1 = arg0.arrayCopy();
		double[] ind2 = arg1.arrayCopy();
		
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
	}

	@Override
	public boolean isSymmetric() {
		return true;
	}

	@Override
	public boolean isSubadditive() {
		return true;
	}

	@Override
	public boolean isIndiscemible() {
		return true;
	}

	@Override
	public double metricBound() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public boolean supportsAcceleration() {
		return false;
	}

	@Override
	public List<Double> getAccelerationCache(List<? extends Vec> arg0) {
		return null;
	}

	@Override
	public List<Double> getAccelerationCache(List<? extends Vec> arg0, ExecutorService arg1) {
		return null;
	}

	@Override
	public double dist(int a, int b, List<? extends Vec> vecs, List<Double> cache) {
		return this.dist(vecs.get(a), vecs.get(b));
	}

	@Override
	public double dist(int arg0, Vec arg1, List<? extends Vec> arg2, List<Double> arg3) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Double> getQueryInfo(Vec arg0) {
		return null;
	}

	@Override
	public double dist(int a, Vec b, List<Double> qi, List<? extends Vec> vecs, List<Double> arg4) {
		return dist(vecs.get(a), b);
	}

	@Override
	public DistanceMetric clone() {
		try{
			return (NDimensionalMappingDistance) super.clone();
		} catch(Exception e){
			return null;
		}
	}
	
}
