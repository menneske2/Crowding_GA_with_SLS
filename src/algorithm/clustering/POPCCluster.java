/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.clustering;

import algorithm.GAIndividual;
import algorithm.GAUtilities;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class POPCCluster{
	
	public static final int CM = 1000; // no idea what changing this does, but it was a variable in the paper.
	public static final int POWER = 10;
	
	public List<GAIndividual> members;
	int[] counts;
	
	public POPCCluster(List<GAIndividual> members){
		this.members = members;
		this.counts = GAUtilities.sumGenomes(members);
	}
	
	public double getJVal(int[] totalCounts, int nClusters){
		double J = 0.0;
		for(int i=0; i<counts.length; i++){
			double val = ((double)counts[i] * CM + 1) / ((double)totalCounts[i] * CM + nClusters);
			J += Math.pow(val, POWER);
		}
		return J;
	}
	
	public double deltaIfRemoved(int gaiIndex, int[] totalCounts, int nClusters){
		double delta = 0.0;
		for(int i=0; i<counts.length; i++){
			if(members.get(gaiIndex).genome[i]){
				delta -= Math.pow(((double)counts[i] * CM + 1) / ((double)totalCounts[i] * CM + nClusters), POWER);
				delta += Math.pow((((double)counts[i]-1) * CM + 1) / ((double)totalCounts[i] * CM + nClusters), POWER);
			}
		}
//		System.out.println("removeDelta: " + delta);
		return delta;
	}
	
	public double deltaIfAdded(GAIndividual gai, int[] totalCounts, int nClusters){
		double delta = 0.0;
		for(int i=0; i<counts.length; i++){
			if(gai.genome[i]){
				delta -= Math.pow(((double)counts[i] * CM + 1) / ((double)totalCounts[i] * CM + nClusters), POWER);
				delta += Math.pow((((double)counts[i]+1) * CM + 1) / ((double)totalCounts[i] * CM + nClusters), POWER);
			}
		}
//		System.out.println("addedDelta: " + delta);
		return delta;
	}
	
	public void add(GAIndividual gai){
		for(int i=0; i<gai.genome.length; i++){
			counts[i] += (((Boolean)gai.genome[i]).hashCode() & 0b10) >> 1;
		}
		members.add(gai);
	}
	
	public void remove(int gaiIndex){
		for(int i=0; i<members.get(gaiIndex).genome.length; i++){
			Boolean b = members.get(gaiIndex).genome[i];
			counts[i] -= (b.hashCode() & 0b10) >> 1;
		}
		members.remove(gaiIndex);
	}
	
}
