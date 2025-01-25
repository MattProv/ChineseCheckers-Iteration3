package org.example;

import org.example.game_logic.StandardBoard;
import org.example.message.serverHandlers.*;
import org.example.server.GameManager;
import org.example.server.Server;
import org.example.server.ServerCallbacksHandler;
import org.example.server.ServerConnection;

public class ServerMain {
    public static void main(final String[] args) {

        System.out.println("Hello World from Server!");

        GameManager gameManager = GameManager.create();

        Server server = Server.create();
        server.serverCallbacksHandler = new ServerCallbacksHandler() {
            @Override
            public void onConnectionClosed(ServerConnection connection) {
                super.onConnectionClosed(connection);
                gameManager.removeUser(connection);
                gameManager.synchronizeUsers();
            }
        };

        gameManager.setBoard(new StandardBoard());

        server.AddHandler(new MoveMessageHandler(gameManager));
        server.AddHandler(new CommandMessageHandler(gameManager));
        server.AddHandler(new UsernameMessageHandler(gameManager));
        server.AddHandler(new RuleTypeMessageHandler(gameManager));
        server.AddHandler(new BoardTypeMessageHandler(gameManager));
        server.AddHandler(new EndTurnMessageHandler(gameManager));
        server.Bind(Config.PORT);
        server.Listen();

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
