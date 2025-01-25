package org.example.message;

import org.example.game_logic.Coordinate;

public class MoveMessage extends Message {
    private final Coordinate start;
    private final Coordinate end;

    public MoveMessage(Coordinate start, Coordinate end) {
        super(MessageType.MOVE);
        this.start = start;
        this.end = end;
    }

    public String toString()
    {
        return start.toString() + " " + end.toString();
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }
}
