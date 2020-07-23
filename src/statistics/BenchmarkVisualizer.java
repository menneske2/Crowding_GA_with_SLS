/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import core.Problem;
import java.math.BigInteger;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkVisualizer {
	
	
	public BenchmarkVisualizer(){
//		BenchmarkLoader loader = new BenchmarkLoader();
//		WritableImage im = createHeatMap(loader.loadF1("F1", 18)); // 18 bits corresponds to 2^9 = 512 pixels per axis.
//		imposeLines(im);
//		displayImage(im);
	}
	
	public static Image getSolutionsOnHeatmap(Image heatMap, List<boolean[]> solutions){
		WritableImage copy = copyImage(heatMap);
		
		for(var bitstring : solutions){
			placeOnMap(copy, bitstring);
		}
		return copy;
	}
	
	private static WritableImage copyImage(Image orig){
		WritableImage copy = new WritableImage((int)orig.getWidth(), (int)orig.getHeight());
		PixelReader pr = orig.getPixelReader();
		PixelWriter pw = copy.getPixelWriter();
		for(int x=0; x<orig.getWidth(); x++){
			for(int y=0; y<orig.getHeight(); y++){
				pw.setColor(x, y, pr.getColor(x, y));
			}
		}
		return copy;
	}
	
	public static void popupImage(Image im){
		ImageView view = new ImageView(im);
		view.setPreserveRatio(true);
		Group root = new Group(view);
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Heatmap");
		stage.show();
	}
	
	public static Image getFullyFeaturedHeatmap(Problem prob){
		WritableImage im = createHeatMap(prob);
		imposeLines(im);
		
		return im;
	}
	
	private static void placeOnMap(WritableImage im, boolean[] bits){
		BigInteger[] partitions = BenchmarkLoader.partitionBitstring(bits, 2);
		double[] normalized = BenchmarkLoader.normalize(partitions, bits.length/2, 0, (int)im.getWidth());
		int locX = (int)Math.round(normalized[0]);
		int locY = (int)Math.round(normalized[1]);
		
		int radius = 2;
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				if(Math.abs(locX-x) + Math.abs(locY-y) <= radius){
					pw.setColor(x, y, Color.RED);
				}
			}
		}
	}
	
	private static WritableImage imposeLines(WritableImage im){
		PixelReader pr = im.getPixelReader();
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<im.getWidth()-1; x++){
			for(int y=0; y<im.getHeight()-1; y++){
				double val = pr.getColor(x, y).getBrightness();
				double neighbourVal = pr.getColor(x+1, y+1).getBrightness();
				int down1 = 0, down2 = 0;
				double step = 0.01;
				while(val>step){
					val -= step;
					down1++;
				}
				while(neighbourVal>step){
					neighbourVal -= step;
					down2++;
				}
				if(down1 != down2){
					pw.setColor(x, y, Color.GOLD);
				}
			}
		}
		return im;
	}
	
	private static WritableImage createHeatMap(Problem prob){
		BenchmarkLoader loader = new BenchmarkLoader();
		prob = loader.loadByName(prob.name, 18);
		
		int axisLength = (int) Math.pow(2, prob.numFeatures/2);
		WritableImage wi = new WritableImage(axisLength, axisLength);
		PixelWriter pw = wi.getPixelWriter();
		
		double lowest = Double.POSITIVE_INFINITY;
		double highest = Double.NEGATIVE_INFINITY;
		double[][] fitnesses = new double[axisLength][axisLength];
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				boolean[] bits = coordsToBoolArray(x, y, prob.numFeatures);
				double fitness = prob.evaluateBitstring(bits, false);
				fitnesses[x][y] = fitness;
				highest = Math.max(highest, fitness);
				lowest = Math.min(lowest, fitness);
			}
		}
		
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				double fitness = fitnesses[x][y];
				double normalized = normalize(fitness, lowest, highest);
				Color c = Color.color(normalized, normalized, normalized);
				pw.setColor(x, y, c);
			}
		}
		
		return wi;
	}
	
	// Normalizes to values in the range [0, 1]
	private static double normalize(double in, double inLowest, double inHighest){
		in -= inLowest;
		in /= inHighest-inLowest;
		return in;
	}
	
	private static boolean[] coordsToBoolArray(int x, int y, int totLen){
		int axisLength = totLen/2;
		String s = Integer.toBinaryString(y);
		while(s.length() < axisLength){
			s = "0" + s;
		}
		s = Integer.toBinaryString(x) + s;
		while(s.length() < axisLength*2){
			s = "0" + s;
		}
		boolean[] bArray = new boolean[axisLength*2];
		for(int i=0; i<bArray.length; i++){
			bArray[i] = s.charAt(i) == '1';
		}
		return bArray;
	}
}
