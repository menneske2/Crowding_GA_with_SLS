/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.GAIndividual;
import algorithm.OptimizerConfig;
import core.Problem;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Fredrik-Oslo
 */
public class DataHarvester {
	
	private final int NUM_THREADS = 4;
	private final int PROB_INDEX = 2;
	private final int RUNS_PER_DATAPOINT = 2;
	
	private final List<Problem> probList;
	private final ExecutorService threadPool;
	
	public DataHarvester(List<Problem> probList){
		this.probList = probList;
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		Problem prob = probList.get(PROB_INDEX);
//		harvestData(prob);
		threadPool.shutdown();
	}
	
	private void harvestData(Problem prob){
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		conf.FITNESS_EVALUATIONS = 1000;
		
		runTest(prob, conf);
	}
	
	private void runTest(Problem prob, OptimizerConfig conf){
		for(int i=0; i<RUNS_PER_DATAPOINT; i++){
			Runnable tester = new AlgTester(this, prob, conf.clone());
			threadPool.submit(tester);
		}
	}
	
	public void reportResults(GAIndividual best){
		System.out.println("score: " + best.fitness);
		String out = "Features used: [";
		for(int i=0; i<best.genome.length; i++){
			if(best.genome[i]){
				out += i + ", ";
			}
		}
		out = out.substring(0, out.length()-2);
		System.out.println(out + "]");
	}
	
}
