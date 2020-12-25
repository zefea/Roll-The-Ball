/*
 * 21.04.2020
 * 
 * Zeynep Ferah Akkurt
 * 
 * 
 */

package application;

import javafx.scene.Scene;
import javafx.application.Application;
import javafx.stage.Stage;

public class Test extends Application {
	// start method
	// @Override
	public void start(Stage primaryStage) throws Exception {
		// Create a scene and place it in the stage
		GameBoard m = new GameBoard();
		Scene scene = new Scene(m.getGame());
		primaryStage.setTitle("Game");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// main
	public static void main(String[] args) {
		launch(args);
	}

}
