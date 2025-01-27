package org.example.server.db;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameCollection startNewGame() {
        GameCollection game = new GameCollection();
        game.setStartTime(LocalDateTime.now());
        return gameRepository.save(game);
    }

    public void saveMove(String gameId, Move move) {
        Optional<GameCollection> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isPresent()) {
            GameCollection game = optionalGame.get();
            game.getMoves().add(move);
            gameRepository.save(game);
        } else {
            throw new IllegalArgumentException("Game not found");
        }
    }

    public GameCollection getGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
    }
}
