package org.example.message.serverHandlers;

import org.example.message.BoardTypeMessage;
import org.example.message.MessageHandler;
import org.example.message.MessageSenderPair;
import org.example.message.MessageType;
import org.example.server.GameManager;

public class BoardTypeMessageHandler extends MessageHandler {
    final GameManager gameManager;
    public BoardTypeMessageHandler(GameManager gameManager) {
        super(MessageType.BOARD_TYPE);

        this.gameManager = gameManager;
    }

    @Override
    public void handle(MessageSenderPair message) {
        BoardTypeMessage boardTypeMessage = (BoardTypeMessage) message.getMessage();
        gameManager.setBoard(boardTypeMessage.getBoardType().createBoard());
    }
}
