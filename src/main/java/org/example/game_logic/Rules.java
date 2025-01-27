package org.example.game_logic;

import java.util.List;

/**
 * Defines the rules for a specific type of board game, including player count validation,
 * move validation, win condition checking, and board setup.
 *
 * @param <T> the type of the board that the rules apply to
 */
public interface Rules<T extends Board> {

    /**
     * Validates the number of players for the game based on the rules.
     *
     * @param playerCount the number of players
     * @return {@code true} if the player count is valid according to the rules; {@code false} otherwise
     */
    boolean validatePlayerCount(int playerCount);

    /**
     * Validates the move made by a player on the game board.
     *
     * @param board the current game board
     * @param move the move to validate
     * @return {@code true} if the move is valid according to the rules; {@code false} otherwise
     */
    boolean validateMove(T board, Move move);

    /**
     * Checks if the given agent (player) has won the game based on the rules.
     * A player is considered to have won if all of their pawns are base locked.
     *
     * @param agent the agent (player) to check for a win condition
     * @return {@code true} if the agent has won the game; {@code false} otherwise
     */
    default boolean checkWinCondition(Agent agent) {
        for (Pawn pawn : agent.getPawns()) {
            if (!pawn.isBaseLocked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assigns bases to the agents based on the game rules.
     *
     * @param board the game board
     * @param agents the list of agents (players) in the game
     */
    void assignBasesToAgents(T board, List<Agent> agents);

    /**
     * Sets up the game board with the necessary elements according to the rules.
     *
     * @param board the game board to set up
     * @param agents the list of agents (players) in the game
     * @return the setup game board
     */
    T setupBoard(T board, List<Agent> agents);

    RulesType getRulesType();
}
