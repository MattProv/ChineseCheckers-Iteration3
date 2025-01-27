package org.example.message;

public class GameToBeLoadedMessage extends Message {
    private final String gameName;

    public GameToBeLoadedMessage(String gameName) {
        super(MessageType.LOAD_GAME);
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    @Override
    public String toString() {
        return gameName;
    }
}
