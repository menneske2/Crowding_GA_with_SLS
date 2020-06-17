/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

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
		int[] genomeSum = new int[genomeLength];
		for(var gai : pop){
			for(int i=0; i<genomeLength; i++){
				Boolean b = gai.genome[i];
				genomeSum[i] += (b.hashCode() & 0b10) >> 1;
			}
		}
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
	
	/**
	 * Finds the number of clusters/niches in the population.
	 * @param estimate The amount of clusters shouldn't change too much over time, so a good estimate will speed ut the process. -1 = no estimate.
	 */
	public static int getNumClusters(List<GAIndividual> pop, int estimate, Random rng){
		List<Double> dists = new ArrayList<>();
		for(int i=1; i<20; i++){
			double dist = kMeansClustering(pop, i, rng);
			dist = Math.round(dist*10)/10.0;
			dists.add(dist);
		}
		System.out.println(dists);
		return 0;
	}
	
	/**
	 * returns the mean distance between data points and their respective centroids.
	 */
	private static double kMeansClustering(List<GAIndividual> pop, int k, Random rng){
		DistanceMeasure distanceMeasure = new ManhattanDistance();
		KMeansPlusPlusClusterer<GAIndividual> clusterer = new KMeansPlusPlusClusterer<>(k, 2, distanceMeasure);
		clusterer.getRandomGenerator().setSeed(rng.nextInt());
		List<CentroidCluster<GAIndividual>> clusters = clusterer.cluster(pop);
		
		double dist = 0;
		for(CentroidCluster<GAIndividual> cluster : clusters){
			Clusterable center = cluster.getCenter();
			for(GAIndividual point : cluster.getPoints()){
				dist += distanceMeasure.compute(center.getPoint(), point.getPoint());
			}
		}
		dist /= pop.size();
		return dist;
	}
}
