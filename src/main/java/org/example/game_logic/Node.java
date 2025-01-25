package org.example.game_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node on a game board.
 * A node has coordinates, a potential base assignment, and tracks its occupancy status and neighbors.
 */
public class Node implements Serializable, Cloneable {

    /**
     * List of neighboring nodes connected to this node.
     */
    private List<Node> neighbours = new ArrayList<>();

    /**
     * The coordinates of this node.
     */
    private Coordinate coordinate;

    /**
     * Indicates if the node is currently occupied by a pawn.
     */
    private boolean isOccupied;

    /**
     * The pawn occupying the node, if any.
     */
    private Pawn occupant = null;

    /**
     * The ID of the base to which this node belongs, or -1 if it does not belong to any base.
     */
    private int baseId = -1;

    /**
     * Creates a node with specified coordinates.
     * @param xCoordinate the x-coordinate of the node.
     * @param yCoordinate the y-coordinate of the node.
     */
    public Node(int xCoordinate, int yCoordinate) {
        this.coordinate = new Coordinate(xCoordinate, yCoordinate);
        this.isOccupied = false;
    }

    /**
     * Creates a node with specified coordinates and base ID.
     * @param xCoordinate the x-coordinate of the node.
     * @param yCoordinate the y-coordinate of the node.
     * @param baseId the ID of the base to which this node belongs.
     */
    public Node(int xCoordinate, int yCoordinate, int baseId) {
        this.coordinate = new Coordinate(xCoordinate, yCoordinate);
        this.isOccupied = false;
        this.baseId = baseId;
    }

    /**
     * Assigns a base ID to the node. If the node already belongs to a base, the assignment is ignored.
     * @param baseId the ID of the base to assign.
     */
    public void assignBase(int baseId) {
        if (this.baseId != -1) {
            System.out.println("Attempted to reassign a base ID: node already assigned base id: " + this.baseId);
            return;
        }
        this.baseId = baseId;
    }

    /**
     * Checks if the node is currently occupied.
     * @return true if the node is occupied, false otherwise.
     */
    public boolean getIsOccupied() {
        return isOccupied;
    }

    /**
     * Marks the node as occupied and assigns the specified pawn to it.
     * @param pawn the {@link Pawn} occupying the node.
     */
    public void setOccupied(Pawn pawn) {
        this.isOccupied = true;
        this.occupant = pawn;
    }

    /**
     * Marks the node as unoccupied if the specified pawn is currently occupying it.
     * @param occupant the {@link Pawn} currently occupying the node.
     */
    public void setUnoccupied(Pawn occupant) {
        if (this.occupant != occupant) {
            return;
        }
        this.isOccupied = false;
        this.occupant = null;
    }

    /**
     * Adds a neighboring node to this node.
     * @param neighbour the {@link Node} to add as a neighbor.
     */
    public void addNeighbour(Node neighbour) {
        if (neighbour != null) {
            neighbours.add(neighbour);
        }
    }

    /**
     * Retrieves the list of neighboring nodes.
     * @return a list of {@link Node} objects representing the neighbors.
     */
    public List<Node> getNeighbours() {
        return neighbours;
    }

    /**
     * Checks if any of the neighboring nodes are occupied.
     * @return true if at least one neighbor is occupied, false otherwise.
     */
    public boolean hasOccupiedNeighbours() {
        for (Node neighbour : neighbours) {
            if (neighbour.getIsOccupied()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the pawn currently occupying the node.
     * @return the {@link Pawn} occupying the node, or null if the node is unoccupied.
     */
    public Pawn getOccupant() {
        return occupant;
    }

    /**
     * Retrieves the base ID of the node.
     * @return the base ID, or -1 if the node does not belong to any base.
     */
    public int getBaseId() {
        return baseId;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Retrieves the x-coordinate of the node.
     * @return the x-coordinate.
     */
    public int getXCoordinate() {
        return coordinate.getX();
    }

    /**
     * Retrieves the y-coordinate of the node.
     * @return the y-coordinate.
     */
    public int getYCoordinate() {
        return coordinate.getY();
    }

    /**
     * Returns a string representation of the node's coordinates.
     * @return a string in the format "(x, y)".
     */
    public String printCoordinates() {
        return "(" + coordinate.getX() + "," + coordinate.getY() + ")";
    }

    /**
     * Compares this node to another object for equality.
     * Two nodes are considered equal if they have the same coordinates.
     * @param o the object to compare to.
     * @return true if the nodes are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(coordinate, node.coordinate);
    }

    /**
     * Computes the hash code for the node based on its coordinates.
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }

    /**
     * Creates a deep clone of this node, including its occupant if present.
     * @return a deep-cloned copy of the node.
     * @throws CloneNotSupportedException if the cloning operation is not supported.
     */
    @Override
    public Node clone() throws CloneNotSupportedException {
        Node cloned = (Node) super.clone();
        cloned.isOccupied = this.isOccupied;
        if (this.occupant != null) {
            cloned.occupant = this.occupant.clone();
        }
        return cloned;
    }
}
