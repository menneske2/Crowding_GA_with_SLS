/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

/**
 *
 * @author Fredrik-Oslo
 */
public class OptimizerConfig implements Cloneable{
	
	public static final int NO_SEED = -1;
	
	public int SEED = -1;
	public int GENERATIONS = 200;
	public int POPULATION_SIZE = 100;
	
	@Override
	public OptimizerConfig clone(){
		try{
		return (OptimizerConfig) super.clone();
		} catch(Exception e){
			e.printStackTrace();
			System.exit(98);
			return null;
		}
	}
	
}
