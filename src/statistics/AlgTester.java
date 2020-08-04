/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.DataReceiver;
import algorithm.GAIndividual;
import algorithm.GeneticAlgorithm;
import algorithm.OptimizerConfig;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import problems.BenchmarkProblem;
import problems.Problem;

/**
 *
 * @author Fredrik-Oslo
 */
public class AlgTester implements DataReceiver, Runnable{
	
	private Problem prob;
	private OptimizerConfig conf;
	private final DataHarvester harvester;
	
	public AlgTester(DataHarvester harvester, Problem prob, OptimizerConfig conf){
		this.harvester = harvester;
		this.prob = prob;
		this.conf = conf;
	}

	@Override
	public void run() {
		BlockingQueue comsChannel = new LinkedBlockingQueue();
		Runnable optimizer = new GeneticAlgorithm(prob, conf, this, comsChannel);
		optimizer.run();
	}
	
	@Override
	public void progressReport(int generation, int FEs, List<GAIndividual> pop, double bestNoPunish, double avgNoPunish, double entropy, double nNiches, 
			double mutaChance, double crossChance, double crowdingFactor){
	}
	
	@Override
	public void registerSolution(Problem p, List<GAIndividual> pop, long timeSpent){
		double separation = 20;
		if(BenchmarkProblem.class.isAssignableFrom(p.getClass()))
			separation = 1 * ((BenchmarkProblem) p).getDimensionality();
		double[] bestN = PerformanceMeasures.nBestSeparatedBy(pop, p.distanceMeasure, 5, separation);
		// Best-n was used to evaluate the composition functions.
		
		int numOptimaFound = -1;
		if(BenchmarkProblem.class.isAssignableFrom(p.getClass())){
			BenchmarkProblem pp = (BenchmarkProblem) p;
			// de bruker epsilon = 0.1*D. dette er for høy accuracy for denne algoritmen siden binære representasjoner ikke er like fleksible som ekte.
			double epsilon = 0.4 * pp.getDimensionality();
			numOptimaFound = PerformanceMeasures.getNumOptimaFound(pp, pop, epsilon);
			for(int i=0; i<bestN.length; i++){
				bestN[i] = -bestN[i];
			}
		}
		
		harvester.reportResults(numOptimaFound, bestN);
	}
	
	@Override
	public boolean requiresWaiting(){
		return false;
	}
	
}
