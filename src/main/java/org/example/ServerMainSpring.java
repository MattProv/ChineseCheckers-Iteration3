package org.example;

import org.example.game_logic.StandardBoard;
import org.example.message.serverHandlers.*;
import org.example.server.GameManager;
import org.example.server.Server;
import org.example.server.ServerCallbacksHandler;
import org.example.server.ServerConnection;
import org.example.server.db.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ServerMainSpring implements CommandLineRunner {

    @Autowired
    private GameService gameService;

    public static void main(String[] args) {
        SpringApplication.run(ServerMainSpring.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
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

        Scanner scanner = new Scanner(System.in);
        String input;

        do {
            input = scanner.nextLine();
        } while (!input.equals("exit"));
        server.Shutdown();
    }
}
