package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.client.GUI.GameScreen;
import org.example.game_logic.Board;
import org.example.game_logic.StandardBoard;

public class TestGUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        GameState gameState = new GameState();
        Board board = new StandardBoard();
        gameState.setBoard(board);

        board.generateBoard();
        board.defineBases();

        GameScreen gameScreen = new GameScreen(gameState);
        Scene gameScene = new Scene(gameScreen, 500, 400);

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Chinese Checkers! - TEST");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
