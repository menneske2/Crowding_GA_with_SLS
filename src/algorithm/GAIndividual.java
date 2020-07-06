/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;


import core.Problem;
import java.util.Random;
import org.apache.commons.math3.ml.clustering.Clusterable;
/**
 *
 * @author Fredrik-Oslo
 */
public class GAIndividual implements Comparable<GAIndividual>, Cloneable, Clusterable{
	
	private final Problem prob;
	private final OptimizerConfig conf;
	private final Random rng;
	public boolean[] genome;
	public double fitness;
	
	public GAIndividual(Problem prob, OptimizerConfig conf, Random rng, boolean[] genome){
		this.prob = prob;
		this.conf = conf;
		this.rng = rng;
		this.genome = genome;
	}
	
	
	public GAIndividual clone(){
		try {
			GAIndividual copy = (GAIndividual) super.clone(); // not deep copying the problem for memory reasons.
			copy.genome = this.genome.clone();
			return copy;
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			System.exit(4);
		}
		return null;
	}
	
	public void evaluateFitness(){
		double RMeasure = prob.evaluateBitstring(genome, true);
		this.fitness = RMeasure;
	}
	

	public String getGenomeAsString(){
		String s = "";
		for(Boolean b : genome){
			s += (b.hashCode() & 0b10) >> 1;
		}
		return s;
	}
	
	public int numberOfFeatures(){
		int num = 0;
		for(Boolean b : genome){
			num += (b.hashCode() & 0b10) >> 1;
		}
		return num;
	}
	
	@Override
	/**
	 * Sorts in descending order. The highest fitness individual will be at the 0th index position.
	 */
	public int compareTo(GAIndividual other) {
		if(this.fitness == other.fitness)
			return 0;
		return this.fitness > other.fitness ? -1 : 1;
	}

	
	@Override
	/**
	 * Method used by the clustering algorithm.
	 */
	public double[] getPoint() {
		// cast the genome into a double[].
		double[] casted = new double[genome.length];
		for(int i=0; i<genome.length; i++){
			Boolean b = genome[i];
			casted[i] = (b.hashCode() & 0b10) >> 1;
		}
		return casted;
	}
}
