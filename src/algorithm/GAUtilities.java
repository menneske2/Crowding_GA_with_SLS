/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 *
 * @author Fredrik-Oslo
 */
public class GAUtilities {
	
	public static int getHammingDistance(GAIndividual gai1, GAIndividual gai2){
		boolean[] g1 = gai1.genome;
		boolean[] g2 = gai2.genome;
		
		int dist = 0;
		for(int i=0; i<g1.length; i++){
			int val1 = (((Boolean)g1[i]).hashCode() & 0b10) >> 1;
			int val2 = (((Boolean)g2[i]).hashCode() & 0b10) >> 1;
			dist += Math.abs(val1 - val2);
		}
		return dist;
	}
	
	public static double getEntropy(List<GAIndividual> pop){
		int genomeLength = pop.get(0).genome.length;
		int[] genomeSum = sumGenomes(pop);
		double[] probabilities = new double[genomeLength*2];
		for(int i=0; i<genomeLength; i+=2){
			probabilities[i] = ((double) genomeSum[i]) / pop.size(); // probability of 1 in that position
			probabilities[i+1] = ((double) (pop.size()-genomeSum[i])) / pop.size(); // probability of 0 in that position.
		}
		double entropy = 0.0;
		for(double p : probabilities){
			if(p == 0.0)
				continue;
			entropy -= p * (Math.log(p) / Math.log(2)); // p * log2 of p.
		}
		return entropy;
	}
	
	public static int[] sumGenomes(List<GAIndividual> pop){
		int genomeLength = pop.get(0).genome.length;
		int[] genomeSum = new int[genomeLength];
		for(var gai : pop){
			for(int i=0; i<genomeLength; i++){
				Boolean b = gai.genome[i];
				genomeSum[i] += (b.hashCode() & 0b10) >> 1;
			}
		}
		return genomeSum;
	}
	
	public static List<Niche> getNiches(List<GAIndividual> pop){
		DistanceMeasure distanceMeasure = new JaccardDistance();
		double epsilon = 0.15;
		DBSCANClusterer clusterer = new DBSCANClusterer(epsilon, 0, distanceMeasure);
		List<Cluster<GAIndividual>> clusters = clusterer.cluster(pop);
		List<Niche> toReturn = new ArrayList<>();
		for(var cluster : clusters){
			toReturn.add(new Niche(cluster));
		}
		return toReturn;
	}
	
	
}


