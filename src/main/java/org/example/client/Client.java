package org.example.client;

import org.example.message.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Client class used to connect to the server
 * and send/receive messages
 */
public class Client implements Runnable{

    private static Client instance;

    //the socket used to connect to the server
    private Socket socket;

    //the output and input streams used to send and receive messages
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    //handlers for the different types of messages
    private final Queue<MessageSenderPair> messageQueue = new LinkedList<>();
    private final List<MessageHandler> messageHandlers =  new ArrayList<>();

    public ClientCallbacksHandler clientCallbacksHandler= new ClientCallbacksHandler();

    private Client()
    {

    }

    public static Client create()
    {
        return instance = new Client();
    }

    public static Client getClient()
    {
        return instance;
    }

    /**
     * Connects to the server
     * and starts the listener thread
     * @param host the host to connect to
     * @param port the port to connect to
     * @return true if the connection was successful, false otherwise
     */
    public boolean Connect(String host, int port)
    {
        try
        {
            socket = new Socket(host, port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            //the thread used to listen for messages from the server
            Thread listenerThread = new Thread(this);
            listenerThread.start();

            clientCallbacksHandler.onConnect();

            return true;
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server not found: " + ex.getMessage()+"\n");
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid host/port!\n");
        }
        catch (ConnectException ex) {
            System.out.println("Connection Refused.\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The listener thread that listens for messages from the server
     */
    @Override
    public void run() {
        try {
            Message msg;
            while (socket != null && socket.isConnected()) {  // Check socket connection
                msg = read();
                AddMessageToQueue(msg);
            }
        } catch (Exception ex) {
            System.out.println("Error: socket error.");
            clientCallbacksHandler.onSocketError();
        } finally {
            // Ensure socket is properly closed
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Disconnects from the server
     */
    public void Disconnect()
    {
        if(socket == null)
            return;
        if(!socket.isConnected())
            return;
        try
        {
            send(new DisconnectMessage());

            socket.close();
            socket = null;
        }
        catch (SocketException e) {
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        clientCallbacksHandler.onDisconnect();
    }


    /**
     * Sends a message to the server
     * @param message the message to send
     * @return true if the message was sent successfully, false otherwise
     *
     * @see Message
     */
    public <T extends Message> boolean send(T message)
    {
        try
        {
            oos.writeObject(message);
            clientCallbacksHandler.onMessageSent(message);

            return true;
        }
        catch (SocketException e) {
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Message read()
    {
        try
        {
            return (Message) ois.readObject();
        }
        catch (EOFException e) {
            Disconnect();
            System.out.println("\nConnection to the server lost");
            return null;
        }
        catch (SocketException e) {
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void AddHandler(MessageHandler handler)
    {
        messageHandlers.add(handler);
    }

    public List<MessageHandler> getMessageHandlersOfType(MessageType type) {
        List<MessageHandler> handlers = new ArrayList<>();
        for (MessageHandler messageHandler : messageHandlers) {
            if(messageHandler.getMessageType() == type) {
                handlers.add(messageHandler);
            }
        }

        return handlers;
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

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void AddMessageToQueue(Message msg)
    {
        if (msg != null) {
            messageQueue.add(new MessageSenderPair(msg, null));
            clientCallbacksHandler.onMessageReceived(msg);
        }
    }
}