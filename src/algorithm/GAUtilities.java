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
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.evaluation.SumOfClusterVariances;
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
	public static int getNumClusters(List<Clusterable> pop, Random rng){
		double expansionPunishmentCoefficient = 0.09; // The lower this is, the more likely this function is to give you higher cluster counts.
		
		// Gathering statistics for k-means for k = {1, ...20}
		List<Double> dists = new ArrayList<>();
		List<Double> silhouettes = new ArrayList<>();
		for(int i=1; i<=50; i++){
			double[] measures = kMeansClustering(pop, i, rng);
			dists.add(measures[0]);
			silhouettes.add(measures[1]);
		}
		
		// Normalizing distance measure to [0, 1]
		double highest = dists.get(0);
		for(int i=0; i<dists.size(); i++){
			dists.set(i, dists.get(i)/highest);
		}
		
		// Finding elbow.
		int elbowIndex = -1;
		// Initializing elbow with the first value that has a higher silhouette value than distance. just in case no actual elbow is found.
		for(int i=0; i<dists.size(); i++){
			if(silhouettes.get(i) > dists.get(i)){
				elbowIndex = i;
				break;
			}
		}

		List<Integer> elbowIndices = new ArrayList<>();
		boolean foundFirstElbow = false;
		for(int i=1; i<dists.size()-1; i++){
			double dBack = dists.get(i) - dists.get(i-1);
			double dForward = dists.get(i+1) - dists.get(i);
			if(silhouettes.get(i) > dists.get(i)){
				boolean potentialElbow = false;
				double diff = Math.abs(dBack-dForward);
				if(Math.abs(dForward/dBack) < 0.7 && diff > 0.02){
					potentialElbow = true;
				}
				if(dForward > 0.01){
					potentialElbow = true;
				}
				if(dists.get(i) < 0.8 * dists.get(i-1) && diff > 0.02){ // endre til 0.03?
					potentialElbow = true;
				}
					
				if(potentialElbow){
					if(!foundFirstElbow){
						elbowIndex = i;
						foundFirstElbow = true;
					}
					elbowIndices.add(i);
				}
			}
		}
		
		int bestIndex = elbowIndex;
		double bestScore = silhouettes.get(elbowIndex);
		for(int i=0; i<elbowIndices.size(); i++){
			boolean newBest = false;
			int ind = elbowIndices.get(i);
			int indexDiff = ind - bestIndex;
			double improvementRatio = silhouettes.get(ind) / bestScore;
			double distImprovement = 1 + dists.get(bestIndex) - dists.get(ind);
			double requiredRatio = Math.log(Math.E + expansionPunishmentCoefficient*indexDiff);
			
			if(improvementRatio * distImprovement > requiredRatio){
				newBest = true;
			}
//			System.out.print((ind+1));
//			if(newBest){
//				System.out.print(" CONQUEORS " + (bestIndex+1));
//			} else{
//				System.out.print(" is worse than " + (bestIndex+1));
//			}
//			System.out.format(". improvementRatio = %.3f. distImpro = %.3f. Combined = %.3f. Needed %.3f. Dists: %.3f and %.3f\n", improvementRatio, distImprovement, improvementRatio * distImprovement, requiredRatio, dists.get(ind), dists.get(bestIndex));
			if(newBest){
				bestScore = silhouettes.get(ind);
				bestIndex = ind;
			}
		}
		
//		System.out.format("Detected %d clusters. %d potential elbows\n", bestIndex+1, elbowIndices.size());
		return bestIndex+1;
	}
	
	
	private static double[] kMeansClustering(List<Clusterable> pop, int k, Random rng){
		DistanceMeasure distanceMeasure = new ManhattanDistance();
		KMeansPlusPlusClusterer<Clusterable> kmeans = new KMeansPlusPlusClusterer<>(k, 20, distanceMeasure);
		kmeans.getRandomGenerator().setSeed(rng.nextInt());
		MultiKMeansPlusPlusClusterer<Clusterable> clusterer = new MultiKMeansPlusPlusClusterer<>(kmeans, 2, new SumOfClusterVariances<>(distanceMeasure));
		List<CentroidCluster<Clusterable>> clusters = clusterer.cluster(pop);
		
		// Statistical measures.
		double sumSquareDist = sumSquareDistance(clusters, distanceMeasure);
		double silhouetteVal = silhouetteValue(clusters, distanceMeasure);
		return new double[]{sumSquareDist, silhouetteVal};
	}
	
	private static double sumSquareDistance(List<CentroidCluster<Clusterable>> clusters, DistanceMeasure distanceMeasure){
		double sumSquareDist = 0;
		int nMembers = 0;
		for(CentroidCluster<Clusterable> cluster : clusters){
			nMembers += cluster.getPoints().size();
			Clusterable center = cluster.getCenter();
			for(Clusterable point : cluster.getPoints()){
				double d = distanceMeasure.compute(center.getPoint(), point.getPoint());
				sumSquareDist += Math.pow(d, 2);
			}
		}
		return sumSquareDist;
	}
	
	private static double silhouetteValue(List<CentroidCluster<Clusterable>> clusters, DistanceMeasure distanceMeasure){
		double totSi = 0.0;
		int nMembers = 0;
		int num = 0;
		if(clusters.size() == 1) // Silhouette value is undefined for k=1.
			return 0;
		for(CentroidCluster<Clusterable> cluster : clusters){
			nMembers += cluster.getPoints().size();
			if(cluster.getPoints().size() == 1){
				num++;
				continue;
			}
			for(Clusterable p1 : cluster.getPoints()){
				double similarity = 0.0;
				for(Clusterable p2 : cluster.getPoints()){
					if(p1 == p2) continue;
					similarity += distanceMeasure.compute(p1.getPoint(), p2.getPoint());
				}
				
				similarity /= cluster.getPoints().size() - 1;
				double dissimilarity = Double.MAX_VALUE;
				for(CentroidCluster<Clusterable> cluster2 : clusters){
					if(cluster2 == cluster) continue;
					if(cluster2.getPoints().isEmpty()) continue;
					double dist = 0.0;
					for(Clusterable p2 : cluster2.getPoints()){
						dist += distanceMeasure.compute(p1.getPoint(), p2.getPoint());
					}
					dist /= cluster2.getPoints().size();
					dissimilarity = Math.min(dissimilarity, dist);
				}
				double si = dissimilarity - similarity;
				si /= Math.max(dissimilarity, similarity);
				totSi += si;
				num++;
			}
		}
		
		return totSi/nMembers;
	}
	
}


