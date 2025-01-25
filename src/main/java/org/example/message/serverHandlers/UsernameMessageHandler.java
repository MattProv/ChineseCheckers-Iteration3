package org.example.message.serverHandlers;

import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.message.UsernameMessage;
import org.example.server.GameManager;
import org.example.server.ServerConnection;
import org.example.server.User;

public class UsernameMessageHandler extends MessageHandler {
    private final GameManager gameManager;

    public UsernameMessageHandler(final GameManager gameManager) {
        super(MessageType.USERNAME);
        this.gameManager = gameManager;
    }

    @Override
    public void handle(final MessageSenderPair message) {
        UsernameMessage usernameMessage = (UsernameMessage) message.getMessage();
        ServerConnection sc = message.getConnection();

        gameManager.addUser(new User(usernameMessage.getUsername(), sc));

        gameManager.synchronizeUsers();
    }
}