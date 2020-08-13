/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class Niche implements Comparable<Niche>{
	
	private final List<GAIndividual> points;
	
	public Niche(List<GAIndividual> gais){
		points = new ArrayList<>();
		for(var gai : gais){
			this.addPoint(gai);
		}
	}
	
	public void addPoint(GAIndividual gai){
		this.points.add(gai);
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
	
	public List<GAIndividual> getPoints(){
		return this.points;
	}

	@Override
	public int compareTo(Niche other) {
		double bestFitHere = this.getBest().fitness;
		double bestFitThere = other.getBest().fitness;
		// high fitness gets low index position.
		return -Double.compare(bestFitHere, bestFitThere);
	}
	
}
