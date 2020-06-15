/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;


import core.Problem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
/**
 *
 * @author Fredrik-Oslo
 */
public class GAIndividual implements Comparable<GAIndividual>, Cloneable{
	
	private final Problem prob;
	private final OptimizerConfig conf;
	private final Random rng;
	public boolean[] genome;
	public double fitness;
	
	public GAIndividual(Problem prob, OptimizerConfig conf, Random rng, boolean[] genome){
		this.prob = prob;
		this.conf = conf;
		this.rng = rng;
		this.genome = genome;
	}
	
	
	public GAIndividual clone(){
		try {
			GAIndividual copy = (GAIndividual) super.clone(); // not deep copying the problem for memory reasons.
			copy.genome = this.genome.clone();
			return copy;
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			System.exit(4);
		}
		return null;
	}
	
	
	public void evaluateFitness(){
		// Removing columns according to the genome bitstring.
		int nCols = 0;
		for(Boolean b : genome){
			nCols += (b.hashCode() & 0b10) >> 1;
		}

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
		
		
		// Splitting into training- and test-set
		int totalSetSize = prob.ys.length;
		int testSetSize = Math.round(totalSetSize*conf.TEST_SET_PROPORTION);
		int trainSetSize = totalSetSize - testSetSize;
		
		List<Integer> indexes = new ArrayList<>();
		for(int i=0; i<totalSetSize; i++)
			indexes.add(i);
		Collections.shuffle(indexes, rng);
		for(int i=0; i<testSetSize; i++){
			indexes.remove(0);
		}
		
		double[][] x_train = new double[trainSetSize][nCols];
		double[] y_train = new double[trainSetSize];
		double[][] x_test = new double[testSetSize][nCols];
		double[] y_test = new double[testSetSize];
		
		int trainProgress = 0;
		int testProgress = 0;
		for(int i=0; i<totalSetSize; i++){
			if(indexes.contains(i)){
				x_train[trainProgress] = reducedXs[i];
				y_train[trainProgress] = prob.ys[i];
				trainProgress++;
			} else{
				x_test[testProgress] = reducedXs[i];
				y_test[testProgress] = prob.ys[i];
				testProgress++;
			}
		}
		if(testSetSize == 0){
			y_test = y_train;
			x_test = x_train;
		}

		
		// Fitting model
		OLSMultipleLinearRegression regressor = new OLSMultipleLinearRegression();
		regressor.newSampleData(y_train, x_train); // Fitting model to training data.
		
		// Calculating RMSE and R manually since the regressor doesn't support testing data without fitting to it.
		double[] params = regressor.estimateRegressionParameters();
		double yMean = Arrays.stream(y_test).average().getAsDouble();
		double totalSumOfSquares = 0.0;
		double residualSumOfSquares = 0.0;
		
		for(int sample=0; sample<x_test.length; sample++){
			double expected = params[0];
			for(int i=1; i<params.length; i++){
				expected += x_test[sample][i-1] * params[i];
			}
			totalSumOfSquares += Math.pow(y_test[sample] - yMean, 2);
			residualSumOfSquares += Math.pow(y_test[sample] - expected, 2);
		}
		double rSquared = 1 - (residualSumOfSquares / totalSumOfSquares);
		rSquared = Math.max(rSquared, 0.000000001);
		double R = Math.sqrt(rSquared);
		double rmse = Math.sqrt(residualSumOfSquares / y_test.length); // Root mean square error (RMSE).
		this.fitness = R;
	}

	public String getGenomeAsString(){
		String s = "";
		for(Boolean b : genome){
			s += (b.hashCode() & 0b10) >> 1;
		}
		return s;
	}
	
	public int numberOfFeatures(){
		int num = 0;
		for(Boolean b : genome){
			num += (b.hashCode() & 0b10) >> 1;
		}
		return num;
	}
	
	@Override
	/**
	 * Sorts in descending order. The highest fitness individual will be at the 0th index position.
	 */
	public int compareTo(GAIndividual other) {
		if(this.fitness == other.fitness)
			return 0;
		return this.fitness > other.fitness ? -1 : 1;
	}
}
