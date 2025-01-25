package org.example.message;

public class UserlistMessage extends Message {
    private final String[] message; // the message content

    public UserlistMessage(final String[] users)
    {
        super(MessageType.USERLIST);
        this.message = users;
    }

    public String[] getMessage()
    {
        return this.message;
    }

    public String toString()
    {
        return "Userlist: " + String.join(", ", message);
    }
}
