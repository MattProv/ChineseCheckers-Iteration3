package org.example.message;

public class EndTurnMessage extends Message {
    public EndTurnMessage() {
        super(MessageType.END_TURN);
    }

    @Override
    public String toString() {
        return "END_TURN";
    }
}
