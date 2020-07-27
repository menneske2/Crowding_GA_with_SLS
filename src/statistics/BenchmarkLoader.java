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
	
	private static List<Problem> probList;
	private static final int DIMENSIONALITY = 2;
	
	public void loadBenchmarkProblems(List<Problem> probList){
		this.probList = probList;
		int bitLength = 512;
		probList.add(loadF1(bitLength));
		probList.add(loadF2(bitLength));
		probList.add(loadF3(bitLength));
		probList.add(loadF4(bitLength));
		probList.add(loadF5(bitLength));
		probList.add(loadF6(bitLength));
		probList.add(loadF7(bitLength));
		probList.add(loadF8(bitLength));
		probList.add(loadAckley(bitLength));
	}
	
	public Problem loadByName(String name, int bitLength){
		name = name.substring(0, 3);
		switch(name){
			case "F1 ":
				return loadF1(bitLength);
			case "F2 ":
				return loadF2(bitLength);
			case "F3 ":
				return loadF3(bitLength);
			case "F4 ":
				return loadF4(bitLength);
			case "F5 ":
				return loadF5(bitLength);
			case "F6 ":
				return loadF6(bitLength);
			case "F7 ":
				return loadF7(bitLength);
			case "F8 ":
				return loadF8(bitLength);
			case "F16":
				return loadAckley(bitLength);
		}
		System.out.println("[BenchmarkLoader] Problem " + name + " not found.");
		return null;
	}
	
	private Problem loadF1(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double dimX = axes[i];
				double y = dimX + 20;
				double ti = 0;
				if(y<0){
					ti = -160 + y*y;
				} else if(y<=15){
					ti = 160.0/15.0 * (y-15);
				} else if(y<=20){
					ti = 200.0/5.0 * (15-y);
				} else if(y>20){
					ti = -200 + Math.pow(y-20, 2);
				}
				total += ti;
			}
			total += 200*axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F1 - Two-peak trap";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = (int)Math.pow(2, DIMENSIONALITY);
		return prob;
	}
	
	private Problem loadF2(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double x = axes[i];
				double ti = 0;
				if(x<0){
					ti = -200 + x*x;
				} else if(x<2.5){
					ti = -80 * (2.5-x);
				} else if(x<5){
					ti = -64*(x-2.5);
				} else if(x<7.5){
					ti = -64*(7.5-x);
				} else if(x<12.5){
					ti = -28*(x-7.5);
				} else if(x<17.5){
					ti = -28*(17.5-x);
				} else if(x<22.5){
					ti = -32*(x-17.5);
				} else if(x<27.5){
					ti = -32*(27.5-x);
				} else if(x<30){
					ti = -80*(x-27.5);
				} else if(x>30){
					ti = -200 + Math.pow(x-30, 2);
				}
				total += ti;
			}
			total += 200*axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F2 - Five-uneven-peak trap";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(2, DIMENSIONALITY);
		prob.numLocalOptima = (int)Math.pow(5, DIMENSIONALITY);
		return prob;
	}
	
	private Problem loadF3(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double x = axes[i] / 20.0;
				double y = x + 0.1;
				double ti = 0;
				if(y<0){
					ti = y*y;
				} else if(y<1){
					double k = 5*Math.PI*y;
					ti = 10 - 15*Math.cos(2*k) + 6*Math.cos(4*k) - Math.cos(6*k); // sin^6(ti)
					ti /= 32.0;
					ti = -ti;
				} else if(y>1){
					ti = y*y;
				}
				total += ti;
			}
			total += axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F3 - Equal minima";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(5, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	private Problem loadF4(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double x = axes[i] / 20.0;
				double y = x + 0.1;
				double ti = 0;
				if(y<0){
					ti = y*y;
				} else if(y<1){
					double k = 5*Math.PI*y;
					double sined = 1.0/32.0 * (10 - 15*Math.cos(2*k) + 6*Math.cos(4*k) - Math.cos(6*k)); // sin^6(ti)
					double other = Math.pow((y-0.1)/0.8, 2);
					other *= -2 * Math.log(2);
					ti = -Math.exp(other) * sined;
				} else if(y>1){
					ti = y*y;
				}
				total += ti;
			}
			total += axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F4 - Decreasing minima";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = (int)Math.pow(5, DIMENSIONALITY);
		return prob;
	}
	
	private Problem loadF5(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double x = axes[i] / 20.0;
				double y = x + 0.079699392688696;
				double ti = 0;
				if(y<0){
					ti = y*y;
				} else if(y<1){
					double k = 5*Math.PI*(Math.pow(y, 0.75) - 0.05);
					double sined = 1.0/32.0 * (10 - 15*Math.cos(2*k) + 6*Math.cos(4*k) - Math.cos(6*k)); // sin^6(ti)
					ti = -sined;
				} else if(y>1){
					ti = y*y;
				}
				total += ti;
			}
			total += axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F5 - Uneven minima";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(5, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	private Problem loadF6(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double[] axeResults = new double[axes.length]; 
			for(int i=0; i<axes.length; i++){
				double x = axes[i] / 5.0;
				double y = x + 2 + i%2;
				axeResults[i] = y;
			}
			double total = 0;
			for(int i=0; i<axeResults.length; i+=2){
				double y = axeResults[i];
				double y2 = axeResults[i+1];
				double first = y*y + y2 - 11;
				double second = y + y2*y2 - 7;
				total += first*first + second*second;
			}
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F6 - Himmelblau's function";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(4, DIMENSIONALITY/2);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	private Problem loadF7(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -3, 2);
			axes[1] = normalize(axesBig, bitLength/DIMENSIONALITY, -2, 2)[1];
			
			double[] axeResults = new double[axes.length]; 
			for(int i=0; i<axes.length; i++){
				double x = axes[i];
				double y = x + (i%2==1 ? -0.089842 : 0.712656);
				axeResults[i] = y;
			}
			double total = 0;
			for(int i=0; i<axeResults.length; i+=2){
				double y = axeResults[i];
				double y2 = axeResults[i+1];
				double first = 4 - 2.1*y*y + Math.pow(y,4)/3.0;
				first *= y*y;
				double second = y*y2;
				double third = (-4 + 4*y2*y2)*y2*y2;
				double full = -4 * (first+second+third);
				total += full;
			}
			total += 4.126514 * axes.length/2.0;
			return total;
		});
		prob.name = "F7 - Six-hump camelback";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(2, DIMENSIONALITY/2);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	private Problem loadF8(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = 0;
			for(int i=0; i<axes.length; i++){
				double x = axes[i] / 5.0;
				double y = x + 4.1112;
				double ti = 0;
				if(y<0.25){
					ti = Math.pow(0.25-y, 2) + Math.sin(10*Math.log(2.5));
				} else if(y<=10){
					ti = Math.sin(10*Math.log(y));
				} else if(y>10){
					ti = Math.pow(y-10, 2) + Math.sin(10*Math.log(10));
				}
				total += ti + 1;
			}
			total /= axes.length;
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F8 - Modified Vincent function";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = (int)Math.pow(6, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	private Problem loadAckley(int bitLength){
		Problem prob = new Problem(null, bitLength, bits -> {
			BigInteger[] axesBig = partitionBitstring(bits, DIMENSIONALITY);
			double[] axes = normalize(axesBig, bitLength/DIMENSIONALITY, -100, 100);
			
			double total = PrimitiveFunctions.f15(axes);
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F16 - Ackley's function";
		prob.dimensionality = DIMENSIONALITY;
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = -1;
		return prob;
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
}
