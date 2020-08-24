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
	private final int RUNS_PER_DATAPOINT = 100;
	
	private final ExecutorService threadPool;
	
	private Problem activeProb;
	private OptimizerConfig activeConf;
	
	private final List<Integer> optimaFound;
	private final List<DoubleArray> best5;
	
	private final String filename = "DataHarvester output.txt";
	private final ResultsWriter writer;
	
	public DataHarvester(Problem prob){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		writer = new ResultsWriter(filename);

		harvestData(prob);
		waitForData();
		analyzeData(prob);
		threadPool.shutdown();
		writer.close();
	}
	
	public DataHarvester(List<Problem> probList){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		writer = new ResultsWriter(filename);
		
		if(true) return;

//		bigHarvestDataset(probList);
		bigHarvest(probList);
		writer.close();
		threadPool.shutdown();
		System.exit(0);
	}

	private void bigHarvestDataset(List<Problem> probList){
		for(var prob : probList){
			if(prob.name.startsWith("Madelon") || prob.name.startsWith("Arcene")){
				writer.writeln("\n-------------------\n" + prob.name + "\n-------------------");
				harvestData(prob);
				waitForData();
				analyzeData(prob);

				best5.clear();
				optimaFound.clear();
			}
		}
	}
	
	private void bigHarvest(List<Problem> probList){
		for(int i=0; i<probList.size(); i++){
			if(!BenchmarkProblem.class.isAssignableFrom(probList.get(i).getClass())) 
				continue;
//			if(i == 8) break; // only do the first 8 problems.
			BenchmarkProblem prob = (BenchmarkProblem) probList.get(i);
			writer.writeln("\n-------------------\n" + prob.name + "\n-------------------");
			for(int j=2; j<=30; j++){
//				if(prob.name.startsWith("F12") && j!=30)
//					continue;
				prob.setDimensionality(j);
				if(prob.name.startsWith("F14") || prob.name.startsWith("F15")){
					if(j!=10 && j!=20 && j!=30)
						continue;
				}
				else if(prob.optimasInPaper == null){
					continue;
				}
				writer.writeln("-------------------\nDimensionality: " + j + "\n-------------------");
				harvestData(prob);
				waitForData();
				analyzeData(prob);
				
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
		activeConf.FITNESS_EVALUATIONS = 200000;
		if(BenchmarkProblem.class.isAssignableFrom(prob.getClass())){
			BenchmarkProblem p = (BenchmarkProblem) prob;
			activeConf.FITNESS_EVALUATIONS = 150000;
//			writer.writeln("Using " + conf.FITNESS_EVALUATIONS + " fitness evaluations per run.");
		}
		writer.write("Running: ");
		runTest(prob, activeConf, RUNS_PER_DATAPOINT);
		
		// i artikkelen til suganthan er maxFEs = 2000 * D * sqrt(q), hvor D er dimensjoner og q er antall peaks i tekstfilene deres.
		// de gjør 25 runs og måler kun antall peaks funnet. 
		// de viser beste, verste, gjennomsnitt og standardavvik. 
		
		// du burde også vise beste, verste, gjennomsnitt og standardavvik for selve fitness scoren. husk å gange med -1 siden de skal være minimiseringsproblemer.
	}
	
	private void analyzeData(Problem prob){
		if(BenchmarkProblem.class.isAssignableFrom(prob.getClass()) && ((BenchmarkProblem)prob).optimasInPaper != null)
			writer.writeln("Known optima: " + ((BenchmarkProblem)prob).optimasInPaper.size());
		writer.writeln("Optima-finding data:");
		Collections.sort(optimaFound);
		writer.writeln("Best run:\t" + optimaFound.get(optimaFound.size()-1));
		writer.writeln("Worst run:\t" + optimaFound.get(0));
		writer.write("Mean:\t\t" + PerformanceMeasures.getMean(optimaFound));
		writer.writeln("(" + PerformanceMeasures.getSTD(optimaFound) + ")");
		
		writer.writeln("\nBest 5 optima data:");
		Collections.sort(best5);
		writer.writeln("Best run:\t" + best5.get(0));
		writer.writeln("Worst run:\t" + best5.get(best5.size()-1));
		double[] best5Data = PerformanceMeasures.analyzeBest5(best5);
		writer.writeln("Mean:\t" + best5Data[0]);
		writer.writeln("STDs:\t" + best5Data[1]);
		String out = "Pastable: " + Math.round(best5Data[0]*100)/100.0 + "(" + Math.round(best5Data[1]*100)/100.0 + ")";
		writer.writeln(out);

		double[] best = new double[best5.size()];
		for(int i=0; i<best5.size(); i++){
			best[i] = best5.get(i).values[0];
		}
		DoubleArray da = new DoubleArray(best);
		writer.writeln("best: " + da);

		writer.flush();
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
		writer.writeln("");
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
		writer.write((this.best5.size()+1)+".");
		writer.flush();
		this.optimaFound.add(optimaFound);
		this.best5.add(new DoubleArray(best5));
		this.notifyAll();
	}
	
	public synchronized void reportCrash(){
		// every now and then you get a nullpointer for someones genome. only happens with certain fitness landscapes.
		runTest(activeProb, activeConf, 1);
		writer.write("crash.");
		writer.flush();
	}
	
}
