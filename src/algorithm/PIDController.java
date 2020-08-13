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
	
	public void updateCrowdingScalingFactor(OptimizerConfig conf, int numNiches){ 
		double error = conf.ACTIVE_NICHES - numNiches;
		double controlRate = 0.02;
		double deltaCC = controlRate * error;
		conf.CROWDING_SCALING_FACTOR += deltaCC;
		conf.CROWDING_SCALING_FACTOR = Math.max(conf.CROWDING_SCALING_FACTOR, 0);
		conf.CROWDING_SCALING_FACTOR = Math.min(conf.CROWDING_SCALING_FACTOR, 2);
//		System.out.format("[PIDController] Crowding factor: %.2f\tdelta: %.2f\terror: %.1f\n", conf.CROWDING_SCALING_FACTOR, deltaCC, error);
	}
	
}
