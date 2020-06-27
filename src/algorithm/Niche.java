/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import org.apache.commons.math3.ml.clustering.Cluster;

/**
 *
 * @author Fredrik-Oslo
 */
public class Niche extends Cluster<GAIndividual> implements Comparable<Niche>{
	
	public Niche(Cluster<GAIndividual> cluster){
		super();
		for(var gai : cluster.getPoints()){
			this.addPoint(gai);
		}
	}
	
	public GAIndividual getBest(){
		double bestFitness = Double.NEGATIVE_INFINITY;
		GAIndividual best = null;
		for(var gai : this.getPoints()){
			if(gai.fitness > bestFitness){
				bestFitness = gai.fitness;
				best = gai;
			}
		}
		return best;
	}

	@Override
	public int compareTo(Niche other) {
		double bestFitHere = this.getBest().fitness;
		double bestFitThere = other.getBest().fitness;
		// high fitness gets low index position.
		return -Double.compare(bestFitHere, bestFitThere);
	}
	
}
