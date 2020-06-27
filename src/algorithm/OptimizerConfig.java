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
	public int GENERATIONS = -1;
	public int POPULATION_SIZE = 50;
	
	public int ELITIST_POPULATION = 2;
	public float CROSSOVER_CHANCE = 0.7f;
	public float MUTATION_CHANCE = 0.2f;
	public float CROWDING_SCALING_FACTOR = 0.5f;
	public boolean PID_ENABLED = true;
	public float PID_CONTROL_RATE = 0.02f;
	public int ACTIVE_NICHES = 5; // if there are more than n active niches, SLS is performed on an inferior niche to find the local optimum, then the individual is reinitialized.
	
	// SLS-parameters
	public boolean SLS_Enabled = false;
	public boolean SLS_TAKE_FIRST_IMPROVEMENT = true; // if false, checks all possibilities before moving in greedy step.
	public int SLS_MAX_FLIPS = 50;
	public int SLS_MAX_FLIPS_IN_GREEDY = 30; // If greedy search can't find an improvement after x tries, assume local optimality.
	public float SLS_P_NOISY = 0.2f;
	
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
