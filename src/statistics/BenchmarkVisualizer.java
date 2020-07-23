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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
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
	
	public static Image getFullyFeaturedHeatmap(Problem prob){
		double[][] fitnessArray = generateNormalizedFitnessArray(prob, 1); // problem, degree of self-multiplication.
		WritableImage im = createHeatMap(fitnessArray, Color.BLACK, Color.WHITE); // fitness, low color, high color.
		drawHeightCurves(im, fitnessArray, 0.005, Color.DARKGRAY); // im, fitness, height difference between lines, color.
		markLocalOptima(im, fitnessArray, 10, 1, Color.BLUE); // im, fitness, detection radius, draw radius, color.
		markGlobalOptima(im, fitnessArray, 2, Color.DARKORANGE); // im, fitness, drawRadius, color.
		
		return im;
	}
	
	public static Image getSolutionsOnHeatmap(Image heatMap, List<boolean[]> solutions){
		WritableImage copy = copyImage(heatMap);
		
		for(var bitstring : solutions){
			placeBitsOnMap(copy, bitstring, 3, Color.RED); // im, bits, drawRadius, color
		}
		return copy;
	}
	
	
	private static void markGlobalOptima(WritableImage im, double[][] fArray, int drawRadius, Color c){
		double highest = 0;
		for(int x=0; x<fArray.length; x++){
			for(int y=0; y<fArray[0].length; y++){
				highest = Math.max(highest, fArray[x][y]);
			}
		}
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<fArray.length; x++){
			for(int y=0; y<fArray[0].length; y++){
				if(fArray[x][y] == highest){
					for(int x2=x-drawRadius; x2<=x+drawRadius; x2++){
						for(int y2=y-drawRadius; y2<=y+drawRadius; y2++){
							if(x2<0 || x2>=im.getWidth() || y2<0 || y2>=im.getHeight())
								continue;
							pw.setColor(x2, y2, c);
						}
					}
				}
			}
		}
		
	}
	
	private static void markLocalOptima(WritableImage im, double[][] fArray, int detectRadius, int drawRadius, Color c){
		PixelWriter pw = im.getPixelWriter();
		
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				double val = fArray[x][y];
				boolean isOptima = true;
				// Left/right
				for(int x2=x-detectRadius; x2<x+detectRadius; x2++){
					if(x2<0 || x2>im.getWidth()-1)
						continue;
					if(fArray[x2][y] > val){
						isOptima = false;
						break;
					}
				}
				// Up/down
				for(int y2=y-detectRadius; y2<y+detectRadius; y2++){
					if(y2<0 || y2>im.getHeight()-1)
						continue;
					if(fArray[x][y2] > val){
						isOptima = false;
						break;
					}
				}
				// Diagonal 1
				for(int i=0; i<detectRadius*2; i++){
					int x2=x-detectRadius + i;
					int y2=y-detectRadius + i;
					if(x2<0 || x2>im.getWidth()-1 || y2<0 || y2>im.getHeight()-1)
							continue;
					if(fArray[x2][y2] > val){
						isOptima = false;
						break;
					}
				}
				// Diagonal 2
				for(int i=0; i<detectRadius*2; i++){
					int x2=x-detectRadius + i;
					int y2=y+detectRadius - i;
					if(x2<0 || x2>im.getWidth()-1 || y2<0 || y2>im.getHeight()-1)
							continue;
					if(fArray[x2][y2] > val){
						isOptima = false;
						break;
					}
				}
				
				if(!isOptima) continue;
				for(int x2=x-drawRadius; x2<=x+drawRadius; x2++){
					for(int y2=y-drawRadius; y2<=y+drawRadius; y2++){
						if(x2<0 || x2>im.getWidth()-1 || y2<0 || y2>im.getHeight()-1)
							continue;
						pw.setColor(x2, y2, c);
					}
				}
			}
		}
	}
	
	
	private static void placeBitsOnMap(WritableImage im, boolean[] bits, int radius, Color c){
		BigInteger[] partitions = BenchmarkLoader.partitionBitstring(bits, 2);
		double[] normalized = BenchmarkLoader.normalize(partitions, bits.length/2, 0, (int)im.getWidth()-1);
		int locX = (int)Math.round(normalized[0]);
		int locY = (int)Math.round(normalized[1]);
		
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
//				if(Math.abs(locX-x) + Math.abs(locY-y) <= radius){ // diamond shape.
//					pw.setColor(x, y, c);
//				}
				if(Math.sqrt(Math.pow(locX-x,2) + Math.pow(locY-y,2)) <= radius){
					pw.setColor(x, y, c);
				}
			}
		}
	}
	
	private static void drawHeightCurves(WritableImage im, double[][] fitnessArray, double step, Color c){
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				double f = fitnessArray[x][y];
				if(f%step < 0.001){
					pw.setColor(x, y, c);
				}
			}
		}
	}
	
	private static WritableImage createHeatMap(double[][] fitArray, Color lowEnd, Color highEnd){
		int axisLength = fitArray.length;
		WritableImage wi = new WritableImage(axisLength, axisLength);
		PixelWriter pw = wi.getPixelWriter();
		
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				Color c = lowEnd.interpolate(highEnd, fitArray[x][y]);
				pw.setColor(x, y, c);
			}
		}
		
		return wi;
	}
	
	private static double[][] generateNormalizedFitnessArray(Problem prob, int degree){
		BenchmarkLoader loader = new BenchmarkLoader();
		prob = loader.loadByName(prob.name, 18);
		
		int axisLength = (int) Math.pow(2, prob.numFeatures/2);
		
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
		double[][] normalized = new double[axisLength][axisLength];
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				double fitness = fitnesses[x][y];
				double normVal = normalize(fitness, lowest, highest);
				normVal = Math.pow(normVal, degree);
				normalized[x][y] = normVal;
			}
		}
		return normalized;
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
