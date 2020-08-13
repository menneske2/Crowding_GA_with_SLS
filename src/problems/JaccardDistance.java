/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.util.List;
import java.util.concurrent.ExecutorService;
import jsat.linear.Vec;
import jsat.linear.distancemetrics.DistanceMetric;

/**
 *
 * @author Fredrik-Oslo
 */
public class JaccardDistance implements DistanceMetric{

//	@Override
//	/**
//	 * A graph of this distance function for an input variable with 500 features can be found here:
//	 * https://www.wolframalpha.com/input/?i=plot+1-%28x%2F%281000-x%29%29+from+0+to+500
//	 * The function has the same shape regardless of input features, so think of it like a percentage-based similarity function.
//	 */
//	public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
//		int intersection = 0;
//		for(int i=0; i<arg0.length; i++){
//			int int0 = (int) arg0[i];
//			int int1 = (int) arg1[i];
//			intersection += 1 - (int0 ^ int1);
//		}
//		double union = arg0.length * 2 - intersection;
//		return 1 - (intersection/union);
//	}

	@Override
	public double dist(Vec arg0, Vec arg1) {
		int intersection = 0;
		for(int i=0; i<arg0.length(); i++){
			int int0 = (int) arg0.get(i);
			int int1 = (int) arg1.get(i);
			intersection += 1 - (int0 ^ int1);
		}
		double union = arg0.length() * 2 - intersection;
		return 1 - (intersection/union);
	}

	@Override
	public boolean isSymmetric() {
		return true;
	}

	@Override
	public boolean isSubadditive() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isIndiscemible() {
		return true;
	}

	@Override
	public double metricBound() {
		return 1.0;
	}

	@Override
	public boolean supportsAcceleration() {
		return false;
	}

	@Override
	public List<Double> getAccelerationCache(List<? extends Vec> arg0) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Double> getAccelerationCache(List<? extends Vec> arg0, ExecutorService arg1) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public double dist(int arg0, int arg1, List<? extends Vec> arg2, List<Double> arg3) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public double dist(int arg0, Vec arg1, List<? extends Vec> arg2, List<Double> arg3) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Double> getQueryInfo(Vec arg0) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public double dist(int arg0, Vec arg1, List<Double> arg2, List<? extends Vec> arg3, List<Double> arg4) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DistanceMetric clone() {
		try{
			return (DistanceMetric)super.clone();
		} catch(Exception e){
			return null;
		}
	}
	
}
