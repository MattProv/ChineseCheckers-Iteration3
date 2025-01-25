package org.example;

import org.example.game_logic.Board;

import java.io.Serializable;

/**
 * Represents the state of the game, including the current game board and whether the game is running.
 * This class is serializable and supports cloning.
 */
public final class GameState implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private Board board;  // The current game board
    private boolean isRunning;

    /**
     * Constructs a GameState with the specified board and running state.
     *
     * @param board the current game board
     * @param isRunning {@code true} if the game is running; {@code false} otherwise
     */
    public GameState(final Board board, final boolean isRunning) {
        this.board = board;
        this.isRunning = isRunning;
    }

    /**
     * Default constructor for GameState, initializing with no board and the game not running.
     */
    public GameState() {
        this.board = null;
        this.isRunning = false;
    }

    /**
     * Gets the current game board.
     *
     * @return the current game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Checks if the game is currently running.
     *
     * @return {@code true} if the game is running; {@code false} otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets the state of the game using another GameState object.
     *
     * @param state the GameState to copy the state from
     */
    public void setState(final GameState state) {
        this.isRunning = state.isRunning;
        this.board = state.board;
    }

    /**
     * Sets the running state of the game.
     *
     * @param b {@code true} to indicate the game is running; {@code false} otherwise
     */
    public void setRunning(final boolean b) {
        this.isRunning = b;
    }

    /**
     * Sets the current game board.
     *
     * @param board the board to set
     */
    public void setBoard(final Board board) {
        this.board = board;
    }

    /**
     * Creates and returns a clone of the current GameState.
     * If the current board is not null, it will also be cloned.
     *
     * @return a cloned GameState object
     */
    @Override
    public GameState clone() {
        try {
            GameState cloned = (GameState) super.clone();
            if (this.board != null) {
                cloned.board = (Board) this.board.clone();  // Ensure Board implements Cloneable
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
