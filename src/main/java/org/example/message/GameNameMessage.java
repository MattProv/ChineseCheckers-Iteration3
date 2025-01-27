package org.example.message;

public class GameNameMessage extends Message {

    private final String gameName;

    public GameNameMessage(String gameName) {
        super(MessageType.GAMENAME);
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
