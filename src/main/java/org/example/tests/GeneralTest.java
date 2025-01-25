package org.example.tests;

import org.example.game_logic.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class GeneralTest {

    private StandardBoard board;
    private StandardRules rules;

    @Before
    public void setUp() {
        board = new StandardBoard();
        board.generateBoard();
        board.defineBases();
        board.defineNeighbours();
        this.rules = new StandardRules();
    }

    @Test
    public void testBoardGeneration() {
        Map<Coordinate, Node> nodes = board.getNodes();
        assertFalse(nodes.isEmpty());
        assertEquals(121, nodes.size());
    }

    @Test
    public void testBaseDefinitions() {
        Map<Integer, Set<Node>> bases = board.getBases();
        assertEquals(6, bases.size());
        assertEquals(10, bases.get(0).size());
        for (Node node : bases.get(0)) {
            System.out.println(node.getXCoordinate() + " " + node.getYCoordinate());
        }
        System.out.println(board.getNode(new Coordinate(12, 0)).getXCoordinate() + " " + board.getNode(new Coordinate(12, 0)).getYCoordinate());
        assertTrue(bases.get(0).contains(board.getNode(new Coordinate(12, 0))));
    }

    @Test
    public void testAddPawn() {
        Coordinate coordinate = new Coordinate(12, 0);
        Node node = board.getNode(coordinate);
        Agent agent = new Agent(1, true);

        board.addPawn(coordinate, agent);
        assertTrue(node.getIsOccupied());
        Pawn pawn = board.getPawn(node);
        assertNotNull(pawn);
        assertEquals(agent, pawn.getOwner());
    }

    @Test
    public void testNeighbourMove() {
        Coordinate start = new Coordinate(12, 0);
        Coordinate end = new Coordinate(11, 1);
        Move move = new Move(board.getNode(start), board.getNode(end));
        Agent agent = new Agent(1, true);
        board.addPawn(start, agent);
        Pawn pom = board.getPawn(board.getNode(start));
        if (rules.validateMove(board, move))
            board.move(new Move(board.getNode(start), board.getNode(end)));
        assertEquals(pom, board.getPawn(board.getNode(end)));
    }

    @Test
    public void testInvalidCoordinateMove() {
        Coordinate start = new Coordinate(12, 0);
        Coordinate end = new Coordinate(11, 3);
        Move move = new Move(board.getNode(start), board.getNode(end));
        Agent agent = new Agent(1, true);
        board.addPawn(start, agent);
        Pawn pom = board.getPawn(board.getNode(start));
        assertFalse(rules.validateMove(board, move));
    }

    @Test
    public void testInvalidOccupiedMove() {
        Coordinate start = new Coordinate(12, 0);
        Coordinate end = new Coordinate(11, 3);
        Move move = new Move(board.getNode(start), board.getNode(end));
        Agent agent = new Agent(1, true);
        board.addPawn(start, agent);
        board.addPawn(end, agent);
        Pawn pom = board.getPawn(board.getNode(start));
        assertFalse(rules.validateMove(board, move));
    }

    @Test
    public void testValidHopMove() {
        Coordinate start = new Coordinate(12, 0);
        Coordinate occupiant = new Coordinate(11, 1);
        Coordinate end = new Coordinate(10, 2);
        Move move = new Move(board.getNode(start), board.getNode(end));
        Agent agent = new Agent(1, true);
        board.addPawn(start, agent);
        board.addPawn(board.getNode(occupiant), agent);
        Pawn pom = board.getPawn(board.getNode(start));
        if (rules.validateMove(board, move))
            board.move(new Move(board.getNode(start), board.getNode(end)));
        assertEquals(pom, board.getPawn(board.getNode(end)));
    }



    @Test
    public void testShowBoard() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        board.showBoard();
        String output = outContent.toString();

        assertTrue(output.contains("Last move: null"));
        assertTrue(output.contains("MOVES:"));
    }

    @Test
    public void testCloneBoard() throws CloneNotSupportedException {
        StandardBoard clonedBoard = board.clone();
        assertNotSame(board, clonedBoard);
        assertEquals(board.getNodes().size(), clonedBoard.getNodes().size());
        assertEquals(board.getBases().size(), clonedBoard.getBases().size());
    }

    @Test
    public void testCheckWinCondition() {
        // Create the agent
        Agent agent = new Agent(1, true);

        // Create the board and base nodes
        StandardBoard board = new StandardBoard();
        int baseId = 0;
        int numPawns = 5;
        Node helpNode = new Node(0, 12);
        Pawn helpPawn = new Pawn(0, agent, helpNode);
        helpNode.setOccupied(helpPawn);
        assertFalse(rules.checkWinCondition(agent));
        helpPawn.makeBaseLocked();
        for (int i = 1; i < numPawns; i++) {
            Node baseNode = new Node(i, 0, baseId); // Example coordinates
            Pawn pawn = new Pawn(i + 1, agent, baseNode);
            baseNode.setOccupied(pawn);
            pawn.makeBaseLocked(); // Simulate pawn being locked in base
        }

        // Assert that all pawns are in the base and win condition is true
        assertTrue(rules.checkWinCondition(agent));


    }
}

