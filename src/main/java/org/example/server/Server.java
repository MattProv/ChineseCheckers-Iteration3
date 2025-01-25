package org.example.server;

import org.example.message.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//TODO:
// Implement logging rather than println and printStackTrace
public final class Server {

    private static Server instance;

    private final List<MessageHandler> messageHandlers = new ArrayList<>();

    private Thread listenerThread;
    private ServerSocket serverSocket;

    private final Queue<MessageSenderPair> messageQueue = new LinkedList<>();
    private final List<ServerConnection> connections = new ArrayList<>();

    public ServerCallbacksHandler serverCallbacksHandler = new ServerCallbacksHandler();

    private boolean running = false;

    private Server()
    {

    }

    public static Server create()
    {
        instance = new Server();

        return instance;
    }

    public static Server getServer()
    {
        return instance;
    }

    public void Bind(int port)
    {
        System.out.println("Binding at port " + port);
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server bound");

            running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Listen()
    {
        if(serverSocket == null || !serverSocket.isBound())
            return;

        listenerThread = new Thread(() -> {
            while(running)
            {
                try {
                    ServerConnection sc = new ServerConnection(serverSocket.accept());
                    connections.add(sc);
                    serverCallbacksHandler.onNewConnection(sc);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        listenerThread.start();
    }

    public void Disconnect(ServerConnection sc)
    {
        try {
            sc.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connections.remove(sc);
        serverCallbacksHandler.onConnectionClosed(sc);
    }

    public void Shutdown()
    {
        running = false;
        for(ServerConnection sc : connections)
        {
            Disconnect(sc);
        }
    }

    public List<MessageHandler> getMessageHandlersOfType(final MessageType type) {
        List<MessageHandler> handlers = new ArrayList<>();
        for (MessageHandler messageHandler : messageHandlers) {
            if(messageHandler.getMessageType() == type) {
                handlers.add(messageHandler);
            }
        }

        return handlers;
    }

    public void Send(final Message message, final ServerConnection sc)
    {
        try {
            sc.send(message);
            serverCallbacksHandler.onMessageSent(sc, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(final Message message, final List<ServerConnection> recipients)
    {
        for(ServerConnection sc : recipients)
        {
            Send(message, sc);
        }
    }

    public void Broadcast(final Message message)
    {
        Send(message, connections);
    }

    public void AddHandler(final MessageHandler handler)
    {
        messageHandlers.add(handler);
    }

    public void HandleMessages()
    {
        while(!messageQueue.isEmpty())
        {
            MessageSenderPair messageSenderPair = messageQueue.poll();

            for(MessageHandler messageHandler : getMessageHandlersOfType(messageSenderPair.getMessageType()))
            {
                messageHandler.handle(messageSenderPair);
            }
        }
    }

    public List<ServerConnection> getConnections() {
        return connections;
    }

    public void AddMessageToQueue(final Message message, final ServerConnection sc)
    {
        messageQueue.add(new MessageSenderPair(message, sc));
        serverCallbacksHandler.onMessageReceived(sc, message);
    }

    public boolean isRunning() {
        return running;
    }
}
