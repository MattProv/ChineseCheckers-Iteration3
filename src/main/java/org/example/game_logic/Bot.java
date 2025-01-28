package org.example.game_logic;

import org.example.server.GameManager;

import java.util.ArrayList;
import java.util.List;

public class Bot extends Agent {
    static double STRAGGLING_PENALTY = 0.0;
    static double ISOLATION_WEIGHT = 0.0;
    static double TOTAL_PROGRESS_PENALTY = 10.0;
    static double REACHING_GOAL_REWARD = 100.0;
    int depth = 2;
    List<Agent> agents;
    Node currentTarget;
    List<Node> reachedTargets = new ArrayList<>();

    public Bot(int id) {
        super(id, false);
    }

    @Override
    public void setAgentList(List<Agent> agents) {
        this.agents = agents;
    }

    private void updateTarget(Board board) {
        if (reachedTargets.contains(this.currentTarget)) {
            return; //If the current target wasn't reached, no need to update
        }
        for (Node node : board.getBases().get(this.getFinishBaseIndex())) {
            if (reachedTargets.contains(node)) {
                continue; //Node was already reached
            }
            this.currentTarget = node;
            return;
        }
    }

    public double totalDistancePenalty(Board board) {
        double totalDistance = 0.0;
        for (Pawn pawn : this.getPawns()) {
            totalDistance += board.calculateDistance(pawn.getLocation(), currentTarget);
        }
        return -totalDistance * TOTAL_PROGRESS_PENALTY;
    }

    public double rewardChainJumps(Board board, Agent agent) {
        int chainJumpOpportunities = 0;

        for (Pawn pawn : agent.getPawns()) {
            List<Move> possibleMoves = pawn.getAllValidMoves(board);
            for (Move move : possibleMoves) {
                if (!pawn.getLocation().getNeighbours().contains(move.getEnd())) {
                    chainJumpOpportunities++;
                }
            }
        }

        return chainJumpOpportunities * 5; // Adjust weight as needed
    }


    public double penalizeStragglingPawns(Board board) {
        int totalDistance = 0;
        int pawnCount = this.getPawns().size();

        // Calculate average distance
        for (Pawn pawn : this.getPawns()) {
            totalDistance += board.calculateDistance(pawn.getLocation(), currentTarget);
        }
        double averageDistance = (double) totalDistance / pawnCount;

        // Penalize pawns that are significantly behind
        double penalty = 0.0;
        for (Pawn pawn : this.getPawns()) {
            if (board.calculateDistance(pawn.getLocation(), currentTarget) > averageDistance) {
                penalty += (board.calculateDistance(pawn.getLocation(), currentTarget) - averageDistance) * STRAGGLING_PENALTY;
            }
        }
        return -penalty;
    }

    public double evaluateClusterFormation() {
        int isolatedPawns = 0;
        for (Pawn pawn : this.getPawns()) {
            for (Node node : pawn.getLocation().getNeighbours()) {
                if (node.getOccupant() != null)
                    if (node.getOccupant().getOwner() == pawn.getOwner())
                        break;
            }
            isolatedPawns++;
        }
        return -isolatedPawns * ISOLATION_WEIGHT; // Adjust penalty as needed
    }

    public double rewardGoalZone() {
        double goalBonus = 0.0;
        for (Pawn pawn : this.getPawns()) {
            if (pawn.isBaseLocked()) {
                goalBonus += REACHING_GOAL_REWARD; // Reward for pawns in the goal
            }
        }

        return goalBonus;
    }

    public double EvaluateBoard(Board board) {
        double move_value = 0.0;
        move_value += totalDistancePenalty(board);
        move_value += penalizeStragglingPawns(board);
        move_value += evaluateClusterFormation();
        move_value += rewardGoalZone();

        return move_value;
    }

    public Move findBestMove(Board board) throws CloneNotSupportedException {
        List<Agent> allPlayers = this.agents;
        double bestValue = Integer.MIN_VALUE;
        int depth = this.depth;
        Move bestMove = null;
        for (Pawn pawn : this.getPawns()) {
            System.out.println("\n||||||||||||| Testing for pawn " + pawn.getLocation().printCoordinates() + " ||||||||||||||");
            List<Move> possibleMoves = pawn.getAllValidMoves(board);
            for (Move move : possibleMoves) {
                Board clonedBoard = board.clone();
                System.out.println("Testing move: " + move.toString());
                clonedBoard.move(move);

                double value = minimax(clonedBoard, allPlayers, (allPlayers.indexOf(this) + 1) % allPlayers.size(), depth - 1);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                clonedBoard.revertMove(move);
            }
        }
        return bestMove;
    }

    private double minimax(Board board, List<Agent> players, int playerIndex, int depth) throws CloneNotSupportedException {
        if (depth == 0) {
            return EvaluateBoard(board);
        }

        Agent nextPlayer = players.get(playerIndex);
        System.out.println("(Depth " + depth + ") Next move: " + nextPlayer.getColor().toString());
        double bestValue = (nextPlayer == this) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Pawn pawn : nextPlayer.getPawns()) {
            System.out.println("(Depth " + depth + ") Testing for pawn " + pawn.getLocation().printCoordinates() + "\n");
            List<Move> possibleMoves = pawn.getAllValidMoves(board);
            for (Move move : possibleMoves) {
                Board clonedBoard = board.clone();
                System.out.println("Testing move: " + move.toString());
                clonedBoard.move(move);

                double value = minimax(clonedBoard, players, (playerIndex + 1) % players.size(), depth - 1);
                System.out.println(value);
                if (nextPlayer == this) {
                    bestValue = Math.max(bestValue, value); // Maximize for AI player
                } else {
                    bestValue = Math.min(bestValue, value); // Minimize for opponents
                }
                clonedBoard.revertMove(move);
            }
        }

        return bestValue;
    }


    @Override
    public void promptMove(Board board) {
        System.out.println("I was prompted to move!");
        updateTarget(board);
        System.out.println("Current target: " + currentTarget.printCoordinates());
        Move move;
        try {
            move = findBestMove(board);
            System.out.println("My move will be " + move.toString());
            GameManager.getInstance().makeMove(this, move);
            GameManager.getInstance().endTurn(this);
            return;
        }
        catch (CloneNotSupportedException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("No move to be made!");
            GameManager.getInstance().endTurn(this);
        }
        System.out.println("No move to be made!");
        GameManager.getInstance().endTurn(this);
    }
}
