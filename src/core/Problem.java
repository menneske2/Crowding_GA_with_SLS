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
	private List<List<Float>> xs;
	private List<Float> ys;
	
	public Problem(String name, List<List<Float>> data){
		this.name = name;
		
		parseDataFile(data);
	}
	
	
	private void parseDataFile(List<List<Float>> data){
		this.xs = new ArrayList<>();
		this.ys = new ArrayList<>();
		
		for(var line : data){
			ys.add(line.remove(line.size()-1));
			xs.add(line);
		}
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
