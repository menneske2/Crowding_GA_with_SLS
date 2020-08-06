/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.GAIndividual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import problems.BenchmarkProblem;

/**
 *
 * @author Fredrik-Oslo
 */
public class PerformanceMeasures {
	
	public static int getNumOptimaFound(BenchmarkProblem prob, List<GAIndividual> pop){
		double epsilon = 0.4 * prob.getDimensionality();
		if(prob.optimasInPaper == null)
			return -1;
		int numFound = 0;
		for(double[] optima : prob.optimasInPaper){
			for(var gai : pop){
				double[] gaiCoords = prob.translateToCoordinates(gai.genome);
				// Calculating euclidean distance.
				double dist = 0;
				for(int i=0; i<gaiCoords.length; i++){
					dist += Math.pow(gaiCoords[i] - optima[i], 2);
				}
				dist = Math.sqrt(dist);
				if(dist <= epsilon){
					numFound++;
					break;
				}
			}
		}
		return numFound;
	}
	
	public static List<boolean[]> getOptimaFound(BenchmarkProblem prob, List<GAIndividual> pop){
		double epsilon = 0.4*prob.getDimensionality();
		if(prob.optimasInPaper == null)
			return null;
		List<boolean[]> optima2 = new ArrayList<>();
		for(double[] optima : prob.optimasInPaper){
			for(var gai : pop){
				double[] gaiCoords = prob.translateToCoordinates(gai.genome);
				// Calculating euclidean distance.
				double dist = 0;
				for(int i=0; i<gaiCoords.length; i++){
					dist += Math.pow(gaiCoords[i] - optima[i], 2);
				}
				dist = Math.sqrt(dist);
				if(dist <= epsilon){
					optima2.add(gai.genome);
					break;
				}
			}
		}
		return optima2;
	}
	
	/**
	 * Gets the top n peaks.
	 * @param n the amount of peaks
	 * @param epsilon how far one peak must be from another in order to be considered.
	 * @return 
	 */
	public static double[] nBestSeparatedBy(List<GAIndividual> pop, DistanceMeasure distMeasure, int n, double epsilon){
		if(n > pop.size()) return null;
		pop = new ArrayList<>(pop);
		Collections.sort(pop);
		List<GAIndividual> chosen = new ArrayList<>();

		while(chosen.size() < n){
			boolean found = false;
			for(int i=0; i<pop.size(); i++){
				boolean newPeak = true;
				for(var gai : chosen){
					if(distMeasure.compute(gai.getPoint(), pop.get(i).getPoint()) <= epsilon){
						newPeak = false;
					}
				}
				if(newPeak){
					chosen.add(pop.get(i));
					pop.remove(i);
					found = true;
					break;
				}
			}
			if(!found)
				chosen.add(pop.get(pop.size()-1)); // add the worst.
		}
		
		double[] f = new double[chosen.size()];
		for(int i=0; i<f.length; i++){
			f[i] = chosen.get(i).fitness;
		}
		
		return f;
	}
	
	public static double getPeakRatio(BenchmarkProblem prob, List<GAIndividual> pop){
		double numFound = getNumOptimaFound(prob, pop);
		double max = prob.optimasInPaper.size();
		return numFound/max;
	}
	
	public static double getMean(List<Integer> vals){
		double[] vals2 = new double[vals.size()];
		for(int i=0; i<vals.size(); i++){
			vals2[i] = vals.get(i);
		}
		return getMean(vals2);
	}
	
	public static double getMean(double[] vals){
		double mean = 0;
		for(int i=0; i<vals.length; i++){
			mean += vals[i];
		}
		mean /= vals.length;
		return mean;
	}
	
	public double getSTD(double[] vals){
		StandardDeviation std = new StandardDeviation(false);
		return std.evaluate(vals);
	}
}
