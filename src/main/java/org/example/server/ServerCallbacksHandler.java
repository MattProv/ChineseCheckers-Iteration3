package org.example.server;

import org.example.message.Message;

public class ServerCallbacksHandler {

    public void onNewConnection(final ServerConnection connection) {
        System.out.println("User connected: " + connection);
        GameManager.getInstance().synchronizeGameState();
    }

    public void onConnectionClosed(final ServerConnection connection) {
        System.out.println("User disconnected: " + connection);
    }

    public void onMessageReceived(final ServerConnection connection, final Message message) {
        System.out.println("Message received: [" + connection + "] " + message.getType().name() + " " + message);
        synchronized ( Server.getServer()) {
            Server.getServer().HandleMessages();
        }
    }

    public void onMessageSent(final ServerConnection connection, final Message message) {
        System.out.println("Message sent: [" + connection + "] " + message.getType().name() + " " + message);
    }
}
