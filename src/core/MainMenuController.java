/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.List;
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
		openProblem(chosen.clone());
	}

	public void addProblems(List<Problem> probs){
		problems.getItems().addAll(probs);
	}
	
	private void openProblem(Problem prob){
		try{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Solver.fxml"));
		Parent root = loader.load();
		SolverController solver = ((SolverController) loader.getController());
		
		solver.setProblem(prob);

		Stage stage = new Stage();
		stage.setTitle(prob.toString());
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		} catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(3);
		}
	}

}
