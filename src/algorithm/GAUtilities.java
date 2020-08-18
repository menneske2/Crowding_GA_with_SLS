/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import problems.BenchmarkProblem;
import problems.Problem;
import smile.clustering.MEC;
import smile.clustering.PartitionClustering;
import smile.math.distance.EuclideanDistance;

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
	
	public static int countFeatures(boolean[] bits){
		int nFeatures = 0;
		for(Boolean b : bits){
			nFeatures += (b.hashCode() & 0b10) >> 1;
		}
		return nFeatures;
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
	
//	public static double[] bitsToDoubles(boolean[] bits){
//		double[] da = new double[bits.length];
//		for(int i=0; i<bits.length; i++){
//			da[i] = bits[i] ? 1 : 0;
//		}
//		return da;
//	}
	
	public static boolean[] doublesToBits(double[] da){
		boolean[] bits = new boolean[da.length];
		for(int i=0; i<da.length; i++){
			bits[i] = da[i]==1;
		}
		return bits;
	}
	
	private static DataSet popToDataSet(List<GAIndividual> pop){
		List<DataPoint> data = new ArrayList<>();
		for(var gai : pop){
			Vec vector = new DenseVector(gai.getPoint());
			data.add(new DataPoint(vector));
		}
		return new SimpleDataSet(data);
	}
	
	private static List<GAIndividual> pointsToGais(List<DataPoint> points, List<GAIndividual> pop){
		List<GAIndividual> toReturn = new ArrayList<>();
		List<GAIndividual> popCopy = new ArrayList<>(pop);
		for(int i=0; i<points.size(); i++){
			boolean[] bits = doublesToBits(points.get(i).getNumericalValues().arrayCopy());
			for(var gai : popCopy){
				if(Arrays.equals(bits, gai.genome)){
					toReturn.add(gai);
					popCopy.remove(gai);
					break;
				}
			}
		}
		return toReturn;
	}
	
	
	public synchronized static List<Niche> getNiches(List<GAIndividual> pop, Problem prob){
		double[][] data = popToDoubles(pop, prob);
		PartitionClustering clusterer = MEC.fit(data, new EuclideanDistance(), 75, 0.02);
		
		int[] labels = clusterer.y;
		List<Niche> niches = new ArrayList<>();
		for(int i=0; i<=Arrays.stream(labels).max().getAsInt(); i++)
			niches.add(new Niche());
		
		for(int i=0; i<labels.length; i++){
			niches.get(labels[i]).addPoint(pop.get(i));
		}
		
		for(int i=niches.size()-1; i>0; i--){
			if(niches.get(i).getPoints().isEmpty()){
				niches.remove(i);
				i++;
			}
		}
		
		return niches;
	}
	
	public static double[][] popToDoubles(List<GAIndividual> pop, Problem prob){
		if(prob.getClass() != BenchmarkProblem.class)
			return null;
		BenchmarkProblem p = (BenchmarkProblem) prob;
		double[][] daa = new double[pop.size()][p.getDimensionality()];
		for(int i=0; i<pop.size(); i++){
			daa[i] = p.translateToCoordinates(pop.get(i).genome, 0, 1);
		}
		return daa;
	}
	
	
}


