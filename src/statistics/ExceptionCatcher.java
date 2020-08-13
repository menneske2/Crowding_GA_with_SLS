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
public class ExceptionCatcher implements Runnable{
	
	private final Runnable runner;
	private DataHarvester harvester;
	
	public ExceptionCatcher(Runnable runner, DataHarvester harvester){
		this.runner = runner;
		this.harvester = harvester;
	}

	@Override
	public void run() {
		try{
			runner.run();
		} catch(Exception e){
//			e.printStackTrace();
//			System.exit(4);
			harvester.reportCrash();
		}
	}
	
}
