/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkLoader {
	
	private static final int BITLENGTH = 512;
	private static final int DIMENSIONALITY = 2;
	private static final int SEED = -1;
	
	/**
	 * Adds the problems defined by all functions from this class that start with "loadF".
	 * @param probList the list to add shit to.
	 */
	public static void loadBenchmarkProblems(List<Problem> probList){
		int bitLength = BITLENGTH;
		
		List<Method> methods = new ArrayList<>(Arrays.asList(BenchmarkLoader.class.getMethods()));
		// Purging non-problem methods.
		for(int i=methods.size()-1; i>=0; i--){
			if(!methods.get(i).getName().startsWith("loadF")){
				methods.remove(i);
			}
		}
		// Sorting methods by name
		Collections.sort(methods, (Method m1, Method m2) -> {
			int num1 = Integer.parseInt(m1.getName().split("-")[0].strip().substring(5));
			int num2 = Integer.parseInt(m2.getName().split("-")[0].strip().substring(5));
			if(num1 == num2)
				return 0;
			return num1<num2 ? -1 : 1;
		});
		// Adding problem instances.
		for(int i=0; i<methods.size(); i++){
			Method m = methods.get(i);
			try{
				Problem p = (BenchmarkProblem) m.invoke(BenchmarkLoader.class, bitLength);
				probList.add(p);
			} catch(Exception e){
				e.printStackTrace();
				System.exit(404);
			}
			
		}
	}
	
	public static BenchmarkProblem loadByName(String name, int bitLength){
		name = name.split("-")[0].strip();
		
		try{
			Method meth = BenchmarkLoader.class.getMethod("load"+name, int.class);
			return (BenchmarkProblem) meth.invoke(BenchmarkLoader.class, bitLength);
		} catch(Exception e){
			e.printStackTrace();
			System.exit(404);
		}
		System.exit(404);
		return null;
	}
	
	public static BenchmarkProblem loadF1(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = (int)Math.pow(2, DIMENSIONALITY);
		return prob;
	}
	
	public static BenchmarkProblem loadF2(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = (int)Math.pow(2, DIMENSIONALITY);
		prob.numLocalOptima = (int)Math.pow(5, DIMENSIONALITY);
		return prob;
	}
	
	public static BenchmarkProblem loadF3(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = (int)Math.pow(5, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	public static BenchmarkProblem loadF4(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = (int)Math.pow(5, DIMENSIONALITY);
		return prob;
	}
	
	public static BenchmarkProblem loadF5(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = (int)Math.pow(5, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	public static BenchmarkProblem loadF6(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = (int)Math.pow(4, DIMENSIONALITY/2);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	public static BenchmarkProblem loadF7(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -3, 2);
			axes[1] = normalize(axesBig, bitLength/dims, -2, 2)[1];
			
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
		prob.numGlobalOptima = (int)Math.pow(2, DIMENSIONALITY/2);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	public static BenchmarkProblem loadF8(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
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
		prob.numGlobalOptima = (int)Math.pow(6, DIMENSIONALITY);
		prob.numLocalOptima = 0;
		return prob;
	}
	
	public static CompositionBenchmarkProblem loadF9(int bitLength){
		String[] fNames = new String[]{"f9", "f9", "f10", "f10", "f11", "f11", "f12", "f12", "f13", "f13"};
		double[][] fMultipliers = constructMultiplierArray(fNames);
		double[] ranges = new double[]{10, 20, 10, 20, 10, 20, 10, 20, 10, 20};
		double[] lambdas = new double[]{1, 1, 1e-6, 1e-6, 1e-6, 1e-6, 1e-4, 1e-4, 1e-5, 1e-5};
		double[] biases = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		double[] searchArea = new double[]{-100, 100};
		double[][] shifts = generateShifts(SEED, 0.5, searchArea, new int[]{fNames.length, DIMENSIONALITY});
		
		CompositionBenchmarkProblem prob = new CompositionBenchmarkProblem(bitLength, DIMENSIONALITY, searchArea, fNames, fMultipliers, ranges, biases, lambdas, shifts);
		prob.name = "F9 - Composition function 1";
		prob.numGlobalOptima = 5;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	public static CompositionBenchmarkProblem loadF10(int bitLength){
		String[] fNames = new String[]{"f10", "f10", "f13", "f13", "f14", "f14", "f12", "f12", "f9", "f9"};
		double[][] fMultipliers = constructMultiplierArray(fNames);
		double[] ranges = new double[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		double[] lambdas = new double[]{1e-5,1e-5,1e-6,1e-6,1e-6,1e-6,1e-4,1e-4,1,1};
		double[] biases = new double[]{0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
		
		double[] searchArea = new double[]{-20, 20};
		double[][] shifts = generateShifts(SEED, 1, searchArea, new int[]{fNames.length, DIMENSIONALITY});
		
		CompositionBenchmarkProblem prob = new CompositionBenchmarkProblem(bitLength, DIMENSIONALITY, searchArea, fNames, fMultipliers, ranges, biases, lambdas, shifts);
		prob.name = "F10 - Composition function 2";
		prob.numGlobalOptima = 5;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	public static CompositionBenchmarkProblem loadF11(int bitLength){
		String[] fNames = new String[]{"f14", "f14", "f18", "f18", "f21", "f21", "f24", "f24", "f19", "f19"};
		double[][] fMultipliers = constructMultiplierArray(fNames, "f14",2.048,100d, "f18",5.12,100d, "f21",5d,100d, "f19",1000d,100d);
		double[] ranges = new double[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
		double[] lambdas = new double[]{0.1, 0.1, 10, 10, 10, 10, 100, 100, 1, 1};
		double[] biases = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		double[] searchArea = new double[]{-100, 100};
		double[][] shifts = generateShifts(SEED, 1, searchArea, new int[]{fNames.length, DIMENSIONALITY});
		
		CompositionBenchmarkProblem prob = new CompositionBenchmarkProblem(bitLength, DIMENSIONALITY, searchArea, fNames, fMultipliers, ranges, biases, lambdas, shifts);
		prob.name = "F11 - Composition function 3";
		prob.numGlobalOptima = 5;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	public static CompositionBenchmarkProblem loadF12(int bitLength){
		String[] fNames = new String[]{"f14", "f14", "f15", "f15", "f21", "f21", "f24", "f24", "f19", "f19"};
		double[][] fMultipliers = constructMultiplierArray(fNames, "f14",2.048,100d, "f15",5.12,100d, "f21",5d,100d, "f19",1000d,100d);
		double[] ranges = new double[]{10, 10, 20, 20, 30, 30, 40, 40, 50, 50};
		double[] lambdas = new double[]{0.1, 0.1, 10, 10, 10, 10, 100, 100, 1, 1};
		double[] biases = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		double[] searchArea = new double[]{-100, 100};
		double[][] shifts = generateShifts(SEED, 0.5, searchArea, new int[]{fNames.length, DIMENSIONALITY});
		
		CompositionBenchmarkProblem prob = new CompositionBenchmarkProblem(bitLength, DIMENSIONALITY, searchArea, fNames, fMultipliers, ranges, biases, lambdas, shifts);
		prob.name = "F12 - Composition function 4";
		prob.numGlobalOptima = 5;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	public static CompositionBenchmarkProblem loadF13(int bitLength){
		String[] fNames = new String[]{"f14", "f22", "f18", "f15", "f16", "f20", "f24", "f23", "f21", "f19"};
		double[][] fMultipliers = constructMultiplierArray(fNames, "f14",2.048,100d, "f22",5d,100d, "f18",5.12d,100d, "f16",0.5d,100d, "f20",5d,100d, "f23",5d,100d, "f21",5d,100d, "f19",1000d,100d);
		double[] ranges = new double[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		double[] lambdas = new double[]{0.1, 10, 10, 0.1, 2.5, 1e-3, 100, 2.5, 10, 1};
		double[] biases = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		double[] searchArea = new double[]{-10, 10};
		double[][] shifts = generateShifts(SEED, 1, searchArea, new int[]{fNames.length, DIMENSIONALITY});
		
		CompositionBenchmarkProblem prob = new CompositionBenchmarkProblem(bitLength, DIMENSIONALITY, searchArea, fNames, fMultipliers, ranges, biases, lambdas, shifts);
		prob.name = "F13 - Composition function 5";
		prob.numGlobalOptima = 5;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	
	public static BenchmarkProblem loadF16(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(bitLength, DIMENSIONALITY, (bits, dims) -> {
			BigInteger[] axesBig = partitionBitstring(bits, dims);
			double[] axes = normalize(axesBig, bitLength/dims, -100, 100);
			
			double total = PrimitiveFunctions.f15(axes);
			return -total; // minus because its a minimization problem. todo: add the lowest value here so that it goes from 0 to best.
		});
		prob.name = "F16 - Ackley's function";
		prob.numGlobalOptima = 1;
		prob.numLocalOptima = -1;
		return prob;
	}
	
	private static double[][] constructMultiplierArray(String[] fNames, Object ... in){
		double[][] mults = new double[fNames.length][2];
		for(int i=0; i<fNames.length; i++){
			mults[i] = new double[]{1, 1};
		}
		
		for(int i=0; i<fNames.length; i++){
			for(int j=0; j<in.length; j+=3){
				String identifier = (String) in[j];
				if(fNames[i].equals(identifier)){
					mults[i] = new double[]{(double) in[j+1], (double) in[j+2]};
				}
			}
		}
		return mults;
	}
	
	private static double[][] generateShifts(int seed, double noiseRatio, double[] searchArea, int[] dimensions){
		double[][] shifts = new double[dimensions[0]][dimensions[1]];
		Random rng = new Random();
		if(seed != -1)
			rng.setSeed(seed);
		
		double diff = searchArea[1] - searchArea[0];
		diff *= noiseRatio; // Dont wanna shift the functions too drastically.
		
		for(int i=0; i<dimensions[0]; i++){
			for(int j=0; j<dimensions[1]; j++){
				shifts[i][j] = (-diff/2) + rng.nextDouble()*diff;
			}
		}
		return shifts;
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
