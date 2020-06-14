/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;


import core.Problem;
import java.util.Random;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
/**
 *
 * @author Fredrik-Oslo
 */
public class GAIndividual {
	
	private final Problem prob;
	private final OptimizerConfig conf;
	private final Random rng;
	private boolean[] genome;
	private double fitness;
	
	public GAIndividual(Problem prob, OptimizerConfig conf, Random rng, boolean[] genome){
		this.prob = prob;
		this.conf = conf;
		this.rng = rng;
		this.genome = genome;
	}
	
	public void evaluateFitness(){
//		// Splitting into training- and test-set
//		int totalSetSize = prob.ys.length;
//		int testSetSize = Math.round(totalSetSize*conf.TEST_SET_PROPORTION);
//		int trainSetSize = totalSetSize - testSetSize;
//		int xLength = prob.xs[0].length;
//		
//		List<Integer> indexes = new ArrayList<>();
//		for(int i=0; i<totalSetSize; i++)
//			indexes.add(i);
//		Collections.shuffle(indexes, rng);
//		for(int i=0; i<testSetSize; i++){
//			indexes.remove(0);
//		}
//		
//		double[][] x_train = new double[trainSetSize][xLength];
//		double[] y_train = new double[trainSetSize];
//		double[][] x_test = new double[testSetSize][xLength];
//		double[] y_test = new double[testSetSize];
//		
//		int trainProgress = 0;
//		int testProgress = 0;
//		for(int i=0; i<totalSetSize; i++){
//			if(indexes.contains(i)){
//				x_train[trainProgress] = prob.xs[i];
//				y_train[trainProgress] = prob.ys[i];
//				trainProgress++;
//			} else{
//				x_test[testProgress] = prob.xs[i];
//				y_test[testProgress] = prob.ys[i];
//				testProgress++;
//			}
//		}
		
		// Removing columns according to the genome bitstring.
		int nCols = 0;
		for(Boolean b : genome){
			nCols += (b.hashCode() & 0b10) >> 1;
		}
		System.out.println("nCols: " + nCols);
		System.out.print("genome: ");
		for(boolean b : genome){
			System.out.print(b + ", ");
		}
		System.out.println("");

		double[][] reducedXs = new double[prob.ys.length][nCols];
		int reducedIndex = 0;
		for(int dataPoint=0; dataPoint<prob.xs.length; dataPoint++){
			for(int i=0; i<genome.length; i++){
				if(genome[i]){
					reducedXs[dataPoint][reducedIndex] = prob.xs[dataPoint][i];
					reducedIndex++;
				}
			}
			reducedIndex = 0;
		}

		
		// Fitting model and gathering statistics.
		OLSMultipleLinearRegression regressor = new OLSMultipleLinearRegression();
		regressor.newSampleData(prob.ys, reducedXs); // nå har den lært.
		
		double rmse = regressor.calculateTotalSumOfSquares();
		rmse /= prob.ys.length;
		rmse = Math.sqrt(rmse);
		double rSquared = regressor.calculateRSquared();
		System.out.println("Root mean square error (RMSE): " + rmse);
		System.out.println("R-squared: " + rSquared);
	}
}
