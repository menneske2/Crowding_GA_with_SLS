/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import core.Problem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Fredrik-Oslo
 */
public class SLS {
	
	private Problem prob;
	private OptimizerConfig conf;
	private Random rng;
	
	
	public SLS(Problem prob, OptimizerConfig conf, Random rng){
		this.prob = prob;
		this.conf = conf;
		this.rng = rng;
		if(conf.SLS_MAX_FLIPS_IN_GREEDY == -1){
			conf.SLS_MAX_FLIPS_IN_GREEDY = Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Attempts to move the best individual in a niche to the local optima.
	 */
	public double optimizeNiche(Niche niche){
		GAIndividual gai = niche.getBest();
		boolean[] optimized = optimize(gai.genome);
		gai.genome = optimized;
		gai.evaluateFitness();
		return gai.fitness;
	}
	
	/**
	 * Reaches local optimum 100% of the time, but very slow.
	 * Unused as of 3/7
	 * @param gai 
	 */
	public boolean[] exhaustivelyOptimize(boolean[] bits){
		double bestFitness = prob.evaluateBitstring(bits, true);
		while(true){
			double newFitness = greedyStep(bits, bestFitness, true);
			if(newFitness > bestFitness){
				bestFitness = newFitness;
			} else{
				break;
			}
		}
		return bits;
	}
	
	private boolean[] optimize(boolean[] bits){
		bits = Arrays.copyOf(bits, bits.length);
		boolean[] bestBits = Arrays.copyOf(bits, bits.length);
		double bestFitness = prob.evaluateBitstring(bits, true);
		
		double newFitness = 0.0;
		for(int loop=0; loop<conf.SLS_MAX_FLIPS; loop++){
			if(rng.nextFloat() < conf.SLS_P_NOISY){
				newFitness = noisyStep(bits);
			} else{
				newFitness = greedyStep(bits, newFitness, false);
				if(newFitness == -1){
					break; // The local optima has been found.
				}
			}
			if(newFitness > bestFitness){
				bestBits = Arrays.copyOf(bits, bits.length);
				bestFitness = newFitness;
			}
		}
		return bestBits;
	}
	
	private double greedyStep(boolean[] bits, double prevFitness, boolean exhaustive){
		int indicesTried = 0;
		List<Integer> unexploredIndices = new ArrayList<>();
		for(int i=0; i<bits.length; i++){
			unexploredIndices.add(i);
		}
		int bestIndex = -1;
		double bestFitness = prevFitness;
		
		while(true){
			if(unexploredIndices.isEmpty()){
				return -1;
			}
			if(!exhaustive && indicesTried >= conf.SLS_MAX_FLIPS_IN_GREEDY){ // Assume this is the local optima.
				return -1;
			}
			// Randomly picking next bit to try flipping. Can be improved by picking statistically discriminatory bits first.
			Integer index = unexploredIndices.get(rng.nextInt(unexploredIndices.size()));
			unexploredIndices.remove(index);
			
			bits[index] = !bits[index];
			double fitness = prob.evaluateBitstring(bits, true);
			bits[index] = !bits[index];
			indicesTried++;
			if(fitness > bestFitness){
				bestFitness = fitness;
				bestIndex = index;
				if(conf.SLS_TAKE_FIRST_IMPROVEMENT)
					break;
			}
		}
		bits[bestIndex] = !bits[bestIndex];
		return bestFitness;
	}
	
	private double noisyStep(boolean[] bits){
		int index = rng.nextInt(bits.length);
		bits[index] = !bits[index];
		return prob.evaluateBitstring(bits, true);
	}
}
