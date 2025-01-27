package org.example;

import org.example.game_logic.Agent;
import org.example.game_logic.Move;
import org.example.game_logic.StandardBoard;
import org.example.message.serverHandlers.*;
import org.example.server.*;
import org.example.server.db.GameDocument;
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

        gameManager.gameManagerCallbackHandler = new GameManagerCallbackHandler() {
            GameDocument game;
            boolean isSaved = false;

            @Override
            public void onGameStarted() {
                super.onGameStarted();
                isSaved = !(gameManager.getGameName() == null || gameManager.getGameName().isEmpty());
                if(isSaved) {
                    game = gameService.startNewGame();
                    game.setBoardType(gameManager.getBoardType());
                    game.setRulesType(gameManager.getRulesType());
                    game.setPlayersCount(gameManager.getPlayersCount());
                    game.setName(gameManager.getGameName());

                    gameService.saveGame(game);
                }
            }

            @Override
            public void onValidMove(Agent agent, Move move, String s) {
                super.onValidMove(agent, move, s);

                if(isSaved){
                    gameService.saveMove(game, org.example.server.db.Move.fromMove(move));
                }
            }

            @Override
            public void onTurnChange(Agent oldTurn, Agent currentTurn, int turnIndex) {
                super.onTurnChange(oldTurn, currentTurn, turnIndex);

                if(isSaved){
                    game.setCurrentTurn(turnIndex);
                    gameService.saveGame(game);
                }
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
