/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import algorithm.GAIndividual;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import problems.BenchmarkProblem;
import smile.math.distance.Distance;

/**
 *
 * @author Fredrik-Oslo
 */
public class PerformanceMeasures {

	public static void savePerformanceChart(String[] names, List<List<double[]>> lines, double lowerBound, String filename){
		Axis xAxis = new NumberAxis();
		Axis yAxis = new NumberAxis(lowerBound, 1.0, 0.05);
		xAxis.setLabel("Fitness evaluations");
		yAxis.setLabel("Classification accuracy");

		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setAnimated(false);

		for(int i=0; i<names.length; i++){
			var line = lines.get(i);
			XYChart.Series series = new XYChart.Series();
			series.setName(names[i]);
			for(var point : line){
				XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(point[0], point[1]);
				Rectangle rect = new Rectangle(0,0);
				rect.setVisible(false);
				data.setNode(rect);
				series.getData().add(data);
			}
			chart.getData().add(series);
		}

		Stage stage = new Stage();
		Scene scene = new Scene(chart);
		stage.setScene(scene);
		stage.setTitle("Performance chart");
		stage.show();

		Image im = scene.snapshot(null);
		try{
			ImageIO.write(SwingFXUtils.fromFXImage(im, null), "png", new File(filename));
		} catch(Exception e){
			throw new Error();
		}
		stage.close();
	}
	
	public static int getNumOptimaFound(BenchmarkProblem prob, List<GAIndividual> pop){
		List<boolean[]> optimaFound = getOptimaFound(prob, pop);
		return optimaFound.size();
	}
	
	public static List<boolean[]> getOptimaFound(BenchmarkProblem prob, List<GAIndividual> pop){
		double epsilon = 0.5*prob.getDimensionality();
		if(prob.optimasInPaper == null)
			return null;
		List<boolean[]> optima2 = new ArrayList<>();
		for(double[] optima : prob.optimasInPaper){
			for(var gai : pop){
				double[] gaiCoords = prob.translateToCoordinates(gai.genome);
				// Calculating euclidean distance.
				double dist = 0;
				for(int i=0; i<gaiCoords.length; i++){
					dist += Math.pow(gaiCoords[i] - optima[i], 2);
				}
				dist = Math.sqrt(dist);
				if(dist <= epsilon){
					optima2.add(gai.genome);
					break;
				}
			}
		}
		return optima2;
	}
	

	public static double[] nBestSeparatedBy(List<GAIndividual> pop, Distance<double[]> distMetric, int n, double epsilon){
		if(n > pop.size()) return null;
		pop = new ArrayList<>(pop);
		Collections.sort(pop);
		List<GAIndividual> chosen = new ArrayList<>();

		while(chosen.size() < n){
			boolean found = false;
			for(int i=0; i<pop.size(); i++){
				boolean newPeak = true;
				double[] candVec = pop.get(i).getPoint();
				for(var gai : chosen){
					double[] gaiVec = gai.getPoint();
					if(distMetric.d(candVec, gaiVec) <= epsilon){
						newPeak = false;
					}
				}
				if(newPeak){
					chosen.add(pop.get(i));
					pop.remove(i);
					found = true;
					break;
				}
			}
			if(!found)
				chosen.add(pop.get(pop.size()-1)); // add the worst.
		}
		
		double[] f = new double[chosen.size()];
		for(int i=0; i<f.length; i++){
			f[i] = chosen.get(i).fitness;
		}
		
		return f;
	}
	
	public static double getPeakRatio(BenchmarkProblem prob, List<GAIndividual> pop){
		double numFound = getNumOptimaFound(prob, pop);
		double max = prob.optimasInPaper.size();
		return numFound/max;
	}
	
	public static double getMean(List<Integer> vals){
		double[] vals2 = new double[vals.size()];
		for(int i=0; i<vals.size(); i++){
			vals2[i] = vals.get(i);
		}
		return getMean(vals2);
	}
	
	public static double getMean(double[] vals){
		double mean = 0;
		for(int i=0; i<vals.length; i++){
			mean += vals[i];
		}
		mean /= vals.length;
		return mean;
	}
	
	public static double getSTD(List<Integer> vals){
		double[] vals2 = new double[vals.size()];
		for(int i=0; i<vals.size(); i++){
			vals2[i] = vals.get(i);
		}
		return getSTD(vals2);
	}
	
	public static double getSTD(double[] vals){
		StandardDeviation std = new StandardDeviation(false);
		return std.evaluate(vals);
	}
	
	public static double[] analyzeBest5(List<DoubleArray> best5){
		int len = best5.get(0).values.length;
		
		double[] vals = new double[best5.size()*len];
		for(int i=0; i<best5.size(); i++){
			for(int j=0; j<len; j++){
				vals[i*len+j] = Math.abs(best5.get(i).values[j]);
			}
		}
		double mean = getMean(vals);
		double std = getSTD(vals);
		
		return new double[]{mean, std};
	}
}
