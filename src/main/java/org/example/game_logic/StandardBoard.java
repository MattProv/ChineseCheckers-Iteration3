package org.example.game_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a standard board for the game, implementing logic for board generation, moves, and state management.
 * This class extends the abstract {@link Board} and provides specific functionality for a predefined board setup.
 * It supports serialization and cloning.
 */
public final class StandardBoard extends Board implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    private ArrayList<Move> moves = new ArrayList<>(); // Tracks all moves made on the board
    private String lastMove = null; // Tracks the last move made

    /**
     * Generates the nodes of the board and initializes their coordinates.
     * The board is created with a hexagonal pattern using predefined coordinates.
     */
    @Override
    public void generateBoard() {
        moves.clear();
        this.addNode(new Coordinate(12, 0));
        this.addNode(new Coordinate(11, 1)); this.addNode(new Coordinate(13, 1));
        for (int i = 10; i<=14; i+=2)
            this.addNode(new Coordinate(i, 2));
        for (int i = 9; i<=15; i+=2)
            this.addNode(new Coordinate(i, 3));
        for (int i = 0; i <= 24; i+=2)
            this.addNode(new Coordinate(i, 4));
        for (int i = 1; i<=23; i+=2)
            this.addNode(new Coordinate(i, 5));
        for (int i = 2; i<=22; i+=2)
            this.addNode(new Coordinate(i, 6));
        for (int i = 3; i<=21; i+=2)
            this.addNode(new Coordinate(i, 7));
        for (int i = 4; i<=20; i+=2)
            this.addNode(new Coordinate(i, 8));
        for (int i = 3; i<=21; i+=2)
            this.addNode(new Coordinate(i, 9));
        for (int i = 2; i<=22; i+=2)
            this.addNode(new Coordinate(i, 10));
        for (int i = 1; i<=23; i+=2)
            this.addNode(new Coordinate(i, 11));
        for (int i = 0; i <= 24; i+=2)
            this.addNode(new Coordinate(i, 12));
        for (int i = 9; i<=15; i+=2)
            this.addNode(new Coordinate(i, 13));
        for (int i = 10; i<=14; i+=2)
            this.addNode(new Coordinate(i, 14));
        this.addNode(new Coordinate(11, 15)); this.addNode(new Coordinate(13, 15));
        this.addNode(new Coordinate(12, 16));
    }

    /**
     * Defines the bases on the board and assigns them to specific nodes.
     * Bases are used to represent player starting and finishing zones.
     * Nodes should be added from the furthest one measuring from the center
     */
    @Override
    public void defineBases() {
        // BASE 0, counting counterclockwise from the top
        assignBaseToNode(new Coordinate(12,0),0);
        assignBaseToNode(new Coordinate(11,1),0);
        assignBaseToNode(new Coordinate(13,1),0);
        assignBaseToNode(new Coordinate(10,2),0);
        assignBaseToNode(new Coordinate(12,2),0);
        assignBaseToNode(new Coordinate(14,2),0);
        assignBaseToNode(new Coordinate(9,3),0);
        assignBaseToNode(new Coordinate(11,3),0);
        assignBaseToNode(new Coordinate(13,3),0);
        assignBaseToNode(new Coordinate(15,3),0);

        // BASE 1
        assignBaseToNode(new Coordinate(0,4),1);
        assignBaseToNode(new Coordinate(2,4),1);
        assignBaseToNode(new Coordinate(1,5),1);
        assignBaseToNode(new Coordinate(4,4),1);
        assignBaseToNode(new Coordinate(3,5),1);
        assignBaseToNode(new Coordinate(2,6),1);
        assignBaseToNode(new Coordinate(6,4),1);
        assignBaseToNode(new Coordinate(5,5),1);
        assignBaseToNode(new Coordinate(4,6),1);
        assignBaseToNode(new Coordinate(3,7),1);

        // BASE 2
        assignBaseToNode(new Coordinate(0,12),2);
        assignBaseToNode(new Coordinate(1,11),2);
        assignBaseToNode(new Coordinate(2,12),2);
        assignBaseToNode(new Coordinate(2,10),2);
        assignBaseToNode(new Coordinate(3,11),2);
        assignBaseToNode(new Coordinate(4,12),2);
        assignBaseToNode(new Coordinate(3,9),2);
        assignBaseToNode(new Coordinate(4,10),2);
        assignBaseToNode(new Coordinate(5,11),2);
        assignBaseToNode(new Coordinate(6,12),2);

        // BASE 3
        assignBaseToNode(new Coordinate(12,16),3);
        assignBaseToNode(new Coordinate(11,15),3);
        assignBaseToNode(new Coordinate(13,15),3);
        assignBaseToNode(new Coordinate(10,14),3);
        assignBaseToNode(new Coordinate(12,14),3);
        assignBaseToNode(new Coordinate(14,14),3);
        assignBaseToNode(new Coordinate(9,13),3);
        assignBaseToNode(new Coordinate(11,13),3);
        assignBaseToNode(new Coordinate(13,13),3);
        assignBaseToNode(new Coordinate(15,13),3);

        // BASE 4
        assignBaseToNode(new Coordinate(24,12),4);
        assignBaseToNode(new Coordinate(22,12),4);
        assignBaseToNode(new Coordinate(23,11),4);
        assignBaseToNode(new Coordinate(20,12),4);
        assignBaseToNode(new Coordinate(21,11),4);
        assignBaseToNode(new Coordinate(22,10),4);
        assignBaseToNode(new Coordinate(19,11),4);
        assignBaseToNode(new Coordinate(18,12),4);
        assignBaseToNode(new Coordinate(20,10),4);
        assignBaseToNode(new Coordinate(21,9),4);

        // BASE 5
        assignBaseToNode(new Coordinate(24,4),5);
        assignBaseToNode(new Coordinate(23,5),5);
        assignBaseToNode(new Coordinate(22,4),5);
        assignBaseToNode(new Coordinate(22,6),5);
        assignBaseToNode(new Coordinate(21,5),5);
        assignBaseToNode(new Coordinate(20,4),5);
        assignBaseToNode(new Coordinate(18,4),5);
        assignBaseToNode(new Coordinate(19,5),5);
        assignBaseToNode(new Coordinate(20,6),5);
        assignBaseToNode(new Coordinate(21,7),5);
    }

    /**
     * Defines neighbors for each node on the board based on predefined offsets.
     * Neighbors are calculated relative to each node's position.
     */
    @Override
    public void defineNeighbours() {
        for (Node node : getNodes().values()) {
            int x = node.getXCoordinate();
            int y = node.getYCoordinate();
            node.addNeighbour(getNode(new Coordinate(x + 2, y)));
            node.addNeighbour(getNode(new Coordinate(x - 2, y)));
            node.addNeighbour(getNode(new Coordinate(x + 1, y + 1)));
            node.addNeighbour(getNode(new Coordinate(x - 1, y + 1)));
            node.addNeighbour(getNode(new Coordinate(x + 1, y - 1)));
            node.addNeighbour(getNode(new Coordinate(x - 1, y - 1)));
        }
    }
    @Override
    public int calculateDistance(Node start, Node end) {
        int distance = 0;

        if (start.equals(end)) {
            return distance; // If start and end are the same, return 0
        }

        HashSet<Node> checkedNodes = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        // Start BFS
        checkedNodes.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // Number of nodes at the current distance level

            // Process all nodes at the current level
            for (int i = 0; i < levelSize; i++) {
                Node currentNode = queue.poll();

                for (Node neighbour : currentNode.getNeighbours()) {
                    if (neighbour.equals(end)) {
                        return distance + 1; // End node found, return the distance
                    }

                    if (!checkedNodes.contains(neighbour)) {
                        checkedNodes.add(neighbour); // Mark the node as visited
                        queue.add(neighbour); // Add to the queue for further exploration
                    }
                }
            }

            distance++; // Increment distance after processing the level
        }

        // If we exhaust the queue (which won't happen in your case), return -1 as a fallback
        return -1;
    }

    /**
     * Moves a pawn from the start node to the end node on the board.
     *
     * @param move The {@link Move} object representing the start and end coordinates of the move.
     * @throws IllegalStateException if no pawn exists at the starting node.
     */
    @Override
    public void move(final Move move) {
        if (this.getPawn(move.getStart()) == null) {
            throw new IllegalStateException("No pawn at the starting node!");
        }
        this.getPawn(move.getStart()).updatePosition(move.getEnd());
        this.updatePawnPosition(move.getStart(), move.getEnd());
        moves.add(move);
        lastMove = move.getStart() + " -> " + move.getEnd();
        System.out.println("Move " + lastMove);
    }

    /**
     * Displays the current state of the board, including the last move and all past moves.
     * Mainly used for debugging or visualization purposes.
     */
    @Override
    public void showBoard() {
        System.out.println("Last move: " + lastMove);
        System.out.println("MOVES:");
        for (Move move : moves) {
            System.out.println("Move " + move.getStart() + " to " + move.getEnd());
        }
    }

    /**
     * Creates and returns a deep copy of the StandardBoard.
     *
     * @return A cloned instance of the current board.
     * @throws CloneNotSupportedException if cloning fails.
     */
    @Override
    public StandardBoard clone() throws CloneNotSupportedException {
        StandardBoard cloned = (StandardBoard) super.clone();
        cloned.moves = new ArrayList<>(this.moves); // Copy moves list
        cloned.lastMove = this.lastMove; // Copy the last move
        return cloned;
    }

    @Override
    public BoardType getBoardType() {
        return BoardType.STANDARD;
    }
}
