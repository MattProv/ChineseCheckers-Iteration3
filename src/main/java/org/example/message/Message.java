package org.example.message;

import java.io.Serializable;

/**
 * Message class used to send messages between the server and the client
 */
public abstract class Message implements Serializable{
    /**
     * Constructor for the Message class
     * Every message must be of known type
     * @see MessageType
     * @param type the type of the message
     */
    Message(MessageType type)
    {
        this.type = type;
    }

    protected MessageType type; // the type of the message

    public MessageType getType() {
        return type;
    }

    public abstract String toString();
}