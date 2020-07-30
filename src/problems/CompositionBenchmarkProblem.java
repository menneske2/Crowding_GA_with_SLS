/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.lang.reflect.Method;
import java.math.BigInteger;

/**
 *
 * @author Fredrik-Oslo
 */
public class CompositionBenchmarkProblem extends BenchmarkProblem{
	
	public CompositionBenchmarkProblem(int numFeatures, int dimensionality, double[] searchRange, String[] functionNames, double[][] fMultipliers, double[] ranges, double[] biases, double[] lambda, double[][] shifts){
		super(numFeatures, dimensionality, null);
		Method[] methods = namesToMethods(functionNames);
		this.bmFunc = constructBenchmarkFunction(searchRange, methods, fMultipliers, ranges, biases, lambda, shifts);
	}
	
	
	private BenchmarkFunction constructBenchmarkFunction(double[] searchRange, Method[] methods, double[][] fMultipliers, double[] ranges, double[] biases, double[] lambdas, double[][] shifts){
		return (bits, dims) ->{
			BigInteger[] axesBig = BenchmarkLoader.partitionBitstring(bits, dims);
			double[] axes = BenchmarkLoader.normalize(axesBig, this.numFeatures/dims, searchRange[0], searchRange[1]);
			
			double[] weights = this.calculateWeights(axes, ranges, shifts);
			
			
			double fitness = 0.0;
			for(int i=0; i<methods.length; i++){
				try{
					double[] newAxes = adjustInput(axes, shifts[i], fMultipliers[i]);
					double f = (double) methods[i].invoke(this, newAxes);
					f *= lambdas[i];
					f += biases[i];
					f *= weights[i];
					if(Double.isFinite(f))
						fitness += f;
				} catch(Exception e){
					e.printStackTrace();
					System.exit(404);
				}
			}
			return -fitness; // minus because these are all minimization problems.
		};
	}
	
	private double[] adjustInput(double[] in, double[] shift, double[] multipliers){
		double[] adjusted = new double[in.length];
		for(int i=0; i<in.length; i++){
			adjusted[i] = in[i] * shift[i] / multipliers[i];
		}
		return adjusted;
	}
	
	private double[] calculateWeights(double[] x, double[] ranges, double[][] shifts){
		
		double[] weights = new double[ranges.length];
		for(int i=0; i<weights.length; i++){
			double power = 0;
			for(int j=0; j<x.length; j++){
				power += Math.pow(x[j] - shifts[i][j], 2);
			}
			double inner = -power / (2 * x.length * Math.pow(ranges[i], 2));
			double downer = Math.sqrt(power);
			
			weights[i] = (1.0/downer) * Math.exp(inner);
		}
		
//		System.out.print("Pre-normalization: ");
//		for(var w : weights){
//			System.out.format("%.5f, ", w);
//		}
//		System.out.println("");
		
		
		// Normalizing weights
		double sum = 0.0;
		for(var w : weights)
			sum += w;
		for(int i=0; i<weights.length; i++){
			weights[i] /= sum;
		}
		
//		System.out.print("Post-normalization: ");
//		for(var w : weights){
//			System.out.format("%.5f, ", w);
//		}
//		System.out.println("");
		
		
		return weights;
	}
	
	private Method[] namesToMethods(String[] names){
		Method[] methods = new Method[names.length];
		for(int i=0; i<names.length; i++){
			try{
				Method meth = PrimitiveFunctions.class.getMethod(names[i], double[].class);
				methods[i] = meth;
			} catch(Exception e){
				e.printStackTrace();
				System.exit(404);
			}
		}
		return methods;
	}
	
}
