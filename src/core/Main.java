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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        stage.setTitle("Crowding GA with SLS");
        stage.setScene(new Scene(root));
		stage.show();
	}
	
	private List<Problem> readProblems(){
		List<Problem> problems = new ArrayList<>();

		File folder = new File("Testing Data");
		for(File f : folder.listFiles()){
			if(!f.getName().endsWith(".txt"))
				continue;
			List<List<Float>> contents = getNumberFile(f);
			Problem p = new Problem(f.getName(), contents);
			problems.add(p);
			
		}
		return problems;
	}
	
	private List<List<Float>> getNumberFile(File f){
		List<List<Float>> contents = new ArrayList<>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			while(true){
				String line = reader.readLine();
				if(line == null)
					break;

				var floats = lineToFloats(line);
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
	
	private List<Float> lineToFloats(String line){
		Set<String> bannedStrings = new HashSet<>(){{add(""); add("\n"); add("\r"); add("\r\n");}};
		List<Float> symbols = new ArrayList<>();
		String[] split = line.split(" ");
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
