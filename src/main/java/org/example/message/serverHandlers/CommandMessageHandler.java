package org.example.message.serverHandlers;

import org.example.message.CommandMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;
import org.example.server.Server;
import org.example.server.ServerConnection;

public final class CommandMessageHandler extends MessageHandler {
    private final GameManager gameManager;

    public CommandMessageHandler(final GameManager gm) {
        super(MessageType.COMMAND);
        gameManager = gm;
    }

    @Override
    public void handle(MessageSenderPair message) {
        CommandMessage commandMessage = (CommandMessage) message.getMessage();
        ServerConnection sc = message.getConnection();

        switch (commandMessage.getCommand()) {
            case SET_PLAYER_COUNT:
                try {
                    int newPlayerCount = Integer.parseInt(commandMessage.getMessage()[1]);
                    gameManager.setPlayerCount(newPlayerCount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;

            case START_GAME:
                gameManager.startGame(Server.getServer().getConnections());
                break;
            default:
                System.out.println(commandMessage.getCommand().name() + " not implemented.");
                break;
        }
    }
}
