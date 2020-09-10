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
	
	public int SEED = NO_SEED;
	public int GENERATIONS = -1;
	public int FITNESS_EVALUATIONS = -1;
	public int POPULATION_SIZE = 150;
	public int ELITIST_NICHES = 20;
	public int TOURNAMENT_SIZE = 2;
	public float CROSSOVER_CHANCE = 0.7f;
	public float MUTATION_CHANCE = 0.2f;
	public float CROWDING_SCALING_FACTOR = 0.0f;
	public float NICHING_EPSILON = 0.03f;
	public boolean FC_ENABLED = true;
	public float FC_CONTROL_RATE = 0.02f;
	public int FC_WANTED_NICHES = 25;
	public boolean BITFLIP_MUTATION = false; // if false, use "turn off 10 random indices" mutation.
	
	// SLS-parameters
	public boolean SLS_ENABLED = true;
	public boolean CULLING_ENABLED = true;
	public int MAX_NICHE_SIZE = 3;
	public boolean SLS_TAKE_FIRST_IMPROVEMENT = true; // if false, checks all possibilities before moving in greedy step.
	public int SLS_MAX_FLIPS = 30;
	public int SLS_MAX_FLIPS_IN_GREEDY = 5; // If greedy search can't find an improvement after x tries, assume local optimality.
	public float SLS_P_NOISY = 0.3f;
	
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
