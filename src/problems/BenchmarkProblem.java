/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import algorithm.GAUtilities;
import cec15_nich_java_code.cec15_nich_func;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jsat.clustering.DBSCAN;
import jsat.clustering.FLAME;
import jsat.clustering.GapStatistic;
import jsat.clustering.HDBSCAN;
import jsat.clustering.LSDBC;
import jsat.clustering.SeedSelectionMethods;
import jsat.clustering.kmeans.ElkanKMeans;
import jsat.clustering.kmeans.GMeans;
import jsat.clustering.kmeans.KMeans;
import jsat.clustering.kmeans.KMeansPDN;
import jsat.clustering.kmeans.XMeans;

/**
 *
 * @author Fredrik-Oslo
 */
public class BenchmarkProblem extends Problem{
	
	private final int funcNumber;
	private final cec15_nich_func tf = new cec15_nich_func();
	
	public final double[] searchRange;
	protected int dimensionality;
	
	public List<double[]> optimasInPaper;
	
	
	public BenchmarkProblem(int funcNumber){
		this.funcNumber = funcNumber;
		this.searchRange = new double[]{-100, 100};
		this.fitnessPunishRatio = 0;

//		this.clusteringAlgorithm = new KMeansPDN();
		this.clusteringAlgorithm = new HDBSCAN();
		setDimensionality(2);
	}
	
	@Override
	public double evaluateBitstring(boolean[] bits, boolean punish) {
		this.fitnessEvaluations++;
		
		double[] axes = translateToCoordinates(bits);
		double[] f = new double[]{0};
		try{
			tf.test_func(axes, f, dimensionality, 1, funcNumber);
		} catch(Exception e){
			System.out.println("[BenchmarkProblem] something went wrong with fitness evaluation");
			e.printStackTrace();
			System.exit(9);
		}
		double fitness = -f[0];
		
		if(punish){
			double ratioUsed = (double) GAUtilities.countFeatures(bits) / bits.length;
			fitness -= fitnessPunishRatio * ratioUsed;
		}
		
		return fitness;
	}
	
	private void assembleOptimaList(){
		File optimaFile = new File("cec15_nich\\optima\\optima_positions_"+funcNumber+"_"+dimensionality+"D.txt");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(optimaFile));
			optimasInPaper = new ArrayList<>();
			while(true){
				String line = reader.readLine();
				if(line == null) break;
				String[] segments = line.strip().split(" ");
				double[] coords = new double[dimensionality];
				int i=0;
				for(String seg : segments){
					if(!seg.equals("")){
						double val = Double.parseDouble(seg.strip());
						coords[i] = val;
						i++;
					}
				}
				optimasInPaper.add(coords);
			}
		} catch(Exception e){
			optimasInPaper = null;
//			System.out.println("Optima file for problem #"+funcNumber+" in "+dimensionality+" dimensions not found.");
		}
	}
	
	public final void setDimensionality(int dims){
		this.dimensionality = dims;
		this.distanceMetric = new NDimensionalMappingDistance(dims);
		this.numFeatures = 10*dims;
		
		Class c = clusteringAlgorithm.getClass();
		if(c == FLAME.class){
			clusteringAlgorithm = new FLAME(distanceMetric, 4, 20);
		} else if(c == HDBSCAN.class){
			clusteringAlgorithm = new HDBSCAN(distanceMetric, 5);
			((HDBSCAN)clusteringAlgorithm).setMinPoints(1);
		} else if(c == LSDBC.class){
			clusteringAlgorithm = new LSDBC(distanceMetric, 1, 4);
		} else if(c == DBSCAN.class){
			clusteringAlgorithm = new DBSCAN(distanceMetric);
		} else if(c == GMeans.class){
			KMeans kMeans = new ElkanKMeans(this.distanceMetric);
			GMeans alg = new GMeans(kMeans);
//			alg.setIterativeRefine(false); // gjør den ish 50% raskere.
			alg.setMinClusterSize(2);
			alg.setSeedSelection(SeedSelectionMethods.SeedSelection.KPP);
			alg.setIterationLimit(10);
			this.clusteringAlgorithm = alg;
		} else if(c == XMeans.class){
			KMeans kMeans = new ElkanKMeans(this.distanceMetric);
			XMeans alg = new XMeans(kMeans);
			alg.setMinClusterSize(2);
			alg.setSeedSelection(SeedSelectionMethods.SeedSelection.KPP);
			alg.setIterationLimit(10);
			this.clusteringAlgorithm = alg;
		} else if(c == KMeansPDN.class){
			KMeans kMeans = new ElkanKMeans(this.distanceMetric);
			kMeans.setIterationLimit(10);
			kMeans.setSeedSelection(SeedSelectionMethods.SeedSelection.KPP);
			KMeansPDN alg = new KMeansPDN(kMeans);
			this.clusteringAlgorithm = alg;
		}
		
		assembleOptimaList();
		
	}
	
	public int getDimensionality(){
		return this.dimensionality;
	}
	
	
	public double[] translateToCoordinates(boolean[] bits){
		BigInteger[] axesBig = partitionBitstring(bits, dimensionality);
		double[] axes = normalize(axesBig, numFeatures/dimensionality, searchRange[0], searchRange[1]);
		if(searchRange.length > 2){
			axes[1] = normalize(axesBig, numFeatures/dimensionality, searchRange[2], searchRange[3])[1];
		}
		return axes;
	}
	
	public static BigInteger[] partitionBitstring(boolean[] bits, int partitions){
		if(bits.length%partitions != 0){
			System.out.println("bitlength: " + bits.length);
			System.out.println("[BenchmarkProblem] invalid combination of bitlength and partitioning");
			throw new Error();
		}
		BigInteger[] toReturn = new BigInteger[partitions];
		int len = bits.length / partitions;
		
		for(int i=0; i<partitions; i++){
			String bs = "";
			for(int j=0; j<len; j++){
				Boolean b = bits[i*len + j];
				bs += (b.hashCode() & 0b10) >> 1;
			}
			toReturn[i] = new BigInteger(bs, 2);
		}
		return toReturn;
	}
	
	public static double[] normalize(BigInteger[] in, int maxBits, double floor, double ceiling){
		String toParse = "";
		for(int i=0; i<maxBits; i++)
			toParse += "1";
		BigInteger max = new BigInteger(toParse, 2);
		
		double[] toReturn = new double[in.length];
		for(int i=0; i<in.length; i++){
			BigInteger num = in[i];
			double ratio = num.doubleValue()/max.doubleValue();
			toReturn[i] = floor + ratio*(ceiling-floor);
		}
		return toReturn;
	}
	
}
