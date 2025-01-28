package org.example.server;

import org.example.game_logic.Agent;
import org.example.game_logic.Move;
import org.example.message.StringMessage;

public class GameManagerCallbackHandler {
    public void onGameStarted() {
        System.out.println("Game started");
        Server.getServer().Broadcast(new StringMessage("Game started!"));
    }
    public void onGameEnded() {
        System.out.println("Game ended");
        Server.getServer().Broadcast(new StringMessage("Game ended!"));
    }

    public void onGameNotStarted(String reason)
    {
        System.out.println("Game not started: " + reason);
        Server.getServer().Broadcast(new StringMessage("Game not started: " + reason));
    }

    public void onPlayerCountChanged(int oldCount, int newCount)
    {
        System.out.println("Player count changed from " + oldCount + " to " + newCount);
        Server.getServer().Broadcast(new StringMessage("Player count changed from " + oldCount + " to " + newCount));
    }

    public void onPlayerCountNotChanged(int newCount, String reason)
    {
        System.out.println("Player count not changed to " + newCount + ": " + reason);
        Server.getServer().Broadcast(new StringMessage("Player count not changed to " + newCount + ": " + reason));
    }

    public void onBoardNotChanged(String reason) {
        System.out.println("Board not changed: " + reason);
        Server.getServer().Broadcast(new StringMessage("Board not changed: " + reason));
    }

    public void onBoardChanged(String info) {
        System.out.println("Board changed: " + info);
        Server.getServer().Broadcast(new StringMessage("Board changed: " + info));
    }

    public void onRulesNotChanged(String reason) {
        System.out.println("Rules not changed: " + reason);
        Server.getServer().Broadcast(new StringMessage("Rules not changed: " + reason));
    }

    public void onRulesChanged(String info) {
        System.out.println("Rules changed: " + info);
        Server.getServer().Broadcast(new StringMessage("Rules changed: " + info));
    }

    public void onInvalidMove(Agent agent, Move move, String reason) {
        System.out.println("Invalid move by " + agent + ": " + reason);
        Server.getServer().Broadcast(new StringMessage("Invalid move by " + agent + ": " + reason));
    }

    public void onValidMove(Agent agent, Move move, String s) {
        System.out.println("Valid move by " + agent + ": " + s);
        Server.getServer().Broadcast(new StringMessage("Valid move by " + agent + ": " + s));
    }

    public void onGameNotLoaded(String reason) {
        System.out.println("Game not loaded: " + reason);
        Server.getServer().Broadcast(new StringMessage("Game not loaded: " + reason));
    }

    public void onGameLoaded(String gameName) {
        System.out.println("Game loaded: " + gameName);
        Server.getServer().Broadcast(new StringMessage("Game loaded: " + gameName));
    }

    public void onTurnChange(Agent oldTurn, Agent currentTurn, int turnIndex) {
        System.out.println("Turn changed from " + oldTurn + " to " + currentTurn);
        Server.getServer().Broadcast(new StringMessage("Turn changed from " + oldTurn + " to " + currentTurn));
    }

    public void onGameNameChanged(String gameName) {
        System.out.println("Game name changed to " + gameName);
        Server.getServer().Broadcast(new StringMessage("Game name changed to " + gameName));
    }

    public void onBotsCountChanged(int botsCount) {
        System.out.println("Bots count changed to " + botsCount);
        Server.getServer().Broadcast(new StringMessage("Bots count changed to " + botsCount));
    }

    public void onPlayerFinished(Agent agent, int position) {
        if (position == 1) {
            System.out.println("We have a winner: " + agent);
            Server.getServer().Broadcast(new StringMessage(agent + " has won!"));
        }
        else {
            System.out.println("Player finished: " + agent);
            Server.getServer().Broadcast(new StringMessage(agent + " takes " + position + " place!"));
        }
    }
}
