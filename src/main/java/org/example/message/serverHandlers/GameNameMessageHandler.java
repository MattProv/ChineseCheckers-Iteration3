package org.example.message.serverHandlers;

import org.example.message.GameNameMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;

public class GameNameMessageHandler extends MessageHandler {

    private final GameManager gameManager;

    public GameNameMessageHandler(GameManager gameManager) {
        super(MessageType.GAMENAME);
        this.gameManager = gameManager;
    }

    @Override
    public void handle(MessageSenderPair message) {
        gameManager.setGameName(((GameNameMessage)message.getMessage()).getGameName());
    }
}
