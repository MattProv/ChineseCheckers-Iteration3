package org.example.game_logic;

import org.example.server.GameManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bot extends Agent {
    Node currentTarget;
    List<Node> reachedTargets = new ArrayList<>();
    public Bot(int id) {
        super(id, false);
    }

    private Node UpdateTarget(Board board) {
        reachedTargets.removeIf(node -> !node.getIsOccupied());
        reachedTargets.removeIf(node -> node.getOccupant().getOwner() != this);
        if (currentTarget == null) {
            for (Node node : board.getBases().get(this.getFinishBaseIndex())) {
                if (reachedTargets.contains(node)) {
                    continue; //Node was already reached
                }
                return node;
            }
        }
        for (Pawn pawn : getPawns()) {
            if (pawn.getLocation() == currentTarget) {
                reachedTargets.add(currentTarget);
                for (Node node : board.getBases().get(this.getFinishBaseIndex())) {
                    if (reachedTargets.contains(node)) {
                        continue; //Node was already reached
                    }
                    return node;
                }
            }
        }
        if (currentTarget.getIsOccupied())
            if (currentTarget.getOccupant().getOwner() != this) { //switch target, but don't mark as reached
                for (Node node : board.getBases().get(this.getFinishBaseIndex())) {
                    if (reachedTargets.contains(node) || node == currentTarget) {
                        continue; //Node was already reached or is occupied
                    }
                    return node;
                }
            }
        //If the current target wasn't reached, no need to update
        return currentTarget; //If all nodes in base are occupied, set null as the game has ended for that bot
    }

    public List<Move> getAllValidMoves(Board board) {
        List<Move> validMoves = new ArrayList<>();
        for (Pawn pawn : this.getPawns()) {
            System.out.println("|||||||||| Checking for pawn " + pawn.getLocation().printCoordinates() + "|||||||||||");
            Node startPosition = pawn.getLocation();
            // Get all direct neighbors of the pawn's current position
            List<Node> neighbors = pawn.getLocation().getNeighbours();

            // Check all direct moves
            for (Node neighbor : neighbors) {
                System.out.println("Checking step move to " + neighbor.printCoordinates());
                if (!neighbor.getIsOccupied()) {
                    if (!pawn.isBaseLocked() || neighbor.getBaseId() == this.getFinishBaseIndex()) {
                        System.out.println("Valid step");
                        validMoves.add(new Move(startPosition, neighbor));
                    }
                }
            }

            // Check all jump moves (recursive search for multi-jumps)
            findJumpMoves(board, startPosition, new ArrayList<>(), validMoves, pawn);
            if (!validMoves.isEmpty()) {
                System.out.println("Valid moves");
                for (Move move : validMoves) {
                    System.out.println(move.toString());
                }
            } else
                System.out.println("Pawn" + pawn.getLocation().printCoordinates() + " can't move anywhere!");
        }
        System.out.println("All valid moves found!");
        return validMoves;
    }

    private void findJumpMoves(Board board, Node currentPosition,
                               List<Node> visited, List<Move> validMoves, Pawn pawn) {
        // Mark the current position as visited to avoid cycles
        visited.add(currentPosition);
        System.out.println("Jumping from " + currentPosition.printCoordinates());
        // Get all direct neighbors
        List<Node> neighbors = currentPosition.getNeighbours();

        for (Node neighbor : neighbors) {
            if (!neighbor.getIsOccupied())
                continue; // Can only jump over occupied nodes
            Node jumpPosition = board.getNode(new Coordinate(currentPosition.getXCoordinate() + ((neighbor.getXCoordinate() - currentPosition.getXCoordinate() )*2),
                    currentPosition.getYCoordinate() + ((neighbor.getYCoordinate() - currentPosition.getYCoordinate())*2)));
            if (jumpPosition == null)
                continue;
            System.out.println("Testing jump move to " + jumpPosition.printCoordinates());
            if (!jumpPosition.getIsOccupied() && !visited.contains(jumpPosition)) {
                if (!pawn.isBaseLocked() || jumpPosition.getBaseId() == this.getFinishBaseIndex()) {
                    System.out.println("Valid move");
                    Move jumpMove = new Move(pawn.getLocation(), jumpPosition);
                    validMoves.add(jumpMove);
                    System.out.println("Checking recursively from " + jumpPosition.printCoordinates());
                    // Recursively search for further jumps
                    findJumpMoves(board, jumpPosition, new ArrayList<>(visited), validMoves, pawn);
                    System.out.println("Finished checking recursively from " + jumpPosition.printCoordinates());
                }
            }
        }
    }

    public Move findBestMove(Board board) {
        List<Move> validMoves = getAllValidMoves(board);
        List<Move> filteredMoves = new ArrayList<>(validMoves);
        // If a move allows a new pawn into base, do it
        for (Move move : filteredMoves) {
            if (move.getStart().getBaseId() != this.getFinishBaseIndex() &&
                    move.getEnd().getBaseId() == this.getFinishBaseIndex())
                return move;
        }
        // 1. Get rid of backward moves
        System.out.println(validMoves.size() + " valid moves left, removing for moving backwards...");
        for (Iterator<Move> iterator = filteredMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (board.calculateDistance(move.getEnd(), currentTarget) >
                    board.calculateDistance(move.getStart(), currentTarget) && filteredMoves.size() > 1) {
                iterator.remove();
            }
        }
        if (filteredMoves.isEmpty()) return validMoves.get(0);

        validMoves = new ArrayList<>(filteredMoves);

        // 2. Get rid of moves that give us suboptimal progress
        System.out.println(validMoves.size() + " valid moves left, removing for poor progress...");
        int maxProgress = 0;
        for (Move move : validMoves) {
            int progress = board.calculateDistance(move.getStart(), currentTarget) -
                    board.calculateDistance(move.getEnd(), currentTarget);
            if (reachedTargets.contains(move.getStart()))
                progress = 0;
            System.out.println("Move " + move.toString() + " progress: " + progress);
            maxProgress = Math.max(maxProgress, progress);
        }
        for (Iterator<Move> iterator = filteredMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (board.calculateDistance(move.getStart(), currentTarget) -
                    board.calculateDistance(move.getEnd(), currentTarget) < maxProgress && filteredMoves.size() > 1) {
                System.out.println("Removing move " + move + " with progress " + (board.calculateDistance(move.getStart(), currentTarget) -
                        board.calculateDistance(move.getEnd(), currentTarget)));
                iterator.remove();
            }
        }
        if (filteredMoves.isEmpty()) return validMoves.get(0);

        validMoves = new ArrayList<>(filteredMoves);

        // A very specific edge case, when pawns at the edge of the base clog up the exit and no one makes progress
        if (maxProgress == 0) {
            System.out.println(validMoves.size() + "Looking for the clogging...");
            for (Move move : filteredMoves) {
                if (move.getStart().getBaseId() == this.getFinishBaseIndex()) {
                    System.out.println(move.toString() + " starts in base...");
                    if (move.getEnd().getBaseId() == this.getFinishBaseIndex()) {
                        System.out.println(move.toString() + " ends in base...");
                        if (!move.getStart().getNeighbours().contains(move.getEnd())) {
                            System.out.println(move.toString() + "And is a jump.");
                            return move;
                        }
                    }
                }
            }
        }

        // 3. For all moves of optimal progress, first we want to move pawns at the back
        System.out.println(validMoves.size() + " valid moves left, removing for rushing...");
        int maxDistance = 0;
        for (Move move : validMoves) {
            int distance = board.calculateDistance(move.getStart(), currentTarget);
            maxDistance = Math.max(maxDistance, distance);
        }
        System.out.println("Max distance: " + maxDistance);
        for (Iterator<Move> iterator = filteredMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (board.calculateDistance(move.getStart(), currentTarget) < maxDistance && filteredMoves.size() > 1) {
                System.out.println("Removing move " + move + ", distance was " + board.calculateDistance(move.getStart(), currentTarget));
                iterator.remove();
            }
        }
        if (filteredMoves.isEmpty()) return validMoves.get(0);

        // 4. Unless we absolutely have to, we don't want to move pawns that reached a target
        System.out.println(validMoves.size() + " valid moves left, removing for leaving targets...");
        for (Iterator<Move> iterator = filteredMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (reachedTargets.contains(move.getStart()) && filteredMoves.size() > 1) {
                System.out.println("Removing move " + move);
                iterator.remove();
            }
        }
        if (filteredMoves.isEmpty()) return validMoves.get(0); // Return the first valid move as a fallback

        validMoves = new ArrayList<>(filteredMoves);


        // 5. If we still have multiple candidates, return random
        System.out.println(filteredMoves.size() + " valid moves left, choosing randomly...");
        return RandomElement.getRandomElement(filteredMoves);
    }



    @Override
    public void promptMove(Board board) {
        try {
            // Simulate thinking for 3 seconds
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted!");
        }
        currentTarget = UpdateTarget(board);
        if (currentTarget != null) {
            System.out.println("Current target: " + currentTarget.printCoordinates());
            Move bestMove = findBestMove(board);
            if (bestMove != null) {
                System.out.println("Chosen move: " + bestMove.toString());
                GameManager.getInstance().makeMove(this, bestMove);
            }
            else {
                System.out.println("Nowhere to move to!");
            }
        }
        else {
            System.out.println("No target set!");
        }
        GameManager.getInstance().endTurn(this);
    }
}
