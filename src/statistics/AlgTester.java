/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.GAIndividual;
import algorithm.GeneticAlgorithm;
import algorithm.OptimizerConfig;
import core.Problem;
import core.SolverController;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Fredrik-Oslo
 */
public class AlgTester extends SolverController implements Runnable{
	
	private final DataHarvester harvester;
	
	public AlgTester(DataHarvester harvester, Problem prob, OptimizerConfig conf){
		this.harvester = harvester;
		this.prob = prob;
		this.conf = conf;
	}

	@Override
	public void run() {
		comsChannel = new LinkedBlockingQueue();
		Runnable optimizer = new GeneticAlgorithm(prob, conf, this, comsChannel);
		optimizer.run();
	}
	
//	@Override
//	public void progressReport(int generation, int FEs, double bestFit, double best, double avg, int featuresInBest, double entropy, double nNiches, 
//			double mutaChance, double crossChance, double crowdingFactor){
//		
//	}
//	
//	@Override
//	public void registerSolution(Problem p, List<GAIndividual> pop, long timeSpent){
//		harvester.reportResults(pop.get(0));
//	}
	
}
