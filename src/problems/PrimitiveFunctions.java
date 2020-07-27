/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

/**
 * Implementations of f9-f24 as described in this paper: https://www.sciencedirect.com/science/article/pii/S221065021500053X
 * @author Fredrik-Oslo
 */
public class PrimitiveFunctions {
	
	// Sphere
	public static double f9(double[] x){
		double sum = 0;
		for(double val : x){
			sum += val*val;
		}
		return sum;
	}
	
	// High conditioned elliptic
	public static double f10(double[] x){
		double sum = 0;
		for(int i=0; i<x.length; i++){
			sum += Math.pow(Math.pow(10,6), (i-1)/(x.length-1)) * Math.pow(x[i], 2);
		}
		return sum;
	}
	
	// Bent cigar.
	public static double f11(double[] x){
		double sum = 0;
		for(int i=1; i<x.length; i++){
			sum += x[i]*x[i];
		}
		sum *= Math.pow(10,6);
		sum += x[0]*x[0];
		return sum;
	}
	
	// Discus
	public static double f12(double[] x){
		double sum = Math.pow(10, 6) * x[0]*x[0];
		for(int i=1; i<x.length; i++){
			sum += x[i]*x[i];
		}
		return sum;
	}
	
	// Different powers
	public static double f13(double[] x){
		double sum = 0;
		for(int i=0; i<x.length; i++){
			double exponent = (i-1)/(x.length-1);
			exponent = 2 + 4*exponent;
			sum += Math.pow(Math.abs(x[i]), exponent);
		}
		return Math.sqrt(sum);
	}
	
	// Rosenbrock's
	public static double f14(double[] x){
		double sum = 0;
		for(int i=0; i<x.length-1; i++){
			double first = 100 * Math.pow(x[i]*x[i] - x[i+1], 2);
			double second = Math.pow(x[i]-1, 2);
			sum += first + second;
		}
		return sum;
	}
	
	// Ackley's
	public static double f15(double[] x){
		double first = 0;
		for(int i=0; i<x.length; i++){
			first += x[i]*x[i];
		}
		first = Math.sqrt(first/x.length);
		first = 20*Math.exp(-0.2 * first);
		
		double second = 0;
		for(int i=0; i<x.length; i++){
			second += Math.cos(2*Math.PI*x[i]);
		}
		second = Math.exp(second/x.length);
		
		return -first -second +20 +Math.E;
	}
	
	// Weierstrass
	public static double f16(double[] x){
		double a = 0.5;
		double b = 3;
		int kMax = 20;
		
		double first = 0;
		for(int i=0; i<x.length; i++){
			for(int k=0; k<kMax; k++){
				double inner = 2*Math.PI*Math.pow(b, k) * (x[i] + 0.5);
				inner = Math.pow(a, k) * Math.cos(inner);
				first += inner;
			}
		}
		
		double second = 0;
		for(int k=0; k<kMax; k++){
			double inner = 2*Math.PI*Math.pow(b, k) * 0.5;
			inner = Math.pow(a, k) * Math.cos(inner);
			second += inner;
		}
		second *= x.length;
		return first - second;
	}
	
	// Griewank's
	public static double f17(double[] x){
		double first = 0;
		for(int i=0; i<x.length; i++){
			first += x[i]*x[i] / 4000;
		}
		double sec = Math.cos(x[0] / 1) + 1;
		for(int i=1; i<x.length; i++){
			sec *= Math.cos(x[i] / Math.sqrt(i+1)) + 1;
		}
		return first - sec;
	}
	
	// Rastrigin's
	public static double f18(double[] x){
		double sum = 0;
		for(int i=0; i<x.length; i++){
			double val = 2*Math.PI*x[i];
			sum += x[i]*x[i] - 10*Math.cos(val) + 10;
		}
		return sum;
	}
	
	// Modified Schwefel's
	public static double f19(double[] x){
		double firstTerm = 418.9829 * x.length;
		double secTerm = 0;
		for(int i=0; i<x.length; i++){
			double z = x[i] + 420.9687462275036;
			double gz = 0;
			if(z<-500){
				double root = Math.sqrt(Math.abs(mod(z,500) - 500));
				double first = (mod(z,500)-500) * Math.sin(root);
				double sec = Math.pow(z+500,2) / 10000*x.length;
				gz = first - sec;
			} else if(z<=500){
				gz = Math.pow(Math.abs(z), 0.5);
				gz = z * Math.sin(gz);
			} else if(z>500){
				double root = Math.sqrt(Math.abs(500 - mod(z,500)));
				double first = (500 - mod(z,500)) * Math.sin(root);
				double sec = Math.pow(z-500,2) / 10000*x.length;
				gz = first - sec;
			}
			secTerm += gz;
		}
		return firstTerm - secTerm;
	}
	
	// Katsuura
	public static double f20(double[] x){
		double sum = 1;
		for(int i=0; i<x.length; i++){
			double inner = 0;
			for(int j=1; j<=32; j++){
				double val = Math.pow(2,j)*x[i] - Math.round(Math.pow(2,j)*x[i]);
				val = Math.abs(val) / Math.pow(2,j);
				inner += val;
			}
			inner = 1 + i*inner;
			inner = Math.pow(inner, 10/Math.pow(x.length, 1.2));
			sum *= inner;
		}
		sum *= 10/Math.pow(x.length, 2);
		sum -= 10/Math.pow(x.length, 2);
		return sum;
	}
	
	// HappyCat function
	public static double f21(double[] x){
		double alpha = 1/4;
		
		double first = 0;
		for(int i=0; i<x.length; i++){
			first += x[i]*x[i];
		}
		first -= x.length;
		first = Math.pow(Math.abs(first), alpha);
		
		double sec = 0;
		double terms = 0;
		double powerTerms = 0;
		for(int i=0; i<x.length; i++){
			terms += x[i];
			powerTerms += x[i]*x[i];
		}
		sec = 0.5*powerTerms + terms;
		sec /= x.length;
		return first + sec + 0.5;
	}
	
	// HGBat
	public static double f22(double[] x){
		double first = 0;
		double val1 = 0;
		double val2 = 0;
		for(int i=0; i<x.length; i++){
			val1 += x[i]*x[i];
			val2 += x[i];
		}
		first = val1*val1 - val2*val2;
		first = Math.pow(Math.abs(first), 2);
		
		double second = 0.5 * val1 + val2;
		second /= x.length;
		
		return first + second + 0.5;
	}
	
	// Expanded Griewank’s plus Rosenbrock’s
	public static double f23(double[] x){
		double sum = 0;
		for(int i=0; i<x.length; i++){
			double[] tempX = new double[]{x[i], x[i%x.length]};
			sum += f17(new double[]{f14(tempX)});
		}
		return sum;
	}
	
	// Expanded Scaffer’s F6 Function
	public static double f24(double[] x){
		double sum = 0;
		for(int i=0; i<x.length; i++){
			double tempX = x[i];
			double tempY = x[i%x.length];
			sum += scafferF6(tempX, tempY);
		}
		return sum;
	}
	
	// Scaffer's F6. not used in compositions, but used in f24.
	private static double scafferF6(double x, double y){
		double upper = Math.hypot(x, y);
		upper = 0.5 * (1.0-Math.cos(2*upper)) - 0.5; // sin^2(x) - 0.5
		double downer = Math.hypot(x, y);
		downer = 1 + 0.001*downer;
		downer *= downer;
		
		return 0.5 + upper/downer;
	}
	
	// Appearantly the standard math library doesnt have this.
	private static double mod(double in, double ceil){
		while(in<0){
			in += ceil;
		}
		return in%ceil;
	}
	
	
}
