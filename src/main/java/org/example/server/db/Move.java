package org.example.server.db;

import org.example.game_logic.Coordinate;

public class Move {
    private Coordinate start;
    private Coordinate end;

    public Move() {
    }

    public Move(Coordinate startPosition, Coordinate endPosition) {
        this.start = startPosition;
        this.end = endPosition;
    }

    public void setStartPosition(Coordinate startPosition) {
        this.start = startPosition;
    }

    public void setEndPosition(Coordinate endPosition) {
        this.end = endPosition;
    }

    public Coordinate getStartPosition() {
        return start;
    }

    public Coordinate getEndPosition() {
        return end;
    }
}
