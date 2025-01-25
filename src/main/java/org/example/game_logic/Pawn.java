package org.example.game_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pawn in the game. Each pawn has an ID, an owner, a location on the board,
 * and a lock status indicating whether it has reached a base and is restricted from moving.
 */
public class Pawn implements Serializable, Cloneable {

    private int id;
    private Agent owner;
    private boolean isBaseLocked = false;
    private Node location;

    /**
     * Constructs a new Pawn with the specified ID, owner, and starting location.
     *
     * @param id the unique identifier of the pawn
     * @param owner the agent that owns the pawn
     * @param location the initial location of the pawn on the board
     */
    public Pawn(int id, Agent owner, Node location) {
        this.id = id;
        this.owner = owner;
        this.owner.addPawn(this);
        this.location = location;
    }

    /**
     * Updates the position of the pawn to a new location.
     * If the pawn reaches a finish base, its base lock is activated.
     *
     * @param newLocation the new location to move the pawn to
     */
    public void updatePosition(Node newLocation) {
        this.location = newLocation;
        this.location.setOccupied(this);
        if (this.location.getBaseId() == this.getOwner().getFinishBaseIndex()) {
            this.isBaseLocked = true;
        }
    }

    /**
     * Locks the pawn at its current base, preventing it from moving further.
     */
    public void makeBaseLocked() {
        this.isBaseLocked = true;
    }

    /**
     * Returns whether the pawn is locked at its base.
     *
     * @return true if the pawn is locked at its base, false otherwise
     */
    public boolean isBaseLocked() {
        return isBaseLocked;
    }

    /**
     * Returns the owner of the pawn.
     *
     * @return the agent who owns the pawn
     */
    public Agent getOwner() {
        return owner;
    }

    /**
     * Returns the location of the pawn.
     *
     * @return the Node where the pawn resides
     */
    public Node getLocation() {
        return location;
    }

    public List<Move> getAllValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        Node startPosition = this.getLocation();

        // Get all direct neighbors of the pawn's current position
        List<Node> neighbors = this.location.getNeighbours();

        // Check all direct moves
        for (Node neighbor : neighbors) {
            if (!neighbor.getIsOccupied()) { // Check if the neighbor is empty
                validMoves.add(new Move(startPosition, neighbor));
            }
        }

        // Check all jump moves (recursive search for multi-jumps)
        findJumpMoves(board, startPosition, new ArrayList<>(), validMoves);
        System.out.println(startPosition.printCoordinates());
        System.out.println("Can move to:");
        for (Move move : validMoves) {
            move.getEnd().printCoordinates();
        }
        return validMoves;
    }

    private void findJumpMoves(Board board, Node currentPosition,
                               List<Node> visited, List<Move> validMoves) {
        // Mark the current position as visited to avoid cycles
        visited.add(currentPosition);

        // Get all direct neighbors
        List<Node> neighbors = currentPosition.getNeighbours();

        for (Node neighbor : neighbors) {
            Node jumpPosition = board.getNode(new Coordinate((currentPosition.getXCoordinate() - neighbor.getXCoordinate() )*2,
                    (currentPosition.getYCoordinate() - neighbor.getYCoordinate())*2));

            // Check if the jump is valid: neighbor is occupied, and jumpPosition is empty
            if (!neighbor.getIsOccupied() && jumpPosition.getIsOccupied()
                    && !visited.contains(jumpPosition)) {

                Move jumpMove = new Move(currentPosition, jumpPosition);
                validMoves.add(jumpMove);

                // Recursively search for further jumps
                findJumpMoves(board, jumpPosition, new ArrayList<>(visited), validMoves);
            }
        }
    }


    /**
     * Creates a clone of the pawn, copying its base lock status.
     *
     * @return a new Pawn object that is a clone of the current one
     * @throws CloneNotSupportedException if cloning is not supported
     */
    @Override
    public Pawn clone() throws CloneNotSupportedException {
        Pawn pawn = (Pawn) super.clone();
        pawn.isBaseLocked = this.isBaseLocked;
        return pawn;
    }
}
