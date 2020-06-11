/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 *
 * @author Fredrik
 */
public class MainMenuController {

	@FXML private ListView problems;

	public MainMenuController(){
		
	}

	@FXML
	private void initChosenProblem(ActionEvent e){
		Problem chosen = (Problem) problems.getSelectionModel().getSelectedItem();
		if(chosen == null)
			chosen = (Problem) problems.getItems().get(0);
		openProblem(chosen);
	}

	public void addProblems(List<Problem> probs){
		problems.getItems().addAll(probs);
	}
	
	private void openProblem(Problem prob){
		
	}

}
