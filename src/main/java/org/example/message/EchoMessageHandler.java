package org.example.message;

import org.example.server.Server;
import org.example.server.ServerConnection;

public final class EchoMessageHandler extends MessageHandler {
    public EchoMessageHandler() {
        super(MessageType.STRING);
    }

    @Override
    public void handle(final MessageSenderPair message) {
        StringMessage stringMessage = (StringMessage) message.getMessage();
        ServerConnection sc = message.getConnection();

        System.out.println(stringMessage.getMessage());
        Server.getServer().Send(stringMessage, sc);
    }
}
