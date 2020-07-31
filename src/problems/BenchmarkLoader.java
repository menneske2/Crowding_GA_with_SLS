/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkLoader {
	
	private static final int BITLENGTH = 512;
	private static final int DIMENSIONALITY = 2;
	
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
		BenchmarkProblem prob = new BenchmarkProblem(1, bitLength, DIMENSIONALITY);
		prob.name = "F1 - Expanded two-peak trap";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 1;
			}
			public int numLocal(int d){
				return (int)Math.pow(2, d)-1;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF2(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(2, bitLength, DIMENSIONALITY);
		prob.name = "F2 - Expanded five-uneven-peak trap";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(2,d);
			}
			public int numLocal(int d){
				return (int)Math.pow(5, d)-(int)Math.pow(2,d);
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
			add(30d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF3(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(3, bitLength, DIMENSIONALITY);
		prob.name = "F3 - Expanded equal minima";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(5,d);
			}
			public int numLocal(int d){
				return 0;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
			add(0.2d);
			add(0.4d);
			add(0.6d);
			add(0.8d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF4(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(4, bitLength, DIMENSIONALITY);
		prob.name = "F4 - Expanded decreasing minima";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 1;
			}
			public int numLocal(int d){
				return (int)Math.pow(5, d)-1;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF5(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(5, bitLength, DIMENSIONALITY);
		prob.name = "F5 - Expanded uneven minima";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(5,d);
			}
			public int numLocal(int d){
				return 0;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
			add(0.166955);
			add(0.370927);
			add(0.601720);
			add(0.854195);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF6(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(6, bitLength, DIMENSIONALITY);
		prob.name = "F6 - Expanded Himmelblau's function";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(4,d/2);
			}
			public int numLocal(int d){
				return 0;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF7(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(7, bitLength, DIMENSIONALITY);
		prob.name = "F7 - Expanded six-hump camel back";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(2,d/2);
			}
			public int numLocal(int d){
				return 0;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(0d);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF8(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(8, bitLength, DIMENSIONALITY);
		prob.name = "F8 - Modified Vincent function";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return (int)Math.pow(6,d);
			}
			public int numLocal(int d){
				return 0;
			}
		};
		prob.setGlobalOptima(new ArrayList<>(){{
			add(-3.7782);
			add(-3.4870);
			add(-2.9411);
			add(-1.9179);
			add(0d);
			add(3.5951);
		}});
		return prob;
	}
	
	public static BenchmarkProblem loadF9(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(9, bitLength, DIMENSIONALITY);
		prob.name = "F9 - Composition function 1";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF10(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(10, bitLength, DIMENSIONALITY);
		prob.name = "F10 - Composition function 2";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF11(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(11, bitLength, DIMENSIONALITY);
		prob.name = "F11 - Composition function 3";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF12(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(12, bitLength, DIMENSIONALITY);
		prob.name = "F12 - Composition function 4";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF13(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(13, bitLength, DIMENSIONALITY);
		prob.name = "F13 - Composition function 5";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF14(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(14, bitLength, DIMENSIONALITY);
		prob.name = "F14 - Composition function 6";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	public static BenchmarkProblem loadF15(int bitLength){
		BenchmarkProblem prob = new BenchmarkProblem(15, bitLength, DIMENSIONALITY);
		prob.name = "F15 - Composition function 7";
		prob.numOptimaCalc = new NumOptimaCalculator(){
			public int numGlobal(int d){
				return 5;
			}
			public int numLocal(int d){
				return 0;
			}
		};
		return prob;
	}
	
	
}
