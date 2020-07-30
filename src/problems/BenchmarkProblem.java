/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import algorithm.GAUtilities;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkProblem extends Problem{
	
	protected BenchmarkFunction bmFunc;
	public int numGlobalOptima, numLocalOptima; // Local optima includes global optima.
	private int dimensionality; 
	
	public BenchmarkProblem(int numFeatures, int dimensionality, BenchmarkFunction bmf){
		this.bmFunc = bmf;
		this.numFeatures = numFeatures;
		this.fitnessPunishRatio = 0;
		setDimensionality(dimensionality);
	}
	
	@Override
	public double evaluateBitstring(boolean[] bits, boolean punish){
		this.fitnessEvaluations++;
		double fitness = this.bmFunc.evaluateFitness(bits, dimensionality);
		
		if(punish){
			double ratioUsed = (double) GAUtilities.countFeatures(bits) / bits.length;
			fitness -= fitnessPunishRatio * ratioUsed;
		}
		
		return fitness;
	}
	
	public final void setDimensionality(int dims){
		this.dimensionality = dims;
		this.distanceMeasure = new NDimensionalMappingDistance(dims);
	}
	
}
