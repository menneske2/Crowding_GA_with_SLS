/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import problems.JaccardDistance;
import java.util.Comparator;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 * This class is only used for most similar / most dissimilar parent selection.
 * Those modes appear to be trash, so delete this when you're sure that they're trash.
 * @author Fredrik-Oslo
 */
public class DistanceComparator implements Comparator<GAIndividual>{
	
	private final double[] comparisonPoint;
	private final DistanceMeasure distanceMeasure;
	
	public DistanceComparator(GAIndividual original){
		comparisonPoint = original.getPoint();
		this.distanceMeasure = new JaccardDistance();
	}

	@Override
	public int compare(GAIndividual arg0, GAIndividual arg1) {
		double dist0 = distanceMeasure.compute(comparisonPoint, arg0.getPoint());
		double dist1 = distanceMeasure.compute(comparisonPoint, arg1.getPoint());
		
		// Most similar will have low index position.
		if(dist0 == dist1)
			return 0;
		return dist0>dist1 ? 1 : -1;
	}
	
}
