/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import algorithm.GAIndividual;
import algorithm.GAUtilities;
import cec15_nich_java_code.cec15_nich_func;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkProblem extends Problem{
	
	private final int funcNumber;
	private final cec15_nich_func tf = new cec15_nich_func();
	
	protected final double[] searchRange;
	protected int dimensionality; 
	
	public NumOptimaCalculator numOptimaCalc;
	protected List<Double> gOptimaOptions;
	
	
	public BenchmarkProblem(int funcNumber, int numFeatures, int dimensionality){
		this.funcNumber = funcNumber;
		this.searchRange = new double[]{-100, 100};
		this.numFeatures = numFeatures;
		this.fitnessPunishRatio = 0;
		setDimensionality(dimensionality);
	}
	
	@Override
	public double evaluateBitstring(boolean[] bits, boolean punish) {
		this.fitnessEvaluations++;
		
		double[] axes = translateToCoordinates(bits);
		double[] f = new double[]{0};
		try{
			tf.test_func(axes, f, dimensionality, 1, funcNumber);
		} catch(Exception e){
			System.out.println("[BenchmarkProblem] something went wrong with fitness evaluation");
			e.printStackTrace();
			System.exit(9);
		}
		double fitness = -f[0];
		
		if(punish){
			double ratioUsed = (double) GAUtilities.countFeatures(bits) / bits.length;
			fitness -= fitnessPunishRatio * ratioUsed;
		}
		
		return fitness;
	}
	
	public void setGlobalOptima(List<Double> optima){
		this.gOptimaOptions = optima;
	}
	
	public int countGlobalsHit(List<GAIndividual> pop, double tolerance){
		if(this.gOptimaOptions == null)
			return -1;
		
		List<double[]> points = new ArrayList<>();
		for(var gai : pop){
			points.add(this.translateToCoordinates(gai.genome));
		}
		
//		System.out.format("[%.3f", axes[0]);
//		for(int i=1; i<axes.length; i++){
//			System.out.format(", %.3f", axes[i]);
//		}
//		System.out.println("]");
//		
//		boolean isOptima = true;
//		for(var coord : axes){
//			boolean probable = false;
//			for(var possibility : gOptimaOptions){
//				if(Math.abs(coord-possibility) <= tolerance){
//					probable = true;
//				}
//			}
//			if(probable == false){
//				isOptima = false;
//				break;
//			}
//		}
		return 0;
	}
	
	public final void setDimensionality(int dims){
		this.dimensionality = dims;
		this.distanceMeasure = new NDimensionalMappingDistance(dims);
	}
	
	public int getDimensionality(){
		return this.dimensionality;
	}
	
	public double[] translateToCoordinates(boolean[] bits){
		BigInteger[] axesBig = partitionBitstring(bits, dimensionality);
		double[] axes = normalize(axesBig, numFeatures/dimensionality, searchRange[0], searchRange[1]);
		if(searchRange.length > 2){
			axes[1] = normalize(axesBig, numFeatures/dimensionality, searchRange[2], searchRange[3])[1];
		}
		return axes;
	}
	
	public static BigInteger[] partitionBitstring(boolean[] bits, int partitions){
		if(bits.length%partitions != 0){
			System.out.println("[BenchmarkLoader] invalid combination of bitlength and partitioning");
			System.exit(96);
		}
		BigInteger[] toReturn = new BigInteger[partitions];
		int len = bits.length / partitions;
		
		for(int i=0; i<partitions; i++){
			String bs = "";
			for(int j=0; j<len; j++){
				Boolean b = bits[i*len + j];
				bs += (b.hashCode() & 0b10) >> 1;
			}
			toReturn[i] = new BigInteger(bs, 2);
		}
		return toReturn;
	}
	
	public static double[] normalize(BigInteger[] in, int maxBits, double floor, double ceiling){
		String toParse = "";
		for(int i=0; i<maxBits; i++)
			toParse += "1";
		BigInteger max = new BigInteger(toParse, 2);
		
		double[] toReturn = new double[in.length];
		for(int i=0; i<in.length; i++){
			BigInteger num = in[i];
			double ratio = num.doubleValue()/max.doubleValue();
			toReturn[i] = floor + ratio*(ceiling-floor);
		}
		return toReturn;
	}
	
}
