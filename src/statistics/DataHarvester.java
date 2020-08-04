/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.OptimizerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import problems.BenchmarkProblem;
import problems.Problem;

/**
 *
 * @author Fredrik-Oslo
 */
public class DataHarvester {
	
	private final int NUM_THREADS = 4;
	private final int RUNS_PER_DATAPOINT = 24;
	
	private final ExecutorService threadPool;
	
	private final List<Integer> optimaFound;
	private final List<DoubleArray> best5;
	
	public DataHarvester(Problem prob){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		harvestData(prob);
		waitForData();
		analyzeData();
		threadPool.shutdown();
	}
	
	private void analyzeData(){
		System.out.println("\nOptima-finding data:");
		Collections.sort(optimaFound);
		System.out.println("Worst run:\t" + optimaFound.get(0));
		System.out.println("Best run:\t" + optimaFound.get(optimaFound.size()-1));
		System.out.println("Mean:\t" + PerformanceMeasures.getMean(optimaFound));
		
		System.out.println("\nBest 5 optima data:");
		Collections.sort(best5);
		System.out.println("Worst run:\t" + best5.get(0));
		System.out.println("Best run:\t" + best5.get(best5.size()-1));
		double[] sums = new double[best5.get(0).values.length];
		for(var da : best5){
			for(int i=0; i<sums.length; i++){
				sums[i] += da.values[i];
			}
		}
		for(int i=0; i<sums.length; i++){
			sums[i] /= best5.size();
		}
		System.out.println("Mean:\t" + dArrayToString(sums, 3));
		// beste/verste = plusset sammen fitness. 
		// vis gjennomsnitt for både beste/verste og totalt.
	}
	
	private String dArrayToString(double[] in, int decimals){
		String out = "[";
		for(int i=0; i<in.length; i++){
			double toWrite = in[i];
			if(decimals != -1)
				toWrite = Math.round(10*decimals*toWrite) / 10*decimals;
			out += toWrite + ", ";
		}
		out = out.substring(0, out.length()-2) + "]";
		return out;
	}
	
	private synchronized void waitForData(){
		while(this.best5.size() < RUNS_PER_DATAPOINT){
			try {
				this.wait();
			} catch (InterruptedException ex) {
				Logger.getLogger(DataHarvester.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	private void harvestData(Problem prob){
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		prob.fitnessPunishRatio = 0.0;
		conf.FITNESS_EVALUATIONS = 1000;
		if(BenchmarkProblem.class.isAssignableFrom(prob.getClass())){
			BenchmarkProblem p = (BenchmarkProblem) prob;
			conf.FITNESS_EVALUATIONS = (int)Math.round(2000 * p.getDimensionality() * Math.sqrt(p.optimasInPaper.size()));
			System.out.println("Using " + conf.FITNESS_EVALUATIONS + " fitness evaluations per run.");
		}
		
		
		// i artikkelen til suganthan er maxFEs = 2000 * D * sqrt(q), hvor D er dimensjoner og q er antall peaks i tekstfilene deres.
		// de gjør 25 runs og måler kun antall peaks funnet. 
		// de viser beste, verste, gjennomsnitt og standardavvik. 
		
		// du burde også vise beste, verste, gjennomsnitt og standardavvik for selve fitness scoren. husk å gange med -1 siden de skal være minimiseringsproblemer.
		
		runTest(prob, conf);
	}
	
	private void runTest(Problem prob, OptimizerConfig conf){
		System.out.println("Running " + RUNS_PER_DATAPOINT + " instances.");
		for(int i=0; i<RUNS_PER_DATAPOINT; i++){
			Runnable tester = new AlgTester(this, prob.clone(), conf.clone());
			threadPool.submit(tester);
		}
	}
	
	public synchronized void reportResults(int optimaFound, double[] best5){
		System.out.println((this.best5.size()+1) + ": Optima found: " + optimaFound + "\tBest 5 fitnesses: " + dArrayToString(best5, 3));
		this.optimaFound.add(optimaFound);
		this.best5.add(new DoubleArray(best5));
		this.notifyAll();
	}
	
}
