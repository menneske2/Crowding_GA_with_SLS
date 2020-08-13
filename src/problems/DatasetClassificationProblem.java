/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problems;

import algorithm.GAUtilities;
import java.util.ArrayList;
import java.util.List;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.DataPointPair;
import jsat.classifiers.bayesian.NaiveBayes;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

/**
 *
 * @author Fredrik-Oslo
 */
public class DatasetClassificationProblem extends Problem{
	
	public ClassificationDataSet datasetTrain, datasetValid;
	

	// numFeatures is only used in benchmark functions.
	public DatasetClassificationProblem(List<ClassificationDataSet> datasets){
		this.numFeatures = datasets.get(0).getNumFeatures();
		this.datasetTrain = datasets.get(0);
		this.datasetValid = datasets.get(1);
		this.distanceMetric = new JaccardDistance();
		this.fitnessPunishRatio = 0.5;
	}
	
	public String getIndexName(int index){
		int numCats = datasetTrain.getNumCategoricalVars();
		if(index >= numCats){
			return datasetTrain.getNumericName(index - numCats);
		} else{
			return datasetTrain.getCategoryName(index);
		}
	}
	
	@Override
	public double evaluateBitstring(boolean[] bits, boolean punish){
		this.fitnessEvaluations++;
		
		
		ClassificationDataSet reducedTrain = reduceFeatures(datasetTrain, bits);
		ClassificationDataSet reducedValid = reduceFeatures(datasetValid, bits);
		
		if(reducedTrain.getNumFeatures() == 0){
			return 0.0;
		}
		
		Classifier classifier = new NaiveBayes();
		classifier.trainC(reducedTrain);
		
		int correct = 0;
		for(int i=0; i<reducedValid.getSampleSize(); i++){
			DataPoint dp = reducedValid.getDataPoint(i);
			int predict = classifier.classify(dp).mostLikely();
			int truth = reducedValid.getDataPointCategory(i);
			if(predict == truth)
				correct++;
		}
		
		double fitness = (double) correct / datasetValid.getSampleSize();
		
		
		if(punish){
			double ratioUsed = (double) GAUtilities.countFeatures(bits) / bits.length;
			fitness -= fitnessPunishRatio * ratioUsed;
		}
		
		return fitness;
	}
	
	private ClassificationDataSet reduceFeatures(ClassificationDataSet full, boolean[] bits){
		// Counting features.
		int nCols = 0;
		for(Boolean b : bits){
			nCols += (b.hashCode() & 0b10) >> 1;
		}
		
		List<DataPointPair<Integer>> reducedPoints = new ArrayList<>();

		// The first n features correspond to the categorical values in the dataset, the rest are for numerical values.
		int numCategorical = full.getNumCategoricalVars();
		
		List<CategoricalData> catData = new ArrayList<>();
		CategoricalData[] fullCatData = full.getCategories();
		for(int i=0; i<numCategorical; i++){
			if(bits[i]){
				catData.add(fullCatData[i]);
			}
		}
		CategoricalData[] reducedCategories = catData.toArray(new CategoricalData[]{});
		
		for(int point=0; point<full.getSampleSize(); point++){
			double[] newNums = new double[nCols];
			int[] newCats = new int[reducedCategories.length];
			
			int iNum = 0;
			int iCat = 0;
			
			DataPointPair<Integer> fullPoint = full.getDataPointPair(point);
			DataPoint fullData = fullPoint.getDataPoint();
			for(int i=0; i<numCategorical; i++){
				if(bits[i]){
					newCats[iCat] = fullData.getCategoricalValue(i);
					iCat++;
				}
			}
			Vec nums = fullData.getNumericalValues();
			for(int i=numCategorical; i<bits.length; i++){
				if(bits[i]){
					newNums[iNum] = nums.get(i-numCategorical);
					iNum++;
				}
			}
			
			DataPoint newP = new DataPoint(new DenseVector(newNums), newCats, reducedCategories);
			DataPointPair<Integer> reducedPoint = new DataPointPair<>(newP, fullPoint.getPair());
			reducedPoints.add(reducedPoint);
		}
		
		ClassificationDataSet reducedSet = new ClassificationDataSet(reducedPoints, full.getPredicting());
		return reducedSet;
	}
	
}
