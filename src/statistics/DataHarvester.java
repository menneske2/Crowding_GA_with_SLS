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
	
	private final int NUM_THREADS = 5;
	private final int RUNS_PER_DATAPOINT = 25;
	
	private final ExecutorService threadPool;
	
	private Problem activeProb;
	private OptimizerConfig activeConf;
	
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
	
	public DataHarvester(List<Problem> probList){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		//		if(true) return;
		bigHarvest(probList);
		threadPool.shutdown();
	}
	
	public void bigHarvest(List<Problem> probList){
		for(int i=0; i<probList.size(); i++){
			if(!BenchmarkProblem.class.isAssignableFrom(probList.get(i).getClass())) 
				continue;
			BenchmarkProblem prob = (BenchmarkProblem) probList.get(i);
			System.out.println("\n-------------------\n" + prob.name + "\n-------------------");
			for(int j=2; j<=30; j++){
//				if(prob.name.startsWith("F2") && j==2)
//					continue;
				prob.setDimensionality(j);
				if(prob.name.startsWith("F14") || prob.name.startsWith("F15")){
					if(j!=10 && j!=20 && j!=30)
						continue;
				}
				else if(prob.optimasInPaper == null){
					continue;
				}
				System.out.println("-------------------\nDimensionality: " + j + "\n-------------------");
				harvestData(prob);
				waitForData();
				analyzeData();
				
				best5.clear();
				optimaFound.clear();
			}
		}
	}
	
	private void harvestData(Problem prob){
		activeProb = prob;
		activeConf = new OptimizerConfig();
		activeConf.SEED = OptimizerConfig.NO_SEED;
		prob.fitnessPunishRatio = 0.0;
		activeConf.FITNESS_EVALUATIONS = 1000;
		if(BenchmarkProblem.class.isAssignableFrom(prob.getClass())){
			BenchmarkProblem p = (BenchmarkProblem) prob;
//			conf.FITNESS_EVALUATIONS = (int)Math.round(2000 * p.getDimensionality() * Math.sqrt(p.optimasInPaper.size()));
			activeConf.FITNESS_EVALUATIONS = 150000;
			p.numFeatures = 10 * p.getDimensionality();
//			System.out.println("Using " + conf.FITNESS_EVALUATIONS + " fitness evaluations per run.");
		}
		System.out.print("Running: ");
		runTest(prob, activeConf, RUNS_PER_DATAPOINT);
		
		// i artikkelen til suganthan er maxFEs = 2000 * D * sqrt(q), hvor D er dimensjoner og q er antall peaks i tekstfilene deres.
		// de gjør 25 runs og måler kun antall peaks funnet. 
		// de viser beste, verste, gjennomsnitt og standardavvik. 
		
		// du burde også vise beste, verste, gjennomsnitt og standardavvik for selve fitness scoren. husk å gange med -1 siden de skal være minimiseringsproblemer.
	}
	
	private void analyzeData(){
		System.out.println("Optima-finding data:");
		Collections.sort(optimaFound);
		System.out.println("Best run:\t" + optimaFound.get(optimaFound.size()-1));
		System.out.println("Worst run:\t" + optimaFound.get(0));
		System.out.print("Mean:\t\t" + PerformanceMeasures.getMean(optimaFound));
		System.out.println("(" + PerformanceMeasures.getSTD(optimaFound) + ")");
		
		System.out.println("\nBest 5 optima data:");
		Collections.sort(best5);
		System.out.println("Best run:\t" + best5.get(0));
		System.out.println("Worst run:\t" + best5.get(best5.size()-1));
		double[] best5Data = PerformanceMeasures.analyzeBest5(best5);
		System.out.println("Mean:\t" + best5Data[0]);
		System.out.println("STDs:\t" + best5Data[1]);
		// beste/verste = plusset sammen fitness. 
		// vis gjennomsnitt for både beste/verste og totalt.
	}
	
	
	private synchronized void waitForData(){
		while(this.best5.size() < RUNS_PER_DATAPOINT){
			try {
				this.notifyAll();
				this.wait();
			} catch (InterruptedException ex) {
				Logger.getLogger(DataHarvester.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		System.out.println("");
		this.notifyAll();
	}
	
	private void runTest(Problem prob, OptimizerConfig conf, int amount){
		for(int i=0; i<amount; i++){
			Runnable tester = new AlgTester(this, prob.clone(), conf.clone());
			Runnable exCatcher = new ExceptionCatcher(tester, this);
			threadPool.submit(exCatcher);
		}
	}
	
	public synchronized void reportResults(int optimaFound, double[] best5){
		System.out.print((this.best5.size()+1)+".");
		this.optimaFound.add(optimaFound);
		this.best5.add(new DoubleArray(best5));
		this.notifyAll();
	}
	
	public synchronized void reportCrash(){
		// every now and then you get a nullpointer for someones genome. only happens with certain fitness landscapes.
		runTest(activeProb, activeConf, 1);
		System.out.print("crash.");
	}
	
}
