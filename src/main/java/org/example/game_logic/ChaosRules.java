package org.example.game_logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents the standard rules for the game, implementing the {@link Rules} interface for {@link StandardBoard}.
 * This class defines the logic for validating player count, assigning bases, setting up the board, and validating moves.
 */
public class ChaosRules implements Rules<StandardBoard> {

    // The allowed player counts for the game
    private static final Set<Integer> ALLOWED_PLAYER_COUNTS = Set.of(2, 3, 4, 6);
    // The number of pawns each player starts with
    private static final int PLAYER_PAWN_COUNT = 10;

    /**
     * Validates the number of players in the game.
     *
     * @param playerCount the number of players to validate
     * @return true if the player count is valid, otherwise false
     */
    @Override
    public boolean validatePlayerCount(int playerCount) {
        return ALLOWED_PLAYER_COUNTS.contains(playerCount);
    }

    /**
     * Assigns bases to the agents (players) on the board.
     * This method determines the base assignments depending on whether the number of agents is odd or even.
     *
     * @param board the {@link StandardBoard} to assign the bases to
     * @param agents the list of {@link Agent} objects representing the players
     */
    @Override
    public void assignBasesToAgents(StandardBoard board, List<Agent> agents) {
        if (agents.size() % 2 != 0) {
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).assignBases(i * 2, (i * 2 + 3) % 6);
            }
        } else {
            int d = 6 / agents.size();
            for (int i = 0; i < agents.size(); i++) {
                agents.get(i).assignBases(i * d, (i * d + 3) % 6);
            }
        }
    }

    /**
     * Sets up the game board by placing the pawns randomly in the middle zone.
     *
     * @param board the {@link StandardBoard} to set up
     * @param agents the list of {@link Agent} objects representing the players
     * @return the updated {@link StandardBoard} after pawns have been placed
     */
    @Override
    public StandardBoard setupBoard(StandardBoard board, List<Agent> agents) {
        System.out.println("Setting up...");
        List<Node> eligibleNodes = new ArrayList<>();

        // Pre-filter eligible nodes
        for (Node node : board.getNodes().values()) {
            if (node.getBaseId() == -1 && !node.getIsOccupied()) {
                eligibleNodes.add(node);
            }
        }

        System.out.println("Empty non-base nodes: " + eligibleNodes.size());
        int totalPawnsSet = 0;

        // Assign pawns to agents
        for (Agent agent : agents) {
            int pawnsToSet = PLAYER_PAWN_COUNT;

            while (pawnsToSet > 0 && !eligibleNodes.isEmpty()) {
                // Pick a random node from the list of eligible nodes
                int randomIndex = (int) (Math.random() * eligibleNodes.size());
                Node selectedNode = eligibleNodes.get(randomIndex);
                board.addPawn(selectedNode, agent);
                pawnsToSet--;
                totalPawnsSet++;
                System.out.println("Pawn set on node: " + selectedNode);
                selectedNode.setOccupied(board.getPawn(selectedNode));
                eligibleNodes.remove(randomIndex);
            }
        }

        System.out.println(totalPawnsSet + " pawns set up");
        return board;
    }

    /**
     * Validates a move based on the current state of the board and the rules of the game.
     * This method checks step and hop moves, ensuring that all conditions are met before allowing a move.
     *
     * @param board the {@link StandardBoard} to validate the move on
     * @param move the {@link Move} object representing the start and end nodes
     * @return true if the move is valid according to the rules, otherwise false
     */
    @Override
    public boolean validateMove(StandardBoard board, Move move) {
        System.out.println("Checking move...");

        Pawn startPawn = board.getPawn(move.getStart());
        Node startNode = move.getStart();
        Node endNode = move.getEnd();
        Agent owner = startPawn.getOwner();

        // Ensure only one pawn is moved per player
        if (owner.getCurrentPawn() != null) {
            if (owner.getCurrentPawn() != startPawn) {
                System.out.println("Can't move multiple pawns in one move!");
                return false;
            }
        }

        // Check if the base is locked and prevent moving out of the finish base
        if (startPawn.isBaseLocked() && endNode.getBaseId() != owner.getFinishBaseIndex()) {
            System.out.println("Can't leave the end base after entering it!");
            return false;
        }

        // Step moves validation (neighboring nodes)
        if (startNode.getNeighbours().contains(endNode)) {
            if (owner.isStepLocked()) {
                System.out.println("Step locked: move invalid");
                return false;
            } else {
                if (endNode.getIsOccupied()) {
                    System.out.println("Can't move to an occupied node");
                    return false;
                }
                System.out.println("Valid move to an empty neighbour");
                owner.hopLock();
                owner.stepLock();
                return true;
            }
        }

        // Hop moves validation
        if (owner.isHopLocked()) {
            System.out.println("Player can't make a hop after taking a step!");
            return false;
        } else {
            // Horizontal hop check
            if (startNode.getYCoordinate() == endNode.getYCoordinate()) {
                int midX = (startNode.getXCoordinate() + endNode.getXCoordinate()) / 2;
                if (Math.abs(startNode.getXCoordinate() - endNode.getXCoordinate()) == 4 &&
                        board.getNode(new Coordinate(midX, startNode.getYCoordinate())).getIsOccupied()) {
                    System.out.println("Valid horizontal hop");
                    owner.setCurrentPawn(startPawn);
                    owner.stepLock();
                    return true;
                }
                System.out.println("Invalid horizontal hop");
                return false;
            }

            // Diagonal hop check
            if (Math.abs(startNode.getYCoordinate() - endNode.getYCoordinate()) == 2) {
                int midX = (startNode.getXCoordinate() + endNode.getXCoordinate()) / 2;
                int midY = (startNode.getYCoordinate() + endNode.getYCoordinate()) / 2;
                Node midNode = board.getNode(new Coordinate(midX, midY));
                if (midNode == null) {
                    System.out.println("Invalid diagonal hop, no pawn in-between");
                    return false;
                }
                if (midNode.getIsOccupied()) {
                    System.out.println("Valid diagonal hop");
                    owner.setCurrentPawn(startPawn);
                    owner.stepLock();
                    return true;
                }
            }
        }

        System.out.println("Move validation failed: No valid condition matched");
        return false;
    }
}