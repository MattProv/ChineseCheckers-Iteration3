package org.example.server;

import org.example.game_logic.Agent;
import org.example.game_logic.Move;
import org.example.message.StringMessage;

public class GameManagerCallbackHandler {
    public void onGameStarted() {
        System.out.println("Game started");
        Server.getServer().Broadcast(new StringMessage("Game started!"));
    }
    public void onGameEnded(Agent agent) {
        System.out.println("Game ended");
        Server.getServer().Broadcast(new StringMessage("Game ended! " + agent + " won!"));
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
}
