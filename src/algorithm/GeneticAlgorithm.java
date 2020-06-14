/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import core.Problem;
import core.SolverController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;

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

	public GeneticAlgorithm(Problem prob, OptimizerConfig conf, SolverController feedbackStation, BlockingQueue comsChannel){
		this.prob = prob;
		this.conf = conf;
		this.feedbackStation = feedbackStation;
		this.comsChannel = comsChannel;
		rng = new Random();
		if(conf.SEED != OptimizerConfig.NO_SEED)
			rng.setSeed(conf.SEED);
	}
	
	@Override
	public void run() {
		Date startTime = new Date();
		
		initPopulation();
		
		int generation = 0;
		while(true){
			if(!comsChannel.isEmpty()){ // stop-button implementation.
				System.out.println(comsChannel.poll());
				break;
			}
			if(conf.GENERATIONS != -1 && generation>=conf.GENERATIONS){
				break;
			}

			population.get(0).evaluateFitness();
			
			
			
			

			generation++;
			
			// Sending progress report to main client.
			final int genCopy = generation; // it needs to be an effectively final variable.
			
			Platform.runLater(()->{
				feedbackStation.progressReport(genCopy, rng.nextInt(), rng.nextInt());
			});
		}


		long timeTaken = new Date().getTime() - startTime.getTime();
		System.out.println("Time taken: " + (int)Math.floor(timeTaken/(1000*60)) + "m" + (timeTaken/1000)%60 + "s");

		Platform.runLater(()->{
			feedbackStation.registerSolution(prob);
		});
	}
	
	
	private void initPopulation(){
		population = new ArrayList<>();
		
		// Random initialization
		for(int person=0; person<conf.POPULATION_SIZE; person++){
			boolean[] chromosome = new boolean[prob.xs[0].length];
			for(int i=0; i<chromosome.length; i++){
				chromosome[i] = rng.nextBoolean();
			}
			GAIndividual newborn = new GAIndividual(prob, conf, rng, chromosome);
			population.add(newborn);
		}
		
	}
}
