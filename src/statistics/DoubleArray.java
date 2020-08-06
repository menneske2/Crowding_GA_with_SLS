/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

/**
 *
 * @author Fredrik-Oslo
 */
public class DoubleArray implements Comparable<DoubleArray>{
	
	public final double[] values;
	
	public DoubleArray(double[] arr){
		this.values = arr;
	}
	public DoubleArray(int len){
		this.values = new double[len];
	}
	
	public double getSum(){
		double sum = 0;
		for(int i=0; i<values.length; i++)
			sum += values[i];
		return sum;
	}
	
	public double getMean(){
		return this.getSum() / (double)values.length;
	}

	@Override
	public int compareTo(DoubleArray other) {
		double a = this.getSum();
		double b = other.getSum();
		if(a == b)
			return 0;
		return a<b ? -1 : 1;
	}
	
	@Override
	public String toString(){
		int decimals = 3;
		int meanDecimals = 1;
		String out = "[";
		for(int i=0; i<values.length; i++){
			double toWrite = values[i];
			if(decimals != -1)
				toWrite = Math.round(Math.pow(10, decimals)*toWrite) / Math.pow(10, decimals);
			out += toWrite + ", ";
		}
		out = out.substring(0, out.length()-2) + "]";
		out += " Mean: " + Math.round(Math.pow(10, meanDecimals)*this.getMean()) / Math.pow(10, meanDecimals);
		out += "(" + Math.round(Math.pow(10, meanDecimals)*PerformanceMeasures.getSTD(values)) / Math.pow(10, meanDecimals) + ")";
		
		return out;
	}
}
