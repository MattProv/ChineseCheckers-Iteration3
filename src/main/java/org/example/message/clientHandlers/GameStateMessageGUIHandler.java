package org.example.message.clientHandlers;

import javafx.application.Platform;
import org.example.GameState;
import org.example.client.GUI.GameScreen;
import org.example.message.GameStateMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;

public class GameStateMessageGUIHandler extends MessageHandler {

    private final GameState gameState;
    private final GameScreen gameScreen;

    public GameStateMessageGUIHandler(GameState gm, GameScreen gameScreen) {
        super(MessageType.GAMESTATE);
        this.gameState = gm;
        this.gameScreen = gameScreen;
    }

    @Override
    public void handle(final MessageSenderPair message) {
        GameStateMessage bm = (GameStateMessage) message.getMessage();

        bm.getGameState().getBoard().showBoard();
        gameState.setState(bm.getGameState());
        Platform.runLater(() -> {
            gameScreen.updateBoard();
            gameScreen.updatePlayerList(bm.getPlayers(), bm.getTurn());
            });
    }
}
