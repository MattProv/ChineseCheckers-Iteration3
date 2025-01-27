package org.example.server.db;

import org.example.game_logic.Coordinate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    public void testGameFlow() {
        // Rozpoczęcie gry
        GameCollection game = gameService.startNewGame();

        // Dodanie ruchów
        gameService.saveMove(game.getId(), new Move(new Coordinate(1, 1), new Coordinate(2, 2)));
        gameService.saveMove(game.getId(), new Move(new Coordinate(2, 2), new Coordinate(3, 3)));

        // Pobranie gry z bazy
        GameCollection loadedGame = gameService.getGame(game.getId());

        // Weryfikacja
        System.out.println("Liczba ruchów: " + loadedGame.getMoves().size()); // Powinno być 2

        assertEquals(2, loadedGame.getMoves().size());
        assertEquals(new Coordinate(1, 1), loadedGame.getMoves().getFirst().getStartPosition());
    }
}
