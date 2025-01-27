package org.example.server.db;

import org.example.game_logic.BoardType;
import org.example.game_logic.RulesType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "games")
public class GameDocument {

    @Id
    private String id; // MongoDB używa Stringa jako identyfikatora
    private LocalDateTime startTime;
    private String name;
    private int playersCount;
    private BoardType boardType;
    private RulesType rulesType;
    private List<Move> moves = new ArrayList<>();

    // Gettery i settery

    public String getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return startTime;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public int setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
        return playersCount;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public BoardType setBoardType(BoardType boardType) {
        this.boardType = boardType;
        return boardType;
    }

    public RulesType getRulesType() {
        return rulesType;
    }

    public RulesType setRulesType(RulesType rulesType) {
        this.rulesType = rulesType;
        return rulesType;
    }

    public List<Move> getMoves() {
        return moves;
    }
}

