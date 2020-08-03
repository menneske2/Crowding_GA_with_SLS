/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 *
 * @author Fredrik-Oslo
 */
public abstract class Problem implements Cloneable{
	
	public String name;
	public int numFeatures;
	public DistanceMeasure distanceMeasure;
	public double fitnessPunishRatio = 0.5;
	
	public int fitnessEvaluations = 0;
	
	
	public abstract double evaluateBitstring(boolean[] bits, boolean punish);
	
	
	@Override
	public Problem clone(){
		try{
		return (Problem) super.clone();
		} catch(Exception e){
			e.printStackTrace();
			System.exit(98);
			return null;
		}
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
