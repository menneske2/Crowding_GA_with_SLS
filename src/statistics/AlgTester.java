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
import java.util.ArrayList;
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
	
	public Problem prob;
	public OptimizerConfig conf;
	private final DataHarvester harvester;
	public final int ID;

	private final List<double[]> performanceData = new ArrayList<>();
	
	public AlgTester(int ID, DataHarvester harvester, Problem prob, OptimizerConfig conf){
		this.harvester = harvester;
		this.prob = prob;
		this.conf = conf;
		this.ID = ID;
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
		performanceData.add(new double[]{FEs, bestNoPunish});
	}
	
	@Override
	public void registerSolution(Problem p, List<GAIndividual> pop, long timeSpent){
		double separation = 20;
		if(BenchmarkProblem.class.isAssignableFrom(p.getClass()))
			separation = 0.01 * ((BenchmarkProblem) p).getDimensionality();
		double[] bestN = PerformanceMeasures.nBestSeparatedBy(pop, p.distanceMetric, 5, separation);
		// Best-n was used to evaluate the composition functions.
		if(p.name.startsWith("F14") || p.name.startsWith("F15")){
			harvester.reportResults(ID, -1, bestN, performanceData);
			return;
		}
		
		int numOptimaFound = -1;
		if(BenchmarkProblem.class.isAssignableFrom(p.getClass())){
			BenchmarkProblem pp = (BenchmarkProblem) p;
			numOptimaFound = PerformanceMeasures.getNumOptimaFound(pp, pop);
			for(int i=0; i<bestN.length; i++){
				bestN[i] = -bestN[i];
			}
		}
		
		harvester.reportResults(ID, numOptimaFound, bestN, performanceData);
	}
	
	@Override
	public boolean requiresWaiting(){
		return false;
	}
	
}
