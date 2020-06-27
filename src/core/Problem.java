/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author Fredrik-Oslo
 */
public class Problem {
	
	private final String name;
	public double[][] xsTrain, xsValid;
	public double[] ysTrain, ysValid;
	public int fitnessEvaluations = 0;
	
	
	public Problem(String name, List<List<Float>> trainSet, List<List<Float>> validationSet){
		this.name = name;
		
		this.ysTrain = new double[trainSet.size()];
		this.xsTrain = parseDataFile(trainSet, ysTrain);
		
		this.ysValid = new double[validationSet.size()];
		this.xsValid = parseDataFile(validationSet, ysValid);
	}
	
	public double evaluateBitstring(boolean[] bits){
		double[][] xTrain = reduceFeatures(xsTrain, bits);
		double[][] xValid = reduceFeatures(xsValid, bits);
		
		// Fitting model
		OLSMultipleLinearRegression regressor = new OLSMultipleLinearRegression();
		regressor.newSampleData(ysTrain, xTrain); // Fitting model to training data.
		
		// Calculating RMSE and R manually since the regressor doesn't support testing data without fitting to it.
		double[] params = regressor.estimateRegressionParameters();
		double yMean = Arrays.stream(ysValid).average().getAsDouble();
		double totalSumOfSquares = 0.0;
		double residualSumOfSquares = 0.0;
		
		for(int sample=0; sample<xValid.length; sample++){
			double expected = params[0];
			for(int i=1; i<params.length; i++){
				expected += xValid[sample][i-1] * params[i];
			}
			totalSumOfSquares += Math.pow(ysValid[sample] - yMean, 2);
			residualSumOfSquares += Math.pow(ysValid[sample] - expected, 2);
		}
		double rSquared = 1 - (residualSumOfSquares / totalSumOfSquares);
		rSquared = Math.max(rSquared, 0.000000001);
		double R = Math.sqrt(rSquared);
		double rmse = Math.sqrt(residualSumOfSquares / ysValid.length); // Root mean square error (RMSE).
		
		this.fitnessEvaluations++;
		return R;
	}
	
	private double[][] reduceFeatures(double[][] full, boolean[] bits){
		int nCols = 0;
		for(Boolean b : bits){
			nCols += (b.hashCode() & 0b10) >> 1;
		}

		double[][] reduced = new double[full.length][nCols];
		int reducedIndex = 0;
		for(int dataPoint=0; dataPoint<full.length; dataPoint++){
			for(int i=0; i<bits.length; i++){
				if(bits[i]){
					reduced[dataPoint][reducedIndex] = full[dataPoint][i];
					reducedIndex++;
				}
			}
			reducedIndex = 0;
		}
		return reduced;
	}
	
	
	private double[][] parseDataFile(List<List<Float>> data, double[] ys){
		double[][] xs = new double[data.size()][data.get(0).size()-1];
		
		for(int i=0; i<data.size(); i++){
			var line = data.get(i);
			ys[i] = line.remove(line.size()-1);
			for(int j=0; j<line.size(); j++)
				xs[i][j] = line.get(j);
		}
		return xs;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
