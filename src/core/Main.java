/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
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

		List<Problem> probList = new ArrayList<>();
		File probFolder = new File("Testing Data");
		readProblems(probFolder, probList, "");
		mmc.addProblems(probList);
		
		DataHarvester harvest = new DataHarvester(probList);

        stage.setTitle("Crowding GA with SLS");
        stage.setScene(new Scene(root));
		stage.show();
	}
	
	private void readProblems(File folder, List<Problem> probList, String prefix){
		for(File f : folder.listFiles()){
			if(f.isDirectory()){
				readProblems(f, probList, prefix+f.getName()+"\\");
			}
			if(f.getName().endsWith(".arff")){
				System.out.println(prefix + f.getName());
				Problem p = loadProblem(f);
				p.name = prefix + f.getName();
				probList.add(p);
			}
		}
	}
	
	private Problem loadProblem(File f){
		DataSet dataset = ARFFLoader.loadArffFile(f);
		List<String> namesNumeric = new ArrayList<>();
		for(int i=0; i<dataset.getNumNumericalVars(); i++){ // feature names get removed at a couple of stages in this process, so backing them up temporarily.
			namesNumeric.add(dataset.getNumericName(i));
		}
		if(dataset.countMissingValues() != 0){
			System.out.println("Found " + dataset.countMissingValues() + " missing values in " + f.getName()+". Removing all rows with missing values.");
			int preLen = dataset.getSampleSize();
			dataset = dataset.getMissingDropped();
			System.out.println("Went from " + preLen + " samples to " + dataset.getSampleSize() + ".");
		}
		int predictIndex = dataset.getCategories().length-1;
		List<DataSet> splits = dataset.randomSplit(new Random(72), new double[]{0.7, 0.3});

		List<ClassificationDataSet> splits2 = new ArrayList<>();
		for(int i=0; i<splits.size(); i++){
			ClassificationDataSet temp = new ClassificationDataSet(splits.get(i), predictIndex);
			splits2.add(temp);
		}
		for(int i=0; i<namesNumeric.size(); i++){ // restoring feature names.
			splits2.get(0).setNumericName(namesNumeric.get(i), i);
			splits2.get(1).setNumericName(namesNumeric.get(i), i);
		}
		Problem p = new Problem(splits2);
		return p;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
