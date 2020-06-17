/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class Problem {
	
	private final String name;
	public double[][] xsTrain, xsValid;
	public double[] ysTrain, ysValid;
	
	
	public Problem(String name, List<List<Float>> trainSet, List<List<Float>> validationSet){
		this.name = name;
		
		this.ysTrain = new double[trainSet.size()];
		this.xsTrain = parseDataFile(trainSet, ysTrain);
		
		this.ysValid = new double[validationSet.size()];
		this.xsValid = parseDataFile(validationSet, ysValid);
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
