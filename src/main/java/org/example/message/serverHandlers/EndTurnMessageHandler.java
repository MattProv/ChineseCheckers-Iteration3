package org.example.message.serverHandlers;

import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;

public class EndTurnMessageHandler extends MessageHandler {
    private final GameManager gameManager;
    public EndTurnMessageHandler(GameManager gameManager) {
        super(MessageType.END_TURN);
        this.gameManager = gameManager;
    }
    @Override
    public void handle(MessageSenderPair message) {
        gameManager.endTurn(gameManager.getPlayerByConnection(message.getConnection()));
    }
}
