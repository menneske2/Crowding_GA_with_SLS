/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.GAIndividual;
import algorithm.OptimizerConfig;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import problems.Problem;

/**
 *
 * @author Fredrik-Oslo
 */
public class DataHarvester {
	
	private final int NUM_THREADS = 4;
	private final int RUNS_PER_DATAPOINT = 1;
	
	private final ExecutorService threadPool;
	
	public DataHarvester(Problem prob){
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		harvestData(prob);
		threadPool.shutdown();
	}
	
	private void harvestData(Problem prob){
		OptimizerConfig conf = new OptimizerConfig();
		conf.SEED = OptimizerConfig.NO_SEED;
		conf.FITNESS_EVALUATIONS = 1000;
		prob.fitnessPunishRatio = 0.0;
		
		// i artikkelen til suganthan er maxFEs = 2000 * D * sqrt(q), hvor D er dimensjoner og q er antall peaks i tekstfilene deres.
		// de bruker epsilon = 0.1*D. dette er for høy accuracy for denne algoritmen siden binære representasjoner ikke er like fleksible som ekte.
		// de gjør 25 runs og måler kun antall peaks funnet. 
		// de viser beste, verste, gjennomsnitt og standardavvik. 
		
		// du burde også vise beste, verste, gjennomsnitt og standardavvik for selve fitness scoren. husk å gange med -1 siden de skal være minimiseringsproblemer.
		
		runTest(prob, conf);
	}
	
	private void runTest(Problem prob, OptimizerConfig conf){
		for(int i=0; i<RUNS_PER_DATAPOINT; i++){
			Runnable tester = new AlgTester(this, prob.clone(), conf.clone());
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
