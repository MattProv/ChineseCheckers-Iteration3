package org.example.message;

import java.util.Arrays;

public final class CommandMessage extends Message {
    private final Commands command;
    private final String[] message;
    public CommandMessage(final Commands command, final String[] message) {
        super(MessageType.COMMAND);
        this.command = command;
        this.message = message;
    }

    public String[] getMessage() {
        return this.message;
    }

    public Commands getCommand() {
        return this.command;
    }

    public String toString()
    {
        return Arrays.toString(message);
    }
}
