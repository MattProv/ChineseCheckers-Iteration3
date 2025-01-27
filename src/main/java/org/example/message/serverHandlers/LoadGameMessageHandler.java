package org.example.message.serverHandlers;

import org.example.message.GameToBeLoadedMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;
import org.example.server.db.GameService;

public class LoadGameMessageHandler extends MessageHandler {
    private final GameManager gameManager;
    private final GameService gameService;

    public LoadGameMessageHandler(GameManager gameManager, GameService gameService) {
        super(MessageType.LOAD_GAME);
        this.gameManager = gameManager;
        this.gameService = gameService;
    }

    @Override
    public void handle(MessageSenderPair message) {
        gameService.getAllGames().forEach(game -> {
            String gameName = ((GameToBeLoadedMessage) message.getMessage()).getGameName();
            if(game.getName() != null && game.getName().equalsIgnoreCase(gameName))
            {
                gameManager.setGameToBeLoaded(game);
            }
        });
    }
}
