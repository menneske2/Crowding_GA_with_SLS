/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 *
 * @author Fredrik-Oslo
 */
public class JaccardDistance implements DistanceMeasure{

	@Override
	public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
		int intersection = 0;
		for(int i=0; i<arg0.length; i++){
			int int0 = (int) arg0[i];
			int int1 = (int) arg1[i];
			intersection += 1 - (int0 ^ int1);
		}
		double union = arg0.length * 2 - intersection;
		return 1 - (intersection/union);
	}
	
}
