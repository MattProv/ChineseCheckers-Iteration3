package org.example.message.clientHandlers;

import org.example.client.GUI.GameScreen;
import org.example.client.GUI.LobbyScreen;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.message.UserlistMessage;

public class UserlistMessageHandler extends MessageHandler {
    private final LobbyScreen lobbyScreen;
    private final GameScreen gameScreen;
    public UserlistMessageHandler(final LobbyScreen lobbyScreen, final GameScreen gameScreen) {
        super(MessageType.USERLIST);

        this.lobbyScreen = lobbyScreen;
        this.gameScreen = gameScreen;
    }

    @Override
    public void handle(final MessageSenderPair message) {
        UserlistMessage userlistMessage = (UserlistMessage) message.getMessage();

        lobbyScreen.setUsersList(userlistMessage.getMessage());
        gameScreen.updateAllUsers(userlistMessage.getMessage());
    }
}
