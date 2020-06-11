/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

		List<Problem> probList = new ArrayList<>();
		mmc.addProblems(probList);

        stage.setTitle("Crowding GA with SLS");
        stage.setScene(new Scene(root));
		stage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
