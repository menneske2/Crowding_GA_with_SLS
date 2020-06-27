/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.List;

/**
 *
 * @author Fredrik-Oslo
 */
public class PIDController {
	
	private double prevNiches = -1; // The number of niches is noisy. using this to normalize.
	private boolean enabled = true;
	
	public PIDController(){
		
	}
	
	public void disable(){
		enabled = false;
	}
	
	public double updateCrowdingScalingFactor(OptimizerConfig conf, List<Niche> niches){ 
		// Smoothing out niche count because it's a noisy measurement.
		double nNiches = niches.size();
		if(prevNiches != -1){
			nNiches = (niches.size() + prevNiches) / 2;
		}
		prevNiches = nNiches;
		if(!enabled) return nNiches;
		
		double error = conf.ACTIVE_NICHES - nNiches;
		double controlRate = 0.02;
		double deltaCC = controlRate * error;
		conf.CROWDING_SCALING_FACTOR += deltaCC;
		conf.CROWDING_SCALING_FACTOR = Math.max(conf.CROWDING_SCALING_FACTOR, 0);
		conf.CROWDING_SCALING_FACTOR = Math.min(conf.CROWDING_SCALING_FACTOR, 5);
		System.out.format("[PIDController] Scaling factor: %.2f\tdelta: %.2f\terror: %.1f\n", conf.CROWDING_SCALING_FACTOR, deltaCC, error);
		return nNiches;
	}
	
}
