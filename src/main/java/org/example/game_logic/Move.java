package org.example.game_logic;

import java.io.Serializable;

/**
 * Represents a move in the game, consisting of a start position and an end position.
 * This class is used to track the movement of pieces on the board, defined by their
 * start and end coordinates.
 */
public class Move implements Serializable {

    private Node startPosition;
    private Node endPosition;

    /**
     * Constructs a new Move with the specified start and end positions.
     *
     * @param startPosition the starting position of the move
     * @param endPosition the ending position of the move
     */
    public Move(Node startPosition, Node endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Returns the starting position of the move.
     *
     * @return the start position
     */
    public Node getStart() {
        return startPosition;
    }

    /**
     * Returns the ending position of the move.
     *
     * @return the end position
     */
    public Node getEnd() {
        return endPosition;
    }

    public String toString()
    {
        return startPosition.printCoordinates() + " -> " + endPosition.printCoordinates();
    }
}
