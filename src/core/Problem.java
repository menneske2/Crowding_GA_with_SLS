/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class Problem {
	
	private final String name;
	public double[][] xs;
	public double[] ys;
	
	public Problem(String name, List<List<Float>> data){
		this.name = name;
		
		parseDataFile(data);
	}
	
	
	private void parseDataFile(List<List<Float>> data){
		this.xs = new double[data.size()][data.get(0).size()-2];
		this.ys = new double[data.size()];
		
		for(int i=0; i<data.size(); i++){
			var line = data.get(i);
			ys[i] = line.remove(line.size()-1);
			line.remove(0); // the first number on each line is the index.
			for(int j=0; j<line.size(); j++)
				xs[i][j] = line.get(j);
				
		}
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
