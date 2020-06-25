/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.clustering;

import algorithm.GAIndividual;
import algorithm.GAUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.evaluation.SumOfClusterVariances;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

/**
 *
 * @author Fredrik-Oslo
 */
public class POPCAlgorithm {
	
	
	/**
	 * Finds the number of clusters/niches in the population.
	 */
	public static int getNumClusters(List<GAIndividual> pop, Random rng){
		// Initializing the algorithm with a large amount of clusters.
		List<POPCCluster> clusters = new ArrayList<>();
		for(var cluster : kMeansClustering(pop, pop.size()*2/3, rng)){
			if(cluster.getPoints().isEmpty()) continue;
			POPCCluster popcCluster = new POPCCluster(cluster.getPoints());
			clusters.add(popcCluster);
		}
		
		int[] totalCounts = GAUtilities.sumGenomes(pop);
		
		// Computing initial J-value. I'm pretty sure this is useless for practical affairs, try commenting it out after you're done with the algorithm.
		double totJ = 0.0;
		for(var c : clusters){
			totJ += c.getJVal(totalCounts, clusters.size());
		}
		
		totJ = getTotalJVal(clusters, totalCounts);
		
		List<Double> JEvolution = new ArrayList<>();
		JEvolution.add(totJ);
		
		// The main loop of the algorithm.
		boolean changed = true;
		while(changed){
			changed = false;
			for(int i=0; i<clusters.size(); i++){
				for(int j=0; j<clusters.get(i).members.size(); j++){
					int largestGainWhere = -1;
					double largestGain = Double.NEGATIVE_INFINITY;
					double deltaBase = clusters.get(i).deltaIfRemoved(j, totalCounts, clusters.size());
					
					for(int k=0; k<clusters.size(); k++){
						if(i != k){
							double delta = deltaBase + clusters.get(k).deltaIfAdded(clusters.get(i).members.get(j), totalCounts, clusters.size());
							if(delta > largestGain){
								largestGain = delta;
								largestGainWhere = k;
							}
						}
					}
					if(largestGain > 0.0){
						GAIndividual gai = clusters.get(i).members.get(j);
						clusters.get(i).remove(j);
						clusters.get(largestGainWhere).add(gai);
						j--;
						changed = true;
					}
				}
				if(clusters.get(i).members.isEmpty()){
					clusters.remove(i);
					i--;
				}
			}
		}
		
//		System.out.println("Clusters: " + clusters.size());

		return clusters.size();
	}
	
	private static double getTotalJVal(List<POPCCluster> clusters, int[] totalCounts){
		double totJ = 0.0;
		for(var c : clusters){
			totJ += c.getJVal(totalCounts, clusters.size());
		}
		return totJ;
	}
	
	
	private static List<CentroidCluster<GAIndividual>> kMeansClustering(List<GAIndividual> pop, int k, Random rng){
		DistanceMeasure distanceMeasure = new ManhattanDistance();
		KMeansPlusPlusClusterer<GAIndividual> kmeans = new KMeansPlusPlusClusterer<>(k, 20, distanceMeasure);
		kmeans.getRandomGenerator().setSeed(rng.nextInt());
		MultiKMeansPlusPlusClusterer<GAIndividual> clusterer = new MultiKMeansPlusPlusClusterer<>(kmeans, 3, new SumOfClusterVariances<>(distanceMeasure));
		return clusterer.cluster(pop);
	}
}
