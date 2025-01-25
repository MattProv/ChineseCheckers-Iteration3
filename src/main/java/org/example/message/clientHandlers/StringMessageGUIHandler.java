package org.example.message.clientHandlers;

import org.example.client.GUI.GameScreen;
import org.example.client.GUI.LobbyScreen;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;

public class StringMessageGUIHandler extends MessageHandler {

    private final LobbyScreen lobbyScreen;
    private final GameScreen gameScreen;

    public StringMessageGUIHandler(final LobbyScreen lobbyScreen, GameScreen gameScreen) {
        super(MessageType.STRING);
        this.lobbyScreen = lobbyScreen;
        this.gameScreen = gameScreen;
    }

    @Override
    public void handle(MessageSenderPair message) {
        lobbyScreen.showServerMessage(message.getMessage().toString());
        gameScreen.showServerMessage(message.getMessage().toString());
    }
}
