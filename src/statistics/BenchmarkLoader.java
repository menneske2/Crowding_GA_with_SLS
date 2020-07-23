/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import core.Problem;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkLoader {
	
	public void loadBenchmarkProblems(List<Problem> probList){
		probList.add(loadF1(64));
	}
	
	public Problem loadByName(String name, int bitLength){
		switch(name){
			case "F1":
				return loadF1(bitLength);
		}
		System.out.println("[BenchmarkLoader] Problem " + name + " not found.");
		return null;
	}
	
	public Problem loadF1(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, 2);
			double[] axes = normalize(axesBig, bitLength/2, -100, 100);
			
			int total = 0;
			for(int i=0; i<axes.length; i++){
				double dimX = axes[i];
				double y = dimX + 20;
				double ti = 0;
				if(y<0){
					ti = -160 + y*y;
				} else if(y>= 0 && y<=15){
					ti = 160.0/15.0 * (y-15);
				} else if(y>15 && y<=20){
					ti = 200.0/5.0 * (15-y);
				} else if(y > 20){
					ti = -200 + Math.pow(y-20, 2);
				}
				total += ti;
			}
			total += 200*axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F1";
		return prob;
	}
	
	public static double[] normalize(BigInteger[] in, int maxBits, int floor, int ceiling){
		double[] toReturn = new double[in.length];
		String toParse = "";
		for(int i=0; i<maxBits; i++)
			toParse += "1";
		BigInteger max = new BigInteger(toParse, 2);
		
		for(int i=0; i<in.length; i++){
			BigInteger num = in[i];
			double ratio = num.doubleValue()/max.doubleValue();
			toReturn[i] = floor + ratio*(ceiling-floor);
		}
		return toReturn;
	}
	
	public static BigInteger[] partitionBitstring(boolean[] bits, int partitions){
		if(bits.length%partitions != 0){
			System.out.println("[Main] invalid combination of bitlength and partitioning");
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
}
