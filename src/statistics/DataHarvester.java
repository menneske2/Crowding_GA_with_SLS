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
	
	private final ExecutorService threadPool;
	
	private final List<Integer> optimaFound;
	private final List<DoubleArray> best5;
	private final List<List<double[]>> performanceData;
	
	private final String filename = "DataHarvester output.txt";
	private final ResultsWriter writer;

	private int nextID = 0;
	
	public DataHarvester(Problem prob){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		performanceData = new ArrayList<>();
		writer = new ResultsWriter(filename);

		int runs = 100;
		harvestData(prob, runs);
		waitForData(runs);
		analyzeData(prob);
		threadPool.shutdown();
		writer.close();
	}
	
	public DataHarvester(List<Problem> probList){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		optimaFound = new ArrayList<>();
		best5 = new ArrayList<>();
		performanceData = new ArrayList<>();
		writer = new ResultsWriter(filename);

		// comment this out in order to run experiments with this class.
		if(true) return;

//		bigHarvestDataset(probList);
//		experiment1(probList);
//		experiment2(probList);
//		experiment3(probList); // ablation
//		experiment4(probList);
//		experiment5(probList);
//		experiment62(probList);
		experiment5(probList);
		writer.close();
		threadPool.shutdown();
		System.exit(0);
	}

	// max members in niche. Feature selection problem
	private void experiment62(List<Problem> probList){
		for(Problem prob : probList){
			double lower = 0.7;
			if(prob.name.startsWith("Madelon"))
				lower = 0.6;
			if(prob.name.startsWith("Arcene")){
				writer.writeln("Starting " + prob.name);
				OptimizerConfig conf = new OptimizerConfig();
				conf.SEED = OptimizerConfig.NO_SEED;
				conf.FITNESS_EVALUATIONS = 1000000;
				conf.CULLING_ENABLED = false;
				runTest(prob, conf, 1);
				conf.CULLING_ENABLED = true;
				int[] vals = new int[]{1, 3, 5, 7};
				for(var val : vals){
					conf.MAX_NICHE_SIZE = val;
					runTest(prob, conf, 1);
				}
				writer.write("Running: ");

				String[] strings = new String[]{"No culling", "Max 1", "Max 3", "Max 5", "Max 7"};
				waitForData(strings.length);
				PerformanceMeasures.savePerformanceChart(strings, performanceData, lower, "exp6 - "+prob.name+".png");
				resetDataStructures();
			}
		}
	}

	// Max members in niche. Synthetic test problems.
	private void experiment61(List<Problem> probList){
		int runs = 100;
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		conf.FITNESS_EVALUATIONS = 200000;
		conf.CULLING_ENABLED = false;
		int[] vals = new int[]{1, 3, 5, 7, 10};
		for(int a=0; a<vals.length+1; a++){
			if(a > 0){
				conf.CULLING_ENABLED = true;
				conf.MAX_NICHE_SIZE = vals[a-1];
				writer.writeln("\n&&&&&&&&&&&&&&&&&&&&&&&&&&\nPsi = " + conf.MAX_NICHE_SIZE + "\n&&&&&&&&&&&&&&&&&&&&&&&&&&");
			} else{
				writer.writeln("\n&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + "CULLING DISABLED" + "\n&&&&&&&&&&&&&&&&&&&&&&&&&&");
			}

			for(int i=0; i<8; i++){
				BenchmarkProblem prob = (BenchmarkProblem) probList.get(i);
				writer.writeln("\n-------------------\n" + prob.name + "\n-------------------");
				for(int j=2; j<=30; j++){
					prob.setDimensionality(j);
					if(prob.optimasInPaper == null){
						continue;
					}
					writer.writeln("-------------------\nDimensionality: " + j + "\n-------------------");
					writer.write("Running: ");
					runTest(prob, conf, runs);
					waitForData(runs);
					analyzeData(prob);
					
					resetDataStructures();
				}
			}
		}
	}

	// Many runs of a certain problem instance, graphed.
	private void experiment5(List<Problem> probList){
		for(Problem prob : probList){
			double lower = 0.7;
			if(prob.name.startsWith("Madelon"))
				lower = 0.6;
			if(prob.name.startsWith("Arcene") || prob.name.startsWith("Madelon")){
				writer.writeln("Starting " + prob.name);
				OptimizerConfig conf = new OptimizerConfig();
				conf.SEED = OptimizerConfig.NO_SEED;
				conf.FITNESS_EVALUATIONS = 1000000;
				int runs = 5;

				writer.write("Running: ");
				runTest(prob, conf, runs);

				String[] strings = new String[runs];
				for(int i=0; i<runs; i++){
					strings[i] = "Run " + (i+1);
				}
				waitForData(strings.length);
				PerformanceMeasures.savePerformanceChart(strings, performanceData, lower, "exp5 - "+prob.name+".png");
				resetDataStructures();
			}
		}
	}

	// Feedback controller vs various values of phi.
	private void experiment4(List<Problem> probList){
		int runs = 100;
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		conf.FITNESS_EVALUATIONS = 200000;
		conf.FC_ENABLED = true;
		float[] vals = new float[]{0f, 1f};
		for(int a=0; a<vals.length+1; a++){
			if(a > 0){
				conf.FC_ENABLED = false;
				conf.CROWDING_SCALING_FACTOR = vals[a-1];
				writer.writeln("\n&&&&&&&&&&&&&&&&&&&&&&&&&&\nphi = " + conf.CROWDING_SCALING_FACTOR + "\n&&&&&&&&&&&&&&&&&&&&&&&&&&");
			} else{
				writer.writeln("\n&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + "FC-enabled" + "\n&&&&&&&&&&&&&&&&&&&&&&&&&&");
			}
			
			for(int i=0; i<8; i++){
				BenchmarkProblem prob = (BenchmarkProblem) probList.get(i);
				writer.writeln("\n-------------------\n" + prob.name + "\n-------------------");
				for(int j=2; j<=30; j++){
					prob.setDimensionality(j);
					if(prob.optimasInPaper == null){
						continue;
					}
					writer.writeln("-------------------\nDimensionality: " + j + "\n-------------------");
					writer.write("Running: ");
					runTest(prob, conf, runs);
					waitForData(runs);
					analyzeData(prob);

					resetDataStructures();
				}
			}
		}
	}

	// Ablation study.
	private void experiment3(List<Problem> probList){
		for(Problem prob : probList){
			if(prob.name.startsWith("Arcene")){
				OptimizerConfig conf = new OptimizerConfig();
				conf.SEED = OptimizerConfig.NO_SEED;
				conf.FITNESS_EVALUATIONS = 1000000;

				writer.write("Running: ");
				runTest(prob, conf, 1);
				conf.FC_ENABLED = false;
				runTest(prob, conf, 1);
				conf.FC_ENABLED = true;
				conf.CULLING_ENABLED = false;
				runTest(prob, conf, 1);
				conf.CULLING_ENABLED = true;

				conf.SLS_ENABLED = false;
				conf.BITFLIP_MUTATION = true;
				conf.FC_ENABLED = false;
				runTest(prob, conf, 1);
				conf.SLS_ENABLED = false; // denne var true da jeg kjørte eksperimentet, så labelet er feil.
				conf.BITFLIP_MUTATION = false;
				conf.FC_ENABLED = true;
				runTest(prob, conf, 1);

				String[] strings = new String[]{"Full algorithm", "No FC", "No culling", "EA only", "EA+FC only"};
				waitForData(strings.length);
				PerformanceMeasures.savePerformanceChart(strings, performanceData, 0.7, "ablation chart.png");
				resetDataStructures();
			}
		}
	}

	// lazySLS gamma study.
	private void experiment2(List<Problem> probList){
		for(Problem prob : probList){
			if(prob.name.startsWith("Arcene")){
				OptimizerConfig conf = new OptimizerConfig();
				conf.SEED = OptimizerConfig.NO_SEED;
				conf.FITNESS_EVALUATIONS = 1000000;

				conf.SLS_ENABLED = false;
				conf.BITFLIP_MUTATION = true;
				runTest(prob, conf, 1);
				conf.BITFLIP_MUTATION = false;
				conf.SLS_ENABLED = true;

				int[] vals = new int[]{5, 10, 20, 40, 80};
				writer.write("Running: ");
				for(int val : vals){
					conf.SLS_MAX_FLIPS_IN_GREEDY = val;
					runTest(prob, conf, 1);
				}
				
				String[] strings = new String[]{"No SLS", "5", "10", "20", "40", "80"};
				waitForData(strings.length);
				PerformanceMeasures.savePerformanceChart(strings, performanceData, 0.7, "lazySLS gamma chart.png");
				resetDataStructures();
			}
		}
	}

	private void bigHarvestDataset(List<Problem> probList){
		int runs = 100;
		for(var prob : probList){
			if(prob.name.startsWith("Madelon") || prob.name.startsWith("Arcene")){
				writer.writeln("\n-------------------\n" + prob.name + "\n-------------------");
				harvestData(prob, runs);
				waitForData(runs);
				analyzeData(prob);

				resetDataStructures();
			}
		}
	}

	// Clustering algorithms.
	// You have to manually set the clustering algorithm in the "getNiches" function, which can be found in the algorithm.GAUtilities class.
	private void experiment1(List<Problem> probList){
		int runsPerDataPoint = 100;
		for(int i=0; i<probList.size(); i++){
			if(!BenchmarkProblem.class.isAssignableFrom(probList.get(i).getClass())) 
				continue;
			if(i == 8) break; // only do the first 8 problems.
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
				harvestData(prob, runsPerDataPoint);
				waitForData(runsPerDataPoint);
				analyzeData(prob);
				
				resetDataStructures();
			}
		}
	}

	private void resetDataStructures(){
		best5.clear();
		optimaFound.clear();
		performanceData.clear();
		nextID = 0;
	}
	
	private void harvestData(Problem prob, int runs){
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		conf.FITNESS_EVALUATIONS = 150000;
		writer.write("Running: ");
		runTest(prob, conf, runs);
	}

	private void runTest(Problem prob, OptimizerConfig conf, int runs){
		for(int i=0; i<runs; i++){
			AlgTester tester = new AlgTester(nextID, this, prob.clone(), conf.clone());
			nextID++;
			Runnable exCatcher = new ExceptionCatcher(tester, this);
			threadPool.submit(exCatcher);
		}
	}
	
	private void analyzeData(Problem prob){
		if(BenchmarkProblem.class.isAssignableFrom(prob.getClass()) && ((BenchmarkProblem)prob).optimasInPaper != null)
			writer.writeln("Known optima: " + ((BenchmarkProblem)prob).optimasInPaper.size());
		writer.writeln("Optima-finding data: (only available for some problem+dimension combos)");
		Collections.sort(optimaFound);
		writer.writeln("Best run:\t" + optimaFound.get(optimaFound.size()-1));
		writer.writeln("Worst run:\t" + optimaFound.get(0));
		writer.write("Mean:\t\t" + PerformanceMeasures.getMean(optimaFound));
		double std = PerformanceMeasures.getSTD(optimaFound);
		writer.writeln("(" + Math.round(std*100)/100 + ")");
		
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
	
	
	private synchronized void waitForData(int runs){
		while(this.best5.size() < runs){
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

	
	public synchronized void reportResults(int ID, int optimaFound, double[] best5, List<double[]> performanceData){
		writer.write((this.best5.size()+1)+".");
		writer.flush();
		this.optimaFound.add(optimaFound);
		this.best5.add(new DoubleArray(best5));
		while(this.performanceData.size() <= ID){
			this.performanceData.add(null);
		}
		this.performanceData.set(ID, performanceData);
		this.notifyAll();
	}
	
	public synchronized void reportCrash(Problem prob, OptimizerConfig conf){
		// every now and then you get a nullpointer for someones genome. only happens with certain fitness landscapes.
		runTest(prob, conf, 1);
		writer.write("crash.");
		writer.flush();
	}
	
}
