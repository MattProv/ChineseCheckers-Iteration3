package org.example.message;

public class PromptMoveMessage extends Message {
    public PromptMoveMessage() {
        super(MessageType.PROMPT_MOVE);
    }

    @Override
    public String toString() {
        return "PromptMoveMessage{}";
    }
}
