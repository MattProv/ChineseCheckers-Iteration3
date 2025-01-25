package org.example.message;

import org.example.game_logic.BoardType;

public class BoardTypeMessage extends Message {
    private BoardType boardType;

    public BoardTypeMessage(BoardType boardType) {
        super(MessageType.BOARD_TYPE);
        this.boardType = boardType;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }

    @Override
    public String toString() {
        return "boardType='" + boardType;
    }
}
