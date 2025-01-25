package org.example.message;

public class UsernameMessage extends Message {
    private final String username;

    public UsernameMessage(final String username) {
        super(MessageType.USERNAME);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String toString() {
        return username;
    }
}
