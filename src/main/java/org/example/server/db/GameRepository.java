package org.example.server.db;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<GameDocument, String> {
}
