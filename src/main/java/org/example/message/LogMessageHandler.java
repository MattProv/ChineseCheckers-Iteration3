package org.example.message;

import org.example.server.ServerConnection;

public final class LogMessageHandler extends MessageHandler {
    public LogMessageHandler(final MessageType type) {
        super(type);
    }

    @Override
    public void handle(final MessageSenderPair message) {
        Message msg = message.getMessage();
        ServerConnection sc = message.getConnection();

        System.out.println((sc!=null?sc.toString() + ": ":"") + "[" + msg.getType().name() +"] " + msg.toString());
    }
}
