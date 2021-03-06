/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import problems.Problem;

/**
 *
 * @author Fredrik-Oslo
 */
public class GeneticAlgorithm implements Runnable{
	
	protected final Problem prob;
	protected final OptimizerConfig conf;
	protected final DataReceiver feedbackStation;
	protected final BlockingQueue comsChannel;

	protected final Random rng;
	protected List<GAIndividual> population;
	protected final SLS sls;
	private final PIDController controller;
	
	public GeneticAlgorithm(Problem prob, OptimizerConfig conf, DataReceiver feedbackStation, BlockingQueue comsChannel){
		this.prob = prob;
		this.conf = conf;
		this.feedbackStation = feedbackStation;
		this.comsChannel = comsChannel;
		rng = new Random();
		if(conf.SEED != OptimizerConfig.NO_SEED)
			rng.setSeed(conf.SEED);
		controller = new PIDController();
		sls = new SLS(prob, conf, rng);
	}
	
	@Override
	public void run() {
		Date startTime = new Date();
		prob.fitnessEvaluations = 0;
		
		initPopulation();
		
		int generation = 0;
		
		// Sending initialization statistics.
		sendProgressReport(generation, GAUtilities.getNiches(population, prob, conf.NICHING_EPSILON));
		
		while(true){
			if(shouldTerminate(generation))
				break;

			// A clustering algorithm partitions the population into clusters.
			List<Niche> niches = GAUtilities.getNiches(population, prob, conf.NICHING_EPSILON);

			// Feedback controller
			if(conf.FC_ENABLED)
				controller.updateCrowdingScalingFactor(conf, niches.size());

			// lazySLS
			if(conf.SLS_ENABLED){
				for(var niche : niches){
					sls.optimizeNiche(niche);
					if(conf.CULLING_ENABLED){
						purgeExcessInNiche(niche);
					}
				}
			}
			
			// Elitist survival of the best individual in all active niches.
			List<GAIndividual> elitist = new ArrayList<>();
			Collections.sort(niches);
			for(int i=0; i<Math.min(niches.size(), conf.ELITIST_NICHES); i++){
				elitist.add(niches.get(i).getBest().clone());
			}
			
			// EA + replacement scheme.
			List<GAIndividual> newGen = newEAGeneration(elitist);
			population = newGen;
			Collections.sort(population);
			
			generation++;
			
			// Gathering statistics and sending progress report to main client.
			sendProgressReport(generation, niches);
		}
		

		long timeTaken = new Date().getTime() - startTime.getTime();

		if(feedbackStation.requiresWaiting()){
			Platform.runLater(()->{
				feedbackStation.registerSolution(prob, population, timeTaken);
			});
		} else{
			feedbackStation.registerSolution(prob, population, timeTaken);
		}
		
	}
	
	private boolean shouldTerminate(int generation){
		if(!comsChannel.isEmpty()){ // stop-button implementation.
			System.out.println(comsChannel.poll());
			return true;
		}
		if(conf.GENERATIONS != -1 && generation>=conf.GENERATIONS){
			return true;
		}
		if(conf.FITNESS_EVALUATIONS != -1 && prob.fitnessEvaluations >= conf.FITNESS_EVALUATIONS){
			return true;
		}
		return false;
	}
	
	private void sendProgressReport(int generation, List<Niche> niches){
		final int genCopy = generation; // it needs to be an effectively final variable.
		double bestR = prob.evaluateBitstring(population.get(0).genome, false);
		double avgR = 0;
		for(var p : population){
			avgR += prob.evaluateBitstring(p.genome, false);
		}
		avgR /= population.size();
		final double avg = avgR;

		double entropy = GAUtilities.getEntropy(population);
		float crowdingFactor = conf.CROWDING_SCALING_FACTOR;
		int nNiches = niches.size();

		List<GAIndividual> popCopy = new ArrayList<>(population);

		if(feedbackStation.requiresWaiting()){
			Platform.runLater(()->{
				feedbackStation.progressReport(genCopy, prob.fitnessEvaluations, popCopy , bestR, avg, entropy, nNiches, 
						conf.MUTATION_CHANCE, conf.CROSSOVER_CHANCE, crowdingFactor);
			});
		} else{
			feedbackStation.progressReport(genCopy, prob.fitnessEvaluations, popCopy , bestR, avg, entropy, nNiches, 
					conf.MUTATION_CHANCE, conf.CROSSOVER_CHANCE, crowdingFactor);
		}
	}
	
	private void purgeExcessInNiche(Niche niche){
		List<GAIndividual> gais = niche.getPoints();
		Collections.sort(gais);
		for(int i=conf.MAX_NICHE_SIZE; i<gais.size(); i++){
			population.remove(gais.get(i));
		}
	}
	
