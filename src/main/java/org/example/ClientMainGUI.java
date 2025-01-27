package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.example.client.Client;
import org.example.client.ClientCallbacksHandler;
import org.example.client.GUI.GameScreen;
import org.example.client.GUI.LobbyScreen;
import org.example.client.GUI.LoginScreen;
import org.example.game_logic.BoardType;
import org.example.game_logic.Coordinate;
import org.example.game_logic.RulesType;
import org.example.message.*;
import org.example.message.clientHandlers.GameStateMessageGUIHandler;
import org.example.message.clientHandlers.PromptMoveMessageHandler;
import org.example.message.clientHandlers.StringMessageGUIHandler;
import org.example.message.clientHandlers.UserlistMessageHandler;

public class ClientMainGUI extends Application {

    private enum ScreenType{
        LOGIN,
        LOBBY,
        GAME
    }

    private ScreenType screenLoaded = ScreenType.LOGIN;
    private Scene loginScene;
    private Scene lobbyScene;
    private Scene gameScene;

    GameScreen gameScreen;

    @Override
    public void start(Stage primaryStage) {

        Client client = Client.create();
        GameState gameState = new GameState();

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(500);

        LoginScreen loginScreen = new LoginScreen();
        loginScene = new Scene(loginScreen, 500, 400);

        LobbyScreen lobbyScreen = new LobbyScreen();
        lobbyScene = new Scene(lobbyScreen, 500, 400);

        gameScreen = new GameScreen(gameState);
        gameScene = new Scene(gameScreen, 500, 400);

        loginScreen.setCallbacksHandler(new LoginScreen.CallbacksHandler() {
            @Override
            public void onConnect(String username, String host, int port) {
                if (client.Connect(host, port)) {
                    client.send(new org.example.message.UsernameMessage(username));
                    setScene(ScreenType.LOBBY, primaryStage);
                } else {
                    showError("Failed to connect to the server.");
                }
            }

            @Override
            public void onError(String message)
            {
                showError(message);
            }
        });

        lobbyScreen.setCallbacksHandler(new LobbyScreen.CallbacksHandler() {
            @Override
            public void onGameStart() {
                client.send(new CommandMessage(Commands.START_GAME, new String[0]));
            }

            @Override
            public void onChangePlayerCount(int playerCount) {
                client.send(new CommandMessage(Commands.SET_PLAYER_COUNT, new String[]{ "", Integer.toString(playerCount)}));
            }

            @Override
            public void onLeaveGame() {
                client.Disconnect();
            }

            @Override
            public void onError(String message) {
                showError(message);
            }

            @Override
            public void onChangeBoardType(BoardType boardType) {
                client.send(new BoardTypeMessage(boardType));
            }

            @Override
            public void onChangeRulesType(RulesType rulesType) {
                client.send(new RulesTypeMessage(rulesType));
            }

            @Override
            public void onSetGameToBeLoaded(String gameName) {
                client.send(new GameToBeLoadedMessage(gameName));
            }

            @Override
            public void onChangeGameName(String gameName) {
                client.send(new GameNameMessage(gameName));
            }

            @Override
            public void onChangeBotsCount(int botsCount) {
                client.send(new BotsCountMessage(botsCount));
            }
        });

        gameScreen.setCallbacksHandler(new GameScreen.CallbacksHandler() {
            @Override
            public void onMove(Coordinate start, Coordinate end) {
                client.send(new MoveMessage(start, end));
            }

            @Override
            public void onEndTurn() {
                client.send(new EndTurnMessage());
                gameScreen.enableEndTurnButton(false);
            }

            @Override
            public void onQuit() {
                client.send(new DisconnectMessage());
            }
        });

        client.clientCallbacksHandler = new ClientCallbacksHandler() {
            @Override
            public void onDisconnect() {
                System.out.println("Disconnected");
                setScene(ScreenType.LOGIN, primaryStage);
            }

            @Override
            public void onSocketError() {
                System.out.println("Error: Received a null message.");
                showError("Error: Received a null message.");
                setScene(ScreenType.LOGIN, primaryStage);
            }
        };
        client.AddHandler(new GameStateMessageGUIHandler(gameState, gameScreen));
        client.AddHandler(new UserlistMessageHandler(lobbyScreen, gameScreen));
        client.AddHandler(new StringMessageGUIHandler(lobbyScreen, gameScreen));
        client.AddHandler(new PromptMoveMessageHandler(gameScreen));
        client.AddHandler(new MessageHandler(MessageType.GAMESTATE) {
            @Override
            public void handle(MessageSenderPair message) {
                GameStateMessage gameStateMessage = (GameStateMessage) message.getMessage();
                if(gameStateMessage.getGameState().isRunning() && screenLoaded != ScreenType.GAME) {
                    setScene(ScreenType.GAME, primaryStage);
                }
            }
        });

        primaryStage.setOnCloseRequest(WindowEvent -> {
            client.Disconnect();

            Platform.exit();
            System.exit(0);
        });

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Chinese Checkers!");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void showError(String message) {
        Platform.runLater(() -> {
            Dialog<String> alertDialog = new Dialog<>();

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alertDialog.getDialogPane().getButtonTypes().add(okButton);

            alertDialog.setTitle("Error");
            alertDialog.setContentText(message);
            alertDialog.showAndWait();
        });
    }

    void setScene(ScreenType screenType, Stage primaryStage) {
        switch (screenType) {
            case LOGIN:
                Platform.runLater(() -> primaryStage.setScene(loginScene));
                break;
            case LOBBY:
                Platform.runLater(() -> primaryStage.setScene(lobbyScene));
                break;
            case GAME:
                Platform.runLater(() -> {
                    primaryStage.setScene(gameScene);
                    gameScreen.updateBoard();
                });
                break;
        }
        screenLoaded = screenType;
    }
}
