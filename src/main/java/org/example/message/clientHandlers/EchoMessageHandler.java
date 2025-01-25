package org.example.message.clientHandlers;

import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.message.StringMessage;

public final class EchoMessageHandler extends MessageHandler {
    public EchoMessageHandler() {
        super(MessageType.STRING);
    }

    @Override
    public void handle(final MessageSenderPair message) {
        StringMessage stringMessage = (StringMessage) message.getMessage();

        System.out.println(stringMessage.getMessage());
    }
}
