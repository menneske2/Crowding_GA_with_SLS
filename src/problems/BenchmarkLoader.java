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
	
	/**
	 * Adds the problems defined by all functions from this class that start with "loadF".
	 * @param probList the list to add shit to.
	 */
	public static void loadBenchmarkProblems(List<Problem> probList){
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
				Problem p = (BenchmarkProblem) m.invoke(BenchmarkLoader.class);
				probList.add(p);
			} catch(Exception e){
				e.printStackTrace();
				System.exit(404);
			}
			
		}
	}
	
	public static BenchmarkProblem loadByName(String name){
		name = name.split("-")[0].strip();
		
		try{
			Method meth = BenchmarkLoader.class.getMethod("load"+name);
			BenchmarkProblem p = (BenchmarkProblem) meth.invoke(BenchmarkLoader.class);
			return p;
		} catch(Exception e){
			e.printStackTrace();
			System.exit(404);
		}
		System.exit(404);
		return null;
	}
	
	public static BenchmarkProblem loadF1(){
		BenchmarkProblem prob = new BenchmarkProblem(1);
		prob.name = "F1 - Expanded two-peak trap";
		return prob;
	}
	
	public static BenchmarkProblem loadF2(){
		BenchmarkProblem prob = new BenchmarkProblem(2);
		prob.name = "F2 - Expanded five-uneven-peak trap";
		return prob;
	}
	
	public static BenchmarkProblem loadF3(){
		BenchmarkProblem prob = new BenchmarkProblem(3);
		prob.name = "F3 - Expanded equal minima";
		return prob;
	}
	
	public static BenchmarkProblem loadF4(){
		BenchmarkProblem prob = new BenchmarkProblem(4);
		prob.name = "F4 - Expanded decreasing minima";
		return prob;
	}
	
	public static BenchmarkProblem loadF5(){
		BenchmarkProblem prob = new BenchmarkProblem(5);
		prob.name = "F5 - Expanded uneven minima";
		return prob;
	}
	
	public static BenchmarkProblem loadF6(){
		BenchmarkProblem prob = new BenchmarkProblem(6);
		prob.name = "F6 - Expanded Himmelblau's function";
		return prob;
	}
	
	public static BenchmarkProblem loadF7(){
		BenchmarkProblem prob = new BenchmarkProblem(7);
		prob.name = "F7 - Expanded six-hump camel back";
		return prob;
	}
	
	public static BenchmarkProblem loadF8(){
		BenchmarkProblem prob = new BenchmarkProblem(8);
		prob.name = "F8 - Modified Vincent function";
		return prob;
	}
	
	public static BenchmarkProblem loadF9(){
		BenchmarkProblem prob = new BenchmarkProblem(9);
		prob.name = "F9 - Composition function 1";
		return prob;
	}
	
	public static BenchmarkProblem loadF10(){
		BenchmarkProblem prob = new BenchmarkProblem(10);
		prob.name = "F10 - Composition function 2";
		return prob;
	}
	
	public static BenchmarkProblem loadF11(){
		BenchmarkProblem prob = new BenchmarkProblem(11);
		prob.name = "F11 - Composition function 3";
		return prob;
	}
	
	public static BenchmarkProblem loadF12(){
		BenchmarkProblem prob = new BenchmarkProblem(12);
		prob.name = "F12 - Composition function 4";
		return prob;
	}
	
	public static BenchmarkProblem loadF13(){
		BenchmarkProblem prob = new BenchmarkProblem(13);
		prob.name = "F13 - Composition function 5";
		return prob;
	}
	
	public static BenchmarkProblem loadF14(){
		BenchmarkProblem prob = new BenchmarkProblem(14);
		prob.name = "F14 - Composition function 6";
		return prob;
	}
	
	public static BenchmarkProblem loadF15(){
		BenchmarkProblem prob = new BenchmarkProblem(15);
		prob.name = "F15 - Composition function 7";
		return prob;
	}
	
	
}
