package org.example.game_logic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract representation of a game board.
 * Provides methods to manage nodes, pawns, and bases, and enforces game-specific rules.
 */
public abstract class Board implements Serializable, Cloneable {

    /**
     * Abstract method to generate the structure of the board.
     * Must be implemented by subclasses.
     */
    public abstract void generateBoard();

    /**
     * Abstract method to define bases on the board.
     * Bases are sets of nodes grouped under specific IDs.
     */
    public abstract void defineBases();

    /**
     * Abstract method to define neighbor relationships between nodes on the board.
     * Must be implemented by subclasses.
     */
    public abstract void defineNeighbours();

    /**
     * Abstract method to move a pawn from one node to another.
     * @param move the {@link Move} object representing the start and end nodes of the move.
     */
    public abstract void move(Move move);

    /**
     * Abstract method to display the current state of the board.
     * Must be implemented by subclasses.
     */
    public abstract void showBoard();

    /**
     * Map storing the nodes of the board, indexed by their coordinates.
     */
    private Map<Coordinate, Node> Nodes = new HashMap<>();

    /**
     * Map storing bases on the board, where each base ID maps to a set of nodes.
     */
    private Map<Integer, Set<Node>> Bases = new HashMap<>();

    /**
     * Map storing pawns, indexed by the node they occupy.
     */
    private Map<Node, Pawn> Pawns = new HashMap<>();

    /**
     * Retrieves the bases on the board.
     * @return a map where the key is the base ID and the value is a set of nodes in that base.
     */
    public Map<Integer, Set<Node>> getBases() {
        return Bases;
    }

    /**
     * Retrieves all nodes on the board.
     * @return a map where the key is a {@link Coordinate} and the value is the corresponding {@link Node}.
     */
    public Map<Coordinate, Node> getNodes() {
        return Nodes;
    }

    /**
     * Retrieves a specific node by its coordinates.
     * @param coordinate the {@link Coordinate} of the node to retrieve.
     * @return the {@link Node} at the specified coordinate, or null if it does not exist.
     */
    public Node getNode(Coordinate coordinate) {
        return Nodes.get(coordinate);
    }

    /**
     * Adds a new node to the board at the specified coordinate.
     * @param coordinate the {@link Coordinate} where the node will be placed.
     */
    public void addNode(Coordinate coordinate) {
        Nodes.put(coordinate, new Node(coordinate.getX(), coordinate.getY()));
    }

    /**
     * Adds a new pawn to the board at a specific coordinate and assigns it to an owner.
     * @param coordinate the {@link Coordinate} where the pawn will be placed.
     * @param owner the {@link Agent} who owns the pawn.
     */
    public void addPawn(Coordinate coordinate, Agent owner) {
        Node node = getNode(coordinate);
        Pawn pawn = new Pawn(Pawns.size() + 1, owner, node);
        Pawns.put(node, pawn);
        node.setOccupied(pawn);
    }

    /**
     * Adds a new pawn to the board at a specific node and assigns it to an owner.
     * @param node the {@link Node} where the pawn will be placed.
     * @param owner the {@link Agent} who owns the pawn.
     */
    public void addPawn(Node node, Agent owner) {
        Pawns.put(node, new Pawn(Pawns.size() + 1, owner, node));
        node.setOccupied(Pawns.get(node));
    }

    /**
     * Retrieves the pawn located at a specific node.
     * @param node the {@link Node} to check.
     * @return the {@link Pawn} located at the node, or null if the node is unoccupied.
     */
    public Pawn getPawn(Node node) {
        return Pawns.get(node);
    }

    /**
     * Updates the position of a pawn from one node to another.
     * Handles locking the pawn in the base if it reaches its destination.
     * @param start the {@link Node} where the pawn currently resides.
     * @param end the {@link Node} where the pawn is being moved to.
     */
    public void updatePawnPosition(Node start, Node end) {
        Pawn pawn = Pawns.get(start);
        start.setUnoccupied(pawn);
        Pawns.remove(start);
        if (end.getBaseId() == pawn.getOwner().getFinishBaseIndex()) {
            pawn.makeBaseLocked();
        }
        Pawns.put(end, pawn);
        end.setOccupied(pawn);
    }

    /**
     * Assigns a base ID to a node, adding it to the specified base group.
     * @param coordinate the {@link Coordinate} of the node.
     * @param baseId the ID of the base to assign.
     */
    protected void assignBaseToNode(Coordinate coordinate, int baseId) {
        Nodes.get(coordinate).assignBase(baseId);
        Bases.computeIfAbsent(baseId, k -> new HashSet<>()).add(Nodes.get(coordinate));
    }

    public int calculateDistance(Node start, Node end) {
        for (int i = 0; i < 16; i++) {
            for (int x = start.getXCoordinate() - 2*i; x <= end.getXCoordinate() + 2*i; x++) {
                for (int y = start.getYCoordinate() - i; y <= end.getYCoordinate() + i; y++) {
                    if (this.getNode(new Coordinate(x, y)) == end)
                        return i;
                }
            }
        }
        return -1;
    }

    /**
     * Creates a deep clone of the board, including its nodes, pawns, and bases.
     * @return a deep-cloned copy of the board.
     * @throws CloneNotSupportedException if the cloning operation is not supported.
     */
    @Override
    public Board clone() throws CloneNotSupportedException {
        Board cloned = (Board) super.clone();

        // Deep copy of Pawns map
        cloned.Pawns = new HashMap<>();
        for (Map.Entry<Node, Pawn> entry : this.Pawns.entrySet()) {
            cloned.Pawns.put(entry.getKey().clone(), entry.getValue().clone());
        }

        // Deep copy of Nodes map
        cloned.Nodes = new HashMap<>();
        for (Map.Entry<Coordinate, Node> entry : this.Nodes.entrySet()) {
            cloned.Nodes.put(entry.getKey(), entry.getValue().clone());
        }

        // Deep copy of Bases map
        cloned.Bases = new HashMap<>();
        for (Map.Entry<Integer, Set<Node>> entry : this.Bases.entrySet()) {
            Set<Node> clonedSet = new HashSet<>();
            for (Node node : entry.getValue()) {
                clonedSet.add(node.clone());
            }
            cloned.Bases.put(entry.getKey(), clonedSet);
        }

        return cloned;
    }

    public abstract BoardType getBoardType();
}
