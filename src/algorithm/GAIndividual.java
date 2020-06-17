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
import org.apache.commons.math3.ml.clustering.Clusterable;
/**
 *
 * @author Fredrik-Oslo
 */
public class GAIndividual implements Comparable<GAIndividual>, Cloneable, Clusterable{
	
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
		double[][] xTrain = reduceFeatures(prob.xsTrain);
		double[][] xValid = reduceFeatures(prob.xsValid);
		
		// Fitting model
		OLSMultipleLinearRegression regressor = new OLSMultipleLinearRegression();
		regressor.newSampleData(prob.ysTrain, xTrain); // Fitting model to training data.
		
		// Calculating RMSE and R manually since the regressor doesn't support testing data without fitting to it.
		double[] params = regressor.estimateRegressionParameters();
		double yMean = Arrays.stream(prob.ysValid).average().getAsDouble();
		double totalSumOfSquares = 0.0;
		double residualSumOfSquares = 0.0;
		
		for(int sample=0; sample<xValid.length; sample++){
			double expected = params[0];
			for(int i=1; i<params.length; i++){
				expected += xValid[sample][i-1] * params[i];
			}
			totalSumOfSquares += Math.pow(prob.ysValid[sample] - yMean, 2);
			residualSumOfSquares += Math.pow(prob.ysValid[sample] - expected, 2);
		}
		double rSquared = 1 - (residualSumOfSquares / totalSumOfSquares);
		rSquared = Math.max(rSquared, 0.000000001);
		double R = Math.sqrt(rSquared);
		double rmse = Math.sqrt(residualSumOfSquares / prob.ysValid.length); // Root mean square error (RMSE).
		this.fitness = R;
	}
	
	private double[][] reduceFeatures(double[][] full){
		int nCols = 0;
		for(Boolean b : genome){
			nCols += (b.hashCode() & 0b10) >> 1;
		}

		double[][] reduced = new double[full.length][nCols];
		int reducedIndex = 0;
		for(int dataPoint=0; dataPoint<full.length; dataPoint++){
			for(int i=0; i<genome.length; i++){
				if(genome[i]){
					reduced[dataPoint][reducedIndex] = full[dataPoint][i];
					reducedIndex++;
				}
			}
			reducedIndex = 0;
		}
		return reduced;
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

	
	@Override
	/**
	 * Method used by the k-means clustering algorithm.
	 */
	public double[] getPoint() {
		// cast the genome into a double[].
		double[] casted = new double[genome.length];
		for(int i=0; i<genome.length; i++){
			Boolean b = genome[i];
			casted[i] = (b.hashCode() & 0b10) >> 1;
		}
		return casted;
	}
}