	private List<GAIndividual> newEAGeneration(List<GAIndividual> elitist){
		
		List<GAIndividual> newPop = new ArrayList<>();
		if(elitist != null) newPop = new ArrayList<>(elitist);
		
		int genomeLength = population.get(0).genome.length;
		
		while(newPop.size() < conf.POPULATION_SIZE){

			
			GAIndividual[] parents = new GAIndividual[2];
			
			// Deterministic tournament selection of parents.
			for(int i=0; i<parents.length; i++){
				GAIndividual[] bracket = new GAIndividual[conf.TOURNAMENT_SIZE];
				for(int j=0; j<bracket.length; j++){
					bracket[j] = population.get(rng.nextInt(population.size()));
				}
				Arrays.sort(bracket);
				parents[i] = bracket[0];
			}
			
			// Most similar/dissimilar parent selection. Appears to be trash.
//			parents[0] = population.get(rng.nextInt(population.size()));
//			GAIndividual[] others = new GAIndividual[conf.TOURNAMENT_SIZE];
//			for(int i=0; i<others.length; i++){
//				others[i] = population.get(rng.nextInt(population.size()));
//			}
//			Comparator comp = new DistanceComparator(parents[0]);
//			if(false) // hvis true, most dissimilar.
//				comp = comp.reversed();
//			Arrays.sort(others, comp);
//			parents[1] = others[0];
			
			
			boolean[][] childGenomes = new boolean[2][genomeLength];
			
			// single point crossover.
			if(rng.nextFloat() < conf.CROSSOVER_CHANCE){
				int point = rng.nextInt(genomeLength);
				for(int i=0; i<point; i++){
					childGenomes[0][i] = parents[1].genome[i];
					childGenomes[1][i] = parents[0].genome[i];
				}
				for(int i=point; i<genomeLength; i++){
					childGenomes[0][i] = parents[0].genome[i];
					childGenomes[1][i] = parents[1].genome[i];
				}
			} else{
				childGenomes[0] = parents[0].genome.clone();
				childGenomes[1] = parents[1].genome.clone();
			}
			
			if(conf.BITFLIP_MUTATION){
				for(int i=0; i<2; i++){
					// Bitflip-mutation.
					if(rng.nextFloat() < conf.MUTATION_CHANCE) {
						int point = rng.nextInt(genomeLength);
						childGenomes[i][point] = !childGenomes[i][point];
					}
				}
			} else{
				for(int i=0; i<2; i++){
					// "Turn off 10 random features"-mutation.
					if(rng.nextFloat() < conf.MUTATION_CHANCE) {
						for(int j=0; j<10; j++){
							int point = rng.nextInt(genomeLength);
							childGenomes[i][point] = false;
						}

					}
				}
			}
			
			// Creating the actual offspring.
			GAIndividual[] children = new GAIndividual[2];
			for(int i=0; i<2; i++){
				GAIndividual gai = new GAIndividual(prob, childGenomes[i]);
				gai.evaluateFitness();
				children[i] = gai;
			}
			
			
			// Crowding implementation.
			int[] distances = new int[4]; // 0 is d00, 1 is d01, 2 is d10, 3 is d11. first number after 'd' is child index. second is parent index.
			for(int c=0; c<2; c++){
				GAIndividual child = children[c];
				for(int p=0; p<2; p++){
					GAIndividual parent = parents[p];
					distances[p*2+c] = GAUtilities.getHammingDistance(child, parent);
				}
			}
			int opposition = 1;
			if(distances[0] + distances[3] <= distances[1] + distances[2]){
				// c1 competes with p1 and c2 competes with p2.
				opposition = 0;
			}
			for(int i=0; i<2; i++){
				GAIndividual c = children[i];
				GAIndividual p = parents[(i+opposition)%2];
				double pc = 0.5;
				if(c.fitness > p.fitness){
					pc = c.fitness / (c.fitness + conf.CROWDING_SCALING_FACTOR * p.fitness);
				} else if(c.fitness < p.fitness){
					pc = (conf.CROWDING_SCALING_FACTOR * c.fitness) / (conf.CROWDING_SCALING_FACTOR * c.fitness + p.fitness);
				}
				if(rng.nextDouble() < pc){
					newPop.add(c);
				} else{
					newPop.add(p.clone());
				}
			}
		}
		
		return newPop;
	}
	
	private void initPopulation(){
		population = new ArrayList<>();
		
		// Random initialization
		for(int person=0; person<conf.POPULATION_SIZE; person++){
			boolean[] chromosome = new boolean[prob.numFeatures];
			for(int i=0; i<chromosome.length; i++){
				chromosome[i] = rng.nextBoolean();
			}
			GAIndividual newborn = new GAIndividual(prob, chromosome);
			newborn.evaluateFitness();
			population.add(newborn);
		}
		Collections.sort(population);
	}
}
