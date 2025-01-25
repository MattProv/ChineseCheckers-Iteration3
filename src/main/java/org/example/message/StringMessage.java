package org.example.message;

public final class StringMessage extends Message {
    private final String message; // the message content

    public StringMessage(final String message)
    {
        super(MessageType.STRING);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString()
    {
        return message;
    }
}