package org.example.game_logic;

import org.example.server.GameManager;

import java.util.List;

public class Bot extends Agent {
    static double STRAGGLING_PENALTY = 5.0;
    static double ISOLATION_WEIGHT = 2.0;
    static double TOTAL_PROGRESS_PENALTY = 0.1;
    static double REACHING_GOAL_REWARD = 15.0;
    int depth = 5;
    List<Agent> agents;
    Node currentTarget;
    List<Node> reachedTargets;

    public Bot(int id) {
        super(id, false);
    }

    @Override
    public void setAgentList(List<Agent> agents) {
        this.agents = agents;
    }

    private Node updateTarget(Board board) {
        if (reachedTargets.contains(currentTarget)) {
            return currentTarget; //If the current target wasn't reached, no need to update
        }
        for (Node node : board.getBases().get(this.getFinishBaseIndex())) {
            if (!reachedTargets.contains(node)) {
                continue; //Node was already reached
            }
            return node;
        }
        return null; //If all nodes in base are occupied, set null as the game has ended for that bot
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
            List<Move> possibleMoves = pawn.getAllValidMoves(board);
            for (Move move : possibleMoves) {
                Board clonedBoard = board.clone();
                clonedBoard.move(move);

                double value = minimax(clonedBoard, allPlayers, (allPlayers.indexOf(this) + 1) % allPlayers.size(), depth - 1);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private double minimax(Board board, List<Agent> players, int playerIndex, int depth) throws CloneNotSupportedException {
        if (depth == 0) {
            return EvaluateBoard(board);
        }

        Agent nextPlayer = players.get(playerIndex);
        double bestValue = (nextPlayer == this) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Pawn pawn : nextPlayer.getPawns()) {
            List<Move> possibleMoves = pawn.getAllValidMoves(board);
            for (Move move : possibleMoves) {
                Board clonedBoard = board.clone();
                clonedBoard.move(move);

                double value = minimax(clonedBoard, players, (playerIndex + 1) % players.size(), depth - 1);

                if (nextPlayer == this) {
                    bestValue = Math.max(bestValue, value); // Maximize for AI player
                } else {
                    bestValue = Math.min(bestValue, value); // Minimize for opponents
                }
            }
        }

        return bestValue;
    }


    @Override
    public void promptMove(Board board) {
        updateTarget(board);
        Move move;
        try {
            move = findBestMove(board);
            GameManager.getInstance().makeMove(this, move);
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        System.out.println("No move to be made!");
    }
}
