package org.example.server.db;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameDocument startNewGame() {
        GameDocument game = new GameDocument();
        game.setStartTime(LocalDateTime.now());
        return gameRepository.save(game);
    }

    public void saveMove(GameDocument game, Move move) {
        game.getMoves().add(move);
        gameRepository.save(game);
    }

    public GameDocument getGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
    }

    public List<GameDocument> getAllGames() {
        return gameRepository.findAll();
    }

    public void saveGame(GameDocument game) {
        gameRepository.save(game);
    }
}
