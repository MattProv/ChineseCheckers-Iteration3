package org.example.client;

import org.example.message.Message;

public class ClientCallbacksHandler {

    public void onDisconnect() {
        System.out.println("Disconnected");
    }

    public void onConnect() {
        System.out.println("Connected");
    }

    public void onMessageReceived(Message message) {
        System.out.println("Message received: " + message.getType().name() + " " + message);
        synchronized (Client.getClient()) {
            Client.getClient().HandleMessages();
        }
    }

    public void onMessageSent(Message message) {
        System.out.println("Message sent: " + message.getType().name() + " " + message);
    }

    public void onSocketError() {
        System.out.println("Error: Received a null message.");
    }
}
