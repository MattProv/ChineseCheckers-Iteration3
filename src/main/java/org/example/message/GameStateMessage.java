package org.example.message;

import org.example.GameState;

public final class GameStateMessage extends Message {

    private final GameState gameState;
    //current turn
    int turn = 0;

    String[] players;

    public GameStateMessage(final GameState gameState, String[] players, int turn) {
        super(MessageType.GAMESTATE);
        this.gameState = gameState;
        this.players = players;
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "Game is " + (gameState.isRunning()?"running":"not running") + ".";
    }

    public GameState getGameState() {
        return gameState;
    }

    public String[] getPlayers() {
        return players;
    }

    public int getTurn() {
        return turn;
    }
}
