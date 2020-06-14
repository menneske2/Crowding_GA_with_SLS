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
	
	public int SEED = 72;
	public int GENERATIONS = 1;
	public int POPULATION_SIZE = 1;
	
	public float TEST_SET_PROPORTION = 0.5f;
	
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
