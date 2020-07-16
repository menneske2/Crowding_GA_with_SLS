/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsat.ARFFLoader;
import jsat.DataSet;
import statistics.DataHarvester;

/**
 *
 * @author Fredrik-Oslo
 */
public class Main extends Application{
	
	MainMenuController mmc;
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
		Parent root = loader.load();
		mmc = loader.getController();

		List<Problem> probList = readProblems();
		mmc.addProblems(probList);
		
		DataHarvester harvest = new DataHarvester(probList);

        stage.setTitle("Crowding GA with SLS");
        stage.setScene(new Scene(root));
		stage.show();
	}
	
	private List<Problem> readProblems(){
		List<Problem> problems = new ArrayList<>();

		File folder = new File("Testing Data");
		for(File f : folder.listFiles()){
			if(f.isDirectory()){
				String prefix = f.getName().toLowerCase();
				List<List<Float>> trainData = getNumberFile(new File(f.getPath()+"\\"+prefix+"_train.data"), " ");
				List<List<Float>> trainLabels = getNumberFile(new File(f.getPath()+"\\"+prefix+"_train.labels"), " ");
				List<List<Float>> validationData = getNumberFile(new File(f.getPath()+"\\"+prefix+"_valid.data"), " ");
				List<List<Float>> validationLabels = getNumberFile(new File(f.getPath()+"\\"+prefix+"_valid.labels"), " ");
				appendLabelsToData(trainData, trainLabels);
				appendLabelsToData(validationData, validationLabels);
				
				List<String> names = getStringFile(new File(f.getPath() + "\\" + prefix + ".names"));
				
				if(names != null){
					List<Integer> removedFeatures = new ArrayList<>(){{
//						add(8);
					}};
					purgeColumns(names, removedFeatures);
					purgeColumns(trainData, removedFeatures);
					purgeColumns(validationData, removedFeatures);
				}
				
				Problem p = new Problem(f.getName(), trainData, validationData, names);
				problems.add(p);
			}
			else if(f.getName().equals("communities.arff")){
//				System.out.println("ayy");
//				DataSet dataset = ARFFLoader.loadArffFile(f);
//				List<DataSet> splits = dataset.randomSplit(new Random(72), new double[]{0.7, 0.3});
//				System.out.println(splits.get(0).getDataPoints().size());
//				System.out.println(splits.get(1).getDataPoints().size());
			}
			else if(f.getName().equals("eucalyptus.arff")){
				System.out.println("ayy");
				DataSet dataset = ARFFLoader.loadArffFile(f);
				List<DataSet> splits = dataset.randomSplit(new Random(72), new double[]{0.7, 0.3});
				System.out.println(splits.get(0).getDataPoints().size());
				System.out.println(splits.get(1).getDataPoints().size());
			}
			else{
				List<List<Float>> data = getNumberFile(f, ",");
				List<List<Float>> validationSet = new ArrayList<>();
				List<List<Float>> trainingSet = splitDataset(data, validationSet);
				
				Problem p = new Problem(f.getName(), trainingSet, validationSet, null);
				problems.add(p);
			}
		}
		return problems;
	}
	
	private List purgeColumns(List in, List<Integer> indices){
		if(in.get(0).getClass() == new ArrayList().getClass()){
			for(int i=0; i<in.size(); i++){
				in.set(i, purgeColumns((List)in.get(i), indices));
			}
			return in;
		}
		
		Collections.sort(indices);
		for(int indicesIndex=indices.size()-1; indicesIndex >= 0; indicesIndex--){ // Traversing in opposite direction to avoid wrong deletion.
			int index = indices.get(indicesIndex);
			in.remove(index);
		}
		return in;
	}

	
	private List<List<Float>> splitDataset(List<List<Float>> data, List<List<Float>> validation){
		float splitRatio = 0.3f;
		Random rng = new Random(72);
		
		List<List<Float>> train = new ArrayList<>(data);
		Collections.shuffle(train, rng);
		int nRemoved = Math.round(train.size() * splitRatio);
		for(int i=0; i<nRemoved; i++){
			validation.add(train.remove(i));
		}
		return train;
	}
	
	private void appendLabelsToData(List<List<Float>> data, List<List<Float>> labels){
		for(int i=0; i<data.size(); i++){
			data.get(i).add(labels.get(i).get(0));
		}
	}
	
	private List<String> getStringFile(File f){
		List<String> contents = new ArrayList<>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			while(true){
				String line = reader.readLine();
				if(line == null)
					break;
				contents.add(line);
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
		return contents;
	}
	
	private List<List<Float>> getNumberFile(File f, String delimiter){
		List<List<Float>> contents = new ArrayList<>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			while(true){
				String line = reader.readLine();
				if(line == null)
					break;

				var floats = lineToFloats(line, delimiter);
				if(floats.isEmpty())
					continue;
				contents.add(floats);
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(4);
		}
		return contents;
	}
	
	private List<Float> lineToFloats(String line, String delimiter){
		Set<String> bannedStrings = new HashSet<>(){{add(""); add("\n"); add("\r"); add("\r\n");}};
		List<Float> symbols = new ArrayList<>();
		String[] split = line.split(delimiter);
		for(String s : split){
			s = s.strip();
			if(!bannedStrings.contains(s))
				symbols.add(Float.parseFloat(s));
		}
		return symbols;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
