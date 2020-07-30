/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import problems.BenchmarkProblem;
import problems.BenchmarkLoader;
import java.math.BigInteger;
import java.util.ArrayList;
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
	
	private static final int SHAPE_DIAMOND = 1;
	private static final int SHAPE_CROSS = 2;
	private static final int SHAPE_SQUARE = 3;
	private static final int SHAPE_CIRCLE = 4;
	
	
	public static Image getFullyFeaturedHeatmap(BenchmarkProblem prob){
		double[][] fArray = generateFitnessArray(prob, 18); // problem, bitstring length
		double[][] normFArray = normalizeFitnessArray(fArray);
		
		WritableImage im = createHeatMap(normFArray, Color.BLACK, Color.CHOCOLATE); // fitness, low color, high color.
		colourFitnessPeaks(im, normFArray, 0.98, 0.001, Color.CHOCOLATE, Color.WHITE); // im, fitness, top threshold, height difference between lines, color low, color high.
		drawHeightCurves(im, normFArray, 0.004, Color.DARKGRAY); // im, fitness, height difference between lines, color.
		
		markLocalOptima(im, fArray, 6, SHAPE_CROSS, 3, Color.BLUE); // im, fitness, detection radius, draw radius, color.
		double optimaSensitivity = 0.3;
		markGlobalOptima(im, fArray, 6, optimaSensitivity, SHAPE_CROSS, 3, Color.DARKORANGE); // im, fitness, sensitivity, drawRadius, color.
		
		return im;
	}
	
	public static Image getSolutionsOnHeatmap(Image heatMap, List<boolean[]> solutions){
		WritableImage copy = copyImage(heatMap);
		
		for(var bitstring : solutions){
			placeBitsOnMap(copy, bitstring, SHAPE_DIAMOND, 2, Color.RED); // im, bits, drawRadius, color
		}
		return copy;
	}
	
	
	private static void markGlobalOptima(WritableImage im, double[][] fArray, int detectRadius, double sensitivity, int shape, int drawRadius, Color c){
		double highest = 0;
		for(int x=0; x<fArray.length; x++){
			for(int y=0; y<fArray[0].length; y++){
				highest = Math.max(highest, fArray[x][y]);
			}
		}
		final double limit = highest - sensitivity;
		
		double[][] newFArray = new double[fArray.length][fArray.length];
		for(int x=0; x<fArray.length; x++){
			for(int y=0; y<fArray.length; y++){
				if(fArray[x][y] >= limit){
					newFArray[x][y] = fArray[x][y];
				} else{
					newFArray[x][y] = -1;
				}
			}
		}
		markLocalOptima(im, newFArray, detectRadius, shape, drawRadius, c);
	}
	
	
	private static void markLocalOptima(WritableImage im, double[][] fArray, int detectRadius, int shape, int drawRadius, Color c){
		PixelWriter pw = im.getPixelWriter();
		int numOptima = 0;
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				double val = fArray[x][y];
				if(val == -1) continue;
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
				
				if(isOptima){
					numOptima++;
					markLocation(x, y, im, shape, drawRadius, c);
				}
			}
		}
		System.out.println("Number of optima: " + numOptima);
	}
	
	private static void placeBitsOnMap(WritableImage im, boolean[] bits, int shape, int radius,  Color c){
		BigInteger[] partitions = BenchmarkLoader.partitionBitstring(bits, 2);
		double[] normalized = BenchmarkLoader.normalize(partitions, bits.length/2, 0, (int)im.getWidth());
		int locX = (int)Math.round(normalized[0]);
		int locY = (int)Math.round(normalized[1]);
		
		markLocation(locX, locY, im, shape, radius, c);
	}
	
	
	private static void drawHeightCurves(WritableImage im, double[][] fArray, double step, Color c){
		PixelWriter pw = im.getPixelWriter();
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				double f = fArray[x][y];
				if(f%step < 0.001){
					pw.setColor(x, y, c);
				}
			}
		}
	}
	
	private static void colourFitnessPeaks(WritableImage im, double[][] fArray, double topThreshold, double step, Color c1, Color c2){
		// Creating array with only the top.
		double[][] topFs = new double[fArray.length][fArray.length];
		for(int x=0; x<fArray.length; x++){
			for(int y=0; y<fArray.length; y++){
				if(fArray[x][y] > topThreshold){
					topFs[x][y] = fArray[x][y];
				} else{
					topFs[x][y] = -1;
				}
			}
		}
		topFs = normalizeFitnessArray(topFs, topThreshold, 1.0);

		// Creating layer arrays
		List<List<int[]>> layers = new ArrayList<>();
		for(int i=0; i<(1/step); i++){
			layers.add(new ArrayList<>());
		}
		
		// Partitioning positions into layers
		for(int x=0; x<im.getWidth(); x++){
			for(int y=0; y<im.getHeight(); y++){
				double f = topFs[x][y];
				if(f<0) continue;
				int layer = 0;
				while(f > step){
					f -= step;
					layer++;
				}
				layers.get(layer).add(new int[]{x,y});
			}
		}
		
		PixelWriter pw = im.getPixelWriter();
		for(int i=0; i<layers.size(); i++){
			double prog = i*step;
			Color col = c1.interpolate(c2, prog);
			for(var pix : layers.get(i)){
				pw.setColor(pix[0], pix[1], col);
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
	
	private static void markLocation(int x, int y, WritableImage im, int shape, int radius, Color c){
		PixelWriter pw = im.getPixelWriter();
		for(int x2=x-radius; x2<=x+radius; x2++){
			for(int y2=y-radius; y2<=y+radius; y2++){
				if(x2<0 || x2>=im.getWidth() || y2<0 || y2>=im.getHeight())
					continue;
				switch(shape){
					case SHAPE_CROSS:
						if(x2==x || y2==y)
							pw.setColor(x2, y2, c);
						break;
					case SHAPE_SQUARE:
						pw.setColor(x2, y2, c);
						break;
					case SHAPE_CIRCLE:
						if(Math.sqrt(Math.pow(x2-x,2) + Math.pow(y2-y,2)) <= radius)
							pw.setColor(x2, y2, c);
						break;
					case SHAPE_DIAMOND:
						if(Math.abs(x2-x) + Math.abs(y2-y) <= radius)
							pw.setColor(x2, y2, c);
						break;
				}
			}
		}
	}
	
	private static double[][] generateFitnessArray(BenchmarkProblem prob, int resolution){
		prob = BenchmarkLoader.loadByName(prob.name, resolution);
		
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
		return fitnesses;
	}
	
	private static double[][] normalizeFitnessArray(double[][] fArray){
		int axisLength = fArray.length;
		
		double lowest = Double.POSITIVE_INFINITY;
		double highest = Double.NEGATIVE_INFINITY;
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				highest = Math.max(highest, fArray[x][y]);
				lowest = Math.min(lowest, fArray[x][y]);
			}
		}
		
		return normalizeFitnessArray(fArray, lowest, highest);
	}
	
	private static double[][] normalizeFitnessArray(double[][] fArray, double lowest, double highest){
		int axisLength = fArray.length;
		
		double[][] normalized = new double[axisLength][axisLength];
		for(int x=0; x<axisLength; x++){
			for(int y=0; y<axisLength; y++){
				double fitness = fArray[x][y];
				double normVal = normalizeTo01(fitness, lowest, highest);
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
	private static double normalizeTo01(double in, double inLowest, double inHighest){
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
