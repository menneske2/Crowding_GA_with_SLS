/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.List;
import problems.Problem;

/**
 *
 * @author Fredrik-Oslo
 */
public interface DataReceiver {
	
	public boolean requiresWaiting(); // Whether or not the optimizer should invoke Platform.runLater() for all progress updates.
	
	public void progressReport(int generation, int FEs, List<GAIndividual> pop, double bestNoPunish, double avgNoPunish, double entropy, double nNiches, 
			double mutaChance, double crossChance, double crowdingFactor);
	
	public void registerSolution(Problem p, List<GAIndividual> pop, long timeSpent);
	
}
