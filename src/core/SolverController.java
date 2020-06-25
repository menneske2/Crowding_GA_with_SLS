/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;


import algorithm.GAIndividual;
import algorithm.GeneticAlgorithm;
import algorithm.OptimizerConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Fredrik
 */
public class SolverController {
	
	@FXML TextField fxSeed;
	@FXML TextField fxPopSize;
	@FXML TextField fxGenerations;
	@FXML TextField fxElitism;
	@FXML TextField fxCrossover;
	@FXML TextField fxMutation;
	@FXML TextField fxCrowdingCoefficient;
	@FXML TextField fxTestSetProportion;

	
	@FXML Button fxStart;
	@FXML Button fxStop;
	@FXML Label fxGenerationCounter;
	@FXML Label fxScore;
	
	@FXML VBox fxChartArea;
//	private LineChart<Number, Number> bestChart;
//	private LineChart<Number, Number> entropyChart;
//	private LineChart<Number, Number> clusterChart;
	private List<LineChart<Number, Number>> charts;
	
	private Problem prob;
	private OptimizerConfig conf;
	private BlockingQueue comsChannel;
	
	public void initialize(){
		conf = new OptimizerConfig();
		loadConfig();
		fxStop.setDisable(true);
		charts = new ArrayList<>();
	}

	public void setProblem(Problem p){
		prob = p;
		generateCharts();
	}

	@FXML
	public void solveProblem(ActionEvent e){
		
//		for(var series : bestChart.getData()){
//			series.getData().clear();
//		}
//		for(var series : entropyChart.getData()){
//			series.getData().clear();
//		}
		for(var chart : charts){
			for(var series : chart.getData()){
				series.getData().clear();
			}
		}
		
		updateConfig();
		OptimizerConfig copy = conf.clone();
		comsChannel = new LinkedBlockingQueue();
		Runnable optimizer = new GeneticAlgorithm(prob, copy, this, comsChannel);
		Thread thread = new Thread(optimizer);
		thread.start();
		
		fxStop.setDisable(false);
		fxStart.setDisable(true);
	}
	
	@FXML
	public void stopProblem(ActionEvent e){
		comsChannel.add("Stopping...");
		fxStop.setDisable(true);
	}
	
	public void progressReport(int generation, double best, double avg, double entropy, int nNiches){
		if(generation < 8000 || generation%10 == 9){
			fxGenerationCounter.setText("Generation: " + (generation+1));
			// Performance-chart
			charts.get(0).getData().get(0).getData().add(new XYChart.Data(generation, best));
			charts.get(0).getData().get(1).getData().add(new XYChart.Data(generation, avg));
			// Entropy-chart
			charts.get(1).getData().get(0).getData().add(new XYChart.Data(generation, entropy));
			// Cluster-chart
			charts.get(2).getData().get(0).getData().add(new XYChart.Data(generation, nNiches));
			fxScore.setText("Best score: " + best);
			
//			clusterChart.getData().get(0).getData().clear();
//			List<XYChart.Data<Number, Number>> temp = new ArrayList<>();
//			for(int i=0; i<JEvo.size(); i++){
//				temp.add(new XYChart.Data(i, JEvo.get(i)));
//			}
//			clusterChart.getData().get(0).getData().addAll(temp);
		}
	}
	
	public void registerSolution(Problem p, GAIndividual best, long timeSpent){
		fxStart.setDisable(false);
		fxStop.setDisable(true);
		System.out.println("Features used: " + best.numberOfFeatures() + "/" + best.genome.length + "\tFinal solution: " + best.getGenomeAsString());
		System.out.println("Time taken: " + (int)Math.floor(timeSpent/(1000*60)) + "m" + (timeSpent/1000)%60 + "s");
	}
	
	private void generateCharts(){
		fxChartArea.getChildren().removeAll();
		charts.clear();
		charts.add(generateChart("Generation", "R-measure (negative values set to 0)", new String[]{"Best R-measure", "Average R-measure"}));
		charts.add(generateChart("Generation", "Entropy", new String[]{"Entropy"}));
		charts.add(generateChart("Generation", "Clustering data", new String[]{"Number of niches"}));
	}
	
	private LineChart generateChart(String xName, String yName, String[] seriesNames){		
		Axis xAxis = new NumberAxis();
		Axis yAxis = new NumberAxis();
		xAxis.setLabel(xName);
		yAxis.setLabel(yName);
		
		LineChart chart = new LineChart<>(xAxis, yAxis);
		chart.setAnimated(false);
		
		for(String s : seriesNames){
			XYChart.Series series = new XYChart.Series();
			series.setName(s);
			chart.getData().add(series);
		}
		
		StackPane pane = new StackPane();
		pane.getChildren().add(chart);
		fxChartArea.getChildren().add(pane);
		
		return chart;
	}

	private void loadConfig(){
		fxSeed.setText("" + conf.SEED);
		fxGenerations.setText("" + conf.GENERATIONS);
		fxPopSize.setText("" + conf.POPULATION_SIZE);
		fxElitism.setText("" + conf.ELITIST_POPULATION);
		fxCrossover.setText("" + conf.CROSSOVER_CHANCE);
		fxMutation.setText("" + conf.MUTATION_CHANCE);
		fxCrowdingCoefficient.setText("" + conf.CROWDING_COEFFICIENT);
	}
	
	private void updateConfig(){
		conf.SEED = Integer.parseInt(fxSeed.getText());
		conf.GENERATIONS = Integer.parseInt(fxGenerations.getText());
		conf.POPULATION_SIZE = Integer.parseInt(fxPopSize.getText());
		conf.ELITIST_POPULATION = Integer.parseInt(fxElitism.getText());
		conf.CROSSOVER_CHANCE = Float.parseFloat(fxCrossover.getText());
		conf.MUTATION_CHANCE = Float.parseFloat(fxMutation.getText());
		conf.CROWDING_COEFFICIENT = Float.parseFloat(fxCrowdingCoefficient.getText());
	}

}
