/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import algorithm.GAUtilities;
import cec15_nich_java_code.cec15_nich_func;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	public final double[] searchRange;
	protected int dimensionality;
	
	public List<double[]> optimasInPaper;
	public NumOptimaCalculator numOptimaCalc;
	
	
	public BenchmarkProblem(int funcNumber){
		this.funcNumber = funcNumber;
		this.searchRange = new double[]{-100, 100};
		this.fitnessPunishRatio = 0;
		
		this.numFeatures = 720;
		setDimensionality(2);
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
	
	private void assembleOptimaList(){
		File optimaFile = new File("cec15_nich\\optima\\optima_positions_"+funcNumber+"_"+dimensionality+"D.txt");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(optimaFile));
			optimasInPaper = new ArrayList<>();
			while(true){
				String line = reader.readLine();
				if(line == null) break;
				String[] segments = line.strip().split(" ");
				double[] coords = new double[dimensionality];
				int i=0;
				for(String seg : segments){
					if(!seg.equals("")){
						double val = Double.parseDouble(seg.strip());
						coords[i] = val;
						i++;
					}
				}
				optimasInPaper.add(coords);
			}
		} catch(Exception e){
			optimasInPaper = null;
//			System.out.println("Optima file for problem #"+funcNumber+" in "+dimensionality+" dimensions not found.");
		}
	}
	
	public final void setDimensionality(int dims){
		this.dimensionality = dims;
		this.distanceMeasure = new NDimensionalMappingDistance(this, dims);
		assembleOptimaList();
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
			System.out.println("bitlength: " + bits.length);
			System.out.println("[BenchmarkProblem] invalid combination of bitlength and partitioning");
			throw new Error();
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
