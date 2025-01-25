package org.example.message;

public final class DisconnectMessage extends Message{
    public DisconnectMessage(){
        super(MessageType.DISCONNECT);
    }

    public String toString(){
        return "DISCONNECT";
    }
}
