package org.example.message.serverHandlers;

import org.example.message.BotsCountMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;

public class SetBotsCountMessageHandler extends MessageHandler {
    private final GameManager gameManager;
    public SetBotsCountMessageHandler(GameManager gameManager) {
        super(MessageType.BOTS_COUNT);

        this.gameManager = gameManager;
    }

    @Override
    public void handle(MessageSenderPair message) {
        int botsCount = ((BotsCountMessage)message.getMessage()).getBotsCount();
        gameManager.setBotsCount(botsCount);
    }
}
