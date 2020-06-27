/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import core.Problem;
import core.SolverController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import org.apache.commons.math3.ml.clustering.Cluster;

/**
 *
 * @author Fredrik-Oslo
 */
public class GeneticAlgorithm implements Runnable{
	
	protected final Problem prob;
	protected final OptimizerConfig conf;
	protected final SolverController feedbackStation;
	protected final BlockingQueue comsChannel;

	protected final Random rng;
	protected List<GAIndividual> population;
	protected final SLS sls;
	private final PIDController controller;
	
	public GeneticAlgorithm(Problem prob, OptimizerConfig conf, SolverController feedbackStation, BlockingQueue comsChannel){
		this.prob = prob;
		this.conf = conf;
		this.feedbackStation = feedbackStation;
		this.comsChannel = comsChannel;
		rng = new Random();
		if(conf.SEED != OptimizerConfig.NO_SEED)
			rng.setSeed(conf.SEED);
		controller = new PIDController();
		if(conf.PID_ENABLED == false)
			controller.disable();
		sls = new SLS(prob, conf, rng);
	}
	
	@Override
	public void run() {
		Date startTime = new Date();
		prob.fitnessEvaluations = 0;
		
		initPopulation();
		
		int generation = 0;
		while(true){
			// Termination criterias
			if(!comsChannel.isEmpty()){ // stop-button implementation.
				System.out.println(comsChannel.poll());
				break;
			}
			if(conf.GENERATIONS != -1 && generation>=conf.GENERATIONS){
				break;
			}

			
			// Replacement scheme
			List<GAIndividual> newGen = newEAGeneration();
			population = newGen;
			Collections.sort(population);
			
			List<Niche> niches = GAUtilities.getNiches(population);
			System.out.format("Number of niches: %d. Fitnesses: [", niches.size());
			for(var niche : niches){
				System.out.format("%.3f, ", niche.getBest().fitness);
			}
			System.out.println("]");
			
			
			double nNichesSmoothed = controller.updateCrowdingScalingFactor(conf, niches);
			
			if(conf.SLS_Enabled){
				SLSPurge(niches);
			}
			
			
			generation++;
			
			
			// Gathering statistics and sending progress report to main client.
			final int genCopy = generation-1; // it needs to be an effectively final variable.
			double avg = 0;
			for(var p : population){
				avg += p.fitness;
			}
			avg /= population.size();
			final double avg2 = avg;
			GAIndividual best = population.get(0);
			double entropy = GAUtilities.getEntropy(population);

			
			Platform.runLater(()->{
				feedbackStation.progressReport(genCopy, prob.fitnessEvaluations, best.fitness, avg2, entropy, nNichesSmoothed, sls.getNImprovementsOverX());
			});
		}


		long timeTaken = new Date().getTime() - startTime.getTime();

		Platform.runLater(()->{
			feedbackStation.registerSolution(prob, population.get(0), timeTaken);
		});
	}
	
	private void SLSPurge(List<Niche> niches){
		sls.resetImprovementStats();
		if(niches.size() > conf.ACTIVE_NICHES){
			long preTime = new Date().getTime();
			double[] improvements = new double[niches.size()];
			for(int i=0; i<niches.size(); i++){
				// reinitialize by picking a good niche and chain-mutating? 2 good niches and crossover?
				Niche chosen = niches.get(i);
				double pre = chosen.getBest().fitness;
				improvements[i] = sls.optimizeNiche(chosen) - pre;
			}
			long timeSpent = new Date().getTime() - preTime;
			System.out.println("SLS-ing all "+niches.size()+" niches took " + (int)Math.floor(timeSpent/(1000*60)) + "m" + (timeSpent/1000)%60 + "s. Improvements over "+sls.x+": " + sls.getNImprovementsOverX());
			System.out.print("Improvements: [");
			for(var d : improvements)
				System.out.format("%.3f, ", d);
			System.out.println("]");

			// Committing genocide in all niches. The elite ones get 1 survivor.
			Collections.sort(niches);
			for(int i=0; i<niches.size(); i++){
				int start = i >= conf.ACTIVE_NICHES ? 0 : 1;
				for(int j=start; j<niches.get(i).getPoints().size(); j++){
					var gai = niches.get(i).getPoints().get(j);
					population.remove(gai);
				}
			}
		}
	}
	
	private List<GAIndividual> newEAGeneration(){
		List<GAIndividual> newPop = new ArrayList<>();
		int genomeLength = population.get(0).genome.length;
		
		for(int i=0; i<conf.ELITIST_POPULATION; i++){
			newPop.add(population.get(i).clone());
		}
		
		while(newPop.size() < conf.POPULATION_SIZE){
			// Parent selection
			GAIndividual[] parents = new GAIndividual[2];
			for(int i=0; i<parents.length; i++) {
				parents[i] = population.get(rng.nextInt(population.size()));
			}
			
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
			
			// Bitflip mutation.
			for (boolean[] genome : childGenomes) {
				if (rng.nextFloat() < conf.MUTATION_CHANCE) {
					int point = rng.nextInt(genomeLength);
					genome[point] = !genome[point];
				}
			}
			
			// Creating the actual offspring.
			GAIndividual[] children = new GAIndividual[2];
			for(int i=0; i<2; i++){
				GAIndividual gai = new GAIndividual(prob, conf, rng, childGenomes[i]);
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
			boolean[] chromosome = new boolean[prob.xsTrain[0].length];
			for(int i=0; i<chromosome.length; i++){
				chromosome[i] = rng.nextBoolean();
			}
			GAIndividual newborn = new GAIndividual(prob, conf, rng, chromosome);
			newborn.evaluateFitness();
			population.add(newborn);
		}
		Collections.sort(population);
	}
}
